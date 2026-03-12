<template>
  <div class="admin-settings">
    <h2>系统设置</h2>
    
    <div class="settings-section">
      <h3>基础设置</h3>
      <div class="form-row">
        <div class="form-group">
          <label>系统名称</label>
          <input v-model="settings.systemName" type="text" />
        </div>
        <div class="form-group">
          <label>系统Logo</label>
          <div class="upload-box">
            <el-icon><Plus /></el-icon>
            <span>点击上传</span>
          </div>
        </div>
      </div>
      <div class="form-group">
        <label>系统公告</label>
        <textarea v-model="settings.announcement" rows="3"></textarea>
      </div>
    </div>

    <div class="settings-section">
      <h3>订单设置</h3>
      <div class="form-row">
        <div class="form-group">
          <label>自动取消时间（分钟）</label>
          <input v-model="settings.autoCancelTime" type="number" />
        </div>
        <div class="form-group">
          <label>配送费（元）</label>
          <input v-model="settings.deliveryFee" type="number" />
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>起送金额（元）</label>
          <input v-model="settings.minOrderAmount" type="number" />
        </div>
        <div class="form-group">
          <label>预计配送时间（分钟）</label>
          <input v-model="settings.deliveryTime" type="number" />
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
          <input v-model="settings.loginFailLimit" type="number" />
        </div>
        <div class="form-group">
          <label>锁定时间（分钟）</label>
          <input v-model="settings.lockTime" type="number" />
        </div>
      </div>
    </div>

    <button class="save-btn" @click="saveSettings">保存设置</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const settings = ref({
  systemName: '吉送外卖',
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

const saveSettings = () => {
  ElMessage.success('设置保存成功')
}
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
}

.save-btn:hover {
  background: #e55a2b;
}
</style>
