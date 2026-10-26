# Story 002 — Débloquer Jenkins et installer les plugins

- **Jalon :** 0 — Outillage
- **Exigences couvertes :** `EXG-COM-02`
- **Dépendances :** story 001

## Description

En tant que développeur, je veux que Jenkins soit administrable et muni des plugins de la chaîne
CI/CD, afin de ne pas découvrir les manques en plein milieu du pipeline.

## Critères d'acceptation

- L'assistant d'installation est déverrouillé avec le mot de passe initial **lu à la volée**
  (`docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword`).
- Le nouveau mot de passe admin est rangé dans un **gestionnaire de mots de passe**, jamais dans un
  fichier du projet (pas même un `.env` ignoré).
- La sécurité est durcie : CSRF (crumb) actif ; aucune URL d'agent exposée, ce que garantit l'écoute
  de 8080 et 50000 sur `127.0.0.1` seulement (prouvé en story 001, tutoriel 008).
- Les plugins suivants sont installés : `git`, `github-branch-source`, `workflow-aggregator`,
  `pipeline-graph-view`, `sonar`, `credentials-binding`, `junit`, `coverage`, `warnings-ng`,
  `pipeline-utility-steps`.
- Un jeton d'API Jenkins existe pour les scripts ; il est rangé dans le gestionnaire de mots de passe
  et passé par les variables `JENKINS_USER` / `JENKINS_TOKEN`.
- Le passage du controller à **0** exécuteur est fait en story 005, une fois l'agent créé.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Liste des plugins installés | `Manage Jenkins` → Plugins |
| Curl authentifié OK | `curl -su "$JENKINS_USER:$JENKINS_TOKEN" http://localhost:8080/api/json` |
| Mot de passe hors du projet | rangé dans le gestionnaire de mots de passe ; aucun fichier du projet ne le contient |

## Automatisation des tests

Recette manuelle : la configuration vit dans l'IHM Jenkins, elle est donc **documentée** plutôt que
testée. Le script `scripts/check-jenkins.sh` vérifie que le crumb est disponible et que les plugins
obligatoires sont présents.

## Documentation

`docs/tooling/jenkins-configuration.md`

## Fichiers à versionner

- `docs/tooling/jenkins-configuration.md`
- `scripts/check-jenkins.sh`

## Fichiers à ne jamais versionner

`.env`, `credentials/`, captures d'écran contenant des mots de passe

## Décisions (25/10/2026, `tasks-fr 002`)

- Liste de plugins modernisée ; `maven-integration` et `publish-over-ssh` retirés (inutiles avec
  `./mvnw` et des environnements locaux) (`1a`).
- Installation dans l'interface, liste documentée et vérifiée par `check-jenkins.sh` (`2a`).
- Controller à 0 exécuteur : story 005 (`3a`). Type d'agent : story 005 (`4a`).
- Scripts authentifiés par jeton d'API, jamais par le mot de passe (`5a`).
- Documentation technique dans `docs/tooling/` (et non `docs/outillage/`), rédigée **en anglais
  uniquement** ; seul le README est bilingue (décision du 25/10/2026).

## Risques

| Risque | Parade |
|--------|--------|
| Plugin déprécié dans la liste | `pipeline-graph-view` remplace `pipeline-stage-view`, `coverage` remplace `jacoco` (décision du 25/10/2026) |
| Oublier un plugin | Liste vérifiée par `check-jenkins.sh` |

## Critères de Done

- [ ] Jenkins déverrouillé, crumb disponible
- [ ] Plugins de la liste présents
- [ ] Configuration documentée

## Message de commit

`docs(outillage): document jenkins unlock and required plugins`
