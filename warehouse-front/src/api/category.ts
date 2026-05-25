import request from '@/utils/request'

export function loadAllCategory(params: any) {
  return request.get('/category/loadAllCategory', { params })
}

export function addCategory(data: any) {
  return request.post('/category/addCategory', data)
}

export function updateCategory(data: any) {
  return request.post('/category/updateCategory', data)
}

export function deleteCategory(id: number) {
  return request.post('/category/deleteCategory', null, { params: { id } })
}

export function loadAllCategoryForSelect() {
  return request.get('/category/loadAllCategoryForSelect')
}
