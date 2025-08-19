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
    <el-card class="profile-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">个人资料</span>
          <el-button type="primary" @click="editMode = !editMode">
            {{ editMode ? '取消编辑' : '编辑资料' }}
          </el-button>
        </div>
      </template>

      <el-row :gutter="30">
        <el-col :span="8">
          <div class="avatar-section">
            <el-avatar :size="120" :src="undefined" class="user-avatar">
              <el-icon size="60"><User /></el-icon>
            </el-avatar>
            <div class="avatar-info">
              <h3>{{ userInfo?.username }}</h3>
              <p class="user-role">{{ authStore.isAdmin ? '管理员' : '普通用户' }}</p>
            </div>
          </div>
        </el-col>

        <el-col :span="16">
          <el-form
            ref="formRef"
            :model="formData"
            :rules="rules"
            label-width="100px"
            class="profile-form"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="formData.username"
                :disabled="!editMode"
                placeholder="请输入用户名"
              />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="formData.email"
                :disabled="!editMode"
                placeholder="请输入邮箱"
              />
            </el-form-item>

            <el-form-item label="注册时间">
              <el-input
                :value="formatDate(userInfo?.createdTime)"
                disabled
                placeholder="-"
              />
            </el-form-item>

            <el-form-item label="账户状态">
              <el-tag :type="userInfo?.status === 1 ? 'success' : 'danger'">
                {{ userInfo?.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </el-form-item>

            <el-form-item v-if="editMode">
              <el-button type="primary" :loading="loading" @click="handleUpdate">
                保存修改
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </el-card>

    <!-- 密码修改卡片 -->
    <el-card class="password-card" shadow="hover">
      <template #header>
        <span class="card-title">密码管理</span>
      </template>

      <div class="password-section">
        <p class="password-tip">为了您的账户安全，建议定期更换密码</p>
        <el-button type="warning" @click="showPasswordDialog = true">
          <el-icon><Lock /></el-icon>
          修改密码
        </el-button>
      </div>
    </el-card>

    <!-- 安全信息卡片 -->
    <el-card class="security-card" shadow="hover">
      <template #header>
        <span class="card-title">安全信息</span>
      </template>

      <div class="security-info">
        <div class="security-item">
          <div class="security-label">
            <el-icon><Shield /></el-icon>
            <span>Token状态</span>
          </div>
          <el-tag type="success">有效</el-tag>
        </div>

        <div class="security-item">
          <div class="security-label">
            <el-icon><Clock /></el-icon>
            <span>最后登录</span>
          </div>
          <span>{{ lastLoginTime }}</span>
        </div>

        <div class="security-item">
          <div class="security-label">
            <el-icon><Location /></el-icon>
            <span>登录IP</span>
          </div>
          <span>127.0.0.1</span>
        </div>
      </div>
    </el-card>

        </div>
      </el-main>
    </el-container>

    <!-- 修改密码对话框 -->
    <ChangePasswordDialog v-model:visible="showPasswordDialog" />
  </el-container>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import ChangePasswordDialog from '@/components/ChangePasswordDialog.vue'

const router = useRouter()

const authStore = useAuthStore()
const formRef = ref<FormInstance>()

const editMode = ref(false)
const loading = ref(false)
const showPasswordDialog = ref(false)

// 用户信息
const userInfo = computed(() => authStore.userInfo)

// 表单数据
const formData = reactive({
  username: '',
  email: ''
})

// 表单验证规则
const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 最后登录时间（模拟）
const lastLoginTime = ref('')

// 下拉菜单命令处理
const handleCommand = async (command: string) => {
  switch (command) {
    case 'profile':
      // 已经在个人资料页面
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

// 格式化日期
const formatDate = (dateString?: string) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

// 重置表单
const resetForm = () => {
  if (userInfo.value) {
    formData.username = userInfo.value.username
    formData.email = userInfo.value.email || ''
  }
}

// 更新资料
const handleUpdate = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      // 这里应该调用更新用户信息的API
      // await updateUserProfile(formData)
      
      // 模拟更新成功
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      ElMessage.success('资料更新成功')
      editMode.value = false
      
      // 重新获取用户信息
      await authStore.fetchUserProfile()
    } catch (error: any) {
      console.error('更新失败:', error)
      ElMessage.error('更新失败')
    } finally {
      loading.value = false
    }
  })
}

onMounted(async () => {
  // 确保用户信息已加载
  if (!userInfo.value) {
    try {
      await authStore.fetchUserProfile()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  // 初始化表单数据
  resetForm()
  
  // 设置最后登录时间
  lastLoginTime.value = new Date().toLocaleString('zh-CN')
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

.profile-card {
  margin-bottom: 20px;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 20px;
}

.user-avatar {
  margin-bottom: 20px;
  background-color: #f0f2f5;
}

.avatar-info h3 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 18px;
}

.user-role {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.profile-form {
  margin-top: 20px;
}

.password-card,
.security-card {
  margin-bottom: 20px;
  width: 100%;
}

.password-section {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px 0;
}

.password-tip {
  margin: 0;
  color: #909399;
  flex: 1;
  font-size: 14px;
}

.security-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.security-item:last-child {
  border-bottom: none;
}

.security-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-row {
  width: 100%;
  margin: 0;
}

.el-col {
  width: 100%;
}

.el-menu {
  border: none;
}

.el-menu-item {
  height: 56px;
  line-height: 56px;
}

/* PC端不同分辨率适配 */
@media (min-width: 1920px) {
  .page-content {
    max-width: 1400px;
  }
  
  .main-content {
    padding: 24px;
  }
}

@media (min-width: 1440px) and (max-width: 1919px) {
  .page-content {
    max-width: 1200px;
  }
  
  .main-content {
    padding: 20px;
  }
}

@media (min-width: 1200px) and (max-width: 1439px) {
  .page-content {
    max-width: 1000px;
  }
  
  .main-content {
    padding: 16px;
  }
}

/* 小屏幕PC适配 */
@media (min-width: 1024px) and (max-width: 1199px) {
  .page-content {
    max-width: 100%;
  }
  
  .main-content {
    padding: 16px;
  }
  
  .el-row .el-col:first-child {
    span: 24 !important;
    margin-bottom: 20px;
  }
  
  .el-row .el-col:last-child {
    span: 24 !important;
  }
}

/* 平板适配 */
@media (max-width: 1023px) {
  .sidebar {
    width: 200px !important;
  }
  
  .main-content {
    padding: 15px;
  }
  
  .el-row .el-col {
    span: 24 !important;
    margin-bottom: 20px;
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .sidebar {
    width: 180px !important;
  }
  
  .main-content {
    padding: 12px;
  }
  
  .avatar-section {
    padding: 15px;
  }
  
  .user-avatar {
    width: 80px !important;
    height: 80px !important;
  }
}
</style>