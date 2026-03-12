<template>
  <div class="merchant-orders">
    <div class="page-header">
      <h2>订单管理</h2>
      <div class="filter-tabs">
        <button :class="{ active: activeTab === 'all' }" @click="activeTab = 'all'">全部</button>
        <button :class="{ active: activeTab === 'pending' }" @click="activeTab = 'pending'">待处理</button>
        <button :class="{ active: activeTab === 'processing' }" @click="activeTab = 'processing'">制作中</button>
        <button :class="{ active: activeTab === 'delivering' }" @click="activeTab = 'delivering'">配送中</button>
        <button :class="{ active: activeTab === 'completed' }" @click="activeTab = 'completed'">已完成</button>
      </div>
    </div>
    
    <div class="orders-list">
      <div v-for="order in filteredOrders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-no">{{ order.orderNo }}</span>
          <span :class="['status', getStatusClass(order.status)]">{{ getStatusText(order.status) }}</span>
        </div>
        <div class="order-body">
          <div class="customer-info">
            <p><strong>{{ order.customerName }}</strong> {{ order.customerPhone }}</p>
            <p class="address">{{ order.address }}</p>
          </div>
          <div class="order-items">
            <p v-for="(item, idx) in order.items" :key="idx">{{ item.name }} x{{ item.quantity }}</p>
          </div>
          <div class="order-total">
            <span>合计: <strong>¥{{ order.total }}</strong></span>
          </div>
        </div>
        <div class="order-actions">
          <button v-if="order.status === 1" class="primary" @click="acceptOrder(order)">接单</button>
          <button v-if="order.status === 2" class="primary" @click="startDelivery(order)">开始配送</button>
          <button v-if="order.status === 3" class="primary" @click="completeOrder(order)">完成订单</button>
          <button @click="viewDetail(order)">查看详情</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const activeTab = ref('all')
const orders = ref([
  { id: 1, orderNo: 'ORD202603120001', customerName: '张三', customerPhone: '13800138001', address: '1号楼301', items: [{ name: '红烧肉盖饭', quantity: 2 }], total: 30, status: 1 },
  { id: 2, orderNo: 'ORD202603120002', customerName: '李四', customerPhone: '13800138002', address: '2号楼502', items: [{ name: '宫保鸡丁饭', quantity: 1 }], total: 13, status: 2 },
  { id: 3, orderNo: 'ORD202603120003', customerName: '王五', customerPhone: '13800138003', address: '3号楼101', items: [{ name: '番茄鸡蛋面', quantity: 3 }], total: 30, status: 1 }
])

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  const statusMap = { pending: 1, processing: 2, delivering: 3, completed: 4 }
  return orders.value.filter(o => o.status === statusMap[activeTab.value])
})

const getStatusClass = (status) => {
  const map = { 1: 'pending', 2: 'processing', 3: 'delivering', 4: 'completed' }
  return map[status]
}

const getStatusText = (status) => {
  const map = { 1: '待接单', 2: '制作中', 3: '配送中', 4: '已完成' }
  return map[status]
}

const acceptOrder = (order) => { order.status = 2 }
const startDelivery = (order) => { order.status = 3 }
const completeOrder = (order) => { order.status = 4 }
const viewDetail = (order) => { console.log('查看订单:', order) }
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
  background: white;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-tabs button.active {
  background: #ff6b35;
  color: white;
  border-color: #ff6b35;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-family: monospace;
  font-size: 14px;
  color: #1f1f1f;
}

.status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status.pending { background: #fff7e6; color: #fa8c16; }
.status.processing { background: #e6f7ff; color: #1890ff; }
.status.delivering { background: #f9f0ff; color: #722ed1; }
.status.completed { background: #f6ffed; color: #52c41a; }

.order-body {
  margin-bottom: 16px;
}

.customer-info {
  margin-bottom: 12px;
}

.customer-info p {
  margin: 0;
  font-size: 14px;
  color: #595959;
}

.customer-info .address {
  color: #8c8c8c;
  font-size: 13px;
  margin-top: 4px;
}

.order-items {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 12px;
}

.order-items p {
  margin: 0;
  font-size: 14px;
  color: #595959;
}

.order-total {
  text-align: right;
  font-size: 14px;
  color: #595959;
}

.order-total strong {
  color: #ff6b35;
  font-size: 18px;
}

.order-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.order-actions button {
  padding: 8px 16px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.order-actions button.primary {
  background: #ff6b35;
  color: white;
  border-color: #ff6b35;
}

.order-actions button:hover {
  opacity: 0.8;
}
</style>
