package com.example.jwt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jwt.entity.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关联数据访问层
 * 
 * @author example
 * @since 2024-01-01
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 为用户分配角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Insert("INSERT INTO user_role (user_id, role_id, created_time) VALUES (#{userId}, #{roleId}, NOW())")
    int assignRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 移除用户角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Delete("DELETE FROM user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    int removeRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 移除用户的所有角色
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM user_role WHERE user_id = #{userId}")
    int removeAllRolesByUserId(@Param("userId") Long userId);
}