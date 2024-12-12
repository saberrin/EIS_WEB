<template>
  <div ref="lineChart" style="width: 100%; height: 300px;"></div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  props: {
    data: Object,  // 接收传递的数据
  },
  watch: {
    // 监听 data 的变化，确保更新时重新渲染图表
    data: {
      handler() {
        this.renderChart();  // 数据更新时重新渲染图表
      },
      deep: true,  // 使用 deep 监听以确保对象内部的值变化也能触发更新
    },
  },
  mounted() {
    this.renderChart();  // 在组件加载时渲染图表
  },
  beforeDestroy() {
    // 销毁图表实例，避免内存泄漏
    if (this.chart) {
      this.chart.dispose();
    }
  },
  methods: {
    renderChart() {
      if (this.chart) {
        // 如果已有图表实例，销毁它
        this.chart.dispose();
      }

      // 初始化新的图表实例
      this.chart = echarts.init(this.$refs.lineChart);
      const option = {
        title: {},
        xAxis: {
          type: 'category',
          data: this.data.dates || [],  // X 轴数据来源于 creationTime
          axisLabel: {
            fontSize: 14,  // 增加字体大小
            fontWeight: 'bold',  // 加粗字体
            color: '#333',  // 改变字体颜色为深灰色
          },
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            fontSize: 14,  // 增加字体大小
            fontWeight: 'bold',  // 加粗字体
            color: '#32c5e9',  // 改变字体颜色为深灰色
          },
        },
        series: [
          {
            data: this.data.values || [],  // Y 轴数据来源于 safetyRate
            type: 'line',
            smooth: true,
            itemStyle: {
              color: '#32c5e9',  // 线条颜色
            },
            lineStyle: {
              width: 3,  // 增加线条宽度
            },
            areaStyle: {
              color: 'rgba(50, 197, 233, 0.1)',  // 使用 rgba，设置透明度
            },
          },
        ],
      };
      this.chart.setOption(option);  // 设置图表选项

      // 调整图表尺寸
      this.chart.resize();
    },
  },
};
</script>

<style scoped>
/* 可以加入样式来调整图表外观 */
</style>
