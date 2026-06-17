<template>
  <div class="auth-page security-page">
    <section class="security-card">
      <button type="button" class="back-button" @click="handleBack">
        <span aria-hidden="true">‹</span>
        返回
      </button>

      <div class="page-title">
        <span class="title-tag">账号安全</span>
        <h2>修改登录密码</h2>
        <p>
          当前无法接入短信验证码时，将通过已登录会话与原密码共同确认本人操作；修改成功后需要重新登录。
        </p>
      </div>

      <div class="account-strip">
        <div>
          <span>当前账户</span>
          <strong>{{ accountName }}</strong>
        </div>
        <div v-if="maskedPhone">
          <span>绑定手机</span>
          <strong>{{ maskedPhone }}</strong>
        </div>
        <div v-if="maskedEmail">
          <span>绑定邮箱</span>
          <strong>{{ maskedEmail }}</strong>
        </div>
      </div>

      <el-alert
        class="security-alert"
        type="info"
        :closable="false"
        show-icon
        title="安全验证说明"
        description="修改密码不依赖短信验证码。请确认浏览器处于本人登录状态，并输入原密码；若忘记原密码，请退出后使用找回密码流程。"
      />

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="security-form"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="form.oldPassword"
            type="password"
            placeholder="请输入当前登录密码"
            autocomplete="current-password"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            maxlength="16"
            placeholder="6-16 位，包含大小写字母和数字"
            autocomplete="new-password"
            show-password
            clearable
          />
          <div class="password-meter" aria-hidden="true">
            <span :class="{ active: passwordScore >= 1 }"></span>
            <span :class="{ active: passwordScore >= 2 }"></span>
            <span :class="{ active: passwordScore >= 3 }"></span>
            <em>{{ passwordStrengthText }}</em>
          </div>
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            maxlength="16"
            placeholder="请再次输入新密码"
            autocomplete="new-password"
            show-password
            clearable
          />
        </el-form-item>

        <div class="form-actions">
          <el-button type="primary" native-type="submit" :loading="loading">
            确认修改
          </el-button>
          <el-button type="default" :disabled="loading" @click="handleBack">
            取消
          </el-button>
        </div>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../../store/auth'
import { changePassword } from '../../api/auth'
import { goBack } from '@/utils/navigation'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,16}$/

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const accountName = computed(() => authStore.user?.username || '当前账户')
const maskedPhone = computed(() => maskPhone(authStore.user?.phone))
const maskedEmail = computed(() => maskEmail(authStore.user?.email))

const passwordScore = computed(() => {
  const value = form.newPassword || ''
  let score = 0
  if (value.length >= 6) score += 1
  if (/[a-z]/.test(value) && /[A-Z]/.test(value)) score += 1
  if (/\d/.test(value)) score += 1
  return score
})

const passwordStrengthText = computed(() => {
  if (!form.newPassword) return '未输入'
  if (!passwordPattern.test(form.newPassword)) return '需包含大小写字母和数字'
  return passwordScore.value >= 3 ? '强度合格' : '继续完善'
})

const validateNewPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入新密码'))
    return
  }
  if (!passwordPattern.test(value)) {
    callback(new Error('密码需为 6-16 位，且包含数字、小写字母和大写字母'))
    return
  }
  if (value === form.oldPassword) {
    callback(new Error('新密码不能与原密码一致'))
    return
  }
  if (form.confirmPassword) {
    formRef.value?.validateField('confirmPassword')
  }
  callback()
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入新密码'))
    return
  }
  if (value !== form.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
    return
  }
  callback()
}

const rules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, message: '原密码至少 6 位', trigger: 'blur' }
  ],
  newPassword: [{ validator: validateNewPassword, trigger: ['blur', 'change'] }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: ['blur', 'change'] }]
}

