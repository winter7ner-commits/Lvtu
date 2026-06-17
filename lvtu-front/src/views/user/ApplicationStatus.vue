<template>
  <div class="page">
    <div class="bg-glow glow1"></div>
    <div class="bg-glow glow2"></div>

    <div class="status-card">
      <div class="top">
        <div>
          <h1>{{ statusTitle }}</h1>
          <p class="desc">{{ statusDesc }}</p>
        </div>
        <div class="badge" :class="statusClass">{{ statusText }}</div>
      </div>

      <div class="timeline">
        <div class="item" :class="{ active: true }">
          <div class="circle"></div>
          <div class="content">
            <h3>提交申请</h3>
            <p>{{ formatTime(application?.createTime) }}</p>
          </div>
        </div>
        <div class="item" :class="{ active: application?.status === 0 || application?.status === 1 || application?.status === 2 }">
          <div class="circle"></div>
          <div class="content">
            <h3>平台审核</h3>
            <p v-if="application?.status === 0">正在审核您的律师资格信息</p>
            <p v-else-if="application?.status === 1">审核通过</p>
            <p v-else-if="application?.status === 2">审核未通过</p>
            <p v-else>等待审核</p>
          </div>
        </div>
        <div class="item" :class="{ active: application?.status === 1 || application?.status === 2 }">
          <div class="circle"></div>
          <div class="content">
            <h3>审核结果</h3>
            <p v-if="application?.status === 1">您已成功成为认证律师</p>
            <p v-else-if="application?.status === 2">申请失败，请查看驳回原因</p>
            <p v-else>等待审核结果</p>
          </div>
        </div>
      </div>

      <div class="info-card">
        <h2>申请资料</h2>
        <div class="info-grid">
          <div class="info-item"><span>申请编号</span><strong>{{ application?.applicationId || '—' }}</strong></div>
          <div class="info-item"><span>申请类型</span><strong>{{ application?.applyType === 1 ? '资料变更' : '首次入驻' }}</strong></div>
          <div class="info-item"><span>提交时间</span><strong>{{ formatTime(application?.createTime) }}</strong></div>
          <div class="info-item"><span>执业证号</span><strong>{{ application?.licenseNo || '—' }}</strong></div>
          <div class="info-item"><span>所属律所</span><strong>{{ application?.lawFirm || '—' }}</strong></div>
          <div class="info-item"><span>执业起始年</span><strong>{{ application?.practiceStartYear || '—' }} 年</strong></div>
          <div class="info-item"><span>审核时间</span><strong>{{ formatTime(application?.auditTime) || '—' }}</strong></div>
        </div>

        <div v-if="application?.licenseUrl" class="license-preview">
          <span>执业证书</span>
          <img :src="getFullImageUrl(application.licenseUrl)" class="license-thumb" @click="openPreview(getFullImageUrl(application.licenseUrl))" />
          <el-button size="small" @click="openPreview(getFullImageUrl(application.licenseUrl))">查看大图</el-button>
        </div>

        <div v-if="application?.status === 2" class="reject-box">
          <div class="reject-title">驳回原因</div>
          <div class="reject-content">{{ application.rejectReason || '未提供具体原因，请联系客服' }}</div>
        </div>
      </div>

      <div class="actions">
        <button class="ghost" @click="goBack">返回</button>
        <button class="ghost" @click="refresh" :disabled="loading">
          {{ loading ? '加载中...' : '刷新状态' }}
        </button>
        <button v-if="application?.status === 2" class="primary" @click="reApply">重新申请</button>
        <button v-if="application?.status === 1" class="primary" @click="goToModify">修改资料</button>
        <button v-if="!application || application?.status === undefined" class="primary" @click="goToApply">立即申请</button>
        <button v-if="application?.status === 0" class="primary disabled-btn" disabled>审核中，请耐心等待</button>
      </div>
    </div>

    <div v-if="previewImage" class="img-modal" @click="closePreview">
      <div class="modal-inner" @click.stop>
        <button class="modal-close" @click="closePreview">✕</button>
        <img :src="previewImage" class="modal-img" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyApplication } from '@/api/application'
import { ElMessage } from 'element-plus'
import { goBack as goBackToPrevious } from '@/utils/navigation'

const router = useRouter()
const application = ref(null)
const previewImage = ref(null)
const loading = ref(false)
const BASE_URL = ''

const getFullImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return BASE_URL + (url.startsWith('/') ? url : '/' + url)
}

const openPreview = (url) => { previewImage.value = url }
const closePreview = () => { previewImage.value = null }

const getCurrentUserId = () => {
  const stored = localStorage.getItem('userId')
  if (stored) return Number(stored)
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
  return currentUser?.userId ? Number(currentUser.userId) : null
}

const userId = getCurrentUserId()

