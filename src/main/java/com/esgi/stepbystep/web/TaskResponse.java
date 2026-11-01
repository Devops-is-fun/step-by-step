package com.esgi.stepbystep.web;

import com.esgi.stepbystep.domain.Task;
import com.esgi.stepbystep.domain.TaskStatus;
import com.esgi.stepbystep.domain.TaskStep;
import java.time.LocalDate;
import java.util.List;

/** A task as returned by the API, with its steps in order. */
public record TaskResponse(Long id, String title, Integer estimatedMinutes, LocalDate plannedFor, TaskStatus status,
                           List<Step> steps) {

    /** One step of a task. */
    public record Step(Long id, Integer position, String label, Integer estimatedMinutes, boolean done) {

        static Step from(TaskStep step) {
            return new Step(step.getId(), step.getPosition(), step.getLabel(), step.getEstimatedMinutes(), step.isDone());
        }
    }

    static TaskResponse from(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getEstimatedMinutes(), task.getPlannedFor(),
                task.getStatus(), task.getSteps().stream().map(Step::from).toList());
    }
}
