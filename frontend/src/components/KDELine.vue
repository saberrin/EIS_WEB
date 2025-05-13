<template>
  <div id="KDELine" class="KDELine" />
</template>

<script setup>
import * as echarts from 'echarts';
import { onMounted, ref, onBeforeUnmount, watch, nextTick } from 'vue';

var chart = ref(null);
var Timer;

const xData = [1, 2];
const yData = [0, 0];

const nowValue = ref(0);
const userIndex = xData.findIndex((x) => x >= nowValue.value);
const userY = yData[userIndex];
const props = defineProps({
  id: {
    type: String,
  },
  KdeData: {
    type: Object,
  },
  min: {
    type: String,
  },
  max: {
    type: String,
  },
});
watch(
  () => props.KdeData,
  (newVal, oldVal) => {
    console.log('KdeData', newVal);
    nowValue.value = newVal;
    setData(
      [
        props.min.toExponential(2),
        ((props.max - props.min) / 2).toExponential(2),
        props.max.toExponential(2),
      ],
      [(props.max - props.min) / 2, 5, 0]
    );
  },
  { deep: true }
);
const initChart = () => {
  chart.value = echarts.init(document.getElementById('KDELine'));

  const option = {
    xAxis: {
      type: 'category',
      data: xData.map((x) => x.toFixed(0)),
      boundaryGap: false,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: {
        color: '#999',
        interval: function (index) {
          // 只显示第一个和最后一个
          return index === 0 || index === xData.length;
        },
        fontSize: '0.08rem',
      },
    },
    yAxis: { show: false },
    series: [
      {
        type: 'line',
        smooth: true,
        data: yData,
        lineStyle: { color: '#6bb046', width: 2 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(107, 176, 70, 0.3)' },
            { offset: 1, color: 'rgba(107, 176, 70, 0)' },
          ]),
        },
        markPoint: {
          symbol: 'pin',
          symbolSize: 30,
          label: {
            show: true,
            color: '#fff',
            fontSize: '0.08rem',
            offset: [120, -40], // 调整 [x, y] 方向偏移量
            fontWeight: 'bold',
            formatter: (params) => {
              return `阻抗值：${nowValue.value}`; // 自定义显示内容
            },
          },
          //无法自适应高度 需手动调整
          data: [{ coord: [0.5, 3.2], itemStyle: { color: '#6bb046' } }],
        },
      },
    ],
  };
  chart.value.setOption(option);
};

const setData = (Xdata, Ydata) => {
  if (Xdata.length <= 0) {
    return;
  }

  chart.value.setOption({
    xAxis: {
      data: Xdata, // 一次性加载全部数据
      axisLabel: { padding: [0, 10, 0, 10] },
    },
    yAxis: {
      type: 'value',
    },

    series: [
      {
        type: 'line',
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
onMounted(() => {
  // initChart();
});
onBeforeUnmount(() => {
  if (Timer) {
    clearInterval(Timer);
  }
});
</script>
<style>
.KDELine {
  width: 70%;
  height: 0.3rem;
}
</style>
