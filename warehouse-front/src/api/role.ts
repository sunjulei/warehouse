import request from '@/utils/request'

export function loadAllRole(params: any) {
  return request.get('/role/loadAllRole', { params })
}

export function addRole(data: any) {
  return request.post('/role/addRole', data)
}

export function updateRole(data: any) {
  return request.post('/role/updateRole', data)
}

export function deleteRole(id: number) {
  return request.post('/role/deleteRole', null, { params: { id } })
}

export function initPermissionByRoleId(roleId: number) {
  return request.get('/role/initPermissionByRoleId', { params: { roleId } })
}

export function saveRolePermission(rid: number, ids: number[]) {
  return request.post('/role/saveRolePermission', null, { params: { rid, ids } })
}
