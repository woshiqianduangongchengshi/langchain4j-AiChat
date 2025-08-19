<template>
  <div class="register-container">
    <!-- 波浪形边框背景 -->
    <div class="wave-background"></div>
    
    <div class="register-card">
      <h1 class="title">AI Chat 注册</h1>
      <!-- 姓名输入框 -->
      <div class="form-group">
        <input type="text" placeholder="请输入姓名" v-model="name" class="input" />
      </div>
      <!-- 密码输入框 -->
      <div class="form-group">
        <input type="password" placeholder="请输入密码" v-model="password" class="input" />
      </div>
      <!-- 生日选择框 -->
      <div class="form-group">
        <input type="date" v-model="birthday" class="input" />
      </div>
      <!-- 注册按钮 -->
      <button class="register-btn" @click="handleRegister">注册</button>
      <!-- 跳转登录链接 -->
      <div class="login-tip">
        已有账号？<router-link to="/login" class="login-link">立即登录</router-link>
      </div>
    </div>
    
    <!-- 添加底部功能展示区域 -->
    <div class="feature-section">
      <div class="feature-item">
        <div class="feature-icon">💬</div>
        <div class="feature-title">智能对话</div>
        <div class="feature-desc">与AI进行自然语言交流</div>
      </div>
      <div class="feature-item">
        <div class="feature-icon">⚙️</div>
        <div class="feature-title">个性化服务</div>
        <div class="feature-desc">根据您的需求提供定制化答案</div>
      </div>
      <div class="feature-item">
        <div class="feature-icon">⭐</div>
        <div class="feature-title">高效便捷</div>
        <div class="feature-desc">快速响应，即时解答</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/login.js'

const name = ref('')
const password = ref('')
const birthday = ref('')
const router = useRouter()

const handleRegister = async () => {
  if (!name.value || !password.value || !birthday.value) {
    ElMessage.warning('请填写姓名、密码和生日')
    return
  }

  try {
    const response = await register(name.value, password.value, birthday.value)
    console.log('注册响应:', response)

    ElMessage.success('注册成功！')
    router.push('/login')
  } catch (error) {
    console.error('注册错误详情:', error)
    ElMessage.error(`注册失败：${error.response?.data?.message || error.message}`)
  }
}
</script>

<style scoped>
/* 改进的样式 - 重点解决占满屏幕问题 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 确保html和body占满整个屏幕 */
html,
body {
  height: 100%;
  margin: 0;
  padding: 0;
}

.register-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1a2a6c, #b21f1f, #fdbb2d);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
  padding: 20px;
  margin: 0;
  width: 100%;
  position: relative;
  overflow: hidden;
}

/* 波浪形边框背景 */
.wave-background {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1200 120' preserveAspectRatio='none'%3E%3Cpath d='M321.39,56.44c58-10.79,114.16-30.13,172-41.86,82.39-16.72,168.19-17.73,250.45-.39C823.78,31,906.67,72,985.66,92.83c70.05,18.48,146.53,26.09,214.34,3V0H0V27.35A600.21,600.21,0,0,0,321.39,56.44Z' fill='%23ffffff' fill-opacity='0.1'/%3E%3C/svg%3E") repeat-x;
  background-size: 1200px 100px;
  z-index: 1;
}

@keyframes gradientBG {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  padding: 40px 30px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 500px; /* 适当增大最大宽度 */
  backdrop-filter: blur(5px);
  transition: transform 0.3s ease;
  position: relative;
  z-index: 2;
  margin-bottom: 30px;
}

.register-card:hover {
  transform: translateY(-5px);
}

.title {
  color: #1a2a6c;
  text-align: center;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1px;
}

.form-group {
  margin-bottom: 25px;
}

.input {
  width: 100%;
  padding: 14px 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s;
  background-color: rgba(255, 255, 255, 0.8);
}

.input:focus {
  outline: none;
  border-color: #1a2a6c;
  box-shadow: 0 0 0 3px rgba(26, 42, 108, 0.1);
  background-color: white;
}

.register-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(to right, #1a2a6c, #b21f1f, #fdbb2d);
  background-size: 200% auto;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 0.5px;
}

.register-btn:hover {
  background-position: right center;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(26, 42, 108, 0.3);
}

.login-tip {
  text-align: center;
  margin-top: 25px;
  color: #666;
  font-size: 14px;
}

.login-link {
  color: #1a2a6c;
  text-decoration: none;
  margin-left: 4px;
  font-weight: 500;
  transition: color 0.3s;
}

.login-link:hover {
  color: #b21f1f;
  text-decoration: underline;
}

/* 新增功能展示区域样式 */
.feature-section {
  display: flex;
  justify-content: space-around;
  align-items: center;
  width: 100%;
  max-width: 800px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 2;
  margin-top: 20px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 15px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
  flex: 1;
  margin: 0 10px;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-3px);
}

.feature-icon {
  font-size: 40px;
  margin-bottom: 10px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.feature-title {
  font-size: 16px;
  font-weight: bold;
  color: white;
  margin-bottom: 8px;
}

.feature-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .feature-section {
    flex-direction: column;
    padding: 15px;
  }
  
  .feature-item {
    margin: 5px 0;
    width: 100%;
  }
}
</style>
