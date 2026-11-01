package com.esgi.stepbystep.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    /** A task with its steps loaded in the same query (the API reads them after the transaction). */
    @EntityGraph(attributePaths = "steps")
    Optional<Task> findWithStepsById(Long id);

    /** All tasks with their steps, oldest first. */
    @EntityGraph(attributePaths = "steps")
    List<Task> findAllByOrderByIdAsc();

    /** True when a task with another status than {@code status} is planned on {@code day}. */
    boolean existsByPlannedForAndStatusNot(LocalDate day, TaskStatus status);

    /** Same as {@link #existsByPlannedForAndStatusNot}, ignoring the task {@code id} itself. */
    boolean existsByPlannedForAndStatusNotAndIdNot(LocalDate day, TaskStatus status, Long id);
}
