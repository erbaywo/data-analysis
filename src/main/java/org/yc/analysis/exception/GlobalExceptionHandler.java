package org.yc.analysis.exception;

import org.yc.analysis.common.response.ResponseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseResult<?> handleBusinessException(BusinessException e) {
        return ResponseResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult<?> handleException(Exception e) {
        return ResponseResult.error(500, "Internal Server Error: " + e.getMessage());
    }
}