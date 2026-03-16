<template>
  <div class="admin-merchants">
    <div class="page-header">
      <h2>商家管理</h2>
      <div class="header-actions">
        <div class="search-box">
          <el-icon><Search /></el-icon>
          <input v-model="searchQuery" type="text" placeholder="搜索商家..." @input="handleSearch" />
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <el-icon class="loading-icon"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
    <div v-else-if="merchants.length === 0" class="empty-state">
      <el-icon><Shop /></el-icon>
      <span>暂无商家数据</span>
    </div>
    <div v-else class="merchants-grid">
      <div v-for="merchant in merchants" :key="merchant.id" class="merchant-card">
        <div class="card-header">
          <img :src="merchant.shopLogo || '/shop-default.jpg'" class="merchant-logo" />
          <div class="merchant-basic">
            <h3>{{ merchant.shopName }}</h3>
            <div class="rating">
              <el-icon v-for="i in 5" :key="i" :class="{ filled: i <= Math.round(merchant.rating || 0) }"><Star /></el-icon>
              <span>{{ merchant.rating || '暂无评分' }}</span>
            </div>
          </div>
          <span :class="['status-badge', merchant.status === 1 ? 'active' : 'inactive']">
            {{ merchant.status === 1 ? '营业中' : '已关闭' }}
          </span>
        </div>
        <div class="card-body">
          <p class="info-item">
            <el-icon><Location /></el-icon>
            <span>{{ merchant.shopAddress || '暂无地址' }}</span>
          </p>
          <p class="info-item">
            <el-icon><CollectionTag /></el-icon>
            <span>{{ merchant.category || '暂无分类' }}</span>
          </p>
          <p class="info-item">
            <el-icon><Clock /></el-icon>
            <span>{{ merchant.businessHours || '暂无营业时间' }}</span>
          </p>
        </div>
        <div class="card-footer">
          <div class="stats">
            <span>销量 {{ merchant.sales || 0 }}</span>
          </div>
          <div class="actions">
            <button class="action-btn" @click="editMerchant(merchant)">编辑</button>
            <button :class="['action-btn', merchant.status === 1 ? 'danger' : 'success']" @click="toggleStatus(merchant)">
              {{ merchant.status === 1 ? '关闭' : '开启' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="merchants.length > 0" class="pagination">
      <span class="total">共 {{ total }} 条</span>
      <div class="page-btns">
        <button :disabled="currentPage === 1" @click="currentPage--">上一页</button>
        <span class="page-num">{{ currentPage }} / {{ totalPages }}</span>
        <button :disabled="currentPage === totalPages" @click="currentPage++">下一页</button>
      </div>
    </div>

    <!-- 编辑商家弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑商家"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
        class="edit-form"
      >
        <el-form-item label="店铺名称" prop="shopName">
          <el-input v-model="editForm.shopName" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item label="店铺地址" prop="shopAddress">
          <el-input v-model="editForm.shopAddress" placeholder="请输入店铺地址" />
        </el-form-item>
        <el-form-item label="经营品类" prop="category">
          <el-input v-model="editForm.category" placeholder="请输入经营品类" />
        </el-form-item>
        <el-form-item label="营业时间" prop="businessHours">
          <el-input v-model="editForm.businessHours" placeholder="如：09:00-22:00" />
        </el-form-item>
        <el-form-item label="店铺简介" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入店铺简介" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="editForm.status">
            <el-radio :label="1">营业中</el-radio>
            <el-radio :label="0">已关闭</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="editLoading" @click="handleSaveEdit">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Search, Star, Location, Clock, CollectionTag, Loading, Shop } from '@element-plus/icons-vue'
import { getMerchantList, updateMerchant, updateMerchantStatus } from '@/api/adminMerchant'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)
const merchants = ref([])
const loading = ref(false)

// 编辑弹窗相关
const editDialogVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref(null)
const editForm = ref({
  id: null,
  shopName: '',
  shopAddress: '',
  category: '',
  businessHours: '',
  description: '',
  status: 1
})

const editRules = {
  shopName: [
    { required: true, message: '请输入店铺名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  shopAddress: [
    { required: true, message: '请输入店铺地址', trigger: 'blur' }
  ]
}

// 加载商家列表
const loadMerchants = async () => {
  try {
    loading.value = true
    const res = await getMerchantList({
      keyword: searchQuery.value,
      page: currentPage.value,
      pageSize: pageSize
    })
    const data = res.data
    merchants.value = data.list || []
    total.value = data.total || 0
  } catch (error) {
    ElMessage.error('加载商家列表失败')
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
    loadMerchants()
  }, 300)
}

// 监听页码变化
watch(currentPage, loadMerchants)

const totalPages = computed(() => Math.ceil(total.value / pageSize))

const editMerchant = (merchant) => {
  editForm.value = {
    id: merchant.id,
    shopName: merchant.shopName,
    shopAddress: merchant.shopAddress || '',
    category: merchant.category || '',
    businessHours: merchant.businessHours || '',
    description: merchant.description || '',
    status: merchant.status
  }
  editDialogVisible.value = true
}

const handleSaveEdit = async () => {
  if (!editFormRef.value) return
  
  try {
    await editFormRef.value.validate()
    editLoading.value = true
    
    await updateMerchant(editForm.value.id, {
      shopName: editForm.value.shopName,
      shopAddress: editForm.value.shopAddress,
      category: editForm.value.category,
      businessHours: editForm.value.businessHours,
      description: editForm.value.description,
      status: editForm.value.status
    })
    
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    loadMerchants()
  } catch (error) {
    if (error !== 'validation') {
      ElMessage.error('保存失败')
    }
  } finally {
    editLoading.value = false
  }
}

const toggleStatus = async (merchant) => {
  try {
    const newStatus = merchant.status === 1 ? 0 : 1
    const actionText = newStatus === 1 ? '开启' : '关闭'
    
    await ElMessageBox.confirm(
      `确定要${actionText}商家 "${merchant.shopName}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await updateMerchantStatus(merchant.id, newStatus)
    merchant.status = newStatus
    ElMessage.success(`${actionText}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadMerchants()
})
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
}

.rating .el-icon {
  font-size: 14px;
  color: #d9d9d9;
}

.rating .el-icon.filled {
  color: #ffc53d;
}

.rating span {
  margin-left: 4px;
  font-size: 14px;
  color: #8c8c8c;
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
  margin: 8px 0;
  font-size: 14px;
  color: #595959;
}

.info-item .el-icon {
  font-size: 16px;
  color: #8c8c8c;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.stats {
  font-size: 14px;
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

.action-btn.success {
  border-color: #52c41a;
  color: #52c41a;
}

.action-btn.success:hover {
  background: #52c41a;
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

.edit-form {
  padding: 20px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
