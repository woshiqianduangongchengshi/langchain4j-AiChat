# langchain4j-AiChat
基于LangChain4J的AI聊天应用，包含前端和后端完整实现。

## 项目概述
该项目是一个基于LangChain4J构建的AI聊天应用，提供了完整的前后端解决方案。前端采用现代Web技术栈，后端基于Java生态系统，实现了与AI模型的交互功能。

## 技术栈
### 前端 (langchain_aichat)
- Vue.js 3
- Vite
- JavaScript/TypeScript
- Pinia (状态管理)
- Vue Router
### 后端 (langchain)
- Java
- Spring Boot
- Maven
- LangChain4J
## 项目结构
```
langchainAi/
├── langchain/          # 后端Java项
目
│   ├── src/            # 源代码
│   ├── target/         # 编译输出
│   ├── pom.xml         # Maven依赖
配置
│   └── ...
└── langchain_aichat/   # 前端Vue项目
    ├── src/            # 源代码
    ├── public/         # 静态资源
    ├── index.html      # 入口HTML
    ├── package.json    # NPM依赖配置
    └── ...
```
## 快速开始
### 前置条件
- JDK 11+ （推荐JDK 17）
- Maven 3.6+
- Node.js 16+
- npm 8+
### 后端运行
```
# 进入后端目录
d:cd JAVAbcak\langchainAi\langchain

# 构建项目
mvn clean install

# 运行应用
mvn spring-boot:run
```
### 前端运行
```
# 进入前端目录
d:cd 
JAVAbcak\langchainAi\langchain_aicha
t

# 安装依赖
npm install

# 运行开发服务器
npm run dev
```
## 功能特性
1. 1.
   AI聊天界面，支持文本交互
2. 2.
   集成LangChain4J框架，实现与多种AI模型的对接
3. 3.
   响应式设计，适配不同设备
4. 4.
   支持聊天历史记录
## 配置说明
### 后端配置
后端配置文件位于 `
d:JAVAbcak\langchainAi\langchain\src\main\resources\application.properties
`，主要配置项包括：
- `spring.ai.openai.api-key`：OpenAI API密钥
- `spring.ai.openai.model`：使用的AI模型名称
- `spring.ai.openai.temperature`：模型温度参数
### 前端配置
前端配置文件位于 `
d:JAVAbcak\langchainAi\langchain_aichat\src\main\resources\application.properties
`，主要配置项包括：
- `VUE_APP_API_URL`：后端API基础URL
- `VUE_APP_API_KEY`：OpenAI API密钥
- `VUE_APP_MODEL_NAME`：使用的AI模型名称
- `VUE_APP_TEMPERATURE`：模型温度参数

### 前置条件
- JDK 11+ （推荐JDK 17）
- Maven 3.6+
- Node.js 16+
- npm 8+
### 后端运行
```
# 进入后端目录
d:cd JAVAbcak\langchainAi\langchain

# 构建项目
mvn clean install

# 运行应用
mvn spring-boot:run
```
### 前端运行
```
# 进入前端目录
d:cd 
JAVAbcak\langchainAi\langchain_aicha
t

# 安装依赖
npm install

# 运行开发服务器
npm run dev
```
## 功能特性
1. 1.
   AI聊天界面，支持文本交互
2. 2.
   集成LangChain4J框架，实现与多种AI模型的对接
3. 3.
   响应式设计，适配不同设备
4. 4.
   支持聊天历史记录
5. 5.
   提供API接口，方便第三方集成
## 配置说明
### 后端配置
后端配置文件位于 `application.properties` （可能需要创建）。主要配置项包括：

```
# 服务器端口
server.port=8080

# 数据库配置
spring.datasource.url=jdbc:mysql://
localhost:3306/ai_chat
spring.datasource.username=root
spring.datasource.password=password

# LangChain4J配置
langchain4j.openai.
api-key=your-openai-api-key
langchain4j.openai.model-name=gpt-3.
5-turbo
```
### 前端配置
前端配置文件位于 `.env.local` 。主要配置项包括：

```
# 后端API基础URL
VITE_API_BASE_URL=http://
localhost:8080/api

# 开发模式配置
VITE_DEV_MODE=true
```
## API文档
API文档位于 `api-documentation.md` ，包含了所有后端API接口的详细说明。

## 部署说明
### 后端部署
1. 1.
   构建可执行JAR文件：
```
mvn clean package -DskipTests
```
2. 1.
   运行JAR文件：
```
java -jar target/langchain-0.0.
1-SNAPSHOT.jar
```
### 前端部署
1. 1.
   构建生产环境代码：
```
npm run build
```
2. 1.
   将生成的 dist 目录下的文件部署到Web服务器（如Nginx、Apache等）。
## 贡献指南
1. 1.
   Fork本仓库
2. 2.
   创建特性分支： git checkout -b feature/your-feature
3. 3.
   提交更改： git commit -m 'Add some feature'
4. 4.
   推送到分支： git push origin feature/your-feature
5. 5.
   提交Pull Request
## 许可证
本项目采用MIT许可证，详情请见LICENSE文件。

## 联系方式
如有问题或建议，请通过以下方式联系：

- GitHub Issues: Issues
- Email: example@example.com
## 鸣谢
- LangChain4J团队提供的优秀框架
- Vue.js和Spring Boot社区