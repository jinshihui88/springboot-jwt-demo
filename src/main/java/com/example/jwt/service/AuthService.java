package com.example.jwt.service;

import com.example.jwt.dto.LoginRequest;
import com.example.jwt.dto.LoginResponse;
import com.example.jwt.dto.RegisterRequest;
import com.example.jwt.entity.User;
import com.example.jwt.exception.CustomException;
import com.example.jwt.mapper.UserMapper;
import com.example.jwt.security.UserDetailsImpl;
import com.example.jwt.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务类
 * 
 * @author example
 * @since 2024-01-01
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用户登录
     * 
     * @param request 登录请求
     * @return 登录响应
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        
        try {
            // 检查账户是否被锁定
            checkAccountLock(username);
            
            // 进行身份认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword())
            );
            
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetails.getUser();
            
            // 重置登录失败次数
            if (redisUtil.getLoginFailCount(username) > 0) {
                redisUtil.clearLoginFailRecord(username);
                userMapper.resetLoginFailCount(user.getId());
            }
            
            // 生成JWT令牌
            LoginResponse response = jwtService.generateLoginResponse(userDetails, user.getId());
            
            log.info("用户登录成功: {}", username);
            return response;
            
        } catch (BadCredentialsException e) {
            // 处理登录失败
            handleLoginFailure(username);
            throw CustomException.unauthorized("用户名或密码错误");
            
        } catch (DisabledException e) {
            throw CustomException.unauthorized("账户已被禁用");
            
        } catch (AuthenticationException e) {
            log.error("认证异常: {} - {}", username, e.getMessage());
            throw CustomException.unauthorized("认证失败");
        }
    }

    /**
     * 用户注册
     * 
     * @param request 注册请求
     */
    @Transactional
    public void register(RegisterRequest request) {
        // 验证密码确认
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw CustomException.validation("两次输入的密码不一致");
        }
        
        // 检查用户名是否已存在
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw CustomException.business("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw CustomException.business("邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setStatus(1); // 正常状态
        user.setLoginFailCount(0);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        user.setDeleted(0);
        
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw CustomException.internal("用户注册失败");
        }
        
        // 为新用户分配默认角色 (USER角色，ID为2)
        userMapper.insertUserRole(user.getId(), 2L);
        
        log.info("用户注册成功: {}", request.getUsername());
    }

    /**
     * 检查账户锁定状态
     * 
     * @param username 用户名
     */
    private void checkAccountLock(String username) {
        User user = userMapper.findByUsername(username);
        if (user != null && user.getLockTime() != null) {
            LocalDateTime lockTime = user.getLockTime();
            LocalDateTime unlockTime = lockTime.plusMinutes(30); // 锁定30分钟
            
            if (LocalDateTime.now().isBefore(unlockTime)) {
                throw CustomException.unauthorized("账户已被锁定，请30分钟后重试");
            } else {
                // 解锁账户
                userMapper.unlockUser(user.getId());
                redisUtil.clearLoginFailRecord(username);
            }
        }
    }

    /**
     * 处理登录失败
     * 
     * @param username 用户名
     */
    private void handleLoginFailure(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return;
        }
        
        int failCount = redisUtil.getLoginFailCount(username) + 1;
        redisUtil.recordLoginFailure(username, failCount, 30); // Redis中记录30分钟
        
        // 更新数据库中的失败次数
        userMapper.updateLoginFailCount(user.getId(), failCount);
        
        // 如果失败次数达到5次，锁定账户
        if (failCount >= 5) {
            userMapper.lockUser(user.getId());
            log.warn("用户账户已被锁定: {}", username);
        }
    }
}