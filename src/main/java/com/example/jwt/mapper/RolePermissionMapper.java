package com.example.jwt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jwt.entity.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色权限关联数据访问层
 * 
 * @author example
 * @since 2024-01-01
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 为角色分配权限
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 影响行数
     */
    @Insert("INSERT INTO role_permission (role_id, permission_id, created_time) VALUES (#{roleId}, #{permissionId}, NOW())")
    int assignPermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 移除角色权限
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 影响行数
     */
    @Delete("DELETE FROM role_permission WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    int removePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 移除角色的所有权限
     * 
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Delete("DELETE FROM role_permission WHERE role_id = #{roleId}")
    int removeAllPermissionsByRoleId(@Param("roleId") Long roleId);
}