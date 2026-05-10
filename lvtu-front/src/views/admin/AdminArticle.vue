<script setup>
import { ref, onMounted } from 'vue';

const API_BASE = 'http://localhost:8081/api';
const categories = ref([]);
const documents = ref([]);
const articles = ref([]);
const isModalOpen = ref(false);
const modalType = ref('category');
const isEditing = ref(false);
const currentItem = ref(null);
const loading = ref(false);
const apiError = ref('');

const categoryForm = ref({ id: null, name: '' });
const documentForm = ref({ id: null, name: '', categoryId: null, publishDate: '' });
const articleForm = ref({ id: null, documentId: null, articleNumber: '', title: '', content: '', sortOrder: 0 });

onMounted(() => {
  loadCategories();
  loadDocuments();
  loadArticles();
});

const loadCategories = async () => {
  try {
    const response = await fetch(`${API_BASE}/categories`);
    const data = await response.json();
    if (data.code === 200) {
      categories.value = data.data || [];
    }
  } catch (error) {
    console.error('加载分类失败:', error);
    apiError.value = '加载分类失败，请检查后端服务';
  }
};

const loadDocuments = async () => {
  try {
    const response = await fetch(`${API_BASE}/documents`);
    const data = await response.json();
    if (data.code === 200) {
      documents.value = data.data || [];
    }
  } catch (error) {
    console.error('加载文件失败:', error);
    apiError.value = '加载文件失败，请检查后端服务';
  }
};

const loadArticles = async () => {
  loading.value = true;
  apiError.value = '';
  try {
    const response = await fetch(`${API_BASE}/articles`);
    const data = await response.json();
    if (data.code === 200) {
      articles.value = data.data || [];
    } else {
      apiError.value = '获取数据失败: ' + (data.message || '未知错误');
    }
  } catch (error) {
    console.error('加载条文失败:', error);
    apiError.value = '加载条文失败，请检查后端服务是否运行';
  }
  loading.value = false;
};

const getCategoryName = (categoryId) => {
  const cat = categories.value.find(c => c.id === categoryId);
  return cat ? cat.name : '未知分类';
};

const getDocumentName = (documentId) => {
  const doc = documents.value.find(d => d.id === documentId);
  return doc ? doc.name : '未知文件';
};

// 分类操作
const openAddCategory = () => {
  modalType.value = 'category';
  isEditing.value = false;
  categoryForm.value = { id: null, name: '' };
  isModalOpen.value = true;
};

const openEditCategory = (category) => {
  modalType.value = 'category';
  isEditing.value = true;
  categoryForm.value = { ...category };
  isModalOpen.value = true;
};

const saveCategory = async () => {
  if (!categoryForm.value.name.trim()) {
    alert('请输入分类名称');
    return;
  }
  try {
    const url = isEditing.value
      ? `${API_BASE}/categories/${categoryForm.value.id}`
      : `${API_BASE}/categories`;
    const method = isEditing.value ? 'PUT' : 'POST';
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(categoryForm.value)
    });
    const data = await response.json();
    if (data.code === 200) {
      closeModal();
      loadCategories();
    } else {
      alert('保存失败: ' + (data.message || '未知错误'));
    }
  } catch (error) {
    console.error('保存分类失败:', error);
    alert('保存失败');
  }
};

const deleteCategory = async (id) => {
  if (!confirm('确定要删除这个分类吗？')) return;
  try {
    await fetch(`${API_BASE}/categories/${id}`, { method: 'DELETE' });
    loadCategories();
  } catch (error) {
    console.error('删除分类失败:', error);
    alert('删除失败');
  }
};

// 文件操作
const openAddDocument = () => {
  modalType.value = 'document';
  isEditing.value = false;
  documentForm.value = { 
    id: null, 
    name: '', 
    categoryId: categories.value.length > 0 ? categories.value[0].id : null, 
    publishDate: '' 
  };
  isModalOpen.value = true;
};

