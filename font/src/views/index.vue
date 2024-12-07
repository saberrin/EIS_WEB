<template>
  <div id="index" ref="appRef">
    <div class="top-left-title">
      <span class="title-text">全面安全，发自内芯</span>
    </div>
    <div class="bg">
      <dv-loading v-if="loading">Loading...</dv-loading>
      <div v-else class="host-body">
        <div class="d-flex jc-center">
          <dv-decoration-10 class="dv-dec-10" />
          <div class="d-flex jc-center">
            <dv-decoration-8 class="dv-dec-8" :color="decorationColor" />
            <div class="title">
              <span class="title-text" style="font-size: 1vw">锂电池阻抗谱检测与AI辅助管理站</span>
              <dv-decoration-6 class="dv-dec-6" :reverse="true" :color="['#50e3c2', '#67a1e5']" />
            </div>
            <dv-decoration-8 class="dv-dec-8" :reverse="true" :color="decorationColor" />
          </div>
          <dv-decoration-10 class="dv-dec-10-s" />
        </div>

        <!-- 第二行 -->
        <div class="d-flex jc-between px-2">
          <div class="d-flex aside-width">
            <div class="react-left ml-4 react-l-s">
              <span class="react-left"></span>
              <span class="text">数据分析1</span>
            </div>
            <div class="react-left ml-3">
              <span class="text">数据分析2</span>
            </div>
          </div>
          <div class="d-flex aside-width">
            <div class="react-right bg-color-blue mr-3">
              <span class="text fw-b">已连接AI安全大陆</span>
            </div>
            <div class="react-right mr-4 react-l-s">
              <span class="react-after"></span>
              <span class="text">{{ dateYear }} {{ dateWeek }} {{ dateDay }}</span>
            </div>
          </div>
        </div>

        <div class="body-box">
          <!-- 第三行数据 -->
          <div class="content-box">
            <div>
              <dv-border-box-12>
                <centerLeft1 />
                <!-- <img src="src\assets\power.png"> -->
              </dv-border-box-12>
            </div>
            <div>
              <dv-border-box-12>
                <centerLeft2 :currentPack="currentPack" />
              </dv-border-box-12>
            </div>
            <!-- 中间 -->
            <div>
              <center />
            </div>
            <!-- 中间 -->
            <div>
              <centerRight2 :averageImpedanceRecords="averageImpedanceRecords" />
            </div>
            <div>
              <dv-border-box-13>
                <centerRight1 :dispersionCoefficientRecords="dispersionCoefficientRecords" />
              </dv-border-box-13>
            </div>
          </div>

          <!-- 第四行数据 -->
          <div class="bottom-box">
            <dv-border-box-13>
              <bottomLeft :packMeasurements="packMeasurements" />
            </dv-border-box-13>
            <dv-border-box-12>
              <bottomRight :packMetrics="packMetrics" />
            </dv-border-box-12>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import drawMixin from "../utils/drawMixin";
import { formatTime } from '../utils/index.js'
import centerLeft1 from './centerLeft1'
import centerLeft2 from './centerLeft2'
import centerRight1 from './centerRight1'
import centerRight2 from './centerRight2'
import center from './center'
import bottomLeft from './bottomLeft'
import bottomRight from './bottomRight'
import axios from "axios";

