<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail, getArticlesByDocument, getCategories, getDocumentsByCategory } from '../../api/law'

const route = useRoute()

const categories = ref([])
const documents = ref([])
const articles = ref([])
const selectedCategory = ref(null)
const selectedDocument = ref(null)
const selectedDocumentName = ref('')
const articleModalVisible = ref(false)
const selectedArticle = ref(null)
const selectedArticleDetail = ref(null)
const loading = ref(false)
const detailLoading = ref(false)
const articlePage = ref(1)

const BROWSE_PAGE_SIZE = 6
const DEFAULT_CATEGORY_NAME = '宪法及宪法相关法'
const DEFAULT_DOCUMENT_NAME = '中华人民共和国宪法'

const pageCount = (total) => Math.max(1, Math.ceil(total / BROWSE_PAGE_SIZE))
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

const articlePageCount = computed(() => pageCount(articles.value.length))
const pagedArticles = computed(() => {
  const start = (articlePage.value - 1) * BROWSE_PAGE_SIZE
  return articles.value.slice(start, start + BROWSE_PAGE_SIZE)
})

const setArticlePage = (page) => {
  articlePage.value = Math.min(Math.max(page, 1), articlePageCount.value)
}

watch(articles, () => {
  articlePage.value = 1
})

const findByName = (items, targetName) => {
  if (!targetName) return null
  return items.find((item) => item.name === targetName)
    || items.find((item) => String(item.name || '').includes(targetName))
}

const selectDefaultLegalDocument = async () => {
  if (!categories.value.length) return
  const targetCategory = findByName(categories.value, DEFAULT_CATEGORY_NAME) || categories.value[0]
  await selectCategory(targetCategory.id, null, DEFAULT_DOCUMENT_NAME)
}

onMounted(async () => {
  await loadCategories()
  if (route.query.category) {
    await selectCategory(
      Number(route.query.category),
      route.query.document ? Number(route.query.document) : null
    )
  } else {
    await selectDefaultLegalDocument()
  }
})

