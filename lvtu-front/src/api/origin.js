import request from './request'

export function getRegionTree() {
  return request.get('/origin/tree')
}
