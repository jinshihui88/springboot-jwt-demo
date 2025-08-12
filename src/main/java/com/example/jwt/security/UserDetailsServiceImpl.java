package com.example.jwt.security;

import com.example.jwt.entity.Permission;
import com.example.jwt.entity.Role;
import com.example.jwt.entity.User;
import com.example.jwt.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户详情服务实现类
 * 
 * @author example
 * @since 2024-01-01
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        User user = userMapper.findByUsername(username);
        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 查询用户角色
        List<Role> roles = userMapper.findRolesByUserId(user.getId());
        
        // 查询用户权限
        List<Permission> permissions = userMapper.findPermissionsByUserId(user.getId());
        
        log.debug("加载用户详情: {}, 角色数: {}, 权限数: {}", username, roles.size(), permissions.size());
        
        return new UserDetailsImpl(user, roles, permissions);
    }
}