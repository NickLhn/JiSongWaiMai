<template>
  <div class="merchant-orders">
    <div class="page-header">
      <h2>订单管理</h2>
      <div class="filter-tabs">
        <button :class="{ active: activeTab === 'all' }" @click="changeTab('all')">全部</button>
        <button :class="{ active: activeTab === 'pending' }" @click="changeTab('pending')">待处理</button>
        <button :class="{ active: activeTab === 'processing' }" @click="changeTab('processing')">制作中</button>
        <button :class="{ active: activeTab === 'delivering' }" @click="changeTab('delivering')">配送中</button>
        <button :class="{ active: activeTab === 'completed' }" @click="changeTab('completed')">已完成</button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="orders.length === 0" class="empty-state">
      <el-icon :size="64"><Document /></el-icon>
      <p>暂无订单</p>
    </div>

    <div v-else class="orders-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-no">{{ order.orderNo }}</span>
          <span :class="['status', getStatusClass(order.status)]">{{ getStatusText(order.status) }}</span>
        </div>
        <div class="order-body">
          <div class="customer-info">
            <p><strong>{{ order.receiverName }}</strong> {{ order.receiverPhone }}</p>
            <p class="address">{{ order.deliveryAddress }}</p>
          </div>
          <div class="order-items">
            <p v-for="(item, idx) in order.items" :key="idx">{{ item.dishName }} x{{ item.quantity }}</p>
          </div>
          <div class="order-total">
            <span>合计: <strong>¥{{ order.totalAmount }}</strong></span>
          </div>
        </div>
        <div class="order-actions">
          <button v-if="order.status === 1" class="primary" @click="handleAccept(order)">接单</button>
          <button v-if="order.status === 2" class="primary" @click="handleDeliver(order)">开始配送</button>
          <button v-if="order.status === 3" class="primary" @click="handleComplete(order)">完成订单</button>
          <button @click="viewDetail(order)">查看详情</button>
        </div>
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <div v-if="selectedOrder" class="order-detail">
        <div class="detail-section">
          <h4>订单信息</h4>
          <p><span>订单号:</span> {{ selectedOrder.orderNo }}</p>
          <p><span>下单时间:</span> {{ selectedOrder.createTime }}</p>
          <p><span>订单状态:</span> {{ getStatusText(selectedOrder.status) }}</p>
        </div>
        <div class="detail-section">
          <h4>收货信息</h4>
          <p><span>收货人:</span> {{ selectedOrder.receiverName }}</p>
          <p><span>联系电话:</span> {{ selectedOrder.receiverPhone }}</p>
          <p><span>配送地址:</span> {{ selectedOrder.deliveryAddress }}</p>
        </div>
        <div class="detail-section">
          <h4>订单商品</h4>
          <div v-for="(item, idx) in selectedOrder.items" :key="idx" class="detail-item">
            <span>{{ item.dishName }} x{{ item.quantity }}</span>
            <span>¥{{ item.price * item.quantity }}</span>
          </div>
        </div>
        <div class="detail-section">
          <h4>费用明细</h4>
          <p><span>商品总额:</span> ¥{{ selectedOrder.totalAmount - selectedOrder.deliveryFee }}</p>
          <p><span>配送费:</span> ¥{{ selectedOrder.deliveryFee }}</p>
          <p class="total"><span>实付金额:</span> <strong>¥{{ selectedOrder.totalAmount }}</strong></p>
        </div>
        <div v-if="selectedOrder.remark" class="detail-section">
          <h4>订单备注</h4>
          <p>{{ selectedOrder.remark }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantOrders, acceptOrder, deliverOrder, completeOrder } from '@/api/merchantOrder'

const activeTab = ref('all')
const orders = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const selectedOrder = ref(null)

const statusMap = {
  all: null,
  pending: 1,
  processing: 2,
  delivering: 3,
  completed: 4
}

