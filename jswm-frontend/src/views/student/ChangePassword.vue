<template>
  <div class="change-password-page">
    <header class="page-header">
      <div class="header-content">
        <button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <h1>修改密码</h1>
        <div class="placeholder"></div>
      </div>
    </header>

    <main class="password-content">
      <div class="password-form">
        <div class="form-item">
          <label>原密码</label>
          <div class="input-wrapper">
            <input 
              v-model="form.oldPassword" 
              :type="showOldPassword ? 'text' : 'password'" 
              placeholder="请输入原密码"
            >
            <button class="toggle-btn" @click="showOldPassword = !showOldPassword">
              <el-icon>
                <View v-if="showOldPassword" />
                <Hide v-else />
              </el-icon>
            </button>
          </div>
        </div>

        <div class="form-item">
          <label>新密码</label>
          <div class="input-wrapper">
            <input 
              v-model="form.newPassword" 
              :type="showNewPassword ? 'text' : 'password'" 
              placeholder="请输入新密码（6-20位）"
            >
            <button class="toggle-btn" @click="showNewPassword = !showNewPassword">
              <el-icon>
                <View v-if="showNewPassword" />
                <Hide v-else />
              </el-icon>
            </button>
          </div>
          <div class="password-strength" v-if="form.newPassword">
            <div class="strength-bar">
              <div 
                class="strength-fill" 
                :style="{ width: strengthPercent + '%', background: strengthColor }"
              ></div>
            </div>
            <span class="strength-text" :style="{ color: strengthColor }">
              {{ strengthText }}
            </span>
          </div>
        </div>

        <div class="form-item">
          <label>确认新密码</label>
          <div class="input-wrapper">
            <input 
              v-model="form.confirmPassword" 
              :type="showConfirmPassword ? 'text' : 'password'" 
              placeholder="请再次输入新密码"
            >
            <button class="toggle-btn" @click="showConfirmPassword = !showConfirmPassword">
              <el-icon>
                <View v-if="showConfirmPassword" />
                <Hide v-else />
              </el-icon>
            </button>
          </div>
          <p v-if="form.confirmPassword && form.confirmPassword !== form.newPassword" class="error-text">
            两次输入的密码不一致
          </p>
        </div>
      </div>

      <div class="tips-section">
        <h3>密码要求</h3>
        <ul>
          <li :class="{ valid: hasLength }">长度为6-20个字符</li>
          <li :class="{ valid: hasLetter }">包含至少一个字母</li>
          <li :class="{ valid: hasNumber }">包含至少一个数字</li>
          <li :class="{ valid: hasSpecial }">包含至少一个特殊字符（可选）</li>
        </ul>
      </div>

      <button 
        class="btn-submit" 
        :disabled="!canSubmit"
        @click="submit"
      >
        确认修改
      </button>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { updatePassword } from '@/api/user'
import { ElMessage } from 'element-plus'
import { ArrowLeft, View, Hide } from '@element-plus/icons-vue'

const router = useRouter()
const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const showOldPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

// 密码强度计算
const hasLength = computed(() => form.value.newPassword.length >= 6 && form.value.newPassword.length <= 20)
const hasLetter = computed(() => /[a-zA-Z]/.test(form.value.newPassword))
const hasNumber = computed(() => /\d/.test(form.value.newPassword))
const hasSpecial = computed(() => /[!@#$%^&*(),.?":{}|<>]/.test(form.value.newPassword))

const strengthScore = computed(() => {
  let score = 0
  if (hasLength.value) score++
  if (hasLetter.value) score++
  if (hasNumber.value) score++
  if (hasSpecial.value) score++
  return score
})

const strengthPercent = computed(() => (strengthScore.value / 4) * 100)

const strengthText = computed(() => {
  const texts = ['', '弱', '中', '强', '非常强']
  return texts[strengthScore.value] || ''
})

const strengthColor = computed(() => {
  const colors = ['', '#ff6b6b', '#ffc107', '#51cf66', '#2ecc71']
  return colors[strengthScore.value] || '#ccc'
})

const canSubmit = computed(() => {
  return form.value.oldPassword && 
         hasLength.value && 
         hasLetter.value && 
         hasNumber.value &&
         form.value.newPassword === form.value.confirmPassword
})

const submit = async () => {
  if (!canSubmit.value) return

  try {
    await updatePassword(form.value.oldPassword, form.value.newPassword)
    ElMessage.success('密码修改成功，请重新登录')
    // 清除登录状态
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '修改失败')
  }
}
</script>

<style scoped>
.change-password-page {
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

.placeholder {
  width: 36px;
}

.password-content {
  padding: 20px;
}

/* 密码表单 */
.password-form {
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
  color: #666;
  margin-bottom: 12px;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: #f5f7fa;
  border-radius: 12px;
  padding: 0 16px;
}

.input-wrapper input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 14px 0;
  font-size: 15px;
  color: #333;
  outline: none;
}

.input-wrapper input::placeholder {
  color: #bbb;
}

.toggle-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 密码强度 */
.password-strength {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: #e0e0e0;
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 2px;
  transition: all 0.3s;
}

.strength-text {
  font-size: 12px;
  font-weight: 500;
}

.error-text {
  font-size: 12px;
  color: #ff6b6b;
  margin-top: 8px;
}

/* 提示区域 */
.tips-section {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
}

.tips-section h3 {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.tips-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-section li {
  padding: 8px 0;
  font-size: 14px;
  color: #999;
  position: relative;
  padding-left: 20px;
  transition: all 0.3s;
}

.tips-section li::before {
  content: '○';
  position: absolute;
  left: 0;
  color: #ccc;
}

.tips-section li.valid {
  color: #51cf66;
}

.tips-section li.valid::before {
  content: '●';
  color: #51cf66;
}

/* 提交按钮 */
.btn-submit {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-submit:hover {
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.btn-submit:disabled {
  background: #ccc;
  cursor: not-allowed;
  box-shadow: none;
}
</style>
