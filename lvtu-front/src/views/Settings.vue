
<template>
  <div class="page">
    <div class="bg-glow glow1"></div>
    <div class="bg-glow glow2"></div>

    <div class="settings-card glass">
      <div class="header">
        <div>

          <h1>设置</h1>
          <p class="desc">管理您的账户与认证信息</p>
        </div>
      </div>

      <div class="settings-list">
        <!-- 认证中心 -->
        <div class="setting-item glass" @click="goToAuthCenter">
          <div class="item-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <path d="M12 2L2 7l10 5 10-5-10-5z"/>
              <path d="M2 17l10 5 10-5"/>
              <path d="M2 12l10 5 10-5"/>
            </svg>
          </div>
          <div class="item-content">
            <h3>认证中心</h3>
            <p>实名认证、律师认证管理</p>
          </div>
          <div class="item-arrow">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </div>
        </div>

        <!-- 申请状态（如果有申请） -->
        <div class="setting-item glass" @click="goToApplicationStatus">
          <div class="item-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
              <polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
          </div>
          <div class="item-content">
            <h3>律师认证进度</h3>
            <p>查看您的申请状态与详情</p>
          </div>
          <div class="item-arrow">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </div>
        </div>

        <!-- 账号安全（预留） -->
        <div class="setting-item glass" @click="goToSecurity">
          <div class="item-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
          </div>
          <div class="item-content">
            <h3>账号安全</h3>
            <p>修改密码、绑定手机</p>
          </div>
          <div class="item-arrow">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </div>
        </div>

        <!-- 通知设置（预留） -->
        <div class="setting-item glass" @click="goToNotification">
          <div class="item-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
              <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
            </svg>
          </div>
          <div class="item-content">
            <h3>通知设置</h3>
            <p>消息提醒、邮件通知</p>
          </div>
          <div class="item-arrow">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </div>
        </div>

        <!-- 关于我们 -->
        <div class="setting-item glass" @click="goToAbout">
          <div class="item-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="16" x2="12" y2="12"/>
              <circle cx="12" cy="8" r="0.5" fill="currentColor" stroke="none"/>
            </svg>
          </div>
          <div class="item-content">
            <h3>关于我们</h3>
            <p>版本号 v1.0.0 & 使用条款</p>
          </div>
          <div class="item-arrow">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </div>
        </div>
      </div>

      <!-- 退出登录按钮 -->
      <div class="logout-section">
        <button class="logout-btn glass" @click="handleLogout">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          退出登录
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const goToAuthCenter = () => {
  router.push('/auth-center')
}

const goToApplicationStatus = () => {
  router.push('/application-status')
}

const goToSecurity = () => {
  ElMessage.info('功能开发中，敬请期待')
  // router.push('/security')
}

const goToNotification = () => {
  ElMessage.info('功能开发中，敬请期待')
  // router.push('/notification')
}

const goToAbout = () => {
  ElMessage.info('律途 v1.0.0 — 法律服务平台')
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // 清除本地存储的登录信息
    localStorage.removeItem('userId')
    localStorage.removeItem('token')
    ElMessage.success('已退出登录')
    router.push('/')
  } catch {
    // 取消退出
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 40px;
  background: radial-gradient(circle at top left, rgba(168,183,201,0.25), transparent 40%),
  radial-gradient(circle at bottom right, rgba(125,148,172,0.18), transparent 45%),
  linear-gradient(180deg, #F5F8FB 0%, #EEF3F7 100%);
  position: relative;
}
.bg-glow {
  position: absolute;
  width: 420px;
  height: 420px;
  border-radius: 50%;
  filter: blur(100px);
  z-index: 0;
}
.glow1 { background: rgba(168,183,201,0.18); top: -120px; left: -100px; }
.glow2 { background: rgba(125,148,172,0.16); bottom: -120px; right: -100px; }
.glass {
  background: rgba(255,255,255,0.42);
  backdrop-filter: blur(18px);
  border: 1px solid rgba(255,255,255,0.28);
  box-shadow: 0 10px 30px rgba(95,116,139,0.10);
  border-radius: 32px;
}

.settings-card {
  max-width: 680px;
  margin: 0 auto;
  padding: 32px 28px;
  position: relative;
  z-index: 1;
}

.header {
  margin-bottom: 32px;
}
.mini {
  color: #7D94AC;
  letter-spacing: 4px;
  font-size: 13px;
  margin-bottom: 8px;
}
h1 {
  font-size: 32px;
  color: #42566E;
  margin-bottom: 8px;
}
.desc {
  color: #6F8298;
  font-size: 14px;
}

.settings-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 40px;
}

.setting-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: rgba(255,255,255,0.28);
}
.setting-item:hover {
  background: rgba(255,255,255,0.5);
  transform: translateX(4px);
}
.setting-item:active {
  transform: scale(0.98);
}

.item-icon {
  width: 44px;
  height: 44px;
  background: rgba(125,148,172,0.2);
  border-radius: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5F748B;
}
.item-content {
  flex: 1;
}
.item-content h3 {
  color: #42566E;
  margin: 0 0 4px 0;
  font-size: 16px;
}
.item-content p {
  color: #7D94AC;
  margin: 0;
  font-size: 13px;
}
.item-arrow {
  color: #9BAABA;
}

.logout-section {
  display: flex;
  justify-content: center;
}
.logout-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  border: none;
  background: rgba(255,255,255,0.32);
  color: #cf1322;
  font-size: 15px;
  border-radius: 40px;
  cursor: pointer;
  transition: all 0.2s;
}
.logout-btn:hover {
  background: rgba(255,255,255,0.6);
  transform: translateY(-2px);
}
.logout-btn:active {
  transform: scale(0.96);
}

@media (max-width: 640px) {
  .page { padding: 20px; }
  .settings-card { padding: 24px 16px; }
  .setting-item { padding: 12px 16px; }
  .item-icon { width: 36px; height: 36px; }
  h1 { font-size: 28px; }
}
</style>