package com.sw.essentiabackend.common.exception;

import lombok.Getter;

@Getter
public class ExceptionResponse {

    private int success;
    private String msg;

    public ExceptionResponse(int success, String msg) {
        this.success = success;
        this.msg = msg;
    }
}