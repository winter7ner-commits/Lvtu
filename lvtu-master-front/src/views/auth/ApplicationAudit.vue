<template>
  <div class="page">
    <div class="bg-glow glow1"></div>
    <div class="bg-glow glow2"></div>

    <div class="toolbar">
      <div>
        <h1>律师认证审核</h1>
      </div>
      <button class="refresh-btn" @click="load" :disabled="loading">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <polyline points="23 4 23 10 17 10"/>
          <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/>
        </svg>
        刷新
      </button>
    </div>


    <div class="content-container">
      <div class="filter-tabs">
        <button
            v-for="tab in statusTabs"
            :key="tab.value"
            class="tab-btn"
            :class="{ active: activeTab === tab.value }"
            @click="activeTab = tab.value"
        >
          {{ tab.label }}
          <span class="tab-count">{{ countByStatus(tab.value) }}</span>
        </button>
      </div>

      <div v-if="!loading && filteredList.length === 0" class="empty-state">
        <p>暂无申请</p>
      </div>

      <div v-else class="audit-list">
        <div v-for="item in filteredList" :key="item.applicationId" class="audit-card">
          <!-- 卡片内容保持不变 -->
          <div class="card-head">
            <div class="applicant-info">
              <div class="avatar">
                <img v-if="item.avatarUrl" :src="getFullImageUrl(item.avatarUrl)" class="avatar-img" />
                <span v-else>{{ getInitial(item.realname || item.username) }}</span>
              </div>
              <div>
                <p class="applicant-name">{{ item.realname || item.username || `用户 #${item.userId}` }}</p>
                <p class="applicant-sub">申请ID #{{ item.applicationId }} · {{ formatDate(item.createTime) }}</p>
              </div>
            </div>
            <span class="status-badge" :class="statusBadgeClass(item.status)">{{ statusLabel(item.status) }}</span>
          </div>

          <div class="info-grid">
            <div class="info-item"><span>执业证号</span><strong>{{ item.licenseNo || '—' }}</strong></div>
            <div class="info-item"><span>所属律所</span><strong>{{ item.lawFirm || '—' }}</strong></div>
            <div class="info-item"><span>执业起始年</span><strong>{{ item.practiceStartYear || '—' }} 年</strong></div>
            <div class="info-item"><span>申请类型</span><strong>{{ item.applyType === 1 ? '资料变更' : '首次入驻' }}</strong></div>
          </div>

          <div v-if="item.licenseUrl" class="license-preview">
            <span>执业证书</span>
            <img :src="getFullImageUrl(item.licenseUrl)" class="license-thumb" @click="openPreview(getFullImageUrl(item.licenseUrl))" />
            <el-button size="small" type="primary" link @click="openPreview(getFullImageUrl(item.licenseUrl))">查看大图</el-button>
          </div>

          <div v-if="item.status === 2 && item.rejectReason" class="reject-info">
            <span class="reject-label">驳回原因：</span>{{ item.rejectReason }}
          </div>

          <div v-if="item.status === 0" class="card-actions">
            <button class="btn-success" :disabled="processingId === item.applicationId" @click="approve(item)">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <polyline points="20 6 9 17 4 12"/>
              </svg>
              通过
            </button>
            <button class="btn-danger" :disabled="processingId === item.applicationId" @click="reject(item)">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
              驳回
            </button>
          </div>
          <div v-else-if="item.auditTime" class="audit-time">审核于 {{ formatDate(item.auditTime) }}</div>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-wrap">加载中...</div>
    <div v-if="previewUrl" class="img-modal" @click="previewUrl = null">
      <div class="modal-inner" @click.stop>
        <button class="modal-close" @click="previewUrl = null">✕</button>
        <img :src="previewUrl" class="modal-img" />
      </div>
    </div>
  </div>
</template>

<script setup>

import { ref, computed, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getApplicationList, approveApplication, rejectApplication } from '@/api/application'

