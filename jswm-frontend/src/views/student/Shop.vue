<template>
  <div class="shop-page">
    <!-- Header -->
    <header class="shop-header">
      <div class="header-left">
        <button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
        </button>
      </div>
      <div class="header-center">
        <span class="shop-name">{{ merchant.shopName || '商家详情' }}</span>
      </div>
      <div class="header-right">
        <button class="favorite-btn" @click="toggleFavorite">
          <el-icon :size="20" :color="isFavorite ? '#ff6b35' : '#fff'">
            <StarFilled v-if="isFavorite" />
            <Star v-else />
          </el-icon>
        </button>
        <button class="cart-btn" @click="showCartDrawer = true">
          <el-icon><ShoppingCart /></el-icon>
          <span v-if="cartItemCount > 0" class="cart-badge">{{ cartItemCount }}</span>
        </button>
      </div>
    </header>

    <!-- Shop Info Hero -->
    <section class="shop-hero">
      <div class="hero-bg" :style="{ backgroundImage: `url(${merchant.shopLogo || '/shop-default.jpg'})` }"></div>
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <div class="shop-info-card">
          <img :src="merchant.shopLogo || '/shop-default.jpg'" class="shop-logo" />
          <div class="shop-info">
            <h1 class="shop-title">{{ merchant.shopName }}</h1>
            <div class="shop-meta">
              <span class="rating">
                <el-icon color="#ffb800"><StarFilled /></el-icon>
                {{ merchant.rating }}
              </span>
              <span class="divider">|</span>
              <span class="sales">月售{{ formatSales(merchant.sales) }}</span>
              <span class="divider">|</span>
              <span class="time">{{ merchant.businessHours }}</span>
            </div>
            <p class="shop-notice">{{ merchant.notice || '欢迎光临，美味等你！' }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Main Content -->
    <main class="shop-main">
      <div class="main-container">
        <!-- Category Sidebar -->
        <aside class="category-sidebar">
          <div
            v-for="cat in categories"
            :key="cat"
            :class="['category-item', { active: selectedCategory === cat }]"
            @click="selectedCategory = cat"
          >
            {{ cat }}
          </div>
        </aside>

        <!-- Dish List -->
        <div class="dish-list">
          <div class="category-title">{{ selectedCategory }}</div>
          
          <div v-if="loading" class="dish-skeleton-list">
            <div v-for="i in 6" :key="i" class="dish-skeleton">
              <div class="skeleton-img"></div>
              <div class="skeleton-content">
                <div class="skeleton-line"></div>
                <div class="skeleton-line short"></div>
              </div>
            </div>
          </div>

          <div v-else-if="filteredDishes.length === 0" class="empty-dishes">
            <el-empty description="暂无菜品" />
          </div>

          <div v-else class="dishes">
            <div
              v-for="dish in filteredDishes"
              :key="dish.id"
              class="dish-card"
            >
              <div class="dish-image">
                <img :src="dish.image || '/dish-default.jpg'" :alt="dish.name" />
                <div v-if="dish.isRecommend" class="recommend-badge">推荐</div>
              </div>
              
              <div class="dish-info">
                <h3 class="dish-name">{{ dish.name }}</h3>
                <p class="dish-desc">{{ dish.description }}</p>
                <div class="dish-tags">
                  <span v-if="dish.isSpicy" class="tag spicy">辣</span>
                  <span v-if="dish.isRecommend" class="tag recommend">招牌</span>
                </div>
                <div class="dish-footer">
                  <div class="dish-price">
                    <span class="currency">¥</span>
                    <span class="price">{{ dish.price }}</span>
                  </div>
                  <div class="quantity-control">
                    <button 
                      v-if="getCartQuantity(dish.id) > 0"
                      class="btn-minus"
                      @click="removeFromCart(dish)"
                    >
                      <el-icon><Minus /></el-icon>
                    </button>
                    <span v-if="getCartQuantity(dish.id) > 0" class="quantity">
                      {{ getCartQuantity(dish.id) }}
                    </span>
                    <button class="btn-plus" @click="addToCart(dish)">
                      <el-icon><Plus /></el-icon>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Cart Bar -->
    <div v-if="cartItems.length > 0" class="cart-bar">
      <div class="cart-info" @click="showCartDrawer = true">
        <div class="cart-icon">
          <el-icon><ShoppingCart /></el-icon>
          <span class="cart-count">{{ cartItemCount }}</span>
        </div>
        <div class="cart-price">
          <span class="total">¥{{ cartTotal }}</span>
          <span class="delivery">另需配送费¥0</span>
        </div>
      </div>
      <button class="checkout-btn" @click="goToCart">
        去结算
      </button>
    </div>

    <!-- Cart Drawer -->
    <transition name="slide-up">
      <div v-if="showCartDrawer" class="cart-drawer-overlay" @click="showCartDrawer = false">
        <div class="cart-drawer" @click.stop>
          <div class="drawer-header">
            <h3>已选商品</h3>
            <button class="clear-btn" @click="clearCart">
              <el-icon><Delete /></el-icon>
              清空
            </button>
          </div>
          <div class="drawer-body">
            <div v-for="item in cartItems" :key="item.id" class="cart-item">
              <img :src="item.dishImage || '/dish-default.jpg'" class="item-img" />
              <div class="item-info">
                <h4>{{ item.dishName }}</h4>
                <p class="item-price">¥{{ item.dishPrice }}</p>
              </div>
              <div class="quantity-control">
                <button class="btn-minus" @click="removeFromCart(item)">
                  <el-icon><Minus /></el-icon>
                </button>
                <span class="quantity">{{ item.quantity }}</span>
                <button class="btn-plus" @click="addToCart(item)">
                  <el-icon><Plus /></el-icon>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMerchantDetail } from '@/api/merchant'
