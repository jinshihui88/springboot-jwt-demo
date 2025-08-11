package com.example.jwt.service;

import com.example.jwt.dto.RefreshTokenRequest;
import com.example.jwt.dto.LoginResponse;
import com.example.jwt.exception.CustomException;
import com.example.jwt.util.JwtUtil;
import com.example.jwt.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * JWT服务类
 * 
 * @author example
 * @since 2024-01-01
 */
@Slf4j
@Service
public class JwtService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 生成登录响应
     * 
     * @param userDetails 用户详情
     * @param userId 用户ID
     * @return 登录响应
     */
    public LoginResponse generateLoginResponse(UserDetails userDetails, Long userId) {
        // 生成访问令牌
        String accessToken = jwtUtil.generateAccessTokenWithUserId(userDetails, userId);
        
        // 生成刷新令牌
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        // 存储刷新令牌到Redis
        long refreshTokenExpiration = System.currentTimeMillis() + jwtUtil.getRefreshTokenExpiration();
        redisUtil.storeRefreshToken(userDetails.getUsername(), refreshToken, refreshTokenExpiration);
        
        // 构建用户信息
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(userId)
                .username(userDetails.getUsername())
                .build();
        
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration())
                .userInfo(userInfo)
                .build();
    }

    /**
     * 刷新访问令牌
     * 
     * @param request 刷新令牌请求
     * @return 新的登录响应
     */
    public LoginResponse refreshAccessToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        // 验证刷新令牌格式
        if (!jwtUtil.validateTokenFormat(refreshToken)) {
            throw CustomException.unauthorized("刷新令牌格式无效");
        }
        
        // 检查令牌类型
        String tokenType = jwtUtil.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw CustomException.unauthorized("令牌类型错误");
        }
        
        // 检查令牌是否过期
        if (jwtUtil.isTokenExpired(refreshToken)) {
            throw CustomException.unauthorized("刷新令牌已过期");
        }
        
        // 检查令牌是否在黑名单中
        if (redisUtil.isTokenBlacklisted(refreshToken)) {
            throw CustomException.unauthorized("刷新令牌已失效");
        }
        
        // 获取用户名
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        
        // 验证Redis中存储的刷新令牌
        String storedRefreshToken = redisUtil.getRefreshToken(username);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw CustomException.unauthorized("刷新令牌无效");
        }
        
        // 加载用户详情
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // 验证令牌
        if (!jwtUtil.validateToken(refreshToken, userDetails)) {
            throw CustomException.unauthorized("刷新令牌验证失败");
        }
        
        // 获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        
        // 生成新的访问令牌
        String newAccessToken = jwtUtil.generateAccessTokenWithUserId(userDetails, userId);
        
        // 构建响应
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration())
                .userInfo(LoginResponse.UserInfo.builder()
                        .id(userId)
                        .username(username)
                        .build())
                .build();
    }

    /**
     * 登出处理
     * 
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     */
    public void logout(String accessToken, String refreshToken) {
        try {
            // 获取用户名
            String username = null;
            if (accessToken != null && jwtUtil.validateTokenFormat(accessToken)) {
                username = jwtUtil.getUsernameFromToken(accessToken);
            } else if (refreshToken != null && jwtUtil.validateTokenFormat(refreshToken)) {
                username = jwtUtil.getUsernameFromToken(refreshToken);
            }
            
            // 将访问令牌加入黑名单
            if (accessToken != null && jwtUtil.validateTokenFormat(accessToken)) {
                long accessTokenExpiration = System.currentTimeMillis() + jwtUtil.getAccessTokenExpiration();
                redisUtil.addTokenToBlacklist(accessToken, accessTokenExpiration);
            }
            
            // 将刷新令牌加入黑名单
            if (refreshToken != null && jwtUtil.validateTokenFormat(refreshToken)) {
                long refreshTokenExpiration = System.currentTimeMillis() + jwtUtil.getRefreshTokenExpiration();
                redisUtil.addTokenToBlacklist(refreshToken, refreshTokenExpiration);
            }
            
            // 删除Redis中存储的刷新令牌
            if (username != null) {
                redisUtil.removeRefreshToken(username);
            }
            
            log.info("用户登出成功: {}", username);
        } catch (Exception e) {
            log.error("登出处理异常: {}", e.getMessage());
            throw CustomException.internal("登出处理失败");
        }
    }

    /**
     * 验证访问令牌
     * 
     * @param token 访问令牌
     * @return 是否有效
     */
    public boolean validateAccessToken(String token) {
        try {
            // 验证令牌格式
            if (!jwtUtil.validateTokenFormat(token)) {
                return false;
            }
            
            // 检查令牌类型
            String tokenType = jwtUtil.getTokenType(token);
            if (!"access".equals(tokenType)) {
                return false;
            }
            
            // 检查令牌是否过期
            if (jwtUtil.isTokenExpired(token)) {
                return false;
            }
            
            // 检查令牌是否在黑名单中
            if (redisUtil.isTokenBlacklisted(token)) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("验证访问令牌异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中获取用户名
     * 
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    /**
     * 从令牌中获取用户ID
     * 
     * @param token 令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }
}