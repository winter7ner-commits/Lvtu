<template>
  <main class="home-page">
    <section class="hero-section">
      <div class="hero-copy">
        <p class="eyebrow">律途法律服务</p>
        <h1>把法律问题交给更合适的专业支持</h1>
        <p class="subtext">检索律师、法条与个人订单，快速进入咨询、查询和跟进流程。</p>

        <div class="hero-search">
          <el-input
            v-model="searchKeyword"
            clearable
            size="large"
            placeholder="输入律师、法条、订单号、业务类型或状态"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" size="large" :loading="searching" @click="handleSearch">
            搜索
          </el-button>
        </div>

        <div class="quick-tags">
          <button
            v-for="tag in quickTags"
            :key="tag"
            type="button"
            @click="searchByKeyword(tag)"
          >
            {{ tag }}
          </button>
        </div>
      </div>

      <div class="hero-panel">
        <div class="logo-stage">
          <span class="logo-ring"></span>
          <img src="/src/assets/images/logo.png" alt="律途" />
        </div>
        <div class="hero-metrics">
          <div>
            <strong>{{ hotLawyers.length || '-' }}</strong>
            <span>热门律师</span>
          </div>
          <div>
            <strong>6</strong>
            <span>服务类型</span>
          </div>
          <div>
            <strong>24h</strong>
            <span>需求流转</span>
          </div>
        </div>
      </div>
    </section>

    <section class="law-band">
      <div class="section-head">
        <div>
          <p class="eyebrow">法律法规</p>
          <h2>按领域快速查看常用法条</h2>
        </div>
        <el-button class="soft-btn" :loading="loadingLegal" @click="loadLegalCategories">刷新法规</el-button>
      </div>

      <div class="law-browser" v-loading="loadingLegal">
        <div class="law-column category-column">
          <div class="law-column-title">法律分类</div>
          <div class="law-scroll">
            <button
              v-for="category in legalCategories"
              :key="category.id"
              type="button"
              :class="['law-row', { active: selectedLawCategory === category.id }]"
              @click="selectLawCategory(category.id)"
            >
              {{ category.name }}
            </button>
            <el-empty v-if="!legalCategories.length && !loadingLegal" description="暂无分类" />
          </div>
        </div>

        <div class="law-column document-column" v-loading="loadingLegalDocuments">
          <div class="law-column-title">法律</div>
          <div class="law-scroll">
            <button
              v-for="document in legalDocuments"
              :key="document.id"
              type="button"
              :class="['law-row', { active: selectedLawDocument === document.id }]"
              @click="selectLawDocument(document.id)"
            >
              {{ document.name }}
            </button>
            <el-empty v-if="selectedLawCategory && !legalDocuments.length && !loadingLegalDocuments" description="暂无法律" />
          </div>
        </div>

        <div class="law-column article-column" v-loading="loadingLegalArticles">
          <div class="law-column-title">法条</div>
          <div class="article-scroll">
            <button
              v-for="article in legalArticles"
              :key="article.id"
              type="button"
              class="article-row"
              @click="openArticleDetail(article)"
            >
              <strong>{{ article.articleNumber }}</strong>
              <span v-if="article.title">{{ article.title }}</span>
              <p>{{ truncateArticle(article.content) }}</p>
            </button>
            <el-empty v-if="selectedLawDocument && !legalArticles.length && !loadingLegalArticles" description="暂无法条" />
          </div>
        </div>
      </div>
    </section>

    <section v-if="!isLawyerAccount" class="service-band">
      <div class="section-head">
        <div>
          <p class="eyebrow">常用服务</p>
          <h2>从问题到订单的快速入口</h2>
        </div>
        <el-button class="soft-btn" @click="router.push('/order-create')">创建订单</el-button>
      </div>

      <div class="service-grid">
        <button
          v-for="service in services"
          :key="service.type"
          type="button"
          class="service-tile"
          @click="router.push({ name: 'OrderCreate', query: { type: service.type } })"
        >
          <span>{{ service.short }}</span>
          <strong>{{ service.name }}</strong>
          <small>{{ service.desc }}</small>
        </button>
      </div>
    </section>

    <section class="home-columns">
      <article class="home-panel" v-loading="loadingHotLawyers">
        <div class="section-head compact">
          <div>
            <p class="eyebrow">律师推荐</p>
            <h2>高评分律师</h2>
          </div>
          <el-button link type="primary" @click="router.push('/lawyer-list')">全部律师</el-button>
        </div>
        <div v-if="hotLawyers.length" class="lawyer-list">
          <button
            v-for="lawyer in hotLawyers"
            :key="lawyer.lawyerId || lawyer.id"
            type="button"
            class="lawyer-row"
            @click="goToLawyerDetail(lawyer.lawyerId || lawyer.id)"
          >
            <div class="avatar">{{ getInitials(lawyer.name) }}</div>
            <div>
              <strong>{{ lawyer.name || '未命名律师' }}</strong>
              <span>{{ lawyer.lawFirm || '暂未填写律所' }}</span>
            </div>
            <em>{{ Number(lawyer.rating || 0).toFixed(1) }}</em>
          </button>
        </div>
        <el-empty v-else description="暂无推荐律师" />
      </article>

      <article v-if="!isLawyerAccount" class="home-panel">
        <div class="section-head compact">
          <div>
            <p class="eyebrow">办事进度</p>
            <h2>个人订单</h2>
          </div>
          <el-button link type="primary" @click="router.push('/orders')">我的订单</el-button>
        </div>
        <div class="order-entry">
          <div>
            <strong>跟进咨询、支付、服务结果与评价</strong>
            <p>登录后可在首页搜索自己的订单号、状态和业务类型。</p>
          </div>
          <el-button type="primary" plain @click="router.push('/orders')">查看订单</el-button>
        </div>
      </article>
    </section>

    <div v-if="detailVisible" class="detail-overlay" @click.self="closeArticleDetail">
      <section class="detail-sheet" v-loading="detailLoading">
        <header class="detail-head">
          <button class="icon-btn" type="button" @click="closeArticleDetail">‹</button>
          <div>
            <span>{{ selectedLawDocumentName }}</span>
            <h2>{{ detailArticle?.articleNumber || '法条详情' }}</h2>
          </div>
          <button :class="['favorite-btn', { active: isFavorited }]" type="button" @click="handleToggleFavorite">
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
                <div class="comment-avatar">{{ (comment.username || '用').slice(0, 1) }}</div>
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
          <button class="comment-submit" type="button" :disabled="commentSubmitting" @click="submitComment">
            {{ commentSubmitting ? '发布中' : '发布' }}
          </button>
          <button :class="['bottom-favorite', { active: isFavorited }]" type="button" @click="handleToggleFavorite">
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

  </main>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTopRatedLawyers } from '@/api/lawyer'
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

