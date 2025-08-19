<script setup lang="ts">
import { RouterView } from 'vue-router'
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

onMounted(() => {
  // 应用启动时初始化认证状态
  authStore.initializeAuth()
})
</script>

<template>
  <div id="app">
    <RouterView />
  </div>
</template>

<style>
* {
  box-sizing: border-box;
}

html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f7fa;
}

#app {
  height: 100vh;
  width: 100%;
}

.el-message {
  z-index: 9999;
}

/* 滚动条样式优化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Element Plus 容器样式 */
.el-container {
  height: 100%;
}

.el-header {
  padding: 0;
  display: flex;
  align-items: center;
}

.el-aside {
  background-color: #ffffff;
  border-right: 1px solid #e4e7ed;
}

.el-main {
  padding: 0;
}

/* Element Plus 菜单样式 */
.el-menu {
  border-right: none;
}

.el-menu-item {
  border-radius: 0;
}

.el-menu-item:hover {
  background-color: #ecf5ff;
}

.el-menu-item.is-active {
  background-color: #ecf5ff;
  color: #409eff;
}

/* Element Plus 卡片样式 */
.el-card {
  margin-bottom: 20px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: box-shadow 0.3s;
}

.el-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.el-card__body {
  padding: 20px;
}

/* Element Plus 按钮样式优化 */
.el-button {
  border-radius: 6px;
  font-weight: 500;
}

/* Element Plus 头像样式 */
.el-avatar {
  background-color: #409eff;
}

/* PC端响应式设计 */
@media (min-width: 1920px) {
  .el-main {
    padding: 24px;
  }
  
  .page-content {
    max-width: 1400px;
  }
}

@media (min-width: 1440px) and (max-width: 1919px) {
  .el-main {
    padding: 20px;
  }
  
  .page-content {
    max-width: 1200px;
  }
}

@media (min-width: 1200px) and (max-width: 1439px) {
  .el-main {
    padding: 16px;
  }
  
  .page-content {
    max-width: 1000px;
  }
}

/* 平板和移动端适配 */
@media (max-width: 1199px) {
  .el-aside {
    width: 200px !important;
  }
  
  .el-main {
    padding: 12px;
  }
  
  .page-content {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .el-aside {
    width: 180px !important;
  }
  
  .el-main {
    padding: 10px;
  }
}
</style>
