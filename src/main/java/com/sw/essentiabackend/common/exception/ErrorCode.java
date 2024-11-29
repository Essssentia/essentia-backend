package com.sw.essentiabackend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "실패했습니다."),

    // Token
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 서명입니다."),
    TOKEN_EXPIRATION(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다. 재로그인 해주세요."),
    NOT_SUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
    FALSE_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 JWT 토큰입니다."),
    HEADER_NOT_FOUND_AUTH(HttpStatus.BAD_REQUEST, "권한 헤더가 잘못되었거나 누락되었습니다."),
    TOKEN_VALIDATE(HttpStatus.BAD_REQUEST, "Invalid JWT token."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
    UNMATCHED_TOKEN(HttpStatus.BAD_REQUEST, "일치하지 않는 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "등록되지 않은 토큰입니다."),

    // User
    INVALID_USERNAME(HttpStatus.BAD_REQUEST,
        "아이디는 최소 6자 이상, 20자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 합니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,
        "최소 8자 이상, 15자 이하이며 알파벳 대소문자(az, AZ), 숫자(0~9),특수문자로 구성되어야 합니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "등록되지 않은 사용자입니다."),
    DUPLICATE_USER(HttpStatus.BAD_REQUEST, "이미 등록된 사용자 입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "중복된 Email 입니다."),
    BAD_MANAGER_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 암호입니다."),
    HEADER_NOT_FOUND_REFRESH(HttpStatus.BAD_REQUEST, "헤더에 토큰이 존재하지 않습니다."),
    USER_NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다. 로그인해주세요."),

    // Admin
    NOT_AUTHORIZED_ADMIN(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다."),

    // Role
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "역할을 찾을 수 없습니다."),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 게시글에 대한 접근 권한이 없습니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 댓글에 대한 접근 권한이 없습니다."),
    DUPLICATE_RECOMMENDATION(HttpStatus.BAD_REQUEST, "이미 추천한 게시글입니다."),
    CANNOT_RECOMMEND_OWN_POST(HttpStatus.BAD_REQUEST, "자신의 게시글은 추천할 수 없습니다."),
    INVALID_POST_DATA(HttpStatus.BAD_REQUEST, "유효하지 않은 게시글 데이터입니다."),
    INVALID_COMMENT_DATA(HttpStatus.BAD_REQUEST, "유효하지 않은 댓글 데이터입니다."),

    // profile
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자의 프로필을 찾을 수 없습니다."),
    DUPLICATE_PROFILE(HttpStatus.BAD_REQUEST, "이미 프로필이 존재합니다."),

    // review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    REVIEW_ACCESS_DENIED(HttpStatus.FORBIDDEN, "리뷰에 대한 접근 권한이 없습니다."),

    // google
    GOOGLE_COMMUNICATION_ERROR(HttpStatus.BAD_REQUEST, "구글 API와의 통신에 실패했습니다."),
    GOOGLE_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "구글 액세스 토큰을 가져오는 데 실패했습니다."),
    GOOGLE_USER_INFO_ERROR(HttpStatus.BAD_REQUEST, "구글 사용자 정보를 가져오는 데 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String msg;
}