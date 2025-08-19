<template>
  <div class="navigation-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>

    <!-- 登出按钮 -->
    <div class="logout-section">
      <button class="logout-btn" @click="handleLogout">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
          <polyline points="16 17 21 12 16 7"></polyline>
          <line x1="21" y1="12" x2="9" y2="12"></line>
        </svg>
        <span>退出登录</span>
      </button>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 欢迎标题 -->
      <div class="welcome-section">
        <h1 class="welcome-title">欢迎使用 AI 智能助手</h1>
        <p class="welcome-subtitle">探索人工智能的无限可能</p>
      </div>

      <!-- 功能卡片区域 -->
      <div class="features-grid">
        <!-- AI对话卡片 -->
        <div class="feature-card" @click="navigateToChat">
          <div class="card-icon chat-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
          </div>
          <h3 class="card-title">AI 对话</h3>
          <p class="card-description">与智能AI进行自然语言对话，获取答案和建议</p>
          <div class="card-button">
            <span>开始对话</span>
            <svg
              class="arrow-icon"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M5 12h14m-7-7l7 7-7 7"></path>
            </svg>
          </div>
        </div>

        <!-- 智能生图卡片 -->
        <div class="feature-card" @click="navigateToImageGen">
          <div class="card-icon image-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
              <circle cx="8.5" cy="8.5" r="1.5"></circle>
              <polyline points="21 15 16 10 5 21"></polyline>
            </svg>
          </div>
          <h3 class="card-title">智能生图</h3>
          <p class="card-description">通过文字描述生成精美图片，释放创意想象力</p>
          <div class="card-button">
            <span>开始创作</span>
            <svg
              class="arrow-icon"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M5 12h14m-7-7l7 7-7 7"></path>
            </svg>
          </div>
        </div>

        <!-- 个人主页卡片 -->
        <div class="feature-card" @click="navigateToProfile">
          <div class="card-icon profile-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
          </div>
          <h3 class="card-title">个人主页</h3>
          <p class="card-description">查看和管理您的个人信息和设置</p>
          <div class="card-button">
            <span>进入主页</span>
            <svg
              class="arrow-icon"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M5 12h14m-7-7l7 7-7 7"></path>
            </svg>
          </div>
        </div>

        <!-- 模型评价卡片 -->
        <div class="feature-card" @click="navigateToReviews">
          <div class="card-icon reviews-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
              <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
          </div>
          <h3 class="card-title">功能评价</h3>
          <p class="card-description">查看其他用户对模型的评价和反馈</p>
          <div class="card-button">
            <span>查看评价</span>
            <svg
              class="arrow-icon"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M5 12h14m-7-7l7 7-7 7"></path>
            </svg>
          </div>
        </div>
      </div>

      <!-- 底部信息 -->
      <div class="footer-info">
        <p>探索更多AI功能，让科技改变生活</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 跳转到AI对话页面
const navigateToChat = () => {
  router.push('/chat')
}

// 跳转到智能生图页面
const navigateToImageGen = () => {
  router.push('/image-gen')
}

// 跳转到个人主页
const navigateToProfile = () => {
  router.push('/profile')
}

// 跳转到评价页面
const navigateToReviews = () => {
  router.push('/reviews')
}

// 处理登出
const handleLogout = () => {
  // 清除localStorage中的token
  localStorage.removeItem('token')

  // 显示登出成功消息
  ElMessage.success('已成功退出登录')

  // 跳转到登录页
  router.push('/login')
}
</script>

<style scoped>
/* 导航页容器 */
.navigation-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 登出按钮区域 */
.logout-section {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  outline: none;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.logout-btn:active {
  transform: translateY(0);
}

.logout-btn svg {
  width: 18px;
  height: 18px;
  stroke-width: 2.5;
}

/* 卡片图标颜色 */
.chat-icon {
  background-image: linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%);
}
.image-icon {
  background-image: linear-gradient(135deg, #f6d365 0%, #fda085 100%);
}
.profile-icon {
  background-image: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%);
}
.reviews-icon {
  background-image: linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%);
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  bottom: 30%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 主要内容 */
.main-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 40px 20px;
}

/* 欢迎区域 */
.welcome-section {
  text-align: center;
  margin-bottom: 60px;
  animation: fadeInUp 1s ease-out;
}

.welcome-title {
  font-size: 3.5rem;
  font-weight: 700;
  color: white;
  margin-bottom: 20px;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  letter-spacing: 2px;
}

.welcome-subtitle {
  font-size: 1.3rem;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 300;
  letter-spacing: 1px;
}

/* 功能卡片网格 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
  max-width: 1000px;
  width: 100%;
  margin-bottom: 60px;
}

/* 功能卡片 */
.feature-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px 30px;
  text-align: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: fadeInUp 1s ease-out 0.3s both;
}

.feature-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  background: rgba(255, 255, 255, 1);
}

.feature-card:nth-child(2) {
  animation-delay: 0.6s;
}

/* 卡片图标 */
.card-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 25px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.feature-card:hover .card-icon {
  transform: scale(1.1);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
}

.card-icon svg {
  width: 40px;
  height: 40px;
  color: white;
  stroke-width: 2.5;
}

/* 卡片文字 */
.card-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 15px;
  letter-spacing: 0.5px;
}

.card-description {
  font-size: 1rem;
  color: #718096;
  line-height: 1.6;
  margin-bottom: 30px;
  min-height: 50px;
}

/* 卡片按钮 */
.card-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #667eea;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.feature-card:hover .card-button {
  gap: 12px;
  color: #764ba2;
}

.arrow-icon {
  width: 20px;
  height: 20px;
  transition: transform 0.3s ease;
}

.feature-card:hover .arrow-icon {
  transform: translateX(4px);
}

/* 底部信息 */
.footer-info {
  text-align: center;
  color: rgba(255, 255, 255, 0.8);
  font-size: 1rem;
  font-weight: 300;
  letter-spacing: 0.5px;
  animation: fadeInUp 1s ease-out 0.9s both;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-title {
    font-size: 2.5rem;
  }

  .welcome-subtitle {
    font-size: 1.1rem;
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .feature-card {
    padding: 30px 20px;
  }

  .card-title {
    font-size: 1.5rem;
  }
}
</style>
