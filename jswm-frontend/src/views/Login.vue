<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="brand-section">
          <div class="logo">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="48" height="48" rx="12" fill="#ff6b35"/>
              <path d="M24 12C18.477 12 14 16.477 14 22c0 3.5 1.5 6.5 4 8.5V36c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2v-5.5c2.5-2 4-5 4-8.5 0-5.523-4.477-10-10-10z" fill="white"/>
              <circle cx="24" cy="22" r="3" fill="#ff6b35"/>
            </svg>
          </div>
          <h1 class="brand-title">校园点餐</h1>
          <p class="brand-subtitle">让美食触手可及</p>
        </div>
        
        <div class="features">
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon :size="24"><Food /></el-icon>
            </div>
            <div class="feature-text">
              <h3>丰富美食</h3>
              <p>汇集校园周边优质商家</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon :size="24"><Timer /></el-icon>
            </div>
            <div class="feature-text">
              <h3>快速送达</h3>
              <p>平均30分钟送达宿舍</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon :size="24"><Wallet /></el-icon>
            </div>
            <div class="feature-text">
              <h3>优惠多多</h3>
              <p>新用户专享大额红包</p>
            </div>
          </div>
        </div>
      </div>
      
      <div class="login-right">
        <div class="login-form-wrapper">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>请登录您的账号</p>
          </div>
          
          <form class="login-form" @submit.prevent="handleLogin">
            <div class="form-group">
              <label for="username">用户名</label>
              <div class="input-wrapper">
                <el-icon class="input-icon"><User /></el-icon>
                <input
                  id="username"
                  v-model="loginForm.username"
                  type="text"
                  class="input"
                  placeholder="请输入用户名"
                  required
                />
              </div>
            </div>
            
            <div class="form-group">
              <label for="password">密码</label>
              <div class="input-wrapper">
                <el-icon class="input-icon"><Lock /></el-icon>
                <input
                  id="password"
                  v-model="loginForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  class="input"
                  placeholder="请输入密码"
                  required
                />
                <el-icon 
                  class="input-icon password-toggle"
                  @click="showPassword = !showPassword"
                >
                  <View v-if="showPassword" />
                  <Hide v-else />
                </el-icon>
              </div>
            </div>
            
            <div class="form-options">
              <label class="remember-me">
                <input type="checkbox" v-model="rememberMe" />
                <span>记住我</span>
              </label>
              <a href="#" class="forgot-password">忘记密码？</a>
            </div>
            
            <button 
              type="submit" 
              class="btn btn-primary login-btn"
              :disabled="loading"
            >
              <span v-if="!loading">登录</span>
              <span v-else class="loading-text">
                <span class="loading-dot"></span>
                <span class="loading-dot"></span>
                <span class="loading-dot"></span>
              </span>
            </button>
          </form>
          
          <div class="divider">
            <span>或者</span>
          </div>
          
          <div class="social-login">
            <button class="social-btn wechat">
              <el-icon><ChatDotRound /></el-icon>
              <span>微信登录</span>
            </button>
          </div>
          
          <div class="register-link">
            还没有账号？
            <router-link to="/register">立即注册</router-link>
          </div>
        </div>
      </div>
    </div>
    
    <div class="login-bg">
      <div class="bg-circle circle-1"></div>
      <div class="bg-circle circle-2"></div>
      <div class="bg-circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/user'
import { setToken, setUserInfo } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import { User, Lock, View, Hide, Food, Timer, Wallet, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const showPassword = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    loading.value = true
    const res = await login(loginForm)
    // res是响应拦截器返回的response.data，所以res.data才是实际用户数据
    const userData = res.data
    setToken(userData.token)
    setUserInfo(userData)
    ElMessage.success('登录成功')
    
    // 根据角色跳转到不同首页
    const role = userData.role
    console.log('登录成功，用户角色:', role)
    switch(role) {
      case 0: // 学生
        router.push('/home')
        break
      case 1: // 管理员
        router.push('/admin/dashboard')
        break
      case 2: // 商家
        router.push('/merchant/dashboard')
        break
      default:
        router.push('/home')
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(40px);
}

.circle-1 {
  width: 600px;
  height: 600px;
  top: -200px;
  right: -200px;
  animation: float 8s ease-in-out infinite;
}

.circle-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  left: -100px;
  animation: float 10s ease-in-out infinite reverse;
}

