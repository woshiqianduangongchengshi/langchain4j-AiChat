<script setup>
// 无需额外逻辑，保持空或根据项目需求添加全局状态
</script>

<template>
  <!-- 路由视图容器，用于渲染当前路由对应的组件（如登录页、注册页等） -->
  <router-view />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body,
#app {
  width: 100%;
  height: 100%;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
</style>