const searchKeyword = ref('')
const searching = ref(false)
const loadingHotLawyers = ref(false)
const hotLawyers = ref([])
const loadingLegal = ref(false)
const loadingLegalDocuments = ref(false)
const loadingLegalArticles = ref(false)
const legalCategories = ref([])
const legalDocuments = ref([])
const legalArticles = ref([])
const selectedLawCategory = ref(null)
const selectedLawDocument = ref(null)
const selectedDetail = ref(null)
const detailVisible = ref(false)
const detailLoading = ref(false)
const commentSubmitting = ref(false)
const commentDraft = ref('')
const feedbackVisible = ref(false)
const feedbackHelpful = ref(true)
const feedbackReason = ref('')
const feedbackContent = ref('')
const feedbackSubmitting = ref(false)

const quickTags = ['婚姻家事', '合同纠纷', '劳动争议', '知识产权', '待支付']
const helpfulReasons = ['描述符合', '内容有趣', '很好理解', '感受一般']
const unhelpfulReasons = ['描述不符合', '读不下去', '不太好懂', '感受一般']

const currentUserId = computed(() => {
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
  return currentUser?.userId || Number(localStorage.getItem('userId')) || null
})
const isLawyerAccount = computed(() => {
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
  return Number(currentUser?.authStatus) === 2
})
const selectedLawDocumentName = computed(() => {
  const document = legalDocuments.value.find((item) => item.id === selectedLawDocument.value)
  return document?.name || ''
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

const services = [
  { short: '咨', name: '在线法律咨询', type: 'ONLINE_CONSULT', desc: '图文沟通' },
  { short: '电', name: '电话法律咨询', type: 'PHONE_CONSULT', desc: '预约通话' },
  { short: '文', name: '文书代写', type: 'DOCUMENT_WRITING', desc: '起草修改' },
  { short: '合', name: '合同审核', type: 'CONTRACT_REVIEW', desc: '风险排查' },
  { short: '家', name: '婚姻家事', type: 'MARRIAGE_FAMILY', desc: '家事纠纷' },
  { short: '诉', name: '诉讼代理', type: 'LITIGATION_AGENT', desc: '案件代理' }
]

const unwrapData = (res) => {
  const payload = res?.data ?? res
  if (payload && typeof payload === 'object' && 'code' in payload) {
    return Number(payload.code) === 200 ? payload.data : null
  }
  return payload
}

const handleSearch = () => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  router.push({
    name: 'SearchResults',
    query: { keyword }
  })
}

const searchByKeyword = (keyword) => {
  searchKeyword.value = keyword
  handleSearch()
}

const loadHotLawyers = async () => {
  loadingHotLawyers.value = true
  try {
    const list = unwrapData(await getTopRatedLawyers(4))
    hotLawyers.value = Array.isArray(list) ? list : []
  } catch (error) {
    hotLawyers.value = []
  } finally {
    loadingHotLawyers.value = false
  }
}

const loadLegalCategories = async () => {
  loadingLegal.value = true
  try {
    const list = unwrapData(await getCategories())
    legalCategories.value = Array.isArray(list) ? list : []
    if (legalCategories.value.length > 0) {
      await selectLawCategory(legalCategories.value[0].id)
    }
  } catch (error) {
    legalCategories.value = []
    legalDocuments.value = []
    legalArticles.value = []
  } finally {
    loadingLegal.value = false
  }
}

const selectLawCategory = async (categoryId) => {
  selectedLawCategory.value = categoryId
  selectedLawDocument.value = null
  legalDocuments.value = []
  legalArticles.value = []
  loadingLegalDocuments.value = true

  try {
    const list = unwrapData(await getDocumentsByCategory(categoryId))
    legalDocuments.value = Array.isArray(list) ? list : []
    if (legalDocuments.value.length > 0) {
      await selectLawDocument(legalDocuments.value[0].id)
    }
  } catch (error) {
    legalDocuments.value = []
  } finally {
    loadingLegalDocuments.value = false
  }
}

const selectLawDocument = async (documentId) => {
  selectedLawDocument.value = documentId
  legalArticles.value = []
  loadingLegalArticles.value = true

  try {
    const list = unwrapData(await getArticlesByDocument(documentId))
    legalArticles.value = Array.isArray(list) ? list : []
  } catch (error) {
    legalArticles.value = []
  } finally {
    loadingLegalArticles.value = false
  }
}

const truncateArticle = (content) => {
  if (!content) return ''
  return content.length > 88 ? `${content.slice(0, 88)}...` : content
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
    selectedDetail.value = unwrapData(response)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载法条详情失败')
  } finally {
    detailLoading.value = false
  }
}

