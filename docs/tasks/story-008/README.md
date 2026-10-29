# Story 008 — Modèle de données et stack PostgreSQL de test

Découpage TDA-friendly en **7 tutoriels**, à faire **dans l'ordre**.

- **Exigences TP couvertes :** support applicatif ; prépare `EXG-COM-10` (tests d'intégration avec
  Docker Compose) et `EXG-BON-02` (migrations)
- **Fichiers produits :**
  - code (écrit par Claude) : entités et repositories (`domain/`), migration Flyway, `application.yml`,
    `application-test.yml`, tests (`TaskValidationTest`, `TaskRepositoryIT`, `StepByStepApplicationIT`),
    `docs/architecture/data-model.md` ;
  - CI/CD (écrits par toi) : `src/test/resources/compose-test.yml`, plugin Failsafe du `pom.xml`,
    README ;
  - **1 commit** poussé sur GitHub.
- **Durée totale :** ⏱️ 1 h 55
- **Prérequis de la story :** story 007 terminée

> **Répertoire de travail de tous les tutoriels :** la racine du projet, le dossier qui contient
> `docs/`.

## L'image de la story

Jusqu'ici, Step by Step était une coquille qui disait « je vais bien ». Claude lui a donné une
**mémoire** : des tâches, des sous-étapes, des traces de rappels. Pour vérifier que cette mémoire
fonctionne **pour de vrai**, il faut une vraie base. Toi, tu construis le **banc d'essai** : une base
PostgreSQL jetable, qu'on allume pour les tests et qu'on éteint après. C'est ce banc que Jenkins
utilisera en story 012.

## Ordre des tutoriels

| # | Titre | Qui | Temps | Difficulté | Dépend de |
|---|-------|-----|-------|------------|-----------|
| [001](task-001.md) | Découvrir le modèle et lancer les tests unitaires | 🤖 | ⏱️ 15 | ⭐ | story 007 |
| [002](task-002.md) | Écrire `compose-test.yml` | 🧑‍💻 | ⏱️ 20 | ⭐⭐⭐ | 001 |
| [003](task-003.md) | Démarrer la base de test et l'interroger | 🧑‍💻 | ⏱️ 15 | ⭐⭐ | 002 |
| [004](task-004.md) | Brancher Failsafe dans le `pom.xml` | 🧑‍💻 | ⏱️ 15 | ⭐⭐ | 001 |
| [005](task-005.md) | Lancer les tests d'intégration et lire leurs rapports | 🧑‍💻 | ⏱️ 20 | ⭐⭐ | 003, 004 |
| [006](task-006.md) | Mettre à jour le README (FR + EN) | 🧑‍💻 | ⏱️ 15 | ⭐ | 005 |
| [007](task-007.md) | Éteindre la base, commiter, pousser, bilan | 🧑‍💻 | ⏱️ 15 | ⭐⭐ | 006 |

Bonne coupure naturelle : après le **003** (≈ 50 min, la base répond).

## Jalons

| Jalon | Tutoriels | Preuve que tu y es |
|-------|-----------|--------------------|
| **J1 — Le modèle existe** | 001 | `./mvnw -B test` : 9 tests verts, **sans Docker** |
| **J2 — Le banc d'essai** | 002, 003 | conteneur `healthy`, `psql` répond |
| **J3 — Tests d'intégration** | 004, 005 | `./mvnw -B verify` : 6 tests Failsafe verts |
| **J4 — Story terminée** | 006, 007 | 1 commit poussé ; base éteinte ; `git status` vide |

## Décisions déjà prises

| Sujet | Décision |
|-------|----------|
| Schéma | **Flyway** (`V1__create_task_model.sql`) ; Hibernate en `ddl-auto=validate` |
| Base de test | `postgres:16.15-alpine3.24`, projet Compose `stepbystep-test`, données en mémoire (`tmpfs`) |
| Accès | PostgreSQL écoute sur 5432 **dans** le conteneur, publié sur `127.0.0.1:5433` |
| Identifiants de test | `stepbystep` / `test` : valeurs **de test**, assumées et documentées |
| Séparation des tests | Surefire = `*Test` (sans base) ; Failsafe = `*IT` (avec base) |
| Doc | `docs/architecture/data-model.md`, en anglais, avec diagramme Mermaid |

Le détail est dans le `## Journal des décisions` du tutoriel 001.
