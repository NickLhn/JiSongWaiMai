<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-header">
        <div class="logo">
          <svg viewBox="0 0 48 48" fill="none">
            <rect width="48" height="48" rx="12" fill="#ff6b35"/>
            <path d="M24 12c-4.418 0-8 3.582-8 8 0 3.5 1.5 6.5 4 8.5V36c0 1.1.9 2 2 2h4c1.1 0 2-.9 2-2v-7.5c2.5-2 4-5 4-8.5 0-4.418-3.582-8-8-8z" fill="white"/>
          </svg>
        </div>
        <h1>创建账号</h1>
        <p>加入校园点餐，开启美食之旅</p>
      </div>

      <form class="register-form" @submit.prevent="handleRegister">
        <div class="form-group">
          <label>用户名</label>
          <div class="input-wrapper">
            <el-icon><User /></el-icon>
            <input
              v-model="form.username"
              type="text"
              placeholder="请输入用户名"
              required
            />
          </div>
        </div>

        <div class="form-group">
          <label>密码</label>
          <div class="input-wrapper">
            <el-icon><Lock /></el-icon>
            <input
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="请输入密码"
              required
            />
            <el-icon class="toggle-btn" @click="showPassword = !showPassword">
              <View v-if="showPassword" />
              <Hide v-else />
            </el-icon>
          </div>
        </div>

        <div class="form-group">
          <label>确认密码</label>
          <div class="input-wrapper">
            <el-icon><Lock /></el-icon>
            <input
              v-model="form.confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              placeholder="请再次输入密码"
              required
            />
            <el-icon class="toggle-btn" @click="showConfirmPassword = !showConfirmPassword">
              <View v-if="showConfirmPassword" />
              <Hide v-else />
            </el-icon>
          </div>
        </div>

        <div class="form-group">
          <label>真实姓名</label>
          <div class="input-wrapper">
            <el-icon><UserFilled /></el-icon>
            <input
              v-model="form.realName"
              type="text"
              placeholder="请输入真实姓名"
              required
            />
          </div>
        </div>

        <div class="form-group">
          <label>手机号</label>
          <div class="input-wrapper">
            <el-icon><Phone /></el-icon>
            <input
              v-model="form.phone"
              type="tel"
              placeholder="请输入手机号"
              required
            />
          </div>
        </div>

        <div class="form-group">
          <label>角色</label>
          <div class="role-selector">
            <button
              type="button"
              :class="['role-btn', { active: form.role === 0 }]"
              @click="form.role = 0"
            >
              <el-icon><User /></el-icon>
              <span>学生</span>
            </button>
            <button
              type="button"
              :class="['role-btn', { active: form.role === 1 }]"
              @click="form.role = 1"
            >
              <el-icon><Shop /></el-icon>
              <span>商家</span>
            </button>
          </div>
        </div>

        <button type="submit" class="submit-btn" :disabled="loading">
          <span v-if="!loading">注册</span>
          <span v-else class="loading-dots">
            <span></span><span></span><span></span>
          </span>
        </button>
      </form>

      <div class="login-link">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'
import { ElMessage } from 'element-plus'
import { User, Lock, View, Hide, UserFilled, Phone, Shop } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  role: 0  // 默认学生，0=学生, 1=商家
})

const handleRegister = async () => {
  if (form.password !== form.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  try {
    loading.value = true
    await register({
      username: form.username,
      password: form.password,
      realName: form.realName,
      phone: form.phone,
      role: form.role
    })
    ElMessage.success('注册成功')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-container {
  width: 100%;
  max-width: 440px;
  background: white;
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
}

.logo svg {
  width: 100%;
  height: 100%;
}

.register-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #212529;
  margin-bottom: 8px;
}

.register-header p {
  color: #6c757d;
  font-size: 15px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #495057;
  margin-bottom: 8px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-wrapper .el-icon {
  position: absolute;
  left: 16px;
  color: #adb5bd;
  font-size: 18px;
}

.input-wrapper .toggle-btn {
  left: auto;
  right: 16px;
  cursor: pointer;
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

.role-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.role-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  background: #f8f9fa;
  border: 2px solid transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.role-btn .el-icon {
  font-size: 28px;
  color: #6c757d;
}

.role-btn span {
  font-size: 14px;
  font-weight: 500;
  color: #495057;
}

.role-btn.active {
  background: #fff5f2;
  border-color: #ff6b35;
}

.role-btn.active .el-icon,
.role-btn.active span {
  color: #ff6b35;
}

.submit-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
  transition: all 0.2s;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(255, 107, 53, 0.3);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-dots {
  display: flex;
  gap: 4px;
  justify-content: center;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  background: white;
  border-radius: 50%;
  animation: bounce 1.4s ease-in-out infinite both;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.login-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #6c757d;
}

.login-link a {
  color: #ff6b35;
  font-weight: 600;
  text-decoration: none;
}

@media (max-width: 480px) {
  .register-container {
    padding: 32px 24px;
  }
  
  .register-header h1 {
    font-size: 24px;
  }
}
</style>
