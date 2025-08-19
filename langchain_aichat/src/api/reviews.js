// 引入axios
import axios from 'axios'

const baseURL = '/api'

// 创建axios实例
const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true
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
 * 获取AI对话评价统计数据
 * @returns {Promise} 获取AI对话评价统计数据的Promise对象
 */
export const getChatReviewStats = () => {
  return api.get('/reviews/chat/stats')
}

/**
 * 获取AI对话评价列表
 * @param {number} page 页码
 * @param {number} pageSize 每页数量
 * @returns {Promise} 获取AI对话评价列表的Promise对象
 */
export const getChatReviews = (page = 1, pageSize = 3) => {
  return api.get('/reviews/chat/list', {
    params: {
      page,
      pageSize,
    },
  })
}

/**
 * 提交AI对话评价
 * @param {Object} reviewData 评价数据
 * @param {number} reviewData.overallRating 整体满意度评分
 * @param {number} reviewData.qualityRating 质量评分
 * @param {number} reviewData.speedRating 速度评分
 * @param {string} reviewData.comment 评价意见
 * @returns {Promise} 提交AI对话评价的Promise对象
 */
export const submitChatReview = (reviewData) => {
  return api.post('/reviews/chat', reviewData)
}

/**
 * 获取智能生图评价统计数据
 * @returns {Promise} 获取智能生图评价统计数据的Promise对象
 */
export const getImageReviewStats = () => {
  return api.get('/reviews/image/stats')
}

/**
 * 获取智能生图评价列表
 * @param {number} page 页码
 * @param {number} pageSize 每页数量
 * @returns {Promise} 获取智能生图评价列表的Promise对象
 */
export const getImageReviews = (page = 1, pageSize = 3) => {
  return api.get('/reviews/image/list', {
    params: {
      page,
      pageSize,
    },
  })
}

/**
 * 提交智能生图评价
 * @param {Object} reviewData 评价数据
 * @param {number} reviewData.overallRating 整体满意度评分
 * @param {number} reviewData.qualityRating 质量评分
 * @param {number} reviewData.speedRating 速度评分
 * @param {string} reviewData.comment 评价意见
 * @returns {Promise} 提交智能生图评价的Promise对象
 */
export const submitImageReview = (reviewData) => {
  return api.post('/reviews/image', reviewData)
}
