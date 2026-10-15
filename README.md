# langchain4j-AiChat · AI 模拟面试助手

基于 **LangChain4j + Ollama** 的全栈 AI 模拟面试应用。AI 扮演资深 Java 后端面试官，结合本地 RAG 面试题库逐题提问、实时点评，并在结束时给出综合评价。支持用户系统、多会话管理、流式输出与 AI 生图。

## 功能特性

- **用户系统**：注册 / 登录 / 修改资料，基于 JWT 的无状态鉴权（Spring Security）
- **AI 模拟面试**：AI 一次出一道 Java 后端面试题，作答后点评并追问；发送「结束面试」返回综合评价
- **流式聊天**：基于 LangChain4j `@AiService` + Reactor `Flux<String>` 的 SSE 流式响应
- **RAG 本地知识库**：启动时加载 `knowledgefork/*.md` 面试题库，Ollama 嵌入模型检索，为提问提供素材
- **多会话管理**：会话增删改查，聊天记忆持久化到 MySQL（MessageWindowChatMemory，保留最近 5 条）
- **AI 生图**：前端直连 SiliconFlow（Kwai-Kolors/Kolors 模型）生成图片
- **面试评价**：对会话进行评分与统计

## 技术栈

| 端 | 技术 |
| --- | --- |
| 后端 `langchain/` | Java 21 · Spring Boot 3.5.4 · LangChain4j 1.0.0-beta3 · Spring Security(JWT) · Spring Data JPA · MySQL · Maven |
| 前端 `langchain_aichat/` | Vue 3 · Vite 7 · Element Plus · Pinia · Vue Router · Axios |
| AI 模型 | Ollama 本地运行：`deepseek-r1:8b`（聊天）+ `nomic-embed-text:v1.5`（RAG 嵌入） |

## 项目结构

```
langchain4j-AiChat/
├── langchain/                # 后端 Spring Boot 项目
│   ├── src/main/java/org/example/langchain/
│   │   ├── aichat/           # AI 服务（@AiService 流式接口）、RAG 检索拼装
│   │   ├── aichatmemory/     # 聊天记忆持久化
│   │   ├── chatfunc/         # 会话管理
│   │   ├── customer/         # 用户与鉴权
│   │   ├── config/           # Security / JWT / CORS 配置
│   │   ├── review/           # 面试评价
│   │   └── tools/            # Function Calling 扩展点（预留）
│   └── src/main/resources/
│       ├── application.properties
│       └── knowledgefork/    # RAG 知识库（Java 面试题库 Markdown）
└── langchain_aichat/         # 前端 Vue 项目
    ├── src/api/              # 接口封装
    ├── src/chat/             # 聊天页
    └── src/components/       # 登录/评价/生图/个人中心等页面
```

## 快速开始

### 前置依赖

| 依赖 | 版本要求 |
| --- | --- |
| JDK | 21 |
| Maven | 3.6+ |
| Node.js | ^20.19 或 >=22.12（项目已用 Volta 固定 22.23.3） |
| MySQL | 8.x |
| Ollama | 需拉取 `deepseek-r1:8b` 与 `nomic-embed-text:v1.5` 两个模型 |

### 1. 准备数据库

```sql
CREATE DATABASE langchain DEFAULT CHARSET utf8mb4;
```

表结构由 JPA `ddl-auto=update` 自动创建，无需手动建表。

### 2. 准备 Ollama 模型

```bash
ollama pull deepseek-r1:8b
ollama pull nomic-embed-text:v1.5
```

> 注意：8b 模型首次加载较慢，如遇到读取超时，可在 `application.properties` 中增加：
> `langchain4j.ollama.streaming-chat-model.timeout=PT5M`

### 3. 启动后端（端口 8081）

```bash
cd langchain
mvn spring-boot:run
```

接口统一前缀为 `/api`（如 `http://localhost:8081/api/auth/login`）。

### 4. 启动前端（端口 5173）

```bash
cd langchain_aichat
npm install
npm run dev
```

Vite 已配置 `/api` 代理到 `http://localhost:8081`。

### 5.（可选）开启 AI 生图

在 `langchain_aichat/` 下新建 `.env.local`：

```bash
VITE_API_KEY=你的SiliconFlow API Key
```

未配置时生图功能不可用，其余功能不受影响。

## 配置说明

### 后端环境变量

`application.properties` 中的敏感项支持环境变量覆盖（冒号后为本地开发默认值）：

| 环境变量 | 说明 | 默认值 |
| --- | --- | --- |
| `DB_USERNAME` | MySQL 用户名 | `root` |
| `DB_PASSWORD` | MySQL 密码 | `123456` |
| `JWT_SECRET` | JWT 签名密钥（≥32 字符，生产必改） | 开发占位值 |

聊天 / 嵌入模型可在 `application.properties` 中切换（`langchain4j.ollama.*`），文件内也保留了 SiliconFlow 远程模型的注释配置。

## License

[MIT](LICENSE)
