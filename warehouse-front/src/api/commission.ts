import request from '@/utils/request'

export function loadAllRules() {
  return request.get('/commission/loadAllRules')
}

export function saveRule(data: any) {
  return request.post('/commission/saveRule', data)
}

export function deleteRule(data: any) {
  return request.post('/commission/deleteRule', data)
}

export function saveTiers(data: any) {
  return request.post('/commission/saveTiers', JSON.stringify(data), { headers: { 'Content-Type': 'application/json' } })
}

export function calculate(data: any) {
  return request.post('/commission/calculate', data)
}

export function loadRecords(params: any) {
  return request.get('/commission/loadRecords', { params })
}

export function confirmRecord(data: any) {
  return request.post('/commission/confirmRecord', data)
}

export function loadMyCommission(params: any) {
  return request.get('/commission/loadMyCommission', { params })
}
