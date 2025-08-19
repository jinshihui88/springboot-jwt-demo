import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import * as authApi from '@/api/auth'
import * as userApi from '@/api/user'
import type { LoginRequest, RegisterRequest, UserProfile } from '@/api/index'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const accessToken = ref<string | null>(localStorage.getItem('accessToken'))
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'))
  const userInfo = ref<UserProfile | null>(null)
  const loading = ref(false)

  // 计算属性
  const isAuthenticated = computed(() => !!accessToken.value)
  const isAdmin = computed(() => userInfo.value?.username === 'admin') // 简单的管理员判断

  // 保存token到localStorage
  const saveTokens = (access: string, refresh: string) => {
    accessToken.value = access
    refreshToken.value = refresh
    localStorage.setItem('accessToken', access)
    localStorage.setItem('refreshToken', refresh)
  }

  // 清除token
  const clearTokens = () => {
    accessToken.value = null
    refreshToken.value = null
    userInfo.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }

  // 登录
  const login = async (loginData: LoginRequest) => {
    loading.value = true
    try {
      const response = await authApi.login(loginData)
      const { accessToken: access, refreshToken: refresh, userInfo: user } = response.data.data
      
      saveTokens(access, refresh)
      userInfo.value = user as UserProfile
      
      return response.data
    } catch (error) {
      clearTokens()
      throw error
    } finally {
      loading.value = false
    }
  }

  // 注册
  const register = async (registerData: RegisterRequest) => {
    loading.value = true
    try {
      const response = await authApi.register(registerData)
      return response.data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = async () => {
    loading.value = true
    try {
      if (refreshToken.value) {
        await authApi.logout(refreshToken.value)
      }
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      clearTokens()
      loading.value = false
    }
  }

  // 获取用户资料
  const fetchUserProfile = async () => {
    try {
      const response = await userApi.getUserProfile()
      userInfo.value = response.data.data
      return response.data
    } catch (error) {
      throw error
    }
  }

  // 初始化用户信息（如果已登录）
  const initializeAuth = async () => {
    if (accessToken.value && !userInfo.value) {
      try {
        await fetchUserProfile()
      } catch (error) {
        // 如果获取用户信息失败，清除token
        clearTokens()
      }
    }
  }

  return {
    // 状态
    accessToken,
    refreshToken,
    userInfo,
    loading,
    // 计算属性
    isAuthenticated,
    isAdmin,
    // 方法
    login,
    register,
    logout,
    fetchUserProfile,
    initializeAuth,
    clearTokens
  }
})