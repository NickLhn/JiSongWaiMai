<template>
  <div class="admin-users">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <div class="search-box">
          <el-icon><Search /></el-icon>
          <input v-model="searchQuery" type="text" placeholder="搜索用户..." />
        </div>
        <button class="filter-btn">
          <el-icon><Filter /></el-icon>
          筛选
        </button>
      </div>
    </div>

    <div class="users-table">
      <div class="table-header">
        <span>用户信息</span>
        <span>手机号</span>
        <span>角色</span>
        <span>状态</span>
        <span>注册时间</span>
        <span>操作</span>
      </div>
      <div class="table-body">
        <div v-if="loading" class="loading-state">
          <el-icon class="loading-icon"><Loading /></el-icon>
          <span>加载中...</span>
        </div>
        <div v-else-if="users.length === 0" class="empty-state">
          <el-icon><UserIcon /></el-icon>
          <span>暂无用户数据</span>
        </div>
        <div v-for="user in users" :key="user.id" class="table-row">
          <div class="user-info">
            <el-avatar :size="40" :src="user.avatar" />
            <div class="user-detail">
              <span class="name">{{ user.name }}</span>
              <span class="email">{{ user.email }}</span>
            </div>
          </div>
          <span class="phone">{{ user.phone }}</span>
          <span :class="['role', getRoleClass(user.role)]">{{ getRoleText(user.role) }}</span>
          <span :class="['status', user.status === 1 ? 'active' : 'inactive']">
            {{ user.status === 1 ? '正常' : '禁用' }}
          </span>
          <span class="time">{{ user.createTime }}</span>
          <div class="actions">
            <button class="action-btn" @click="editUser(user)">编辑</button>
            <button class="action-btn danger" @click="toggleStatus(user)">
              {{ user.status === 1 ? '禁用' : '启用' }}
            </button>
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
import { ref, computed, onMounted, watch } from 'vue'
import { Search, Filter, Loading, User as UserIcon } from '@element-plus/icons-vue'
import { getUserList, updateUserStatus } from '@/api/adminUser'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)
const users = ref([])
const loading = ref(false)

// 加载用户列表
const loadUsers = async () => {
  try {
    loading.value = true
    const res = await getUserList({
      keyword: searchQuery.value,
      page: currentPage.value,
      pageSize: pageSize
    })
    const data = res.data
    users.value = (data.list || []).map(u => ({
      id: u.id,
      name: u.realName || u.username,
      email: u.email || '-',
      phone: u.phone || '-',
      role: u.role,
      status: u.status,
      avatar: u.avatar,
      createTime: u.createTime ? u.createTime.substring(0, 10) : '-'
    }))
    total.value = data.total || 0
  } catch (error) {
    ElMessage.error('加载用户列表失败')
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
    loadUsers()
  }, 300)
}

// 监听搜索关键词变化
watch(searchQuery, handleSearch)

// 监听页码变化
watch(currentPage, loadUsers)

const totalPages = computed(() => Math.ceil(total.value / pageSize))

const getRoleText = (role) => {
  const map = { 0: '学生', 1: '商家', 2: '管理员' }
  return map[role] || '未知'
}

const getRoleClass = (role) => {
  const map = { 0: 'student', 1: 'merchant', 2: 'admin' }
  return map[role] || ''
}

const editUser = (user) => {
  console.log('编辑用户:', user)
  ElMessage.info('编辑功能开发中')
}

const toggleStatus = async (user) => {
  try {
    const newStatus = user.status === 1 ? 0 : 1
    const actionText = newStatus === 1 ? '启用' : '禁用'
    
    await ElMessageBox.confirm(
      `确定要${actionText}用户 "${user.name}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await updateUserStatus(user.id, newStatus)
    user.status = newStatus
    ElMessage.success(`${actionText}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users {
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

.filter-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: white;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-btn:hover {
  border-color: #ff6b35;
  color: #ff6b35;
}

.users-table {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1.5fr 1fr 1fr 1.5fr 1.5fr;
  gap: 16px;
  padding: 16px 24px;
  background: #f5f5f5;
  font-size: 14px;
  font-weight: 600;
  color: #595959;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1.5fr 1fr 1fr 1.5fr 1.5fr;
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

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
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

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-detail .name {
  font-weight: 600;
  color: #1f1f1f;
}

.user-detail .email {
  font-size: 12px;
  color: #8c8c8c;
}

.role {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  width: fit-content;
}

.role.student { background: #e6f7ff; color: #1890ff; }
.role.admin { background: #fff1f0; color: #ff4d4f; }
.role.merchant { background: #f6ffed; color: #52c41a; }

.status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  width: fit-content;
}

.status.active { background: #f6ffed; color: #52c41a; }
.status.inactive { background: #fff1f0; color: #ff4d4f; }

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

.action-btn.danger:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
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
