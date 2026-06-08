import request from './request'

export const login = (data) => {
  return request.post('/api/auth/login', data).then((res) => res.data)
}

// export const adminLogin = (data) => {
//   return request.post('/api/auth/admin/login', data).then((res) => res.data)
// }

export const register = (data) => {
  return request.post('/api/auth/register', data).then((res) => res.data)
}
// export const adminRegister = (data) => {
//   return request.post('/api/auth/admin/register', data).then((res) => res.data)
// }
export const logout = () => {
  return request.post('/api/auth/logout').then((res) => res.data)
}

export const forgotPassword = (data) => {
  return request.post('/api/auth/forgot-password', data).then((res) => res.data)
}

export const changePassword = (data) => {
  return request.post('/api/auth/change-password', data).then((res) => res.data)
}

export const me = () => {
  return request.get('/api/auth/me').then((res) => res.data)
}

// 查询已注销账号
export const queryDeactivatedAccounts = (phone) => {
  return request.post('/api/auth/restore/query', { phone }).then(res => res.data)
}
// 检查手机号冲突
export const checkPhoneConflict = (phone) => {
  return request.post('/api/auth/restore/check-conflict', { phone }).then(res => res.data)
}
// 执行恢复账号
export const executeRestore = (data) => {
  return request.post('/api/auth/restore/execute', data).then(res => res.data)
}

