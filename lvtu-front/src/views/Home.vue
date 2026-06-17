<template>
  <main
    ref="homePageRef"
    :class="['home-page', { 'wave-active': waveAnimationActive, 'reveal-ready': revealMotionReady }]"
  >
    <section class="hero-section">
      <div class="hero-copy">
        <p class="eyebrow">律途法律服务平台</p>
        <h1>让每一个法律问题都有清晰路径</h1>
        <p class="subtext">从律师匹配、法规查询到咨询下单与订单跟进，律途帮助你更快找到可信赖的专业支持。</p>

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

      <div class="hero-panel" aria-label="平台服务概览">
        <div class="logo-stage">
          <img src="/icons/logo.png" alt="律途" width="320" height="213" fetchpriority="high" decoding="async" />
        </div>
      </div>
    </section>

    <section v-if="!isLawyerAccount" class="service-band">
      <div class="section-head">
        <div>
          <p class="eyebrow">常见法律需求</p>
          <h2>梳理<span class="service-title-accent">常见法律场景</span>，按需匹配专业支持</h2>
        </div>
      </div>

      <div class="service-grid">
        <article
          v-for="service in services"
          :key="service.key"
          class="service-tile"
        >
          <span v-if="service.common" class="service-badge">常用</span>
          <span class="service-initial">{{ service.short }}</span>
          <strong>{{ service.name }}</strong>
          <small>{{ service.desc }}</small>
        </article>
      </div>
    </section>

    <div v-if="!isLawyerAccount" class="home-section-divider" aria-hidden="true"></div>

    <section v-if="!isLawyerAccount" class="advisor-band" aria-labelledby="advisor-band-title">
      <div class="advisor-visual">
        <img src="/Home_pic2.png" alt="在线咨询、电话咨询、文书代写、合同审核等法律服务方式" loading="lazy" decoding="async" />
      </div>

      <div class="advisor-copy">
        <p class="eyebrow">专业法律支持</p>
        <h2 id="advisor-band-title">连接合适的<span>法律顾问</span></h2>
        <p>
          无论是在线咨询、电话沟通、文书代写还是合同审核，都可以先发布服务需求，让律师基于问题背景给出处理建议。
        </p>
        <el-button class="advisor-cta" type="primary" @click="router.push('/order-create')">去发布服务</el-button>
      </div>
    </section>

    <section ref="lawBandRef" class="law-band">
      <div class="section-head">
        <div>
          <p class="eyebrow">法律法规</p>
          <h2>按领域快速查看常用法条</h2>
        </div>
        <el-button class="soft-btn" @click="router.push('/laws')">所有法规</el-button>
      </div>

      <!-- 三栏共用一个浏览器容器，通过细分隔线表达从概览进入工具区的层级。 -->
      <div class="law-browser" :aria-busy="loadingLegal || loadingLegalDocuments || loadingLegalArticles">
        <div class="law-column category-column">
          <div class="law-column-title">法律分类</div>
          <div class="law-scroll">
            <div v-if="!legalLoadStarted || loadingLegal" class="law-skeleton" aria-hidden="true">
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

        <div class="law-column document-column">
          <div class="law-column-title">法律</div>
          <div class="law-scroll">
            <div v-if="!legalLoadStarted || loadingLegal || loadingLegalDocuments" class="law-skeleton" aria-hidden="true">
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

        <div class="law-column article-column">
          <div class="law-column-title">法条</div>
          <div class="article-scroll">
            <div v-if="!legalLoadStarted || loadingLegal || loadingLegalArticles" class="law-skeleton article" aria-hidden="true">
              <span v-for="item in 3" :key="item"></span>
            </div>
            <template v-else>
              <button
                v-for="article in visibleLegalArticles"
                :key="article.id"
                type="button"
                :class="['article-row', { active: selectedLegalArticle === article.id }]"
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

    <section class="home-columns">
      <article class="home-panel" :aria-busy="loadingHotLawyers">
        <div class="section-head panel-head compact">
          <div>
            <p class="eyebrow">律师推荐</p>
            <h2>高评分律师</h2>
          </div>
          <el-button link type="primary" @click="router.push('/lawyer-list')">查看更多</el-button>
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
        <div class="section-head panel-head compact">
          <div>
            <p class="eyebrow">办事进度</p>
            <h2>个人订单</h2>
          </div>
          <el-button link type="primary" @click="goToOrders">查看订单</el-button>
        </div>
        <div v-if="currentUserId" class="order-entry">
          <div>
            <strong>跟进咨询、支付、服务结果与评价</strong>
            <p>可在首页搜索自己的订单号、状态和业务类型。</p>
          </div>
          <el-button type="primary" plain @click="goToOrders">查看订单</el-button>
        </div>
        <!-- 未登录时保留订单模块位置，避免登录前后双栏布局跳动。 -->
        <div v-else class="order-entry order-entry-placeholder">
          <div>
            <strong>登录后查看个人订单进度</strong>
            <p>可跟进咨询、支付、服务结果与评价，也可以用订单号快速检索。</p>
          </div>
          <el-button type="primary" plain @click="goToOrders">登录查看</el-button>
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
        </header>

        <div v-if="detailArticle" class="detail-scroll">
          <section class="article-full-text detail-primary">
            <div class="section-label">条文原文</div>
            <h3 v-if="detailArticle.title">{{ detailArticle.title }}</h3>
            <p>{{ detailArticle.content }}</p>
          </section>

          <aside class="detail-secondary">
            <section class="explanation-block">
              <div class="section-label">解释</div>
              <p v-if="selectedDetail?.explanation?.content">{{ selectedDetail.explanation.content }}</p>
              <el-empty v-else description="暂无解释" />
              <div class="explanation-feedback-actions">
                <button type="button" @click="openFeedback(true)">
                  <span aria-hidden="true">👍</span>
                  有帮助
                </button>
                <button type="button" @click="openFeedback(false)">
                  <span aria-hidden="true">👎</span>
                  无帮助
                </button>
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
          </aside>
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
        <button
          class="feedback-submit"
          type="button"
          :disabled="feedbackSubmitting || !feedbackReason"
          :aria-busy="feedbackSubmitting"
          @click="submitFeedback"
        >
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
const selectedLegalArticle = ref(null)
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
const previousBodyPaddingRight = ref('')
const bodyScrollLocked = ref(false)
const homePageRef = ref(null)
const lawBandRef = ref(null)
const waveAnimationActive = ref(false)
const revealMotionReady = ref(false)
const legalLoadStarted = ref(false)
const visibleArticleLimit = ref(8)
const ARTICLE_BATCH_SIZE = 8

