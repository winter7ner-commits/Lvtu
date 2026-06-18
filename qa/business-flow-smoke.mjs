import assert from 'node:assert/strict'

const config = {
  baseUrl: env('LVTU_API_BASE', 'http://localhost:8080').replace(/\/+$/, ''),
  password: env('LVTU_TEST_PASSWORD', 'Test123456'),
  userAccount: env('LVTU_TEST_USER', 'user04'),
  lawyerAccount: env('LVTU_TEST_LAWYER', 'lawyA'),
  adminAccount: env('LVTU_TEST_ADMIN', 'admin_super'),
  customerUserId: Number(env('LVTU_TEST_USER_ID', '500001')),
  lawyerUserId: Number(env('LVTU_TEST_LAWYER_USER_ID', '500003')),
  lawyerId: Number(env('LVTU_TEST_LAWYER_ID', '700001'))
}

const state = {
  userToken: null,
  lawyerToken: null,
  adminToken: null,
  orderId: null,
  paymentId: null,
  resultId: null,
  evaluationId: null
}

const checks = [
  ['公开基础数据', checkPublicCatalog],
  ['登录与权限身份', checkLogin],
  ['用户资料与实名认证读取', checkUserProfileAndVerification],
  ['发单与支付', checkOrderAndPayment],
  ['律师接单与提交结果', checkLawyerOrderFlow],
  ['用户申请修改与律师重提', checkRevisionFlow],
  ['用户确认、评价与结算入口', checkConfirmationEvaluationAndSettlement],
  ['通知、后台订单与治理入口', checkNotificationsAndAdmin]
]

for (const [name, fn] of checks) {
  await step(name, fn)
}

console.log('\nAll Lvtu business-flow smoke checks passed.')
console.log(`Created smoke-test order: ${state.orderId}`)

async function checkPublicCatalog() {
  const categories = await api('GET', '/api/categories')
  assert.ok(Array.isArray(categories), '法律分类应返回数组')

  const documents = await api('GET', '/api/documents')
  assert.ok(Array.isArray(documents), '法律文件应返回数组')

  const articles = await api('GET', '/api/articles')
  assert.ok(Array.isArray(articles), '法条列表应返回数组')

  const lawyers = await rawJson('GET', '/lawyer/list')
  assert.ok(Array.isArray(lawyers), '律师列表应返回数组')

  const topRated = await api('GET', '/lawyer/top-rated?limit=3')
  assert.ok(Array.isArray(topRated), '高评分律师应返回数组')

  const regions = await api('GET', '/origin/tree')
  assert.ok(Array.isArray(regions), '地区树应返回数组')
}

async function checkLogin() {
  const userLogin = await api('POST', '/api/auth/login', {
    username: config.userAccount,
    password: config.password
  })
  state.userToken = requiredToken(userLogin, '用户登录')
  assert.equal(Number(userLogin.user.userId), config.customerUserId, '用户账号与测试用户ID不匹配')

  const lawyerLogin = await api('POST', '/api/auth/login', {
    username: config.lawyerAccount,
    password: config.password
  })
  state.lawyerToken = requiredToken(lawyerLogin, '律师登录')
  assert.equal(Number(lawyerLogin.user.userId), config.lawyerUserId, '律师账号与测试律师用户ID不匹配')

  const adminLogin = await api('POST', '/api/auth/admin/login', {
    username: config.adminAccount,
    password: config.password
  })
  state.adminToken = requiredToken(adminLogin, '后台登录')
  assert.equal(adminLogin.user.userType, 3, '后台账号必须是管理员')

  const me = await api('GET', '/api/auth/me', null, userHeaders())
  assert.equal(Number(me.userId), config.customerUserId, 'me 接口应返回当前用户')
}

async function checkUserProfileAndVerification() {
  const profile = await api('GET', `/api/user/info?userId=${config.customerUserId}`)
  assert.equal(Number(profile.userId), config.customerUserId, '用户资料应可读取')
  assert.equal(profile.isVerified, true, '测试用户应已实名认证')

  const verification = await api('GET', `/api/user/verification/${config.customerUserId}`)
  assert.equal(Number(verification.userId), config.customerUserId, '实名认证记录应可读取')

  const updated = await api('PUT', '/api/user/profile', {
    phone: profile.phone,
    email: profile.email,
    region: profile.region || '上海'
  }, userHeaders())
  assert.equal(Number(updated.userId), config.customerUserId, '用户资料更新应返回当前用户')

  const lawyerProfile = await api('GET', `/api/lawyer/orders/profile/by-user/${config.lawyerUserId}`)
  assert.equal(Number(lawyerProfile.lawyerId), config.lawyerId, '律师用户应能查到律师资料')
}

