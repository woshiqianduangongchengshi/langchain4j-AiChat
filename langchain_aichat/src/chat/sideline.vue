<template>
  <div class="sideline-container">
    <!-- 顶部创建按钮区域 -->
    <div class="sideline-header">
      <button @click="showCreateDialog = true" class="new-chat-btn">
        <span class="plus-icon">+</span> 新建对话
      </button>
    </div>

    <!-- 历史对话标题 -->
    <div class="history-title">历史对话</div>

    <!-- 中间历史对话区域 -->
    <div class="sessions-list">
      <!-- 添加v-if判断，确保只有当sessions有数据时才渲染 -->
      <div
        v-for="session in sessions"
        :key="session.id"
        class="session-item"
        :class="{ active: session.id === currentSession?.id }"
        @click="selectSession(session)"
      >
        <div class="session-name">{{ session.sessionName || '未命名对话' }}</div>
        <!-- 修改这里，使用session.sessionId -->
        <button @click.stop="handleDeleteSession(session.sessionId)" class="delete-btn">×</button>
      </div>
      <!-- 当没有会话时显示提示 -->
      <div v-if="sessions.length === 0" class="no-sessions">
        暂无对话历史
        <button @click="handleCreateSession('默认对话')" class="create-default-btn">
          创建默认对话
        </button>
      </div>
    </div>

    <!-- 底部登出区域 -->
    <div class="sideline-footer">
      <button @click="handleLogout" class="logout-btn">登出</button>
    </div>

    <!-- 创建新对话弹窗 -->
    <div v-if="showCreateDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>创建新对话</h3>
        <input
          v-model="newSessionName"
          type="text"
          placeholder="请输入对话名称"
          class="dialog-input"
          required
        />
        <div class="dialog-buttons">
          <button @click="showCreateDialog = false" class="cancel-btn">取消</button>
          <button
            @click="handleCreateSession()"
            class="confirm-btn"
            :disabled="
              !newSessionName || typeof newSessionName !== 'string' || !newSessionName.trim()
            "
          >
            确认
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 导入必要的库
import { ref, onMounted } from 'vue'
import { createSession, getUserSessions, deleteSession } from '@/api/aichat.js'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

// 路由实例
const router = useRouter()

// 响应式变量
const sessions = ref([])
const currentSession = ref(null)
const showCreateDialog = ref(false)
const newSessionName = ref('')
const isInitialLoad = ref(true)

// 定义事件
const emit = defineEmits(['sessionChange'])

// 创建默认会话
const createDefaultSession = async () => {
  try {
    const response = await createSession('默认对话')
    let newSession = response.data || {}
    // 确保新会话有id属性
    newSession = {
      ...newSession,
      id: newSession.id || newSession.sessionId,
    }
    sessions.value.push(newSession)
    selectSession(newSession)
  } catch (error) {
    console.error('创建默认会话失败:', error)
    ElMessage.error('创建默认会话失败，请手动创建')
  }
}

// 选择会话
const selectSession = (session) => {
  currentSession.value = session
  emit('sessionChange', session)
}

// 处理创建新会话
const handleCreateSession = async (defaultName = '') => {
  // 如果提供了默认名称，直接使用
  if (defaultName) {
    newSessionName.value = defaultName
  }

  // 确保会话名称不为空，增加类型检查
  const sessionName =
    newSessionName.value && typeof newSessionName.value === 'string'
      ? newSessionName.value.trim() || '未命名对话'
      : '未命名对话'

  try {
    // 添加日志：显示发送给后端的请求数据
    console.log('=== 创建新会话 ===')
    console.log('发送给后端的会话名称:', sessionName)
    console.log('请求参数:', { sessionName })

    // 使用请求体传递参数
    const response = await createSession(sessionName)
    let newSession = response.data || {}

    // 添加日志：显示后端返回的完整响应数据
    console.log('=== 后端响应信息 ===')
    console.log('完整响应对象:', response)
    console.log('响应状态码:', response.status)
    console.log('响应状态文本:', response.statusText)
    console.log('响应头:', response.headers)
    console.log('后端返回的会话数据:', response.data)

    // 统一处理返回的会话数据结构
    newSession = {
      ...newSession,
      id: newSession.id,
      sessionId: newSession.sessionId,
      sessionName: sessionName, // 直接使用用户输入的名称
    }

    // 添加日志：显示处理后的会话数据
    console.log('=== 处理后的会话数据 ===')
    console.log('新会话对象:', newSession)

    sessions.value.unshift(newSession) // 新会话添加到最前面
    selectSession(newSession)
    newSessionName.value = ''
    showCreateDialog.value = false
  } catch (error) {
    console.error('创建会话失败:', error)
    ElMessage.error(`创建会话失败: ${error.message || '未知错误'}`)
  }
}

// 加载用户会话
const loadUserSessions = async () => {
  try {
    const response = await getUserSessions()
    sessions.value = Array.isArray(response.data) ? response.data : []

    // 确保每个会话都有id和sessionId
    sessions.value = sessions.value.map((session) => ({
      ...session,
      id: session.id,
      sessionId: session.sessionId,
    }))

    // 如果有多个会话，按updatedAt排序，最新的排在前面
    if (sessions.value.length > 1) {
      sessions.value.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
    }

    // 首次加载且无会话时，自动创建默认会话
    if (isInitialLoad.value && sessions.value.length === 0) {
      await createDefaultSession()
    } else if (sessions.value.length > 0) {
      // 有会话时默认选择第一个（最新的）
      selectSession(sessions.value[0])
    }
  } catch (error) {
    console.error('加载会话失败:', error)
    ElMessage.error('加载会话失败，请刷新页面重试')
    // 加载失败且是首次加载时尝试创建默认会话
    if (isInitialLoad.value) {
      await createDefaultSession()
    }
  } finally {
    isInitialLoad.value = false
  }
}

