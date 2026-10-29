package com.esgi.stepbystep.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/** Trace of a reminder sent for a task, on one channel. */
@Entity
@Table(name = "notification_log")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @NotNull
    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Column(nullable = false)
    private boolean success;

    protected NotificationLog() {
        // for JPA
    }

    public NotificationLog(Task task, NotificationChannel channel, Instant sentAt, boolean success) {
        this.task = task;
        this.channel = channel;
        this.sentAt = sentAt;
        this.success = success;
    }

    public Long getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public boolean isSuccess() {
        return success;
    }
}
