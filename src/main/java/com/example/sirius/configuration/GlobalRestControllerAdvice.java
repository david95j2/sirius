package com.example.sirius.configuration;


import com.example.sirius.exception.AppException;
import com.example.sirius.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;

@Slf4j
@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NoHandlerFoundException ex) {
        log.error("NoHandlerFoundException: {}", ex.toString());
        return createResponseEntity("해당 URL을 찾을 수 없습니다.", ErrorCode.URL_NOT_FOUND.getStatus(), "text/plain");
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleAppException(AppException e) {
        log.error("AppException: {}", e.toString());
        return createErrorResponse(e.getErrorCode().getStatus(), e.getErrorCode(), e.getErrorCode().getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.toString());
        return createErrorResponse(ErrorCode.METHOD_NOT_ALLOWED.getStatus(), ErrorCode.METHOD_NOT_ALLOWED, e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException: {}", e.toString());
        return createErrorResponse(ErrorCode.METHOD_NOT_ALLOWED.getStatus(), ErrorCode.METHOD_NOT_ALLOWED, e.getMessage());
    }

    private ResponseEntity<?> createErrorResponse(HttpStatus status, ErrorCode errorCode, String errorMessage) {
        HashMap<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorCode", errorCode.getStatus());
        errorResponse.put("status", errorCode.getStatus().value());
        errorResponse.put("message", errorMessage);

        return ResponseEntity
                .status(status)
                .headers(createHeaders("application/json"))
                .body(errorResponse);
    }

    private ResponseEntity<String> createResponseEntity(String body, HttpStatus status, String contentType) {
        return new ResponseEntity<>(body, createHeaders(contentType), status);
    }

    private HttpHeaders createHeaders(String contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", contentType + ";charset=UTF-8");
        return headers;
    }
}
