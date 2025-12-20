import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/CodeGen.vue'),
    meta: {
      title: '代码生成'
    }
  },
  {
    path: '/preview',
    name: 'Preview',
    component: () => import('@/views/CodePreview.vue'),
    meta: {
      title: '代码预览'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || 'S2S'} - SQL to SpringBoot 代码生成工具`
  next()
})

export default router