const closeArticleDetail = () => {
  detailVisible.value = false
  closeFeedback()
}

const handleToggleFavorite = async () => {
  if (!currentUserId.value) {
    promptLogin(router, router.currentRoute.value.fullPath, '登录或注册后，你可以收藏常用法条，后续在个人中心快速查看。')
    return
  }
  if (!detailArticle.value) return

  try {
    const response = await toggleArticleFavorite(detailArticle.value.id, currentUserId.value)
    const data = unwrapData(response)
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
    const currentArticle = detailArticle.value
    commentDraft.value = ''
    await openArticleDetail(currentArticle)
    ElMessage.success('评论已发布')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '评论发布失败')
  } finally {
    commentSubmitting.value = false
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

const goToLawyerDetail = (id) => {
  if (!id) return
  router.push(`/lawyer/${id}`)
}

const getInitials = (name = '律师') => name.slice(-2)

onMounted(() => {
  loadHotLawyers()
  loadLegalCategories()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background: #f5f7fb;
  color: #172033;
  text-align: left;
  overflow-x: hidden;
}

.home-page *,
.home-page *::before,
.home-page *::after {
  box-sizing: border-box;
}

.home-page .hero-section,
.home-page .law-band,
.home-page .service-band,
.home-page .home-columns {
  width: min(1180px, 100%);
  margin-left: auto;
  margin-right: auto;
}

.home-page .hero-section {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(320px, 0.8fr);
  gap: 28px;
  align-items: stretch;
}

.home-page .hero-copy,
.home-page .hero-panel,
.home-page .law-band,
.home-page .service-band,
.home-page .home-panel {
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.home-page .hero-copy,
.home-page .hero-panel {
  min-width: 0;
}

.home-page .hero-copy {
  padding: 34px;
  animation: homeFadeUp 0.58s ease both;
}

.home-page .eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-size: 13px;
  font-weight: 800;
}

.home-page h1,
.home-page h2,
.home-page h3,
.home-page .subtext {
  margin: 0;
}

.home-page h1 {
  max-width: 720px;
  color: #172033;
  font-size: 38px;
  font-weight: 800;
  line-height: 1.18;
  letter-spacing: 0;
}

.home-page h2 {
  color: #172033;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 0;
}

.home-page h3 {
  color: #172033;
  font-size: 18px;
  font-weight: 800;
}

.home-page .subtext {
  margin-top: 14px;
  color: #667085;
  font-size: 16px;
}

.home-page .hero-search {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 112px;
  gap: 12px;
  margin-top: 26px;
}

.home-page .hero-search :deep(.el-input__wrapper),
.home-page .hero-search :deep(.el-button) {
  border-radius: 8px;
}

.home-page .quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

.home-page .quick-tags button {
  padding: 8px 12px;
  border: 1px solid #dbe4f0;
  border-radius: 999px;
  background: #f8fafc;
  color: #475467;
  cursor: pointer;
  font-size: 13px;
  font-weight: 700;
}

.home-page .quick-tags button:hover {
  border-color: #2563eb;
  color: #1d4ed8;
}

.home-page .hero-panel {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: stretch;
  padding: 24px;
  overflow: hidden;
  animation: homeFadeUp 0.7s ease 0.08s both;
}

.home-page .logo-stage {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 236px;
  isolation: isolate;
}

.home-page .logo-stage::before {
  content: '';
  position: absolute;
  width: 260px;
  height: 260px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.16), rgba(37, 99, 235, 0) 62%);
  animation: logoGlow 3.6s ease-in-out infinite;
  z-index: -2;
}