const documentCache = new Map()
const articleCache = new Map()
let legalObserver = null
let revealObserver = null
let revealTargets = []
let waveScrollFrame = 0
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
    const scrollbarWidth = typeof window !== 'undefined'
      ? window.innerWidth - document.documentElement.clientWidth
      : 0
    previousBodyOverflow.value = document.body.style.overflow
    previousBodyPaddingRight.value = document.body.style.paddingRight
    if (scrollbarWidth > 0) {
      const currentPaddingRight = Number.parseFloat(window.getComputedStyle(document.body).paddingRight) || 0
      document.body.style.paddingRight = `${currentPaddingRight + scrollbarWidth}px`
    }
    document.body.style.overflow = 'hidden'
    bodyScrollLocked.value = true
    return
  }

  if (!shouldLock && bodyScrollLocked.value) {
    document.body.style.overflow = previousBodyOverflow.value
    document.body.style.paddingRight = previousBodyPaddingRight.value
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
  {
    key: 'labor-dispute',
    short: '劳',
    name: '劳动争议',
    type: 'ONLINE_CONSULT',
    desc: '围绕劳动合同、工资社保、工伤认定和仲裁维权梳理方案。',
    common: true
  },
  {
    key: 'marriage-family',
    short: '婚',
    name: '婚姻家事',
    type: 'MARRIAGE_FAMILY',
    desc: '处理离婚、抚养、继承、财产分割等家庭法律问题。'
  },
  {
    key: 'contract-dispute',
    short: '合',
    name: '合同纠纷',
    type: 'CONTRACT_REVIEW',
    desc: '协助审查合同风险、违约责任、款项追偿和争议处理路径。'
  },
  {
    key: 'intellectual-property',
    short: '知',
    name: '知识产权',
    type: 'ONLINE_CONSULT',
    desc: '支持商标、著作权、专利保护及侵权线索的初步判断。'
  }
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

const revealVisibleTargets = () => {
  if (typeof window === 'undefined' || !revealMotionReady.value) return
  const triggerLine = window.innerHeight * 0.84

  revealTargets.forEach((target) => {
    if (target.classList.contains('is-visible')) return
    const rect = target.getBoundingClientRect()
    if (rect.top < triggerLine && rect.bottom > 0) {
      target.classList.add('is-visible')
      revealObserver?.unobserve(target)
    }
  })
}

const enableRevealMotion = () => {
  if (revealMotionReady.value || prefersReducedMotion()) return
  revealMotionReady.value = true

  nextTick(() => {
    window.requestAnimationFrame(revealVisibleTargets)
  })
}

const setupHomeRevealObserver = () => {
  if (
    typeof window === 'undefined'
    || prefersReducedMotion()
    || !homePageRef.value
    || typeof window.IntersectionObserver !== 'function'
  ) {
    return
  }

  revealTargets = Array.from(
    homePageRef.value.querySelectorAll('.service-band, .advisor-band, .law-band, .home-columns')
  )
  if (!revealTargets.length) return

  revealObserver = new window.IntersectionObserver((entries) => {
    if (!revealMotionReady.value) return

    entries.forEach((entry) => {
      if (!entry.isIntersecting) return
      entry.target.classList.add('is-visible')
      revealObserver?.unobserve(entry.target)
    })
  }, {
    rootMargin: '0px 0px -16% 0px',
    threshold: 0.12
  })

  revealTargets.forEach((target) => {
    target.classList.remove('is-visible')
    revealObserver.observe(target)
  })
}

const updateWaveAnimationState = () => {
  if (typeof window === 'undefined' || prefersReducedMotion()) return

  const shouldActivateWave = window.scrollY > 12
  if (waveAnimationActive.value !== shouldActivateWave) {
    waveAnimationActive.value = shouldActivateWave
  }
  if (shouldActivateWave) {
    enableRevealMotion()
  }
}

const handleHomeScrollMotion = () => {
  if (waveScrollFrame || typeof window === 'undefined') return

  waveScrollFrame = window.requestAnimationFrame(() => {
    waveScrollFrame = 0
    updateWaveAnimationState()
    revealVisibleTargets()
  })
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
      selectedLegalArticle.value = null
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
      selectedLegalArticle.value = null
    }
  } catch (error) {
    if (requestId !== currentCategoryRequestId) return
    legalCategories.value = []
    legalDocuments.value = []
    legalArticles.value = []
    selectedLawCategory.value = null
    selectedLawDocument.value = null
    selectedLegalArticle.value = null
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
  selectedLegalArticle.value = null
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
    selectedLegalArticle.value = null
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
  selectedLegalArticle.value = null
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
  selectedLegalArticle.value = article.id
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
  if (feedbackSubmitting.value || !feedbackReason.value) return
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

const goToOrders = () => {
  if (!currentUserId.value) {
    promptLogin(router, router.currentRoute.value.fullPath, '登录或注册后，你可以查看个人订单进度。')
    return
  }
  router.push('/orders')
}

const getInitials = (name = '律师') => name.slice(-2)

watch([detailVisible, feedbackVisible], updateBodyScrollLock)

onMounted(() => {
  scheduleIdleTask(loadHotLawyers, 500)
  nextTick(() => {
    setupLegalObserver()
    setupHomeRevealObserver()
    if (typeof window !== 'undefined' && !prefersReducedMotion()) {
      window.addEventListener('scroll', handleHomeScrollMotion, { passive: true })
    }
  })
})

onBeforeUnmount(() => {
  legalObserver?.disconnect()
  legalObserver = null
  revealObserver?.disconnect()
  revealObserver = null
  revealTargets = []
  if (typeof window !== 'undefined') {
    window.removeEventListener('scroll', handleHomeScrollMotion)
    if (waveScrollFrame) {
      window.cancelAnimationFrame(waveScrollFrame)
      waveScrollFrame = 0
    }
  }
  if (typeof document !== 'undefined' && bodyScrollLocked.value) {
    document.body.style.overflow = previousBodyOverflow.value
    document.body.style.paddingRight = previousBodyPaddingRight.value
  }
})
</script>

<style scoped>
.home-page {
  --home-ease-out: cubic-bezier(0.22, 1, 0.36, 1);
  --home-ease-expo: cubic-bezier(0.16, 1, 0.3, 1);
  min-height: 100vh;
  padding: 0 20px 56px;
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
.home-page a:focus-visible,
.home-page [tabindex]:focus-visible {
  outline: 2px solid #2563eb;
  outline-offset: 2px;
}

.home-page :deep(.el-button:focus-visible) {
  outline: 2px solid #2563eb;
  outline-offset: 2px;
}

.home-page :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #2563eb;
}

.home-page .hero-section,
.home-page .law-band,
.home-page .service-band,
.home-page .home-section-divider,
.home-page .advisor-band,
.home-page .home-columns {
  width: min(1180px, 100%);
  margin-left: auto;
  margin-right: auto;
}

.home-page .law-band,
.home-page .advisor-band,
.home-page .home-columns {
  content-visibility: auto;
  contain-intrinsic-size: 420px;
}

.home-page .hero-section {
  position: relative;
  isolation: isolate;
  overflow: hidden;
  display: grid;
  grid-template-columns: minmax(0, 560px) minmax(420px, 560px);
  gap: clamp(44px, 6vw, 92px);
  align-items: center;
  justify-content: center;
  width: 100vw;
  max-width: none;
  min-height: clamp(540px, calc(70svh - 90px), 720px);
  margin-left: calc(50% - 50vw);
  margin-right: calc(50% - 50vw);
  padding: calc(92px + var(--home-header-overlap, 0px)) max(32px, calc((100vw - 1320px) / 2 + 48px)) 190px;
  border-radius: 0;
  background:
    linear-gradient(180deg, #2f6edf 0%, #2f6edf 5%, rgba(47, 110, 223, 0.88) 24%, rgba(37, 99, 235, 0.56) 60%, rgba(245, 247, 251, 0.38) 88%, #f5f7fb 100%),
    linear-gradient(90deg, rgba(24, 64, 175, 0.94) 0%, rgba(37, 99, 235, 0.76) 52%, rgba(30, 64, 175, 0.66) 100%),
    url('/Home_court.png') center 43% / cover no-repeat;
}

.home-page .hero-section::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background:
    radial-gradient(70% 42% at 50% -8%, rgba(255, 255, 255, 0.34), transparent 72%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.18) 0%, transparent 44%),
    linear-gradient(105deg, transparent 0 22%, rgba(255, 255, 255, 0.13) 29%, transparent 38% 100%);
  opacity: 0.55;
  transform: translate3d(0, 0, 0);
}

