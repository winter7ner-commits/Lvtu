
<template>
  <div class="help-center-page">
    <div class="help-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h2>帮助中心</h2>
        <p>常见问题与使用指南，快速找到您需要的答案</p>
      </div>

      <!-- 搜索栏 -->
      <div class="search-section">
        <div class="search-box">
          <span class="search-icon">🔍</span>
          <input
            type="text"
            v-model="searchQuery"
            placeholder="搜索问题，如：如何下单、退款、注销账号..."
            class="search-input"
          />
          <button v-if="searchQuery" class="clear-btn" @click="searchQuery = ''">✕</button>
        </div>
      </div>

      <!-- 快捷入口 -->
      <div class="quick-links">
        <div class="quick-link-card" @click="scrollToCategory('order')">
          <div class="quick-icon">📋</div>
          <span>订单相关</span>
        </div>
        <div class="quick-link-card" @click="scrollToCategory('payment')">
          <div class="quick-icon">💰</div>
          <span>支付退款</span>
        </div>
        <div class="quick-link-card" @click="scrollToCategory('account')">
          <div class="quick-icon">👤</div>
          <span>账户管理</span>
        </div>
        <div class="quick-link-card" @click="scrollToCategory('lawyer')">
          <div class="quick-icon">⚖️</div>
          <span>律师服务</span>
        </div>
        <div class="quick-link-card" @click="scrollToCategory('safety')">
          <div class="quick-icon">🔒</div>
          <span>安全隐私</span>
        </div>
      </div>

      <!-- FAQ 分类列表 -->
      <div class="faq-sections">
        <div
          v-for="category in filteredCategories"
          :key="category.id"
          :id="'category-' + category.id"
          class="faq-category"
        >
          <h3 class="category-title">
            <span class="category-icon">{{ category.icon }}</span>
            {{ category.name }}
          </h3>
          <div class="faq-list">
            <div
              v-for="faq in category.items"
              :key="faq.id"
              class="faq-item"
              :class="{ expanded: expandedId === category.id + '-' + faq.id }"
            >
              <div class="faq-question" @click="toggleFaq(category.id, faq.id)">
                <span class="question-text">{{ faq.question }}</span>
                <span class="expand-icon" :class="{ rotated: expandedId === category.id + '-' + faq.id }">›</span>
              </div>
              <transition name="faq-expand">
                <div v-if="expandedId === category.id + '-' + faq.id" class="faq-answer">
                  <div class="answer-content" v-html="faq.answer"></div>
                  <!-- 如果有关联操作 -->
                  <div v-if="faq.action" class="faq-action">
                    <button
                      class="action-btn"
                      :class="faq.action.type"
                      @click="handleAction(faq.action)"
                    >
                      {{ faq.action.label }}
                    </button>
                  </div>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </div>

      <!-- 没有搜索结果 -->
      <div v-if="searchQuery && filteredCategories.length === 0" class="no-result">
        <div class="no-result-icon">😕</div>
        <p class="no-result-text">没有找到相关问题</p>
        <p class="no-result-hint">试试换个关键词搜索</p>
      </div>

      <!-- 底部反馈区 -->
      <div class="feedback-section">
        <div class="feedback-card">
          <h3>没有找到答案？</h3>
          <p>如果您的问题未在上方列出，可以通过以下方式获取帮助：</p>
          <div class="feedback-actions">
            <div class="feedback-item">
              <span class="feedback-icon">📧</span>
              <div>
                <div class="feedback-label">发送邮件</div>
                <div class="feedback-value">service@lvtu.com</div>
              </div>
            </div>
            <div class="feedback-item">
              <span class="feedback-icon">📞</span>
              <div>
                <div class="feedback-label">客服热线</div>
                <div class="feedback-value">400-8888-888</div>
              </div>
            </div>
            <div class="feedback-item">
              <span class="feedback-icon">🕐</span>
              <div>
                <div class="feedback-label">服务时间</div>
                <div class="feedback-value">工作日 9:00 - 18:00</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchQuery = ref('')
const expandedId = ref(null)

