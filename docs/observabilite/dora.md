
# Observabilité et métriques DORA

## Objectif

La Story 024 met en place Prometheus et Grafana pour
surveiller l'application Step by Step et suivre quatre
indicateurs de livraison logicielle calculés depuis Jenkins
et Git.

## Architecture

- Spring Boot Actuator expose `/actuator/prometheus`.
- Prometheus collecte les métriques des environnements
  staging et production.
- `scripts/dora_metrics.py` analyse les builds Jenkins
  et l'historique Git.
- `scripts/dora-metrics.sh` lance le calcul.
- Le résultat est enregistré dans
  `observability/dora/dora-metrics.csv`.
- `scripts/dora_exporter.py` expose le CSV au format
  Prometheus sur le port 9105.
- Grafana affiche les quatre indicateurs DORA.

## Métriques calculées

### 1. Fréquence des déploiements

Nombre de déploiements en production confirmés
pendant chaque semaine ISO.

### 2. Délai médian commit → production

Médiane des différences entre l'horodatage Git du
commit déployé et l'heure du déploiement en production.

Unité : heures.

### 3. Délai médian intégration → production

Médiane des différences entre l'horodatage du commit
retrouvé dans l'historique first-parent de `main`
et l'heure du déploiement en production.

Unité : heures.

Cet horodatage est une approximation du moment de
l'intégration et ne constitue pas nécessairement
l'heure exacte de fusion d'une pull request.

### 4. Taux d'échec des changements

Nombre de déploiements pour lesquels une trace de
rollback est identifiée, divisé par le nombre total
de déploiements analysés, multiplié par 100.

Unité : pourcentage.

## Calcul des métriques

Variables d'environnement :

- `JENKINS_URL` : URL Jenkins, par défaut
  `http://localhost:8080/`
- `JENKINS_USER` : utilisateur Jenkins
- `JENKINS_TOKEN` : jeton API Jenkins
- `JENKINS_JOB` : chemin du job, par défaut
  `step-by-step/main`

Exécution :

```bash
bash scripts/dora-metrics.sh
bash scripts/check-dora.sh
```

Les identifiants Jenkins ne doivent pas être enregistrés
dans Git.

## Test hors ligne

Le script peut être testé avec une liste de builds et
une console Jenkins :

```bash
python scripts/dora_metrics.py \
  --builds-file dora-test/main-builds.json \
  --console-file dora-test/main-20-console.txt
```

Ce mode n'analyse que les consoles effectivement fournies.
Il ne doit pas être considéré comme une mesure exhaustive
de tous les déploiements Jenkins.

## Démarrage de l'observabilité

```bash
docker compose up -d
```

Interfaces :

- Prometheus : http://localhost:9090
- Cibles Prometheus : http://localhost:9090/targets
- Grafana : http://localhost:3000

Le mot de passe administrateur Grafana est fourni via
la variable `GRAFANA_ADMIN_PASSWORD`.

## Limitations

- Le mode hors ligne ne confirme que les déploiements
  présents dans les consoles disponibles.
- Le délai de fusion est estimé à partir de l'historique
  Git et peut différer de l'heure de fusion GitHub.
- L'heure de déploiement peut être approximée par la
  fin du build si l'API Jenkins ne fournit pas l'heure
  exacte de l'étape de production.
- Le taux d'échec repose sur les traces de rollback
  identifiables dans les consoles Jenkins.
- Les métriques exportées utilisent un label `week`.
  Le dashboard présente donc des valeurs par semaine,
  mais pas encore une véritable série temporelle
  horodatée à la date historique de chaque semaine.
- Les données doivent être recalculées régulièrement
  pour intégrer les nouveaux déploiements.

## Validation

Vérifier :

1. Le CSV généré et ses colonnes.
2. L'exécution de `scripts/check-dora.sh`.
3. L'état `UP` de la cible `step-by-step-dora`.
4. Les quatre panneaux du dashboard Grafana.
5. Les endpoints `/actuator/prometheus` de staging
   et production après déploiement de la version
   instrumentée.
