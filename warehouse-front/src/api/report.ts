import request from '@/utils/request'

export function inportAnalysis(type: string, startDate: string, endDate: string) {
  return request.get('/report/inportAnalysis', { params: { type, startDate, endDate } })
}

export function salesAnalysis(type: string, startDate: string, endDate: string) {
  return request.get('/report/salesAnalysis', { params: { type, startDate, endDate } })
}

export function inportGoodsAnalysis(type: string, startDate: string, endDate: string) {
  return request.get('/report/inportGoodsAnalysis', { params: { type, startDate, endDate } })
}

export function salesGoodsAnalysis(type: string, startDate: string, endDate: string) {
  return request.get('/report/salesGoodsAnalysis', { params: { type, startDate, endDate } })
}

export function profitAnalysis(type: string, startDate: string, endDate: string) {
  return request.get('/report/profitAnalysis', { params: { type, startDate, endDate } })
}