async function checkOrderAndPayment() {
  const order = await api('POST', '/api/orders', {
    userId: config.customerUserId,
    assignmentType: 'PUBLIC',
    serviceTypeId: 101,
    totalAmount: 9.99,
    formData: {
      contactName: '自动化测试',
      contactPhone: '13800000004',
      consultMode: '在线咨询',
      summary: `业务闭环自动化测试 ${new Date().toISOString()}`
    }
  }, userHeaders())

  state.orderId = Number(order.orderId)
  assert.ok(state.orderId, '创建订单应返回 orderId')
  assert.equal(order.status, '待支付', '新订单应为待支付')

  const payment = await api('GET', `/api/payments/order/${state.orderId}`)
  state.paymentId = Number(payment.paymentId)
  assert.ok(state.paymentId, '创建订单后应自动生成支付记录')
  assert.equal(payment.status, '未支付', '初始支付记录应未支付')

  const paid = await api('PUT', `/api/payments/${state.paymentId}/status?status=${encodeURIComponent('已支付')}`)
  assert.equal(paid, true, '支付状态更新应成功')

  const paidOrder = await api('GET', `/api/orders/${state.orderId}`)
  assert.equal(paidOrder.status, '待接单', '支付成功后订单应进入待接单')
}

async function checkLawyerOrderFlow() {
  const available = await api('GET', '/api/lawyer/orders/available')
  assert.ok(available.some((item) => Number(item.orderId) === state.orderId), '支付后的公共订单应进入接单大厅')

  await api('POST', `/api/lawyer/orders/${state.orderId}/accept`, {
    lawyerId: config.lawyerId
  }, lawyerHeaders())

  const acceptedOrder = await api('GET', `/api/orders/${state.orderId}`)
  assert.equal(acceptedOrder.status, '处理中', '律师接单后订单应进入处理中')
  assert.equal(Number(acceptedOrder.lawyerId), config.lawyerId, '订单应记录接单律师')

  await api('POST', `/api/lawyer/orders/${state.orderId}/result`, {
    lawyerId: config.lawyerId,
    title: '自动化测试服务结果',
    content: '律师已完成自动化业务流程的初次服务结果提交。',
    attachments: []
  }, lawyerHeaders())

  const result = await api('GET', `/api/orders/${state.orderId}/result?userId=${config.customerUserId}`, null, userHeaders())
  state.resultId = Number(result.id)
  assert.ok(state.resultId, '提交结果后用户应能查看服务结果')
  assert.equal(result.status, 0, '首次提交的服务结果状态应为已提交')

  const pendingConfirmOrder = await api('GET', `/api/orders/${state.orderId}`)
  assert.equal(pendingConfirmOrder.status, '待客户确认', '提交服务结果后订单应待客户确认')
}

async function checkRevisionFlow() {
  const revised = await api('POST', `/api/orders/${state.orderId}/revision`, {
    userId: config.customerUserId,
    content: '请补充说明处理依据和后续注意事项。'
  }, userHeaders())
  assert.equal(Number(revised.orderId), state.orderId, '申请修改应返回服务结果')
  assert.equal(revised.status, 2, '申请修改后服务结果应标记为需修改')

  const processingOrder = await api('GET', `/api/orders/${state.orderId}`)
  assert.equal(processingOrder.status, '处理中', '未达到修改上限时订单应回到处理中')

  await api('PUT', `/api/lawyer/results/${state.resultId}`, {
    lawyerId: config.lawyerId,
    title: '自动化测试服务结果（已补充）',
    content: '已补充处理依据、风险提示和后续注意事项。',
    attachments: []
  }, lawyerHeaders())

  const resubmitted = await api('GET', `/api/orders/${state.orderId}/result?userId=${config.customerUserId}`, null, userHeaders())
  assert.equal(resubmitted.status, 0, '律师重提后服务结果应恢复为已提交')
  assert.ok(resubmitted.submissions.length >= 2, '重新提交后应保留提交历史')

  const pendingConfirmOrder = await api('GET', `/api/orders/${state.orderId}`)
  assert.equal(pendingConfirmOrder.status, '待客户确认', '重新提交后订单应待客户确认')
}

