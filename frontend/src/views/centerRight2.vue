<template>
  <div id="centerRight2">
    <div class="bg-color-black">
      <div class="d-flex pt-2 pl-2">
        <span>
          <icon name="align-left" class="text-icon"></icon>
        </span>
        <span class="fs-xl text mx-2">电池箱EIS离散度</span>
      </div>
      <div class="d-flex ai-center flex-column body-box">
        <dv-capsule-chart class="dv-cap-chart" :config="config" />
        <!-- <centerRight2Chart1 /> -->
      </div>
    </div>
  </div>
</template>

<script>
// import centerRight2Chart1 from '@/components/echart/centerRight/centerRightChart'

export default {
  props: {
    dispersionCoefficientRecords: {
      type: Array,
      required: true
    }
  },
  watch: {
    dispersionCoefficientRecords: {
      handler(newVal, oldVal) {
        console.log('new dispersionCoefficientRecordse records observed ' + JSON.stringify(newVal),oldVal)
        this.updateChartData(newVal);
      },
      immediate: true
    }
  },
  data() {
    return {
      config: {
        data: [
          { name: 'Pack1:', value: 200 },
          { name: 'Pack2:', value: 67 },
          { name: 'Pack3:', value: 123 },
          { name: 'Pack4:', value: 55 },
          { name: 'Pack5:', value: 88 },
          { name: 'Pack6:', value: 90 },
          { name: 'Pack7:', value: 40 },
          { name: 'Pack8:', value: 60 },
        ],
      }
    }
  },
  // components: { centerRight2Chart1 }
    methods: {
    updateChartData(records) {
      const limitedRecords = records.slice(0, 8);
      const chartData = limitedRecords.map((record) => ({
        name: `Pack ${record.packId}`,  
        value: record.dispersionCoefficient
      }));

      this.config.data = chartData;
      console.log('Updated chart config:', this.config); 
    }
  }
}
</script>
<style lang="scss" scoped>
$box-height: 410px;
$box-width: 340px;
$highlight-color: #37a2da;
$bg-dark: #1a1d2e;
$bg-light: #0f1325;

#centerRight2 {
  padding: 5px;
  height: $box-height;
  width: $box-width;
  border-radius: 5px;
  background: linear-gradient(135deg, $bg-dark, $bg-light);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);

  .bg-color-black {
    padding: 5px;
    height: 100%;
    width: 100%;
    border-radius: 10px;
    background-color: $bg-dark;
  }

  .text {
    color: #c3cbde;
  }

  .d-flex {
    display: flex;
    align-items: center;
  }

  .body-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    height: 100%;
    width: 100%;
    border-radius: 10px;
  }

  .dv-cap-chart {
    width: 100%;
    height: 100%;  // Ensures the chart fills the container
  }

  /* Optional: Add some padding or border for the content */
  .d-flex.pt-2.pl-2 {
    padding-top: 10px;
    padding-left: 10px;
  }

  .fs-xl {
    font-size: 18px;
  }

  .mx-2 {
    margin-left: 8px;
    margin-right: 8px;
  }
}
</style>
