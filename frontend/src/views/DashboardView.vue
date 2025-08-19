<template>
  <el-container class="dashboard-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="header-left">
          <h2>JWT Demo 系统</h2>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="undefined">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ authStore.userInfo?.username || 'admin' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人资料
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <el-container class="main-container">
      <!-- 侧边栏 -->
      <el-aside width="240px" class="sidebar">
        <el-menu
          :default-active="$route.path"
          class="sidebar-menu"
          router
          :collapse="false"
        >
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <template #title>仪表板</template>
          </el-menu-item>
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <template #title>个人资料</template>
          </el-menu-item>
          <el-menu-item v-if="authStore.isAdmin" index="/admin/users">
            <el-icon><UserFilled /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区域 -->
      <el-main class="main-content">
        <div class="page-content">
          <!-- 欢迎卡片 -->
          <el-card class="welcome-card" shadow="hover">
            <div class="welcome-content">
              <div class="welcome-text">
                <h3>欢迎回来，{{ authStore.userInfo?.username || 'admin' }}！</h3>
                <p>今天是 {{ currentDate }}</p>
              </div>
              <el-icon size="48" color="#409EFF"><Star /></el-icon>
            </div>
          </el-card>

          <!-- 统计卡片 -->
          <el-row :gutter="20" class="stats-section">
            <el-col :span="8">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-info">
                    <div class="stat-title">在线用户</div>
                    <div class="stat-value">1,234</div>
                  </div>
                  <el-icon size="40" color="#67C23A"><UserFilled /></el-icon>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-info">
                    <div class="stat-title">今日访问</div>
                    <div class="stat-value">5,678</div>
                  </div>
                  <el-icon size="40" color="#E6A23C"><View /></el-icon>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-info">
                    <div class="stat-title">消息数量</div>
                    <div class="stat-value">999</div>
                  </div>
                  <el-icon size="40" color="#F56C6C"><Message /></el-icon>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 功能区域 -->
          <el-row :gutter="20" class="function-section">
            <el-col :span="12">
              <el-card class="function-card" shadow="hover">
                <template #header>
                  <span class="card-title">快速操作</span>
                </template>
                <div class="function-buttons">
                  <el-button type="primary" @click="$router.push('/profile')">
                    <el-icon><User /></el-icon>查看资料
                  </el-button>
                  <el-button type="success">
                    <el-icon><Edit /></el-icon>编辑信息
                  </el-button>
                  <el-button v-if="authStore.isAdmin" type="warning" @click="$router.push('/admin/users')">
                    <el-icon><Setting /></el-icon>用户管理
                  </el-button>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card class="function-card" shadow="hover">
                <template #header>
                  <span class="card-title">系统信息</span>
                </template>
                <div class="system-info">
                  <div class="info-item">
                    <span class="info-label">登录时间：</span>
                    <span class="info-value">{{ loginTime }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">用户角色：</span>
                    <span class="info-value">{{ authStore.isAdmin ? '管理员' : '普通用户' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">Token状态：</span>
                    <span class="info-value">有效</span>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-main>
    </el-container>

    <!-- 修改密码对话框 -->
    <ChangePasswordDialog v-model:visible="showPasswordDialog" />
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import ChangePasswordDialog from '@/components/ChangePasswordDialog.vue'

const router = useRouter()
const authStore = useAuthStore()

const showPasswordDialog = ref(false)

// 当前日期
const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 登录时间（模拟）
const loginTime = ref('')

// 下拉菜单命令处理
const handleCommand = async (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'password':
      showPasswordDialog.value = true
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await authStore.logout()
        ElMessage.success('退出成功')
        router.push('/login')
      } catch (error: any) {
        if (error !== 'cancel') {
          console.error('退出登录失败:', error)
          ElMessage.error('退出登录失败')
        }
      }
      break
  }
}

onMounted(async () => {
  // 初始化用户信息
  if (!authStore.userInfo) {
    try {
      await authStore.fetchUserProfile()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
  
  // 设置登录时间
  loginTime.value = new Date().toLocaleString('zh-CN')
})
</script>

<style scoped>
/* 整体布局 */
.dashboard-layout {
  height: 100vh;
  width: 100%;
}

/* 顶部导航栏 */
.header {
  background-color: #ffffff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0;
  height: 60px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.header-left h2 {
  margin: 0;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
}

.header-right .user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.header-right .user-info:hover {
  background-color: #f5f7fa;
}

.username {
  font-weight: 500;
  color: #303133;
  margin-left: 8px;
  margin-right: 8px;
}

/* 主容器 */
.main-container {
  height: calc(100vh - 60px);
}

/* 侧边栏 */
.sidebar {
  background-color: #ffffff;
  border-right: 1px solid #e4e7ed;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}

.sidebar-menu .el-menu-item {
  height: 56px;
  line-height: 56px;
}

/* 主内容区域 */
.main-content {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
}

/* 欢迎卡片 */
.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.welcome-text h3 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 22px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

/* 统计卡片区域 */
.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 20px;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

/* 功能区域 */
.function-section {
  margin-bottom: 20px;
}

.function-card {
  height: 200px;
}

.card-title {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
}

.function-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 10px;
}

.function-buttons .el-button {
  justify-content: flex-start;
  width: 100%;
}

.system-info {
  padding-top: 10px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  color: #606266;
  font-size: 14px;
}

.info-value {
  color: #303133;
  font-weight: 500;
  font-size: 14px;
}

/* Element Plus组件自定义 */
.el-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.el-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.el-row {
  margin-left: 0 !important;
  margin-right: 0 !important;
}

.el-col {
  padding-left: 10px;
  padding-right: 10px;
}

/* PC端不同分辨率适配 */
@media (min-width: 1920px) {
  .page-content {
    max-width: 1400px;
  }
  
  .stats-section .el-col {
    padding-left: 15px;
    padding-right: 15px;
  }
  
  .function-section .el-col {
    padding-left: 15px;
    padding-right: 15px;
  }
}

@media (min-width: 1440px) and (max-width: 1919px) {
  .page-content {
    max-width: 1200px;
  }
  
  .stats-section .el-col {
    padding-left: 12px;
    padding-right: 12px;
  }
}

@media (min-width: 1200px) and (max-width: 1439px) {
  .page-content {
    max-width: 1000px;
  }
  
  .stats-section .el-col {
    padding-left: 10px;
    padding-right: 10px;
  }
}

/* 小屏幕PC适配 */
@media (min-width: 1024px) and (max-width: 1199px) {
  .page-content {
    max-width: 100%;
  }
  
  .stats-section .el-col {
    padding-left: 8px;
    padding-right: 8px;
  }
  
  .stat-value {
    font-size: 24px;
  }
}

/* 平板适配 */
@media (max-width: 1023px) {
  .sidebar {
    width: 200px !important;
  }
  
  .stats-section .el-col {
    span: 12 !important;
    margin-bottom: 15px;
  }
  
  .function-section .el-col {
    span: 24 !important;
    margin-bottom: 15px;
  }
  
  .stat-value {
    font-size: 22px;
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .sidebar {
    width: 180px !important;
  }
  
  .main-content {
    padding: 15px;
  }
  
  .stats-section .el-col {
    span: 24 !important;
    margin-bottom: 20px;
  }
  
  .welcome-content {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
  
  .stat-content {
    padding: 15px;
  }
}
</style>