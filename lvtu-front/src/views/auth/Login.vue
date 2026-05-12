<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref('login')

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  agree: false
})

const handleLogin = async () => {
  if (!loginForm.username) {
    alert('请输入用户名')
    return
  }
  if (!loginForm.password) {
    alert('请输入密码')
    return
  }
  
  try {
    const response = await fetch('http://localhost:8081/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: loginForm.username,
        password: loginForm.password
      })
    })
    
    const data = await response.json()
    
    if (data?.code === 200) {
      localStorage.setItem('authToken', data.data.token)
      localStorage.setItem('currentUser', JSON.stringify(data.data.user))
      localStorage.setItem('isLoggedIn', 'true')
      
      if (data.data.user.userType === 3) {
        window.location.href = 'http://localhost:5180'
      } else {
        router.push('/')
      }
    } else {
      alert(data?.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    alert('登录失败，请检查后端服务是否启动')
  }
}

const handleRegister = async () => {
  if (!registerForm.username) {
    alert('请输入用户名')
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
  
  try {
    const response = await fetch('http://localhost:8081/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: registerForm.username,
        password: registerForm.password
      })
    })
    
    const data = await response.json()
    
    if (data?.code === 200) {
      alert('注册成功，请登录')
      activeTab.value = 'login'
    } else {
      alert(data?.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    alert('注册失败，请检查后端服务是否启动')
  }
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
            v-model="loginForm.username" 
            type="text" 
            placeholder="请输入用户名" 
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
        <div class="form-item remember">
          <label>
            <input type="checkbox" v-model="loginForm.remember" />
            <span>记住我</span>
          </label>
        </div>
        <button type="submit" class="submit-btn">登录</button>
        <div class="links">
          <a href="#" class="forgot-link">忘记密码？</a>
        </div>
      </form>

      <form v-else @submit.prevent="handleRegister" class="form">
        <div class="form-item">
          <input 
            v-model="registerForm.username" 
            type="text" 
            placeholder="请输入用户名" 
            class="form-input"
          />
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

.logo-text {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1e88e5;
}

.tabs {
  display: flex;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
}

.tabs button {
  flex: 1;
  padding: 0.75rem;
  background: none;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  color: #666;
  position: relative;
}

.tabs button.active {
  color: #1e88e5;
  font-weight: 500;
}

.tabs button.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 00;
  height: 2px;
  background: #1e88e5;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-item {
  display: flex;
  flex-direction: column;
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

.form-item.remember {
  flex-direction: row;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #666;
}

.form-item.remember input[type="checkbox"] {
  width: 1rem;
  height: 1rem;
}

.submit-btn {
  background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
  color: white;
  border: none;
  padding: 0.875rem;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: box-shadow 0.3s;
}

.submit-btn:hover {
  box-shadow: 0 2px 10px rgba(30, 136, 229, 0.4);
}

.links {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.875rem;
}

.forgot-link {
  color: #1e88e5;
  text-decoration: none;
}

.forgot-link:hover {
  text-decoration: underline;
}
</style>