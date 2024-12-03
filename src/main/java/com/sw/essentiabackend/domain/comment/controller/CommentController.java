package com.sw.essentiabackend.domain.comment.controller;

import com.sw.essentiabackend.common.ApiResponse;
import com.sw.essentiabackend.common.ResponseText;
import com.sw.essentiabackend.domain.comment.dto.CommentRequestDto;
import com.sw.essentiabackend.domain.comment.dto.CommentResponseDto;
import com.sw.essentiabackend.domain.comment.service.CommentService;
import com.sw.essentiabackend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성 API
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createComment(
        @PathVariable Long reviewId,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody CommentRequestDto requestDto) {

        CommentResponseDto responseDto = new CommentResponseDto(
            commentService.createComment(reviewId, userDetails.getUser(), requestDto.getContent(),
                null)
        );

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.COMMENT_CREATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.CREATED.value()))
            .data(responseDto)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 대댓글 생성 API
     */
    @PostMapping("/{parentId}/replies")
    public ResponseEntity<ApiResponse> createReply(
        @PathVariable Long reviewId,
        @PathVariable Long parentId,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody CommentRequestDto requestDto) {

        CommentResponseDto responseDto = new CommentResponseDto(
            commentService.createComment(reviewId, userDetails.getUser(), requestDto.getContent(),
                parentId)
        );

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.REPLY_CREATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.CREATED.value()))
            .data(responseDto)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 특정 리뷰의 댓글 및 대댓글 조회 API
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getCommentsByReview(@PathVariable Long reviewId) {
        List<CommentResponseDto> responseDtos = commentService.getCommentsByReview(reviewId)
            .stream()
            .map(CommentResponseDto::new)
            .collect(Collectors.toList());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.COMMENT_LIST_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDtos)
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 댓글 수정 API
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse> updateComment(
        @PathVariable Long commentId,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody CommentRequestDto requestDto) {

        CommentResponseDto responseDto = new CommentResponseDto(
            commentService.updateComment(commentId, userDetails.getUser(), requestDto.getContent())
        );

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.COMMENT_UPDATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 댓글 삭제 API
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(
        @PathVariable Long commentId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentService.deleteComment(commentId, userDetails.getUser());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.COMMENT_DELETE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 대댓글 수정 API
     */
    @PutMapping("/{parentId}/replies/{replyId}")
    public ResponseEntity<ApiResponse> updateComment(
        @PathVariable Long parentId,
        @PathVariable Long replyId,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody CommentRequestDto requestDto) {

        CommentResponseDto responseDto = new CommentResponseDto(
            commentService.updateComment(replyId, userDetails.getUser(), requestDto.getContent())
        );

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.REPLY_UPDATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 대댓글 삭제 API
     */
    @DeleteMapping("/{parentId}/replies/{replyId}")
    public ResponseEntity<ApiResponse> deleteComment(
        @PathVariable Long parentId,
        @PathVariable Long replyId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentService.deleteComment(replyId, userDetails.getUser());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.REPLY_DELETE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}