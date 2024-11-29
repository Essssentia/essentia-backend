package com.sw.essentiabackend.config.oauth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserInfoDto {
    private Long id; // 구글 사용자 고유 ID
    private String name; // 구글 사용자 이름
    private String email; // 구글 사용자 이메일

    public GoogleUserInfoDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}