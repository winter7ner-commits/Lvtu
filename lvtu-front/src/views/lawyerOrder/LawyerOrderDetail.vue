<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  acceptLawyerOrder,
  getLawyerProfileByUserId,
  getLawyerOrderDetail,
  getServiceResult,
  submitServiceResult,
  uploadServiceResultAttachment,
  updateServiceResult
} from '@/api/lawyerOrder'
import { useRegionOptions } from '@/components/order/useRegionOptions'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const resultLoading = ref(false)
const attachmentUploadCount = ref(0)
const order = ref(null)
const serviceResult = ref(null)
const currentLawyerId = ref(null)
const resultDialogVisible = ref(false)
const selectedResultRecord = ref(null)
const resultForm = ref({
  title: '',
  content: '',
  attachments: []
})
const attachmentUploadFiles = ref([])
const attachmentUploading = computed(() => attachmentUploadCount.value > 0)
const collapsedInfo = ref([])
const { regionOptions } = useRegionOptions()

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
const canAccept = computed(() => {
  if (order.value?.status !== '待接单') return false
  if (order.value?.assignmentType !== 'DIRECT') return true
  return Number(order.value?.targetLawyerId) === Number(currentLawyerId.value)
})
const canSubmitResult = computed(() => {
  return order.value?.status === '处理中' && Number(order.value?.lawyerId) === Number(currentLawyerId.value)
})
const canViewResult = computed(() => {
  return ['处理中', '待客户确认', '待评价', '已完成', '平台介入'].includes(order.value?.status) && Number(order.value?.lawyerId) === Number(currentLawyerId.value)
})
const isRevisionMode = computed(() => serviceResult.value?.status === 2 && order.value?.status === '处理中')
const resultRecords = computed(() => {
  if (!serviceResult.value) return []
  const records = serviceResult.value.submissions?.length
    ? [...serviceResult.value.submissions]
    : [{
      id: 'current',
      submissionNo: 1,
      title: serviceResult.value.title,
      content: serviceResult.value.content,
      createdAt: serviceResult.value.updatedAt || serviceResult.value.createdAt,
      attachments: serviceResult.value.attachments || []
    }]
  return records.sort((a, b) => Number(a.submissionNo || 0) - Number(b.submissionNo || 0))
})
const latestSubmissionNo = computed(() => {
  if (!resultRecords.value.length) return 0
  return Math.max(...resultRecords.value.map((item) => Number(item.submissionNo || 0)))
})
const initialSubmission = computed(() => {
  return resultRecords.value.find((item) => Number(item.submissionNo || 0) <= 1) || null
})
const getSubmissionTitle = (item) => {
  const no = Number(item?.submissionNo || 1)
  return no <= 1 ? '提交结果' : `第 ${no - 1} 次修改后提交`
}
const getRevisionSubmission = (revision) => {
  if (Number(revision?.status) === 0) return null
  const targetNo = Number(revision?.revisionNo || 0) + 1
  const exact = resultRecords.value.find((item) => Number(item.submissionNo || 0) === targetNo)
  if (exact) return exact
  if (Number(revision?.revisionNo || 0) === (serviceResult.value?.revisionRequestCount || 0) && serviceResult.value) {
    return {
      id: `current-revision-${targetNo}`,
      submissionNo: targetNo,
      title: serviceResult.value.title,
      content: serviceResult.value.content,
      createdAt: serviceResult.value.updatedAt || serviceResult.value.createdAt,
      attachments: serviceResult.value.attachments || []
    }
  }
  return null
}

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

