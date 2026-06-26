package com.smartparcel.locker.exception;

public class ParcelNotFoundException extends RuntimeException {
    public ParcelNotFoundException() {
        super("Parcel not found");
    }
}
