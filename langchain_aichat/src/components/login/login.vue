<template>
  <div class="login-container">
    <!-- 波浪形边框背景 -->
    <div class="wave-background"></div>
    
    <div class="login-card">
      <h1 class="title">AI Chat 登录</h1>
      <div class="form-group">
        <input type="text" placeholder="请输入用户名" v-model="username" class="input" />
      </div>
      <div class="form-group">
        <input type="password" placeholder="请输入密码" v-model="password" class="input" />
      </div>
      <button class="login-btn" @click="handleLogin">登录</button>
      <div class="register-tip">
        还没有账号？<router-link to="/register" class="register-link">立即注册</router-link>
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
import { login } from '@/api/login.js'
import { ElMessage } from 'element-plus' // 引入Element Plus消息组件

const username = ref('')
const password = ref('')
const router = useRouter()

const handleLogin = async () => {
  if (!username.value || !password.value) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  try {
    const response = await login(username.value, password.value)
    // 登录成功后保存token和用户信息
    const { token, id, name, type } = response.data
    localStorage.setItem('token', token)
    localStorage.setItem('userInfo', JSON.stringify({ id, name, type }))
    localStorage.setItem('isLoggedIn', 'true')
    
    ElMessage.success('登录成功！')
    // 在登录成功的地方，将原来的 router.push('/chat') 改为：
    router.push('/navigation')
  } catch (error) {
    // 处理错误情况
    if (error.response) {
      ElMessage.error(`登录失败：${error.response.data.message || '用户名或密码错误'}`)
    } else {
      ElMessage.error(`网络请求失败：${error.message}`)
    }
  }
}
</script>

<style scoped>
/* 重置全局默认样式，避免浏览器差异 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 让body成为真正的全屏容器基础 */
html,
body {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden; /* 避免滚动条干扰（可根据需求调整） */
}

/* 登录容器：全屏覆盖 + 渐变背景 + 居中内容 */
.login-container {
  width: 100%;
  height: 100vh; /* 强制占满视口高度 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1a2a6c, #b21f1f, #fdbb2d);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
  position: relative;
  overflow: hidden;
  /* 修复背景可能出现的重复或空白问题 */
  background-repeat: no-repeat;
  background-attachment: fixed;
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

/* 渐变动画关键帧（保持原逻辑） */
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

/* 登录卡片：优化宽高适配 + 视觉层次 */
.login-card {
  background: rgba(255, 255, 255, 0.95);
  padding: 40px 30px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 500px;
  backdrop-filter: blur(5px);
  transition: transform 0.3s ease;
  position: relative;
  z-index: 2;
  margin-bottom: 30px;
}

.login-card:hover {
  transform: translateY(-5px);
}

/* 标题、输入框、按钮等样式保持原逻辑，仅确保继承容器适配性 */
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

.login-btn {
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

.login-btn:hover {
  background-position: right center;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(26, 42, 108, 0.3);
}

.register-tip {
  text-align: center;
  margin-top: 25px;
  color: #666;
  font-size: 14px;
}

.register-link {
  color: #1a2a6c;
  text-decoration: none;
  margin-left: 4px;
  font-weight: 500;
  transition: color 0.3s;
}

.register-link:hover {
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
