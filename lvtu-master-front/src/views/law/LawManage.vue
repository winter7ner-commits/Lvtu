<script setup>
import { computed, onMounted, ref, watch } from 'vue'
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
const documentPage = ref(1)
const articlePage = ref(1)

const PAGE_SIZE = 9

const panelTitle = computed(() => {
  if (activePanel.value === 'documents') return `法律文件（${documents.value.length}个）`
  if (activePanel.value === 'articles') return `法律条文（${articles.value.length}个）`
  return `法律分类（${categories.value.length}个）`
})

const documentName = (id) => documents.value.find((item) => item.id === id)?.name || '-'
const categoryName = (id) => categories.value.find((item) => item.id === id)?.name || '-'
const pageCount = (total) => Math.max(1, Math.ceil(total / PAGE_SIZE))
const paginationItems = (current, total) => {
  if (total <= 7) {
    return Array.from({ length: total }, (_, index) => ({
      key: `page-${index + 1}`,
      type: 'page',
      page: index + 1
    }))
  }

  const items = [{ key: 'page-1', type: 'page', page: 1 }]
  const start = Math.max(2, current - 1)
  const end = Math.min(total - 1, current + 1)

  if (start > 2) {
    items.push({
      key: 'ellipsis-prev',
      type: 'ellipsis',
      direction: 'prev',
      page: Math.max(1, current - 5)
    })
  }

  for (let page = start; page <= end; page += 1) {
    items.push({ key: `page-${page}`, type: 'page', page })
  }

  if (end < total - 1) {
    items.push({
      key: 'ellipsis-next',
      type: 'ellipsis',
      direction: 'next',
      page: Math.min(total, current + 5)
    })
  }

  items.push({ key: `page-${total}`, type: 'page', page: total })
  return items
}
const pageRange = (total, page) => {
  if (!total) return { start: 0, end: 0 }
  return {
    start: (page - 1) * PAGE_SIZE + 1,
    end: Math.min(page * PAGE_SIZE, total)
  }
}

const documentPageCount = computed(() => pageCount(documents.value.length))
const articlePageCount = computed(() => pageCount(articles.value.length))
const pagedDocuments = computed(() => {
  const start = (documentPage.value - 1) * PAGE_SIZE
  return documents.value.slice(start, start + PAGE_SIZE)
})
const pagedArticles = computed(() => {
  const start = (articlePage.value - 1) * PAGE_SIZE
  return articles.value.slice(start, start + PAGE_SIZE)
})
const documentRange = computed(() => pageRange(documents.value.length, documentPage.value))
const articleRange = computed(() => pageRange(articles.value.length, articlePage.value))

const setActivePanel = (panel) => {
  activePanel.value = panel
}

const setPage = (type, page) => {
  if (type === 'documents') {
    documentPage.value = Math.min(Math.max(page, 1), documentPageCount.value)
  } else {
    articlePage.value = Math.min(Math.max(page, 1), articlePageCount.value)
  }
}

watch(documents, () => {
  documentPage.value = Math.min(documentPage.value, documentPageCount.value)
})

