import assert from 'node:assert/strict'
import { describe, it } from 'node:test'
import { readdir, readFile } from 'node:fs/promises'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const rootDir = path.resolve(__dirname, '..')
const controllerDir = path.join(rootDir, 'lvtu-server', 'src', 'main', 'java', 'com', 'bitzh', 'lvtu', 'controller')

const featureContracts = [
  ['账号注册', 'POST', '/api/auth/register'],
  ['用户登录', 'POST', '/api/auth/login'],
  ['后台登录', 'POST', '/api/auth/admin/login'],
  ['退出登录', 'POST', '/api/auth/logout'],
  ['修改密码', 'POST', '/api/auth/change-password'],
  ['忘记密码', 'POST', '/api/auth/forgot-password'],
  ['当前登录用户', 'GET', '/api/auth/me'],
  ['个人资料更新', 'PUT', '/api/user/profile'],
  ['账号注销', 'DELETE', '/api/user/deactivate'],
  ['取消账号注销', 'POST', '/api/user/deactivate/cancel'],
  ['实名认证查询', 'GET', '/api/user/verification/{userId}'],
  ['实名认证提交', 'POST', '/api/user/verification/submit'],
  ['实名认证后台列表', 'GET', '/api/admin/realname-verifications'],
  ['实名认证通过', 'POST', '/api/admin/realname-verifications/{verificationId}/approve'],
  ['实名认证驳回', 'POST', '/api/admin/realname-verifications/{verificationId}/reject'],
  ['律师认证提交', 'POST', '/application/submit'],
  ['我的律师认证', 'GET', '/application/my'],
  ['律师认证后台列表', 'GET', '/application/list'],
  ['律师认证通过', 'POST', '/application/approve'],
  ['律师认证驳回', 'POST', '/application/reject'],
  ['律师列表', 'GET', '/lawyer/list'],
  ['律师详情', 'GET', '/lawyer/{lawyerId}'],
  ['首页高评分律师', 'GET', '/lawyer/top-rated'],
  ['地区树', 'GET', '/origin/tree'],
  ['法律分类列表', 'GET', '/api/categories'],
  ['法律分类创建', 'POST', '/api/categories'],
  ['法律分类更新', 'PUT', '/api/categories/{id}'],
  ['法律分类删除', 'DELETE', '/api/categories/{id}'],
  ['法律文件列表', 'GET', '/api/documents'],
  ['按分类查文件', 'GET', '/api/documents/category/{categoryId}'],
  ['法律文件创建', 'POST', '/api/documents'],
  ['法律文件更新', 'PUT', '/api/documents/{id}'],
  ['法律文件删除', 'DELETE', '/api/documents/{id}'],
  ['法条列表', 'GET', '/api/articles'],
  ['法条详情', 'GET', '/api/articles/{id}/detail'],
  ['法条评论列表', 'GET', '/api/articles/{id}/comments'],
  ['法条发表评论', 'POST', '/api/articles/{id}/comments'],
  ['法条收藏切换', 'POST', '/api/articles/{id}/favorite'],
  ['法条解释反馈', 'POST', '/api/articles/{id}/explanation-feedback'],
  ['法条创建', 'POST', '/api/articles'],
  ['法条更新', 'PUT', '/api/articles/{id}'],
  ['法条删除', 'DELETE', '/api/articles/{id}'],
  ['创建订单', 'POST', '/api/orders'],
  ['订单详情', 'GET', '/api/orders/{id}'],
  ['用户订单列表', 'GET', '/api/orders/user/{userId}'],
  ['订单状态更新', 'PUT', '/api/orders/{id}/status'],
  ['创建支付', 'POST', '/api/payments'],
  ['支付详情', 'GET', '/api/payments/{id}'],
  ['订单支付记录', 'GET', '/api/payments/order/{orderId}'],
  ['支付状态更新', 'PUT', '/api/payments/{id}/status'],
  ['律师资料查询', 'GET', '/api/lawyer/orders/profile/by-user/{userId}'],
  ['接单大厅', 'GET', '/api/lawyer/orders/available'],
  ['律师订单详情', 'GET', '/api/lawyer/orders/{orderId}'],
  ['律师接单', 'POST', '/api/lawyer/orders/{orderId}/accept'],
  ['我的接单', 'GET', '/api/lawyer/orders/my'],
  ['提交服务结果', 'POST', '/api/lawyer/orders/{orderId}/result'],
  ['律师查看服务结果', 'GET', '/api/lawyer/orders/{orderId}/result'],
  ['重新提交服务结果', 'PUT', '/api/lawyer/results/{resultId}'],
  ['用户查看服务结果', 'GET', '/api/orders/{orderId}/result'],
  ['确认订单完成', 'POST', '/api/orders/{orderId}/confirm'],
  ['申请修改服务结果', 'POST', '/api/orders/{orderId}/revision'],
  ['用户评价', 'POST', '/api/evaluations'],
  ['评价更新', 'PUT', '/api/evaluations/{id}'],
  ['评价详情', 'GET', '/api/evaluations/{id}'],
  ['律师评价列表', 'GET', '/api/evaluations/lawyers/{lawyerId}'],
  ['评价举报', 'POST', '/api/reports'],
  ['评价举报列表', 'GET', '/api/reports/evaluations/{evaluationId}'],
  ['后台评价处理', 'POST', '/api/admin/evaluations/actions'],
  ['后台评价日志', 'GET', '/api/admin/evaluations/{evaluationId}/logs'],
  ['消息列表', 'GET', '/api/notifications'],
  ['未读消息数', 'GET', '/api/notifications/unread-count'],
  ['标记消息已读', 'PUT', '/api/notifications/{id}/read'],
  ['全部消息已读', 'PUT', '/api/notifications/read-all'],
  ['后台用户列表', 'GET', '/api/admin/users'],
  ['后台用户编辑', 'PUT', '/api/admin/users/{userId}'],
  ['后台用户状态', 'PUT', '/api/admin/users/{userId}/status'],
  ['后台用户角色', 'PUT', '/api/admin/users/{userId}/admin-role'],
  ['后台用户删除', 'DELETE', '/api/admin/users/{userId}'],
  ['后台订单列表', 'GET', '/api/admin/orders'],
  ['待结算列表', 'GET', '/api/settlements/pending'],
  ['支付结算', 'PUT', '/api/settlements/{settlementId}/pay'],
  ['平台介入列表', 'GET', '/api/admin/interventions'],
  ['平台介入日志', 'GET', '/api/admin/interventions/{orderId}/logs'],
  ['平台介入处理', 'POST', '/api/admin/interventions/{orderId}/actions'],
  ['修改次数配置读取', 'GET', '/api/admin/settings/revision-limit'],
  ['修改次数配置更新', 'PUT', '/api/admin/settings/revision-limit'],
  ['注销冷静期读取', 'GET', '/api/admin/settings/cancel-cooling-days'],
  ['注销冷静期更新', 'PUT', '/api/admin/settings/cancel-cooling-days'],
  ['解释反馈列表', 'GET', '/api/admin/legal-article-feedback'],
  ['解释反馈处理', 'PUT', '/api/admin/legal-article-feedback/{id}/handle']
]

