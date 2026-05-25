import request from '@/utils/request'

export function loadAllCache() {
  return request.get('/cache/loadAllCache')
}

export function deleteCache(key: string) {
  return request.post('/cache/deleteCache', null, { params: { key } })
}

export function removeAllCache() {
  return request.post('/cache/removeAllCache')
}

export function syncCache() {
  return request.post('/cache/syncCache')
}
