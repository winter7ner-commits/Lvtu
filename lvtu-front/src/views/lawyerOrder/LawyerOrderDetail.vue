<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  acceptLawyerOrder,
  getLawyerProfileByUserId,
  getLawyerOrderDetail,
  getServiceResult,
  submitServiceResult
} from '@/api/lawyerOrder'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const resultLoading = ref(false)
const order = ref(null)
const serviceResult = ref(null)
const currentLawyerId = ref(null)
const resultDialogVisible = ref(false)
const resultForm = ref({
  title: '',
  content: '',
  attachments: []
})
const attachmentDraft = ref({
  fileName: '',
  fileUrl: '',
  fileType: ''
})

const serviceTypeMap = {
  101: '在线法律咨询',
  102: '电话法律咨询',
  103: '文书代写',
  104: '合同审核',
  105: '婚姻家事',
  106: '诉讼代理'
}

const orderId = computed(() => route.params.orderId)
const parsedFormData = computed(() => parseFormData(order.value?.formData))
const canAccept = computed(() => order.value?.status === '待接单')
const canSubmitResult = computed(() => {
  return order.value?.status === '处理中' && Number(order.value?.lawyerId) === Number(currentLawyerId.value)
})
const canViewResult = computed(() => {
  return ['待客户确认', '待评价', '已完成'].includes(order.value?.status) && Number(order.value?.lawyerId) === Number(currentLawyerId.value)
})

const unwrap = (res) => res?.data?.data ?? res?.data ?? null
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

const labelize = (key) => {
  const labelMap = {
    summary: '需求摘要',
    description: '详细说明',
    contactName: '联系人',
    contactPhone: '联系电话',
    city: '所在城市',
    address: '地址',
    deadline: '期望完成时间'
  }
  return labelMap[key] || key
}

const getStatusType = (status) => {
  if (status === '处理中') return 'primary'
  if (status === '待客户确认') return 'warning'
  if (status === '待评价') return 'warning'
  if (status === '已完成') return 'success'
  if (status === '待接单') return 'info'
  return 'info'
}

const getResultStatusText = (status) => {
  if (status === 0) return '已提交'
  if (status === 1) return '用户已确认'
  if (status === 2) return '需修改'
  return '未知'
}

const loadCurrentLawyer = async () => {
  if (currentLawyerId.value) return currentLawyerId.value
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
  if (!currentUser?.userId) {
    ElMessage.warning('请先登录律师账号')
    return null
  }
  const res = await getLawyerProfileByUserId(currentUser.userId)
  const lawyer = unwrap(res)
  currentLawyerId.value = lawyer?.lawyerId || null
  return currentLawyerId.value
}

const loadOrder = async () => {
  loading.value = true
  try {
    await loadCurrentLawyer()
    const res = await getLawyerOrderDetail(orderId.value)
    order.value = unwrap(res)
    if (canViewResult.value) {
      await loadResult()
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '订单详情加载失败')
  } finally {
    loading.value = false
  }
}

const loadResult = async () => {
  resultLoading.value = true
  try {
    const res = await getServiceResult(orderId.value, currentLawyerId.value)
    serviceResult.value = unwrap(res)
  } catch (error) {
    serviceResult.value = null
    if (order.value?.status !== '处理中') {
      ElMessage.warning(error?.response?.data?.message || '暂未查询到服务结果')
    }
  } finally {
    resultLoading.value = false
  }
}

const handleAccept = async () => {
  try {
    const lawyerId = await loadCurrentLawyer()
    if (!lawyerId) return
    await ElMessageBox.confirm('确认接下该订单并开始处理吗？', '确认接单', {
      confirmButtonText: '确认接单',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await acceptLawyerOrder(orderId.value, lawyerId)
    ElMessage.success('接单成功')
    await loadOrder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '接单失败')
    }
  }
}

const openResultDialog = () => {
  resultDialogVisible.value = true
}

const addAttachment = () => {
  if (!attachmentDraft.value.fileName || !attachmentDraft.value.fileUrl) {
    ElMessage.warning('请填写附件名称和地址')
    return
  }
  resultForm.value.attachments.push({ ...attachmentDraft.value })
  attachmentDraft.value = { fileName: '', fileUrl: '', fileType: '' }
}

const removeAttachment = (index) => {
  resultForm.value.attachments.splice(index, 1)
}

