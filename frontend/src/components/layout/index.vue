<template>
  <!-- 主体区域 -->
  <!-- 左侧功能按钮区域 -->
  <div class="left-sidebar">
    <div
      v-for="(item, index) in props.containerData"
      :key="index"
      :class="{ active: selectedIndex == index }"
      @click="changeSelectedIndex(index, item)"
    ></div>
  </div>

  <!-- 右侧运行信息区域 -->
  <!-- <div class="right-sidebar"> -->
  <!-- 标题 -->
  <!-- <div class="panel-title">运行信息</div> -->

  <!-- 关键指标 -->
  <!-- <div class="metrics">
      <div v-for="(item, index) in metrics" :key="index" class="metric-item">
        <span class="label">{{ item.label }}</span>
        <span class="value">{{ item.value }}</span>
      </div>
    </div>
  </div> -->
  <!-- 底部区域 -->
  <div class="footer">
    <div class="footer-left" style="overflow: hidden">
      <!-- 标题部分 -->
      <!-- <div class="header">
        <h2>基本情况</h2>
        <span class="subtitle">SMOOTHING METRICS</span>
      </div> -->

      <!-- 信息表格 -->
      <div class="info-grid">
        <div class="info-row">
          <div>
            <span class="label">总巡检测数</span>
            <span class="value highlight">{{ overViewData?.totalInspections || 0 }}</span>
          </div>
          <div>
            <span class="label">总测量数</span>
            <span class="value highlight">{{
              overViewData?.totalMeasurements || 0
            }}</span>
          </div>
          <div>
            <span class="label">本月巡检测数</span>
            <span class="value highlight">{{
              overViewData?.monthlyInspections || 0
            }}</span>
          </div>

          <div>
            <span class="label">安全风险样本库总量</span>
            <span class="value highlight">{{
              overViewData?.totalRiskySamples || 0
            }}</span>
          </div>
          <div>
            <span class="label">累计排查隐患总数</span>
            <span class="value highlight">{{
              overViewData?.cumulativeRiskIdentifications || 0
            }}</span>
          </div>
          <div>
            <span class="label">最近更新时间</span>
            <span class="value highlight">{{
              overViewData?.lastUpdatedAt.replace('T', ' ').slice(0, 16) || 0
            }}</span>
          </div>
        </div>

        <div style="font-size: 0.08rem">
          <div class="warnning">
            <img src="../../assets/img/warn.png" style="width: 0.15rem" alt="" />
            历史预警信息
          </div>
          <ul class="warnningUl" style="overflow-y: scroll; height: 100%">
            <li v-for="(item, index) in alarmData" :key="index" class="liStyle">
              {{ item }}
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="footer-right">
      <div class="chart-placeholder">
        <div style="width: 85%">
          <DoubleLineChart
            :id="'DoubleLineChart3'"
            :legendOrient="'horizontal'"
            :isShowLegend="false"
            :HistoryData="ContainerMetricData"
          />
        </div>
      </div>
      <div class="legend">
        <div><i class="green"></i>标准差</div>
        <div><i class="blue"></i>阻抗均值</div>
        <div><i class="red"></i>变异系数</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import DoubleLineChart from '../DoubleLineChart.vue';
import store from '../../store/index';
import { GetPackMetricsBycontainer } from '../../api/packApi.ts';
const props = defineProps({
  overViewData: {
    type: Object,
  },
  alarmData: {
    type: Array,
  },
  containerData: {
    type: Array,
  },
});
watch(
  () => store.state.CurrentContainer,
  (newVal, oldVal) => {
    console.log('CurrentContainer变化了', newVal);

    GetPackMetricsBycontainerFun();
  },
  { deep: true }
);

// 模拟数据
const currentTime = ref('2025-02-24 16:27:08');
const daysCount = ref(365);
const activePower = ref('42.320');
const reactivePower = ref('17.610');
const selectedIndex = ref(1);
const ContainerMetricData = ref(null);

// 运行信息
const metrics = ref([
  { label: '运行功率 (kW)', value: '49.320' },
  { label: '无功功率 (kVar)', value: '17.610' },
  { label: '可用容量 (MWh)', value: '66.930' },
  { label: '可用电量 (MWh)', value: '7.927' },
  { label: '充电功率 (kW)', value: '74.954' },
  { label: '放电功率 (kW)', value: '9.860' },
]);
// SOH 进度条数值
const sohValue = ref(38.9);

// 按钮组
const buttons = ref(['充放电', '存储监控', '空调', '关闭']);
// 返回按钮点击事件
const handleBack = () => {
  // 这里可以添加实际返回逻辑，比如路由跳转
  console.log('点击返回');
};
const changeSelectedIndex = (index, item) => {
  selectedIndex.value = index;
  store.commit('setCurrentContainer', item);
};
//底部右侧图表数据
const GetPackMetricsBycontainerFun = async () => {
  let res = await GetPackMetricsBycontainer(store.state.CurrentContainer.id);
  ContainerMetricData.value = res.packHistograms;
};
onMounted(() => {
  GetPackMetricsBycontainerFun();
});
</script>

