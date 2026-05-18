import request from './request'

export const getPendingSettlements = () => {
  return request.get('/api/settlements/pending').then((res) => res.data)
}

export const paySettlement = (settlementId) => {
  return request.put(`/api/settlements/${settlementId}/pay`).then((res) => res.data)
}
