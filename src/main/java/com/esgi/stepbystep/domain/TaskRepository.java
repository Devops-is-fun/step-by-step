package com.esgi.stepbystep.domain;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    /** True when a task with another status than {@code status} is planned on {@code day}. */
    boolean existsByPlannedForAndStatusNot(LocalDate day, TaskStatus status);

    /** Same as {@link #existsByPlannedForAndStatusNot}, ignoring the task {@code id} itself. */
    boolean existsByPlannedForAndStatusNotAndIdNot(LocalDate day, TaskStatus status, Long id);
}
