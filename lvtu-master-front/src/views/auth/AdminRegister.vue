<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>管理员注册用户</h2>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="form.userType" placeholder="请选择用户类型">
            <el-option label="普通用户" :value="1" />
            <el-option label="律师" :value="2" />
            <el-option label="管理员" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">注册</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../store/auth'
import { adminRegister } from '../../api/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const form = ref({
  username: '',
  password: '',
  email: '',
  phone: '',
  userType: 1
})

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password || !form.value.userType) {
    window.alert('请填写必需信息')
    return
  }
  loading.value = true
  try {
    const response = await adminRegister(form.value)
    if (response?.code === 200) {
      window.alert('用户注册成功')
      router.push('/admin') // 假设有管理员面板
    } else {
      window.alert(response?.message || '注册失败')
    }
  } catch (error) {
    window.alert('注册失败，请稍后重试')
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
</style>