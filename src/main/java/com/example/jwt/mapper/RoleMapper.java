package com.example.jwt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jwt.entity.Permission;
import com.example.jwt.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色数据访问层
 * 
 * @author example
 * @since 2024-01-01
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色名称查询角色信息
     * 
     * @param roleName 角色名称
     * @return 角色信息
     */
    @Select("SELECT * FROM role WHERE role_name = #{roleName} AND deleted = 0")
    Role findByRoleName(@Param("roleName") String roleName);

    /**
     * 根据角色ID查询权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Select("SELECT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.deleted = 0")
    List<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 为角色分配权限
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 影响行数
     */
    int assignPermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 移除角色权限
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 影响行数
     */
    int removePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}