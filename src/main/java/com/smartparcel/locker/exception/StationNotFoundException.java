package com.smartparcel.locker.exception;

public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException() {
        super("Locker station not found");
    }
}
