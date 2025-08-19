import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/components/login/login.vue'
import Register from '@/components/login/register.vue'
import Navigation from '@/components/navigation/navigation.vue'
import ChatIndex from '@/chat/index.vue'
import ImageGen from '@/components/image-gen/image-gen.vue'
import ProfilePage from '@/components/profile/ProfilePage.vue' // 新增
import ReviewsPage from '@/components/reviews/ReviewsPage.vue' // 新增

const routes = [
  // 根路径重定向到登录页
  { path: '/', redirect: '/login' },
  // 登录路由
  { path: '/login', name: 'Login', component: Login },
  // 注册路由
  { path: '/register', name: 'Register', component: Register },
  // 导航页路由
  { path: '/navigation', name: 'Navigation', component: Navigation, meta: { requiresAuth: true } },
  // 聊天页面路由
  { path: '/chat', name: 'Chat', component: ChatIndex, meta: { requiresAuth: true } },
  // 智能生图页面路由
  { path: '/image-gen', name: 'ImageGen', component: ImageGen, meta: { requiresAuth: true } },
  // 个人主页路由
  { path: '/profile', name: 'Profile', component: ProfilePage, meta: { requiresAuth: true } },
  // 评价页路由
  { path: '/reviews', name: 'Reviews', component: ReviewsPage, meta: { requiresAuth: true } },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// 添加路由守卫，确保只有登录后才能访问受保护的页面
router.beforeEach((to, from, next) => {
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 检查是否已登录（这里假设你有一个存储登录状态的方式）
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
    if (isLoggedIn) {
      next()
    } else {
      // 未登录则重定向到登录页
      next('/login')
    }
  } else {
    next()
  }
})

export default router
