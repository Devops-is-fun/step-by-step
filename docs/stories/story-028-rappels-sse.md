# Story 028 — Rappels planifiés et notification in-app (bonus applicatif)

- **Jalon :** 5 — Bonus (après bouclage du périmètre principal)
- **Exigences couvertes :** `REG-04`, `REG-05`, et application des règles restantes `REG-03`, `REG-06`, `REG-07`
- **Dépendances :** stories 009, 019, 021

## Précondition

Ne démarrer cette story **que** si les stories 001 à 027 sont cochées. Elle complète l'application,
mais n'apporte aucun point du barème : c'est un choix assumé (décision : « noyau minimal d'abord,
le reste de l'application en bonus »).

## Description

En tant que personne qui procrastine, je veux qu'on me rappelle ma sous-étape du jour à l'heure que
j'ai choisie, et que je voie une seule sous-étape à la fois, afin de ne jamais être faced à une liste
qui paralyse.

## Critères d'acceptation

- `REG-05` : la vue « aujourd'hui » rend **exactement une** sous-étape — la première non faite de la
  tâche du jour — jamais une liste.
- Cocher cette sous-étape affiche immédiatement la suivante, sans rechargement complet.
- `REG-03` : une tâche terminée est figée ; toute tentative de modification est refusée et testée.
- `REG-06` : validation du libellé (1–80 caractères) avec message en langage courant.
- `REG-07` : la durée totale affichée est la somme des durées des sous-étapes.
- `REG-04` : un job `@Scheduled` envoie le rappel à l'heure choisie **uniquement** s'il reste une
  sous-étape non faite ; le rappel part via le webhook Discord (credential `discord-webhook`) et crée
  une ligne `NotificationLog`.
- La notification in-app arrive en direct par **SSE** (`EventSource`), sans rafraîchissement de page.
- Le job planifié est testé unitairement (le temps est injecté, pas `Instant.now()` figé).

## Preuves attendues

| Preuve | Where |
|--------|------- |
| Une seule sous-étape affichée | capture de l'écran principal |
| Notification SSE reçue | console du navigateur |
| Rappel Discord parti | capture du message horodaté |
| Ligne `NotificationLog` créée | table `notification_log` |
| Aucun rappel pour une tâche terminée | test unitaire |

## Automatisation des tests

- `DailyViewServiceTest` : renvoie 0, 1, ou exactement 1 sous-étape selon l'état.
- `ReminderSchedulerTest` : horloge injectée (`Clock`), trois cas — rappel envoyé, aucun rappel car
  plus rien à faire, aucun rappel car pas d'heure.
- `TaskServiceTest` : `REG-03` (tâche figée), `REG-06` (libellé trop long), `REG-07` (somme).

## Documentation

`docs/architecture/rappels-et-sse.md` : pourquoi SSE plutôt que WebSocket, rôle du même webhook Discord
que les notifications de build, limites d'un `@Scheduled` mono-instance.

## Fichiers à versionner

`src/main/java/com/esgi/stepbystep/service/DailyViewService.java`,
`src/main/java/com/esgi/stepbystep/service/ReminderScheduler.java`,
`src/main/resources/static/js/daily.js`, templates Thymeleaf, tests associés

## Fichiers à ne jamais versionner

L'URL du webhook Discord

## Risques

| Risque | Parade |
|--------|--------|
| `@Scheduled` en double si deux instances tournent | Verrou en base ou instance unique par stack |
| SSE en cascade derrière nginx | `proxy_buffering off` dans `nginx.conf` |
| Fuseau horaire décalé | Stocker en UTC, afficher en local ; tests avec `Clock` fixe |

## Critères de Done

- [ ] `REG-05` : une seule sous-étape, testé
- [ ] `REG-03`, `REG-06`, `REG-07` testées
- [ ] Rappel Discord conditionnel par `REG-04`
- [ ] Notification SSE fonctionnelle

## Message de commit

`feat(app): add daily view reminders and sse notifications`
