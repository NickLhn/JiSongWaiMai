<template>
  <div class="student-layout">
    <!-- Main Content -->
    <main class="main-content">
      <router-view />
    </main>
    
    <!-- Bottom Tab Navigation -->
    <nav class="bottom-nav">
      <router-link to="/home" class="nav-item" :class="{ active: $route.path === '/home' }">
        <el-icon :size="24"><HomeFilled /></el-icon>
        <span>首页</span>
      </router-link>
      <router-link to="/orders" class="nav-item" :class="{ active: $route.path === '/orders' }">
        <el-icon :size="24"><Document /></el-icon>
        <span>订单</span>
      </router-link>
      <router-link to="/profile" class="nav-item" :class="{ active: $route.path === '/profile' }">
        <el-icon :size="24"><User /></el-icon>
        <span>我的</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import { HomeFilled, Document, User } from '@element-plus/icons-vue'
</script>

<style scoped>
.student-layout {
  min-height: 100vh;
  background: #f8f9fa;
  padding-bottom: 70px;
}

.main-content {
  min-height: calc(100vh - 70px);
}

/* Bottom Navigation */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 70px;
  background: white;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 8px 24px;
  color: #adb5bd;
  text-decoration: none;
  transition: all 0.2s;
}

.nav-item .el-icon {
  font-size: 24px;
}

.nav-item span {
  font-size: 12px;
}

.nav-item.active {
  color: #ff6b35;
}

.nav-item:active {
  transform: scale(0.95);
}
</style>