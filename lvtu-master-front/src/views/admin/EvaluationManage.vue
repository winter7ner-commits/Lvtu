<script setup>
import { computed, onMounted, ref } from 'vue'
import { getEvaluationLogs, getOrders, handleEvaluation } from '../../api/admin'

const rows = ref([])
const loading = ref(false)
const keyword = ref('')
const statusFilter = ref('')
const featuredFilter = ref('')
const actionTarget = ref(null)
const actionType = ref('')
const actionReason = ref('')
const logs = ref([])
const logTarget = ref(null)

const getCurrentAdminId = () => {
  try {
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
    return Number(currentUser?.userId || currentUser?.id || userInfo?.id || localStorage.getItem('userId')) || null
  } catch {
    return null
  }
}

const actionTextMap = {
  HIDE: '隐藏评价',
  DELETE: '删除评价',
  RESTORE: '恢复评价',
  FEATURE: '设为精选',
  UNFEATURE: '取消精选'
}

const statusTextMap = {
  normal: '正常',
  hidden: '隐藏',
  deleted: '已删除',
  pending: '待审核'
}

const evaluations = computed(() => {
  return rows.value
    .filter((item) => item.evaluation_id)
    .map((item) => ({
      ...item,
      statusText: statusTextMap[item.evaluation_status] || item.evaluation_status || '-',
      featured: Boolean(item.is_featured)
    }))
})

const filteredEvaluations = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  return evaluations.value.filter((item) => {
    const statusMatched = !statusFilter.value || item.evaluation_status === statusFilter.value
    const featuredMatched = featuredFilter.value === '' || String(item.featured) === featuredFilter.value
    const keyMatched = !key || [
      item.evaluation_id,
      item.order_id,
      item.user_name,
      item.lawyer_name,
      item.evaluation_content
    ].some((field) => String(field || '').toLowerCase().includes(key))
    return statusMatched && featuredMatched && keyMatched
  })
})

const summary = computed(() => ({
  total: evaluations.value.length,
  reported: evaluations.value.filter((item) => Number(item.report_count || 0) > 0).length,
  featured: evaluations.value.filter((item) => item.featured).length,
  hidden: evaluations.value.filter((item) => item.evaluation_status === 'hidden').length
}))

const formatTime = (value) => value ? String(value).replace('T', ' ').slice(0, 16) : '-'
const formatScore = (value) => value == null ? '-' : Number(value).toFixed(1)

const loadEvaluations = async () => {
  loading.value = true
  try {
    const result = await getOrders()
    rows.value = result.code === 200 ? result.data || [] : []
  } finally {
    loading.value = false
  }
}

const openAction = (item, type) => {
  actionTarget.value = item
  actionType.value = type
  actionReason.value = ''
}

const closeAction = () => {
  actionTarget.value = null
  actionType.value = ''
  actionReason.value = ''
}

const submitAction = async () => {
  if (!actionTarget.value || actionReason.value.trim().length < 10) return
  const adminId = getCurrentAdminId()
  if (!adminId) return
  await handleEvaluation({
    adminId,
    evaluationId: actionTarget.value.evaluation_id,
    actionType: actionType.value,
    reason: actionReason.value.trim()
  })
  closeAction()
  await loadEvaluations()
}

const openLogs = async (item) => {
  logTarget.value = item
  logs.value = await getEvaluationLogs(item.evaluation_id)
}

onMounted(loadEvaluations)
</script>

