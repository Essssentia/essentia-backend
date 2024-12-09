package com.sw.essentiabackend.domain.review.repository;

import com.sw.essentiabackend.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByUserId(Long userId); // 특정 사용자의 리뷰 조회
}