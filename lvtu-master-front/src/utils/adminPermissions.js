export const ADMIN_ROLES = {
  SUPER_ADMIN: 'SUPER_ADMIN',
  CERT_AUDITOR: 'CERT_AUDITOR',
  OPERATOR: 'OPERATOR',
  CUSTOMER_SERVICE: 'CUSTOMER_SERVICE'
}

export const ADMIN_ROLE_LABELS = {
  [ADMIN_ROLES.SUPER_ADMIN]: '超级管理员',
  [ADMIN_ROLES.CERT_AUDITOR]: '认证审核员',
  [ADMIN_ROLES.OPERATOR]: '运营管理员',
  [ADMIN_ROLES.CUSTOMER_SERVICE]: '客服专员'
}

export const ADMIN_ROLE_OPTIONS = [
  { label: ADMIN_ROLE_LABELS[ADMIN_ROLES.SUPER_ADMIN], value: ADMIN_ROLES.SUPER_ADMIN },
  { label: ADMIN_ROLE_LABELS[ADMIN_ROLES.CERT_AUDITOR], value: ADMIN_ROLES.CERT_AUDITOR },
  { label: ADMIN_ROLE_LABELS[ADMIN_ROLES.OPERATOR], value: ADMIN_ROLES.OPERATOR },
  { label: ADMIN_ROLE_LABELS[ADMIN_ROLES.CUSTOMER_SERVICE], value: ADMIN_ROLES.CUSTOMER_SERVICE }
]

const ADMIN_ROLE_ALIASES = {
  SUPER_ADMIN: ADMIN_ROLES.SUPER_ADMIN,
  ADMIN_SUPER: ADMIN_ROLES.SUPER_ADMIN,
  CERT_AUDITOR: ADMIN_ROLES.CERT_AUDITOR,
  ADMIN_AUDIT: ADMIN_ROLES.CERT_AUDITOR,
  OPERATOR: ADMIN_ROLES.OPERATOR,
  ADMIN_OPERATOR: ADMIN_ROLES.OPERATOR,
  CUSTOMER_SERVICE: ADMIN_ROLES.CUSTOMER_SERVICE,
  ADMIN_SERVICE: ADMIN_ROLES.CUSTOMER_SERVICE
}

export const ADMIN_MENU_ITEMS = [
  { label: '用户管理', path: '/users', roles: [ADMIN_ROLES.SUPER_ADMIN] },
  { label: '法条管理', path: '/law-management', roles: [ADMIN_ROLES.SUPER_ADMIN, ADMIN_ROLES.OPERATOR] },
  { label: '法条浏览', path: '/law-browse', roles: [ADMIN_ROLES.SUPER_ADMIN, ADMIN_ROLES.OPERATOR] },
  { label: '解释反馈', path: '/article-feedback', roles: [ADMIN_ROLES.SUPER_ADMIN, ADMIN_ROLES.OPERATOR] },
  { label: '认证审核', path: '/auth-audit', roles: [ADMIN_ROLES.CERT_AUDITOR] },
  { label: '订单查询', path: '/orders', roles: [ADMIN_ROLES.SUPER_ADMIN, ADMIN_ROLES.CUSTOMER_SERVICE] },
  { label: '结算管理', path: '/settlements', roles: [ADMIN_ROLES.SUPER_ADMIN] },
  { label: '平台介入', path: '/interventions', roles: [ADMIN_ROLES.SUPER_ADMIN, ADMIN_ROLES.CUSTOMER_SERVICE] },
  { label: '评价管理', path: '/evaluations', roles: [ADMIN_ROLES.SUPER_ADMIN, ADMIN_ROLES.OPERATOR] },
  { label: '角色管理', path: '/roles', roles: [ADMIN_ROLES.SUPER_ADMIN] },
  { label: '系统设置', path: '/settings', roles: [ADMIN_ROLES.SUPER_ADMIN] }
]

export const normalizeRoleValue = (role) => {
  if (!role) return ''
  const key = String(role).trim().replace(/[\s-]+/g, '_').toUpperCase()
  return ADMIN_ROLE_ALIASES[key] || key
}

export const normalizeAdminRole = (user) => {
  if (!user || Number(user.userType ?? user.user_type) !== 3) return ''
  return normalizeRoleValue(user.adminRole ?? user.admin_role) || ADMIN_ROLES.SUPER_ADMIN
}

export const canAccessAdminRoute = (user, roles = []) => {
  if (!user || Number(user.userType ?? user.user_type) !== 3) return false
  if (!roles.length) return true
  const role = normalizeAdminRole(user)
  return roles.map(normalizeRoleValue).includes(role)
}

export const firstAdminPath = (user) => {
  const item = ADMIN_MENU_ITEMS.find((menu) => canAccessAdminRoute(user, menu.roles))
  return item?.path || '/admin/login'
}