.home-page .logo-stage::after {
  content: '';
  position: absolute;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #34d399;
  box-shadow: 0 0 20px rgba(52, 211, 153, 0.45);
  transform: translate(116px, -58px);
  animation: logoDot 4.8s ease-in-out infinite;
  z-index: -1;
}

.home-page .logo-ring {
  position: absolute;
  width: 250px;
  height: 250px;
  border: 1px solid rgba(37, 99, 235, 0.16);
  border-radius: 50%;
  animation: logoOrbit 10s linear infinite;
  z-index: -1;
}

.home-page .logo-ring::before,
.home-page .logo-ring::after {
  content: '';
  position: absolute;
  border-radius: 50%;
}

.home-page .logo-ring::before {
  inset: 20px;
  border: 1px dashed rgba(20, 184, 166, 0.18);
}

.home-page .logo-ring::after {
  top: 28px;
  right: 46px;
  width: 8px;
  height: 8px;
  background: #2563eb;
  box-shadow: 0 0 18px rgba(37, 99, 235, 0.48);
}

.home-page .hero-panel img {
  display: block;
  width: min(100%, 320px);
  max-width: 100%;
  height: auto;
  max-height: 210px;
  margin: 0 auto;
  object-fit: contain;
  filter: drop-shadow(0 16px 18px rgba(37, 99, 235, 0.14));
  animation: logoFloat 4.2s ease-in-out infinite;
}

.home-page .hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 18px;
}

.home-page .hero-metrics div {
  padding: 12px 10px;
  border-radius: 8px;
  background: #f6f8fc;
  text-align: center;
  animation: homeFadeUp 0.5s ease both;
}

