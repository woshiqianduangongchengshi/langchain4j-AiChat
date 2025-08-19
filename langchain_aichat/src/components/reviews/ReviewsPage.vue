<template>
  <div class="reviews-container">
    <div class="header">
      <button @click="goBack" class="back-btn">&lt; 返回导航</button>
      <h1 class="page-title">功能评价</h1>
    </div>

    <!-- 分页导航 -->
    <div class="pagination-nav">
      <button
        class="page-btn"
        :class="{ active: currentPage === 'chat' }"
        @click="currentPage = 'chat'"
      >
        AI对话评价
      </button>
      <button
        class="page-btn"
        :class="{ active: currentPage === 'image' }"
        @click="currentPage = 'image'"
      >
        智能生图评价
      </button>
    </div>

    <!-- 功能切换 -->
    <div class="function-switch">
      <button
        class="switch-btn"
        :class="{ active: currentMode === 'submit' }"
        @click="currentMode = 'submit'"
      >
        提交评价
      </button>
      <button
        class="switch-btn"
        :class="{ active: currentMode === 'view' }"
        @click="currentMode = 'view'"
      >
        查看评价
      </button>
    </div>

    <!-- AI对话评价页面 -->
    <div v-if="currentPage === 'chat'" class="review-page">
      <!-- 提交评价表单 -->
      <div v-if="currentMode === 'submit'" class="review-form">
        <h2>AI对话功能评价</h2>
        <div class="rating-group">
          <label>整体满意度</label>
          <select v-model="chatReview.overallRating" class="rating-select">
            <option value="">请选择</option>
            <option value="5">非常满意</option>
            <option value="4">满意</option>
            <option value="3">一般</option>
            <option value="2">不满意</option>
            <option value="1">非常不满意</option>
          </select>
        </div>
        <div class="rating-group">
          <label>回答质量</label>
          <select v-model="chatReview.qualityRating" class="rating-select">
            <option value="">请选择</option>
            <option value="5">非常好</option>
            <option value="4">好</option>
            <option value="3">一般</option>
            <option value="2">差</option>
            <option value="1">非常差</option>
          </select>
        </div>
        <div class="rating-group">
          <label>响应速度</label>
          <select v-model="chatReview.speedRating" class="rating-select">
            <option value="">请选择</option>
            <option value="5">非常快</option>
            <option value="4">快</option>
            <option value="3">一般</option>
            <option value="2">慢</option>
            <option value="1">非常慢</option>
          </select>
        </div>
        <div class="rating-group">
          <label>评价意见</label>
          <textarea
            v-model="chatReview.comment"
            class="comment-textarea"
            placeholder="请输入您对AI对话功能的评价意见..."
            rows="4"
          ></textarea>
        </div>
        <button @click="submitChatReview" class="submit-btn">提交评价</button>
      </div>

      <!-- 查看评价列表 -->
      <div v-if="currentMode === 'view'" class="reviews-list">
        <div class="list-header">
          <h2>AI对话功能评价</h2>
          <div class="rating-summary">
            <div class="summary-item">
              <span class="summary-label">整体满意度</span>
              <div class="star-rating">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="star"
                  :class="{ filled: i <= chatStats.averageOverallRating }"
                  >★</span
                >
                <span class="rating-value">{{ chatStats.averageOverallRating.toFixed(1) }}</span>
              </div>
            </div>
            <div class="summary-item">
              <span class="summary-label">回答质量</span>
              <div class="star-rating">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="star"
                  :class="{ filled: i <= chatStats.averageQualityRating }"
                  >★</span
                >
                <span class="rating-value">{{ chatStats.averageQualityRating.toFixed(1) }}</span>
              </div>
            </div>
            <div class="summary-item">
              <span class="summary-label">响应速度</span>
              <div class="star-rating">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="star"
                  :class="{ filled: i <= chatStats.averageSpeedRating }"
                  >★</span
                >
                <span class="rating-value">{{ chatStats.averageSpeedRating.toFixed(1) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-for="review in chatReviews" :key="review.id" class="review-card">
          <div class="review-header">
            <span class="username">{{ review.username }}</span>
            <span class="timestamp">{{ formatDate(review.createdAt) }}</span>
          </div>
          <div class="review-ratings">
            <div class="rating-item">
              <span class="rating-label">整体:</span>
              <div class="mini-stars">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="mini-star"
                  :class="{ filled: i <= review.overallRating }"
                  >★</span
                >
              </div>
            </div>
            <div class="rating-item">
              <span class="rating-label">质量:</span>
              <div class="mini-stars">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="mini-star"
                  :class="{ filled: i <= review.qualityRating }"
                  >★</span
                >
              </div>
            </div>
            <div class="rating-item">
              <span class="rating-label">速度:</span>
              <div class="mini-stars">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="mini-star"
                  :class="{ filled: i <= review.speedRating }"
                  >★</span
                >
              </div>
            </div>
          </div>
          <p v-if="review.comment" class="review-text">{{ review.comment }}</p>
        </div>

        <!-- 分页控件 -->
        <div class="pagination-controls">
          <button
            class="page-control-btn"
            :disabled="chatPagination.page === 1"
            @click="changeChatPage(chatPagination.page - 1)"
          >
            上一页
          </button>
          <span class="page-info"
            >第 {{ chatPagination.page }} 页，共 {{ chatPagination.totalPages }} 页</span
          >
          <button
            class="page-control-btn"
            :disabled="chatPagination.page === chatPagination.totalPages"
            @click="changeChatPage(chatPagination.page + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 智能生图评价页面 -->
    <div v-if="currentPage === 'image'" class="review-page">
      <!-- 提交评价表单 -->
      <div v-if="currentMode === 'submit'" class="review-form">
        <h2>智能生图功能评价</h2>
        <div class="rating-group">
          <label>整体满意度</label>
          <select v-model="imageReview.overallRating" class="rating-select">
            <option value="">请选择</option>
            <option value="5">非常满意</option>
            <option value="4">满意</option>
            <option value="3">一般</option>
            <option value="2">不满意</option>
            <option value="1">非常不满意</option>
          </select>
        </div>
        <div class="rating-group">
          <label>图片质量</label>
          <select v-model="imageReview.qualityRating" class="rating-select">
            <option value="">请选择</option>
            <option value="5">非常好</option>
            <option value="4">好</option>
            <option value="3">一般</option>
            <option value="2">差</option>
            <option value="1">非常差</option>
          </select>
        </div>
        <div class="rating-group">
          <label>生成速度</label>
          <select v-model="imageReview.speedRating" class="rating-select">
            <option value="">请选择</option>
            <option value="5">非常快</option>
            <option value="4">快</option>
            <option value="3">一般</option>
            <option value="2">慢</option>
            <option value="1">非常慢</option>
          </select>
        </div>
        <div class="rating-group">
          <label>评价意见</label>
          <textarea
            v-model="imageReview.comment"
            class="comment-textarea"
            placeholder="请输入您对智能生图功能的评价意见..."
            rows="4"
          ></textarea>
        </div>
        <button @click="submitImageReview" class="submit-btn">提交评价</button>
      </div>

      <!-- 查看评价列表 -->
      <div v-if="currentMode === 'view'" class="reviews-list">
        <div class="list-header">
          <h2>智能生图功能评价</h2>
          <div class="rating-summary">
            <div class="summary-item">
              <span class="summary-label">整体满意度</span>
              <div class="star-rating">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="star"
                  :class="{ filled: i <= imageStats.averageOverallRating }"
                  >★</span
                >
                <span class="rating-value">{{ imageStats.averageOverallRating.toFixed(1) }}</span>
              </div>
            </div>
            <div class="summary-item">
              <span class="summary-label">图片质量</span>
              <div class="star-rating">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="star"
                  :class="{ filled: i <= imageStats.averageQualityRating }"
                  >★</span
                >
                <span class="rating-value">{{ imageStats.averageQualityRating.toFixed(1) }}</span>
              </div>
            </div>
            <div class="summary-item">
              <span class="summary-label">生成速度</span>
              <div class="star-rating">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="star"
                  :class="{ filled: i <= imageStats.averageSpeedRating }"
                  >★</span
                >
                <span class="rating-value">{{ imageStats.averageSpeedRating.toFixed(1) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-for="review in imageReviews" :key="review.id" class="review-card">
          <div class="review-header">
            <span class="username">{{ review.username }}</span>
            <span class="timestamp">{{ formatDate(review.createdAt) }}</span>
          </div>
          <div class="review-ratings">
            <div class="rating-item">
              <span class="rating-label">整体:</span>
              <div class="mini-stars">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="mini-star"
                  :class="{ filled: i <= review.overallRating }"
                  >★</span
                >
              </div>
            </div>
            <div class="rating-item">
              <span class="rating-label">质量:</span>
              <div class="mini-stars">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="mini-star"
                  :class="{ filled: i <= review.qualityRating }"
                  >★</span
                >
              </div>
            </div>
            <div class="rating-item">
              <span class="rating-label">速度:</span>
              <div class="mini-stars">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="mini-star"
                  :class="{ filled: i <= review.speedRating }"
                  >★</span
                >
              </div>
            </div>
          </div>
          <p v-if="review.comment" class="review-text">{{ review.comment }}</p>
        </div>

        <!-- 分页控件 -->
        <div class="pagination-controls">
          <button
            class="page-control-btn"
            :disabled="imagePagination.page === 1"
            @click="changeImagePage(imagePagination.page - 1)"
          >
            上一页
          </button>
          <span class="page-info"
            >第 {{ imagePagination.page }} 页，共 {{ imagePagination.totalPages }} 页</span
          >
          <button
            class="page-control-btn"
            :disabled="imagePagination.page === imagePagination.totalPages"
            @click="changeImagePage(imagePagination.page + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getChatReviewStats,
  getChatReviews,
  submitChatReview as submitChatReviewApi,
  getImageReviewStats,
  getImageReviews,
  submitImageReview as submitImageReviewApi,
} from '@/api/reviews'

const router = useRouter()

// 当前页面
const currentPage = ref('chat')

// 当前模式（提交评价/查看评价）
const currentMode = ref('view') // 修改为'view'

// AI对话评价数据
const chatReview = reactive({
  overallRating: '',
  qualityRating: '',
  speedRating: '',
  comment: '',
})

// 智能生图评价数据
const imageReview = reactive({
  overallRating: '',
  qualityRating: '',
  speedRating: '',
  comment: '',
})

// AI对话评价统计数据
const chatStats = reactive({
  averageOverallRating: 0,
  averageQualityRating: 0,
  averageSpeedRating: 0,
  totalReviews: 0,
})

// 智能生图评价统计数据
const imageStats = reactive({
  averageOverallRating: 0,
  averageQualityRating: 0,
  averageSpeedRating: 0,
  totalReviews: 0,
})

// AI对话评价列表
const chatReviews = ref([])

// 智能生图评价列表
const imageReviews = ref([])

// AI对话分页信息
const chatPagination = reactive({
  page: 1,
  totalPages: 1,
  pageSize: 3,
})

// 智能生图分页信息
const imagePagination = reactive({
  page: 1,
  totalPages: 1,
  pageSize: 3,
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  // 如果日期字符串包含T，说明是ISO格式，只取T之前的部分
  if (dateString.includes('T')) {
    return dateString.split('T')[0]
  }
  // 如果不是ISO格式，直接返回原字符串
  return dateString
}

// 获取AI对话评价统计数据
const fetchChatReviewStats = async () => {
  try {
    const response = await getChatReviewStats()
    if (response.data && response.data.data) {
      const stats = response.data.data
      chatStats.averageOverallRating = stats.averageOverallRating || 0
      chatStats.averageQualityRating = stats.averageQualityRating || 0
      chatStats.averageSpeedRating = stats.averageSpeedRating || 0
      chatStats.totalReviews = stats.totalReviews || 0
    }
  } catch (error) {
    console.error('获取AI对话评价统计数据失败:', error)
    ElMessage.error('获取评价统计数据失败')
  }
}

// 获取AI对话评价列表
const fetchChatReviews = async (page = 1) => {
  try {
    const response = await getChatReviews(page, chatPagination.pageSize)
    //console.log('AI对话评价列表响应:', response)
    if (response.data && response.data.data) {
      const data = response.data.data
      chatReviews.value = data.reviews || []
      chatPagination.page = data.page || 1
      chatPagination.totalPages = data.totalPages || 1
    }
  } catch (error) {
    console.error('获取AI对话评价列表失败:', error)
    ElMessage.error('获取评价列表失败')
  }
}

// 获取智能生图评价统计数据
const fetchImageReviewStats = async () => {
  try {
    const response = await getImageReviewStats()
    if (response.data && response.data.data) {
      const stats = response.data.data
      imageStats.averageOverallRating = stats.averageOverallRating || 0
      imageStats.averageQualityRating = stats.averageQualityRating || 0
      imageStats.averageSpeedRating = stats.averageSpeedRating || 0
      imageStats.totalReviews = stats.totalReviews || 0
    }
  } catch (error) {
    console.error('获取智能生图评价统计数据失败:', error)
    ElMessage.error('获取评价统计数据失败')
  }
}

// 获取智能生图评价列表
const fetchImageReviews = async (page = 1) => {
  try {
    const response = await getImageReviews(page, imagePagination.pageSize)
    //console.log('智能生图评价列表响应:', response)
    if (response.data && response.data.data) {
      const data = response.data.data
      imageReviews.value = data.reviews || []
      imagePagination.page = data.page || 1
      imagePagination.totalPages = data.totalPages || 1
    }
  } catch (error) {
    console.error('获取智能生图评价列表失败:', error)
    ElMessage.error('获取评价列表失败')
  }
}

// 提交AI对话评价
const submitChatReview = async () => {
  if (!chatReview.overallRating || !chatReview.qualityRating || !chatReview.speedRating) {
    ElMessage.warning('请完成所有评分项')
    return
  }

  try {
    await submitChatReviewApi({
      overallRating: parseInt(chatReview.overallRating),
      qualityRating: parseInt(chatReview.qualityRating),
      speedRating: parseInt(chatReview.speedRating),
      comment: chatReview.comment,
    })
    ElMessage.success('评价提交成功')

    // 重置表单
    chatReview.overallRating = ''
    chatReview.qualityRating = ''
    chatReview.speedRating = ''
    chatReview.comment = ''

    // 刷新统计数据
    fetchChatReviewStats()
  } catch (error) {
    console.error('提交AI对话评价失败:', error)
    ElMessage.error('提交评价失败')
  }
}

// 提交智能生图评价
const submitImageReview = async () => {
  if (!imageReview.overallRating || !imageReview.qualityRating || !imageReview.speedRating) {
    ElMessage.warning('请完成所有评分项')
    return
  }

  try {
    await submitImageReviewApi({
      overallRating: parseInt(imageReview.overallRating),
      qualityRating: parseInt(imageReview.qualityRating),
      speedRating: parseInt(imageReview.speedRating),
      comment: imageReview.comment,
    })
    ElMessage.success('评价提交成功')

    // 重置表单
    imageReview.overallRating = ''
    imageReview.qualityRating = ''
    imageReview.speedRating = ''
    imageReview.comment = ''

    // 刷新统计数据
    fetchImageReviewStats()
  } catch (error) {
    console.error('提交智能生图评价失败:', error)
    ElMessage.error('提交评价失败')
  }
}

// 切换AI对话评价页码
const changeChatPage = async (page) => {
  if (page < 1 || page > chatPagination.totalPages) return
  await fetchChatReviews(page)
}

// 切换智能生图评价页码
const changeImagePage = async (page) => {
  if (page < 1 || page > imagePagination.totalPages) return
  await fetchImageReviews(page)
}

// 页面加载时获取评价数据
onMounted(async () => {
  await fetchChatReviewStats()
  await fetchChatReviews()
  await fetchImageReviewStats()
  await fetchImageReviews()
})

const goBack = () => {
  router.push('/navigation')
}
</script>

<style scoped>
.reviews-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.page-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #333;
}

/* 分页导航 */
.pagination-nav {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  gap: 10px;
}

.page-btn {
  padding: 12px 24px;
  background: white;
  color: #667eea;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover {
  background: #f7fafc;
  border-color: #667eea;
}

.page-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
}

/* 功能切换 */
.function-switch {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  gap: 10px;
}

.switch-btn {
  padding: 10px 20px;
  background: white;
  color: #667eea;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.switch-btn:hover {
  background: #f7fafc;
  border-color: #667eea;
}

.switch-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
}

/* 评价页面 */
.review-page {
  max-width: 1200px;
  margin: 0 auto;
}

/* 提交评价表单 */
.review-form {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

h2 {
  margin-top: 0;
  margin-bottom: 25px;
  font-size: 1.4rem;
  color: #4a5568;
  text-align: center;
}

.rating-group {
  margin-bottom: 20px;
}

.rating-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #4a5568;
}

.rating-select {
  width: 100%;
  padding: 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  transition: all 0.3s ease;
}

.rating-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.comment-textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  transition: all 0.3s ease;
}

