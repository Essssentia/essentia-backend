package com.sw.essentiabackend.domain.comment.service;

import com.sw.essentiabackend.common.exception.CustomException;
import com.sw.essentiabackend.common.exception.ErrorCode;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.comment.entity.Comment;
import com.sw.essentiabackend.domain.comment.repository.CommentRepository;
import com.sw.essentiabackend.domain.review.entity.Review;
import com.sw.essentiabackend.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public Comment createComment(Long reviewId, User user, String content, Long parentId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        Comment parent = null;
        if (parentId != null) {
            parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTFOUND_REVIEW_COMMENT));
        }

        Comment comment = new Comment(user, review, content);
        if (parent != null) {
            parent.getReplies().add(comment);
        }

        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByReview(Long reviewId) {
        return commentRepository.findByReviewIdOrderByCreatedAtDesc(reviewId);
    }

    @Transactional
    public Comment updateComment(Long commentId, User user, String content) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOTFOUND_REVIEW_COMMENT));

        if (!comment.getUser().equals(user)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_AUTHORIZED);
        }

        comment.updateContent(content);
        return comment;
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOTFOUND_REVIEW_COMMENT));

        if (!comment.getUser().equals(user)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_AUTHORIZED);
        }

        commentRepository.delete(comment);
    }
}