const identityKeys = ['realName', 'phone', 'idCard', 'verifiedText', 'accountType']
const contactKeys = ['wechat', 'email', 'emergencyContact', 'emergencyPhone']
const hiddenDemandKeys = new Set([...identityKeys, 'verified', ...contactKeys, 'agreeTerms'])
const demandSectionConfig = [
  {
    title: '基础信息',
    tone: 'blue',
    keys: [
      'businessType',
      'region',
      'isUrgent',
      'expectedDate',
      'budget',
      'serviceSubject',
      'consultMode',
      'consultationType',
      'consultationDirection',
      'businessDirection',
      'marriageStatus',
      'contractType',
      'contractRole',
      'documentType',
      'caseType',
      'currentStage',
      'caseAmount',
      'hasLawyer',
      'preferredDate',
      'preferredTimeSlot',
      'callDuration',
      'deadline',
      'city',
      'address',
      'contactName',
      'contactPhone',
      'needFollowUp',
      'needUrgent',
      'needLawyerSignature'
    ]
  },
  {
    title: '问题与诉求',
    tone: 'amber',
    keys: [
      'summary',
      'description',
      'problemTitle',
      'problemDescription',
      'keyProblem',
      'eventDescription',
      'documentPurpose',
      'coreDemand',
      'helpExpected',
      'reviewFocus',
      'expectedService',
      'supplementaryRemarks'
    ]
  },
  {
    title: '相关人员与财产',
    tone: 'green',
    keys: [
      'otherPartyName',
      'otherPartyContact',
      'oppositePartyName',
      'propertyHouse',
      'propertyVehicle',
      'propertyDeposit',
      'propertyDebt',
      'propertyOther',
      'hasChildren',
      'childrenCount',
      'childrenAge'
    ]
  },
  {
    title: '证据材料',
    tone: 'purple',
    keys: ['existingMaterials', 'contractFile', 'evidenceFiles']
  }
]
const wideDemandKeys = new Set([
  'summary',
  'description',
  'problemTitle',
  'problemDescription',
  'keyProblem',
  'eventDescription',
  'supplementaryRemarks',
  'coreDemand',
  'helpExpected',
  'reviewFocus',
  'expectedService',
  'existingMaterials',
  'contractFile',
  'evidenceFiles'
])

const fieldLabelMap = {
  realName: '真实姓名',
  phone: '手机号',
  idCard: '身份证号',
  verified: '认证状态',
  verifiedText: '认证状态',
  accountType: '账户类型',
  wechat: '微信号',
  email: '邮箱',
  emergencyContact: '紧急联系人',
  emergencyPhone: '紧急电话',
  businessType: '业务类型',
  region: '所属地区',
  isUrgent: '是否紧急',
  expectedDate: '期望完成时间',
  budget: '预估预算',
  serviceSubject: '服务对象',
  consultationType: '咨询类型',
  problemTitle: '问题标题',
  problemDescription: '问题详细描述',
  helpExpected: '希望获得的帮助',
  consultationDirection: '咨询方向',
  preferredDate: '希望通话日期',
  preferredTimeSlot: '希望通话时间段',
  callDuration: '通话时长选择',
  keyProblem: '本次最想解决的问题',
  needFollowUp: '是否需要回访',
  documentType: '文书类型',
  documentPurpose: '文书用途',
  eventDescription: '事件经过详细描述',
  otherPartyName: '对方姓名/公司名称',
  otherPartyContact: '对方联系方式',
  needUrgent: '是否需要加急',
  needLawyerSignature: '是否需要律师署名版',
  contractType: '合同类型',
  contractRole: '合同身份',
  reviewFocus: '审核重点',
  contractFile: '上传合同文件',
  supplementaryRemarks: '补充说明',
  businessDirection: '业务方向',
  marriageStatus: '婚姻状态',
  coreDemand: '核心诉求',
  propertyHouse: '房产情况',
  propertyVehicle: '车辆情况',
  propertyDeposit: '存款情况',
  propertyDebt: '债务情况',
  propertyOther: '其他重要资产',
  hasChildren: '是否有子女',
  childrenCount: '子女人数',
  childrenAge: '子女年龄',
  caseType: '案件类型',
  currentStage: '当前阶段',
  caseAmount: '涉案金额',
  oppositePartyName: '对方姓名/公司名称',
  hasLawyer: '是否已有律师',
  existingMaterials: '已有材料',
  expectedService: '期望律师服务',
  evidenceFiles: '证据材料',
  agreeTerms: '隐私与授权确认',
  summary: '需求摘要',
  description: '详细说明',
  consultMode: '咨询方式',
  contactName: '联系人',
  contactPhone: '联系电话',
  city: '所在城市',
  address: '地址',
  deadline: '期望完成时间'
}

const consultModeValueLabelMap = {
  online: '在线咨询',
  phone: '电话咨询',
  video: '视频咨询',
  offline: '线下咨询',
  text: '图文咨询',
  voice: '语音咨询',
  online_consult: '在线法律咨询',
  phone_consult: '电话法律咨询'
}

