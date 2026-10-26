# Story 012 — Exécuter les tests d'intégration avec Docker Compose

- **Jalon :** 1 — Intégration continue
- **Exigences couvertes :** `EXG-COM-10`
- **Dépendances :** stories 008, 011

## Description

En tant que responsable CI/CD, je veux un stage dédié qui démarre la base PostgreSQL via Docker
Compose, exécute les tests d'intégration Failsafe, puis arrête le stack, afin de valider l'exigence
« tests d'intégration avec Docker Compose » sans laisser de conteneur résiduel.

## Critères d'acceptation

- Le stage démarre `src/test/resources/compose-test.yml` en arrière-plan et **attend** que PostgreSQL
  soit sain (healthcheck), sans `sleep` fixe.
- Le stage exécute `./mvnw -B failsafe:integration-test failsafe:verify`.
- Un `post { always }` exécute `docker compose down -v` : le stack est arrêté **même si les tests
  échouent**.
- Un test d'intégration en échec fait échouer le build.
- Après le build, aucun conteneur du stack de test ne subsiste (`docker compose ps` vide).

## Preuves attendues

| Preuve | Where |
|--------|-------|
| `Tests run: N, Failures: 0, Errors: 0` | sortie du stage |
| Logs de démarrage PostgreSQL | console |
| Stack arrêté après build | `docker compose -f ... ps` → aucun service |
| Build rouge sur échec IT | test volontairement cassé |

## Automatisation des tests

`src/test/java/**/*IT.java`. Vérification du non-fuite de conteneurs, scriptée dans la story :

```bash
docker compose -f src/test/resources/compose-test.yml ps -q | wc -l   # attendu : 0
```

## Documentation

`docs/architecture/tests-integration.md` : pourquoi Failsafe et non Surefire, attente de santé,
garantie d'arrêt.

## Fichiers à versionner

- `Jenkinsfile` (stage `Integration Tests`)
- `src/test/java/com/esgi/stepbystep/**/*IT.java`
- `src/test/resources/compose-test.yml` (déjà créé en story 008)

## Fichiers à ne jamais versionner

`pgdata/`, journaux du stack

## Risques

| Risque | Parade |
|--------|--------|
| Base pas prête → tests erronés | Attente par healthcheck, pas par temporisation |
| Conteneurs orphelins si le build est arrêté | `post { always }` + `timeout` de stage |
| Socket Docker absent sur l'agent | Vérifié par story 005 |

## Critères de Done

- [ ] Stage démarre, attend, teste et arrête le stack
- [ ] Arrêt garanti même en cas d'échec
- [ ] Échec d'intégration = build rouge
- [ ] Aucun résidu de conteneur

## Message de commit

`ci(pipeline): run integration tests against docker compose stack`
