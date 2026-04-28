<template>
  <footer class="footer">
    <div class="footer-container">
      <!-- Footer Content -->
      <div class="footer-content">
        <!-- About Section -->
        <div class="footer-section">
          <div class="section-title">关于LVTU</div>
          <ul class="section-links">
            <li><a href="#" class="footer-link">公司介绍</a></li>
            <li><a href="#" class="footer-link">发展历程</a></li>
            <li><a href="#" class="footer-link">企业文化</a></li>
            <li><a href="#" class="footer-link">新闻动态</a></li>
          </ul>
        </div>

        <!-- Service Section -->
        <div class="footer-section">
          <div class="section-title">服务</div>
          <ul class="section-links">
            <li><a href="#" class="footer-link">找律师</a></li>
            <li><a href="#" class="footer-link">法律咨询</a></li>
            <li><a href="#" class="footer-link">案件委托</a></li>
            <li><a href="#" class="footer-link">帮助中心</a></li>
          </ul>
        </div>

        <!-- Support Section -->
        <div class="footer-section">
          <div class="section-title">支持</div>
          <ul class="section-links">
            <li><a href="#" class="footer-link">联系我们</a></li>
            <li><a href="#" class="footer-link">常见问题</a></li>
            <li><a href="#" class="footer-link">用户反馈</a></li>
            <li><a href="#" class="footer-link">意见建议</a></li>
          </ul>
        </div>

        <!-- Contact Section -->
        <div class="footer-section">
          <div class="section-title">联系方式</div>
          <div class="contact-info">
            <p class="contact-item">
              <i class="icon-phone"></i>
              <span>400-8888-888</span>
            </p>
            <p class="contact-item">
              <i class="icon-email"></i>
              <span>service@lvtu.com</span>
            </p>
            <p class="contact-item">
              <i class="icon-location"></i>
              <span>北京市朝阳区</span>
            </p>
          </div>
        </div>
      </div>

      <!-- Divider -->
      <div class="footer-divider"></div>

      <!-- Bottom Section -->
      <div class="footer-bottom">
        <div class="footer-left">
          <p class="copyright">
            &copy; 2024 LVTU Law Services. All rights reserved.
          </p>
          <div class="footer-policies">
            <a href="#" class="policy-link">隐私政策</a>
            <span class="separator">|</span>
            <a href="#" class="policy-link">用户协议</a>
            <span class="separator">|</span>
            <a href="#" class="policy-link">免责声明</a>
          </div>
        </div>

        <div class="footer-right">
          <div class="social-links">
            <a href="#" class="social-icon" title="微博">
              <i class="icon-weibo"></i>
            </a>
            <a href="#" class="social-icon" title="微信">
              <i class="icon-wechat"></i>
            </a>
            <a href="#" class="social-icon" title="QQ">
              <i class="icon-qq"></i>
            </a>
            <a href="#" class="social-icon" title="抖音">
              <i class="icon-douyin"></i>
            </a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
// Footer component - no additional logic needed
</script>

<style scoped>
.footer {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-top: 3px solid #2563eb;
  padding: 50px 20px 20px;
  margin-top: 50px;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* Footer Content */
.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 40px;
  margin-bottom: 40px;
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e40af;
  margin-bottom: 10px;
  position: relative;
  padding-bottom: 8px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 30px;
  height: 3px;
  background: linear-gradient(90deg, #2563eb, #60a5fa);
  border-radius: 2px;
}

.section-links {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.footer-link {
  color: #64748b;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  display: inline-block;
  position: relative;
}

.footer-link::before {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: #2563eb;
  transition: width 0.3s ease;
}

.footer-link:hover {
  color: #2563eb;
}

.footer-link:hover::before {
  width: 100%;
}

/* Contact Info */
.contact-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #64748b;
  font-size: 14px;
  margin: 0;
}

.contact-item i {
  color: #2563eb;
  font-size: 16px;
}

/* Divider */
.footer-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, #cbd5e1, transparent);
  margin: 30px 0;
}

/* Bottom Section */
.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.footer-left {
  flex: 1;
  min-width: 200px;
}

.copyright {
  color: #64748b;
  font-size: 13px;
  margin: 0 0 10px 0;
}

.footer-policies {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.policy-link {
  color: #64748b;
  text-decoration: none;
  font-size: 13px;
  transition: color 0.3s ease;
}

.policy-link:hover {
  color: #2563eb;
}

.separator {
  color: #cbd5e1;
}

/* Social Links */
.footer-right {
  flex-shrink: 0;
}

.social-links {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
}

.social-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #2563eb, #1e40af);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  text-decoration: none;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(30, 64, 175, 0.1);
}

.social-icon:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(30, 64, 175, 0.2);
  background: linear-gradient(135deg, #1e40af, #1e3a8a);
}

/* Responsive */
@media (max-width: 768px) {
  .footer {
    padding: 30px 15px 15px;
    margin-top: 30px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .footer-bottom {
    flex-direction: column;
    justify-content: center;
    text-align: center;
  }

  .footer-left,
  .footer-right {
    width: 100%;
  }

  .social-links {
    justify-content: center;
  }

  .footer-policies {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .footer {
    padding: 20px 10px 10px;
  }

  .footer-content {
    gap: 20px;
  }

  .section-links {
    gap: 8px;
  }

  .section-title {
    font-size: 14px;
  }

  .footer-link {
    font-size: 13px;
  }
}
</style>
