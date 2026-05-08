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