.comment-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

/* 查看评价列表 */
.reviews-list {
  max-width: 1000px;
  margin: 0 auto;
}

.list-header {
  background: white;
  border-radius: 16px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

.list-header h2 {
  margin-bottom: 20px;
}

.rating-summary {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 15px;
}

.summary-item {
  text-align: center;
}

.summary-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #4a5568;
}

.star-rating {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.star {
  font-size: 24px;
  color: #e2e8f0;
}

.star.filled {
  color: #fbbf24;
}

.rating-value {
  font-weight: 600;
  color: #4a5568;
  margin-left: 5px;
}

.review-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.review-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.12);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.username {
  font-weight: 700;
  color: #667eea;
}

.timestamp {
  font-size: 12px;
  color: #a0aec0;
}

.review-ratings {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.rating-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.rating-label {
  font-size: 12px;
  color: #718096;
}

.mini-stars {
  display: flex;
  gap: 2px;
}

.mini-star {
  font-size: 14px;
  color: #e2e8f0;
}

.mini-star.filled {
  color: #fbbf24;
}

.review-text {
  color: #4a5568;
  line-height: 1.6;
  margin: 0;
}

/* 分页控件 */
.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.page-control-btn {
  padding: 10px 20px;
  background: white;
  color: #667eea;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-control-btn:hover:not(:disabled) {
  background: #f7fafc;
  border-color: #667eea;
}

.page-control-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #4a5568;
  font-weight: 500;
}

.back-btn {
  padding: 10px 20px;
  background: white;
  color: #667eea;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: #f7fafc;
  border-color: #667eea;
}
</style>
