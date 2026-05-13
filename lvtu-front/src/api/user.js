import request from './request'

// 获取用户信息
export const getUserProfile = () => {
  return request.get('/api/auth/me').then((res) => res.data)
}

// 更新用户信息
export const updateUserProfile = (data) => {
  return request.put('/api/user/profile', data).then((res) => res.data)
}

// 注销用户
export const deactivateUser = () => {
  return request.delete('/api/user/deactivate').then((res) => res.data)
}


// 上传头像
export const uploadAvatar = (file) => {
  const formData = new FormData()
  formData.append('avatar', file) // 这里的 'avatar' 必须和后端 @RequestParam("avatar") 一致
  return request({
    url: '/api/user/avatar',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: formData
  })
}