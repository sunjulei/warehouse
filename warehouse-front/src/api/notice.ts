import request from '@/utils/request'
import type { DataGridView, ResultObj } from '@/types/api'
import type { Notice } from '@/types/notice'

export interface NoticeSearchParams {
  page?: number
  limit?: number
  title?: string
  opername?: string
  startTime?: string
  endTime?: string
}

export function loadAllNotice(params: NoticeSearchParams): Promise<DataGridView<Notice>> {
  return request.get('/notice/loadAllNotice', { params })
}

export function loadNoticeById(id: number): Promise<DataGridView<Notice>> {
  return request.get('/notice/loadNoticeById', { params: { id } })
}

export function addNotice(data: Partial<Notice>): Promise<ResultObj> {
  return request.post('/notice/addNotice', data)
}

export function updateNotice(data: Partial<Notice>): Promise<ResultObj> {
  return request.post('/notice/updateNotice', data)
}

export function deleteNotice(id: number): Promise<ResultObj> {
  return request.post('/notice/deleteNotice', null, { params: { id } })
}

export function batchDeleteNotice(ids: number[]): Promise<ResultObj> {
  return request.post('/notice/batchDeleteNotice', null, { params: { ids } })
}
