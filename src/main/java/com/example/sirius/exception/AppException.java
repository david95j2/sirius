package com.example.sirius.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException{
    private ErrorCode errorCode;
    private String message;

    // fail
    public AppException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    public AppException(ErrorCode errorCode, String new_message) {
        this.errorCode = errorCode;
        this.message = new_message;
    }
}