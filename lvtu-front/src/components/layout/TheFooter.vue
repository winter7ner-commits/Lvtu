<template>
  <footer class="footer">
    <div class="footer-container">
      <section class="footer-brand">
        <div class="brand-line">
          <img src="/icons/logo.png" alt="律途" />
          <strong>律途</strong>
        </div>
        <p>面向用户和律师的法律服务平台，提供律师查询、订单咨询、法律法规浏览和服务进度跟踪。</p>
      </section>

      <nav class="footer-links" aria-label="底部导航">
        <div>
          <h2>平台</h2>
          <router-link to="/">首页</router-link>
          <router-link to="/about">关于我们</router-link>
          <router-link to="/laws">法律法规</router-link>
        </div>
        <div>
          <h2>服务</h2>
          <router-link to="/lawyer-list">律师查询</router-link>
          <router-link to="/order-create">发布服务</router-link>
          <router-link to="/orders">我的订单</router-link>
        </div>
        <div>
          <h2>支持</h2>
          <router-link to="/notifications">消息通知</router-link>
          <router-link to="/settings">账号设置</router-link>
          <router-link to="/auth-center">认证中心</router-link>
        </div>
        <div>
          <h2>联系</h2>
          <span>400-8888-888</span>
          <span>service@lvtu.com</span>
          <span>珠海市香洲区</span>
        </div>
      </nav>

      <div class="footer-bottom">
        <span>© 2026 LVTU Law Services. All rights reserved.</span>
        <span>法律服务信息以平台审核与具体律师沟通结果为准</span>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  margin-top: 0;
  padding: 36px 20px 22px;
  border-top: 1px solid #dbeafe;
  background: #f8fbff;
  color: #344054;
}

.footer-container {
  display: grid;
  grid-template-columns: minmax(220px, 0.8fr) minmax(0, 1.2fr);
  gap: 32px;
  width: min(1200px, 100%);
  margin: 0 auto;
}

.footer-brand {
  min-width: 0;
}

.brand-line {
  display: flex;
  gap: 10px;
  align-items: center;
}

.brand-line img {
  width: 42px;
  height: 42px;
  border-radius: 50%;
}

.brand-line strong {
  color: #172033;
  font-size: 24px;
}

.footer-brand p {
  margin: 14px 0 0;
  color: #475467;
  line-height: 1.8;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}

.footer-links div {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 0;
}

.footer-links h2 {
  margin: 0 0 2px;
  color: #172033;
  font-size: 15px;
}

.footer-links a,
.footer-links span {
  color: #475467;
  font-size: 14px;
  line-height: 1.5;
}

.footer-links a:hover {
  color: #1d4ed8;
}

.footer-bottom {
  grid-column: 1 / -1;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  padding-top: 22px;
  border-top: 1px solid #e5eaf3;
  color: #667085;
  font-size: 13px;
}

@media (max-width: 900px) {
  .footer-container,
  .footer-links {
    grid-template-columns: 1fr;
  }

  .footer-bottom {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
