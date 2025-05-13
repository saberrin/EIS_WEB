<template>
  <div :id="props.id" class="DoubleLineChart" />
</template>

<script setup>
import * as echarts from 'echarts';
import { onMounted, ref, onBeforeUnmount, watch, nextTick } from 'vue';
import store from '../store/index';

var chart = ref(null);
var Timer;
const mockXdata = [1, 2, 3, 4, 5];
const mockYdata = [10, 20, 50, 40, 30];
const status = ref('week');

const props = defineProps({
  HistoryData: {
    type: Object,
  },
  HistoryDataByPeriod: {
    type: Object,
  },
  id: {
    type: String,
  },
  showPeriod: {
    type: Boolean,
  },
  isShowLegend: {
    type: Boolean,
  },
  legendOrient: {
    type: String,
  },
  DrtData: {
    type: Object,
  },
});
watch(
  () => props.HistoryData,
  (newVal, oldVal) => {
    console.log('HistoryData变化了', newVal);
    const xData = newVal.map((item) => item.time);

    const yData = newVal;

    setData(yData);
  },
  { deep: true }
);
watch(
  () => props.HistoryDataByPeriod,
  (newVal, oldVal) => {
    console.log('HistoryDataByPeriod变化了', newVal);

    const yData = newVal;

    setDataByPeriod(yData);
  },
  { deep: true }
);
watch(
  () => props.DrtData,
  (newVal, oldVal) => {
    console.log('DrtData变化了', newVal);

    const xData = newVal.map((item) => item.freqFine);
    const yData = newVal.map((item) => item.gammaRidgeFine);

    setDrtData(xData, yData);
  },
  { deep: true }
);

function getRFC3339Date(daysAgo) {
  const date = new Date();
  date.setUTCDate(date.getUTCDate() - daysAgo);
  return date.toISOString();
}

const initChart = () => {
  chart.value = echarts.init(document.getElementById(props.id));

  const option = {
    
    //你的代码
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        lineStyle: {
          color: 'rgb(126,199,255)',
        },
      },
    },
    legend: {
      show: props.isShowLegend ? true : false,
      itemWidth: 30,
      itemHeight: 10,
      top: props.legendOrient == 'vertical' ? '30%' : '0',
      right: props.legendOrient == 'vertical' ? '3%' : 'center',
      orient: props.legendOrient, // 让图例垂直排列
      itemGap: 20,
      textStyle: {
        color: 'rgba(255, 255, 255, 1)',
        fontSize: '0.08rem',
        padding: [0, 8, 0, 8],
      },
    },
    dataZoom: [
      {
        type: 'slider',
        yAxisIndex: 0,
        start: 0,
        end: 100,
        width: '2%',  // 调整滑块的高度
        textStyle: {
          color: '#fff', // 设置文字颜色
          fontSize: '0.08rem',
        },
      },
      {
        type: 'slider',
        xAxisIndex: 0,
        start: 0,
        end: 100,
        height: '6%',  // 调整滑块的高度
        bottom: '5%',   // 调整滑块位置
        textStyle: {
          color: '#fff', // 设置文字颜色
          fontSize: '0.08rem',
        },
      },
    ],
    grid: {
      top: '10%',
      left: '5%',
      right: props.legendOrient == 'vertical' ? '14%' : '7%',
      bottom: '20%',
      containLabel: true, // ✅ 让 grid 自动适应轴标签
    },
    xAxis: [
      {
        type: 'category',
        boundaryGap: true,
        axisLine: {
          show: true,
          lineStyle: {
            color: '#fff',
          },
        },
        axisLabel: {
          textStyle: {
            color: '#fff',
            fontSize: '0.07rem',
          },
        },
        splitLine: {
          show: false,
        },
        axisTick: {
          show: false,
        },
        data: [],
      },
    ],
    yAxis: [
      {
        name: '',
        nameTextStyle: {
          color: '#fff',
          fontSize: 22,
          padding: [0, 60, 0, 0],
        },
        interval: 50, // 每隔20一个刻度
        scale: true, // 动态调整范围
        // interval: 0.005, // 刻度间隔变小
        type: 'value',
        splitLine: {
          show: false,
        },
        axisLine: {
          show: true,
          lineStyle: {
            color: '#fff',
            width: 2,
          },
        },
        axisLabel: {
          show: true,
          textStyle: {
            color: '#fff',
            fontSize: '0.08rem',
          },
        },
        axisTick: {
          show: false,
        },
      },
    ],
    series: [],
  };

  chart.value.setOption(option);
};
const tabPeriod = (type) => {
  status.value = type;
  let dayNum;
  if (type == 'month') {
    dayNum = 29;
  }
  if (type == 'week') {
    dayNum = 6;
  }
  if (type == 'day') {
    dayNum = 0;
  }
  store.commit('setdateFrom', getRFC3339Date(dayNum));
};

