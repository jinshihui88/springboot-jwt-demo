import api from './index'
import type { UserProfile, ApiResponse } from './index'

// 获取用户资料
export const getUserProfile = () => {
  return api.get<ApiResponse<UserProfile>>('/user/profile')
}

// 修改密码
export const changePassword = (data: { oldPassword: string; newPassword: string; confirmPassword: string }) => {
  return api.put<ApiResponse<string>>('/user/password', data)
}

// 获取所有用户（管理员权限）
export const getAllUsers = () => {
  return api.get<ApiResponse<any>>('/user/admin/users')
}