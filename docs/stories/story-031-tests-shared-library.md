# Story 031 — Tester la Shared Library avec JenkinsPipelineUnit (bonus)

- **Jalon :** 5 — Bonus
- **Exigences couvertes :** `EXG-BON-03`
- **Dépendances :** story 020

## Précondition

Story 020 terminée. Bonus non vu en cours : il crédibilise la bibliothèque, mais ne remplace rien.

## Description

En tant que responsable de la bibliothèque partagée, je veux tester mes étapes hors de Jenkins, afin
de détecter une régression de logique de pipeline avant de Fusionner un changement de tag.

## Critères d'acceptation

- Le dépôt `cicd-shared-lib` utilise `JenkinsPipelineUnit` avec le plugin Maven correspondant.
- Les étapes à logique non triviale (calcul de nom d'image, choix du message de notification,
  formation de la commande de déploiement) sont couvertes par des tests unitaires du pipeline.
- Les tests tournent en local sans Jenkins : `./mvnw -B test` dans la bibliothèque.
- Un test échoue quand on casse volontairement une étape — preuve que le test mord.

## Preuves attendues

| Preuve | Where |
|--------|------- |
| Rapports de tests de la lib | `cicd-shared-lib/target/surefire-reports/` |
| Test rouge sur étape cassée | sortie console |
| Commande de test dans le README de la lib | `cicd-shared-lib/README.md` |

## Automatisation des tests

Le projet **est** le test : `cicd-shared-lib` doit exposer `./mvnw -B test` en commande de CI propre,
éventuellement appelée par un job multibranch dédié si le temps le permet.

## Documentation

`cicd-shared-lib/README.md`, section « Tester la bibliothèque ».

## Fichiers à versionner

`cicd-shared-lib/pom.xml`, `cicd-shared-lib/src/test/groovy/*.groovy`

## Fichiers à ne jamais versionner

`cicd-shared-lib/target/`

## Risques

| Risque | Parade |
|--------|--------|
| API de JenkinsPipelineUnit mal maîtrisée, temps perdu | Plafond de temps : 1 journée ; abandon propre si exceeded |
| Faux sentiment de sécurité | Les tests unitaires ne remplacent pas un build réel |

## Critères de Done

- [ ] Tests de la lib exécutables sans Jenkins
- [ ] Au moins une étape à logique testée
- [ ] Test rouge démontré sur régression
- [ ] Abandon possible sans casse si le temps manque

## Message de commit

`test(library): cover shared library steps with jenkinspipelineunit`
