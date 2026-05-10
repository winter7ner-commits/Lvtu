<script setup>import { ref, onMounted } from 'vue';
import axios from 'axios';
const API_BASE = 'http://localhost:8081/api';
const categories = ref([]);
const documents = ref([]);
const articles = ref([]);
const selectedCategory = ref(null);
const selectedDocument = ref(null);
const isModalOpen = ref(false);
const isEditing = ref(false);
const currentArticle = ref({
 id: null,
 documentId: null,
 articleNo: '',
 chapterTitle: '',
 content: '',
 sortOrder: 0
});
const loading = ref(false);
const apiError = ref('');
onMounted(() => {
 loadCategories();
 loadDocuments();
 loadArticles();
});
const loadCategories = async () => {
 try {
 const response = await axios.get(`${API_BASE}/categories`);
 console.log('分类数据:', response.data);
 if (response.data.code === 200) {
 categories.value = response.data.data || [];
 }
 }
 catch (error) {
 console.error('加载分类失败:', error);
 apiError.value = '加载分类失败，请检查后端服务';
 }
};
const loadDocuments = async () => {
 try {
 const response = await axios.get(`${API_BASE}/documents`);
 console.log('文件数据:', response.data);
 if (response.data.code === 200) {
 documents.value = response.data.data || [];
 }
 }
 catch (error) {
 console.error('加载文件失败:', error);
 apiError.value = '加载文件失败，请检查后端服务';
 }
};
const loadArticles = async () => {
 loading.value = true;
 apiError.value = '';
 try {
 const response = await axios.get(`${API_BASE}/articles`);
 console.log('条文数据:', response.data);
 if (response.data.code === 200) {
 articles.value = response.data.data || [];
 }
 else {
 apiError.value = '获取数据失败: ' + (response.data.message || '未知错误');
 }
 }
 catch (error) {
 console.error('加载条文失败:', error);
 apiError.value = '加载条文失败，请检查后端服务是否运行';
 }
 loading.value = false;
};
const openAddModal = () => {
 isEditing.value = false;
 currentArticle.value = {
 id: null,
 documentId: selectedDocument.value || (documents.value.length > 0 ? documents.value[0].id : null),
 articleNo: '',
 chapterTitle: '',
 content: '',
 sortOrder: 0
 };
 isModalOpen.value = true;
};
const openEditModal = (article) => {
 isEditing.value = true;
 currentArticle.value = { ...article };
 isModalOpen.value = true;
};
const closeModal = () => {
 isModalOpen.value = false;
};
const saveArticle = async () => {
 if (!currentArticle.value.documentId) {
 alert('请选择所属文件');
 return;
 }
 if (!currentArticle.value.articleNo.trim()) {
 alert('请输入条文编号');
 return;
 }
 try {
 if (isEditing.value) {
 await axios.put(`${API_BASE}/articles/${currentArticle.value.id}`, currentArticle.value);
 }
 else {
 await axios.post(`${API_BASE}/articles`, currentArticle.value);
 }
 closeModal();
 loadArticles();
 }
 catch (error) {
 console.error('保存条文失败:', error);
 alert('保存失败');
 }
};
const deleteArticle = async (id) => {
 if (!confirm('确定要删除这条记录吗？')) {
 return;
 }
 try {
 await axios.delete(`${API_BASE}/articles/${id}`);
 loadArticles();
 }
 catch (error) {
 console.error('删除条文失败:', error);
 alert('删除失败');
 }
};
const getDocumentName = (documentId) => {
 const doc = documents.value.find(d => d.id === documentId);
 return doc ? doc.name : '未知文件';
};
</script>

<template>
  <div class="admin-container">
    <div class="admin-header">
      <h1>法规条文管理</h1>
      <button class="btn-add" @click="openAddModal">添加新条文</button>
    </div>

    <div class="admin-content">
      <!-- 错误提示 -->
      <div v-if="apiError" class="error-message">
        ⚠️ {{ apiError }}
      </div>

      <div v-if="loading" class="loading">加载中...</div>

      <table class="article-table" v-else>
        <thead>
          <tr>
            <th>ID</th>
            <th>条文编号</th>
            <th>章节标题</th>
            <th>所属文件</th>
            <th>排序</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="article in articles" :key="article.id">
            <td>{{ article.id }}</td>
            <td>{{ article.articleNo }}</td>
            <td>{{ article.chapterTitle || '-' }}</td>
            <td>{{ getDocumentName(article.documentId) }}</td>
            <td>{{ article.sortOrder }}</td>
            <td>
              <button class="btn-edit" @click="openEditModal(article)">编辑</button>
              <button class="btn-delete" @click="deleteArticle(article.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="articles.length === 0 && !loading" class="empty-state">
        <p>暂无条文记录</p>
        <p v-if="!apiError">提示：请先启动后端服务，数据将自动加载</p>
      </div>
    </div>

    <div v-if="isModalOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ isEditing ? '编辑条文' : '添加条文' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>所属文件</label>
            <select v-model="currentArticle.documentId">
              <option v-for="doc in documents" :key="doc.id" :value="doc.id">
                {{ doc.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>条文编号</label>
            <input type="text" v-model="currentArticle.articleNo" placeholder="如：第1条" />
          </div>
          <div class="form-group">
            <label>章节标题</label>
            <input type="text" v-model="currentArticle.chapterTitle" placeholder="如：第一章 总则" />
          </div>
          <div class="form-group">
            <label>条文内容</label>
            <textarea v-model="currentArticle.content" rows="6" placeholder="请输入条文内容"></textarea>
          </div>
          <div class="form-group">
            <label>排序号</label>
            <input type="number" v-model="currentArticle.sortOrder" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeModal">取消</button>
          <button class="btn-save" @click="saveArticle">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-container {
  min-height: calc(100vh - 120px);
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.admin-header h1 {
  font-size: 1.8rem;
  color: #1e88e5;
}

.btn-add {
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-add:hover {
  box-shadow: 0 2px 10px rgba(30, 136, 229, 0.4);
}

.admin-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  padding: 1.5rem;
}

.error-message {
  background: #fef2f2;
  border: 1px solid #fee2e2;
  color: #dc2626;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.article-table {
  width: 100%;
  border-collapse: collapse;
}

.article-table th,
.article-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #e4e7ed;
}

.article-table th {
  background: #f5f7fa;
  font-weight: 600;
  color: #303133;
}

.article-table tbody tr:hover {
  background: #f5f7fa;
}

.btn-edit,
.btn-delete {
  padding: 0.4rem 0.8rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 0.5rem;
  font-size: 0.9rem;
}

.btn-edit {
  background: #409eff;
  color: white;
}

.btn-edit:hover {
  background: #66b1ff;
}

.btn-delete {
  background: #f56c6c;
  color: white;
}

.btn-delete:hover {
  background: #f78989;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #999;
}

.empty-state p {
  margin: 0.5rem 0;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
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
  color: #666;
}

.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #1e88e5;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding: 1.5rem;
  border-top: 1px solid #e4e7ed;
}

.btn-cancel,
.btn-save {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
}

.btn-cancel {
  background: #909399;
  color: white;
}

.btn-cancel:hover {
  background: #a6a9ad;
}

.btn-save {
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
}

.btn-save:hover {
  box-shadow: 0 2px 10px rgba(30, 136, 229, 0.4);
}
</style>