import request from '@/utils/request'

export function loadAllUser(params: any) {
  return request.get('/user/loadAllUser', { params })
}

export function addUser(data: any) {
  return request.post('/user/addUser', data)
}

export function updateUser(data: any) {
  return request.post('/user/updateUser', data)
}

export function deleteUser(id: number) {
  return request.post(`/user/deleteUser/${id}`)
}

export function resetPwd(id: number) {
  return request.post(`/user/resetPwd/${id}`)
}

export function loadUserMaxOrderNum() {
  return request.get('/user/loadUserMaxOrderNum')
}

export function changeChineseToPinyin(username: string) {
  return request.get('/user/changeChineseToPinyin', { params: { username } })
}

export function loadUsersByDeptId(deptId: number) {
  return request.get('/user/loadUsersByDeptId', { params: { deptId } })
}

export function loadUserById(id: number) {
  return request.get('/user/loadUserById', { params: { id } })
}

export function queryMgrByUserId(userId: number) {
  return request.get('/user/queryMgrByUserId', { params: { userId } })
}

export function initRoleByUserId(id: number) {
  return request.get('/user/initRoleByUserId', { params: { id } })
}

export function saveUserRole(uid: number, ids: number[]) {
  return request.post('/user/saveUserRole', null, { params: { uid, ids } })
}

export function getNowUser() {
  return request.get('/user/getNowUser')
}

export function updateUserInfo(data: any) {
  return request.post('/user/updateUserInfo', data)
}

export function changePassword(oldPassword: string, newPwdOne: string, newPwdTwo: string) {
  return request.post('/user/changePassword', null, { params: { oldPassword, newPwdOne, newPwdTwo } })
}