.circle-3 {
  width: 300px;
  height: 300px;
  top: 50%;
  left: 30%;
  animation: float 12s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(5deg); }
}

.login-container {
  display: flex;
  width: 100%;
  max-width: 1200px;
  min-height: 600px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  position: relative;
  z-index: 1;
  margin: 20px;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: white;
}

.brand-section {
  text-align: center;
}

.logo {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
}

.logo svg {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 10px 20px rgba(0, 0, 0, 0.2));
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.brand-subtitle {
  font-size: 18px;
  opacity: 0.9;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateX(8px);
}

.feature-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
}

.feature-text h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.feature-text p {
  font-size: 14px;
  opacity: 0.8;
}

.login-right {
  flex: 1;
  padding: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-form-wrapper {
  width: 100%;
  max-width: 400px;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-header h2 {
  font-size: 32px;
  font-weight: 700;
  color: #2d3436;
  margin-bottom: 8px;
}

.form-header p {
  font-size: 16px;
  color: #636e72;
}

.login-form {
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
  font-weight: 500;
  color: #2d3436;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  color: #b2bec3;
  font-size: 20px;
}

.input-icon.password-toggle {
  left: auto;
  right: 16px;
  cursor: pointer;
  transition: color 0.2s;
}

.input-icon.password-toggle:hover {
  color: #ff6b35;
}

.input {
  width: 100%;
  padding: 14px 16px 14px 48px;
  font-size: 15px;
  border: 2px solid #dfe6e9;
  border-radius: 12px;
  background: white;
  transition: all 0.3s ease;
  outline: none;
}

.input:focus {
  border-color: #ff6b35;
  box-shadow: 0 0 0 4px rgba(255, 107, 53, 0.1);
}

.input::placeholder {
  color: #b2bec3;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #636e72;
}

.remember-me input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: #ff6b35;
  cursor: pointer;
}

.forgot-password {
  color: #ff6b35;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.forgot-password:hover {
  color: #e55a2b;
}

.login-btn {
  width: 100%;
  padding: 16px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 8px;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(255, 107, 53, 0.3);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-text {
  display: flex;
  gap: 4px;
  justify-content: center;
}

.loading-dot {
  width: 8px;
  height: 8px;
  background: white;
  border-radius: 50%;
  animation: bounce 1.4s ease-in-out infinite both;
}

.loading-dot:nth-child(1) { animation-delay: -0.32s; }
.loading-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.divider {
  display: flex;
  align-items: center;
  margin: 24px 0;
  color: #b2bec3;
  font-size: 14px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #dfe6e9;
}

.divider span {
  padding: 0 16px;
}

.social-login {
  display: flex;
  justify-content: center;
}

.social-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 14px;
  font-size: 15px;
  font-weight: 500;
  background: #07c160;
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.social-btn:hover {
  background: #06ad56;
  transform: translateY(-2px);
}

.register-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #636e72;
}

.register-link a {
  color: #ff6b35;
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
}

.register-link a:hover {
  text-decoration: underline;
}

@media (max-width: 968px) {
  .login-left {
    display: none;
  }
  
  .login-right {
    padding: 40px 30px;
  }
}

@media (max-width: 480px) {
  .login-container {
    margin: 0;
    border-radius: 0;
    min-height: 100vh;
  }
  
  .login-right {
    padding: 30px 20px;
  }
  
  .form-header h2 {
    font-size: 28px;
  }
}
</style>
