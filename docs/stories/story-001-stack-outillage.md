# Story 001 — Démarrer l'outillage complet en une commande

- **Jalon :** 0 — Outillage
- **Exigences couvertes :** `EXG-COM-01`, `EXG-COM-02`
- **Dépendances :** aucune

## Description

En tant que responsable CI/CD, je veux démarrer Jenkins, SonarQube et Nexus depuis un seul fichier
`docker-compose.yml`, afin que mon poste et l'agent de build repartent d'un état identique à chaque
session de travail.

## Critères d'acceptation

- `docker compose up -d` démarre exactement trois services : Jenkins, SonarQube, Nexus.
- Les trois interfaces répondent en HTTP 200 : Jenkins `localhost:8080`, SonarQube `localhost:9000`,
  Nexus `localhost:8081`.
- `docker compose down` puis `up -d` laisse les données intactes (volumes nommés, pas de `tmpfs`).
- Aucun mot de passe ni token n'apparaît en clair dans le fichier versionné.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| `docker compose ps` : 3 services `Up` | terminal |
| 3 codes HTTP 200 | `curl -s -o /dev/null -w '%{http_code}'` |
| Persistance après `down`/`up` | `docker volume ls` + re-test HTTP |

## Automatisation des tests

Recette manuelle normée (pas de code applicatif) : la preuve est le résultat des trois commandes
ci-dessus, consigné dans la story.

## Documentation

Section « Démarrage rapide » du `README.md` (finalisée en story 025, amorcée ici).

## Fichiers à versionner

- `docker-compose.yml`
- `.gitignore` (créé au tutoriel 010 ; complété en story 006)

Pas de `.env.example` ici : le fichier d'outillage ne contient aucune variable d'environnement, donc
rien à documenter. Le premier vrai `.env.example` apparaît en story 015, avec les mots de passe
PostgreSQL de staging.

## Fichiers à ne jamais versionner

`.env`, `volumes/`, `sonarqube_data/`, `nexus_data/`, `jenkins_home/`

## Risques

| Risque | Parade |
|--------|--------|
| Ports déjà occupés | `ss -ltn` avant `up` (tutoriel 001) ; sinon changer le port hôte |
| RAM insuffisante | Plafonds JVM explicites dans le fichier ; mesuré à 5,3 Gio disponibles |
| SonarQube refuse de démarrer | `vm.max_map_count` doit être ≥ 262144 ; vérifié dans le tutoriel 001 |
| Version SonarQube non pinnée | `9.9.8-community`, dernière version acceptant Java 17 |

## Critères de Done

- [ ] Les 3 services démarrent avec une seule commande
- [ ] Les 3 interfaces répondent 200
- [ ] Les données survivent à un `down`/`up`
- [ ] Aucun secret en clair dans le dépôt

## Message de commit

`build(outillage): start jenkins sonarqube and nexus from one compose file`
