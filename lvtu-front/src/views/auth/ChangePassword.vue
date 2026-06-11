<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>修改密码</h2>
      <el-form :model="form" label-position="top">
        <el-form-item label="旧密码">
          <el-input type="password" v-model="form.oldPassword" placeholder="请输入旧密码" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input type="password" v-model="form.newPassword" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input type="password" v-model="form.confirmPassword" placeholder="请再次输入新密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading" class="submit-btn">确认修改</el-button>
        </el-form-item>
        <el-form-item class="auth-footer">
          <span>不想修改了？</span>
          <button @click="goBack" class="auth-link">返回</button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../store/auth'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const form = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

// 获取来源页面路径
const fromPath = ref('/user-profile')

// 监听路由变化，更新来源页面
watch(() => router.currentRoute.value.query.from, (newVal) => {
  if (newVal) {
    fromPath.value = newVal
  }
}, { immediate: true })

// 返回来源页面
const goBack = () => {
  // 直接跳转到来源页面，而不是使用back
  if (fromPath.value === '/settings') {
    router.push('/settings')
  } else if (fromPath.value === '/security-settings') {
    router.push('/security-settings')
  } else {
    router.push('/user-profile')
  }
}

const handleSubmit = async () => {
  if (!form.value.oldPassword || !form.value.newPassword || !form.value.confirmPassword) {
    return ElMessage.warning('请填写完整信息')
  }
  if (form.value.newPassword !== form.value.confirmPassword) {
    return ElMessage.warning('两次输入的新密码不一致')
  }
  if (form.value.oldPassword === form.value.newPassword) {
    return ElMessage.warning('新密码不能与旧密码一致')
  }
  
  loading.value = true
  try {
    const response = await request.post('/api/auth/change-password', {
      username: authStore.user?.username,
      oldPassword: form.value.oldPassword,
      newPassword: form.value.newPassword
    })
    if (response?.data?.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      authStore.logout()
      router.push('/login')
    } else {
      ElMessage.error(response?.data?.message || '修改失败')
    }
  } catch (error) {
    ElMessage.error('修改失败，请稍后重试')
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
  background: none;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  padding: 0;
  text-decoration: none;
  margin-left: 5px;
}

.auth-link:hover {
  text-decoration: underline;
}
</style>
