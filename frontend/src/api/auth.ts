import api from './index'
import type { LoginRequest, LoginResponse, RegisterRequest, ApiResponse } from './index'

// 用户登录
export const login = (data: LoginRequest) => {
  return api.post<ApiResponse<LoginResponse>>('/auth/login', data)
}

// 用户注册
export const register = (data: RegisterRequest) => {
  return api.post<ApiResponse<string>>('/auth/register', data)
}

// 刷新token
export const refreshToken = (refreshToken: string) => {
  return api.post<ApiResponse<LoginResponse>>('/auth/refresh', { refreshToken })
}

// 用户登出
export const logout = (refreshToken?: string) => {
  const params = refreshToken ? `?refreshToken=${refreshToken}` : ''
  return api.post<ApiResponse<string>>(`/auth/logout${params}`)
}