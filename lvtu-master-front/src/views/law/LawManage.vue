<script setup>
import { computed, onMounted, ref } from 'vue'
import {
  getCategories,
  getDocuments,
  getArticles,
  createCategory,
  createDocument,
  createArticle,
  updateCategory,
  updateDocument,
  updateArticle,
  deleteCategory,
  deleteDocument,
  deleteArticle
} from '../../api/law'

const activePanel = ref('categories')
const categories = ref([])
const documents = ref([])
const articles = ref([])
const loading = ref(false)
const modalVisible = ref(false)
const modalType = ref('category')
const isEditing = ref(false)
const form = ref({})

const panelTitle = computed(() => {
  if (activePanel.value === 'documents') return `法律文件（${documents.value.length}个）`
  if (activePanel.value === 'articles') return `法律条文（${articles.value.length}个）`
  return `法律分类（${categories.value.length}个）`
})

const documentName = (id) => documents.value.find((item) => item.id === id)?.name || '-'
const categoryName = (id) => categories.value.find((item) => item.id === id)?.name || '-'

const loadData = async () => {
  loading.value = true
  try {
    const [categoryRes, documentRes, articleRes] = await Promise.all([
      getCategories(),
      getDocuments(),
      getArticles()
    ])
    categories.value = categoryRes.code === 200 ? categoryRes.data || [] : []
    documents.value = documentRes.code === 200 ? documentRes.data || [] : []
    articles.value = articleRes.code === 200 ? articleRes.data || [] : []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

const openAdd = (type) => {
  modalType.value = type
  isEditing.value = false
  if (type === 'document') {
    form.value = { name: '', categoryId: categories.value[0]?.id || null, publishDate: '', sortOrder: 0 }
  } else if (type === 'article') {
    form.value = { documentId: documents.value[0]?.id || null, articleNumber: '', title: '', content: '', sortOrder: 0 }
  } else {
    form.value = { name: '', parentId: 0, sortOrder: 0 }
  }
  modalVisible.value = true
}

const openEdit = (type, item) => {
  modalType.value = type
  isEditing.value = true
  form.value = { ...item }
  modalVisible.value = true
}

const saveItem = async () => {
  if (modalType.value === 'document') {
    if (isEditing.value) await updateDocument(form.value.id, form.value)
    else await createDocument(form.value)
  } else if (modalType.value === 'article') {
    if (isEditing.value) await updateArticle(form.value.id, form.value)
    else await createArticle(form.value)
  } else {
    if (isEditing.value) await updateCategory(form.value.id, form.value)
    else await createCategory(form.value)
  }
  modalVisible.value = false
  loadData()
}

const removeItem = async (type, id) => {
  if (!confirm('确定删除吗？')) return
  if (type === 'document') await deleteDocument(id)
  else if (type === 'article') await deleteArticle(id)
  else await deleteCategory(id)
  loadData()
}
</script>

<template>
  <main class="page-shell">
    <div class="page-head">
      <h1>法律法规管理</h1>
      <div class="actions">
        <router-link class="action-link browse" to="/law-browse">浏览条文</router-link>
        <button class="action-btn blue" @click="openAdd('category')">添加分类</button>
        <button class="action-btn green" @click="openAdd('document')">添加文件</button>
        <button class="action-btn yellow" @click="openAdd('article')">添加条文</button>
      </div>
    </div>

    <section class="content-card">
      <div class="card-title">
        <span>📁 {{ panelTitle }}</span>
        <div class="segment">
          <button :class="{ active: activePanel === 'categories' }" @click="activePanel = 'categories'">分类</button>
          <button :class="{ active: activePanel === 'documents' }" @click="activePanel = 'documents'">文件</button>
          <button :class="{ active: activePanel === 'articles' }" @click="activePanel = 'articles'">条文</button>
        </div>
      </div>

      <div v-if="loading" class="loading">加载中...</div>

      <table v-else-if="activePanel === 'categories'" class="data-table">
        <thead>
          <tr><th>ID</th><th>分类名称</th><th>排序</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="item in categories" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.name }}</td>
            <td>{{ item.sortOrder }}</td>
            <td>
              <button class="edit-btn" @click="openEdit('category', item)">编辑</button>
              <button class="delete-btn" @click="removeItem('category', item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <table v-else-if="activePanel === 'documents'" class="data-table">
        <thead>
          <tr><th>ID</th><th>文件名称</th><th>所属分类</th><th>发布日期</th><th>排序</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="item in documents" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.name }}</td>
            <td>{{ categoryName(item.categoryId) }}</td>
            <td>{{ item.publishDate || '-' }}</td>
            <td>{{ item.sortOrder }}</td>
            <td>
              <button class="edit-btn" @click="openEdit('document', item)">编辑</button>
              <button class="delete-btn" @click="removeItem('document', item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <table v-else class="data-table">
        <thead>
          <tr><th>ID</th><th>所属文件</th><th>条文编号</th><th>章节</th><th>内容</th><th>排序</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="item in articles" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ documentName(item.documentId) }}</td>
            <td>{{ item.articleNumber }}</td>
            <td>{{ item.title || '-' }}</td>
            <td class="ellipsis">{{ item.content }}</td>
            <td>{{ item.sortOrder }}</td>
            <td>
              <button class="edit-btn" @click="openEdit('article', item)">编辑</button>
              <button class="delete-btn" @click="removeItem('article', item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <div v-if="modalVisible" class="modal-mask" @click.self="modalVisible = false">
      <div class="modal">
        <h2>{{ isEditing ? '编辑' : '添加' }}</h2>
        <template v-if="modalType === 'category'">
          <label>分类名称<input v-model="form.name" /></label>
          <label>排序号<input v-model.number="form.sortOrder" type="number" /></label>
        </template>
        <template v-else-if="modalType === 'document'">
          <label>文件名称<input v-model="form.name" /></label>
          <label>所属分类<select v-model="form.categoryId"><option v-for="item in categories" :key="item.id" :value="item.id">{{ item.name }}</option></select></label>
          <label>发布日期<input v-model="form.publishDate" type="date" /></label>
          <label>排序号<input v-model.number="form.sortOrder" type="number" /></label>
        </template>
        <template v-else>
          <label>所属文件<select v-model="form.documentId"><option v-for="item in documents" :key="item.id" :value="item.id">{{ item.name }}</option></select></label>
          <label>条文编号<input v-model="form.articleNumber" /></label>
          <label>章节标题<input v-model="form.title" /></label>
          <label>条文内容<textarea v-model="form.content" rows="6"></textarea></label>
          <label>排序号<input v-model.number="form.sortOrder" type="number" /></label>
        </template>
        <div class="modal-actions">
          <button class="cancel-btn" @click="modalVisible = false">取消</button>
          <button class="save-btn" @click="saveItem">保存</button>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.page-shell { padding: 48px 46px; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 38px; }
