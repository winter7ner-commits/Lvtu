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
            aria-label="搜索律师、法条、订单或服务类型"
            placeholder="输入律师、法条、订单号、业务类型或状态"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" size="large" @click="handleSearch">
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
          <img src="/icons/logo.png" alt="律途" width="320" height="213" fetchpriority="high" decoding="async" />
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

    <section ref="lawBandRef" class="law-band">
      <div class="section-head">
        <div>
          <p class="eyebrow">法律法规</p>
          <h2>按领域快速查看常用法条</h2>
        </div>
        <el-button class="soft-btn" :loading="loadingLegal" @click="refreshLegalCategories">刷新法规</el-button>
      </div>

      <div class="law-browser" v-loading="loadingLegal" :aria-busy="loadingLegal || loadingLegalDocuments || loadingLegalArticles">
        <div class="law-column category-column">
          <div class="law-column-title">法律分类</div>
          <div class="law-scroll">
            <div v-if="!legalLoadStarted" class="law-skeleton" aria-hidden="true">
              <span v-for="item in 5" :key="item"></span>
            </div>
            <template v-else>
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
            </template>
          </div>
        </div>

        <div class="law-column document-column" v-loading="loadingLegalDocuments">
          <div class="law-column-title">法律</div>
          <div class="law-scroll">
            <div v-if="!legalLoadStarted" class="law-skeleton" aria-hidden="true">
              <span v-for="item in 4" :key="item"></span>
            </div>
            <template v-else>
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
            </template>
          </div>
        </div>

        <div class="law-column article-column" v-loading="loadingLegalArticles">
          <div class="law-column-title">法条</div>
          <div class="article-scroll">
            <div v-if="!legalLoadStarted" class="law-skeleton article" aria-hidden="true">
              <span v-for="item in 3" :key="item"></span>
            </div>
            <template v-else>
              <button
                v-for="article in visibleLegalArticles"
                :key="article.id"
                type="button"
                class="article-row"
                @click="openArticleDetail(article)"
              >
                <strong>{{ article.articleNumber }}</strong>
                <span v-if="article.title">{{ article.title }}</span>
                <p>{{ article.preview }}</p>
              </button>
              <button v-if="hiddenLegalArticleCount > 0" type="button" class="article-more" @click="showMoreLegalArticles">
                加载更多法条（{{ hiddenLegalArticleCount }}）
              </button>
              <el-empty v-if="selectedLawDocument && !legalArticles.length && !loadingLegalArticles" description="暂无法条" />
            </template>
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
        <el-empty v-else-if="hotLawyersLoaded" description="暂无推荐律师" />
        <div v-else class="lawyer-skeleton" aria-hidden="true">
          <span v-for="item in 3" :key="item"></span>
        </div>
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

    <button type="button" class="home-back-top" aria-label="回到顶部" @click="scrollToTop">
      ↑
    </button>

    <div v-if="detailVisible" class="detail-overlay" @click.self="closeArticleDetail">
      <section
        ref="detailSheetRef"
        class="detail-sheet"
        v-loading="detailLoading"
        role="dialog"
        aria-modal="true"
        aria-labelledby="article-detail-title"
        tabindex="-1"
        @keydown="handleDialogKeydown($event, closeArticleDetail)"
      >
        <header class="detail-head">
          <button class="icon-btn" type="button" aria-label="关闭法条详情" @click="closeArticleDetail">‹</button>
          <div>
            <span>{{ selectedLawDocumentName }}</span>
            <h2 id="article-detail-title">{{ detailArticle?.articleNumber || '法条详情' }}</h2>
          </div>
          <button
            :class="['favorite-btn', { active: isFavorited }]"
            type="button"
            :aria-pressed="isFavorited"
            :aria-label="isFavorited ? '取消收藏当前法条' : '收藏当前法条'"
            @click="handleToggleFavorite"
          >
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
              <h3 aria-live="polite">全部评论（{{ commentCount }}）</h3>
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
            aria-label="输入法条评论"
            placeholder="期待有价值的评论..."
            :disabled="commentSubmitting"
            @keyup.enter="submitComment"
          />
          <button class="comment-submit" type="button" :disabled="commentSubmitting" :aria-busy="commentSubmitting" @click="submitComment">
            {{ commentSubmitting ? '发布中' : '发布' }}
          </button>
          <button
            :class="['bottom-favorite', { active: isFavorited }]"
            type="button"
            :aria-pressed="isFavorited"
            :aria-label="isFavorited ? '取消收藏当前法条' : '收藏当前法条'"
            @click="handleToggleFavorite"
          >
            <span>{{ isFavorited ? '★' : '☆' }}</span>
            {{ favoriteCount }}
          </button>
        </footer>
      </section>
    </div>

    <div v-if="feedbackVisible" class="feedback-mask" @click.self="closeFeedback">
      <section
        ref="feedbackPanelRef"
        class="feedback-panel"
        role="dialog"
        aria-modal="true"
        aria-labelledby="feedback-title"
        tabindex="-1"
        @keydown="handleDialogKeydown($event, closeFeedback)"
      >
        <button class="feedback-close" type="button" aria-label="关闭反馈面板" @click="closeFeedback">×</button>
        <p class="feedback-kicker">建议反馈</p>
        <h3 id="feedback-title">{{ feedbackTitle }}</h3>
        <div class="feedback-reasons">
          <button
            v-for="reason in feedbackReasons"
            :key="reason"
            type="button"
            :class="{ selected: feedbackReason === reason }"
            :aria-pressed="feedbackReason === reason"
            @click="feedbackReason = feedbackReason === reason ? '' : reason"
          >
            {{ reason }}
          </button>
        </div>
        <div class="feedback-textarea">
          <textarea
            v-model="feedbackContent"
            maxlength="140"
            aria-label="补充反馈内容"
            aria-describedby="feedback-count"
            :placeholder="feedbackPlaceholder"
          ></textarea>
          <span id="feedback-count" aria-live="polite">{{ feedbackContent.length }}/140</span>
        </div>
        <button class="feedback-submit" type="button" :disabled="feedbackSubmitting" :aria-busy="feedbackSubmitting" @click="submitFeedback">
          {{ feedbackSubmitting ? '提交中' : '提交' }}
        </button>
      </section>
    </div>

  </main>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
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
const loadingHotLawyers = ref(false)
const hotLawyersLoaded = ref(false)
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
const detailSheetRef = ref(null)
const feedbackPanelRef = ref(null)
const detailReturnFocusElement = ref(null)
const feedbackReturnFocusElement = ref(null)
const previousBodyOverflow = ref('')
const bodyScrollLocked = ref(false)
const lawBandRef = ref(null)
const legalLoadStarted = ref(false)
const visibleArticleLimit = ref(8)
const ARTICLE_BATCH_SIZE = 8

