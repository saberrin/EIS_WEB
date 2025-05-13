<template>
  <div style="height: 100%; width: 100%; position: relative">
    <div id="heatMapLine1" class="heatMapLine1" />
  </div>
</template>

<script setup>
// import * as echarts from 'echarts';
import { onMounted, ref, onBeforeUnmount, watch, nextTick } from 'vue';

var chart = ref(null);
var Timer;

const props = defineProps({
  id: {
    type: String,
  },
  NyquistData: {
    type: Object,
  },
});
// 生成x轴数据 (0到10，间隔0.2)
// var xData = [];
// for (var x = 0; x <= 10; x += 0.2) {
//   xData.push(parseFloat(x.toFixed(1)));
// }

// // 生成y轴数据 (-5到5，间隔0.2)
// var yData = [];
// for (var y = -5; y <= 5; y += 0.2) {
//   yData.push(parseFloat(y.toFixed(1)));
// }

// HSV到RGB转换函数
function hsvToRgb(h, s, v) {
  h /= 60;
  var c = v * s;
  var x = c * (1 - Math.abs((h % 2) - 1));
  var m = v - c;

  var rgb;
  if (h < 1) rgb = [c, x, 0];
  else if (h < 2) rgb = [x, c, 0];
  else if (h < 3) rgb = [0, c, x];
  else if (h < 4) rgb = [0, x, c];
  else if (h < 5) rgb = [x, 0, c];
  else rgb = [c, 0, x];

  return [
    Math.round((rgb[0] + m) * 255),
    Math.round((rgb[1] + m) * 255),
    Math.round((rgb[2] + m) * 255),
  ];
}

// 生成热力图数据
// var heatmapData = [];
// var minValue = Infinity;
// var maxValue = -Infinity;

// xData.forEach(function (x, xIndex) {
//   yData.forEach(function (y, yIndex) {
//     var sinValue = 2 * Math.sin(x);
//     var value = Math.exp(-Math.pow(y - sinValue, 2) / 0.5);
//     heatmapData.push([xIndex, yIndex, value]);

//    if (value < minValue) minValue = value;
//     if (value > maxValue) maxValue = value;
//   });
// });

// 创建HSV颜色映射
function createHsvColorMap(min, max) {
  var colorMap = [];
  // 从蓝色(240°)到红色(0°)的渐变
  for (var i = 0; i <= 100; i++) {
    var ratio = i / 100;
    var hue = 240 * (1 - ratio); // 240°(蓝) → 0°(红)
    var rgb = hsvToRgb(hue, 1, 1);
    colorMap.push([min + (max - min) * ratio, `rgb(${rgb[0]}, ${rgb[1]}, ${rgb[2]})`]);
  }
  return colorMap;
}

// function createHsvColorMap(min, max) {
//   var colorMap = [];
//   // 从蓝色(240°)到红色(0°)的渐变
//   for (var i = 0; i <= 100; i++) {
//     var ratio = i / 100;
//     var hue = 240 * (1 - ratio); // 240°(蓝) → 0°(红)
//     var saturation = 1; // 增加饱和度为 1，使颜色更鲜艳
//     var value = 0.8 + 0.2 * ratio; // 增加亮度使颜色更明亮

//     // 调整 HSV 到 RGB 的转换，使颜色更加鲜明
//     var rgb = hsvToRgb(hue, saturation, value);
//     colorMap.push([min + (max - min) * ratio, `rgb(${rgb[0]}, ${rgb[1]}, ${rgb[2]})`]);
//   }
//   return colorMap;
// }

