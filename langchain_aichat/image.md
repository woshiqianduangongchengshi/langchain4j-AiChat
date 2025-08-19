# Qwen-Image 图像生成 API 文档
## 概述
本文档描述了基于 Qwen-Image 模型的图像生成 API 接口。该接口使用本地部署的 Qwen-Image 模型，通过文本提示生成高质量图像。

## 基础信息
- 基础URL : http://localhost:8080
- API版本 : v1
- 认证方式 : JWT Bearer Token
- 内容类型 : application/json
## 接口列表
### 1. 生成图像
接口地址 : POST /api/image-generation/generate

描述 : 根据文本提示生成图像

请求参数 :

```
{
  "prompt": "一只可爱的小猫在花园里玩耍
  ",
  "negativePrompt": "模糊, 低质量, 扭
  曲",
  "width": 512,
  "height": 512,
  "steps": 20,
  "cfgScale": 7.5
}
```
参数说明 :

参数名 类型 必填 默认值 说明 prompt String 是 - 图像生成的正面提示词 negativePrompt String 否 "" 图像生成的负面提示词 width Integer 否 512 生成图像的宽度 height Integer 否 512 生成图像的高度 steps Integer 否 20 生成步数，影响图像质量 cfgScale Double 否 7.5 CFG缩放参数，控制提示词影响程度

响应格式 :

```
{
  "success": true,
  "message": "图像生成成功",
  "imageBase64": 
  "iVBORw0KGgoAAAANSUhEUgAAAQAAAAEAC
  AYAAABccqhmAAAABHNCSVQICAgIfAhkiAA
  AAAlwSFlzAAA..."
}
```
响应说明 :

字段名 类型 说明 success Boolean 生成是否成功 message String 响应消息 imageBase64 String Base64编码的图像数据

### 2. 健康检查
接口地址 : GET /api/image-generation/health

描述 : 检查图像生成服务状态

请求参数 : 无

响应格式 :

```
{
  "status": "UP",
  "modelLoaded": true,
  "modelPath": 
  "D:\\LM_modle\\modles\\Qwen-Image"
  ,
  "timestamp": "2025-08-02T13:15:30.
  456Z"
}
```
响应说明 :

字段名 类型 说明 status String 服务状态 (UP/DOWN) modelLoaded Boolean 模型是否已加载 modelPath String 模型文件路径 timestamp String 检查时间戳

