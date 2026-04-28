import request from '@/utils/request'

// 获取列表
export function getLawyerList(params) {
  return request({
    url: '/lawyer/list',
    method: 'get',
    params
  })
}

// 获取详情
export function getLawyerDetail(id) {
  return request({
    url: `/lawyer/${id}`,
    method: 'get'
  })
}

// 获取法律领域列表
export function getSpecialtyList() {
  return request({
    url: '/specialty/list',
    method: 'get'
  })
}

// 获取评分最高的律师（热门推荐）
export function getTopRatedLawyers(limit = 3) {
  return request({
    url: '/lawyer/top-rated',
    method: 'get',
    params: { limit }
  })
}