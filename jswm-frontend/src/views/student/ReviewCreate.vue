<template>
  <div class="review-page">
    <header class="page-header">
      <button class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <h1>{{ isEdit ? '修改评价' : '发表评价' }}</h1>
    </header>

    <main class="review-content" v-if="order">
      <!-- 订单信息 -->
      <div class="order-info card">
        <h3>订单信息</h3>
        <div class="merchant-info">
          <el-icon><Shop /></el-icon>
          <span>{{ merchantName }}</span>
        </div>
        <div class="dishes-list">
          <div v-for="item in orderItems" :key="item.id" class="dish-item">
            <img :src="item.dishImage || '/dish-default.jpg'" class="dish-img" />
            <div class="dish-info">
              <span class="dish-name">{{ item.dishName }}</span>
              <span class="dish-quantity">x{{ item.quantity }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 评分区域 -->
      <div class="rating-section card">
        <h3>总体评价</h3>
        <div class="rating-stars">
          <el-rate
            v-model="form.rating"
            :max="5"
            :colors="['#ff9900', '#ff9900', '#ff9900']"
            void-color="#c0c4cc"
            :size="32"
          />
          <span class="rating-text">{{ ratingText }}</span>
        </div>
      </div>

      <!-- 评价内容 -->
      <div class="content-section card">
        <h3>评价内容</h3>
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="5"
          placeholder="分享您的用餐体验，帮助其他小伙伴做出选择~"
          maxlength="500"
          show-word-limit
        />
      </div>

      <!-- 匿名选项 -->
      <div class="anonymous-section card">
        <el-checkbox v-model="form.isAnonymous">
          <span>匿名评价</span>
          <span class="anonymous-tip">您的评价将以匿名形式展示</span>
        </el-checkbox>
      </div>

      <!-- 提交按钮 -->
      <div class="submit-section">
        <button class="submit-btn" :disabled="!canSubmit" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '提交评价' }}
        </button>
      </div>
    </main>

    <!-- 加载状态 -->
    <div v-else class="loading-state">
      <el-skeleton :rows="10" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Shop } from '@element-plus/icons-vue'
import { getOrderDetail } from '@/api/order'
import { submitReview, getReviewByOrderId } from '@/api/review'

const route = useRoute()
const router = useRouter()
const orderId = route.query.orderId

const order = ref(null)
const orderItems = ref([])
const merchantName = ref('')
const isEdit = ref(false)
const loading = ref(false)

const form = ref({
  orderId: orderId,
  rating: 5,
  content: '',
  isAnonymous: false
})

const ratingText = computed(() => {
  const texts = ['', '非常差', '差', '一般', '满意', '非常满意']
  return texts[form.value.rating]
})

const canSubmit = computed(() => {
  return form.value.rating > 0 && form.value.content.trim().length >= 5
})

const fetchOrderDetail = async () => {
  try {
    const res = await getOrderDetail(orderId)
    order.value = res.data
    orderItems.value = res.data.items || []
    merchantName.value = res.data.merchantName || '未知商家'
  } catch (error) {
    ElMessage.error('获取订单信息失败')
  }
}

const checkExistingReview = async () => {
  try {
    const res = await getReviewByOrderId(orderId)
    if (res.data) {
      isEdit.value = true
      form.value.rating = res.data.rating
      form.value.content = res.data.content
      form.value.isAnonymous = res.data.isAnonymous
    }
  } catch (error) {
    // 没有评价记录，正常流程
  }
}

const handleSubmit = async () => {
  if (!canSubmit.value) {
    ElMessage.warning('请填写完整评价信息')
    return
  }

  try {
    loading.value = true
    await submitReview(form.value)
    ElMessage.success(isEdit.value ? '修改成功' : '评价提交成功')
    router.back()
  } catch (error) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!orderId) {
    ElMessage.error('缺少订单信息')
    router.back()
    return
  }
  fetchOrderDetail()
  checkExistingReview()
})
</script>

<style scoped>
.review-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

.page-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: white;
  box-shadow: var(--shadow-sm);
}

.page-header h1 {
  flex: 1;
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  margin: 0;
}

.back-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: var(--radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-btn:hover {
  background: var(--bg-tertiary);
}

.review-content {
  padding: 16px;
}

.card {
  background: white;
  border-radius: var(--radius-lg);
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-sm);
}

.card h3 {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 12px 0;
  color: var(--text-primary);
}

.merchant-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  font-weight: 500;
}

.dishes-list {
  margin-top: 12px;
}

.dish-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid var(--border-light);
}

.dish-item:last-child {
  border-bottom: none;
}

.dish-img {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  object-fit: cover;
}

.dish-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dish-name {
  font-size: 14px;
  color: var(--text-primary);
}

.dish-quantity {
  font-size: 13px;
  color: var(--text-secondary);
}

.rating-stars {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px 0;
}

.rating-text {
  font-size: 14px;
  color: var(--text-secondary);
}

.anonymous-tip {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-left: 8px;
}

.submit-section {
  padding: 16px;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 500;
  color: white;
  background: var(--primary-color);
  border: none;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background: var(--primary-hover);
}

.submit-btn:disabled {
  background: var(--text-disabled);
  cursor: not-allowed;
}

.loading-state {
  padding: 16px;
}
</style>
