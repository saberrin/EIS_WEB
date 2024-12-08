<template>
  <div ref="radarChart" style="width: 100%; height: 300px;"></div>
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
      const chart = echarts.init(this.$refs.radarChart);
      const option = {
        title: {
          text: '雷达图',
        },
        radar: {
          indicator: [
            { name: '指标1', },
            { name: '指标2', },
            { name: '指标3',  },
            { name: '指标4',  },
            { name: '指标5',  },
            { name: '指标6', },
          ],
        },
        series: [
          {
            name: '数据',
            type: 'radar',
            data: [
              {
                value: this.data.maxData,
                name: '最大值',
                itemStyle: { color: 'green' },
              },
              {
                value: this.data.minData,
                name: '最小值',
                itemStyle: { color: 'red' },
              },
            ],
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
