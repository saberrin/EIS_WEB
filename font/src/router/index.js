import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
  path: '/',
  name: 'index',
  component: () => import('../views/index.vue')
  },
  {
  path: '/eis',
  name: 'eis',
  component: () => import('../views/test.vue')
  }]


const router = new VueRouter({
  mode: 'history',
  routes
})

router.beforeEach((to, from, next) => {
  console.log('Global beforeEach hook:', to, from);
  next();
});
export default router