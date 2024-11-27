package com.sw.essentiabackend.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

// API 응답을 표준화하여 처리하기 위한 클래스
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Json 직렬화 시 값이 null인 필드는 응답에서 제외 설정.
public class ApiResponse<T> {

    private String msg; // 응답 메시지
    private String statuscode; // 상태 코드
    private T data; // 실제 데이터

    public ApiResponse(String msg, String statuscode, T data) {
        this.msg = msg;
        this.statuscode = statuscode;
        this.data = data;
    }
}