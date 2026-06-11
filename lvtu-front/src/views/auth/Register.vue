<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>注册</h2>
      <el-form :model="form" :rules="rules" label-position="top" ref="formRef">
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="3-20个字符，只能包含字母、数字、下划线和连字符"
            maxlength="20"
            clearable
          />
          <div class="form-hint">至少3个字符</div>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input 
            type="password" 
            v-model="form.password" 
            placeholder="至少6个字符"
            maxlength="20"
            show-password
            clearable
          />
          <div class="form-hint">6-20个字符</div>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input 
            v-model="form.email" 
            placeholder="请输入有效的邮箱地址"
            clearable
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="form.phone" 
            placeholder="请输入11位手机号码"
            maxlength="11"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading" class="submit-btn">注册</el-button>
        </el-form-item>
        <el-form-item class="auth-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="auth-link">去登录</router-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../../api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = ref({ username: '', password: '', email: '', phone: '' })

// 验证规则：与登录页面保持一致
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名至少3个字符', trigger: 'blur' },
    { max: 20, message: '用户名最多20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: '用户名只能包含字母、数字、下划线和连字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' },
    { max: 20, message: '密码最多20个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true

    const response = await register(form.value)
    if (response?.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(response?.message || '注册失败')
    }
  } catch (error) {
    if (error?.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('注册失败，请检查输入信息')
    }
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

/* 表单提示样式 */
.form-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
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
