package com.example.jwt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jwt.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限数据访问层
 * 
 * @author example
 * @since 2024-01-01
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据权限名称查询权限信息
     * 
     * @param permissionName 权限名称
     * @return 权限信息
     */
    @Select("SELECT * FROM permission WHERE permission_name = #{permissionName} AND deleted = 0")
    Permission findByPermissionName(@Param("permissionName") String permissionName);

    /**
     * 根据URL和HTTP方法查询权限信息
     * 
     * @param url URL
     * @param method HTTP方法
     * @return 权限列表
     */
    @Select("SELECT * FROM permission WHERE url = #{url} AND method = #{method} AND deleted = 0")
    List<Permission> findByUrlAndMethod(@Param("url") String url, @Param("method") String method);

    /**
     * 查询所有权限
     * 
     * @return 权限列表
     */
    @Select("SELECT * FROM permission WHERE deleted = 0 ORDER BY created_time ASC")
    List<Permission> findAllPermissions();
}