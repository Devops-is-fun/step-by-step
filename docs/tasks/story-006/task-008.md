# Tutoriel 008 — Story #006 : Commits de la story 004, de la story 005 et des docs

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 007
**Prérequis :** 4 commits dans l'historique
**Exigence TP couverte :** `EXG-REND-01`
**Fichier principal :** l'historique Git

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Enregistrer les stories 004 (Nexus) et 005 (agent), puis toute la documentation de méthode
(spécification, stories, tutoriels, skills, énoncé). À la fin, `git status` est **vide**.

---

## 🪜 Les étapes

### Étape 1 : Commit de la story 004

**Où :** la racine du projet

**Action :**

> ```bash
> git add scripts/check-nexus.sh docs/tooling/nexus.md
> git status --short | grep -v '^??'
> git commit -m "ci(nexus): add nexus check script and documentation"
> ```

**Résultat attendu :** 2 lignes `A ` ; puis `OK  no secret found…` et `2 files changed`.

---

### Étape 2 : Commit de la story 005

**Où :** la racine du projet

**Action :**

> ```bash
> git add jenkins-agent/Dockerfile scripts/check-agents.sh docs/tooling/jenkins-agent.md
> git status --short | grep -v '^??'
> git commit -m "build(jenkins): add dedicated build agent and forbid controller executors"
> ```

**Résultat attendu :** 3 lignes `A ` ; puis `OK  no secret found…` et `3 files changed`.

---

### Étape 3 : Commit des docs de méthode

**Où :** la racine du projet

**Action :**

> ```bash
> git add docs/Projet_CICD_Jenkins.pdf docs/specification docs/stories docs/tasks .opencode/skills
> git status --short | grep -v '^??' | wc -l
> git status --short | grep -E 'node_modules|package|\.idea' || echo "OK : no stray file"
> git commit -m "docs: add specification, user stories and tutorials"
> ```

**Résultat attendu :** une centaine de fichiers dans le carton, `OK : no stray file`, puis
`OK  no secret found…`.

**Pourquoi `.opencode/skills` et pas `.opencode` ?** `node_modules` (62 Mo) n'a rien à faire dans un
dépôt. `.opencode/.gitignore` l'ignore déjà, mais ce fichier s'ignore **lui-même** (il contient la
ligne `.gitignore`) : viser `.opencode/skills` évite toute ambiguïté.

**Pourquoi versionner les skills ?** Ils montrent **comment** tu as travaillé (spec → stories →
tutoriels) : un vrai plus à présenter à la soutenance.

---

### Étape 4 : Le carton est vide, le dossier est propre

**Où :** la racine du projet

**Action :**

> ```bash
> git status --short
> git log --oneline | wc -l
> ```

**Résultat attendu :** **rien** pour `git status`, puis `7`.

**Pourquoi `git status` vide est la preuve ?** Tout fichier du projet est soit **enregistré**, soit
**ignoré volontairement** : il ne reste aucun fichier « oublié » qui pourrait contenir un secret ou un
réglage de ta machine.

**✅ Vérifie que :**
- [ ] `git status --short` n'affiche rien
- [ ] 7 commits

---

## 🧹 Comment tout annuler

Le dernier commit seulement : `git reset --soft HEAD~1`. **Tant que rien n'est poussé.**

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `git status` montre encore un `??` à l'étape 4 | Lis le nom : un fichier du projet oublié → `git add` + commit ; un fichier personnel → ajoute-le au `.gitignore` (et commite le `.gitignore`) |
| `The following paths are ignored by one of your .gitignore files` | Tu as visé un chemin ignoré (`.opencode` en entier ?) : reprends la commande exacte de l'étape 3 |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Historique complet | `git log --oneline \| wc -l` | `7` |
| Dossier propre | `git status --short` | vide |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tout le jalon 0 est enregistré, story par story. 🎉

Prochain tutoriel : **#009** — Créer le dépôt GitHub privé et pousser ⏱️ 20 min

Pause méritée ☕
