# Tutoriel 009 — Story #007 : Commiter, pousser et faire le bilan

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriels 004, 007 et 008
**Prérequis :** tous les tutoriels précédents terminés
**Exigence TP couverte :** `EXG-REND-01`
**Fichier principal :** l'historique Git

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Enregistrer la story en **3 commits**, chacun avec ses seuls fichiers, puis pousser.

---

## 🪜 Les étapes

### Étape 1 : Commit du vigile, **d'abord**

**Où :** la racine du projet

**Action :**

> ```bash
> git add scripts/check-no-secrets.sh
> git status --short | grep -v '^??'
> git commit -m "fix(repo): skip the maven wrapper script in the secret assignment scan"
> ```

**Résultat attendu :** dans la liste, `M  scripts/check-no-secrets.sh` (d'autres lignes ` M` pour
`docs/…` peuvent apparaître, **sans** espace avant le `M` : elles ne sont pas dans le carton) ; puis
`OK  no secret found…` et `1 file changed`.

**Pourquoi d'abord ?** Le vigile **corrigé** doit être en place quand `mvnw` passe la porte (étape 2).

---

### Étape 2 : Commit de l'application

**Où :** la racine du projet

**Action :**

> ```bash
> git add pom.xml mvnw mvnw.cmd .mvn/wrapper/maven-wrapper.properties .gitattributes src Dockerfile .dockerignore README.md README.en.md
> git status --short | grep '^[AM]'
> git commit -m "build(app): bootstrap spring boot project with maven wrapper and dockerfile"
> ```

**Résultat attendu :** 12 lignes : `M  README.md`, `M  README.en.md` et 10 `A ` (dont les 3 fichiers
de `src/`) ; puis `OK  no secret found…` et `12 files changed`.

**Un avertissement `CRLF will be replaced by LF` (ou l'inverse) sur `mvnw.cmd` ?** Normal : c'est
`.gitattributes` qui range les fins de ligne. Rien à faire.

**Pourquoi pas `target/` ?** Il est ignoré : on versionne la **recette**, jamais le **plat cuisiné**.

---

### Étape 3 : Commit des docs de la story

**Où :** la racine du projet

**Action :**

> ```bash
> git add docs/tasks/story-007 docs/stories/story-007-squelette-spring-boot.md docs/specification/specification.md .opencode/skills
> git status --short | grep '^[AM]'
> git commit -m "docs(app): add story 007 tutorials and record spring boot decisions"
> ```

**Résultat attendu :** les 10 fichiers de `docs/tasks/story-007/` en `A `, quelques `M ` ; puis
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

**✅ Vérifie que :**
- [ ] `working tree clean`

---

## ✅ Bilan de la story 007

| Critère d'acceptation | Preuve | Tutoriel |
|-----------------------|--------|----------|
| `./mvnw -B verify` < 2 min | `BUILD SUCCESS`, `Total time` | 001 |
| `maven.compiler.release` = 17 | `major version: 61` | 001 |
| JAR `step-by-step-0.1.0-SNAPSHOT.jar` | `ls target/*.jar` | 001 |
| `/actuator/health` `UP` en local | `curl localhost:8090/…` | 002 |
| Image multi-étapes qui répond `UP` | `docker run` + `curl` | 006 |
| README « Step by Step », artefact `step-by-step` | `README.md`, `pom.xml` | 008 |
| `sonar.projectKey` + `sonar.host.url` dans le `pom.xml` | `help:evaluate` | 003 |
| Première analyse, `ncloc > 0` | API `measures` | 004 |

### 🛡️ Garde-fous du TP

- [ ] Aucun secret versionné : le vigile a dit `OK` aux 3 commits
- [ ] Aucun build sur le controller (pas de build Jenkins dans cette story)
- [ ] `git status` vide

---

## 🎉 Bravo !

Tu as terminé la story 007 ! 🎉 Step by Step **existe**, passe au contrôle qualité, et sait voyager
dans une image Docker. Le jalon 1 (socle applicatif) est lancé.

**Prochaine story :** Story 008 — Modèle et PostgreSQL de test (`/ tasks-fr 008`).

Pause méritée ☕
