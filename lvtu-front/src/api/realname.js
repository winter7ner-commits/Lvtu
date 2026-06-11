import request from '@/utils/request'

// 提交实名认证申请
export function submitRealNameApplication(data) {
  return request({
    url: '/api/user/realname/submit',
    method: 'post',
    data
  })
}

// 上传身份证照片
export function uploadIdCardImage(file) {
  const formData = new FormData()
  formData.append('file', file)

  return request({
    url: '/api/user/upload/idcard',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取我的实名认证申请
export function getMyRealNameApplication(userId) {
  return request({
    url: `/api/user/realname/${userId}`,
    method: 'get'
  })
}
