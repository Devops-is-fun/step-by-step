package com.esgi.stepbystep.service;

import com.esgi.stepbystep.domain.Task;
import com.esgi.stepbystep.domain.TaskRepository;
import com.esgi.stepbystep.domain.TaskStatus;
import com.esgi.stepbystep.domain.TaskStep;
import com.esgi.stepbystep.service.exception.AlreadyPlannedException;
import com.esgi.stepbystep.service.exception.PlannedTimeRequiresDateException;
import com.esgi.stepbystep.service.exception.StepTooLongException;
import com.esgi.stepbystep.service.exception.StepTooShortException;
import com.esgi.stepbystep.service.exception.TaskNotFoundException;
import com.esgi.stepbystep.service.exception.TaskStepNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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

    /** All tasks, oldest first. */
    @Transactional(readOnly = true)
    public List<Task> listTasks() {
        return tasks.findAllByOrderByIdAsc();
    }

    /** One task with its steps. */
    @Transactional(readOnly = true)
    public Task getTask(Long taskId) {
        return findTask(taskId);
    }

    /** Creates a task, optionally planned on a day (REG-02). */
    public Task createTask(String title, int estimatedMinutes, LocalDate plannedFor) {
        return createTask(title, estimatedMinutes, plannedFor, null);
    }

    /** Creates a task, optionally planned on a day and time (REG-02). */
    public Task createTask(String title, int estimatedMinutes, LocalDate plannedFor, LocalTime plannedTime) {
        validatePlannedTime(plannedFor, plannedTime);
        if (plannedFor != null && tasks.existsByPlannedForAndStatusNot(plannedFor, TaskStatus.DONE)) {
            throw new AlreadyPlannedException(plannedFor);
        }
        Task task = new Task(title, estimatedMinutes);
        task.setPlannedFor(plannedFor);
        task.setPlannedTime(plannedTime);
        return tasks.save(task);
    }

    /** Plans an existing task on a day, or removes its day when {@code day} is null (REG-02). */
    public Task planFor(Long taskId, LocalDate day) {
        return planFor(taskId, day, null);
    }

    /** Plans an existing task on a day and optional start time, or removes its schedule. */
    public Task planFor(Long taskId, LocalDate day, LocalTime time) {
        validatePlannedTime(day, time);
        Task task = findTask(taskId);
        if (day != null && tasks.existsByPlannedForAndStatusNotAndIdNot(day, TaskStatus.DONE, taskId)) {
            throw new AlreadyPlannedException(day);
        }
        task.setPlannedFor(day);
        task.setPlannedTime(time);
        return task;
    }

    /** Appends a step to a task (REG-01). */
    public TaskStep addStep(Long taskId, String label, int estimatedMinutes) {
        validateStepDuration(estimatedMinutes);
        Task task = findTask(taskId);
        TaskStatus nextStatus = task.getSteps().stream().anyMatch(TaskStep::isDone)
                ? TaskStatus.IN_PROGRESS : TaskStatus.TODO;
        ensureCanTransitionTo(task, nextStatus);
        return task.addStep(label, estimatedMinutes);
    }

    /** Updates whether a step is complete and recalculates its task status. */
    public Task setStepDone(Long taskId, Long stepId, boolean done) {
        Task task = findTask(taskId);
        TaskStep step = findStep(task, stepId);
        TaskStatus nextStatus = statusForSteps(task.getSteps().stream()
                .map(current -> current == step ? done : current.isDone())
                .toList());
        ensureCanTransitionTo(task, nextStatus);
        step.setDone(done);
        task.refreshStatusFromSteps();
        return task;
    }

    /** Deletes a step without renumbering the remaining steps. */
    public Task deleteStep(Long taskId, Long stepId) {
        Task task = findTask(taskId);
        TaskStep step = findStep(task, stepId);
        TaskStatus nextStatus = statusForSteps(task.getSteps().stream()
                .filter(current -> current != step)
                .map(TaskStep::isDone)
                .toList());
        ensureCanTransitionTo(task, nextStatus);
        task.removeStep(step);
        return task;
    }

    /** Deletes a task and its dependent steps and notifications. */
    public void deleteTask(Long taskId) {
        tasks.delete(findTask(taskId));
    }

    private void validateStepDuration(int minutes) {
        if (minutes < 1) {
            throw new StepTooShortException();
        }
        if (minutes > TaskStep.MAX_MINUTES) {
            throw new StepTooLongException(minutes);
        }
    }

    private void validatePlannedTime(LocalDate day, LocalTime time) {
        if (time != null && day == null) {
            throw new PlannedTimeRequiresDateException();
        }
    }

    private void ensureCanTransitionTo(Task task, TaskStatus nextStatus) {
        LocalDate day = task.getPlannedFor();
        if (task.getStatus() == TaskStatus.DONE && nextStatus != TaskStatus.DONE && day != null
                && tasks.existsByPlannedForAndStatusNotAndIdNot(day, TaskStatus.DONE, task.getId())) {
            throw new AlreadyPlannedException(day);
        }
    }

    private TaskStatus statusForSteps(List<Boolean> completedSteps) {
        long completed = completedSteps.stream().filter(Boolean::booleanValue).count();
        if (completedSteps.isEmpty() || completed == 0) {
            return TaskStatus.TODO;
        }
        return completed == completedSteps.size() ? TaskStatus.DONE : TaskStatus.IN_PROGRESS;
    }

    private TaskStep findStep(Task task, Long stepId) {
        return task.getSteps().stream()
                .filter(step -> stepId != null && stepId.equals(step.getId()))
                .findFirst()
                .orElseThrow(() -> new TaskStepNotFoundException(task.getId(), stepId));
    }

    private Task findTask(Long taskId) {
        return tasks.findWithStepsById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
    }
}
