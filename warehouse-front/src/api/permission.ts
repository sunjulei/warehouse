import request from '@/utils/request'

export function loadPermissionManagerLeftTreeJson() {
  return request.get('/permission/loadPermissionManagerLeftTreeJson')
}

export function loadAllPermission(params: any) {
  return request.get('/permission/loadAllPermission', { params })
}

export function addPermission(data: any) {
  return request.post('/permission/addPermission', data)
}

export function updatePermission(data: any) {
  return request.post('/permission/updatePermission', data)
}

export function deletePermission(id: number) {
  return request.post('/permission/deletePermission', null, { params: { id } })
}

export function loadPermissionMaxOrderNum() {
  return request.get('/permission/loadPermissionMaxOrderNum')
}
