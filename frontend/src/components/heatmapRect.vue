<template>
  <div :id="props.id" class="heatmapRect" />
</template>

<script setup>
import * as echarts from 'echarts';
import { onMounted, ref, onBeforeUnmount, watch, nextTick } from 'vue';

var chart = ref(null);
var Timer;
var hours = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13'];
var days = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13'];

var data = [
  [0, 0, 53.6],
  [0, 1, 7.7],
  [0, 2, 26.4],
  [0, 3, 34.1],
  [0, 4, 12.4],
  [0, 5, 12.4],
  [0, 6, 12.4],
  [0, 7, 12.4],
  [0, 8, 12.4],
  [0, 9, 12.4],
  [0, 10, 12.4],
  [0, 11, 12.4],
  [0, 12, 12.4],

  [1, 0, 39.9],
  [1, 1, 3.21],
  [1, 2, 43.3],
  [1, 3, 15.4],
  [1, 4, 31.0],
  [1, 5, 0],
  [1, 6, 0],
  [1, 7, 0],
  [1, 8, 0],
  [1, 9, 0],
  [1, 10, 0],
  [1, 11, 0],
  [1, 12, 0],
  [2, 0, 19.0],
  [2, 1, 12.5],
  [2, 2, 17.9],
  [2, 3, 18.2],
  [2, 4, 22.0],
  [2, 5, 0],
  [2, 6, 0],
  [2, 7, 0],
  [2, 8, 0],
  [2, 9, 0],
  [2, 10, 0],
  [2, 11, 0],
  [2, 12, 0],
  [3, 0, 31.0],
  [3, 1, 38.5],
  [3, 2, 52.5],
  [3, 3, 11.4],
  [3, 4, 6.9],
  [3, 5, 0],
  [3, 6, 0],
  [3, 7, 0],
  [3, 8, 0],
  [3, 9, 0],
  [3, 10, 0],
  [3, 11, 0],
  [3, 12, 0],
  [4, 0, 9.5],
  [4, 1, 9.0],
  [4, 2, 3.9],
  [4, 3, 2.3],
  [4, 4, 18.8],
  [4, 5, 0],
  [4, 6, 0],
  [4, 7, 0],
  [4, 8, 0],
  [4, 9, 0],
  [4, 10, 0],
  [4, 11, 0],
  [4, 12, 0],
  [5, 0, 9.5],
  [5, 1, 1.0],
  [5, 2, 9.9],
  [5, 3, 2.3],
  [5, 4, 12.8],
  [5, 5, 0],
  [5, 6, 0],
  [5, 7, 0],
  [5, 8, 0],
  [5, 9, 0],
  [5, 10, 0],
  [5, 11, 0],
  [5, 12, 0],
  [6, 0, 53.6],
  [6, 1, 7.7],
  [6, 2, 26.4],
  [6, 3, 34.1],
  [6, 4, 12.4],
  [6, 5, 0],
  [6, 6, 0],
  [6, 7, 0],
  [6, 8, 0],
  [6, 9, 0],
  [6, 10, 0],
  [6, 11, 0],
  [6, 12, 0],
  [7, 0, 39.9],
  [7, 1, 3.21],
  [7, 2, 43.3],
  [7, 3, 15.4],
  [7, 4, 31.0],
  [7, 5, 0],
  [7, 6, 0],
  [7, 7, 0],
  [7, 8, 0],
  [7, 9, 0],
  [7, 10, 0],
  [7, 11, 0],
  [7, 12, 0],
  [8, 0, 19.0],
  [8, 1, 12.5],
  [8, 2, 17.9],
  [8, 3, 18.2],
  [8, 4, 22.0],
  [8, 5, 0],
  [8, 6, 0],
  [8, 7, 0],
  [8, 8, 0],
  [8, 9, 0],
  [8, 10, 0],
  [8, 11, 0],
  [8, 12, 0],
  [9, 0, 31.0],
  [9, 1, 38.5],
  [9, 2, 42.5],
  [9, 3, 11.4],
  [9, 4, 6.9],
  [9, 5, 0],
  [9, 6, 0],
  [9, 7, 0],
  [9, 8, 0],
  [9, 9, 0],
  [9, 10, 0],
  [9, 11, 0],
  [9, 12, 0],
  [10, 0, 9.5],
  [10, 1, 0.0],
  [10, 2, 1.9],
  [10, 3, 2.3],
  [10, 4, 18.8],
  [10, 5, 0],
  [10, 6, 0],
  [10, 7, 0],
  [10, 8, 0],
  [10, 9, 0],
  [10, 10, 0],
  [10, 11, 0],
  [10, 12, 0],
  [11, 0, 31.0],
  [11, 1, 38.5],
  [11, 2, 60.5],
  [11, 3, 11.4],
  [11, 4, 6.9],
  [11, 5, 0],
  [11, 6, 0],
  [11, 7, 0],
  [11, 8, 0],
  [11, 9, 0],
  [11, 10, 0],
  [11, 11, 0],
  [11, 12, 0],
  [12, 0, 31.0],
  [12, 1, 38.5],
  [12, 2, 60.5],
  [12, 3, 11.4],
  [12, 4, 6.9],
  [12, 5, 0],
  [12, 6, 0],
  [12, 7, 0],
  [12, 8, 0],
  [12, 9, 0],
  [12, 10, 0],
  [12, 11, 0],
  [12, 12, 0],
];