.home-page .hero-section::after {
  content: '';
  position: absolute;
  left: -7vw;
  right: -7vw;
  bottom: -1px;
  z-index: 0;
  height: 204px;
  pointer-events: none;
  background:
    url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1440 220' preserveAspectRatio='none'%3E%3Cpath d='M0 68 C130 120 240 150 380 142 C520 134 625 88 758 74 C903 59 1025 103 1166 113 C1288 122 1372 98 1440 78 L1440 220 L0 220 Z' fill='%23f5f7fb'/%3E%3C/svg%3E") center bottom / 108% 100% no-repeat;
  transform: translate3d(0, 0, 0);
}

.home-page.wave-active .hero-section::before {
  animation: heroWaterSweep 1200ms var(--home-ease-expo) both;
}

.home-page.wave-active .hero-section::after {
  animation:
    heroWaveRise 1080ms var(--home-ease-expo) both,
    heroWaveDrift 9800ms ease-in-out 1080ms infinite alternate;
}

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
  position: relative;
  z-index: 1;
}

.home-page .hero-copy {
  width: min(100%, 560px);
  max-width: 560px;
  padding: 0;
  align-self: center;
}

.home-page .hero-copy > .eyebrow,
.home-page .hero-copy > h1,
.home-page .hero-copy > .subtext,
.home-page .hero-search,
.home-page .quick-tags,
.home-page .hero-panel {
  animation: homeTopFloatIn 880ms var(--home-ease-out) both;
}

