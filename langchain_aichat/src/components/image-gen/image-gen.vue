<template>
  <div class="image-gen-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <button @click="goBack" class="back-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5m7-7l-7 7 7 7" />
        </svg>
        返回导航
      </button>
      <h1 class="page-title">智能生图</h1>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 输入区域 -->
      <!-- 在输入区域上方添加服务状态指示器 -->
      <div class="input-section">
        <div :class="['service-status', serviceStatus]">
          <div class="status-indicator"></div>
          <span>
            {{
              serviceStatus === 'online'
                ? '图像生成服务在线'
                : serviceStatus === 'offline'
                  ? '图像生成服务离线'
                  : '正在检查服务状态...'
            }}
          </span>
        </div>

        <!-- 现有的输入区域内容 -->
        <div class="input-group">
          <label for="prompt">图片描述</label>
          <textarea
            id="prompt"
            v-model="prompt"
            placeholder="请输入你想要生成的图片描述，例如：一只可爱的小猫在花园里玩耍..."
            rows="4"
            class="prompt-input"
          ></textarea>
        </div>

        <!-- 在输入区域添加负面提示词 -->
        <div class="input-group">
          <label for="negativePrompt">负面提示词（可选）</label>
          <textarea
            id="negativePrompt"
            v-model="negativePrompt"
            placeholder="输入不希望在图像中出现的内容，例如：模糊，低质量，扭曲..."
            rows="2"
            class="prompt-input"
          ></textarea>
        </div>

        <!-- 添加更多参数控制 -->
        <div class="controls">
          <div class="control-group">
            <label for="steps">推理步数</label>
            <input
              id="steps"
              v-model.number="steps"
              type="range"
              min="1"
              max="100"
              class="steps-slider"
            />
            <span class="steps-value">{{ steps }}</span>
          </div>

          <div class="control-group">
            <label for="imageSize">图片尺寸</label>
            <select id="imageSize" v-model="imageSize" class="size-select">
              <option value="1024x1024">1024x1024 (1:1)</option>
              <option value="960x1280">960x1280 (3:4)</option>
              <option value="768x1024">768x1024 (3:4)</option>
              <option value="720x1440">720x1440 (1:2)</option>
              <option value="720x1280">720x1280 (9:16)</option>
            </select>
          </div>

          <div class="control-group">
            <label for="batchSize">生成数量</label>
            <select id="batchSize" v-model.number="batchSize" class="size-select">
              <option value="1">1张</option>
              <option value="2">2张</option>
              <option value="3">3张</option>
              <option value="4">4张</option>
            </select>
          </div>

          <div class="control-group">
            <label for="cfgScale">CFG缩放</label>
            <input
              id="cfgScale"
              v-model.number="cfgScale"
              type="range"
              min="0"
              max="20"
              step="0.5"
              class="cfg-slider"
            />
            <span class="cfg-value">{{ cfgScale }}</span>
          </div>

          <div class="control-group">
            <label for="seed">随机种子（可选）</label>
            <input
              id="seed"
              v-model.number="seed"
              type="number"
              min="0"
              max="9999999999"
              placeholder="留空自动生成"
              class="seed-input"
            />
          </div>
        </div>

        <button @click="handleGenerateImage" :disabled="isGenerating" class="generate-btn">
          <span v-if="!isGenerating">生成图片</span>
          <span v-else class="generating">
            <span class="loading-dot"></span>
            <span class="loading-dot"></span>
            <span class="loading-dot"></span>
            生成中...
          </span>
        </button>
      </div>

      <!-- 结果展示区域 -->
      <div class="result-section">
        <div v-if="!generatedImage" class="placeholder">
          <svg
            class="placeholder-icon"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="1"
          >
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
            <circle cx="8.5" cy="8.5" r="1.5"></circle>
            <polyline points="21 15 16 10 5 21"></polyline>
          </svg>
          <p>在上方输入描述并点击生成按钮来创建图片</p>
        </div>

        <div v-else class="image-result">
          <img :src="imageUrl" alt="Generated image" class="generated-image" />

          <!-- 添加图片导航器 -->
          <div v-if="generatedImages.length > 1" class="image-navigator">
            <button @click="previousImage" :disabled="currentImageIndex === 0" class="nav-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="15 18 9 12 15 6"></polyline>
              </svg>
            </button>
            <span class="image-counter"
              >{{ currentImageIndex + 1 }} / {{ generatedImages.length }}</span
            >
            <button
              @click="nextImage"
              :disabled="currentImageIndex === generatedImages.length - 1"
              class="nav-btn"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="9 18 15 12 9 6"></polyline>
              </svg>
            </button>
          </div>

          <div class="image-actions">
            <button @click="downloadImage" class="action-btn download-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="7 10 12 15 17 10"></polyline>
                <line x1="12" y1="15" x2="12" y2="3"></line>
              </svg>
              下载图片
            </button>
            <button @click="generateNew" class="action-btn new-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="1 4 1 10 7 10"></polyline>
                <path d="M3.51 15a9 9 0 1 0 2.13-9.36L1 10"></path>
              </svg>
              重新生成
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { generateImage, checkHealth } from '@/api/aichat'
import { ElMessage } from 'element-plus'

