package com.sw.essentiabackend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// 전역적으로 예외를 처리하기 위한 클래스
@ControllerAdvice
public class GlobalExceptionHandler {

    //사용자 오류
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> UnauthorizedExceptionHandler(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    // 커스텀 예외 발생할 때 호출
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(CustomException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(
                new ExceptionResponse(ex.getErrorCode().getHttpStatus().value(), ex.getMessage()));
    }

    // 유효성 검증 오류
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(
        MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMessage =
            fieldError != null ? fieldError.getDefaultMessage() : "Validation Error";
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ExceptionResponse(HttpStatus.FORBIDDEN.value(), errorMessage));
    }
}