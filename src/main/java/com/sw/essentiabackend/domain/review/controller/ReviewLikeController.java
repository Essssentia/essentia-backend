package com.sw.essentiabackend.domain.review.controller;

import com.sw.essentiabackend.common.ApiResponse;
import com.sw.essentiabackend.common.ResponseText;
import com.sw.essentiabackend.domain.review.service.ReviewLikeService;
import com.sw.essentiabackend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}/likes")
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    /**
     * 리뷰 좋아요 API
     *
     * @param reviewId 리뷰 ID
     * @param userDetails 사용자 정보
     * @return 좋아요 성공 메시지
     */
    @PostMapping
    public ResponseEntity<ApiResponse> likeReview(
        @PathVariable Long reviewId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        reviewLikeService.likeReview(reviewId, userDetails.getUser());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.REVIEW_LIKE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.CREATED.value()))
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 리뷰 좋아요 취소 API
     *
     * @param reviewId 리뷰 ID
     * @param userDetails 사용자 정보
     * @return 좋아요 취소 성공 메시지
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse> unlikeReview(
        @PathVariable Long reviewId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        reviewLikeService.unlikeReview(reviewId, userDetails.getUser());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.REVIEW_UNLIKE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