const valueLabelMap = {
  legalAdvice: '法律意见',
  riskAnalysis: '风险分析',
  solutionSuggestion: '处理方案建议',
  worthSuing: '是否值得起诉',
  riskClause: '风险条款排查',
  breachLiability: '违约责任',
  paymentClause: '付款条款',
  terminationClause: '解约条款',
  confidentialityClause: '保密条款',
  jurisdiction: '管辖法院',
  propertyDivision: '财产分割',
  childCustody: '争取孩子抚养权',
  wantDivorce: '想离婚',
  infidelityEvidence: '对方出轨证据处理',
  domesticViolenceRights: '家暴维权',
  fullRepresentation: '全程代理',
  singleAppearance: '单次出庭',
  executionRepresentation: '执行代理',
  indictment: '起诉状',
  judgment: '判决书',
  arbitrationAward: '仲裁裁决书',
  other: '其他',
  evidence: '证据材料',
  contract: '合同/协议',
  chatRecord: '聊天记录',
  transferRecord: '转账记录',
  privacy: '我已阅读并同意《隐私保护协议》',
  service: '我已阅读并同意《法律服务协议》',
  legal: '我已阅读并同意《法律服务协议》',
  truthful: '我确认提交的信息真实有效',
  truth: '我确认提交的信息真实有效',
  authorize: '我授权平台将案件信息提供给承办律师',
  auth: '我授权平台将案件信息提供给承办律师'
}

const getFieldLabel = (key) => fieldLabelMap[key] || key
const pickEntries = (keys) => keys
  .filter((key) => Object.prototype.hasOwnProperty.call(parsedFormData.value, key))
  .map((key) => ({ key, label: getFieldLabel(key), value: parsedFormData.value[key] }))

const identityEntries = computed(() => pickEntries(identityKeys))
const contactEntries = computed(() => pickEntries(contactKeys))
const makeDemandEntry = (key, value) => ({
  key,
  label: getFieldLabel(key),
  value,
  wide: wideDemandKeys.has(key)
})

const demandSections = computed(() => {
  const data = parsedFormData.value
  const usedKeys = new Set()
  const sections = demandSectionConfig.map((section) => {
    const entries = section.keys
      .filter((key) => Object.prototype.hasOwnProperty.call(data, key) && !hiddenDemandKeys.has(key))
      .map((key) => {
        usedKeys.add(key)
        return makeDemandEntry(key, data[key])
      })

    return { ...section, entries }
  }).filter((section) => section.entries.length)

  const otherEntries = Object.entries(data)
    .filter(([key]) => !hiddenDemandKeys.has(key) && !usedKeys.has(key))
    .map(([key, value]) => makeDemandEntry(key, value))

  if (otherEntries.length) {
    sections.push({
      title: '其他信息',
      tone: 'gray',
      entries: otherEntries
    })
  }

  return sections
})
const hasDemandData = computed(() => demandSections.value.some((section) => section.entries.length))

const displayFileList = (value) => {
  const files = Array.isArray(value) ? value : [value]
  return files.map((file) => {
    if (!file || typeof file !== 'object') return file
    const sizeText = file.size ? `（${Math.round(Number(file.size) / 1024)} KB）` : ''
    return `${file.name || file.fileName || file.url || file.fileUrl || '附件'}${sizeText}`
  }).join('、')
}

const isFileLike = (value) => {
  return value && typeof value === 'object' && (
    'name' in value ||
    'fileName' in value ||
    'url' in value ||
    'fileUrl' in value
  )
}

const findRegionPath = (values, options) => {
  if (!Array.isArray(values) || values.length === 0) return []
  const [current, ...rest] = values

  for (const option of options || []) {
    if (String(option.value) === String(current) || option.label === current) {
      if (rest.length === 0) return [option.label]
      const childPath = findRegionPath(rest, option.children || [])
      return [option.label, ...childPath]
    }
  }

  return values
}

const displayRegion = (value) => {
  if (!Array.isArray(value)) return value || '-'
  return findRegionPath(value, regionOptions.value).join(' / ') || '-'
}

const displayValue = (value, key = '') => {
  if (key === 'region') return displayRegion(value)
  if (key === 'consultMode') return consultModeValueLabelMap[value] || value || '-'
  if (value === true) return '是'
  if (value === false) return '否'
  if (Array.isArray(value)) {
    if (value.length === 0) return '-'
    if (value.every((item) => isFileLike(item))) return displayFileList(value)
    return value.map((item) => valueLabelMap[item] || item).join('、')
  }
  if (isFileLike(value)) return displayFileList(value)
  if (value && typeof value === 'object') return JSON.stringify(value)
  if (typeof value === 'string' && /^\d{4}-\d{2}-\d{2}T/.test(value)) {
    return value.slice(0, 10)
  }
  const displayText = valueLabelMap[value] || value || '-'
  if (displayText !== '-' && ['budget', 'caseAmount'].includes(key)) {
    return `${displayText} 元`
  }
  return displayText
}