// FAQ 数据
const categories = ref([
  {
    id: 'order',
    name: '订单相关',
    icon: '📋',
    items: [
      {
        id: 'how-to-order',
        question: '如何下单购买法律服务？',
        answer: `<p>您可以通过以下步骤下单：</p>
          <ol>
            <li>在导航栏点击「服务」，选择您需要的法律服务类型（如在线咨询、电话咨询、文书代写等）</li>
            <li>填写服务需求表单，详细描述您的法律问题</li>
            <li>确认订单信息并完成支付</li>
            <li>等待律师接单，开始服务</li>
          </ol>
          <p>您也可以先浏览律师列表，选择心仪的律师后直接下单。</p>`,
        action: { type: 'primary', label: '去下单', route: '/order-create' }
      },
      {
        id: 'order-status',
        question: '如何查看订单进度？',
        answer: `<p>登录后，点击导航栏「订单」→「全部订单」，即可查看所有订单及其当前状态。订单状态说明：</p>
          <ul>
            <li><strong>待支付</strong>：订单已创建，等待您完成付款</li>
            <li><strong>处理中</strong>：律师已接单，正在为您提供服务</li>
            <li><strong>待确认</strong>：律师已提交服务成果，等待您确认</li>
            <li><strong>已完成</strong>：服务已完成，您可以进行评价</li>
            <li><strong>已取消</strong>：订单已取消</li>
          </ul>`,
        action: { type: 'primary', label: '查看我的订单', route: '/orders' }
      },
      {
        id: 'cancel-order',
        question: '如何取消订单？',
        answer: `<p>订单取消规则如下：</p>
          <ul>
            <li><strong>待支付订单</strong>：可直接取消，无需审核</li>
            <li><strong>处理中订单</strong>：需提交取消申请，律师确认后取消。如律师已开始服务，可能产生部分费用</li>
            <li><strong>已完成订单</strong>：无法取消，如有异议可申请售后</li>
          </ul>
          <p>进入「我的订单」→ 选择对应订单 → 点击「取消订单」即可操作。</p>`,
        action: { type: 'primary', label: '查看我的订单', route: '/orders' }
      },
      {
        id: 'evaluate-order',
        question: '如何对律师服务进行评价？',
        answer: `<p>订单完成后，您可以对该次服务进行评价：</p>
          <ol>
            <li>进入「我的订单」，找到状态为「已完成」的订单</li>
            <li>点击「评价」按钮</li>
            <li>对律师的专业度、服务态度、响应速度等进行星级评分</li>
            <li>填写文字评价（选填）</li>
            <li>提交评价</li>
          </ol>
          <p>您的评价将帮助其他用户选择合适的律师，也会激励律师提供更优质的服务。</p>`,
        action: { type: 'primary', label: '去评价', route: '/orders?status=pending_review' }
      }
    ]
  },
  {
    id: 'payment',
    name: '支付退款',
    icon: '💰',
    items: [
      {
        id: 'payment-methods',
        question: '支持哪些支付方式？',
        answer: `<p>目前律途平台支持以下支付方式：</p>
          <ul>
            <li>支付宝</li>
            <li>微信支付</li>
            <li>银行卡支付（借记卡/信用卡）</li>
          </ul>
          <p>所有支付均通过安全加密通道处理，请放心支付。</p>`
      },
      {
        id: 'refund-policy',
        question: '如何申请退款？退款规则是什么？',
        answer: `<p>退款规则根据订单状态有所不同：</p>
          <ul>
            <li><strong>待支付</strong>：无需退款，直接取消订单即可</li>
            <li><strong>处理中</strong>：如律师尚未开始服务，可全额退款；如律师已开始服务，将根据服务进度按比例退款</li>
            <li><strong>已完成</strong>：如对服务质量有异议，可在完成后7天内申请售后，平台将介入处理</li>
          </ul>
          <p>退款申请提交后，一般1-3个工作日内处理，退款将原路返回您的支付账户。</p>`,
        action: { type: 'primary', label: '查看我的订单', route: '/orders' }
      },
      {
        id: 'payment-failed',
        question: '支付失败怎么办？',
        answer: `<p>如果支付失败，请按以下步骤排查：</p>
          <ol>
            <li>检查网络连接是否正常</li>
            <li>确认支付账户余额是否充足</li>
            <li>确认支付限额是否超限</li>
            <li>尝试更换支付方式</li>
            <li>如多次失败，请联系支付平台客服</li>
          </ol>
          <p>注意：支付失败不会扣款，请放心重试。如已扣款但订单未更新，请联系我们。</p>`
      }
    ]
  },
  {
    id: 'account',
    name: '账户管理',
    icon: '👤',
    items: [
      {
        id: 'change-password',
        question: '如何修改密码？',
        answer: `<p>修改密码步骤：</p>
          <ol>
            <li>登录后，进入「个人资料」页面</li>
            <li>在「安全设置」区域点击「修改密码」</li>
            <li>输入当前密码和新密码</li>
            <li>确认保存即可</li>
          </ol>
          <p>如果忘记密码，可以在登录页点击「忘记密码」通过手机号或邮箱重置。</p>`,
        action: { type: 'primary', label: '修改密码', route: '/change-password' }
      },
      {
        id: 'deactivate-account',
        question: '如何注销账号？注销后数据怎么处理？',
        answer: `<p><strong>⚠️ 账号注销是重要操作，请仔细阅读以下说明：</strong></p>
          <ul>
            <li><strong>注销条件</strong>：账号下无进行中的订单，无未处理的退款/纠纷</li>
            <li><strong>数据处理</strong>：注销后，您的个人信息将被删除或匿名化处理，订单记录将保留但脱敏</li>
            <li><strong>冷静期</strong>：提交注销申请后，有7天冷静期，期间您可以登录取消注销</li>
            <li><strong>可恢复</strong>：冷静期过后账号将被正式注销，但您仍可通过绑定的手机号自助恢复账号</li>
          </ul>
          <p>如果您只是暂时不想使用，建议您直接退出登录即可，无需注销账号。</p>
          <p>如确定需要注销，请前往「个人资料」→「安全设置」→「注销账号」。</p>`,
        action: { type: 'danger', label: '前往注销', route: '/user-profile' }
      },
      {
        id: 'restore-account',
        question: '如何恢复已注销的账号？',
        answer: `<p>如果您误注销了账号，可以通过以下步骤自助恢复：</p>
          <ol>
            <li>在登录页点击「恢复已注销账号」</li>
            <li>输入注销账号时绑定的手机号，系统将查询关联的已注销账号</li>
            <li>选择您要恢复的账号</li>
            <li>确认恢复（如手机号已被其他账号使用，需确认手机号转移）</li>
            <li>恢复成功后系统会自动登录，并为您生成新的用户名</li>
          </ol>
          <p><strong>注意事项：</strong></p>
          <ul>
            <li>恢复后系统会生成新的随机用户名，请在登录后及时修改</li>
            <li>如果该手机号已绑定其他正常使用的账号，恢复时手机号将转移至恢复的账号，原账号将失去手机绑定</li>
            <li>原密码保持不变，可使用新用户名+原密码登录</li>
          </ul>`,
        action: { type: 'primary', label: '恢复已注销账号', route: '/restore-account' }
      },
      {
        id: 'update-profile',
        question: '如何修改个人资料？',
        answer: `<p>修改个人资料步骤：</p>
          <ol>
            <li>进入「个人资料」页面</li>
            <li>点击「编辑」按钮</li>
            <li>修改您需要更改的信息（用户名、手机号、邮箱、地区等）</li>
            <li>点击「保存」</li>
          </ol>
          <p>头像也可以点击头像区域的「更换头像」进行更换。</p>`,
        action: { type: 'primary', label: '修改资料', route: '/user-profile' }
      },
      {
        id: 'real-name-verify',
        question: '如何进行实名认证？',
        answer: `<p>实名认证是保障交易安全的重要措施：</p>
          <ol>
            <li>进入「个人资料」页面</li>
            <li>在实名认证栏点击「去认证」</li>
            <li>按照提示上传身份证件并填写相关信息</li>
            <li>提交后等待审核，一般1-2个工作日完成</li>
          </ol>
          <p>实名认证后可享受更多平台服务，如发布委托、成为律师等。</p>`,
        action: { type: 'primary', label: '去认证', route: '/user-profile' }
      }
    ]
  },
  {
    id: 'lawyer',
    name: '律师服务',
    icon: '⚖️',
    items: [
      {
        id: 'how-to-find-lawyer',
        question: '如何找到合适的律师？',
        answer: `<p>您可以通过以下方式寻找律师：</p>
          <ol>
            <li><strong>律师列表</strong>：浏览平台所有律师，按专业领域、评分、价格等筛选</li>
            <li><strong>搜索</strong>：在搜索栏输入关键词，如"合同纠纷"、"离婚律师"等</li>
            <li><strong>下单匹配</strong>：直接下单，系统会根据您的需求推荐合适的律师</li>
          </ol>
          <p>建议您查看律师的个人简介、执业年限、用户评价等信息，选择最适合您的律师。</p>`,
        action: { type: 'primary', label: '查找律师', route: '/lawyer-list' }
      },
      {
        id: 'become-lawyer',
        question: '如何成为平台律师？',
        answer: `<p>成为律途平台律师需要以下步骤：</p>
          <ol>
            <li>注册并登录账号</li>
            <li>进入「认证中心」，申请律师认证</li>
            <li>提交律师执业证、身份证等资质材料</li>
            <li>平台审核（一般3-5个工作日）</li>
            <li>审核通过后，完善律师个人资料</li>
            <li>开始接单，为用户提供法律服务</li>
          </ol>
          <p>律师认证需要持有有效的律师执业证书。</p>`,
        action: { type: 'primary', label: '申请律师认证', route: '/auth-center' }
      },
      {
        id: 'service-types',
        question: '平台提供哪些法律服务？',
        answer: `<p>律途平台目前提供以下法律服务：</p>
          <ul>
            <li><strong>在线法律咨询</strong>：通过文字方式与律师在线沟通</li>
            <li><strong>电话法律咨询</strong>：预约律师电话咨询，更高效沟通</li>
            <li><strong>文书代写</strong>：律师代写法律文书（起诉状、答辩状等）</li>
            <li><strong>合同审核</strong>：律师帮您审查合同条款，规避风险</li>
            <li><strong>婚姻家事</strong>：离婚、财产分割、抚养权等婚姻家庭法律服务</li>
            <li><strong>诉讼代理</strong>：律师代理出庭，全程跟进案件</li>
          </ul>`,
        action: { type: 'primary', label: '查看服务', route: '/order-create' }
      }
    ]
  },
  {
    id: 'safety',
    name: '安全隐私',
    icon: '🔒',
    items: [
      {
        id: 'data-safety',
        question: '我的个人信息安全吗？',
        answer: `<p>律途非常重视用户的信息安全，我们采取了以下措施：</p>
          <ul>
            <li>所有数据传输采用SSL加密</li>
            <li>敏感信息（身份证、银行卡等）加密存储</li>
            <li>严格的权限控制，律师只能查看与案件相关的必要信息</li>
            <li>定期安全审计，防范数据泄露风险</li>
          </ul>
          <p>我们承诺不会将您的个人信息出售或分享给第三方。</p>`
      },
      {
        id: 'lawyer-verify',
        question: '如何确保律师的真实性？',
        answer: `<p>平台对入驻律师有严格的审核机制：</p>
          <ul>
            <li>审核律师执业证书真伪</li>
            <li>核实律师执业律所信息</li>
            <li>定期更新律师资质信息</li>
            <li>用户评价体系持续监督律师服务质量</li>
          </ul>
          <p>您可以在律师详情页查看其执业证号、执业律所等认证信息。</p>`
      },
      {
        id: 'dispute-handling',
        question: '与律师发生纠纷怎么办？',
        answer: `<p>如对律师服务有异议，您可以：</p>
          <ol>
            <li><strong>协商解决</strong>：先与律师沟通，尝试协商解决</li>
            <li><strong>申请退款</strong>：在订单详情页申请退款，说明原因</li>
            <li><strong>平台介入</strong>：如协商无果，可申请平台介入调解</li>
            <li><strong>投诉举报</strong>：如律师存在违规行为，可进行投诉举报</li>
          </ol>
          <p>平台会在1-3个工作日内响应您的投诉，并公正处理。</p>`
      }
    ]
  }
])

