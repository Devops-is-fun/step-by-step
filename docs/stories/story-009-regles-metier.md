# Story 009 — Implémenter les règles REG-01 et REG-02 avec leurs tests unitaires

- **Jalon :** 1 — Socle applicatif
- **Exigences couvertes :** support applicatif ; alimente `EXG-COM-09`, `EXG-N1a-01`, `EXG-COM-11`
- **Dépendances :** story 008

## Description

En tant que personne qui utilise Step by Step, je veux que l'application refuse une sous-étape de plus
de 30 minutes et refuse une deuxième tâche non terminée à la même date, afin de respecter les deux
règles qui font le différenciateur TDA.

## Critères d'acceptation

- `REG-01` : `TaskStep` avec `estimatedMinutes` hors de 1–30 est refusée ; le message d'erreur invite
  explicitement à redécouper.
- `REG-02` : planifier une tâche à une date déjà occupée par une tâche non terminée est refusé.
- Les deux règles vivent dans le **service métier**, pas dans le contrôleur ni dans la vue.
- Chaque règle a au moins un test unitaire **positif** et un test unitaire **négatif** (frontière 30/31,
  date vide, date occupée, date de tâche terminée).
- Les tests unitaires sont rapides et n'utilisent ni base ni contexte Spring complet (Mockito).

## Preuves attendues

| Preuve | Where |
|--------|-------|
| 4+ tests unitaires verts | `target/surefire-reports/*.xml` |
| Frontière 30 acceptée / 31 refusée | noms des tests |
| Rapport JUnit exploitable | `TEST-*.xml` non vide |

## Automatisation des tests

```bash
./mvnw -B test -Dtest='TaskServiceTest'
```

Tests : `TaskServiceTest#shouldRejectStepLongerThan30Minutes`,
`#shouldAcceptStepOfExactly30Minutes`, `#shouldRejectSecondUnfinishedTaskOnSameDate`,
`#shouldAcceptAnotherTaskWhenFirstIsDone`.

## Documentation

`docs/architecture/business-rules.md` (en anglais) : règle, point d'application, test associé.

## Fichiers à versionner

- `src/main/java/com/esgi/stepbystep/service/TaskService.java`
- `src/main/java/com/esgi/stepbystep/service/exception/StepTooLongException.java`
- `src/main/java/com/esgi/stepbystep/service/exception/AlreadyPlannedException.java`
- `src/main/java/com/esgi/stepbystep/service/exception/StepTooShortException.java`, `BusinessRuleException.java`, `TaskNotFoundException.java`
- `src/main/java/com/esgi/stepbystep/domain/TaskRepository.java` (requêtes `REG-02`)
- `docs/architecture/business-rules.md`
- `src/test/java/com/esgi/stepbystep/service/TaskServiceTest.java`

## Fichiers à ne jamais versionner

rien de spécifique

## Risques

| Risque | Parade |
|--------|--------|
| Règles implémentées dans le contrôleur → non testables | Règles exclusivement dans `TaskService` |
| Couverture faible sur les cas limites | Tests de frontière explicites exigés ci-dessus |
| SonarQube signale la duplication | Un helper privé `validateStepDuration` |

## Critères de Done

- [ ] `REG-01` et `REG-02` implémentées dans le service
- [ ] 4 tests unitaires (2 positifs, 2 négatifs) verts
- [ ] Tests exécutables sans Docker ni base

## Décisions (`tasks-fr 009`, 29/10/2026)

| Sujet | Décision |
|-------|----------|
| Périmètre | `REG-01` et `REG-02` seulement (`1a`) |
| `REG-02` | requêtes `existsByPlannedForAndStatusNot…` avant enregistrement, index unique en filet (`2a`) ; requêtes prouvées par `TaskRepositoryIT` |
| Messages | français pour l'utilisateur, code en anglais (`3a`) |
| Doc | `docs/architecture/business-rules.md`, en anglais, par Claude (`4a`) |
| Partie de l'étudiante | lecture du rapport JUnit et de l'analyse SonarQube (`5a`) |
| Constat SonarQube | Quality Gate **rouge** sur *Coverage on New Code* (0 %) : pas encore de JaCoCo (story 021) ; 0 bug, 0 vulnérabilité, 0 code smell |

## Message de commit

1. `feat(app): enforce step duration and one task per day business rules`
2. `docs(app): add story 009 tutorials and record business rule decisions`