.home-page .hero-copy > .eyebrow {
  animation-delay: 120ms;
}

.home-page .hero-copy > h1 {
  animation-delay: 210ms;
}

.home-page .hero-copy > .subtext {
  animation-delay: 300ms;
}

.home-page .hero-search {
  animation-delay: 420ms;
}

.home-page .quick-tags {
  animation-delay: 540ms;
}

.home-page .hero-panel {
  animation-delay: 340ms;
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
  font-size: 30px;
  font-weight: 800;
  line-height: 1.2;
  letter-spacing: 0;
}

.home-page .hero-section .eyebrow {
  color: rgba(255, 255, 255, 0.78);
}

.home-page .hero-section h1 {
  color: #ffffff;
  font-size: 42px;
  line-height: 1.16;
}

.home-page .hero-section .subtext {
  max-width: 540px;
  color: rgba(255, 255, 255, 0.88);
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
  flex-wrap: nowrap;
  gap: 10px;
  max-width: 100%;
  margin-top: 16px;
  overflow-x: auto;
  overscroll-behavior-inline: contain;
  padding-bottom: 2px;
  scrollbar-width: none;
}

.home-page .quick-tags::-webkit-scrollbar {
  display: none;
}

.home-page .quick-tags button {
  flex: 0 0 auto;
  min-height: 44px;
  padding: 8px 12px;
  border: 1px solid rgba(255, 255, 255, 0.38);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  color: #ffffff;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    color 0.18s ease;
}

.home-page .quick-tags button:hover {
  border-color: rgba(255, 255, 255, 0.72);
  color: #ffffff;
  background: rgba(255, 255, 255, 0.22);
}

.home-page .quick-tags button:active {
  background: rgba(255, 255, 255, 0.28);
}

.home-page .hero-panel {
  display: flex;
  justify-content: center;
  align-items: center;
  align-self: center;
  justify-self: center;
  width: min(100%, 540px);
  max-width: 540px;
  margin-left: 0;
  padding: 0;
  gap: 0;
  border-radius: 8px;
  background: transparent;
  overflow: visible;
  contain: paint;
  transform: none;
}

.home-page .logo-stage {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 500px;
  aspect-ratio: 1;
  min-height: 500px;
  padding: 0;
  isolation: isolate;
}

.home-page .logo-stage::before {
  content: none;
}

.home-page .hero-panel img {
  display: block;
  width: 500px;
  max-width: 100%;
  height: 500px;
  margin: 0 auto;
  object-fit: contain;
}

.home-page .service-band {
  position: relative;
  z-index: 2;
  display: grid;
  grid-template-columns: minmax(250px, 0.78fr) minmax(0, 1.22fr);
  gap: 28px;
  align-items: center;
  margin-top: -92px;
  padding: 0 0 10px;
  border: 0;
  background: transparent;
  box-shadow: none;
}

.home-page .law-band {
  margin-top: 28px;
  padding: 22px;
}

.home-page .home-section-divider {
  height: 1px;
  margin-top: 34px;
  background: #d8dee8;
}

.home-page .advisor-band {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(320px, 0.72fr);
  gap: clamp(38px, 6vw, 84px);
  align-items: center;
  padding: 38px 0 44px;
  background: transparent;
}

.home-page.reveal-ready .service-band,
.home-page.reveal-ready .advisor-band,
.home-page.reveal-ready .law-band,
.home-page.reveal-ready .home-columns {
  opacity: 0;
  transform: translate3d(0, 28px, 0);
  transition:
    opacity 0.76s var(--home-ease-out),
    transform 0.76s var(--home-ease-out);
}

