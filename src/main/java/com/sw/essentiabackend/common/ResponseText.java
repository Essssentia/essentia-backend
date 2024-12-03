package com.sw.essentiabackend.common;

import lombok.Getter;

@Getter
public enum ResponseText {

    // admin
    ADMIN_SIGNUP_SUCCESS("관리자 회원가입 성공"),
    ADMIN_LOGIN_SUCCESS("관리자 로그인 성공"),
    ADMIN_USER_DELETE_SUCCESS("유저 회원탈퇴 처리 성공"),
    ADMIN_APPROVE_SUCCESS("관리자 승인 성공"),
    ADMIN_REJECT_SUCCESS("관리자 승인 거부 성공"),
    PENDING_APPROVAL_LIST_SUCCESS("승인 대기 중인 유저 목록 조회 성공"),

    // auth
    AUTH_SIGNUP_SUCCESS("회원가입 성공"),
    AUTH_LOGIN_SUCCESS("로그인 성공"),
    AUTH_LOGOUT_SUCCESS("로그아웃 성공"),
    AUTH_RESIGN_SUCCESS("회원탈퇴 성공"),
    AUTH_TOKEN_REISSUE_SUCCESS("토큰 재발급 성공"),
    GOOGLE_LOGIN_SUCCESS("구글 로그인 성공"),

    // profile
    PROFILE_CREATE_SUCCESS("프로필 생성 성공"),
    PROFILE_UPDATE_SUCCESS("프로필 업데이트 성공"),
    PROFILE_DELETE_SUCCESS("프로필 삭제 성공"),
    PROFILE_FETCH_SUCCESS("프로필 조회 성공"),

    // review
    REVIEW_CREATE_SUCCESS("리뷰 작성에 성공했습니다."),
    REVIEW_UPDATE_SUCCESS("리뷰 수정에 성공했습니다."),
    REVIEW_RETRIEVE_SUCCESS("리뷰 조회에 성공했습니다."),
    REVIEW_USER_LIST_SUCCESS("특정 사용자의 리뷰 조회에 성공했습니다."),
    REVIEW_LIST_SUCCESS("리뷰 목록 조회에 성공했습니다."),
    REVIEW_DELETE_SUCCESS("리뷰 삭제에 성공했습니다."),

    // comment
    COMMENT_CREATE_SUCCESS("댓글 작성에 성공했습니다."),
    COMMENT_LIST_SUCCESS("댓글 목록 조회에 성공했습니다."),
    COMMENT_ALL_LIST_SUCCESS("모든 댓글 조회에 성공했습니다."),
    COMMENT_DELETE_SUCCESS("댓글 삭제에 성공했습니다."),
    COMMENT_UPDATE_SUCCESS("댓글 수정에 성공했습니다."),

    // like
    REVIEW_LIKE_SUCCESS("리뷰 좋아요를 성공적으로 처리했습니다."),
    REVIEW_UNLIKE_SUCCESS("리뷰 좋아요를 취소했습니다."),
    COMMENT_LIKE_SUCCESS("댓글 좋아요를 성공적으로 처리했습니다."),
    COMMENT_UNLIKE_SUCCESS("댓글 좋아요를 취소했습니다."),

    REPLY_CREATE_SUCCESS("대댓글 작성에 성공했습니다."),
    REPLY_RETRIEVE_SUCCESS("대댓글 조회에 성공했습니다."),
    REPLY_ALL_LIST_SUCCESS("모든 댓글 조회에 성공했습니다."),
    REPLY_UPDATE_SUCCESS("대댓글 수정에 성공했습니다."),
    REPLY_DELETE_SUCCESS("대댓글 삭제에 성공했습니다.");

    private String msg;

    ResponseText(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public String format(Object... args) {
        return String.format(msg, args);
    }
}