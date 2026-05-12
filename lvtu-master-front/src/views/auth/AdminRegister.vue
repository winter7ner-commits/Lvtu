<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminRegister } from '../../api/auth'

const router = useRouter()

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
})

const loading = ref(false)
const error = ref('')

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password) {
    error.value = '请填写用户名和密码'
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  error.value = ''

  try {
    const result = await adminRegister({
      username: form.value.username,
      password: form.value.password,
      email: form.value.email,
      phone: form.value.phone,
      userType: 3
    })
    if (result.code === 200) {
      router.push('/login')
    } else {
      error.value = result.message || '注册失败'
    }
  } catch (err) {
    error.value = '注册失败，请检查后端服务'
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h1>律途 - 管理员注册</h1>
        <p>创建管理员账号</p>
      </div>
      <form class="register-form" @submit.prevent="handleSubmit">
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
        <div class="form-group">
          <label>确认密码</label>
          <input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
          />
        </div>
        <div class="form-group">
          <label>邮箱（选填）</label>
          <input
            v-model="form.email"
            type="email"
            placeholder="请输入邮箱"
          />
        </div>
        <div class="form-group">
          <label>手机号（选填）</label>
          <input
            v-model="form.phone"
            type="tel"
            placeholder="请输入手机号"
          />
        </div>
        <div v-if="error" class="error-message">{{ error }}</div>
        <button type="submit" class="register-btn" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      <div class="register-footer">
        <span>已有账号？</span>
        <button class="login-link" @click="goToLogin">立即登录</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f4fc 0%, #d4e8f5 100%);
}

.register-box {
  background: white;
  padding: 2.5rem;
  border-radius: 12px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 450px;
}

.register-header {
  text-align: center;
  margin-bottom: 2rem;
}

.register-header h1 {
  color: #1e88e5;
  margin-bottom: 0.5rem;
}

.register-header p {
  color: #666;
}

.register-form {
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

.register-btn {
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
  border: none;
  padding: 0.9rem;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: box-shadow 0.3s;
}

.register-btn:hover:not(:disabled) {
  box-shadow: 0 2px 10px rgba(30, 136, 229, 0.4);
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.register-footer {
  margin-top: 1.5rem;
  text-align: center;
  display: flex;
  justify-content: center;
  gap: 0.5rem;
}

.register-footer span {
  color: #666;
}

.login-link {
  background: none;
  border: none;
  color: #1e88e5;
  cursor: pointer;
  text-decoration: underline;
}
</style>