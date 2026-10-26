# Story 022 — Descendre le pipeline sous les 10 minutes

- **Jalon :** 3 — N1a — Qualité et retour rapide
- **Exigences couvertes :** `EXG-N1a-04`
- **Dépendances :** story 012

## Description

En tant que développeur qui itère, je veux un pipeline qui se termine en moins de 10 minutes, afin
d'avoir une boucle de feedback courte. Je veux comprendre quelles optimisations ont servi et lesquelles
n'ont servi à rien.

## Critères d'acceptation

- Les étapes indépendantes s'exécutent en `parallel` (au minimum lint et tests unitaires, qui
  n'utilisent pas la même base).
- Les étapes qui ne peuvent pas être parallélisées le justifient **par écrit** (tests d'intégration et
  SonarQube consomment tous deux le même artefact).
- Le cache Maven est partagé entre les builds : volume `.m2` monté sur l'agent.
- La durée de chaque stage est mesurée et affichée ; un tableau de comparaison avant/après est
  produit.
- **Trois** builds consécutifs sont chronométrés : la médiane doit être sous 10 minutes.
- Les dépendances Maven ne sont résolues qu'une fois (pas de résolution par stage).

## Preuves attendues

| Preuve | Where |
|--------|------- |
| Vue **Parallel Stages** / concurrence | vue Pipeline du build |
| Durée totale par build | vue build : `Finished in X m Y s` |
| Cache : `Downloading` quasi absent au 2e build | console |
| Tableau comparatif | `docs/architecture/performance-pipeline.md` |

## Automatisation des tests

```bash
# mesure, sur 3 builds consécutifs
for i in 1 2 3; do
  curl -su "$JENKINS_USER:$JENKINS_TOKEN" \
    "http://localhost:8080/job/step-by-step/api/json?tree=lastCompletedBuild[duration]"
done
```

`scripts/check-duration.sh` échoue si la médiane dépasse 600 000 ms. Le critère devient donc un test,
pas une impression.

## Documentation

`docs/architecture/performance-pipeline.md` : avant/après, gains mesurés, ce qui n'a **pas** aidé
(exemple : paralléliser SonarQube et les tests d'intégration, impossible sans duplication d'analyse).

## Fichiers à versionner

- `Jenkinsfile` (blocs `parallel`, `options { timestamps() }`)
- `docker-compose.yml` (volume `.m2` sur l'agent)
- `scripts/check-duration.sh`
- `docs/architecture/performance-pipeline.md`

## Fichiers à ne jamais versionner

`.m2/` local

## Risques

| Risque | Parade |
|--------|--------|
| `parallel` sur des étapes qui partagent le répertoire de travail | Chaque branche travaille dans son propre répertoire |
| SonarQube ignoré car son cache est invalidé | Cache `.scannerwork` sur l'agent, à mesurer |
| Optimisation au détriment de la lisibilité | Toute parallélisation justifiée dans le rapport |

## Critères de Done

- [ ] `parallel` utilisé pour les étapes indépendantes
- [ ] Cache Maven partagé et efficace
- [ ] Médiane de 3 builds sous 10 minutes, mesurée
- [ ] Gains et limites documentés

## Message de commit

`ci(pipeline): parallelize stages and cache maven repository`
