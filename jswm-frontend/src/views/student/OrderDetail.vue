<template>
  <div class="order-detail-page">
    <header class="page-header">
      <button class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <h1>订单详情</h1>
      <div class="placeholder"></div>
    </header>

    <main class="order-content" v-if="order.id">
      <!-- 订单状态 -->
      <div class="status-section">
        <div class="status-icon">
          <el-icon :size="48">
            <component :is="getStatusIcon(order.status)" />
          </el-icon>
        </div>
        <h2 class="status-text">{{ getStatusText(order.status) }}</h2>
        <p class="status-desc">{{ getStatusDesc(order.status) }}</p>
      </div>

      <!-- 配送信息 -->
      <div class="info-section">
        <h3 class="section-title">
          <el-icon><Location /></el-icon>
          配送信息
        </h3>
        <div class="info-content">
          <p class="info-name">{{ order.receiverName }} {{ order.receiverPhone }}</p>
          <p class="info-address">{{ order.deliveryAddress }}</p>
        </div>
      </div>

      <!-- 商家信息 -->
      <div class="info-section">
        <h3 class="section-title">
          <el-icon><Shop /></el-icon>
          {{ order.merchantName }}
        </h3>
        <div class="goods-list">
          <div v-for="item in order.items" :key="item.id" class="goods-item">
            <img :src="item.dishImage || '/dish-default.jpg'" class="goods-image">
            <div class="goods-info">
              <h4>{{ item.dishName }}</h4>
              <p class="goods-price">¥{{ item.price }} x {{ item.quantity }}</p>
            </div>
            <span class="goods-subtotal">¥{{ item.subtotal }}</span>
          </div>
        </div>
        <div class="info-footer">
          <span>备注</span>
          <span class="remark">{{ order.remark || '无' }}</span>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="info-section">
        <h3 class="section-title">订单信息</h3>
        <div class="info-rows">
          <div class="info-row">
            <span>订单编号</span>
            <span>{{ order.orderNo }}</span>
          </div>
          <div class="info-row">
            <span>下单时间</span>
            <span>{{ formatTime(order.createTime) }}</span>
          </div>
          <div class="info-row">
            <span>商品总价</span>
            <span>¥{{ order.totalAmount }}</span>
          </div>
          <div class="info-row">
            <span>配送费</span>
            <span>¥{{ order.deliveryFee || 0 }}</span>
          </div>
          <div class="info-row total">
            <span>实付金额</span>
            <span class="total-price">¥{{ order.payAmount }}</span>
          </div>
        </div>
      </div>
    </main>

    <!-- 底部操作栏 -->
    <footer class="order-footer" v-if="order.id">
      <div class="footer-actions">
        <button 
          v-if="order.status === 0" 
          class="btn-primary"
          @click="payOrder"
        >
          立即支付
        </button>
        <button 
          v-if="order.status === 3" 
          class="btn-primary"
          @click="confirmOrder"
        >
          确认收货
        </button>
        <button 
          v-if="[0, 1, 2].includes(order.status)" 
          class="btn-default"
          @click="cancelOrder"
        >
          取消订单
        </button>
        <button class="btn-default" @click="contactMerchant">
          联系商家
        </button>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, 
  Location, 
  Shop, 
  Clock, 
  Warning, 
  CircleCheck, 
  CircleClose,
  Box,
  Truck
} from '@element-plus/icons-vue'
import { getOrderDetail, cancelOrder as cancelOrderApi, confirmOrder as confirmOrderApi } from '@/api/order'

const route = useRoute()
const router = useRouter()
const orderId = route.params.id

const order = ref({})

const loadOrderDetail = async () => {
  if (!orderId) {
    ElMessage.error('订单ID不能为空')
    router.push('/orders')
    return
  }
  try {
    const res = await getOrderDetail(orderId)
    order.value = res.data
  } catch (error) {
    ElMessage.error('加载订单失败')
    router.push('/orders')
  }
}

const getStatusText = (status) => {
  const texts = { 0: '待付款', 1: '待接单', 2: '制作中', 3: '配送中', 4: '已完成', 5: '已取消' }
  return texts[status] || '未知'
}

const getStatusDesc = (status) => {
  const descs = { 
    0: '请在15分钟内完成支付', 
    1: '商家正在确认订单', 
    2: '商家正在制作美食', 
    3: '骑手正在配送中', 
    4: '订单已完成，期待您的评价', 
    5: '订单已取消' 
  }
  return descs[status] || ''
}

const getStatusIcon = (status) => {
  const icons = { 
    0: 'Warning', 
    1: 'Clock', 
    2: 'Box', 
    3: 'Truck', 
    4: 'CircleCheck', 
    5: 'CircleClose' 
  }
  return icons[status] || 'Clock'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2,'0')}-${date.getDate().toString().padStart(2,'0')} ${date.getHours().toString().padStart(2,'0')}:${date.getMinutes().toString().padStart(2,'0')}`
}

const payOrder = () => {
  router.push(`/payment?orderId=${order.value.id}`)
}

const confirmOrder = async () => {
  try {
    await ElMessageBox.confirm('确认已收到订单？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await confirmOrderApi(order.value.id)
    ElMessage.success('确认成功')
    loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const cancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelOrderApi(order.value.id)
    ElMessage.success('取消成功')
    loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const contactMerchant = () => {
  ElMessage.info('客服电话：400-123-4567')
}

onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
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

.order-content {
  padding: 16px;
}

.status-section {
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
  border-radius: 12px;
  padding: 32px;
  text-align: center;
  color: white;
  margin-bottom: 16px;
}

.status-icon {
  margin-bottom: 16px;
}

.status-text {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
}

.status-desc {
  font-size: 14px;
  opacity: 0.9;
}

.info-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.section-title .el-icon {
  color: #ff6b35;
}

.info-content {
  padding-left: 28px;
}

.info-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.info-address {
  color: #666;
  font-size: 14px;
}

.goods-list {
  margin-bottom: 16px;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.goods-item:last-child {
  border-bottom: none;
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
  color: #999;
  font-size: 12px;
}

.goods-subtotal {
  color: #333;
  font-weight: 600;
}

.info-footer {
  display: flex;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  color: #666;
  font-size: 14px;
}

.info-footer .remark {
  color: #999;
}

.info-rows {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 14px;
}

.info-row.total {
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  font-weight: 600;
  color: #333;
}

.total-price {
  color: #ff6b35;
  font-size: 18px;
}

.order-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: white;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
  z-index: 1000;
  max-width: 600px;
  margin: 0 auto;
  box-sizing: border-box;
}

.footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-primary {
  padding: 10px 24px;
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.btn-default {
  padding: 10px 24px;
  background: white;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
}
</style>
