<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>管理员登录</h2>
      <el-form :model="form" label-position="top">
        <el-form-item label="管理员用户名">
          <el-input v-model="form.username" placeholder="请输入管理员用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">登录</el-button>
        </el-form-item>
        <el-form-item class="auth-footer">
          <span>普通用户？</span>
          <router-link to="/login" class="auth-link">用户登录</router-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
<<<<<<< HEAD

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const form = ref({ username: '', password: '' })

const redirectPath = route.query.redirect ? String(route.query.redirect) : '/admin-articles'
=======
import { useAuthStore } from '../../store/auth'
import { adminLogin } from '../../api/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loading = ref(false)
const form = ref({ username: '', password: '' })

const redirectPath = route.query.redirect ? String(route.query.redirect) : '/admin' // 假设管理员面板在 /admin
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password) {
    window.alert('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
<<<<<<< HEAD
    const response = await fetch('http://localhost:8081/api/auth/admin/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(form.value)
    })
    const data = await response.json()
    if (data?.code === 200) {
      localStorage.setItem('authToken', data.data.token)
      localStorage.setItem('currentUser', JSON.stringify(data.data.user))
      localStorage.setItem('isLoggedIn', 'true')
      router.push(redirectPath)
    } else {
      window.alert(data?.message || '管理员登录失败')
=======
    const response = await adminLogin(form.value)
    if (response?.code === 200) {
      const data = response.data
      authStore.setAuth(data.token, data.user)
      router.push(redirectPath)
    } else {
      window.alert(response?.message || '管理员登录失败')
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
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
  background: #f5f7fb;
}
.auth-card {
  width: 420px;
  padding: 32px;
  border-radius: 20px;
  background-color: #ffffff;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.08);
}
.auth-card h2 {
  margin-bottom: 24px;
  font-size: 28px;
  color: #1f2a56;
}
.auth-link {
  margin-left: 16px;
  color: #409eff;
}
.auth-footer {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 8px;
}
</style>