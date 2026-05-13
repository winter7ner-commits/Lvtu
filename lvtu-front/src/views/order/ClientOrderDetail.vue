<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderById } from '@/api/order'
import { confirmOrderComplete, getOrderResultForUser } from '@/api/lawyerOrder'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const resultLoading = ref(false)
const confirming = ref(false)
const order = ref(null)
const serviceResult = ref(null)

const serviceTypeMap = {
  101: '在线法律咨询',
  102: '电话法律咨询',
  103: '文书代写',
  104: '合同审核',
  105: '婚姻家事',
  106: '诉讼代理'
}

const orderId = computed(() => route.params.orderId)
const currentUserId = computed(() => {
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
  return currentUser?.userId || Number(localStorage.getItem('userId')) || null
})
const parsedFormData = computed(() => parseFormData(order.value?.formData))
const canConfirm = computed(() => order.value?.status === '待评价' && Number(order.value?.userId) === Number(currentUserId.value))

const formatServiceType = (id) => serviceTypeMap[id] || `服务类型 ${id || '-'}`
const formatMoney = (value) => `¥${Number(value || 0).toFixed(2)}`
const formatTime = (value) => (value ? String(value).replace('T', ' ').slice(0, 16) : '-')

const parseFormData = (raw) => {
  if (!raw) return {}
  if (typeof raw === 'object') return raw
  try {
    return JSON.parse(raw)
  } catch {
    return { 需求内容: raw }
  }
}

const displayValue = (value) => {
  if (Array.isArray(value)) return value.join('、')
  if (value && typeof value === 'object') return JSON.stringify(value)
  return value || '-'
}

const getStatusType = (status) => {
  if (status === '待接单') return 'info'
  if (status === '处理中') return 'primary'
  if (status === '待评价') return 'warning'
  if (status === '已完成') return 'success'
  return 'info'
}

const getResultStatusText = (status) => {
  if (status === 0) return '已提交'
  if (status === 1) return '用户已确认'
  if (status === 2) return '需修改'
  return '未知'
}

const loadOrder = async () => {
  loading.value = true
  try {
    const response = await getOrderById(orderId.value)
    if (response?.code !== 200) {
      ElMessage.error(response?.message || '订单详情加载失败')
      return
    }
    order.value = response.data
    if (['待评价', '已完成'].includes(order.value?.status)) {
      await loadResult()
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '订单详情加载失败')
  } finally {
    loading.value = false
  }
}

const loadResult = async () => {
  if (!currentUserId.value) return
  resultLoading.value = true
  try {
    const response = await getOrderResultForUser(orderId.value, currentUserId.value)
    serviceResult.value = response?.data?.data ?? response?.data ?? null
  } catch {
    serviceResult.value = null
  } finally {
    resultLoading.value = false
  }
}

