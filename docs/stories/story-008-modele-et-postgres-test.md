# Story 008 — Modèle de données et stack PostgreSQL de test

- **Jalon :** 1 — Socle applicatif
- **Exigences couvertes :** support applicatif ; prépare `EXG-COM-10` et `EXG-BON-02`
- **Dépendances :** story 007

## Description

En tant que développeur, je veux le modèle `Task` / `TaskStep` / `NotificationLog` et une base
PostgreSQL lançable par Docker Compose, afin que les tests d'intégration du pipeline aient une base
réelle à interroger (`EXG-COM-10`).

## Critères d'acceptation

- Les trois entités JPA de la spécification (section 4.4) sont mappées, avec les contraintes de
  champs (titre 1–120, `estimatedMinutes` 1–30, `label` 1–80, `position` unique par tâche).
- `plannedFor` est nullable ; `reminderAt` est nullable.
- `src/test/resources/compose-test.yml` démarre PostgreSQL 16, port interne 5433, base
  `stepbystep_test`, avec healthcheck.
- Un test d'intégration Failsafe persiste puis relit une tâche avec ses sous-étapes.
- Les tests unitaires (Surefire) n'utilisent **aucune** base : ils restent exécutables sans Docker.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Tests d'intégration verts | `target/failsafe-reports/` |
| Table créée et lisible | sortie du test, ou `psql` dans le conteneur |
| Tests unitaires sans base | `mvnw -B surefire:test` seul passe |

## Automatisation des tests

- `TaskRepositoryIT` (Failsafe) : CRUD + lecture des sous-étapes ordonnées par `position`.
- Commandes : `docker compose -f src/test/resources/compose-test.yml up -d && ./mvnw -B verify`.

## Documentation

`docs/architecture/data-model.md` (en anglais) : diagramme Mermaid des 3 entités et tableau des contraintes.

## Fichiers à versionner

- `src/main/java/com/esgi/stepbystep/domain/` (entités, repositories)
- `src/main/resources/db/migration/V1__create_task_model.sql`
- `src/main/resources/application.yml`, `src/test/resources/application-test.yml`
- `src/test/resources/compose-test.yml`
- `src/test/java/com/esgi/stepbystep/**/*IT.java`, `src/test/java/com/esgi/stepbystep/**/*Test.java`
- `pom.xml` (dépendances JPA/Flyway/PostgreSQL, plugin Failsafe)
- `docs/architecture/data-model.md`

## Fichiers à ne jamais versionner

`.env`, `pgdata/`, volumes Docker locaux

## Risques

| Risque | Parade |
|--------|--------|
| Schéma de base divergent du modèle | `spring.jpa.hibernate.ddl-auto=validate` en test |
| Mot de passe de test en clair | Valeur de **test** uniquement, dans `compose-test.yml`, assumée et documentée |
| SQL portable non garanti | Une seule version de PostgreSQL, épinglée |

## Critères de Done

- [ ] 3 entités mappées avec contraintes
- [ ] `compose-test.yml` démarre un PostgreSQL sain
- [ ] Test Failsafe d'intégration vert
- [ ] Surefire seul passe sans Docker

## Décisions (`tasks-fr 008`, 29/10/2026)

| Sujet | Décision |
|-------|----------|
| Schéma | Flyway dès cette story (`V1__create_task_model.sql`), Hibernate en `ddl-auto=validate` (`1a`) |
| `compose-test.yml` | écrit par l'étudiante (`2a`) : `postgres:16.15-alpine3.24`, `name: stepbystep-test`, `tmpfs`, healthcheck `pg_isready` |
| Failsafe | branché par l'étudiante (`3a`) ; Surefire = `*Test`, Failsafe = `*IT` |
| Port | 5432 dans le conteneur, `127.0.0.1:5433` sur le poste (`4a`) ; accès depuis l'agent Jenkins → story 012 |
| Doc | `docs/architecture/data-model.md`, en anglais, par Claude (`5a`) |
| Datasource | fournie par l'environnement (`SPRING_DATASOURCE_*`) ; l'application ne démarre plus sans base |
| Test de démarrage | `StepByStepApplicationTests` devient `StepByStepApplicationIT` (il a besoin de la base) |

## Message de commit

1. `feat(app): add task domain model and postgres test stack`
2. `docs(app): add story 008 tutorials and record data model decisions`
