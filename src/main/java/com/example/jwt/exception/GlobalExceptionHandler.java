package com.example.jwt.exception;

import com.example.jwt.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器
 * 
 * @author example
 * @since 2024-01-01
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e, HttpServletRequest request) {
        log.error("自定义异常: {} - {}", request.getRequestURI(), e.getMessage());
        ApiResponse<Object> response = ApiResponse.error(e.getCode(), e.getMessage());
        return ResponseEntity.status(getHttpStatus(e.getCode())).body(response);
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("参数验证异常: {} - {}", request.getRequestURI(), e.getMessage());
        
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMessage.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }
        
        ApiResponse<Object> response = ApiResponse.error(400, "参数验证失败: " + errorMessage.toString());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(BindException e, HttpServletRequest request) {
        log.error("绑定异常: {} - {}", request.getRequestURI(), e.getMessage());
        
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMessage.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }
        
        ApiResponse<Object> response = ApiResponse.error(400, "参数绑定失败: " + errorMessage.toString());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.error("约束违反异常: {} - {}", request.getRequestURI(), e.getMessage());
        
        StringBuilder errorMessage = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
        }
        
        ApiResponse<Object> response = ApiResponse.error(400, "约束验证失败: " + errorMessage.toString());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.error("缺少请求参数异常: {} - {}", request.getRequestURI(), e.getMessage());
        ApiResponse<Object> response = ApiResponse.error(400, "缺少必需的请求参数: " + e.getParameterName());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理方法参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.error("参数类型不匹配异常: {} - {}", request.getRequestURI(), e.getMessage());
        ApiResponse<Object> response = ApiResponse.error(400, "参数类型不匹配: " + e.getName());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理HTTP请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("HTTP方法不支持异常: {} - {}", request.getRequestURI(), e.getMessage());
        ApiResponse<Object> response = ApiResponse.error(405, "不支持的HTTP方法: " + e.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error("资源不存在异常: {} - {}", request.getRequestURI(), e.getMessage());
        ApiResponse<Object> response = ApiResponse.error(404, "请求的资源不存在: " + e.getRequestURL());
        return ResponseEntity.notFound().build();
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.error("认证失败异常: {} - {}", request.getRequestURI(), e.getMessage());
        ApiResponse<Object> response = ApiResponse.error(401, "用户名或密码错误");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 处理账户禁用异常
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Object>> handleDisabledException(DisabledException e, HttpServletRequest request) {
        log.error("账户禁用异常: {} - {}", request.getRequestURI(), e.getMessage());
        ApiResponse<Object> response = ApiResponse.error(401, "账户已被禁用");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 处理账户锁定异常
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Object>> handleLockedException(LockedException e, HttpServletRequest request) {
        log.error("账户锁定异常: {} - {}", request.getRequestURI(), e.getMessage());
        ApiResponse<Object> response = ApiResponse.error(401, "账户已被锁定");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("权限不足异常: {} - {}", request.getRequestURI(), e.getMessage());
        ApiResponse<Object> response = ApiResponse.error(403, "权限不足，访问被拒绝");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception e, HttpServletRequest request) {
        log.error("未知异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        ApiResponse<Object> response = ApiResponse.error(500, "服务器内部错误，请稍后重试");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 根据错误码获取HTTP状态码
     */
    private HttpStatus getHttpStatus(Integer code) {
        switch (code) {
            case 400:
                return HttpStatus.BAD_REQUEST;
            case 401:
                return HttpStatus.UNAUTHORIZED;
            case 403:
                return HttpStatus.FORBIDDEN;
            case 404:
                return HttpStatus.NOT_FOUND;
            case 405:
                return HttpStatus.METHOD_NOT_ALLOWED;
            case 422:
                return HttpStatus.UNPROCESSABLE_ENTITY;
            case 429:
                return HttpStatus.TOO_MANY_REQUESTS;
            case 500:
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}