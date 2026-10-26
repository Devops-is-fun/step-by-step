# Story 020 — Extraire les étapes communes dans une Shared Library versionnée

- **Jalon :** 3 — Modules et fiabilité
- **Exigences couvertes :** `EXG-COM-18`
- **Dépendances :** stories 011, 014, 016

## Décision structurante

La Shared Library vit dans un **dépôt Git séparé** (`cicd-shared-lib`), choix retenu pour respecter
`EXG-COM-18` (« versionnée par tag ») sans mélanger le code de pipeline dans l'application.

## Description

En tant que responsable CI/CD, je veux que les étapes communes (checkout, build, publication,
déploiement, notification) vivent dans une bibliothèque partagée et versionnée par tag, afin de ne pas
dupliquer ces étapes entre Jenkinsfiles et de pouvoir faire évoluer la chaîne sans toucher à
l'application.

## Critères d'acceptation

- Le dépôt `cicd-shared-lib` existe sur GitHub, avec la structure `vars/` (+ `src/` et `resources/`
  seulement si le cours les a vus) : `vars/buildApp.groovy`, `vars/publishArtifact.groovy`,
  `vars/deployEnv.groovy`, `vars/notifyDiscord.groovy`.
- Une **Global Pipeline Library** Jenkins est configurée sur ce dépôt.
- Le tag `v1.0.0` existe et est celui utilisé par le Jenkinsfile :
  `@Library('cicd-shared-lib@v1.0.0')`.
- Une modification de la bibliothèque **sans nouveau tag** ne change pas le comportement du build
  courant — invariant vérifié.
- Chaque étape de la bibliothèque est **idempotente** et accepte des paramètres explicites (pas de
  variable globale implicite).

## Preuves attendues

| Preuve | Where |
|--------|------- |
| `Loading library cicd-shared-lib@v1.0.0` | console du build |
| Tag `v1.0.0` sur GitHub | page des tags |
| Jenkinsfile ramené à ~30 lignes | diff du fichier |
| Invariant de version respecté | 2 builds consécutifs identiques après modif non taguée |

## Automatisation des tests

Recette de l'invariant de version, consignée dans la story :

```bash
# 1. modifier vars/buildApp.groovy SANS taguer  -> build : comportement inchangé
# 2. git tag v1.0.1 && @Library('cicd-shared-lib@v1.0.1') -> build : nouveau comportement
```

## Documentation

`cicd-shared-lib/README.md` : liste des étapes disponibles, paramètres, politique de versionnement
(semver + tag), règles de contribution.

## Fichiers à versionner

Dépôt `cicd-shared-lib` : `vars/*.groovy`, `README.md`, `.gitignore`
Dépôt application : `Jenkinsfile` (import + appel d'étapes)

## Fichiers à ne jamais versionner

`cicd-shared-lib/.m2/`, artefacts de la lib

## Risques

| Risque | Parade |
|--------|--------|
| Syntaxe Groovy de pipeline fragile | Écriture d'étapes simples, testées une par une |
| Bibliothèque non « versionnée » en pratique | Toujours un tag sémantique dans l'annotation |
| Duplication residualle entre lib et Jenkinsfile | Revue : aucun `sh` métier dans le Jenkinsfile |

## Critères de Done

- [ ] Dépôt séparé + Global Pipeline Library configurée
- [ ] Import par tag `@v1.0.0`
- [ ] Invariant « pas de tag ⇒ pas de changement » vérifié
- [ ] Jenkinsfile recentré sur l'orchestration

## Message de commit

`ci(library): move common pipeline steps into versioned shared library`
