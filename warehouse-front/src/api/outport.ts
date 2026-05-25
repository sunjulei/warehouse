import request from '@/utils/request'

export function loadAllOutport(params: any) {
  return request.get('/outport/loadAllOutport', { params })
}

export function addOutport(id: number, number: number, remark: string) {
  return request.post('/outport/addOutport', null, { params: { id, number, remark } })
}

export function deleteOutport(id: number) {
  return request.post('/outport/deleteOutport', null, { params: { id } })
}

export function cancelOutport(id: number) {
  return request.post('/outport/cancelOutport', null, { params: { id } })
}
