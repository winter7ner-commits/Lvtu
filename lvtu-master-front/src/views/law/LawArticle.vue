<script setup>
import { ref, onMounted } from 'vue'
import { getArticles, createArticle, updateArticle, deleteArticle, getDocuments } from '../../api/law'

const articles = ref([])
const documents = ref([])
const loading = ref(false)
const showModal = ref(false)
const isEditing = ref(false)
const currentItem = ref({ id: null, documentId: null, articleNumber: '', title: '', content: '', sortOrder: 0 })

const loadDocuments = async () => {
  try {
    const result = await getDocuments()
    if (result.code === 200) {
      documents.value = result.data || []
    }
  } catch (error) {
    console.error('加载文件失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const result = await getArticles()
    if (result.code === 200) {
      articles.value = result.data || []
    }
  } catch (error) {
    console.error('加载条文失败:', error)
  }
  loading.value = false
}

onMounted(() => {
  loadDocuments()
  loadData()
})

const getDocumentName = (documentId) => {
  const doc = documents.value.find(d => d.id === documentId)
  return doc ? doc.name : '未知文件'
}

const handleAdd = () => {
  isEditing.value = false
  currentItem.value = { 
    id: null, 
    documentId: documents.value.length > 0 ? documents.value[0].id : null, 
    articleNumber: '', 
    title: '', 
    content: '', 
    sortOrder: 0 
  }
  showModal.value = true
}

const handleEdit = (item) => {
  isEditing.value = true
  currentItem.value = { ...item }
  showModal.value = true
}

const handleSave = async () => {
  if (!currentItem.value.documentId) {
    alert('请选择所属文件')
    return
  }
  if (!currentItem.value.articleNumber.trim()) {
    alert('请输入条文编号')
    return
  }
  if (!currentItem.value.content.trim()) {
    alert('请输入条文内容')
    return
  }
  try {
    if (isEditing.value && currentItem.value.id) {
      await updateArticle(currentItem.value.id, currentItem.value)
    } else {
      await createArticle({ 
        documentId: currentItem.value.documentId, 
        articleNumber: currentItem.value.articleNumber,
        title: currentItem.value.title,
        content: currentItem.value.content,
        sortOrder: currentItem.value.sortOrder 
      })
    }
    showModal.value = false
    loadData()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}

const handleDelete = async (id) => {
  if (!confirm('确定要删除这条条文吗？')) return
  try {
    await deleteArticle(id)
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
      <h1>法律条文管理</h1>
      <button class="add-btn" @click="handleAdd">添加条文</button>
    </div>
    
    <div v-if="loading" class="loading">加载中...</div>
    
    <div class="law-table-container">
      <table class="law-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>所属文件</th>
            <th>条文编号</th>
            <th>标题</th>
            <th>内容预览</th>
            <th>排序</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in articles" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ getDocumentName(item.documentId) }}</td>
            <td>{{ item.articleNumber }}</td>
            <td>{{ item.title || '-' }}</td>
            <td class="content-cell">{{ item.content.length > 50 ? item.content.substring(0, 50) + '...' : item.content }}</td>
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
      <div class="modal-content large">
        <div class="modal-header">
          <h2>{{ isEditing ? '编辑条文' : '添加条文' }}</h2>
          <button class="close-btn" @click="showModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>所属文件</label>
            <select v-model="currentItem.documentId">
              <option v-for="doc in documents" :key="doc.id" :value="doc.id">
                {{ doc.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>条文编号</label>
            <input v-model="currentItem.articleNumber" type="text" placeholder="如：第1条" />
          </div>
          <div class="form-group">
            <label>标题（选填）</label>
            <input v-model="currentItem.title" type="text" placeholder="如：宪法基本原则" />
          </div>
          <div class="form-group">
            <label>条文内容</label>
            <textarea v-model="currentItem.content" rows="8" placeholder="请输入条文内容"></textarea>
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
  max-width: 1400px;
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

.content-cell {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  max-width: 500px;
}

.modal-content.large {
  max-width: 600px;
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

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.7rem;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-family: inherit;
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