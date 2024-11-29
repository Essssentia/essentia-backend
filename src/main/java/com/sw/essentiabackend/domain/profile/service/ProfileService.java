package com.sw.essentiabackend.domain.profile.service;

import com.sw.essentiabackend.common.exception.CustomException;
import com.sw.essentiabackend.common.exception.ErrorCode;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.profile.dto.ProfileRequestDto;
import com.sw.essentiabackend.domain.profile.dto.ProfileResponseDto;
import com.sw.essentiabackend.domain.profile.entity.Profile;
import com.sw.essentiabackend.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    /**
     * 프로필 생성 로직
     *
     * @param user       프로필을 생성할 사용자
     * @param requestDto 프로필 생성 요청 데이터
     * @return ProfileResponseDto 생성된 프로필 데이터
     * @throws CustomException 이미 프로필이 존재하는 경우
     */
    @Transactional
    public ProfileResponseDto createProfile(User user, ProfileRequestDto requestDto) {
        if (profileRepository.findByUserId(user.getId()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_PROFILE);
        }

        Profile profile = new Profile(user, requestDto.getIntro(), requestDto.getProfileImageUrl());
        profileRepository.save(profile);
        return new ProfileResponseDto(profile);
    }

    /**
     * 프로필 수정 로직
     *
     * @param user       프로필을 수정할 사용자
     * @param requestDto 프로필 수정 요청 데이터
     * @return ProfileResponseDto 수정된 프로필 데이터
     * @throws CustomException 프로필이 존재하지 않는 경우
     */
    @Transactional
    public ProfileResponseDto updateProfile(User user, ProfileRequestDto requestDto) {
        Profile profile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));

        profile.updateProfile(requestDto.getIntro(), requestDto.getProfileImageUrl());
        profileRepository.save(profile);
        return new ProfileResponseDto(profile);
    }

    /**
     * 프로필 조회 로직
     *
     * @param userId 프로필을 조회할 사용자 ID
     * @return ProfileResponseDto 조회된 프로필 데이터
     * @throws CustomException 프로필이 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));
        return new ProfileResponseDto(profile);
    }
}