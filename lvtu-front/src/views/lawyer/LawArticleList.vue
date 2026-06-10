<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  createArticleComment,
  getArticleDetail,
  getArticlesByDocument,
  getCategories,
  getDocumentsByCategory,
  submitExplanationFeedback,
  toggleArticleFavorite
} from '@/api/lawArticle'
import { promptLogin } from '@/utils/loginPrompt'

const router = useRouter()
const route = useRoute()

const categories = ref([])
const documents = ref([])
const articles = ref([])
const selectedCategory = ref(null)
const selectedDocument = ref(null)
const selectedDocumentName = ref('')
const selectedDetail = ref(null)
const detailVisible = ref(false)
const loading = ref(false)
const detailLoading = ref(false)
const commentSubmitting = ref(false)
const commentDraft = ref('')
const feedbackVisible = ref(false)
const feedbackHelpful = ref(true)
const feedbackReason = ref('')
const feedbackContent = ref('')
const feedbackSubmitting = ref(false)

const helpfulReasons = ['描述符合', '内容有趣', '很好理解', '感受一般']
const unhelpfulReasons = ['描述不符合', '读不下去', '不太好懂', '感受一般']

const currentUserId = computed(() => {
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
  return currentUser?.userId || Number(localStorage.getItem('userId')) || null
})

const detailArticle = computed(() => selectedDetail.value?.article || null)
const detailComments = computed(() => selectedDetail.value?.comments || [])
const isFavorited = computed(() => !!selectedDetail.value?.favorited)
const favoriteCount = computed(() => Number(selectedDetail.value?.favoriteCount || 0))
const commentCount = computed(() => Number(selectedDetail.value?.commentCount || detailComments.value.length || 0))
const feedbackReasons = computed(() => feedbackHelpful.value ? helpfulReasons : unhelpfulReasons)
const feedbackTitle = computed(() => feedbackHelpful.value ? '这条解释对你有帮助吗？' : '这条解释哪里还需要改进？')
const feedbackPlaceholder = computed(() =>
  feedbackHelpful.value ? '期待您的建议，我们会持续优化内容～' : '请写下您的问题，我们会认真对待～'
)

onMounted(async () => {
  await loadCategories()
  if (route.query.category) {
    await selectCategory(
      Number(route.query.category),
      route.query.document ? Number(route.query.document) : null,
      route.query.article ? Number(route.query.article) : null
    )
  }
})

const unwrap = (response) => response?.data?.data ?? response?.data ?? []

const loadCategories = async () => {
  loading.value = true
  try {
    const response = await getCategories()
    categories.value = unwrap(response)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载法规分类失败')
  } finally {
    loading.value = false
  }
}

const selectCategory = async (categoryId, preferredDocumentId = null, preferredArticleId = null) => {
  selectedCategory.value = categoryId
  selectedDocument.value = null
  selectedDetail.value = null
  detailVisible.value = false
  articles.value = []
  selectedDocumentName.value = ''
  loading.value = true

  try {
    const response = await getDocumentsByCategory(categoryId)
    documents.value = unwrap(response)
    if (documents.value.length > 0) {
      const targetDocument = documents.value.find((item) => item.id === preferredDocumentId) || documents.value[0]
      await selectDocument(targetDocument.id, preferredArticleId)
    }
  } catch (error) {
    documents.value = []
    ElMessage.error(error?.response?.data?.message || '加载法规文件失败')
  } finally {
    loading.value = false
  }
}

const selectDocument = async (documentId, preferredArticleId = null) => {
  selectedDocument.value = documentId
  selectedDetail.value = null
  detailVisible.value = false
  const document = documents.value.find((item) => item.id === documentId)
  selectedDocumentName.value = document?.name || ''
  loading.value = true

  try {
    const response = await getArticlesByDocument(documentId)
    articles.value = unwrap(response)
    if (preferredArticleId) {
      const targetArticle = articles.value.find((item) => item.id === preferredArticleId)
      if (targetArticle) {
        await openArticleDetail(targetArticle)
      }
    }
  } catch (error) {
    articles.value = []
    ElMessage.error(error?.response?.data?.message || '加载条文失败')
  } finally {
    loading.value = false
  }
}

