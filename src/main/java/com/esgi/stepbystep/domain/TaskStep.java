package com.esgi.stepbystep.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** One action of a task, short enough to start right away (30 minutes at most). */
@Entity
@Table(
        name = "task_step",
        uniqueConstraints = @UniqueConstraint(name = "uk_task_step_position", columnNames = {"task_id", "position"}))
public class TaskStep {

    public static final int LABEL_MAX_LENGTH = 80;
    public static final int MAX_MINUTES = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @NotBlank
    @Size(max = LABEL_MAX_LENGTH)
    @Column(nullable = false, length = LABEL_MAX_LENGTH)
    private String label;

    @NotNull
    @Min(1)
    @Max(MAX_MINUTES)
    @Column(name = "estimated_minutes", nullable = false)
    private Integer estimatedMinutes;

    /** Order of the step inside its task, starting at 1. */
    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false)
    private boolean done;

    protected TaskStep() {
        // for JPA
    }

    TaskStep(Task task, String label, int estimatedMinutes, int position) {
        this.task = task;
        this.label = label;
        this.estimatedMinutes = estimatedMinutes;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
