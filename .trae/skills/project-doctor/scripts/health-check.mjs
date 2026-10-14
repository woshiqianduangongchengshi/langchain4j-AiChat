#!/usr/bin/env node
/**
 * project-doctor 静态体检脚本（只读，不修改任何文件）
 *
 * 扫描内容：
 *   1. Java 类文件命名是否符合大驼峰
 *   2. 前端 src/api 下 axios 实例是否重复、withCredentials 是否一致
 *   3. application.properties 中 ddl-auto / JWT 密钥 / 数据库密码配置
 *   4. 前端 .env.local 是否存在（生图 VITE_API_KEY）
 *   5. 输出需要人工关联判断的线索：permitAll 路径、Principal 取用法
 *
 * 用法（在项目根目录执行）：
 *   node .trae/skills/project-doctor/scripts/health-check.mjs
 *
 * 退出码恒为 0：本脚本只负责产出报告，不作为流水线门禁。
 */
import { existsSync, readdirSync, readFileSync, statSync } from 'node:fs'
import { basename, dirname, join, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

// 兼容 Node 18（import.meta.dirname 仅 Node 20.11+ 可用）
const scriptDir = dirname(fileURLToPath(import.meta.url))
// scripts -> project-doctor -> skills -> .trae -> 项目根
const projectRoot = resolve(scriptDir, '../../../..')

/** @type {{level: string, area: string, title: string, evidence: string, suggestion: string}[]} */
const findings = []

const record = (level, area, title, evidence, suggestion) => {
  findings.push({ level, area, title, evidence, suggestion })
}

const read = (relPath) => {
  const abs = join(projectRoot, relPath)
  return existsSync(abs) ? readFileSync(abs, 'utf-8') : null
}

const relative = (abs) => abs.replace(projectRoot, '').replace(/\\/g, '/').replace(/^\//, '')

const walk = (dir, acc = []) => {
  if (!existsSync(dir)) return acc
  for (const entry of readdirSync(dir)) {
    const abs = join(dir, entry)
    if (statSync(abs).isDirectory()) {
      walk(abs, acc)
    } else if (entry.endsWith('.java')) {
      acc.push(abs)
    }
  }
  return acc
}

// ---------------------------------------------------------------------------
// 1. Java 类文件命名：必须大驼峰；首字母小写或全大写缩写均视为违规
// ---------------------------------------------------------------------------
const javaRoot = join(projectRoot, 'langchain/src/main/java')
walk(javaRoot).forEach((abs) => {
  const stem = basename(abs).replace(/\.java$/, '')
  const startsLower = /^[a-z]/.test(stem)
  const allUpper = /^[A-Z]{2,}$/.test(stem)
  if (startsLower || allUpper) {
    record(
      'P2',
      '后端规范',
      `Java 类名不符合大驼峰：${stem}.java`,
      relative(abs),
      '重命名为大驼峰（如 AiServices、ChatConfig、HelloController、AiTools），并同步更新类名、引用与组件注入点',
    )
  }
})

// ---------------------------------------------------------------------------
// 2. 前端 axios 实例重复 / 配置不一致
// ---------------------------------------------------------------------------
const apiDir = join(projectRoot, 'langchain_aichat/src/api')
if (existsSync(apiDir)) {
  const instances = readdirSync(apiDir)
    .filter((f) => f.endsWith('.js'))
    .map((file) => {
      const content = readFileSync(join(apiDir, file), 'utf-8')
      return {
        file,
        createCount: (content.match(/axios\.create\(/g) || []).length,
        hasAuthInterceptor: content.includes('interceptors.request'),
        withCredentials: /withCredentials\s*:\s*true/.test(content),
      }
    })
    .filter((item) => item.createCount > 0)

  if (instances.length > 1) {
    record(
      'P2',
      '前端规范',
      `存在 ${instances.length} 个各自创建 axios 实例的 api 文件`,
      instances.map((i) => `${i.file}（axios.create x${i.createCount}）`).join('；'),
      '抽取统一的 src/api/request.js（baseURL、Bearer 拦截器、401 跳登录、错误提示），各业务文件只保留接口定义',
    )
  }

  const withCreds = new Set(instances.map((i) => i.withCredentials))
  if (instances.length > 1 && withCreds.size > 1) {
    record(
      'P2',
      '前端规范',
      '各 axios 实例的 withCredentials 配置不一致',
      instances.map((i) => `${i.file}: withCredentials=${i.withCredentials}`).join('；'),
      '统一在单一实例中配置，避免鉴权/Cookie 行为出现文件级差异',
    )
  }

  const noResponseErrorHandler = instances
    .filter((i) => i.hasAuthInterceptor)
    .filter((i) => {
      const content = readFileSync(join(apiDir, i.file), 'utf-8')
      return !content.includes('interceptors.response')
    })
  if (noResponseErrorHandler.length > 0) {
    record(
      'P2',
      '前端规范',
      'axios 只有请求拦截器，缺少响应拦截器（无统一 401/错误处理）',
      noResponseErrorHandler.map((i) => i.file).join('、'),
      '在统一实例上增加 response 拦截器：401 清除 token 并跳转登录，其余错误用 Element Plus 统一提示',
    )
  }
}

// ---------------------------------------------------------------------------
// 3. 后端配置：ddl-auto / 硬编码密钥
// ---------------------------------------------------------------------------
const props = read('langchain/src/main/resources/application.properties')
if (props) {
  const ddl = props.match(/^spring\.jpa\.hibernate\.ddl-auto=(.+)$/m)
  if (ddl && ddl[1].trim() === 'update') {
    record(
      'P1',
      '配置安全',
      'spring.jpa.hibernate.ddl-auto=update',
      'langchain/src/main/resources/application.properties:10',
      '开发环境可保留；生产/演示环境必须改为 validate 或 none，表结构变更走显式 SQL/Flyway，避免 Hibernate 自动改表',
    )
  }

  const jwtSecret = props.match(/^jwt\.secret=(.+)$/m)
  if (jwtSecret && !jwtSecret[1].includes('${')) {
    record(
      'P1',
      '配置安全',
      'JWT 密钥硬编码在配置文件中',
      'langchain/src/main/resources/application.properties:53',
      '改为 jwt.secret=${JWT_SECRET}，通过环境变量注入，并确认该文件不被提交到公开仓库',
    )
  }

  const dbPassword = props.match(/^spring\.datasource\.password=(.+)$/m)
  if (dbPassword && !dbPassword[1].includes('${')) {
    record(
      'P1',
      '配置安全',
      '数据库密码明文硬编码（root/123456）',
      'langchain/src/main/resources/application.properties:7',
      '改为 spring.datasource.password=${DB_PASSWORD}，本地开发用环境变量或本地 profile',
    )
  }
}

// ---------------------------------------------------------------------------
// 4. 前端生图环境变量
// ---------------------------------------------------------------------------
const envLocal = join(projectRoot, 'langchain_aichat/.env.local')
if (!existsSync(envLocal)) {
  record(
    'P2',
    '前端环境',
    '缺少 langchain_aichat/.env.local，生图 VITE_API_KEY 未配置',
    'langchain_aichat/.env.local',
    '新建 .env.local 写入 VITE_API_KEY=xxx（该文件应在 .gitignore 中）；注意 VITE_ 变量会打进浏览器包，仅限低权限测试 key',
  )
}

// ---------------------------------------------------------------------------
// 5. 人工复核线索（脚本不下结论，仅汇总证据供关联分析）
// ---------------------------------------------------------------------------
const signals = []
const securityConfig = read('langchain/src/main/java/org/example/langchain/config/SecurityConfig.java')
if (securityConfig) {
  const permitAllPaths = [...securityConfig.matchAll(/requestMatchers\("([^"]+)"\)\.permitAll\(\)/g)].map(
    (m) => m[1],
  )
  signals.push({ label: 'SecurityConfig 中 permitAll 路径', value: permitAllPaths.join(', ') || '无' })
}

walk(javaRoot).forEach((abs) => {
  const content = readFileSync(abs, 'utf-8')
  const hits = []
  if (content.includes('(User) authentication.getPrincipal()')) hits.push('强转 authentication.getPrincipal()')
  if (content.includes('@AuthenticationPrincipal User')) hits.push('@AuthenticationPrincipal User')
  if (hits.length > 0) {
    signals.push({ label: relative(abs), value: hits.join('、') })
  }
})

// ---------------------------------------------------------------------------
// 输出报告
// ---------------------------------------------------------------------------
const order = { P0: 0, P1: 1, P2: 2 }
findings.sort((a, b) => order[a.level] - order[b.level])

console.log('================ project-doctor 静态体检 ================')
console.log(`项目根目录: ${projectRoot}`)
console.log(`发现问题: ${findings.length} 个（P0 ${findings.filter((f) => f.level === 'P0').length} / P1 ${
  findings.filter((f) => f.level === 'P1').length
} / P2 ${findings.filter((f) => f.level === 'P2').length}）`)
console.log('')

for (const level of ['P0', 'P1', 'P2']) {
  const items = findings.filter((f) => f.level === level)
  if (items.length === 0) continue
  console.log(`---------------- ${level} ----------------`)
  items.forEach((f, i) => {
    console.log(`${level}-${i + 1} [${f.area}] ${f.title}`)
    console.log(`    证据: ${f.evidence}`)
    console.log(`    建议: ${f.suggestion}`)
  })
  console.log('')
}

console.log('---------------- 人工复核线索（脚本不下结论） ----------------')
signals.forEach((s) => console.log(`- ${s.label}: ${s.value}`))
console.log('')
console.log('请对照 SKILL.md 清单完成语义检查（permitAll 与 Principal 联动、异常处理、流式请求等）。')

process.exit(0)