.home-page.reveal-ready .service-band.is-visible,
.home-page.reveal-ready .advisor-band.is-visible,
.home-page.reveal-ready .law-band.is-visible,
.home-page.reveal-ready .home-columns.is-visible {
  opacity: 1;
  transform: translate3d(0, 0, 0);
}

.home-page.reveal-ready .service-band .section-head,
.home-page.reveal-ready .service-band .service-tile,
.home-page.reveal-ready .home-columns .home-panel {
  opacity: 0;
  transform: translate3d(0, 18px, 0);
  transition:
    opacity 0.62s var(--home-ease-out),
    transform 0.62s var(--home-ease-out);
}

.home-page.reveal-ready .service-band.is-visible .section-head,
.home-page.reveal-ready .service-band.is-visible .service-tile,
.home-page.reveal-ready .home-columns.is-visible .home-panel {
  opacity: 1;
  transform: translate3d(0, 0, 0);
}

.home-page.reveal-ready .service-band.is-visible .service-tile:nth-child(1) {
  transition-delay: 120ms;
}

.home-page.reveal-ready .service-band.is-visible .service-tile:nth-child(2) {
  transition-delay: 190ms;
}

.home-page.reveal-ready .service-band.is-visible .service-tile:nth-child(3) {
  transition-delay: 260ms;
}

.home-page.reveal-ready .service-band.is-visible .service-tile:nth-child(4) {
  transition-delay: 330ms;
}

.home-page.reveal-ready .home-columns.is-visible .home-panel:nth-child(2) {
  transition-delay: 140ms;
}

.home-page .advisor-visual {
  min-width: 0;
}

.home-page .advisor-visual img {
  display: block;
  width: min(100%, 560px);
  height: auto;
  object-fit: contain;
}

.home-page .advisor-copy {
  min-width: 0;
  max-width: 430px;
}

.home-page .advisor-copy h2 {
  margin-top: 6px;
  font-size: 34px;
  font-weight: 500;
  line-height: 1.18;
}

.home-page .advisor-copy h2 span {
  display: block;
  color: #e11d2e;
  font-size: 40px;
  font-weight: 800;
}

.home-page .advisor-copy p:not(.eyebrow) {
  max-width: 42ch;
  margin-top: 18px;
  color: #475467;
  font-size: 15px;
  line-height: 1.75;
}

.home-page .advisor-cta {
  --el-button-bg-color: #2563eb;
  --el-button-border-color: #2563eb;
  --el-button-text-color: #ffffff;
  --el-button-hover-bg-color: #1e40af;
  --el-button-hover-border-color: #1e40af;
  --el-button-active-bg-color: #1d4ed8;
  --el-button-active-border-color: #1d4ed8;
  min-width: 148px;
  min-height: 48px;
  margin-top: 24px;
  border-radius: 6px;
  font-weight: 700;
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
  color: #667085;
  font-weight: 700;
}

.home-page .compact {
  margin-bottom: 16px;
}

.home-page .service-band .section-head {
  display: block;
  margin-bottom: 0;
  padding: 28px 8px 28px 0;
}

.home-page .service-band .section-head h2 {
  max-width: 410px;
  font-size: 34px;
  line-height: 1.18;
}

.home-page .service-title-accent {
  color: #e11d2e;
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
  text-align: left;
}

.home-page .lawyer-row {
  cursor: pointer;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    color 0.18s ease;
}

.home-page .lawyer-row:hover {
  border-color: #2563eb;
}

.home-page .lawyer-row:active {
  background: #f6f8fc;
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
  border-color: #2563eb;
  color: #1d4ed8;
  background: #eff6ff;
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
  color: #667085;
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
  font-weight: 700;
  box-shadow: none;
  transition:
    background-color 0.18s ease,
    border-color 0.18s ease,
    color 0.18s ease;
}

.home-page .home-back-top:hover {
  background: #eff6ff;
  border-color: #2563eb;
  color: #1d4ed8;
}

.home-page .home-back-top:active {
  background: #edf1f7;
}

.home-page .service-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
  align-items: start;
  padding-top: 8px;
}

.home-page .law-browser {
  display: grid;
  grid-template-columns: 190px 260px minmax(0, 1fr);
  gap: 0;
  min-height: 360px;
  max-height: 390px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  overflow: hidden;
  contain: layout paint;
}

.home-page .law-column {
  min-width: 0;
  border: 0;
  border-radius: 0;
  background: #ffffff;
  overflow: hidden;
}

.home-page .law-column:not(:last-child) {
  border-right: 1px solid #e5eaf3;
}

.home-page .law-column-title {
  height: 48px;
  display: flex;
  align-items: center;
  padding: 0 14px;
  border-bottom: 1px solid #e5eaf3;
  background: #f6f8fc;
  color: #172033;
  font-size: 15px;
  font-weight: 700;
}