// 搜索过滤
const filteredCategories = computed(() => {
  if (!searchQuery.value.trim()) return categories.value

  const query = searchQuery.value.toLowerCase()
  return categories.value
    .map(category => {
      const filteredItems = category.items.filter(
        item =>
          item.question.toLowerCase().includes(query) ||
          item.answer.toLowerCase().includes(query)
      )
      return { ...category, items: filteredItems }
    })
    .filter(category => category.items.length > 0)
})

// 展开/折叠
const toggleFaq = (categoryId, faqId) => {
  const key = categoryId + '-' + faqId
  expandedId.value = expandedId.value === key ? null : key
}

// 滚动到分类
const scrollToCategory = (categoryId) => {
  const el = document.getElementById('category-' + categoryId)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

// 处理操作按钮
const handleAction = (action) => {
  if (action.route) {
    router.push(action.route)
  }
}
</script>

<style scoped>
.help-center-page {
  min-height: calc(100vh - 70px);
  background: #f0f2f5;
  padding: 40px 20px;
}

.help-container {
  max-width: 900px;
  margin: 0 auto;
}

/* 页面标题 */
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

/* 搜索栏 */
.search-section {
  margin-bottom: 28px;
}

.search-box {
  display: flex;
  align-items: center;
  background: #ffffff;
  border-radius: 12px;
  padding: 12px 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 2px solid transparent;
  transition: all 0.3s;
}

.search-box:focus-within {
  border-color: #2563eb;
  box-shadow: 0 2px 16px rgba(37, 99, 235, 0.12);
}

.search-icon {
  font-size: 18px;
  margin-right: 12px;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 15px;
  color: #1f2a56;
  background: transparent;
}

.search-input::placeholder {
  color: #b0b3c5;
}

.clear-btn {
  background: none;
  border: none;
  color: #8c8fa3;
  cursor: pointer;
  font-size: 16px;
  padding: 4px 8px;
  border-radius: 50%;
  transition: all 0.2s;
}

.clear-btn:hover {
  background: #f0f2f5;
  color: #1f2a56;
}

/* 快捷入口 */
.quick-links {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-bottom: 36px;
}

.quick-link-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 12px;
  background: #ffffff;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
}