const getStatusType = (status) => {
  if (status === '处理中') return 'primary'
  if (status === '待客户确认') return 'warning'
  if (status === '待评价') return 'warning'
  if (status === '已完成') return 'success'
  if (status === '待接单') return 'info'
  if (status === '平台介入') return 'danger'
  return 'info'
}

const getResultStatusText = (status) => {
  if (status === 0) return '已提交'
  if (status === 1) return '用户已确认'
  if (status === 2) return '需修改'
  return '未知'
}

const getRevisionStatusText = (status) => {
  if (status === 0) return '待处理'
  if (status === 1) return '已重新提交'
  if (status === 2) return '平台介入'
  return '未知'
}

const getRevisionStatusClass = (status) => {
  if (status === 0) return 'status-pending'
  if (status === 1) return 'status-submitted'
  if (status === 2) return 'status-intervention'
  return 'status-muted'
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
  if (isRevisionMode.value && serviceResult.value) {
    resultForm.value = {
      title: serviceResult.value.title || '',
      content: serviceResult.value.content || '',
      attachments: (serviceResult.value.attachments || []).map((item) => ({
        fileName: item.fileName,
        fileUrl: item.fileUrl,
        fileType: item.fileType || ''
      }))
    }
  } else if (!resultDialogVisible.value) {
    resultForm.value = {
      title: '',
      content: '',
      attachments: []
    }
  }
  attachmentUploadFiles.value = resultForm.value.attachments.map((item) => ({
    name: item.fileName,
    url: item.fileUrl,
    status: 'success',
    response: { data: item }
  }))
  resultDialogVisible.value = true
}

const beforeAttachmentUpload = (file) => {
  const maxSize = 10 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.warning('单个附件不能超过10MB')
    return false
  }
  return true
}

const handleAttachmentUpload = async (options) => {
  attachmentUploadCount.value += 1
  try {
    const res = await uploadServiceResultAttachment(options.file)
    const attachment = res?.data?.data
    if (!attachment?.fileUrl) {
      throw new Error(res?.data?.message || '附件上传失败')
    }
    resultForm.value.attachments.push(attachment)
    const target = attachmentUploadFiles.value.find((item) => item.uid === options.file.uid)
    if (target) {
      target.name = attachment.fileName
      target.url = attachment.fileUrl
      target.status = 'success'
      target.response = { data: attachment }
    }
    options.onSuccess?.(attachment)
    ElMessage.success('附件上传成功')
  } catch (error) {
    options.onError?.(error)
    ElMessage.error(error?.response?.data?.message || error?.message || '附件上传失败')
  } finally {
    attachmentUploadCount.value = Math.max(0, attachmentUploadCount.value - 1)
  }
}

const handleAttachmentRemove = (uploadFile) => {
  const fileUrl = uploadFile?.url || uploadFile?.response?.data?.fileUrl || uploadFile?.response?.fileUrl
  const fileName = uploadFile?.name
  resultForm.value.attachments = resultForm.value.attachments.filter((item) => {
    if (fileUrl) return item.fileUrl !== fileUrl
    return item.fileName !== fileName
  })
  attachmentUploadFiles.value = attachmentUploadFiles.value.filter((item) => item.uid !== uploadFile.uid)
}

