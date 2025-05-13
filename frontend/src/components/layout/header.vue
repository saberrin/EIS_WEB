<script setup lang="ts">
import { ref, onMounted, defineEmits, watch, nextTick, computed } from 'vue';
import store from '../../store/index';
const emit = defineEmits(['customEvent']);
const isShow = ref(false);
const time = ref(new Date().toLocaleTimeString());
const seletIndex = ref(1);
const updateTime = () => {
  time.value = new Date().toLocaleTimeString();
};
const go = (page: string) => {
  if (page == 'DataView') {
    seletIndex.value = 1;
  }
  emit('customEvent', page);
};
const exportFun = () => {
  seletIndex.value = 3;
  store.commit('setshowTimeChose', true);
};
const showAbout = () => {
  store.commit('setshowAbout', true);
};
const CurrentComponent = computed(() => {
  return store.state.CurrentComponent;
});
watch(
  () => store.state.CurrentComponent,
  (newVal, oldVal) => {
    console.log(newVal, '当前组件');

    isShow.value = newVal == 'ThreeView' ? false : true;
  },
  { immediate: true }
);
onMounted(() => {
  setInterval(updateTime, 1000);
});
</script>

<template>
  <!-- 头部区域 -->
  <div class="header">
    <div class="header-left">
      <span class="system-name">电化学阻抗谱大数据可视化平台</span>
    </div>
    <div class="header-right">
      <div
        class="tab"
        :class="{ active: seletIndex == 1 && CurrentComponent != 'DataView' }"
        @click="go('ThreeView')"
      >
        首页
      </div>
      <div class="tab" @click="showAbout">关于</div>
      <div class="tab" :class="{ active: seletIndex == 3 }" @click="exportFun()">
        数据导出
      </div>

      <div class="tab">{{ time }}</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.header {
  display: flex;
  background: url('../../assets/img/headBg.png') no-repeat 100% 100%/100% 100%;
  height: 0.4rem;
  padding-left: 0.4rem;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  .system-name {
    font-size: 0.15rem;
    font-weight: 800;
  }
  .header-right {
    width: 4rem;
    height: 100%;
    display: flex;
    align-items: center;
    padding-left: 0.1rem;
    background: url('../../assets/img/headerRight.png') no-repeat 100% 100%/100% 100%;
    div {
      display: flex;
      width: 6rem;
      justify-content: center;
      cursor: pointer;
      text-align: center;
      align-items: center;
      height: 60%;
    }
    .tab {
      font-size: 0.1rem;
      font-weight: 600;
    }
  }
  h1 {
    font-size: 24px;
  }
  .active {
    font-size: 0.1rem;

    background: url('../../assets/img/tabBg.png') no-repeat 100% 100%/100% 100%;
  }
  .dashboard-info {
    display: flex;
    gap: 15px;
  }
}
</style>
