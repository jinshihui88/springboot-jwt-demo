package com.example.jwt.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 刷新令牌请求DTO
 * 
 * @author example
 * @since 2024-01-01
 */
@Data
public class RefreshTokenRequest {

    /**
     * 刷新令牌
     */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}