## 前端调用示例
### JavaScript 示例
```
// 生成图像
async function generateImage
(prompt, options = {}) {
  const defaultOptions = {
    negativePrompt: "",
    width: 512,
    height: 512,
    steps: 20,
    cfgScale: 7.5
  };

  const requestBody = {
    prompt,
    ...defaultOptions,
    ...options
  };

  try {
    const response = await fetch
    ('http://localhost:8080/api/
    image-generation/generate', {
      method: 'POST',
      headers: {
        'Content-Type': 
        'application/json',
        'Authorization': `Bearer $
        {yourJwtToken}`
      },
      body: JSON.stringify
      (requestBody)
    });

    const result = await response.
    json();
    
    if (result.success) {
      // 将Base64图像显示在页面上
      const imgElement = document.
      createElement('img');
      imgElement.src = `data:image/
      png;base64,${result.
      imageBase64}`;
      document.body.appendChild
      (imgElement);
    } else {
      console.error('图像生成失败:', 
      result.message);
    }
    
    return result;
  } catch (error) {
    console.error('请求失败:', 
    error);
    throw error;
  }
}

// 使用示例
generateImage("一只可爱的小猫在花园里玩
耍", {
  negativePrompt: "模糊, 低质量",
  width: 512,
  height: 512
});

// 健康检查
async function checkHealth() {
  try {
    const response = await fetch
    ('http://localhost:8080/api/
    image-generation/health');
    const health = await response.
    json();
    console.log('服务状态:', health);
    return health;
  } catch (error) {
    console.error('健康检查失败:', 
    error);
    throw error;
  }
}
```
### React 示例
```
import React, { useState } from 
'react';

const ImageGenerator = () => {
  const [prompt, setPrompt] = 
  useState('');
  const [negativePrompt, 
  setNegativePrompt] = useState('');
  const [loading, setLoading] = 
  useState(false);
  const [generatedImage, 
  setGeneratedImage] = useState
  (null);
  const [error, setError] = useState
  ('');

  const handleGenerate = async () 
  => {
    if (!prompt.trim()) {
      setError('请输入提示词');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await fetch
      ('/api/image-generation/
      generate', {
        method: 'POST',
        headers: {
          'Content-Type': 
          'application/json',
          'Authorization': `Bearer $
          {localStorage.getItem
          ('jwtToken')}`
        },
        body: JSON.stringify({
          prompt,
          negativePrompt,
          width: 512,
          height: 512,
          steps: 20,
          cfgScale: 7.5
        })
      });

      const result = await response.
      json();
      
      if (result.success) {
        setGeneratedImage
        (`data:image/png;base64,$
        {result.imageBase64}`);
      } else {
        setError(result.message || '
        图像生成失败');
      }
    } catch (err) {
      setError('网络请求失败');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div 
    className="image-generator">
      <h2>Qwen-Image 图像生成器</h2>
      
      <div className="form-group">
        <label>提示词:</label>
        <textarea
          value={prompt}
          onChange={(e) => setPrompt
          (e.target.value)}
          placeholder="输入你想要生成
          的图像描述..."
          rows={3}
        />
      </div>
      
      <div className="form-group">
        <label>负面提示词 (可选):</
        label>
        <textarea
          value={negativePrompt}
          onChange={(e) => 
          setNegativePrompt(e.
          target.value)}
          placeholder="输入不希望出现
          在图像中的内容..."
          rows={2}
        />
      </div>
      
      <button 
        onClick={handleGenerate} 
        disabled={loading}
        className="generate-btn"
      >
        {loading ? '生成中...' : '生
        成图像'}
      </button>
      
      {error && <div 
      className="error-message">
      {error}</div>}
      
      {generatedImage && (
        <div className="result">
          <h3>生成结果:</h3>
          <img src={generatedImage} 
          alt="Generated" 
          className="generated-image
          " />
          <a 
            href={generatedImage} 
            download="generated-imag
            e.png"
            className="download-btn"
          >
            下载图像
          </a>
        </div>
      )}
    </div>
  );
};

export default ImageGenerator;
```
## convertNDListToImage 方法实现建议
在 ImageGenerationService.java 中， convertNDListToImage 方法负责将模型的输出转换为图像对象。以下是几种实现方式：