async function checkConfirmationEvaluationAndSettlement() {
  await api('POST', `/api/orders/${state.orderId}/confirm`, {
    userId: config.customerUserId
  }, userHeaders())

  const pendingReviewOrder = await api('GET', `/api/orders/${state.orderId}`)
  assert.equal(pendingReviewOrder.status, '待评价', '用户确认后订单应进入待评价')

  const evaluation = await rawJson('POST', '/api/evaluations', {
    orderId: state.orderId,
    lawyerId: config.lawyerId,
    professionalScore: 5,
    responsivenessScore: 5,
    attitudeScore: 5,
    content: '自动化测试评价：流程清晰，响应及时。',
    isAnonymous: false
  }, userHeaders())
  state.evaluationId = Number(evaluation.evaluationId)
  assert.ok(state.evaluationId, '评价成功应返回 evaluationId')
  assert.equal(Number(evaluation.userId), config.customerUserId, '评价用户应来自登录 token')

  const completedOrder = await api('GET', `/api/orders/${state.orderId}`)
  assert.equal(completedOrder.status, '已完成', '评价后订单应完成')

  const settlements = await api('GET', '/api/settlements/pending', null, adminHeaders())
  assert.ok(settlements.some((item) => Number(item.orderId) === state.orderId), '用户确认后应生成待结算记录')
}

async function checkNotificationsAndAdmin() {
  const userNotifications = await api('GET', `/api/notifications?userId=${config.customerUserId}`, null, userHeaders())
  assert.ok(Array.isArray(userNotifications), '用户消息列表应返回数组')

  const unread = await api('GET', `/api/notifications/unread-count?userId=${config.customerUserId}`, null, userHeaders())
  assert.equal(typeof unread, 'number', '未读消息数应返回数字')

  const orders = await api('GET', '/api/admin/orders', null, adminHeaders())
  assert.ok(orders.some((item) => Number(item.order_id) === state.orderId), '后台订单列表应包含自动化测试订单')

  const evaluations = await rawJson('GET', `/api/evaluations/lawyers/${config.lawyerId}?page=1&size=10`)
  assert.ok(Array.isArray(evaluations.list), '律师评价列表应返回分页数据')

  const logs = await rawJson('GET', `/api/admin/evaluations/${state.evaluationId}/logs`, null, adminHeaders())
  assert.ok(Array.isArray(logs), '后台评价日志应返回数组')

  const revisionLimit = await api('GET', '/api/admin/settings/revision-limit', null, adminHeaders())
  assert.ok(Number(revisionLimit.maxRevisionRequestCount) >= 0, '修改次数配置应可读取')
}

async function step(name, fn) {
  process.stdout.write(`\n[TEST] ${name} ... `)
  await fn()
  console.log('ok')
}

async function api(method, route, body, headers = {}) {
  const json = await rawJson(method, route, body, headers)
  assert.equal(json.code, 200, `${method} ${route} failed: ${JSON.stringify(json)}`)
  return json.data
}

async function rawJson(method, route, body, headers = {}) {
  const response = await fetch(`${config.baseUrl}${route}`, {
    method,
    headers: {
      ...(body ? { 'Content-Type': 'application/json' } : {}),
      ...headers
    },
    body: body ? JSON.stringify(body) : undefined
  })
  const text = await response.text()
  let json = null
  try {
    json = text ? JSON.parse(text) : null
  } catch (error) {
    throw new Error(`${method} ${route} returned non-JSON response (${response.status}): ${text.slice(0, 500)}`)
  }
  assert.ok(response.ok, `${method} ${route} HTTP ${response.status}: ${text}`)
  return json
}

function requiredToken(loginData, label) {
  assert.ok(loginData.token, `${label} 应返回 token`)
  return loginData.token
}

function userHeaders() {
  return authHeaders(state.userToken)
}

function lawyerHeaders() {
  return authHeaders(state.lawyerToken)
}

function adminHeaders() {
  return authHeaders(state.adminToken)
}

function authHeaders(token) {
  return { Authorization: `Bearer ${token}` }
}

function env(name, fallback) {
  return process.env[name] || fallback
}
