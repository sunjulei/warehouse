import request from '@/utils/request'

export function loadAllCustomer(params: any) {
  return request.get('/customer/loadAllCustomer', { params })
}

export function addCustomer(data: any) {
  return request.post('/customer/addCustomer', data)
}

export function updateCustomer(data: any) {
  return request.post('/customer/updateCustomer', data)
}

export function deleteCustomer(id: number) {
  return request.delete('/customer/deleteCustomer', { params: { id } })
}

export function loadAllCustomerForSelect() {
  return request.get('/customer/loadAllCustomerForSelect')
}
