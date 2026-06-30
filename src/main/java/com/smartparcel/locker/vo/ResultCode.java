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
    RECIPIENT_NOT_FOUND(40410, "recipient not found"),
    STATION_NOT_FOUND(40420, "locker station not found"),
    PARCEL_NOT_FOUND(40430, "parcel not found"),
    GROUP_NOT_FOUND(40440, "group not found"),
    USER_NOT_FOUND(40450, "user not found"),
    INVALID_COLLECTION_CODE(40460, "invalid or expired collection code"),
    EMAIL_EXISTS(40900, "email already registered"),
    DUPLICATE_MEMBER(40920, "member already in group"),
    LOCKER_NOT_OPEN(40930, "locker is not open"),
    NO_LOCKER_AVAILABLE(42200, "no suitable locker available"),
    INTERNAL_ERROR(50000, "internal server error");
    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
