package org.yc.analysis.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 统一业务异常，无自定义业务状态码
     */
    public BusinessException(String message) {
        super(message);
        this.code = 50000;
    }
}