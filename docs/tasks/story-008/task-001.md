# Tutoriel 001 — Story #008 : Découvrir le modèle et lancer les tests unitaires

**Qui :** 🤖 Claude a implémenté — tu découvres et tu vérifies
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐
**Dépend de :** story 007 terminée
**Prérequis :** aucun (pas besoin de Docker dans ce tutoriel)
**Exigence TP couverte :** support applicatif ; prépare `EXG-COM-09`
**Fichier principal :** `src/main/java/com/esgi/stepbystep/domain/`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Repérer ce que Claude a ajouté, et constater que les tests **unitaires** passent **sans aucune
base de données**.

## 📋 Ce que tu vas apprendre

- La différence entre un test **unitaire** et un test **d'intégration**
- Ce qu'est une **migration** Flyway
- Pourquoi `./mvnw -B verify` aura bientôt besoin d'une base, mais pas `./mvnw -B test`

---

## 🪜 Les étapes

### Étape 1 : Fais le tour des changements

**Où :** la racine du projet

**Action :**

> ```bash
> git status --short
> ```

**Résultat attendu :** ces lignes (l'ordre peut varier) :

```
 M docs/specification/specification.md
 M docs/stories/story-008-modele-et-postgres-test.md
 M docs/stories/story-030-migrations-flyway.md
 M docs/tasks/story-007/task-009.md
 M pom.xml
 M src/main/resources/application.yml
 D src/test/java/com/esgi/stepbystep/StepByStepApplicationTests.java
?? docs/architecture/
?? docs/tasks/story-008/
?? src/main/java/com/esgi/stepbystep/domain/
?? src/main/resources/db/
?? src/test/java/com/esgi/stepbystep/StepByStepApplicationIT.java
?? src/test/java/com/esgi/stepbystep/domain/
?? src/test/resources/
```

| Fichier | Rôle |
|---------|------|
| `domain/Task.java`, `TaskStep.java`, `NotificationLog.java` | les 3 **entités** : une tâche, ses sous-étapes, les traces de rappel |
| `domain/TaskStatus.java`, `NotificationChannel.java` | les valeurs permises (`TODO`/`IN_PROGRESS`/`DONE`, `DISCORD`/`IN_APP`) |
| `domain/TaskRepository.java`, `NotificationLogRepository.java` | les **repositories** : lire et écrire en base sans écrire de SQL |
| `db/migration/V1__create_task_model.sql` | la **migration** Flyway : le SQL qui crée les tables |
| `application.yml` | Hibernate **vérifie** le schéma (`validate`) au lieu de le créer |
| `src/test/resources/application-test.yml` | où les tests d'intégration trouvent la base de test |
| `TaskValidationTest` | test **unitaire** des contraintes (titre, durée, libellé) |
| `TaskRepositoryIT`, `StepByStepApplicationIT` | tests **d'intégration** : ils ont besoin de PostgreSQL |
| `docs/architecture/data-model.md` | le diagramme des 3 entités |
| `docs/tasks/story-007/task-009.md` | une correction du tutoriel de la story 007 |
| `docs/specification/…`, `docs/stories/…` | les décisions de cette story, reportées dans la spec et les stories 008 et 030 |

**Pourquoi `StepByStepApplicationTests` a disparu ?** Il démarrait toute l'application. Maintenant
que l'application a besoin d'une base, ce test est devenu un test d'intégration : il a été renommé
`StepByStepApplicationIT`.

---

### Étape 2 : Lis la migration

**Où :** `src/main/resources/db/migration/V1__create_task_model.sql`, dans IntelliJ

**Action :**

> Ouvre le fichier et repère les 3 `CREATE TABLE` et la ligne `CREATE UNIQUE INDEX`.

**Résultat attendu :** les tables `task`, `task_step`, `notification_log` ; un index qui interdit deux
tâches **non terminées** le même jour (`REG-02`).

**Pourquoi Flyway ?** Une migration est une **marche d'escalier** numérotée (`V1`, puis `V2`…).
Flyway note dans la base les marches déjà montées (table `flyway_schema_history`) et ne monte que
les nouvelles. Ta base de test, staging et production auront **exactement** le même schéma.

---

### Étape 3 : Lance les tests unitaires, sans Docker

**Où :** la racine du projet

**Action :**

> ```bash
> ./mvnw -B test
> ```

**Résultat attendu :** après quelques téléchargements :

```
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: … -- in com.esgi.stepbystep.domain.TaskValidationTest
…
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Pourquoi les `*IT` ne tournent pas ?** `./mvnw test` lance **Surefire**, qui ne prend que les
classes finissant par `Test` ou `Tests`. Les classes `*IT` attendent **Failsafe**, que tu brancheras
au tutoriel 004. Résultat : un développeur sans Docker peut toujours lancer les tests unitaires.

**✅ Vérifie que :**
- [ ] `Tests run: 9, Failures: 0`
- [ ] Aucun conteneur PostgreSQL n'a été démarré (`docker ps` n'en montre pas)

---

## 🧹 Comment tout annuler

Rien à annuler : ce tutoriel ne fait que lire et tester.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `BUILD FAILURE` dans `TaskValidationTest` | Copie-moi les 30 dernières lignes : c'est mon code, je corrige |
| IntelliJ souligne `jakarta.persistence` en rouge | Clique **Load Maven Changes** (l'icône Maven en haut à droite de l'éditeur du `pom.xml`) |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Tests unitaires | `./mvnw -B test` | `Tests run: 9, Failures: 0` |
| Sans base | `docker ps` | pas de conteneur `postgres` |

---

## Journal des décisions

Réponses de cadrage `tasks-fr 008` (29/10/2026) : **1a, 2a, 3a, 4a, 5a**.

| # | Question | Réponse | Conséquence |
|---|----------|---------|-------------|
| 1 | Qui crée les tables | **a** — Flyway + `ddl-auto=validate` | `V1__create_task_model.sql` ; la story 030 se réduit à l'expand/contract |
| 2 | Auteur de `compose-test.yml` | **a** — toi, guidée | tutoriels 002-003 |
| 3 | Séparation unitaires / intégration | **a** — toi, guidée | tutoriel 004 |
| 4 | Accès à PostgreSQL | **a** — 5432 dans le conteneur, `127.0.0.1:5433` sur le poste | l'accès depuis l'agent Jenkins sera traité en story 012 |
| 5 | Doc du modèle | **a** — Claude, en anglais | `docs/architecture/data-model.md` |

Décisions prises à la génération (vérifiées sur un PostgreSQL jetable) :

| Sujet | Décision | Raison |
|-------|----------|--------|
| Nom du projet Compose | `name: stepbystep-test` dans le fichier | sans lui, Compose prendrait le nom du dossier (`resources`) ; le réseau s'appelle `stepbystep-test_default` (utile pour l'image Docker et en story 012) |
| Données | `tmpfs` (en mémoire) | base jetable : rien ne reste sur le disque après `down`, pas de volume à oublier |
| Identifiants | `stepbystep` / `test` | valeur de test (risque assumé de la story) ; 4 caractères, donc ignorée par le vigile, qui cherche des valeurs de 8 caractères ou plus |
| L'application exige une base | la datasource vient de l'environnement (`SPRING_DATASOURCE_*`) | chaque stack (test, staging, prod) fournit la sienne ; le README l'explique (tutoriel 006) |

---

## 🎉 Bravo !

Step by Step a une **mémoire**. 🎉 **Jalon J1 atteint.**

Prochain tutoriel : **#002** — Écrire `compose-test.yml` ⏱️ 20 min
