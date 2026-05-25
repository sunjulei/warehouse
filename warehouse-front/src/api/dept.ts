import request from '@/utils/request'

export function loadDeptManagerLeftTreeJson() {
  return request.get('/dept/loadDeptManagerLeftTreeJson')
}

export function loadAllDept(params: any) {
  return request.get('/dept/loadAllDept', { params })
}

export function addDept(data: any) {
  return request.post('/dept/addDept', data)
}

export function updateDept(data: any) {
  return request.post('/dept/updateDept', data)
}

export function deleteDept(id: number) {
  return request.post('/dept/deleteDept', null, { params: { id } })
}

export function loadDeptMaxOrderNum() {
  return request.get('/dept/loadDeptMaxOrderNum')
}

export function checkDeptHasChildrenNode(id: number) {
  return request.get('/dept/checkDeptHasChildrenNode', { params: { id } })
}
