import request from '@/utils/request'

export function loadAllNotice(params: any) {
  return request.get('/notice/loadAllNotice', { params })
}

export function loadNoticeById(id: number) {
  return request.get('/notice/loadNoticeById', { params: { id } })
}

export function addNotice(data: any) {
  return request.post('/notice/addNotice', data)
}

export function updateNotice(data: any) {
  return request.post('/notice/updateNotice', data)
}

export function deleteNotice(id: number) {
  return request.post('/notice/deleteNotice', null, { params: { id } })
}

export function batchDeleteNotice(ids: number[]) {
  return request.post('/notice/batchDeleteNotice', null, { params: { ids } })
}
