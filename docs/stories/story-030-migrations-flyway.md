# Story 030 — Migrations expand/contract avec Flyway (bonus)

- **Jalon :** 5 — Bonus
- **Exigences couvertes :** `EXG-BON-02`
- **Dépendances :** stories 008, 015, 017

## Précondition

Story 017 (blue/green) terminée : c'est le seul contexte où une migration « sans interruption de
service » a un sens.

## Description

En tant que responsable de production, je veux appliquer une évolution de schéma sans coupure de
service, en suivant le motif expand/contract, afin de prouver que je sais faire évoluer une base
utilisée par une application déployée.

## Critères d'acceptation

- Flyway est activé (déjà fait en story 008 : `V1__create_task_model.sql`) ; les scripts de migration
  sont versionnés sous `src/main/resources/db/migration/`.
- Une migration **expand** est démontrée : ajout d'une colonne nouvelle **nullable** (ou nouvelle
  colonne avec valeur par défaut), compatible avec l'ancienne version de l'application.
- La migration **contract** (suppression de la colonne ou renommage) n'est appliquée qu'**après** que
  la nouvelle version est celle servie par le proxy.
- L'ordre expand → déploiement → vérification → contract est écrit dans une procédure.
- La migration est idempotente du point de vue de Flyway (`flyway_schema_history` montre une seule
  application).
- Le rollback blue/green reste possible à chaque étape.

## Preuves attendues

| Preuve | Where |
|--------|------- |
| Scripts `V1__...`, `V2__...`, `V3__...` | dépôt |
| `flyway_schema_history` | `psql` dans le conteneur |
| Version bleue active pendant l'expand | `/actuator/info` via le proxy |
| Absence d'erreur applicative | logs staging et production |

## Automatisation des tests

Test d'intégration `FlywayMigrationIT` : applique les scripts sur un PostgreSQL vierge et vérifie que
le schéma final correspond au modèle JPA. Un test vérifie que la version **ancienne** de l'application
supporte encore le schéma après expand (c'est tout l'intérêt).

## Documentation

`docs/architecture/migrations.md` : procédure expand/contract, exemple réel, raison de ne pas faire
de migration destructive dans le même déploiement.

## Fichiers à versionner

`src/main/resources/db/migration/V*.sql`, test d'intégration, documentation

## Fichiers à ne jamais versionner

`.env` (URL de base avec mot de passe)

## Risques

| Risque | Parade |
|--------|--------|
| Migration destructive accidentelle | Revue : toute suppression exige une story dédiée |
| `ddl-auto` en `update` qui masque une migration manquante | `validate` en production |
| Base staging ≠ base prod | Scripts identiques, appliqués par l'artefact lui-même |

## Critères de Done

- [ ] Flyway actif, scripts versionnés et appliqués une seule fois
- [ ] Expand sans coupure de service démontré
- [ ] Contract postérieur à la bascule, documenté
- [ ] Test d'intégration de migration vert

## Message de commit

`feat(app): add flyway migrations with expand contract procedure`
