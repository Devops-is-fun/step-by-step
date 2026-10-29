package com.esgi.stepbystep.service.exception;

/** REG-01: a step lasts at least 1 minute. */
public class StepTooShortException extends BusinessRuleException {

    public StepTooShortException() {
        super("Une sous-étape dure au moins 1 minute.");
    }
}
