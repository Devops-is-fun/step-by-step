# Tutoriel 005 — Story #009 : Commiter, pousser, bilan

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 004
**Prérequis :** base de test éteinte
**Exigence TP couverte :** `EXG-REND-01`
**Fichier principal :** l'historique Git

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Enregistrer la fin de la story en **2 commits** et pousser.

---

## 🪜 Les étapes

### Étape 1 : Commit de la correction du `pom.xml`

**Où :** la racine du projet

**Action :**

> ```bash
> git add pom.xml
> git status --short | grep '^[AMD]'
> git commit -m "fix(build): remove duplicated failsafe plugin declaration"
> ```

**Résultat attendu :** une ligne `M  pom.xml` ; puis `OK  no secret found…` et `1 file changed`.

**Et le code des règles ?** Il est **déjà** dans l'historique : il est parti avec le commit
`0498e35`, le 29/10 (voir `## Note` en bas de ce tutoriel).

---

### Étape 2 : Commit des tutoriels de la story

**Où :** la racine du projet

**Action :**

> ```bash
> git add docs/tasks/story-009 docs/specification
> git status --short | grep '^[AMD]'
> git commit -m "docs(app): add story 009 tutorials"
> ```

**Résultat attendu :** les 6 fichiers de `docs/tasks/story-009/` en `A ` (et `M  docs/specification/retours.md`
s'il a changé) ; puis `OK  no secret found…`.

---

### Étape 3 : Pousse

**Où :** la racine du projet

**Action :**

> ```bash
> git push
> git status
> ```

**Résultat attendu :** `main -> main`, `Your branch is up to date with 'origin/main'.`,
`nothing to commit, working tree clean`.

---

## ✅ Bilan de la story 009

| Critère d'acceptation | Preuve | Tutoriel |
|-----------------------|--------|----------|
| `REG-01` : hors 1–30 refusé, message « redécoupe » | `shouldRejectStepLongerThan30Minutes`, `shouldRejectStepOfZeroMinutes` | 001, 002 |
| `REG-02` : date occupée refusée | `shouldRejectSecondUnfinishedTaskOnSameDate`, `shouldRejectPlanningOnOccupiedDate` | 001, 002 |
| Règles dans le **service** | `TaskService` | 001 |
| Positif **et** négatif, frontières 30/31, date vide, date occupée, tâche terminée | 12 tests de `TaskServiceTest` | 002 |
| Sans base ni contexte Spring (Mockito) | `./mvnw -B test` sans Docker | 001 |

### 🛡️ Garde-fous du TP

- [ ] Le vigile a dit `OK` aux 2 commits
- [ ] `./mvnw -B validate` n'affiche plus `duplicate declaration`
- [ ] Jeton SonarQube oublié (`unset`)
- [ ] Base de test éteinte ; `git status` vide

---

## 🎉 Bravo !

Tu as terminé la story 009 ! 🎉 Les deux règles qui font le différenciateur TDA de Step by Step sont
écrites, prouvées, et passées au contrôle qualité.

**Le socle applicatif est en place** (stories 007 à 009). La suite : le **pipeline Jenkins**
lui-même. Prochaine
story : 010 — Multibranch et webhook (`/ tasks-fr 010`).

Pause méritée ☕

---

## Note

Le 29/10, la story 008 a été refaite par erreur après la génération de la story 009 : le commit
`0498e35` (message de la story 008) contient en réalité le **code de la story 009**, et le plugin
Failsafe s'est retrouvé déclaré deux fois dans le `pom.xml`. L'historique déjà poussé n'est **pas
réécrit** : on corrige vers l'avant (étape 1).
