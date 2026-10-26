# Tutoriel 009 — Story #006 : Créer le dépôt GitHub privé et pousser

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 008
**Prérequis :** 7 commits ; connectée à ton compte GitHub de l'école sur github.com
**Exigence TP couverte :** `EXG-REND-01`
**Fichier principal :** `https://github.com`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Créer un dépôt GitHub **privé** et vide, le brancher comme `origin` en SSH, et y pousser ton
historique.

## 📋 Ce que tu vas apprendre

- Utiliser un alias de `~/.ssh/config` dans une adresse Git
- Pourquoi le dépôt distant doit être **vide** à la création
- `git push -u` : pousser **et** relier la branche

---

## 🪜 Les étapes

### Étape 1 : Vérifie que GitHub reconnaît ta clé d'école

**Où :** la racine du projet

**Action :**

> ```bash
> ssh -T github
> ```

**Résultat attendu :** `Hi <ton-compte>! You've successfully authenticated, but GitHub does not provide shell access.`
(Si une phrase de passe est demandée, c'est celle de ta clé `id_esgi`.)

**Pourquoi `github` et pas `github.com` ?** `github` est l'**alias** de ton `~/.ssh/config` : il
dit « va sur `github.com`, avec l'utilisateur `git` et la clé `~/.ssh/id_esgi` ». Ta clé d'école est
choisie automatiquement.

**✅ Vérifie que :**
- [ ] `<ton-compte>` est bien ton compte **d'école**

---

### Étape 2 : Crée le dépôt sur GitHub

**Où :** `https://github.com/new`

**Action :**

> | Champ | Valeur |
> |-------|--------|
> | Owner | ton compte d'école |
> | Repository name | `step-by-step` |
> | Description | `CI/CD project (ESGI M1): Jenkins, SonarQube, Nexus` |
> | Visibility | **Private** |
> | Add a README file | ☐ **décoché** |
> | Add .gitignore | `None` |
> | Choose a license | `None` |
>
> Clique **Create repository**.

**Pourquoi tout décocher ?** Un README créé par GitHub serait un **commit** que ton dépôt local n'a
pas : le premier `push` serait refusé (historiques différents). Un dépôt vide accepte ton historique
tel quel.

**Pourquoi privé ?** C'est un risque de la story : un dépôt public exposerait le moindre oubli. Tu
pourras inviter ton professeur.

---

### Étape 3 : Branche `origin`

**Où :** la racine du projet

**Action :**

> Remplace `<ton-compte>` par ton nom de compte GitHub :
>
> ```bash
> git remote add origin github:<ton-compte>/step-by-step.git
> git remote -v
> ```

**Résultat attendu :** deux lignes `origin  github:<ton-compte>/step-by-step.git (fetch)` et
`(push)`.

---

### Étape 4 : Pousse

**Où :** la racine du projet

**Action :**

> ```bash
> git push -u origin main
> ```

**Résultat attendu :** des lignes `Writing objects: 100%`, puis
`* [new branch]      main -> main` et `branch 'main' set up to track 'origin/main'`.

**Pourquoi `-u` ?** Il relie ta branche `main` à `origin/main` : ensuite, un simple `git push` ou
`git status` sait à quoi se comparer.

---

### Étape 5 : Vérifie sur GitHub

**Où :** `https://github.com/<ton-compte>/step-by-step`

**Action :**

> Rafraîchis la page du dépôt.

**Résultat attendu :** le badge **Private**, tes dossiers (`docs`, `jenkins-agent`, `scripts`…), ton
README affiché en bas, et **7 commits**.

**✅ Vérifie que :**
- [ ] Badge **Private**
- [ ] Pas de dossier `.idea` ni `node_modules`

---

## 🧹 Comment tout annuler

1. `git remote remove origin`
2. Sur GitHub : **Settings** du dépôt → **Delete this repository** (tout en bas).

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `Permission denied (publickey)` à l'étape 1 | La clé publique `~/.ssh/id_esgi.pub` n'est pas dans **Settings → SSH and GPG keys** de ton compte d'école |
| `Hi <autre-compte>!` | La mauvaise clé répond : vérifie `IdentityFile ~/.ssh/id_esgi` dans `~/.ssh/config` |
| `! [rejected] main -> main (fetch first)` | Le dépôt GitHub n'était pas vide (README coché) : supprime-le et recrée-le **vide** |
| `error: remote origin already exists` | `git remote set-url origin github:<ton-compte>/step-by-step.git` |
| `Repository not found` | Faute dans `<ton-compte>` ou dans le nom du dépôt : `git remote -v` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande / Où | Attendu |
|--------------|---------------|---------|
| Distant configuré | `git remote -v` | `origin` en `github:…` |
| Branche reliée | `git status` | `Your branch is up to date with 'origin/main'.` |
| Dépôt privé | github.com | badge **Private** |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Ton travail est maintenant **sauvegardé hors de ta machine** : un disque
qui lâche ne te ferait plus rien perdre. 🎉

Prochain tutoriel : **#010** — Faire le bilan ⏱️ 15 min

Pause méritée ☕
