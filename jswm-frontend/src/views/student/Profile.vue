<template>
  <div class="profile-page">
    <!-- Header -->
    <header class="page-header">
      <button class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <h1>个人中心</h1>
      <div class="placeholder"></div>
    </header>

    <!-- Profile Hero -->
    <section class="profile-hero">
      <div class="hero-bg"></div>
      <div class="profile-card">
        <div class="avatar-wrapper">
          <img :src="userInfo?.avatar || '/avatar-default.jpg'" class="avatar" />
          <button class="edit-avatar" @click="showAvatarUpload = true">
            <el-icon><Camera /></el-icon>
          </button>
        </div>
        <div class="user-info">
          <h2 class="user-name">{{ userInfo?.realName || '用户' }}</h2>
          <p class="user-id">ID: {{ userInfo?.username }}</p>
        </div>
      </div>
    </section>

    <!-- Stats -->
    <section class="stats-section">
      <div class="stat-item">
        <span class="stat-value">{{ orderCount }}</span>
        <span class="stat-label">订单</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ couponCount }}</span>
        <span class="stat-label">优惠券</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ favoriteCount }}</span>
        <span class="stat-label">收藏</span>
      </div>
    </section>

    <!-- Menu List -->
    <section class="menu-section">
      <div class="menu-group">
        <div class="menu-item" @click="goTo('/orders')">
          <div class="menu-icon orange">
            <el-icon><Document /></el-icon>
          </div>
          <span class="menu-text">我的订单</span>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="goTo('/favorites')">
          <div class="menu-icon red">
            <el-icon><Star /></el-icon>
          </div>
          <span class="menu-text">我的收藏</span>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="goTo('/coupons')">
          <div class="menu-icon yellow">
            <el-icon><Ticket /></el-icon>
          </div>
          <span class="menu-text">优惠券</span>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="goTo('/address')">
          <div class="menu-icon blue">
            <el-icon><Location /></el-icon>
          </div>
          <span class="menu-text">收货地址</span>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="menu-group">
        <div class="menu-item" @click="goTo('/profile/edit')">
          <div class="menu-icon green">
            <el-icon><Edit /></el-icon>
          </div>
          <span class="menu-text">编辑资料</span>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="goTo('/password/change')">
          <div class="menu-icon purple">
            <el-icon><Lock /></el-icon>
          </div>
          <span class="menu-text">修改密码</span>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="menu-group">
        <div class="menu-item" @click="goTo('/settings')">
          <div class="menu-icon gray">
            <el-icon><Setting /></el-icon>
          </div>
          <span class="menu-text">设置</span>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item logout" @click="handleLogout">
          <div class="menu-icon logout-icon">
            <el-icon><SwitchButton /></el-icon>
          </div>
          <span class="menu-text">退出登录</span>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </section>

    <!-- Edit Profile Modal -->
    <transition name="slide-up">
      <div v-if="showEditProfile" class="modal-overlay" @click="showEditProfile = false">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h3>编辑资料</h3>
            <button class="close-btn" @click="showEditProfile = false">
              <el-icon><Close /></el-icon>
            </button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>真实姓名</label>
              <input v-model="form.realName" type="text" placeholder="请输入真实姓名" />
            </div>
            <div class="form-group">
              <label>手机号</label>
              <input v-model="form.phone" type="tel" placeholder="请输入手机号" />
            </div>
            <div class="form-group">
              <label>邮箱</label>
              <input v-model="form.email" type="email" placeholder="请输入邮箱" />
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="showEditProfile = false">取消</button>
            <button class="btn-save" @click="updateProfile" :disabled="updating">
              {{ updating ? '保存中...' : '保存' }}
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfo as getUserInfoApi, updateUserInfo } from '@/api/user'
import { getUserInfo, setUserInfo, removeToken, removeUserInfo } from '@/utils/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Camera, Document, Star, Ticket, Location, Edit, Lock, Setting, SwitchButton, ArrowRight, Close } from '@element-plus/icons-vue'

