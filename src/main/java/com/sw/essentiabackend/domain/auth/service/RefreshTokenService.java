package com.sw.essentiabackend.domain.auth.service;

import com.sw.essentiabackend.common.exception.CustomException;
import com.sw.essentiabackend.common.exception.ErrorCode;
import com.sw.essentiabackend.domain.auth.dto.TokenResponseDto;
import com.sw.essentiabackend.domain.auth.entity.RefreshToken;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.auth.repository.RefreshTokenRepository;
import com.sw.essentiabackend.domain.auth.repository.UserRepository;
import com.sw.essentiabackend.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * 토큰 재발급
     *
     * @param request
     * @return
     */
    @Transactional
    public TokenResponseDto reissueToken(HttpServletRequest request) {
        String refreshToken = jwtUtil.extractRefreshToken(request);

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));

        User user = userRepository.findByUsername(storedToken.getUser().getUsername())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtUtil.createAccessToken(user.getUsername(),
            user.getRole().toString());
        String newRefreshToken = jwtUtil.createRefreshToken(user.getUsername(),
            String.valueOf(user.getRole()));

        storedToken.updateToken(newRefreshToken);

        return new TokenResponseDto(user.getId(), newAccessToken, newRefreshToken);
    }

    /**
     * 토큰 저장
     *
     * @param userId       사용자 ID
     * @param refreshToken 리프레시 토큰
     */
    @Transactional
    public void saveRefreshToken(Long userId, String refreshToken) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        RefreshToken token = refreshTokenRepository.findByUserId(userId)
            .orElseGet(() -> new RefreshToken(refreshToken, user));

        token.updateToken(refreshToken);
        refreshTokenRepository.save(token);
    }

    /**
     * 토큰 삭제
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void removeRefreshToken(Long userId) {
        RefreshToken token = refreshTokenRepository.findByUserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));

        refreshTokenRepository.delete(token);
    }
}