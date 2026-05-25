import request from '@/utils/request'

export function loadAllProvider(params: any) {
  return request.get('/provider/loadAllProvider', { params })
}

export function addProvider(data: any) {
  return request.post('/provider/addProvider', data)
}

export function updateProvider(data: any) {
  return request.post('/provider/updateProvider', data)
}

export function deleteProvider(id: number) {
  return request.post('/provider/deleteProvider', null, { params: { id } })
}

export function loadAllProviderForSelect() {
  return request.get('/provider/loadAllProviderForSelect')
}
