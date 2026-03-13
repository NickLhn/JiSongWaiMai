<template>
  <div class="cart-page">
    <header class="page-header">
      <button class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <h1>购物车</h1>
      <button class="clear-btn" @click="clearCart" v-if="cartItems.length > 0">
        清空
      </button>
    </header>

    <main class="cart-content">
      <div v-if="loading" class="skeleton-list">
        <div v-for="i in 3" :key="i" class="skeleton-item"></div>
      </div>

      <div v-else-if="cartItems.length === 0" class="empty-cart">
        <div class="empty-icon">
          <el-icon :size="80"><ShoppingCart /></el-icon>
        </div>
        <h2>购物车是空的</h2>
        <p>快去挑选美味的食物吧</p>
        <button class="btn-primary" @click="$router.push('/home')">去逛逛</button>
      </div>

      <div v-else class="cart-list">
        <div class="merchant-info">
          <el-icon><Shop /></el-icon>
          <span>{{ merchantName }}</span>
        </div>

        <div class="cart-items">
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <img :src="item.dishImage || '/dish-default.jpg'" class="item-image" />
            <div class="item-details">
              <h3>{{ item.dishName }}</h3>
              <p class="item-price">¥{{ item.dishPrice }}</p>
            </div>
            <div class="quantity-control">
              <button class="btn-minus" @click="decreaseQuantity(item)">
                <el-icon><Minus /></el-icon>
              </button>
              <span class="quantity">{{ item.quantity }}</span>
              <button class="btn-plus" @click="increaseQuantity(item)">
                <el-icon><Plus /></el-icon>
              </button>
            </div>
          </div>
        </div>

        <div class="cart-summary">
          <div class="summary-row">
            <span>商品总价</span>
            <span>¥{{ cartTotal }}</span>
          </div>
          <div class="summary-row">
            <span>配送费</span>
            <span>¥0</span>
          </div>
          <div class="summary-row total">
            <span>合计</span>
            <span class="total-price">¥{{ cartTotal }}</span>
          </div>
        </div>
      </div>
    </main>

    <!-- 底部结算栏 -->
    <div v-show="!loading && cartItems.length > 0" class="cart-footer">
      <div class="footer-info">
        <span class="total-label">合计:</span>
        <span class="total-amount">¥{{ cartTotal }}</span>
      </div>
      <button class="checkout-btn" @click="goToCheckout">
        去结算
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList, updateCartItem, deleteCartItem, clearCart as clearCartApi } from '@/api/cart'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, ShoppingCart, Shop, Minus, Plus } from '@element-plus/icons-vue'

const router = useRouter()
const cartItems = ref([])
const loading = ref(true)

const merchantName = computed(() => {
  return cartItems.value[0]?.merchantName || ''
})

const cartTotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.dishPrice * item.quantity), 0).toFixed(2)
})

const loadCart = async () => {
  try {
    loading.value = true
    const res = await getCartList()
    cartItems.value = res.data
  } catch (error) {
    ElMessage.error('加载购物车失败')
  } finally {
    loading.value = false
  }
}

const increaseQuantity = async (item) => {
  try {
    await updateCartItem(item.id, item.quantity + 1)
    await loadCart()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const decreaseQuantity = async (item) => {
  try {
    if (item.quantity > 1) {
      await updateCartItem(item.id, item.quantity - 1)
    } else {
      await deleteCartItem(item.id)
    }
    await loadCart()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const clearCart = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await clearCartApi()
    cartItems.value = []
    ElMessage.success('购物车已清空')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

const goToCheckout = () => {
  router.push('/orders/create')
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background: #f8f9fa;
  padding-bottom: 100px;
  position: relative;
  max-width: 600px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.page-header h1 {
  font-size: 18px;
  font-weight: 600;
}

.back-btn, .clear-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border: none;
  border-radius: 12px;
  cursor: pointer;
}

.clear-btn {
  width: auto;
  padding: 0 16px;
  font-size: 14px;
  color: #ff6b35;
}

.cart-content {
  padding: 16px;
  max-width: 600px;
  margin: 0 auto;
}

.empty-cart {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  color: #dee2e6;
  margin-bottom: 20px;
}

.empty-cart h2 {
  font-size: 20px;
  color: #495057;
  margin-bottom: 8px;
}

.empty-cart p {
  color: #adb5bd;
  margin-bottom: 24px;
}

.btn-primary {
  padding: 12px 32px;
  background: #ff6b35;
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}

.merchant-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  margin-bottom: 12px;
  font-weight: 600;
}

.cart-items {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #f1f3f4;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 10px;
  object-fit: cover;
}

.item-details {
  flex: 1;
}

.item-details h3 {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 6px;
}

.item-price {
  color: #ff6b35;
  font-weight: 600;
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
}

.btn-minus {
  background: #e9ecef;
  color: #495057;
}

.btn-plus {
  background: #ff6b35;
  color: white;
}

.quantity {
  font-weight: 600;
  min-width: 24px;
  text-align: center;
}

.cart-summary {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-top: 12px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  color: #6c757d;
}

.summary-row.total {
  border-top: 1px solid #e9ecef;
  margin-top: 8px;
  padding-top: 16px;
  font-size: 18px;
  font-weight: 700;
  color: #212529;
}

.total-price {
  color: #ff6b35;
}

.cart-footer {
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
  z-index: 1000;
  max-width: 600px;
  margin: 0 auto;
  box-sizing: border-box;
}

.footer-info {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-label {
  font-size: 14px;
  color: #6c757d;
}

.total-amount {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b35;
}

.checkout-btn {
  padding: 14px 32px;
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
  transition: all 0.3s ease;
}

.checkout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 107, 53, 0.4);
}

.checkout-btn:active {
  transform: translateY(0);
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skeleton-item {
  height: 100px;
  background: linear-gradient(90deg, #e9ecef 25%, #dee2e6 50%, #e9ecef 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 12px;
}

@keyframes skeleton-loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
