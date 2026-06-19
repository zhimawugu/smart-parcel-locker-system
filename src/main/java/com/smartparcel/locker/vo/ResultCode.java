package com.smartparcel.locker.vo;

import lombok.Getter;

/**
 * Business status codes carried in {@link ApiResponse#code()}. These are
 * independent of the HTTP status, which always stays 200.
 */
@Getter
public enum ResultCode {

    SUCCESS(0, "success"),
    PARAM_ERROR(40000, "parameter validation failed"),
    UNAUTHORIZED(40100, "invalid email or password"),
    EMAIL_EXISTS(40900, "email already registered"),
    INTERNAL_ERROR(50000, "internal server error");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
