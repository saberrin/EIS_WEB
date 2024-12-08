<template>
  <div id="centerRight1">
    <div class="bg-color-black">
      <div class="d-flex pt-2 pl-2">
        <span>
          <icon name="chart-line" class="text-icon"></icon>
        </span>
        <div class="d-flex">
          <span class="fs-xl text mx-2">特征阻抗展示</span>
        </div>
      </div>
      <div class="d-flex jc-center body-box">
        <dv-scroll-board class="dv-scr-board"  :config="config" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    averageImpedanceRecords: {
      type: Array,
      required: true
    }
  },
  watch: {
    averageImpedanceRecords: {
      handler(newVal, oldVal) {
        console.log('new averageImpedanceRecords records observed ' + JSON.stringify(newVal),oldVal)
        this.updateChartData(newVal);
      },
      immediate: true
    }
  },
  data() {
    return {
      config: {
        header: ['电池箱', '特征率', '阻抗值'],
        data: [],
        rowNum: 8, //表格行数
        headerHeight: 35,
        headerBGC: '#0f1325', //表头
        oddRowBGC: '#0f1325', //奇数行
        evenRowBGC: '#171c33', //偶数行
        index: true,
        columnWidth: [50],
        align: ['center', 'center', 'center'] 
      }
    }
  },
  //不分组展示
   // components: { centerRight2Chart1 }
  // methods: {
  //   updateChartData(records) {
  //     // 这里的 records 是一个数组，其中包含每个 packId 和对应的 averageImpedanceRecords
  //     const ChartData = records.map(record => {
  //       return record.averageImpedanceRecords.map(item => {
  //         return [
  //           `pack${record.packId}`, 
  //           item.frequency, 
  //           `<span class='colorGrass'>${item.averageImpedance.toFixed(2)}%</span>` 
  //         ];
  //       });
  //     }).flat(); 

  //     this.config.data = ChartData; 
  //   }
  // }
  methods: {
  updateChartData(records) {
    // 扁平化所有的数据，并按 frequency 分组
    const groupedData = {};

    // 遍历
    records.forEach(record => {
      record.averageImpedanceRecords.forEach(item => {
        if (!groupedData[item.frequency]) {
          groupedData[item.frequency] = [];
        }

        groupedData[item.frequency].push({
          packId: `pack${record.packId}`,  
          frequency: item.frequency,      
          averageImpedance: item.averageImpedance.toFixed(2) 
        });
      });
    });

    // 按 frequency 排序频率
    const sortedFrequencies = Object.keys(groupedData).sort((a, b) => a - b);

    // 组装
    const updateChartData = [];
    sortedFrequencies.forEach(frequency => {
      groupedData[frequency].forEach(item => {
        updateChartData.push([
          item.packId,               
          item.frequency,          
          `<span class='colorGrass'>${item.averageImpedance}%</span>`  // 阻抗值
        ]);
      });
    });

    this.config.data = updateChartData;  // 更新表格数据
  }
}
}
</script>

<style lang="scss" scoped>
$box-height: 410px;
$box-width: 300px;
#centerRight1 {
  padding: 16px;
  padding-top: 20px;
  height: $box-height;
  width: $box-width;
  border-radius: 5px;
  .bg-color-black {
    height: $box-height - 30px;
    border-radius: 10px;
  }
  .text {
    color: #c3cbde;

  }
  .body-box {
    border-radius: 10px;
    overflow: hidden;
    .dv-scr-board {
      width: 270px;
      height: 340px;
    }
    
  }
  
}
</style>