const truncateContent = (content) => {
  if (!content) return ''
  return content.length > 120 ? `${content.substring(0, 120)}...` : content
}

const formatTime = (value) => {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}

const openArticleDetail = async (article) => {
  detailVisible.value = true
  detailLoading.value = true
  commentDraft.value = ''
  closeFeedback()
  try {
    const response = await getArticleDetail(article.id, currentUserId.value)
    selectedDetail.value = unwrap(response)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载条文详情失败')
  } finally {
    detailLoading.value = false
  }
}

const openFeedback = (helpful) => {
  feedbackHelpful.value = helpful
  feedbackReason.value = ''
  feedbackContent.value = ''
  feedbackVisible.value = true
}

const closeFeedback = () => {
  feedbackVisible.value = false
  feedbackReason.value = ''
  feedbackContent.value = ''
}

const submitFeedback = async () => {
  if (!currentUserId.value) {
    promptLogin(router, router.currentRoute.value.fullPath, '登录或注册后，你可以反馈法条解释质量，帮助平台优化内容。')
    return
  }
  if (!detailArticle.value) return

  feedbackSubmitting.value = true
  try {
    await submitExplanationFeedback(detailArticle.value.id, {
      userId: currentUserId.value,
      explanationId: selectedDetail.value?.explanation?.id || null,
      helpful: feedbackHelpful.value,
      reason: feedbackReason.value,
      content: feedbackContent.value.trim()
    })
    closeFeedback()
    ElMessage.success('反馈已提交')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '反馈提交失败')
  } finally {
    feedbackSubmitting.value = false
  }
}

const handleToggleFavorite = async () => {
  if (!currentUserId.value) {
    promptLogin(router, router.currentRoute.value.fullPath, '登录或注册后，你可以收藏常用法条，后续在个人中心快速查看。')
    return
  }
  if (!detailArticle.value) return

  try {
    const response = await toggleArticleFavorite(detailArticle.value.id, currentUserId.value)
    const data = unwrap(response)
    selectedDetail.value = {
      ...selectedDetail.value,
      favorited: data.favorited,
      favoriteCount: data.favoriteCount,
      commentCount: data.commentCount
    }
    ElMessage.success(data.favorited ? '已收藏' : '已取消收藏')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '收藏操作失败')
  }
}

const submitComment = async () => {
  if (!currentUserId.value) {
    promptLogin(router, router.currentRoute.value.fullPath, '登录或注册后，你可以参与法条评论，和其他用户交流法律问题。')
    return
  }
  if (!commentDraft.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  if (!detailArticle.value) return

  commentSubmitting.value = true
  try {
    await createArticleComment(detailArticle.value.id, {
      userId: currentUserId.value,
      content: commentDraft.value.trim()
    })
    commentDraft.value = ''
    await openArticleDetail(detailArticle.value)
    ElMessage.success('评论已发布')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '评论发布失败')
  } finally {
    commentSubmitting.value = false
  }
}
</script>