const setData = (Ydata) => {
  const Xdata = [];
  const colors = ['#ff0000', '#00ff00', '#0000ff', '#ff00ff', '#00ffff', '#ffff00']; // 颜色列表
  const seriesData = {}; // 用于存储每条折线数据
  const lineNames = ['absImpedanceMean', 'impedanceStdDev', 'minAbsImpedance'];
  // 初始化 seriesData
  lineNames.forEach((name) => {
    seriesData[name] = [];
  });
  const series = []; // 存储最终的 series 配置
  // 遍历所有电池
  Ydata.forEach((item, index) => {
    const color = colors[index % colors.length]; // 给不同电池分配颜色
    // const color = item.color || '#000000'; // 读取 pack 的颜色，默认黑色
    lineNames.forEach((name) => {
      const lineData = []; // 存储该 pack 的单条线数据

      item.packHistogram.forEach((obj, idx) => {
    
    // 格式化时间
        // const time = new Date(obj.timestamp).toLocaleString()
        const time = new Date(obj.timestamp).toLocaleDateString(); // 这里将时间格式化为年月日

        if (!Xdata.includes(time)) {
          Xdata.push(time);
        }

        lineData.push({
          value: [time, obj[name]],
          itemStyle: { color },
        });
      });

      // 给当前 pack 的这条线添加到 series
      series.push({
        // name: `${name} (Pack ${item.packId})`, // 显示 Pack ID，避免混乱
        name: `${name} `, // 显示 Pack ID，避免混乱

        type: 'line',
        symbol: 'circle',
        lineStyle: { width: 5 }, // 线条颜色
        // itemStyle: { color }, // 拐点颜色
        itemStyle: {
          normal: {
            label: {
              show: false,
              color: '#fff',
              fontSize: '0.08rem',

              position: 'top',
              formatter: (res) => (res.value ? res.value[1].toFixed(2) : '0'),
            },
          },
        },
        data: lineData,
      });
    });
  });

  console.log(series, 'series');

  chart.value.setOption({
    graphic: [
      {
        type: 'text',
        left: '6%',
        top: '30%',
        rotation: Math.PI / 2,

        style: {
          text: '幅值(Ω)',
          fill: '#fff',
          fontSize: '0.08rem',
        },
      },
    ],
    xAxis: {
      data: Xdata, // 一次性加载全部数据
      axisLabel: { padding: [3, 10, 0, 10] },
    },
    yAxis: {
      type: 'value',
    },

    series: series,
  });
};
const setDataByPeriod = (Ydata) => {
  const Xdata = []; // 存储 x 轴时间
  const series = []; // 存储 series 配置
  // const lineNames = ['absImpedanceMean', 'impedanceStdDev', 'coefficientOfVariation']; // y 轴参数
  const lineNames = ['realPart10Hz', 'imagPart10Hz', 'r0']; // y 轴参数

  const colors = ['#ff0000', '#00ff00', '#0000ff']; // 线条颜色

  // 初始化 series 数据结构
  const seriesData = {
    realPart10Hz: [],
    imagPart10Hz: [],
    r0: [],
  };

  // 遍历数据，填充 X 轴和 Y 轴数据
  Ydata.forEach((item, index) => {
    // const time = `2024-01-${String(index + 1).padStart(2, '0')}`; // 生成时间数据
    const time = new Date(item.timestamp).toLocaleDateString(); // 生成时间数据

    Xdata.push(time);

    // 遍历每个参数，将对应的值存入 seriesData
    lineNames.forEach((name) => {
      seriesData[name].push({
        value: [time, item[name]],
      });
    });
  });

  // 生成 series 配置
  lineNames.forEach((name, idx) => {
    series.push({
      name, // 图例名称
      type: 'line',
      symbol: 'circle',
      lineStyle: { width: 5 }, // 线条颜色

      itemStyle: {
        color: colors[idx],
        normal: {
          label: {
            show: false,
            color: '#fff',
            fontSize: '0.08rem',
            position: 'top',
            formatter: (res) => (res.value ? res.value[1].toFixed(2) : '0'),
          },
        },
      },
      data: seriesData[name], // 对应参数的数据
    });
  });
  console.log(seriesData, Ydata, '单个pack历史数据');
  const legendNameMap = {
    realPart10Hz: '10Hz实部阻抗',
    imagPart10Hz: '10Hz虚部阻抗',
    r0: '电解质电阻',
  };

  // 设置 ECharts 配置
  chart.value.setOption({
    graphic: [
      {
        type: 'text',
        left: '6%',
        top: '30%',
        rotation: Math.PI / 2,

        style: {
          text: '幅值(Ω)',
          fill: '#fff',
          fontSize: '0.08rem',
        },
      },
    ],
    grid: {
      left: '4%',
      bottom: '21%',
      containLabel: true, // ✅ 让 grid 自动适应轴标签
    },
    legend: {
      data: lineNames, // 确保 legend 匹配 series.name
      formatter: (name) => legendNameMap[name] || name, // 根据映射转换
    },
    xAxis: {
      type: 'category',
      data: Xdata,
      axisLabel: { padding: [3, 10, 0, 10] },
    },
    yAxis: {
      type: 'value',
    },
    series: series,
  });
};
const setDrtData = (Xdata, Ydata) => {
  console.log(Ydata, 'setDrtData');

  chart.value.setOption({
    legend: {
      show: false,
    },
    grid: {
      left: '8%',
      containLabel: true, // ✅ 让 grid 自动适应轴标签
    },
    graphic: [
      {
        type: 'text',
        right: 'center',
        bottom: '10%',
        style: {
          text: '松弛时间常数（s）',
          fill: '#fff',
          fontSize: '0.08rem',
        },
      },
      {
        type: 'text',
        left: '3%',
        top: '10%',
        rotation: Math.PI / 2,

        style: {
          text: 'drt分布函数（Ω）',
          fill: '#fff',
          fontSize: '0.08rem',
        },
      },
    ],
    dataZoom: [{ show: false }, { show: false }],
    xAxis: {
      data: Xdata, // 一次性加载全部数据
      axisLine: {
        show: true,
        lineStyle: {
          color: '#fff',
          width: 2,
        },
      },
      axisLabel: {
        padding: [3, 10, 0, 10],
        formatter: (res) => {
          return !isNaN(Number(res)) ? Number(res).toFixed(2) : '0';
        },
      },
    },
    yAxis: {
      axisLine: {
        show: true,
        lineStyle: {
          color: '#fff',
          width: 2,
        },
      },
      type: 'value',
    },

    series: [
      {
        name: 'gammaRidgeFine',
        type: 'line',
        symbol: 'circle',
        lineStyle: { width: 5 }, // 线条颜色
        itemStyle: {
          normal: {
            label: {
              show: true,
              color: '#fff',
              fontSize: '0.08rem',
              position: 'top',
              formatter: (res) => (res.value ? res.value.toFixed(2) : '0'),
            },
          },
        },
        data: Ydata,
      },
    ],
  });
};
watch(
  () => window.nowPage,
  (newVal, oldVal) => {
    console.log('OverViewData变化了');
    nextTick().then(() => {
      initChart();
    });
  },
  { immediate: true }
);

onBeforeUnmount(() => {
  if (Timer) {
    clearInterval(Timer);
  }
});
</script>
<style>
.DoubleLineChart {
  width: 100%;
  height: 100%;
}
.period {
  margin-left: 0.2rem;
  width: 0.8rem;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  align-items: center;
  border-radius: 0.01rem;
  color: #fff;
  font-size: 0.08rem;
  .btn {
    line-height: 0.1rem !important;
    text-align: center;
    cursor: pointer;
    border: 2px solid #3e5d82;
  }
}
.active {
  background: url('../assets/img/yellow.png') no-repeat 100% 100% / 100% 100%;
}
</style>
