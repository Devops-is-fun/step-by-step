package com.esgi.stepbystep;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** The whole application starts against the test database (Flyway migrations + schema validation). */
@SpringBootTest
@ActiveProfiles("test")
class StepByStepApplicationIT {

    @Test
    void contextLoads() {
    }
}
