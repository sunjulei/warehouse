import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_URL || '/warehouse',
  timeout: 15000,
  withCredentials: true
})

/** 后端基础路径（拼接验证码、图片等直连 URL 时统一使用，避免硬编码 /warehouse） */
export const BASE_URL: string = service.defaults.baseURL as string

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

// 401 提示节流：并发请求同时 401 时只弹一次提示、只跳一次登录页
let lastUnauthorizedAt = 0

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
  async (error) => {
    const status = error.response?.status
    if (status === 401) {
      const now = Date.now()
      if (now - lastUnauthorizedAt > 5000) {
        lastUnauthorizedAt = now
        ElMessage.error('登录已过期，请重新登录')
      }
      // 清除本地登录状态（动态 import 避免与 stores/auth 循环依赖）
      const { useAuthStore } = await import('@/stores/auth')
      useAuthStore().reset()
      router.push('/login')
    } else if (status === 403) {
      ElMessage.error('无权限访问此接口')
    } else if (status === 404) {
      ElMessage.error('请求的资源不存在')
    } else if (status >= 500) {
      ElMessage.error('服务器错误，请稍后重试')
    } else if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      ElMessage.error('请求超时，请检查网络后重试')
    } else if (error.message === 'Network Error') {
      ElMessage.error('网络连接异常，请检查网络')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
