import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('wardrobe_token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && typeof body.code === 'number' && body.code !== 200) {
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(new Error(body.message))
    }
    return body ? body.data : response.data
  },
  (error) => {
    const status = error.response ? error.response.status : 0
    const msg = error.response && error.response.data ? error.response.data.message : error.message
    if (status === 401) {
      localStorage.removeItem('wardrobe_token')
      ElMessage.error('登录已过期，请重新登录')
      router.push({ name: 'Login' })
    } else {
      ElMessage.error(msg || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
