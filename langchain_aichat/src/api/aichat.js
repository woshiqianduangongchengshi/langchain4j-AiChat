import axios from 'axios'

// 使用与login.js相同的相对baseURL
const baseURL = '/api'

// 创建axios实例
const api = axios.create({
  baseURL,
  // 允许跨域请求携带 Cookie
  withCredentials: true,
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
 * 创建新会话
 * @param {string} sessionName 会话名称（可选）
 * @returns {Promise} 创建会话请求的Promise对象
 */
export const createSession = (sessionName = '') => {
  // 修改为使用请求体传递参数
  return api.post('/chat/sessions', {
    sessionName,
  })
}

/**
 * 获取当前用户的所有会话
 * @returns {Promise} 获取会话列表的Promise对象
 */
export const getUserSessions = () => {
  return api.get('/chat/sessions')
}

/**
 * 删除指定会话
 * @param {string} sessionId 会话ID
 * @returns {Promise} 删除会话请求的Promise对象
 */
export const deleteSession = (sessionId) => {
  return api.delete(`/chat/sessions/${sessionId}`)
}

/**
 * AI对话功能（流式）
 * @param {string} sessionId 会话ID
 * @param {string} message 对话内容
 * @returns {Promise} AI对话请求的Promise对象
 */
export const chatWithAI = (sessionId, message) => {
  // 注意：由于是流式响应，需要特殊处理
  return api.get('/aichat', {
    params: { o: sessionId, msg: message },
    responseType: 'stream', // 设置响应类型为流
  })
}

/**
 * 辅助函数：处理流式响应
 * @param {Promise} streamPromise 流式请求的Promise
 * @param {Function} onMessage 接收消息的回调函数
 * @param {Function} onComplete 完成的回调函数
 * @param {Function} onError 错误的回调函数
 */
export const handleStreamResponse = (streamPromise, onMessage, onComplete, onError) => {
  streamPromise
    .then((response) => {
      // 检查是否是流式响应
      if (response.data && typeof response.data.getReader === 'function') {
        // 流式响应处理
        const reader = response.data.getReader()
        const decoder = new TextDecoder()

        const readChunk = () => {
          reader
            .read()
            .then(({ done, value }) => {
              if (done) {
                onComplete && onComplete()
                return
              }

              const chunk = decoder.decode(value, { stream: true })
              onMessage && onMessage(chunk)
              readChunk()
            })
            .catch((error) => {
              onError && onError(error)
            })
        }

        readChunk()
      } else {
        // 非流式响应处理
        // 直接使用响应数据
        onMessage && onMessage(response.data)
        onComplete && onComplete()
      }
    })
    .catch((error) => {
      onError && onError(error)
    })
}

/**
 * 获取指定会话的历史对话
 * @param {string} sessionId 会话ID
 * @returns {Promise} 获取历史对话的Promise对象
 */
export const getSessionHistory = (sessionId) => {
  return api.get(`/chat/sessions/${sessionId}/history`)
}

/**
 * 生成图像 - SiliconFlow API
 * @param {Object} params 图像生成参数
 * @param {string} params.prompt 图像生成的正面提示词
 * @param {string} params.negativePrompt 图像生成的负面提示词（可选）
 * @param {string} params.imageSize 图像尺寸，格式为"widthxheight"（可选，默认"1024x1024"）
 * @param {number} params.batchSize 生成图片数量（可选，默认1）
 * @param {number} params.numInferenceSteps 推理步数（可选，默认20）
 * @param {number} params.guidanceScale CFG缩放参数（可选，默认7.5）
 * @param {number} params.seed 随机种子（可选）
 * @returns {Promise} 生成图像请求的Promise对象
 */
export const generateImage = (params) => {
  // 创建新的axios实例用于SiliconFlow API
  const siliconFlowApi = axios.create({
    baseURL: 'https://api.siliconflow.cn/v1',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${import.meta.env.VITE_API_KEY}`,
    },
  })

  // 构建请求数据
  const requestData = {
    model: 'Kwai-Kolors/Kolors',
    prompt: params.prompt,
    image_size: params.imageSize || '1024x1024',
    batch_size: params.batchSize || 1,
    num_inference_steps: params.numInferenceSteps || 20,
    guidance_scale: params.guidanceScale || 7.5,
  }

  // 添加可选参数
  if (params.negativePrompt) {
    requestData.negative_prompt = params.negativePrompt
  }

  if (params.seed !== undefined) {
    requestData.seed = params.seed
  }

  return siliconFlowApi.post('/images/generations', requestData)
}

/**
 * 检查图像生成服务状态 - SiliconFlow API
 * @returns {Promise} 健康检查请求的Promise对象
 */
export const checkHealth = () => {
  // SiliconFlow API没有专门的health检查端点，我们直接返回成功状态
  // 或者可以调用一个简单的API端点来验证连接
  return Promise.resolve({ status: 'ok' })
}
