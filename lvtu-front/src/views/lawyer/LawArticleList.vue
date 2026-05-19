<script setup>
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const categories = ref([])
const documents = ref([])
const articles = ref([])
const selectedCategory = ref(null)
const selectedDocument = ref(null)
const selectedDocumentName = ref('')
const articleModalVisible = ref(false)
const selectedArticle = ref(null)
const loading = ref(false)

const searchType = ref('name')
const searchKeyword = ref('')
const searchResults = ref([])
const showSearchResults = ref(false)
const searchBarVisible = ref(true)
const searchInputRef = ref(null)
const searchError = ref('')

const suggestions = ref([])
const showSuggestions = ref(false)

const API_BASE = 'http://localhost:8080/api'

const suggestionKeywords = [
  '民法', '刑法', '合同法', '婚姻法', '劳动法',
  '公司法', '知识产权', '诉讼', '仲裁', '赔偿',
  '合同', '侵权', '继承', '房产', '债务'
]

const smartSuggestions = computed(() => {
  if (!searchKeyword.value.trim()) {
    return suggestionKeywords.slice(0, 6)
  }
  const keyword = searchKeyword.value.toLowerCase()
  return suggestionKeywords.filter(k =>
      k.toLowerCase().includes(keyword) || keyword.includes(k.toLowerCase())
  ).slice(0, 6)
})

watch(searchKeyword, (newVal) => {
  if (newVal.trim()) {
    showSuggestions.value = true
    showSearchResults.value = false
    searchError.value = ''
  } else {
    showSuggestions.value = false
    showSearchResults.value = false
    searchError.value = ''
  }
})

const handleKeywordInput = () => {
  if (searchKeyword.value.trim()) {
    showSuggestions.value = true
    showSearchResults.value = false
    searchError.value = ''
  } else {
    showSuggestions.value = false
  }
}

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    showSearchResults.value = false
    searchResults.value = []
    showSuggestions.value = false
    searchError.value = ''
    return
  }

  loading.value = true
  searchError.value = ''
  try {
    let url = ''
    if (searchType.value === 'name') {
      url = `${API_BASE}/documents/search?name=${encodeURIComponent(searchKeyword.value)}`
    } else {
      url = `${API_BASE}/articles/search?keyword=${encodeURIComponent(searchKeyword.value)}`
    }

    const response = await fetch(url)
    if (response.ok) {
      const result = await response.json()
      if (result.code === 200) {
        searchResults.value = result.data || []
        showSearchResults.value = true
      } else {
        searchResults.value = []
        searchError.value = result.message || '搜索失败'
        showSearchResults.value = true
      }
    } else {
      searchResults.value = []
      searchError.value = '搜索请求失败'
      showSearchResults.value = true
    }
  } catch (error) {
    console.error('搜索失败:', error)
    searchResults.value = []
    searchError.value = '搜索失败，请稍后重试'
    showSearchResults.value = true
  }
  showSuggestions.value = false
  loading.value = false
}

const clearSearch = () => {
  searchKeyword.value = ''
  searchResults.value = []
  showSearchResults.value = false
  showSuggestions.value = false
  searchError.value = ''
}

const selectSuggestion = (keyword) => {
  searchKeyword.value = keyword
  showSuggestions.value = false
  handleSearch()
}

const selectDocumentFromSearch = (documentId, documentName) => {
  selectedDocument.value = documentId
  selectedDocumentName.value = documentName
  showSearchResults.value = false
  showSuggestions.value = false
  searchKeyword.value = ''

  selectDocument(documentId)
}

const openArticleFromSearch = (article) => {
  selectedArticle.value = article
  articleModalVisible.value = true
  showSearchResults.value = false
  showSuggestions.value = false
  searchKeyword.value = ''
}

const toggleSearchBar = () => {
  searchBarVisible.value = !searchBarVisible.value
  if (!searchBarVisible.value) {
    clearSearch()
  }
}

const handleDocumentClick = () => {
  showSearchResults.value = false
  showSuggestions.value = false
}

const handleCategoryClick = () => {
  showSearchResults.value = false
  showSuggestions.value = false
}