export default {
  mixins: [drawMixin],
  data() {
    return {
      timing: null,
      loading: true,
      dateDay: null,
      dateYear: null,
      dateWeek: null,
      weekday: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
      decorationColor: ['#568aea', '#000000'],
      systemConfiguration: null,
      packAdvancementIntervalId: null,
      pollingOverviewIntervalId: null,
      packMetrics: {},
      packMeasurements: [],
      currentContainer: 0,
      currentCluster: 0,
      currentPack: 0,
      averageImpedanceRecords: [],
      dispersionCoefficientRecords: []
    }
  },
  components: {
    centerLeft1,
    centerLeft2,
    centerRight1,
    centerRight2,
    center,
    bottomLeft,
    bottomRight
  },
  mounted() {
    this.timeFn()
    this.cancelLoading()
    axios.get('http://localhost:8080/api/v1/system-configuration')
      .then((response) => {
        this.systemConfiguration = response.data
        console.log('system configuration ' + JSON.stringify(this.systemConfiguration))
        this.startAdvancing()
        this.startPollingOverview()
      })
  },
  beforeDestroy() {
    clearInterval(this.timing)
    clearInterval(this.packAdvancementIntervalId)
    clearInterval(this.pollingOverviewIntervalId)
  },
  methods: {
    timeFn() {
      this.timing = setInterval(() => {
        const now = new Date()
        this.dateDay = formatTime(now, 'HH: mm: ss')
        this.dateYear = formatTime(now, 'yyyy-MM-dd')
        this.dateWeek = this.weekday[now.getDay()]
      }, 1000)
    },
    cancelLoading() {
      setTimeout(() => {
        this.loading = false
      }, 500)
    },
    fetchPackMetrics() {
      const container = this.systemConfiguration.containers[this.currentContainer]
      const cluster = container.clusters[this.currentCluster]
      const packId = cluster.packs[this.currentPack]
      axios.get(`http://localhost:8080/api/v1/containers/${container.id}/clusters/${cluster.id}/pack-metrics/${packId}`)
        .then((response) => {
          let res = response.data
          this.packMeasurements = res.measurements
          this.packMetrics = res.metrics
        })
      console.log('container ' + container.id + ' cluster ' + cluster.id + ' pack ' + packId)
    },
    startAdvancing() {
      this.fetchPackMetrics()

      this.packAdvancementIntervalId = setInterval(() => {
        this.currentPack++
        if (this.currentPack == this.systemConfiguration.containers[this.currentContainer].clusters[this.currentCluster].packs.length) {
          this.currentPack = 0
          this.currentCluster++
          if (this.currentCluster == this.systemConfiguration.containers[this.currentContainer].clusters.length) {
            this.currentCluster = 0
            this.currentContainer++
            if (this.currentContainer == this.systemConfiguration.containers.length) {
              this.currentContainer = 0
            }
          }
        }
        this.fetchPackMetrics()
      }, 5000)
    },
    fetchPackOverview() {
      console.log('pack overview fetched')
      let averageImpedanceRecords = []
      let dispersionCoefficientRecords = []

      // Create an array to hold all the axios requests
      let axiosRequests = []

      for (let container of this.systemConfiguration.containers) {
        for (let cluster of container.clusters) {
          // Add each axios request to the array
          let request = axios.get(`http://localhost:8080/api/v1/containers/${container.id}/clusters/${cluster.id}/pack-overviews`)
            .then((response) => {
              let records = response.data.records
              records.forEach(record => {
                averageImpedanceRecords.push({
                  packId: record.packId,
                  averageImpedanceRecords: record.averageImpedanceRecords
                })
                dispersionCoefficientRecords.push({
                  packId: record.packId,
                  dispersionCoefficient: record.dispersionCoefficient // Assuming this is the correct field
                })
              })
            })

          axiosRequests.push(request)
        }
      }

      // Wait for all the axios requests to complete before updating the state
      Promise.all(axiosRequests)
        .then(() => {
          this.averageImpedanceRecords = averageImpedanceRecords
          this.dispersionCoefficientRecords = dispersionCoefficientRecords
        })
        .catch((error) => {
          console.error('Error fetching pack overview data:', error)
        })
    },
    startPollingOverview() {
      this.fetchPackOverview()
      this.pollingOverviewIntervalId = setInterval(() => {
        this.fetchPackOverview()
      }, 3600000)
    }
  }
}
</script>

<style lang="scss" scoped>
.top-left-title {
  position: absolute;
  top: 30px;
  /* Adjust based on your desired distance from the top */
  left: 20px;
  /* Adjust based on your desired distance from the left */
  font-size: 1.5rem;
  /* Adjust the font size */
  font-weight: bold;
  color: #37a2da;
  /* Choose a color that suits your design */
  z-index: 999;
  /* Make sure it stays on top */
}

@import '../assets/scss/index.scss';
</style>
