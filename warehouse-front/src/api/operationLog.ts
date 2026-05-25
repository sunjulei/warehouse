import request from '@/utils/request'

export function loadAllOperationLog(params: any) {
  return request.get('/operationLog/loadAllOperationLog', { params })
}
