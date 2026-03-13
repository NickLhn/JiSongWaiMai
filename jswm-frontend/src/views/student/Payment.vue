<template>
  <div class="payment-page">
    <header class="page-header">
      <button class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <h1>订单支付</h1>
      <div class="placeholder"></div>
    </header>

    <main class="payment-content">
      <!-- 支付金额 -->
      <div class="amount-section">
        <p class="amount-label">支付金额</p>
        <h2 class="amount-value">¥{{ orderInfo.payAmount }}</h2>
        <p class="order-no">订单号：{{ orderInfo.orderNo }}</p>
      </div>

      <!-- 支付方式 -->
      <div class="payment-methods">
        <h3>选择支付方式</h3>
        <div 
          v-for="method in paymentMethods" 
          :key="method.value"
          :class="['method-item', { active: selectedMethod === method.value }]"
          @click="selectedMethod = method.value"
        >
          <div class="method-icon">
            <el-icon :size="24">
              <component :is="method.icon" />
            </el-icon>
          </div>
          <div class="method-info">
            <span class="method-name">{{ method.label }}</span>
            <span class="method-desc">{{ method.desc }}</span>
          </div>
          <div class="method-check">
            <el-icon v-if="selectedMethod === method.value"><CircleCheck /></el-icon>
          </div>
        </div>
      </div>
    </main>

    <!-- 底部支付按钮 -->
    <footer class="payment-footer">
      <div class="footer-info">
        <span class="total-label">合计:</span>
        <span class="total-amount">¥{{ orderInfo.payAmount }}</span>
      </div>
      <button class="pay-btn" :disabled="submitting" @click="handlePay">
        {{ submitting ? '支付中...' : '确认支付' }}
      </button>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, CircleCheck, Wallet, CreditCard } from '@element-plus/icons-vue'
import { getOrderDetail, payOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()
const orderId = route.query.orderId

const orderInfo = ref({})
const selectedMethod = ref('wechat')
const submitting = ref(false)

const paymentMethods = [
  { value: 'wechat', label: '微信支付', desc: '推荐使用微信支付', icon: 'Wallet' },
  { value: 'alipay', label: '支付宝', desc: '数亿用户都在用', icon: 'CreditCard' }
]

const loadOrderDetail = async () => {
  if (!orderId) {
    ElMessage.error('订单ID不能为空')
    router.push('/orders')
    return
  }
  try {
    const res = await getOrderDetail(orderId)
    orderInfo.value = res.data
    if (orderInfo.value.status !== 0) {
      ElMessage.warning('该订单无需支付')
      router.push('/orders')
    }
  } catch (error) {
    ElMessage.error('加载订单失败')
    router.push('/orders')
  }
}

const handlePay = async () => {
  submitting.value = true
  try {
    await payOrder(orderId, selectedMethod.value)
    ElMessage.success('支付成功')
    router.push('/orders')
  } catch (error) {
    ElMessage.error(error.message || '支付失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.payment-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 80px;
  max-width: 600px;
  margin: 0 auto;
  position: relative;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
  color: white;
}

.back-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.2);
  border: none;
  border-radius: 50%;
  color: white;
  cursor: pointer;
}

.page-header h1 {
  font-size: 18px;
  font-weight: 600;
}

.placeholder {
  width: 36px;
}

.payment-content {
  padding: 16px;
}

.amount-section {
  background: white;
  border-radius: 12px;
  padding: 32px;
  text-align: center;
  margin-bottom: 16px;
}

.amount-label {
  color: #666;
  font-size: 14px;
  margin-bottom: 12px;
}

.amount-value {
  color: #ff6b35;
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 12px;
}

.order-no {
  color: #999;
  font-size: 12px;
}

.payment-methods {
  background: white;
  border-radius: 12px;
  padding: 16px;
}

.payment-methods h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.method-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.method-item:last-child {
  margin-bottom: 0;
}

.method-item.active {
  border-color: #ff6b35;
  background: #fff8f6;
}

.method-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 12px;
  margin-right: 12px;
  color: #ff6b35;
}

.method-info {
  flex: 1;
}

.method-name {
  display: block;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.method-desc {
  display: block;
  font-size: 12px;
  color: #999;
}

.method-check {
  color: #ff6b35;
  font-size: 24px;
}

.payment-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: white;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
  z-index: 1000;
  max-width: 600px;
  margin: 0 auto;
  box-sizing: border-box;
}

.footer-info {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.total-label {
  color: #666;
  font-size: 14px;
}

.total-amount {
  color: #ff6b35;
  font-size: 24px;
  font-weight: 700;
}

.pay-btn {
  padding: 12px 32px;
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.pay-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.pay-btn:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.4);
}
</style>
