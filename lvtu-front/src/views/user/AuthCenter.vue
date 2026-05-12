<template>
  <div class="page">
    <div class="bg-glow glow1"></div>
    <div class="bg-glow glow2"></div>

    <div class="content-container">

      <div class="back-header">
        <button class="back-btn" @click="goToSettings">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          返回
        </button>
      </div>

      <div class="hero-card">
        <div>
          <h1>认证中心</h1>
          <p class="desc">管理您的实名认证与律师认证</p>
        </div>
        <div class="status-card">
          <span>律师状态</span>
          <h2>{{ lawyerStatusText }}</h2>
        </div>
      </div>

      <div class="grid-area">
        <div class="feature-card">
          <div class="card-top">
            <h3>实名认证</h3>
            <el-tag :type="realNameVerified ? 'success' : 'info'" effect="light" round>
              {{ realNameVerified ? '已认证' : '未认证' }}
            </el-tag>
          </div>
          <p>根据法律法规，请完成实名认证后方可申请律师资格。</p>
          <button @click="goToRealName">{{ realNameVerified ? '查看认证信息' : '去实名' }}</button>
        </div>

        <div class="feature-card">
          <div class="card-top">
            <h3>律师认证</h3>
            <el-tag :type="lawyerStatusType" effect="light" round>{{ lawyerStatusText }}</el-tag>
          </div>
          <p v-if="lawyerStatus === 0">提交执业信息，等待管理员审核后获得律师身份。</p>
          <p v-else-if="lawyerStatus === 1">您已是认证律师，可享受律师专属功能。</p>
          <p v-else-if="lawyerStatus === 2">您的申请已被驳回，请修改后重新提交。</p>
          <p v-else-if="lawyerStatus === 3">您的申请正在审核中，请耐心等待。</p>
          <div class="action-buttons">
            <button v-if="lawyerStatus === 0" @click="goToApply" :disabled="isPending">申请认证</button>
            <button v-else-if="lawyerStatus === 1" @click="goToModify">修改资料</button>
            <button v-else-if="lawyerStatus === 2" @click="goToApply">重新申请</button>
          </div>
          <div v-if="isPending" class="pending-tip">您已有申请正在审核中，请等待审核结果。</div>
          <div v-if="lawyerStatus !== 0 && application?.applicationId" class="detail-link">
            <el-link type="primary" @click="goToDetail">查看申请详情 →</el-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { getMyApplication } from '@/api/application'
import { me } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const realNameVerified = ref(false)
const application = ref(null)

const fetchUserInfo = async () => {
  try {
    const response = await me()
    if (response?.code === 200 && response.data) {
      const userData = response.data
      authStore.setUser(userData)
      realNameVerified.value = userData.isVerified === true
    } else {
      console.error('获取用户信息失败', response?.message)
    }
  } catch (error) {
    console.error('请求 /me 异常', error)
  }
}

onMounted(async () => {
  await fetchUserInfo()
  const userId = authStore.user?.userId
  if (userId) {
    try {
      const appRes = await getMyApplication(userId)
      application.value = appRes?.data?.data || appRes?.data || null
    } catch (err) {
      console.error('获取申请状态失败', err)
    }
  }
})

const lawyerStatus = computed(() => {
  if (!application.value) return 0
  if (application.value.status === 1) return 1
  if (application.value.status === 2) return 2
  if (application.value.status === 0) return 3
  return 0
})

const lawyerStatusText = computed(() => {
  const s = lawyerStatus.value
  return s === 0 ? '未申请' : s === 1 ? '已通过' : s === 2 ? '已驳回' : s === 3 ? '审核中' : '未知'
})

const lawyerStatusType = computed(() => {
  const s = lawyerStatus.value
  return s === 0 ? 'info' : s === 1 ? 'success' : s === 2 ? 'danger' : s === 3 ? 'warning' : 'info'
})

const isPending = computed(() => application.value?.status === 0)

const goToRealName = () => router.push('/user/realname')
const goToApply = () => {
  if (!realNameVerified.value) {
    ElMessage.warning('请先完成实名认证')
    return
  }
  router.push({ path: '/apply', query: { apply_type: 0 } })
}
const goToModify = () => router.push({ path: '/apply', query: { apply_type: 1 } })
const goToDetail = () => router.push('/application-status')
const goToSettings = () => router.push('/settings')
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

.content-container {
  max-width: 1100px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

/* 返回按钮区域 */
.back-header {
  margin-bottom: 16px;
}
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #5F748B;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 30px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}
.back-btn:hover {
  background: #42566E;      /* 悬停加深 */
  transform: translateY(-1px);
}

.hero-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 28px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
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
.status-card {
  padding: 16px 28px;
  border-radius: 20px;
  background: #f8fafc;
  text-align: center;
}
.status-card span {
  color: #8c9aab;
  font-size: 13px;
}
.status-card h2 {
  margin: 6px 0 0;
  color: #1a2c3e;
  font-size: 20px;
}

.grid-area {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}
.feature-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 28px 32px;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.feature-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.1);
}
.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
h3 {
  color: #1a2c3e;
  font-size: 20px;
  margin: 0;
}
.feature-card p {
  color: #6c7a8a;
  line-height: 1.6;
  margin: 0 0 24px 0;
}
button {
  background: #1a73e8;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}
button:hover:not(:disabled) {
  background: #1557b0;
  transform: translateY(-1px);
}
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.pending-tip {
  margin-top: 16px;
  color: #d48806;
  font-size: 13px;
}
.detail-link {
  margin-top: 18px;
}
:deep(.el-tag) {
  border: none;
  background: #f0f2f5;
  color: #5a6e7c;
  padding: 6px 16px;
  border-radius: 30px;
}
:deep(.el-tag--success) {
  background: #f6ffed;
  color: #389e0d;
}
:deep(.el-tag--danger) {
  background: #fff2f0;
  color: #cf1322;
}
:deep(.el-tag--warning) {
  background: #fff7e6;
  color: #d48806;
}

</style>