const openEditDocument = (doc) => {
  modalType.value = 'document';
  isEditing.value = true;
  documentForm.value = { 
    id: doc.id, 
    name: doc.name, 
    categoryId: doc.categoryId,
    publishDate: doc.publishDate || '' 
  };
  isModalOpen.value = true;
};

const saveDocument = async () => {
  if (!documentForm.value.name.trim()) {
    alert('请输入文件名称');
    return;
  }
  try {
    const url = isEditing.value
      ? `${API_BASE}/documents/${documentForm.value.id}`
      : `${API_BASE}/documents`;
    const method = isEditing.value ? 'PUT' : 'POST';
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(documentForm.value)
    });
    const data = await response.json();
    if (data.code === 200) {
      closeModal();
      loadDocuments();
    } else {
      alert('保存失败: ' + (data.message || '未知错误'));
    }
  } catch (error) {
    console.error('保存文件失败:', error);
    alert('保存失败');
  }
};

const deleteDocument = async (id) => {
  if (!confirm('确定要删除这个文件吗？')) return;
  try {
    await fetch(`${API_BASE}/documents/${id}`, { method: 'DELETE' });
    loadDocuments();
  } catch (error) {
    console.error('删除文件失败:', error);
    alert('删除失败');
  }
};

// 法条操作
const openAddArticle = () => {
  modalType.value = 'article';
  isEditing.value = false;
  articleForm.value = { 
    id: null, 
    documentId: documents.value.length > 0 ? documents.value[0].id : null, 
    articleNumber: '', 
    title: '', 
    content: '', 
    sortOrder: 0 
  };
  isModalOpen.value = true;
};

const openEditArticle = (article) => {
  modalType.value = 'article';
  isEditing.value = true;
  articleForm.value = { ...article };
  isModalOpen.value = true;
};

const saveArticle = async () => {
  if (!articleForm.value.documentId) {
    alert('请选择所属文件');
    return;
  }
  if (!articleForm.value.articleNumber.trim()) {
    alert('请输入条文编号');
    return;
  }
  try {
    const url = isEditing.value
      ? `${API_BASE}/articles/${articleForm.value.id}`
      : `${API_BASE}/articles`;
    const method = isEditing.value ? 'PUT' : 'POST';
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(articleForm.value)
    });
    const data = await response.json();
    if (data.code === 200) {
      closeModal();
      loadArticles();
    } else {
      alert('保存失败: ' + (data.message || '未知错误'));
    }
  } catch (error) {
    console.error('保存条文失败:', error);
    alert('保存失败');
  }
};

const deleteArticle = async (id) => {
  if (!confirm('确定要删除这条记录吗？')) return;
  try {
    await fetch(`${API_BASE}/articles/${id}`, { method: 'DELETE' });
    loadArticles();
  } catch (error) {
    console.error('删除条文失败:', error);
    alert('删除失败');
  }
};

const closeModal = () => {
  isModalOpen.value = false;
};
</script>

