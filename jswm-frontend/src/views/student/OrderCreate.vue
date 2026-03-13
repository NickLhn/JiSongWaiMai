<template>
  <div class="order-create-page">
    <header class="page-header">
      <button class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <h1>确认订单</h1>
      <div class="placeholder"></div>
    </header>

    <main class="order-content">
      <!-- 配送地址 -->
      <section class="address-section" @click="selectAddress">
        <div v-if="selectedAddress" class="address-info">
          <div class="address-header">
            <el-icon><Location /></el-icon>
            <span class="name">{{ selectedAddress.receiverName }}</span>
            <span class="phone">{{ selectedAddress.receiverPhone }}</span>
          </div>
          <p class="address-detail">{{ selectedAddress.detailAddress }}</p>
        </div>
        <div v-else class="no-address">
          <el-icon><Location /></el-icon>
          <span>请选择配送地址</span>
        </div>
        <el-icon class="arrow"><ArrowRight /></el-icon>
      </section>

      <!-- 商品信息 -->
      <section class="goods-section">
        <div class="merchant-name">
          <el-icon><Shop /></el-icon>
          <span>{{ merchantName }}</span>
        </div>
        <div class="goods-list">
          <div v-for="item in cartItems" :key="item.id" class="goods-item">
            <img :src="item.dishImage || '/dish-default.jpg'" class="goods-image" />
            <div class="goods-info">
              <h4>{{ item.dishName }}</h4>
              <p class="goods-price">¥{{ item.dishPrice }}</p>
            </div>
            <span class="goods-quantity">x{{ item.quantity }}</span>
          </div>
        </div>
      </section>

      <!-- 订单备注 -->
      <section class="remark-section">
        <span class="label">订单备注</span>
        <input 
          v-model="remark" 
          type="text" 
          placeholder="请输入备注信息（选填）"
          class="remark-input"
        />
      </section>

      <!-- 费用明细 -->
      <section class="fee-section">
        <div class="fee-row">
          <span>商品总价</span>
          <span>¥{{ cartTotal }}</span>
        </div>
        <div class="fee-row">
          <span>配送费</span>
          <span>¥0</span>
        </div>
        <div class="fee-row total">
          <span>实付金额</span>
          <span class="total-price">¥{{ cartTotal }}</span>
        </div>
      </section>
    </main>

    <!-- 底部结算栏 -->
    <footer class="order-footer">
      <div class="footer-info">
        <span class="total-label">合计:</span>
        <span class="total-amount">¥{{ cartTotal }}</span>
      </div>
      <button 
        class="submit-btn" 
        :disabled="!selectedAddress || submitting"
        @click="submitOrder"
      >
        {{ submitting ? '提交中...' : '提交订单' }}
      </button>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList } from '@/api/cart'
import { createOrder } from '@/api/order'
import { getAddresses } from '@/api/user'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Location, ArrowRight, Shop } from '@element-plus/icons-vue'

const router = useRouter()
const cartItems = ref([])
const addresses = ref([])
const selectedAddress = ref(null)
const remark = ref('')
const submitting = ref(false)

const merchantName = computed(() => {
  return cartItems.value[0]?.merchantName || ''
})

const merchantId = computed(() => {
  return cartItems.value[0]?.merchantId || null
})

const cartTotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.dishPrice * item.quantity), 0).toFixed(2)
})

const loadCart = async () => {
  try {
    const res = await getCartList()
    cartItems.value = res.data
    if (cartItems.value.length === 0) {
      ElMessage.warning('购物车为空')
      router.replace('/cart')
    }
  } catch (error) {
    ElMessage.error('加载购物车失败')
  }
}

const loadAddresses = async () => {
  try {
    const res = await getAddresses()
    addresses.value = res.data || []
    // 选择默认地址
    selectedAddress.value = addresses.value.find(addr => addr.isDefault) || addresses.value[0]
  } catch (error) {
    console.error('加载地址失败', error)
  }
}

const selectAddress = () => {
  router.push('/address?select=true')
}

const submitOrder = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择配送地址')
    return
  }
  
  if (!merchantId.value) {
    ElMessage.error('商家信息错误')
    return
  }

  try {
    submitting.value = true
    const res = await createOrder({
      merchantId: merchantId.value,
      receiverName: selectedAddress.value.receiverName,
      receiverPhone: selectedAddress.value.receiverPhone,
      deliveryAddress: selectedAddress.value.detailAddress,
      remark: remark.value
    })
    
    ElMessage.success('订单创建成功')
    // 跳转到支付页面或订单详情
    router.push(`/orders`)
  } catch (error) {
    ElMessage.error(error.message || '创建订单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCart()
  loadAddresses()
})
</script>

<style scoped>
.order-create-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 80px;
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

.order-content {
  padding: 12px;
}

.address-section {
  display: flex;
  align-items: center;
  padding: 16px;
  background: white;
  border-radius: 12px;
  margin-bottom: 12px;
  cursor: pointer;
}

.address-info {
  flex: 1;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.address-header .el-icon {
  color: #ff6b35;
  font-size: 18px;
}

.name {
  font-weight: 600;
  color: #333;
}

.phone {
  color: #666;
  font-size: 14px;
}

.address-detail {
  color: #333;
  font-size: 14px;
  padding-left: 26px;
}

.no-address {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #999;
}

.arrow {
  color: #ccc;
}

.goods-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
}

.merchant-name {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 12px;
  font-weight: 600;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.goods-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.goods-info {
  flex: 1;
}

.goods-info h4 {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.goods-price {
  color: #ff6b35;
  font-weight: 600;
}

.goods-quantity {
  color: #999;
  font-size: 14px;
}

.remark-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  margin-bottom: 12px;
}

.label {
  color: #333;
  font-weight: 500;
  white-space: nowrap;
}

.remark-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
  color: #333;
}

.remark-input::placeholder {
  color: #999;
}

.fee-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
}

.fee-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  color: #666;
  font-size: 14px;
}

.fee-row.total {
  border-top: 1px solid #f0f0f0;
  margin-top: 8px;
  padding-top: 16px;
}

.total-price {
  color: #ff6b35;
  font-size: 20px;
  font-weight: 700;
}

.order-footer {
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

.submit-btn {
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

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.submit-btn:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.4);
}
</style>
