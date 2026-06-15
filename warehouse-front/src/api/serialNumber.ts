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
