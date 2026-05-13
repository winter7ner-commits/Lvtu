<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-box">
        <!-- Logo -->
        <div class="login-header">
          <img src="/public/icons/logo.png" alt="LVTU" class="logo" />
          <h1>律途</h1>
          <p>您的法律智能助手</p>
        </div>

        <!-- Login Form -->
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          @submit.prevent="handleLogin"
          class="login-form"
        >
          <!-- Username/Email -->
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名或邮箱"
              prefix-icon="User"
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <!-- Password -->
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              clearable
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <!-- Remember Me & Forgot Password -->
          <div class="login-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <el-link type="primary" href="#" @click.prevent="handleForgotPassword">忘记密码？</el-link>
          </div>

          <!-- Login Button -->
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

          <!-- Sign Up Link -->
          <div class="signup-link">
            没有账户？<el-link type="primary" @click="goToSignup">立即注册</el-link>
          </div>
        </el-form>

        <!-- Demo Credentials
        <div class="demo-info">
          <p>演示账户：</p>
          <p>用户名：demo | 密码：123456</p>
        </div> -->
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../store/auth'
import { login as loginAPI } from '../../api/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loginFormRef = ref(null)
const isLoading = ref(false)
const rememberMe = ref(true)

// Login form data
const loginForm = reactive({
  username: 'demo',
  password: '123456'
})

// Validation rules
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

// Handle login
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    isLoading.value = true

    // Call backend API to validate user credentials
    try {
      const response = await loginAPI(loginForm.username, loginForm.password)
      
      // Check if login was successful
      if (response.data && response.data.data) {
        const userData = response.data.data
        authStore.login(userData)
        ElMessage.success('登录成功！')
        
        // Redirect to previous page or home
        const redirectPath = route.query.redirect || '/'
        setTimeout(() => {
          router.push(redirectPath)
        }, 500)
      } else {
        ElMessage.error('登录失败，请检查用户名和密码')
      }
    } catch (error) {
      console.error('登录错误:', error)
      if (error.response?.status === 401) {
        ElMessage.error('用户名或密码错误')
      } else if (error.response?.data?.message) {
        ElMessage.error(error.response.data.message)
      } else {
        ElMessage.error('登录失败，请检查网络连接')
      }
    }
  } catch (error) {
    ElMessage.error('请检查输入')
  } finally {
    isLoading.value = false
  }
}

// Handle forgot password
const handleForgotPassword = () => {
  ElMessage.info('忘记密码功能开发中...')
}

// Navigate to signup
const goToSignup = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 400px;
}

.login-box {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  padding: 40px 30px;
}

/* Header */
.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin-bottom: 15px;
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

/* Form */
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

/* Login Options */
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

/* Login Button */
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

/* Signup Link */
.signup-link {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 15px;
}

/* Demo Info */
.demo-info {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
  font-size: 12px;
  color: #999;
}

.demo-info p {
  margin: 4px 0;
}

/* Responsive */
@media (max-width: 480px) {
  .login-box {
    padding: 30px 20px;
  }

  .login-header h1 {
    font-size: 24px;
  }
}
</style>