import { getDishList } from '@/api/dish'
import { getCartList, addToCart as addCartItem, updateCartItem, deleteCartItem, clearCart as clearCartApi } from '@/api/cart'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ShoppingCart, StarFilled, Star, Plus, Minus, Delete } from '@element-plus/icons-vue'
import { addFavorite, removeFavorite, checkFavorite } from '@/api/user'

const route = useRoute()
const router = useRouter()
const merchantId = route.params.id

const merchant = ref({})
const dishes = ref([])
const cart = ref([])
const loading = ref(true)
const selectedCategory = ref('全部')
const showCartDrawer = ref(false)
const isFavorite = ref(false)

const categories = computed(() => {
  const cats = ['全部', ...new Set(dishes.value.map(d => d.category))]
  return cats
})

const filteredDishes = computed(() => {
  if (selectedCategory.value === '全部') return dishes.value
  return dishes.value.filter(d => d.category === selectedCategory.value)
})

const cartItems = computed(() => cart.value)

const cartItemCount = computed(() => {
  return cart.value.reduce((sum, item) => sum + item.quantity, 0)
})

const cartTotal = computed(() => {
  return cart.value.reduce((sum, item) => sum + (item.dishPrice * item.quantity), 0).toFixed(2)
})

const getCartQuantity = (dishId) => {
  const item = cart.value.find(c => c.dishId === dishId)
  return item ? item.quantity : 0
}

const loadMerchant = async () => {
  try {
    const res = await getMerchantDetail(merchantId)
    merchant.value = res.data
    // 检查是否已收藏
    checkFavoriteStatus()
  } catch (error) {
    ElMessage.error('加载商家信息失败')
  }
}

const checkFavoriteStatus = async () => {
  try {
    const res = await checkFavorite(merchantId)
    isFavorite.value = res.data
  } catch (error) {
    console.error('检查收藏状态失败', error)
  }
}

