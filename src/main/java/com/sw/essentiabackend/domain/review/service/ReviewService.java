package com.sw.essentiabackend.domain.review.service;

import com.sw.essentiabackend.common.exception.CustomException;
import com.sw.essentiabackend.common.exception.ErrorCode;
import com.sw.essentiabackend.config.aws.S3Uploader;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.review.dto.ReviewRequestDto;
import com.sw.essentiabackend.domain.review.dto.ReviewResponseDto;
import com.sw.essentiabackend.domain.review.entity.Review;
import com.sw.essentiabackend.domain.review.repository.ReviewRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Uploader s3Uploader;

    /**
     * 리뷰 생성 로직
     */
    @Transactional
    public ReviewResponseDto createReview(User user, ReviewRequestDto requestDto, MultipartFile reviewImage)
        throws IOException {
        // S3에 이미지 업로드
        String imageUrl = reviewImage != null ? s3Uploader.upload(reviewImage, "reviews") : null;

        Review review = new Review(
            user,
            requestDto.getTitle(),
            requestDto.getFragranceType(),
            requestDto.getContent(),
            requestDto.getTags(),
            imageUrl
        );
        reviewRepository.save(review);
        return new ReviewResponseDto(review);
    }

    /**
     * 리뷰 수정 로직
     */
    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, User user, ReviewRequestDto requestDto, MultipartFile reviewImage) throws IOException {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.REVIEW_ACCESS_DENIED);
        }

        // 기존 이미지 삭제 및 새 이미지 업로드
        if (reviewImage != null) {
            if (review.getReviewImageUrl() != null) {
                s3Uploader.delete(review.getReviewImageUrl());
            }
            String imageUrl = s3Uploader.upload(reviewImage, "reviews");
            review.update(
                requestDto.getTitle(),
                requestDto.getFragranceType(),
                requestDto.getContent(),
                requestDto.getTags(),
                imageUrl
            );
        } else {
            review.update(
                requestDto.getTitle(),
                requestDto.getFragranceType(),
                requestDto.getContent(),
                requestDto.getTags(),
                review.getReviewImageUrl()
            );
        }

        return new ReviewResponseDto(review);
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        return new ReviewResponseDto(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findAllByUserId(userId);
        if (reviews.isEmpty()) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND); // 리뷰가 없을 경우 예외 발생
        }
        return reviews.stream()
            .map(ReviewResponseDto::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        return reviewRepository.findAll().stream()
            .map(ReviewResponseDto::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.REVIEW_ACCESS_DENIED);
        }

        reviewRepository.delete(review);
    }
}