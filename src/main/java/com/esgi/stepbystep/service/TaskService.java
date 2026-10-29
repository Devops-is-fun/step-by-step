package com.esgi.stepbystep.service;

import com.esgi.stepbystep.domain.Task;
import com.esgi.stepbystep.domain.TaskRepository;
import com.esgi.stepbystep.domain.TaskStatus;
import com.esgi.stepbystep.domain.TaskStep;
import com.esgi.stepbystep.service.exception.AlreadyPlannedException;
import com.esgi.stepbystep.service.exception.StepTooLongException;
import com.esgi.stepbystep.service.exception.StepTooShortException;
import com.esgi.stepbystep.service.exception.TaskNotFoundException;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business rules of tasks (docs/architecture/business-rules.md). */
@Service
@Transactional
public class TaskService {

    private final TaskRepository tasks;

    public TaskService(TaskRepository tasks) {
        this.tasks = tasks;
    }

    /** Creates a task, optionally planned on a day (REG-02). */
    public Task createTask(String title, int estimatedMinutes, LocalDate plannedFor) {
        if (plannedFor != null && tasks.existsByPlannedForAndStatusNot(plannedFor, TaskStatus.DONE)) {
            throw new AlreadyPlannedException(plannedFor);
        }
        Task task = new Task(title, estimatedMinutes);
        task.setPlannedFor(plannedFor);
        return tasks.save(task);
    }

    /** Plans an existing task on a day, or removes its day when {@code day} is null (REG-02). */
    public Task planFor(Long taskId, LocalDate day) {
        Task task = findTask(taskId);
        if (day != null && tasks.existsByPlannedForAndStatusNotAndIdNot(day, TaskStatus.DONE, taskId)) {
            throw new AlreadyPlannedException(day);
        }
        task.setPlannedFor(day);
        return task;
    }

    /** Appends a step to a task (REG-01). */
    public TaskStep addStep(Long taskId, String label, int estimatedMinutes) {
        validateStepDuration(estimatedMinutes);
        return findTask(taskId).addStep(label, estimatedMinutes);
    }

    private void validateStepDuration(int minutes) {
        if (minutes < 1) {
            throw new StepTooShortException();
        }
        if (minutes > TaskStep.MAX_MINUTES) {
            throw new StepTooLongException(minutes);
        }
    }

    private Task findTask(Long taskId) {
        return tasks.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
    }
}
