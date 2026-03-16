<template>
  <div class="admin-dashboard">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card blue">
        <div class="stat-icon"><el-icon><User /></el-icon></div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalUsers }}</span>
          <span class="stat-label">总用户数</span>
        </div>
      </div>
      <div class="stat-card orange">
        <div class="stat-icon"><el-icon><Shop /></el-icon></div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalMerchants }}</span>
          <span class="stat-label">入驻商家</span>
        </div>
      </div>
      <div class="stat-card green">
        <div class="stat-icon"><el-icon><ShoppingCart /></el-icon></div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalOrders }}</span>
          <span class="stat-label">总订单数</span>
        </div>
      </div>
      <div class="stat-card purple">
        <div class="stat-icon"><el-icon><Money /></el-icon></div>
        <div class="stat-info">
          <span class="stat-value">¥{{ stats.totalRevenue }}</span>
          <span class="stat-label">总交易额</span>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-row">
      <div class="chart-card">
        <div class="card-header">
          <h3>订单趋势</h3>
          <div class="time-filter">
            <button :class="{ active: timeRange === 'week' }" @click="timeRange = 'week'">本周</button>
            <button :class="{ active: timeRange === 'month' }" @click="timeRange = 'month'">本月</button>
          </div>
        </div>
        <div class="chart-content">
          <div class="mock-line-chart">
            <div v-for="(point, index) in orderTrend" :key="index" class="point" :style="{ left: index * 14 + '%', bottom: point + '%' }">
              <div class="tooltip">{{ point }}单</div>
            </div>
            <svg class="line-svg" viewBox="0 0 100 100" preserveAspectRatio="none">
              <polyline :points="getLinePoints(orderTrend)" fill="none" stroke="#ff6b35" stroke-width="2"/>
            </svg>
          </div>
        </div>
      </div>

      <div class="chart-card">
        <div class="card-header">
          <h3>商家分类占比</h3>
        </div>
        <div class="chart-content">
          <div class="category-list">
            <div v-for="cat in categoryStats" :key="cat.name" class="category-item">
              <div class="cat-info">
                <span class="cat-name">{{ cat.name }}</span>
                <span class="cat-count">{{ cat.count }}家</span>
              </div>
              <div class="progress-bar">
                <div class="progress" :style="{ width: cat.percentage + '%' }"></div>
              </div>
              <span class="percentage">{{ cat.percentage }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最近活动 -->
    <div class="activity-card">
      <div class="card-header">
        <h3>最近活动</h3>
      </div>
      <div class="activity-list">
        <div v-for="(activity, index) in recentActivities" :key="index" class="activity-item">
          <div class="activity-icon" :class="activity.type">
            <el-icon v-if="activity.type === 'user'"><User /></el-icon>
            <el-icon v-else-if="activity.type === 'merchant'"><Shop /></el-icon>
            <el-icon v-else-if="activity.type === 'order'"><ShoppingCart /></el-icon>
            <el-icon v-else><Bell /></el-icon>
          </div>
          <div class="activity-content">
            <p class="activity-text">{{ activity.text }}</p>
            <span class="activity-time">{{ activity.time }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { User, Shop, ShoppingCart, Money, Bell } from '@element-plus/icons-vue'
import { getAdminDashboard } from '@/api/adminDashboard'
import { ElMessage } from 'element-plus'

const loading = ref(false)

const stats = ref({
  totalUsers: 0,
  totalMerchants: 0,
  totalOrders: 0,
  totalRevenue: 0
})

const timeRange = ref('week')
const orderTrend = ref([0, 0, 0, 0, 0, 0, 0])
const orderTrendLabels = ref(['', '', '', '', '', '', ''])

const categoryStats = ref([])

const recentActivities = ref([])

const getLinePoints = (data) => {
  return data.map((val, idx) => `${idx * 14 + 7},${100 - val}`).join(' ')
}

// 加载看板数据
const loadDashboardData = async () => {
  try {
    loading.value = true
    const res = await getAdminDashboard()
    const data = res.data

    // 统计数据
    stats.value.totalUsers = data.totalUsers || 0
    stats.value.totalMerchants = data.totalMerchants || 0
    stats.value.totalOrders = data.totalOrders || 0
    stats.value.totalRevenue = data.totalRevenue || 0

    // 订单趋势
    if (data.orderTrend && data.orderTrend.length > 0) {
      orderTrend.value = data.orderTrend.map(item => item.count)
      orderTrendLabels.value = data.orderTrend.map(item => item.date)
    }

    // 商家分类统计
    categoryStats.value = data.categoryStats || []

    // 最近活动
    recentActivities.value = data.recentActivities || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 0;
}

/* 统计卡片 */
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
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.stat-card.blue .stat-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-card.orange .stat-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-card.green .stat-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-card.purple .stat-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f1f1f;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
}

/* 图表区域 */
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
  margin-bottom: 20px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1f1f1f;
  margin: 0;
}

.time-filter {
  display: flex;
  gap: 8px;
}

.time-filter button {
  padding: 6px 16px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.time-filter button.active {
  background: #ff6b35;
  color: white;
  border-color: #ff6b35;
}

.chart-content {
  height: 200px;
}

/* 折线图 */
.mock-line-chart {
  position: relative;
  height: 100%;
  border-bottom: 1px solid #f0f0f0;
  border-left: 1px solid #f0f0f0;
}

.point {
  position: absolute;
  width: 12px;
  height: 12px;
  background: #ff6b35;
  border-radius: 50%;
  transform: translate(-50%, 50%);
  cursor: pointer;
  transition: all 0.2s;
}

.point:hover {
  transform: translate(-50%, 50%) scale(1.5);
}

.point:hover .tooltip {
  opacity: 1;
  transform: translate(-50%, -30px);
}

.tooltip {
  position: absolute;
  bottom: 100%;
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

.line-svg {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

/* 分类列表 */
.category-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.category-item {
  display: grid;
  grid-template-columns: 1fr 2fr auto;
  align-items: center;
  gap: 16px;
}

.cat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.cat-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f1f1f;
}

.cat-count {
  font-size: 12px;
  color: #8c8c8c;
}

.progress-bar {
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background: linear-gradient(90deg, #ff6b35 0%, #ff8c5a 100%);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.percentage {
  font-size: 14px;
  font-weight: 600;
  color: #ff6b35;
  min-width: 40px;
  text-align: right;
}

/* 活动列表 */
.activity-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  transition: background 0.2s;
}

.activity-item:hover {
  background: #f5f5f5;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
}

.activity-icon.user { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.activity-icon.merchant { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.activity-icon.order { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.activity-icon.system { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }

.activity-content {
  flex: 1;
}

.activity-text {
  font-size: 14px;
  color: #1f1f1f;
  margin: 0 0 4px;
}

.activity-time {
  font-size: 12px;
  color: #8c8c8c;
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
