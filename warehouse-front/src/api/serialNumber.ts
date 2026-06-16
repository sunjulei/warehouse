import request from '@/utils/request'

export function loadAllSerialNumber(params: any) {
  return request.get('/serialNumber/loadAllSerialNumber', { params })
}

export function addSerialNumber(data: any) {
  return request.post('/serialNumber/addSerialNumber', data)
}

export function batchAddSerialNumber(data: any) {
  return request.post('/serialNumber/batchAddSerialNumber', data)
}

export function updateSerialNumber(data: any) {
  return request.post('/serialNumber/updateSerialNumber', data)
}

export function deleteSerialNumber(id: number) {
  return request.post('/serialNumber/deleteSerialNumber', null, { params: { id } })
}

/**
 * 获取商品可用序列号
 */
export function getAvailableSerialNumbers(goodsId: number) {
  return request.get('/serialNumber/getAvailableSerialNumbers', { params: { goodsId } })
}

/**
 * 批量入库序列号
 */
export function batchInportSerialNumbers(data: { goodsId: number, serialNumbers: string[], inportId: number }) {
  return request.post('/serialNumber/batchInport', data)
}
