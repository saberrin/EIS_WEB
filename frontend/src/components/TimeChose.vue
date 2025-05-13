<template>
  <Dialog v-model:open="open" title="选择日期范围" @cancel="onCancel" @ok="onOk">
    <DatePicker
      :width="240"
      v-model="rangeValue"
      range
      format="yyyy-MM-dd"
      placeholder="请选择日期范围"
    />
  </Dialog>
</template>

<script setup>
import store from '../store/index';
import { exportAPI } from '../api/packApi.ts';
import { ref, computed } from 'vue';
const open = computed(() => {
  return store.state.showTimeChose;
});
const rangeValue = ref(null);
function onCancel() {
  // 点击蒙层或 Esc 键或右上角叉或取消按钮的回调
  console.log('cancel');
  store.commit('setshowTimeChose', false);
}
async function onOk() {
  // 点击确定的回调
  console.log('ok');
  store.commit('setshowTimeChose', false);
  store.commit('setExportStartTime', rangeValue.value);
  store.commit('setExportEndTime', rangeValue.value);

  try {
    // if (!response.ok) {
    //       throw new Error('下载失败');
    //     }

    const url = '/export-data';
    const params = {
      dateFrom: new Date(rangeValue.value[0]).toISOString(),
      dateTo: new Date(rangeValue.value[1]).toISOString(),
    };
    let res = await exportAPI(params);
    // 生成一个临时下载链接
    const downloadUrl = window.URL.createObjectURL(res);
    const a = document.createElement('a');
    a.href = downloadUrl;
    a.download = '电池数据.csv'; // 设置下载文件名（根据需要修改）
    document.body.appendChild(a);
    a.click(); // 触发下载
    a.remove(); // 清理临时元素
    window.URL.revokeObjectURL(downloadUrl); // 释放 URL 对象

    // const query = new URLSearchParams(params).toString();
    // const finalUrl = `${url}?${query}`;

    // const response = await fetch(finalUrl, {
    //   method: 'GET',
    //   headers: {
    //     'Content-Type': 'text/csv',
    //   },
    // });

    // if (!response.ok) throw new Error('下载失败');

    // const blob = await response.blob();

    // const a = document.createElement('a');
    // a.href = window.URL.createObjectURL(blob);
    // a.download = 'data.csv';
    // a.click();
    // a.remove();
  } catch (error) {
    console.error('下载失败：', error);
    // alert('下载失败，请稍后再试');
  }
}
</script>
<style lang="scss">
.m-dialog-wrap .m-dialog {
  width: 2.5rem !important;

  margin-top: 15% !important;
}

.dialog-body-wrap {
  height: 1.8rem !important;
}

.dialog-header,
.dp__input,
.dp__input_icons {
  font-size: 0.1rem !important;
}

.m-datepicker {
  width: 2.2rem !important;
  font-size: 0.07rem !important;
}

.dp--menu-wrapper {
  top: 0.3rem !important;
}

.dp__input_wrap {
  margin-top: 0.1rem;
}

.dp__menu_inner {
  width: 2rem !important;
  height: 1rem !important;
  font-size: 0.05rem !important;
}

.dialog-footer {
  display: flex;
  justify-content: center;

  div {
    width: 0.8rem;
    height: 0.2rem !important;
    font-size: 0.08rem !important;
  }
}
.m-scroll-vertical {
  background: transparent !important;
  height: 1rem !important;
  box-shadow: none !important;
}
.scroll-item-wrap {
  border-color: transparent !important;
}
</style>
