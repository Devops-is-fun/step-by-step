package com.esgi.stepbystep.web;

import com.esgi.stepbystep.service.exception.BusinessRuleException;
import com.esgi.stepbystep.service.exception.TaskNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Turns refusals into readable JSON errors: {"error": "..."}. */
@RestControllerAdvice
public class ApiErrorHandler {

    /** A business rule (REG-01, REG-02) refused the operation: its message is meant for the user. */
    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> businessRule(BusinessRuleException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(TaskNotFoundException exception) {
        return Map.of("error", exception.getMessage());
    }

    /** Invalid request body (missing title, blank label...) or field constraint of the model. */
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalid(Exception exception) {
        return Map.of("error", "Requête invalide : vérifie le titre, le libellé et les durées.");
    }
}
