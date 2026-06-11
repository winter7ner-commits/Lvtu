<template>
  <div class="security-page">
    <div class="security-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="header-bg-glow glow1"></div>
        <div class="header-bg-glow glow2"></div>
        <div class="header-content">
          <button class="back-btn" @click="goBack">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="15 18 9 12 15 6"/>
            </svg>
            返回
          </button>
          <div class="title-section">
            <div class="title-icon">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                <path d="M12 2L2 7L12 12L22 7L12 2Z"/>
                <path d="M2 17L12 22L22 17"/>
                <path d="M2 12L12 17L22 12"/>
              </svg>
            </div>
            <div>
              <h2>账号安全</h2>
              <p>管理您的账户安全设置</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 安全设置卡片 -->
      <div class="security-cards">
        <!-- 密码安全卡片 -->
        <div class="security-card glass" @click="goChangePassword">
          <div class="card-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
          </div>
          <div class="card-content">
            <h3>修改密码</h3>
            <p>定期修改密码，提高账户安全性</p>
          </div>
          <div class="card-status">
            <span class="status-badge">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
              </svg>
              安全
            </span>
            <div class="arrow">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </div>
          </div>
        </div>

        <!-- 安全问答卡片 -->
        <div class="security-card glass" @click="goSecurityQuestions">
          <div class="card-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <circle cx="12" cy="12" r="10"/>
              <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/>
              <line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
          </div>
          <div class="card-content">
            <h3>安全问答设置</h3>
            <p>设置安全问题，用于找回密码和身份验证</p>
          </div>
          <div class="card-status">
            <span class="status-badge important">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
              重要
            </span>
            <div class="arrow">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </div>
          </div>
        </div>

        <!-- 账号恢复卡片 -->
        <div class="security-card glass" @click="goRecoverAccount">
          <div class="card-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <polyline points="23 4 23 10 17 10"/>
              <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/>
            </svg>
          </div>
          <div class="card-content">
            <h3>恢复已注销账号</h3>
            <p>恢复您之前注销的账号，保留历史数据</p>
          </div>
          <div class="card-status">
            <span class="status-badge important">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
              重要
            </span>
            <div class="arrow">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </div>
          </div>
        </div>

        <!-- 设备管理卡片 -->
        <div class="security-card glass" @click="goDeviceManagement">
          <div class="card-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <rect x="4" y="4" width="16" height="16" rx="2" ry="2"/>
              <line x1="8" y1="4" x2="8" y2="20"/>
              <line x1="12" y1="4" x2="12" y2="20"/>
              <line x1="16" y1="4" x2="16" y2="20"/>
              <line x1="2" y1="8" x2="22" y2="8"/>
              <line x1="2" y1="12" x2="22" y2="12"/>
              <line x1="2" y1="16" x2="22" y2="16"/>
            </svg>
          </div>
          <div class="card-content">
            <h3>登录设备管理</h3>
            <p>查看和管理您的登录设备，确保账户安全</p>
          </div>
          <div class="card-status">
            <span class="status-badge">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
              </svg>
              安全
            </span>
            <div class="arrow">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- 安全提示 -->
      <div class="security-tips glass">
        <div class="tips-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/>
            <line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
        </div>
        <div class="tips-content">
          <h4>安全提示</h4>
          <p>请定期更换密码，不要在公共设备上保存登录状态，避免使用过于简单的密码。</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.push('/settings')
}

const goChangePassword = () => {
  // 跳转到修改密码页面，并传递来源参数
  router.push({
    path: '/change-password',
    query: { from: '/security-settings' }
  })
}

const goSecurityQuestions = () => {
  router.push('/security-questions')
}

const goRecoverAccount = () => {
  router.push({
    path: '/restore-account',
    query: { from: '/security-settings' }
  })
}

const goDeviceManagement = () => {
  router.push('/device-management')
}
</script>

