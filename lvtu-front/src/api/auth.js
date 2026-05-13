import request from '@/utils/request'

/**
 * 用户登录 - 从数据库验证用户名和密码
 * 对应后端：
 * POST /api/users/login
 */
export const login = (username, password) => {
  return request({
    method: 'POST',
    url: '/users/login',
    data: {
      username,
      password
    }
  })
}

/**
 * 获取当前用户信息
 * 对应后端：
 * GET /api/users/me
 */
export const getCurrentUser = () => {
  return request({
    method: 'GET',
    url: '/users/me'
  })
}

/**
 * 用户注册
 * 对应后端：
 * POST /api/users/register
 */
export const register = (userData) => {
  return request({
    method: 'POST',
    url: '/users/register',
    data: userData
  })
}

/**
 * 用户退出登录
 * 对应后端：
 * POST /api/users/logout
 */
export const logout = () => {
  return request({
    method: 'POST',
    url: '/users/logout'
  })
}
