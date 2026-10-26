# Story 024 — Mesurer et suivre les métriques DORA

- **Jalon :** 3 — N3b — Observabilité et DORA (module de niveau 3)
- **Exigences couvertes :** `EXG-N3b-01`, `EXG-N3b-02`, `EXG-N3b-03`
- **Dépendances :** stories 001, 007

## Description

En tant que responsable de projet, je veux mesurer ma performance de livraison selon les quatre
métriques DORA et les suivre dans Grafana, afin de démontrer l'effet de la chaîne CI/CD sur la durée
du projet.

## Les quatre métriques

| Métrique | Définition retenue | Source |
|----------|--------------------|--------|
| **Fréquence de déploiement** | Nombre de déploiements en production par semaine | Jenkins API (stages de déploiement) |
| **Délai de mise en production** | Durée médiane entre commit en `main` et mise en production | Jenkins + `git log` |
| **Délai de changement** | Durée médiane entre changement infinitesimal (merge) et mise en production | Jenkins + `git log` |
| **Taux d'échec des changements** | Part des déploiements nécessitant un retour arrière ou d'un correctif urgent | Jenkins (builds rouges en prod) |

## Critères d'acceptation

- `spring-boot-starter-actuator` expose `/actuator/prometheus` ; Prometheus scrape staging et
  production et affiche les cibles `up`.
- `scripts/dora-metrics.sh` calcule les 4 métriques depuis l'API Jenkins et `git log`, et produit un
  fichier de séries historiques (`observability/dora/dora-metrics.csv`) avec une ligne par semaine.
- Un dashboard Grafana (JSON versionné, importable) affiche les 4 métriques sur la durée du projet.
- Le calcul est **reproductible** : relancer le script sur les mêmes données redonne les mêmes valeurs.
- Les valeurs sont interprétées dans le rapport technique (story 026).

## Preuves attendues

| Preuve | Where |
|--------|------- |
| Cibles `up` | Prometheus → Status → Targets |
| `/actuator/prometheus` non vide | `curl localhost:8082/actuator/prometheus` |
| CSV qui grossit à chaque semaine | fichier versionné, historique Git |
| Dashboard 4 panneaux | Grafana |
| Comparaison avant/après chaîne CI/CD | rapport technique |

## Automatisation des tests

`scripts/check-dora.sh` : échoue si le CSV ne contient pas les 4 colonnes attendues, ou si une
période est vide alors qu'un build existe. Le calcul est donc vérifiable automatiquement.

## Documentation

`docs/observabilite/dora.md` : définition de chaque métrique, formule exacte, limites de la mesure
(câle : durée de vie du projet TP).

## Fichiers à versionner

- `docker-compose.yml` (services `prometheus`, `grafana`)
- `observability/prometheus.yml`
- `observability/grafana/provisioning/datasources/datasource.yml`
- `observability/grafana/dashboards/dora.json`
- `scripts/dora-metrics.sh`, `scripts/check-dora.sh`
- `observability/dora/dora-metrics.csv`
- `docs/observabilite/dora.md`

## Fichiers à ne jamais versionner

`grafana_data/`, `prometheus_data/`, mots de passe Grafana

## Risques

| Risque | Parade |
|--------|--------|
| Taille d'échantillon faible (projet court) | L'expliciter dans le rapport : tendance, pas benchmark |
| Métriques DORA mal comprises | Documenter les formules et la source de chaque donnée |
| Grafana configurée à la main, non reproductible | Provisioning versionné |

## Critères de Done

- [ ] Prometheus scrape l'application (`up`)
- [ ] 4 métriques calculées par script reproductible
- [ ] Dashboard Grafana versionné et importé
- [ ] Historique suivi et interprétable

## Message de commit

`feat(observability): add prometheus grafana and dora metrics dashboard`
