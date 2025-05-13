<template>
  <div :id="props.id" :style="{ height: props.height, width: props.width }" />
</template>

<script setup>
import * as echarts from 'echarts';
import { onMounted, ref, onBeforeUnmount, watch, nextTick } from 'vue';

var chart = ref(null);
var Timer;
const mockXdata = [1, 2, 3, 4, 5];
const mockYdata = [10, 20, 50, 40, 30];

const props = defineProps({
  width: {
    type: String,
    required: true,
  },
  height: {
    type: String,
    required: true,
  },
  id: {
    type: String,
    required: true,
  },
  lineColor: {
    type: String,
  },
  isSmooth: {
    type: Boolean,
  },

  Bodedata: {
    type: Object,
  },
  isOverViewData: {
    type: Boolean,
  },
  overViewData: {
    type: Object,
  },
});
const isLoaded = ref(false);

const setData = (Xdata, lYdata, RYdata) => {
  const lineNames = ['frequency', 'phase']; // y 轴参数
  const colors = ['#ff0000', '#00ff00']; // 线条颜色

  chart.value.setOption({
    graphic: [
      {
        type: 'text',
        right: '26%',
        top: '10%',
        rotation: Math.PI / 2, // 逆时针旋转 90 度
        style: {
          text: 'Phase Angle (degree)',
          fill: '#fff',
          fontSize: '0.06rem',
        },
      },
      {
        type: 'text',
        left: '1%',
        top: '5%',
        rotation: -Math.PI / 2, // 逆时针旋转 90 度
        style: {
          text: 'Phase Angle (degree)',
          fill: '#fff',
          fontSize: '0.06rem',
        },
      },
    ],
    dataZoom: [{ show: false }, { show: false }],
    grid: {
      top: '10%',
      left: '5%',
      right: '30%',
      bottom: '5%',
      containLabel: true, // ✅ 让 grid 自动适应轴标签
    },
    xAxis: {
      type: 'log',
      min: 1,
      logBase: 10,
      data: Xdata, // 一次性加载全部数据
      axisLabel: {
        show: true,
        textStyle: {
          color: '#fff',
          fontSize: '0.08rem',
        },
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: 'rgba(221, 221, 221, 1)',
          width: 2,
        },
      },
    },
    yAxis: [
      {
        name: 'phase',
        type: 'log',
        min: 1,
        logBase: 10,
        axisLabel: {
          show: true,
          textStyle: {
            color: '#fff',
            fontSize: '0.08rem',
          },
        },
        axisLine: {
          show: true,
          lineStyle: {
            color: 'rgba(221, 221, 221, 1)',
            width: 2,
          },
        },
      },
      {
        type: 'value',
        name: 'amplitude',
        position: 'right', // ✅ 确保是 right
        axisLabel: {
          show: true,
          textStyle: {
            color: '#fff',
            fontSize: '0.08rem',
          },
        },

        axisLine: {
          show: true,
          lineStyle: {
            color: 'rgba(221, 221, 221, 1)',
            width: 2,
          },
        },
        axisTick: {
          show: false,
        },

        splitLine: {
          show: true,
          lineStyle: {
            color: '#11366e',
          },
        },
      },
    ],
    series: [
      {
        name: 'amplitude',
        type: 'line',
        symbol: 'circle',
        yAxisIndex: 1, // ✅ 确保是 right
        lineStyle: { color: colors[0], width: 5 }, // 线条颜色
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
        data: lYdata, // 对应参数的数据
      },
      {
        name: 'phase',
        type: 'line',
        symbol: 'circle',
        yAxisIndex: 0, // ✅ 确保是 right
        lineStyle: { color: colors[1], width: 5 }, // 线条颜色
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
        data: RYdata, // 对应参数的数据
      },
    ],
  });
};
const setOverViewData = (Ydata) => {
  console.log(Ydata,'总览xdata');
  
  const Xdata = [];
  const series = [];

  const lineNames = [
    'impedanceStdDev',
    'absImpedanceMean',
    'maxAbsImpedance',
    'minAbsImpedance',
  ]; // y 轴参数
  const colors = ['#FAC858', '#5470c6', '#91CC75']; // 线条颜色

  const seriesData = {
    impedanceStdDev: [],
    absImpedanceMean: [],
    maxAbsImpedance: [],
    minAbsImpedance: [],
  };
  Ydata.forEach((item, index) => {
    const time = new Date(item.timestamp).toLocaleDateString()
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
      lineStyle: { color: colors[idx], width: 5 }, // 线条颜色
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
      data: seriesData[name], // 对应参数的数据
    });
  });
  console.log(series, '单个pack  series');
  const legendNameMap = {
    absImpedanceMean: '阻抗均值',
    impedanceStdDev: '标准差',
    maxAbsImpedance: '最大阻抗',
    minAbsImpedance: '最小阻抗',
  };
  chart.value.setOption({
    graphic: [
      {
        type: 'text',
        left: '6%',
        top: '25%',
        rotation: Math.PI / 2,

        style: {
          text: '幅值(Ω)',
          fill: '#fff',
          fontSize: '0.06rem',
        },
      },
    ],
    legend: {
      data: lineNames, // 确保 legend 匹配 series.name
      formatter: (name) => legendNameMap[name] || name, // 根据映射转换
    },
    xAxis: {
      type: 'category',
      data: Xdata, // 一次性加载全部数据
      axisLabel: { padding: [3, 10, 0, 10] },
    },
    yAxis: {
      type: 'value',
    },

    series: series,
  });

  if (Timer) {
    clearInterval(Timer);
  }

  // // 开启滚动
  // Timer = setInterval(function () {
  //   startIndex += 1;

  //   // 如果到达末尾，重新开始
  //   if (startIndex >= Xdata.length) {
  //     startIndex = 0;
  //   }

  //   // 计算 start 和 end 的百分比
  //   const startPercent = (startIndex / Xdata.length) * 100;
  //   const endPercent = ((startIndex + maxVisible) / Xdata.length) * 100;

  //   chart.value.setOption({
  //     dataZoom: [
  //       {
  //         start: startPercent,
  //         end: endPercent,
  //       },
  //     ],
  //   });
  // }, 2000); // 每 2 秒滚动
};
watch(
  () => props.Bodedata,
  (newVal, oldVal) => {
    console.log('Bodedata变化了', props.id, newVal);
    const phase = newVal.map((item) => item.phase);
    // const amplitude = newVal.map((item) => item.amplitude.toFixed(2));
    const frequency = newVal.map((item) => item.frequency.toFixed(2));

    setData(frequency, phase, phase);
  },
  { deep: true }
);
watch(
  () => props.overViewData,
  (newVal, oldVal) => {
    console.log('纵览数据', props.id, newVal);


    setOverViewData(newVal.packHistogram);
  },
  { deep: true }
);

