package com.example.jwt.util;

import com.example.jwt.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 响应工具类
 * 
 * @author example
 * @since 2024-01-01
 */
@Slf4j
public class ResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 写入JSON响应
     * 
     * @param response HttpServletResponse
     * @param status HTTP状态码
     * @param apiResponse API响应对象
     */
    public static void writeJsonResponse(HttpServletResponse response, HttpStatus status, ApiResponse<?> apiResponse) {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            String json = objectMapper.writeValueAsString(apiResponse);
            response.getWriter().write(json);
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("写入JSON响应失败: {}", e.getMessage());
        }
    }

    /**
     * 写入成功响应
     * 
     * @param response HttpServletResponse
     * @param data 响应数据
     */
    public static void writeSuccessResponse(HttpServletResponse response, Object data) {
        ApiResponse<Object> apiResponse = ApiResponse.success(data);
        writeJsonResponse(response, HttpStatus.OK, apiResponse);
    }

    /**
     * 写入成功响应（无数据）
     * 
     * @param response HttpServletResponse
     * @param message 响应消息
     */
    public static void writeSuccessResponse(HttpServletResponse response, String message) {
        ApiResponse<Object> apiResponse = ApiResponse.success(message, null);
        writeJsonResponse(response, HttpStatus.OK, apiResponse);
    }

    /**
     * 写入错误响应
     * 
     * @param response HttpServletResponse
     * @param status HTTP状态码
     * @param message 错误消息
     */
    public static void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message) {
        ApiResponse<Object> apiResponse = ApiResponse.error(status.value(), message);
        writeJsonResponse(response, status, apiResponse);
    }

    /**
     * 写入未认证响应
     * 
     * @param response HttpServletResponse
     * @param message 错误消息
     */
    public static void writeUnauthorizedResponse(HttpServletResponse response, String message) {
        writeErrorResponse(response, HttpStatus.UNAUTHORIZED, message);
    }

    /**
     * 写入禁止访问响应
     * 
     * @param response HttpServletResponse
     * @param message 错误消息
     */
    public static void writeForbiddenResponse(HttpServletResponse response, String message) {
        writeErrorResponse(response, HttpStatus.FORBIDDEN, message);
    }

    /**
     * 写入服务器错误响应
     * 
     * @param response HttpServletResponse
     * @param message 错误消息
     */
    public static void writeInternalServerErrorResponse(HttpServletResponse response, String message) {
        writeErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 写入参数错误响应
     * 
     * @param response HttpServletResponse
     * @param message 错误消息
     */
    public static void writeBadRequestResponse(HttpServletResponse response, String message) {
        writeErrorResponse(response, HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 写入资源不存在响应
     * 
     * @param response HttpServletResponse
     * @param message 错误消息
     */
    public static void writeNotFoundResponse(HttpServletResponse response, String message) {
        writeErrorResponse(response, HttpStatus.NOT_FOUND, message);
    }

    /**
     * 写入请求方法不支持响应
     * 
     * @param response HttpServletResponse
     * @param message 错误消息
     */
    public static void writeMethodNotAllowedResponse(HttpServletResponse response, String message) {
        writeErrorResponse(response, HttpStatus.METHOD_NOT_ALLOWED, message);
    }

    /**
     * 写入请求过于频繁响应
     * 
     * @param response HttpServletResponse
     * @param message 错误消息
     */
    public static void writeTooManyRequestsResponse(HttpServletResponse response, String message) {
        writeErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS, message);
    }
}