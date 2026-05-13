<template>
  <div class="profile-page">
    <div class="profile-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h2>个人资料</h2>
        <p>管理您的个人资料和账户设置</p>
      </div>

      <!-- 头像区域 -->
      <div class="avatar-section">
        <div class="avatar-wrapper">
          <img :src="form.avatarUrl || defaultAvatar" alt="用户头像" class="avatar-img" />
          <div class="avatar-upload-btn" @click="triggerAvatarUpload">
            <span>更换头像</span>
            <input ref="avatarInput" type="file" accept="image/*" @change="handleAvatarChange" hidden />
          </div>
        </div>
        <div class="user-brief">
          <h3>{{ form.username }}</h3>
          <span class="user-type-badge" :class="userTypeClass">{{ userTypeLabel }}</span>
        </div>
      </div>

      <!-- 信息编辑区域 -->
      <div class="info-section">
        <div class="section-title">
          <h3>基本资料</h3>
          <button v-if="!isEditing" class="edit-btn" @click="startEdit">
            ✏️ 编辑
          </button>
          <div v-else class="edit-actions">
            <button class="cancel-btn" @click="cancelEdit">取消</button>
            <button class="save-btn" @click="handleSave" :disabled="saving">
              {{ saving ? '保存中...' : '保存' }}
            </button>
          </div>
        </div>

        <div class="info-form">
          <div class="form-row">
            <div class="form-group">
              <label>用户名</label>
              <input type="text" v-model="form.username" disabled class="form-input disabled" />
            </div>
            <div class="form-group">
              <label>用户类型</label>
              <input type="text" :value="userTypeLabel" disabled class="form-input disabled" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>手机号</label>
              <input
                type="tel"
                v-model="form.phone"
                :disabled="!isEditing"
                class="form-input"
                :class="{ disabled: !isEditing }"
                placeholder="请输入手机号"
              />
            </div>
            <div class="form-group">
              <label>邮箱</label>
              <input
                type="email"
                v-model="form.email"
                :disabled="!isEditing"
                class="form-input"
                :class="{ disabled: !isEditing }"
                placeholder="请输入邮箱"
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>所在地区</label>
              <input
                type="text"
                v-model="form.region"
                :disabled="!isEditing"
                class="form-input"
                :class="{ disabled: !isEditing }"
                placeholder="请输入所在地区"
              />
            </div>
            <div class="form-group">
              <label>实名认证</label>
              <div class="verify-status">
                <span v-if="form.isVerified" class="verified">✅ 已认证</span>
                <span v-else class="unverified">❌ 未认证</span>
                <button v-if="!form.isVerified && isEditing" class="verify-btn" @click="goVerify">去认证</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 账户信息 -->
      <div class="info-section">
        <div class="section-title">
          <h3>账户信息</h3>
        </div>
        <div class="account-info">
          <div class="account-item">
            <span class="account-label">注册时间</span>
            <span class="account-value">{{ formatDateTime(form.createdTime) }}</span>
          </div>
          <div class="account-item">
            <span class="account-label">账户状态</span>
            <span class="account-value status-active">正常</span>
          </div>
          <div class="account-item">
            <span class="account-label">律师认证</span>
            <span class="account-value" :class="authStatusClass">{{ authStatusLabel }}</span>
          </div>
        </div>
      </div>

      <!-- 安全设置 -->
      <div class="info-section">
        <div class="section-title">
          <h3>安全设置</h3>
        </div>
        <div class="security-actions">
          <button class="security-btn" @click="goChangePassword">修改密码</button>
          <button class="security-btn danger" @click="handleLogout">退出登录</button>
          <button class="security-btn danger" @click="handleDeactivate">注销账号</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../store/auth'
import { updateUserProfile, uploadAvatar, deactivateUser } from '../../api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const avatarInput = ref(null)

const defaultAvatar = '/icons/default-avatar.png'
const isEditing = ref(false)
const saving = ref(false)

const formatDateTime = (date) => {
  if (!date) return '未知'
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN') + ' ' + d.toLocaleTimeString('zh-CN')
}