.home-page .hero-metrics div:nth-child(1) {
  animation-delay: 0.18s;
}

.home-page .hero-metrics div:nth-child(2) {
  animation-delay: 0.26s;
}

.home-page .hero-metrics div:nth-child(3) {
  animation-delay: 0.34s;
}

.home-page .hero-metrics strong {
  display: block;
  color: #1d4ed8;
  font-size: 22px;
}

.home-page .hero-metrics span {
  color: #667085;
  font-size: 12px;
}

.home-page .service-band {
  margin-top: 20px;
  padding: 22px;
}

.home-page .law-band {
  margin-top: 20px;
  padding: 22px;
}

.home-page .section-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.home-page .section-head {
  margin-bottom: 18px;
}

.home-page .section-head > span {
  color: #1d4ed8;
  font-weight: 800;
}

.home-page .compact {
  margin-bottom: 16px;
}

.home-page .lawyer-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.home-page .lawyer-row,
.home-page .service-tile {
  width: 100%;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  cursor: pointer;
  text-align: left;
  transition: all 0.2s ease;
}

.home-page .lawyer-row:hover,
.home-page .service-tile:hover {
  border-color: #93c5fd;
  box-shadow: 0 10px 22px rgba(37, 99, 235, 0.1);
  transform: translateY(-1px);
}

.home-page .lawyer-row strong {
  display: block;
  color: #172033;
  font-size: 15px;
  font-weight: 800;
}

.home-page .lawyer-row span {
  display: block;
  margin-top: 4px;
  color: #667085;
  font-size: 12px;
}

.home-page .soft-btn {
  border-color: #d6e4ff;
  color: #1d4ed8;
  background: #ffffff;
}

.home-page .service-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 12px;
}

.home-page .law-browser {
  display: grid;
  grid-template-columns: 190px 260px minmax(0, 1fr);
  gap: 14px;
  min-height: 360px;
  max-height: 390px;
}

.home-page .law-column {
  min-width: 0;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #fbfdff;
  overflow: hidden;
}

.home-page .law-column-title {
  height: 48px;
  display: flex;
  align-items: center;
  padding: 0 14px;
  border-bottom: 1px solid #e5eaf3;
  background: #f8fafc;
  color: #172033;
  font-size: 15px;
  font-weight: 800;
}

.home-page .law-scroll,
.home-page .article-scroll {
  max-height: 312px;
  padding: 10px;
  overflow-y: auto;
}

.home-page .article-scroll {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.home-page .law-scroll::-webkit-scrollbar,
.home-page .article-scroll::-webkit-scrollbar {
  width: 6px;
}

.home-page .law-scroll::-webkit-scrollbar-thumb,
.home-page .article-scroll::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: #cbd5e1;
}

.home-page .law-row {
  display: block;
  width: 100%;
  min-height: 42px;
  padding: 10px 12px;
  border: 0;
  border-radius: 6px;
  background: transparent;
  color: #344054;
  text-align: left;
  line-height: 1.45;
  cursor: pointer;
}

.home-page .law-row:hover {
  background: #f1f5f9;
}

.home-page .law-row.active {
  background: #2563eb;
  color: #ffffff;
  font-weight: 800;
}

.home-page .article-row {
  width: 100%;
  padding: 14px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.home-page .article-row:hover {
  border-color: #93c5fd;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.1);
  transform: translateY(-1px);
}

.home-page .article-row strong {
  color: #2563eb;
  font-size: 15px;
}

.home-page .article-row span {
  margin-left: 8px;
  color: #667085;
  font-size: 13px;
}

.home-page .article-row p {
  margin: 8px 0 0;
  color: #344054;
  line-height: 1.7;
  word-break: break-word;
}

.home-page .service-tile {
  min-height: 126px;
  padding: 16px;
  animation: homeFadeUp 0.5s ease both;
}

.home-page .service-tile:nth-child(1) {
  animation-delay: 0.08s;
}

.home-page .service-tile:nth-child(2) {
  animation-delay: 0.12s;
}

.home-page .service-tile:nth-child(3) {
  animation-delay: 0.16s;
}

.home-page .service-tile:nth-child(4) {
  animation-delay: 0.2s;
}

.home-page .service-tile:nth-child(5) {
  animation-delay: 0.24s;
}

.home-page .service-tile:nth-child(6) {
  animation-delay: 0.28s;
}

.home-page .service-tile span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-weight: 900;
}

