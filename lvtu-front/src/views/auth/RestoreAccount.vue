<template>
  <div class="restore-page">
    <div class="restore-container">
      <div class="restore-card">
        <!-- 头部 -->
        <div class="restore-header">
          <img src="/icons/logo.png" alt="LVTU" class="logo" />
          <h2>恢复已注销账号</h2>
          <p>通过绑定的手机号找回您已注销的账号</p>
        </div>

        <!-- 步骤指示器 -->
        <div class="steps-indicator">
          <div class="step" :class="{ active: currentStep >= 1, done: currentStep > 1 }">
            <div class="step-circle">{{ currentStep > 1 ? '✓' : '1' }}</div>
            <span>输入手机号</span>
          </div>
          <div class="step-line" :class="{ active: currentStep > 1 }"></div>
          <div class="step" :class="{ active: currentStep >= 2, done: currentStep > 2 }">
            <div class="step-circle">{{ currentStep > 2 ? '✓' : '2' }}</div>
            <span>选择账号</span>
          </div>
          <div class="step-line" :class="{ active: currentStep > 2 }"></div>
          <div class="step" :class="{ active: currentStep >= 3 }">
            <div class="step-circle">3</div>
            <span>确认恢复</span>
          </div>
        </div>

        <!-- Step 1: 输入手机号 -->
        <div v-if="currentStep === 1" class="step-content">
          <div class="form-section">
            <label class="form-label">请输入注销账号时绑定的手机号</label>
            <el-input
              v-model="phone"
              placeholder="请输入手机号"
              maxlength="11"
              clearable
              size="large"
              @keyup.enter="handleQueryAccounts"
            >
              <template #prefix>
                <span class="input-prefix">📱</span>
              </template>
            </el-input>
            <p class="form-hint">系统将查询该手机号关联的所有已注销账号</p>
          </div>
          <button class="btn-primary" :disabled="!phone || loading" @click="handleQueryAccounts">
            {{ loading ? '查询中...' : '查询已注销账号' }}
          </button>
        </div>

        <!-- Step 2: 选择账号 -->
        <div v-if="currentStep === 2" class="step-content">
          <div class="section-title">
            <span class="section-icon">📋</span>
            找到 {{ deactivatedAccounts.length }} 个已注销账号
          </div>
          <p class="section-desc">请选择您要恢复的账号：</p>

          <div class="account-list">
            <div
              v-for="account in deactivatedAccounts"
              :key="account.userId"
              class="account-card"
              :class="{ selected: selectedAccountId === account.userId }"
              @click="selectAccount(account)"
            >
              <div class="account-radio">
                <div class="radio-circle" :class="{ checked: selectedAccountId === account.userId }">
                  <div v-if="selectedAccountId === account.userId" class="radio-dot"></div>
                </div>
              </div>
              <div class="account-info">
                <div class="account-name">
                  <span class="name-label">用户名</span>
                  <span class="name-value">{{ account.username }}</span>
                </div>
                <div class="account-meta">
                  <span class="meta-item">
                    <span class="meta-label">注册时间</span>
                    <span class="meta-value">{{ formatDate(account.createdTime) }}</span>
                  </span>
                  <span class="meta-item">
                    <span class="meta-label">账号ID</span>
                    <span class="meta-value">{{ account.userId }}</span>
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div class="btn-group">
            <button class="btn-secondary" @click="currentStep = 1">上一步</button>
            <button class="btn-primary" :disabled="!selectedAccountId" @click="handleCheckConflict">
              下一步
            </button>
          </div>
        </div>

        <!-- Step 3: 确认恢复 -->
        <div v-if="currentStep === 3" class="step-content">
          <!-- 无冲突 -->
          <div v-if="!conflictInfo.hasConflict" class="confirm-section">
            <div class="confirm-icon">✅</div>
            <h3>确认恢复账号</h3>
            <div class="confirm-info">
              <div class="info-row">
                <span class="info-label">恢复账号</span>
                <span class="info-value">{{ selectedAccount?.username }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">绑定手机号</span>
                <span class="info-value">{{ phone }}</span>
              </div>
            </div>
            <div class="notice-box info">
              <div class="notice-icon">ℹ️</div>
              <div class="notice-content">
                <p>恢复后，系统将为您生成新的用户名，您可以用新用户名和原密码登录。登录后请在个人资料中修改用户名。</p>
              </div>
            </div>
          </div>

          <!-- 有冲突 -->
          <div v-if="conflictInfo.hasConflict" class="confirm-section">
            <div class="confirm-icon">⚠️</div>
            <h3>手机号冲突提醒</h3>
            <div class="notice-box warning">
              <div class="notice-icon">⚠️</div>
              <div class="notice-content">
                <p><strong>该手机号当前已绑定至其他活跃账号！</strong></p>
                <p>恢复此账号后，以下变更将生效：</p>
                <ul>
                  <li>手机号 <strong>{{ phone }}</strong> 将转移至恢复的账号</li>
                  <li>您当前正常使用的账号（用户名：<strong>{{ conflictInfo.conflictUsername }}</strong>）将失去手机绑定，之后只能使用用户名+密码登录</li>
                  <li>恢复的账号将获得新的随机用户名</li>
                </ul>
              </div>
            </div>
            <div class="confirm-checkbox">
              <label class="checkbox-label">
                <input type="checkbox" v-model="confirmTransfer" />
                <span>我已了解以上变更，确认将手机号转移至恢复的账号</span>
              </label>
            </div>
          </div>

          <div class="btn-group">
            <button class="btn-secondary" @click="currentStep = 2">上一步</button>
            <button
              class="btn-primary"
              :disabled="conflictInfo.hasConflict && !confirmTransfer"
              @click="handleExecuteRestore"
            >
              {{ restoring ? '恢复中...' : '确认恢复账号' }}
            </button>
          </div>
        </div>

        <!-- 恢复成功 -->
        <div v-if="currentStep === 4" class="step-content">
          <div class="success-section">
            <div class="success-icon">🎉</div>
            <h3>账号恢复成功！</h3>
            <div class="success-info">
              <div class="info-row">
                <span class="info-label">新用户名</span>
                <span class="info-value highlight">{{ restoredUser?.username }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">绑定手机号</span>
                <span class="info-value">{{ phone }}</span>
              </div>
            </div>
            <div class="notice-box info">
              <div class="notice-icon">💡</div>
              <div class="notice-content">
                <p>您已自动登录。建议登录后前往「个人资料」修改用户名为您喜欢的名称。</p>
              </div>
            </div>
            <button class="btn-primary" @click="goHome">进入首页</button>
          </div>
        </div>

        <!-- 底部链接 -->
        <div class="restore-footer">
          <router-link to="/login" class="footer-link">返回登录</router-link>
          <router-link to="/help-center" class="footer-link">帮助中心</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../store/auth'
import { queryDeactivatedAccounts, checkPhoneConflict, executeRestore } from '../../api/auth'

const router = useRouter()
const authStore = useAuthStore()

const currentStep = ref(1)
const phone = ref('')
const loading = ref(false)
const restoring = ref(false)

// Step 2 数据
const deactivatedAccounts = ref([])
const selectedAccountId = ref(null)

// Step 3 数据
const conflictInfo = ref({ hasConflict: false, conflictUsername: '', conflictUserId: null })
const confirmTransfer = ref(false)

// Step 4 数据
const restoredUser = ref(null)

const selectedAccount = computed(() => {
  return deactivatedAccounts.value.find(a => a.userId === selectedAccountId.value)
})

const formatDate = (dateStr) => {
  if (!dateStr) return '未知'
  const d = new Date(dateStr)
  return d.toLocaleDateString('zh-CN')
}

// Step 1: 查询已注销账号
const handleQueryAccounts = async () => {
  if (!phone.value || phone.value.trim().length < 11) {
    ElMessage.warning('请输入正确的11位手机号')
    return
  }
  loading.value = true
  try {
    const res = await queryDeactivatedAccounts(phone.value.trim())
    if (res?.code === 200 && res.data?.length > 0) {
      deactivatedAccounts.value = res.data
      selectedAccountId.value = null
      currentStep.value = 2
    } else {
      ElMessage.error(res?.message || '该手机号未关联已注销账号')
    }
  } catch (error) {
    ElMessage.error('查询失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// Step 2: 选择账号
const selectAccount = (account) => {
  selectedAccountId.value = account.userId
}

// 检查手机号冲突
const handleCheckConflict = async () => {
  if (!selectedAccountId.value) {
    ElMessage.warning('请选择要恢复的账号')
    return
  }
  try {
    const res = await checkPhoneConflict(phone.value.trim())
    if (res?.code === 200) {
      conflictInfo.value = res.data || { hasConflict: false }
      confirmTransfer.value = false
      currentStep.value = 3
    } else {
      ElMessage.error(res?.message || '检查失败')
    }
  } catch (error) {
    ElMessage.error('检查失败，请稍后重试')
  }
}

// Step 3: 执行恢复
const handleExecuteRestore = async () => {
  if (conflictInfo.value.hasConflict && !confirmTransfer.value) {
    ElMessage.warning('请先确认手机号转移')
    return
  }
  restoring.value = true
  try {
    const res = await executeRestore({
      userId: selectedAccountId.value,
      confirmPhoneTransfer: conflictInfo.value.hasConflict ? confirmTransfer.value : false
    })
    if (res?.code === 200 && res.data) {
      restoredUser.value = res.data.user
      // 自动登录
      if (res.data.token && res.data.user) {
        authStore.setAuth(res.data.token, res.data.user)
      }
      currentStep.value = 4
      ElMessage.success('账号恢复成功！')
    } else {
      ElMessage.error(res?.message || '恢复失败')
    }
  } catch (error) {
    ElMessage.error('恢复失败，请稍后重试')
  } finally {
    restoring.value = false
  }
}

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.restore-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  padding: 20px;
}

.restore-container {
  width: 100%;
  max-width: 520px;
}

.restore-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  padding: 40px 32px;
}

/* 头部 */
.restore-header {
  text-align: center;
  margin-bottom: 28px;
}

.logo {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-bottom: 12px;
  object-fit: cover;
}

.restore-header h2 {
  font-size: 24px;
  color: #1f2a56;
  margin: 0 0 8px 0;
  font-weight: 700;
}

.restore-header p {
  color: #8c8fa3;
  font-size: 14px;
  margin: 0;
}

/* 步骤指示器 */
.steps-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32px;
  gap: 0;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.step-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e5e7eb;
  color: #9ca3af;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
}

.step.active .step-circle {
  background: #2563eb;
  color: white;
}

.step.done .step-circle {
  background: #10b981;
  color: white;
}

.step span {
  font-size: 12px;
  color: #9ca3af;
  white-space: nowrap;
}

.step.active span {
  color: #2563eb;
  font-weight: 500;
}

.step-line {
  width: 48px;
  height: 2px;
  background: #e5e7eb;
  margin: 0 8px;
  margin-bottom: 20px;
  transition: background 0.3s;
}

.step-line.active {
  background: #2563eb;
}

/* 步骤内容 */
.step-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 表单 */
.form-section {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #1f2a56;
  margin-bottom: 10px;
}

.form-hint {
  font-size: 12px;
  color: #8c8fa3;
  margin: 8px 0 0 0;
}

.input-prefix {
  font-size: 16px;
}

/* 按钮 */
.btn-primary {
  width: 100%;
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);
  color: white;
  transition: all 0.3s;
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-secondary {
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: 1px solid #d1d5db;
  background: white;
  color: #555770;
  transition: all 0.3s;
}

.btn-secondary:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.btn-group {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.btn-group .btn-secondary {
  flex: 0 0 auto;
  min-width: 100px;
}

.btn-group .btn-primary {
  flex: 1;
}

/* 账号列表 */
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2a56;
  margin-bottom: 6px;
}

.section-icon {
  font-size: 20px;
}

.section-desc {
  font-size: 13px;
  color: #8c8fa3;
  margin: 0 0 16px 0;
}

.account-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.account-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.account-card:hover {
  border-color: #93c5fd;
  background: #f0f7ff;
}

.account-card.selected {
  border-color: #2563eb;
  background: #eff6ff;
}

.account-radio {
  flex-shrink: 0;
}

.radio-circle {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #d1d5db;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.radio-circle.checked {
  border-color: #2563eb;
}

.radio-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #2563eb;
}

.account-info {
  flex: 1;
}

.account-name {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.name-label {
  font-size: 12px;
  color: #8c8fa3;
}

.name-value {
  font-size: 15px;
  font-weight: 600;
  color: #1f2a56;
}

.account-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-label {
  font-size: 12px;
  color: #8c8fa3;
}

.meta-value {
  font-size: 13px;
  color: #555770;
}

/* 确认区域 */
.confirm-section {
  text-align: center;
}

.confirm-icon {
  font-size: 40px;
  margin-bottom: 12px;
}

.confirm-section h3 {
  font-size: 18px;
  color: #1f2a56;
  margin: 0 0 20px 0;
}

.confirm-info, .success-info {
  background: #f9fafb;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 16px;
  text-align: left;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.info-row + .info-row {
  border-top: 1px solid #e5e7eb;
}

.info-label {
  font-size: 13px;
  color: #8c8fa3;
}

.info-value {
  font-size: 14px;
  color: #1f2a56;
  font-weight: 500;
}

.info-value.highlight {
  color: #2563eb;
  font-weight: 700;
  font-size: 16px;
}

/* 提示框 */
.notice-box {
  display: flex;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 10px;
  text-align: left;
  margin-bottom: 16px;
}

.notice-box.info {
  background: #eff6ff;
  border: 1px solid #bfdbfe;
}

.notice-box.warning {
  background: #fef3c7;
  border: 1px solid #fcd34d;
}

.notice-icon {
  font-size: 20px;
  flex-shrink: 0;
  margin-top: 2px;
}

.notice-content {
  flex: 1;
}

.notice-content p {
  font-size: 13px;
  color: #555770;
  margin: 0 0 6px 0;
  line-height: 1.6;
}

.notice-content p:last-child {
  margin-bottom: 0;
}

.notice-content strong {
  color: #1f2a56;
}

.notice-content ul {
  margin: 8px 0 0 0;
  padding-left: 18px;
}

.notice-content li {
  font-size: 13px;
  color: #555770;
  margin-bottom: 4px;
  line-height: 1.6;
}

/* 复选框 */
.confirm-checkbox {
  text-align: left;
  margin: 16px 0;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  cursor: pointer;
  font-size: 13px;
  color: #555770;
  line-height: 1.6;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  margin-top: 2px;
  flex-shrink: 0;
  accent-color: #2563eb;
  cursor: pointer;
}

/* 成功区域 */
.success-section {
  text-align: center;
}

.success-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.success-section h3 {
  font-size: 20px;
  color: #1f2a56;
  margin: 0 0 20px 0;
}

.success-section .btn-primary {
  margin-top: 20px;
  max-width: 200px;
}

/* 底部链接 */
.restore-footer {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f0f2f5;
}

.footer-link {
  font-size: 13px;
  color: #8c8fa3;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-link:hover {
  color: #2563eb;
}

/* 响应式 */
@media (max-width: 480px) {
  .restore-card {
    padding: 28px 20px;
  }

  .restore-header h2 {
    font-size: 20px;
  }

  .step-line {
    width: 28px;
  }

  .step span {
    font-size: 11px;
  }

  .account-meta {
    flex-direction: column;
    gap: 4px;
  }

  .btn-group {
    flex-direction: column;
  }

  .btn-group .btn-secondary {
    width: 100%;
  }
}
</style>