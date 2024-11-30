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
            <!-- Display all items without hiding -->
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
  data() {
    return {
      currentIndex: 0, // Current highlighted index
      carouselItems: [
        '数据1', '数据2', '数据3', '数据4',
        '数据5', '数据6', '数据7', '数据8'
      ],
      timer: null // Timer for automatic highlight change
    };
  },
  methods: {
    // Start auto highlight switching
    startAutoPlay() {
      this.timer = setInterval(() => {
        this.nextItem();
      }, 2000); // Highlight changes every 2 seconds
    },
    nextItem() {
      this.currentIndex = (this.currentIndex + 1) % this.carouselItems.length;
    }
  },
  mounted() {
    this.startAutoPlay(); // Start on load
  },
  beforeDestroy() {
    clearInterval(this.timer); // Clear timer on component destroy
  }
};
</script>
<style lang="scss" scoped>
$box-height: 410px;
$box-width: 360px;
$highlight-color: #37a2da;
$bg-dark: #1a1d2e;
$bg-light: #0f1325;
$carousel-bg-color: #2b3a4f; // Opaque background color for the carousel items

#centerRight1 {
  padding: 16px;
  padding-top: 20px;
  height: $box-height;
  width: $box-width;
  border-radius: 10px;
  background: linear-gradient(135deg, $bg-dark, $bg-light);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  border: 3px solid $highlight-color; /* Thicker border around the container */

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
    border: 3px solid $highlight-color; /* Thicker border around the carousel container */
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
    background: $carousel-bg-color; /* Opaque background color for items */
    color: white;
    font-weight: bold;
    opacity: 0.9;
    transition: all 0.3s ease-in-out;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2); /* Subtle divider between items */
  }

  .active {
    background: $highlight-color; /* Highlight color for active item */
    font-size: 18px; /* Slightly larger font for emphasis */
    transform: scale(1.05); /* Slightly scales the active item */
    box-shadow: 0 4px 10px rgba(55, 162, 218, 0.6); /* Subtle glow */
    opacity: 1;
  }
}
</style>
