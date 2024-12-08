<template>
  <div ref="lineChart" style="width: 100%; height: 300px;"></div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  props: {
    data: Object,
  },
  watch: {
    data: 'renderChart',
  },
  mounted() {
    this.renderChart();
  },
  methods: {
    renderChart() {
      const chart = echarts.init(this.$refs.lineChart);
      const option = {
        title: {
          text: '折线图',
        },
        xAxis: {
          type: 'category',
          data: this.data.dates,
        },
        yAxis: {
          type: 'value',
        },
        series: [
          {
            data: this.data.values,
            type: 'line',
            smooth: true,
            itemStyle: {
              color: '#4CAF50', // 自定义颜色
            },
          },
        ],
      };
      chart.setOption(option);
    },
  },
};
</script>

<style scoped>
/* 可以加入样式来调整图表外观 */
</style>
