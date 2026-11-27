package com.esgi.stepbystep.service.exception;

public class TaskStepNotFoundException extends RuntimeException {

    public TaskStepNotFoundException(Long taskId, Long stepId) {
        super("Step " + stepId + " not found for task " + taskId);
    }
}
