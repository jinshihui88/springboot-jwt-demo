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
          <el-menu-item index="/admin/users">
            <el-icon><UserFilled /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区域 -->
      <el-main class="main-content">
        <div class="page-content">
          <el-card class="users-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">用户管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
            <el-button type="success">
              <el-icon><Plus /></el-icon>
              添加用户
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :model="searchForm" :inline="true" class="search-form">
          <el-form-item label="用户名">
            <el-input
              v-model="searchForm.username"
              placeholder="请输入用户名"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input
              v-model="searchForm.email"
              placeholder="请输入邮箱"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 120px"
            >
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><RefreshLeft /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 用户表格 -->
      <el-table
        v-loading="loading"
        :data="userList"
        style="width: 100%"
        stripe
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { getAllUsers } from '@/api/user'
import ChangePasswordDialog from '@/components/ChangePasswordDialog.vue'

const router = useRouter()
const authStore = useAuthStore()
const showPasswordDialog = ref(false)

// 加载状态
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  username: '',
  email: '',
  status: null as number | null
})

// 用户列表
const userList = ref<any[]>([])

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 模拟用户数据
const mockUsers = [
  {
    id: 1,
    username: 'admin',
    email: 'admin@example.com',
    status: 1,
    createdTime: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    username: 'user',
    email: 'user@example.com',
    status: 1,
    createdTime: '2024-01-02 14:30:00'
  },
  {
    id: 3,
    username: 'test',
    email: 'test@example.com',
    status: 0,
    createdTime: '2024-01-03 09:15:00'
  }
]

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    // 这里应该调用真实的API
    // const response = await getAllUsers()
    // userList.value = response.data.data
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    userList.value = mockUsers
    pagination.total = mockUsers.length
  } catch (error: any) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchUserList()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    username: '',
    email: '',
    status: null
  })
  handleSearch()
}

// 刷新数据
const refreshData = () => {
  fetchUserList()
}

// 编辑用户
const handleEdit = (row: any) => {
  ElMessage.info(`编辑用户: ${row.username}`)
  // 这里可以打开编辑对话框
}

// 切换用户状态
const handleToggleStatus = async (row: any) => {
  try {
    const action = row.status === 1 ? '禁用' : '启用'
    await ElMessageBox.confirm(`确定要${action}用户 ${row.username} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 这里应该调用API更新状态
    row.status = row.status === 1 ? 0 : 1
    ElMessage.success(`${action}成功`)
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 删除用户
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 ${row.username} 吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    // 这里应该调用API删除用户
    const index = userList.value.findIndex(user => user.id === row.id)
    if (index > -1) {
      userList.value.splice(index, 1)
      pagination.total--
    }
    
    ElMessage.success('删除成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 页面大小改变
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  fetchUserList()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.currentPage = page
  fetchUserList()
}

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
  
  fetchUserList()
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

.users-card {
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

.header-actions {
  display: flex;
  gap: 12px;
}

.search-bar {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 6px;
  width: 100%;
}

.search-form {
  margin: 0;
  width: 100%;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  width: 100%;
}

.el-table {
  margin-bottom: 20px;
  width: 100%;
}

.el-form-item {
  margin-right: 20px;
  margin-bottom: 10px;
}

.el-button {
  margin: 0 5px;
}

/* PC端不同分辨率适配 */
@media (min-width: 1920px) {
  .page-content {
    max-width: 1400px;
  }
  
  .main-content {
    padding: 24px;
  }
  
  .search-bar {
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
  
  .search-bar {
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
  
  .search-form .el-form-item {
    margin-right: 15px;
    margin-bottom: 15px;
  }
  
  .el-table {
    font-size: 13px;
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
  
  .search-bar {
    padding: 15px;
  }
  
  .search-form .el-form-item {
    margin-right: 10px;
    margin-bottom: 12px;
  }
  
  .el-table {
    font-size: 12px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-start;
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
  
  .search-bar {
    padding: 12px;
  }
  
  .search-form {
    flex-direction: column;
  }
  
  .search-form .el-form-item {
    margin-right: 0;
    margin-bottom: 15px;
    width: 100%;
  }
  
  .search-form .el-input,
  .search-form .el-select {
    width: 100% !important;
  }
  
  .el-table {
    font-size: 11px;
  }
  
  .pagination {
    flex-direction: column;
    gap: 10px;
  }
}
</style>