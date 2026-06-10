<script setup>
import { computed, onMounted, ref } from 'vue'
import {
  getInterventionLogs,
  getInterventions,
  getRevisionLimitConfig,
  handleIntervention,
  updateRevisionLimitConfig
} from '../../api/admin'
import { useAuthStore } from '../../store/auth'
import { normalizeAdminRole } from '../../utils/adminPermissions'

const authStore = useAuthStore()
const rows = ref([])
const logs = ref([])
const loading = ref(false)
const savingLimit = ref(false)
const actionSubmitting = ref(false)
const keyword = ref('')
const maxRevisionRequestCount = ref(2)
const actionTarget = ref(null)
const actionType = ref('')
const actionReason = ref('')
const logTarget = ref(null)

const actionOptions = [
  { type: 'REQUIRE_LAWYER_RESUBMIT', label: '要求律师重提', desc: '订单回到处理中，律师需要重新提交结果。' },
  { type: 'RETURN_TO_CONFIRMATION', label: '退回用户确认', desc: '订单回到待客户确认，用户可再次确认。' },
  { type: 'MARK_PENDING_REVIEW', label: '判定服务完成', desc: '订单进入待评价，视为平台判定服务完成。' }
]
const canManageSettings = computed(() => normalizeAdminRole(authStore.user) === 'SUPER_ADMIN')

const filteredRows = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  if (!key) return rows.value
  return rows.value.filter((item) => [
    item.order_id,
    item.user_name,
    item.user_phone,
    item.lawyer_name,
    item.service_name,
    item.latest_revision_content,
    item.result_title
  ].some((field) => String(field || '').toLowerCase().includes(key)))
})

const adminId = computed(() =>
  authStore.user?.userId ||
  authStore.user?.user_id ||
  authStore.user?.id ||
  Number(localStorage.getItem('userId')) ||
  null
)

const formatMoney = (value) => value == null ? '-' : `￥${Number(value).toFixed(2)}`
const formatTime = (value) => value ? String(value).replace('T', ' ').slice(0, 16) : '-'
const actionLabel = (type) => actionOptions.find((item) => item.type === type)?.label || type

const loadConfig = async () => {
  const result = await getRevisionLimitConfig()
  if (result.code === 200) {
    maxRevisionRequestCount.value = Number(result.data?.maxRevisionRequestCount || 2)
  }
}

const saveConfig = async () => {
  savingLimit.value = true
  try {
    const result = await updateRevisionLimitConfig(Number(maxRevisionRequestCount.value))
    if (result.code === 200) {
      maxRevisionRequestCount.value = Number(result.data?.maxRevisionRequestCount || maxRevisionRequestCount.value)
    }
  } finally {
    savingLimit.value = false
  }
}

