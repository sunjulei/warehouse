import request from '@/utils/request'

export function loadAllStocktake(params: any) {
  return request.get('/stocktake/loadAllStocktake', { params })
}

export function createStocktake(data: any) {
  return request.post('/stocktake/createStocktake', data)
}

export function loadStocktakeItems(params: any) {
  return request.get('/stocktake/loadStocktakeItems', { params })
}

export function saveStocktakeItems(data: any[]) {
  return request.post('/stocktake/saveStocktakeItems', data)
}

export function submitStocktake(data: any) {
  return request.post('/stocktake/submitStocktake', data)
}

export function cancelStocktake(data: any) {
  return request.post('/stocktake/cancelStocktake', data)
}