.home-page .law-scroll,
.home-page .article-scroll {
  max-height: 312px;
  padding: 10px;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
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
  display: none;
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
  position: relative;
  overflow: hidden;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: linear-gradient(90deg, #edf1f7, #f6f8fc 46%, #edf1f7);
  background-size: 180% 100%;
  animation: homeSkeleton 1.2s ease-in-out infinite;
}

.home-page .law-skeleton span {
  height: 40px;
  border-radius: 6px;
}

.home-page .law-skeleton.article span {
  height: 98px;
  border-radius: 8px;
}

.home-page .lawyer-skeleton span {
  height: 72px;
}

.home-page .law-row {
  position: relative;
  display: block;
  width: 100%;
  min-height: 44px;
  padding: 10px 12px 10px 16px;
  border: 0;
  border-radius: 6px;
  background: transparent;
  color: #344054;
  text-align: left;
  line-height: 1.45;
  cursor: pointer;
  transition:
    background-color 0.18s ease,
    color 0.18s ease;
}

.home-page .law-row::before,
.home-page .article-row::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  bottom: 8px;
  width: 2px;
  border-radius: 999px;
  background: #2563eb;
  opacity: 0;
}

.home-page .law-row:hover {
  background: #f6f8fc;
}

.home-page .law-row:active {
  background: #edf1f7;
}

.home-page .law-row.active {
  background: #eff6ff;
  color: #1d4ed8;
  font-weight: 700;
}

.home-page .law-row.active::before,
.home-page .article-row.active::before {
  opacity: 1;
}

.home-page .article-row {
  position: relative;
  width: 100%;
  min-width: 0;
  padding: 14px 14px 14px 18px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  text-align: left;
  cursor: pointer;
  content-visibility: auto;
  contain-intrinsic-size: 96px;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    color 0.18s ease;
}

.home-page .article-row:hover {
  border-color: #2563eb;
}

.home-page .article-row:active {
  background: #f6f8fc;
}

.home-page .article-row.active {
  border-color: #2563eb;
  background: #eff6ff;
}

.home-page .article-row strong {
  color: #172033;
  font-size: 15px;
}

.home-page .article-row.active strong {
  color: #1d4ed8;
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
  background: #ffffff;
  color: #667085;
  cursor: pointer;
  font-weight: 600;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    color 0.18s ease;
}

.home-page .article-more:hover {
  border-color: #2563eb;
  background: #eff6ff;
  color: #1d4ed8;
}

.home-page .article-more:active {
  background: #edf1f7;
}

.home-page .service-tile {
  position: relative;
  min-height: 152px;
  padding: 20px;
  overflow-wrap: anywhere;
}

.home-page .service-tile:nth-child(1) {
  background: #ffffff;
}

.home-page .service-tile:nth-child(2) {
  margin-top: 34px;
  background: #f8fbff;
}

.home-page .service-tile:nth-child(3) {
  margin-top: -10px;
  background: #fbf8ff;
}

.home-page .service-tile:nth-child(4) {
  margin-top: 24px;
  background: #f7fbfa;
}

.home-page .service-initial {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-weight: 600;
}

.home-page .service-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 24px;
  padding: 3px 9px;
  border: 1px solid #d6e4ff;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
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
  align-items: stretch;
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
  font-weight: 700;
}

.home-page .lawyer-row em {
  color: #344054;
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
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #f6f8fc;
}

.home-page .order-entry-placeholder {
  background: #ffffff;
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
  align-items: center;
  justify-content: center;
  padding: 28px;
  background: rgba(15, 23, 42, 0.35);
  animation: detailOverlayIn 0.2s ease-out;
}

.home-page .detail-sheet {
  position: relative;
  display: flex;
  flex-direction: column;
  width: min(1120px, calc(100vw - 56px));
  height: min(720px, calc(100vh - 56px));
  min-height: 520px;
  border: 1px solid #e5eaf3;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.24);
  overflow: hidden;
  animation: detailSheetIn 0.2s ease-out;
}

.home-page .detail-head {
  display: grid;
  grid-template-columns: 52px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
  min-height: 76px;
  padding: 14px 22px;
  border-bottom: 1px solid #edf1f7;
  background: #f6f8fc;
}

.home-page .icon-btn {
  width: 44px;
  height: 44px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  color: #172033;
  font-size: 28px;
  cursor: pointer;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    color 0.18s ease;
}

.home-page .icon-btn:hover {
  border-color: #2563eb;
  background: #eff6ff;
  color: #1d4ed8;
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
  line-height: 1.35;
}

.home-page .bottom-favorite,
.home-page .comment-submit {
  border: 1px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 700;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    color 0.18s ease;
}

.home-page .bottom-favorite.active {
  color: #1d4ed8;
  background: #eff6ff;
}

.home-page .bottom-favorite:hover {
  background: #eff6ff;
  color: #1d4ed8;
}

.home-page .bottom-favorite:active {
  background: #edf1f7;
}

.home-page .detail-scroll {
  flex: 1;
  overflow: hidden;
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(340px, 0.85fr);
  gap: 18px;
  min-height: 0;
  padding: 20px 22px 86px;
}