const toggleFavorite = async () => {
  try {
    if (isFavorite.value) {
      await removeFavorite(merchantId)
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(merchantId)
      ElMessage.success('收藏成功')
    }
    isFavorite.value = !isFavorite.value
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const loadDishes = async () => {
  try {
    loading.value = true
    const res = await getDishList({ merchantId: parseInt(merchantId), status: 1 })
    dishes.value = res.data
  } catch (error) {
    ElMessage.error('加载菜品失败')
  } finally {
    loading.value = false
  }
}

const loadCart = async () => {
  try {
    const res = await getCartList()
    cart.value = res.data.filter(item => item.merchantId === parseInt(merchantId))
  } catch (error) {
    console.error(error)
  }
}

const addToCart = async (dish) => {
  try {
    const existingItem = cart.value.find(item => item.dishId === dish.id)
    if (existingItem) {
      await updateCartItem(existingItem.id, existingItem.quantity + 1)
    } else {
      await addCartItem({
        merchantId: parseInt(merchantId),
        dishId: dish.id,
        quantity: 1
      })
    }
    await loadCart()
    ElMessage.success('已加入购物车')
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const removeFromCart = async (dish) => {
  try {
    const existingItem = cart.value.find(item => item.dishId === dish.id)
    if (existingItem) {
      if (existingItem.quantity > 1) {
        await updateCartItem(existingItem.id, existingItem.quantity - 1)
      } else {
        await deleteCartItem(existingItem.id)
      }
      await loadCart()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const clearCart = async () => {
  try {
    await clearCartApi()
    cart.value = []
    showCartDrawer.value = false
    ElMessage.success('购物车已清空')
  } catch (error) {
    ElMessage.error('清空失败')
  }
}

const goToCart = () => {
  router.push('/cart')
}

const formatSales = (sales) => {
  if (sales >= 1000) return `${(sales / 1000).toFixed(1)}k`
  return sales
}

onMounted(() => {
  loadMerchant()
  loadDishes()
  loadCart()
})
</script>

<style scoped>
.shop-page {
  min-height: 100vh;
  background: #f8f9fa;
  padding-bottom: 80px;
}

/* Header */
.shop-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.back-btn, .cart-btn, .favorite-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.back-btn:hover, .cart-btn:hover, .favorite-btn:hover {
  background: #ff6b35;
  color: white;
}

.favorite-btn {
  margin-right: 8px;
}

.cart-btn {
  position: relative;
}

.cart-badge {
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

.shop-name {
  font-size: 18px;
  font-weight: 600;
  color: #212529;
}

/* Hero */
.shop-hero {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  filter: blur(8px);
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.3), rgba(0,0,0,0.6));
}

.hero-content {
  position: relative;
  z-index: 1;
  height: 100%;
  display: flex;
  align-items: flex-end;
  padding: 20px;
}

.shop-info-card {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  width: 100%;
}

.shop-logo {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  object-fit: cover;
  border: 3px solid white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.shop-info {
  flex: 1;
  color: white;
}

.shop-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 8px;
}

.shop-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  margin-bottom: 8px;
}

.shop-meta .rating {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #ffb800;
  font-weight: 600;
}

.shop-meta .divider {
  opacity: 0.5;
}

.shop-notice {
  font-size: 13px;
  opacity: 0.9;
}

/* Main */
.shop-main {
  padding: 16px;
}

.main-container {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  gap: 16px;
}

/* Sidebar */
.category-sidebar {
  width: 100px;
  flex-shrink: 0;
  background: white;
  border-radius: 12px;
  padding: 8px;
  position: sticky;
  top: 80px;
  height: fit-content;
}

.category-item {
  padding: 12px 8px;
  text-align: center;
  font-size: 14px;
  color: #6c757d;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 4px;
}

.category-item:hover {
  background: #f8f9fa;
}

.category-item.active {
  background: #ff6b35;
  color: white;
  font-weight: 600;
}

/* Dish List */
.dish-list {
  flex: 1;
  background: white;
  border-radius: 12px;
  padding: 16px;
}

.category-title {
  font-size: 18px;
  font-weight: 700;
  color: #212529;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e9ecef;
}

.dishes {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dish-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.2s;
}

.dish-card:hover {
  background: #e9ecef;
}

.dish-image {
  position: relative;
  width: 100px;
  height: 100px;
  flex-shrink: 0;
  border-radius: 10px;
  overflow: hidden;
}

.dish-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recommend-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  padding: 4px 8px;
  background: #ff6b35;
  color: white;
  font-size: 11px;
  font-weight: 600;
  border-radius: 4px;
}

.dish-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.dish-name {
  font-size: 16px;
  font-weight: 600;
  color: #212529;
  margin-bottom: 6px;
}

.dish-desc {
  font-size: 13px;
  color: #6c757d;
  margin-bottom: 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.dish-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.tag {
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 4px;
}

.tag.spicy {
  background: #fff5f5;
  color: #dc3545;
}

.tag.recommend {
  background: #fff3cd;
  color: #856404;
}

.dish-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.dish-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.dish-price .currency {
  font-size: 14px;
  color: #ff6b35;
  font-weight: 600;
}

.dish-price .price {
  font-size: 22px;
  color: #ff6b35;
  font-weight: 700;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-minus, .btn-plus {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-minus {
  background: #e9ecef;
  color: #495057;
}

.btn-minus:hover {
  background: #dee2e6;
}

.btn-plus {
  background: #ff6b35;
  color: white;
}

.btn-plus:hover {
  background: #e55a2b;
}

.quantity {
  font-size: 16px;
  font-weight: 600;
  color: #212529;
  min-width: 24px;
  text-align: center;
}

/* Skeleton */
.dish-skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dish-skeleton {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.skeleton-img {
  width: 100px;
  height: 100px;
  background: linear-gradient(90deg, #e9ecef 25%, #dee2e6 50%, #e9ecef 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 10px;
}

.skeleton-content {
  flex: 1;
}

.skeleton-line {
  height: 16px;
  background: linear-gradient(90deg, #e9ecef 25%, #dee2e6 50%, #e9ecef 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 4px;
  margin-bottom: 12px;
}

.skeleton-line.short {
  width: 60%;
  height: 14px;
}

@keyframes skeleton-loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* Cart Bar */
.cart-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: white;
  box-shadow: 0 -4px 12px rgba(0,0,0,0.08);
  z-index: 100;
}

.cart-info {
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
}

.cart-icon {
  position: relative;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ff6b35;
  color: white;
  border-radius: 50%;
  font-size: 24px;
}

.cart-count {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 22px;
  height: 22px;
  background: #dc3545;
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-price {
  display: flex;
  flex-direction: column;
}

.cart-price .total {
  font-size: 20px;
  font-weight: 700;
  color: #ff6b35;
}

.cart-price .delivery {
  font-size: 12px;
  color: #6c757d;
}

.checkout-btn {
  padding: 14px 32px;
  background: #ff6b35;
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.checkout-btn:hover {
  background: #e55a2b;
}

/* Cart Drawer */
.cart-drawer-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  z-index: 200;
  display: flex;
  align-items: flex-end;
}

.cart-drawer {
  width: 100%;
  max-height: 70vh;
  background: white;
  border-radius: 20px 20px 0 0;
  overflow: hidden;
}

.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e9ecef;
}

.drawer-header h3 {
  font-size: 18px;
  font-weight: 600;
}

.clear-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  background: #f8f9fa;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  color: #6c757d;
  cursor: pointer;
}

.drawer-body {
  max-height: 50vh;
  overflow-y: auto;
  padding: 16px 20px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f1f3f4;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}

.item-info {
  flex: 1;
}

.item-info h4 {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}

.item-price {
  font-size: 14px;
  color: #ff6b35;
  font-weight: 600;
}

/* Transitions */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
}

.slide-up-enter-from .cart-drawer,
.slide-up-leave-to .cart-drawer {
  transform: translateY(100%);
}

@media (max-width: 600px) {
  .main-container {
    flex-direction: column;
  }
  
  .category-sidebar {
    width: 100%;
    position: static;
    display: flex;
    gap: 8px;
    overflow-x: auto;
    padding: 12px;
  }
  
  .category-item {
    white-space: nowrap;
    margin-bottom: 0;
  }
  
  .shop-hero {
    height: 160px;
  }
  
  .shop-logo {
    width: 60px;
    height: 60px;
  }
  
  .shop-title {
    font-size: 18px;
  }
}
</style>
