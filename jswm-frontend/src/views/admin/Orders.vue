<template>
  <div class="admin-orders">
    <div class="page-header">
      <h2>订单管理</h2>
      <div class="header-actions">
        <div class="search-box">
          <el-icon><Search /></el-icon>
          <input v-model="searchQuery" type="text" placeholder="搜索订单号..." @input="handleSearch" />
        </div>
        <div class="filter-tabs">
          <button :class="{ active: activeTab === 'all' }" @click="changeTab('all')">全部</button>
          <button :class="{ active: activeTab === 'pending' }" @click="changeTab('pending')">待处理</button>
          <button :class="{ active: activeTab === 'processing' }" @click="changeTab('processing')">进行中</button>
          <button :class="{ active: activeTab === 'completed' }" @click="changeTab('completed')">已完成</button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <el-icon class="loading-icon"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
    <div v-else-if="orders.length === 0" class="empty-state">
      <el-icon><Document /></el-icon>
      <span>暂无订单数据</span>
    </div>
    <div v-else class="orders-table">
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
        <div v-for="order in orders" :key="order.id" class="table-row">
          <span class="order-no">{{ order.orderNo }}</span>
          <div class="user-cell">
            <span class="name">{{ order.userName }}</span>
            <span class="phone">{{ order.userPhone }}</span>
          </div>
          <span class="merchant">{{ order.merchantName }}</span>
          <span class="amount">¥{{ order.amount }}</span>
          <span :class="['status', getStatusClass(order.status)]">{{ getStatusText(order.status) }}</span>
          <span class="time">{{ formatTime(order.createTime) }}</span>
          <div class="actions">
            <button class="action-btn" @click="viewDetail(order)">详情</button>
            <button v-if="order.status === 1" class="action-btn danger" @click="cancelOrder(order)">取消</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="orders.length > 0" class="pagination">
      <span class="total">共 {{ total }} 条</span>
      <div class="page-btns">
        <button :disabled="currentPage === 1" @click="currentPage--">上一页</button>
        <span class="page-num">{{ currentPage }} / {{ totalPages }}</span>
        <button :disabled="currentPage === totalPages" @click="currentPage++">下一页</button>
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-section">
          <h4>订单信息</h4>
          <div class="detail-row">
            <span class="label">订单号：</span>
            <span class="value">{{ currentOrder.orderNo }}</span>
          </div>
          <div class="detail-row">
            <span class="label">订单状态：</span>
            <span :class="['status-tag', getStatusClass(currentOrder.status)]">{{ getStatusText(currentOrder.status) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">下单时间：</span>
            <span class="value">{{ formatTime(currentOrder.createTime) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">订单金额：</span>
            <span class="value amount">¥{{ currentOrder.amount }}</span>
          </div>
        </div>

        <div class="detail-section">
          <h4>用户信息</h4>
          <div class="detail-row">
            <span class="label">收货人：</span>
            <span class="value">{{ currentOrder.receiverName || currentOrder.userName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">联系电话：</span>
            <span class="value">{{ currentOrder.receiverPhone || currentOrder.userPhone }}</span>
          </div>
          <div class="detail-row">
            <span class="label">配送地址：</span>
            <span class="value">{{ currentOrder.deliveryAddress || '-' }}</span>
          </div>
        </div>

        <div class="detail-section">
          <h4>商家信息</h4>
          <div class="detail-row">
            <span class="label">商家名称：</span>
            <span class="value">{{ currentOrder.merchantName }}</span>
          </div>
        </div>

        <div v-if="currentOrder.remark" class="detail-section">
          <h4>订单备注</h4>
          <div class="detail-row">
            <span class="value">{{ currentOrder.remark }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button v-if="currentOrder && currentOrder.status === 1" type="primary" @click="handleAcceptOrder">接单</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 取消订单弹窗 -->
    <el-dialog
      v-model="cancelDialogVisible"
      title="取消订单"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form :model="cancelForm" label-width="80px">
        <el-form-item label="取消原因">
          <el-input v-model="cancelForm.reason" type="textarea" :rows="3" placeholder="请输入取消原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelDialogVisible = false">取消</el-button>
          <el-button type="danger" :loading="cancelLoading" @click="handleConfirmCancel">确认取消</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Search, Loading, Document } from '@element-plus/icons-vue'
import { getOrderList, updateOrderStatus, cancelOrder } from '@/api/adminOrder'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchQuery = ref('')
const activeTab = ref('all')
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)
const orders = ref([])
const loading = ref(false)

// 详情弹窗
const detailDialogVisible = ref(false)
const currentOrder = ref(null)

// 取消弹窗
const cancelDialogVisible = ref(false)
const cancelLoading = ref(false)
const cancelForm = ref({
  orderId: null,
  reason: ''
})

// 状态映射
const statusMap = {
  1: { text: '待接单', class: 'pending', tab: 'pending' },
  2: { text: '制作中', class: 'processing', tab: 'processing' },
  3: { text: '配送中', class: 'delivering', tab: 'processing' },
  4: { text: '已完成', class: 'completed', tab: 'completed' },
  5: { text: '已取消', class: 'cancelled', tab: 'completed' }
}

// 加载订单列表
const loadOrders = async () => {
  try {
    loading.value = true
    
    // 根据当前tab确定状态
    let status = null
    if (activeTab.value === 'pending') status = 1
    else if (activeTab.value === 'processing') status = null // 2和3
    else if (activeTab.value === 'completed') status = null // 4和5
    
    const res = await getOrderList({
      keyword: searchQuery.value,
      status: status,
      page: currentPage.value,
      pageSize: pageSize
    })
    
    let list = res.data.list || []
    
    // 前端过滤processing和completed
    if (activeTab.value === 'processing') {
      list = list.filter(o => o.status === 2 || o.status === 3)
    } else if (activeTab.value === 'completed') {
      list = list.filter(o => o.status === 4 || o.status === 5)
    }
    
    orders.value = list
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索防抖
let searchTimeout
const handleSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 1
    loadOrders()
  }, 300)
}

// 切换标签
const changeTab = (tab) => {
  activeTab.value = tab
  currentPage.value = 1
  loadOrders()
}

// 监听页码变化
watch(currentPage, loadOrders)

const totalPages = computed(() => Math.ceil(total.value / pageSize))

const getStatusClass = (status) => {
  return statusMap[status]?.class || ''
}

const getStatusText = (status) => {
  return statusMap[status]?.text || '未知'
}

const formatTime = (time) => {
  if (!time) return '-'
  if (typeof time === 'string') {
    return time.replace('T', ' ').substring(0, 16)
  }
  return time
}

const viewDetail = (order) => {
  currentOrder.value = order
  detailDialogVisible.value = true
}

const handleAcceptOrder = async () => {
  try {
    await updateOrderStatus(currentOrder.value.id, 2) // 制作中
    ElMessage.success('接单成功')
    currentOrder.value.status = 2
    detailDialogVisible.value = false
    loadOrders()
  } catch (error) {
    ElMessage.error('接单失败')
  }
}

const cancelOrder = (order) => {
  cancelForm.value.orderId = order.id
  cancelForm.value.reason = ''
  cancelDialogVisible.value = true
}

const handleConfirmCancel = async () => {
  if (!cancelForm.value.reason.trim()) {
    ElMessage.warning('请输入取消原因')
    return
  }
  
  try {
    cancelLoading.value = true
    await cancelOrder(cancelForm.value.orderId, cancelForm.value.reason)
    ElMessage.success('订单已取消')
    cancelDialogVisible.value = false
    loadOrders()
  } catch (error) {
    ElMessage.error('取消失败')
  } finally {
    cancelLoading.value = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadOrders()
})
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

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  color: #8c8c8c;
  gap: 12px;
}

.loading-state .el-icon {
  font-size: 32px;
  animation: rotate 1s linear infinite;
}

.empty-state .el-icon {
  font-size: 48px;
  color: #d9d9d9;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
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
  font-weight: 500;
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

.merchant {
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
  width: fit-content;
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

.time {
  color: #8c8c8c;
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

.action-btn.danger {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.action-btn.danger:hover {
  background: #ff4d4f;
  color: white;
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

.order-detail {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1f1f1f;
  margin: 0 0 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-row {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
}

.detail-row .label {
  width: 100px;
  color: #8c8c8c;
  flex-shrink: 0;
}

.detail-row .value {
  flex: 1;
  color: #1f1f1f;
}

.detail-row .value.amount {
  font-weight: 600;
  color: #ff6b35;
  font-size: 18px;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.pending {
  background: #fff7e6;
  color: #fa8c16;
}

.status-tag.processing {
  background: #e6f7ff;
  color: #1890ff;
}

.status-tag.delivering {
  background: #f6ffed;
  color: #52c41a;
}

.status-tag.completed {
  background: #f6ffed;
  color: #52c41a;
}

.status-tag.cancelled {
  background: #fff1f0;
  color: #ff4d4f;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