const form = ref({
  username: '',
  phone: '',
  email: '',
  avatarUrl: defaultAvatar,
  region: '',
  isVerified: false,
  userType: 1,
  authStatus: 0,
  createdTime: '',
})

const formBackup = ref({})

const userTypeLabel = computed(() => {
  const types = { 1: '普通用户', 2: '律师', 3: '管理员' }
  return types[form.value.userType] || '未知'
})

const userTypeClass = computed(() => {
  const classes = { 1: 'type-user', 2: 'type-lawyer', 3: 'type-admin' }
  return classes[form.value.userType] || ''
})

const authStatusLabel = computed(() => {
  const statuses = { 0: '未申请', 1: '审核中', 2: '已通过', 3: '已拒绝' }
  return statuses[form.value.authStatus] || '未知'
})

const authStatusClass = computed(() => {
  const classes = { 0: 'auth-none', 1: 'auth-pending', 2: 'auth-passed', 3: 'auth-rejected' }
  return classes[form.value.authStatus] || ''
})

const loadUserInfo = () => {
  const user = authStore.user
  if (user) {
    form.value = {
      username: user.username || '',
      phone: user.phone || '',
      email: user.email || '',
      avatarUrl: user.avatarUrl || defaultAvatar,
      region: user.region || '',
      isVerified: user.isVerified || false,
      userType: user.userType || 1,
      authStatus: user.authStatus || 0,
      createdTime: user.createdTime || '',
    }
  }
}

const startEdit = () => {
  formBackup.value = { ...form.value }
  isEditing.value = true
}

const cancelEdit = () => {
  form.value = { ...formBackup.value }
  isEditing.value = false
}

const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

const handleSave = async () => {
  saving.value = true
  try {
    const data = {
      phone: form.value.phone,
      email: form.value.email,
      region: form.value.region,
    }
    const response = await updateUserProfile(data)
    if (response?.code === 200) {
      await authStore.initAuth()
      isEditing.value = false
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response?.message || '保存失败')
    }
  } catch (error) {
    console.error('更新用户信息错误:', error)
    const message = error.response?.data?.message || error.message || '保存失败，请稍后重试'
    ElMessage.error(message)
  } finally {
    saving.value = false
  }
}

const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  if (file.size > 2 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 2MB')
    return
  }

  try {
    const res = await uploadAvatar(file)
    if (res.code === 200) {
      form.value.avatarUrl = res.data 
      // 后端已更新数据库中的 avatarUrl，只需刷新用户信息即可
      await authStore.initAuth()
      loadUserInfo() 
      ElMessage.success('头像上传并保存成功！')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络错误，上传失败')
  }
}

const goVerify = () => {
  ElMessage.info('实名认证功能开发中')
}

const goChangePassword = () => {
  router.push('/change-password')
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    authStore.logout()
    router.push('/')
  }).catch(() => {})
}

