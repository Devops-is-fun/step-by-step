# Business rules

The rules that make Step by Step fit an ADHD profile. Each rule lives in the **service layer**
(`TaskService`), never in a controller or a view, so that it is enforced whatever the entry point and
tested without a database. The rules not listed here (`REG-03` to `REG-07`) come with story 028.

## Implemented rules

| ID | Rule | Enforced in | Refusal | Tests |
|----|------|-------------|---------|-------|
| `REG-01` | A step lasts between 1 and 30 minutes; beyond, the user is invited to split it | `TaskService#addStep` → `validateStepDuration` | `StepTooLongException` (> 30), `StepTooShortException` (< 1) | `TaskServiceTest`: `shouldAcceptStepOfExactly30Minutes`, `shouldRejectStepLongerThan30Minutes`, `shouldAcceptStepOfOneMinute`, `shouldRejectStepOfZeroMinutes` |
| `REG-02` | On a given day, at most **one** unfinished task is planned | `TaskService#createTask`, `TaskService#planFor` | `AlreadyPlannedException` | `TaskServiceTest`: `shouldAcceptTaskWithoutDate`, `shouldAcceptTaskOnFreeDate`, `shouldRejectSecondUnfinishedTaskOnSameDate`, `shouldAcceptAnotherTaskWhenFirstIsDone`, `shouldRejectPlanningOnOccupiedDate`, `shouldPlanOnFreeDate`, `shouldRemoveTheDateOfATask` |

## Defence in depth

| Rule | Service (readable message) | Entity (Bean Validation) | Database (last guard) |
|------|----------------------------|--------------------------|-----------------------|
| `REG-01` | `validateStepDuration` | `@Min(1) @Max(30)` on `TaskStep#estimatedMinutes` | `CHECK (estimated_minutes BETWEEN 1 AND 30)` |
| `REG-02` | `existsByPlannedForAndStatusNot…` queries | — | partial unique index `uk_task_planned_for_unfinished` |

The repository queries used by `REG-02` are checked against PostgreSQL in
`TaskRepositoryIT#findsOnlyUnfinishedTasksWhenCheckingADay`: a mock cannot prove that a query ignores
`DONE` tasks.

## User messages

Messages of `BusinessRuleException` subclasses are shown to the user, in French and in plain words
(for example: « Cette sous-étape dure 45 minutes : c'est plus de 30 minutes. Découpe-la en plusieurs
sous-étapes plus courtes. »). Code, logs and technical exceptions stay in English.
