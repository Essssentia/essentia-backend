package com.sw.essentiabackend.domain.profile.service;

import com.sw.essentiabackend.common.exception.CustomException;
import com.sw.essentiabackend.common.exception.ErrorCode;
import com.sw.essentiabackend.config.aws.S3Uploader;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.profile.dto.ProfileRequestDto;
import com.sw.essentiabackend.domain.profile.dto.ProfileResponseDto;
import com.sw.essentiabackend.domain.profile.entity.Profile;
import com.sw.essentiabackend.domain.profile.repository.ProfileRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final S3Uploader s3Uploader;

    /**
     * 프로필 생성 로직
     */
    @Transactional
    public ProfileResponseDto createProfile(User user, ProfileRequestDto requestDto,
        MultipartFile profileImage)
        throws IOException {
        if (profileRepository.findByUserId(user.getId()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_PROFILE);
        }

        // S3에 이미지 업로드
        String imageUrl = profileImage != null ? s3Uploader.upload(profileImage, "profiles") : null;

        Profile profile = new Profile(user, requestDto.getBio(), imageUrl);
        profileRepository.save(profile);
        return new ProfileResponseDto(profile);
    }

    /**
     * 프로필 수정 로직
     */
    @Transactional
    public ProfileResponseDto updateProfile(User user, String bio, MultipartFile profileImage) throws IOException {
        Profile profile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));

        // 프로필 업데이트 (이미지 없이 bio만 수정 가능)
        if (profileImage != null) {
            if (profile.getProfileImageUrl() != null) {
                s3Uploader.delete(profile.getProfileImageUrl());
            }
            String imageUrl = s3Uploader.upload(profileImage, "profiles");
            profile.updateProfile(bio, imageUrl);
        } else {
            profile.updateProfile(bio, profile.getProfileImageUrl());
        }

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