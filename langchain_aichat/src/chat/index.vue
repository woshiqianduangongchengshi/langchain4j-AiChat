<template>
  <div class="chat-main-container">
    <!-- 顶部导航栏 -->
    <div class="chat-header">
      <button @click="goToNavigation" class="back-to-nav-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5m7-7l-7 7 7 7"/>
        </svg>
        返回导航
      </button>
      <h1 class="chat-title">AI 模拟面试</h1>
    </div>
    
    <div class="chat-content-wrapper">
      <Sideline @sessionChange="handleSessionChange" />
      <AiChat :currentSession="currentSession" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Sideline from './sideline.vue'
import AiChat from './aichat.vue'

const router = useRouter()
const currentSession = ref(null)

// 处理会话变化
const handleSessionChange = (session) => {
  currentSession.value = session
}

// 返回导航页
const goToNavigation = () => {
  router.push('/navigation')
}
</script>

<style scoped>
.chat-main-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
  background: #f8fafc;
}

/* 顶部导航栏 */
.chat-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border-bottom: 1px solid #e2e8f0;
  z-index: 100;
}

.back-to-nav-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
}

.back-to-nav-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
  background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
}

.back-to-nav-btn svg {
  width: 16px;
  height: 16px;
  stroke-width: 2.5;
}

.chat-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
  letter-spacing: 0.5px;
}

/* 聊天内容区域包装器 */
.chat-content-wrapper {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-header {
    padding: 10px 16px;
  }
  
  .back-to-nav-btn {
    padding: 6px 12px;
    font-size: 12px;
  }
  
  .back-to-nav-btn svg {
    width: 14px;
    height: 14px;
  }
  
  .chat-title {
    font-size: 1.1rem;
  }
}
</style>