const documentCache = new Map()
const articleCache = new Map()
let legalObserver = null
let currentCategoryRequestId = 0
let currentDocumentRequestId = 0
let currentArticleRequestId = 0

const focusableSelector = [
  'a[href]',
  'button:not([disabled])',
  'textarea:not([disabled])',
  'input:not([disabled])',
  'select:not([disabled])',
  '[tabindex]:not([tabindex="-1"])'
].join(', ')

const quickTags = ['婚姻家事', '合同纠纷', '劳动争议', '知识产权', '待支付']
const helpfulReasons = ['描述符合', '内容有趣', '很好理解', '感受一般']
const unhelpfulReasons = ['描述不符合', '读不下去', '不太好懂', '感受一般']

const parseCurrentUser = () => {
  try {
    return JSON.parse(localStorage.getItem('currentUser') || 'null')
  } catch (error) {
    return null
  }
}

const getActiveElement = () => {
  if (typeof document === 'undefined') return null
  const activeElement = document.activeElement
  return activeElement && typeof activeElement.focus === 'function' ? activeElement : null
}

const getFocusableElements = (container) => {
  if (!container) return []
  return Array.from(container.querySelectorAll(focusableSelector)).filter((element) => {
    const disabled = element.hasAttribute('disabled') || element.getAttribute('aria-disabled') === 'true'
    return !disabled && element.getClientRects().length > 0
  })
}

