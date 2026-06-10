<script setup>
import { computed, onMounted, ref } from 'vue'
import { getArticleFeedback, handleArticleFeedback } from '../../api/admin'

const feedbackRows = ref([])
const loading = ref(false)
const keyword = ref('')
const statusFilter = ref('')
const helpfulFilter = ref('')

const filteredRows = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  return feedbackRows.value.filter((item) => {
    const statusMatched = !statusFilter.value || item.status === statusFilter.value
    const helpfulMatched = helpfulFilter.value === '' || String(Boolean(item.helpful)) === helpfulFilter.value
    const keyMatched = !key || [
      item.id,
      item.articleNumber,
      item.articleTitle,
      item.documentName,
      item.username,
      item.phone,
      item.reason,
      item.content
    ].some((field) => String(field || '').toLowerCase().includes(key))
    return statusMatched && helpfulMatched && keyMatched
  })
})

const summary = computed(() => ({
  total: feedbackRows.value.length,
  pending: feedbackRows.value.filter((item) => item.status === 'pending').length,
  helpful: feedbackRows.value.filter((item) => item.helpful).length,
  unhelpful: feedbackRows.value.filter((item) => !item.helpful).length
}))

const formatTime = (value) => value ? String(value).replace('T', ' ').slice(0, 16) : '-'
const statusText = (status) => ({ pending: '待处理', handled: '已处理' }[status] || status || '-')

const loadFeedback = async () => {
  loading.value = true
  try {
    const result = await getArticleFeedback()
    feedbackRows.value = result.code === 200 ? result.data || [] : []
  } finally {
    loading.value = false
  }
}

const markHandled = async (item) => {
  await handleArticleFeedback(item.id)
  await loadFeedback()
}

onMounted(loadFeedback)
</script>

<template>
  <main class="page-shell">
    <section class="page-head">
      <div>
        <p class="eyebrow">内容反馈</p>
        <h1>解释反馈</h1>
        <p>查看用户对法条解释的有帮助和无帮助反馈。</p>
      </div>
      <button class="primary-btn" type="button" @click="loadFeedback">刷新</button>
    </section>

    <section class="summary-grid">
      <div class="summary-card">
        <span>反馈总数</span>
        <strong>{{ summary.total }}</strong>
      </div>
      <div class="summary-card warning">
        <span>待处理</span>
        <strong>{{ summary.pending }}</strong>
      </div>
      <div class="summary-card success">
        <span>有帮助</span>
        <strong>{{ summary.helpful }}</strong>
      </div>
      <div class="summary-card muted">
        <span>无帮助</span>
        <strong>{{ summary.unhelpful }}</strong>
      </div>
    </section>

    <section class="content-card">
      <div class="toolbar">
        <select v-model="statusFilter">
          <option value="">全部状态</option>
          <option value="pending">待处理</option>
          <option value="handled">已处理</option>
        </select>
        <select v-model="helpfulFilter">
          <option value="">全部反馈</option>
          <option value="true">有帮助</option>
          <option value="false">无帮助</option>
        </select>
        <input v-model="keyword" placeholder="搜索法条、用户、原因、内容" />
      </div>

      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>反馈</th>
              <th>法条</th>
              <th>用户</th>
              <th>类型</th>
              <th>原因</th>
              <th>补充说明</th>
              <th>状态</th>
              <th>时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="9" class="empty">加载中...</td>
            </tr>
            <tr v-else-if="filteredRows.length === 0">
              <td colspan="9" class="empty">暂无反馈</td>
            </tr>
            <tr v-for="item in filteredRows" v-else :key="item.id">
              <td><strong>#{{ item.id }}</strong></td>
              <td>
                <div>{{ item.documentName || '-' }}</div>
                <small>{{ item.articleNumber || '-' }} {{ item.articleTitle || '' }}</small>
              </td>
              <td>
                <div>{{ item.username || `用户${item.userId}` }}</div>
                <small>{{ item.phone || '-' }}</small>
              </td>
              <td>
                <span :class="['tag', item.helpful ? 'tag-helpful' : 'tag-unhelpful']">
                  {{ item.helpful ? '有帮助' : '无帮助' }}
                </span>
              </td>
              <td>{{ item.reason || '-' }}</td>
              <td class="content-cell">{{ item.content || '-' }}</td>
              <td><span :class="['tag', `tag-${item.status}`]">{{ statusText(item.status) }}</span></td>
              <td>{{ formatTime(item.createdAt) }}</td>
              <td>
                <button
                  v-if="item.status !== 'handled'"
                  class="table-btn"
                  type="button"
                  @click="markHandled(item)"
                >
                  标记已处理
                </button>
                <span v-else class="handled-text">已处理</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>

