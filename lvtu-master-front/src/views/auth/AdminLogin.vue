<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../store/auth'
import { adminLogin } from '../../api/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  username: '',
  password: ''
})

const loading = ref(false)
const error = ref('')

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password) {
    error.value = '请填写用户名和密码'
    return
  }

  loading.value = true
  error.value = ''

  try {
    const result = await adminLogin(form.value)
    if (result && result.data && result.data.token && result.data.user) {
      authStore.setAuth(result.data.token, {
        userId: result.data.user.userId,
        username: result.data.user.username,
        userType: result.data.user.userType
      })
      router.push('/articles')
    } else {
      error.value = result?.message || '登录失败'
    }
  } catch (err) {
    error.value = '登录失败，请检查后端服务'
    console.error('Login error:', err)
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>律途 - 管理员登录</h1>
        <p>请输入管理员账号信息</p>
      </div>
      <form class="login-form" @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>用户名</label>
          <input
            v-model="form.username"
            type="text"
            placeholder="请输入用户名"
          />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
          />
        </div>
        <div v-if="error" class="error-message">{{ error }}</div>
        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      <div class="login-footer">
        <span>还没有账号？</span>
        <button class="register-link" @click="goToRegister">立即注册</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f4fc 0%, #d4e8f5 100%);
}

.login-box {
  background: white;
  padding: 2.5rem;
  border-radius: 12px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.login-header h1 {
  color: #1e88e5;
  margin-bottom: 0.5rem;
}

.login-header p {
  color: #666;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 500;
  color: #333;
}

.form-group input {
  padding: 0.8rem;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group input:focus {
  outline: none;
  border-color: #1e88e5;
}

.error-message {
  background: #fef2f2;
  border: 1px solid #fee2e2;
  color: #dc2626;
  padding: 0.8rem;
  border-radius: 4px;
  text-align: center;
}

.login-btn {
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
  border: none;
  padding: 0.9rem;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: box-shadow 0.3s;
}

.login-btn:hover:not(:disabled) {
  box-shadow: 0 2px 10px rgba(30, 136, 229, 0.4);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-footer {
  margin-top: 1.5rem;
  text-align: center;
  display: flex;
  justify-content: center;
  gap: 0.5rem;
}

.login-footer span {
  color: #666;
}

.register-link {
  background: none;
  border: none;
  color: #1e88e5;
  cursor: pointer;
  text-decoration: underline;
}
</style>