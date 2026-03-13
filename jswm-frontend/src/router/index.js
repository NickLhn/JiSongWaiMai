import { createRouter, createWebHistory } from 'vue-router'
import { getToken, getUserInfo, removeToken, removeUserInfo } from '@/utils/auth'
import { ElMessage } from 'element-plus'

// 公共路由
const publicRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { public: true }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/ForgotPassword.vue'),
    meta: { public: true }
  }
]

// 学生端路由
const studentRoutes = {
  path: '/',
  component: () => import('@/layouts/StudentLayout.vue'),
  meta: { requiresAuth: true, role: 0 },
  redirect: '/home',
  children: [
    {
      path: 'home',
      name: 'StudentHome',
      component: () => import('@/views/student/Home.vue')
    },
    {
      path: 'shop/:id',
      name: 'StudentShop',
      component: () => import('@/views/student/Shop.vue')
    },
    {
      path: 'cart',
      name: 'StudentCart',
      component: () => import('@/views/student/Cart.vue')
    },
    {
      path: 'orders',
      name: 'StudentOrders',
      component: () => import('@/views/student/Orders.vue')
    },
    {
      path: 'orders/create',
      name: 'StudentOrderCreate',
      component: () => import('@/views/student/OrderCreate.vue')
    },
    {
      path: 'profile',
      name: 'StudentProfile',
      component: () => import('@/views/student/Profile.vue')
    },
    {
      path: 'favorites',
      name: 'StudentFavorites',
      component: () => import('@/views/student/Favorites.vue')
    },
    {
      path: 'coupons',
      name: 'StudentCoupons',
      component: () => import('@/views/student/Coupons.vue')
    },
    {
      path: 'address',
      name: 'StudentAddress',
      component: () => import('@/views/student/Address.vue')
    },
    {
      path: 'profile/edit',
      name: 'StudentEditProfile',
      component: () => import('@/views/student/EditProfile.vue')
    },
    {
      path: 'password/change',
      name: 'StudentChangePassword',
      component: () => import('@/views/student/ChangePassword.vue')
    },
    {
      path: 'settings',
      name: 'StudentSettings',
      component: () => import('@/views/student/Settings.vue')
    }
  ]
}

// 商家端路由
const merchantRoutes = {
  path: '/merchant',
  component: () => import('@/layouts/MerchantLayout.vue'),
  meta: { requiresAuth: true, role: 2 },
  redirect: '/merchant/dashboard',
  children: [
    {
      path: 'dashboard',
      name: 'MerchantDashboard',
      component: () => import('@/views/merchant/Dashboard.vue')
    },
    {
      path: 'dishes',
      name: 'MerchantDishes',
      component: () => import('@/views/merchant/Dishes.vue')
    },
    {
      path: 'orders',
      name: 'MerchantOrders',
      component: () => import('@/views/merchant/Orders.vue')
    },
    {
      path: 'settings',
      name: 'MerchantSettings',
      component: () => import('@/views/merchant/Settings.vue')
    }
  ]
}

// 管理端路由
const adminRoutes = {
  path: '/admin',
  component: () => import('@/layouts/AdminLayout.vue'),
  meta: { requiresAuth: true, role: 1 },
  redirect: '/admin/dashboard',
  children: [
    {
      path: 'dashboard',
      name: 'AdminDashboard',
      component: () => import('@/views/admin/Dashboard.vue')
    },
    {
      path: 'users',
      name: 'AdminUsers',
      component: () => import('@/views/admin/Users.vue')
    },
    {
      path: 'merchants',
      name: 'AdminMerchants',
      component: () => import('@/views/admin/Merchants.vue')
    },
    {
      path: 'orders',
      name: 'AdminOrders',
      component: () => import('@/views/admin/Orders.vue')
    },
    {
      path: 'settings',
      name: 'AdminSettings',
      component: () => import('@/views/admin/Settings.vue')
    }
  ]
}

// 404页面
const notFoundRoute = {
  path: '/:pathMatch(.*)*',
  name: 'NotFound',
  component: () => import('@/views/NotFound.vue')
}

const routes = [
  ...publicRoutes,
  studentRoutes,
  merchantRoutes,
  adminRoutes,
  notFoundRoute
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  const userInfo = getUserInfo()
  
  // 公共页面直接放行
  if (to.meta.public) {
    // 已登录用户访问登录页，根据角色跳转
    if (token && to.path === '/login') {
      return redirectByRole(userInfo?.role, next)
    }
    return next()
  }
  
  // 需要登录但未登录
  if (to.meta.requiresAuth && !token) {
    ElMessage.warning('请先登录')
    return next('/login')
  }
  
  // 角色权限检查
  if (to.meta.role !== undefined && userInfo?.role !== to.meta.role) {
    ElMessage.error('无权访问该页面')
    return redirectByRole(userInfo?.role, next)
  }
  
  next()
})

// 根据角色跳转
function redirectByRole(role, next) {
  switch(role) {
    case 0: // 学生
      return next('/home')
    case 1: // 管理员
      return next('/admin/dashboard')
    case 2: // 商家
      return next('/merchant/dashboard')
    default:
      removeToken()
      removeUserInfo()
      return next('/login')
  }
}

export default router
