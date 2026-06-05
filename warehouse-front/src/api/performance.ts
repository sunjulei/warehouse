import request from '@/utils/request'

export function salesRanking(params: any) {
  return request.get('/performance/salesRanking', { params })
}

export function goodsRanking(params: any) {
  return request.get('/performance/goodsRanking', { params })
}