const router = useRouter()
const prompt = ref('')
const negativePrompt = ref('')
const imageSize = ref('1024x1024')
const batchSize = ref(1)
const steps = ref(20)
const cfgScale = ref(7.5)
const seed = ref(null)
const isGenerating = ref(false)
const generatedImage = ref(null)
const imageUrl = ref('')
const serviceStatus = ref('unknown')

// 添加存储所有生成图片的数组
const generatedImages = ref([])
const currentImageIndex = ref(0)

// 返回导航页
const goBack = () => {
  router.push('/navigation')
}

// 检查服务状态
const checkServiceStatus = async () => {
  try {
    // 直接设置为在线，因为SiliconFlow API没有专门的health检查端点
    serviceStatus.value = 'online'
  } catch (error) {
    console.error('检查服务状态失败:', error)
    serviceStatus.value = 'offline'
    ElMessage.error('无法连接到图像生成服务')
  }
}

// 生成图片
const handleGenerateImage = async () => {
  if (!prompt.value.trim()) {
    ElMessage.warning('请输入图片描述')
    return
  }

  // 检查服务状态
  if (serviceStatus.value === 'offline') {
    ElMessage.error('图像生成服务当前不可用，请稍后再试')
    return
  }

  isGenerating.value = true

  try {
    const response = await generateImage({
      prompt: prompt.value,
      negativePrompt: negativePrompt.value,
      imageSize: imageSize.value,
      batchSize: batchSize.value,
      numInferenceSteps: steps.value,
      guidanceScale: cfgScale.value,
      seed: seed.value,
    })

    if (response.data && response.data.images && response.data.images.length > 0) {
      // 存储所有生成的图片
      generatedImages.value = response.data.images.map((image, index) => {
        const imageUrl = image.url ? image.url : `data:image/png;base64,${image.image_base64}`
        return {
          ...image,
          imageUrl,
          index,
        }
      })

      // 显示第一张图片
      currentImageIndex.value = 0
      const firstImage = generatedImages.value[0]
      imageUrl.value = firstImage.imageUrl
      generatedImage.value = firstImage

      ElMessage.success(`成功生成 ${response.data.images.length} 张图片`)
    } else {
      ElMessage.error('图像生成失败：未获取到有效图像数据')
    }
  } catch (error) {
    console.error('生成图片失败:', error)
    ElMessage.error('生成图片失败，请重试')
  } finally {
    isGenerating.value = false
  }
}

// 下载图片
const downloadImage = () => {
  if (!imageUrl.value) return

  const link = document.createElement('a')
  link.href = imageUrl.value
  link.download = `generated-image-${Date.now()}.png`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 重新生成 - 修复为重新发送请求
const generateNew = async () => {
  // 重新调用生成函数，使用相同的参数
  await handleGenerateImage()
}

// 切换图片显示
const switchImage = (index) => {
  if (index >= 0 && index < generatedImages.value.length) {
    currentImageIndex.value = index
    const image = generatedImages.value[index]
    imageUrl.value = image.imageUrl
    generatedImage.value = image
  }
}

// 上一张图片
const previousImage = () => {
  if (currentImageIndex.value > 0) {
    switchImage(currentImageIndex.value - 1)
  }
}

// 下一张图片
const nextImage = () => {
  if (currentImageIndex.value < generatedImages.value.length - 1) {
    switchImage(currentImageIndex.value + 1)
  }
}

// 组件挂载时检查服务状态
import { onMounted } from 'vue'
onMounted(() => {
  checkServiceStatus()
})
</script>

<style scoped>
/* 生图页面容器 */
.image-gen-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 顶部导航 */
.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 15px 30px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: #5a67d8;
  transform: translateY(-2px);
}

.back-btn svg {
  width: 18px;
  height: 18px;
}

.page-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

/* 主要内容 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  align-items: start;
}

/* 输入区域 */
.input-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.input-group {
  margin-bottom: 25px;
}

.input-group label {
  display: block;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 10px;
  font-size: 14px;
}

.prompt-input {
  width: 100%;
  padding: 15px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  resize: vertical;
  transition: all 0.3s ease;
  font-family: inherit;
}

