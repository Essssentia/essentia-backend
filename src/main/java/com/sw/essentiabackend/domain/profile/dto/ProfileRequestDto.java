package com.sw.essentiabackend.domain.profile.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileRequestDto {

    private String intro;
    private String profileImageUrl;
}