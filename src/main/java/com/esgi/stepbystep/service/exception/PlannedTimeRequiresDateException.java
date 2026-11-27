package com.esgi.stepbystep.service.exception;

/** A planned start time is only meaningful when the task has a planned day. */
public class PlannedTimeRequiresDateException extends BusinessRuleException {

    public PlannedTimeRequiresDateException() {
        super("Une heure de début nécessite une date prévue.");
    }
}