.prompt-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 控制区域 */
.controls {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.control-group {
  display: flex;
  align-items: center;
  gap: 15px;
}

.control-group label {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
  min-width: 80px;
}

.steps-slider {
  flex: 1;
  height: 6px;
  border-radius: 3px;
  background: #e2e8f0;
  outline: none;
  -webkit-appearance: none;
}

.steps-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #667eea;
  cursor: pointer;
  transition: all 0.3s ease;
}

.steps-slider::-webkit-slider-thumb:hover {
  background: #5a67d8;
  transform: scale(1.1);
}

.steps-value {
  font-weight: 600;
  color: #667eea;
  min-width: 30px;
  text-align: center;
}

.generate-btn {
  padding: 15px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.generate-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.generate-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* 加载动画 */
.generating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: white;
  animation: loading 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes loading {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

/* 结果展示区域 */
.result-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder {
  text-align: center;
  color: #a0aec0;
}

.placeholder-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.placeholder p {
  font-size: 16px;
  margin: 0;
}

.image-result {
  text-align: center;
  width: 100%;
}

.generated-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  margin-bottom: 20px;
}

.image-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.download-btn {
  background: #48bb78;
  color: white;
}

.download-btn:hover {
  background: #38a169;
  transform: translateY(-2px);
}

.new-btn {
  background: #ed8936;
  color: white;
}

.new-btn:hover {
  background: #dd6b20;
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px 15px;
  }

  .header {
    padding: 10px 15px;
  }

  .page-title {
    font-size: 1.2rem;
  }

  .input-section,
  .result-section {
    padding: 20px;
  }
}
.size-select {
  padding: 8px 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.size-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.cfg-slider {
  flex: 1;
  height: 6px;
  border-radius: 3px;
  background: #e2e8f0;
  outline: none;
  -webkit-appearance: none;
}

.cfg-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #764ba2;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cfg-slider::-webkit-slider-thumb:hover {
  background: #6b46a1;
  transform: scale(1.1);
}

.cfg-value {
  font-weight: 600;
  color: #764ba2;
  min-width: 40px;
  text-align: center;
}

/* 服务状态指示器 */
.service-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
  padding: 10px;
  border-radius: 8px;
  font-size: 14px;
}

.service-status.online {
  background: rgba(72, 187, 120, 0.1);
  color: #38a169;
}

.service-status.offline {
  background: rgba(245, 108, 108, 0.1);
  color: #e53e3e;
}

.service-status.unknown {
  background: rgba(237, 137, 54, 0.1);
  color: #dd6b20;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 currentColor;
  }
  70% {
    box-shadow: 0 0 0 6px transparent;
  }
  100% {
    box-shadow: 0 0 0 0 transparent;
  }
}

/* 图片导航器 */
.image-navigator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  margin-bottom: 20px;
}

.nav-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: #667eea;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-btn:hover:not(:disabled) {
  background: #5a67d8;
  transform: scale(1.1);
}

.nav-btn:disabled {
  background: #cbd5e0;
  cursor: not-allowed;
  opacity: 0.6;
}

.nav-btn svg {
  width: 16px;
  height: 16px;
}

.image-counter {
  font-size: 14px;
  font-weight: 600;
  color: #4a5568;
  background: #f7fafc;
  padding: 6px 12px;
  border-radius: 20px;
}

.download-btn {
  background: #48bb78;
  color: white;
}

.download-btn:hover {
  background: #38a169;
  transform: translateY(-2px);
}

.new-btn {
  background: #ed8936;
  color: white;
}

.new-btn:hover {
  background: #dd6b20;
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px 15px;
  }

  .header {
    padding: 10px 15px;
  }

  .page-title {
    font-size: 1.2rem;
  }

  .input-section,
  .result-section {
    padding: 20px;
  }
}
.size-select {
  padding: 8px 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.size-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.cfg-slider {
  flex: 1;
  height: 6px;
  border-radius: 3px;
  background: #e2e8f0;
  outline: none;
  -webkit-appearance: none;
}

.cfg-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #764ba2;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cfg-slider::-webkit-slider-thumb:hover {
  background: #6b46a1;
  transform: scale(1.1);
}

.cfg-value {
  font-weight: 600;
  color: #764ba2;
  min-width: 40px;
  text-align: center;
}

/* 服务状态指示器 */
.service-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
  padding: 10px;
  border-radius: 8px;
  font-size: 14px;
}

.service-status.online {
  background: rgba(72, 187, 120, 0.1);
  color: #38a169;
}

.service-status.offline {
  background: rgba(245, 108, 108, 0.1);
  color: #e53e3e;
}

.service-status.unknown {
  background: rgba(237, 137, 54, 0.1);
  color: #dd6b20;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 currentColor;
  }
  70% {
    box-shadow: 0 0 0 6px transparent;
  }
  100% {
    box-shadow: 0 0 0 0 transparent;
  }
}
</style>