const focusFirstElement = (container) => {
  if (!container || typeof container.focus !== 'function') return
  const focusableElements = getFocusableElements(container)
  const target = focusableElements[0] || container
  target.focus({ preventScroll: true })
}

const restoreFocusTo = (target, fallbackContainer = null) => {
  nextTick(() => {
    if (target && typeof document !== 'undefined' && document.contains(target) && typeof target.focus === 'function') {
      target.focus({ preventScroll: true })
      return
    }
    if (fallbackContainer) {
      focusFirstElement(fallbackContainer)
    }
  })
}

const handleDialogKeydown = (event, closeHandler) => {
  if (event.key === 'Escape') {
    event.preventDefault()
    closeHandler()
    return
  }
  if (event.key !== 'Tab') return

  const focusableElements = getFocusableElements(event.currentTarget)
  if (!focusableElements.length) {
    event.preventDefault()
    event.currentTarget.focus({ preventScroll: true })
    return
  }

  const firstElement = focusableElements[0]
  const lastElement = focusableElements[focusableElements.length - 1]

  if (event.shiftKey && document.activeElement === firstElement) {
    event.preventDefault()
    lastElement.focus({ preventScroll: true })
  } else if (!event.shiftKey && document.activeElement === lastElement) {
    event.preventDefault()
    firstElement.focus({ preventScroll: true })
  }
}

const prefersReducedMotion = () => (
  typeof window !== 'undefined'
  && typeof window.matchMedia === 'function'
  && window.matchMedia('(prefers-reduced-motion: reduce)').matches
)

const updateBodyScrollLock = () => {
  if (typeof document === 'undefined') return
  const shouldLock = detailVisible.value || feedbackVisible.value

  if (shouldLock && !bodyScrollLocked.value) {
    previousBodyOverflow.value = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    bodyScrollLocked.value = true
    return
  }

  if (!shouldLock && bodyScrollLocked.value) {
    document.body.style.overflow = previousBodyOverflow.value
    bodyScrollLocked.value = false
  }
}

const currentUserId = computed(() => {
  const currentUser = parseCurrentUser()
  return currentUser?.userId || Number(localStorage.getItem('userId')) || null
})
const isLawyerAccount = computed(() => {
  const currentUser = parseCurrentUser()
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
const visibleLegalArticles = computed(() => legalArticles.value.slice(0, visibleArticleLimit.value))
const hiddenLegalArticleCount = computed(() => Math.max(legalArticles.value.length - visibleArticleLimit.value, 0))

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

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: prefersReducedMotion() ? 'auto' : 'smooth' })
}

const scheduleIdleTask = (task, timeout = 900) => {
  if (typeof window !== 'undefined' && typeof window.requestIdleCallback === 'function') {
    window.requestIdleCallback(task, { timeout })
    return
  }
  window.setTimeout(task, Math.min(timeout, 300))
}

const startLegalLoad = () => {
  if (legalLoadStarted.value || loadingLegal.value) return
  legalLoadStarted.value = true
  loadLegalCategories()
}

const setupLegalObserver = () => {
  if (typeof window === 'undefined' || legalLoadStarted.value) return

  if (!lawBandRef.value || typeof window.IntersectionObserver !== 'function') {
    scheduleIdleTask(startLegalLoad, 1200)
    return
  }

  legalObserver = new window.IntersectionObserver((entries) => {
    if (!entries.some((entry) => entry.isIntersecting)) return

    legalObserver?.disconnect()
    legalObserver = null
    startLegalLoad()
  }, { rootMargin: '220px 0px' })

  legalObserver.observe(lawBandRef.value)
}

