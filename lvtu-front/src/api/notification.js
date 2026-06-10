import request from '@/utils/request'

export function getNotifications(userId) {
  return request({
    url: '/api/notifications',
    method: 'get',
    params: { userId }
  })
}

export function getUnreadNotificationCount(userId) {
  return request({
    url: '/api/notifications/unread-count',
    method: 'get',
    params: { userId }
  })
}

export function markNotificationRead(id, userId) {
  return request({
    url: `/api/notifications/${id}/read`,
    method: 'put',
    params: { userId }
  })
}

export function markAllNotificationsRead(userId) {
  return request({
    url: '/api/notifications/read-all',
    method: 'put',
    params: { userId }
  })
}
