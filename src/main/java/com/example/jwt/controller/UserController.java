package com.example.jwt.controller;

import com.example.jwt.dto.ApiResponse;
import com.example.jwt.dto.ChangePasswordRequest;
import com.example.jwt.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 
 * @author example
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户相关API")
public class UserController {

    /**
     * 获取用户信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的基本信息")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", userDetails.getUser().getId());
        profile.put("username", userDetails.getUser().getUsername());
        profile.put("email", userDetails.getUser().getEmail());
        profile.put("status", userDetails.getUser().getStatus());
        profile.put("createdTime", userDetails.getUser().getCreatedTime());
        
        log.info("获取用户信息: {}", userDetails.getUsername());
        return ApiResponse.success("获取用户信息成功", profile);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前用户的登录密码")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        log.info("用户修改密码: {}", userDetails.getUsername());
        // 这里应该调用用户服务的修改密码方法
        // userService.changePassword(userDetails.getUser().getId(), request);
        
        return ApiResponse.success("密码修改成功");
    }

    /**
     * 管理员接口示例
     */
    @GetMapping("/admin/users")
    @Operation(summary = "获取所有用户（管理员）", description = "管理员权限：获取系统所有用户信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> getAllUsers() {
        log.info("管理员获取所有用户");
        return ApiResponse.success("管理员功能测试成功", "这是管理员才能访问的接口");
    }
}