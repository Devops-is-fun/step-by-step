# Story 023 — Automatiser les releases avec jreleaser

- **Jalon :** 3 — N1b — Releases automatisées
- **Exigences couvertes :** `EXG-N1b-01` à `EXG-N1b-06` (module N1b)
- **Dépendances :** stories 006, 014

## Description

En tant que mainteneur, je veux que la version, le changelog, le tag Git et les notes de version soient
calculés automatiquement à partir de mes messages de commit, afin qu'une release soit reproductible et
que le tag publié corresponde toujours à un artefact publié.

## Critères d'acceptation

- Les messages de commit suivent les **Conventional Commits**, en anglais
  (`feat(scope): ...`, `fix(scope): ...`, `chore: ...`) ; un commit mal formé est rejeté ou signalé.
- `jreleaser` calcule la version sémantique depuis les commits depuis le dernier tag :
  - `fix:` → patch, `feat:` → minor, `BREAKING CHANGE:` → major.
- Le changelog est généré et versionné dans le dépôt (`CHANGELOG.md`), groupé par type.
- Un **tag Git** `vX.Y.Z` est créé et poussé.
- Les notes de version sont publiées et rattachées à la release GitHub.
- L'artefact de la release est publié sous cette version (et non sous une version `-SNAPSHOT`).
- Le schéma des notes est customize pour le TP (catégories lisibles, lien vers les tickets).

## Preuves attendues

| Preuve | Where |
|--------|------- |
| Logs jreleaser : version calculée | console du stage `Release` |
| `CHANGELOG.md` mis à jour | diff du commit de release |
| Tag `v0.1.0` | `git tag` + page GitHub Releases |
| Notes de version | GitHub Releases |
| Artefact sous `0.1.0` | Nexus |

## Automatisation des tests

La table de correspondance Conventional Commits → incrément est le test. Recette sur une branche
jetable :

| Messages depuis le dernier tag | Version attendue |
|---|---|
| `chore: update readme` | inchangée (pas de release) |
| `fix(task): correct step order` | patch |
| `feat(app): add daily view` | minor |
| `feat(app): x` + `BREAKING CHANGE: ...` | major |

Les 4 cas sont consignés dans la story ; c'est la preuve que `EXG-N1b-02` fonctionne.

## Documentation

`CHANGELOG.md`, `.jreleaser.yml` commenté, section « Releases » du `README.md` : commande manuelle
`./mvnw -B jreleaser:release` en secours.

## Fichiers à versionner

- `.jreleaser.yml`
- `CHANGELOG.md`
- `Jenkinsfile` (stage `Release`)
- `README.md`

## Fichiers à ne jamais versionner

`.env` (les variables `GITHUB_TOKEN` et `JRELEASER_GITHUB_TOKEN` viennent des credentials)

## Risques

| Risque | Parade |
|--------|--------|
| Push du tag impossible avec la credential | Token GitHub à portée de tag dans la credential |
| Release déclenchée sur un commit non conforme | Stage déclenché par un tag annoté, donc état stable |
| Version divergente entre POM et Nexus | Une seule source : le tag |

## Critères de Done

- [ ] Version sémantique calculée correctement (4 cas testés)
- [ ] Changelog et tag générés
- [ ] Notes de version publiées
- [ ] Artefact publié sous la version de la release

## Message de commit

`ci(release): automate version changelog and git tags with jreleaser`
