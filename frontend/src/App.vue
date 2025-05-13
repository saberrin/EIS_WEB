<script setup lang="ts">
import { ref, computed } from 'vue';
import store from './store/index.js';

import ThreeView from './view/ThreeView.vue';
import DataView from './view/DataView.vue';
import Header from './components/layout/header.vue';

const currentComponent = ref<keyof typeof components>('ThreeView');
window.nowPage = 'ThreeView';
// 组件映射
const views: Record<string, Component> = {
  ThreeView,
  DataView,
};
// 监听子组件事件
const handleCustomEvent = (viewName) => {
  console.log('父组件收到事件:', viewName);
  currentComponent.value = viewName;
  store.commit('setCurrentComponent', viewName);

  window.nowPage = viewName;
};
const open = computed(() => {
  return store.state.showAbout;
});
function onCancel() {
  // 点击蒙层或 Esc 键或右上角叉或取消按钮的回调
  console.log('cancel');
  store.commit('setshowAbout', false);
}
</script>

<template>
  <Header @customEvent="handleCustomEvent" />
  <div class="aboutDialog">
    <Dialog v-model:open="open" @cancel="onCancel">
      <template #default>
        <div class="about">
          <h2>电芯级阻抗谱检测与AI辅助管理技术简介</h2>
          <p>
            本技术由厦门大学物理系及其产业化公司玄盾睿安开发，旨在解决锂电池热失控预警问题，在电池失效、引发火灾之前，通过检测电芯的阻抗谱，利用物理数据模型反向分析，找出故障电芯，有效防止事故发生。技术核心是电芯级智能阻抗谱巡检仪和阻抗谱大数据处理系统，具有如下先进功能：
          </p>
          <p>
            （1）巡检仪：通过给电池叠加一个不同频率的交流小信号，检测电芯在对应频率下的阻抗参数，形成阻抗谱（EIS），实现对电芯的无损检测。巡检仪通过快插接口接入储能柜，对每颗电芯建立独立信道，在线无损采集电芯级阻抗谱数据，构建电池物理-化学状态“软探针”。
          </p>
          <p>
            （2）大数据处理系统：作为后端决策大脑，依托算力对海量阻抗谱数据进行清洗、建模与AI分析，构建电池健康画像，预测剩余寿命（RUL），并生成可视化报告与预警建议，实现从数据到决策的闭环管理，有效保障储能电站的用电安全。
          </p>
        </div>
      </template>
    </Dialog>
  </div>
  <component
    v-if="currentComponent == 'ThreeView'"
    :is="views['ThreeView']"
    @customEvent="handleCustomEvent"
  />
  <component
    v-if="currentComponent == 'DataView'"
    :is="views['DataView']"
    @customEvent="handleCustomEvent"
  />
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
.about {
  h2 {
    font-size: 0.15rem;
    text-align: center;
    color: #fff;
  }
  p {
    font-size: 0.09rem;
    text-indent: 0.1rem;
    line-height: 0.18rem;
    text-align: justify;
    color: #fff;
  }
}
</style>
<style>
.aboutDialog {
  .m-dialog-wrap .m-dialog {
    width: 5rem !important;
  }
  .dialog-body-wrap {
    border: 0.01rem solid #fff !important;
    border-radius: 0.1rem !important;
    height: 3rem !important;
    right: 0 !important;
    padding: 0.1rem 0.3rem !important;
    background: url('/dialogBg.png') no-repeat 100% 100%/ 100% 100% !important;
  }
  .dialog-footer {
    display: none;
  }

  .m-dialog-wrap .m-dialog {
    top: 0 !important;
  }
}
.m-dialog-wrap .m-dialog .dialog-body-wrap .close-action svg {
  font-size: 1rem !important;
  width: 2rem !important;
  height: 2rem !important;
  color: #fff !important;
}
</style>
