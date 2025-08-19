import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // 加载环境变量
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': resolve(__dirname, 'src'),
      },
    },
    // 添加环境变量配置
    define: {
      __APP_ENV__: JSON.stringify(env.VITE_API_KEY),
    },
    server: {
      proxy: {
        '/api': {
          target: 'http://localhost:8081',
          changeOrigin: true,
          followRedirects: true,
          // 移除CORS头信息，让后端处理CORS
          // headers: {
          //   'Access-Control-Allow-Origin': '*',
          // },
          // 添加预检请求支持
          onProxyReq: (proxyReq, req, res) => {
            console.log(`代理请求: ${req.method} ${req.url} -> ${proxyReq.protocol}//${proxyReq.host}${proxyReq.path}`)
          },
          onProxyRes: (proxyRes, req, res) => {
            console.log(`代理响应: ${req.method} ${req.url} -> ${proxyRes.statusCode}`)
          },
          onError: (err, req, res) => {
            console.error('代理错误:', err)
          }
        },
      },
    },
  }
})
