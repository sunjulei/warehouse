import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_URL || '/warehouse',
  timeout: 15000,
  withCredentials: true
})

// POST请求自动将JSON转为form表单格式（后端用VO接收表单参数）
service.interceptors.request.use((config) => {
  const contentType = config.headers?.['Content-Type']
  const isJsonRequest = typeof contentType === 'string' && contentType.includes('application/json')
  if (config.method === 'post' && config.data
    && !isJsonRequest
    && !Array.isArray(config.data)
    && !(config.data instanceof FormData)
    && !(config.data instanceof URLSearchParams)) {
    const params = new URLSearchParams()
    for (const [key, value] of Object.entries(config.data)) {
      if (value !== null && value !== undefined) {
        params.append(key, String(value))
      }
    }
    config.data = params
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    // blob 响应直接返回（验证码图片等）
    if (response.config.responseType === 'blob') {
      return response.data
    }
    const res = response.data
    // DataGridView: {code: 0, msg: "", count: N, data: [...]}
    // ResultObj: {code: 200|-1, msg: "..."}
    if (res.code === -1) {
      ElMessage.error(res.msg || '操作失败')
      return Promise.reject(new Error(res.msg || '操作失败'))
    }
    return res
  },
  (error) => {
    if (error.response?.status === 401) {
      // 跳转到登录页（登录页会自动清除过期的登录状态）
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else if (error.response?.status === 403) {
      ElMessage.error('无权限访问此接口')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
