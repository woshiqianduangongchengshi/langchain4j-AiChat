---
name: project-doctor
description: 对 langchain4j-AiChat 前后端做代码体检并输出 P0/P1/P2 分级问题报告，覆盖鉴权安全、配置硬编码、Java/Vue 代码规范与测试构建。当用户要求项目体检、健康检查、排查隐患、代码质量或安全性检查、上线前自查时使用。不要用于直接编写业务功能或自动重构。
---

# project-doctor — 项目代码体检

本 skill 对当前仓库做**只读体检**，产出带证据的分级问题报告。默认只诊断、不开刀；是否修复、修复范围由用户逐项确认。

- 后端：`langchain/`（Spring Boot 3.5 + Java 21 + Spring Security/JWT + JPA/MySQL + LangChain4j）
- 前端：`langchain_aichat/`（Vue 3 + Vite + Element Plus + 纯 JS，非 TypeScript）

## 触发条件

用户表达以下意图时执行：体检、健康检查、过一遍代码、排查隐患、上线前检查、代码质量检查、安全自查。

开始前只需确认两点（默认值可直接采用，不必追问）：

1. 体检范围 —— 默认前后端全量；用户可指定仅后端/仅前端/仅某模块。
2. 是否只出报告 —— 默认是。用户明确说"顺便修"时，也只允许修 P0/P1，且每修一项先说明改动点。

## 执行流程

### 第 1 步：跑静态检查脚本

在项目根目录 `d:\project\-langchain4j-AiChat` 下执行（Windows 直接用 node，不要用 ps1 脚本以避开执行策略限制）：

```powershell
node .trae/skills/project-doctor/scripts/health-check.mjs
```

脚本自动覆盖：Java 类命名、前端 axios 实例重复/拦截器缺失、ddl-auto、JWT 与数据库密码硬编码、`.env.local` 缺失，并输出 permitAll 路径与 Principal 取用法的关联线索。

### 第 2 步：完成脚本做不了的语义检查

按下方清单逐项打开对应文件**现场核实**，脚本输出只作为线索。问题可能已被修复——以当前代码为准，不得凭历史记忆直接报问题。

### 第 3 步：跑构建与测试（用户允许执行命令时）

```powershell
# 前端
cd langchain_aichat; npm run build; npm run test:unit
# 后端（Windows 用 mvnw.cmd）
cd langchain; .\mvnw.cmd -q compile; .\mvnw.cmd test
```

环境依赖（Ollama:11434 的 deepseek-r1:8b 与 nomic-embed-text:v1.5、MySQL 库 langchain）不要求现场拉起，只在报告中提示运行时前置条件。

## 体检清单

### A. 鉴权与安全（最高优先）

- **A1 permitAll 与取 Principal 联动（本项目历史高发坑，必查）**
  `SecurityConfig` 对 `/chat/sessions/**`、`/reviews/**`、`/aichat` 放行，但：
  - `ChatSessionService` 各方法直接 `(User) authentication.getPrincipal()` 强转——无 token 调用时 principal 为 null，表现为 **500 而非 401**。
  - `ReviewController` 用 `@AuthenticationPrincipal User user`，`ReviewService.submitReview` 直接 `review.setUser(user)` 落库——匿名可提交评价，可能写入空用户或触发约束异常。
  - 判定口径：放行路径本身不违规，违规的是"放行 + 代码内又假定用户必登录"。修复二选一：将这些路径改为 `authenticated()`；或在入口判空并返回 401/拒绝匿名写入。
- **A2 密钥与密码硬编码**：`jwt.secret`、`spring.datasource.password` 未用 `${ENV}` 占位。前端 `VITE_API_KEY` 会被打进浏览器包，只能用低权限测试 key，需在报告中点明这一固有暴露面。
- **A3 `/aichat` 为 GET 且 permitAll**：msg 走 query，存在长消息/特殊字符转义问题，且本地模型可被匿名调用消耗资源。至少在报告中提示，不擅自改接口契约（前端已在调用）。

### B. 配置与数据层

