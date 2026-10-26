# Story 026 — Rédiger le rapport technique

- **Jalon :** 4 — Rendus attendus
- **Exigences couvertes :** `EXG-REND-03`
- **Dépendances :** stories 015 à 024

## Description

En tant que jury, je veux un rapport technique qui expose l'architecture, les choix argumentés et les
métriques DORA mesurées, afin d'évaluer ma capacité à raisonner et pas seulement à exécuter.

## Critères d'acceptation

Le rapport contient :

1. **Contexte et périmètre** : pourquoi N1a + N1b + N2a + N3b, et pourquoi pas N2b ni N3a.
2. **Architecture** : le diagramme de la chaîne, les composants, les ports, les environnements.
3. **Choix techniques argumentés**, chacun avec alternative écartée et raison : Java 17,
   Thymeleaf, PostgreSQL, Nexus, jreleaser, tunnel webhook, blue/green local.
4. **Sécurité** : l'inventaire des secrets et la preuve qu'aucun n'est versionné.
5. **Métriques DORA** : le tableau des 4 métriques, valeurs mesurées, graphique Grafana, et la
   limite honnête de l'échantillon.
6. **Performance du pipeline** : avant/après, gains réels et échecs des optimisations tentées.
7. **Écarts et compromis assumés** : module non retenu, tunnel en remplacement d'un webhook natif.
8. **Ce que j'ai appris**, y compris les pannes rencontrées.

## Preuves attendues

| Preuve | Where |
|--------|------- |
| Rapport complet | `docs/rapport-technique/rapport.md` |
| Chaque affirmation adossée à une preuve | liens vers logs, CSV, captures |
| Diagrammes lisibles | images versionnées ou mermaid |

## Automatisation des tests

`scripts/check-report.sh` : vérifie la présence des 8 sections, l'existence du fichier
`observability/dora/dora-metrics.csv` et du dashboard Grafana référencé, et l'absence de `[À_VALIDER]`
ou de `[TODO]` dans le rapport.

## Documentation

`docs/rapport-technique/rapport.md`, complété par `docs/rapport-technique/figures/`.

## Fichiers à versionner

- `docs/rapport-technique/rapport.md`
- `docs/rapport-technique/figures/*.png`

## Fichiers à ne jamais versionner

Captures contenant des secrets (segmentées avant insertion)

## Risques

| Risque | Parade |
|--------|--------|
| Rapport rédigé le dernier jour | Rédiger la section architecture dès la story 011 |
| Affirmations non prouvées | Une affirmation sans preuve n'est pas écrite |
| Métriques DORA Presentations comme un benchmark | Rappeler la durée de l'échantillon |

## Critères de Done

- [ ] 8 sections présentes
- [ ] Métriques DORA mesurées et commentées
- [ ] Choix argumentés avec alternatives écartées
- [ ] `check-report.sh` vert, aucun `[À_VALIDER]`

## Message de commit

`docs(report): add technical report with architecture and dora metrics`
