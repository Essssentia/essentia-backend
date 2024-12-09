package com.sw.essentiabackend.config.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.essentiabackend.common.exception.CustomException;
import com.sw.essentiabackend.common.exception.ErrorCode;
import com.sw.essentiabackend.config.oauth.dto.GoogleUserInfoDto;
import com.sw.essentiabackend.domain.auth.dto.TokenResponseDto;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.auth.entity.UserRoleEnum;
import com.sw.essentiabackend.domain.auth.repository.UserRepository;
import com.sw.essentiabackend.domain.auth.service.RefreshTokenService;
import com.sw.essentiabackend.domain.profile.entity.Profile;
import com.sw.essentiabackend.domain.profile.repository.ProfileRepository;
import com.sw.essentiabackend.jwt.JwtUtil;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j(topic = "GOOGLE Login")
@Service
@RequiredArgsConstructor
public class GoogleService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectURI;

    /**
     * 구글 로그인을 처리하는 메서드.
     *
     * @param code 구글 서버로부터 받은 인가 코드
     * @return 생성된 JWT 토큰을 반환
     * @throws JsonProcessingException JSON 파싱 중 발생할 수 있는 예외
     */
    public TokenResponseDto googleLogin(String code) throws JsonProcessingException {
        String accessToken = getToken(code);
        GoogleUserInfoDto googleUserInfo = getGoogleUserInfo(accessToken);
        User googleUser = registerGoogleUserIfNeeded(googleUserInfo);

        // Step 4: Create a profile for the user if not already created
        Optional<Profile> existingProfile = profileRepository.findByUserId(googleUser.getId());
        if (existingProfile.isEmpty()) {
            Profile profile = new Profile(googleUser, "Welcome to your profile!",
                null);
            profileRepository.save(profile);
        }

        // Step 5: Generate JWT tokens (access and refresh tokens)
        String newAccessToken = jwtUtil.createAccessToken(googleUser.getUsername(),
            googleUser.getRole().toString());
        String newRefreshToken = jwtUtil.createRefreshToken(googleUser.getUsername(),
            googleUser.getRole().toString());

        // Step 6: Save the refresh token
        refreshTokenService.saveRefreshToken(googleUser.getId(), newRefreshToken);

        // Step 7: Return the tokens
        return new TokenResponseDto(googleUser.getId(), newAccessToken, newRefreshToken);
    }

    /**
     * 인가 코드를 사용하여 구글 서버로부터 액세스 토큰을 얻는 메서드.
     *
     * @param code 구글 서버로부터 받은 인가 코드
     * @return 액세스 토큰
     * @throws JsonProcessingException JSON 파싱 중 발생할 수 있는 예외
     */
    private String getToken(String code) throws JsonProcessingException {
        log.info("인가코드 : " + code);

        // 구글 토큰 요청 URL 생성
        URI uri = UriComponentsBuilder
            .fromUriString("https://oauth2.googleapis.com") // 구글 인증 서버 주소
            .path("/token") // 토큰 요청 경로
            .encode()
            .build()
            .toUri();

        // HTTP 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 바디 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectURI);
        body.add("code", code);

        // 토큰 요청
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
            .post(uri)
            .headers(headers)
            .body(body);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(requestEntity, String.class);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GOOGLE_COMMUNICATION_ERROR);
        }

        // JSON 응답에서 액세스 토큰 추출
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        if (jsonNode.has("access_token")) {
            return jsonNode.get("access_token").asText();
        } else {
            throw new CustomException(ErrorCode.GOOGLE_TOKEN_ERROR);
        }
    }

    /**
     * 액세스 토큰을 사용하여 구글 사용자 정보를 조회하는 메서드.
     *
     * @param accessToken 구글 서버로부터 받은 액세스 토큰
     * @return GoogleUserInfoDto 구글 사용자 정보 DTO
     * @throws JsonProcessingException JSON 파싱 중 발생할 수 있는 예외
     */
    private GoogleUserInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
        log.info("accessToken : " + accessToken);

        // 구글 사용자 정보 요청 URL 생성
        URI uri = UriComponentsBuilder
            .fromUriString("https://www.googleapis.com") // 구글 API 서버 주소
            .path("/oauth2/v3/userinfo") // 사용자 정보 요청 경로
            .encode()
            .build()
            .toUri();

        // HTTP 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 사용자 정보 요청
        RequestEntity<Void> requestEntity = RequestEntity
            .get(uri)
            .headers(headers)
            .build();

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(requestEntity, String.class);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GOOGLE_COMMUNICATION_ERROR);
        }

        // JSON 응답에서 사용자 정보 추출
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        // 각 필드가 존재하는지 확인
        JsonNode idNode = jsonNode.get("sub");
        JsonNode nameNode = jsonNode.get("name");
        JsonNode emailNode = jsonNode.get("email");

        if (idNode == null || nameNode == null || emailNode == null) {
            log.error("Google 응답에 필수 필드가 없습니다: " + jsonNode.toString());
            throw new CustomException(ErrorCode.GOOGLE_USER_INFO_ERROR);
        }

        Long id = idNode.asLong();
        String name = nameNode.asText();
        String email = emailNode.asText();

        log.info("구글 사용자 정보: " + id + ", " + name + ", " + email);

        return new GoogleUserInfoDto(id, name, email);
    }

    /**
     * 구글 사용자를 저장하거나 이미 존재하는지 확인하는 메서드.
     *
     * @param googleUserInfo 구글 사용자 정보 DTO
     * @return 등록된 사용자 정보(User)
     */
    private User registerGoogleUserIfNeeded(GoogleUserInfoDto googleUserInfo) {

        Long googleId = googleUserInfo.getId();
        User googleUser = userRepository.findByGoogleId(googleId).orElse(null);

        // 사용자가 없으면 새로 등록
        if (googleUser == null) {
            String googleEmail = googleUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(googleEmail).orElse(null);

            if (sameEmailUser != null) {
                googleUser = sameEmailUser.googleIdUpdate(googleId);
            } else {
                // 새로운 사용자 생성
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                googleUser = new User(
                    googleUserInfo.getName(),
                    encodedPassword,
                    googleEmail,
                    UserRoleEnum.USER,
                    googleId
                );
            }
            userRepository.save(googleUser);
        }
        return googleUser;
    }
}