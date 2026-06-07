import request from '@/utils/request'

export function loadAllGoods(params: any) {
  return request.get('/goods/loadAllGoods', { params })
}

export function addGoods(data: any) {
  return request.post('/goods/addGoods', data)
}

export function updateGoods(data: any) {
  return request.post('/goods/updateGoods', data)
}

export function deleteGoods(id: number, goodsimg: string) {
  return request.post('/goods/deleteGoods', null, { params: { id, goodsimg } })
}

export function loadAllGoodsForSelect() {
  return request.get('/goods/loadAllGoodsForSelect')
}

export function loadGoodsForPOS(params: { page?: number; limit?: number; keyword?: string }) {
  return request.get('/goods/loadGoodsForPOS', { params })
}

export function loadGoodsByProviderId(providerid: number, allStatus?: number) {
  return request.get('/goods/loadGoodsByProviderId', { params: { providerid, allStatus } })
}

export function updateGoodsAvailable(id: number, available: number) {
  return request.post('/goods/updateGoodsAvailable', null, { params: { id, available } })
}

export function loadAllWarningGoods() {
  return request.get('/goods/loadAllWarningGoods')
}

export function loadDashboardStats() {
  return request.get('/goods/loadDashboardStats')
}

export function loadRecentOperations() {
  return request.get('/goods/loadRecentOperations')
}