<template>
  <div class="admin-container">
    <div class="admin-header">
      <h1>法律法规管理</h1>
      <div class="header-buttons">
        <button class="btn-add" @click="openAddCategory">添加分类</button>
        <button class="btn-add btn-document" @click="openAddDocument">添加文件</button>
        <button class="btn-add btn-article" @click="openAddArticle">添加条文</button>
      </div>
    </div>

    <div class="admin-content">
      <div v-if="apiError" class="error-message">⚠️ {{ apiError }}</div>
      <div v-if="loading" class="loading">加载中...</div>

      <!-- 分类管理 -->
      <div class="section">
        <h2>📁 法律分类（{{ categories.length }}个）</h2>
        <table class="article-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>分类名称</th>
              <th>排序</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cat in categories" :key="cat.id">
              <td>{{ cat.id }}</td>
              <td>{{ cat.name }}</td>
              <td>{{ cat.sortOrder }}</td>
              <td>
                <button class="btn-edit" @click="openEditCategory(cat)">编辑</button>
                <button class="btn-delete" @click="deleteCategory(cat.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 文件管理 -->
      <div class="section">
        <h2>📄 法律文件（{{ documents.length }}个）</h2>
        <table class="article-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>文件名称</th>
              <th>所属分类</th>
              <th>发布日期</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="doc in documents" :key="doc.id">
              <td>{{ doc.id }}</td>
              <td>{{ doc.name }}</td>
              <td>{{ getCategoryName(doc.categoryId) }}</td>
              <td>{{ doc.publishDate }}</td>
              <td>
                <button class="btn-edit" @click="openEditDocument(doc)">编辑</button>
                <button class="btn-delete" @click="deleteDocument(doc.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 条文管理 -->
      <div class="section">
        <h2>📜 法律条文（{{ articles.length }}条）</h2>
        <table class="article-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>条文编号</th>
              <th>标题</th>
              <th>所属文件</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="article in articles" :key="article.id">
              <td>{{ article.id }}</td>
              <td>{{ article.articleNumber }}</td>
              <td>{{ article.title || '-' }}</td>
              <td>{{ getDocumentName(article.documentId) }}</td>
              <td>
                <button class="btn-edit" @click="openEditArticle(article)">编辑</button>
                <button class="btn-delete" @click="deleteArticle(article.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 模态框 -->
    <div v-if="isModalOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>
            {{ modalType === 'category' ? (isEditing ? '编辑分类' : '添加分类') : '' }}
            {{ modalType === 'document' ? (isEditing ? '编辑文件' : '添加文件') : '' }}
            {{ modalType === 'article' ? (isEditing ? '编辑条文' : '添加条文') : '' }}
          </h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <!-- 分类表单 -->
          <div v-if="modalType === 'category'" class="form-group">
            <label>分类名称</label>
            <input type="text" v-model="categoryForm.name" placeholder="请输入分类名称" />
          </div>

          <!-- 文件表单 -->
          <div v-if="modalType === 'document'" class="form-group">
            <label>文件名称</label>
            <input type="text" v-model="documentForm.name" placeholder="请输入文件名称" />
          </div>
          <div v-if="modalType === 'document'" class="form-group">
            <label>所属分类</label>
            <select v-model="documentForm.categoryId">
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
          </div>
          <div v-if="modalType === 'document'" class="form-group">
            <label>发布日期</label>
            <input type="date" v-model="documentForm.publishDate" />
          </div>

          <!-- 条文表单 -->
          <div v-if="modalType === 'article'" class="form-group">
            <label>所属文件</label>
            <select v-model="articleForm.documentId">
              <option v-for="doc in documents" :key="doc.id" :value="doc.id">
                {{ doc.name }}
              </option>
            </select>
          </div>
          <div v-if="modalType === 'article'" class="form-group">
            <label>条文编号</label>
            <input type="text" v-model="articleForm.articleNumber" placeholder="如：第1条" />
          </div>
          <div v-if="modalType === 'article'" class="form-group">
            <label>标题</label>
            <input type="text" v-model="articleForm.title" placeholder="如：宪法基本原则" />
          </div>
          <div v-if="modalType === 'article'" class="form-group">
            <label>条文内容</label>
            <textarea v-model="articleForm.content" rows="6" placeholder="请输入条文内容"></textarea>
          </div>
          <div v-if="modalType === 'article'" class="form-group">
            <label>排序号</label>
            <input type="number" v-model="articleForm.sortOrder" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeModal">取消</button>
          <button class="btn-save" @click="modalType === 'category' ? saveCategory() : (modalType === 'document' ? saveDocument() : saveArticle())">
            保存
          </button>
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

.header-buttons {
  display: flex;
  gap: 1rem;
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

.btn-document {
  background: linear-gradient(135deg, #67c23a 0%, #5daf34 100%);
}

.btn-article {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.admin-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  padding: 1.5rem;
}

.section {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 2px solid #e4e7ed;
}

.section:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.section h2 {
  font-size: 1.2rem;
  color: #303133;
  margin-bottom: 1rem;
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
  padding: 0.75rem;
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
  font-size: 0.85rem;
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
  max-width: 500px;
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