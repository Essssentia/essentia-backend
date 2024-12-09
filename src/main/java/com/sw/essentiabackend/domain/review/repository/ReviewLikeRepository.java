package com.sw.essentiabackend.domain.review.repository;

import com.sw.essentiabackend.domain.review.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);
    void deleteByReviewIdAndUserId(Long reviewId, Long userId);
}
