package com.sw.essentiabackend.domain.profile.controller;

import com.sw.essentiabackend.common.ApiResponse;
import com.sw.essentiabackend.common.ResponseText;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.profile.dto.ProfileRequestDto;
import com.sw.essentiabackend.domain.profile.dto.ProfileResponseDto;
import com.sw.essentiabackend.domain.profile.service.ProfileService;
import com.sw.essentiabackend.security.UserDetailsImpl;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 프로필 생성 API
     *
     * @param userDetails  인증된 사용자 정보
     * @param requestDto   프로필 생성 요청 데이터
     * @param profileImage 프로필 이미지 파일
     * @return 생성된 프로필 데이터
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createProfile(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestPart ProfileRequestDto requestDto,
        @RequestPart(required = false) MultipartFile profileImage) throws IOException {

        User user = userDetails.getUser();
        ProfileResponseDto responseDto = profileService.createProfile(user, requestDto,
            profileImage);

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.PROFILE_CREATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.CREATED.value()))
            .data(responseDto)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 프로필 수정 API
     *
     * @param userDetails  인증된 사용자 정보
     * @param profileImage 프로필 이미지 파일
     * @return 수정된 프로필 데이터
     */
    @PutMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse> updateProfile(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestPart(value = "bio", required = false) String bio,
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws IOException {
        User user = userDetails.getUser();
        ProfileResponseDto responseDto = profileService.updateProfile(user, bio, profileImage);

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.PROFILE_UPDATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 프로필 조회 API
     *
     * @param userId 조회할 사용자 ID
     * @return 조회된 프로필 데이터
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getProfile(@PathVariable Long userId) {
        ProfileResponseDto responseDto = profileService.getProfile(userId);

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.PROFILE_FETCH_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
