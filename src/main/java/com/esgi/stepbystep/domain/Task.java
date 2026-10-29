package com.esgi.stepbystep.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/** Something to do, split into steps of 30 minutes at most. */
@Entity
@Table(name = "task")
public class Task {

    public static final int TITLE_MAX_LENGTH = 120;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = TITLE_MAX_LENGTH)
    @Column(nullable = false, length = TITLE_MAX_LENGTH)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @NotNull
    @Min(1)
    @Column(name = "estimated_minutes", nullable = false)
    private Integer estimatedMinutes;

    /** Day the task is planned for; optional (no mandatory deadline). */
    @Column(name = "planned_for")
    private LocalDate plannedFor;

    /** Local date and time of the reminder; optional. */
    @Column(name = "reminder_at")
    private LocalDateTime reminderAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.TODO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Valid
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<TaskStep> steps = new ArrayList<>();

    protected Task() {
        // for JPA
    }

    public Task(String title, int estimatedMinutes) {
        this.title = title;
        this.estimatedMinutes = estimatedMinutes;
    }

    /** Appends a step after the existing ones. */
    public TaskStep addStep(String label, int estimatedMinutes) {
        TaskStep step = new TaskStep(this, label, estimatedMinutes, steps.size() + 1);
        steps.add(step);
        return step;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public LocalDate getPlannedFor() {
        return plannedFor;
    }

    public void setPlannedFor(LocalDate plannedFor) {
        this.plannedFor = plannedFor;
    }

    public LocalDateTime getReminderAt() {
        return reminderAt;
    }

    public void setReminderAt(LocalDateTime reminderAt) {
        this.reminderAt = reminderAt;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<TaskStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }
}
