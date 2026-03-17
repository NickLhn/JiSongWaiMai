<template>
  <div class="merchant-settings">
    <h2>店铺设置</h2>
    
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="6" animated />
    </div>
    
    <template v-else>
      <div class="settings-card">
        <h3>基本信息</h3>
        <div class="form-group">
          <label>店铺名称</label>
          <input v-model="form.name" type="text" placeholder="请输入店铺名称" />
        </div>
        <div class="form-group">
          <label>店铺公告</label>
          <textarea v-model="form.notice" rows="3" placeholder="请输入店铺公告"></textarea>
        </div>
        <div class="form-group">
          <label>联系电话</label>
          <input v-model="form.phone" type="tel" placeholder="请输入联系电话" />
        </div>
        <div class="form-group">
          <label>营业时间</label>
          <input v-model="form.businessHours" type="text" placeholder="如: 07:00-21:00" />
        </div>
        <div class="form-group">
          <label>店铺地址</label>
          <input v-model="form.address" type="text" placeholder="请输入店铺地址" />
        </div>
      </div>
      
      <div class="settings-card">
        <h3>配送设置</h3>
        <div class="form-group">
          <label>配送费 (元)</label>
          <input v-model="form.deliveryFee" type="number" min="0" step="0.01" />
        </div>
        <div class="form-group">
          <label>起送金额 (元)</label>
          <input v-model="form.minOrderAmount" type="number" min="0" step="0.01" />
        </div>
        <div class="form-group">
          <label>预计配送时间 (分钟)</label>
          <input v-model="form.deliveryTime" type="number" min="0" />
        </div>
      </div>
      
      <button class="save-btn" :disabled="saving" @click="saveSettings">
        {{ saving ? '保存中...' : '保存设置' }}
      </button>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyMerchantInfo, updateMyMerchantInfo } from '@/api/merchant'

const loading = ref(true)
const saving = ref(false)

const form = ref({
  name: '',
  notice: '',
  phone: '',
  businessHours: '',
  address: '',
  deliveryFee: 0,
  minOrderAmount: 0,
  deliveryTime: 30
})

const loadSettings = async () => {
  try {
    loading.value = true
    const res = await getMyMerchantInfo()
    if (res.data) {
      form.value = {
        name: res.data.shopName || '',
        notice: res.data.description || '',
        phone: res.data.phone || '',
        businessHours: res.data.businessHours || '',
        address: res.data.shopAddress || '',
        deliveryFee: res.data.deliveryFee || 0,
        minOrderAmount: res.data.minOrderAmount || 0,
        deliveryTime: res.data.deliveryTime || 30
      }
    }
  } catch (error) {
    ElMessage.error('加载店铺信息失败')
  } finally {
    loading.value = false
  }
}

const saveSettings = async () => {
  if (!form.value.name) {
    ElMessage.warning('请输入店铺名称')
    return
  }

  try {
    saving.value = true
    // 将前端字段映射为后端字段
    const data = {
      shopName: form.value.name,
      description: form.value.notice,
      businessHours: form.value.businessHours,
      shopAddress: form.value.address,
      deliveryFee: form.value.deliveryFee,
      minOrderAmount: form.value.minOrderAmount,
      deliveryTime: form.value.deliveryTime
    }
    await updateMyMerchantInfo(data)
    ElMessage.success('设置保存成功')
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadSettings()
})
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

.loading-state {
  padding: 40px;
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
  color: #333;
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #ff6b35;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.save-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.save-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.4);
}

.save-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
</style>