<style scoped>
.page-shell { padding: 40px 46px 56px; }
.page-head { display: flex; justify-content: space-between; gap: 24px; align-items: flex-end; margin-bottom: 22px; }
.eyebrow { margin: 0 0 8px; color: #2563eb; font-weight: 800; }
h1 { margin: 0; color: #172033; font-size: 30px; }
.page-head p:last-child { margin: 8px 0 0; color: #667085; }
.primary-btn, .table-btn { border: 0; border-radius: 6px; background: #2563eb; color: #fff; padding: 10px 18px; font-weight: 800; cursor: pointer; }
.summary-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 18px; }
.summary-card { padding: 18px; border: 1px solid #d6e4ff; border-radius: 8px; background: #f8fbff; }
.summary-card span { display: block; color: #667085; font-size: 13px; margin-bottom: 8px; }
.summary-card strong { color: #1d4ed8; font-size: 28px; }
.summary-card.warning { background: #fff7ed; border-color: #fed7aa; }
.summary-card.warning strong { color: #c2410c; }
.summary-card.success { background: #f0fdf4; border-color: #bbf7d0; }
.summary-card.success strong { color: #15803d; }
.summary-card.muted { background: #f8fafc; border-color: #e2e8f0; }
.summary-card.muted strong { color: #475569; }
.content-card { background: #fff; border-radius: 8px; padding: 22px; box-shadow: 0 5px 18px rgba(30, 55, 90, 0.08); }
.toolbar { display: grid; grid-template-columns: 150px 150px minmax(220px, 1fr); gap: 12px; margin-bottom: 18px; }
.toolbar select, .toolbar input { height: 42px; border: 1px solid #d8dee9; border-radius: 6px; padding: 0 12px; font-size: 14px; }
.table-wrap { overflow-x: auto; }
.data-table { min-width: 1180px; width: 100%; border-collapse: collapse; }
.data-table th { background: #f7f7fb; color: #344054; text-align: left; padding: 14px; font-size: 14px; }
.data-table td { border-top: 1px solid #e6e8ef; padding: 14px; vertical-align: top; font-size: 14px; color: #344054; }
.data-table small { display: block; margin-top: 5px; color: #667085; }
.content-cell { max-width: 260px; white-space: pre-wrap; word-break: break-word; }
.empty { text-align: center; color: #98a2b3; padding: 34px !important; }
.tag { display: inline-flex; align-items: center; border-radius: 999px; padding: 4px 10px; font-size: 12px; font-weight: 800; }
.tag-helpful { background: #ecfdf3; color: #027a48; }
.tag-unhelpful { background: #fff7ed; color: #c2410c; }
.tag-pending { background: #eff6ff; color: #1d4ed8; }
.tag-handled { background: #f1f5f9; color: #475569; }
.handled-text { color: #98a2b3; font-weight: 700; }

@media (max-width: 900px) {
  .page-shell { padding: 28px 18px; }
  .summary-grid { grid-template-columns: repeat(2, 1fr); }
  .toolbar { grid-template-columns: 1fr; }
}
</style>
