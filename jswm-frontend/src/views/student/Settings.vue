<template>
  <div class="settings-page">
    <header class="page-header">
      <div class="header-content">
        <button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <h1>设置</h1>
        <div class="placeholder"></div>
      </div>
    </header>

    <main class="settings-content">
      <!-- 账号设置 -->
      <div class="settings-group">
        <h3>账号设置</h3>
        <div class="settings-list">
          <div class="setting-item" @click="$router.push('/profile/edit')">
            <div class="item-left">
              <div class="item-icon blue">
                <el-icon><User /></el-icon>
              </div>
              <span class="item-label">编辑资料</span>
            </div>
            <div class="item-right">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
          <div class="setting-item" @click="$router.push('/password/change')">
            <div class="item-left">
              <div class="item-icon green">
                <el-icon><Lock /></el-icon>
              </div>
              <span class="item-label">修改密码</span>
            </div>
            <div class="item-right">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
          <div class="setting-item" @click="$router.push('/address')">
            <div class="item-left">
              <div class="item-icon orange">
                <el-icon><Location /></el-icon>
              </div>
              <span class="item-label">收货地址</span>
            </div>
            <div class="item-right">
              <span class="item-value">管理</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 消息通知 -->
      <div class="settings-group">
        <h3>消息通知</h3>
        <div class="settings-list">
          <div class="setting-item">
            <div class="item-left">
              <div class="item-icon purple">
                <el-icon><Bell /></el-icon>
              </div>
              <span class="item-label">订单通知</span>
            </div>
            <div class="item-right">
              <el-switch v-model="settings.orderNotify" active-color="#667eea" />
            </div>
          </div>
          <div class="setting-item">
            <div class="item-left">
              <div class="item-icon pink">
                <el-icon><Message /></el-icon>
              </div>
              <span class="item-label">优惠活动</span>
            </div>
            <div class="item-right">
              <el-switch v-model="settings.promotionNotify" active-color="#667eea" />
            </div>
          </div>
          <div class="setting-item">
            <div class="item-left">
              <div class="item-icon cyan">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <span class="item-label">系统消息</span>
            </div>
            <div class="item-right">
              <el-switch v-model="settings.systemNotify" active-color="#667eea" />
            </div>
          </div>
        </div>
      </div>

      <!-- 通用设置 -->
      <div class="settings-group">
        <h3>通用设置</h3>
        <div class="settings-list">
          <div class="setting-item">
            <div class="item-left">
              <div class="item-icon yellow">
                <el-icon><Moon /></el-icon>
              </div>
              <span class="item-label">深色模式</span>
            </div>
            <div class="item-right">
              <el-switch v-model="settings.darkMode" active-color="#667eea" />
            </div>
          </div>
        </div>
      </div>

      <!-- 关于 -->
      <div class="settings-group">
        <h3>关于</h3>
        <div class="settings-list">
          <div class="setting-item">
            <div class="item-left">
              <div class="item-icon primary">
                <el-icon><InfoFilled /></el-icon>
              </div>
              <span class="item-label">关于我们</span>
            </div>
            <div class="item-right">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
          <div class="setting-item">
            <div class="item-left">
              <div class="item-icon primary">
                <el-icon><Document /></el-icon>
              </div>
              <span class="item-label">用户协议</span>
            </div>
            <div class="item-right">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
          <div class="setting-item">
            <div class="item-left">
              <div class="item-icon primary">
                <el-icon><Lock /></el-icon>
              </div>
              <span class="item-label">隐私政策</span>
            </div>
            <div class="item-right">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
          <div class="setting-item">
            <div class="item-left">
              <div class="item-icon primary">
                <el-icon><Service /></el-icon>
              </div>
              <span class="item-label">联系客服</span>
            </div>
            <div class="item-right">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 版本信息 -->
      <div class="version-info">
        <p>当前版本 v1.0.0</p>
        <p class="copyright">© 2024 极送外卖 版权所有</p>
      </div>

      <!-- 退出登录 -->
      <button class="btn-logout" @click="logout">
        <el-icon><SwitchButton /></el-icon>
        退出登录
      </button>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { removeToken, removeUserInfo } from '@/utils/auth'
import {
  ArrowLeft, User, Lock, Location, ArrowRight, Bell, Message,
  ChatDotRound, Moon, Delete, InfoFilled, Document, Service,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const cacheSize = ref('12.5 MB')

const settings = ref({
  orderNotify: true,
  promotionNotify: true,
  systemNotify: true,
  darkMode: false
})

const logout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    removeToken()
    removeUserInfo()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    // 用户取消
  }
}
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
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

.settings-content {
  padding: 20px;
}

/* 设置分组 */
.settings-group {
  margin-bottom: 20px;
}

.settings-group h3 {
  font-size: 14px;
  color: #999;
  margin: 0 0 12px 16px;
  font-weight: 500;
}

.settings-list {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.setting-item:active {
  background: #f5f5f5;
}

.setting-item:not(:last-child) {
  border-bottom: 1px solid #f5f5f5;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
}

.item-icon.blue { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.item-icon.green { background: linear-gradient(135deg, #51cf66 0%, #40c057 100%); }
.item-icon.orange { background: linear-gradient(135deg, #ff922b 0%, #fd7e14 100%); }
.item-icon.purple { background: linear-gradient(135deg, #9775fa 0%, #7950f2 100%); }
.item-icon.pink { background: linear-gradient(135deg, #f783ac 0%, #e64980 100%); }
.item-icon.cyan { background: linear-gradient(135deg, #22b8cf 0%, #15aabf 100%); }
.item-icon.yellow { background: linear-gradient(135deg, #fcc419 0%, #fab005 100%); }
.item-icon.red { background: linear-gradient(135deg, #ff6b6b 0%, #fa5252 100%); }
.item-icon.primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }

.item-label {
  font-size: 15px;
  color: #333;
}

.item-right {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #999;
}

.item-value {
  font-size: 14px;
  color: #999;
}

/* 版本信息 */
.version-info {
  text-align: center;
  padding: 32px 20px;
  color: #999;
  font-size: 13px;
}

.version-info p {
  margin: 4px 0;
}

.version-info .copyright {
  font-size: 12px;
  opacity: 0.7;
}

/* 退出登录按钮 */
.btn-logout {
  width: 100%;
  padding: 16px;
  background: #fff;
  color: #ff6b6b;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.btn-logout:hover {
  background: #ffebee;
}

/* 响应式 */
@media (max-width: 480px) {
  .settings-content {
    padding: 16px;
  }
}
</style>
