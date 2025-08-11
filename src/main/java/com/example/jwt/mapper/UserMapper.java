package com.example.jwt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jwt.entity.Permission;
import com.example.jwt.entity.Role;
import com.example.jwt.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据访问层
 * 
 * @author example
 * @since 2024-01-01
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户信息
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE email = #{email} AND deleted = 0")
    User findByEmail(@Param("email") String email);

    /**
     * 根据用户ID查询用户角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM role r " +
            "INNER JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询用户权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select("SELECT DISTINCT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.deleted = 0")
    List<Permission> findPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 更新用户登录失败次数
     * 
     * @param userId 用户ID
     * @param failCount 失败次数
     * @return 影响行数
     */
    int updateLoginFailCount(@Param("userId") Long userId, @Param("failCount") Integer failCount);

    /**
     * 重置用户登录失败次数
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    int resetLoginFailCount(@Param("userId") Long userId);

    /**
     * 锁定用户账户
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    int lockUser(@Param("userId") Long userId);

    /**
     * 解锁用户账户
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    int unlockUser(@Param("userId") Long userId);

    /**
     * 为用户分配角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Insert("INSERT INTO user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}