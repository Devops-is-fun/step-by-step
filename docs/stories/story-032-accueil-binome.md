# Story 032 — Accueillir le binôme (Morgan)

- **Jalon :** 1 — Intégration continue (à faire juste après la story 010)
- **Exigences couvertes :** `EXG-REND-01`, `EXG-REND-02` (part), `EXG-REND-06`, `EXG-COM-07`
- **Dépendances :** story 010
- **Qui :** Géraldine (dépôt, GitHub, `CONTRIBUTING.md`, visite guidée) puis Morgan (clone, relance de l'application, première PR)

## Description

En tant que nouveau membre du binôme, je veux savoir comment contribuer sans casser la chaîne
(branches, PR, vigile des secrets, usage de l'IA) et pouvoir relancer toute la stack sur ma machine,
afin de prendre mes stories (015, 017, 023, 024, 025) en autonomie.

## Critères d'acceptation

- `CONTRIBUTING.md` (anglais, à la racine) décrit :
  - le flux **branche → PR → relecture par l'autre membre → merge**, plus aucun push direct sur `main` ;
  - l'installation du vigile après un clone : `git config core.hooksPath .githooks` ;
  - les règles de secrets (Bitwarden + credentials Jenkins seulement, jamais dans un fichier ni dans
    une IA) ;
  - le tableau d'usage de l'IA par catégorie de fichiers (spécification, « Organisation du binôme ») ;
  - le lien vers `docs/tooling/git-conventions.md` (Conventional Commits).
- Sur GitHub, `main` est **protégée** : PR obligatoire, **1 approbation**, statut
  `continuous-integration/jenkins/branch` ou `…/pr-merge` ✅ exigé, pas de *force push*.
- Morgan a cloné le dépôt, installé le hook, et sa **première PR** (ses notes du premier lancement
  dans `docs/team/onboarding.md`) a été relue, construite par Jenkins (✅) et mergée.
- Le binôme a relancé **sur sa machine** l'outillage (`docker compose up -d`) et l'application (README) :
  les écarts rencontrés sont notés pour la story 025.
- Une **visite guidée** (`docs/team/onboarding.md`, français) explique en une page ce qui existe
  déjà : outillage, agent, vigile, relais smee, job Multibranch, application, et où trouver chaque doc.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Protection de `main` | GitHub → Settings → Branches → règle `main` |
| Push direct refusé | `git push` sur `main` → `protected branch` |
| Première PR du binôme | PR mergée, statut ✅, 1 approbation |
| Hook installé chez le binôme | `git config core.hooksPath` → `.githooks` |

## Automatisation des tests

Le stage **Secrets** de la story 011 relance le vigile dans Jenkins : un commit qui contourne le
hook local est quand même arrêté.

## Documentation

- `CONTRIBUTING.md` (anglais)
- `docs/team/onboarding.md` (français : visite guidée pour le binôme)

## Fichiers à versionner

- `CONTRIBUTING.md`
- `docs/team/onboarding.md`

## Fichiers à ne jamais versionner

Rien de spécifique ; aucun secret partagé par fichier ou par messagerie (Bitwarden : partage d'entrée
si besoin).

## Risques

| Risque | Parade |
|--------|--------|
| Jenkins éteint (machine de l'étudiante) → PR du binôme sans statut | Le binôme lance sa propre stack pour tester ; le statut arrive au rallumage (scan) |
| Hook non installé chez le binôme | Stage Secrets dans Jenkins (story 011) |
| Secret collé dans ChatGPT ou Claude | Règle écrite dans `CONTRIBUTING.md` ; en cas de fuite : révoquer et recréer le secret |
| Protection de branche indisponible (dépôt **privé** sur un compte GitHub Free) | Vérifier si le compte d'école a GitHub Pro (Student Developer Pack) ; sinon, la règle « PR + relecture » reste une convention écrite dans `CONTRIBUTING.md`, et le statut Jenkins reste visible sur chaque PR |
| Les deux membres modifient le même fichier | PR petites et fréquentes ; `git pull` avant de créer une branche |

## Critères de Done

- [ ] `CONTRIBUTING.md` et `docs/team/onboarding.md` mergés
- [ ] `main` protégée
- [ ] Première PR du binôme mergée avec ✅ et approbation
- [ ] Stack relancée chez le binôme

## Message de commit

`docs(team): add contributing guide and onboarding tour`

## Constat (01/11/2026)

Le ruleset `protect-main` est créé, mais GitHub ne l'applique pas : « Your rulesets won't be enforced
on this private repository until you move to GitHub Team organization account ». En attendant GitHub
Pro (demande du *GitHub Student Developer Pack*), la règle « branche + PR + statut ✅ » est une
**convention** écrite dans `CONTRIBUTING.md`. Le test du push refusé (tutoriel 004, étapes 2-3) sera
fait à l'activation. Écart à citer dans le rapport technique (story 026, section 7).