<template>
  <main class="page-shell">
    <section class="page-head">
      <div>
        <p class="eyebrow">评价治理</p>
        <h1>评价管理</h1>
        <p>查看服务评价、处理举报风险，并维护精选评价。</p>
      </div>
      <button class="primary-btn" type="button" @click="loadEvaluations">刷新</button>
    </section>

    <section class="summary-grid">
      <div class="summary-card">
        <span>评价总数</span>
        <strong>{{ summary.total }}</strong>
      </div>
      <div class="summary-card warning">
        <span>有举报</span>
        <strong>{{ summary.reported }}</strong>
      </div>
      <div class="summary-card success">
        <span>精选</span>
        <strong>{{ summary.featured }}</strong>
      </div>
      <div class="summary-card muted">
        <span>已隐藏</span>
        <strong>{{ summary.hidden }}</strong>
      </div>
    </section>

    <section class="content-card">
      <div class="toolbar">
        <select v-model="statusFilter">
          <option value="">全部状态</option>
          <option value="normal">正常</option>
          <option value="hidden">隐藏</option>
          <option value="deleted">已删除</option>
          <option value="pending">待审核</option>
        </select>
        <select v-model="featuredFilter">
          <option value="">全部精选状态</option>
          <option value="true">精选</option>
          <option value="false">未精选</option>
        </select>
        <input v-model="keyword" placeholder="搜索评价、订单、用户、律师" />
      </div>

      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>评价</th>
              <th>订单/用户</th>
              <th>律师</th>
              <th>评分</th>
              <th>状态</th>
              <th>内容</th>
              <th>时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="8" class="empty">加载中...</td>
            </tr>
            <tr v-else-if="filteredEvaluations.length === 0">
              <td colspan="8" class="empty">暂无评价</td>
            </tr>
            <tr v-for="item in filteredEvaluations" v-else :key="item.evaluation_id">
              <td>
                <strong>#{{ item.evaluation_id }}</strong>
                <span v-if="item.featured" class="tag tag-featured">精选</span>
              </td>
              <td>
                <div>订单 #{{ item.order_id }}</div>
                <small>{{ item.user_name || item.user_id }}</small>
              </td>
              <td>{{ item.lawyer_name || item.lawyer_id || '-' }}</td>
              <td>
                <div class="score">{{ formatScore(item.total_score) }}</div>
                <small>专业 {{ item.professional_score }} / 响应 {{ item.responsiveness_score }} / 态度 {{ item.attitude_score }}</small>
              </td>
              <td>
                <span :class="['tag', `tag-${item.evaluation_status}`]">{{ item.statusText }}</span>
                <span v-if="item.report_count" class="tag tag-report">举报 {{ item.report_count }}</span>
              </td>
              <td class="content-cell">{{ item.evaluation_content || '-' }}</td>
              <td>{{ formatTime(item.evaluation_created_time) }}</td>
              <td>
                <div class="actions">
                  <button type="button" @click="openLogs(item)">日志</button>
                  <button v-if="item.evaluation_status !== 'hidden'" type="button" @click="openAction(item, 'HIDE')">隐藏</button>
                  <button v-if="item.evaluation_status === 'hidden'" type="button" @click="openAction(item, 'RESTORE')">恢复</button>
                  <button v-if="!item.featured" type="button" @click="openAction(item, 'FEATURE')">精选</button>
                  <button v-if="item.featured" type="button" @click="openAction(item, 'UNFEATURE')">取消精选</button>
                  <button v-if="item.evaluation_status !== 'deleted'" class="danger" type="button" @click="openAction(item, 'DELETE')">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <div v-if="actionTarget" class="modal-mask" @click.self="closeAction">
      <div class="modal">
        <div class="modal-head">
          <h2>{{ actionTextMap[actionType] }}</h2>
          <button type="button" @click="closeAction">×</button>
        </div>
        <p class="modal-sub">评价 #{{ actionTarget.evaluation_id }}，请填写操作原因。</p>
        <textarea v-model="actionReason" maxlength="300" rows="5" placeholder="请输入至少 10 个字符"></textarea>
        <div class="modal-footer">
          <span>{{ actionReason.length }}/300</span>
          <button type="button" @click="closeAction">取消</button>
          <button class="primary-btn" type="button" :disabled="actionReason.trim().length < 10" @click="submitAction">确认</button>
        </div>
      </div>
    </div>

    <div v-if="logTarget" class="modal-mask" @click.self="logTarget = null">
      <div class="modal">
        <div class="modal-head">
          <h2>操作日志 #{{ logTarget.evaluation_id }}</h2>
          <button type="button" @click="logTarget = null">×</button>
        </div>
        <div v-if="logs.length === 0" class="empty">暂无操作日志</div>
        <div v-else class="log-list">
          <div v-for="log in logs" :key="log.logId" class="log-item">
            <strong>{{ log.actionType }}</strong>
            <span>{{ formatTime(log.createdTime) }}</span>
            <p>{{ log.reason || '-' }}</p>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.page-shell { padding: 40px 46px 56px; }