const handleSubmit = async () => {
  if (!authStore.user?.username) {
    ElMessage.warning('登录状态已失效，请重新登录')
    authStore.logout()
    router.push('/login')
    return
  }

  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    await ElMessageBox.confirm('确认修改密码？修改成功后将退出当前登录状态。', '安全确认', {
      confirmButtonText: '确认修改',
      cancelButtonText: '返回检查',
      type: 'warning'
    })
  } catch {
    return
  }

  loading.value = true
  try {
    const response = await changePassword({
      username: authStore.user.username,
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })

    if (response?.code === 200) {
      ElMessage.success('密码修改成功，请使用新密码重新登录')
      authStore.logout()
      router.push('/login')
    } else {
      ElMessage.error(response?.message || '修改失败，请检查原密码')
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '修改失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleBack = () => {
  goBack(router, '/settings')
}

function maskPhone(phone) {
  if (!phone) return ''
  const text = String(phone)
  if (text.length < 7) return text
  return `${text.slice(0, 3)}****${text.slice(-4)}`
}

function maskEmail(email) {
  if (!email) return ''
  const [name, domain] = String(email).split('@')
  if (!name || !domain) return email
  const visible = name.length <= 2 ? name[0] : `${name.slice(0, 2)}***`
  return `${visible}@${domain}`
}
</script>

<style scoped>
.security-page {
  min-height: calc(100vh - 70px);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 40px 16px 64px;
  letter-spacing: 0;
}

.security-card {
  width: min(100%, 720px);
  background: #ffffff;
  border: 1px solid #e6ebf2;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.back-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 34px;
  padding: 0 12px;
  border: 1px solid #d8e0ea;
  border-radius: 8px;
  background: #ffffff;
  color: #344054;
  cursor: pointer;
  transition: border-color 0.18s ease, color 0.18s ease, background-color 0.18s ease;
}

.back-button span {
  font-size: 22px;
  line-height: 1;
}

.back-button:hover {
  border-color: #1e40af;
  color: #1e40af;
  background: #f5f8ff;
}

.page-title {
  margin: 24px 0;
  text-align: left;
}

.title-tag {
  display: inline-flex;
  margin-bottom: 10px;
  color: #1e40af;
  font-size: 13px;
  font-weight: 600;
}

.page-title h2 {
  margin: 0 0 10px;
  color: #101828;
  font-size: 26px;
  line-height: 1.25;
}

.page-title p {
  max-width: 62ch;
  color: #5d6678;
  font-size: 14px;
  line-height: 1.7;
}

.account-strip {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
  padding: 16px;
  margin-bottom: 18px;
  border: 1px solid #e6ebf2;
  border-radius: 12px;
  background: #f8fafc;
}

.account-strip div {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.account-strip span {
  color: #667085;
  font-size: 12px;
}

.account-strip strong {
  color: #1f2a56;
  font-size: 14px;
  font-weight: 600;
  overflow-wrap: anywhere;
}

.security-alert {
  margin-bottom: 22px;
  text-align: left;
}

.security-form {
  text-align: left;
}

.security-form :deep(.el-form-item__label) {
  color: #344054;
  font-weight: 600;
}

.security-form :deep(.el-input__wrapper) {
  min-height: 44px;
  border-radius: 8px;
  box-shadow: 0 0 0 1px #d8e0ea inset;
  background: #ffffff;
  transition: box-shadow 0.18s ease, background-color 0.18s ease;
}

.security-form :deep(.el-input__wrapper:hover),
.security-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1e40af inset, 0 0 0 3px rgba(30, 64, 175, 0.12);
}

.password-meter {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(3, 1fr) auto;
  align-items: center;
  gap: 6px;
  margin-top: 10px;
}

.password-meter span {
  height: 4px;
  border-radius: 999px;
  background: #e6ebf2;
}

.password-meter span.active {
  background: #1e40af;
}

.password-meter em {
  color: #667085;
  font-size: 12px;
  font-style: normal;
  white-space: nowrap;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 28px;
}

.form-actions :deep(.el-button) {
  min-width: 120px;
  min-height: 42px;
  border-radius: 8px;
  font-weight: 600;
}

.form-actions :deep(.el-button--primary) {
  background: #1e40af;
  border-color: #1e40af;
}

@media (max-width: 640px) {
  .security-page {
    padding: 24px 12px 48px;
  }

  .security-card {
    padding: 20px;
  }

  .page-title h2 {
    font-size: 22px;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions :deep(.el-button) {
    width: 100%;
    margin-left: 0;
  }
}
</style>
