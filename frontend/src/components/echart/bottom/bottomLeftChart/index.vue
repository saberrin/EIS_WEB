<template>
  <div>
    <Chart :cdata="cdata" />
  </div>
</template>

<script>
import Chart from './chart.vue';
export default {
  props: {
    cdata: {
      type: Object,
      required: true, // cdata 从父组件传递进来
    },
  },
  components: {
    Chart,
  },
};
</script>

<style lang="scss" scoped>
</style>