const loadCategories = async () => {
  loading.value = true
  try {
    const result = await getCategories()
    categories.value = result.code === 200 ? result.data || [] : []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
  loading.value = false
}

const selectCategory = async (categoryId, preferredDocumentId = null, preferredDocumentName = '') => {
  selectedCategory.value = categoryId
  selectedDocument.value = null
  articles.value = []
  articlePage.value = 1
  selectedDocumentName.value = ''
  loading.value = true

  try {
    const result = await getDocumentsByCategory(categoryId)
    documents.value = result.code === 200 ? result.data || [] : []
  } catch (error) {
    console.error('加载文件失败:', error)
    documents.value = []
  }

  if (documents.value.length > 0) {
    const targetDocument = documents.value.find((item) => item.id === preferredDocumentId)
      || findByName(documents.value, preferredDocumentName)
      || documents.value[0]
    await selectDocument(targetDocument.id)
  }
  loading.value = false
}

const selectDocument = async (documentId) => {
  selectedDocument.value = documentId
  const document = documents.value.find((item) => item.id === documentId)
  if (document) selectedDocumentName.value = document.name
  articlePage.value = 1
  loading.value = true

  try {
    const result = await getArticlesByDocument(documentId)
    articles.value = result.code === 200 ? result.data || [] : []
  } catch (error) {
    console.error('加载条文失败:', error)
    articles.value = []
  }
  loading.value = false
}

const truncateContent = (content) => {
  if (content && content.length > 100) return `${content.substring(0, 100)}...`
  return content || ''
}

const openArticleModal = async (article) => {
  selectedArticle.value = article
  selectedArticleDetail.value = null
  articleModalVisible.value = true
  detailLoading.value = true
  try {
    const result = await getArticleDetail(article.id)
    selectedArticleDetail.value = result.code === 200 ? result.data : null
  } catch (error) {
    console.error('加载法条详情失败:', error)
  } finally {
    detailLoading.value = false
  }
}
</script>

<template>
  <div class="law-article-container">
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div class="three-tier-layout">
      <div class="tier tier-categories">
        <h3>法规分类</h3>
        <ul class="category-list">
          <li
            v-for="category in categories"
            :key="category.id"
            :class="{ active: selectedCategory === category.id }"
            @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </li>
        </ul>
      </div>

      <div class="tier tier-documents">
        <h3>法律法规文件</h3>
        <ul class="document-list">
          <li
            v-for="document in documents"
            :key="document.id"
            :class="{ active: selectedDocument === document.id }"
            @click="selectDocument(document.id)"
          >
            {{ document.name }}
          </li>
        </ul>
      </div>

      <div class="tier tier-articles">
        <h3>法条内容</h3>
        <div v-if="selectedDocumentName" class="document-header">
          <span class="document-title">{{ selectedDocumentName }}</span>
        </div>
        <div v-if="articles.length > 0" class="article-list">
          <div
            v-for="article in pagedArticles"
            :key="article.id"
            class="article-item"
            @click="openArticleModal(article)"
          >
            <div class="article-header">
              <span class="article-no">{{ article.articleNumber }}</span>
              <span v-if="article.title" class="article-title">{{ article.title }}</span>
            </div>
            <p class="article-content">{{ truncateContent(article.content) }}</p>
          </div>
        </div>
        <div v-if="articles.length > 0" class="pagination-bar">
          <div class="pagination-actions" aria-label="法条浏览分页">
            <button class="pager-arrow" type="button" :disabled="articlePage === 1" @click="setArticlePage(articlePage - 1)">&lt;</button>
            <template v-for="item in paginationItems(articlePage, articlePageCount)" :key="item.key">
              <button
                v-if="item.type === 'page'"
                type="button"
                class="pager-page"
                :class="{ active: item.page === articlePage }"
                @click="setArticlePage(item.page)"
              >
                {{ item.page }}
              </button>
              <button
                v-else
                type="button"
                :class="['pager-ellipsis', `is-${item.direction}`]"
                @click="setArticlePage(item.page)"
              >
                <span>...</span>
              </button>
            </template>
            <button class="pager-arrow" type="button" :disabled="articlePage === articlePageCount" @click="setArticlePage(articlePage + 1)">&gt;</button>
          </div>
        </div>
        <div v-else class="empty-state">
          <p>请选择一个法律法规文件查看条文内容</p>
        </div>
      </div>
    </div>

    <div v-if="articleModalVisible" class="modal-overlay" @click.self="articleModalVisible = false">
      <div class="modal-content" v-loading="detailLoading">
        <div class="modal-header">
          <h2>法条详情</h2>
          <button class="close-btn" @click="articleModalVisible = false">×</button>
        </div>
        <div v-if="selectedArticle" class="article-detail">
          <div class="detail-row">
            <span class="detail-label">所属文件：</span>
            <span class="detail-value">{{ selectedDocumentName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">条文编号：</span>
            <span class="detail-value">{{ selectedArticle.articleNumber }}</span>
          </div>
          <div v-if="selectedArticle.title" class="detail-row">
            <span class="detail-label">标题：</span>
            <span class="detail-value">{{ selectedArticle.title }}</span>
          </div>
          <section class="readonly-section">
            <div class="section-title">条文原文</div>
            <div class="content-value">{{ selectedArticle.content }}</div>
          </section>

          <section class="readonly-section">
            <div class="section-title">解释</div>
            <div v-if="selectedArticleDetail?.explanation?.content" class="content-value">
              {{ selectedArticleDetail.explanation.content }}
            </div>
            <div v-else class="empty-line">暂无解释</div>
          </section>

          <section class="readonly-section">
            <div class="section-title">评论（{{ selectedArticleDetail?.comments?.length || 0 }}）</div>
            <div v-if="selectedArticleDetail?.comments?.length" class="admin-comment-list">
              <article v-for="comment in selectedArticleDetail.comments" :key="comment.id" class="admin-comment-item">
                <div class="comment-meta">
                  <strong>{{ comment.username || `用户${comment.userId}` }}</strong>
                  <span>{{ comment.createdAt ? String(comment.createdAt).replace('T', ' ').slice(0, 16) : '-' }}</span>
                </div>
                <p>{{ comment.content }}</p>
              </article>
            </div>
            <div v-else class="empty-line">暂无评论</div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.law-article-container {
  position: relative;
  height: 100vh;
  min-height: 640px;
  overflow: hidden;
  padding: 18px 20px;
}

.loading-overlay {
  position: absolute;
  inset: 0;
  z-index: 100;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(245, 247, 251, 0.72);
}

.loading-spinner {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: conic-gradient(var(--admin-primary), rgba(37, 99, 235, 0));
  mask: radial-gradient(farthest-side, transparent calc(100% - 4px), #000 0);
  animation: spin 0.9s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.three-tier-layout {
  display: grid;
  grid-template-columns: 250px 300px minmax(0, 1fr);
  gap: 14px;
  height: 100%;
  max-width: none;
  margin: 0;
}

.tier {
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid var(--admin-border);
  border-radius: var(--admin-radius-md);
  background: #ffffff;
}

.tier h3 {
  flex: 0 0 auto;
  margin: 0;
  padding: 18px 20px;
  border-bottom: 1px solid var(--admin-divider);
  background: var(--admin-panel);
  color: var(--admin-text);
  font-size: 16px;
  font-weight: 800;
}

.category-list,
.document-list {
  flex: 1;
  min-height: 0;
  margin: 0;
  padding: 14px;
  overflow-y: auto;
  list-style: none;
}

.category-list li,
.document-list li {
  min-height: 42px;
  display: flex;
  align-items: center;
  padding: 9px 12px;
  border-radius: var(--admin-radius-sm);
  color: var(--admin-text-secondary);
  cursor: pointer;
  font-size: 14px;
  font-weight: 700;
  transition: background-color 0.18s ease, color 0.18s ease;
}

.category-list li + li,
.document-list li + li {
  margin-top: 6px;
}

.category-list li:hover,
.document-list li:hover {
  background: var(--admin-panel);
  color: var(--admin-primary-text);
}

.category-list li.active,
.document-list li.active {
  background: var(--admin-primary);
  color: #ffffff;
}

.tier-articles {
  min-width: 0;
}

.document-header {
  flex: 0 0 auto;
  padding: 18px 20px;
  border-bottom: 1px solid var(--admin-primary-deep);
  background: var(--admin-primary);
}

.document-title {
  color: #ffffff;
  font-size: 18px;
  font-weight: 900;
}

.article-list {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 16px;
  background: #ffffff;
}

.article-item {
  padding: 16px 18px;
  border: 1px solid var(--admin-divider);
  border-radius: var(--admin-radius-md);
  background: #ffffff;
  cursor: pointer;
  transition: background-color 0.18s ease, border-color 0.18s ease;
}

.article-item + .article-item {
  margin-top: 12px;
}

.article-item:hover {
  border-color: var(--admin-soft-blue-border);
  background: #fbfdff;
}

.article-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 8px;
}

.article-no {
  color: var(--admin-primary-text);
  font-size: 16px;
  font-weight: 900;
}

.article-title {
  color: var(--admin-success);
  font-size: 13px;
  font-weight: 800;
}

.article-content {
  margin: 0;
  color: var(--admin-text-secondary);
  font-size: 14px;
  line-height: 1.7;
}

.pagination-bar {
  flex: 0 0 auto;
  display: flex;
  justify-content: center;
  padding: 12px 16px 16px;
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

.empty-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  color: var(--admin-text-muted);
}

.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(15, 23, 42, 0.45);
}

.modal-content {
  width: min(760px, 92vw);
  max-height: 82vh;
  overflow-y: auto;
  border: 1px solid var(--admin-border);
  border-radius: var(--admin-radius-md);
  background: #ffffff;
  box-shadow: 0 24px 64px rgba(15, 23, 42, 0.22);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--admin-divider);
}

.modal-header h2 {
  margin: 0;
  color: var(--admin-text);
  font-size: 21px;
  font-weight: 800;
}

.close-btn {
  width: 34px;
  height: 34px;
  border: 1px solid var(--admin-border);
  border-radius: var(--admin-radius-sm);
  background: #ffffff;
  color: var(--admin-text-secondary);
  cursor: pointer;
  font-size: 20px;
}

.article-detail {
  padding: 20px;
}

.detail-row {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.detail-label {
  min-width: 90px;
  color: var(--admin-text-muted);
  font-weight: 800;
}

.detail-value {
  color: var(--admin-text-secondary);
}

.content-value {
  color: var(--admin-text-secondary);
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}

.readonly-section {
  margin-top: 14px;
  padding: 14px;
  border: 1px solid var(--admin-divider);
  border-radius: var(--admin-radius-md);
  background: var(--admin-panel);
}

.section-title {
  margin-bottom: 10px;
  color: var(--admin-text);
  font-weight: 800;
}

.empty-line {
  color: var(--admin-text-muted);
  line-height: 1.7;
}

.admin-comment-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.admin-comment-item {
  padding: 12px;
  border: 1px solid var(--admin-divider);
  border-radius: var(--admin-radius-md);
  background: #ffffff;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
  color: var(--admin-text-muted);
  font-size: 12px;
}

.comment-meta strong {
  color: var(--admin-text);
}

.admin-comment-item p {
  margin: 0;
  color: var(--admin-text-secondary);
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

@media (max-width: 1100px) {
  .law-article-container {
    overflow-x: auto;
  }

  .three-tier-layout {
    min-width: 980px;
  }
}
</style>
