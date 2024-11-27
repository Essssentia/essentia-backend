package com.sw.essentiabackend.domain.auth.dto;

import lombok.Getter;

@Getter
public class TokenResponseDto {
    private Long userId;
    private String accessToken;
    private String refreshToken;

    public TokenResponseDto(Long userId, String accessToken, String refreshToken) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}