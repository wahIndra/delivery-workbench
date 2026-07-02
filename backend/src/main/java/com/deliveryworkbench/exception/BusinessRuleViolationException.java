package com.deliveryworkbench.exception;

/**
 * Thrown when a business rule (BR-01 to BR-10) is violated.
 * For example: moving a request to READY_FOR_DEVELOPMENT when DoR is not READY.
 * Maps to HTTP 422 Unprocessable Entity.
 */
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
