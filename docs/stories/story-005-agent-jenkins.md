# Story 005 — Créer l'agent Jenkins et interdire le build sur le controller

- **Jalon :** 0 — Outillage
- **Exigences couvertes :** `EXG-COM-04`
- **Dépendances :** stories 001, 002

## Description

En tant que responsable CI/CD, je veux que tous les builds s'exécutent sur un agent dédié et que le
controller n'ait **aucun** exécuteur, afin de respecter la contrainte « jamais sur le controller » et
de rendre cette contrainte vérifiable automatiquement.

## Critères d'acceptation

- Un nœud `docker-agent` est créé, `Online`, avec 2 exécuteurs.
- Le `Built-In Node` du controller affiche **0** exécuteur.
- Le nœud `docker-agent` a accès au socket Docker (nécessaire aux étapes build image et tests
  d'intégration).
- Un job de démonstration affiche le nom du nœud d'exécution : la sortie contient `docker-agent`.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Nœuds et exécuteurs | `Manage Jenkins` → Nodes |
| Exécution sur l'agent | Console du job : `Running on docker-agent` |
| Vue par nœud | `Build Executor Status` : deux sections, une vide |

## Automatisation des tests

`scripts/check-agents.sh` interroge l'API Jenkins :

```bash
curl -su "$JENKINS_USER:$JENKINS_TOKEN" \
  "http://localhost:8080/computer/(built-in)/api/json?tree=numExecutors"
```

Le script échoue si `numExecutors != 0`, et échoue si `docker-agent` est `Offline` ou a 0 exécuteur.
Dans cette story, le script est lancé depuis le terminal (`JENKINS_USER` / `JENKINS_TOKEN`). Son appel
par le pipeline lui-même, avec sa propre credential, est reporté à la story 011 (`5a`, `tasks-fr 005`).

## Documentation

`docs/tooling/jenkins-agent.md`

## Fichiers à versionner

- `docker-compose.yml` (service `jenkins-agent`, avec la clé **publique** SSH)
- `jenkins-agent/Dockerfile`
- `scripts/check-agents.sh`
- `docs/tooling/jenkins-agent.md`

## Fichiers à ne jamais versionner

`.env`, toute clé **privée** SSH (elle vit uniquement dans la credential Jenkins `docker-agent-ssh-key`)

## Risques

| Risque | Parade |
|--------|--------|
| Agent `Offline` après redémarrage | `restart: unless-stopped` + *Keep this agent online as much as possible* |
| Volume Docker non visible depuis l'agent | Monter `/var/run/docker.sock` dans l'agent |
| Un job sans `agent { label }` retombe sur le controller | Étape de vérification `check-agents.sh` + convention de code |

## Décisions (`tasks-fr 005`, 26/10/2026)

| # | Sujet | Décision |
|---|-------|----------|
| 1 | Connexion | `1b` — agent **SSH** (`jenkins/ssh-agent:8.6.0-jdk17`) : clé publique dans `docker-compose.yml`, clé privée **uniquement** dans la credential `docker-agent-ssh-key`. Plugin *SSH Build Agents* déjà installé (dépendance) |
| 2 | Image | `2a` — `jenkins-agent/Dockerfile` : + CLI Docker `29.9.0` et Compose `v5.5.1` (`COPY --from`) |
| 3 | Docker | `3a` — socket de l'hôte monté ; groupe `docker-host` GID `963` créé dans l'image (`group_add` ne s'applique pas aux sessions SSH) ; limite documentée |
| 4 | Démonstration | `4a` — job Pipeline `agent-smoke-test`, conservé |
| 5 | Script | `5a` — terminal ; appel par le pipeline en story 011 |

Host Key Verification Strategy : *Non verifying*, car l'image régénère ses clés d'hôte à chaque
recréation ; port 22 non publié (réseau `cicd` uniquement).

## Critères de Done

- [ ] `docker-agent` Online avec 2 exécuteurs
- [ ] Controller à 0 exécuteur
- [ ] `check-agents.sh` passe et échoue si l'invariant est rompu

## Message de commit

`build(jenkins): add dedicated build agent and forbid controller executors`
