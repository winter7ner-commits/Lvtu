<<<<<<< HEAD
<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref('login')

const loginForm = reactive({
  phone: '',
  password: '',
  code: '',
  remember: false
})

const registerForm = reactive({
  phone: '',
  code: '',
  password: '',
  confirmPassword: '',
  agree: false
})

const handleLogin = () => {
  if (!loginForm.phone) {
    alert('请输入手机号')
    return
  }
  if (!loginForm.password) {
    alert('请输入密码')
    return
  }
  localStorage.setItem('token', 'demo-token')
  router.push('/')
}

const handleRegister = () => {
  if (!registerForm.phone) {
    alert('请输入手机号')
    return
  }
  if (!registerForm.code) {
    alert('请输入验证码')
    return
  }
  if (!registerForm.password) {
    alert('请输入密码')
    return
  }
  if (registerForm.password !== registerForm.confirmPassword) {
    alert('两次输入的密码不一致')
    return
  }
  if (!registerForm.agree) {
    alert('请同意用户协议和隐私政策')
    return
  }
  alert('注册成功，请登录')
  activeTab.value = 'login'
}

const sendCode = () => {
  if (!loginForm.phone) {
    alert('请输入手机号')
    return
  }
  alert('验证码已发送')
}

const sendRegisterCode = () => {
  if (!registerForm.phone) {
    alert('请输入手机号')
    return
  }
  alert('验证码已发送')
}
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <div class="logo-section">
        <span class="logo-text">律途</span>
      </div>
      
      <div class="tabs">
        <button :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">登录</button>
        <button :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">注册</button>
      </div>

      <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="form">
        <div class="form-item">
          <input 
            v-model="loginForm.phone" 
            type="text" 
            placeholder="请输入手机号" 
            class="form-input"
          />
        </div>
        <div class="form-item">
          <input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            class="form-input"
          />
        </div>
        <div class="form-item">
          <input 
            v-model="loginForm.code" 
            type="text" 
            placeholder="请输入验证码" 
            class="form-input code-input"
          />
          <button type="button" class="code-btn" @click="sendCode">获取验证码</button>
        </div>
        <div class="form-item remember">
          <label>
            <input type="checkbox" v-model="loginForm.remember" />
            <span>已阅读并同意《用户协议》和《隐私政策》</span>
          </label>
        </div>
        <button type="submit" class="submit-btn">登录</button>
        <a href="#" class="forgot-link">忘记密码？</a>
      </form>

      <form v-else @submit.prevent="handleRegister" class="form">
        <div class="form-item">
          <input 
            v-model="registerForm.phone" 
            type="text" 
            placeholder="请输入手机号" 
            class="form-input"
          />
        </div>
        <div class="form-item">
          <input 
            v-model="registerForm.code" 
            type="text" 
            placeholder="请输入验证码" 
            class="form-input code-input"
          />
          <button type="button" class="code-btn" @click="sendRegisterCode">获取验证码</button>
        </div>
        <div class="form-item">
          <input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码" 
            class="form-input"
          />
        </div>
        <div class="form-item">
          <input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请确认密码" 
            class="form-input"
          />
        </div>
        <div class="form-item remember">
          <label>
            <input type="checkbox" v-model="registerForm.agree" />
            <span>已阅读并同意《用户协议》和《隐私政策》</span>
          </label>
        </div>
        <button type="submit" class="submit-btn">注册</button>
      </form>
=======
﻿<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>登录</h2>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">登录</el-button>
          <router-link to="/forgot-password" class="auth-link">忘记密码？</router-link>
        </el-form-item>
        <el-form-item class="auth-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="auth-link">立即注册</router-link>
        </el-form-item>
      </el-form>
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
    </div>
  </div>
</template>

<<<<<<< HEAD
<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f4fc 0%, #d4e8f5 100%);
  padding: 2rem;
}

.login-box {
  background: white;
  border-radius: 12px;
  padding: 2.5rem;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 4px 20px rgba(79, 195, 247, 0.2);
}

.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

=======
<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../store/auth'
import { login } from '../../api/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loading = ref(false)
const form = ref({ username: '', password: '' })

const redirectPath = route.query.redirect ? String(route.query.redirect) : '/'

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password) {
    window.alert('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const response = await login(form.value)
    if (response?.code === 200) {
      const data = response.data
      const user = data.user

      if(user.userType === 3) {
        window.alert('管理员请使用管理员专用入口')
        return
      }

      authStore.setAuth(data.token, data.user)
      router.push(redirectPath)
    } else {
      window.alert(response?.message || '登录失败')
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
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