.home-page .service-tile strong,
.home-page .service-tile small {
  display: block;
}

.home-page .service-tile strong {
  margin-top: 12px;
  color: #172033;
  font-size: 15px;
}

.home-page .service-tile small {
  margin-top: 5px;
  color: #667085;
}

.home-page .home-columns {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 18px;
  margin-top: 20px;
}

.home-page .home-panel {
  padding: 22px;
}

.home-page .lawyer-row {
  display: grid;
  grid-template-columns: 46px minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  padding: 12px;
}

.home-page .avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: 8px;
  background: #eff6ff;
  color: #1d4ed8;
  font-weight: 900;
}

.home-page .lawyer-row em {
  color: #1d4ed8;
  font-style: normal;
  font-weight: 900;
}

.home-page .order-entry {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  min-height: 144px;
  padding: 18px;
  border-radius: 8px;
  background: #f6f8fc;
}

.home-page .order-entry strong {
  color: #172033;
}

.home-page .order-entry p {
  margin: 8px 0 0;
  color: #667085;
  line-height: 1.7;
}

.home-page .detail-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  background: rgba(15, 23, 42, 0.42);
}

.home-page .detail-sheet {
  position: relative;
  display: flex;
  flex-direction: column;
  width: min(760px, 100vw);
  height: 100vh;
  background: #ffffff;
}

.home-page .detail-head {
  display: grid;
  grid-template-columns: 52px minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  min-height: 72px;
  padding: 12px 18px;
  border-bottom: 1px solid #edf1f7;
}

.home-page .icon-btn {
  width: 40px;
  height: 40px;
  border: 0;
  border-radius: 50%;
  background: #f3f6fb;
  color: #172033;
  font-size: 28px;
  cursor: pointer;
}

.home-page .detail-head span {
  display: block;
  color: #667085;
  font-size: 13px;
}

.home-page .detail-head h2 {
  margin-top: 3px;
  color: #172033;
  font-size: 20px;
}

.home-page .favorite-btn,
.home-page .bottom-favorite,
.home-page .comment-submit {
  border: 0;
  border-radius: 999px;
  cursor: pointer;
  font-weight: 700;
}

.home-page .favorite-btn {
  padding: 9px 14px;
  background: #f3f6fb;
  color: #475467;
}

.home-page .favorite-btn.active,
.home-page .bottom-favorite.active {
  color: #1d4ed8;
  background: #eff6ff;
}

.home-page .detail-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 24px 26px 120px;
}

.home-page .section-label {
  margin-bottom: 12px;
  color: #2563eb;
  font-size: 14px;
  font-weight: 700;
}

.home-page .article-full-text,
.home-page .explanation-block,
.home-page .comment-block {
  padding-bottom: 28px;
  margin-bottom: 28px;
  border-bottom: 1px solid #edf1f7;
}

.home-page .article-full-text h3 {
  margin-bottom: 12px;
  color: #172033;
  font-size: 18px;
}

.home-page .article-full-text p,
.home-page .explanation-block p {
  color: #2f3a4c;
  font-size: 18px;
  line-height: 2;
  white-space: pre-wrap;
}

.home-page .explanation-feedback-actions {
  display: flex;
  gap: 12px;
  margin-top: 18px;
}

.home-page .explanation-feedback-actions button {
  min-width: 88px;
  height: 36px;
  border: 1px solid #d8dee9;
  border-radius: 999px;
  background: #ffffff;
  color: #344054;
  font-weight: 700;
  cursor: pointer;
}

.home-page .explanation-feedback-actions button:hover {
  border-color: #2563eb;
  color: #2563eb;
  background: #eff6ff;
}

.home-page .comment-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.home-page .comment-head h3 {
  font-size: 18px;
}

.home-page .comment-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.home-page .comment-item {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  gap: 12px;
}

