export const ADMIN_ROLES = {
  SUPER_ADMIN: 'SUPER_ADMIN',
  CERT_AUDITOR: 'CERT_AUDITOR',
  OPERATOR: 'OPERATOR',
  CUSTOMER_SERVICE: 'CUSTOMER_SERVICE'
}

export const ADMIN_ROLE_LABELS = {
  SUPER_ADMIN: '超级管理员',
  CERT_AUDITOR: '认证审核员',
  OPERATOR: '运营管理员',
  CUSTOMER_SERVICE: '客服专员'
}

export const ADMIN_MENU_ITEMS = [
  { label: '用户管理', path: '/users', roles: ['SUPER_ADMIN', 'OPERATOR'] },
  { label: '法条管理', path: '/law-management', roles: ['SUPER_ADMIN', 'OPERATOR'] },
  { label: '解释反馈', path: '/article-feedback', roles: ['SUPER_ADMIN', 'OPERATOR'] },
  { label: '认证审核', path: '/auth-audit', roles: ['SUPER_ADMIN', 'CERT_AUDITOR'] },
  { label: '订单查询', path: '/orders', roles: ['SUPER_ADMIN', 'OPERATOR', 'CUSTOMER_SERVICE'] },
  { label: '结算管理', path: '/settlements', roles: ['SUPER_ADMIN'] },
  { label: '平台介入', path: '/interventions', roles: ['SUPER_ADMIN', 'CUSTOMER_SERVICE'] },
  { label: '评价管理', path: '/evaluations', roles: ['SUPER_ADMIN', 'OPERATOR'] },
  { label: '角色管理', path: '/roles', roles: ['SUPER_ADMIN'] },
  { label: '系统设置', path: '/settings', roles: ['SUPER_ADMIN'] }
]

export const normalizeAdminRole = (user) => {
  if (!user || Number(user.userType) !== 3) return ''
  return user.adminRole || 'SUPER_ADMIN'
}

export const canAccessAdminRoute = (user, roles = []) => {
  if (!user || Number(user.userType) !== 3) return false
  if (!roles.length) return true
  return roles.includes(normalizeAdminRole(user))
}

export const firstAdminPath = (user) => {
  const item = ADMIN_MENU_ITEMS.find((menu) => canAccessAdminRoute(user, menu.roles))
  return item?.path || '/admin/login'
}
