package com.sw.essentiabackend.domain.review.service;

import com.sw.essentiabackend.common.exception.CustomException;
import com.sw.essentiabackend.common.exception.ErrorCode;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.review.dto.ReviewRequestDto;
import com.sw.essentiabackend.domain.review.dto.ReviewResponseDto;
import com.sw.essentiabackend.domain.review.entity.Review;
import com.sw.essentiabackend.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewResponseDto createReview(User user, ReviewRequestDto requestDto) {
        Review review = new Review(
            user,
            requestDto.getTitle(),
            requestDto.getFragranceType(),
            requestDto.getContent(),
            requestDto.getTags(),
            requestDto.getReviewImageUrl()
        );
        reviewRepository.save(review);
        return new ReviewResponseDto(review);
    }

    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, User user, ReviewRequestDto requestDto) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.REVIEW_ACCESS_DENIED);
        }

        review.update(
            requestDto.getTitle(),
            requestDto.getFragranceType(),
            requestDto.getContent(),
            requestDto.getTags(),
            requestDto.getReviewImageUrl()
        );
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