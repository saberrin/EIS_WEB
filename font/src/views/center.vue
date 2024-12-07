<template>
  <div id="center">
    <div class="up">
      <div
        class="bg-color-black item"
        v-for="item in titleItem"
        :key="item.title"
      >
        <p class="ml-3 colorBlue fw-b fs-xl">{{ item.title }}</p>
        <div>
          <dv-digital-flop
            class="dv-dig-flop ml-1 mt-2 pl-3"
            :config="item.number"
          />
        </div>
      </div>
      <div class="d-flex">
        <span class="fs-xl text mx-2">任务通过率</span>
        <dv-decoration-3 class="dv-dec-3" />
      </div>
    </div>
    <div class="d-flex jc-center">
      <CenterChart />
    </div>
  </div>
</template>

<script>
import CenterChart from "@/components/echart/center/centerChartRate"; 
import axios from "axios";

export default {
  data() {
    return {
      titleItem: [
        {
          title: "今年累计任务建次数",
          number: {
            number: [0],
            toFixed: 1,
            textAlign: "left",
            content: "{nt}",
            style: { fontSize: 26 },
          },
        },
        {
          title: "本月巡检次数",
          number: {
            number: [0],
            toFixed: 1,
            textAlign: "left",
            content: "{nt}",
            style: { fontSize: 26 },
          },
        },
        {
          title: "累计巡检次数",
          number: {
            number: [0],
            toFixed: 1,
            textAlign: "left",
            content: "{nt}",
            style: { fontSize: 26 },
          },
        },
        {
          title: "AI大数据样本库总量",
          number: {
            number: [0],
            toFixed: 1,
            textAlign: "left",
            content: "{nt}",
            style: { fontSize: 26 },
          },
        },
        {
          title: "安全风险样本库总量",
          number: {
            number: [0],
            toFixed: 1,
            textAlign: "left",
            content: "{nt}",
            style: { fontSize: 26 },
          },
        },
        {
          title: "累计排查隐患次数",
          number: {
            number: [this],
            toFixed: 1,
            textAlign: "left",
            content: "{nt}",
            style: { fontSize: 26 },
          },
        },
      ],
    };
  },
  methods: {
    fetchData() {
      axios.get("http://localhost:8080/api/v1/overview")
        .then((response) => {
          const data = response.data;

          // 更新 titleItem 数组的内容
          this.titleItem = [
            {
              title: "今年累计任务建次数",
              number: {
                number: [data.totalInspections || 0], 
                toFixed: 1,
                textAlign: "left",
                content: "{nt}",
                style: { fontSize: 26 },
              },
            },
            {
              title: "本月巡检次数",
              number: {
                number: [data.monthlyInspections || 0], 
                toFixed: 1,
                textAlign: "left",
                content: "{nt}",
                style: { fontSize: 26 },
              },
            },
            {
              title: "累计巡检次数",
              number: {
                number: [data.totalMeasurements || 0], 
                toFixed: 1,
                textAlign: "left",
                content: "{nt}",
                style: { fontSize: 26 },
              },
            },
            {
              title: "AI大数据样本库总量",
              number: {
                number: [data.totalRiskySamples || 0], 
                toFixed: 1,
                textAlign: "left",
                content: "{nt}",
                style: { fontSize: 26 },
              },
            },
            {
              title: "安全风险样本库总量",
              number: {
                number: [data.cumulativeRiskIdentifications || 0], 
                toFixed: 1,
                textAlign: "left",
                content: "{nt}",
                style: { fontSize: 26 },
              },
            },
            {
              title: "累计排查隐患次数",
              number: {
                number: [100], 
                toFixed: 1,
                textAlign: "left",
                content: "{nt}",
                style: { fontSize: 26 },
              },
            },
          ];
          console.log("接口返回的数据:", data); 
        })
        .catch((error) => {
          console.error("数据获取失败:", error); 
        });
    },
  },
  mounted() {
    // 初始化加载数据
    this.fetchData();

    // 每天更新一次数据
    setInterval(() => {
      this.fetchData();
    }, 24 * 60 * 60 * 1000); // 每24小时更新一次数据
  },
  components: {
    CenterChart, 
  },
};
</script>

<style lang="scss" scoped>
#center {
  display: flex;
  flex-direction: column;
  .up {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-around;
    .item {
      border-radius: 6px;
      padding-top: 8px;
      margin-top: 8px;
      width: 32%;
      height: 70px;
      .dv-dig-flop {
        width: 150px;
        height: 30px;
      }
    }
  }
}

.text {
  color: #c3cbde;
}
.dv-dec-3 {
  position: relative;
  width: 100px;
  height: 20px;
  top: -3px;
}

.bottom-data {
  .item-box {
    & > div {
      padding-right: 5px;
    }
    font-size: 14px;
    float: right;
    position: relative;
    width: 50%;
    color: #d3d6dd;
    .dv-digital-flop {
      width: 120px;
      height: 30px;
    }
    // 金币
    .coin {
      position: relative;
      top: 6px;
      font-size: 20px;
      color: #ffc107;
    }
    .colorYellow {
      color: yellowgreen;
    }
    p {
      text-align: center;
    }
  }
}
</style>
