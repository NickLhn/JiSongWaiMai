<template>
  <div class="admin-settings">
    <h2>系统设置</h2>
    
    <div v-if="loading" class="loading-state">
      <el-icon class="loading-icon"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
    
    <template v-else>
      <div class="settings-section">
        <h3>基础设置</h3>
        <div class="form-row">
          <div class="form-group">
            <label>系统名称</label>
            <input v-model="settings.systemName" type="text" placeholder="请输入系统名称" />
          </div>
          <div class="form-group">
            <label>系统Logo</label>
            <div class="upload-wrapper">
              <div v-if="settings.systemLogo" class="logo-preview">
                <img :src="settings.systemLogo" alt="Logo" />
                <div class="logo-overlay" @click="triggerUpload">
                  <el-icon><Refresh /></el-icon>
                  <span>更换</span>
                </div>
              </div>
              <div v-else class="upload-box" @click="triggerUpload">
                <el-icon><Plus /></el-icon>
                <span>点击上传</span>
              </div>
              <input
                ref="logoInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleLogoUpload"
              />
            </div>
          </div>
        </div>
        <div class="form-group">
          <label>系统公告</label>
          <textarea v-model="settings.announcement" rows="3" placeholder="请输入系统公告"></textarea>
        </div>
      </div>

      <div class="settings-section">
        <h3>订单设置</h3>
        <div class="form-row">
          <div class="form-group">
            <label>自动取消时间（分钟）</label>
            <input v-model.number="settings.autoCancelTime" type="number" min="0" placeholder="15" />
          </div>
          <div class="form-group">
            <label>配送费（元）</label>
            <input v-model.number="settings.deliveryFee" type="number" min="0" placeholder="0" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>起送金额（元）</label>
            <input v-model.number="settings.minOrderAmount" type="number" min="0" placeholder="15" />
          </div>
          <div class="form-group">
            <label>预计配送时间（分钟）</label>
            <input v-model.number="settings.deliveryTime" type="number" min="0" placeholder="30" />
          </div>
        </div>
      </div>

      <div class="settings-section">
        <h3>通知设置</h3>
        <div class="switch-list">
          <div class="switch-item">
            <div class="switch-info">
              <span class="switch-label">新订单通知</span>
              <span class="switch-desc">有新订单时发送通知给商家</span>
            </div>
            <el-switch v-model="settings.notifyNewOrder" />
          </div>
          <div class="switch-item">
            <div class="switch-info">
              <span class="switch-label">订单完成通知</span>
              <span class="switch-desc">订单完成后通知用户</span>
            </div>
            <el-switch v-model="settings.notifyOrderComplete" />
          </div>
          <div class="switch-item">
            <div class="switch-info">
              <span class="switch-label">商家入驻通知</span>
              <span class="switch-desc">有新商家申请入驻时通知管理员</span>
            </div>
            <el-switch v-model="settings.notifyMerchantApply" />
          </div>
        </div>
      </div>

      <div class="settings-section">
        <h3>安全设置</h3>
        <div class="form-row">
          <div class="form-group">
            <label>登录失败锁定次数</label>
            <input v-model.number="settings.loginFailLimit" type="number" min="1" placeholder="5" />
          </div>
          <div class="form-group">
            <label>锁定时间（分钟）</label>
            <input v-model.number="settings.lockTime" type="number" min="1" placeholder="30" />
          </div>
        </div>
      </div>

      <button class="save-btn" :disabled="saving" @click="saveSettings">
        <el-icon v-if="saving" class="loading-icon"><Loading /></el-icon>
        <span>{{ saving ? '保存中...' : '保存设置' }}</span>
      </button>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Loading, Refresh } from '@element-plus/icons-vue'
import { getSettings, updateSettings } from '@/api/adminSetting'
import { uploadImage } from '@/api/upload'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const logoInput = ref(null)

const settings = ref({
  systemName: '吉送外卖',
  systemLogo: '',
  announcement: '欢迎使用吉送外卖系统，祝您用餐愉快！',
  autoCancelTime: 15,
  deliveryFee: 0,
  minOrderAmount: 15,
  deliveryTime: 30,
  notifyNewOrder: true,
  notifyOrderComplete: true,
  notifyMerchantApply: true,
  loginFailLimit: 5,
  lockTime: 30
})

// 加载设置
const loadSettings = async () => {
  try {
    loading.value = true
    const res = await getSettings()
    if (res.data) {
      settings.value = { ...settings.value, ...res.data }
    }
  } catch (error) {
    ElMessage.error('加载设置失败')
  } finally {
    loading.value = false
  }
}

// 触发上传
const triggerUpload = () => {
  logoInput.value?.click()
}

// 处理Logo上传
const handleLogoUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请上传图片文件')
    return
  }

  // 验证文件大小（最大2MB）
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }

  try {
    ElMessage.info('正在上传...')
    const res = await uploadImage(file)
    settings.value.systemLogo = res.data
    ElMessage.success('Logo上传成功')
  } catch (error) {
    ElMessage.error('上传失败')
  }

  // 清空input，允许重复上传同一文件
  event.target.value = ''
}

// 保存设置
const saveSettings = async () => {
  try {
    saving.value = true
    await updateSettings(settings.value)
    ElMessage.success('设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 页面加载时获取设置
onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.admin-settings {
  padding: 0;
}

.admin-settings h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1f1f1f;
  margin: 0 0 24px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  color: #8c8c8c;
  gap: 12px;
}

.loading-state .el-icon {
  font-size: 32px;
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.settings-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.settings-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f1f1f;
  margin: 0 0 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #595959;
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #ff6b35;
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.form-group textarea {
  resize: vertical;
}

.upload-wrapper {
  display: inline-block;
}

.upload-box {
  width: 120px;
  height: 120px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;
  color: #8c8c8c;
}

.upload-box:hover {
  border-color: #ff6b35;
  color: #ff6b35;
}

.logo-preview {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
}

.logo-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.logo-overlay {
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
  gap: 4px;
  color: white;
  opacity: 0;
  transition: opacity 0.2s;
}

.logo-preview:hover .logo-overlay {
  opacity: 1;
}

.logo-overlay .el-icon {
  font-size: 20px;
}

.logo-overlay span {
  font-size: 12px;
}

.switch-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.switch-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.switch-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.switch-label {
  font-size: 14px;
  font-weight: 500;
  color: #1f1f1f;
}

.switch-desc {
  font-size: 12px;
  color: #8c8c8c;
}

.save-btn {
  width: 100%;
  padding: 16px;
  background: #ff6b35;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.save-btn:hover:not(:disabled) {
  background: #e55a2b;
}

.save-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.save-btn .loading-icon {
  animation: rotate 1s linear infinite;
}
</style>
