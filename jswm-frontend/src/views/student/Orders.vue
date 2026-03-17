<template>
  <div class="orders-page">
    <!-- Header -->
    <header class="page-header">
      <div class="header-content">
        <button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <h1>我的订单</h1>
        <div class="placeholder"></div>
      </div>
    </header>

    <!-- Order Stats -->
    <div class="stats-bar">
      <div class="stat-item">
        <div class="stat-value">{{ orders.length }}</div>
        <div class="stat-label">全部</div>
      </div>
      <div class="stat-item">
        <div class="stat-value text-warning">{{ pendingCount }}</div>
        <div class="stat-label">待付款</div>
      </div>
      <div class="stat-item">
        <div class="stat-value text-primary">{{ processingCount }}</div>
        <div class="stat-label">进行中</div>
      </div>
      <div class="stat-item">
        <div class="stat-value text-success">{{ completedCount }}</div>
        <div class="stat-label">已完成</div>
      </div>
    </div>

    <!-- Order Tabs -->
    <div class="order-tabs">
      <div 
        v-for="tab in tabs" 
        :key="tab.value"
        :class="['tab-item', { active: activeTab === tab.value }]"
        @click="activeTab = tab.value"
      >
        <span class="tab-text">{{ tab.label }}</span>
        <span v-if="tab.count > 0" class="tab-badge">{{ tab.count }}</span>
      </div>
    </div>

    <main class="orders-content">
      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div v-for="i in 3" :key="i" class="skeleton-card">
          <div class="skeleton-header">
            <div class="skeleton-avatar"></div>
            <div class="skeleton-line short"></div>
          </div>
          <div class="skeleton-body">
            <div class="skeleton-line"></div>
            <div class="skeleton-line medium"></div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredOrders.length === 0" class="empty-state">
        <div class="empty-icon">
          <el-icon :size="64"><DocumentDelete /></el-icon>
        </div>
        <h3>{{ emptyTitle }}</h3>
        <p>{{ emptyDesc }}</p>
        <button class="btn-primary" @click="$router.push('/home')">
          <el-icon><Food /></el-icon>
          去点餐
        </button>
      </div>

      <!-- Order List -->
      <div v-else class="orders-list">
        <transition-group name="order-list">
          <div 
            v-for="order in filteredOrders" 
            :key="order.id" 
            class="order-card"
            @click="viewOrderDetail(order.id)"
          >
            <!-- Order Header -->
            <div class="order-header">
              <div class="merchant-info">
                <div class="merchant-avatar">
                  <img :src="order.merchantLogo || '/shop-default.jpg'" alt="">
                </div>
                <div class="merchant-detail">
                  <span class="merchant-name">{{ order.merchantName }}</span>
                  <span class="order-time">{{ formatTime(order.createTime) }}</span>
                </div>
                <el-icon class="arrow-icon"><ArrowRight /></el-icon>
              </div>
              <div :class="['status-tag', getStatusClass(order.status)]">
                <el-icon v-if="order.status === 0"><Warning /></el-icon>
                <el-icon v-else-if="order.status === 4"><CircleCheck /></el-icon>
                <el-icon v-else-if="order.status === 5"><CircleClose /></el-icon>
                <span>{{ getStatusText(order.status) }}</span>
              </div>
            </div>

            <!-- Order Items -->
            <div class="order-items">
              <!-- 商品列表 -->
              <div class="items-list">
                <div 
                  v-for="(item, index) in order.items" 
                  :key="index"
                  class="item-row"
                >
                  <div class="item-image-small">
                    <img :src="item.dishImage || '/dish-default.jpg'" alt="">
                  </div>
                  <div class="item-info">
                    <span class="item-name">{{ item.dishName }}</span>
                    <span class="item-price">¥{{ item.price }}</span>
                  </div>
                  <span class="item-quantity">x{{ item.quantity }}</span>
                </div>
              </div>
              
              <!-- 订单汇总 -->
              <div class="order-summary">
                <span class="item-count">共{{ getTotalQuantity(order) }}件</span>
                <div class="price-info">
                  <span class="price-label">实付</span>
                  <span class="price-value">¥{{ order.payAmount }}</span>
                </div>
              </div>
            </div>

            <!-- Order Actions -->
            <div class="order-actions">
              <div class="action-left">
                <span v-if="order.status === 0" class="countdown">
                  <el-icon><Timer /></el-icon>
                  {{ getCountdown(order) }}
                </span>
              </div>
              <div class="action-right">
                <button 
                  v-if="order.status === 0" 
                  class="btn-pay"
                  @click.stop="payOrder(order)"
                >
                  立即支付
                </button>
                <button 
                  v-if="order.status === 3" 
                  class="btn-confirm"
                  @click.stop="confirmOrder(order)"
                >
                  确认收货
                </button>
                <button 
                  v-if="order.status === 4 && !order.isReviewed" 
                  class="btn-review"
                  @click.stop="reviewOrder(order)"
                >
                  评价
                </button>
                <button 
                  v-if="[0, 1, 2].includes(order.status)" 
                  class="btn-cancel"
                  @click.stop="cancelOrder(order)"
                >
                  取消
                </button>
                <button 
                  class="btn-detail"
                  @click.stop="viewOrderDetail(order.id)"
                >
                  查看详情
                </button>
              </div>
            </div>
          </div>
        </transition-group>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList, cancelOrder as cancelOrderApi, confirmOrder as confirmOrderApi } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, DocumentDelete, Food, ArrowRight, Warning, CircleCheck, CircleClose, Timer } from '@element-plus/icons-vue'