const showMoreLegalArticles = () => {
  visibleArticleLimit.value += ARTICLE_BATCH_SIZE
}

const loadHotLawyers = async () => {
  loadingHotLawyers.value = true
  try {
    const list = unwrapData(await getTopRatedLawyers(4))
    hotLawyers.value = Array.isArray(list) ? list : []
  } catch (error) {
    hotLawyers.value = []
  } finally {
    hotLawyersLoaded.value = true
    loadingHotLawyers.value = false
  }
}

const loadLegalCategories = async ({ force = false } = {}) => {
  const requestId = ++currentCategoryRequestId
  legalLoadStarted.value = true
  loadingLegal.value = true
  try {
    if (force) {
      documentCache.clear()
      articleCache.clear()
      legalDocuments.value = []
      legalArticles.value = []
      selectedLawCategory.value = null
      selectedLawDocument.value = null
    }
    const list = unwrapData(await getCategories())
    if (requestId !== currentCategoryRequestId) return
    legalCategories.value = Array.isArray(list) ? list : []
    if (legalCategories.value.length > 0) {
      await selectLawCategory(legalCategories.value[0].id, { force })
    } else {
      legalDocuments.value = []
      legalArticles.value = []
      selectedLawCategory.value = null
      selectedLawDocument.value = null
    }
  } catch (error) {
    if (requestId !== currentCategoryRequestId) return
    legalCategories.value = []
    legalDocuments.value = []
    legalArticles.value = []
    selectedLawCategory.value = null
    selectedLawDocument.value = null
    ElMessage.error(error?.response?.data?.message || '法规加载失败，请稍后重试')
  } finally {
    if (requestId === currentCategoryRequestId) {
      loadingLegal.value = false
    }
  }
}

const refreshLegalCategories = () => {
  legalObserver?.disconnect()
  legalObserver = null
  return loadLegalCategories({ force: true })
}

const selectLawCategory = async (categoryId, options = {}) => {
  const { force = false } = options
  if (!force && selectedLawCategory.value === categoryId && legalDocuments.value.length) return

  const requestId = ++currentDocumentRequestId
  selectedLawCategory.value = categoryId
  selectedLawDocument.value = null
  legalDocuments.value = []
  legalArticles.value = []
  visibleArticleLimit.value = ARTICLE_BATCH_SIZE

  if (!force && documentCache.has(categoryId)) {
    legalDocuments.value = documentCache.get(categoryId)
    if (legalDocuments.value.length > 0) {
      await selectLawDocument(legalDocuments.value[0].id)
    }
    return
  }

  loadingLegalDocuments.value = true

  try {
    const list = unwrapData(await getDocumentsByCategory(categoryId))
    if (requestId !== currentDocumentRequestId) return
    const documents = Array.isArray(list) ? list : []
    legalDocuments.value = documents
    documentCache.set(categoryId, documents)
    if (legalDocuments.value.length > 0) {
      await selectLawDocument(legalDocuments.value[0].id)
    }
  } catch (error) {
    if (requestId !== currentDocumentRequestId) return
    legalDocuments.value = []
    legalArticles.value = []
    selectedLawDocument.value = null
    ElMessage.error(error?.response?.data?.message || '法律列表加载失败')
  } finally {
    if (requestId === currentDocumentRequestId) {
      loadingLegalDocuments.value = false
    }
  }
}

