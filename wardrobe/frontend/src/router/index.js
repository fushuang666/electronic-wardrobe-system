import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import Layout from '@/views/Layout.vue'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { public: true } },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue'), meta: { public: true } },
  {
    path: '/',
    component: Layout,
    redirect: '/clothing',
    children: [
      { path: 'clothing', name: 'Clothing', component: () => import('@/views/Clothing.vue'), meta: { title: '衣物管理' } },
      { path: 'outfit', name: 'Outfit', component: () => import('@/views/Outfit.vue'), meta: { title: '手动搭配' } },
      { path: 'weather', name: 'Weather', component: () => import('@/views/Weather.vue'), meta: { title: '天气联动' } },
      { path: 'analytics', name: 'Analytics', component: () => import('@/views/Analytics.vue'), meta: { title: '数据分析' } },
      { path: 'reminder', name: 'Reminder', component: () => import('@/views/Reminder.vue'), meta: { title: '场景提醒' } },
      { path: 'wardrobe-manage', name: 'WardrobeManage', component: () => import('@/views/WardrobeManage.vue'), meta: { title: '衣柜管理' } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { title: '个人资料' } }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (!to.meta.public && !userStore.token) {
    return { name: 'Login', query: { redirect: to.fullPath } }
  }
})

export default router