<template>
  <div class="law-article-page" v-loading="loading">
    <section class="page-head">
      <div>
        <p class="eyebrow">法律条文</p>
        <h1>法条查询</h1>
        <p class="subtext">查看条文原文、平台解释和用户评论。</p>
      </div>
    </section>

    <section class="law-workspace">
      <aside class="side-panel">
        <div class="panel-title">法规分类</div>
        <button
          v-for="category in categories"
          :key="category.id"
          :class="['nav-row', { active: selectedCategory === category.id }]"
          @click="selectCategory(category.id)"
        >
          {{ category.name }}
        </button>
      </aside>

      <aside class="side-panel document-panel">
        <div class="panel-title">法规文件</div>
        <button
          v-for="document in documents"
          :key="document.id"
          :class="['nav-row', { active: selectedDocument === document.id }]"
          @click="selectDocument(document.id)"
        >
          {{ document.name }}
        </button>
        <el-empty v-if="selectedCategory && documents.length === 0" description="暂无法规文件" />
      </aside>

      <main class="article-panel">
        <div class="article-panel-head">
          <div>
            <span>当前法规</span>
            <strong>{{ selectedDocumentName || '请选择法规文件' }}</strong>
          </div>
        </div>

        <div v-if="articles.length > 0" class="article-list">
          <article
            v-for="article in articles"
            :key="article.id"
            class="article-card"
            @click="openArticleDetail(article)"
          >
            <div class="article-meta">
              <strong>{{ article.articleNumber }}</strong>
              <span v-if="article.title">{{ article.title }}</span>
            </div>
            <p>{{ truncateContent(article.content) }}</p>
          </article>
        </div>
        <el-empty v-else description="请选择法规文件查看条文" />
      </main>
    </section>

    <div v-if="detailVisible" class="detail-overlay" @click.self="detailVisible = false">
      <section class="detail-sheet" v-loading="detailLoading">
        <header class="detail-head">
          <button class="icon-btn" @click="detailVisible = false">‹</button>
          <div>
            <span>{{ selectedDocumentName }}</span>
            <h2>{{ detailArticle?.articleNumber || '法条详情' }}</h2>
          </div>
          <button :class="['favorite-btn', { active: isFavorited }]" @click="handleToggleFavorite">
            <span>{{ isFavorited ? '★' : '☆' }}</span>
            收藏
          </button>
        </header>

        <div v-if="detailArticle" class="detail-scroll">
          <section class="article-full-text">
            <div class="section-label">条文原文</div>
            <h3 v-if="detailArticle.title">{{ detailArticle.title }}</h3>
            <p>{{ detailArticle.content }}</p>
          </section>

          <section class="explanation-block">
            <div class="section-label">解释</div>
            <p v-if="selectedDetail?.explanation?.content">{{ selectedDetail.explanation.content }}</p>
            <el-empty v-else description="暂无解释" />
            <div class="explanation-feedback-actions">
              <button type="button" @click="openFeedback(true)">有帮助</button>
              <button type="button" @click="openFeedback(false)">无帮助</button>
            </div>
          </section>

          <section class="comment-block">
            <div class="comment-head">
              <h3>全部评论（{{ commentCount }}）</h3>
            </div>
            <div v-if="detailComments.length" class="comment-list">
              <article v-for="comment in detailComments" :key="comment.id" class="comment-item">
                <div class="avatar">{{ (comment.username || '用').slice(0, 1) }}</div>
                <div>
                  <div class="comment-meta">
                    <strong>{{ comment.username || `用户${comment.userId}` }}</strong>
                    <span>{{ formatTime(comment.createdAt) }}</span>
                  </div>
                  <p>{{ comment.content }}</p>
                </div>
              </article>
            </div>
            <el-empty v-else description="还没有评论" />
          </section>
        </div>

        <footer class="detail-actions">
          <input
            v-model="commentDraft"
            maxlength="1000"
            placeholder="期待有价值的评论..."
            @keyup.enter="submitComment"
          />
          <button class="comment-submit" :disabled="commentSubmitting" @click="submitComment">
            {{ commentSubmitting ? '发布中' : '发布' }}
          </button>
          <button :class="['bottom-favorite', { active: isFavorited }]" @click="handleToggleFavorite">
            <span>{{ isFavorited ? '★' : '☆' }}</span>
            {{ favoriteCount }}
          </button>
        </footer>
      </section>
    </div>

    <div v-if="feedbackVisible" class="feedback-mask" @click.self="closeFeedback">
      <section class="feedback-panel">
        <button class="feedback-close" type="button" @click="closeFeedback">×</button>
        <p class="feedback-kicker">建议反馈</p>
        <h3>{{ feedbackTitle }}</h3>
        <div class="feedback-reasons">
          <button
            v-for="reason in feedbackReasons"
            :key="reason"
            type="button"
            :class="{ selected: feedbackReason === reason }"
            @click="feedbackReason = feedbackReason === reason ? '' : reason"
          >
            {{ reason }}
          </button>
        </div>
        <div class="feedback-textarea">
          <textarea v-model="feedbackContent" maxlength="140" :placeholder="feedbackPlaceholder"></textarea>
          <span>{{ feedbackContent.length }}/140</span>
        </div>
        <button class="feedback-submit" type="button" :disabled="feedbackSubmitting" @click="submitFeedback">
          {{ feedbackSubmitting ? '提交中' : '提交' }}
        </button>
      </section>
    </div>
  </div>
