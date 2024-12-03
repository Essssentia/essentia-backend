package com.sw.essentiabackend.domain.review.service;

import com.sw.essentiabackend.common.exception.CustomException;
import com.sw.essentiabackend.common.exception.ErrorCode;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.review.entity.Review;
import com.sw.essentiabackend.domain.review.entity.ReviewLike;
import com.sw.essentiabackend.domain.review.repository.ReviewLikeRepository;
import com.sw.essentiabackend.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional
    public void likeReview(Long reviewId, User user) {
        if (reviewLikeRepository.existsByReviewIdAndUserId(reviewId, user.getId())) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_LIKED);
        }
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        reviewLikeRepository.save(new ReviewLike(review, user));
    }

    @Transactional
    public void unlikeReview(Long reviewId, User user) {
        if (!reviewLikeRepository.existsByReviewIdAndUserId(reviewId, user.getId())) {
            throw new CustomException(ErrorCode.REVIEW_NOT_LIKED);
        }
        reviewLikeRepository.deleteByReviewIdAndUserId(reviewId, user.getId());
    }
}
