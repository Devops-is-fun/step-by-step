# Story 015 — Créer l'environnement staging et un script de déploiement idempotent

- **Jalon :** 2 — Livraison continue
- **Exigences couvertes :** `EXG-COM-03`, `EXG-COM-14`
- **Dépendances :** stories 001, 014

## Description

En tant que responsable CI/CD, je veux un environnement staging reproductible et un script de
déploiement idempotent, afin que le même artefact soit déployable plusieurs fois avec le même résultat.

## Critères d'acceptation

- `environments/staging/docker-compose.yml` définit **exactement deux** services : l'application et son
  PostgreSQL propre, avec réseau dédié et ports non conflictifs avec la production.
- `scripts/deploy.sh <env> <image-tag>` converge vers l'état voulu : noms de conteneurs fixes,
  `docker compose up -d`.
- Exécuter le script **deux fois** avec le même tag réussit deux fois et ne recrée pas l'infra.
- Le script vérifie le healthcheck `/actuator/health` et **échoue** si l'application ne répond pas.
- Les identifiants de base de données sont injectés par variables d'environnement, jamais écrits en
  clair dans le fichier versionné.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| `docker compose ps` : 2 services Up | terminal |
| Deux exécutions successives en `exit 0` | log du script horodaté |
| `curl localhost:8082/actuator/health` = `UP` | terminal |
| Aucun secret en clair | `check-no-secrets.sh` |

## Automatisation des tests

```bash
scripts/deploy.sh staging <tag> && scripts/deploy.sh staging <tag>
curl -sf localhost:8082/actuator/health
```

Les deux appels doivent réussir. Un troisième appel avec un tag d'image **inexistant** doit **échouer** :
le script refuse de déclarer un déploiement réussi sans image disponible.

## Documentation

`docs/architecture/environnements.md` : ports, réseau, base de données propre, procédure de
redémarrage, raison de l'idempotence.

## Fichiers à versionner

- `environments/staging/docker-compose.yml`
- `scripts/deploy.sh`, `scripts/healthcheck.sh`
- `docs/architecture/environnements.md`

## Fichiers à ne jamais versionner

`environments/staging/.env`, volumes de base locale

## Risques

| Risque | Parade |
|--------|--------|
| Migration de schéma en cours de route | Le même schéma sert staging et prod (recette documentée) |
| Conflit de port | Tableau des ports dans `specification.md` § 5.1, respecté |
| Mot de passe en clair dans le compose | Variables d'environnement + credential Jenkins |

## Critères de Done

- [ ] Stack staging à 2 services, isolée
- [ ] `deploy.sh` exécuté deux fois sans erreur ni recréation
- [ ] Healthcheck obligatoire et en échec ⇒ échec du script
- [ ] Aucun secret en clair

## Message de commit

`feat(envs): add idempotent staging deployment script`
