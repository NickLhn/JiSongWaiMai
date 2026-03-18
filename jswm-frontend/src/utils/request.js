import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { removeToken, removeUserInfo } from '@/utils/auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

let authRedirecting = false

const handleAuthError = (message) => {
  removeToken()
  removeUserInfo()

  if (!authRedirecting) {
    authRedirecting = true
    ElMessage.error(message || '登录已失效，请重新登录')

    const currentPath = router.currentRoute?.value?.path
    if (currentPath !== '/login') {
      router.replace('/login').finally(() => {
        setTimeout(() => {
          authRedirecting = false
        }, 300)
      })
    } else {
      setTimeout(() => {
        authRedirecting = false
      }, 300)
    }
  }

  return Promise.reject(new Error(message || '登录已失效，请重新登录'))
}

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      if (res.code === 401 || res.code === 403) {
        return handleAuthError(res.message)
      }
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    const status = error?.response?.status
    if (status === 401 || status === 403) {
      return handleAuthError(error?.response?.data?.message)
    }
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