const initChart = () => {
  chart.value = echarts.init(document.getElementById(props.id));

  const option = {
    
    grid: {
      top: '10%',
      left: '10%',
      right: '25%',
      bottom: '15%',
      containLabel: true, // ✅ 让 grid 自动适应轴标签
    },
    legend: {
      show: true,
      itemWidth: 30,
      itemHeight: 10,
      top: '30%',
      right: '5%',
      orient: 'vertical', // 让图例垂直排列
      itemGap: 20,
      textStyle: {
        color: 'rgba(255, 255, 255, 1)',
        fontSize: '0.06rem',
        padding: [0, 8, 0, 8],
      },
    },
    // backgroundColor: "rgba(0,0,0,.5)",
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'line',
        shadowStyle: {
          color: 'rgba(81, 127, 253, 0.1)',
        },
        label: {
          show: false,
          color: '#517FFD',
          backgroundColor: 'transparent',
          fontSize: '0.06rem',
        },
      },

      formatter: function (params) {
        var str = '<div class="my-tooltip">';
        params.forEach((el) => {
          if (el.seriesName !== 'maxdata') {
            str += `<div><span class="name large">${el.name}年</span><br/><span class="value">${el.value} %</span></div>`;
          }
        });
        str += '</div>';
        return str;
      },
    },
    xAxis: {
      // data: mockXdata,
      data: [],

      axisLine: {
        show: true, // 隐藏X轴轴线
        lineStyle: {
          color: 'rgba(221, 221, 221, 1)',
          width: 2,
        },
      },
      splitLine: { lineStyle: { color: 'rgba(255,255,255, .1)' } },
      axisTick: {
        show: false, // 隐藏X轴刻度
        length: 6, // 设置刻度线的长度
        lineStyle: {
          color: '#FFF', // 刻度线的颜色
          width: 2, // 刻度线的宽度
        },
      },
      axisLabel: {
        show: true,
        textStyle: {
          color: '#FFF',
          fontSize: '0.08rem',
        },
      },
    },
    // dataZoom: [
    //   {
    //     type: 'slider',
    //     yAxisIndex: 0,
    //     left: 20, // 距离右边10px
    //     top: 10, // 距离顶部50px
    //     start: 0,
    //     end: 100,
    //   },
    //   {
    //     type: 'slider',
    //     xAxisIndex: 0,
    //     start: 0,
    //     end: 100,
    //   },
    // ],
    yAxis: [
      {
        type: 'value',
        name: '',

        nameTextStyle: {
          color: '#FFF',
          padding: [0, 10, 10, 0],
          fontSize: '0.08rem',
        },
        // max: 100, // 可设置最大值
        // min: props.isOverViewData ? -0.5 : 0,
        // interval:props.isOverViewData?0.1:0.001,
        splitLine: { lineStyle: { color: 'rgba(255,255,255, .1)' } },
        axisTick: {
          show: false,
        },
        axisLine: {
          show: true,
          lineStyle: {
            color: 'rgba(221, 221, 221, 1)',
            width: 2,
          },
        },
        axisLabel: {
          show: true,
          textStyle: {
            color: '#FFF',
            fontSize: '0.08rem',
          },
        },
      },
    ],
    series: [],
  };
  chart.value.setOption(option);
};
// 窗口变化时调整大小
const resizeChart = () => {
  if (chart.value) chart.value.resize();
};
watch(
  () => window.nowPage,
  (newVal, oldVal) => {
    console.log('OverViewData变化了');
    nextTick().then(() => {
      initChart();
      window.addEventListener('resize', resizeChart);
    });
  },
  { immediate: true }
);

onMounted(() => {
  console.log('mounted');
  initChart();
});
onBeforeUnmount(() => {
  if (Timer) {
    clearInterval(Timer);
  }
});
</script>
<style></style>
