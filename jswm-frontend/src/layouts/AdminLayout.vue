<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="logo">
        <div class="logo-icon">
          <el-icon :size="28"><Management /></el-icon>
        </div>
        <h1>管理系统</h1>
      </div>
      
      <nav class="menu">
        <router-link to="/admin/dashboard" class="menu-item" :class="{ active: $route.path === '/admin/dashboard' }">
          <el-icon><DataBoard /></el-icon>
          <span>数据看板</span>
        </router-link>
        <router-link to="/admin/users" class="menu-item" :class="{ active: $route.path.includes('/admin/users') }">
          <el-icon><UserFilled /></el-icon>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/merchants" class="menu-item" :class="{ active: $route.path.includes('/admin/merchants') }">
          <el-icon><Shop /></el-icon>
          <span>商家管理</span>
        </router-link>
        <router-link to="/admin/orders" class="menu-item" :class="{ active: $route.path.includes('/admin/orders') }">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </router-link>
        <router-link to="/admin/settings" class="menu-item" :class="{ active: $route.path === '/admin/settings' }">
          <el-icon><Tools /></el-icon>
          <span>系统设置</span>
        </router-link>
      </nav>
    </aside>
    
    <!-- Main Content -->
    <main class="main-content">
      <header class="top-header">
        <div class="header-left">
          <button class="collapse-btn" @click="isCollapsed = !isCollapsed">
            <el-icon :size="20"><Fold v-if="!isCollapsed" /><Expand v-else /></el-icon>
          </button>
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <div class="search-box">
            <el-icon><Search /></el-icon>
            <input type="text" placeholder="搜索..." />
          </div>
          <el-badge :value="3" class="notification-btn">
            <el-icon :size="20"><Bell /></el-icon>
          </el-badge>
          <div class="user-dropdown">
            <el-avatar :size="36" :src="userInfo?.avatar || '/avatar-default.jpg'" />
            <span class="username">{{ userInfo?.realName || '管理员' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
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
import { useRoute } from 'vue-router'
import { getUserInfo } from '@/utils/auth'
import { Management, DataBoard, UserFilled, Shop, List, Tools, Fold, Expand, Search, Bell, ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const userInfo = ref(getUserInfo())
const isCollapsed = ref(false)

const pageTitle = computed(() => {
  const titles = {
    '/admin/dashboard': '数据看板',
    '/admin/users': '用户管理',
    '/admin/merchants': '商家管理',
    '/admin/orders': '订单管理',
    '/admin/settings': '系统设置'
  }
  return titles[route.path] || '管理系统'
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f6ffed;
}

/* Sidebar */
.sidebar {
  width: 240px;
  background: white;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.logo {
  height: 70px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
  border-bottom: 1px solid #f0f0f0;
}

.logo-icon {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #722ed1 0%, #b37feb 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.logo h1 {
  font-size: 18px;
  font-weight: 600;
  color: #1f1f1f;
  margin: 0;
}

/* Menu */
.menu {
  flex: 1;
  padding: 16px 12px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  margin-bottom: 4px;
  border-radius: 8px;
  color: #666;
  text-decoration: none;
  transition: all 0.2s;
}

.menu-item:hover {
  background: #f6ffed;
  color: #389e0d;
}

.menu-item.active {
  background: #d9f7be;
  color: #389e0d;
  font-weight: 500;
}

.menu-item .el-icon {
  font-size: 18px;
}

.menu-item span {
  font-size: 14px;
}

/* Main Content */
.main-content {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
}

.top-header {
  height: 70px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  width: 36px;
  height: 36px;
  background: #f5f5f5;
  border: none;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.collapse-btn:hover {
  background: #e8e8e8;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f1f1f;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f5f5f5;
  border-radius: 20px;
  width: 240px;
}

.search-box input {
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  width: 100%;
}

.notification-btn {
  width: 40px;
  height: 40px;
  background: #f5f5f5;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.notification-btn:hover {
  background: #e8e8e8;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: #f5f5f5;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.user-dropdown:hover {
  background: #e8e8e8;
}

.username {
  font-size: 14px;
  color: #333;
}

.content-wrapper {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>