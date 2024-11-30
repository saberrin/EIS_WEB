<template>
  <div>
    <Chart :cdata="cdata" />
  </div>
</template>

<script>
import Chart from './chart.vue';
export default {
  data () {
    return {
      // cdata: {
      //   xData: ["循环稳定性", "电池寿命", "剩余循环次数", "循环次数"],
      //   seriesData: [
      //     { value: 10, name: "循环稳定性" },
      //     { value: 5, name: "电池寿命" },
      //     { value: 15, name: "剩余循环次数" },
      //     { value: 25, name: "循环次数" },
      //   ]
      // }
    }
  },
  components: {
    Chart,
  },
  mounted () {
  },
  methods: {
  }
}
</script>

<style lang="scss" scoped>
</style>