# Tutoriel 010 — Story #006 : Faire le bilan

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐
**Dépend de :** Tutoriel 009
**Prérequis :** dépôt poussé sur GitHub
**Exigence TP couverte :** `EXG-REND-01`, `EXG-COM-19` (part)
**Fichier principal :** l'historique Git

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Rassembler les preuves de la story, et vérifier que **l'historique entier** ne contient aucun secret.

---

## 🪜 Les étapes

### Étape 1 : Les preuves de la story

**Où :** la racine du projet

**Action :**

> ```bash
> git log --oneline
> git status
> git remote -v
> git check-ignore -v .env target/
> ```

**Résultat attendu :** 7 commits ; `nothing to commit, working tree clean` ;
`origin  github:<ton-compte>/step-by-step.git` ; les deux règles de `.gitignore`.

---

### Étape 2 : Fouille tout l'historique

**Où :** la racine du projet

**Action :**

> ```bash
> git log -p | grep -cE -e '-----BEGIN [A-Z ]*PRIVATE KEY-----$|sq[apu]_[0-9a-f]{40}|\b11[0-9a-f]{32}\b|gh[pousr]_[A-Za-z0-9]{36}'
> ```

**Résultat attendu :** `0`

**Pourquoi en plus du vigile ?** Le vigile fouille chaque **nouveau** commit. Cette commande relit
**tous** les commits passés (`git log -p` affiche chaque modification) avec le filet « forme ». C'est
la vérification « `git log` ne contient aucun secret » de la story. Résultat vérifié à la génération,
sur une copie de ton projet.

---

### Étape 3 : Le vigile, à la main

**Où :** la racine du projet

**Action :**

> ```bash
> ./scripts/check-no-secrets.sh; echo "exit code: $?"
> ```

**Résultat attendu :** `OK  no secret found in the files to commit`, `exit code: 0`. Le carton est
vide, mais le vigile fouille aussi tout ce qui est **déjà** suivi par Git.

---

## ✅ Avant de passer à la story suivante — bilan de la story 006

| Critère d'acceptation | Preuve |
|-----------------------|--------|
| Branche `main`, premier commit | `git log --oneline` : 7 commits |
| `origin` GitHub **privé** | `git remote -v` ; badge **Private** |
| `.gitignore` complet | `git check-ignore -v` (tutoriel 002 : 11 règles prouvées) |
| Aucun fichier machine suivi | `.idea/`, `*.iml` retirés (tutoriel 001) ; `git status` vide |
| Aucun secret dans l'index **ni** l'historique | vigile `OK` ; `git log -p \| grep -c …` = `0` |
| `check-no-secrets.sh` passe, et bloque quand il le doit | tutoriel 004 |

### 🛡️ Garde-fous du TP

- [ ] Aucun secret dans un fichier versionné
- [ ] Aucun build ne s'est exécuté sur le controller
- [ ] `git status` vide

### À partir de maintenant

Chaque story se termine par **un commit** (vérifié par le vigile) puis un `git push`. Tes tutoriels
précédents disaient « aucun commit avant la story 006 » : cette règle est **levée**. 🎉

---

## 🎉 Bravo !

Tu as terminé la story 006, **et tout le jalon 0 — Outillage** ! 🎉🎉 Ton outillage CI/CD est
configuré, vérifiable en une commande par outil, documenté en anglais, et sauvegardé sur GitHub.

**Prochaine story :** Story 007 — Squelette Spring Boot (le début de l'application !). Ses tutoriels
seront générés avec `/ tasks-fr 007`.

Pause méritée ☕
