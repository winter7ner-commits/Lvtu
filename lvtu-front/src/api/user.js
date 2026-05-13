// src/api/user.js
import request from './request'

export const getUserInfo = (userId) => {
    // 使用 params 传递 userId
    return request.get('/user/info', { params: { userId } })
}

export const getUserVerification = (userId) => {
    return request.get('/user/verification', { params: { userId } })
}
