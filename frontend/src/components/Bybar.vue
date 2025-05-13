<template>
  <div class="bar">
    <div class="minmax">
      <div>{{ props.min?.toExponential(2) }}</div>
      <div>{{ props.max?.toExponential(2) }}</div>
    </div>

    <div class="line">
      <div class="blue"></div>
      <div class="green"></div>
      <div class="orange"></div>
      <div class="red"></div>
      <span class="point" :style="{ left: ratio }"
        ><span class="value">{{ props.byData?.toExponential(2) }}</span></span
      >
    </div>
  </div>
</template>

<script setup>
import * as echarts from 'echarts';
import { onMounted, ref, onBeforeUnmount, watch, nextTick, computed } from 'vue';

var chart = ref(null);
var option;
const ratio = computed(() => {
  if (props.max === props.min) return '0%'; // 防止除零错误
  const value =
    (Number(props.byData?.toExponential(2)) - props.min) / (props.max - props.min);
  return `${(value * 100).toExponential(2)}%`; // 转换为百分比
});

const props = defineProps({
  id: {
    type: String,
  },

  byData: {
    type: String,
  },
  min: {
    type: String,
  },
  max: {
    type: String,
  },
});

onMounted(() => {
  console.log('byData变化了', props.byData);

  // initChart();
});
</script>
<style lang="scss" scoped>
.bar {
  position: relative;
  width: 70%;
  height: 0.3rem;
  display: flex;
  align-items: center;

  .minmax {
    width: 100%;
    height: 0.05rem;
    position: absolute;
    bottom: 0.05rem;
    display: flex;
    justify-content: space-between;
    color: #fff;
  }

  .line {
    width: 100%;
    position: relative;
    display: grid;
    grid-template-columns: repeat(4, 1fr);

    div {
      height: 0.05rem;
    }

    .value {
      width: 1rem;

      position: absolute;
      left: 0.1rem;
      bottom: 0.07rem;
    }
  }

  .point {
    content: '';
    display: inline-block;
    width: 0.1rem;
    /* 圆的大小 */
    height: 0.1rem;
    border-radius: 50%;
    /* 让其变成圆形 */
    border: 3px solid #ff5722;
    /* 设置边框颜色 */
    background-color: white;
    /* 圆圈内部颜色 */
    position: absolute;
    top: 50%;
    left: 100%;
    transform: translateY(-50%);
  }

  .blue {
    background: #2e7dfd;
  }

  .green {
    background: #a4ca56;
  }

  .orange {
    background: #f6a933;
  }

  .red {
    background: #f95c51;
  }
}
</style>