const router = useRouter()
const orders = ref([])
const loading = ref(true)
const activeTab = ref('all')

const tabs = [
  { label: '全部', value: 'all', count: 0 },
  { label: '待付款', value: 'pending', count: 0 },
  { label: '进行中', value: 'processing', count: 0 },
  { label: '已完成', value: 'completed', count: 0 }
]

const statusMap = {
  all: null,
  pending: [0],
  processing: [1, 2, 3],
  completed: [4, 5]
}

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  return orders.value.filter(order => statusMap[activeTab.value]?.includes(order.status))
})

const pendingCount = computed(() => orders.value.filter(o => o.status === 0).length)
const processingCount = computed(() => orders.value.filter(o => [1, 2, 3].includes(o.status)).length)
const completedCount = computed(() => orders.value.filter(o => [4, 5].includes(o.status)).length)

const emptyTitle = computed(() => {
  const titles = { all: '暂无订单', pending: '暂无待付款订单', processing: '暂无进行中订单', completed: '暂无已完成订单' }
  return titles[activeTab.value]
})

const emptyDesc = computed(() => {
  const descs = { all: '快去下单享受美食吧！', pending: '您没有待付款的订单', processing: '您没有进行中的订单', completed: '您还没有完成的订单' }
  return descs[activeTab.value]
})

