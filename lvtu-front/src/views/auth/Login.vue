<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-box">
        <div class="login-header">
          <img src="/icons/logo.png" alt="LVTU" class="logo" />
          <h1>律途</h1>
          <p>您的法律智能助手</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              clearable
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <div class="login-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <el-link type="primary" @click.prevent="handleForgotPassword">忘记密码？</el-link>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              :loading="isLoading"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>

          <div class="signup-link">
            没有账户？<el-link type="primary" @click="goToSignup">立即注册</el-link>
          </div>
          <div class="restore-link">
            账号已注销？<el-link type="primary" @click="goToRestore">恢复已注销账号</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { useAuthStore } from '../../store/auth'
import { login as loginAPI } from '../../api/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loginFormRef = ref(null)
const isLoading = ref(false)
const rememberMe = ref(true)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' },
    { min: 3, message: '用户名至少3个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    isLoading.value = true

    const response = await loginAPI({
      username: loginForm.username,
      password: loginForm.password
    })

    if (response?.code === 200 && response.data?.token && response.data?.user) {
      const { token, user } = response.data

      if (user.userType === 3) {
        ElMessage.warning('管理员请使用管理员专用入口')
        return
      }

      authStore.setAuth(token, user)
      if (rememberMe.value) {
        localStorage.setItem('rememberMe', 'true')
      } else {
        localStorage.removeItem('rememberMe')
      }

      ElMessage.success('登录成功！')
      const redirectPath = route.query.redirect ? String(route.query.redirect) : '/'
      setTimeout(() => {
        router.push(redirectPath)
      }, 300)
    } else {
      ElMessage.error(response?.message || '登录失败，请检查用户名和密码')
    }
  } catch (error) {
    if (error?.response?.status === 401) {
      ElMessage.error('用户名或密码错误')
    } else if (error?.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('登录失败，请稍后重试')
    }
  } finally {
    isLoading.value = false
  }
}

const handleForgotPassword = () => {
  router.push('/forgot-password')
}

const goToSignup = () => {
  router.push('/register')
}

const goToRestore = () => {
  router.push('/restore-account')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 400px;
}

.login-box {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  padding: 40px 30px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin-bottom: 15px;
  object-fit: cover;
}

.login-header h1 {
  font-size: 28px;
  color: #1e40af;
  margin: 0 0 8px 0;
  font-weight: 700;
}

.login-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.login-form {
  margin-top: 30px;
}

.login-form :deep(.el-input) {
  height: 40px;
}

.login-form :deep(.el-input__wrapper) {
  border-color: #d9d9d9;
  background-color: #fafafa;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #40a9ff;
  background-color: white;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #1e40af;
  background-color: white;
  box-shadow: 0 0 0 2px rgba(30, 64, 175, 0.1);
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  margin-bottom: 20px;
}

.login-options :deep(.el-checkbox) {
  margin: 0;
}

.login-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);
  border: none;
}

.login-btn:hover {
  background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%);
}

.signup-link {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 15px;
}

.signup-link :deep(.el-link) {
  vertical-align: baseline;
}

.restore-link {
  text-align: center;
  font-size: 13px;
  color: #8c8fa3;
  margin-top: 10px;
}

.restore-link :deep(.el-link) {
  vertical-align: baseline;
  font-size: 13px;
}

@media (max-width: 480px) {
  .login-box {
    padding: 30px 20px;
  }

  .login-header h1 {
    font-size: 24px;
  }
}
</style>