describe('Lvtu API feature contract', async () => {
  const backendRoutes = await loadBackendRoutes()

  for (const [feature, method, route] of featureContracts) {
    it(`${feature}: ${method} ${route}`, () => {
      assert.ok(
        backendRoutes.has(routeKey(method, route)),
        `Missing backend route for ${feature}: ${method} ${route}\nKnown routes:\n${[...backendRoutes].sort().join('\n')}`
      )
    })
  }
})

async function loadBackendRoutes() {
  const files = await listJavaFiles(controllerDir)
  const routes = new Set()

  for (const file of files) {
    const source = await readFile(file, 'utf8')
    const basePath = firstAnnotationPath(source.match(/@RequestMapping\s*\(([^)]*)\)/s)?.[1] ?? '')

    const mappingPattern = /@(Get|Post|Put|Delete|Patch)Mapping\s*(?:\(([^)]*)\))?/gs
    let match
    while ((match = mappingPattern.exec(source)) !== null) {
      const method = match[1].toUpperCase()
      const childPath = firstAnnotationPath(match[2] ?? '')
      routes.add(routeKey(method, joinRoute(basePath, childPath)))
    }
  }

  return routes
}

async function listJavaFiles(dir) {
  const entries = await readdir(dir, { withFileTypes: true })
  const files = await Promise.all(entries.map((entry) => {
    const fullPath = path.join(dir, entry.name)
    return entry.isDirectory() ? listJavaFiles(fullPath) : fullPath.endsWith('.java') ? [fullPath] : []
  }))
  return files.flat()
}

function firstAnnotationPath(annotationArgs) {
  const match = annotationArgs.match(/"([^"]*)"/)
  return match?.[1] ?? ''
}

function joinRoute(base, child) {
  const parts = [base, child].filter(Boolean).map((part) => part.replace(/^\/+|\/+$/g, ''))
  return `/${parts.join('/')}`.replace(/\/+/g, '/')
}

function routeKey(method, route) {
  return `${method} ${route}`
}