<style scoped>
.security-page {
  min-height: calc(100vh - 70px);
  padding: 40px 20px;
  background: radial-gradient(circle at top left, rgba(168,183,201,0.25), transparent 40%),
    radial-gradient(circle at bottom right, rgba(125,148,172,0.18), transparent 45%),
    linear-gradient(180deg, #F5F8FB 0%, #EEF3F7 100%);
  position: relative;
  overflow: hidden;
}

.security-container {
  max-width: 900px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

/* 页面标题 */
.page-header {
  margin-bottom: 40px;
  position: relative;
  padding: 30px 0;
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
}

.header-bg-glow {
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  filter: blur(80px);
  z-index: -1;
}

.glow1 {
  background: rgba(168,183,201,0.18);
  top: -100px;
  left: -100px;
}

.glow2 {
  background: rgba(125,148,172,0.16);
  bottom: -100px;
  right: -100px;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.title-icon {
  width: 56px;
  height: 56px;
  background: rgba(125,148,172,0.2);
  border-radius: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5F748B;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(125,148,172,0.15);
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255,255,255,0.42);
  border: 1px solid rgba(255,255,255,0.28);
  color: #1e40af;
  font-size: 14px;
  cursor: pointer;
  padding: 10px 18px;
  border-radius: 20px;
  transition: all 0.3s ease;
  width: fit-content;
  box-shadow: 0 4px 12px rgba(30, 64, 175, 0.1);
}

.back-btn:hover {
  background: rgba(255,255,255,0.6);
  transform: translateX(-2px);
  box-shadow: 0 6px 16px rgba(30, 64, 175, 0.15);
}

.page-header h2 {
  font-size: 32px;
  color: #42566E;
  margin: 0;
  font-weight: 600;
}

.page-header p {
  color: #6F8298;
  font-size: 14px;
  margin: 0;
}

/* 安全设置卡片 */
.security-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.security-card {
  background: rgba(255,255,255,0.42);
  backdrop-filter: blur(18px);
  border: 1px solid rgba(255,255,255,0.28);
  box-shadow: 0 10px 30px rgba(95,116,139,0.10);
  border-radius: 24px;
  padding: 24px;
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  overflow: hidden;
}

.security-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 15px 35px rgba(95,116,139,0.15);
  background: rgba(255,255,255,0.5);
  border-color: rgba(255,255,255,0.5);
}

.card-icon {
  width: 56px;
  height: 56px;
  background: rgba(125,148,172,0.2);
  border-radius: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5F748B;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(125,148,172,0.15);
  transition: all 0.3s ease;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  color: #42566E;
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 600;
}

.card-content p {
  color: #7D94AC;
  margin: 0;
  font-size: 14px;
}

.card-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  background: #e8f4fd;
  color: #1890ff;
  box-shadow: 0 2px 6px rgba(24,144,255,0.1);
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-badge.important {
  background: #fff1f0;
  color: #ff4d4f;
}

.arrow {
  color: #9BAABA;
  transition: transform 0.3s ease;
}

.security-card:hover .arrow {
  transform: translateX(4px);
}

.security-card:hover .card-icon {
  background: rgba(125,148,172,0.3);
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(125,148,172,0.2);
}

/* 安全提示 */
.security-tips {
  background: rgba(255,255,255,0.42);
  backdrop-filter: blur(18px);
  border: 1px solid rgba(255,255,255,0.28);
  box-shadow: 0 10px 30px rgba(95,116,139,0.10);
  border-radius: 24px;
  padding: 24px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  position: relative;
  overflow: hidden;
}

.tips-icon {
  width: 48px;
  height: 48px;
  background: rgba(255,249,244,0.8);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ff7875;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(255,120,117,0.15);
}

.tips-content h4 {
  color: #42566E;
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tips-content p {
  color: #7D94AC;
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
}

/* 响应式 */
@media (max-width: 768px) {
  .security-cards {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .security-card {
    padding: 20px;
  }

  .card-icon {
    width: 48px;
    height: 48px;
  }

  .card-content h3 {
    font-size: 16px;
  }

  .security-tips {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .security-page {
    padding: 20px 12px;
  }

  .page-header {
    padding: 20px 0;
  }

  .page-header h2 {
    font-size: 28px;
  }

  .security-cards {
    gap: 16px;
  }

  .security-card {
    padding: 16px;
  }
}
</style>