const list = ref([])
const loading = ref(false)
const processingId = ref(null)
const activeTab = ref(-1)
const previewUrl = ref(null)

const getFullImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  if (url.startsWith('/')) return 'http://localhost:8080' + url
  return 'http://localhost:8080/' + url
}
const openPreview = (url) => { previewUrl.value = url }
const statusTabs = [
  { label: '全部', value: -1 },
  { label: '待审核', value: 0 },
  { label: '已通过', value: 1 },
  { label: '已驳回', value: 2 }
]
const filteredList = computed(() => {
  const val = activeTab.value
  if (val === -1) return list.value
  return list.value.filter(item => item.status === val)
})
const countByStatus = (status) => {
  if (status === -1) return list.value.length
  return list.value.filter(i => i.status === status).length
}
const load = async () => {
  loading.value = true
  try {
    const res = await getApplicationList()
    let data = res?.data?.data || res?.data || []
    list.value = data.map(item => ({
      applicationId: item.applicationId || item.application_id,
      userId: item.userId || item.user_id,
      licenseNo: item.licenseNo || item.license_no,
      licenseUrl: item.licenseUrl || item.license_url,
      lawFirm: item.lawFirm || item.law_firm,
      practiceStartYear: item.practiceStartYear || item.practice_start_year,
      status: item.status,
      rejectReason: item.rejectReason || item.reject_reason,
      createTime: item.createTime || item.create_time,
      auditTime: item.auditTime || item.audit_time,
      applyType: item.applyType ?? item.apply_type,
      username: item.username,
      realname: item.realname,
      avatarUrl: item.avatarUrl
    }))
  } catch (err) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}
const approve = async (item) => {
  await ElMessageBox.confirm('通过该申请？', '确认', { type: 'success' }).catch(() => null)
  processingId.value = item.applicationId
  try {
    await approveApplication(item.applicationId)
    ElMessage.success('已通过')
    await load()
  } catch { ElMessage.error('操作失败') }
  finally { processingId.value = null }
}
const reject = async (item) => {
  let reason = ''
  try {
    const { value } = await ElMessageBox.prompt('请输入驳回原因', '驳回', {
      inputValidator: (val) => !!val?.trim() || '原因不能为空'
    })
    reason = value
  } catch { return }
  processingId.value = item.applicationId
  try {
    await rejectApplication(item.applicationId, reason)
    ElMessage.success('已驳回')
    await load()
  } catch { ElMessage.error('操作失败') }
  finally { processingId.value = null }
}
const getInitial = (name) => name?.slice(0,1).toUpperCase() || '?'
const formatDate = (dt) => dt ? new Date(dt).toLocaleString() : '—'
const statusLabel = (s) => s === 0 ? '待审核' : s === 1 ? '已通过' : s === 2 ? '已驳回' : '未知'
const statusBadgeClass = (s) => {
  if (s === 0) return 'badge-warning'
  if (s === 1) return 'badge-success'
  if (s === 2) return 'badge-danger'
  return 'badge-default'
}
onMounted(load)
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


.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  background: #ffffff;
  border-radius: 24px;
  padding: 18px 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  position: relative;
  z-index: 1;
  max-width: 1100px;
  margin-left: auto;
  margin-right: auto;
}
.toolbar h1 {
  font-size: 26px;
  color: #1a2c3e;
  margin: 0;
}
.refresh-btn {
  background: #f0f2f5;
  border: none;
  padding: 8px 20px;
  border-radius: 30px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  color: #5a6e7c;
  transition: all 0.2s;
}
.refresh-btn:hover:not(:disabled) {
  background: #e4e9ef;
}

