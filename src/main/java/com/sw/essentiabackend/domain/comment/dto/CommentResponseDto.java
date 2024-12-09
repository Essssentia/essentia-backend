package com.sw.essentiabackend.domain.comment.dto;

import com.sw.essentiabackend.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private Long id;
    private String username;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.username = comment.getUser().getUsername();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getModifiedAt();
    }
}