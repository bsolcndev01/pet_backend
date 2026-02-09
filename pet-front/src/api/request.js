import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

// 创建axios实例
const base =
  import.meta.env.VITE_API_BASE_URL && import.meta.env.VITE_API_BASE_URL.startsWith('http')
    ? import.meta.env.VITE_API_BASE_URL
    : '/api'

const service = axios.create({
  // 默认走 /api 代理，若需直连后端请将 VITE_API_BASE_URL 设置为 http://host:port/api
  baseURL: base,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 添加token
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 业务错误处理（假设后端返回code不为200时为错误）
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(res)
    }
    
    return res
  },
  (error) => {
    console.error('响应错误:', error)
    
    // 401未授权
    if (error.response && error.response.status === 401) {
      const authStore = useAuthStore()
      ElMessageBox.confirm(
        '登录状态已过期，请重新登录',
        '提示',
        {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        authStore.logout()
        router.push(`/login?redirect=${router.currentRoute.fullPath}`)
      })
    } else {
      ElMessage.error(error.message || '服务器错误')
    }
    
    return Promise.reject(error)
  }
)

export default service