onMounted(() => {
  loadCategories()
  if (route.query.category) {
    setTimeout(() => {
      selectCategory(Number(route.query.category))
    }, 100)
  }

  document.addEventListener('click', (e) => {
    const searchContainer = document.querySelector('.search-bar-container')
    if (searchContainer && !searchContainer.contains(e.target)) {
      showSuggestions.value = false
    }
  })
})

const loadCategories = async () => {
  loading.value = true
  try {
    const response = await fetch(`${API_BASE}/categories`)
    if (response.ok) {
      const result = await response.json()
      categories.value = result.data || []
    }
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
    const response = await fetch(`${API_BASE}/documents/category/${categoryId}`)
    if (response.ok) {
      const result = await response.json()
      documents.value = result.data || []
    }
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
  const document = documents.value.find(d => d.id === documentId)
  if (document) {
    selectedDocumentName.value = document.name
  }
  loading.value = true

  try {
    const response = await fetch(`${API_BASE}/articles/document/${documentId}`)
    if (response.ok) {
      const result = await response.json()
      articles.value = result.data || []
    }
  } catch (error) {
    console.error('加载条文失败:', error)
    articles.value = []
  }
  loading.value = false
}

const truncateContent = (content) => {
  if (content && content.length > 100) {
    return content.substring(0, 100) + '...'
  }
  return content || ''
}

const openArticleModal = (article) => {
  selectedArticle.value = article
  articleModalVisible.value = true
}
</script>

<template>
  <div class="law-article-container">
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-if="searchBarVisible" class="search-bar-container">
      <div class="search-bar">
        <div class="search-type-selector">
          <label :class="{ active: searchType === 'name' }" @click="searchType = 'name'; clearSearch()">
            <input type="radio" v-model="searchType" value="name" hidden>
            法律名称
          </label>
          <label :class="{ active: searchType === 'keyword' }" @click="searchType = 'keyword'; clearSearch()">
            <input type="radio" v-model="searchType" value="keyword" hidden>
            关键字
          </label>
        </div>
        <div class="search-input-wrapper">
          <input
              type="text"
              v-model="searchKeyword"
              placeholder="请输入搜索内容..."
              class="search-input"
              @keyup.enter="handleSearch"
              @input="handleKeywordInput"
              ref="searchInputRef"
          />
          <button v-if="searchKeyword" class="clear-btn" @click="clearSearch">×</button>
          <button class="search-btn" @click="handleSearch">
            <span class="search-icon">🔍</span>
          </button>
        </div>
        <button class="hide-btn" @click="toggleSearchBar">
          <span>▼</span>
        </button>
      </div>

      <div v-if="showSuggestions && smartSuggestions.length > 0" class="suggestions-panel">
        <div class="suggestions-header">
          <span class="suggest-icon">💡</span>
          <span>智慧搜索</span>
        </div>
        <div class="suggestions-list">
          <button
              v-for="keyword in smartSuggestions"
              :key="keyword"
              class="suggestion-item"
              @click="selectSuggestion(keyword)"
          >
            {{ keyword }}
          </button>
        </div>
      </div>

      <div v-if="showSearchResults" class="search-results-panel">
        <div v-if="searchResults.length > 0" class="results-header">
          <span>搜索结果</span>
          <button class="close-btn" @click="clearSearch">×</button>
        </div>
        <div v-if="searchResults.length > 0" class="results-list">
          <div
              v-for="item in searchResults"
              :key="item.id"
              class="result-item"
              @click="searchType === 'name' ? selectDocumentFromSearch(item.id, item.name) : openArticleFromSearch(item)"
          >
            <div class="result-title">
              <span v-if="searchType === 'name'" class="result-icon">📄</span>
              <span v-else class="result-icon">📝</span>
              <span>{{ searchType === 'name' ? item.name : item.title || item.articleNumber }}</span>
            </div>
            <p v-if="searchType === 'keyword' && item.content" class="result-preview">
              {{ item.content.substring(0, 100) }}...
            </p>
          </div>
        </div>
        <div v-else-if="searchError" class="no-results">
          <p>{{ searchError }}</p>
        </div>
        <div v-else-if="showSearchResults" class="no-results">
          <p>未找到相关内容</p>
        </div>
      </div>
    </div>

    <div v-if="!searchBarVisible" class="show-search-bar">
      <button class="show-search-btn" @click="toggleSearchBar">
        <span>🔍 显示搜索栏</span>
      </button>
    </div>

    <div class="three-tier-layout">
      <div class="tier tier-categories">
        <h3>法规分类</h3>
        <ul class="category-list">
          <li
              v-for="category in categories"
              :key="category.id"
              :class="{ active: selectedCategory === category.id }"
              @click="handleCategoryClick(); selectCategory(category.id)"
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
              @click="handleDocumentClick(); selectDocument(document.id)"
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
      <div class="modal-content">
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
          <div class="detail-row content-row">
            <span class="detail-label">条文内容：</span>
            <div class="content-value">{{ selectedArticle.content }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.law-article-container {
  min-height: calc(100vh - 120px);
  padding: 1rem;
  position: relative;
}

.show-search-bar {
  max-width: 1400px;
  margin: 0 auto 1rem;
  text-align: right;
}

.show-search-btn {
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  border: none;
  border-radius: 6px;
  color: white;
  cursor: pointer;
  font-size: 0.9rem;
  transition: opacity 0.2s;
}

.show-search-btn:hover {
  opacity: 0.9;
}

.search-bar-container {
  max-width: 1400px;
  margin: 0 auto 1rem;
  position: relative;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  padding: 0.5rem;
}

.search-type-selector {
  display: flex;
  background: #f5f7fa;
  border-radius: 6px;
  padding: 2px;
}

.search-type-selector label {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  color: #666;
  transition: all 0.2s;
}

.search-type-selector label.active {
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
}

.search-input {
  flex: 1;
  padding: 0.75rem 1rem;
  border: 1px solid #e4e7ed;
  border-radius: 6px 0 0 6px;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.2s;
}

.search-input:focus {
  border-color: #1e88e5;
}

.clear-btn {
  padding: 0.75rem;
  background: #f5f7fa;
  border: none;
  border-left: 1px solid #e4e7ed;
  cursor: pointer;
  font-size: 1rem;
  color: #666;
  transition: background 0.2s;
}

.clear-btn:hover {
  background: #e4e7ed;
}

.hide-btn {
  padding: 0.5rem 1rem;
  background: #f5f7fa;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.85rem;
  color: #666;
  transition: all 0.2s;
}

.hide-btn:hover {
  background: #e4e7ed;
}

.hide-btn span {
  display: block;
  transform: rotate(0deg);
  transition: transform 0.2s;
}

.suggestions-panel {
  position: absolute;
  top: calc(100% + 0.5rem);
  left: 0;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  z-index: 100;
}

.suggestions-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  border-bottom: 1px solid #e4e7ed;
  font-weight: bold;
  color: #333;
}

.suggest-icon {
  font-size: 1rem;
}

.suggestions-list {
  display: flex;
  flex-wrap: wrap;
  padding: 1rem;
  gap: 0.5rem;
}

.suggestion-item {
  padding: 0.5rem 1rem;
  background: #f5f7fa;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.85rem;
  color: #666;
  transition: all 0.2s;
}

.suggestion-item:hover {
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
}

.search-btn {
  padding: 0.75rem 1.25rem;
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  border: none;
  border-radius: 0 6px 6px 0;
  cursor: pointer;
  color: white;
  transition: opacity 0.2s;
}

.search-btn:hover {
  opacity: 0.9;
}

.search-icon {
  font-size: 1rem;
}

.search-results-panel {
  position: absolute;
  top: calc(100% + 0.5rem);
  left: 0;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  z-index: 100;
  max-height: 400px;
  overflow-y: auto;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #e4e7ed;
  font-weight: bold;
  color: #333;
}

.results-header .close-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: #666;
  padding: 0.25rem 0.5rem;
}

.results-list {
  padding: 0.5rem;
}

.result-item {
  padding: 0.75rem 1rem;
  cursor: pointer;
  border-radius: 6px;
  transition: background 0.2s;
}

.result-item:hover {
  background: #f5f7fa;
}

.result-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.95rem;
  color: #333;
}

.result-icon {
  font-size: 1rem;
}

.result-preview {
  margin: 0.5rem 0 0;
  font-size: 0.85rem;
  color: #666;
  line-height: 1.4;
}

.no-results {
  padding: 2rem;
  text-align: center;
  color: #999;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
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
  height: calc(100vh - 180px);
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
</style>
