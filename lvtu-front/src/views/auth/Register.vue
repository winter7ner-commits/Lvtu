<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>注册</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" maxlength="6" show-word-limit placeholder="3-6个字符，字母/数字/下划线/连字符" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="form.password" maxlength="16" show-password placeholder="6-16位，含数字、小写字母、大写字母" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="form.confirmPassword" maxlength="16" show-password placeholder="请再次输入密码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" maxlength="11" show-word-limit placeholder="请输入11位手机号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading" class="submit-btn">注册</el-button>
        </el-form-item>
        <el-form-item class="auth-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="auth-link">去登录</router-link>
          <span class="footer-divider">·</span>
          <router-link to="/forgot-password" class="auth-link">忘记密码？</router-link>
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
const loading = ref(false)
const formRef = ref(null)
const form = ref({ username: '', password: '', confirmPassword: '', email: '', phone: '' })

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
    return
  }
  if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,16}$/.test(value)) {
    callback(new Error('密码需为6-16位，且包含数字、小写字母和大写字母'))
    return
  }
  if (form.value.confirmPassword) {
    formRef.value?.validateField('confirmPassword')
  }
  callback()
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
    return
  }
  if (value !== form.value.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9_-]{3,6}$/, message: '用户名需为3-6个字符，只能包含字母、数字、下划线和连字符', trigger: 'blur' }
  ],
  password: [{ validator: validatePassword, trigger: ['blur', 'change'] }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: ['blur', 'change'] }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const response = await register({
      username: form.value.username,
      password: form.value.password,
      email: form.value.email,
      phone: form.value.phone
    })
    if (response?.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(response?.message || '注册失败')
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '注册失败，请稍后重试')
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
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
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

.footer-divider {
  color: #c0c4cc;
}

.auth-link:hover {
  text-decoration: underline;
}
</style>