const handleSubmitResult = async () => {
  if (!resultForm.value.title.trim()) {
    ElMessage.warning('请填写结果标题')
    return
  }
  if (attachmentUploading.value) {
    ElMessage.warning('附件还在上传中，请稍后提交')
    return
  }
  const lawyerId = await loadCurrentLawyer()
  if (!lawyerId) return
  submitting.value = true
  try {
    const payload = {
      lawyerId,
      title: resultForm.value.title,
      content: resultForm.value.content,
      attachments: resultForm.value.attachments
    }
    if (isRevisionMode.value) {
      await updateServiceResult(serviceResult.value.id, payload)
    } else {
      await submitServiceResult(orderId.value, payload)
    }
    ElMessage.success(isRevisionMode.value ? '服务结果已重新提交' : '服务结果提交成功')
    resultDialogVisible.value = false
    resultForm.value = { title: '', content: '', attachments: [] }
    attachmentUploadFiles.value = []
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
            <div class="panel-tags">
              <el-tag v-if="order.assignmentType === 'DIRECT'" type="warning" effect="dark">用户指定</el-tag>
              <el-tag :type="getStatusType(order.status)" effect="plain">{{ order.status }}</el-tag>
            </div>
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
            <div v-if="order.assignmentType === 'DIRECT'">
              <span>指定律师</span>
              <strong>{{ order.targetLawyerId || '-' }}</strong>
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
            <el-button v-if="canSubmitResult && !isRevisionMode" type="primary" size="large" @click="openResultDialog">
              提交服务结果
            </el-button>
            <el-button v-if="canViewResult && !serviceResult" size="large" @click="loadResult">查看服务结果</el-button>
          </div>
        </article>

        <article class="info-panel folded-panel">
          <el-collapse v-model="collapsedInfo">
            <el-collapse-item name="identity">
              <template #title>
                <span class="collapse-title">用户实名信息</span>
              </template>
              <div v-if="identityEntries.length" class="compact-info-list">
                <div v-for="item in identityEntries" :key="item.key" class="compact-info-row">
                  <span>{{ item.label }}</span>
                  <strong>{{ displayValue(item.value, item.key) }}</strong>
                </div>
              </div>
              <el-empty v-else description="暂无实名信息" />
            </el-collapse-item>

            <el-collapse-item name="contact">
              <template #title>
                <span class="collapse-title">联系补充信息</span>
              </template>
              <div v-if="contactEntries.length" class="compact-info-list">
                <div v-for="item in contactEntries" :key="item.key" class="compact-info-row">
                  <span>{{ item.label }}</span>
                  <strong>{{ displayValue(item.value, item.key) }}</strong>
                </div>
              </div>
              <el-empty v-else description="暂无联系补充信息" />
            </el-collapse-item>
          </el-collapse>
        </article>
      </section>

      <section class="demand-panel">
        <div class="panel-title">
          <h2>用户需求</h2>
        </div>
        <div v-if="hasDemandData" class="demand-sections">
          <div
            v-for="section in demandSections"
            :key="section.title"
            :class="['demand-section', `demand-section-${section.tone}`]"
          >
            <div class="section-title">
              <span></span>
              <strong>{{ section.title }}</strong>
            </div>
            <div class="form-data">
              <div
                v-for="item in section.entries"
                :key="item.key"
                :class="['form-row', { 'form-row-wide': item.wide }]"
              >
                <span>{{ item.label }}</span>
                <p>{{ displayValue(item.value, item.key) }}</p>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无需求详情" />
      </section>

      <section v-if="canViewResult || serviceResult" class="result-panel" v-loading="resultLoading">
        <div class="panel-title">
          <h2>律师处理流程</h2>
          <el-tag v-if="serviceResult" type="success" effect="plain">
            {{ getResultStatusText(serviceResult.status) }}
          </el-tag>
        </div>

        <template v-if="serviceResult">
          <div class="process-list">
            <article v-if="initialSubmission" class="process-item process-item-result">
              <div class="process-marker"></div>
              <div class="process-body">
                <div class="process-head">
                  <div>
                    <strong>提交结果</strong>
                    <span>{{ formatTime(initialSubmission.createdAt) }}</span>
                  </div>
                  <em>已提交</em>
                </div>
                <h3>{{ initialSubmission.title }}</h3>
                <p class="result-content">{{ initialSubmission.content || '无补充说明' }}</p>
                <div v-if="initialSubmission.attachments?.length" class="attachment-list">
                  <a
                    v-for="file in initialSubmission.attachments"
                    :key="file.id || file.fileUrl"
                    :href="file.fileUrl"
                    target="_blank"
                  >
                    {{ file.fileName }}
                  </a>
                </div>
              </div>
            </article>

            <article
              v-for="item in serviceResult.revisionRequests || []"
              :key="item.id"
              class="process-item process-item-revision"
            >
              <div class="process-marker"></div>
              <div class="process-body revision-process-body">
                <div class="revision-main">
                  <div class="process-head">
                    <div>
                      <strong>第 {{ item.revisionNo }} 次修改意见</strong>
                      <span>{{ formatTime(item.createdAt) }}</span>
                    </div>
                  </div>
                  <p>{{ item.content }}</p>
                </div>
                <div class="revision-side">
                  <span :class="['revision-status', getRevisionStatusClass(item.status)]">
                    {{ getRevisionStatusText(item.status) }}
                  </span>
                  <el-button
                    v-if="Number(item.status) === 0 && isRevisionMode"
                    class="revision-action-btn"
                    type="warning"
                    size="default"
                    @click="openResultDialog"
                  >
                    提交结果
                  </el-button>
                  <el-button
                    v-else-if="getRevisionSubmission(item)"
                    class="revision-action-btn detail-action-btn"
                    type="warning"
                    size="small"
                    @click="selectedResultRecord = getRevisionSubmission(item)"
                  >
                    查看详细
                  </el-button>
                </div>
              </div>
            </article>
          </div>
        </template>
        <el-empty v-else description="暂无服务结果" />
      </section>
    </template>

    <el-dialog v-model="resultDialogVisible" :title="isRevisionMode ? '重新提交服务结果' : '提交服务结果'" width="640px">
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
            <el-upload
              v-model:file-list="attachmentUploadFiles"
              class="result-upload"
              drag
              multiple
              :http-request="handleAttachmentUpload"
              :before-upload="beforeAttachmentUpload"
              :on-remove="handleAttachmentRemove"
            >
              <div class="upload-placeholder">
                <span>点击或拖拽文件到这里上传</span>
                <small>支持 PDF、Word、图片等常见文件，单个文件不超过 10MB</small>
              </div>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resultDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting || attachmentUploading" @click="handleSubmitResult">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog
      :model-value="!!selectedResultRecord"
      width="640px"
      :title="selectedResultRecord ? `${getSubmissionTitle(selectedResultRecord)}详情` : '提交结果详情'"
      @close="selectedResultRecord = null"
    >
      <template v-if="selectedResultRecord">
        <div class="result-dialog-meta">
          <span>{{ formatTime(selectedResultRecord.createdAt) }}</span>
        </div>
        <h3 class="result-dialog-title">{{ selectedResultRecord.title }}</h3>
        <p class="result-dialog-content">{{ selectedResultRecord.content || '无补充说明' }}</p>
        <div v-if="selectedResultRecord.attachments?.length" class="attachment-list">
          <a
            v-for="file in selectedResultRecord.attachments"
            :key="file.id || file.fileUrl"
            :href="file.fileUrl"
            target="_blank"
          >
            {{ file.fileName }}
          </a>
        </div>
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
.demand-panel,
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
  grid-template-columns: minmax(0, 1.25fr) minmax(320px, 0.75fr);
  gap: 18px;
  align-items: start;
}

.info-panel,
.demand-panel,
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

.panel-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.info-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.folded-panel {
  padding-top: 8px;
  padding-bottom: 8px;
}

.folded-panel :deep(.el-collapse) {
  border-top: 0;
  border-bottom: 0;
}

.folded-panel :deep(.el-collapse-item__header) {
  min-height: 52px;
  border-bottom-color: #edf1f7;
  color: #172033;
  font-weight: 700;
}

.folded-panel :deep(.el-collapse-item__wrap) {
  border-bottom-color: #edf1f7;
}

.folded-panel :deep(.el-collapse-item__content) {
  padding-bottom: 14px;
}

.collapse-title {
  font-size: 16px;
}

.compact-info-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.compact-info-row {
  display: grid;
  grid-template-columns: 96px minmax(0, 1fr);
  gap: 12px;
  padding: 12px 14px;
  background: #f6f8fc;
  border-radius: 10px;
}

.compact-info-row span {
  color: #667085;
  font-size: 13px;
}

.compact-info-row strong {
  color: #172033;
  font-size: 14px;
  word-break: break-word;
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
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.demand-sections {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.demand-section {
  padding: 16px;
  border: 1px solid var(--section-border);
  border-radius: 12px;
  background: var(--section-bg);
}

.demand-section-blue {
  --section-bg: #f8fbff;
  --section-border: #d6e4ff;
  --section-mark: #2563eb;
}

.demand-section-amber {
  --section-bg: #fffaf0;
  --section-border: #fde7b5;
  --section-mark: #d97706;
}

.demand-section-green {
  --section-bg: #f6fef9;
  --section-border: #bbf7d0;
  --section-mark: #16a34a;
}

.demand-section-purple {
  --section-bg: #fbf7ff;
  --section-border: #e9d5ff;
  --section-mark: #9333ea;
}

.demand-section-gray {
  --section-bg: #f8fafc;
  --section-border: #e2e8f0;
  --section-mark: #64748b;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: #172033;
}

.section-title span {
  width: 4px;
  height: 18px;
  border-radius: 999px;
  background: var(--section-mark);
}

.section-title strong {
  font-size: 15px;
}

.demand-section .form-row {
  background: #ffffff;
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.form-row-wide {
  grid-column: span 3;
}

.form-row p {
  margin: 0;
  line-height: 1.8;
  color: #172033;
  word-break: break-word;
  white-space: pre-wrap;
}

.demand-panel,
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

.process-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.process-item {
  display: grid;
  grid-template-columns: 24px minmax(0, 1fr);
  gap: 12px;
  position: relative;
}

.process-item::before {
  content: '';
  position: absolute;
  left: 11px;
  top: 28px;
  bottom: -20px;
  width: 2px;
  background: #e5eaf3;
}

.process-item:last-child::before {
  display: none;
}

.process-marker {
  width: 24px;
  height: 24px;
  border: 5px solid #eff6ff;
  border-radius: 999px;
  background: #2563eb;
}

.process-body {
  padding: 16px;
  border: 1px solid #dbeafe;
  border-radius: 12px;
  background: #f8fbff;
}

.process-item-revision .process-body {
  border-color: #fde7b5;
  background: #fffaf0;
}

.process-head {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  align-items: center;
  margin-bottom: 10px;
}

.process-head div {
  min-width: 0;
}

.process-head strong {
  display: block;
  color: #172033;
  font-size: 16px;
}

.process-head span {
  display: block;
  margin-top: 4px;
  color: #667085;
  font-size: 13px;
}

.process-head em {
  padding: 4px 10px;
  border-radius: 999px;
  background: #ecfdf3;
  color: #027a48;
  font-size: 12px;
  font-style: normal;
  font-weight: 700;
}

.process-body h3 {
  margin-bottom: 8px;
}

.revision-process-body {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 120px;
  gap: 18px;
  align-items: center;
}

.revision-main p {
  margin: 0;
  color: #344054;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.revision-side {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.revision-status {
  display: inline-flex;
  justify-content: center;
  min-width: 84px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.status-pending {
  background: #fff7ed;
  color: #c2410c;
}

.status-submitted {
  background: #ecfdf3;
  color: #027a48;
}

.status-intervention {
  background: #fef3c7;
  color: #92400e;
}

.status-muted {
  background: #f2f4f7;
  color: #475467;
}

.revision-action-btn {
  flex: 0 0 auto;
  min-width: 92px;
  height: 36px;
  padding: 0 16px;
  border-color: #f59e0b;
  background: #f59e0b;
  color: #fff;
  font-weight: 700;
}

.revision-action-btn:hover,
.revision-action-btn:focus {
  border-color: #d97706;
  background: #d97706;
  color: #fff;
}

.detail-action-btn {
  min-width: 82px;
  height: 30px;
  padding: 0 12px;
  font-size: 12px;
}

.result-dialog-meta {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 14px;
  color: #667085;
}

.result-dialog-title {
  margin: 0 0 12px;
  color: #172033;
}

.result-dialog-content {
  margin: 0;
  color: #344054;
  line-height: 1.9;
  white-space: pre-wrap;
  word-break: break-word;
}

.attachment-editor {
  width: 100%;
}

.result-upload {
  width: 100%;
}

.result-upload :deep(.el-upload) {
  width: 100%;
}

.result-upload :deep(.el-upload-dragger) {
  width: 100%;
  padding: 22px 16px;
  border-radius: 10px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  color: #172033;
}

.upload-placeholder span {
  font-weight: 700;
}

.upload-placeholder small {
  color: #667085;
}

@media (max-width: 900px) {
  .page-head,
  .detail-grid {
    display: flex;
    flex-direction: column;
    align-items: stretch;
  }

  .info-list,
  .form-data {
    grid-template-columns: 1fr;
  }

  .form-row-wide {
    grid-column: span 1;
  }

  .revision-process-body {
    grid-template-columns: 1fr;
  }

  .revision-side {
    align-items: flex-start;
  }
}
</style>
