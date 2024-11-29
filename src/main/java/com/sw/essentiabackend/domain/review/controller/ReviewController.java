package com.sw.essentiabackend.domain.review.controller;

import com.sw.essentiabackend.common.ApiResponse;
import com.sw.essentiabackend.common.ResponseText;
import com.sw.essentiabackend.domain.review.dto.ReviewRequestDto;
import com.sw.essentiabackend.domain.review.dto.ReviewResponseDto;
import com.sw.essentiabackend.domain.review.service.ReviewService;
import com.sw.essentiabackend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 생성 API
     *
     * @param userDetails 인증된 사용자 정보
     * @param requestDto  리뷰 생성 요청 데이터
     * @return 생성된 리뷰 데이터
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody ReviewRequestDto requestDto) {

        ReviewResponseDto responseDto = reviewService.createReview(userDetails.getUser(),
            requestDto);

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.REVIEW_CREATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.CREATED.value()))
            .data(responseDto)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 리뷰 수정 API
     *
     * @param reviewId    수정할 리뷰 ID
     * @param userDetails 인증된 사용자 정보
     * @param requestDto  리뷰 수정 요청 데이터
     * @return 수정된 리뷰 데이터
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> updateReview(
        @PathVariable Long reviewId,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody ReviewRequestDto requestDto) {

        ReviewResponseDto responseDto = reviewService.updateReview(reviewId, userDetails.getUser(),
            requestDto);

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.REVIEW_UPDATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 특정 리뷰 조회 API
     *
     * @param reviewId 조회할 리뷰 ID
     * @return 조회된 리뷰 데이터
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> getReviewById(@PathVariable Long reviewId) {

        ReviewResponseDto responseDto = reviewService.getReviewById(reviewId);

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.REVIEW_RETRIEVE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 특정 사용자의 리뷰 조회 API
     *
     * @param userId 리뷰를 조회할 사용자 ID
     * @return 해당 사용자의 리뷰 목록
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse> getReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponseDto> responseDtos = reviewService.getReviewsByUserId(userId);

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.REVIEW_USER_LIST_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDtos)
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 전체 리뷰 조회 API
     *
     * @return 모든 리뷰 데이터
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllReviews() {

        List<ReviewResponseDto> responseDtos = reviewService.getAllReviews();

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.REVIEW_LIST_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDtos)
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 리뷰 삭제 API
     *
     * @param reviewId    삭제할 리뷰 ID
     * @param userDetails 인증된 사용자 정보
     * @return 삭제 성공 메시지
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(
        @PathVariable Long reviewId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        reviewService.deleteReview(reviewId, userDetails.getUser());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.REVIEW_DELETE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}