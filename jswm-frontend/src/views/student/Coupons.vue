<template>
  <div class="coupons-page">
    <header class="page-header">
      <div class="header-content">
        <button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <h1>我的优惠券</h1>
        <div class="placeholder"></div>
      </div>
    </header>

    <!-- 优惠券统计 -->
    <div class="coupon-stats">
      <div class="stat-card available">
        <div class="stat-icon">
          <el-icon><Ticket /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ availableCount }}</span>
          <span class="stat-label">可用优惠券</span>
        </div>
      </div>
    </div>

    <!-- 标签页 -->
    <div class="coupon-tabs">
      <div 
        v-for="tab in tabs" 
        :key="tab.value"
        :class="['tab-item', { active: activeTab === tab.value }]"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </div>
    </div>

    <main class="coupons-content">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-list">
        <div v-for="i in 3" :key="i" class="skeleton-coupon"></div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="filteredCoupons.length === 0" class="empty-state">
        <div class="empty-icon" :style="{ background: emptyIconBg }">
          <el-icon :size="48"><component :is="emptyIcon" /></el-icon>
        </div>
        <h3>{{ emptyTitle }}</h3>
        <p>{{ emptyDesc }}</p>
      </div>

      <!-- 优惠券列表 -->
      <div v-else class="coupons-list">
        <div 
          v-for="coupon in filteredCoupons" 
          :key="coupon.id" 
          :class="['coupon-card', coupon.status === 0 ? 'available' : 'unavailable']"
        >
          <div class="coupon-left">
            <div class="coupon-amount">
              <template v-if="coupon.type === 1">
                <span class="currency">¥</span>
                <span class="value">{{ coupon.discountAmount }}</span>
              </template>
              <template v-else-if="coupon.type === 2">
                <span class="value">{{ (coupon.discountRate * 10).toFixed(1) }}</span>
                <span class="unit">折</span>
              </template>
              <template v-else>
                <span class="value">免</span>
                <span class="unit">运费</span>
              </template>
            </div>
            <div class="coupon-condition">
              {{ coupon.minAmount > 0 ? `满${coupon.minAmount}元可用` : '无门槛' }}
            </div>
          </div>
          <div class="coupon-divider"></div>
          <div class="coupon-right">
            <div class="coupon-info">
              <h3 class="coupon-name">{{ coupon.name }}</h3>
              <p class="coupon-time">{{ formatTime(coupon.endTime) }} 到期</p>
              <span v-if="coupon.merchantId" class="coupon-scope">指定商家可用</span>
              <span v-else class="coupon-scope universal">全平台通用</span>
            </div>
            <button 
              v-if="coupon.status === 0" 
              class="btn-use"
              @click="useCoupon(coupon)"
            >
              去使用
            </button>
            <span v-else class="status-text">
              {{ coupon.status === 1 ? '已使用' : '已过期' }}
            </span>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCoupons, getAvailableCouponCount } from '@/api/user'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Ticket, CircleCheck, Clock } from '@element-plus/icons-vue'

const router = useRouter()
const coupons = ref([])
const loading = ref(true)
const activeTab = ref('available')
const availableCount = ref(0)

const tabs = [
  { label: '可用', value: 'available' },
  { label: '已使用', value: 'used' },
  { label: '已过期', value: 'expired' }
]

const filteredCoupons = computed(() => {
  const statusMap = { available: 0, used: 1, expired: 2 }
  const status = statusMap[activeTab.value]
  return coupons.value.filter(c => c.status === status)
})

const emptyTitle = computed(() => {
  const titles = { available: '暂无可用优惠券', used: '暂无已使用优惠券', expired: '暂无过期优惠券' }
  return titles[activeTab.value]
})

const emptyDesc = computed(() => {
  const descs = { 
    available: '快去领取优惠券享受优惠吧！', 
    used: '您还没有使用过优惠券', 
    expired: '没有过期的优惠券' 
  }
  return descs[activeTab.value]
})

const emptyIcon = computed(() => {
  const icons = { available: 'Ticket', used: 'CircleCheck', expired: 'Clock' }
  return icons[activeTab.value]
})

const emptyIconBg = computed(() => {
  const bgs = { 
    available: 'linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%)', 
    used: 'linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%)', 
    expired: 'linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%)' 
  }
  return bgs[activeTab.value]
})

const loadCoupons = async () => {
  try {
    loading.value = true
    const res = await getCoupons()
    coupons.value = res.data || []
    const countRes = await getAvailableCouponCount()
    availableCount.value = countRes.data || 0
  } catch (error) {
    ElMessage.error('加载优惠券失败')
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

const useCoupon = (coupon) => {
  if (coupon.merchantId) {
    router.push(`/shop/${coupon.merchantId}`)
  } else {
    router.push('/home')
  }
}

onMounted(() => {
  loadCoupons()
})
</script>

<style scoped>
.coupons-page {
  min-height: 100vh;
  background: #f5f7fa;
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

/* 优惠券统计 */
.coupon-stats {
  padding: 16px 20px;
  background: #fff;
  margin-bottom: 12px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

/* 标签页 */
.coupon-tabs {
  display: flex;
  background: #fff;
  padding: 0 20px;
  border-bottom: 1px solid #f0f0f0;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 16px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  position: relative;
  transition: all 0.3s;
}

.tab-item.active {
  color: #667eea;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20%;
  right: 20%;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 3px 3px 0 0;
}

/* 优惠券内容 */
.coupons-content {
  padding: 16px 20px;
}

/* 加载状态 */
.loading-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skeleton-coupon {
  height: 120px;
  background: #fff;
  border-radius: 16px;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: #fff;
}

.empty-state h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 8px;
}

.empty-state p {
  font-size: 14px;
  color: #999;
}

/* 优惠券列表 */
.coupons-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.coupon-card {
  display: flex;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.coupon-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.coupon-card.unavailable {
  opacity: 0.6;
}

.coupon-left {
  width: 120px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.coupon-card.unavailable .coupon-left {
  background: #ccc;
}

.coupon-amount {
  display: flex;
  align-items: baseline;
  margin-bottom: 4px;
}

.currency {
  font-size: 16px;
  font-weight: 500;
}

.value {
  font-size: 36px;
  font-weight: 700;
}

.unit {
  font-size: 16px;
}

.coupon-condition {
  font-size: 12px;
  opacity: 0.9;
}

.coupon-divider {
  width: 0;
  border-left: 2px dashed #e0e0e0;
  margin: 12px 0;
}

.coupon-right {
  flex: 1;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.coupon-info {
  flex: 1;
}

.coupon-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.coupon-time {
  font-size: 13px;
  color: #999;
  margin: 0 0 8px 0;
}

.coupon-scope {
  display: inline-block;
  padding: 4px 8px;
  background: #f0f0f0;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
}

.coupon-scope.universal {
  background: #e8f5e9;
  color: #4caf50;
}

.btn-use {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-use:hover {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.status-text {
  font-size: 14px;
  color: #999;
  white-space: nowrap;
}

/* 响应式 */
@media (max-width: 480px) {
  .coupons-content {
    padding: 12px 16px;
  }
  
  .coupon-left {
    width: 100px;
    padding: 16px;
  }
  
  .value {
    font-size: 28px;
  }
  
  .coupon-right {
    padding: 16px;
  }
}
</style>
