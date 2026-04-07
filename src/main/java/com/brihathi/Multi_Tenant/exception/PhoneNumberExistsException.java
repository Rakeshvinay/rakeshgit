package com.brihathi.Multi_Tenant.exception;

public class PhoneNumberExistsException extends RuntimeException {
    public PhoneNumberExistsException(String message) {
        super(message);
    }
} 