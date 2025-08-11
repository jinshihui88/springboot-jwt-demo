package com.example.jwt.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常类
 * 
 * @author example
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    public CustomException() {
        super();
    }

    public CustomException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public CustomException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public CustomException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 业务异常
     */
    public static CustomException business(String message) {
        return new CustomException(400, message);
    }

    /**
     * 认证异常
     */
    public static CustomException unauthorized(String message) {
        return new CustomException(401, message);
    }

    /**
     * 权限异常
     */
    public static CustomException forbidden(String message) {
        return new CustomException(403, message);
    }

    /**
     * 资源不存在异常
     */
    public static CustomException notFound(String message) {
        return new CustomException(404, message);
    }

    /**
     * 服务器内部错误
     */
    public static CustomException internal(String message) {
        return new CustomException(500, message);
    }

    /**
     * 参数验证异常
     */
    public static CustomException validation(String message) {
        return new CustomException(422, message);
    }

    /**
     * 请求过于频繁异常
     */
    public static CustomException tooManyRequests(String message) {
        return new CustomException(429, message);
    }
}