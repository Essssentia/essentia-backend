package com.sw.essentiabackend.domain.auth.service;

import com.sw.essentiabackend.common.exception.CustomException;
import com.sw.essentiabackend.common.exception.ErrorCode;
import com.sw.essentiabackend.domain.auth.dto.LoginRequestDto;
import com.sw.essentiabackend.domain.auth.dto.SignUpRequestDto;
import com.sw.essentiabackend.domain.auth.dto.SignUpResponseDto;
import com.sw.essentiabackend.domain.auth.dto.TokenResponseDto;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.auth.entity.UserRoleEnum;
import com.sw.essentiabackend.domain.auth.repository.UserRepository;
import com.sw.essentiabackend.domain.profile.entity.Profile;
import com.sw.essentiabackend.domain.profile.repository.ProfileRepository;
import com.sw.essentiabackend.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입 로직
     *
     * @param requestDto 회원가입 요청 데이터. 사용자 이름, 패스워드, 이름, 이메일, 관리자인지 여부를 포함함.
     * @return SignUpResponseDto 회원가입 처리 결과. 가입한 유저의 정보를 포함함.
     * @throws CustomException 사용자 이름이 중복된 경우
     * @throws IllegalArgumentException 이메일이 중복된 경우, 관리자 암호가 틀린 경우
     */
    @Transactional
    public SignUpResponseDto signup(SignUpRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }

        // 이메일 중복 확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        UserRoleEnum role = UserRoleEnum.USER;

        // 사용자 저장
        User user = new User(username, password, email, role);
        userRepository.save(user);

        // 기본 프로필 생성
        Profile defaultProfile = new Profile(
            user,
            "Welcome to your profile!", // 기본 한 줄 소개
            null // 기본 프로필 이미지 (없음)
        );
        profileRepository.save(defaultProfile);

        return SignUpResponseDto.builder().user(user).build();
    }

    /**
     * 로그인 로직
     *
     * @param requestDto 로그인 요청 데이터. 사용자 이름, 패스워드, 역할을 포함함.
     * @return TokenResponseDto 로그인 성공 시, 액세스 토큰과 리프레시 토큰을 반환함.
     * @throws CustomException 사용자 이름이 존재하지 않는 경우, 탈퇴한 사용자, 비밀번호 불일치, 역할 불일치
     */
    public TokenResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        String accessToken = jwtUtil.createAccessToken(user.getUsername(), user.getRole().toString());
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername(), user.getRole().toString());

        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

        return new TokenResponseDto(user.getId(), accessToken, refreshToken);
    }

    /**
     * 로그아웃 로직
     *
     * @param user 로그아웃할 사용자. 사용자 ID를 기반으로 리프레시 토큰을 제거함.
     */
    public void logout(User user) {
        refreshTokenService.removeRefreshToken(user.getId());
    }

    /**
     * 회원탈퇴 로직
     *
     * @param user 탈퇴할 사용자. 사용자 ID를 기반으로 리프레시 토큰을 제거하고, 사용자 상태를 탈퇴로 변경함.
     */
    public void resign(User user) {
        refreshTokenService.removeRefreshToken(user.getId());
        user.resign();
        userRepository.save(user);
    }
}