const handleSubmitResult = async () => {
  if (!resultForm.value.title.trim()) {
    ElMessage.warning('请填写结果标题')
    return
  }
  const lawyerId = await loadCurrentLawyer()
  if (!lawyerId) return
  submitting.value = true
  try {
    await submitServiceResult(orderId.value, {
      lawyerId,
      title: resultForm.value.title,
      content: resultForm.value.content,
      attachments: resultForm.value.attachments
    })
    ElMessage.success('服务结果提交成功')
    resultDialogVisible.value = false
    resultForm.value = { title: '', content: '', attachments: [] }
    await loadOrder()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '服务结果提交失败')
  } finally {
    submitting.value = false
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
        <p class="subtext">确认用户需求、接单处理，并在完成后提交服务结果。</p>
      </div>
      <div class="head-actions">
        <el-button @click="router.push('/lawyer/orders/available')">接单大厅</el-button>
        <el-button @click="router.push('/lawyer/orders/my')">我的订单</el-button>
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
              <span>用户 ID</span>
              <strong>{{ order.userId }}</strong>
            </div>
            <div>
              <span>接单律师</span>
              <strong>{{ order.lawyerId || '未接单' }}</strong>
            </div>
            <div>
              <span>创建时间</span>
              <strong>{{ formatTime(order.createdTime) }}</strong>
            </div>
            <div>
              <span>更新时间</span>
              <strong>{{ formatTime(order.updatedTime) }}</strong>
            </div>
          </div>

          <div class="action-row">
            <el-button v-if="canAccept" type="primary" size="large" @click="handleAccept">立即接单</el-button>
            <el-button v-if="canSubmitResult" type="primary" size="large" @click="openResultDialog">提交服务结果</el-button>
            <el-button v-if="canViewResult && !serviceResult" size="large" @click="loadResult">查看服务结果</el-button>
          </div>
        </article>

        <article class="info-panel">
          <div class="panel-title">
            <h2>用户需求</h2>
          </div>
          <div v-if="Object.keys(parsedFormData).length" class="form-data">
            <div v-for="(value, key) in parsedFormData" :key="key" class="form-row">
              <span>{{ labelize(key) }}</span>
              <p>{{ value || '-' }}</p>
            </div>
          </div>
          <el-empty v-else description="暂无需求详情" />
        </article>
      </section>

      <section v-if="canViewResult || serviceResult" class="result-panel" v-loading="resultLoading">
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
        <el-empty v-else description="暂无服务结果" />
      </section>
    </template>

    <el-dialog v-model="resultDialogVisible" title="提交服务结果" width="640px">
      <el-form label-position="top">
        <el-form-item label="结果标题">
          <el-input v-model="resultForm.title" maxlength="100" show-word-limit placeholder="例如：合同审核意见书" />
        </el-form-item>
        <el-form-item label="结果说明">
          <el-input
            v-model="resultForm.content"
            type="textarea"
            :rows="6"
            maxlength="5000"
            show-word-limit
            placeholder="填写处理结论、主要风险点或下一步建议"
          />
        </el-form-item>
        <el-form-item label="附件">
          <div class="attachment-editor">
            <div class="attachment-inputs">
              <el-input v-model="attachmentDraft.fileName" placeholder="文件名" />
              <el-input v-model="attachmentDraft.fileUrl" placeholder="文件地址" />
              <el-input v-model="attachmentDraft.fileType" placeholder="文件类型" />
              <el-button @click="addAttachment">添加</el-button>
            </div>
            <div v-if="resultForm.attachments.length" class="draft-list">
              <div v-for="(file, index) in resultForm.attachments" :key="`${file.fileName}-${index}`">
                <span>{{ file.fileName }}</span>
                <el-button type="danger" link @click="removeAttachment(index)">删除</el-button>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resultDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmitResult">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.detail-page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background-color: #f5f5f5;
  color: #172033;
}

.page-head,
.detail-grid,
.result-panel {
  width: 100%;
  max-width: 1180px;
  margin-left: auto;
  margin-right: auto;
}

.page-head {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: flex-end;
  margin-bottom: 24px;
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

h2 {
  font-size: 18px;
}

h3 {
  font-size: 18px;
  margin-bottom: 10px;
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
  background: #ffffff;
  border: 1px solid #e5eaf3;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.panel-title {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  margin-bottom: 18px;
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
  border-radius: 10px;
}

.info-list span,
.form-row span {
  display: block;
  color: #667085;
  font-size: 13px;
  margin-bottom: 8px;
}

.info-list strong {
  color: #172033;
}

.money {
  color: #dc2626;
}

.action-row {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.form-data {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-row p {
  margin: 0;
  line-height: 1.7;
  color: #172033;
  word-break: break-word;
}

.result-panel {
  margin-top: 18px;
}

.result-content {
  margin: 0;
  line-height: 1.8;
  color: #344054;
  white-space: pre-wrap;
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

.attachment-editor {
  width: 100%;
}

.attachment-inputs {
  display: grid;
  grid-template-columns: 1fr 1fr 120px auto;
  gap: 10px;
}

.draft-list {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.draft-list div {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 10px;
  background: #f6f8fc;
  border-radius: 6px;
}

@media (max-width: 900px) {
  .page-head,
  .detail-grid {
    display: flex;
    flex-direction: column;
    align-items: stretch;
  }

  .info-list,
  .attachment-inputs {
    grid-template-columns: 1fr;
  }
}
</style>
