// 引入axios
import axios from 'axios'

const baseURL = '/api'

// 创建axios实例
const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 添加请求拦截器，用于添加认证令牌
api.interceptors.request.use(
  (config) => {
    // 从localStorage中获取认证令牌
    const token = localStorage.getItem('token')
    // 如果存在令牌，则添加到请求头
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    // 处理请求错误
    return Promise.reject(error)
  },
)

/**
 * 登录
 * @param {string} name 用户名
 * @param {string} password 密码
 * @returns {Promise} 登录请求的Promise对象
 */
export const login = (name, password) => {
  return api.post('/auth/login', {
    name,
    password,
  })
}

/**
 * 注册
 * @param {string} name 用户名
 * @param {string} password 密码
 * @param {string} birthday 生日
 * @returns {Promise} 注册请求的Promise对象
 */
export const register = (name, password, birthday) => {
  return api.post('/auth/register', {
    name,
    password,
    birthday,
  })
}

/**
 * 获取当前用户信息
 * @returns {Promise} 获取用户信息请求的Promise对象
 */
export const getCurrentUser = () => {
  return api.get('/auth/me')
}

/**
 * 修改用户密码
 * @param {string} currentPassword 当前密码
 * @param {string} newPassword 新密码
 * @returns {Promise} 修改密码请求的Promise对象
 */
export const changePassword = (currentPassword, newPassword) => {
  return api.put('/auth/password', {
    currentPassword,
    newPassword,
  })
}
