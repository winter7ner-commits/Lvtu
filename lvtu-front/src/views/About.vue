<template>
  <main class="about-page">
    <section class="about-hero">
      <div class="brand-mark">
        <img src="/icons/logo.png" alt="律途" />
      </div>
      <div class="hero-copy">
        <p class="eyebrow">关于我们</p>
        <h1>律途</h1>
        <p>
          律途是面向普通用户与律师的法律服务平台，帮助用户更高效地查找律师、提交咨询需求、查看法律法规，并持续跟进订单处理结果。
        </p>
      </div>
    </section>

    <section class="about-grid" aria-label="平台介绍">
      <article class="info-panel">
        <h2>我们做什么</h2>
        <p>
          平台围绕“找律师、查法规、办咨询、看进度”组织功能，让法律服务从信息查找进入可执行的流程。
        </p>
      </article>
      <article class="info-panel">
        <h2>服务对象</h2>
        <p>
          普通用户可以发布咨询和委托需求；律师用户可以维护资料、查看可接订单并提交服务结果。
        </p>
      </article>
      <article class="info-panel">
        <h2>平台原则</h2>
        <p>
          信息清晰、流程透明、反馈及时。涉及认证、订单和法规内容的页面都优先保持可读、可追踪和易操作。
        </p>
      </article>
    </section>

    <section class="contact-panel">
      <div>
        <p class="eyebrow">联系律途</p>
        <h2>需要帮助或反馈建议？</h2>
        <p>你可以通过客服热线、邮箱或平台消息入口联系我们。</p>
      </div>
      <div class="contact-list">
        <span>400-8888-888</span>
        <span>service@lvtu.com</span>
        <span>珠海市香洲区</span>
      </div>
    </section>
  </main>
</template>

<style scoped>
.about-page {
  min-height: 100vh;
  padding: 32px 20px 56px;
  background: #f5f7fb;
  color: #172033;
  text-align: left;
}

.about-hero,
.about-grid,
.contact-panel {
  width: min(1120px, 100%);
  margin-left: auto;
  margin-right: auto;
}

.about-hero {
  display: grid;
  grid-template-columns: 180px minmax(0, 1fr);
  gap: 28px;
  align-items: center;
  padding: 28px;
  border: 1px solid #dbeafe;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 4px 8px rgba(15, 23, 42, 0.08);
}

.brand-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 156px;
  border-radius: 8px;
  background: #eff6ff;
}

.brand-mark img {
  width: 132px;
  height: auto;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-size: 13px;
  font-weight: 800;
}

h1,
h2,
p {
  margin: 0;
}

h1 {
  color: #172033;
  font-size: 36px;
  line-height: 1.2;
}

h2 {
  color: #172033;
  font-size: 20px;
  line-height: 1.35;
}

.hero-copy p:not(.eyebrow),
.info-panel p,
.contact-panel p {
  margin-top: 10px;
  color: #475467;
  line-height: 1.8;
}

.about-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-top: 18px;
}

.info-panel,
.contact-panel {
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  background: #ffffff;
}

.info-panel {
  padding: 20px;
}

.contact-panel {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: center;
  margin-top: 18px;
  padding: 22px;
}

.contact-list {
  display: grid;
  gap: 10px;
  min-width: 220px;
}

.contact-list span {
  padding: 10px 12px;
  border-radius: 8px;
  background: #eff6ff;
  color: #1d4ed8;
  font-weight: 700;
}

@media (max-width: 860px) {
  .about-hero,
  .about-grid {
    grid-template-columns: 1fr;
  }

  .contact-panel {
    flex-direction: column;
    align-items: stretch;
  }
}

@media (max-width: 640px) {
  .about-page {
    padding: 20px 12px 40px;
  }

  .about-hero {
    padding: 18px;
  }

  h1 {
    font-size: 30px;
  }
}
</style>
