import request from '@/utils/request'

export function batchAddSales(data: any[]) {
  return request.post('/sales/batchAddSales', data)
}

export function updateSales(data: any) {
  return request.post('/sales/updateSales', data)
}

// 订单相关API
export function loadAllOrders(params: any) {
  return request.get('/sales/loadAllOrders', { params })
}

export function loadOrderDetail(orderNo: string) {
  return request.get('/sales/loadOrderDetail', { params: { orderNo } })
}

export function returnSingleGoods(salesId: number, returnNumber?: number) {
  return request.post('/sales/returnSingleGoods', null, { params: { salesId, returnNumber } })
}

export function returnOrder(orderNo: string) {
  return request.post('/sales/returnOrder', null, { params: { orderNo } })
}

export function addToOrder(data: any[]) {
  return request.post('/sales/addToOrder', data)
}

export function loadReturnAddRecords(params: any) {
  return request.get('/sales/loadReturnAddRecords', { params })
}
