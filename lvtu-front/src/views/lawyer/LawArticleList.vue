<script setup>
import { ref, onMounted } from 'vue'
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

onMounted(() => {
  loadCategories()
  if (route.query.category) {
    setTimeout(() => {
      selectCategory(Number(route.query.category))
    }, 100)
  }
})

const loadCategories = () => {
  categories.value = [
    { id: 1, name: '宪法', status: 1 },
    { id: 2, name: '民法', status: 1 },
    { id: 3, name: '刑法', status: 1 },
    { id: 4, name: '行政法', status: 1 },
    { id: 5, name: '商法', status: 1 },
    { id: 6, name: '民事诉讼法', status: 1 },
    { id: 7, name: '刑事诉讼法', status: 1 },
    { id: 8, name: '行政诉讼法', status: 1 }
  ]
}

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  selectedDocument.value = null
  articles.value = []
  selectedDocumentName.value = ''
  
  const mockDocuments = {
    1: [{ id: 1, name: '中华人民共和国宪法', categoryId: 1 }],
    2: [{ id: 2, name: '中华人民共和国民法典', categoryId: 2 }],
    3: [{ id: 3, name: '中华人民共和国刑法', categoryId: 3 }],
    4: [{ id: 4, name: '中华人民共和国行政法', categoryId: 4 }],
    5: [{ id: 5, name: '中华人民共和国公司法', categoryId: 5 }],
    6: [{ id: 6, name: '中华人民共和国民事诉讼法', categoryId: 6 }],
    7: [{ id: 7, name: '中华人民共和国刑事诉讼法', categoryId: 7 }],
    8: [{ id: 8, name: '中华人民共和国行政诉讼法', categoryId: 8 }]
  }
  documents.value = mockDocuments[categoryId] || []
  if (documents.value.length > 0) {
    selectDocument(documents.value[0].id)
  }
}

const selectDocument = (documentId) => {
  selectedDocument.value = documentId
  const document = documents.value.find(d => d.id === documentId)
  if (document) {
    selectedDocumentName.value = document.name
  }
  
  const mockArticles = {
    1: [
      { id: 1, documentId: 1, articleNo: '第一条', chapterTitle: '第一章 总则', content: '中华人民共和国是工人阶级领导的、以工农联盟为基础的人民民主专政的社会主义国家。', sortOrder: 1 },
      { id: 2, documentId: 1, articleNo: '第二条', chapterTitle: '第一章 总则', content: '中华人民共和国的一切权力属于人民。', sortOrder: 2 },
      { id: 3, documentId: 1, articleNo: '第三条', chapterTitle: '第一章 总则', content: '中华人民共和国的国家机构实行民主集中制的原则。', sortOrder: 3 }
    ],
    2: [
      { id: 4, documentId: 2, articleNo: '第一条', chapterTitle: '第一章 基本规定', content: '为了保护民事主体的合法权益，调整民事关系，维护社会和经济秩序，适应中国特色社会主义发展要求，弘扬社会主义核心价值观，根据宪法，制定本法。', sortOrder: 1 },
      { id: 5, documentId: 2, articleNo: '第二条', chapterTitle: '第一章 基本规定', content: '民法调整平等主体的自然人、法人和非法人组织之间的人身关系和财产关系。', sortOrder: 2 },
      { id: 6, documentId: 2, articleNo: '第三条', chapterTitle: '第一章 基本规定', content: '民事主体的人身权利、财产权利以及其他合法权益受法律保护，任何组织或者个人不得侵犯。', sortOrder: 3 },
      { id: 14, documentId: 2, articleNo: '第四条', chapterTitle: '第一章 基本规定', content: '民事主体在民事活动中的法律地位一律平等。', sortOrder: 4 },
      { id: 15, documentId: 2, articleNo: '第五条', chapterTitle: '第一章 基本规定', content: '民事主体从事民事活动，应当遵循自愿原则，按照自己的意思设立、变更、终止民事法律关系。', sortOrder: 5 }
    ],
    3: [
      { id: 7, documentId: 3, articleNo: '第一条', chapterTitle: '第一章 刑法的任务、基本原则和适用范围', content: '为了惩罚犯罪，保护人民，根据宪法，结合我国同犯罪作斗争的具体经验及实际情况，制定本法。', sortOrder: 1 },
      { id: 8, documentId: 3, articleNo: '第二条', chapterTitle: '第一章 刑法的任务、基本原则和适用范围', content: '中华人民共和国刑法的任务，是用刑罚同一切犯罪行为作斗争，以保卫国家安全，保卫人民民主专政的政权和社会主义制度，保护国有财产和劳动群众集体所有的财产，保护公民私人所有的财产，保护公民的人身权利、民主权利和其他权利，维护社会秩序、经济秩序，保障社会主义建设事业的顺利进行。', sortOrder: 2 }
    ],
    4: [
      { id: 9, documentId: 4, articleNo: '第一条', chapterTitle: '第一章 总则', content: '为了规范行政许可的设定和实施，保护公民、法人和其他组织的合法权益，维护公共利益和社会秩序，保障和监督行政机关有效实施行政管理，根据宪法，制定本法。', sortOrder: 1 }
    ],
    5: [
      { id: 10, documentId: 5, articleNo: '第一条', chapterTitle: '第一章 总则', content: '为了规范公司的组织和行为，保护公司、股东和债权人的合法权益，维护社会经济秩序，促进社会主义市场经济的发展，制定本法。', sortOrder: 1 }
    ],
    6: [
      { id: 11, documentId: 6, articleNo: '第一条', chapterTitle: '第一章 任务、适用范围和基本原则', content: '中华人民共和国民事诉讼法以宪法为根据，结合我国民事审判工作的经验和实际情况制定。', sortOrder: 1 }
    ],
    7: [
      { id: 12, documentId: 7, articleNo: '第一条', chapterTitle: '第一章 任务和基本原则', content: '为了保证刑法的正确实施，惩罚犯罪，保护人民，保障国家安全和社会公共安全，维护社会主义社会秩序，根据宪法，制定本法。', sortOrder: 1 }
    ],
    8: [
      { id: 13, documentId: 8, articleNo: '第一条', chapterTitle: '第一章 总则', content: '为保证人民法院公正、及时审理行政案件，解决行政争议，保护公民、法人和其他组织的合法权益，监督行政机关依法行使职权，根据宪法，制定本法。', sortOrder: 1 }
    ]
  }
  articles.value = mockArticles[documentId] || []
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
              <span class="article-no">{{ article.articleNo }}</span>
              <span v-if="article.chapterTitle" class="chapter-title">{{ article.chapterTitle }}</span>
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
            <span class="detail-value">{{ selectedArticle.articleNo }}</span>
          </div>
          <div v-if="selectedArticle.chapterTitle" class="detail-row">
            <span class="detail-label">章节标题：</span>
            <span class="detail-value">{{ selectedArticle.chapterTitle }}</span>
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

.chapter-title {
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
