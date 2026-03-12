<template>
  <div class="admin-orders">
    <div class="page-header">
      <h2>订单管理</h2>
      <div class="header-actions">
        <div class="search-box">
          <el-icon><Search /></el-icon>
          <input v-model="searchQuery" type="text" placeholder="搜索订单号..." />
        </div>
        <div class="filter-tabs">
          <button :class="{ active: activeTab === 'all' }" @click="activeTab = 'all'">全部</button>
          <button :class="{ active: activeTab === 'pending' }" @click="activeTab = 'pending'">待处理</button>
          <button :class="{ active: activeTab === 'processing' }" @click="activeTab = 'processing'">进行中</button>
          <button :class="{ active: activeTab === 'completed' }" @click="activeTab = 'completed'">已完成</button>
        </div>
      </div>
    </div>

    <div class="orders-table">
      <div class="table-header">
        <span>订单号</span>
        <span>用户信息</span>
        <span>商家</span>
        <span>金额</span>
        <span>状态</span>
        <span>下单时间</span>
        <span>操作</span>
      </div>
      <div class="table-body">
        <div v-for="order in filteredOrders" :key="order.id" class="table-row">
          <span class="order-no">{{ order.orderNo }}</span>
          <div class="user-cell">
            <span class="name">{{ order.userName }}</span>
            <span class="phone">{{ order.userPhone }}</span>
          </div>
          <span class="merchant">{{ order.merchantName }}</span>
          <span class="amount">¥{{ order.amount }}</span>
          <span :class="['status', getStatusClass(order.status)]">{{ getStatusText(order.status) }}</span>
          <span class="time">{{ order.createTime }}</span>
          <div class="actions">
            <button @click="viewDetail(order)">详情</button>
            <button v-if="order.status === 1" class="primary" @click="cancelOrder(order)">取消</button>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination">
      <span class="total">共 {{ total }} 条</span>
      <div class="page-btns">
        <button :disabled="currentPage === 1" @click="currentPage--">上一页</button>
        <span class="page-num">{{ currentPage }} / {{ totalPages }}</span>
        <button :disabled="currentPage === totalPages" @click="currentPage++">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const searchQuery = ref('')
const activeTab = ref('all')
const currentPage = ref(1)
const pageSize = 10
const total = ref(8934)

const orders = ref([
  { id: 1, orderNo: 'ORD202603120001', userName: '张三', userPhone: '13800138001', merchantName: '美味餐厅', amount: 35, status: 1, createTime: '2024-03-12 10:30' },
  { id: 2, orderNo: 'ORD202603120002', userName: '李四', userPhone: '13800138002', merchantName: '快乐汉堡', amount: 28, status: 2, createTime: '2024-03-12 10:25' },
  { id: 3, orderNo: 'ORD202603120003', userName: '王五', userPhone: '13800138003', merchantName: '鲜果时光', amount: 45, status: 3, createTime: '2024-03-12 10:20' },
  { id: 4, orderNo: 'ORD202603120004', userName: '赵六', userPhone: '13800138004', merchantName: '川味小厨', amount: 52, status: 4, createTime: '2024-03-12 10:15' },
  { id: 5, orderNo: 'ORD202603120005', userName: '钱七', userPhone: '13800138005', merchantName: '美味餐厅', amount: 18, status: 1, createTime: '2024-03-12 10:10' }
])

const filteredOrders = computed(() => {
  let result = orders.value
  if (activeTab.value !== 'all') {
    const statusMap = { pending: 1, processing: 2, completed: 4 }
    result = result.filter(o => o.status === statusMap[activeTab.value] || (activeTab.value === 'processing' && o.status === 3))
  }
  if (searchQuery.value) {
    result = result.filter(o => o.orderNo.includes(searchQuery.value))
  }
  return result
})

const totalPages = computed(() => Math.ceil(total.value / pageSize))

const getStatusClass = (status) => {
  const map = { 1: 'pending', 2: 'processing', 3: 'delivering', 4: 'completed', 5: 'cancelled' }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = { 1: '待接单', 2: '制作中', 3: '配送中', 4: '已完成', 5: '已取消' }
  return map[status] || '未知'
}

const viewDetail = (order) => {
  console.log('查看订单:', order)
}

const cancelOrder = (order) => {
  order.status = 5
  ElMessage.success('订单已取消')
}
</script>

<style scoped>
.admin-orders {
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: white;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  width: 240px;
}

.search-box input {
  border: none;
  outline: none;
  font-size: 14px;
  width: 100%;
  background: transparent;
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
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-tabs button.active {
  background: #ff6b35;
  color: white;
  border-color: #ff6b35;
}

.orders-table {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.table-header {
  display: grid;
  grid-template-columns: 1.5fr 1.5fr 1.2fr 1fr 1fr 1.2fr 1.2fr;
  gap: 16px;
  padding: 16px 24px;
  background: #f5f5f5;
  font-size: 14px;
  font-weight: 600;
  color: #595959;
}

.table-row {
  display: grid;
  grid-template-columns: 1.5fr 1.5fr 1.2fr 1fr 1fr 1.2fr 1.2fr;
  gap: 16px;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  align-items: center;
  font-size: 14px;
  color: #595959;
}

.table-row:last-child {
  border-bottom: none;
}

.order-no {
  font-family: monospace;
  color: #1f1f1f;
}

.user-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-cell .name {
  font-weight: 500;
  color: #1f1f1f;
}

.user-cell .phone {
  font-size: 12px;
  color: #8c8c8c;
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
  width: fit-content;
}

.status.pending { background: #fff7e6; color: #fa8c16; }
.status.processing { background: #e6f7ff; color: #1890ff; }
.status.delivering { background: #f9f0ff; color: #722ed1; }
.status.completed { background: #f6ffed; color: #52c41a; }
.status.cancelled { background: #fff1f0; color: #ff4d4f; }

.actions {
  display: flex;
  gap: 8px;
}

.actions button {
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.actions button:hover {
  border-color: #ff6b35;
  color: #ff6b35;
}

.actions button.primary {
  background: #ff6b35;
  color: white;
  border-color: #ff6b35;
}

.actions button.primary:hover {
  background: #e55a2b;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding: 16px 24px;
  background: white;
  border-radius: 12px;
}

.total {
  font-size: 14px;
  color: #8c8c8c;
}

.page-btns {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-btns button {
  padding: 8px 16px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btns button:hover:not(:disabled) {
  border-color: #ff6b35;
  color: #ff6b35;
}

.page-btns button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-num {
  font-size: 14px;
  color: #595959;
}
</style>