const loadOrders = async () => {
  try {
    loading.value = true
    const status = statusMap[activeTab.value]
    const res = await getMerchantOrders(status)
    if (res.code === 200) {
      orders.value = res.data || []
    } else {
      ElMessage.error(res.message || '加载订单失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '加载订单失败')
  } finally {
    loading.value = false
  }
}

const changeTab = (tab) => {
  activeTab.value = tab
  loadOrders()
}

const getStatusClass = (status) => {
  const map = { 1: 'pending', 2: 'processing', 3: 'delivering', 4: 'completed', 5: 'cancelled' }
  return map[status]
}

const getStatusText = (status) => {
  const map = { 1: '待接单', 2: '制作中', 3: '配送中', 4: '已完成', 5: '已取消' }
  return map[status]
}

const handleAccept = async (order) => {
  try {
    await ElMessageBox.confirm('确认接单？', '提示', { type: 'warning' })
    const res = await acceptOrder(order.id)
    if (res.code === 200) {
      ElMessage.success('接单成功')
      loadOrders()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const handleDeliver = async (order) => {
  try {
    await ElMessageBox.confirm('确认开始配送？', '提示', { type: 'warning' })
    const res = await deliverOrder(order.id)
    if (res.code === 200) {
      ElMessage.success('开始配送')
      loadOrders()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const handleComplete = async (order) => {
  try {
    await ElMessageBox.confirm('确认完成订单？', '提示', { type: 'warning' })
    const res = await completeOrder(order.id)
    if (res.code === 200) {
      ElMessage.success('订单已完成')
      loadOrders()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const viewDetail = (order) => {
  selectedOrder.value = order
  detailVisible.value = true
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.merchant-orders {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1f1f1f;
  margin: 0;
}

.filter-tabs {
  display: flex;
  gap: 8px;
}

.filter-tabs button {
  padding: 8px 16px;
  border: 1px solid #d9d9d9;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tabs button:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.filter-tabs button.active {
  background: #1890ff;
  border-color: #1890ff;
  color: #fff;
}

.loading-state {
  padding: 40px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #999;
}

.empty-state .el-icon {
  margin-bottom: 16px;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-size: 14px;
  color: #666;
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
  background: #f6ffed;
  color: #52c41a;
}

.status.completed {
  background: #f6ffed;
  color: #52c41a;
}

.status.cancelled {
  background: #fff1f0;
  color: #ff4d4f;
}

.order-body {
  margin-bottom: 16px;
}

.customer-info {
  margin-bottom: 12px;
}

.customer-info p {
  margin: 4px 0;
  font-size: 14px;
}

.customer-info .address {
  color: #666;
  font-size: 13px;
}

.order-items {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 12px;
}

.order-items p {
  margin: 4px 0;
  font-size: 14px;
}

.order-total {
  text-align: right;
  font-size: 14px;
}

.order-total strong {
  color: #ff4d4f;
  font-size: 16px;
}

.order-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.order-actions button {
  padding: 8px 16px;
  border: 1px solid #d9d9d9;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.order-actions button:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.order-actions button.primary {
  background: #1890ff;
  border-color: #1890ff;
  color: #fff;
}

.order-actions button.primary:hover {
  background: #40a9ff;
}

/* 订单详情样式 */
.order-detail {
  max-height: 500px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-section:last-child {
  border-bottom: none;
}

.detail-section h4 {
  font-size: 14px;
  font-weight: 600;
  color: #1f1f1f;
  margin: 0 0 12px 0;
}

.detail-section p {
  margin: 8px 0;
  font-size: 14px;
  display: flex;
  justify-content: space-between;
}

.detail-section p span:first-child {
  color: #666;
}

.detail-section p.total {
  font-size: 16px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #e8e8e8;
}

.detail-section p.total strong {
  color: #ff4d4f;
  font-size: 18px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}

.detail-item:last-child {
  border-bottom: none;
}
</style>
