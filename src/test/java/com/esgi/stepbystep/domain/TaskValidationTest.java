package com.esgi.stepbystep.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Field constraints of the model, checked without any database. */
class TaskValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        factory.close();
    }

    @Test
    void acceptsAValidTaskWithSteps() {
        Task task = new Task("Call the property manager", 25);
        task.addStep("Find the phone number", 5);
        task.addStep("Make the call", 20);

        assertThat(validator.validate(task)).isEmpty();
    }

    @Test
    void rejectsABlankTitle() {
        assertThat(violatedFields(new Task("  ", 10))).containsExactly("title");
    }

    @Test
    void acceptsATitleOf120Characters() {
        assertThat(validator.validate(new Task("a".repeat(120), 10))).isEmpty();
    }

    @Test
    void rejectsATitleOf121Characters() {
        assertThat(violatedFields(new Task("a".repeat(121), 10))).containsExactly("title");
    }

    @Test
    void acceptsAStepOf30Minutes() {
        Task task = new Task("Tidy the desk", 30);
        task.addStep("Clear the desk", 30);

        assertThat(validator.validate(task)).isEmpty();
    }

    @Test
    void rejectsAStepLongerThan30Minutes() {
        Task task = new Task("Write the report", 45);
        task.addStep("Write everything", 31);

        assertThat(violatedFields(task)).containsExactly("steps[0].estimatedMinutes");
    }

    @Test
    void rejectsAStepOfZeroMinutes() {
        Task task = new Task("Water the plants", 5);
        task.addStep("Fill the watering can", 0);

        assertThat(violatedFields(task)).containsExactly("steps[0].estimatedMinutes");
    }

    @Test
    void rejectsAStepLabelOf81Characters() {
        Task task = new Task("Plan the week", 10);
        task.addStep("a".repeat(81), 10);

        assertThat(violatedFields(task)).containsExactly("steps[0].label");
    }

    @Test
    void numbersStepsInTheOrderTheyAreAdded() {
        Task task = new Task("Cook dinner", 40);
        task.addStep("Chop the vegetables", 15);
        task.addStep("Cook", 25);

        assertThat(task.getSteps()).extracting(TaskStep::getPosition).containsExactly(1, 2);
    }

    private Set<String> violatedFields(Object bean) {
        Set<ConstraintViolation<Object>> violations = validator.validate(bean);
        return violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(java.util.stream.Collectors.toSet());
    }
}
