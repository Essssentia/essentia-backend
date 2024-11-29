package com.sw.essentiabackend.jwt;

import com.sw.essentiabackend.common.exception.CustomException;
import com.sw.essentiabackend.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "RefreshToken";
    public static final String BEARER_PREFIX = "Bearer ";

    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";

    // 토큰 만료시간
    @Value("${jwt.time.access}")
    private Long ACCESS_TOKEN_TIME;
    @Value("${jwt.time.refresh}")
    private Long REFRESH_TOKEN_TIME;

    // 시크릿 키
    @Value("${jwt.secret.key}")
    private String secretKey;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // Access Token 생성
    // 사용자 이름과 역할을 기반으로 REFRESH_TOKEN_TIME 동안 유효한 새로운 리프레시 토큰을 생성 메서드
    public String createAccessToken(String username, String role) {
        Date now = new Date();
        return Jwts.builder()
            .setSubject(username)
            .claim(AUTHORIZATION_KEY, role)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_TIME))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    // Refresh Token 생성
    // 이름 & 역할을 기반으로 REFRESH_TOKEN_TIME 동안 유효한 새로운 리프레시 토큰 생성 메서드
    public String createRefreshToken(String username, String role) {
        Date now = new Date();
        return Jwts.builder()
            .setSubject(username)
            .claim(AUTHORIZATION_KEY, role)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_TIME))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    // JWT 토큰의 Bearer 접두사 제거 후 반환
    public String substringToken(String token) {
        return token.substring(BEARER_PREFIX.length());
    }

    // header 에서 JWT 가져오기
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return substringToken(bearerToken);
        }
        throw new CustomException(ErrorCode.HEADER_NOT_FOUND_AUTH);
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    // HTTP 요청 헤더에서 Refresh Token 추출 메서드
    public String extractRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(REFRESH_TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        throw new CustomException(ErrorCode.HEADER_NOT_FOUND_AUTH);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.NOT_SUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.FALSE_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRATION);
        }
    }

    public String getTokenFromRequest(HttpServletRequest req) {
        return req.getHeader(AUTHORIZATION_HEADER);
    }
}