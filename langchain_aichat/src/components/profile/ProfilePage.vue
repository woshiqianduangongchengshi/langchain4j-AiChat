<template>
  <div class="profile-container">
    <div class="header">
      <button @click="goBack" class="back-btn">&lt; 返回导航</button>
      <h1 class="page-title">个人中心</h1>
    </div>
    <div class="main-content">
      <div class="info-card">
        <h2>基本信息</h2>
        <div class="info-item">
          <span class="label">用户名:</span>
          <span class="value">{{ user.username }}</span>
        </div>
        <div class="info-item">
          <span class="label">生日:</span>
          <span class="value">{{ user.birthday }}</span>
        </div>
      </div>
      <div class="password-card">
        <h2>修改密码</h2>
        <div class="input-group">
          <label for="current-password">当前密码</label>
          <input
            type="password"
            id="current-password"
            v-model="passwordForm.currentPassword"
            placeholder="请输入当前密码"
          />
        </div>
        <div class="input-group">
          <label for="new-password">新密码</label>
          <input
            type="password"
            id="new-password"
            v-model="passwordForm.newPassword"
            placeholder="请输入新密码"
          />
        </div>
        <div class="input-group">
          <label for="confirm-password">确认新密码</label>
          <input
            type="password"
            id="confirm-password"
            v-model="passwordForm.confirmPassword"
            placeholder="请再次输入新密码"
          />
        </div>
        <button @click="handleChangePassword" class="submit-btn">确认修改</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCurrentUser, changePassword } from '@/api/login'

const router = useRouter()

// 用户信息
const user = ref({
  username: '',
  birthday: '',
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})

// 格式化日期，只显示日期部分
const formatDate = (dateString) => {
  if (!dateString) return ''
  // 如果日期字符串包含T，说明是ISO格式，只取T之前的部分
  if (dateString.includes('T')) {
    return dateString.split('T')[0]
  }
  // 如果不是ISO格式，直接返回原字符串
  return dateString
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await getCurrentUser()
    console.log('获取用户信息成功:', response)

    if (response) {
      user.value = {
        username: response.data.username,
        birthday: formatDate(response.data.birthday),
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
    // 如果获取用户信息失败，可能是token过期，跳转到登录页
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
  }
}

const goBack = () => {
  router.push('/navigation')
}

const handleChangePassword = async () => {
  if (!passwordForm.currentPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
    ElMessage.warning('请填写所有密码字段')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }

  try {
    // 调用后端API修改密码
    const response = await changePassword(passwordForm.currentPassword, passwordForm.newPassword)
    
    if (response.data && response.data.code === 200) {
      ElMessage.success('密码修改成功')
      
      // 清空表单
      passwordForm.currentPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    } else {
      ElMessage.error(response.data?.message || '密码修改失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    
    if (error.response) {
      // 处理不同的错误状态码
      switch (error.response.status) {
        case 400:
          ElMessage.error('请求参数错误')
          break
        case 401:
          ElMessage.error('未授权，请重新登录')
          localStorage.removeItem('token')
          router.push('/login')
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error('密码修改失败')
      }
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
  }
}

// 页面加载时获取用户信息
onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
/* 样式将与项目整体风格保持一致 */
.profile-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
}

.page-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #333;
}

.main-content {
  display: grid;
  grid-template-columns: 1fr 1.5fr;
  gap: 30px;
  max-width: 1000px;
  margin: 0 auto;
}

.info-card,
.password-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-top: 0;
  margin-bottom: 25px;
  font-size: 1.4rem;
  color: #4a5568;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 10px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 16px;
}

.label {
  font-weight: 600;
  color: #718096;
}
.value {
  color: #2d3748;
}

.input-group {
  margin-bottom: 20px;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #4a5568;
}

.input-group input {
  width: 100%;
  padding: 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.input-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.submit-btn,
.back-btn {
  padding: 12px 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover,
.back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}
</style>