const loadInterventions = async () => {
  loading.value = true
  try {
    const result = await getInterventions()
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
  if (!actionTarget.value || actionReason.value.trim().length < 6 || !adminId.value) return
  actionSubmitting.value = true
  try {
    await handleIntervention(actionTarget.value.order_id, {
      actionType: actionType.value,
      reason: actionReason.value.trim(),
      adminId: adminId.value
    })
    closeAction()
    await loadInterventions()
  } finally {
    actionSubmitting.value = false
  }
}

const openLogs = async (item) => {
  logTarget.value = item
  const result = await getInterventionLogs(item.order_id)
  logs.value = result.code === 200 ? result.data || [] : []
}

onMounted(async () => {
  await Promise.all([loadConfig(), loadInterventions()])
})
</script>

<template>
  <main class="page-shell">
    <section class="page-head">
      <div>
        <p class="eyebrow">争议处理</p>
        <h1>平台介入</h1>
        <p>处理修改次数用尽后仍有争议的订单，并配置用户可申请修改次数。</p>
      </div>
      <button class="primary-btn" type="button" @click="loadInterventions">刷新</button>
    </section>

    <section v-if="canManageSettings" class="settings-card">
      <div>
        <span>修改次数上限</span>
        <strong>用户最多可申请 {{ maxRevisionRequestCount }} 次修改</strong>
      </div>
      <div class="limit-control">
        <input v-model.number="maxRevisionRequestCount" type="number" min="0" max="10" />
        <button class="primary-btn" type="button" :disabled="savingLimit" @click="saveConfig">
          {{ savingLimit ? '保存中' : '保存设置' }}
        </button>
      </div>
    </section>

    <section class="content-card">
      <div class="toolbar">
        <input v-model="keyword" placeholder="搜索订单、用户、律师、争议说明" />
      </div>

      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>订单</th>
              <th>用户</th>
              <th>律师</th>
              <th>服务</th>
              <th>修改次数</th>
              <th>最新修改意见</th>
              <th>当前结果</th>
              <th>时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="9" class="empty">加载中...</td>
            </tr>
            <tr v-else-if="filteredRows.length === 0">
              <td colspan="9" class="empty">暂无平台介入订单</td>
            </tr>
            <tr v-for="item in filteredRows" v-else :key="item.order_id">
              <td>
                <strong>#{{ item.order_id }}</strong>
                <small>{{ formatMoney(item.total_amount) }}</small>
              </td>
              <td>
                <div>{{ item.user_name || item.user_id }}</div>
                <small>{{ item.user_phone || '-' }}</small>
              </td>
              <td>
                <div>{{ item.lawyer_name || item.lawyer_id || '-' }}</div>
                <small>{{ item.law_firm || '-' }}</small>
              </td>
              <td>{{ item.service_name || '-' }}</td>
              <td>{{ item.revision_count || 0 }} / {{ maxRevisionRequestCount }}</td>
              <td class="content-cell">
                <strong v-if="item.latest_revision_no">第 {{ item.latest_revision_no }} 次</strong>
                <p>{{ item.latest_revision_content || '-' }}</p>
              </td>
              <td class="content-cell">
                <strong>{{ item.result_title || '-' }}</strong>
                <p>{{ item.result_content || '-' }}</p>
              </td>
              <td>{{ formatTime(item.updated_time) }}</td>
              <td>
                <div class="actions">
                  <button type="button" @click="openLogs(item)">日志</button>
                  <button type="button" @click="openAction(item, 'REQUIRE_LAWYER_RESUBMIT')">要求重提</button>
                  <button type="button" @click="openAction(item, 'RETURN_TO_CONFIRMATION')">退回确认</button>
                  <button class="success" type="button" @click="openAction(item, 'MARK_PENDING_REVIEW')">判定完成</button>
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
          <h2>{{ actionLabel(actionType) }} #{{ actionTarget.order_id }}</h2>
          <button type="button" @click="closeAction">×</button>
        </div>
        <p class="modal-sub">{{ actionOptions.find((item) => item.type === actionType)?.desc }}</p>
        <textarea v-model="actionReason" maxlength="500" rows="5" placeholder="请输入处理说明，至少 6 个字符"></textarea>
        <div class="modal-footer">
          <span>{{ actionReason.length }}/500</span>
          <button type="button" @click="closeAction">取消</button>
          <button
            class="primary-btn"
            type="button"
            :disabled="actionReason.trim().length < 6 || actionSubmitting"
            @click="submitAction"
          >
            {{ actionSubmitting ? '提交中' : '确认处理' }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="logTarget" class="modal-mask" @click.self="logTarget = null">
      <div class="modal">
        <div class="modal-head">
          <h2>处理日志 #{{ logTarget.order_id }}</h2>
          <button type="button" @click="logTarget = null">×</button>
        </div>
        <div v-if="logs.length === 0" class="empty">暂无处理日志</div>
        <div v-else class="log-list">
          <div v-for="log in logs" :key="log.id" class="log-item">
            <strong>{{ actionLabel(log.action_type) }}</strong>
            <span>{{ formatTime(log.created_at) }} / {{ log.admin_name || log.admin_id }}</span>
            <p>{{ log.reason }}</p>
            <small>{{ log.from_status }} -> {{ log.to_status }}</small>
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
.settings-card { display: flex; justify-content: space-between; gap: 18px; align-items: center; padding: 18px 20px; margin-bottom: 18px; border: 1px solid #d6e4ff; border-radius: 8px; background: #f8fbff; }
.settings-card span { display: block; margin-bottom: 6px; color: #667085; font-size: 13px; }
.settings-card strong { color: #1d4ed8; font-size: 20px; }
.limit-control { display: flex; gap: 10px; align-items: center; }
.limit-control input { width: 96px; height: 42px; border: 1px solid #d8dee9; border-radius: 6px; padding: 0 12px; font-size: 16px; }
.content-card { background: #fff; border-radius: 8px; padding: 22px; box-shadow: 0 5px 18px rgba(30, 55, 90, 0.08); }
.toolbar { display: flex; justify-content: flex-end; margin-bottom: 18px; }
.toolbar input { width: 360px; height: 42px; border: 1px solid #d8dee9; border-radius: 6px; padding: 0 12px; font-size: 14px; }
.table-wrap { overflow-x: auto; }
.data-table { min-width: 1420px; width: 100%; border-collapse: collapse; }
.data-table th { background: #f7f7fb; color: #344054; text-align: left; padding: 14px; font-size: 14px; }
.data-table td { border-top: 1px solid #e6e8ef; padding: 14px; vertical-align: top; font-size: 14px; color: #344054; }
.data-table small { display: block; margin-top: 5px; color: #667085; }
.content-cell { max-width: 260px; }
.content-cell p { margin: 6px 0 0; color: #475467; line-height: 1.7; white-space: pre-wrap; word-break: break-word; }
.empty { text-align: center; color: #98a2b3; padding: 34px !important; }
.actions { display: flex; flex-wrap: wrap; gap: 8px; }
.actions button { border: 0; border-radius: 6px; background: #eff6ff; color: #1d4ed8; padding: 8px 10px; font-weight: 800; cursor: pointer; }
.actions .success { background: #ecfdf3; color: #027a48; }
.modal-mask { position: fixed; inset: 0; background: rgba(15, 23, 42, .45); display: flex; align-items: center; justify-content: center; z-index: 200; }
.modal { width: min(620px, 92vw); max-height: 86vh; overflow: auto; background: #fff; border-radius: 8px; padding: 24px; }
.modal-head { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #edf0f5; margin-bottom: 18px; }
.modal-head h2 { margin: 0 0 16px; color: #172033; }
.modal-head button { border: 0; background: transparent; font-size: 28px; cursor: pointer; }
.modal-sub { margin: 0 0 14px; color: #667085; }
.modal textarea { width: 100%; box-sizing: border-box; border: 1px solid #d8dee9; border-radius: 8px; padding: 12px; resize: vertical; line-height: 1.7; }
.modal-footer { display: flex; justify-content: flex-end; align-items: center; gap: 10px; margin-top: 14px; }
.modal-footer span { margin-right: auto; color: #98a2b3; }
.modal-footer button { border: 1px solid #d8dee9; border-radius: 6px; background: #fff; padding: 9px 16px; cursor: pointer; }
.modal-footer .primary-btn { border: 0; background: #2563eb; color: #fff; }
.log-list { display: flex; flex-direction: column; gap: 12px; }
.log-item { padding: 12px 14px; border: 1px solid #e5eaf3; border-radius: 8px; background: #f8fafc; }
.log-item strong { color: #172033; }
.log-item span, .log-item small { display: block; margin-top: 4px; color: #667085; }
.log-item p { margin: 8px 0 0; color: #344054; line-height: 1.7; white-space: pre-wrap; }

@media (max-width: 900px) {
  .page-shell { padding: 28px 18px; }
  .settings-card { flex-direction: column; align-items: stretch; }
  .toolbar input { width: 100%; }
}
</style>