.page-head { display: flex; justify-content: space-between; gap: 24px; align-items: flex-end; margin-bottom: 22px; }
.eyebrow { margin: 0 0 8px; color: #2563eb; font-weight: 800; }
h1 { margin: 0; color: #172033; font-size: 30px; }
.page-head p:last-child { margin: 8px 0 0; color: #667085; }
.primary-btn { border: 0; border-radius: 6px; background: #2563eb; color: #fff; padding: 10px 18px; font-weight: 800; cursor: pointer; }
.primary-btn:disabled { opacity: .55; cursor: not-allowed; }
.summary-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 18px; }
.summary-card { padding: 18px; border: 1px solid #d6e4ff; border-radius: 8px; background: #f8fbff; }
.summary-card span { display: block; color: #667085; font-size: 13px; margin-bottom: 8px; }
.summary-card strong { color: #1d4ed8; font-size: 28px; }
.summary-card.warning { background: #fff7ed; border-color: #fed7aa; }
.summary-card.warning strong { color: #c2410c; }
.summary-card.success { background: #f0fdf4; border-color: #bbf7d0; }
.summary-card.success strong { color: #15803d; }
.summary-card.muted { background: #f8fafc; border-color: #e2e8f0; }
.summary-card.muted strong { color: #475467; }
.content-card { background: #fff; border-radius: 8px; padding: 22px; box-shadow: 0 8px 24px rgba(15, 23, 42, .07); }
.toolbar { display: flex; gap: 12px; margin-bottom: 18px; }
.toolbar input, .toolbar select { border: 1px solid #d8dee9; border-radius: 6px; padding: 10px 12px; font-size: 14px; }
.toolbar input { width: 320px; }
.table-wrap { overflow-x: auto; }
.data-table { min-width: 1280px; width: 100%; border-collapse: collapse; }
.data-table th { background: #f6f8fc; color: #344054; text-align: left; padding: 14px; }
.data-table td { border-top: 1px solid #edf1f7; padding: 14px; vertical-align: top; }
.data-table small { color: #667085; }
.score { color: #0958d9; font-weight: 900; font-size: 18px; }
.content-cell { max-width: 280px; color: #344054; line-height: 1.6; white-space: pre-wrap; }
.tag { display: inline-flex; margin: 0 6px 6px 0; padding: 4px 8px; border-radius: 999px; background: #eef4ff; color: #1d4ed8; font-size: 12px; font-weight: 800; }
.tag-featured { background: #fff7e6; color: #ad6800; }
.tag-report { background: #fff1f3; color: #c01048; }
.tag-hidden { background: #f8fafc; color: #475467; }
.tag-deleted { background: #fff1f0; color: #b42318; }
.tag-pending { background: #fef3c7; color: #92400e; }
.actions { display: flex; flex-wrap: wrap; gap: 8px; }
.actions button { border: 1px solid #d6e4ff; border-radius: 6px; background: #fff; color: #1d4ed8; padding: 7px 10px; cursor: pointer; }
.actions .danger { border-color: #fecaca; color: #b42318; }
.empty { text-align: center; color: #98a2b3; padding: 32px; }
.modal-mask { position: fixed; inset: 0; background: rgba(15,23,42,.42); display: flex; align-items: center; justify-content: center; z-index: 200; }
.modal { width: min(560px, 92vw); max-height: 82vh; overflow: auto; background: #fff; border-radius: 10px; padding: 22px; box-shadow: 0 24px 64px rgba(15,23,42,.22); }
.modal-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.modal-head h2 { margin: 0; color: #172033; }
.modal-head button { border: 0; background: transparent; font-size: 26px; cursor: pointer; }
.modal-sub { color: #667085; }
textarea { width: 100%; border: 1px solid #d8dee9; border-radius: 8px; padding: 12px; font: inherit; resize: vertical; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; align-items: center; margin-top: 14px; }
.modal-footer span { margin-right: auto; color: #98a2b3; font-size: 12px; }
.modal-footer button { border: 1px solid #d8dee9; border-radius: 6px; background: #fff; padding: 9px 14px; cursor: pointer; }
.modal-footer .primary-btn { border: 0; background: #2563eb; }
.log-list { display: flex; flex-direction: column; gap: 10px; }
.log-item { padding: 12px; border: 1px solid #edf1f7; border-radius: 8px; background: #f8fafc; }
.log-item span { float: right; color: #98a2b3; font-size: 12px; }
.log-item p { margin: 8px 0 0; color: #475467; }
@media (max-width: 900px) {
  .page-shell { padding: 24px 16px; }
  .page-head, .toolbar { flex-direction: column; align-items: stretch; }
  .summary-grid { grid-template-columns: repeat(2, 1fr); }
  .toolbar input { width: 100%; }
}
</style>
