<template>
  <div class="auth-page reset-page">
    <section class="reset-card">
      <div class="page-title">
        <span class="title-tag">账号找回</span>
        <h2>重置登录密码</h2>
        <p>
          为避免他人仅凭用户名重置密码，请填写注册时绑定的手机号与邮箱；核验通过后再启用新密码。
        </p>
      </div>

      <el-steps :active="1" simple class="reset-steps">
        <el-step title="核验账号" />
        <el-step title="设置新密码" />
        <el-step title="重新登录" />
      </el-steps>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="reset-form"
        @submit.prevent="handleSubmit"
      >
        <div class="form-section">
          <h3>账号信息</h3>
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model.trim="form.username"
              placeholder="请输入注册用户名"
              autocomplete="username"
              clearable
            />
          </el-form-item>

          <el-form-item label="绑定手机号" prop="phone">
            <el-input
              v-model.trim="form.phone"
              maxlength="11"
              placeholder="请输入注册时绑定的 11 位手机号"
              autocomplete="tel"
              clearable
            />
          </el-form-item>

          <el-form-item label="绑定邮箱" prop="email">
            <el-input
              v-model.trim="form.email"
              placeholder="请输入注册时绑定的邮箱"
              autocomplete="email"
              clearable
            />
          </el-form-item>
        </div>

        <div class="form-section">
          <h3>新密码</h3>
          <el-form-item label="新密码" prop="password">
            <el-input
              v-model="form.password"
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
        </div>

        <el-alert
          class="reset-alert"
          type="warning"
          :closable="false"
          show-icon
          title="无法完成资料核验？"
          description="如果注册邮箱不可用，请联系平台客服进行人工身份核验。"
        />

        <div class="form-actions">
          <el-button type="primary" native-type="submit" :loading="loading">
            重置密码
          </el-button>
          <el-button :disabled="loading" @click="goLogin">
            返回登录
          </el-button>
        </div>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { forgotPassword } from '../../api/auth'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,16}$/

const form = reactive({
  username: typeof route.query.account === 'string' ? route.query.account : '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const passwordScore = computed(() => {
  const value = form.password || ''
  let score = 0
  if (value.length >= 6) score += 1
  if (/[a-z]/.test(value) && /[A-Z]/.test(value)) score += 1
  if (/\d/.test(value)) score += 1
  return score
})

const passwordStrengthText = computed(() => {
  if (!form.password) return '未输入'
  if (!passwordPattern.test(form.password)) return '需包含大小写字母和数字'
  return passwordScore.value >= 3 ? '强度合格' : '继续完善'
})

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入新密码'))
    return
  }
  if (!passwordPattern.test(value)) {
    callback(new Error('密码需为 6-16 位，且包含数字、小写字母和大写字母'))
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
  if (value !== form.password) {
    callback(new Error('两次输入的新密码不一致'))
    return
  }
  callback()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9_-]{3,6}$/, message: '用户名需为 3-6 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入绑定手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入绑定邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [{ validator: validatePassword, trigger: ['blur', 'change'] }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: ['blur', 'change'] }]
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    await ElMessageBox.confirm('确认提交密码重置？系统将校验账号绑定资料。', '重置确认', {
      confirmButtonText: '确认重置',
      cancelButtonText: '返回检查',
      type: 'warning'
    })
  } catch {
    return
  }

  loading.value = true
  try {
    const response = await forgotPassword({
      username: form.username,
      phone: form.phone,
      email: form.email,
      password: form.password
    })

    if (response?.code === 200) {
      ElMessage.success('密码已重置，请使用新密码登录')
      router.push({ path: '/login', query: { account: form.username } })
    } else {
      ElMessage.error(response?.message || '重置失败，请核对账号资料')
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '重置失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.reset-page {
  min-height: calc(100vh - 70px);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 40px 16px 64px;
  letter-spacing: 0;
}

.reset-card {
  width: min(100%, 760px);
  background: #ffffff;
  border: 1px solid #e6ebf2;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.page-title {
  text-align: left;
  margin-bottom: 22px;
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
  max-width: 64ch;
  color: #5d6678;
  font-size: 14px;
  line-height: 1.7;
}

.reset-steps {
  margin-bottom: 28px;
  border-radius: 10px;
}

.reset-form {
  text-align: left;
}

.form-section {
  margin-bottom: 24px;
}

.form-section h3 {
  margin: 0 0 14px;
  color: #1f2a56;
  font-size: 16px;
  font-weight: 700;
}

.reset-form :deep(.el-form-item__label) {
  color: #344054;
  font-weight: 600;
}

.reset-form :deep(.el-input__wrapper) {
  min-height: 44px;
  border-radius: 8px;
  box-shadow: 0 0 0 1px #d8e0ea inset;
  background: #ffffff;
  transition: box-shadow 0.18s ease, background-color 0.18s ease;
}

.reset-form :deep(.el-input__wrapper:hover),
.reset-form :deep(.el-input__wrapper.is-focus) {
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

.reset-alert {
  margin: 4px 0 24px;
}

.form-actions {
  display: flex;
  gap: 12px;
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
  .reset-page {
    padding: 24px 12px 48px;
  }

  .reset-card {
    padding: 20px;
  }

  .page-title h2 {
    font-size: 22px;
  }

  .reset-steps {
    display: none;
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
