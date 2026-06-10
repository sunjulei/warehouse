import request from '@/utils/request'

export function login(data: { loginname: string; pwd: string; code: string }) {
  return request.post('/login/login', data)
}

export function getCodeUrl(): string {
  return '/warehouse/login/getCode?t=' + Date.now()
}

export function currentUser() {
  return request.get('/login/currentUser')
}

export function logout() {
  return request.get('/login/logout')
}
