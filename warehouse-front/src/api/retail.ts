import request from '@/utils/request'

export function loadAllRetail(params: any) {
  return request.get('/retail/loadAllRetail', { params })
}

export function addRetail(data: any) {
  return request.post('/retail/addRetail', data)
}

export function updateRetail(data: any) {
  return request.post('/retail/updateRetail', data)
}

export function deleteRetail(id: number) {
  return request.post('/retail/deleteRetail', null, { params: { id } })
}

export function batchAddRetail(data: any[]) {
  return request.post('/retail/batchAddRetail', data, {
    headers: { 'Content-Type': 'application/json' }
  })
}

export function loadAllOrders(params: any) {
  return request.get('/retail/loadAllOrders', { params })
}

export function loadOrderDetail(orderNo: string) {
  return request.get('/retail/loadOrderDetail', { params: { orderNo } })
}

export function returnSingleGoods(retailId: number, returnNumber?: number) {
  return request.post('/retail/returnSingleGoods', null, { params: { retailId, returnNumber } })
}

export function returnOrder(orderNo: string) {
  return request.post('/retail/returnOrder', null, { params: { orderNo } })
}

export function addToOrder(data: any[]) {
  return request.post('/retail/addToOrder', data, {
    headers: { 'Content-Type': 'application/json' }
  })
}

export function loadReturnAddRecords(params: any) {
  return request.get('/retail/loadReturnAddRecords', { params })
}
