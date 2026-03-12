<template>
  <div class="merchant-dishes">
    <div class="page-header">
      <h2>菜品管理</h2>
      <button class="add-btn" @click="showAddModal = true">
        <el-icon><Plus /></el-icon>
        添加菜品
      </button>
    </div>
    
    <div class="dishes-grid">
      <div v-for="dish in dishes" :key="dish.id" class="dish-card">
        <img :src="dish.image || '/dish-default.jpg'" class="dish-img" />
        <div class="dish-info">
          <h3>{{ dish.name }}</h3>
          <p class="desc">{{ dish.description }}</p>
          <div class="dish-meta">
            <span class="price">¥{{ dish.price }}</span>
            <span :class="['status', dish.status === 1 ? 'on' : 'off']">
              {{ dish.status === 1 ? '在售' : '下架' }}
            </span>
          </div>
          <div class="dish-actions">
            <button class="edit-btn" @click="editDish(dish)">编辑</button>
            <button class="toggle-btn" @click="toggleStatus(dish)">
              {{ dish.status === 1 ? '下架' : '上架' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'

const showAddModal = ref(false)
const dishes = ref([
  { id: 1, name: '红烧肉盖饭', description: '精选五花肉，肥而不腻', price: 15, status: 1, image: '/images/dish/dish1.jpg' },
  { id: 2, name: '宫保鸡丁饭', description: '经典川菜，香辣可口', price: 13, status: 1, image: '/images/dish/dish2.jpg' },
  { id: 3, name: '番茄鸡蛋面', description: '家常味道，营养丰富', price: 10, status: 1, image: '/images/dish/dish3.jpg' },
  { id: 4, name: '炸鸡腿', description: '外酥里嫩，香脆可口', price: 8, status: 0, image: '/images/dish/dish4.jpg' }
])

const editDish = (dish) => {
  console.log('编辑菜品:', dish)
}

const toggleStatus = (dish) => {
  dish.status = dish.status === 1 ? 0 : 1
}
</script>

<style scoped>
.merchant-dishes {
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

.add-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
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

.dishes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.dish-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.dish-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.dish-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.dish-info {
  padding: 16px;
}

.dish-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f1f1f;
  margin: 0 0 8px;
}

.desc {
  font-size: 13px;
  color: #8c8c8c;
  margin: 0 0 12px;
  line-height: 1.5;
}

.dish-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.price {
  font-size: 20px;
  font-weight: 700;
  color: #ff6b35;
}

.status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status.on {
  background: #f6ffed;
  color: #52c41a;
}

.status.off {
  background: #fff1f0;
  color: #ff4d4f;
}

.dish-actions {
  display: flex;
  gap: 8px;
}

.dish-actions button {
  flex: 1;
  padding: 8px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.dish-actions button:hover {
  border-color: #ff6b35;
  color: #ff6b35;
}
</style>
