<template>
  <div class="merchant-layout">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="logo">
        <div class="logo-icon">
          <el-icon :size="32"><Shop /></el-icon>
        </div>
        <div class="logo-text">
          <h1>商家后台</h1>
          <p>Merchant Center</p>
        </div>
      </div>
      
      <nav class="menu">
        <router-link to="/merchant/dashboard" class="menu-item" :class="{ active: $route.path === '/merchant/dashboard' }">
          <el-icon><DataLine /></el-icon>
          <span>数据概览</span>
        </router-link>
        <router-link to="/merchant/dishes" class="menu-item" :class="{ active: $route.path.includes('/merchant/dishes') }">
          <el-icon><Food /></el-icon>
          <span>菜品管理</span>
        </router-link>
        <router-link to="/merchant/orders" class="menu-item" :class="{ active: $route.path.includes('/merchant/orders') }">
          <el-icon><ShoppingBag /></el-icon>
          <span>订单管理</span>
        </router-link>
        <router-link to="/merchant/settings" class="menu-item" :class="{ active: $route.path === '/merchant/settings' }">
          <el-icon><Setting /></el-icon>
          <span>店铺设置</span>
        </router-link>
      </nav>
      
      <div class="sidebar-footer">
        <div class="user-info">
          <el-avatar :size="40" :src="userInfo?.avatar || '/avatar-default.jpg'" />
          <div class="user-detail">
            <span class="name">{{ userInfo?.realName || '商家' }}</span>
            <span class="role">商家账号</span>
          </div>
        </div>
        <button class="logout-btn" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
        </button>
      </div>
    </aside>
    
    <!-- Main Content -->
    <main class="main-content">
      <header class="top-header">
        <div class="breadcrumb">
          <span class="current">{{ pageTitle }}</span>
        </div>
        <div class="header-actions">
          <el-badge :value="pendingOrderCount" class="action-btn" v-if="pendingOrderCount > 0">
            <el-icon :size="20"><Bell /></el-icon>
          </el-badge>
          <button class="action-btn" @click="toggleFullscreen">
            <el-icon :size="20"><FullScreen /></el-icon>
          </button>
        </div>
      </header>
      <div class="content-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo, removeToken, removeUserInfo } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import { Shop, DataLine, Food, ShoppingBag, Setting, SwitchButton, Bell, FullScreen } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userInfo = ref(getUserInfo())
const pendingOrderCount = ref(5)

const pageTitle = computed(() => {
  const titles = {
    '/merchant/dashboard': '数据概览',
    '/merchant/dishes': '菜品管理',
    '/merchant/orders': '订单管理',
    '/merchant/settings': '店铺设置'
  }
  return titles[route.path] || '商家后台'
})

const handleLogout = () => {
  removeToken()
  removeUserInfo()
  ElMessage.success('退出成功')
  router.push('/login')
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}
</script>

<style scoped>
.merchant-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

/* Sidebar */
.sidebar {
  width: 260px;
  background: #001529;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.logo {
  height: 80px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.logo-text h1 {
  color: white;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.logo-text p {
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
  margin: 4px 0 0;
}

/* Menu */
.menu {
  flex: 1;
  padding: 16px 12px;
  overflow-y: auto;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  margin-bottom: 4px;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.65);
  text-decoration: none;
  transition: all 0.2s;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.05);
  color: white;
}

.menu-item.active {
  background: #ff6b35;
  color: white;
}

.menu-item .el-icon {
  font-size: 20px;
}

.menu-item span {
  font-size: 15px;
}

/* Sidebar Footer */
.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.user-detail .name {
  color: white;
  font-size: 14px;
  font-weight: 500;
}

.user-detail .role {
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
}

.logout-btn {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.65);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.logout-btn:hover {
  background: rgba(255, 107, 53, 0.8);
  color: white;
}

/* Main Content */
.main-content {
  flex: 1;
  margin-left: 260px;
  display: flex;
  flex-direction: column;
}

.top-header {
  height: 64px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.breadcrumb .current {
  font-size: 18px;
  font-weight: 600;
  color: #1f1f1f;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-btn {
  width: 40px;
  height: 40px;
  background: #f5f5f5;
  border: none;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #e8e8e8;
}

.content-wrapper {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>