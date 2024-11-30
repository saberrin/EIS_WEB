<template>
  <div class="eis-list">
    <h1 class="title">EIS 数据列表</h1>
    <div class="list-container">
      <ul class="list">
        <li v-for="eis in eisList" :key="eis.eis_id" class="list-item">
          <div class="item-content">
            <p><strong>EIS ID:</strong> {{ eis.eis_id }}</p>
            <p><strong>Time ID:</strong> {{ eis.time_id }}</p>
            <p><strong>Start Time:</strong> {{ eis.start_time }}</p>
            <p><strong>End Time:</strong> {{ eis.end_time }}</p>
            <p><strong>Real Imp:</strong> {{ eis.real_imp }}</p>
            <p><strong>Imag Imp:</strong> {{ eis.imag_imp }}</p>
            <p><strong>Impedance:</strong> {{ eis.impedance }}</p>
            <p><strong>Phase:</strong> {{ eis.phase }}</p>
            <p><strong>Frequency:</strong> {{ eis.frequency }}</p>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      eisList: [] // 用于存储后端返回的 EIS 数据列表
    };
  },
  mounted() {
    // 发送 GET 请求到后端接口获取数据
    axios.get('http://127.0.0.1:9090/eis/index')
      .then(response => {
        // 请求成功，将数据保存到 eisList 数组中
        this.eisList = response.data;
      })
      .catch(error => {
        // 请求失败，处理错误
        console.error('Error fetching data:', error);
      });
  }
};
</script>

<style scoped>
/* 局部样式，只应用于当前组件 */
.eis-list {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.title {
  font-size: 24px;
  margin-bottom: 20px;
}

.list-container {
  max-height: 400px; /* 设置容器的最大高度 */
  overflow-y: auto; /* 添加垂直滚动条 */
}

.list {
  list-style: none;
  padding: 0;
}

.list-item {
  margin-bottom: 20px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
}

.item-content {
  font-size: 16px;
  color: #333; /* 修改文字颜色 */
}
</style>
