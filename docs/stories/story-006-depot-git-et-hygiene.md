# Story 006 — Initialiser le dépôt Git et poser l'hygiène de versionnement

- **Jalon :** 0 — Outillage
- **Exigences couvertes :** `EXG-REND-01`, `EXG-COM-19` (part)
- **Dépendances :** aucune

## Description

En tant que responsable de projet, je veux un dépôt Git propre, sans secret et sans fichier machine,
afin que le pipeline puisse construire ce dépôt et que l'inventaire des secrets soit crédible.

> **Coordination avec la story 001 :** le `.gitignore` est déjà créé au tutoriel 010 de la story 001
> avec les entrées Docker (`.env`, volumes, données des services). Il ne reste ici qu'à **compléter**
> ce fichier avec les entrées IDE et Git, puis à faire le premier commit.

## Critères d'acceptation

- Le dépôt est initialisé, branche `main`, premier commit existant.
- Le dépôt distant GitHub est créé et configuré comme `origin`.
- `.gitignore` couvre `.env` et ses variantes, `target/`, `.idea/`, `*.iml`, `*.key`, `*.pem`,
  `id_rsa*`, `id_ed25519*`, `secrets/`, `volumes/`, `*.log`, `/data/` (racine seulement : un package
  Java `…/data/` reste versionnable).
- `git status` ne liste aucun fichier de configuration locale machine.
- `git status` **et** `git log` ne contiennent aucun secret : c'est une vérification de routine, pas
  un one-off.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Historique | `git log --oneline` |
| État propre | `git status` vide |
| Distants | `git remote -v` |
| `.gitignore` effectif | `git check-ignore -v .env target/` |

## Automatisation des tests

`scripts/check-no-secrets.sh`, lancé **automatiquement avant chaque commit** par le hook versionné
`.githooks/pre-commit` (`git config core.hooksPath .githooks`). Il fouille l'**index**
(`git grep --cached`) avec deux filets :

1. **formes connues**, dans tous les fichiers : clé privée, jetons SonarQube, Jenkins et GitHub ;
2. **valeur longue (≥ 8 caractères) affectée à un nom de secret**, hors Markdown (la prose des docs
   parle de mots de passe sans en contenir).

Le script échoue (et bloque le commit) si l'un des filets trouve quelque chose, ou si `git grep`
lui-même échoue.

## Documentation

`docs/tooling/git-conventions.md` : règles de nommage de branche (`feature/*`), messages
Conventional Commits en anglais.

## Fichiers à versionner

- `.gitignore`
- `README.md` (squelette)
- `docs/tooling/git-conventions.md`
- `scripts/check-no-secrets.sh`
- `.githooks/pre-commit`

## Fichiers à ne jamais versionner

`.env`, `.idea/`, `target/`, `*.pem`, `*.key`, `id_rsa*`

## Risques

| Risque | Parade |
|--------|--------|
| Fichiers `.idea/` déjà staged (IDE) | `git rm --cached -r .idea` puis `.gitignore` |
| Secret commité puis supprimé | Réécrire l'historique (`git filter-repo`) et changer le secret |
| Depot public par accident | Créer le dépôt distant en **privé** |

## Décisions (`tasks-fr 006`, 26/10/2026)

| # | Sujet | Décision |
|---|-------|----------|
| 1 | Identité | `1a` — nom + e-mail du compte GitHub de l'école, en config **locale** du dépôt |
| 2 | Distant | `2a` — GitHub **privé** `step-by-step`, SSH via l'alias `github` (clé `id_esgi`) |
| 3 | Contenu | `3a` — `.opencode/skills/` et le PDF de l'énoncé versionnés ; `node_modules` exclu |
| 4 | Historique | `4a` — 7 commits : hygiène, puis un par story 001 → 005, puis les docs |
| 5 | Scan | `5a` — hook versionné `.githooks/pre-commit` |

## Critères de Done

- [ ] Dépôt initialisé avec un commit sur `main`, `origin` privé configuré
- [ ] `.gitignore` complété (entrées IDE, plus celles de Docker déjà posées en story 001)
- [ ] `git status` ne liste aucun fichier de configuration locale machine
- [ ] `check-no-secrets.sh` passe

## Message de commit

`chore(repo): initialise project repository and version control hygiene`
