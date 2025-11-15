import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // 使用Vite代理的API路径
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 加载实例
let loadingInstance = null

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 显示加载中
    loadingInstance = ElLoading.service({
      lock: true,
      text: '加载中...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  error => {
    // 隐藏加载中
    if (loadingInstance) {
      loadingInstance.close()
    }
    ElMessage.error('请求错误')
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 隐藏加载中
    if (loadingInstance) {
      loadingInstance.close()
    }
    
    // 统一处理响应格式
    const res = response.data
    
    // 如果响应是数组，直接返回
    if (Array.isArray(res)) {
      return res
    }
    
    // 如果响应包含code字段且不等于200，视为错误
    if (res.code !== undefined && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    // 返回响应数据中的data字段，如果存在
    return res.data !== undefined ? res.data : res
  },
  error => {
    // 隐藏加载中
    if (loadingInstance) {
      loadingInstance.close()
    }
    
    // 处理网络错误
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          // 可以在这里跳转到登录页
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求地址不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(`错误码：${error.response.status}`)
      }
    } else {
      ElMessage.error('网络异常，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default request