const loadOrders = async () => {
  try {
    loading.value = true
    const res = await getOrderList()
    orders.value = res.data || []
  } catch (error) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const getStatusText = (status) => {
  const texts = { 0: '待付款', 1: '待接单', 2: '制作中', 3: '配送中', 4: '已完成', 5: '已取消' }
  return texts[status] || '未知'
}

const getStatusClass = (status) => {
  const classes = { 0: 'warning', 1: 'primary', 2: 'primary', 3: 'primary', 4: 'success', 5: 'danger' }
  return classes[status] || 'default'
}

const getTotalQuantity = (order) => {
  return order.items?.reduce((sum, item) => sum + item.quantity, 0) || 0
}

const getCountdown = (order) => {
  return '15:00'
}

const viewOrderDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

const payOrder = (order) => {
  router.push(`/payment?orderId=${order.id}`)
}

const confirmOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确认已收到订单？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
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
  router.push(`/review/create?orderId=${order.id}`)
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
  background: linear-gradient(180deg, #fff8f5 0%, #ffffff 100%);
}

/* Header */
.page-header {
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
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

/* Stats Bar */
.stats-bar {
  display: flex;
  justify-content: space-around;
  padding: 20px;
  background: #fff;
  margin: 16px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(255, 107, 53, 0.08);
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.stat-value.text-warning { color: #ff9500; }
.stat-value.text-primary { color: #ff6b35; }
.stat-value.text-success { color: #34c759; }

.stat-label {
  font-size: 13px;
  color: #999;
}

/* Order Tabs */
.order-tabs {
  display: flex;
  padding: 0 16px 16px;
  gap: 12px;
  overflow-x: auto;
}

.tab-item {
  flex: 1;
  min-width: 80px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.tab-item.active {
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
  color: #fff;
}

.tab-text {
  font-size: 14px;
  font-weight: 500;
}

.tab-badge {
  background: #ff3b30;
  color: #fff;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
}

.tab-item.active .tab-badge {
  background: rgba(255, 255, 255, 0.3);
}

/* Orders Content */
.orders-content {
  padding: 0 16px 100px;
}

/* Loading State */
.loading-state {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skeleton-card {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.skeleton-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.skeleton-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-line {
  height: 16px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

.skeleton-line.short {
  width: 120px;
}

.skeleton-line.medium {
  width: 80%;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #fff5f0 0%, #ffe8e0 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  color: #ff6b35;
}

.empty-state h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 8px;
}

.empty-state p {
  font-size: 14px;
  color: #999;
  margin-bottom: 24px;
}

.btn-primary {
  padding: 14px 32px;
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
  color: #fff;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 107, 53, 0.4);
}

/* Orders List */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  cursor: pointer;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* Order Header */
.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f5f5f5;
}

.merchant-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.merchant-avatar {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f5f5;
}

.merchant-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.merchant-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.merchant-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.order-time {
  font-size: 12px;
  color: #999;
}

.arrow-icon {
  color: #ccc;
  margin-left: 4px;
}

.status-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
}

.status-tag.warning {
  background: #fff5e6;
  color: #ff9500;
}

.status-tag.primary {
  background: #fff0eb;
  color: #ff6b35;
}

.status-tag.success {
  background: #e8f5e9;
  color: #34c759;
}

.status-tag.danger {
  background: #ffebee;
  color: #ff3b30;
}

/* Order Items */
.order-items {
  margin-bottom: 16px;
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.item-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.item-row:last-child {
  border-bottom: none;
}

.item-image-small {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f5f5;
}

.item-image-small img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
}

.item-price {
  font-size: 13px;
  color: #ff6b6b;
  font-weight: 500;
}

.item-quantity {
  font-size: 13px;
  color: #999;
  font-weight: 500;
}

.order-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.item-count {
  font-size: 13px;
  color: #999;
}

.price-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-label {
  font-size: 13px;
  color: #666;
}

.price-value {
  font-size: 18px;
  font-weight: 700;
  color: #ff6b35;
}

/* Order Actions */
.order-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid #f5f5f5;
}

.countdown {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #ff9500;
}

.action-right {
  display: flex;
  gap: 8px;
}

.action-right button {
  padding: 8px 16px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.btn-pay {
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
  color: #fff;
}

.btn-confirm {
  background: linear-gradient(135deg, #34c759 0%, #30d158 100%);
  color: #fff;
}

.btn-review {
  background: linear-gradient(135deg, #5856d6 0%, #7c7ae6 100%);
  color: #fff;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-detail {
  background: transparent;
  color: #999;
  border: 1px solid #e0e0e0 !important;
}

.action-right button:hover {
  transform: scale(1.05);
}

/* Transitions */
.order-list-enter-active,
.order-list-leave-active {
  transition: all 0.3s ease;
}

.order-list-enter-from,
.order-list-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* Responsive */
@media (max-width: 480px) {
  .stats-bar {
    margin: 12px;
    padding: 16px;
  }
  
  .stat-value {
    font-size: 20px;
  }
  
  .orders-content {
    padding: 0 12px 100px;
  }
  
  .order-card {
    padding: 12px;
  }
  
  .item-image {
    width: 56px;
    height: 56px;
  }
  
  .more-items {
    width: 56px;
    height: 56px;
  }
}
</style>