<template>
  <div class="admin-merchants">
    <div class="page-header">
      <h2>商家管理</h2>
      <div class="header-actions">
        <div class="search-box">
          <el-icon><Search /></el-icon>
          <input v-model="searchQuery" type="text" placeholder="搜索商家..." />
        </div>
        <button class="add-btn" @click="showAddModal = true">
          <el-icon><Plus /></el-icon>
          添加商家
        </button>
      </div>
    </div>

    <div class="merchants-grid">
      <div v-for="merchant in filteredMerchants" :key="merchant.id" class="merchant-card">
        <div class="card-header">
          <img :src="merchant.logo || '/shop-default.jpg'" class="merchant-logo" />
          <div class="merchant-basic">
            <h3>{{ merchant.name }}</h3>
            <div class="rating">
              <el-icon v-for="i in 5" :key="i" :class="{ filled: i <= merchant.rating }"><Star /></el-icon>
              <span>{{ merchant.rating }}</span>
            </div>
          </div>
          <span :class="['status-badge', merchant.status === 1 ? 'active' : 'inactive']">
            {{ merchant.status === 1 ? '营业中' : '已关闭' }}
          </span>
        </div>
        <div class="card-body">
          <p class="info-item">
            <el-icon><Location /></el-icon>
            <span>{{ merchant.address }}</span>
          </p>
          <p class="info-item">
            <el-icon><Phone /></el-icon>
            <span>{{ merchant.phone }}</span>
          </p>
          <p class="info-item">
            <el-icon><Clock /></el-icon>
            <span>{{ merchant.businessHours }}</span>
          </p>
        </div>
        <div class="card-footer">
          <div class="stats">
            <span>月售 {{ merchant.monthlySales }}</span>
            <span>评分 {{ merchant.rating }}</span>
          </div>
          <div class="actions">
            <button @click="editMerchant(merchant)">编辑</button>
            <button :class="{ danger: merchant.status === 1 }" @click="toggleStatus(merchant)">
              {{ merchant.status === 1 ? '关闭' : '开启' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Search, Plus, Star, Location, Phone, Clock } from '@element-plus/icons-vue'

const searchQuery = ref('')
const showAddModal = ref(false)

const merchants = ref([
  { id: 1, name: '美味餐厅', rating: 4.8, status: 1, address: '校园东区食堂1楼', phone: '400-123-4567', businessHours: '07:00-21:00', monthlySales: 1256, logo: '/shop1.jpg' },
  { id: 2, name: '快乐汉堡', rating: 4.5, status: 1, address: '校园西区食堂2楼', phone: '400-123-4568', businessHours: '09:00-22:00', monthlySales: 892, logo: '/shop2.jpg' },
  { id: 3, name: '鲜果时光', rating: 4.9, status: 0, address: '校园北区商业街', phone: '400-123-4569', businessHours: '08:00-20:00', monthlySales: 567, logo: '/shop3.jpg' },
  { id: 4, name: '川味小厨', rating: 4.6, status: 1, address: '校园南区食堂3楼', phone: '400-123-4570', businessHours: '10:00-21:00', monthlySales: 723, logo: '/shop4.jpg' }
])

const filteredMerchants = computed(() => {
  if (!searchQuery.value) return merchants.value
  return merchants.value.filter(m => m.name.includes(searchQuery.value))
})

const editMerchant = (merchant) => {
  console.log('编辑商家:', merchant)
}

const toggleStatus = (merchant) => {
  merchant.status = merchant.status === 1 ? 0 : 1
}
</script>

<style scoped>
.admin-merchants {
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
  gap: 12px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: white;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  width: 280px;
}

.search-box input {
  border: none;
  outline: none;
  font-size: 14px;
  width: 100%;
  background: transparent;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: #ff6b35;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  background: #e55a2b;
}

.merchants-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 24px;
}

.merchant-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.merchant-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.merchant-logo {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  object-fit: cover;
}

.merchant-basic {
  flex: 1;
}

.merchant-basic h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f1f1f;
  margin: 0 0 8px;
}

.rating {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #ffc107;
  font-size: 14px;
}

.rating span {
  margin-left: 4px;
  color: #595959;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background: #f6ffed;
  color: #52c41a;
}

.status-badge.inactive {
  background: #fff1f0;
  color: #ff4d4f;
}

.card-body {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 8px;
  font-size: 13px;
  color: #595959;
}

.info-item:last-child {
  margin-bottom: 0;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #8c8c8c;
}

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

.actions button.danger:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
}
</style>