.quick-link-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.12);
}

.quick-icon {
  font-size: 28px;
}

.quick-link-card span {
  font-size: 14px;
  color: #1f2a56;
  font-weight: 500;
}

/* FAQ 分类 */
.faq-category {
  margin-bottom: 28px;
}

.category-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  color: #1f2a56;
  margin: 0 0 16px 0;
  font-weight: 600;
}

.category-icon {
  font-size: 22px;
}

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.faq-item {
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
  transition: all 0.3s;
}

.faq-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.faq-item.expanded {
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.1);
}

.faq-question {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px;
  cursor: pointer;
  user-select: none;
  transition: background 0.2s;
}

.faq-question:hover {
  background: #f8f9fc;
}

.question-text {
  font-size: 15px;
  color: #1f2a56;
  font-weight: 500;
  flex: 1;
  padding-right: 16px;
}

.expand-icon {
  font-size: 20px;
  color: #8c8fa3;
  transition: transform 0.3s;
  flex-shrink: 0;
  font-weight: bold;
}

.expand-icon.rotated {
  transform: rotate(90deg);
  color: #2563eb;
}

.faq-answer {
  padding: 0 22px 20px;
  border-top: 1px solid #f0f2f5;
}

.answer-content {
  padding-top: 16px;
  font-size: 14px;
  color: #555770;
  line-height: 1.8;
}

