package com.sw.essentiabackend.domain.comment.repository;

import com.sw.essentiabackend.domain.comment.entity.Comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByReviewIdOrderByCreatedAtDesc(Long reviewId);
}