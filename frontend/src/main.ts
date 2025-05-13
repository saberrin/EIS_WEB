import { createApp } from 'vue';
import store from './store';
import './style.css';
import App from './App.vue';

import { TextScroll,Dialog,DatePicker  } from 'vue-amazing-ui';

import 'vue-amazing-ui/css'
createApp(App).use(store).use(TextScroll).use(Dialog).use(DatePicker).mount('#app');
