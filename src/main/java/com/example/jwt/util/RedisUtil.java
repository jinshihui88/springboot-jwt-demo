package com.example.jwt.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * 
 * @author example
 * @since 2024-01-01
 */
@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 存储数据
     * 
     * @param key 键
     * @param value 值
     * @return 是否成功
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("Redis存储数据失败，key: {}, error: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 存储数据并设置过期时间
     * 
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否成功
     */
    public boolean set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            log.error("Redis存储数据失败，key: {}, error: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 获取数据
     * 
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis获取数据失败，key: {}, error: {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * 删除数据
     * 
     * @param key 键
     * @return 是否成功
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Redis删除数据失败，key: {}, error: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 判断键是否存在
     * 
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis判断键是否存在失败，key: {}, error: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 设置过期时间
     * 
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否成功
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            log.error("Redis设置过期时间失败，key: {}, error: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 获取过期时间
     * 
     * @param key 键
     * @return 过期时间（秒）
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key);
            return expire != null ? expire : -1;
        } catch (Exception e) {
            log.error("Redis获取过期时间失败，key: {}, error: {}", key, e.getMessage());
            return -1;
        }
    }

    /**
     * 将token加入黑名单
     * 
     * @param token token
     * @param expireTime 过期时间（毫秒）
     */
    public void addTokenToBlacklist(String token, long expireTime) {
        String key = "blacklist:token:" + token;
        long ttl = expireTime - System.currentTimeMillis();
        if (ttl > 0) {
            set(key, "blacklisted", ttl, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 检查token是否在黑名单中
     * 
     * @param token token
     * @return 是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        String key = "blacklist:token:" + token;
        return hasKey(key);
    }

    /**
     * 存储刷新token
     * 
     * @param username 用户名
     * @param refreshToken 刷新token
     * @param expireTime 过期时间（毫秒）
     */
    public void storeRefreshToken(String username, String refreshToken, long expireTime) {
        String key = "refresh_token:" + username;
        long ttl = expireTime - System.currentTimeMillis();
        if (ttl > 0) {
            set(key, refreshToken, ttl, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 获取刷新token
     * 
     * @param username 用户名
     * @return 刷新token
     */
    public String getRefreshToken(String username) {
        String key = "refresh_token:" + username;
        Object token = get(key);
        return token != null ? token.toString() : null;
    }

    /**
     * 删除刷新token
     * 
     * @param username 用户名
     */
    public void removeRefreshToken(String username) {
        String key = "refresh_token:" + username;
        delete(key);
    }

    /**
     * 记录登录失败次数
     * 
     * @param username 用户名
     * @param failCount 失败次数
     * @param lockTime 锁定时间（分钟）
     */
    public void recordLoginFailure(String username, int failCount, int lockTime) {
        String key = "login_fail:" + username;
        set(key, failCount, lockTime, TimeUnit.MINUTES);
    }

    /**
     * 获取登录失败次数
     * 
     * @param username 用户名
     * @return 失败次数
     */
    public int getLoginFailCount(String username) {
        String key = "login_fail:" + username;
        Object count = get(key);
        return count != null ? Integer.parseInt(count.toString()) : 0;
    }

    /**
     * 清除登录失败记录
     * 
     * @param username 用户名
     */
    public void clearLoginFailRecord(String username) {
        String key = "login_fail:" + username;
        delete(key);
    }
}