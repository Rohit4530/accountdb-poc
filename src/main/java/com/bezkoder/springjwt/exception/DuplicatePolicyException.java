package com.bezkoder.springjwt.exception;

public class DuplicatePolicyException extends RuntimeException {
    public DuplicatePolicyException(String message) {
        super(message);
    }

    public DuplicatePolicyException(String message, Throwable cause) {
        super(message, cause);
    }
}
