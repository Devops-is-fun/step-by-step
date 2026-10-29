# Tutoriel 007 — Story #008 : Éteindre la base, commiter, pousser, bilan

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 006
**Prérequis :** tous les tutoriels précédents terminés
**Exigence TP couverte :** `EXG-REND-01`
**Fichier principal :** l'historique Git

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Éteindre le banc d'essai, puis enregistrer la story en **2 commits** et pousser.

---

## 🪜 Les étapes

### Étape 1 : Éteins la base de test

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose -f src/test/resources/compose-test.yml down
> docker ps -a --filter name=stepbystep-test -q | wc -l
> ```

**Résultat attendu :** `Container stepbystep-test-postgres-1  Removed`,
`Network stepbystep-test_default  Removed`, puis `0`.

**Pourquoi `down` sans `-v` ?** Il n'y a **aucun volume** : les données étaient en mémoire (`tmpfs`).
Et tu gardes ton réflexe : jamais de `-v` par habitude.

---

### Étape 2 : Commit du modèle et du banc d'essai

**Où :** la racine du projet

**Action :**

> ```bash
> git add pom.xml src docs/architecture README.md README.en.md
> git status --short | grep '^[AMD]'
> git commit -m "feat(app): add task domain model and postgres test stack"
> ```

**Résultat attendu :** 19 lignes (dont `D  src/test/java/com/esgi/stepbystep/StepByStepApplicationTests.java`,
`A  src/test/resources/compose-test.yml`, `A  docs/architecture/data-model.md`), puis
`OK  no secret found…` et `19 files changed`.

**Pourquoi `git add src` suffit pour la suppression ?** `git add <dossier>` enregistre **tout** ce qui
a changé dedans : ajouts, modifications **et** suppressions.

---

### Étape 3 : Commit des docs de la story

**Où :** la racine du projet

**Action :**

> ```bash
> git add docs/tasks/story-007/task-009.md docs/tasks/story-008 docs/stories docs/specification
> git status --short | grep '^[AMD]'
> git commit -m "docs(app): add story 008 tutorials and record data model decisions"
> ```

**Résultat attendu :** les 8 fichiers de `docs/tasks/story-008/` en `A `, quelques `M ` ; puis
`OK  no secret found…`.

---

### Étape 4 : Pousse

**Où :** la racine du projet

**Action :**

> ```bash
> git push
> git status
> ```

**Résultat attendu :** `main -> main`, puis `Your branch is up to date with 'origin/main'.` et
`nothing to commit, working tree clean`.

---

## ✅ Bilan de la story 008

| Critère d'acceptation | Preuve | Tutoriel |
|-----------------------|--------|----------|
| 3 entités JPA avec leurs contraintes | `TaskValidationTest` (9 tests) + contraintes SQL | 001, 005 |
| `plannedFor` et `reminderAt` facultatifs | `TaskRepositoryIT` (`reminderAt` nul relu) | 005 |
| `compose-test.yml` : PostgreSQL 16, 5433, `stepbystep_test`, healthcheck | `up -d --wait` → `healthy` | 002, 003 |
| Test Failsafe : persiste et relit une tâche avec ses sous-étapes | `TaskRepositoryIT`, 5 tests | 005 |
| Tests unitaires sans base | `./mvnw -B test` sans Docker | 001, 004 |

### 🛡️ Garde-fous du TP

- [ ] Le vigile a dit `OK` aux 2 commits
- [ ] Seul secret « en clair » : le mot de passe **de test** `test`, documenté
- [ ] Base de test éteinte ; `git status` vide

---

## 🎉 Bravo !

Tu as terminé la story 008 ! 🎉 Step by Step a une mémoire **testée contre une vraie base**, et tu as
construit le banc d'essai que Jenkins allumera lui-même en story 012.

**Prochaine story :** Story 009 — Règles métier (`/ tasks-fr 009`).

Pause méritée ☕
