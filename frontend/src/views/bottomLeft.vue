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
      handler(newVal, oldVal) {
        console.log('New measurements observed:', newVal);
        this.processPackMeasurements(newVal); // 处理传入的数据
      },
      immediate: true,
    },
  },

methods: {
  processPackMeasurements(measurements) {
    if (!Array.isArray(measurements)) {console.log("not array");return;}
    // 设置 x 轴为 realImpedance 和 y 轴为 imaginaryImpedance * -1
    const sortedMeasurements = [...measurements].sort((a, b) => a.realImpedance - b.realImpedance);
    this.cdata.category = sortedMeasurements.map((item) => item.realImpedance || 'N/A');
    this.cdata.rateData = sortedMeasurements.map((item) => item.imaginaryImpedance * -1 || 0);
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