const selectLawDocument = async (documentId, options = {}) => {
  const { force = false } = options
  if (!force && selectedLawDocument.value === documentId && legalArticles.value.length) return

  const requestId = ++currentArticleRequestId
  selectedLawDocument.value = documentId
  legalArticles.value = []
  visibleArticleLimit.value = ARTICLE_BATCH_SIZE

  if (!force && articleCache.has(documentId)) {
    legalArticles.value = articleCache.get(documentId)
    return
  }

  loadingLegalArticles.value = true

  try {
    const list = unwrapData(await getArticlesByDocument(documentId))
    if (requestId !== currentArticleRequestId) return
    const articles = Array.isArray(list)
      ? list.map((article) => ({
          ...article,
          preview: truncateArticle(article.content)
        }))
      : []
    legalArticles.value = articles
    articleCache.set(documentId, articles)
  } catch (error) {
    if (requestId !== currentArticleRequestId) return
    legalArticles.value = []
    ElMessage.error(error?.response?.data?.message || '法条加载失败')
  } finally {
    if (requestId === currentArticleRequestId) {
      loadingLegalArticles.value = false
    }
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

const openArticleDetail = async (article, options = {}) => {
  const { preserveFocus = false } = options
  if (!preserveFocus) {
    detailReturnFocusElement.value = getActiveElement()
    selectedDetail.value = null
  }
  detailVisible.value = true
  detailLoading.value = true
  commentDraft.value = ''
  closeFeedback({ restoreFocus: false })
  await nextTick()
  if (!preserveFocus) {
    focusFirstElement(detailSheetRef.value)
  }

  try {
    const response = await getArticleDetail(article.id, currentUserId.value)
    selectedDetail.value = unwrapData(response)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载法条详情失败')
    if (!preserveFocus) {
      closeArticleDetail()
    }
  } finally {
    detailLoading.value = false
  }
}

const closeArticleDetail = () => {
  detailVisible.value = false
  closeFeedback({ restoreFocus: false })
  restoreFocusTo(detailReturnFocusElement.value)
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
    if (!data) throw new Error('Empty favorite response')
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
  if (commentSubmitting.value) return
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
    await openArticleDetail(currentArticle, { preserveFocus: true })
    ElMessage.success('评论已发布')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '评论发布失败')
  } finally {
    commentSubmitting.value = false
  }
}

const openFeedback = async (helpful) => {
  feedbackReturnFocusElement.value = getActiveElement()
  feedbackHelpful.value = helpful
  feedbackReason.value = ''
  feedbackContent.value = ''
  feedbackVisible.value = true
  await nextTick()
  focusFirstElement(feedbackPanelRef.value)
}

const closeFeedback = (options = {}) => {
  const { restoreFocus = true } = options
  feedbackVisible.value = false
  feedbackReason.value = ''
  feedbackContent.value = ''
  if (restoreFocus) {
    restoreFocusTo(feedbackReturnFocusElement.value, detailVisible.value ? detailSheetRef.value : null)
  }
}

