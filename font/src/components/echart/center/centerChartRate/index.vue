\<template>
  <div>
    <Chart :cdata="cdata" />
  </div>
</template>

<script>
import Chart from './chart.vue';
import axios from 'axios';

export default {
  data() {
    return {
      cdata: {
        xData: [
          "今年累计任务建次数",
          "本月巡检次数",
          "累计巡检次数",
          "AI大数据样本库总量",
          "安全风险样本库总量",
          "累计排查隐患次数"
        ],
        seriesData: [
          { value: 0, name: "今年累计任务建次数" },
          { value: 0, name: "本月巡检次数" },
          { value: 0, name: "累计巡检次数" },
          { value: 0, name: "AI大数据样本库总量" },
          { value: 0, name: "安全风险样本库总量" },
          { value: 0, name: "累计排查隐患次数" }
        ]
      }
    };
  },
  components: {
    Chart,
  },
  mounted() {
    
    this.fetchData();

    
    setInterval(() => {
      this.fetchData();
    }, 24 * 60 * 60 * 1000); // 每24小时更新一次数据
  },
  methods: {
    fetchData() {
      axios
        .get('http://192.168.137.2:8080/api/v1/overview')
        .then((response) => {
          const data = response.data;

          
          this.cdata.seriesData = [
            { value: data.totalInspections || 0, name: "今年累计任务建次数" },
            { value: data.monthlyInspections || 0, name: "本月巡检次数" },
            { value: data.totalMeasurements || 0, name: "累计巡检次数" },
            { value: data.totalRiskySamples || 0, name: "AI大数据样本库总量" },
            { value: data.cumulativeRiskIdentifications || 0, name: "安全风险样本库总量" },
            { value: 100, name: "累计排查隐患次数" } 
          ];
          console.log('接口返回的数据:', data);
        })
        .catch((error) => {
          console.error('数据获取失败:', error);
        });
    }
  }
};
</script>

<style lang="scss" scoped>
</style>
