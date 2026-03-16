<template>
  <div class="merchant-dashboard">
    <!-- Stats Cards -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon blue">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-label">今日销售额</span>
          <span class="stat-value">¥{{ todaySales }}</span>
          <span class="stat-change up">+12.5%</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">
          <el-icon><ShoppingBag /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-label">今日订单</span>
          <span class="stat-value">{{ todayOrders }}</span>
          <span class="stat-change up">+8.2%</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-label">新增客户</span>
          <span class="stat-value">{{ newCustomers }}</span>
          <span class="stat-change down">-2.1%</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple">
          <el-icon><Star /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-label">店铺评分</span>
          <span class="stat-value">{{ rating }}</span>
          <span class="stat-change up">+0.1</span>
        </div>
      </div>
    </div>

    <!-- Charts Row -->
    <div class="charts-row">
      <div class="chart-card">
        <div class="card-header">
          <h3>销售趋势</h3>
          <div class="time-range">
            <button :class="{ active: salesRange === 'week' }" @click="salesRange = 'week'">本周</button>
            <button :class="{ active: salesRange === 'month' }" @click="salesRange = 'month'">本月</button>
          </div>
        </div>
        <div class="chart-placeholder">
          <div class="mock-chart">
            <div v-for="(item, index) in salesData" :key="index" class="bar" :style="{ height: item + '%' }">
              <span class="tooltip">¥{{ item * 10 }}</span>
            </div>
          </div>
          <div class="x-labels">
            <span v-for="(label, index) in salesLabels" :key="index">{{ label }}</span>
          </div>
        </div>
      </div>

      <div class="chart-card">
        <div class="card-header">
          <h3>订单状态分布</h3>
        </div>
        <div class="order-status-list">
          <div class="status-item">
            <div class="status-info">
              <span class="dot pending"></span>
              <span>待处理</span>
            </div>
            <span class="count">{{ pendingOrders }}</span>
          </div>
          <div class="status-item">
            <div class="status-info">
              <span class="dot processing"></span>
              <span>制作中</span>
            </div>
            <span class="count">{{ processingOrders }}</span>
          </div>
          <div class="status-item">
            <div class="status-info">
              <span class="dot delivering"></span>
              <span>配送中</span>
            </div>
            <span class="count">{{ deliveringOrders }}</span>
          </div>
          <div class="status-item">
            <div class="status-info">
              <span class="dot completed"></span>
              <span>已完成</span>
            </div>
            <span class="count">{{ completedOrders }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Orders -->
    <div class="orders-card">
      <div class="card-header">
        <h3>最新订单</h3>
        <router-link to="/merchant/orders" class="view-all">查看全部</router-link>
      </div>
      <div class="orders-table">
        <div class="table-header">
          <span>订单号</span>
          <span>客户</span>
          <span>商品</span>
          <span>金额</span>
          <span>状态</span>
          <span>操作</span>
        </div>
        <div class="table-body">
          <div v-for="order in recentOrders" :key="order.id" class="table-row">
            <span class="order-no">{{ order.orderNo }}</span>
            <span>{{ order.customerName }}</span>
            <span>{{ order.items }}</span>
            <span class="amount">¥{{ order.amount }}</span>
            <span :class="['status', getStatusClass(order.status)]">{{ getStatusText(order.status) }}</span>
            <div class="actions">
              <button v-if="order.status === 1" class="action-btn primary" @click="handleAcceptOrder(order.id)">接单</button>
              <button v-if="order.status === 2" class="action-btn primary" @click="handleDeliverOrder(order.id)">配送</button>
              <button class="action-btn" @click="viewDetail(order.id)">详情</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Money, ShoppingBag, User, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getMerchantDashboard } from '@/api/dashboard'
import { acceptOrder, deliverOrder } from '@/api/order'

const router = useRouter()
const loading = ref(false)

// Stats
const todaySales = ref(0)
const todayOrders = ref(0)
const newCustomers = ref(0)
const rating = ref(4.8)

// Sales Chart
const salesRange = ref('week')
const salesData = ref([0, 0, 0, 0, 0, 0, 0])
const salesLabels = ref(['周一', '周二', '周三', '周四', '周五', '周六', '周日'])

// Order Status
const pendingOrders = ref(0)
const processingOrders = ref(0)
const deliveringOrders = ref(0)
const completedOrders = ref(0)

// Recent Orders
const recentOrders = ref([])

