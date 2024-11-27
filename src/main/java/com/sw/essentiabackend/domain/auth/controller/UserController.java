package com.sw.essentiabackend.domain.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sw.essentiabackend.common.ApiResponse;
import com.sw.essentiabackend.common.ResponseText;
import com.sw.essentiabackend.config.oauth.service.GoogleService;
import com.sw.essentiabackend.domain.auth.dto.LoginRequestDto;
import com.sw.essentiabackend.domain.auth.dto.SignUpRequestDto;
import com.sw.essentiabackend.domain.auth.dto.SignUpResponseDto;
import com.sw.essentiabackend.domain.auth.dto.TokenResponseDto;
import com.sw.essentiabackend.domain.auth.service.RefreshTokenService;
import com.sw.essentiabackend.domain.auth.service.UserService;
import com.sw.essentiabackend.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final GoogleService googleService;

    /**
     * 회원가입 API
     *
     * @param requestDto 회원가입 요청 데이터
     * @return 회원가입 응답 데이터
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignUpRequestDto requestDto) {
        SignUpResponseDto responseDto = userService.signup(requestDto);
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.AUTH_SIGNUP_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.CREATED.value()))
            .data(responseDto)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인 API
     *
     * @param requestDto 로그인 요청 데이터
     * @return 발급된 토큰 응답 데이터
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequestDto requestDto) {
        TokenResponseDto responseDto = userService.login(requestDto);
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.AUTH_LOGIN_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 로그아웃 API
     *
     * @param userDetails 인증된 사용자 정보
     * @return 로그아웃 성공 응답
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.logout(userDetails.getUser());
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.AUTH_LOGOUT_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 회원탈퇴 API
     *
     * @param userDetails 인증된 사용자 정보
     * @return 회원탈퇴 성공 응답
     */
    @PostMapping("/resign")
    public ResponseEntity<ApiResponse> withdraw(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.resign(userDetails.getUser());
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.AUTH_RESIGN_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.NO_CONTENT.value()))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 토큰 재발급 API
     *
     * @param request HTTP 요청 정보
     * @return 재발급된 토큰 응답 데이터
     */
    @PostMapping("/token/reissue")
    public ResponseEntity<ApiResponse> refreshToken(HttpServletRequest request) {
        TokenResponseDto responseDto = refreshTokenService.reissueToken(request);
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.AUTH_TOKEN_REISSUE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 구글 로그인 API
     *
     * @param code 구글 서버로부터 전달받은 인가 코드
     * @return 리다이렉트 URL
     * @throws JsonProcessingException JSON 파싱 중 발생할 수 있는 예외
     */
    @GetMapping("/google/callback")
    public ResponseEntity<ApiResponse> googleLogin(@RequestParam String code)
        throws JsonProcessingException {
        TokenResponseDto responseDto = googleService.googleLogin(code);

        ApiResponse apiResponse = ApiResponse.builder()
            .msg(ResponseText.GOOGLE_LOGIN_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}