// 新增：注销账号逻辑
const handleDeactivate = () => {
  ElMessageBox.confirm('注销账号后数据将无法恢复，确定要注销吗？', '危险操作', {
    confirmButtonText: '确认注销',
    cancelButtonText: '取消',
    type: 'error'
  }).then(async () => {
    try {
      const res = await deactivateUser()
      if (res?.code === 200) {
        ElMessage.success('账号已注销')
        authStore.logout()
        router.push('/login')
      } else {
        ElMessage.error(res?.message || '注销失败')
      }
    } catch (error) {
      ElMessage.error('注销请求失败，请稍后重试')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  min-height: calc(100vh - 70px);
  background: #f0f2f5;
  padding: 40px 20px;
}

.profile-container {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h2 {
  font-size: 26px;
  color: #1f2a56;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.page-header p {
  color: #8c8fa3;
  font-size: 14px;
  margin: 0;
}

/* 头像区域 */
.avatar-section {
  display: flex;
  align-items: center;
  gap: 30px;
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  flex-shrink: 0;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-upload-btn {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none; /* 默认事件穿透 */
}

/* 悬停即显现，无需任何编辑状态判断 */
.avatar-wrapper:hover .avatar-upload-btn {
  opacity: 1;
  pointer-events: auto;
}


.user-brief h3 {
  font-size: 22px;
  color: #1f2a56;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.user-type-badge {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1px;
}

.type-user {
  background: #e8f4fd;
  color: #1890ff;
}

.type-lawyer {
  background: #fff7e6;
  color: #fa8c16;
}

.type-admin {
  background: #f9f0ff;
  color: #722ed1;
}

/* 信息卡片通用样式 */
.info-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 28px 32px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f5;
}

.section-title h3 {
  font-size: 18px;
  color: #1f2a56;
  margin: 0;
  font-weight: 600;
}

/* 按钮样式 */
.edit-btn {
  background: transparent;
  border: 1px solid #d9e1e8;
  color: #1e40af;
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.edit-btn:hover {
  background: #f0f5ff;
  border-color: #1e40af;
}

.edit-actions {
  display: flex;
  gap: 12px;
}

.cancel-btn {
  background: transparent;
  border: 1px solid #d9e1e8;
  color: #666;
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: #f5f5f5;
}

.save-btn {
  background: #1e40af;
  border: none;
  color: #ffffff;
  padding: 8px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(30, 64, 175, 0.2);
}

.save-btn:hover {
  background: #1d3a9e;
  box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3);
}

.save-btn:disabled {
  background: #a0aec0;
  box-shadow: none;
  cursor: not-allowed;
}

/* 表单样式 */
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.form-row:last-child {
  margin-bottom: 0;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 13px;
  color: #8c8fa3;
  font-weight: 500;
}

.form-input {
  padding: 12px 16px;
  border: 1px solid #e8ecf4;
  border-radius: 10px;
  font-size: 14px;
  color: #1f2a56;
  outline: none;
  transition: all 0.3s ease;
  background: #ffffff;
}

.form-input:focus {
  border-color: #1e40af;
  box-shadow: 0 0 0 3px rgba(30, 64, 175, 0.1);
}

.form-input.disabled {
  background: #f8f9fb;
  color: #8c8fa3;
  cursor: not-allowed;
  border-color: #f0f2f5;
}

/* 认证状态 */
.verify-status {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
}

.verified {
  color: #52c41a;
  font-size: 14px;
  font-weight: 500;
}

.unverified {
  color: #ff4d4f;
  font-size: 14px;
  font-weight: 500;
}

.verify-btn {
  background: transparent;
  border: none;
  color: #1e40af;
  font-size: 13px;
  cursor: pointer;
  text-decoration: underline;
  font-weight: 500;
}

.verify-btn:hover {
  color: #1d3a9e;
}

/* 账户信息 */
.account-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.account-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f2f5;
}

.account-item:last-child {
  border-bottom: none;
}

.account-label {
  color: #8c8fa3;
  font-size: 14px;
}

.account-value {
  color: #1f2a56;
  font-size: 14px;
  font-weight: 500;
}

.status-active {
  color: #52c41a;
}

.auth-none {
  color: #8c8fa3;
}

.auth-pending {
  color: #fa8c16;
}

.auth-passed {
  color: #52c41a;
}

.auth-rejected {
  color: #ff4d4f;
}

/* 安全设置 */
.security-actions {
  display: flex;
  gap: 16px;
}

.security-btn {
  padding: 12px 28px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f0f2f5;
  border: 1px solid #e8ecf4;
  color: #1f2a56;
}

.security-btn:hover {
  background: #e8ecf4;
  transform: translateY(-1px);
}

.security-btn.danger {
  background: #fff1f0;
  border-color: #ffccc7;
  color: #ff4d4f;
}

.security-btn.danger:hover {
  background: #ffccc7;
}

/* 响应式 */
@media (max-width: 768px) {
  .profile-page {
    padding: 20px 12px;
  }

  .avatar-section {
    flex-direction: column;
    text-align: center;
    padding: 24px;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .security-actions {
    flex-direction: column;
  }

  .info-section {
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .page-header h2 {
    font-size: 22px;
  }

  .avatar-img {
    width: 90px;
    height: 90px;
  }

  .avatar-wrapper {
    width: 90px;
    height: 90px;
  }
}
</style>
