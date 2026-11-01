# Visite guidée du projet

Bienvenue Morgan ! Cette page explique en 5 minutes ce qui existe déjà. Les règles de travail sont dans
`CONTRIBUTING.md` : lis-le d'abord.

## La chaîne, aujourd'hui

```
git push ──► GitHub ──webhook──► smee.io ──► relais (conteneur) ──► Jenkins (controller)
                                                                       │
                                                                       ▼
                                                    agent docker-agent : ./mvnw -B validate
                                                                       │
                                                                       ▼
                                                 statut ✅/❌ affiché sur le commit et la PR
```

Tout tourne en Docker Compose, **sur la machine de Géraldine**. Jenkins n'est pas exposé à
Internet : c'est le relais qui va chercher les webhooks chez smee.io.

## Ce qui est fait

| Story | Ce qui existe | Doc |
|-------|---------------|-----|
| 001 | Jenkins, SonarQube, Nexus en une commande (`docker compose up -d`) | `README.md` |
| 002 | Jenkins débloqué, plugins, jeton pour les scripts | `docs/tooling/jenkins-configuration.md` |
| 003 | SonarQube : projet `step-by-step`, Quality Gate à 60 % de couverture | `docs/tooling/sonarqube.md` |
| 004 | Nexus : dépôt `maven-releases`, compte de déploiement | `docs/tooling/nexus.md` |
| 005 | Agent Jenkins `docker-agent` : **aucun build sur le controller** | `docs/tooling/jenkins-agent.md` |
| 006 | Dépôt Git, vigile anti-secrets (hook `pre-commit`) | `docs/tooling/git-conventions.md` |
| 007 | Application Spring Boot 4.1 / Java 17, image Docker, première analyse Sonar | `README.md` |
| 008 | Modèle de données, Flyway, PostgreSQL de test (`compose-test.yml`), tests d'intégration | `docs/architecture/data-model.md` |
| 009 | Règles métier `REG-01` et `REG-02`, 21 tests unitaires | `docs/architecture/business-rules.md` |
| 010 | Job Multibranch `step-by-step`, webhook GitHub via relais smee.io | `docs/tooling/webhook-relay.md` |

## Tes stories

| Story | Sujet | Ce dont tu as besoin chez toi |
|-------|-------|-------------------------------|
| 015 | Stack staging idempotente | Docker, l'image de l'application |
| 017 | Blue/green en production (module N2a) | Docker, nginx |
| 023 | Releases automatisées (module N1b) | Git, Maven |
| 024 | Prometheus, Grafana, métriques DORA (module N3b) | Docker |
| 025 | README de relance complète | toute la stack : c'est le test du README |

Chaque story est décrite dans `docs/stories/story-0XX-….md` (critères d'acceptation, preuves,
risques). Les décisions déjà prises sont dans `docs/specification/specification.md`, section
« Journal des décisions ».

## Petit glossaire

| Mot | Sens ici |
|-----|----------|
| Controller | le Jenkins principal : il orchestre, il ne construit **jamais** |
| Agent | le conteneur `cicd-jenkins-agent`, où tournent les builds |
| Multibranch | un job qui crée tout seul un sous-job par branche et par PR |
| Vigile | `scripts/check-no-secrets.sh`, lancé avant chaque commit (hook) et par Jenkins |
| Relais smee | le conteneur `cicd-webhook-relay` qui apporte les webhooks de GitHub à Jenkins |
| Quality Gate | le verdict de SonarQube ; il bloquera le pipeline (story 013) |
| Credential | un secret rangé dans Jenkins, appelé par son identifiant, jamais écrit en clair |

## Par où commencer

1. `CONTRIBUTING.md`, section « First setup ».
2. Lancer l'application chez toi : `README.md`, section « Lancer l'application en local ».
3. Ta première PR : note tes écarts du premier lancement en bas de cette page.

## Notes du premier lancement (Morgan)

Java : Java 11 était initialement installé sur ma machine. Installation de Temurin 17 et configuration de JAVA_HOME pour respecter les prérequis du projet.

Docker : version 27.5.1 utilisée au lieu de la version 29.x indiquée dans les prérequis. Les commandes de lancement et les tests ont néanmoins fonction

Bash sous Windows : la commande bash scripts/check-no-secrets.sh lançait WSL, qui n'était pas correctement configuré. Le script a fonctionné en utilisant Git Bash avec C:\Program Files\Git\bin\bash.exe.

Validation locale : 21 tests unitaires et 7 tests d'intégration réussis. Application démarrée en JAR puis en conteneur Docker, avec un état de santé UP dans les deux cas.