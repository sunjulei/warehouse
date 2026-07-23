import request, { BASE_URL } from '@/utils/request'

export function uploadFile(file: File) {
  const formData = new FormData()
  formData.append('mf', file)
  return request.post('/file/uploadFile', formData)
}

export function getImageUrl(path: string): string {
  return BASE_URL + '/file/showImageByPath?path=' + encodeURIComponent(path)
}
