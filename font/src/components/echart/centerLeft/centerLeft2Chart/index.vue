<template>
  <div>
    <Chart :cdata="cdata" />
     <dv-scroll-ranking-board class="dv-scr-rank-board mt-1" :config="ranking" />
  </div>
  
</template>

<script>
import Chart from './chart.vue';
export default {
  data () {
    return {
       ranking: {
        data: [
          {
            name: '材料体系: ',
            value: "磷酸铁锂"
          },
          {
            name: '标称电压: ',
            value: "3.3v"
          },
          {
            name: '充电终止电压: ',
            value: 3.2
          },
          {
            name: '放电终止电压',
            value: 66
          },
          {
            name: '最大放电电流: ',
            value: "2.5A"
          },
          {
            name: '工作温度范围',
            value: "~20-60"
          },
        ],
        carousel: 'single',
        // unit: '人'
      },
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
.ranking {
      padding: 10px;
      width: 59%;
      .dv-scr-rank-board {
        height: 225px;
      }
    }
</style>