const loadData = async () => {
  if (!userId) {
    ElMessage.warning('用户未登录')
    return
  }
  loading.value = true
  try {
    const res = await getMyApplication(userId)
    // 兼容多种返回结构
    let data = res?.data?.data || res?.data || null
    if (data && typeof data === 'object') {
      application.value = {
        applicationId: data.applicationId,
        userId: data.userId,
        licenseNo: data.licenseNo,
        licenseUrl: data.licenseUrl,
        lawFirm: data.lawFirm,
        practiceStartYear: data.practiceStartYear,
        status: data.status,
        rejectReason: data.rejectReason,
        createTime: data.createTime,
        auditTime: data.auditTime,
        applyType: data.applyType
      }
    } else {
      application.value = null
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('获取申请信息失败')
    application.value = null
  } finally {
    loading.value = false
  }
}

const refresh = () => loadData()

const statusText = computed(() => {
  const s = application.value?.status
  return s === 0 ? '审核中' : s === 1 ? '已通过' : s === 2 ? '已驳回' : '未申请'
})
const statusTitle = computed(() => {
  const s = application.value?.status
  return s === 0 ? '认证审核中' : s === 1 ? '认证已通过' : s === 2 ? '认证失败' : '暂无申请'
})
const statusDesc = computed(() => {
  const s = application.value?.status
  return s === 0 ? '平台正在审核您的律师认证资料' : s === 1 ? '您已获得律师身份认证' : s === 2 ? '您的申请未通过审核，请查看驳回原因' : '您当前暂无律师认证申请'
})
const statusClass = computed(() => {
  const s = application.value?.status
  return s === 0 ? 'warning' : s === 1 ? 'success' : s === 2 ? 'danger' : 'default'
})
const formatTime = (t) => t ? new Date(t).toLocaleString('zh-CN', { year:'numeric', month:'2-digit', day:'2-digit', hour:'2-digit', minute:'2-digit' }) : '—'

const goBack = () => {
  goBackToPrevious(router, '/settings')
}
const reApply = () => router.push({ path: '/apply', query: { apply_type: 0 } })
const goToModify = () => router.push({ path: '/apply', query: { apply_type: 1 } })
const goToApply = () => router.push({ path: '/apply', query: { apply_type: 0 } })

onMounted(loadData)
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 32px 80px;
  background: radial-gradient(circle at top left, rgba(168,183,201,0.25), transparent 40%),
  radial-gradient(circle at bottom right, rgba(125,148,172,0.18), transparent 45%),
  linear-gradient(180deg, #F5F8FB 0%, #EEF3F7 100%);
  position: relative;
}
.bg-glow {
  position: absolute;
  width: 420px;
  height: 420px;
  border-radius: 50%;
  filter: blur(100px);
  z-index: 0;
}
.glow1 { background: rgba(168,183,201,0.18); top: -120px; left: -100px; }
.glow2 { background: rgba(125,148,172,0.16); bottom: -120px; right: -100px; }

.status-card {
  max-width: 1100px;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 32px;
  position: relative;
  z-index: 1;
}
.top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}
h1 {
  font-size: 28px;
  color: #1a2c3e;
  margin: 0 0 8px 0;
}
.desc {
  color: #6c7a8a;
  margin: 0;
}
.badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}
.warning { background: #fff7e6; color: #d48806; }
.success { background: #f6ffed; color: #389e0d; }
.danger { background: #fff2f0; color: #cf1322; }
.default { background: #f5f5f5; color: #8c8c8c; }

.timeline {
  display: flex;
  justify-content: space-between;
  margin-bottom: 32px;
  padding: 0 20px;
}
.item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  opacity: 0.4;
  position: relative;
}
.item:not(:last-child):before {
  content: '';
  position: absolute;
  top: 12px;
  left: 50%;
  width: 100%;
  height: 2px;
  background: #e0e6ed;
}
.item.active {
  opacity: 1;
}
.item.active .circle {
  background: #1a73e8;
  border-color: #1a73e8;
}
.circle {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #fff;
  border: 2px solid #d0d8e3;
  margin-bottom: 12px;
  position: relative;
  z-index: 1;
  background: #fff;
}
.item.active .circle {
  border-color: #1a73e8;
  background: #1a73e8;
  box-shadow: 0 0 0 4px rgba(26,115,232,0.2);
}
.content h3 {
  font-size: 14px;
  color: #1a2c3e;
  margin: 0 0 4px 0;
}
.content p {
  font-size: 12px;
  color: #8c9aab;
  margin: 0;
}

.info-card {
  background: #f8fafc;
  border-radius: 20px;
  padding: 24px;
  margin-top: 16px;
}
.info-card h2 {
  font-size: 18px;
  color: #1a2c3e;
  margin: 0 0 20px 0;
}
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.info-item {
  display: flex;
  flex-direction: column;
}
.info-item span {
  font-size: 12px;
  color: #8c9aab;
  margin-bottom: 4px;
}
.info-item strong {
  color: #1a2c3e;
  font-size: 14px;
}

.license-preview {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #e8edf2;
}
.license-preview span {
  font-size: 13px;
  color: #5a6e7c;
}
.license-thumb {
  width: 80px;
  height: 56px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid #e0e6ed;
}
.reject-box {
  margin-top: 20px;
  background: #fff2f0;
  border: 1px solid #fecaca;
  border-radius: 12px;
  padding: 16px;
}
.reject-title {
  font-weight: 600;
  color: #cf1322;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.reject-content {
  color: #a8071a;
  font-size: 14px;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid #e8edf2;
}
button {
  border: none;
  padding: 10px 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.primary {
  background: #1a73e8;
  color: white;
}
.primary:hover:not(:disabled) {
  background: #1557b0;
  transform: translateY(-1px);
}
.ghost {
  background: #f0f2f5;
  color: #5a6e7c;
}
.ghost:hover {
  background: #e4e9ef;
}
.disabled-btn {
  background: #d0d8e3;
  cursor: not-allowed;
  opacity: 0.7;
}

.img-modal {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.modal-inner {
  position: relative;
}
.modal-close {
  position: absolute;
  top: -40px;
  right: -40px;
  background: rgba(255,255,255,0.2);
  border: none;
  color: #fff;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  font-size: 20px;
  cursor: pointer;
}
.modal-img {
  max-width: 85vw;
  max-height: 85vh;
  border-radius: 12px;
  object-fit: contain;
}


</style>
