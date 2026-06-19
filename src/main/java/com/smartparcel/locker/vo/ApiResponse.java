package com.smartparcel.locker.vo;

/**
 * Unified API envelope: {@code {code, msg, data}}. The HTTP status is always
 * 200; callers inspect {@code code} (0 = success) to determine the outcome.
 */
public record ApiResponse<T>(int code, String msg, T data) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    public static <T> ApiResponse<T> error(ResultCode resultCode) {
        return new ApiResponse<>(resultCode.getCode(), resultCode.getMsg(), null);
    }

    public static <T> ApiResponse<T> error(ResultCode resultCode, String msg) {
        return new ApiResponse<>(resultCode.getCode(), msg, null);
    }
}