watch(articles, () => {
  articlePage.value = Math.min(articlePage.value, articlePageCount.value)
})

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
    <section class="page-head">
      <div>
        <h1>法律法规管理</h1>
        <p>维护法律分类、文件目录和条文内容，保持前台法条检索数据准确。</p>
      </div>
      <div class="actions">
        <router-link class="action-link browse" to="/law-browse">浏览条文</router-link>
        <button class="action-btn blue" type="button" @click="openAdd('category')">添加分类</button>
        <button class="action-btn green" type="button" @click="openAdd('document')">添加文件</button>
        <button class="action-btn yellow" type="button" @click="openAdd('article')">添加条文</button>
      </div>
    </section>

    <section class="content-card law-manage-card">
      <div class="law-card-head">
        <div>
          <div class="card-title">{{ panelTitle }}</div>
          <p v-if="activePanel === 'categories'" class="panel-desc">用于组织法律文件的基础目录。</p>
          <p v-else-if="activePanel === 'documents'" class="panel-desc">每页显示 9 个法律文件，可在下方翻页。</p>
          <p v-else class="panel-desc">每页显示 9 条法律条文，可在下方翻页。</p>
        </div>
        <div class="segment">
          <button type="button" :class="{ active: activePanel === 'categories' }" @click="setActivePanel('categories')">分类</button>
          <button type="button" :class="{ active: activePanel === 'documents' }" @click="setActivePanel('documents')">文件</button>
          <button type="button" :class="{ active: activePanel === 'articles' }" @click="setActivePanel('articles')">条文</button>
        </div>
      </div>

      <div v-if="loading" class="loading">加载中...</div>

      <template v-else-if="activePanel === 'categories'">
        <div class="table-wrap">
          <table class="data-table law-data-table">
            <thead>
              <tr><th>ID</th><th>分类名称</th><th>排序</th><th>操作</th></tr>
            </thead>
            <tbody>
              <tr v-if="categories.length === 0">
                <td colspan="4" class="empty">暂无分类</td>
              </tr>
              <template v-else>
                <tr v-for="item in categories" :key="item.id">
                  <td>{{ item.id }}</td>
                  <td class="name-cell">{{ item.name }}</td>
                  <td>{{ item.sortOrder }}</td>
                  <td class="action-cell">
                    <button class="edit-btn" type="button" @click="openEdit('category', item)">编辑</button>
                    <button class="delete-btn" type="button" @click="removeItem('category', item.id)">删除</button>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
      </template>

      <template v-else-if="activePanel === 'documents'">
        <div class="table-wrap">
          <table class="data-table law-data-table">
            <thead>
              <tr><th>ID</th><th>文件名称</th><th>所属分类</th><th>发布日期</th><th>排序</th><th>操作</th></tr>
            </thead>
            <tbody>
              <tr v-if="documents.length === 0">
                <td colspan="6" class="empty">暂无法律文件</td>
              </tr>
              <template v-else>
                <tr v-for="item in pagedDocuments" :key="item.id">
                  <td>{{ item.id }}</td>
                  <td class="name-cell">{{ item.name }}</td>
                  <td>{{ categoryName(item.categoryId) }}</td>
                  <td>{{ item.publishDate || '-' }}</td>
                  <td>{{ item.sortOrder }}</td>
                  <td class="action-cell">
                    <button class="edit-btn" type="button" @click="openEdit('document', item)">编辑</button>
                    <button class="delete-btn" type="button" @click="removeItem('document', item.id)">删除</button>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
        <div class="pagination-bar">
          <div class="pagination-actions" aria-label="法律文件分页">
            <button class="pager-arrow" type="button" :disabled="documentPage === 1" @click="setPage('documents', documentPage - 1)">&lt;</button>
            <template v-for="item in paginationItems(documentPage, documentPageCount)" :key="item.key">
              <button
                v-if="item.type === 'page'"
                type="button"
                class="pager-page"
                :class="{ active: item.page === documentPage }"
                @click="setPage('documents', item.page)"
              >
                {{ item.page }}
              </button>
              <button
                v-else
                type="button"
                :class="['pager-ellipsis', `is-${item.direction}`]"
                @click="setPage('documents', item.page)"
              >
                <span>...</span>
              </button>
            </template>
            <button class="pager-arrow" type="button" :disabled="documentPage === documentPageCount" @click="setPage('documents', documentPage + 1)">&gt;</button>
          </div>
          <span class="pagination-summary">第 {{ documentRange.start }}-{{ documentRange.end }} 条，共 {{ documents.length }} 条</span>
        </div>
      </template>

      <template v-else>
        <div class="table-wrap">
          <table class="data-table law-data-table article-table">
            <thead>
              <tr><th>ID</th><th>所属文件</th><th>条文编号</th><th>章节</th><th>内容</th><th>排序</th><th>操作</th></tr>
            </thead>
            <tbody>
              <tr v-if="articles.length === 0">
                <td colspan="7" class="empty">暂无法律条文</td>
              </tr>
              <template v-else>
                <tr v-for="item in pagedArticles" :key="item.id">
                  <td>{{ item.id }}</td>
                  <td>{{ documentName(item.documentId) }}</td>
                  <td>{{ item.articleNumber }}</td>
                  <td>{{ item.title || '-' }}</td>
                  <td class="ellipsis">{{ item.content }}</td>
                  <td>{{ item.sortOrder }}</td>
                  <td class="action-cell">
                    <button class="edit-btn" type="button" @click="openEdit('article', item)">编辑</button>
                    <button class="delete-btn" type="button" @click="removeItem('article', item.id)">删除</button>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
        <div class="pagination-bar">
          <div class="pagination-actions" aria-label="法律条文分页">
            <button class="pager-arrow" type="button" :disabled="articlePage === 1" @click="setPage('articles', articlePage - 1)">&lt;</button>
            <template v-for="item in paginationItems(articlePage, articlePageCount)" :key="item.key">
              <button
                v-if="item.type === 'page'"
                type="button"
                class="pager-page"
                :class="{ active: item.page === articlePage }"
                @click="setPage('articles', item.page)"
              >
                {{ item.page }}
              </button>
              <button
                v-else
                type="button"
                :class="['pager-ellipsis', `is-${item.direction}`]"
                @click="setPage('articles', item.page)"
              >
                <span>...</span>
              </button>
            </template>
            <button class="pager-arrow" type="button" :disabled="articlePage === articlePageCount" @click="setPage('articles', articlePage + 1)">&gt;</button>
          </div>
          <span class="pagination-summary">第 {{ articleRange.start }}-{{ articleRange.end }} 条，共 {{ articles.length }} 条</span>
        </div>
      </template>
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
.page-head p {
  margin: 8px 0 0;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.action-btn,
.action-link {
  min-height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--admin-radius-sm);
  padding: 8px 14px;
  font-size: 14px;
  font-weight: 800;
  line-height: 1.2;
  text-decoration: none;
  cursor: pointer;
}

