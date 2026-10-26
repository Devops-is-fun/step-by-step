# Story 029 — Minuteur Pomodoro sur la sous-étape en cours (bonus)

- **Jalon :** 5 — Bonus (après bouclage du périmètre principal)
- **Exigences couvertes :** `EXG-APP-OPT-01`
- **Dépendances :** story 028

## Précondition

Story 028 terminée. Fonctionnalité **optionnelle**, annoncée comme telle.

## Description

En tant que personne qui se disperse, je veux un minuteur Pomodoro sur la sous-étape affichée, afin de
travailler en blocs courts cohérents avec le découpage de 30 minutes.

## Critères d'acceptation

- Le minuteur démarre, se met en pause et se relance depuis l'écran de la sous-étape du jour.
- La durée par défaut est calée sur l'estimation de la sous-étape (plafonnée à 30 minutes).
- Une notification in-app (SSE) signale la fin du cycle de travail.
- Le minuteur ne modifie **aucune** donnée métier : il n'écrit pas de durée « réelle » dans le modèle.
- Aucun minuteur de 25 minutes codé en dur sans justification dans le rapport.

## Preuves attendues

| Preuve | Where |
|--------|------- |
| Capture du minuteur en cours | dossier soutenance |
| Notification de fin de cycle | console navigateur |
| Absence d'écriture métier | revue du code |

## Automatisation des tests

Test du composant de minuteur côté client : logique pure (démarrage / pause / affichage) testée
séparément du DOM. Les règles métier n'étant pas affectées, aucun test JUnit supplémentaire n'est
requis.

## Documentation

`docs/architecture/rappels-et-sse.md`, section « Pomodoro » : périmètre volontairement minimal.

## Fichiers à versionner

`src/main/resources/static/js/pomodoro.js`, template associé

## Fichiers à ne jamais versionner

rien

## Risques

| Risque | Parade |
|--------|--------|
| Fonctionnalité qui déborde sur le TP | Définir une taille maximale : 1 écran, 1 script |
| Se discrediter sur un bonus non demandé | Assumer publiquement que c'est optionnel |

## Critères de Done

- [ ] Minuteur fonctionnel sur la sous-étape du jour
- [ ] Notification de fin de cycle
- [ ] Aucun effet sur le modèle métier
- [ ] Documenté comme optionnel

## Message de commit

`feat(app): add optional pomodoro timer on current step`
