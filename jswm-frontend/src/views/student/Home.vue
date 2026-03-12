<template>
  <div class="home-page">
    <!-- Header -->
    <header class="main-header">
      <div class="header-container">
        <div class="brand" @click="$router.push('/home')">
          <div class="logo">
            <svg viewBox="0 0 40 40" fill="none">
              <rect width="40" height="40" rx="10" fill="#ff6b35"/>
              <path d="M20 10c-4.418 0-8 3.582-8 8 0 2.8 1.2 5.2 3.2 6.8V30c0 .88.72 1.6 1.6 1.6h6.4c.88 0 1.6-.72 1.6-1.6v-5.2c2-1.6 3.2-4 3.2-6.8 0-4.418-3.582-8-8-8z" fill="white"/>
            </svg>
          </div>
          <span class="brand-text">校园点餐</span>
        </div>
        
        <div class="header-actions">
          <button class="icon-btn" @click="$router.push('/cart')">
            <el-icon><ShoppingCart /></el-icon>
            <span v-if="cartCount > 0" class="badge">{{ cartCount }}</span>
          </button>
          
          <div class="user-menu" @click="showUserMenu = !showUserMenu">
            <img :src="userInfo?.avatar || '/avatar-default.jpg'" class="avatar" />
            <el-icon><ArrowDown /></el-icon>
            
            <transition name="fade">
              <div v-if="showUserMenu" class="dropdown-menu">
                <div class="menu-item" @click="$router.push('/profile')">
                  <el-icon><User /></el-icon>
                  <span>个人中心</span>
                </div>
                <div class="menu-item" @click="$router.push('/orders')">
                  <el-icon><Document /></el-icon>
                  <span>我的订单</span>
                </div>
                <div class="menu-divider"></div>
                <div class="menu-item logout" @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </div>
              </div>
            </transition>
          </div>
        </div>
      </div>
    </header>

    <!-- Search Section -->
    <section class="search-section">
      <div class="search-container">
        <h1 class="search-title">今天想吃点什么？</h1>
        <div class="search-box">
          <el-icon class="search-icon"><Search /></el-icon>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索商家、菜品..."
            @input="handleSearch"
          />
          <button class="search-btn">搜索</button>
        </div>
        
        <div class="category-tabs">
          <button
            v-for="cat in categories"
            :key="cat"
            :class="['tab-btn', { active: selectedCategory === cat }]"
            @click="selectedCategory = cat"
          >
            {{ cat }}
          </button>
        </div>
      </div>
    </section>

    <!-- Main Content -->
    <main class="main-content">
      <div class="content-container">
        <!-- Section Title -->
        <div class="section-header">
          <h2>推荐商家</h2>
          <div class="sort-options">
            <button :class="{ active: sortBy === 'rating' }" @click="sortBy = 'rating'">评分最高</button>
            <button :class="{ active: sortBy === 'sales' }" @click="sortBy = 'sales'">销量最好</button>
            <button :class="{ active: sortBy === 'distance' }" @click="sortBy = 'distance'">距离最近</button>
          </div>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="merchant-grid">
          <div v-for="i in 8" :key="i" class="merchant-card skeleton">
            <div class="skeleton-img"></div>
            <div class="skeleton-content">
              <div class="skeleton-line"></div>
              <div class="skeleton-line short"></div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else-if="filteredMerchants.length === 0" class="empty-state">
          <el-empty description="暂无商家信息" />
        </div>

        <!-- Merchant Grid -->
        <div v-else class="merchant-grid">
          <div
            v-for="merchant in filteredMerchants"
            :key="merchant.id"
            class="merchant-card"
            @click="goToShop(merchant.id)"
          >
            <div class="card-image">
              <img :src="merchant.shopLogo || '/shop-default.jpg'" :alt="merchant.shopName" />
              <div class="delivery-badge">
                <el-icon><Timer /></el-icon>
                <span>30分钟</span>
              </div>
            </div>
            
            <div class="card-content">
              <h3 class="merchant-name">{{ merchant.shopName }}</h3>
              <p class="merchant-desc">{{ merchant.description }}</p>
              
              <div class="merchant-stats">
                <div class="rating">
                  <el-icon color="#ffb800"><StarFilled /></el-icon>
                  <span class="score">{{ merchant.rating }}</span>
                  <span class="sales">月售{{ formatSales(merchant.sales) }}</span>
                </div>
                <div class="delivery-info">
                  <span class="time">{{ merchant.businessHours }}</span>
                </div>
              </div>
              
              <div class="merchant-tags">
                <span class="tag">{{ merchant.category }}</span>
                <span v-if="merchant.rating >= 4.8" class="tag highlight">好评如潮</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMerchantList } from '@/api/merchant'
import { getCartList } from '@/api/cart'
import { getUserInfo, removeToken, removeUserInfo } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import { ShoppingCart, ArrowDown, User, Document, SwitchButton, Search, Timer, StarFilled } from '@element-plus/icons-vue'

const router = useRouter()
const merchants = ref([])
const cart = ref([])
const userInfo = ref(getUserInfo())
const loading = ref(true)
const searchQuery = ref('')
const selectedCategory = ref('全部')
const sortBy = ref('rating')
const showUserMenu = ref(false)

const categories = ['全部', '快餐简餐', '饮品甜点', '小吃夜宵', '面食粥点']

const cartCount = computed(() => cart.value.reduce((sum, item) => sum + item.quantity, 0))

