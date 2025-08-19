<template>
  <div class="chat-container">
    <div class="chat-header">
      <h2>{{ currentSession?.sessionName || '默认对话' }}</h2>
      <!-- 新增的时间和问候语展示区域 -->
      <div class="header-extra">
        <div class="greeting">{{ greeting }}，{{ username || '用户' }}</div>
        <div class="current-time">{{ currentTime }}</div>
      </div>
    </div>
    <div class="chat-messages" ref="messagesContainer">
      <!-- 消息列表 -->
      <div v-for="message in messages" :key="message.id" class="message-wrapper">
        <div v-if="message.type === 'user'" class="message user-message">
          <div class="message-content">{{ message.content }}</div>
        </div>
        <div v-else class="message ai-message">
          <div class="message-avatar">
            <div class="ai-avatar">AI</div>
          </div>
          <div class="message-content">
            <!-- 修改AI消息内容渲染方式 -->
            <div v-if="hasThinkingContent(message.content)" class="think-content-container">
              <div class="think-label">思考过程：</div>
              <div class="think-content">{{ extractThinkingContent(message.content) }}</div>
              <div class="answer-label">回答：</div>
              <div class="answer-content">{{ extractAnswerContent(message.content) }}</div>
            </div>
            <div v-else>{{ message.content }}</div>
          </div>
        </div>
      </div>
      <!-- 加载中状态 -->
      <div v-if="isLoading" class="loading-indicator">
        <div class="loading-dot"></div>
        <div class="loading-dot"></div>
        <div class="loading-dot"></div>
      </div>
    </div>
    <div class="chat-input">
      <textarea
        v-model="inputMessage"
        placeholder="发送消息..."
        @keydown.enter.exact="handleSendMessage"
        @keydown.enter.shift="($event) => $event.preventDefault()"
      ></textarea>
      <button @click="handleSendMessage" :disabled="!inputMessage.trim() || isLoading">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { chatWithAI, handleStreamResponse, getSessionHistory } from '@/api/aichat.js'
// 导入获取当前用户信息的API
import { getCurrentUser } from '@/api/login.js'
import { ElMessage } from 'element-plus'

// 接收父组件传递的当前会话ID
const props = defineProps({
  currentSession: { type: Object, required: true },
})

// 响应式变量
const inputMessage = ref('')
const messages = ref([])
const isLoading = ref(false)
const messagesContainer = ref(null)
const route = useRoute()
const currentTime = ref('')
// 新增存储用户名的响应式变量
const username = ref('')
const userLoading = ref(false)

// 修改函数：检查是否包含思考内容
const hasThinkingContent = (content) => {
  return content.includes('<think>') && content.includes('</think>')
}

// 修改函数：提取思考内容
const extractThinkingContent = (content) => {
  // 匹配thinking标签之间的内容
  const match = content.match(/<think>([\s\S]*?)<\/think>/)
  if (match && match[1]) {
    return match[1].trim()
  }
  return ''
}

// 修改函数：提取回答内容
const extractAnswerContent = (content) => {
  // 移除thinking标签及其之间的内容
  let cleanContent = content.replace(/<think>[\s\S]*?<\/think>/g, '')
  return cleanContent.trim()
}

// 计算问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour >= 6 && hour < 12) {
    return '早上好'
  } else if (hour >= 12 && hour < 14) {
    return '中午好'
  } else if (hour >= 14 && hour < 18) {
    return '下午好'
  } else {
    return '晚上好'
  }
})

