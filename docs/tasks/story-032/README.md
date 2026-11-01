# Story 032 — Accueillir Morgan dans le binôme

Découpage TDA-friendly en **7 tutoriels**, en **deux parties**, à faire **dans l'ordre**.

- **Exigences TP couvertes :** `EXG-REND-01`, `EXG-REND-02` (part), `EXG-REND-06`, `EXG-COM-07`
- **Fichiers produits :** `CONTRIBUTING.md` (anglais), `docs/team/onboarding.md` (français) ; une règle
  de protection de `main` sur GitHub ; la première PR de Morgan
- **Durée totale :** ⏱️ 45 min pour toi, ⏱️ 50 min pour Morgan
- **Prérequis de la story :** story 010 terminée (PR #1 mergée, `git stash pop` fait)

> **Répertoire de travail de tous les tutoriels :** la racine du projet, le dossier qui contient
> `docs/`.

## L'image de la story

Morgan entre dans l'atelier. Avant de confier les clés, on affiche le **règlement intérieur**
(`CONTRIBUTING.md`), on organise la **visite des lieux** (`onboarding.md`), et on pose une
**serrure** sur la porte de `main` : on n'y entre plus que par une PR vérifiée par Jenkins.

## Partie 1 — toi

| # | Titre | Temps | Difficulté | Dépend de |
|---|-------|-------|------------|-----------|
| [001](task-001.md) | Écrire `CONTRIBUTING.md` sur une branche | ⏱️ 15 | ⭐⭐ | story 010 |
| [002](task-002.md) | Écrire la visite guidée `docs/team/onboarding.md` | ⏱️ 10 | ⭐ | 001 |
| [003](task-003.md) | Commiter, ouvrir la PR, merger | ⏱️ 10 | ⭐⭐ | 002 |
| [004](task-004.md) | Protéger `main` et prévenir Morgan | ⏱️ 10 | ⭐⭐ | 003 |

## Partie 2 — Morgan (à transmettre)

| # | Titre | Temps | Difficulté | Dépend de |
|---|-------|-------|------------|-----------|
| [005](task-005.md) | Cloner le dépôt et activer le vigile | ⏱️ 10 | ⭐ | 004 |
| [006](task-006.md) | Lancer l'application sur sa machine | ⏱️ 20 | ⭐⭐ | 005 |
| [007](task-007.md) | La première PR de Morgan, et le bilan de la story | ⏱️ 20 | ⭐⭐ | 006 |

Le tutoriel 007 se fait **à deux** : ta machine (Jenkins) doit être allumée, et tu relis la PR de
Morgan.

## Jalons

| Jalon | Tutoriels | Preuve |
|-------|-----------|--------|
| **J1 — Le règlement** | 001 à 003 | `CONTRIBUTING.md` et `onboarding.md` sur `main`, via une PR ✅ |
| **J2 — La serrure** | 004 | un `git push` direct sur `main` est **refusé** |
| **J3 — Morgan opérationnel** | 005 à 007 | première PR de Morgan : vigile `OK`, statut ✅, relue par toi, mergée |

## Décisions déjà prises

| Sujet | Décision |
|-------|----------|
| Protection de `main` | PR obligatoire + statut `continuous-integration/jenkins/pr-merge` ✅ obligatoire ; relecture par l'autre membre **attendue** mais pas bloquante |
| Stack de Morgan | Docker + l'application seulement (base de test, build, image) ; la relance complète de l'outillage viendra avec la story 025 |
| Visite guidée | en français, `docs/team/onboarding.md` |
| Rédaction | contenu exact fourni par Claude, collé et relu par toi |

Le détail est dans le `## Journal des décisions` du tutoriel 001.