const filteredMerchants = computed(() => {
  let result = [...merchants.value]
  
  if (selectedCategory.value !== '全部') {
    result = result.filter(m => m.category === selectedCategory.value)
  }
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(m => 
      m.shopName.toLowerCase().includes(query) ||
      m.description.toLowerCase().includes(query)
    )
  }
  
  // Sort
  if (sortBy.value === 'rating') {
    result.sort((a, b) => b.rating - a.rating)
  } else if (sortBy.value === 'sales') {
    result.sort((a, b) => b.sales - a.sales)
  }
  
  return result
})

const loadMerchants = async () => {
  try {
    loading.value = true
    const res = await getMerchantList({ status: 1 })
    merchants.value = res.data
  } catch (error) {
    ElMessage.error('加载商家列表失败')
  } finally {
    loading.value = false
  }
}

const loadCart = async () => {
  try {
    const res = await getCartList()
    cart.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const goToShop = (id) => {
  router.push(`/shop/${id}`)
}

const handleSearch = () => {
  // Debounce search
}

const formatSales = (sales) => {
  if (sales >= 1000) return `${(sales / 1000).toFixed(1)}k`
  return sales
}

const handleLogout = () => {
  removeToken()
  removeUserInfo()
  ElMessage.success('退出成功')
  router.push('/login')
}

onMounted(() => {
  loadMerchants()
  loadCart()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f8f9fa;
}

/* Header */
.main-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 12px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo {
  width: 40px;
  height: 40px;
}

.logo svg {
  width: 100%;
  height: 100%;
}

.brand-text {
  font-size: 20px;
  font-weight: 700;
  color: #ff6b35;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-btn {
  position: relative;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.icon-btn:hover {
  background: #ff6b35;
  color: white;
}

.icon-btn .badge {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 20px;
  height: 20px;
  background: #ff6b35;
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-menu {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px 6px 6px;
  background: #f8f9fa;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.2s;
}

.user-menu:hover {
  background: #e9ecef;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
  padding: 8px;
  min-width: 160px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  color: #495057;
}

.menu-item:hover {
  background: #f8f9fa;
}

.menu-item.logout {
  color: #dc3545;
}

.menu-divider {
  height: 1px;
  background: #e9ecef;
  margin: 8px 0;
}

/* Search Section */
.search-section {
  background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
  padding: 48px 24px;
}

.search-container {
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.search-title {
  font-size: 32px;
  font-weight: 700;
  color: white;
  margin-bottom: 24px;
}

.search-box {
  position: relative;
  display: flex;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
}

.search-icon {
  position: absolute;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 20px;
  color: #adb5bd;
}

.search-box input {
  flex: 1;
  padding: 18px 20px 18px 52px;
  border: none;
  font-size: 16px;
  outline: none;
}

.search-box input::placeholder {
  color: #adb5bd;
}

.search-btn {
  padding: 0 32px;
  background: #ff6b35;
  color: white;
  border: none;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.search-btn:hover {
  background: #e55a2b;
}

.category-tabs {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 10px 20px;
  background: rgba(255,255,255,0.2);
  border: 2px solid transparent;
  border-radius: 24px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn:hover {
  background: rgba(255,255,255,0.3);
}

.tab-btn.active {
  background: white;
  color: #ff6b35;
}

/* Main Content */
.main-content {
  padding: 32px 24px;
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #212529;
}

.sort-options {
  display: flex;
  gap: 8px;
}

.sort-options button {
  padding: 8px 16px;
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 20px;
  font-size: 14px;
  color: #6c757d;
  cursor: pointer;
  transition: all 0.2s;
}

.sort-options button:hover,
.sort-options button.active {
  background: #ff6b35;
  color: white;
  border-color: #ff6b35;
}

/* Merchant Grid */
.merchant-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.merchant-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.merchant-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.1);
}

.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.merchant-card:hover .card-image img {
  transform: scale(1.05);
}

.delivery-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  background: rgba(0,0,0,0.7);
  color: white;
  font-size: 12px;
  border-radius: 20px;
}

.card-content {
  padding: 16px;
}

.merchant-name {
  font-size: 18px;
  font-weight: 700;
  color: #212529;
  margin-bottom: 6px;
}

.merchant-desc {
  font-size: 14px;
  color: #6c757d;
  margin-bottom: 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.merchant-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.rating {
  display: flex;
  align-items: center;
  gap: 6px;
}

.rating .score {
  font-size: 16px;
  font-weight: 700;
  color: #ffb800;
}

.rating .sales {
  font-size: 13px;
  color: #adb5bd;
}

.delivery-info .time {
  font-size: 13px;
  color: #28a745;
  font-weight: 500;
}

.merchant-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  padding: 4px 10px;
  background: #f8f9fa;
  color: #6c757d;
  font-size: 12px;
  border-radius: 12px;
}

.tag.highlight {
  background: #fff3cd;
  color: #856404;
}

/* Skeleton */
.skeleton {
  pointer-events: none;
}

.skeleton-img {
  height: 180px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
}

.skeleton-content {
  padding: 16px;
}

.skeleton-line {
  height: 18px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 4px;
  margin-bottom: 10px;
}

.skeleton-line.short {
  width: 60%;
  height: 14px;
}

@keyframes skeleton-loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .search-title {
    font-size: 24px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .merchant-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }
}

@media (max-width: 480px) {
  .header-container {
    padding: 12px 16px;
  }
  
  .search-section {
    padding: 32px 16px;
  }
  
  .search-title {
    font-size: 20px;
  }
  
  .merchant-grid {
    grid-template-columns: 1fr;
  }
}
</style>