.page-head h1 { margin: 0; color: #1d8fd6; font-size: 31px; font-weight: 800; }
.actions { display: flex; gap: 18px; }
.action-btn, .action-link { border: 0; border-radius: 6px; color: #fff; padding: 17px 40px; font-size: 18px; font-weight: 700; cursor: pointer; box-shadow: 0 5px 12px rgba(0, 0, 0, 0.12); text-decoration: none; line-height: 1; }
.browse { background: linear-gradient(135deg, #64748b, #334155); }
.blue { background: linear-gradient(135deg, #1d94e8, #086bc1); }
.green { background: linear-gradient(135deg, #67c83d, #35a619); }
.yellow { background: linear-gradient(135deg, #f4bd4b, #eea733); }
.content-card { background: #fff; border-radius: 6px; padding: 42px; box-shadow: 0 5px 18px rgba(30, 55, 90, 0.08); }
.card-title { display: flex; justify-content: space-between; align-items: center; margin-bottom: 26px; font-size: 23px; font-weight: 800; }
.segment { display: flex; gap: 8px; }
.segment button { border: 1px solid #d6e1f0; background: #fff; border-radius: 4px; padding: 8px 14px; cursor: pointer; }
.segment button.active { color: #fff; background: #1d8fd6; border-color: #1d8fd6; }
.data-table { width: 100%; border-collapse: collapse; table-layout: fixed; }
.data-table th { background: #f7f7fb; font-size: 19px; text-align: left; padding: 20px; }
.data-table td { border-top: 1px solid #e6e8ef; padding: 18px 20px; font-size: 17px; }
.ellipsis { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.edit-btn, .delete-btn { border: 0; color: #fff; border-radius: 4px; padding: 10px 18px; margin-right: 10px; cursor: pointer; font-size: 15px; }
.edit-btn { background: #409eff; }
.delete-btn { background: #ef6b6b; }
.loading { padding: 36px; color: #666; text-align: center; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,.45); display: flex; align-items: center; justify-content: center; z-index: 200; }
.modal { width: 560px; max-height: 86vh; overflow: auto; background: #fff; border-radius: 8px; padding: 24px; }
.modal h2 { margin: 0 0 18px; color: #1d8fd6; }
.modal label { display: block; margin-bottom: 14px; font-weight: 700; }
.modal input, .modal select, .modal textarea { width: 100%; margin-top: 8px; padding: 10px; border: 1px solid #d7dce5; border-radius: 4px; font: inherit; }
.modal-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; }
.cancel-btn, .save-btn { border: 0; border-radius: 4px; color: #fff; padding: 10px 20px; cursor: pointer; }
.cancel-btn { background: #909399; }
.save-btn { background: #1d8fd6; }
</style>
