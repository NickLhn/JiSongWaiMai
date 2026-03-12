<template>
  <div class="merchant-settings">
    <h2>店铺设置</h2>
    
    <div class="settings-card">
      <h3>基本信息</h3>
      <div class="form-group">
        <label>店铺名称</label>
        <input v-model="form.shopName" type="text" />
      </div>
      <div class="form-group">
        <label>店铺公告</label>
        <textarea v-model="form.notice" rows="3"></textarea>
      </div>
      <div class="form-group">
        <label>联系电话</label>
        <input v-model="form.phone" type="tel" />
      </div>
      <div class="form-group">
        <label>营业时间</label>
        <input v-model="form.businessHours" type="text" placeholder="如: 07:00-21:00" />
      </div>
    </div>
    
    <div class="settings-card">
      <h3>配送设置</h3>
      <div class="form-group">
        <label>配送费 (元)</label>
        <input v-model="form.deliveryFee" type="number" />
      </div>
      <div class="form-group">
        <label>起送金额 (元)</label>
        <input v-model="form.minOrderAmount" type="number" />
      </div>
      <div class="form-group">
        <label>预计配送时间 (分钟)</label>
        <input v-model="form.deliveryTime" type="number" />
      </div>
    </div>
    
    <button class="save-btn" @click="saveSettings">保存设置</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const form = ref({
  shopName: '美味餐厅',
  notice: '欢迎光临，新鲜现做，美味又实惠！',
  phone: '400-123-4567',
  businessHours: '07:00-21:00',
  deliveryFee: 0,
  minOrderAmount: 15,
  deliveryTime: 30
})

const saveSettings = () => {
  ElMessage.success('设置保存成功')
}
</script>

<style scoped>
.merchant-settings {
  padding: 0;
}

.merchant-settings h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1f1f1f;
  margin: 0 0 24px;
}

.settings-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.settings-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f1f1f;
  margin: 0 0 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #595959;
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #ff6b35;
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.form-group textarea {
  resize: vertical;
}

.save-btn {
  width: 100%;
  padding: 16px;
  background: #ff6b35;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.save-btn:hover {
  background: #e55a2b;
}
</style>
