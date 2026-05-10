<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>忘记密码</h2>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input type="password" v-model="form.password" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">重置密码</el-button>
        </el-form-item>
        <el-form-item class="auth-footer">
          <span>记得密码了？</span>
          <router-link to="/login" class="auth-link">去登录</router-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { forgotPassword } from '../../api/auth'

const router = useRouter()
const loading = ref(false)
const form = ref({ username: '', password: '' })

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password) {
    window.alert('请输入用户名和新密码')
    return
  }
  loading.value = true
  try {
    const response = await forgotPassword(form.value)
    if (response?.code === 200) {
      window.alert('密码已重置，请使用新密码登录')
      router.push('/login')
    } else {
      window.alert(response?.message || '重置失败')
    }
  } catch (error) {
    window.alert('重置失败，请稍后重试')
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
