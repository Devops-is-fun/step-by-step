# Tutoriel 006 — Story #006 : Le premier commit

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 005
**Prérequis :** tutoriels 001 à 005 terminés
**Exigence TP couverte :** `EXG-REND-01`
**Fichier principal :** l'historique Git

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Faire **le premier commit du projet** : les règles d'hygiène et le vigile, avant tout le reste.

## 📋 Ce que tu vas apprendre

- Vérifier le contenu du carton **avant** de le fermer
- Voir le vigile travailler sur un vrai commit

---

## 🪜 Les étapes

### Étape 1 : Remplis le carton, fichier par fichier

**Où :** la racine du projet

**Action :**

> ```bash
> git add .gitignore scripts/check-no-secrets.sh .githooks/pre-commit docs/tooling/git-conventions.md
> ```

**Pourquoi ces 4 fichiers d'abord ?** On installe le **vigile avant d'ouvrir la porte** : tous les
commits suivants seront fouillés.

---

### Étape 2 : Regarde le carton avant de le fermer

**Où :** la racine du projet

**Action :**

> ```bash
> git status --short | grep -v '^??'
> ```

**Résultat attendu :** exactement 4 lignes, toutes en `A ` :

```
A  .githooks/pre-commit
A  .gitignore
A  docs/tooling/git-conventions.md
A  scripts/check-no-secrets.sh
```

**✅ Vérifie que :**
- [ ] 4 lignes, pas une de plus

---

### Étape 3 : Commite

**Où :** la racine du projet

**Action :**

> ```bash
> git commit -m "chore(repo): initialise version control hygiene and secret scan"
> ```

**Résultat attendu :** d'abord le vigile, `OK  no secret found in the files to commit`, puis
`[main (root-commit) xxxxxxx] chore(repo): …` et `4 files changed`.

**Pourquoi `root-commit` ?** C'est le tout premier commit du dépôt : il n'a pas de parent. 🎉

---

### Étape 4 : Vérifie l'historique

**Où :** la racine du projet

**Action :**

> ```bash
> git log --oneline
> git log -1 --format='%an <%ae>'
> ```

**Résultat attendu :** une ligne de commit, puis **ton identité d'école** (tutoriel 001).

**✅ Vérifie que :**
- [ ] L'auteur n'est **pas** ton adresse Vates

---

## 🧹 Comment tout annuler

```bash
git update-ref -d HEAD
```

(Annule le premier commit ; les fichiers restent dans l'index. **Seulement** tant qu'il n'est pas
poussé sur GitHub.)

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Le vigile répond `KO` | Lis la ligne signalée : si c'est une vraie valeur, retire-la ; sinon, note-la dans `retours.md` et montre-la-moi |
| Mauvais auteur à l'étape 4 | Tutoriel 001 étape 2, puis `git commit --amend --reset-author --no-edit` |
| Plus de 4 lignes à l'étape 2 | `git rm --cached <fichier en trop>` (avant le premier commit, `git restore --staged` échoue : pas encore de `HEAD`) |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Premier commit | `git log --oneline` | 1 ligne `chore(repo): …` |
| Auteur | `git log -1 --format='%an <%ae>'` | identité d'école |

---

## 🎉 Bravo !

**Premier commit du projet !** 🎉 Le registre est ouvert, et le vigile est à la porte.

Prochain tutoriel : **#007** — Commits des stories 001 à 003 ⏱️ 20 min

Pause méritée ☕
