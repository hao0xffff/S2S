import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

// API base URL
const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

// Create axios instance
const service: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 60000, // 60 seconds for code generation
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor
service.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  async (response: AxiosResponse) => {
    // Handle file download (blob response)
    if (response.config.responseType === 'blob') {
      const blob = response.data as Blob
      
      // Check if response is actually an error (JSON error wrapped in blob)
      // Usually error responses have small size or JSON content type
      if (blob.type === 'application/json' || (blob.size < 1000 && blob.size > 0)) {
        try {
          const text = await blob.text()
          const errorData = JSON.parse(text)
          ElMessage.error(errorData.message || '下载失败')
          throw new Error(errorData.message || '下载失败')
        } catch (parseError) {
          // If it's our thrown error, re-throw it
          if (parseError instanceof Error && parseError.message.includes('下载失败')) {
            throw parseError
          }
          // If parsing fails, it might be a real small blob, return it
          return blob
        }
      }
      
      // Return the blob data directly
      return blob
    }
    
    // Handle normal JSON response
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.message || '请求失败')
      throw new Error(res.message || '请求失败')
    }
  },
  async (error) => {
    console.error('Response error:', error)
    
    // Handle blob error response
    if (error.response?.config?.responseType === 'blob') {
      try {
        const blob = error.response.data as Blob
        if (blob instanceof Blob) {
          const text = await blob.text()
          const errorData = JSON.parse(text)
          ElMessage.error(errorData.message || '下载失败')
          return Promise.reject(new Error(errorData.message || '下载失败'))
        }
      } catch {
        // Ignore parsing errors
      }
    }
    
    let message = '请求失败'
    if (error.response) {
      message = error.response.data?.message || `请求失败: ${error.response.status}`
    } else if (error.request) {
      message = '网络错误，请检查网络连接'
    } else {
      message = error.message || '请求失败'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service

