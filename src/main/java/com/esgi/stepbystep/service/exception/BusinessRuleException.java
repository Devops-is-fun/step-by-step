package com.esgi.stepbystep.service.exception;

/** A business rule (REG-xx) refused the operation; the message is meant for the user. */
public abstract class BusinessRuleException extends RuntimeException {

    protected BusinessRuleException(String message) {
        super(message);
    }
}