.browse {
  border: 1px solid var(--admin-border);
  background: #ffffff;
  color: var(--admin-text-secondary);
}

.blue,
.green,
.yellow {
  border: 1px solid var(--admin-primary);
  background: var(--admin-primary);
  color: #ffffff;
}

.law-manage-card {
  padding: 0;
  overflow: hidden;
}

.law-card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 20px 22px;
  border-bottom: 1px solid var(--admin-divider);
  background: #ffffff;
}

.card-title {
  margin: 0;
}

.panel-desc {
  margin: 6px 0 0;
  color: var(--admin-text-muted);
}

.segment {
  margin: 0;
}

.table-wrap {
  width: 100%;
  overflow-x: auto;
}

.law-data-table {
  min-width: 960px;
  table-layout: fixed;
}

.article-table {
  min-width: 1120px;
}

.law-data-table th,
.law-data-table td {
  white-space: nowrap;
}

.law-data-table th:first-child,
.law-data-table td:first-child {
  width: 86px;
}

.action-cell {
  width: 150px;
}

.name-cell {
  color: var(--admin-text);
  font-weight: 800;
}

.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
}

.edit-btn,
.delete-btn {
  margin-right: 8px;
}

.empty,
.loading {
  padding: 36px;
  color: var(--admin-text-muted);
  text-align: center;
}

.pagination-bar {
  display: grid;
  justify-items: center;
  gap: 6px;
  padding: 16px 22px 18px;
  border-top: 1px solid var(--admin-divider);
  background: #ffffff;
}

.pagination-actions {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
}

.pagination-actions button {
  width: 32px;
  height: 32px;
  border: 0;
  border-radius: 6px;
  background: transparent;
  color: var(--admin-text-secondary);
  font-weight: 800;
  line-height: 1;
  cursor: pointer;
}

.pagination-actions button:hover:not(:disabled),
.pagination-actions button.active {
  background: var(--admin-soft-blue);
  color: var(--admin-primary-text);
}

.pagination-actions button:disabled {
  color: var(--admin-text-disabled);
  cursor: not-allowed;
}

.pager-ellipsis span {
  display: inline-block;
}

.pager-ellipsis:hover span {
  font-size: 0;
}

.pager-ellipsis.is-prev:hover::after {
  content: '《';
  font-size: 14px;
}

.pager-ellipsis.is-next:hover::after {
  content: '》';
  font-size: 14px;
}

.pagination-summary {
  color: var(--admin-text-muted);
  font-size: 12px;
}

.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(15, 23, 42, 0.45);
}

.modal {
  width: min(560px, 92vw);
  max-height: 86vh;
  overflow: auto;
  border: 1px solid var(--admin-border);
  border-radius: var(--admin-radius-md);
  background: #ffffff;
  padding: 22px;
  box-shadow: 0 24px 64px rgba(15, 23, 42, 0.22);
}

.modal h2 {
  margin: 0 0 18px;
  color: var(--admin-text);
  font-size: 21px;
}

.modal label {
  display: block;
  margin-bottom: 14px;
  color: var(--admin-text-secondary);
  font-weight: 800;
}

.modal input,
.modal select,
.modal textarea {
  width: 100%;
  margin-top: 8px;
  padding: 10px 12px;
  border: 1px solid #d8dee9;
  border-radius: var(--admin-radius-sm);
  font: inherit;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

@media (max-width: 900px) {
  .law-card-head,
  .page-head {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
