package com.smartparcel.locker.exception;

public class RecipientNotFoundException extends RuntimeException {
    public RecipientNotFoundException() {
        super("Recipient not found");
    }
}
