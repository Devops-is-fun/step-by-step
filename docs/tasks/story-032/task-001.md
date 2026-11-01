# Tutoriel 001 — Story #032 : Écrire `CONTRIBUTING.md` sur une branche

**Pour :** toi
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** story 010 terminée
**Prérequis :** sur `main`, à jour ; `git stash pop` fait (les docs « binôme » sont revenues)
**Exigence TP couverte :** `EXG-REND-01`, `EXG-REND-06`
**Fichier principal :** `CONTRIBUTING.md` (racine du projet)

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Écrire le **règlement intérieur** du dépôt, sur une branche : à partir de maintenant, toi aussi tu
passes par une PR.

## 📋 Ce que tu vas apprendre

- Ce qu'est un `CONTRIBUTING.md` et pourquoi GitHub le met en avant
- Écrire des règles d'équipe **vérifiables**

---

## 🪜 Les étapes

### Étape 1 : Vérifie ton point de départ

**Où :** la racine du projet

**Action :**

> ```bash
> git branch --show-current
> git status --short
> ```

**Résultat attendu :** `main`, puis les fichiers modifiés pour le binôme :

```
 M .opencode/skills/skills/specification-fr/SKILL.md
 M .opencode/skills/skills/story-en/SKILL.md
 M .opencode/skills/skills/story-fr/SKILL.md
 M .opencode/skills/skills/tasks-fr/SKILL.md
 M docs/specification/specification.md
 M docs/stories/plan-implementation.md
 M docs/stories/story-010-multibranch-webhook.md
 M docs/stories/story-011-jenkinsfile-ci.md
 M docs/stories/story-025-readme.md
 M docs/stories/story-026-rapport-technique.md
 M docs/stories/story-027-soutenance.md
?? docs/stories/story-032-accueil-binome.md
?? docs/tasks/story-032/
```