const submitFeedback = async () => {
  if (feedbackSubmitting.value) return
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

watch([detailVisible, feedbackVisible], updateBodyScrollLock)

onMounted(() => {
  scheduleIdleTask(loadHotLawyers, 500)
  nextTick(setupLegalObserver)
})

onBeforeUnmount(() => {
  legalObserver?.disconnect()
  legalObserver = null
  if (typeof document !== 'undefined' && bodyScrollLocked.value) {
    document.body.style.overflow = previousBodyOverflow.value
  }
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

.home-page button:focus-visible,
.home-page input:focus-visible,
.home-page textarea:focus-visible,
.home-page [tabindex]:focus-visible {
  outline: 3px solid rgba(37, 99, 235, 0.28);
  outline-offset: 2px;
}

.home-page :deep(.el-button:focus-visible) {
  outline: 3px solid rgba(37, 99, 235, 0.28);
  outline-offset: 2px;
}

.home-page :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.18);
}

.home-page .hero-section,
.home-page .law-band,
.home-page .service-band,
.home-page .home-columns {
  width: min(1180px, 100%);
  margin-left: auto;
  margin-right: auto;
}

.home-page .law-band,
.home-page .service-band,
.home-page .home-columns {
  content-visibility: auto;
  contain-intrinsic-size: 420px;
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
  box-shadow: none;
}

.home-page .hero-copy,
.home-page .hero-panel {
  min-width: 0;
}

.home-page .hero-copy {
  padding: 34px;
}

.home-page .eyebrow {
  margin: 0 0 8px;
  color: #667085;
  font-size: 13px;
  font-weight: 700;
}

.home-page h1,
.home-page h2,
.home-page h3,
.home-page .subtext {
  margin: 0;
  overflow-wrap: anywhere;
}

.home-page h1 {
  max-width: 720px;
  color: #172033;
  font-size: 38px;
  font-weight: 700;
  line-height: 1.18;
  letter-spacing: 0;
}

.home-page h2 {
  color: #172033;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0;
}

.home-page h3 {
  color: #172033;
  font-size: 18px;
  font-weight: 700;
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
  min-height: 44px;
  padding: 8px 12px;
  border: 1px solid #dbe4f0;
  border-radius: 999px;
  background: #f8fafc;
  color: #475467;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
}

.home-page .quick-tags button:hover {
  border-color: #cbd5e1;
  color: #334155;
  background: #f1f5f9;
}

.home-page .quick-tags button:active {
  background: #e5eaf3;
}

.home-page .hero-panel {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: stretch;
  padding: 24px;
  overflow: hidden;
  contain: paint;
}

.home-page .logo-stage {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 236px;
  padding: 14px;
  isolation: isolate;
}

.home-page .logo-stage::before {
  content: '';
  position: absolute;
  inset: 18px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #f8fafc;
  z-index: -1;
}

.home-page .hero-panel img {
  display: block;
  width: min(100%, 320px);
  max-width: 100%;
  height: auto;
  max-height: 210px;
  margin: 0 auto;
  object-fit: contain;
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
  background: #f8fafc;
  text-align: center;
}

.home-page .hero-metrics strong {
  display: block;
  color: #334155;
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

.home-page .section-head > div,
.home-page .order-entry > div,
.home-page .detail-head > div,
.home-page .lawyer-row > div,
.home-page .comment-item > div {
  min-width: 0;
}

.home-page .section-head {
  margin-bottom: 18px;
}

.home-page .section-head > span {
  color: #475467;
  font-weight: 700;
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
  transition:
    border-color 0.18s ease,
    box-shadow 0.18s ease,
    transform 0.18s ease;
}

.home-page .lawyer-row:hover,
.home-page .service-tile:hover {
  border-color: #cbd5e1;
  box-shadow: none;
  transform: none;
}

.home-page .lawyer-row:active,
.home-page .service-tile:active {
  background: #f8fafc;
}

.home-page .lawyer-row strong {
  display: block;
  color: #172033;
  font-size: 15px;
  font-weight: 700;
  overflow-wrap: anywhere;
}

.home-page .lawyer-row span {
  display: block;
  margin-top: 4px;
  color: #667085;
  font-size: 12px;
  overflow-wrap: anywhere;
}

.home-page .soft-btn {
  border-color: #d6e4ff;
  color: #1d4ed8;
  background: #ffffff;
}

.home-page .soft-btn:hover,
.home-page .soft-btn:focus {
  border-color: #b7cffb;
  color: #1d4ed8;
  background: #f8fbff;
}

.home-page .soft-btn:active {
  background: #eff6ff;
}

.home-page .home-back-top {
  position: fixed;
  right: 34px;
  bottom: 42px;
  z-index: 900;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 44px;
  height: 44px;
  padding: 0;
  border: 1px solid #d6e4ff;
  border-radius: 8px;
  background: #ffffff;
  color: #475467;
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
  font-weight: 700;
  box-shadow: none;
  transition:
    background-color 0.18s ease,
    border-color 0.18s ease,
    color 0.18s ease,
    transform 0.18s ease;
}

.home-page .home-back-top:hover {
  background: #f8fafc;
  border-color: #d9e1e8;
  color: #334155;
}

.home-page .home-back-top:active {
  background: #eef3fa;
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
  contain: layout paint;
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
  font-weight: 700;
}

.home-page .law-scroll,
.home-page .article-scroll {
  max-height: 312px;
  padding: 10px;
  overflow-y: auto;
  content-visibility: auto;
  contain-intrinsic-size: 312px;
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

.home-page .law-skeleton,
.home-page .lawyer-skeleton {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.home-page .law-skeleton span,
.home-page .lawyer-skeleton span {
  display: block;
  border-radius: 8px;
  background: linear-gradient(90deg, #eef3fa, #f8fafc 46%, #eef3fa);
  background-size: 180% 100%;
  animation: homeSkeleton 1.2s ease-in-out infinite;
}

.home-page .law-skeleton span {
  height: 40px;
}

.home-page .law-skeleton.article span {
  height: 82px;
}

.home-page .lawyer-skeleton span {
  height: 72px;
  border: 1px solid #e5eaf3;
}

.home-page .law-row {
  display: block;
  width: 100%;
  min-height: 44px;
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

.home-page .law-row:active {
  background: #e5eaf3;
}

.home-page .law-row.active {
  background: #2563eb;
  color: #ffffff;
  font-weight: 600;
}

.home-page .article-row {
  width: 100%;
  min-width: 0;
  padding: 14px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  text-align: left;
  cursor: pointer;
  content-visibility: auto;
  contain-intrinsic-size: 96px;
  transition:
    border-color 0.18s ease,
    box-shadow 0.18s ease,
    transform 0.18s ease;
}

.home-page .article-row:hover {
  border-color: #cbd5e1;
  box-shadow: none;
  transform: none;
}

.home-page .article-row:active {
  background: #f8fafc;
}

.home-page .article-row strong {
  color: #334155;
  font-size: 15px;
}

.home-page .article-row span {
  margin-left: 8px;
  color: #667085;
  font-size: 13px;
  overflow-wrap: anywhere;
}

.home-page .article-row p {
  margin: 8px 0 0;
  color: #344054;
  line-height: 1.7;
  word-break: break-word;
}

.home-page .article-more {
  width: 100%;
  min-height: 44px;
  border: 1px solid #d6e4ff;
  border-radius: 8px;
  background: #f8fafc;
  color: #475467;
  cursor: pointer;
  font-weight: 600;
}

.home-page .article-more:hover {
  border-color: #cbd5e1;
  background: #f1f5f9;
}

.home-page .article-more:active {
  background: #e5eaf3;
}

.home-page .service-tile {
  min-height: 126px;
  padding: 16px;
  overflow-wrap: anywhere;
}

.home-page .service-tile span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 999px;
  background: #f1f5f9;
  color: #475467;
  font-weight: 600;
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
  background: #f1f5f9;
  color: #475467;
  font-weight: 700;
}

.home-page .lawyer-row em {
  color: #475467;
  font-style: normal;
  font-weight: 700;
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
  width: 44px;
  height: 44px;
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
  font-weight: 700;
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
  min-height: 44px;
  padding: 9px 14px;
  background: #f3f6fb;
  color: #475467;
}

.home-page .favorite-btn.active,
.home-page .bottom-favorite.active {
  color: #334155;
  background: #f1f5f9;
}

.home-page .favorite-btn:hover,
.home-page .bottom-favorite:hover {
  background: #eef3fa;
  color: #334155;
}

.home-page .favorite-btn:active,
.home-page .bottom-favorite:active {
  background: #e5eaf3;
}

.home-page .detail-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 24px 26px 120px;
  content-visibility: auto;
  contain-intrinsic-size: 680px;
}

.home-page .section-label {
  margin-bottom: 12px;
  color: #667085;
  font-size: 14px;
  font-weight: 600;
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
  font-weight: 700;
}

.home-page .article-full-text p,
.home-page .explanation-block p {
  color: #2f3a4c;
  font-size: 18px;
  line-height: 2;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.home-page .explanation-feedback-actions {
  display: flex;
  gap: 12px;
  margin-top: 18px;
}

.home-page .explanation-feedback-actions button {
  min-width: 88px;
  min-height: 44px;
  padding: 8px 14px;
  border: 1px solid #d8dee9;
  border-radius: 999px;
  background: #ffffff;
  color: #344054;
  font-weight: 700;
  cursor: pointer;
}

.home-page .explanation-feedback-actions button:hover {
  border-color: #cbd5e1;
  color: #334155;
  background: #f8fafc;
}

.home-page .explanation-feedback-actions button:active {
  background: #eef3fa;
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
  background: #f1f5f9;
  color: #475467;
  font-weight: 600;
}

.home-page .comment-meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.home-page .comment-meta strong {
  color: #172033;
  font-weight: 600;
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

.home-page .detail-actions input:focus-visible {
  background: #ffffff;
  outline: 3px solid rgba(37, 99, 235, 0.28);
  outline-offset: 2px;
}

.home-page .comment-submit {
  height: 44px;
  padding: 0 18px;
  background: #2563eb;
  color: #ffffff;
}

.home-page .comment-submit:hover:not(:disabled) {
  background: #1e40af;
}

.home-page .comment-submit:active:not(:disabled) {
  background: #1d4ed8;
}

.home-page .comment-submit:disabled {
  background: #98a2b3;
  opacity: 1;
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
  max-height: min(720px, calc(100vh - 48px));
  overflow-y: auto;
  padding: 30px 24px 26px;
  border: 1px solid #e5eaf3;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.14);
  text-align: center;
}

.home-page .feedback-close {
  position: absolute;
  right: 10px;
  top: 8px;
  width: 44px;
  height: 44px;
  border: 0;
  border-radius: 50%;
  background: #f3f6fb;
  color: #667085;
  font-size: 20px;
  cursor: pointer;
}

.home-page .feedback-close:hover {
  background: #eef3fa;
  color: #334155;
}

.home-page .feedback-close:active {
  background: #e5eaf3;
}

.home-page .feedback-kicker {
  color: #98a2b3;
  font-size: 13px;
}

.home-page .feedback-panel h3 {
  margin-top: 8px;
  color: #172033;
  font-size: 18px;
  font-weight: 700;
}

.home-page .feedback-reasons {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 24px;
}

.home-page .feedback-reasons button {
  min-height: 44px;
  padding: 8px 12px;
  border: 1px solid #e5eaf3;
  border-radius: 999px;
  background: #f8fafc;
  color: #344054;
  font-weight: 700;
  cursor: pointer;
}

.home-page .feedback-reasons button.selected {
  border-color: #cbd5e1;
  background: #f1f5f9;
  color: #334155;
}

.home-page .feedback-reasons button:hover {
  border-color: #cbd5e1;
  background: #f1f5f9;
}

.home-page .feedback-reasons button:active {
  background: #e5eaf3;
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
  overflow-wrap: anywhere;
}

.home-page .feedback-textarea textarea:focus {
  border-color: #2563eb;
}

.home-page .feedback-textarea textarea:focus-visible {
  outline: 3px solid rgba(37, 99, 235, 0.22);
  outline-offset: 2px;
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
  background: #475467;
  color: #ffffff;
  font-weight: 600;
  cursor: pointer;
}

.home-page .feedback-submit:hover:not(:disabled) {
  background: #344054;
}

.home-page .feedback-submit:active:not(:disabled) {
  background: #172033;
}

.home-page .feedback-submit:disabled {
  background: #98a2b3;
  opacity: 1;
  cursor: not-allowed;
}

@keyframes homeSkeleton {
  0%,
  100% {
    background-position: 100% 0;
  }

  50% {
    background-position: 0 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .home-page *,
  .home-page *::before,
  .home-page *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
    transition-delay: 0ms !important;
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

  .home-page .home-back-top {
    right: 14px;
    bottom: 18px;
  }
}
</style>
