package com.esgi.stepbystep.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    List<NotificationLog> findByTaskIdOrderBySentAtAsc(Long taskId);
}
