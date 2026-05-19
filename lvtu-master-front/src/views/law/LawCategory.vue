<script setup>
import { ref, onMounted } from 'vue'
import { getCategories, createCategory, updateCategory, deleteCategory } from '../../api/law'

const categories = ref([])
const loading = ref(false)
const showModal = ref(false)
const isEditing = ref(false)
const currentItem = ref({ id: null, name: '', sortOrder: 0 })

const loadData = async () => {
  loading.value = true
  try {
    const result = await getCategories()
    if (result.code === 200) {
      categories.value = result.data || []
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
  loading.value = false
}

onMounted(() => loadData())

const handleAdd = () => {
  isEditing.value = false
  currentItem.value = { id: null, name: '', sortOrder: 0 }
  showModal.value = true
}

const handleEdit = (item) => {
  isEditing.value = true
  currentItem.value = { ...item }
  showModal.value = true
}

const handleSave = async () => {
  if (!currentItem.value.name.trim()) {
    alert('请输入分类名称')
    return
  }
  try {
    if (isEditing.value && currentItem.value.id) {
      await updateCategory(currentItem.value.id, currentItem.value)
    } else {
      await createCategory({ name: currentItem.value.name, sortOrder: currentItem.value.sortOrder })
    }
    showModal.value = false
    loadData()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}

const handleDelete = async (id) => {
  if (!confirm('确定要删除这个分类吗？')) return
  try {
    await deleteCategory(id)
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败')
  }
}
</script>

<template>
  <div class="law-container">
    <div class="law-header">
      <h1>法律分类管理</h1>
      <button class="add-btn" @click="handleAdd">添加分类</button>
    </div>
    
    <div v-if="loading" class="loading">加载中...</div>
    
    <div class="law-table-container">
      <table class="law-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>分类名称</th>
            <th>排序</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in categories" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.name }}</td>
            <td>{{ item.sortOrder }}</td>
            <td>
              <button class="edit-btn" @click="handleEdit(item)">编辑</button>
              <button class="delete-btn" @click="handleDelete(item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ isEditing ? '编辑分类' : '添加分类' }}</h2>
          <button class="close-btn" @click="showModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>分类名称</label>
            <input v-model="currentItem.name" type="text" placeholder="请输入分类名称" />
          </div>
          <div class="form-group">
            <label>排序号</label>
            <input v-model.number="currentItem.sortOrder" type="number" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showModal = false">取消</button>
          <button class="save-btn" @click="handleSave">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.law-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.law-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.law-header h1 {
  color: #1e88e5;
}

.add-btn {
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  cursor: pointer;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.law-table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.law-table {
  width: 100%;
  border-collapse: collapse;
}

.law-table th,
.law-table td {
  padding: 0.8rem;
  text-align: left;
  border-bottom: 1px solid #e4e7ed;
}

.law-table th {
  background: #f5f7fa;
  font-weight: 600;
}

.edit-btn,
.delete-btn {
  padding: 0.4rem 0.8rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 0.5rem;
}

.edit-btn {
  background: #409eff;
  color: white;
}

.delete-btn {
  background: #f56c6c;
  color: white;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.2rem;
  border-bottom: 1px solid #e4e7ed;
}

.modal-header h2 {
  margin: 0;
  color: #1e88e5;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.modal-body {
  padding: 1.2rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 0.7rem;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding: 1.2rem;
  border-top: 1px solid #e4e7ed;
}

.cancel-btn,
.save-btn {
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background: #909399;
  color: white;
}

.save-btn {
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
}
</style>