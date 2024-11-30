<template>
  <div class="module-container">
    <div class="left-section">
      <div class="circle-container">
        <!-- 蓝色圆环 -->
        <div class="circle" :style="getCircleStyle(50, 'lightblue')">
          <span class="percentage-text">50%</span>
        </div>
      </div>
      <div class="circle-container">
        <!-- 橙色圆环 -->
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
import RadarChart from "./RadarChart"; // 雷达图组件
import LineChart from "./LineChart"; // 折线图组件

export default {
  components: {
    RadarChart,
    LineChart,
  },
  data() {
    return {
      // 雷达图数据：最大值和最小值
      radarData: {
        maxData: [80, 70, 90, 85, 60, 75], // 最大值数据
        minData: [40, 30, 50, 45, 20, 35], // 最小值数据
      },
      // 折线图数据
      lineData: {
        dates: ["2024-11-01", "2024-11-02", "2024-11-03", "2024-11-04", "2024-11-05"],
        values: [10, 50, 55, 20, 65],
      },
    };
  },
  methods: {
    // 动态生成圆环的边框样式
    getCircleStyle(percentage, color) {
      // 获取更浅的颜色
      const lightColor = this.getLightenedColor(color);
      return {
        background: "transparent", // 确保圆环内部透明
        border: `6px solid ${lightColor}`, // 设置边框颜色为更浅的颜色
      };
    },
    // 获取更浅的颜色
    getLightenedColor(color) {
      const colorMap = {
        lightblue: 'rgb(173, 216, 230)', // 浅蓝色
        orange: 'rgb(255, 179, 71)', // 浅橙色
      };
      return colorMap[color] || color;
    },
  },
};
</script>

<style scoped>
.module-container {
  display: flex;
  justify-content: space-between;
  padding: 20px;
  height: 100%; /* 使整个模块充满父容器的高度 */
}

.left-section {
  width: 30%;
  display: flex;
  justify-content: space-between; /* 左侧容器内并排显示圆环 */
  align-items: flex-start; /* 调整圆环位于模块左上角 */
  flex-direction: row; /* 确保圆环水平排列 */
}

.circle-container {
  text-align: center;
}

.circle {
  width: 100px;
  height: 100px;
  border-radius: 50%; /* 确保圆形 */
  background-color: transparent; /* 圆环内部透明 */
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
