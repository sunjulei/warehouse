import request from '@/utils/request'

export function loadAllSales(params: any) {
  return request.get('/sales/loadAllSales', { params })
}

export function addSales(data: any) {
  return request.post('/sales/addSales', data)
}

export function updateSales(data: any) {
  return request.post('/sales/updateSales', data)
}

export function deleteSales(id: number) {
  return request.post('/sales/deleteSales', null, { params: { id } })
}
