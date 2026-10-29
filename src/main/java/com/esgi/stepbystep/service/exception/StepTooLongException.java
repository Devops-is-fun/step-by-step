package com.esgi.stepbystep.service.exception;

import com.esgi.stepbystep.domain.TaskStep;

/** REG-01: a step lasts 30 minutes at most. */
public class StepTooLongException extends BusinessRuleException {

    public StepTooLongException(int minutes) {
        super("Cette sous-étape dure " + minutes + " minutes : c'est plus de " + TaskStep.MAX_MINUTES
                + " minutes. Découpe-la en plusieurs sous-étapes plus courtes.");
    }
}
