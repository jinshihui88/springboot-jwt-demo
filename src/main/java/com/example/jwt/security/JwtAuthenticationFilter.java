package com.example.jwt.security;

import com.example.jwt.service.JwtService;
import com.example.jwt.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 * 
 * @author example
 * @since 2024-01-01
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${jwt.token-header}")
    private String tokenHeader;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        // 移除context-path前缀来进行路径匹配
        String contextPath = request.getContextPath();
        String pathToMatch = requestURI;
        if (contextPath != null && !contextPath.isEmpty() && requestURI.startsWith(contextPath)) {
            pathToMatch = requestURI.substring(contextPath.length());
        }

        log.info("JWT过滤器处理请求路径: {}, 匹配路径: {}", requestURI, pathToMatch);

        // 跳过无需认证的路径
        if (shouldSkipAuthentication(pathToMatch)) {
            log.info("跳过JWT认证，路径: {} (匹配路径: {})", requestURI, pathToMatch);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 获取JWT令牌
            String token = getTokenFromRequest(request);
            log.info("token: {}", token);

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 验证令牌
                if (jwtService.validateAccessToken(token)) {
                    // 获取用户名
                    String username = jwtService.getUsernameFromToken(token);

                    // 加载用户详情
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 设置认证信息到安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("设置用户认证信息到安全上下文: {}", username);
                } else {
                    log.debug("JWT令牌验证失败");
                }
            }
        } catch (Exception e) {
            log.error("JWT认证过滤器异常: {}", e.getMessage());
            ResponseUtil.writeUnauthorizedResponse(response, "认证失败");
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 判断是否应该跳过认证
     * @param requestURI 请求URI
     * @return true表示跳过认证，false表示需要认证
     */
    private boolean shouldSkipAuthentication(String requestURI) {
        // 无需认证的路径列表
        String[] skipPaths = {
            "/auth/",
            "/swagger-ui/",
            "/v3/api-docs",
            "/actuator/health",
            "/car-model/"
        };

        for (String path : skipPaths) {
            if (requestURI.startsWith(path)) {
                log.info("跳过JWT认证，路径: {} 匹配规则: {}", requestURI, path);
                return true;
            }
        }
        log.info("需要JWT认证，路径: {}", requestURI);
        return false;
    }

    /**
     * 从请求中获取JWT令牌
     * 
     * @param request HTTP请求
     * @return String JWT令牌
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(tokenHeader);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenPrefix + " ")) {
            return bearerToken.substring(tokenPrefix.length() + 1);
        }
        return null;
    }
}