### 方式一：直接提取图像数据
```
private Image convertNDListToImage
(NDList ndList) {
    try {
        // 假设输出列表的第一个元素就是
        图像数据
        NDArray imageData = ndList.
        get(0);
        
        // 将NDArray转换为
        BufferedImage
        BufferedImage bufferedImage 
        = new BufferedImage(
            (int) imageData.getShape
            ().get(1),  // 宽度
            (int) imageData.getShape
            ().get(0),  // 高度
            BufferedImage.
            TYPE_INT_RGB
        );
        
        // 将数据填充到BufferedImage
        for (int y = 0; y < 
        imageData.getShape().get
        (0); y++) {
            for (int x = 0; x < 
            imageData.getShape().get
            (1); x++) {
                int r = (int) 
                (imageData.get(y, 
                x, 0).toFloat() * 
                255);
                int g = (int) 
                (imageData.get(y, 
                x, 1).toFloat() * 
                255);
                int b = (int) 
                (imageData.get(y, 
                x, 2).toFloat() * 
                255);
                int rgb = (r << 
                16) | (g << 8) | b;
                bufferedImage.setRGB
                (x, y, rgb);
            }
        }
        
        return ImageFactory.
        getInstance().fromImage
        (bufferedImage);
    } catch (Exception e) {
        logger.error("转换NDList到图
        像失败", e);
        throw new RuntimeException("
        图像转换失败", e);
    }
}
```
### 方式二：查找图像张量
```
private Image convertNDListToImage
(NDList ndList) {
    try {
        // 查找图像张量，可能在不同的位
        置
        NDArray imageData = null;
        
        // 尝试不同的键名
        String[] possibleKeys = 
        {"image", "output", 
        "generated", "result"};
        for (String key : 
        possibleKeys) {
            if (ndList.contains
            (key)) {
                imageData = ndList.
                get(key);
                break;
            }
        }
        
        // 如果没有找到键，尝试获取第一
        个元素
        if (imageData == null && 
        ndList.size() > 0) {
            imageData = ndList.get
            (0);
        }
        
        if (imageData == null) {
            throw new 
            RuntimeException("无法在
            输出中找到图像数据");
        }
        
        // 数据预处理和转换
        imageData = 
        preprocessImageData
        (imageData);
        
        // 转换为BufferedImage
        BufferedImage bufferedImage 
        = convertToBufferedImage
        (imageData);
        
        return ImageFactory.
        getInstance().fromImage
        (bufferedImage);
    } catch (Exception e) {
        logger.error("转换NDList到图
        像失败", e);
        throw new RuntimeException("
        图像转换失败", e);
    }
}

private NDArray preprocessImageData
(NDArray imageData) {
    // 根据需要进行数据预处理
    // 例如：归一化、维度调整等
    return imageData;
}

private BufferedImage 
convertToBufferedImage(NDArray 
imageData) {
    // 实现具体的转换逻辑
    // ... 
}
```
### 方式三：字典形式获取
```
private Image convertNDListToImage
(NDList ndList) {
    try {
        // 假设输出是字典形式，包含多个
        键值对
        NDArray imageData = null;
        
        // 检查是否是字典形式的输出
        if (ndList.size() == 1 && 
        ndList.get(0).isScalar()) {
            // 可能是单个标量值，需要特
            殊处理
            logger.warn("输出是单个标
            量值，可能不是图像数据");
        }
        
        // 遍历所有输出，查找可能是图像
        的数据
        for (int i = 0; i < ndList.
        size(); i++) {
            NDArray array = ndList.
            get(i);
            if (isLikelyImageData
            (array)) {
                imageData = array;
                break;
            }
        }
        
        if (imageData == null) {
            throw new 
            RuntimeException("无法识
            别图像数据");
        }
        
        // 使用DJL的图像处理工具
        return processImageData
        (imageData);
    } catch (Exception e) {
        logger.error("转换NDList到图
        像失败", e);
        throw new RuntimeException("
        图像转换失败", e);
    }
}

private boolean isLikelyImageData
(NDArray array) {
    // 判断NDArray是否可能是图像数据
    long[] shape = array.getShape().
    getShape();
    return shape.length == 3 || 
    shape.length == 4; // HWC或NHWC
    格式
}

private Image processImageData
(NDArray imageData) {
    // 使用DJL的图像处理工具进行转换
    // ...
    return ImageFactory.getInstance
    ().fromImage(/* 处理后的图像 */);
}
```
## 测试步骤
### 1. 单元测试 convertNDListToImage 方法
```
import org.junit.jupiter.api.Test;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.engine.Engine;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.
ImageFactory;

import static org.junit.jupiter.api.
Assertions.*;

class ImageGenerationServiceTest {
    
    @Test
    void testConvertNDListToImage() 
    {
        // 创建测试用的NDManager
        try (NDManager manager = 
        NDManager.newBaseManager()) 
        {
            // 创建模拟的图像数据 
            (3x3x3 RGB图像)
            float[] imageData = new 
            float[27];
            for (int i = 0; i < 27; 
            i++) {
                imageData[i] = i / 
                255.0f; // 归一化到
                0-1
            }
            
            NDArray imageArray = 
            manager.create
            (imageData, new long[]
            {3, 3, 3});
            NDList ndList = new 
            NDList(imageArray);
            
            // 创建服务实例
            ImageGenerationService 
            service = new 
            ImageGenerationService(/
            * 参数 */);
            
            // 测试转换方法
            Image result = service.
            convertNDListToImage
            (ndList);
            
            // 验证结果
            assertNotNull(result);
            assertEquals(3, result.
            getWidth());
            assertEquals(3, result.
            getHeight());
        }
    }
    
    @Test
    void 
    testConvertNDListToImageWithEmpt
    yList() {
        NDList emptyList = new 
        NDList();
        ImageGenerationService 
        service = new 
        ImageGenerationService(/* 参
        数 */);
        
        assertThrows
        (RuntimeException.class, () 
        -> {
            service.
            convertNDListToImage
            (emptyList);
        });
    }
}
```
### 2. 集成测试
```
import org.junit.jupiter.api.Test;
import org.springframework.beans.
factory.annotation.Autowired;
import org.springframework.boot.
test.context.SpringBootTest;
import org.springframework.boot.
test.web.client.TestRestTemplate;
import org.springframework.http.
HttpEntity;
import org.springframework.http.
HttpHeaders;
import org.springframework.http.
MediaType;
import org.springframework.http.
ResponseEntity;

@SpringBootTest(webEnvironment = 
SpringBootTest.WebEnvironment.
RANDOM_PORT)
class 
ImageGenerationIntegrationTest {
    
    @Autowired
    private TestRestTemplate 
    restTemplate;
    
    @Test
    void testGenerateImageEndpoint
    () {
        // 准备请求数据
        String requestBody = "
        {\"prompt\":\"测试图像\",
        \"width\":256,
        \"height\":256}";
        
        HttpHeaders headers = new 
        HttpHeaders();
        headers.setContentType
        (MediaType.
        APPLICATION_JSON);
        // 添加JWT token（如果需要）
        // headers.setBearerAuth
        ("test-token");
        
        HttpEntity<String> request 
        = new HttpEntity<>
        (requestBody, headers);
        
        // 发送请求
        ResponseEntity<String> 
        response = restTemplate.
        postForEntity(
            "/api/image-generation/
            generate", 
            request, 
            String.class
        );
        
        // 验证响应
        assertEquals(200, response.
        getStatusCodeValue());
        assertNotNull(response.
        getBody());
        assertTrue(response.getBody
        ().contains("imageBase64"));
    }
    
    @Test
    void testHealthEndpoint() {
        ResponseEntity<String> 
        response = restTemplate.
        getForEntity(
            "/api/image-generation/
            health", 
            String.class
        );
        
        assertEquals(200, response.
        getStatusCodeValue());
        assertNotNull(response.
        getBody());
        assertTrue(response.getBody
        ().contains("status"));
    }
}
```
## 常见问题
### 1. 模型加载失败
问题 : ai.djl.repository.zoo.ModelNotFoundException: No model matching the criteria is found

