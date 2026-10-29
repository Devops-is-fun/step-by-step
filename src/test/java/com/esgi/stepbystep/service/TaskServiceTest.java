package com.esgi.stepbystep.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.esgi.stepbystep.domain.Task;
import com.esgi.stepbystep.domain.TaskRepository;
import com.esgi.stepbystep.domain.TaskStatus;
import com.esgi.stepbystep.domain.TaskStep;
import com.esgi.stepbystep.service.exception.AlreadyPlannedException;
import com.esgi.stepbystep.service.exception.StepTooLongException;
import com.esgi.stepbystep.service.exception.StepTooShortException;
import com.esgi.stepbystep.service.exception.TaskNotFoundException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** REG-01 and REG-02, without database nor Spring context: the repository is a Mockito mock. */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    private static final Long TASK_ID = 42L;
    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 2);

    @Mock
    private TaskRepository tasks;

    @InjectMocks
    private TaskService service;

    // REG-01 — a step lasts between 1 and 30 minutes

    @Test
    void shouldAcceptStepOfExactly30Minutes() {
        Task task = new Task("Tidy the desk", 30);
        when(tasks.findById(TASK_ID)).thenReturn(Optional.of(task));

        TaskStep step = service.addStep(TASK_ID, "Clear the desk", 30);

        assertThat(step.getEstimatedMinutes()).isEqualTo(30);
        assertThat(task.getSteps()).containsExactly(step);
    }

    @Test
    void shouldRejectStepLongerThan30Minutes() {
        assertThatThrownBy(() -> service.addStep(TASK_ID, "Write everything", 31))
                .isInstanceOf(StepTooLongException.class)
                .hasMessageContaining("31 minutes")
                .hasMessageContaining("Découpe-la");
        verifyNoInteractions(tasks);
    }

    @Test
    void shouldAcceptStepOfOneMinute() {
        when(tasks.findById(TASK_ID)).thenReturn(Optional.of(new Task("Water the plants", 1)));

        assertThat(service.addStep(TASK_ID, "Fill the watering can", 1).getEstimatedMinutes()).isEqualTo(1);
    }

    @Test
    void shouldRejectStepOfZeroMinutes() {
        assertThatThrownBy(() -> service.addStep(TASK_ID, "Nothing", 0)).isInstanceOf(StepTooShortException.class);
        verifyNoInteractions(tasks);
    }

    @Test
    void shouldRejectStepOnUnknownTask() {
        when(tasks.findById(TASK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addStep(TASK_ID, "Make the call", 10))
                .isInstanceOf(TaskNotFoundException.class);
    }

    // REG-02 — at most one unfinished task per day

    @Test
    void shouldAcceptTaskWithoutDate() {
        when(tasks.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task task = service.createTask("Call the bank", 10, null);

        assertThat(task.getPlannedFor()).isNull();
        verify(tasks, never()).existsByPlannedForAndStatusNot(any(), any());
    }

    @Test
    void shouldAcceptTaskOnFreeDate() {
        when(tasks.existsByPlannedForAndStatusNot(MONDAY, TaskStatus.DONE)).thenReturn(false);
        when(tasks.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(service.createTask("Call the bank", 10, MONDAY).getPlannedFor()).isEqualTo(MONDAY);
    }

    @Test
    void shouldRejectSecondUnfinishedTaskOnSameDate() {
        when(tasks.existsByPlannedForAndStatusNot(MONDAY, TaskStatus.DONE)).thenReturn(true);

        assertThatThrownBy(() -> service.createTask("Book the dentist", 10, MONDAY))
                .isInstanceOf(AlreadyPlannedException.class)
                .hasMessageContaining("2026-11-02");
        verify(tasks, never()).save(any());
    }

    @Test
    void shouldAcceptAnotherTaskWhenFirstIsDone() {
        // The only task planned on Monday is DONE: the query ignores DONE tasks, so the day is free
        when(tasks.existsByPlannedForAndStatusNot(MONDAY, TaskStatus.DONE)).thenReturn(false);
        when(tasks.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task task = service.createTask("Book the dentist", 10, MONDAY);

        assertThat(task.getPlannedFor()).isEqualTo(MONDAY);
        verify(tasks).existsByPlannedForAndStatusNot(MONDAY, TaskStatus.DONE);
    }

    @Test
    void shouldRejectPlanningOnOccupiedDate() {
        Task task = new Task("Book the dentist", 10);
        when(tasks.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(tasks.existsByPlannedForAndStatusNotAndIdNot(MONDAY, TaskStatus.DONE, TASK_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.planFor(TASK_ID, MONDAY)).isInstanceOf(AlreadyPlannedException.class);
        assertThat(task.getPlannedFor()).isNull();
    }

    @Test
    void shouldPlanOnFreeDate() {
        Task task = new Task("Book the dentist", 10);
        when(tasks.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(tasks.existsByPlannedForAndStatusNotAndIdNot(MONDAY, TaskStatus.DONE, TASK_ID)).thenReturn(false);

        assertThat(service.planFor(TASK_ID, MONDAY).getPlannedFor()).isEqualTo(MONDAY);
    }

    @Test
    void shouldRemoveTheDateOfATask() {
        Task task = new Task("Book the dentist", 10);
        task.setPlannedFor(MONDAY);
        when(tasks.findById(TASK_ID)).thenReturn(Optional.of(task));

        assertThat(service.planFor(TASK_ID, null).getPlannedFor()).isNull();
        verify(tasks, never()).existsByPlannedForAndStatusNotAndIdNot(any(), any(), any());
    }
}
