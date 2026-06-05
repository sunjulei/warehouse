import request from '@/utils/request'

export function loadAllMember(params: any) {
  return request.get('/member/loadAllMember', { params })
}

export function findMember(params: any) {
  return request.get('/member/findMember', { params })
}

export function addMember(data: any) {
  return request.post('/member/addMember', data)
}

export function updateMember(data: any) {
  return request.post('/member/updateMember', data)
}

export function recharge(data: any) {
  return request.post('/member/recharge', data)
}

export function consume(data: any) {
  return request.post('/member/consume', data)
}

export function loadMemberRecords(params: any) {
  return request.get('/member/loadMemberRecords', { params })
}

export function deleteMember(data: any) {
  return request.post('/member/deleteMember', data)
}
