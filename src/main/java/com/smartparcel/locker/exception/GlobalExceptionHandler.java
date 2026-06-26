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
    @ExceptionHandler(RecipientNotFoundException.class)
    public ApiResponse<Void> handleRecipientNotFound(RecipientNotFoundException ex) {
        return ApiResponse.error(ResultCode.RECIPIENT_NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(StationNotFoundException.class)
    public ApiResponse<Void> handleStationNotFound(StationNotFoundException ex) {
        return ApiResponse.error(ResultCode.STATION_NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(NoLockerAvailableException.class)
    public ApiResponse<Void> handleNoLockerAvailable(NoLockerAvailableException ex) {
        return ApiResponse.error(ResultCode.NO_LOCKER_AVAILABLE, ex.getMessage());
    }
    @ExceptionHandler(ParcelNotFoundException.class)
    public ApiResponse<Void> handleParcelNotFound(ParcelNotFoundException ex) {
        return ApiResponse.error(ResultCode.PARCEL_NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(LockerNotOpenException.class)
    public ApiResponse<Void> handleLockerNotOpen(LockerNotOpenException ex) {
        return ApiResponse.error(ResultCode.LOCKER_NOT_OPEN, ex.getMessage());
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