const handleConfirm = async () => {
  if (!currentUserId.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await ElMessageBox.confirm('确认该订单服务已经完成吗？确认后订单将进入已完成状态。', '确认完成', {
      confirmButtonText: '确认完成',
      cancelButtonText: '取消',
      type: 'warning'
    })
    confirming.value = true
    const response = await confirmOrderComplete(orderId.value, currentUserId.value)
    if ((response?.data?.code ?? response?.code) === 200) {
      ElMessage.success('订单已确认完成')
      await loadOrder()
    } else {
      ElMessage.error(response?.data?.message || response?.message || '确认失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '确认失败')
    }
  } finally {
    confirming.value = false
  }
}

onMounted(loadOrder)
</script>

<template>
  <div class="detail-page" v-loading="loading">
    <section class="page-head">
      <div>
        <p class="eyebrow">订单详情</p>
        <h1>订单 #{{ order?.orderId || orderId }}</h1>
        <p class="subtext">查看订单需求、律师处理结果，并确认服务完成。</p>
      </div>
      <div class="head-actions">
        <el-button @click="router.push('/orders')">返回订单</el-button>
        <el-button v-if="canConfirm" type="primary" :loading="confirming" @click="handleConfirm">确认完成</el-button>
      </div>
    </section>

    <template v-if="order">
      <section class="detail-grid">
        <article class="info-panel">
          <div class="panel-title">
            <h2>订单信息</h2>
            <el-tag :type="getStatusType(order.status)" effect="plain">{{ order.status }}</el-tag>
          </div>
          <div class="info-list">
            <div>
              <span>服务类型</span>
              <strong>{{ formatServiceType(order.serviceTypeId) }}</strong>
            </div>
            <div>
              <span>订单金额</span>
              <strong class="money">{{ formatMoney(order.totalAmount) }}</strong>
            </div>
            <div>
              <span>接单律师</span>
              <strong>{{ order.lawyerId || '未接单' }}</strong>
            </div>
            <div>
              <span>创建时间</span>
              <strong>{{ formatTime(order.createdTime) }}</strong>
            </div>
          </div>
        </article>

        <article class="info-panel">
          <div class="panel-title">
            <h2>发布需求</h2>
          </div>
          <div v-if="Object.keys(parsedFormData).length" class="form-data">
            <div v-for="(value, key) in parsedFormData" :key="key" class="form-row">
              <span>{{ key }}</span>
              <p>{{ displayValue(value) }}</p>
            </div>
          </div>
          <el-empty v-else description="暂无需求详情" />
        </article>
      </section>

      <section class="result-panel" v-loading="resultLoading">
        <div class="panel-title">
          <h2>服务结果</h2>
          <el-tag v-if="serviceResult" type="success" effect="plain">
            {{ getResultStatusText(serviceResult.status) }}
          </el-tag>
        </div>
        <template v-if="serviceResult">
          <h3>{{ serviceResult.title }}</h3>
          <p class="result-content">{{ serviceResult.content || '无补充说明' }}</p>
          <div v-if="serviceResult.attachments?.length" class="attachment-list">
            <a v-for="file in serviceResult.attachments" :key="file.id || file.fileUrl" :href="file.fileUrl" target="_blank">
              {{ file.fileName }}
            </a>
          </div>
        </template>
        <el-empty v-else description="律师暂未提交服务结果" />
      </section>
    </template>
  </div>
</template>

<style scoped>
.detail-page {
  max-width: 1180px;
  min-height: 520px;
  margin: 0 auto;
  padding: 32px 20px 56px;
  color: #172033;
}

.page-head,
.panel-title {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-end;
  margin-bottom: 24px;
}

.panel-title {
  align-items: center;
  margin-bottom: 18px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-weight: 700;
}

h1,
h2,
h3 {
  margin: 0;
}

h1 {
  font-size: 30px;
}

h2,
h3 {
  font-size: 18px;
}

.subtext {
  margin: 10px 0 0;
  color: #667085;
}

.head-actions {
  display: flex;
  gap: 10px;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
  align-items: start;
}

.info-panel,
.result-panel {
  padding: 20px;
  background: #fff;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  box-shadow: 0 8px 22px rgba(30, 64, 175, 0.06);
}

.info-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.info-list div,
.form-row {
  padding: 14px;
  background: #f6f8fc;
  border-radius: 8px;
}

.info-list span,
.form-row span {
  display: block;
  color: #667085;
  font-size: 13px;
  margin-bottom: 8px;
}

.money {
  color: #dc2626;
}

.form-data {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-row p,
.result-content {
  margin: 0;
  line-height: 1.8;
  color: #172033;
  word-break: break-word;
  white-space: pre-wrap;
}

.result-panel {
  margin-top: 18px;
}

.attachment-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

.attachment-list a {
  padding: 8px 12px;
  color: #1d4ed8;
  background: #eff6ff;
  border-radius: 6px;
}

@media (max-width: 900px) {
  .page-head,
  .detail-grid {
    display: flex;
    flex-direction: column;
    align-items: stretch;
  }

  .info-list {
    grid-template-columns: 1fr;
  }
}
</style>
