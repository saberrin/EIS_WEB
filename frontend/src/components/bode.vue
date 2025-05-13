<template>
  <div id="bode" class="bode" />
</template>

<script setup>
import { onMounted, ref, onBeforeUnmount, watch, nextTick } from 'vue';

const props = defineProps({
  id: {
    type: String,
  },
  Bodedata: {
    type: Object,
  },
});
function drawBode() {
  // 模拟示例数据
  // 频率数据（单位：Hz）-- 采用对数变化的频率数组
  var frequencies = [];

  // 模拟的复数数据：real_parts 和 imag_parts 模拟实际测量值
  var realParts = [];
  var imagParts = [];

  // 根据复数数据计算幅值和相位（单位：度）
  var magnitude = [];
  var phase = [];
  for (var i = 0; i < frequencies.length; i++) {
    // 幅值的计算：sqrt(real^2 + imag^2)
    var mag = Math.sqrt(Math.pow(realParts[i], 2) + Math.pow(imagParts[i], 2));
    magnitude.push(mag);

    // 相位角计算：atan2(imag, real) 转换成角度
    var ph = Math.atan2(imagParts[i], realParts[i]) * (180 / Math.PI);
    phase.push(ph);
  }

  // 定义幅值曲线（蓝色）
  var traceMagnitude = {
    x: frequencies,
    y: magnitude,
    mode: 'lines+markers',
    name: 'Magnitude',
    line: { color: 'blue' },
    yaxis: 'y',
  };

  // 定义相位曲线（红色）
  var tracePhase = {
    x: frequencies,
    y: phase,
    mode: 'lines+markers',
    name: 'Phase',
    line: { color: 'red' },
    yaxis: 'y2',
  };

  var data = [traceMagnitude, tracePhase];

  // 定义图表的布局，包括双y轴和对数x轴
  var layout = {
    title: 'Bode Plot 示例',
    xaxis: {
      title: 'Frequency (Hz)',
      type: 'log', // 对数坐标
      autorange: true,
    },
    yaxis: {
      title: 'Magnitude',
      titlefont: { color: 'blue' },
      tickfont: { color: 'blue' },
    },
    yaxis2: {
      title: 'Phase (Degrees)',
      titlefont: { color: 'red' },
      tickfont: { color: 'red' },
      overlaying: 'y',
      side: 'right',
    },
    legend: {
      x: 0.1,
      y: 1.1,
      orientation: 'h',
    },
    margin: { t: 50, b: 50, l: 70, r: 70 },
    paper_bgcolor: 'transparent', // 图表整体背景
    plot_bgcolor: 'transparent', // 绘图区背景
  };

  // 使用 Plotly 绘制图表
  Plotly.newPlot('bode', data, layout);
}
const setData = (data) => {
  // 提取 frequency、phase 和 amplitude
  const frequencies = data.map((item) => item.frequency);
  const phases = data.map((item) => item.phase);
  const amplitudes = data.map((item) => item.amplitude);

  // 定义幅值曲线（红色）
  const traceAmplitude = {
    x: frequencies,
    y: amplitudes,
    mode: 'lines+markers',
    name: 'Amplitude',
    line: { color: 'red' },
    yaxis: 'y2', // 右侧 y 轴
  };

  // 定义相位曲线（蓝色）
  const tracePhase = {
    x: frequencies,
    y: phases,
    mode: 'lines+markers',
    name: 'Phase',
    line: { color: 'blue' },
    yaxis: 'y', // 左侧 y 轴
  };
  const remToPx = (rem) => {
    return parseFloat(getComputedStyle(document.documentElement).fontSize) * rem;
  };
  const dataToPlot = [tracePhase, traceAmplitude];

  // 定义图表的布局，包括双y轴和对数x轴
  const layout = {
    xaxis: {
      title: 'Frequency (Hz)',
      titlefont: { color: 'white', size: remToPx(0.08) }, // 1.2rem 转 px

      tickfont: { color: 'white', size: remToPx(0.05) },
      type: 'log', // 对数坐标
      autorange: true,
      gridcolor: '#fff', // 设置 x 轴网格线颜色
      linecolor: '#fff',
    },
    yaxis: {
      title: 'Phase (Degrees)',
      titlefont: { color: 'blue', size: remToPx(0.08) },
      tickfont: { color: 'blue', size: remToPx(0.04) },
      gridcolor: '#fff', // 设置 x 轴网格线颜色
    },
    yaxis2: {
      title: 'Amplitude',
      titlefont: { color: 'red', size: remToPx(0.08) },
      tickfont: { color: 'red', size: remToPx(0.04) },
      overlaying: 'y',
      side: 'right',
      gridcolor: '#fff', // 设置 x 轴网格线颜色
    },
    legend: {
      x: 0,
      y: 1.2,
      orientation: 'h',
      font: {
        color: 'white', // 设置图例文字颜色
        size: remToPx(0.05), // 设置文字大小
      },
    },
    hoverlabel: {
      font: {
        size: remToPx(0.06), // 你想要的字体大小
      },
    },
    margin: { t: 0, b: remToPx(0.15), l: 70, r: 70 },
    paper_bgcolor: 'transparent', // 图表整体背景
    plot_bgcolor: 'transparent', // 绘图区背景
  };

  // 使用 Plotly 绘制图表
  Plotly.newPlot('bode', dataToPlot, layout);
};
watch(
  () => window.nowPage,
  (newVal, oldVal) => {
    console.log('OverViewData变化了');
    nextTick().then(() => {
      drawBode();
    });
  },
  { immediate: true }
);
watch(
  () => props.Bodedata,
  (newVal, oldVal) => {
    console.log('bodeData变化了', props.id, newVal);

    setData(newVal);
  },
  { deep: true }
);
onMounted(() => {
  // initChart();
});
</script>
<style>
.bode {
  width: 100%;
  height: 75%;
}
</style>
