<template>
  <div id="bottomLeft">
    <div class="bg-color-black">
      <div class="d-flex pt-2 pl-2">
        <span>
          <icon name="chart-bar" class="text-icon"></icon>
        </span>
        <div class="d-flex">
          <span class="fs-xl text mx-2">数据统计图</span>
        </div>
      </div>
      <div>
        <BottomLeftChart :cdata="cdata" />
      </div>
    </div>
  </div>
</template>

<script>
import BottomLeftChart from '@/components/echart/bottom/bottomLeftChart';
export default {
  props: {
    packMeasurements: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      cdata: {
        category: [],
        lineData: [],
        barData: [],
        rateData: [],
      },
    };
  },
  watch: {
    packMeasurements: {
      handler(newVal) {
        console.log('New measurements observed:', newVal);
        this.processPackMeasurements(newVal); // 处理传入的数据
      },
      immediate: true,
    },
  },
  methods: {
    // 处理 packMeasurements 数据并映射到 cdata
    processPackMeasurements(measurements) {
      if (!Array.isArray(measurements)) return;
      this.cdata.category = measurements.map((item) => item.cellId || 'N/A');
      this.cdata.lineData = measurements.map((item) => item.imaginaryImpedance || 0);
      this.cdata.barData = measurements.map((item) => item.realImpedance|| 0);
      // this.cdata.category = measurements.map((item) => item.imaginaryImpedance || 0);
      // this.cdata.barData = measurements.map((item) => item.realImpedance || 0);
      
      // 计算 rateData
      this.cdata.rateData = [];
      for (let i = 0; i < this.cdata.barData.length; i++) {
        const rate =
          this.cdata.lineData[i] > 0
            ? (this.cdata.barData[i] / this.cdata.lineData[i]).toFixed(2): 0;
        this.cdata.rateData.push(rate);
      }
    },
  },
  components: {
    BottomLeftChart,
  },
};
</script>

<style lang="scss" scoped>
$box-height: 520px;
$box-width: 100%;
#bottomLeft {
  padding: 20px 16px;
  height: $box-height;
  width: $box-width;
  border-radius: 5px;
  .bg-color-black {
    height: $box-height - 35px;
    border-radius: 10px;
  }
  .text {
    color: #c3cbde;
  }
  .chart-box {
    margin-top: 16px;
    width: 170px;
    height: 170px;
    .active-ring-name {
      padding-top: 10px;
    }
  }
}
</style>
