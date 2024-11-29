package com.sw.essentiabackend.domain.profile.dto;

import com.sw.essentiabackend.domain.profile.entity.Profile;
import lombok.Getter;

@Getter
public class ProfileResponseDto {

    private Long userId;
    private String intro;
    private String profileImageUrl;

    public ProfileResponseDto(Profile profile) {
        this.userId = profile.getUser().getId();
        this.intro = profile.getIntro();
        this.profileImageUrl = profile.getProfileImageUrl();
    }
}