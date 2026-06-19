package com.smartparcel.locker.exception;

import com.smartparcel.locker.vo.ApiResponse;
import com.smartparcel.locker.vo.ResultCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Maps application exceptions to the unified {@link ApiResponse} envelope. The
 * HTTP status stays 200; the failure is conveyed via {@code code}/{@code msg}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ApiResponse<Void> handleEmailExists(EmailAlreadyExistsException ex) {
        return ApiResponse.error(ResultCode.EMAIL_EXISTS, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ApiResponse<Void> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ApiResponse.error(ResultCode.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ApiResponse.error(ResultCode.PARAM_ERROR, details);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleUnexpected(Exception ex) {
        return ApiResponse.error(ResultCode.INTERNAL_ERROR);
    }
}