.home-page .detail-primary::-webkit-scrollbar,
.home-page .detail-secondary::-webkit-scrollbar,
.home-page .comment-list::-webkit-scrollbar {
  display: none;
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
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: none;
}

.home-page .detail-primary {
  min-height: 0;
  padding: 24px 26px;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.home-page .detail-secondary {
  min-height: 0;
  display: grid;
  grid-template-rows: auto auto;
  gap: 14px;
  align-content: start;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.home-page .explanation-block,
.home-page .comment-block {
  padding: 20px;
}

.home-page .article-full-text h3 {
  margin-bottom: 12px;
  color: #172033;
  font-size: 18px;
  font-weight: 700;
}

.home-page .article-full-text p,
.home-page .explanation-block p {
  color: #344054;
  font-size: 17px;
  line-height: 1.95;
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
  border: 1px solid #e5eaf3;
  border-radius: 999px;
  background: #ffffff;
  color: #344054;
  font-weight: 700;
  cursor: pointer;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    color 0.18s ease;
}

.home-page .explanation-feedback-actions button:hover {
  border-color: #2563eb;
  color: #1d4ed8;
  background: #eff6ff;
}

.home-page .explanation-feedback-actions button:active {
  background: #edf1f7;
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
  padding-right: 2px;
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
  background: #eff6ff;
  color: #1d4ed8;
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
  padding: 14px 22px;
  border-top: 1px solid #edf1f7;
  background: #ffffff;
  box-shadow: none;
}

.home-page .detail-actions input {
  min-width: 0;
  height: 44px;
  padding: 0 16px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  color: #172033;
  outline: none;
}

.home-page .detail-actions input:focus-visible {
  background: #ffffff;
  border-color: #2563eb;
  outline: 2px solid #2563eb;
  outline-offset: 2px;
}

.home-page .comment-submit {
  height: 44px;
  padding: 0 18px;
  border-color: #2563eb;
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
  border-color: #e5eaf3;
  background: #ffffff;
  color: #667085;
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
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.14);
  text-align: center;
  animation: detailSheetIn 0.2s ease-out;
}

.home-page .feedback-close {
  position: absolute;
  right: 10px;
  top: 8px;
  width: 44px;
  height: 44px;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
  color: #667085;
  font-size: 20px;
  cursor: pointer;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    color 0.18s ease;
}

.home-page .feedback-close:hover {
  border-color: #2563eb;
  background: #eff6ff;
  color: #1d4ed8;
}

.home-page .feedback-close:active {
  background: #edf1f7;
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
  background: #ffffff;
  color: #344054;
  font-weight: 700;
  cursor: pointer;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    color 0.18s ease;
}

.home-page .feedback-reasons button.selected {
  border-color: #2563eb;
  background: #eff6ff;
  color: #1d4ed8;
}

.home-page .feedback-reasons button:hover {
  border-color: #2563eb;
  background: #eff6ff;
  color: #1d4ed8;
}

.home-page .feedback-reasons button:active {
  background: #edf1f7;
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
  border-radius: 8px;
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
  outline: 2px solid #2563eb;
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
  border: 1px solid #2563eb;
  border-radius: 8px;
  background: #2563eb;
  color: #ffffff;
  font-weight: 600;
  cursor: pointer;
  transition:
    opacity 0.18s ease,
    background-color 0.18s ease,
    border-color 0.18s ease;
}

.home-page .feedback-submit:hover:not(:disabled) {
  border-color: #1e40af;
  background: #1e40af;
}

.home-page .feedback-submit:active:not(:disabled) {
  background: #1d4ed8;
}

.home-page .feedback-submit:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

@keyframes detailOverlayIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

@keyframes detailSheetIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes heroWaterSweep {
  from {
    opacity: 0.2;
    transform: translate3d(0, -18%, 0);
  }

  to {
    opacity: 0.55;
    transform: translate3d(0, 0, 0);
  }
}

@keyframes heroWaveRise {
  from {
    opacity: 0.92;
    transform: translate3d(0, 26px, 0);
  }

  to {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
}

@keyframes heroWaveDrift {
  from {
    background-position: 46% bottom;
  }

  to {
    background-position: 54% bottom;
  }
}

@keyframes homeTopFloatIn {
  from {
    opacity: 0;
    transform: translate3d(0, 24px, 0);
  }

  to {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
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

  .home-page .hero-copy > .eyebrow,
  .home-page .hero-copy > h1,
  .home-page .hero-copy > .subtext,
  .home-page .hero-search,
  .home-page .quick-tags,
  .home-page .hero-panel {
    opacity: 1 !important;
    transform: none !important;
  }

  .home-page.reveal-ready .service-band,
  .home-page.reveal-ready .advisor-band,
  .home-page.reveal-ready .law-band,
  .home-page.reveal-ready .home-columns,
  .home-page.reveal-ready .service-band .section-head,
  .home-page.reveal-ready .service-band .service-tile,
  .home-page.reveal-ready .home-columns .home-panel {
    opacity: 1 !important;
    transform: none !important;
  }
}

@media (max-width: 1024px) {
  .home-page .hero-section,
  .home-page .service-band,
  .home-page .advisor-band,
  .home-page .home-columns {
    grid-template-columns: 1fr;
  }

  .home-page .hero-section {
    min-height: clamp(500px, calc(68svh - 84px), 660px);
    padding: calc(72px + var(--home-header-overlap, 0px)) 38px 150px;
  }

  .home-page .hero-section h1 {
    font-size: 36px;
  }

  .home-page .hero-panel {
    width: min(100%, 500px);
    max-width: 100%;
    margin-left: 0;
    justify-self: center;
    transform: none;
  }

  .home-page .logo-stage {
    width: min(500px, 100%);
    min-height: 0;
  }

  .home-page .service-band {
    margin-top: -68px;
  }

  .home-page .service-band .section-head {
    padding: 0;
  }

  .home-page .service-band .section-head h2 {
    max-width: 620px;
    font-size: 28px;
  }

  .home-page .service-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .home-page .advisor-band {
    gap: 30px;
    padding: 34px 28px 40px;
  }

  .home-page .advisor-visual img {
    margin: 0 auto;
  }

  .home-page .advisor-copy {
    max-width: 620px;
  }

  .home-page .advisor-copy h2 {
    font-size: 30px;
  }

  .home-page .advisor-copy h2 span {
    font-size: 34px;
  }

  .home-page .law-browser {
    grid-template-columns: 150px 220px minmax(0, 1fr);
  }

  .home-page .detail-overlay {
    padding: 16px;
  }

  .home-page .detail-sheet {
    width: min(100%, 720px);
    height: calc(100vh - 32px);
    min-height: 0;
  }

  .home-page .detail-scroll {
    grid-template-columns: 1fr;
    overflow-y: auto;
    padding: 18px 18px 96px;
  }

  .home-page .detail-primary {
    overflow: visible;
  }

  .home-page .detail-secondary {
    overflow: visible;
  }

  .home-page .comment-list {
    max-height: none;
  }
}

@media (max-width: 640px) {
  .home-page {
    padding: 0 12px 40px;
  }

  .home-page .hero-section {
    min-height: clamp(480px, calc(64svh - 70px), 620px);
    padding: calc(54px + var(--home-header-overlap, 0px)) 20px 124px;
  }

  .home-page .hero-copy,
  .home-page .hero-panel,
  .home-page .law-band,
  .home-page .service-band,
  .home-page .advisor-band,
  .home-page .home-panel {
    padding: 16px;
  }

  .home-page h1 {
    font-size: 28px;
  }

  .home-page .hero-section h1 {
    font-size: 30px;
  }

  .home-page .service-band {
    margin-top: -56px;
  }

  .home-page .home-section-divider {
    margin-top: 24px;
  }

  .home-page .service-band .section-head h2 {
    font-size: 24px;
  }

  .home-page .advisor-band {
    gap: 22px;
    padding-top: 24px;
    padding-bottom: 30px;
  }

  .home-page .advisor-copy h2 {
    font-size: 26px;
  }

  .home-page .advisor-copy h2 span {
    font-size: 30px;
  }

  .home-page .advisor-copy p:not(.eyebrow) {
    font-size: 14px;
  }

  .home-page .advisor-cta {
    width: 100%;
  }

  .home-page .hero-search,
  .home-page .service-grid {
    grid-template-columns: 1fr;
  }

  .home-page .hero-panel {
    width: 100%;
    gap: 0;
    transform: none;
  }

  .home-page .logo-stage {
    justify-self: center;
    width: min(320px, 100%);
    min-height: 0;
  }

  .home-page .hero-panel img {
    width: min(320px, 100%);
    height: min(320px, 80vw);
  }

  .home-page .service-grid {
    gap: 12px;
    padding-top: 0;
  }

  .home-page .service-tile:nth-child(n) {
    margin-top: 0;
  }

  .home-page .law-browser {
    grid-template-columns: 1fr;
    max-height: none;
  }

  .home-page .law-column:not(:last-child) {
    border-right: 0;
    border-bottom: 1px solid #e5eaf3;
  }

  .home-page .law-scroll {
    max-height: 180px;
  }

  .home-page .article-scroll {
    max-height: 240px;
  }

  .home-page .detail-overlay {
    padding: 8px;
  }

  .home-page .detail-sheet {
    width: calc(100vw - 16px);
    height: calc(100vh - 16px);
    border-radius: 10px;
  }

  .home-page .detail-head {
    min-height: 68px;
    padding: 10px 12px;
  }

  .home-page .detail-scroll {
    padding: 16px 14px 120px;
  }

  .home-page .detail-primary,
  .home-page .explanation-block,
  .home-page .comment-block {
    padding: 16px;
  }

  .home-page .article-full-text p,
  .home-page .explanation-block p {
    font-size: 17px;
  }

  .home-page .detail-actions {
    grid-template-columns: minmax(0, 1fr) auto;
    padding: 12px;
  }

  .home-page .bottom-favorite {
    display: none;
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
