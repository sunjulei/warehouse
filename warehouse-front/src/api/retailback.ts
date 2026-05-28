import request from '@/utils/request'

export function loadAllRetailback(params: any) {
  return request.get('/retailback/loadAllRetailback', { params })
}

export function addRetailback(id: number, number: number, remark: string) {
  return request.post('/retailback/addRetailback', null, { params: { id, number, remark } })
}

export function cancelRetailback(id: number) {
  return request.post('/retailback/cancelRetailback', null, { params: { id } })
}