<style scoped lang="scss">
.ems-container {
  position: absolute;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.left-sidebar {
  position: absolute;
  left: 0;
  top: 35%;
  transform: translateY(-50%);
  width: 0.5rem;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0.1rem;
  div {
    width: 100%;
    height: 0.3rem;
    line-height: 0.3rem;
    background-color: #164d5060;
    color: #fff;
    font-size: 0.08rem;
    text-align: center;
    margin-bottom: 0.15rem;
    cursor: pointer;
  }
  div.active {
    background: #164d50;
  }
}

.sidebar-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 60px;
  margin-bottom: 10px;
  background-color: transparent;
  border: none;
  color: white;
  cursor: pointer;
}

.large-button {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: green;
  border: none;
  margin-top: auto;
  cursor: pointer;
}

.center-content {
  flex: 1;
  background-color: #000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.right-sidebar {
  position: absolute;
  right: 0;
  top: 6%;
  height: 35rem;
  width: 20rem;

  background-color: #03050690;
  padding: 1.5rem;
  box-sizing: border-box;
  color: white;
}
/* 标题 */
.panel-title {
  font-size: 38px;
  font-weight: bold;
  text-align: left;
  margin-bottom: 2.5rem;
}

/* 指标 */
.metrics {
  gap: 10px;
  margin-bottom: 2.5rem;
}

.metric-item {
  display: flex;
  padding: 1rem;
  margin-bottom: 2rem;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-radius: 5px;
}

.label {
  font-size: 38px;

  color: #bbb;
}

.value {
  font-size: 38px;

  font-weight: bold;
  color: #49f;
}

/* SOH 进度条 */
.soh-container {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.soh-label {
  font-size: 14px;
}

.soh-bar {
  flex: 1;
  height: 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  position: relative;
}

.soh-progress {
  height: 100%;
  background: #49f;
  border-radius: 4px;
}

/* 按钮组 */
.button-group {
  display: flex;
  gap: 10px;
}

.action-button {
  flex: 1;
  padding: 8px;
  background: #222;
  border: 1px solid #49f;
  color: #49f;
  border-radius: 5px;
  cursor: pointer;
  text-align: center;
  transition: 0.3s;
}

.action-button:hover {
  background: #49f;
  color: #fff;
}
.running-info {
  display: flex;
  flex-direction: column;
}

.info-item {
  margin-bottom: 10px;
}

.control-options {
  margin-top: 10px;
}

.footer {
  display: flex;
  width: 100%;
  height: 2rem;
  position: absolute;
  bottom: 0;
  justify-content: space-between;
  background: rgba(0, 0, 0, 0.7); /* 半透明黑色背景 */

  color: white;
  padding: 0.1rem 0.11rem;
}
.footer-left {
  width: 39%;
  padding: 0.1rem;

  background-color: #1e202350;
  /* 标题部分 */
  .header {
    width: 100%;
    display: flex;
    align-items: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.3);
    padding-bottom: 8px;
    margin-bottom: 12px;
  }

  .header h2 {
    font-size: 18px;
    margin: 0;
  }

  .subtitle {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.6);
    margin-left: 8px;
  }

  /* 信息网格 */
  .info-grid {
    display: flex;
    height: 100%;
    div {
      width: 50%;
      padding: 0 0.05rem;
    }
  }

  .info-row {
    height: 100%;
    div {
      display: flex;
      align-items: center;
      width: 100%;
      height: 0.29rem;
    }
  }

  .label {
    color: rgba(255, 255, 255, 0.8);
    text-align: left;
    font-size: 0.1rem;
  }

  .value {
    flex: 1;
    text-align: right;
    font-size: 0.09rem;
    padding-right: 0.1rem;
    font-weight: bold;
  }
}
.footer-right {
  position: relative;
  width: 60%;
  background-color: #1e202350;
}
.basic-info-item {
  margin-bottom: 5px;
}

.chart-placeholder,
.placeholder {
  margin-top: 0.2rem;
  display: flex;
  width: 100%;
  height: 90%;
}
.legend {
  display: flex;
  flex-direction: column;
  position: absolute;
  right: 0.2rem;
  top: 0.6rem;
  font-size: 0.08rem;
}
.warnning {
  box-sizing: border-box;
  height: 0.3rem;
  display: flex;
  align-items: center;
  gap: 0.1rem;
  width: 100% !important;
  background: url('../../assets/img/warnbg.png') no-repeat;
  background-size: cover; /* 保持比例，填充整个区域 */
  background-position: center; /* 居中显示 */
  padding: 0.05rem !important;
  font-size: 0.09rem;
}
ul::-webkit-scrollbar {
  /*滚动条整体样式*/
  width: 0.04rem; /*高宽分别对应横竖滚动条的尺寸*/
  height: 0.01rem;
}
ul::-webkit-scrollbar-thumb {
  /*滚动条里面小方块*/
  border-radius: 10px;
  height: 0.03rem;
  -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  // background: #E5E5E5;
  background: red;
}
ul::-webkit-scrollbar-track {
  /*滚动条里面轨道*/
  -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  border-radius: 10px;
  background: #ffffff;
}
.liStyle {
  height: 0.2rem;
  line-height: 0.2rem;
}
.green {
  display: inline-block;
  width: 0.06rem;
  height: 0.04rem;
  background: #91cc75;
  margin-right: 0.06rem;
}
.blue {
  display: inline-block;
  width: 0.06rem;
  height: 0.04rem;
  background: #37477b;
  margin-right: 0.06rem;
}
.red {
  display: inline-block;
  width: 0.06rem;
  height: 0.04rem;
  background: #fac858;
  margin-right: 0.06rem;
}
</style>