.home-page .comment-avatar {
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

.home-page .comment-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.home-page .comment-meta strong {
  color: #172033;
}

.home-page .comment-meta span {
  color: #98a2b3;
  font-size: 13px;
}

.home-page .comment-item p {
  color: #344054;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}

.home-page .detail-actions {
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

.home-page .detail-actions input {
  min-width: 0;
  height: 44px;
  padding: 0 16px;
  border: 0;
  border-radius: 999px;
  background: #f3f6fb;
  color: #172033;
  outline: none;
}

.home-page .comment-submit {
  height: 44px;
  padding: 0 18px;
  background: #2563eb;
  color: #ffffff;
}

.home-page .comment-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.home-page .bottom-favorite {
  height: 44px;
  min-width: 78px;
  background: #f3f6fb;
  color: #475467;
}

.home-page .feedback-mask {
  position: fixed;
  inset: 0;
  z-index: 1200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(15, 23, 42, 0.35);
}

.home-page .feedback-panel {
  position: relative;
  width: min(420px, 100%);
  padding: 30px 24px 26px;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.22);
  text-align: center;
}

.home-page .feedback-close {
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

.home-page .feedback-kicker {
  color: #98a2b3;
  font-size: 13px;
}

.home-page .feedback-panel h3 {
  margin-top: 8px;
  color: #172033;
  font-size: 18px;
}

.home-page .feedback-reasons {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 24px;
}

.home-page .feedback-reasons button {
  height: 42px;
  border: 1px solid #e5eaf3;
  border-radius: 999px;
  background: #f8fafc;
  color: #344054;
  font-weight: 700;
  cursor: pointer;
}

.home-page .feedback-reasons button.selected {
  border-color: #f59e0b;
  background: #fff7ed;
  color: #b45309;
}

.home-page .feedback-textarea {
  position: relative;
  margin-top: 18px;
}

.home-page .feedback-textarea textarea {
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

.home-page .feedback-textarea textarea:focus {
  border-color: #f59e0b;
}

.home-page .feedback-textarea span {
  position: absolute;
  right: 12px;
  bottom: 10px;
  color: #98a2b3;
  font-size: 12px;
}

.home-page .feedback-submit {
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

.home-page .feedback-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@keyframes homeFadeUp {
  from {
    opacity: 0;
    translate: 0 14px;
  }

  to {
    opacity: 1;
    translate: 0 0;
  }
}

@keyframes logoFloat {
  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }

  50% {
    transform: translateY(-10px) rotate(1deg);
  }
}

@keyframes logoGlow {
  0%,
  100% {
    opacity: 0.68;
    transform: scale(0.96);
  }

  50% {
    opacity: 1;
    transform: scale(1.04);
  }
}

@keyframes logoOrbit {
  to {
    transform: rotate(360deg);
  }
}

@keyframes logoDot {
  0%,
  100% {
    transform: translate(116px, -58px) scale(1);
  }

  50% {
    transform: translate(102px, -70px) scale(1.28);
  }
}

@media (prefers-reduced-motion: reduce) {
  .home-page *,
  .home-page *::before,
  .home-page *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    scroll-behavior: auto !important;
  }
}

@media (max-width: 1024px) {
  .home-page .hero-section,
  .home-page .home-columns {
    grid-template-columns: 1fr;
  }

  .home-page .service-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .home-page .law-browser {
    grid-template-columns: 150px 220px minmax(0, 1fr);
  }
}

@media (max-width: 640px) {
  .home-page {
    padding: 20px 12px 40px;
  }

  .home-page .hero-copy,
  .home-page .hero-panel,
  .home-page .law-band,
  .home-page .service-band,
  .home-page .home-panel {
    padding: 16px;
  }

  .home-page h1 {
    font-size: 28px;
  }

  .home-page .hero-search,
  .home-page .service-grid {
    grid-template-columns: 1fr;
  }

  .home-page .law-browser {
    grid-template-columns: 1fr;
    max-height: none;
  }

  .home-page .law-scroll {
    max-height: 180px;
  }

  .home-page .article-scroll {
    max-height: 240px;
  }

  .home-page .section-head,
  .home-page .order-entry {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
