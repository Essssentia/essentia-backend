package com.sw.essentiabackend.domain.profile.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ProfileRequestDto {

    private String bio;
    private String profileImageUrl;
}