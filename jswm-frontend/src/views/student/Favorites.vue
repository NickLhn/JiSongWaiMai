<template>
  <div class="favorites-page">
    <header class="page-header">
      <div class="header-content">
        <button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <h1>我的收藏</h1>
        <button class="edit-btn" @click="isEditing = !isEditing">
          {{ isEditing ? '完成' : '编辑' }}
        </button>
      </div>
    </header>

    <main class="favorites-content">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-grid">
        <div v-for="i in 4" :key="i" class="skeleton-card"></div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="favorites.length === 0" class="empty-state">
        <div class="empty-icon">
          <el-icon :size="64"><Star /></el-icon>
        </div>
        <h3>暂无收藏</h3>
        <p>收藏喜欢的商家，方便下次快速找到</p>
        <button class="btn-primary" @click="$router.push('/home')">
          <el-icon><Shop /></el-icon>
          去逛逛
        </button>
      </div>

      <!-- 收藏列表 -->
      <div v-else class="favorites-grid">
        <div 
          v-for="item in favorites" 
          :key="item.id" 
          class="favorite-card"
          @click="goToShop(item.merchant?.id)"
        >
          <div class="card-image">
            <img :src="item.merchant?.shopLogo || '/shop-default.jpg'" alt="">
            <div v-if="isEditing" class="select-mask" @click.stop="toggleSelect(item.id)">
              <div :class="['select-circle', { selected: selectedItems.includes(item.id) }]">
                <el-icon v-if="selectedItems.includes(item.id)"><Check /></el-icon>
              </div>
            </div>
          </div>
          <div class="card-info">
            <h3 class="shop-name">{{ item.merchant?.shopName }}</h3>
            <div class="shop-meta">
              <span class="rating">
                <el-icon><StarFilled /></el-icon>
                {{ item.merchant?.rating }}
              </span>
              <span class="sales">月售{{ item.merchant?.sales }}</span>
            </div>
            <div class="shop-tags">
              <span class="tag">{{ item.merchant?.category }}</span>
              <span class="tag time">{{ item.merchant?.businessHours }}</span>
            </div>
            <p class="shop-address">
              <el-icon><Location /></el-icon>
              {{ item.merchant?.shopAddress }}
            </p>
          </div>
          <button class="remove-btn" @click.stop="removeFavorite(item.merchant?.id)">
            <el-icon><Close /></el-icon>
          </button>
        </div>
      </div>
    </main>

    <!-- 底部操作栏 -->
    <div v-if="isEditing && selectedItems.length > 0" class="bottom-bar">
      <div class="select-all" @click="toggleSelectAll">
        <div :class="['select-circle', { selected: isAllSelected }]">
          <el-icon v-if="isAllSelected"><Check /></el-icon>
        </div>
        <span>全选</span>
      </div>
      <button class="btn-delete" @click="batchRemove">
        删除({{ selectedItems.length }})
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFavorites, removeFavorite as removeFavoriteApi } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Star, Shop, StarFilled, Location, Close, Check } from '@element-plus/icons-vue'

const router = useRouter()
const favorites = ref([])
const loading = ref(true)
const isEditing = ref(false)
const selectedItems = ref([])

const isAllSelected = computed(() => {
  return favorites.value.length > 0 && selectedItems.value.length === favorites.value.length
})

const loadFavorites = async () => {
  try {
    loading.value = true
    const res = await getFavorites()
    favorites.value = res.data || []
  } catch (error) {
    ElMessage.error('加载收藏失败')
  } finally {
    loading.value = false
  }
}

const goToShop = (merchantId) => {
  if (!merchantId) return
  router.push(`/shop/${merchantId}`)
}

const removeFavorite = async (merchantId) => {
  if (!merchantId) return
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeFavoriteApi(merchantId)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const toggleSelect = (id) => {
  const index = selectedItems.value.indexOf(id)
  if (index > -1) {
    selectedItems.value.splice(index, 1)
  } else {
    selectedItems.value.push(id)
  }
}

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedItems.value = []
  } else {
    selectedItems.value = favorites.value.map(item => item.id)
  }
}

const batchRemove = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedItems.value.length} 个收藏吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // 批量删除
    for (const id of selectedItems.value) {
      const item = favorites.value.find(f => f.id === id)
      if (item?.merchant?.id) {
        await removeFavoriteApi(item.merchant.id)
      }
    }
    ElMessage.success('删除成功')
    selectedItems.value = []
    isEditing.value = false
    loadFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 80px;
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

.edit-btn {
  padding: 6px 16px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 16px;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.edit-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.favorites-content {
  padding: 16px 20px;
}

/* 加载状态 */
.loading-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.skeleton-card {
  background: #fff;
  border-radius: 16px;
  height: 280px;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #fff8e1 0%, #ffecb3 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  color: #ffc107;
}

.empty-state h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 8px;
}

.empty-state p {
  font-size: 14px;
  color: #999;
  margin-bottom: 24px;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 24px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

/* 收藏网格 */
.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.favorite-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
}

.favorite-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.card-image {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.favorite-card:hover .card-image img {
  transform: scale(1.05);
}

.select-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  padding: 12px;
}

.select-circle {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 2px solid #fff;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  transition: all 0.3s;
}

.select-circle.selected {
  background: #667eea;
  border-color: #667eea;
}

.card-info {
  padding: 16px;
}

.shop-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.shop-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.rating {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #ffc107;
  font-size: 14px;
  font-weight: 500;
}

.sales {
  font-size: 13px;
  color: #999;
}

.shop-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.tag {
  padding: 4px 8px;
  background: #f0f0f0;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
}

.tag.time {
  background: #e8f5e9;
  color: #4caf50;
}

.shop-address {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.remove-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  transition: all 0.3s;
}

.favorite-card:hover .remove-btn {
  opacity: 1;
}

.remove-btn:hover {
  background: rgba(255, 107, 107, 0.9);
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
}

.btn-delete {
  padding: 10px 24px;
  background: #ff6b6b;
  color: #fff;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-delete:hover {
  background: #ee5a5a;
}

/* 响应式 */
@media (max-width: 480px) {
  .favorites-grid {
    grid-template-columns: 1fr;
  }
  
  .favorites-content {
    padding: 12px 16px;
  }
}
</style>
