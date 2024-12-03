package com.sw.essentiabackend.domain.comment.service;

import com.sw.essentiabackend.common.exception.CustomException;
import com.sw.essentiabackend.common.exception.ErrorCode;
import com.sw.essentiabackend.domain.auth.entity.User;
import com.sw.essentiabackend.domain.comment.entity.Comment;
import com.sw.essentiabackend.domain.comment.entity.CommentLike;
import com.sw.essentiabackend.domain.comment.repository.CommentLikeRepository;
import com.sw.essentiabackend.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public void likeComment(Long reviewId, Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, user.getId())) {
            throw new CustomException(ErrorCode.COMMENT_ALREADY_LIKED);
        }
        commentLikeRepository.save(new CommentLike(comment, user));
    }

    @Transactional
    public void unlikeComment(Long reviewId, Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!commentLikeRepository.existsByCommentIdAndUserId(commentId, user.getId())) {
            throw new CustomException(ErrorCode.COMMENT_NOT_LIKED);
        }
        commentLikeRepository.deleteByCommentIdAndUserId(commentId, user.getId());
    }
}