# Story 018 — Valider manuellement la mise en production depuis le même artefact

- **Jalon :** 2 — Livraison continue
- **Exigences couvertes :** `EXG-COM-16`, `EXG-COM-17`
- **Dépendances :** stories 014, 016, 017

## Description

En tant que responsable de production, je veux qu'une validation manuelle explicite soit exigée avant
la mise en production, et que la production reçoive **exactement** l'artefact déjà validé en staging.

## Critères d'acceptation

- Le stage `Deploy Production` utilise `input { id, message, ok, submitter, submitterParameter }`.
- Seul un compte explicitement listé dans `submitter` peut valider ; un autre compte connecté ne voit
  pas le bouton de validation et ne peut pas approuver.
- Le paramètre de soumission (`submitterParameter`) est **journalisé** dans les logs du build :
  l'identité de qui a validé est prouvée.
- La production récupère l'image **par son tag** dans le registre/artefact ; **aucune reconstruction**
  n'a lieu : le digest déployé en production est **identique** à celui déployé en staging.
- Un refus (`Abort`) stoppe le pipeline sans toucher à la production.

## Preuves attendues

| Preuve | Where |
|--------|------- |
| Build en attente de validation | vue build, bouton visible |
| Compte non autorisé sans bouton | observation d'une session avec un autre compte |
| `submitterParameter` journalisé | console du stage |
| Digests staging == production | `docker inspect --format '{{index .Config.Image}}'` + digest registre |
| Version servie | `/actuator/info` via `localhost:8088` |

## Automatisation des tests

```bash
# identité des artefacts, à exécuter juste après déploiement
docker inspect -f '{{.Config.Image}}' step-by-step-staging   # staging
docker inspect -f '{{.Config.Image}}' step-by-step-blue       # production
# attendu : même tag, et même digest côté registre
```

Le script `scripts/check-same-artifact.sh` automate cette comparaison et est appelé par le pipeline
après bascule.

## Documentation

`docs/architecture/blue-green.md`, section « Passage en production » ; préciser que `main` reste
libre de merge : la validation est une barrière humaine, pas une branche protégée.

## Fichiers à versionner

- `Jenkinsfile` (stage `Deploy Production`)
- `scripts/check-same-artifact.sh`
- `docs/architecture/blue-green.md`

## Fichiers à ne jamais versionner

rien de spécifique

## Risques

| Risque | Parade |
|--------|--------|
| Jenkins exposé par le tunnel ⇒ validation par un tiers | Restreindre l'accès au tunnel + authentification Jenkins |
| Timeout de l'input | `timeout` explicite, avec conséquence documentée |
| Rebuild silencieux en production | Vérification automatique du digest |

## Critères de Done

- [ ] `input` avec `submitter` présent et restrictif
- [ ] Identité du validateur journalisée
- [ ] Artefact de production identique à celui de staging, prouvé par script
- [ ] Refus ⇒ aucune modification de la production

## Message de commit

`ci(pipeline): gate production with manual input and submitter check`