(Si tu ne vois pas les ` M` : `git stash list`. S'il reste `docs binome`, lance `git stash pop`.)

---

### Étape 2 : Crée la branche

**Où :** la racine du projet

**Action :**

> ```bash
> git switch -c docs-team-onboarding
> ```

**Résultat attendu :** `Switched to a new branch 'docs-team-onboarding'`. Tes modifications non
commitées te suivent sur la branche.

---

### Étape 3 : Crée `CONTRIBUTING.md`

**Où :** la racine du projet (à côté de `README.md`), dans IntelliJ

**Action :**

> Crée `CONTRIBUTING.md` avec ce contenu **exact** :
>
> ````markdown
> # Contributing to Step by Step
>
> Step by Step is built by a team of two. This guide is the team agreement: read it before your
> first commit.
>
> | Member | Stories | Main area |
> |--------|---------|-----------|
> | Géraldine | 001-014, 016, 018-022 | Jenkins: pipeline, quality, artefacts, secrets, Shared Library |
> | Morgan | 015, 017, 023, 024, 025 | delivery: staging, blue/green, releases, DORA, README |
> | both | 026, 027 | technical report, demo video, reviews |
>
> The detailed split is in `docs/specification/specification.md`, section « Organisation du binôme ».
> New here? Start with the guided tour: `docs/team/onboarding.md`.
>
> ## 1. First setup
>
> ```bash
> git clone git@github.com:GeraldineRouy/step-by-step.git
> cd step-by-step
> git config core.hooksPath .githooks        # enables the secret scan before each commit
> git config user.name "First Last"          # this repository only
> git config user.email "you@school.example"
> ```
>
> Check: `git config core.hooksPath` prints `.githooks`.
>
> ## 2. Workflow
>
> `main` is protected: nobody pushes to it directly.
>
> 1. `git switch main && git pull`
> 2. `git switch -c <type>-<topic>`, for example `feat-staging-stack` (no `/`: Jenkins encodes it in URLs)
> 3. Small commits following Conventional Commits (`docs/tooling/git-conventions.md`)
> 4. `git push -u origin <branch>`, then open a pull request on GitHub
> 5. Wait for the Jenkins status `continuous-integration/jenkins/pr-merge` ✅ (required to merge)
> 6. The other member reviews the PR (expected for every PR, even when not enforced)
> 7. **Merge pull request**, then **Delete branch**
>
> Jenkins runs on one member's machine. No status after a few minutes? Ask for the stack to be
> started; never work around the check.
>
> ## 3. Secrets
>
> - Secrets live **only** in Jenkins credentials and in the password manager: never in a file, not
>   even an ignored `.env`.
> - Never paste a token, a password or the smee.io URL into ChatGPT, Claude or a chat.
> - Load a secret in a terminal with `read -rs NAME && export NAME`, then `unset NAME`.
> - Leaked a secret? Revoke it and create a new one first, then clean up.
> - The pre-commit hook and the **Secrets** stage of the pipeline scan every change
>   (`scripts/check-no-secrets.sh`).
>
> ## 4. Using AI assistants
>
> The course allows tools outside its scope **if you can explain them**, and each member must be able
> to explain the whole chain.
>
> | Category | Files | AI generation |
> |----------|-------|---------------|
> | Application code | `src/`, tests, application dependencies in `pom.xml` | ✅ generate directly, then check: tests green, quick review |
> | CI/CD | `Jenkinsfile`, Shared Library, `scripts/`, Compose files, `Dockerfile`, nginx | 🟠 guided: understand every line you commit; the other member reviews the PR |
> | Docs and report | `docs/tooling/`, README, report | 🟡 a draft is fine, rewrite it in your own words |
> | Secrets | tokens, passwords, smee URL, credentials | ❌ never |
>
> ## 5. Project skills
>
> The method skills (specification, stories, tutorials, feedback) are versioned in `.opencode/skills/skills/`:
> - **opencode** loads them directly;
> - **Claude Code**: ask it to read `.opencode/skills/skills/<skill>/SKILL.md` and follow it (for example `tasks-fr` with the story number);
> - **ChatGPT**: create a Project and attach the `SKILL.md`, the specification and the story as files.
>
> ## 6. Definition of done for a PR
>
> - [ ] Jenkins status ✅
> - [ ] Reviewed by the other member
> - [ ] Docs updated when behaviour changes (`docs/tooling/`, README)
> - [ ] No secret in the diff
> ````
>
> **Enregistre.** Si IntelliJ demande **« Add file to Git? »** : **Cancel**.

**Pourquoi en anglais ?** C'est la langue de tous les fichiers « outils » du dépôt (`docs/tooling/`,
`git-conventions.md`, messages de commit). La visite guidée, elle, sera en français.

**Pourquoi ce nom de fichier ?** GitHub affiche un lien vers `CONTRIBUTING.md` quand quelqu'un ouvre
une PR ou une *issue* : la règle est sous les yeux au bon moment.

**✅ Vérifie que :**
- [ ] Le tableau de l'équipe contient Géraldine et Morgan
- [ ] L'aperçu Markdown montre 6 sections numérotées

---

## 🧹 Comment tout annuler

```bash
rm CONTRIBUTING.md
git switch main
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `git switch -c` refuse (`would be overwritten`) | Copie-moi le message exact |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Branche | `git branch --show-current` | `docs-team-onboarding` |
| Fichier | `ls CONTRIBUTING.md` | présent |

---

## Journal des décisions

Réponses de cadrage `tasks-fr 032` (01/11/2026) : **1a, 2a, 3a, 4a**.

| # | Question | Réponse | Conséquence |
|---|----------|---------|-------------|
| 1 | Exigences avant un merge sur `main` | **a** — PR + statut Jenkins ✅ obligatoires ; relecture attendue, pas bloquante | personne n'est bloqué si l'autre est indisponible |
| 2 | Stack du binôme | **a** — Docker + application | tutoriel 006 en 20 min ; la relance complète de l'outillage teste le README (story 025) |
| 3 | Langue de la visite guidée | **a** — français | `docs/team/onboarding.md` |
| 4 | Rédaction | **a** — contenu fourni par Claude | tutoriels 001 et 002 |

---

## 🎉 Bravo !

Le règlement intérieur est écrit. 🎉

Prochain tutoriel : **#002** — Écrire la visite guidée ⏱️ 10 min
