<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>管理员登录</h2>
      <form @submit.prevent="handleSubmit" class="login-form">
        <div class="form-item">
          <label>管理员用户名</label>
          <input v-model="form.username" type="text" placeholder="请输入管理员用户名" class="form-input" />
        </div>
        <div class="form-item">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码" class="form-input" />
        </div>
        <button type="submit" class="submit-btn" :disabled="loading">
          <span v-if="loading">登录中...</span>
          <span v-else>登录</span>
        </button>
        <div class="auth-footer">
          <span>普通用户？</span>
          <router-link to="/login" class="auth-link">用户登录</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const handleSubmit = async () => {
  if (!form.username || !form.password) {
    window.alert('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const response = await fetch('http://localhost:8081/api/auth/admin/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(form)
    })
    const data = await response.json()
    if (data?.code === 200) {
      localStorage.setItem('authToken', data.data.token)
      localStorage.setItem('currentUser', JSON.stringify(data.data.user))
      localStorage.setItem('isLoggedIn', 'true')
      router.push('/admin-articles')
    } else {
      window.alert(data?.message || '管理员登录失败')
    }
  } catch (error) {
    window.alert('登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: calc(100vh - 70px);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 16px;
  background: linear-gradient(135deg, #e8f4fc 0%, #d4e8f5 100%);
}

.auth-card {
  width: 420px;
  padding: 32px;
  border-radius: 12px;
  background-color: #ffffff;
  box-shadow: 0 4px 20px rgba(79, 195, 247, 0.2);
}

.auth-card h2 {
  margin-bottom: 24px;
  font-size: 24px;
  color: #1e88e5;
  text-align: center;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-item {
  display: flex;
  flex-direction: column;
}

.form-item label {
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.form-input {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.3s;
}

.form-input:focus {
  border-color: #1e88e5;
}

.submit-btn {
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
  border: none;
  padding: 0.875rem;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-btn:hover:not(:disabled) {
  box-shadow: 0 2px 10px rgba(30, 136, 229, 0.4);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.auth-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 0.5rem;
}

.auth-link {
  color: #ff7043;
  text-decoration: none;
}

.auth-link:hover {
  text-decoration: underline;
}
</style>