// 更新时间的函数
const updateTime = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  currentTime.value = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 获取当前用户信息
const fetchCurrentUser = async () => {
  try {
    userLoading.value = true
    const response = await getCurrentUser()
    if (response.data && response.data.name) {
      username.value = response.data.name
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败，将使用默认名称')
    username.value = '用户'
  } finally {
    userLoading.value = false
  }
}

// 监听会话变化，加载对应会话的消息
watch(
  () => props.currentSession,
  (newSession) => {
    if (newSession) {
      // 加载历史对话
      loadHistoryMessages(newSession.sessionId)
    }
  },
  { immediate: true },
)

// 加载历史对话
const loadHistoryMessages = async (sessionId) => {
  try {
    isLoading.value = true
    const response = await getSessionHistory(sessionId)
    // 处理响应数据
    if (response.data && response.data.length > 0) {
      // 解析content中的历史对话
      const historyData = JSON.parse(response.data[0].content)
      // 转换为messages所需格式，并过滤知识库标识
      messages.value = historyData.map((item, index) => {
        let content = item.text || (item.contents && item.contents[0].text) || ''

        // 过滤掉知识库标识内容
        const knowledgeBasePrefix = 'Answer using the following information:'
        if (content.includes(knowledgeBasePrefix)) {
          // 找到知识库内容的起始位置
          const startIndex = content.indexOf(knowledgeBasePrefix)
          // 尝试找到知识库内容的结束位置（假设以用户问题或AI回答结束）
          // 这里使用一些常见的结束标记来识别
          const endMarkers = ['\n\nAnswer:', '\n\n问题:', '\n\n请回答:']
          let endIndex = content.length

          for (const marker of endMarkers) {
            const markerIndex = content.indexOf(marker, startIndex)
            if (markerIndex !== -1 && markerIndex < endIndex) {
              endIndex = markerIndex
            }
          }

          // 移除知识库内容
          content = content.substring(0, startIndex) + content.substring(endIndex)
          content = content.trim()
        }

        return {
          id: index,
          type: item.type === 'USER' ? 'user' : 'ai',
          content: content,
        }
      })
    } else {
      messages.value = []
    }
  } catch (error) {
    console.error('加载历史对话失败:', error)
    messages.value = []
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

// 发送消息
const handleSendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return

  const message = inputMessage.value.trim()
  inputMessage.value = ''

  // 添加用户消息到列表
  messages.value.push({
    id: Date.now(),
    type: 'user',
    content: message,
  })

  // 滚动到底部
  scrollToBottom()

  // 调用AI聊天接口
  isLoading.value = true
  try {
    const aiMessageId = Date.now() + 1
    // 先添加一个空的AI消息占位符
    messages.value.push({
      id: aiMessageId,
      type: 'ai',
      content: '',
    })

    // 调用接口
    const streamPromise = chatWithAI(props.currentSession.sessionId, message)
    console.log('streamPromise', streamPromise)

    handleStreamResponse(
      streamPromise,
      (chunk) => {
        // 查找对应的AI消息并追加内容
        const aiMessage = messages.value.find((m) => m.id === aiMessageId)
        if (aiMessage) {
          aiMessage.content += chunk
          scrollToBottom()
        }
      },
      () => {
        isLoading.value = false
        scrollToBottom()
      },
      (error) => {
        console.error('AI对话错误:', error)
        // 查找对应的AI消息并更新为错误信息
        const aiMessage = messages.value.find((m) => m.id === aiMessageId)
        if (aiMessage) {
          // 检查错误对象的结构，获取合适的错误信息
          let errorMessage = '未知错误'
          if (error.message) {
            errorMessage = error.message
          } else if (error.response && error.response.data) {
            errorMessage = error.response.data
          }
          aiMessage.content = `抱歉，对话过程中出现错误: ${errorMessage}`
        }
        isLoading.value = false
        scrollToBottom()
      },
    )
  } catch (error) {
    console.error('发送消息失败:', error)
    // 查找对应的AI消息并更新为错误信息
    const aiMessage = messages.value.find((m) => m.id === aiMessageId)
    if (aiMessage) {
      aiMessage.content = `抱歉，发送消息失败: ${error.message || '未知错误'}`
    }
    isLoading.value = false
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 组件挂载时滚动到底部、启动时间更新并获取用户信息
onMounted(() => {
  scrollToBottom()
  updateTime()
  // 每隔1秒更新一次时间
  setInterval(updateTime, 1000)
  // 获取用户信息
  fetchCurrentUser()
})
</script>

<style scoped>
/* 基础样式，与登录注册页面保持一致的风格 */
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #f5f7fa, #c3cfe2);
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.chat-header {
  padding: 16px 24px;
  background: linear-gradient(135deg, #1a2a6c, #b21f1f, #fdbb2d);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
}

.chat-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

/* 新增的header-extra样式 */
.header-extra {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: rgba(255, 255, 255, 0.9);
}

.greeting {
  font-size: 14px;
}

.current-time {
  font-size: 14px;
}

.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background-color: rgba(255, 255, 255, 0.8);
  scrollbar-width: thin;
  scrollbar-color: #ccc #f5f5f5;
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f5f5f5;
  border-radius: 10px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 10px;
}

.message-wrapper {
  margin-bottom: 16px;
  display: flex;
}

.message {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 8px;
  word-break: break-word;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-message {
  margin-left: auto;
  background: linear-gradient(135deg, #1a2a6c, #2a5298);
  color: white;
  border-top-right-radius: 2px;
}

.ai-message {
  display: flex;
  background: white;
  color: #333;
  border-top-left-radius: 2px;
}

.message-avatar {
  margin-right: 10px;
}

.ai-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #1a2a6c, #b21f1f, #fdbb2d);
  border-radius: 50%;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.message-content {
  line-height: 1.6;
}

.chat-input {
  padding: 16px 24px;
  background: white;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
}

.chat-input textarea {
  width: 100%;
  height: 80px;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: none;
  font-size: 16px;
  margin-bottom: 12px;
  transition: all 0.3s;
}

.chat-input textarea:focus {
  outline: none;
  border-color: #1a2a6c;
  box-shadow: 0 0 0 3px rgba(26, 42, 108, 0.1);
}

.chat-input button {
  padding: 10px 24px;
  background: linear-gradient(to right, #1a2a6c, #b21f1f, #fdbb2d);
  background-size: 200% auto;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  float: right;
}

.chat-input button:hover {
  background-position: right center;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(26, 42, 108, 0.3);
}

.chat-input button:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.loading-indicator {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.loading-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #1a2a6c;
  margin-right: 6px;
  animation: pulse 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(2) {
  animation-delay: -0.32s;
}

.loading-dot:nth-child(3) {
  animation-delay: -0.64s;
}

@keyframes pulse {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

/* 可以添加加载状态的样式（如果需要） */
.user-loading {
  font-style: italic;
  color: rgba(255, 255, 255, 0.7);
}

/* 新增思考内容和回答内容的样式 */
.think-content-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.think-label {
  font-size: 12px;
  font-weight: bold;
  color: #888;
  margin-bottom: 4px;
}

.think-content {
  background-color: #f5f5f5;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  color: #666;
  font-style: italic;
  border-left: 3px solid #ccc;
}

.answer-label {
  font-size: 12px;
  font-weight: bold;
  color: #888;
  margin-top: 8px;
  margin-bottom: 4px;
}

.answer-content {
  line-height: 1.6;
}
</style>
