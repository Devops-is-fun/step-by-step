# Story 006 — Initialiser le dépôt Git et poser l'hygiène de versionnement

Découpage TDA-friendly en **10 tutoriels**, à faire **dans l'ordre**.

- **Exigences TP couvertes :** `EXG-REND-01`, `EXG-COM-19` (part)
- **Fichiers produits :** `.gitignore` (complété), `scripts/check-no-secrets.sh`, `.githooks/pre-commit`,
  `docs/tooling/git-conventions.md` ; **7 commits** ; un dépôt GitHub privé
- **Durée totale :** ⏱️ 2 h 50
- **Prérequis de la story :** story 005 terminée ; accès à ton compte GitHub de l'école

> **Répertoire de travail de tous les tutoriels :** la racine du projet, le dossier qui contient
> `docs/`.

## L'image de la story

Jusqu'ici, ton travail vivait dans des fichiers **non suivis**. On ouvre maintenant le **registre**
(Git) : chaque story y entre avec une ligne datée et signée (un commit). Avant d'ouvrir le registre,
on installe un **vigile à la porte** (le hook) qui fouille chaque entrée à la recherche d'un secret.

## Ordre des tutoriels

| # | Titre | Temps | Difficulté | Dépend de |
|---|-------|-------|------------|-----------|
| [001](task-001.md) | Régler ton identité Git et nettoyer l'index | ⏱️ 15 | ⭐⭐ | story 005 |
| [002](task-002.md) | Compléter le `.gitignore` | ⏱️ 15 | ⭐⭐ | 001 |
| [003](task-003.md) | Écrire `scripts/check-no-secrets.sh` | ⏱️ 20 | ⭐⭐⭐ | 002 |
| [004](task-004.md) | Brancher le vigile (hook) et le tester | ⏱️ 15 | ⭐⭐ | 003 |
| [005](task-005.md) | Écrire `docs/tooling/git-conventions.md` | ⏱️ 15 | ⭐⭐ | 004 |
| [006](task-006.md) | Le premier commit | ⏱️ 15 | ⭐⭐ | 005 |
| [007](task-007.md) | Commits des stories 001 à 003 | ⏱️ 20 | ⭐⭐ | 006 |
| [008](task-008.md) | Commits de la story 004, de la story 005 et des docs | ⏱️ 20 | ⭐⭐ | 007 |
| [009](task-009.md) | Créer le dépôt GitHub privé et pousser | ⏱️ 20 | ⭐⭐⭐ | 008 |
| [010](task-010.md) | Faire le bilan | ⏱️ 15 | ⭐ | 009 |

## Jalons

| Jalon | Tutoriels | Preuve que tu y es |
|-------|-----------|--------------------|
| **J1 — Dépôt prêt** | 001, 002 | identité locale ; `.idea/` hors de l'index ; `git check-ignore` |
| **J2 — Vigile en place** | 003, 004 | un faux secret **bloque** un commit |
| **J3 — Historique** | 005 à 008 | `git log --oneline` : 7 commits Conventional Commits |
| **J4 — Sur GitHub** | 009 | `git remote -v` ; dépôt **privé** visible sur github.com |
| **J5 — Story terminée** | 010 | `git status` vide ; aucun secret dans l'historique |

## Décisions déjà prises

| Sujet | Décision |
|-------|----------|
| Identité Git | nom + e-mail de ton compte GitHub de l'école, **pour ce dépôt seulement** |
| Dépôt distant | GitHub **privé**, en SSH via ton alias `github` (clé `id_esgi`) |
| Contenu | tout le projet + `.opencode/skills/` (ta méthode) + le PDF de l'énoncé ; **pas** `node_modules` |
| Historique | un commit par story (001 → 005), plus un pour l'hygiène et un pour les docs |
| Scan des secrets | hook Git versionné `.githooks/pre-commit` (`core.hooksPath`) |
| Langue | messages de commit, script et `git-conventions.md` en anglais |

Le détail est dans le `## Journal des décisions` du tutoriel 001.

## Après la story

Le **jalon 0 — Outillage** est terminé. À partir de maintenant, chaque story se termine par un commit
(vérifié par le vigile).