/* 内容容器：统一最大宽度，左右自动居中，保证筛选栏和卡片边缘对齐 */
.content-container {
  max-width: 1100px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.filter-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 28px;
  background: #ffffff;
  border-radius: 60px;
  padding: 8px 24px;
  width: 100%;           /* 占满父容器宽度，与卡片同宽 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  box-sizing: border-box;
}
.tab-btn {
  background: transparent;
  border: none;
  padding: 8px 24px;
  border-radius: 40px;
  color: #5a6e7c;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.tab-btn.active {
  background: #1a73e8;
  color: white;
}
.tab-count {
  margin-left: 8px;
  font-size: 12px;
  background: rgba(0,0,0,0.08);
  padding: 0 8px;
  border-radius: 20px;
}
.tab-btn.active .tab-count {
  background: rgba(255,255,255,0.25);
  color: white;
}

/* 卡片列表 */
.audit-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.audit-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 24px 28px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.2s;
  width: 100%;
  box-sizing: border-box;
}
.audit-card:hover {
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.1);
}


.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.applicant-info {
  display: flex;
  gap: 14px;
  align-items: center;
}
.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #e8edf2;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  overflow: hidden;
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.applicant-name {
  font-weight: 600;
  font-size: 16px;
  color: #1a2c3e;
  margin: 0;
}
.applicant-sub {
  font-size: 13px;
  color: #8c9aab;
  margin: 6px 0 0;
}
.status-badge {
  padding: 6px 16px;
  border-radius: 30px;
  font-size: 13px;
  font-weight: 500;
}
.badge-warning { background: #fff7e6; color: #d48806; }
.badge-success { background: #f6ffed; color: #389e0d; }
.badge-danger { background: #fff2f0; color: #cf1322; }
.badge-default { background: #f5f5f5; color: #8c8c8c; }

.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  background: #f8fafc;
  border-radius: 20px;
  padding: 16px 20px;
  margin-bottom: 16px;
}
.info-item {
  display: flex;
  flex-direction: column;
}
.info-item span {
  font-size: 12px;
  color: #8c9aab;
  margin-bottom: 6px;
}
.info-item strong {
  color: #1a2c3e;
  font-size: 15px;
}

.license-preview {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  padding: 8px 0;
}
.license-thumb {
  width: 80px;
  height: 56px;
  object-fit: cover;
  border-radius: 10px;
  cursor: pointer;
  border: 1px solid #e8edf2;
}
.reject-info {
  background: #fff2f0;
  border-radius: 16px;
  padding: 12px 18px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #cf1322;
}
.reject-label {
  font-weight: 600;
  margin-right: 8px;
}
.card-actions {
  display: flex;
  gap: 16px;
  padding-top: 16px;
  border-top: 1px solid #e8edf2;
}
.btn-success, .btn-danger {
  border: none;
  padding: 10px 28px;
  border-radius: 30px;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}
.btn-success:active, .btn-danger:active { transform: scale(0.96); }
.btn-success:disabled, .btn-danger:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-success { background: #52c41a; }
.btn-danger { background: #ff4d4f; }
.btn-success:hover:not(:disabled) { background: #389e0d; }
.btn-danger:hover:not(:disabled) { background: #cf1322; }

.audit-time {
  font-size: 13px;
  color: #8c9aab;
  padding-top: 12px;
  border-top: 1px solid #e8edf2;
}
.empty-state {
  background: #ffffff;
  padding: 60px;
  text-align: center;
  border-radius: 24px;
  color: #8c9aab;
}
.loading-wrap {
  text-align: center;
  padding: 40px;
  color: #8c9aab;
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
  border-radius: 16px;
  object-fit: contain;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .page {
    padding: 32px 40px;
  }
  .content-container {
    max-width: 960px;
  }
}
@media (max-width: 768px) {
  .page {
    padding: 20px;
  }
  .content-container {
    max-width: 100%;
  }
  .filter-tabs {
    flex-wrap: wrap;
    justify-content: center;
    border-radius: 30px;
    padding: 8px 16px;
  }
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  .audit-card {
    padding: 18px;
  }
  .toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>