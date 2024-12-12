<template>
  <div id="centerRight1">
    <div class="bg-color-black">
      <div class="d-flex pt-2 pl-2">
        <span>
          <icon name="chart-line" class="text-icon"></icon>
        </span>
        <div class="d-flex">
          <span class="fs-xl text mx-2">轮播展示</span>
        </div>
      </div>
      <div class="d-flex jc-center body-box">
        <div class="dv-scr-board">
          <div class="carousel-wrapper">
            <div
              v-for="(item, index) in carouselItems"
              :key="index"
              :class="['carousel-item', { 'active': currentIndex === index }]">
              {{ item }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    currentPack: {
      type: Number,
      required: true
    }
  },
  watch: {
    currentPack(newVal) {
      console.log('Received new currentPack:', newVal);
      this.updateCarouselIndex(newVal);
    }
  },
  data() {
    return {
      currentIndex: 0, // Track the active item
      carouselItems: ["Pack 0", "Pack 1", "Pack 2", "Pack 3", "Pack 4", "Pack 5", "Pack 6", "Pack 7"], // Static packs
    };
  },
  methods: {
    // Update the index when the currentPack changes
    updateCarouselIndex(packId) {
      if (packId >= 0 && packId < this.carouselItems.length) {
        this.currentIndex = packId; 
      }
    },
  },

  mounted() {
    // Initialize with the currentPack value from the parent
    this.updateCarouselIndex(this.currentPack);
  },

  beforeDestroy() {
    // Clear the interval if any set before destroying the component
    clearInterval(this.timer); 
  }
};
</script>

<style lang="scss" scoped>
$box-height: 410px;
$box-width: 360px;
$highlight-color: #37a2da;
$bg-dark: #1a1d2e;
$bg-light: #0f1325;
$carousel-bg-color: #2b3a4f;

#centerRight1 {
  padding: 16px;
  padding-top: 20px;
  height: $box-height;
  width: $box-width;
  border-radius: 10px;
  background: linear-gradient(135deg, $bg-dark, $bg-light);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  border: 3px solid $highlight-color;

  .bg-color-black {
    height: $box-height - 30px;
    border-radius: 10px;
    background-color: $bg-dark;
    padding: 10px;
  }

  .header {
    font-size: 18px;
    color: $highlight-color;
    display: flex;
    align-items: center;
    .text {
      color: #c3cbde;
      font-weight: bold;
    }
  }

  .body-box {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 10px;
    width: 100%;
    height: 100%;
  }

  .dv-scr-board {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 8px;
    background-color: rgba(0, 0, 0, 0.5);
    overflow: hidden;
    border: 3px solid $highlight-color;
  }

  .carousel-wrapper {
    display: flex;
    flex-direction: column;
    height: 100%;
    width: 100%;
  }

  .carousel-item {
    height: 12.5%; /* Each item takes 1/8th of the container height */
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background: $carousel-bg-color;
    color: white;
    font-weight: bold;
    opacity: 0.9;
    transition: all 0.3s ease-in-out;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2); /* Subtle divider between items */
  }

  .active {
    background: $highlight-color;
    font-size: 18px;
    transform: scale(1.05);
    box-shadow: 0 4px 10px rgba(55, 162, 218, 0.6);
    opacity: 1;
  }
}
</style>
