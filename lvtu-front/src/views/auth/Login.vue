<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>登录</h2>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" placeholder="请输入密码" show-password />
        </el-form-item>
        <!-- 将忘记密码独立右对齐 -->
        <el-form-item class="forgot-password-item">
          <router-link to="/forgot-password" class="auth-link">忘记密码？</router-link>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading" class="submit-btn">登录</el-button>
        </el-form-item>
        <el-form-item class="auth-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="auth-link">立即注册</router-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../store/auth'
import { login } from '../../api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loading = ref(false)
const form = ref({ username: '', password: '' })
const redirectPath = route.query.redirect ? String(route.query.redirect) : '/'

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password) {
    return ElMessage.warning('请输入用户名和密码')
  }
  loading.value = true
  try {
    const response = await login(form.value)
    if (response?.code === 200) {
      const data = response.data
      authStore.setAuth(data.token, data.user)
      ElMessage.success('登录成功')
      router.push(redirectPath)
    } else {
      ElMessage.error(response?.message || '登录失败')
    }
  } catch (error) {
    ElMessage.error('登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 页面容器：垂直水平居中 */
.auth-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 85vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
}

/* 卡片样式 */
.auth-card {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
}

.auth-card h2 {
  text-align: center;
  color: #303133;
  margin-bottom: 30px;
  font-size: 24px;
}

/* 统一输入框样式 */
.auth-card :deep(.el-input__wrapper) {
  background-color: #f5f7fa;
  border-radius: 8px;
  box-shadow: none;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
}

.auth-card :deep(.el-input__wrapper:hover),
.auth-card :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  background-color: #ffffff;
}

/* 忘记密码右对齐 */
.forgot-password-item {
  margin-bottom: 10px;
  margin-top: -10px;
  text-align: right;
}

/* 按钮全宽及圆角优化 */
.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
  letter-spacing: 2px;
}

/* 底部链接样式 */
.auth-footer {
  text-align: center;
  margin-top: 10px;
  color: #909399;
  font-size: 14px;
}

.auth-link {
  color: #409eff;
  text-decoration: none;
  font-weight: 500;
}

.auth-link:hover {
  text-decoration: underline;
}
</style>