const props = defineProps({
  id: {
    type: String,
  },
  heatData: {
    type: Object,
  },
  heatDevData: {
    type: Object,
  },
});
// watch(
//   () => props.heatData,
//   (newVal, oldVal) => {
//     chart.value.setOption({
//       series: [
//         {
//           data: newVal,
//         },
//       ],
//     });
//   },
//   { deep: true }
// );
// watch(
//   () => props.heatDevData,
//   (newVal, oldVal) => {
//     chart.value.setOption({
//       series: [
//         {
//           data: newVal,
//         },
//       ],
//     });
//   },
//   { deep: true }
// );
watch(
  () => props.heatData,
  (newVal, oldVal) => {
    // 保留小数点后两位
    const formattedData = newVal.map((item) => [
      item[0],  // x坐标
      item[1],  // y坐标
      parseFloat(item[2].toFixed(2))  // z值，保留小数点后两位
    ]);

    // 计算 heatData 的最大值和最小值，用于动态设置 visualMap
    const zValues = formattedData.map((item) => item[2]);
    const zMin = Math.min(...zValues);
    const zMax = Math.max(...zValues);

    // 更新图表数据
    chart.value.setOption({
      graphic: [
      {
        type: 'text',
        left: '0%',
        top: '30%',
        rotation: Math.PI / 2,

        style: {
          text: '特征阻抗实部相关性',
          fill: '#fff',
          fontSize: '0.08rem',
        },
      },
    ],
      visualMap: {
        min: zMin,  // 动态设置 min
        max: zMax,  // 动态设置 max
        calculable: true,
        show: false,  // 不显示 visualMap
        orient: 'vertical',
        right: '5%',
        bottom: '15%',
        color: ['#0d59b7', '#bee8ff'],  // 自定义渐变色
      },
      series: [
        {
          data: formattedData,
          type: 'heatmap',
          label: {
            show: false,  // 显示数据标签
          },
          itemStyle: {
            normal: {
              areaColor: 'rgba(0, 255, 255, 0.5)', // 添加填充颜色
              borderColor: 'rgba(255, 255, 255, 0.3)',  // 边框颜色
              borderWidth: 1,  // 边框宽度
            },
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
      ],
    });
  },
  { deep: true }
);

watch(
  () => props.heatDevData,
  (newVal, oldVal) => {
    // 保留小数点后两位
    const formattedData = newVal.map((item) => [
      item[0],  // x坐标
      item[1],  // y坐标
      parseFloat(item[2].toFixed(2))  // z值，保留小数点后两位
    ]);

    // 计算 heatDevData 的最大值和最小值，用于动态设置 visualMap
    const zValues = formattedData.map((item) => item[2]);
    const zMin = Math.min(...zValues);
    const zMax = Math.max(...zValues);

    // 更新图表数据
    chart.value.setOption({
      graphic: [
      {
        type: 'text',
        left: '0%',
        top: '30%',
        rotation: Math.PI / 2,

        style: {
          text: '特征阻抗虚部相关性',
          fill: '#fff',
          fontSize: '0.08rem',
        },
      },
    ],
      visualMap: {
        min: zMin,  // 动态设置 min
        max: zMax,  // 动态设置 max
        calculable: true,
        show: false,  // 不显示 visualMap
        orient: 'vertical',
        right: '5%',
        bottom: '15%',
        color: ['#0d59b7', '#bee8ff'],  // 自定义渐变色
      },
      series: [
        {
          data: formattedData,
          type: 'heatmap',
          label: {
            show: false,  // 显示数据标签
          },
          itemStyle: {
            normal: {
              areaColor: 'rgba(0, 255, 255, 0.5)', // 添加填充颜色
              borderColor: 'rgba(255, 255, 255, 0.3)',  // 边框颜色
              borderWidth: 1,  // 边框宽度
            },
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
      ],
    });
  },
  { deep: true }
);


const initChart = () => {
  chart.value = echarts.init(document.getElementById(props.id));

  const option = {
    tooltip: {
      trigger: 'item',
      textStyle: {
        fontSize: 0.08 * parseFloat(getComputedStyle(document.documentElement).fontSize),
      },
      formatter: ' {c}',
    },
    animation: false,
    grid: {
      top: '10%',
      bottom: '10%',
    },
    xAxis: {
      type: 'category',
      data: hours,
      splitArea: {
        show: true,
      },
      axisLabel: {
        show: true,
        textStyle: {
          color: '#FFF',
          fontSize: '0.08rem',
        },
      },
      axisTick: {
        lineStyle: {
          color: '#c4c4c4',
        },
      },
    },
    yAxis: {
      type: 'category',
      data: days,
      splitArea: {
        show: true,
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        interval: 1,
        show: true,
        textStyle: {
          color: '#FFF',
          fontSize: '0.08rem',
        },
      },
    },
    visualMap: {
      min: 0,
      max: 70,
      calculable: true,
      show: false,
      orient: 'vertical',
      right: '5%',
      bottom: '15%',
      color: ['#0d59b7', '#bee8ff'],
    },
    series: [
      {
        name: '',
        type: 'heatmap',
        data: [],
        label: {
          show: false,
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
      },
    ],
  };
  chart.value.setOption(option);
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
onMounted(() => {
  // initChart();
});
</script>
<style>
.heatmapRect {
  width: 50%;
  height: 100%;
}
</style>
