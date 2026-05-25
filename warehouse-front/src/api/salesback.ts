import request from '@/utils/request'

export function loadAllSalesback(params: any) {
  return request.get('/salesback/loadAllSalesback', { params })
}

export function addSalesback(id: number, number: number, remark: string) {
  return request.post('/salesback/addSalesback', null, { params: { id, number, remark } })
}

export function deleteSalesback(id: number) {
  return request.post('/salesback/deleteSalesback', null, { params: { id } })
}

export function cancelSalesback(id: number) {
  return request.post('/salesback/cancelSalesback', null, { params: { id } })
}
