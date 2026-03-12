<template>
  <div class="forgot-password-page">
    <div class="page-container">
      <!-- Header -->
      <header class="page-header">
        <button class="back-btn" @click="$router.push('/login')">
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <h1>忘记密码</h1>
        <div class="placeholder"></div>
      </header>

      <!-- Progress Steps -->
      <div class="progress-steps">
        <div :class="['step', { active: currentStep >= 1, completed: currentStep > 1 }]">
          <div class="step-number">1</div>
          <span class="step-label">验证身份</span>
        </div>
        <div class="step-line" :class="{ completed: currentStep > 1 }"></div>
        <div :class="['step', { active: currentStep >= 2, completed: currentStep > 2 }]">
          <div class="step-number">2</div>
          <span class="step-label">重置密码</span>
        </div>
        <div class="step-line" :class="{ completed: currentStep > 2 }"></div>
        <div :class="['step', { active: currentStep >= 3 }]">
          <div class="step-number">3</div>
          <span class="step-label">完成</span>
        </div>
      </div>

      <!-- Step 1: Verify Identity -->
      <div v-if="currentStep === 1" class="step-content">
        <div class="step-header">
          <div class="step-icon">
            <el-icon><User /></el-icon>
          </div>
          <h2>验证身份</h2>
          <p>请输入您的账号信息以验证身份</p>
        </div>

        <form class="form" @submit.prevent="handleVerify">
          <div class="form-group">
            <label>用户名</label>
            <div class="input-wrapper">
              <el-icon class="input-icon"><User /></el-icon>
              <input
                v-model="form.username"
                type="text"
                placeholder="请输入用户名"
                required
              />
            </div>
          </div>

          <div class="form-group">
            <label>手机号</label>
            <div class="input-wrapper">
              <el-icon class="input-icon"><Phone /></el-icon>
              <input
                v-model="form.phone"
                type="tel"
                placeholder="请输入注册手机号"
                required
              />
            </div>
          </div>

          <div class="form-group">
            <label>验证码</label>
            <div class="input-wrapper verify-code">
              <el-icon class="input-icon"><Message /></el-icon>
              <input
                v-model="form.verifyCode"
                type="text"
                placeholder="请输入验证码"
                required
              />
              <button
                type="button"
                class="send-code-btn"
                :disabled="countdown > 0"
                @click="sendVerifyCode"
              >
                {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
              </button>
            </div>
          </div>

          <button type="submit" class="submit-btn" :disabled="verifying">
            <span v-if="!verifying">下一步</span>
            <span v-else class="loading-dots">
              <span></span><span></span><span></span>
            </span>
          </button>
        </form>
      </div>

      <!-- Step 2: Reset Password -->
      <div v-if="currentStep === 2" class="step-content">
        <div class="step-header">
          <div class="step-icon">
            <el-icon><Lock /></el-icon>
          </div>
          <h2>重置密码</h2>
          <p>请设置您的新密码</p>
        </div>

        <form class="form" @submit.prevent="handleReset">
          <div class="form-group">
            <label>新密码</label>
            <div class="input-wrapper">
              <el-icon class="input-icon"><Lock /></el-icon>
              <input
                v-model="form.newPassword"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入新密码"
                required
              />
              <el-icon
                class="toggle-btn"
                @click="showPassword = !showPassword"
              >
                <View v-if="showPassword" />
                <Hide v-else />
              </el-icon>
            </div>
            <div class="password-strength">
              <div class="strength-bar">
                <div
                  class="strength-fill"
                  :style="{ width: passwordStrength + '%', background: strengthColor }"
                ></div>
              </div>
              <span class="strength-text">{{ strengthText }}</span>
            </div>
          </div>

          <div class="form-group">
            <label>确认密码</label>
            <div class="input-wrapper">
              <el-icon class="input-icon"><Lock /></el-icon>
              <input
                v-model="form.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="请再次输入新密码"
                required
              />
              <el-icon
                class="toggle-btn"
                @click="showConfirmPassword = !showConfirmPassword"
              >
                <View v-if="showConfirmPassword" />
                <Hide v-else />
              </el-icon>
            </div>
          </div>

          <div class="password-tips">
            <h4>密码要求：</h4>
            <ul>
              <li :class="{ met: hasLength }">至少8个字符</li>
              <li :class="{ met: hasLetter }">包含字母</li>
              <li :class="{ met: hasNumber }">包含数字</li>
              <li :class="{ met: hasSpecial }">包含特殊字符</li>
            </ul>
          </div>

          <button type="submit" class="submit-btn" :disabled="resetting">
            <span v-if="!resetting">重置密码</span>
            <span v-else class="loading-dots">
              <span></span><span></span><span></span>
            </span>
          </button>
        </form>
      </div>

      <!-- Step 3: Success -->
      <div v-if="currentStep === 3" class="step-content success">
        <div class="success-icon">
          <el-icon><CircleCheckFilled /></el-icon>
        </div>
        <h2>密码重置成功</h2>
        <p>您的密码已成功重置，请使用新密码登录</p>
        <button class="submit-btn" @click="$router.push('/login')">
          去登录
        </button>
      </div>
    </div>

    <!-- Background Decoration -->
    <div class="bg-decoration">
      <div class="bg-circle circle-1"></div>
      <div class="bg-circle circle-2"></div>
      <div class="bg-circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, User, Phone, Message, Lock, View, Hide, CircleCheckFilled } from '@element-plus/icons-vue'

const router = useRouter()
const currentStep = ref(1)
const countdown = ref(0)
const verifying = ref(false)
const resetting = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const form = reactive({
  username: '',
  phone: '',
  verifyCode: '',
  newPassword: '',
  confirmPassword: ''
})

// Password strength calculation
const hasLength = computed(() => form.newPassword.length >= 8)
const hasLetter = computed(() => /[a-zA-Z]/.test(form.newPassword))
const hasNumber = computed(() => /\d/.test(form.newPassword))
const hasSpecial = computed(() => /[!@#$%^&*(),.?":{}|<>]/.test(form.newPassword))

const passwordStrength = computed(() => {
  let strength = 0
  if (hasLength.value) strength += 25
  if (hasLetter.value) strength += 25
  if (hasNumber.value) strength += 25
  if (hasSpecial.value) strength += 25
  return strength
})

const strengthText = computed(() => {
  if (passwordStrength.value === 0) return '请输入密码'
  if (passwordStrength.value <= 25) return '弱'
  if (passwordStrength.value <= 50) return '中'
  if (passwordStrength.value <= 75) return '强'
  return '非常强'
})

const strengthColor = computed(() => {
  if (passwordStrength.value <= 25) return '#dc3545'
  if (passwordStrength.value <= 50) return '#ffc107'
  if (passwordStrength.value <= 75) return '#17a2b8'
  return '#28a745'
})

const sendVerifyCode = () => {
  if (!form.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  
  countdown.value = 60
  ElMessage.success('验证码已发送')
  
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

const handleVerify = async () => {
  if (!form.username || !form.phone || !form.verifyCode) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  verifying.value = true
  
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    currentStep.value = 2
    ElMessage.success('验证成功')
  } catch (error) {
    ElMessage.error('验证失败，请重试')
  } finally {
    verifying.value = false
  }
}

const handleReset = async () => {
  if (!form.newPassword || !form.confirmPassword) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  if (form.newPassword !== form.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  if (passwordStrength.value < 50) {
    ElMessage.warning('密码强度不足，请加强密码')
    return
  }
  
  resetting.value = true
  
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    currentStep.value = 3
    ElMessage.success('密码重置成功')
  } catch (error) {
    ElMessage.error('重置失败，请重试')
  } finally {
    resetting.value = false
  }
}
</script>

<style scoped>
.forgot-password-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.page-container {
  width: 100%;
  max-width: 480px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

/* Header */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
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
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 12px;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.placeholder {
  width: 40px;
}

/* Progress Steps */
.progress-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px 20px;
  background: #f8f9fa;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.step-number {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #e9ecef;
  color: #6c757d;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s;
}

.step.active .step-number {
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
}

.step.completed .step-number {
  background: #28a745;
  color: white;
}

.step-label {
  font-size: 12px;
  color: #6c757d;
  font-weight: 500;
}

.step.active .step-label {
  color: #ff6b35;
}

.step-line {
  width: 60px;
  height: 2px;
  background: #e9ecef;
  margin: 0 8px;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.step-line.completed {
  background: #28a745;
}

/* Step Content */
.step-content {
  padding: 20px 30px 40px;
}

.step-header {
  text-align: center;
  margin-bottom: 30px;
}

.step-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #fff5f2 0%, #fff0eb 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 36px;
  color: #ff6b35;
}

.step-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #212529;
  margin-bottom: 8px;
}

.step-header p {
  font-size: 14px;
  color: #6c757d;
}

/* Form */
.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #495057;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  color: #adb5bd;
  font-size: 18px;
}

.input-wrapper input {
  width: 100%;
  padding: 14px 16px 14px 48px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  font-size: 15px;
  outline: none;
  transition: all 0.2s;
}

.input-wrapper input:focus {
  border-color: #ff6b35;
  box-shadow: 0 0 0 4px rgba(255, 107, 53, 0.1);
}

.toggle-btn {
  position: absolute;
  right: 16px;
  color: #adb5bd;
  cursor: pointer;
  font-size: 18px;
}

.input-wrapper.verify-code input {
  padding-right: 110px;
}

.send-code-btn {
  position: absolute;
  right: 6px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.send-code-btn:disabled {
  background: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
}

/* Password Strength */
.password-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 4px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: #e9ecef;
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
  color: #6c757d;
  min-width: 50px;
  text-align: right;
}

/* Password Tips */
.password-tips {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
}

.password-tips h4 {
  font-size: 13px;
  font-weight: 600;
  color: #495057;
  margin-bottom: 10px;
}

.password-tips ul {
  list-style: none;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.password-tips li {
  font-size: 12px;
  color: #6c757d;
  display: flex;
  align-items: center;
  gap: 6px;
}

.password-tips li::before {
  content: '○';
  font-size: 10px;
}

.password-tips li.met {
  color: #28a745;
}

.password-tips li.met::before {
  content: '✓';
  color: #28a745;
  font-weight: bold;
}

/* Submit Button */
.submit-btn {
  padding: 16px;
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 10px;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(255, 107, 53, 0.3);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Loading Dots */
.loading-dots {
  display: flex;
  gap: 4px;
  justify-content: center;
  align-items: center;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  background: white;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* Success State */
.step-content.success {
  text-align: center;
  padding: 40px 30px;
}

.success-icon {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  font-size: 48px;
  color: #28a745;
}

.success h2 {
  font-size: 24px;
  font-weight: 700;
  color: #212529;
  margin-bottom: 12px;
}

.success p {
  font-size: 14px;
  color: #6c757d;
  margin-bottom: 30px;
}

/* Background Decoration */
.bg-decoration {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
  animation: float 20s infinite ease-in-out;
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  left: -50px;
  animation: float 15s infinite ease-in-out reverse;
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: pulse 10s infinite ease-in-out;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  33% { transform: translate(30px, -30px) rotate(120deg); }
  66% { transform: translate(-20px, 20px) rotate(240deg); }
}

@keyframes pulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.1; }
  50% { transform: translate(-50%, -50%) scale(1.2); opacity: 0.2; }
}

/* Responsive */
@media (max-width: 480px) {
  .forgot-password-page {
    padding: 0;
  }
  
  .page-container {
    border-radius: 0;
    min-height: 100vh;
  }
}
</style>