<template>
  <div class="module-container">
    <div class="left-section">
      <!-- 蓝色圆环 -->
      <div class="circle-container">
        <div class="circle" :style="getCircleStyle(50, 'lightblue')">
          <span class="percentage-text">50%</span>
        </div>
      </div>
      <!-- 橙色圆环 -->
      <div class="circle-container">
        <div class="circle" :style="getCircleStyle(30, 'orange')">
          <span class="percentage-text">30%</span>
        </div>
      </div>
    </div>

    <div class="right-section">
      <div class="radar-chart">
        <RadarChart :data="radarData" />
      </div>
      <div class="line-chart">
        <LineChart :data="lineData" />
      </div>
    </div>
  </div>
</template>

<script>
import RadarChart from "./RadarChart"; // 引入雷达图组件
import LineChart from "./LineChart"; // 引入折线图组件

export default {
  props: {
    packMetrics: {
      type: Object,
      required: true, // cdata 从父组件传递进来
    },
  },
  components: {
    RadarChart,
    LineChart
  },
  data() {
    return {
      // 初始化雷达图数据
      radarData: {
        maxData: [],
        minData: []
      },
      // 初始化折线图数据
      lineData: {
        dates: [],
        values: []
      }
    };
  },
  watch: {
    // 监听父组件传递的 packMetrics 数据变化
    packMetrics: {
      handler(newVal) {
        if (newVal) {
          // 更新雷达图数据
          if (newVal.diagnostics) {
            this.radarData.maxData = Object.values(newVal.diagnostics.max || {});
            this.radarData.minData = Object.values(newVal.diagnostics.min || {});
          }

          // 更新折线图数据
          this.lineData.dates = newVal.packHealthHistory.map((item) => item.creationTime);
          this.lineData.values = newVal.packHealthHistory.map((item) => item.safetyRate);
        }
      },
      immediate: true  // 初次渲染时立即调用 handler
    }
  },
  methods: {
    // 动态生成圆环的样式
    getCircleStyle(percentage, color) {
      const lightColor = this.getLightenedColor(color);
      return {
        background: "transparent",
        border: `6px solid ${lightColor}`
      };
    },
    // 获取更浅的颜色
    getLightenedColor(color) {
      const colorMap = {
        lightblue: 'rgb(173, 216, 230)', // 浅蓝色
        orange: 'rgb(255, 179, 71)', // 浅橙色
      };
      return colorMap[color] || color;
    }
  }
};
</script>

<style scoped>
.module-container {
  display: flex;
  justify-content: space-between;
  padding: 20px;
  height: 100%; 
}

.left-section {
  width: 30%;
  display: flex;
  justify-content: space-between; 
  align-items: flex-start; 
  flex-direction: row; 
}

.circle-container {
  text-align: center;
}

.circle {
  width: 100px;
  height: 100px;
  border-radius: 50%; 
  background-color: transparent; 
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  color: lightblue; /* 默认文字颜色 */
  border: 6px solid transparent; /* 默认边框透明 */
  border-image: conic-gradient(lightblue 0% 50%, transparent 50% 100%) 1; /* 默认圆环的样式 */
}

.percentage-text {
  color: lightblue; /* 50%标志的颜色 */
  font-size: 18px;
}

.right-section {
  width: 65%;
  display: flex;
  flex-direction: column; /* 垂直排列 */
  justify-content: flex-start; /* 保持顶部对齐 */
  height: 100%; /* 让右侧部分的高度充满父容器 */
}
.line-chart {
  width: 180%; /* Keep the width as is */
  height: 90%; /* Ensure the height is still half of the container */
  margin-top: -19%; /* Add some top margin for spacing */
  position: relative; /* Enable positioning */
  top: -20px; /* Move it upwards */
  left: -380px; /* Move it towards the left */
  align-self: flex-start; /* Keep it aligned to the top */
}

.radar-chart {
  margin-bottom: 20px;
  position: relative;
  top: -70px;  /* Adjust to move upwards */
  right: -80px; /* Adjust to move towards the right */
}
</style>
