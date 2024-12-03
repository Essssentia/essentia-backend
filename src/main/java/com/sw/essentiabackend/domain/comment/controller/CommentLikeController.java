package com.sw.essentiabackend.domain.comment.controller;

import com.sw.essentiabackend.common.ApiResponse;
import com.sw.essentiabackend.common.ResponseText;
import com.sw.essentiabackend.domain.comment.service.CommentLikeService;
import com.sw.essentiabackend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}/comments/{commentId}/likes")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    /**
     * 댓글 좋아요 추가 API
     *
     * @param reviewId    리뷰 ID
     * @param commentId   댓글 ID
     * @param userDetails 인증된 사용자 정보
     * @return 성공 메시지와 상태 코드
     */
    @PostMapping
    public ResponseEntity<ApiResponse> likeComment(
        @PathVariable Long reviewId,
        @PathVariable Long commentId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentLikeService.likeComment(reviewId, commentId, userDetails.getUser());
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.COMMENT_LIKE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.CREATED.value()))
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 댓글 좋아요 취소 API
     *
     * @param reviewId    리뷰 ID
     * @param commentId   댓글 ID
     * @param userDetails 인증된 사용자 정보
     * @return 성공 메시지와 상태 코드
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse> unlikeComment(
        @PathVariable Long reviewId,
        @PathVariable Long commentId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentLikeService.unlikeComment(reviewId, commentId, userDetails.getUser());
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.COMMENT_UNLIKE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}