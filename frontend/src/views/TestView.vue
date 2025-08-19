<template>
  <div class="test-container">
    <h1>测试页面</h1>
    <p>如果你能看到这个页面，说明前端路由工作正常！</p>
    <div class="test-info">
      <h3>系统信息：</h3>
      <ul>
        <li>前端服务器：运行正常</li>
        <li>Vue Router：工作正常</li>
        <li>Element Plus：已加载</li>
        <li>当前时间：{{ currentTime }}</li>
      </ul>
    </div>
    <div class="test-buttons">
      <el-button type="primary" @click="$router.push('/login')">
        前往登录页
      </el-button>
      <el-button type="success" @click="testApi">
        测试API连接
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const currentTime = ref('')

const testApi = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/actuator/health')
    if (response.ok) {
      ElMessage.success('后端API连接正常！')
    } else {
      ElMessage.warning('后端API响应异常')
    }
  } catch (error) {
    ElMessage.error('无法连接到后端API')
  }
}

onMounted(() => {
  currentTime.value = new Date().toLocaleString('zh-CN')
})
</script>

<style scoped>
.test-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
  text-align: center;
}

.test-container h1 {
  color: #409EFF;
  font-size: 32px;
  margin-bottom: 20px;
}

.test-info {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  margin: 30px 0;
  text-align: left;
}

.test-info h3 {
  color: #303133;
  margin-bottom: 15px;
}

.test-info ul {
  list-style: none;
  padding: 0;
}

.test-info li {
  padding: 8px 0;
  color: #606266;
  border-bottom: 1px solid #e4e7ed;
}

.test-info li:last-child {
  border-bottom: none;
}

.test-buttons {
  margin-top: 30px;
}

.test-buttons .el-button {
  margin: 0 10px;
}
</style>