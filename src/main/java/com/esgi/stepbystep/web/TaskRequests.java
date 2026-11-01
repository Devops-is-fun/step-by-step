package com.esgi.stepbystep.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** JSON bodies accepted by the task API. */
public final class TaskRequests {

    private TaskRequests() {
    }

    /** POST /api/tasks */
    public record CreateTask(@NotBlank String title, @NotNull Integer estimatedMinutes, LocalDate plannedFor) {
    }

    /** POST /api/tasks/{id}/steps */
    public record AddStep(@NotBlank String label, @NotNull Integer estimatedMinutes) {
    }

    /** PUT /api/tasks/{id}/planned-for — a null day removes the planning. */
    public record PlanTask(LocalDate plannedFor) {
    }
}