</template>

<style scoped>
.law-article-page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background: #f5f7fb;
  color: #172033;
}

.page-head,
.law-workspace {
  width: 100%;
  max-width: 1240px;
  margin-left: auto;
  margin-right: auto;
}

.page-head {
  margin-bottom: 22px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-weight: 700;
}

h1,
h2,
h3,
p {
  margin: 0;
}

h1 {
  font-size: 30px;
}

.subtext {
  margin-top: 10px;
  color: #667085;
}

.law-workspace {
  display: grid;
  grid-template-columns: 190px 260px minmax(0, 1fr);
  gap: 16px;
  align-items: start;
}

.side-panel,
.article-panel {
  min-height: 620px;
  background: #ffffff;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.side-panel {
  padding: 12px;
}

.panel-title {
  padding: 8px 8px 14px;
  color: #172033;
  font-weight: 700;
}

.nav-row {
  display: block;
  width: 100%;
  padding: 11px 12px;
  border: 0;
  border-radius: 6px;
  background: transparent;
  color: #344054;
  text-align: left;
  cursor: pointer;
}

.nav-row:hover {
  background: #f3f6fb;
}

.nav-row.active {
  background: #2563eb;
  color: #ffffff;
  font-weight: 700;
}

.article-panel {
  overflow: hidden;
}

.article-panel-head {
  padding: 18px 20px;
  border-bottom: 1px solid #edf1f7;
}

.article-panel-head span {
  display: block;
  margin-bottom: 6px;
  color: #667085;
  font-size: 13px;
}

.article-panel-head strong {
  color: #172033;
  font-size: 18px;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 560px;
  padding: 16px;
  overflow-y: auto;
}

.article-card {
  padding: 16px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.article-card:hover {
  border-color: #93c5fd;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.1);
}

.article-meta {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
}

.article-meta strong {
  color: #2563eb;
}

.article-meta span {
  color: #667085;
  font-size: 13px;
}

.article-card p {
  color: #344054;
  line-height: 1.8;
}

.detail-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  background: rgba(15, 23, 42, 0.42);
}

.detail-sheet {
  position: relative;
  display: flex;
  flex-direction: column;
  width: min(760px, 100vw);
  height: 100vh;
  background: #ffffff;
}

.detail-head {
  display: grid;
  grid-template-columns: 52px minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  min-height: 72px;
  padding: 12px 18px;
  border-bottom: 1px solid #edf1f7;
}

.icon-btn {
  width: 40px;
  height: 40px;
  border: 0;
  border-radius: 50%;
  background: #f3f6fb;
  color: #172033;
  font-size: 28px;
  cursor: pointer;
}

.detail-head span {
  display: block;
  color: #667085;
  font-size: 13px;
}

.detail-head h2 {
  margin-top: 3px;
  color: #172033;
  font-size: 20px;
}

.favorite-btn,
.bottom-favorite,
.comment-submit {
  border: 0;
  border-radius: 999px;
  cursor: pointer;
  font-weight: 700;
}

.favorite-btn {
  padding: 9px 14px;
  background: #f3f6fb;
  color: #475467;
}

.favorite-btn.active,
.bottom-favorite.active {
  color: #1d4ed8;
  background: #eff6ff;
}

.detail-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 24px 26px 120px;
}

.section-label {
  margin-bottom: 12px;
  color: #2563eb;
  font-size: 14px;
  font-weight: 700;
}

.article-full-text,
.explanation-block,
.comment-block {
  padding-bottom: 28px;
  margin-bottom: 28px;
  border-bottom: 1px solid #edf1f7;
}

.article-full-text h3 {
  margin-bottom: 12px;
  color: #172033;
  font-size: 18px;
}

.article-full-text p,
.explanation-block p {
  color: #2f3a4c;
  font-size: 18px;
  line-height: 2;
  white-space: pre-wrap;
}

