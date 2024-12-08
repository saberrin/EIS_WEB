<template>
  <div>
    <Chart :cdata="cdata" />
  </div>
</template>

<script>
import Chart from './chart.vue'
import axios from 'axios';
export default {
  data () {
    return {
      eisList: [],
      cdata: {
        category: [],
       //img
        lineData: [],
        //real
        barData:[],
        rateData: []
        
      }
    };
  },
  components: {
    Chart,
  },
  mounted () {
    this.setData();
    

  },
  methods: {
    // 根据自己的业务情况修改
    setData () {
       axios.get('http://127.0.0.1:9090/eis/index')
      .then(response => {
        // 请求成功，将数据保存到 eisList 数组中
        this.eisList = response.data;
        console.log("这是eis的数据",this.eisList);
        // 清空lineData，以防止重复添加数据
        this.cdata.lineData = [];
        this.cdata.barData = [];
         this.cdata.rateData = [];
        // 遍历eisList中的imag_imp数据，添加到cdata的lineData中
    for (let i = 0; i < this.eisList.length; i += 50) {
    const item = this.eisList[i];
    //this.cdata.lineData.push(item.imag_imp * -1);
    this.cdata.category.push(item.imag_imp * -1);
   this.cdata.lineData.push(item.imag_imp * -1);
    this.cdata.barData.push(item.real_imp);
    let rate = (item.imag_imp * -1) / item.real_imp;
    this.cdata.rateData.push(rate.toFixed(2));
}
      
      })
      .catch(error => {
        // 请求失败，处理错误
        console.error('Error fetching data:', error);
      });
    },
  }
};
</script>

<style lang="scss" scoped>
</style>