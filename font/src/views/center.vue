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
        <CenterChart/>
      </div>
  </div>
</template>

<script>
import CenterChart from '@/components/echart/center/centerChartRate';
import axios from 'axios';
export default {
  data() {
    return {
      vctList: [],
      titleItem: [
        {
          title: '今年累计任务建次数',
          number: {
            number: [120],
            toFixed: 1,
            textAlign: 'left',
            content: '{nt}',
            style: {
              fontSize: 26
            }
          }
        },
        {
          title: '本月巡检次数',
          number: {
            number: [18],
            toFixed: 1,
            textAlign: 'left',
            content: '{nt}',
            style: {
              fontSize: 26
            }
          }
        },
        {
          title: '累计巡检次数',
          number: {
            number: [2],
            toFixed: 1,
            textAlign: 'left',
            content: '{nt}',
            style: {
              fontSize: 26
            }
          }
        },
        {
          title: 'AI大数据样本库总量',
          number: {
            number: [14],
            toFixed: 1,
            textAlign: 'left',
            content: '{nt}',
            style: {
              fontSize: 26
            }
          }
        },
        {
          title: '安全风险样本库总量',
          number: {
            number: [106],
            toFixed: 1,
            textAlign: 'left',
            content: '{nt}',
            style: {
              fontSize: 26
            }
          }
        },
        {
          title: '累计排查隐患次数',
          number: {
            number: [100],
            toFixed: 1,
            textAlign: 'left',
            content: '{nt}',
            style: {
              fontSize: 26
            }
          }
        }
      ],
      water: {
        data: [24, 45],
        shape: 'roundRect',
        formatter: '{value}%',
        waveNum: 3
      },
      
    }
  },
  methods: {
  scrollSmoothly() {
    const container = document.querySelector('.vct-list');
    if (container) {
      let i = 0;
      const scrollInterval = setInterval(() => {
        container.scrollTop += 5; // 设置滚动速度，可调整
        i++;
        if (i >= this.vctList.length * 2 - 1) {
          clearInterval(scrollInterval); // 停止滚动
          // 在滚动到最后一个元素后返回到第一个元素
          setTimeout(() => {
            container.scrollTop = 0;
            this.scrollSmoothly(); // 重新开始滚动
          }, 1000); // 设置停留时间，可调整
        }
      }, 500); // 每隔50毫秒滚动一次，可调整
    }
  }
},
 mounted() {
  axios.get('http://127.0.0.1:9090/vct/index')
    .then(response => {
      const data = response.data;
      if (Array.isArray(data)) {
        this.vctList = data;
      } else if (typeof data === 'object') {
        this.vctList = Object.values(data);
      }
      //// 执行自定义的滚动方法
     this.scrollSmoothly(); // 开始逐步滚动
      // xxx
      console.log("打印一下vct",this.vctList); // 打印转换后的vctList数组
    })
    .catch(error => {
      console.error('Error fetching data:', error);
    });
},
components: {
    CenterChart
  }
}
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

// /* 自定义滚动条样式 */
// .vct-list::-webkit-scrollbar {
//   width: 10px; /* 设置滚动条宽度 */
// }

// .vct-list::-webkit-scrollbar-track {
//   background: transparent; /* 设置滚动条背景颜色为透明 */
// }

// .vct-list::-webkit-scrollbar-thumb {
//   background: rgba(0, 0, 0, 0.2); /* 设置滚动条thumb的颜色 */
//   border-radius: 10px; /* 设置滚动条thumb的圆角 */
// }

// .vct-item {
//   border: 1px solid #ccc;
//   border-radius: 5px;
//   padding: 10px;
//   margin-bottom: 10px;
// }


// .vct-item p {
//   margin: 5px 0;
// }

// .voltage {
//   color: #FFA500; /* 您可以根据需要设置颜色 */
// }

// .current {
//   color: #00FFFF; /* 您可以根据需要设置颜色 */
// }

//     .percent {
//       width: 40%;
//       display: flex;
//       flex-wrap: wrap;
//       .item {
//         width: 50%;
//         height: 120px;
//         span {
//           margin-top: 8px;
//           font-size: 14px;
//           display: flex;
//           justify-content: center;
//         }
//       }
//       .water {
//         width: 100%;
//         .dv-wa-le-po {
//           height: 120px;
//         }
//       }
      
//     }
//   }
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
