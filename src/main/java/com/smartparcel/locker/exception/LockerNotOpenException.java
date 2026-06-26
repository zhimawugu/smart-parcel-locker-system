package com.smartparcel.locker.exception;

public class LockerNotOpenException extends RuntimeException {
    public LockerNotOpenException() {
        super("Locker is not open; the session may have expired");
    }
}
