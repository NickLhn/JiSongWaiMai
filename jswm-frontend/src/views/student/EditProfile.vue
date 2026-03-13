<template>
  <div class="edit-profile-page">
    <header class="page-header">
      <div class="header-content">
        <button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <h1>编辑资料</h1>
        <button class="save-btn" @click="saveProfile">保存</button>
      </div>
    </header>

    <main class="profile-content">
      <!-- 头像区域 -->
      <div class="avatar-section">
        <div class="avatar-wrapper" @click="triggerUpload">
          <img :src="userInfo.avatar || '/avatar-default.jpg'" alt="">
          <div class="avatar-mask">
            <el-icon><Camera /></el-icon>
            <span>更换头像</span>
          </div>
        </div>
        <input 
          ref="fileInput" 
          type="file" 
          accept="image/*" 
          style="display: none" 
          @change="handleFileChange"
        >
      </div>

      <!-- 表单区域 -->
      <div class="form-section">
        <div class="form-item">
          <label>昵称</label>
          <input 
            v-model="userInfo.realName" 
            type="text" 
            placeholder="请输入昵称"
          >
        </div>
        <div class="form-item">
          <label>手机号</label>
          <input 
            v-model="userInfo.phone" 
            type="tel" 
            placeholder="请输入手机号"
            maxlength="11"
          >
        </div>
        <div class="form-item">
          <label>邮箱</label>
          <input 
            v-model="userInfo.email" 
            type="email" 
            placeholder="请输入邮箱"
          >
        </div>
      </div>

      <!-- 账号信息 -->
      <div class="account-section">
        <h3>账号信息</h3>
        <div class="info-item">
          <span class="label">用户名</span>
          <span class="value">{{ userInfo.username }}</span>
        </div>
        <div class="info-item">
          <span class="label">注册时间</span>
          <span class="value">{{ formatDate(userInfo.createTime) }}</span>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfo, updateUserInfo, updateAvatar } from '@/api/user'
import { uploadFile } from '@/api/upload'
import { setUserInfo } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Camera } from '@element-plus/icons-vue'

const router = useRouter()
const fileInput = ref(null)
const userInfo = ref({
  username: '',
  realName: '',
  phone: '',
  email: '',
  avatar: '',
  createTime: ''
})

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    userInfo.value = res.data || {}
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

const triggerUpload = () => {
  fileInput.value?.click()
}

const handleFileChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  // 验证文件大小 (2MB)
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }

  // 上传文件到服务器
  try {
    ElMessage.info('正在上传...')
    const res = await uploadFile(file, 'avatars')
    userInfo.value.avatar = res.data
    ElMessage.success('头像上传成功')
  } catch (error) {
    ElMessage.error('头像上传失败')
  }
}

const saveProfile = async () => {
  if (!userInfo.value.realName) {
    ElMessage.warning('请输入昵称')
    return
  }

  try {
    // 更新用户信息（包括头像URL）
    const updateData = {
      realName: userInfo.value.realName,
      phone: userInfo.value.phone,
      email: userInfo.value.email,
      avatar: userInfo.value.avatar
    }
    await updateUserInfo(updateData)

    // 更新本地存储的用户信息
    const currentUserInfo = userInfo.value
    setUserInfo(currentUserInfo)

    ElMessage.success('保存成功')
    router.back()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.edit-profile-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 16px 20px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-header h1 {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.back-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.save-btn {
  padding: 6px 16px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 16px;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.save-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.profile-content {
  padding: 20px;
}

/* 头像区域 */
.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
}

.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-wrapper:hover .avatar-mask {
  opacity: 1;
}

.avatar-mask span {
  font-size: 12px;
  margin-top: 4px;
}

/* 表单区域 */
.form-section {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
}

.form-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.form-item:last-child {
  border-bottom: none;
}

.form-item label {
  display: block;
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.form-item input {
  width: 100%;
  border: none;
  font-size: 16px;
  color: #333;
  outline: none;
}

.form-item input::placeholder {
  color: #ccc;
}

/* 账号信息 */
.account-section {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
}

.account-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-size: 14px;
  color: #666;
}

.info-item .value {
  font-size: 14px;
  color: #333;
}
</style>
