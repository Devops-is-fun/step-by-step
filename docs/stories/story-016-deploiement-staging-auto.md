# Story 016 — Déployer automatiquement en staging depuis `main`

- **Jalon :** 2 — Livraison continue
- **Exigences couvertes :** `EXG-COM-15`
- **Dépendances :** stories 011, 015

## Description

En tant que développeur, je veux que mon push sur `main` déploie immédiatement en staging, sans
intervention manuelle, afin de voir la version en conditions réelles.

## Critères d'acceptation

- Le stage `Deploy Staging` s'exécute **uniquement** si la branche courante est `main`
  (`when { branch 'main' }`).
- Un push sur une branche `feature/*` construit et teste mais **ne déploie pas** : la preuve est
  l'absence du stage dans le build.
- Le déploiement utilise l'artefact produit par ce **même** build (pas une reconstruction).
- Le tag de l'image déployée apparaît dans le log du stage.
- Après déploiement, `/actuator/info` de staging expose la même version que l'artefact Nexus.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Stage sur push `main` | vue build |
| Stage absent sur push `feature/*` | vue build |
| Tag dans le log | console |
| `actuator/info` staging | `curl localhost:8082/actuator/info` |

## Automatisation des tests

Recette en deux temps, les deux résultats étant consignés dans la story :

```bash
git switch -c feature/test-no-deploy && git commit --allow-empty -m "test: skip deploy" && git push
git switch main && git commit --allow-empty -m "chore: trigger staging deploy" && git push
```

Le premier push ne doit produire aucun déploiement ; le second doit mettre staging à jour.

## Documentation

`docs/architecture/environnements.md`, section « Comportement par branche » (complète le tableau
`specification.md` § 5.4).

## Fichiers à versionner

- `Jenkinsfile` (stage `Deploy Staging` + condition de branche)
- `docs/architecture/environnements.md`

## Fichiers à ne jamais versionner

rien de spécifique

## Risques

| Risque | Parade |
|--------|--------|
| `when { branch }` non pris en compte sur un tag | Utiliser `when { branch 'main' }` (pas `anyOf`) |
| Déploiement malgré un build partiel | `post { success }` sur le stage |
| Tests lancés deux fois (locale + agent) | Un seul poste de build documenté |

## Critères de Done

- [ ] Push `main` ⇒ staging mis à jour sans intervention
- [ ] Push `feature/*` ⇒ aucun déploiement
- [ ] Version servie = version de l'artefact du build

## Message de commit

`ci(pipeline): deploy to staging automatically on main`