const router = useRouter()
const userInfo = ref(getUserInfo())
const showEditProfile = ref(false)
const showAvatarUpload = ref(false)
const showChangePassword = ref(false)
const showSettings = ref(false)
const updating = ref(false)
const orderCount = ref(12)
const couponCount = ref(3)
const favoriteCount = ref(8)

const form = reactive({
  realName: '',
  phone: '',
  email: ''
})

const loadUserInfo = async () => {
  try {
    const res = await getUserInfoApi()
    userInfo.value = res.data
    form.realName = res.data.realName || ''
    form.phone = res.data.phone || ''
    form.email = res.data.email || ''
  } catch (error) {
    console.error(error)
  }
}

const updateProfile = async () => {
  try {
    updating.value = true
    await updateUserInfo(form)
    ElMessage.success('保存成功')
    loadUserInfo()
    showEditProfile.value = false
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    updating.value = false
  }
}

const goTo = (path) => {
  router.push(path)
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    removeToken()
    removeUserInfo()
    ElMessage.success('退出成功')
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('退出失败')
    }
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f8f9fa;
  padding-bottom: 24px;
}

/* Header */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.page-header h1 {
  font-size: 18px;
  font-weight: 600;
}

.back-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border: none;
  border-radius: 12px;
  cursor: pointer;
}

.placeholder {
  width: 40px;
}

/* Profile Hero */
.profile-hero {
  position: relative;
  padding: 0 16px;
  margin-top: -1px;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 120px;
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  border-radius: 0 0 24px 24px;
}

.profile-card {
  position: relative;
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-top: 60px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-wrapper {
  position: relative;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.edit-avatar {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  background: #ff6b35;
  border: 2px solid white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 20px;
  font-weight: 700;
  color: #212529;
  margin-bottom: 4px;
}

.user-id {
  font-size: 14px;
  color: #adb5bd;
}

/* Stats */
.stats-section {
  display: flex;
  justify-content: space-around;
  padding: 20px 16px;
  margin: 16px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b35;
}

.stat-label {
  font-size: 13px;
  color: #6c757d;
}

/* Menu */
.menu-section {
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.menu-group {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:not(:last-child) {
  border-bottom: 1px solid #f1f3f4;
}

.menu-item:hover {
  background: #f8f9fa;
}

.menu-item.logout:hover {
  background: #fff5f5;
}

.menu-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.menu-icon.orange {
  background: #fff5f2;
  color: #ff6b35;
}

.menu-icon.red {
  background: #fff5f5;
  color: #dc3545;
}

.menu-icon.yellow {
  background: #fffbeb;
  color: #f59e0b;
}

.menu-icon.blue {
  background: #eff6ff;
  color: #3b82f6;
}

.menu-icon.green {
  background: #f0fdf4;
  color: #22c55e;
}

.menu-icon.purple {
  background: #faf5ff;
  color: #a855f7;
}

.menu-icon.gray {
  background: #f3f4f6;
  color: #6b7280;
}

.menu-icon.logout-icon {
  background: #fef2f2;
  color: #dc3545;
}

.menu-text {
  flex: 1;
  font-size: 15px;
  color: #212529;
}

.menu-item.logout .menu-text {
  color: #dc3545;
}

.menu-arrow {
  color: #adb5bd;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  z-index: 200;
  display: flex;
  align-items: flex-end;
}

.modal-content {
  width: 100%;
  background: white;
  border-radius: 24px 24px 0 0;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f1f3f4;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border: none;
  border-radius: 10px;
  cursor: pointer;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #495057;
  margin-bottom: 8px;
}

.form-group input {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  font-size: 15px;
  outline: none;
  transition: all 0.2s;
}

.form-group input:focus {
  border-color: #ff6b35;
  box-shadow: 0 0 0 4px rgba(255, 107, 53, 0.1);
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 16px 20px 24px;
  border-top: 1px solid #f1f3f4;
}

.modal-footer button {
  flex: 1;
  padding: 14px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}

.btn-cancel {
  background: #f8f9fa;
  color: #6c757d;
}

.btn-save {
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
}

.btn-save:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Transitions */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
}

.slide-up-enter-from .modal-content,
.slide-up-leave-to .modal-content {
  transform: translateY(100%);
}
</style>