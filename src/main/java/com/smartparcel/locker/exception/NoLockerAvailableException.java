package com.smartparcel.locker.exception;

public class NoLockerAvailableException extends RuntimeException {
    public NoLockerAvailableException() {
        super("No suitable locker available");
    }
}