.answer-content :deep(ol),
.answer-content :deep(ul) {
  padding-left: 20px;
  margin: 8px 0;
}

.answer-content :deep(li) {
  margin-bottom: 6px;
}

.answer-content :deep(strong) {
  color: #1f2a56;
}

.answer-content :deep(p) {
  margin: 8px 0;
}

.faq-action {
  margin-top: 16px;
  padding-top: 12px;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.3s;
}

.action-btn.primary {
  background: #2563eb;
  color: #ffffff;
}

.action-btn.primary:hover {
  background: #1d4ed8;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.action-btn.danger {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.action-btn.danger:hover {
  background: #dc2626;
  color: #ffffff;
}

/* FAQ 展开动画 */
.faq-expand-enter-active,
.faq-expand-leave-active {
  transition: all 0.3s ease;
  max-height: 500px;
  overflow: hidden;
}

.faq-expand-enter-from,
.faq-expand-leave-to {
  max-height: 0;
  opacity: 0;
  padding-top: 0;
  padding-bottom: 0;
}

/* 无搜索结果 */
.no-result {
  text-align: center;
  padding: 60px 20px;
}

.no-result-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.no-result-text {
  font-size: 18px;
  color: #1f2a56;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.no-result-hint {
  font-size: 14px;
  color: #8c8fa3;
  margin: 0;
}

/* 底部反馈区 */
.feedback-section {
  margin-top: 48px;
}

.feedback-card {
  background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);
  border-radius: 16px;
  padding: 36px;
  color: #ffffff;
}

.feedback-card h3 {
  font-size: 20px;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.feedback-card > p {
  font-size: 14px;
  opacity: 0.85;
  margin: 0 0 28px 0;
}

.feedback-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.feedback-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 18px;
  backdrop-filter: blur(10px);
}

.feedback-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.feedback-label {
  font-size: 12px;
  opacity: 0.75;
  margin-bottom: 4px;
}

.feedback-value {
  font-size: 15px;
  font-weight: 500;
}

/* 响应式 */
@media (max-width: 768px) {
  .help-center-page {
    padding: 24px 16px;
  }

  .quick-links {
    grid-template-columns: repeat(3, 1fr);
  }

  .feedback-actions {
    grid-template-columns: 1fr;
  }

  .page-header h2 {
    font-size: 22px;
  }
}

@media (max-width: 480px) {
  .quick-links {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
