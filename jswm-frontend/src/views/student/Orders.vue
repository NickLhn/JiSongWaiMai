<template>
  <div class="orders-page">
    <header class="page-header">
      <button class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <h1>我的订单</h1>
      <div class="placeholder"></div>
    </header>

    <!-- Order Tabs -->
    <div class="order-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        :class="['tab-btn', { active: activeTab === tab.value }]"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
        <span v-if="tab.count" class="tab-badge">{{ tab.count }}</span>
      </button>
    </div>

    <main class="orders-content">
      <div v-if="loading" class="skeleton-list">
        <div v-for="i in 3" :key="i" class="skeleton-card"></div>
      </div>

      <div v-else-if="filteredOrders.length === 0" class="empty-orders">
        <el-icon :size="80"><Document /></el-icon>
        <h2>暂无订单</h2>
        <p>您还没有相关订单</p>
        <button class="btn-primary" @click="$router.push('/home')">去点餐</button>
      </div>

      <div v-else class="orders-list">
        <div v-for="order in filteredOrders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="merchant-info">
              <el-icon><Shop /></el-icon>
              <span class="merchant-name">{{ order.merchantName }}</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
            <span :class="['order-status', getStatusClass(order.status)]">
              {{ getStatusText(order.status) }}
            </span>
          </div>

          <div class="order-items" @click="viewOrderDetail(order.id)">
            <div class="items-preview">
              <img
                v-for="(item, index) in order.items?.slice(0, 3)"
                :key="index"
                :src="item.dishImage || '/dish-default.jpg'"
                class="item-img"
              />
              <span v-if="order.items?.length > 3" class="more-items">
                +{{ order.items.length - 3 }}
              </span>
            </div>
            <div class="order-total">
              <span class="total-label">合计</span>
              <span class="total-price">¥{{ order.payAmount }}</span>
            </div>
          </div>

          <div class="order-footer">
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
            <div class="order-actions">
              <button
                v-if="order.status === 0"
                class="btn-pay"
                @click="payOrder(order)"
              >
                立即支付
              </button>
              <button
                v-if="order.status === 3"
                class="btn-confirm"
                @click="confirmOrder(order)"
              >
                确认收货
              </button>
              <button
                v-if="order.status === 4 && !order.isReviewed"
                class="btn-review"
                @click="reviewOrder(order)"
              >
                评价
              </button>
              <button
                v-if="[0, 1, 2].includes(order.status)"
                class="btn-cancel"
                @click="cancelOrder(order)"
              >
                取消订单
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList, cancelOrder as cancelOrderApi, confirmOrder as confirmOrderApi } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Document, Shop, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const orders = ref([])
const loading = ref(true)
const activeTab = ref('all')

const tabs = [
  { label: '全部', value: 'all' },
  { label: '待付款', value: 'pending', count: 0 },
  { label: '进行中', value: 'processing', count: 0 },
  { label: '已完成', value: 'completed', count: 0 }
]

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  if (activeTab.value === 'pending') return orders.value.filter(o => o.status === 0)
  if (activeTab.value === 'processing') return orders.value.filter(o => [1, 2, 3].includes(o.status))
  if (activeTab.value === 'completed') return orders.value.filter(o => [4, 5].includes(o.status))
  return orders.value
})

const loadOrders = async () => {
  try {
    loading.value = true
    const res = await getOrderList()
    orders.value = res.data
  } catch (error) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const getStatusText = (status) => {
  const statusMap = {
    0: '待付款',
    1: '已支付',
    2: '制作中',
    3: '配送中',
    4: '已完成',
    5: '已取消'
  }
  return statusMap[status] || '未知'
}

const getStatusClass = (status) => {
  const classMap = {
    0: 'status-pending',
    1: 'status-paid',
    2: 'status-making',
    3: 'status-delivering',
    4: 'status-completed',
    5: 'status-cancelled'
  }
  return classMap[status] || ''
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const viewOrderDetail = (orderId) => {
  router.push(`/orders/${orderId}`)
}

const payOrder = (order) => {
  router.push(`/orders/pay/${order.id}`)
}

const confirmOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'info'
    })
    await confirmOrderApi(order.id)
    ElMessage.success('确认成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const reviewOrder = (order) => {
  router.push(`/orders/review/${order.id}`)
}

const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelOrderApi(order.id)
    ElMessage.success('取消成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
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
  background: #f8f9fa;
  border: none;
  border-radius: 12px;
  cursor: pointer;
}

.placeholder {
  width: 40px;
}

.order-tabs {
  display: flex;
  gap: 8px;
  padding: 16px;
  background: white;
  border-bottom: 1px solid #f1f3f4;
  overflow-x: auto;
}

.tab-btn {
  padding: 10px 20px;
  background: #f8f9fa;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  color: #6c757d;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
  position: relative;
}

.tab-btn.active {
  background: #ff6b35;
  color: white;
}

.tab-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 18px;
  height: 18px;
  background: #dc3545;
  color: white;
  font-size: 11px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.orders-content {
  padding: 16px;
  max-width: 600px;
  margin: 0 auto;
}

.empty-orders {
  text-align: center;
  padding: 60px 20px;
  color: #dee2e6;
}

.empty-orders h2 {
  font-size: 20px;
  color: #495057;
  margin: 16px 0 8px;
}

.empty-orders p {
  color: #adb5bd;
  margin-bottom: 24px;
}

.btn-primary {
  padding: 12px 32px;
  background: #ff6b35;
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f1f3f4;
}

.merchant-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.merchant-name {
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-paid {
  background: #d1ecf1;
  color: #0c5460;
}

.status-making {
  background: #d4edda;
  color: #155724;
}

.status-delivering {
  background: #cce5ff;
  color: #004085;
}

.status-completed {
  background: #d4edda;
  color: #155724;
}

.status-cancelled {
  background: #f8d7da;
  color: #721c24;
}

.order-items {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  cursor: pointer;
}

.items-preview {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}

.more-items {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 14px;
  color: #6c757d;
}

.order-total {
  text-align: right;
}

.total-label {
  font-size: 13px;
  color: #6c757d;
  margin-right: 8px;
}

.total-price {
  font-size: 18px;
  font-weight: 700;
  color: #ff6b35;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8f9fa;
}

.order-time {
  font-size: 13px;
  color: #adb5bd;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.order-actions button {
  padding: 8px 16px;
  border: none;
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-pay {
  background: #ff6b35;
  color: white;
}

.btn-confirm {
  background: #28a745;
  color: white;
}

.btn-review {
  background: #ffc107;
  color: #212529;
}

.btn-cancel {
  background: #f8f9fa;
  color: #6c757d;
  border: 1px solid #e9ecef;
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skeleton-card {
  height: 180px;
  background: linear-gradient(90deg, #e9ecef 25%, #dee2e6 50%, #e9ecef 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 16px;
}

@keyframes skeleton-loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
