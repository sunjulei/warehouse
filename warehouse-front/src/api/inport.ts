import request from '@/utils/request'

export function loadAllInport(params: any) {
  return request.get('/inport/loadAllInport', { params })
}

export function addInport(data: any) {
  return request.post('/inport/addInport', data)
}

export function updateInport(data: any) {
  return request.post('/inport/updateInport', data)
}

export function deleteInport(id: number) {
  return request.post('/inport/deleteInport', null, { params: { id } })
}
