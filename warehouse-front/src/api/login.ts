import request from '@/utils/request'
import type { ResultObj } from '@/types/api'

export function login(data: { loginname: string; pwd: string; code: string }): Promise<ResultObj> {
  return request.post('/login/login', data)
}

export function getCodeUrl(): string {
  return '/warehouse/login/getCode?t=' + Date.now()
}

export function currentUser(): Promise<ResultObj> {
  return request.get('/login/currentUser')
}

export function logout(): Promise<ResultObj> {
  return request.get('/login/logout')
}
