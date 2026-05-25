import request from '@/utils/request'

export function loadAllLoginfo(params: any) {
  return request.get('/loginfo/loadAllLoginfo', { params })
}

export function deleteLoginfo(id: number) {
  return request.post('/loginfo/deleteLoginfo', null, { params: { id } })
}

export function batchDeleteLoginfo(ids: number[]) {
  return request.post('/loginfo/batchDeleteLoginfo', null, { params: { ids } })
}
