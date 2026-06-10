<script setup>
import { onMounted, ref } from 'vue'
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

onMounted(() => {
  loadCategories()
  if (route.query.category) {
    setTimeout(() => {
      selectCategory(Number(route.query.category))
    }, 100)
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

const selectCategory = async (categoryId) => {
  selectedCategory.value = categoryId
  selectedDocument.value = null
  articles.value = []
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
    selectDocument(documents.value[0].id)
  }
  loading.value = false
}

const selectDocument = async (documentId) => {
  selectedDocument.value = documentId
  const document = documents.value.find((item) => item.id === documentId)
  if (document) selectedDocumentName.value = document.name
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
            v-for="article in articles"
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
  min-height: calc(100vh - 70px);
  padding: 1rem;
  position: relative;
}

.loading-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1e88e5;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.three-tier-layout {
  display: grid;
  grid-template-columns: 200px 250px 1fr;
  gap: 1rem;
  height: calc(100vh - 110px);
  max-width: 1400px;
  margin: 0 auto;
}

.tier {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
}

.tier h3 {
  padding: 1rem;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  margin: 0;
  font-size: 1rem;
  color: #303133;
}

.category-list,
.document-list {
  flex: 1;
  list-style: none;
  padding: 0.5rem;
  overflow-y: auto;
}

.category-list li,
.document-list li {
  padding: 0.75rem;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
  font-size: 0.9rem;
}

.category-list li:hover,
.document-list li:hover {
  background: #f5f7fa;
}

.category-list li.active,
.document-list li.active {
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
}

.tier-articles {
  display: flex;
  flex-direction: column;
}

.document-header {
  padding: 1rem;
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  border-bottom: 1px solid #1565c0;
}

.document-title {
  font-size: 1.1rem;
  font-weight: bold;
  color: white;
}

.article-list {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
}

.article-item {
  background: #fafafa;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1rem;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #e4e7ed;
}

.article-item:hover {
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  transform: translateX(5px);
}

.article-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.article-no {
  font-weight: bold;
  color: #1e88e5;
  font-size: 1rem;
}

.article-title {
  font-size: 0.85rem;
  color: #67c23a;
}

.article-content {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
  line-height: 1.6;
}

.empty-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.modal-overlay {
  position: fixed;
  inset: 0;
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
  max-width: 760px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
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

.article-detail {
  padding: 1.5rem;
}

.detail-row {
  margin-bottom: 1rem;
  display: flex;
  gap: 0.5rem;
}

.detail-label {
  font-weight: bold;
  color: #666;
  min-width: 90px;
}

.detail-value {
  color: #333;
}

.content-row {
  flex-direction: column;
}

.content-value {
  white-space: pre-wrap;
  word-break: break-all;
  color: #333;
  line-height: 1.8;
}

.readonly-section {
  margin-top: 1.2rem;
  padding: 1rem;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #f8fbff;
}

.section-title {
  margin-bottom: 0.75rem;
  color: #172033;
  font-weight: 800;
}

.empty-line {
  color: #909399;
  line-height: 1.7;
}

.admin-comment-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.admin-comment-item {
  padding: 0.8rem;
  border: 1px solid #edf1f7;
  border-radius: 8px;
  background: #ffffff;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 0.5rem;
  color: #667085;
  font-size: 0.85rem;
}

.comment-meta strong {
  color: #172033;
}

.admin-comment-item p {
  margin: 0;
  color: #344054;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
