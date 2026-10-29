package com.esgi.stepbystep.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

/** Persistence of the task model in the real PostgreSQL test database (compose-test.yml). */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TaskRepositoryIT {

    @Autowired
    private TaskRepository tasks;

    @Autowired
    private NotificationLogRepository notifications;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savesATaskAndReadsItsStepsBackInOrder() {
        Task task = new Task("Call the property manager", 25);
        task.setPlannedFor(LocalDate.of(2026, 11, 2));
        task.addStep("Find the phone number", 5);
        task.addStep("Write down the questions", 10);
        task.addStep("Make the call", 10);
        Long id = tasks.save(task).getId();
        entityManager.flush();
        entityManager.clear();

        Task found = tasks.findById(id).orElseThrow();

        assertThat(found.getTitle()).isEqualTo("Call the property manager");
        assertThat(found.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(found.getPlannedFor()).isEqualTo(LocalDate.of(2026, 11, 2));
        assertThat(found.getReminderAt()).isNull();
        assertThat(found.getCreatedAt()).isNotNull();
        assertThat(found.getSteps())
                .extracting(TaskStep::getLabel)
                .containsExactly("Find the phone number", "Write down the questions", "Make the call");
    }

    @Test
    void updatesAndDeletesATaskWithItsSteps() {
        Task task = new Task("Tidy the desk", 15);
        task.addStep("Clear the desk", 15);
        Long id = tasks.save(task).getId();
        entityManager.flush();
        entityManager.clear();

        Task saved = tasks.findById(id).orElseThrow();
        saved.setStatus(TaskStatus.DONE);
        entityManager.flush();
        entityManager.clear();
        assertThat(tasks.findById(id).orElseThrow().getStatus()).isEqualTo(TaskStatus.DONE);

        tasks.deleteById(id);
        entityManager.flush();
        assertThat(tasks.findById(id)).isEmpty();
        assertThat(entityManager.getEntityManager()
                        .createQuery("select count(s) from TaskStep s", Long.class)
                        .getSingleResult())
                .isZero();
    }

    @Test
    void refusesTwoStepsAtTheSamePosition() {
        Task task = new Task("Plan the week", 20);
        task.addStep("List the appointments", 10);
        task.addStep("Pick the priorities", 10).setPosition(1);

        assertThatThrownBy(() -> {
                    tasks.save(task);
                    entityManager.flush();
                })
                .hasMessageContaining("uk_task_step_position");
    }

    @Test
    void refusesTwoUnfinishedTasksOnTheSameDay() {
        LocalDate day = LocalDate.of(2026, 11, 3);
        Task first = new Task("Call the bank", 10);
        first.setPlannedFor(day);
        Task second = new Task("Book the dentist", 10);
        second.setPlannedFor(day);
        tasks.save(first);

        assertThatThrownBy(() -> {
                    tasks.save(second);
                    entityManager.flush();
                })
                .hasMessageContaining("uk_task_planned_for_unfinished");
    }

    @Test
    void keepsTheNotificationsOfATask() {
        Task task = tasks.save(new Task("Water the plants", 5));
        notifications.save(new NotificationLog(task, NotificationChannel.DISCORD, Instant.parse("2026-11-02T08:00:00Z"), true));
        notifications.save(new NotificationLog(task, NotificationChannel.IN_APP, Instant.parse("2026-11-02T08:00:01Z"), true));
        entityManager.flush();
        entityManager.clear();

        assertThat(notifications.findByTaskIdOrderBySentAtAsc(task.getId()))
                .extracting(NotificationLog::getChannel)
                .containsExactly(NotificationChannel.DISCORD, NotificationChannel.IN_APP);
    }
}