解决方案 :

- 检查模型路径是否正确： qwen.image.model-path=D:\LM_modle\modles\Qwen-Image
- 确保模型文件存在且完整
- 检查模型格式是否与DJL兼容
### 2. 图像转换失败
问题 : No matching default translator found

解决方案 :

- 确保模型输入输出类型配置正确： .setTypes(NDList.class, NDList.class)
- 检查模型输出的数据格式
- 实现自定义的translator处理特定的数据格式
### 3. 内存不足
问题 : 生成大尺寸图像时出现内存溢出

解决方案 :

- 减小图像尺寸
- 增加JVM堆内存： -Xmx4g 或更高
- 优化图像处理逻辑，及时释放资源
### 4. 生成速度慢
问题 : 图像生成时间过长

解决方案 :

- 减少 steps 参数值
- 使用GPU加速（如果可用）
- 优化模型加载和预处理逻辑
## 注意事项
1. 1.
   模型路径 : 确保 application.properties 中的模型路径配置正确，且模型文件完整。
2. 2.
   内存管理 : 图像生成是内存密集型操作，建议在生产环境中配置足够的JVM内存。
3. 3.
   错误处理 : 在前端实现完善的错误处理机制，提供友好的用户反馈。
4. 4.
   安全性 : 确保API接口有适当的认证和授权机制。
5. 5.
   性能优化 : 对于高频使用场景，考虑实现模型预热和连接池管理。
6. 6.
   日志记录 : 启用详细的日志记录，便于问题排查和性能监控。
## 版本历史
版本 日期 描述 1.0 2025-08-02 初始版本，支持基本的图像生成功能

## 联系方式
如有问题或建议，请联系开发团队。