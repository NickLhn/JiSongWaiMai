<template>
  <div class="address-page">
    <header class="page-header">
      <div class="header-content">
        <button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <h1>收货地址</h1>
        <div class="placeholder"></div>
      </div>
    </header>

    <main class="address-content">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-list">
        <div v-for="i in 3" :key="i" class="skeleton-address"></div>
      </div>

      <!-- 地址列表 -->
      <div v-else-if="addresses.length > 0" class="address-list">
        <div 
          v-for="address in addresses" 
          :key="address.id" 
          :class="['address-card', { default: address.isDefault === 1 }]"
        >
          <div class="address-info" @click="selectAddress(address)">
            <div class="address-header">
              <span class="receiver-name">{{ address.receiverName }}</span>
              <span class="receiver-phone">{{ maskPhone(address.receiverPhone) }}</span>
              <span v-if="address.isDefault === 1" class="default-tag">默认</span>
              <span v-if="address.label" class="label-tag">{{ address.label }}</span>
            </div>
            <p class="address-detail">
              {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}
            </p>
          </div>
          <div class="address-actions">
            <button class="action-btn" @click="editAddress(address)">
              <el-icon><Edit /></el-icon>
            </button>
            <button class="action-btn delete" @click="deleteAddress(address.id)">
              <el-icon><Delete /></el-icon>
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <div class="empty-icon">
          <el-icon :size="64"><Location /></el-icon>
        </div>
        <h3>暂无收货地址</h3>
        <p>添加收货地址，方便下单时快速选择</p>
      </div>
    </main>

    <!-- 添加按钮 -->
    <div class="bottom-bar">
      <button class="btn-add" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        添加新地址
      </button>
    </div>

    <!-- 添加/编辑地址弹窗 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑地址' : '添加地址'"
      width="90%"
      :close-on-click-modal="false"
      class="address-dialog"
    >
      <el-form :model="addressForm" label-position="top" class="address-form">
        <el-form-item label="收货人">
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入手机号码" maxlength="11" />
        </el-form-item>
        <el-form-item label="所在地区">
          <el-input v-model="addressForm.province" placeholder="省份" style="width: 30%; margin-right: 5%;" />
          <el-input v-model="addressForm.city" placeholder="城市" style="width: 30%; margin-right: 5%;" />
          <el-input v-model="addressForm.district" placeholder="区县" style="width: 30%;" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input 
            v-model="addressForm.detailAddress" 
            type="textarea" 
            :rows="2" 
            placeholder="请输入详细地址，如门牌号、楼栋等"
          />
        </el-form-item>
        <el-form-item label="地址标签">
          <el-radio-group v-model="addressForm.label">
            <el-radio-button label="学校">学校</el-radio-button>
            <el-radio-button label="宿舍">宿舍</el-radio-button>
            <el-radio-button label="家">家</el-radio-button>
            <el-radio-button label="公司">公司</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="saveAddress">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAddresses, addAddress, updateAddress, deleteAddress as deleteAddressApi, setDefaultAddress } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Edit, Delete, Location, Plus } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const addresses = ref([])
const loading = ref(true)
const showAddDialog = ref(false)
const isEdit = ref(false)
const editingId = ref(null)

const addressForm = ref({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  label: '学校',
  isDefault: false
})

const loadAddresses = async () => {
  try {
    loading.value = true
    const res = await getAddresses()
    addresses.value = res.data || []
  } catch (error) {
    ElMessage.error('加载地址失败')
  } finally {
    loading.value = false
  }
}

const maskPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const selectAddress = (address) => {
  if (route.query.select === '1') {
    // 从下单页面过来，选择地址后返回
    router.back()
  }
}

const editAddress = (address) => {
  isEdit.value = true
  editingId.value = address.id
  addressForm.value = {
    receiverName: address.receiverName,
    receiverPhone: address.receiverPhone,
    province: address.province,
    city: address.city,
    district: address.district,
    detailAddress: address.detailAddress,
    label: address.label || '学校',
    isDefault: address.isDefault === 1
  }
  showAddDialog.value = true
}

const deleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAddressApi(id)
    ElMessage.success('删除成功')
    loadAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const saveAddress = async () => {
  if (!addressForm.value.receiverName) {
    ElMessage.warning('请输入收货人姓名')
    return
  }
  if (!addressForm.value.receiverPhone) {
    ElMessage.warning('请输入手机号码')
    return
  }
  if (!addressForm.value.detailAddress) {
    ElMessage.warning('请输入详细地址')
    return
  }

  try {
    const data = {
      ...addressForm.value,
      isDefault: addressForm.value.isDefault ? 1 : 0
    }
    
    if (isEdit.value) {
      await updateAddress(editingId.value, data)
      ElMessage.success('修改成功')
    } else {
      await addAddress(data)
      ElMessage.success('添加成功')
    }
    
    showAddDialog.value = false
    resetForm()
    loadAddresses()
  } catch (error) {
    ElMessage.error(isEdit.value ? '修改失败' : '添加失败')
  }
}

const resetForm = () => {
  isEdit.value = false
  editingId.value = null
  addressForm.value = {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    label: '学校',
    isDefault: false
  }
}

onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
.address-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 80px;
  max-width: 600px;
  margin: 0 auto;
  position: relative;
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

.placeholder {
  width: 36px;
}

.address-content {
  padding: 16px 20px;
}

/* 加载状态 */
.loading-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skeleton-address {
  height: 100px;
  background: #fff;
  border-radius: 12px;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

/* 地址列表 */
.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.address-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.address-card.default {
  border: 1px solid #667eea;
}

.address-info {
  flex: 1;
  cursor: pointer;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.receiver-phone {
  font-size: 14px;
  color: #666;
}

.default-tag {
  padding: 2px 8px;
  background: #667eea;
  color: #fff;
  font-size: 12px;
  border-radius: 4px;
}

.label-tag {
  padding: 2px 8px;
  background: #f0f0f0;
  color: #666;
  font-size: 12px;
  border-radius: 4px;
}

.address-detail {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: #f5f5f5;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn:hover {
  background: #e0e0e0;
}

.action-btn.delete:hover {
  background: #ffebee;
  color: #f44336;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  color: #2196f3;
}

.empty-state h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 8px;
}

.empty-state p {
  font-size: 14px;
  color: #999;
}

/* 底部栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 12px 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 1000;
  max-width: 600px;
  margin: 0 auto;
  box-sizing: border-box;
}

.btn-add {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-add:hover {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 弹窗样式 */
:deep(.address-dialog) {
  border-radius: 16px;
}

:deep(.address-dialog .el-dialog__header) {
  text-align: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.address-dialog .el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
}

.address-form {
  padding: 20px 0;
}

:deep(.address-form .el-form-item__label) {
  font-weight: 500;
  color: #333;
}

.dialog-footer {
  display: flex;
  gap: 12px;
}

.dialog-footer .el-button {
  flex: 1;
  height: 44px;
  border-radius: 22px;
}

.dialog-footer .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}
</style>