// 处理删除会话
const handleDeleteSession = async (sessionId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个会话吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    console.log('sessionId', sessionId)

    // 用户点击确定后的逻辑
    await deleteSession(sessionId)
    // 从列表中移除会话，使用sessionId而不是id
    sessions.value = sessions.value.filter((session) => session.sessionId !== sessionId)
    // 如果删除的是当前选中的会话，则选择第一个会话或显示空状态
    if (currentSession.value && currentSession.value.sessionId === sessionId) {
      if (sessions.value.length > 0) {
        selectSession(sessions.value[0])
      } else {
        currentSession.value = null
        emit('sessionChange', null)
        // 可以选择创建新会话
        // await createDefaultSession()
      }
    }
  } catch (error) {
    // 用户取消操作时的处理
    if (error.name !== 'ElMessageBoxCancel') {
      console.error('删除会话失败:', error)
      ElMessage.error(`删除会话失败: ${error.message || '未知错误'}`)
    }
  }
}

// 处理登出
const handleLogout = async () => {
  try {
    // 清除可能的登录状态（如token）
    localStorage.removeItem('token')
    // 跳转到登录页
    await router.push('/login')
    // 显示登出成功提示
    ElMessage.success('登出成功')
  } catch (error) {
    console.error('登出失败:', error)
    ElMessage.error('登出失败，请重试')
  }
}

// 组件挂载时加载会话
onMounted(() => {
  loadUserSessions()
})
</script>

<style scoped>
/* 基础样式 */
.sideline-container {
  width: 280px;
  height: 100vh;
  background: white;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 历史对话标题 */
.history-title {
  padding: 12px 16px;
  color: #4e5969;
  font-size: 14px;
  font-weight: 600;
  border-bottom: 1px solid #f2f3f5;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 顶部区域 */
.sideline-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.new-chat-btn {
  width: 100%;
  padding: 10px;
  background: linear-gradient(to right, #1a2a6c, #b21f1f, #fdbb2d);
  background-size: 200% auto;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.plus-icon {
  margin-right: 8px;
  font-weight: bold;
}

.new-chat-btn:hover {
  background-position: right center;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(26, 42, 108, 0.3);
}

/* 中间会话列表区域 */
.sessions-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.sessions-list::-webkit-scrollbar {
  width: 6px;
}

.sessions-list::-webkit-scrollbar-track {
  background: #f7f8fa;
  border-radius: 3px;
}

.sessions-list::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

.sessions-list::-webkit-scrollbar-thumb:hover {
  background: #c0c4cc;
}

.session-item {
  padding: 14px 16px;
  border-radius: 10px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #ffffff;
  border: 1px solid transparent;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
}

.session-item:hover {
  background-color: #f7f8fa;
  border-color: #e5e6eb;
  transform: translateX(2px);
}

.session-item.active {
  background-color: #eef0ff;
  color: #4066ff;
  border-color: #d9e0ff;
  font-weight: 500;
}

.session-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 3px;
  background-color: #4066ff;
  border-radius: 0 3px 3px 0;
}

.session-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
  line-height: 1.4;
}

.delete-btn {
  background: transparent;
  border: none;
  color: #909399;
  font-size: 16px;
  cursor: pointer;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s;
}

.session-item:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background-color: #f5f5f5;
  color: #b21f1f;
}

/* 底部登出区域 */
.sideline-footer {
  padding: 16px;
  border-top: 1px solid #f2f3f5;
}

.logout-btn {
  width: 100%;
  padding: 10px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  color: #707683;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.logout-btn:hover {
  background: linear-gradient(135deg, #e4e7ed 0%, #dcdfe6 100%);
  color: #f56c6c;
  border-color: #dcdfe6;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.15);
}

/* 弹窗样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.dialog {
  background: white;
  border-radius: 12px;
  padding: 24px;
  width: 350px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.dialog h3 {
  margin-top: 0;
  margin-bottom: 16px;
  color: #1a2a6c;
  font-size: 20px;
}

.dialog-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.dialog-input:focus {
  outline: none;
  border-color: #1a2a6c;
  box-shadow: 0 0 0 3px rgba(26, 42, 108, 0.1);
}

.dialog-input:invalid {
  border-color: #ff4d4f;
}

.dialog-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn {
  padding: 8px 16px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn:hover {
  background: #e0e0e0;
}

.confirm-btn {
  padding: 8px 16px;
  background: linear-gradient(to right, #1a2a6c, #b21f1f, #fdbb2d);
  background-size: 200% auto;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.confirm-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background-position: initial;
}

.confirm-btn:hover:not(:disabled) {
  background-position: right center;
}

/* 无会话时的样式 */
.no-sessions {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
  text-align: center;
  padding: 20px;
  background-color: #fcfcfd;
  border-radius: 12px;
  margin: 10px;
}

.create-default-btn {
  margin-top: 15px;
  padding: 8px 16px;
  background-color: #4096ff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
}

.create-default-btn:hover {
  background-color: #2583f6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 150, 255, 0.2);
}
</style>
