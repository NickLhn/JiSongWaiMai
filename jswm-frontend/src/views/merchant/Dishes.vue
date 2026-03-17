<template>
  <div class="merchant-dishes">
    <div class="page-header">
      <h2>菜品管理</h2>
      <button class="add-btn" @click="openAddModal">
        <el-icon><Plus /></el-icon>
        添加菜品
      </button>
    </div>
    
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="3" animated />
    </div>
    
    <div v-else-if="dishes.length === 0" class="empty-state">
      <el-icon :size="64"><Food /></el-icon>
      <p>暂无菜品，点击上方按钮添加</p>
    </div>
    
    <div v-else class="dishes-grid">
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
            <button class="delete-btn" @click="deleteDish(dish)">删除</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑菜品弹窗 -->
    <el-dialog
      v-model="showModal"
      :title="isEdit ? '编辑菜品' : '添加菜品'"
      width="500px"
      destroy-on-close
    >
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="菜品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜品名称" />
        </el-form-item>
        
        <el-form-item label="菜品描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入菜品描述"
          />
        </el-form-item>
        
        <el-form-item label="价格" prop="price">
          <el-input-number 
            v-model="form.price" 
            :min="0" 
            :precision="2"
            :step="1"
            style="width: 200px"
          />
          <span class="unit">元</span>
        </el-form-item>
        
        <el-form-item label="菜品图片">
          <el-upload
            class="avatar-uploader"
            :http-request="customUpload"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <img v-if="form.image" :src="form.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">在售</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          {{ isEdit ? '保存' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Food } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyDishes, addDish, updateDish, deleteDish as deleteDishApi } from '@/api/dish'
import { uploadImage } from '@/api/upload'

const dishes = ref([])
const loading = ref(false)
const showModal = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = ref({
  id: null,
  name: '',
  description: '',
  price: 0,
  image: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const loadDishes = async () => {
  try {
    loading.value = true
    const res = await getMyDishes()
    if (res.code === 200) {
      dishes.value = res.data || []
    } else {
      ElMessage.error(res.message || '加载菜品失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '加载菜品失败')
  } finally {
    loading.value = false
  }
}

const openAddModal = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    description: '',
    price: 0,
    image: '',
    status: 1
  }
  showModal.value = true
}

const editDish = (dish) => {
  isEdit.value = true
  form.value = { ...dish }
  showModal.value = true
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    submitting.value = true
    if (isEdit.value) {
      await updateDish(form.value.id, form.value)
      ElMessage.success('修改成功')
    } else {
      await addDish(form.value)
      ElMessage.success('添加成功')
    }
    showModal.value = false
    loadDishes()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const toggleStatus = async (dish) => {
  try {
    const newStatus = dish.status === 1 ? 0 : 1
    await updateDish(dish.id, { ...dish, status: newStatus })
    dish.status = newStatus
    ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteDish = async (dish) => {
  try {
    await ElMessageBox.confirm('确定要删除该菜品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteDishApi(dish.id)
    ElMessage.success('删除成功')
    loadDishes()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const customUpload = async (options) => {
  try {
    const res = await uploadImage(options.file)
    if (res.code === 200) {
      form.value.image = res.data
      ElMessage.success('上传成功')
      options.onSuccess(res)
    } else {
      ElMessage.error(res.message || '上传失败')
      options.onError(new Error(res.message))
    }
  } catch (error) {
    ElMessage.error('上传失败')
    options.onError(error)
  }
}

const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    ElMessage.error('只支持 JPG/PNG 格式!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

onMounted(() => {
  loadDishes()
})
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
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.4);
}

.loading-state {
  padding: 40px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #999;
}

.empty-state .el-icon {
  margin-bottom: 16px;
  color: #ddd;
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
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
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

.dish-actions .edit-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.dish-actions .toggle-btn:hover {
  border-color: #52c41a;
  color: #52c41a;
}

.dish-actions .delete-btn:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.unit {
  margin-left: 8px;
  color: #666;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #ff6b35;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c8c8c;
}

.avatar {
  width: 120px;
  height: 120px;
  object-fit: cover;
}
</style>
