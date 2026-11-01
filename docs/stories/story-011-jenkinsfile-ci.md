# Story 011 — Écrire le Jenkinsfile déclaratif : lint et tests unitaires

- **Jalon :** 1 — Intégration continue
- **Exigences couvertes :** `EXG-COM-05`, `EXG-COM-08`, `EXG-COM-09`
- **Dépendances :** stories 005, 007, 009

## Description

En tant que responsable CI/CD, je veux un `Jenkinsfile` déclaratif versionné à la racine du dépôt qui
exécute le lint et les tests unitaires sur l'agent, afin de matérialiser le Pipeline as Code exigé et
d'obtenir un rapport JUnit par build.

## Critères d'acceptation

- `Jenkinsfile` à la racine du dépôt, en syntaxe **déclarative** (`pipeline { agent { } stages { } }`).
- Le pipeline déclare explicitement `agent { label 'docker-agent' }` ; aucun stage ne s'exécute sur le
  controller (`EXG-COM-04`).
- Le pipeline appelle `scripts/check-agents.sh` (story 005) avec une credential Jenkins dédiée
  (jeton d'API d'un compte en lecture) : l'invariant `EXG-COM-04` est testé à chaque build.
- Stage **Secrets** : `./scripts/check-no-secrets.sh` sur le code récupéré, échec si un secret est
  trouvé. C'est le filet qui ne dépend pas du hook local (qu'un membre du binôme pourrait ne pas avoir
  installé).
- Stage **Lint** : `./mvnw -B checkstyle:check spotbugs:check`, échec si violations.
- Stage **Unit Tests** : `./mvnw -B test`.
- Stage **Report** : `junit` comme `post { always }` + `archiveArtifacts` des rapports XML.
- Le stage de tests affiche l'icône de test dans Jenkins.
- Un commit contenant une violation Checkstyle **fait échouer** le build (preuve pour la story 027).

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Stage names | vue **Pipeline** du build |
| `Running on docker-agent` | console |
| Rapport de tests | onglet **Tests** du build |
| Échec sur violation | build rouge, log Checkstyle |

## Automatisation des tests

Le pipeline est lui-même le test. Vérification complémentaire :
`./mvnw -B checkstyle:check spotbugs:check test` doit passer en local — le stage reproduit donc la
commande exacte, sans divergence local/CI.

## Documentation

`docs/architecture/pipeline-ci.md` : rôle de chaque stage, ordre d'exécution, code de sortie attendu.

## Fichiers à versionner

- `Jenkinsfile`
- `config/checkstyle/checkstyle.xml`
- `pom.xml` (plugins `checkstyle`, `spotbugs`)

## Fichiers à ne jamais versionner

`target/`, rapports de build locaux

## Risques

| Risque | Parade |
|--------|--------|
| `maven-integration` étape ignorée, Maven local absent | Utiliser `./mvnw` partout |
| Checkstyle trop strict, build rouge en boucle | Partir d'un profil `sun_checks.xml` allégé, durci ensuite |
| Stage publié alors que le build échoue | `post { always }` + `allowEmptyResults` maîtrisé |

## Critères de Done

- [ ] `Jenkinsfile` déclaratif versionné
- [ ] Lint et tests unitaires exécutés sur `docker-agent`
- [ ] Rapport JUnit publié et archivé
- [ ] Une violation fait échouer le build

## Message de commit

`ci(pipeline): add declarative jenkinsfile with lint and unit test stages`
