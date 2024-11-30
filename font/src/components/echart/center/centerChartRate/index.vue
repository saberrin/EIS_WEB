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
      cdata: {
        xData: ["今年累计任务建次数", "本月巡检次数", "累计巡检次数", "AI大数据样本库总量", "安全风险样本库总量", "累计排查隐患次数"],
        seriesData: [
          { value: 10, name: "今年累计任务建次数" },
          { value: 5, name: "本月巡检次数" },
          { value: 15, name: "累计巡检次数" },
          { value: 25, name: "AI大数据样本库总量" },
          { value: 20, name: "安全风险样本库总量" },
          { value: 35, name: "累计排查隐患次数" }
        ]
      }
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