package com.esgi.stepbystep;

import static org.assertj.core.api.Assertions.assertThat;

import com.esgi.stepbystep.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

/** The whole application starts against the test database (Flyway migrations + schema validation). */
@SpringBootTest
@ActiveProfiles("test")
class StepByStepApplicationIT {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context.getBean(TaskService.class)).isNotNull();
    }
}
