package com.sw.essentiabackend.domain.profile.dto;

import com.sw.essentiabackend.domain.profile.entity.Profile;
import lombok.Getter;

@Getter
public class ProfileResponseDto {

    private Long userId;
    private String username;
    private String bio;
    private String profileImageUrl;

    public ProfileResponseDto(Profile profile) {
        this.userId = profile.getUser().getId();
        this.username = profile.getUser().getUsername();
        this.bio = profile.getBio();
        this.profileImageUrl = profile.getProfileImageUrl();
    }
}