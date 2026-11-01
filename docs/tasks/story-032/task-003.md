# Tutoriel 003 — Story #032 : Commiter, ouvrir la PR, merger

**Pour :** toi
**Temps estimé :** ⏱️ 10 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** stack `cicd` et relais démarrés
**Exigence TP couverte :** `EXG-COM-07`, `EXG-REND-01`
**Fichier principal :** la PR #2

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Faire entrer le règlement, la visite guidée et toutes les mises à jour « binôme » dans `main`… par
une PR, comme le règlement l'exige déjà.

---

## 🪜 Les étapes

### Étape 1 : Commit des docs « binôme »

**Où :** la racine du projet

**Action :**

> ```bash
> git add docs/specification docs/stories .opencode/skills
> git status --short | grep '^[AMD]'
> git commit -m "docs(team): switch to a two-person team and a recorded demo"
> ```

**Résultat attendu :** une dizaine de lignes `M ` et `A  docs/stories/story-032-accueil-binome.md` ;
puis `OK  no secret found…`.

---

### Étape 2 : Commit du règlement et de la visite

**Où :** la racine du projet

**Action :**

> ```bash
> git add CONTRIBUTING.md docs/team/onboarding.md docs/tasks/story-032
> git status --short | grep '^[AMD]'
> git commit -m "docs(team): add contributing guide and onboarding tour"
> ```

**Résultat attendu :** `A  CONTRIBUTING.md`, `A  docs/team/onboarding.md`, les 8 fichiers de
`docs/tasks/story-032/` ; puis `OK  no secret found…`. `git status --short` est ensuite **vide**.

---

### Étape 3 : Pousse et ouvre la PR

**Où :** la racine du projet, puis GitHub

**Action :**

> ```bash
> git push -u origin docs-team-onboarding
> ```
>
> Puis sur GitHub : **Compare & pull request** → titre
> `docs(team): add contributing guide and onboarding tour` → **Create pull request**.

**Résultat attendu :** la PR #2. Après ≈ 30 s, en bas de la PR :
`continuous-integration/jenkins/pr-merge — This commit looks good` ✅.

---

### Étape 4 : Merge et retour sur `main`

**Où :** la PR, puis la racine du projet

**Action :**

> Sur GitHub : **Merge pull request → Confirm merge → Delete branch**. Puis :
>
> ```bash
> git switch main
> git pull
> git branch -d docs-team-onboarding
> ```

**Résultat attendu :** `Fast-forward`, `Deleted branch docs-team-onboarding`.

**✅ Vérifie que :**
- [ ] `git status` : `up to date with 'origin/main'`, rien à commiter

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Pas de statut sur la PR après 2 minutes | `docker logs cicd-webhook-relay \| tail -3` ; puis Jenkins → `step-by-step` → **Scan Repository Now** |
| Statut ❌ | Ouvre la console de `PR-2` dans Jenkins et montre-moi l'erreur |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Où | Attendu |
|--------------|----|---------|
| PR #2 | GitHub | **Merged**, statut ✅ |
| `CONTRIBUTING.md` sur `main` | page d'accueil du dépôt | un onglet ou lien **Contributing** à côté du README |

---

## 🎉 Bravo !

Le règlement est officiel. 🎉 **Jalon J1 atteint.**

Prochain tutoriel : **#004** — Protéger `main` et prévenir Morgan ⏱️ 10 min
