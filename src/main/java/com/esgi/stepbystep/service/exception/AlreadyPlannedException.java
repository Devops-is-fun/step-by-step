package com.esgi.stepbystep.service.exception;

import java.time.LocalDate;

/** REG-02: at most one unfinished task per day. */
public class AlreadyPlannedException extends BusinessRuleException {

    public AlreadyPlannedException(LocalDate day) {
        super("Une tâche non terminée est déjà prévue le " + day
                + ". Termine-la, ou choisis un autre jour : une seule tâche par jour.");
    }
}
