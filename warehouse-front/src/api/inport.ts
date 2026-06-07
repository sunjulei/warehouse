import request from '@/utils/request'

export function loadAllInport(params: any) {
  return request.get('/inport/loadAllInport', { params })
}

export function addInport(data: any) {
  return request.post('/inport/addInport', data)
}

export function batchAddInport(data: any[]) {
  return request.post('/inport/batchAddInport', data)
}

export function updateInport(data: any) {
  return request.post('/inport/updateInport', data)
}

export function deleteInport(id: number) {
  return request.post('/inport/deleteInport', null, { params: { id } })
}

// 订单相关API
export function loadAllOrders(params: any) {
  return request.get('/inport/loadAllOrders', { params })
}

export function loadOrderDetail(orderNo: string) {
  return request.get('/inport/loadOrderDetail', { params: { orderNo } })
}

export function returnSingleGoods(inportId: number, returnNumber?: number) {
  return request.post('/inport/returnSingleGoods', null, { params: { inportId, returnNumber } })
}

export function returnOrder(orderNo: string) {
  return request.post('/inport/returnOrder', null, { params: { orderNo } })
}

export function addToOrder(data: any[]) {
  return request.post('/inport/addToOrder', data)
}

export function loadReturnAddRecords(params: any) {
  return request.get('/inport/loadReturnAddRecords', { params })
}
