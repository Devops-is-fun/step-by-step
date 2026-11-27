package com.esgi.stepbystep.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.esgi.stepbystep.domain.Task;
import com.esgi.stepbystep.service.TaskService;
import com.esgi.stepbystep.service.exception.AlreadyPlannedException;
import com.esgi.stepbystep.service.exception.PlannedTimeRequiresDateException;
import com.esgi.stepbystep.service.exception.StepTooLongException;
import com.esgi.stepbystep.service.exception.TaskNotFoundException;
import com.esgi.stepbystep.service.exception.TaskStepNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** HTTP layer only: the service is a Mockito mock, no database. */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 2);

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TaskService service;

    @Test
    void listsTasks() throws Exception {
        Task task = new Task("Call the bank", 10);
        task.addStep("Find the number", 5);
        when(service.listTasks()).thenReturn(List.of(task));

        mvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Call the bank"))
                .andExpect(jsonPath("$[0].steps[0].label").value("Find the number"))
                .andExpect(jsonPath("$[0].steps[0].position").value(1));
    }

    @Test
    void createsATask() throws Exception {
        Task task = new Task("Call the bank", 10);
        task.setPlannedFor(MONDAY);
        when(service.createTask("Call the bank", 10, MONDAY, null)).thenReturn(task);

        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Call the bank\",\"estimatedMinutes\":10,\"plannedFor\":\"2026-11-02\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plannedFor").value("2026-11-02"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void refusesATaskWithoutTitle() throws Exception {
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\" \",\"estimatedMinutes\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void refusesASecondTaskOnTheSameDayWithTheRuleMessage() throws Exception {
        when(service.createTask(any(), eq(10), eq(MONDAY), isNull())).thenThrow(new AlreadyPlannedException(MONDAY));

        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Book the dentist\",\"estimatedMinutes\":10,\"plannedFor\":\"2026-11-02\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(new AlreadyPlannedException(MONDAY).getMessage()));
    }

    @Test
    void addsAStep() throws Exception {
        Task task = new Task("Tidy the desk", 30);
        task.addStep("Clear the desk", 30);
        when(service.getTask(7L)).thenReturn(task);

        mvc.perform(post("/api/tasks/7/steps").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Clear the desk\",\"estimatedMinutes\":30}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.steps[0].estimatedMinutes").value(30));
    }

    @Test
    void refusesAStepLongerThan30MinutesWithTheRuleMessage() throws Exception {
        when(service.addStep(7L, "Write everything", 45)).thenThrow(new StepTooLongException(45));

        mvc.perform(post("/api/tasks/7/steps").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"label\":\"Write everything\",\"estimatedMinutes\":45}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(new StepTooLongException(45).getMessage()));
    }

    @Test
    void updatesStepCompletionAndReturnsTheUpdatedTask() throws Exception {
        Task task = new Task("Tidy the desk", 10);
        task.addStep("Clear the desk", 10).setDone(true);
        task.setStatus(com.esgi.stepbystep.domain.TaskStatus.DONE);
        when(service.setStepDone(7L, 15L, true)).thenReturn(task);

        mvc.perform(put("/api/tasks/7/steps/15/done").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"done\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.steps[0].done").value(true));
    }

    @Test
    void deletesAStepAndReturnsTheUpdatedTask() throws Exception {
        Task task = new Task("Tidy the desk", 10);
        when(service.deleteStep(7L, 15L)).thenReturn(task);

        mvc.perform(delete("/api/tasks/7/steps/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.steps").isEmpty())
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void deletesATaskWithNoContentResponse() throws Exception {
        mvc.perform(delete("/api/tasks/7"))
                .andExpect(status().isNoContent());
    }

    @Test
    void answers404WhenStepDoesNotBelongToTask() throws Exception {
        when(service.deleteStep(7L, 15L)).thenThrow(new TaskStepNotFoundException(7L, 15L));

        mvc.perform(delete("/api/tasks/7/steps/15"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void plansATask() throws Exception {
        Task task = new Task("Call the bank", 10);
        task.setPlannedFor(MONDAY);
        when(service.planFor(7L, MONDAY, null)).thenReturn(task);

        mvc.perform(put("/api/tasks/7/planned-for").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plannedFor\":\"2026-11-02\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plannedFor").value("2026-11-02"));
    }

    @Test
    void createsATaskWithAPlannedStartTime() throws Exception {
        Task task = new Task("Call the bank", 10);
        task.setPlannedFor(MONDAY);
        task.setPlannedTime(LocalTime.of(9, 30));
        when(service.createTask("Call the bank", 10, MONDAY, LocalTime.of(9, 30))).thenReturn(task);

        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Call the bank\",\"estimatedMinutes\":10,"
                                + "\"plannedFor\":\"2026-11-02\",\"plannedTime\":\"09:30\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plannedTime").value("09:30:00"));
    }

    @Test
    void refusesAPlannedTimeWithoutADate() throws Exception {
        when(service.createTask(any(), eq(10), isNull(), eq(LocalTime.of(9, 30))))
                .thenThrow(new PlannedTimeRequiresDateException());

        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Call the bank\",\"estimatedMinutes\":10,"
                                + "\"plannedTime\":\"09:30\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(new PlannedTimeRequiresDateException().getMessage()));
    }

    @Test
    void answers404ForAnUnknownTask() throws Exception {
        when(service.getTask(99L)).thenThrow(new TaskNotFoundException(99L));

        mvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Task 99 not found"));
    }
}