.explanation-feedback-actions {
  display: flex;
  gap: 12px;
  margin-top: 18px;
}

.explanation-feedback-actions button {
  min-width: 88px;
  height: 36px;
  border: 1px solid #d8dee9;
  border-radius: 999px;
  background: #ffffff;
  color: #344054;
  font-weight: 700;
  cursor: pointer;
}

.explanation-feedback-actions button:hover {
  border-color: #2563eb;
  color: #2563eb;
  background: #eff6ff;
}

.comment-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.comment-head h3 {
  font-size: 18px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.comment-item {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  gap: 12px;
}

.avatar {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #e0ecff;
  color: #1d4ed8;
  font-weight: 700;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.comment-meta strong {
  color: #172033;
}

.comment-meta span {
  color: #98a2b3;
  font-size: 13px;
}

.comment-item p {
  color: #344054;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}

.detail-actions {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  gap: 10px;
  align-items: center;
  padding: 14px 18px;
  border-top: 1px solid #edf1f7;
  background: #ffffff;
}

.detail-actions input {
  min-width: 0;
  height: 44px;
  padding: 0 16px;
  border: 0;
  border-radius: 999px;
  background: #f3f6fb;
  color: #172033;
  outline: none;
}

.comment-submit {
  height: 44px;
  padding: 0 18px;
  background: #2563eb;
  color: #ffffff;
}

.comment-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.bottom-favorite {
  height: 44px;
  min-width: 78px;
  background: #f3f6fb;
  color: #475467;
}

.feedback-mask {
  position: fixed;
  inset: 0;
  z-index: 1200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(15, 23, 42, 0.35);
}

.feedback-panel {
  position: relative;
  width: min(420px, 100%);
  padding: 30px 24px 26px;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.22);
  text-align: center;
}

.feedback-close {
  position: absolute;
  right: 14px;
  top: 12px;
  width: 30px;
  height: 30px;
  border: 0;
  border-radius: 50%;
  background: #f3f6fb;
  color: #667085;
  font-size: 20px;
  cursor: pointer;
}

.feedback-kicker {
  color: #98a2b3;
  font-size: 13px;
}

.feedback-panel h3 {
  margin-top: 8px;
  color: #172033;
  font-size: 18px;
}

.feedback-reasons {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 24px;
}

.feedback-reasons button {
  height: 42px;
  border: 1px solid #e5eaf3;
  border-radius: 999px;
  background: #f8fafc;
  color: #344054;
  font-weight: 700;
  cursor: pointer;
}

.feedback-reasons button.selected {
  border-color: #f59e0b;
  background: #fff7ed;
  color: #b45309;
}

.feedback-textarea {
  position: relative;
  margin-top: 18px;
}

.feedback-textarea textarea {
  width: 100%;
  height: 106px;
  padding: 14px 14px 28px;
  border: 1px solid #e5eaf3;
  border-radius: 12px;
  resize: none;
  outline: none;
  color: #172033;
  line-height: 1.6;
  box-sizing: border-box;
}

.feedback-textarea textarea:focus {
  border-color: #f59e0b;
}

.feedback-textarea span {
  position: absolute;
  right: 12px;
  bottom: 10px;
  color: #98a2b3;
  font-size: 12px;
}

.feedback-submit {
  width: 100%;
  height: 44px;
  margin-top: 18px;
  border: 0;
  border-radius: 999px;
  background: #d97706;
  color: #ffffff;
  font-weight: 800;
  cursor: pointer;
}

.feedback-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 960px) {
  .law-workspace {
    grid-template-columns: 1fr;
  }

  .side-panel,
  .article-panel {
    min-height: auto;
  }

  .document-panel {
    display: block;
  }
}

@media (max-width: 560px) {
  .law-article-page {
    padding: 20px 12px 40px;
  }

  .detail-scroll {
    padding: 20px 18px 120px;
  }

  .article-full-text p,
  .explanation-block p {
    font-size: 17px;
  }

  .detail-actions {
    grid-template-columns: minmax(0, 1fr) auto;
  }

  .bottom-favorite {
    display: none;
  }
}
</style>
