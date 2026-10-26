# Story 014 — Publier un artefact versionné au hash du commit

- **Jalon :** 1 — Intégration continue
- **Exigences couvertes :** `EXG-COM-12`, `EXG-COM-13`
- **Dépendances :** stories 004, 011

## Description

En tant que responsable CI/CD, je veux que chaque build produise un JAR et une image Docker tagués par
le hash du commit et publiés dans Nexus, afin de pouvoir déployer **exactement** ce qui a été validé.

## Critères d'acceptation

- La version de l'artefact suit `<versionSemver>-<sha-court>` (exemple `0.1.0-a1b2c3d`).
- Le SHA vient de `scm vars` de Jenkins (checkout avec `lightweight checkout` ou
  `GIT_COMMIT.take(7)`), jamais d'une saisie manuelle.
- Le JAR est publié dans le dépôt Maven `maven-releases` de Nexus via `deploy-file`, avec la credential
  `nexus-deploy-creds`.
- L'image Docker est construite et taguée avec le **même** SHA, puis poussée dans un registre
  accessible aux stacks staging et production (registre local Nexus ou registry du compose).
- Une page de détails Nexus est créée pour l'artefact et affiche le SHA dans la version.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| `step-by-step-0.1.0-a1b2c3d.jar` listé | Nexus → `maven-releases` |
| Page de détails de l'artefact | Nexus (bouton *Component details*) |
| `docker images` avec tag SHA | terminal |
| Identité du SHA dans le log | console du stage |

## Automatisation des tests

`scripts/check-artifact.sh` : après build, compare le SHA du tag de l'image Docker et la version de
l'artefact Nexus. Le script échoue s'ils diffèrent — l'invariant `EXG-COM-12` est donc testé.

## Documentation

`docs/architecture/artefacts.md` : convention de nommage, chaîne Git → version, où retrouver l'artefact.

## Fichiers à versionner

- `Jenkinsfile` (stages `Package` et `Publish`)
- `scripts/build-image.sh`
- `scripts/check-artifact.sh`
- `docs/architecture/artefacts.md`

## Fichiers à ne jamais versionner

`settings.xml` local, credentials Nexus, `*.tar` d'image exportés

## Risques

| Risque | Parade |
|--------|--------|
| Republier la même version refusée par Nexus | Version unique par commit ; en cas de rejeu, bumper la version |
| Registre d'images indisponible | Nexus `docker-hosted` en alternative, ou registry locale du compose |
| `scm` indisponible | `GIT_COMMIT` issu du checkout, avec repli documenté |

## Critères de Done

- [ ] Version suffixée par le SHA court
- [ ] JAR publié et lisible dans Nexus
- [ ] Image Docker taguée identiquement
- [ ] `check-artifact.sh` passe

## Message de commit

`ci(pipeline): publish commit hashed jar and image to nexus`