const initChart = () => {
  chart.value = echarts.init(document.getElementById('heatMapLine1'));

  const option = {
    graphic: [
      {
        type: 'text',
        right: 'center',
        bottom: '10%',
        style: {
          text: 'Zre(μΩ)',
          fill: '#fff',
          fontSize: '0.08rem',
        },
      },
      {
        type: 'text',
        left: '3%',
        top: '20%',
        rotation: Math.PI / 2,

        style: {
          text: 'Zim(μΩ)',
          fill: '#fff',
          fontSize: '0.08rem',
        },
      },
    ],
    grid: {
      width: '100%',
      left: '10%',
      top: '2%',
      right: '25%',
      bottom: '24%',
      containLabel: true,
    },
    xAxis: {
      type: 'value',
      // data: xData,
      data: [],
      splitArea: {
        show: true,
      },

      axisLabel: {
        textStyle: {
          color: '#fff',
          fontSize: '0.08rem',
        },
        // interval: function (index) {
        //   // 只显示5个刻度：0, 2.5, 5, 7.5, 10
        //   return index % xInterval === 0;
        // },
      },
    },
    yAxis: {
      type: 'value',
      // data: yData,
      data: [],

      splitArea: {
        show: true,
      },
      axisLabel: {
        textStyle: {
          color: '#fff',
          fontSize: '0.08rem',
        },
      },
    },
    // visualMap: {
    //   min: 0,
    //   max: maxValue,
    //   calculable: true,
    //   inRange: {
    //     color: createHsvColorMap(0, maxValue).map((item) => item[1]),
    //   },
    //   orient: 'vertical',
    //   right: '0%',
    //   bottom: '25%',
    //   textStyle: {
    //     color: '#000',
    //   },
    // },
    series: [
      {
        name: '热力图',
        type: 'heatmap',
        // data: heatmapData,

        data: [],
        label: {
          show: false,
        },
        itemStyle: {
          borderColor: 'rgba(255,255,255,0.3)',
          borderWidth: 0.3,
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0,0,0,0.5)',
          },
        },
      },
      {
        name: '三角曲线',

        type: 'line',
        data: [],

        symbol: 'none',
        lineStyle: {
          color: '#000',
          width: 3,
        },
      },
    ],
  };
  // console.log(maxValue, 'maxValue');

  chart.value.setOption(option);
};
// 归一化函数
function normalizeData(data) {
  const min = Math.min(...data);
  const max = Math.max(...data);
  return data.map((value) => (value - min) / (max - min));
}
function getHeatmapBounds(heatmapdata) {
  let xMin = Infinity,
    xMax = -Infinity;
  let yMin = Infinity,
    yMax = -Infinity;

  for (const [x, y] of heatmapdata) {
    if (x < xMin) xMin = x;
    if (x > xMax) xMax = x;
    if (y < yMin) yMin = y;
    if (y > yMax) yMax = y;
  }

  return { xMin, xMax, yMin, yMax };
}

const setData = (heatmapdata, formattedData, xmin, xmax, ymin, ymax) => {
  const { xMin, xMax, yMin, yMax } = getHeatmapBounds(heatmapdata);
  const zValues = heatmapdata.map((item) => item[2]);
  const zMin = Math.min(...zValues);
  const zMax = Math.max(...zValues);

  console.log('Z 最小最大值:', zMin, zMax);
  console.log('Minmax:', xMin, xMax, yMin, yMax); // 输出最小值
  chart.value.setOption({

    xAxis: {
      min: Math.round(xmin),  // 强制转换为整数
      max: Math.round(xmax),  // 强制转换为整数
    },
    yAxis: {
      min: Math.round(ymin),  // 强制转换为整数
      max: Math.round(ymax),  // 强制转换为整数
    },
    visualMap: {
      min: zMin,
      max: zMax,
      calculable: true,
      inRange: {
        color: createHsvColorMap(0, zMax).map((item) => item[1]),
      },
      orient: 'vertical',
      right: '2%',
      bottom: '25%',
      textStyle: {
        color: '#000',
      },
    },
    series: [
      {
        type: 'heatmap',
        data: heatmapdata,
      },
      {
        type: 'line',
        data: formattedData,
      },
    ],
  });
};
watch(
  () => props.NyquistData,
  (newVal, oldVal) => {
    console.log('NyquistData变化了', newVal);
    const formattedData = newVal?.metrics?.map((item) => [
      Number(item.realImpedance.toFixed(0)) - 1,
      Number(item.imaginaryImpedance.toFixed(0)),
    ]);

    let minX,
      maxX,
      minY,
      maxY = 0;
    if (newVal?.xAxisRange && newVal?.yAxisRange) {
      ({ min: minX, max: maxX } = newVal.xAxisRange);
      ({ min: minY, max: maxY } = newVal.yAxisRange);
    }
    console.log(newVal.heatMap, 'ny图数据');

    setData(newVal.heatMap, formattedData, minX, maxX, minY, maxY);
  },
  { deep: true }
);
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
.heatMapLine1 {
  width: 98%;
  height: 75%;
  overflow: hidden;  /* 确保内容不会超出容器 */
}

#heatMap {
  position: absolute !important;
  top: 0;
  width: 100%;
  height: 80%;
}
</style>
