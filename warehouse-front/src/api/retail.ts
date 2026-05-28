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