// 加载看板数据
const loadDashboardData = async () => {
  try {
    loading.value = true
    const res = await getMerchantDashboard()
    const data = res.data
    
    // 统计数据
    todaySales.value = data.todaySales || 0
    todayOrders.value = data.todayOrders || 0
    newCustomers.value = data.newCustomers || 0
    rating.value = data.rating || 4.8
    
    // 订单状态
    pendingOrders.value = data.pendingOrders || 0
    processingOrders.value = data.processingOrders || 0
    deliveringOrders.value = data.deliveringOrders || 0
    completedOrders.value = data.completedOrders || 0
    
    // 销售趋势
    if (data.salesTrend && data.salesTrend.length > 0) {
      salesData.value = data.salesTrend.map(item => {
        // 将金额转换为百分比高度（假设最大值为500）
        const amount = parseFloat(item.amount) || 0
        return Math.min((amount / 500) * 100, 100)
      })
      salesLabels.value = data.salesTrend.map(item => item.date)
    }
    
    // 最新订单
    recentOrders.value = data.recentOrders || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const getStatusClass = (status) => {
  const map = { 1: 'pending', 2: 'processing', 3: 'delivering', 4: 'completed' }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = { 1: '待接单', 2: '制作中', 3: '配送中', 4: '已完成' }
  return map[status] || '未知'
}

const handleAcceptOrder = async (id) => {
  try {
    await acceptOrder(id)
    ElMessage.success('已接单')
    loadDashboardData() // 刷新数据
  } catch (error) {
    ElMessage.error('接单失败')
  }
}

const handleDeliverOrder = async (id) => {
  try {
    await deliverOrder(id)
    ElMessage.success('开始配送')
    loadDashboardData() // 刷新数据
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadDashboardData()
})

const viewDetail = (id) => {
  router.push(`/merchant/orders?id=${id}`)
}
</script>

<style scoped>
.merchant-dashboard {
  padding: 0;
}

/* Stats Row */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-icon.blue {
  background: #e6f7ff;
  color: #1890ff;
}

.stat-icon.orange {
  background: #fff7e6;
  color: #fa8c16;
}

.stat-icon.green {
  background: #f6ffed;
  color: #52c41a;
}

.stat-icon.purple {
  background: #f9f0ff;
  color: #722ed1;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f1f1f;
}

.stat-change {
  font-size: 12px;
  font-weight: 500;
}

.stat-change.up {
  color: #52c41a;
}

.stat-change.down {
  color: #ff4d4f;
}

/* Charts Row */
.charts-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1f1f1f;
  margin: 0;
}

.time-range {
  display: flex;
  gap: 8px;
}

.time-range button {
  padding: 6px 16px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.time-range button.active {
  background: #ff6b35;
  color: white;
  border-color: #ff6b35;
}

/* Mock Chart */
.chart-placeholder {
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.mock-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 160px;
  gap: 16px;
}

.bar {
  flex: 1;
  background: linear-gradient(180deg, #ff6b35 0%, #ff8c5a 100%);
  border-radius: 8px 8px 0 0;
  min-height: 20px;
  position: relative;
  transition: all 0.3s;
  cursor: pointer;
}

.bar:hover {
  opacity: 0.8;
}

.bar:hover .tooltip {
  opacity: 1;
  transform: translate(-50%, -10px);
}

.tooltip {
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  background: #1f1f1f;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  opacity: 0;
  transition: all 0.2s;
  white-space: nowrap;
}

.x-labels {
  display: flex;
  justify-content: space-around;
  margin-top: 12px;
  font-size: 12px;
  color: #8c8c8c;
}

/* Order Status */
.order-status-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.status-item:last-child {
  border-bottom: none;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #595959;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.dot.pending {
  background: #faad14;
}

.dot.processing {
  background: #1890ff;
}

.dot.delivering {
  background: #722ed1;
}

.dot.completed {
  background: #52c41a;
}

.count {
  font-size: 18px;
  font-weight: 600;
  color: #1f1f1f;
}

/* Orders Card */
.orders-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.view-all {
  color: #ff6b35;
  font-size: 14px;
  text-decoration: none;
}

.view-all:hover {
  text-decoration: underline;
}

.orders-table {
  margin-top: 16px;
}

.table-header {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1.5fr 1fr 1fr 1.5fr;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  font-weight: 600;
  color: #8c8c8c;
}

.table-row {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1.5fr 1fr 1fr 1.5fr;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  color: #595959;
  align-items: center;
}

.table-row:last-child {
  border-bottom: none;
}

.order-no {
  font-family: monospace;
  color: #1f1f1f;
}

.amount {
  font-weight: 600;
  color: #ff6b35;
}

.status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status.pending {
  background: #fff7e6;
  color: #fa8c16;
}

.status.processing {
  background: #e6f7ff;
  color: #1890ff;
}

.status.delivering {
  background: #f9f0ff;
  color: #722ed1;
}

.status.completed {
  background: #f6ffed;
  color: #52c41a;
}

.actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  border-color: #ff6b35;
  color: #ff6b35;
}

.action-btn.primary {
  background: #ff6b35;
  color: white;
  border-color: #ff6b35;
}

.action-btn.primary:hover {
  background: #e55a2b;
}

@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-row {
    grid-template-columns: 1fr;
  }
}
</style>
