package com.esgi.stepbystep.web;

import com.esgi.stepbystep.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST API of Step by Step: the business rules stay in TaskService, the controller only translates HTTP. */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService tasks;

    public TaskController(TaskService tasks) {
        this.tasks = tasks;
    }

    @GetMapping
    public List<TaskResponse> list() {
        return tasks.listTasks().stream().map(TaskResponse::from).toList();
    }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable Long id) {
        return TaskResponse.from(tasks.getTask(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskRequests.CreateTask request) {
        return TaskResponse.from(tasks.createTask(request.title(), request.estimatedMinutes(), request.plannedFor()));
    }

    @PostMapping("/{id}/steps")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse addStep(@PathVariable Long id, @Valid @RequestBody TaskRequests.AddStep request) {
        tasks.addStep(id, request.label(), request.estimatedMinutes());
        return TaskResponse.from(tasks.getTask(id));
    }

    @PutMapping("/{id}/planned-for")
    public TaskResponse plan(@PathVariable Long id, @RequestBody TaskRequests.PlanTask request) {
        return TaskResponse.from(tasks.planFor(id, request.plannedFor()));
    }
}
