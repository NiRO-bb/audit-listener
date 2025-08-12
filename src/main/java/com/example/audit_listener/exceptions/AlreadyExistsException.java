package com.example.audit_listener.exceptions;

/**
 * Throws then current entity already exists in database table.
 */
public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String message) {
        super(message);
    }

}