- **B1** `ddl-auto=update`：开发可接受，报告须注明生产须改 `validate`/`none`。
- **B2** 业务异常一律 `throw new RuntimeException(...)`，全仓库无 `@RestControllerAdvice` 全局异常处理，前端拿到的是 Spring 默认错误结构而非统一 `ApiResponse`。
- **B3** `ReviewService.getReviewList` 分页参数未做边界校验（page < 1、超大 pageSize）。
- **B4** 正向确认项（无问题就明确写"通过"，不要硬凑问题）：`spring.jpa.open-in-view=false`、JWT filter 的过期/格式异常分支、BCrypt 密码加密。

### C. 后端代码规范

- **C1** Java 类名必须大驼峰。历史锚点：`THUDM`、`aiservices`、`chatconfig`、`hellocontroller`、`aitools`（以脚本实测为准）。重命名会牵连 Spring 组件名与注入，报告中给出影响面，不直接改。
- **C2** 注入风格不统一：`ReviewController/ReviewService` 用字段注入 `@Autowired`，其余模块用 `@RequiredArgsConstructor` 构造器注入。仅记录为 P2，不批量改。
- **C3** 包结构分层应保持 `controller / service / repository / entity / dto` 清晰，跨模块直接引用实体（如 review 直接依赖 customer.User 属正常聚合，不算问题，勿误报）。

### D. 前端代码规范

- **D1** `src/api` 下 `login.js`、`aichat.js`、`reviews.js` 各自 `axios.create`，Bearer 拦截器复制三份，`withCredentials` 不一致，且无 response 拦截器（401 不跳登录）。
- **D2** `aichat.js` 中 `chatWithAI` 声明 `responseType: 'stream'` 并调用 `response.data.getReader()`——浏览器 XHR 版 axios 不支持 stream，该函数与 `handleStreamResponse` 可能是无效代码。**必须打开 `src/chat/aichat.vue` 核实真实流式实现**（fetch/EventSource/直接读取），再下"死代码"结论。
- **D3** 检查新页面是否遵循项目惯例：接口放 `src/api`、路由在 `src/router/index.js` 注册、用 Element Plus 而非新增 UI 依赖。
- **D4** 纯 JS 是本项目现状，禁止建议"迁移 TypeScript"之类的大范围改造。

### E. 测试与构建

- **E1** 后端测试仅有空的 `LangchainApplicationTests`；前端仅脚手架 `HelloWorld.spec.js`。报告缺口即可，不强行补测试。
- **E2** 构建/测试命令失败属于 P0（主分支不可构建），需附完整错误输出。

## 严重级别

- **P0**：不可构建/运行、安全漏洞可直接利用（如匿名可读写他人数据、密钥真实泄露到公网仓库）、主流程数据丢失。
- **P1**：鉴权缺陷导致错误码/数据污染、生产配置风险、明显的空指针路径、密钥硬编码。
- **P2**：命名、重复代码、注入风格、缺少拦截器、参数校验、测试缺口。

## 输出模板

用中文输出，结构固定：

```markdown
## 项目体检报告（日期，分支/commit 可选）

**总览**：P0 x 个 / P1 x 个 / P2 x 个；构建：通过/失败；一句话结论。

### P0（必须修）
1. 标题
   - 证据：文件相对路径:行号，附关键代码片段
   - 影响：具体后果与触发方式（不夸大）
   - 建议：最小改动方案，说明影响面

### P1 / P2
（同上，按级别分组）

### 已确认通过的项
- 简要列出核查过但没有问题的关键点，避免用户重复担心

### 待核实（运行时才能确认）
- 如 Ollama 模型、MySQL 数据、真实流式链路等
```

## 规则与禁忌

- 每个问题必须给出**可点击的文件路径与行号证据**；无法静态确认的放进"待核实"，禁止臆断。
- 只报告当前代码真实存在的问题；脚本锚点中的历史问题若已修复，明确写"已修复"，不再计入总数。
- 风格偏好不得包装成缺陷；与本清单判定口径冲突时以清单为准。
- 未经用户确认不得修改任何代码、不得提交 git、不得改接口契约（尤其 `/aichat` 的请求形态）。
- 不建议技术栈迁移（JS→TS、换 ORM、换安全框架等超范围动作）。
- 修复时遵守最小改动：只动问题相关代码，不顺便格式化、不重构周边文件。
