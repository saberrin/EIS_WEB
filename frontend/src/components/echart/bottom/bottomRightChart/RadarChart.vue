<template>
  <div ref="radarChart" style="width: 100%; height: 300px;"></div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  props: {
    data: Object,  // 接收传递的数据
  },
  watch: {
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
    if (this.chart) {
      this.chart.dispose();  // 销毁图表实例，避免内存泄漏
    }
  },
  methods: {
    renderChart() {
      if (this.chart) {
        this.chart.dispose();  // 如果已有图表实例，销毁它
      }

      this.chart = echarts.init(this.$refs.radarChart);
      const option = {
        title: {
        
          left: 'center',
          top: 10,
          textStyle: {
            fontSize: 20,
            fontWeight: 'bold',
            color: '#fff',
          },
        },
        radar: {
          indicator: [
            { name: 'SEI膜参数' },
            { name: '温度预测' },
            { name: '锂枝晶参数' },
            { name: '电解液参数' },
            { name: '极化电位' },
            { name: '锂离子电导率参数' },
          ],
          shape: 'circle',
          splitArea: {
            areaStyle: {
              color: ['rgba(0, 0, 0, 0)', 'rgba(0, 0, 0, 0.1)', 'rgba(0, 0, 0, 0.2)'],
            },
          },
        },
        series: [
          {
            name: '数据',
            type: 'radar',
            data: [
              {
                value: this.data.maxData || [],
                name: '最大值',
                itemStyle: {
                  color: 'rgba(55, 162, 218, 1)',  // 设置最大值颜色为更饱和的蓝色
                },
                areaStyle: {
                  color: 'rgba(55, 162, 218, 0.4)',  // 最大值的区域填充颜色增加透明度
                },
                lineStyle: {
                  width: 4,  // 增加最大值的线条宽度
                },
                emphasis: {
                  itemStyle: {
                    color: 'rgba(255, 159, 127, 1)',  // 鼠标悬停时变为橙色
                  },
                  label: {
                    show: true,
                    fontSize: 18,
                    color: '#fff',
                  },
                },
              },
              {
                value: this.data.minData || [],
                name: '最小值',
                itemStyle: {
                  color: 'rgba(255, 159, 127, 1)',  // 设置最小值颜色为橙色
                },
                areaStyle: {
                  color: 'rgba(255, 159, 127, 0.4)',  // 最小值的区域填充颜色增加透明度
                },
                lineStyle: {
                  width: 4,  // 增加最小值的线条宽度
                },
                emphasis: {
                  itemStyle: {
                    color: 'rgba(55, 162, 218, 1)',  // 鼠标悬停时变为蓝色
                  },
                  label: {
                    show: true,
                    fontSize: 18,
                    color: '#fff',
                  },
                },
              },
            ],
          },
        ],
        animationDuration: 1000,  // 设置加载时动画持续的时间
        animationEasing: 'cubicOut',  // 设置动画的缓动效果
        animationDelay: (idx) => idx * 100,  // 每个数据项的动画延迟
      };
      this.chart.setOption(option);
      this.chart.resize();  // 调整图表尺寸
    },
  },
};
</script>

<style scoped>
/* 调整 radarChart 容器的尺寸 */
div[ref="radarChart"] {
  width: 100%;
  height: 300px;  /* 根据需要修改高度 */
  border-radius: 8px; /* 圆角边框 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 添加轻微阴影效果 */
}
</style>
