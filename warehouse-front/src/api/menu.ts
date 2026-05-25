import request from '@/utils/request'

export function loadIndexLeftMenuJson() {
  return request.get('/menu/loadIndexLeftMenuJson')
}

export function loadMenuManagerLeftTreeJson() {
  return request.get('/menu/loadMenuManagerLeftTreeJson')
}

export function loadAllMenu(params: any) {
  return request.get('/menu/loadAllMenu', { params })
}

export function addMenu(data: any) {
  return request.post('/menu/addMenu', data)
}

export function updateMenu(data: any) {
  return request.post('/menu/updateMenu', data)
}

export function deleteMenu(id: number) {
  return request.post('/menu/deleteMenu', null, { params: { id } })
}

export function loadMenuMaxOrderNum() {
  return request.get('/menu/loadMenuMaxOrderNum')
}

export function checkMenuHasChildrenNode(id: number) {
  return request.get('/menu/checkMenuHasChildrenNode', { params: { id } })
}
