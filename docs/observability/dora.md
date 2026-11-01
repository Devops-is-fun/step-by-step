
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

### 5. Temps médian de restauration

Pour chaque déploiement en production en échec (trace de
rollback), durée jusqu'au déploiement réussi suivant,
comptée dans la semaine de l'échec. Grâce au blue/green,
le service lui-même continue de répondre pendant ce temps :
c'est la durée pour **remettre une nouvelle version en
service**, pas une durée d'indisponibilité.

Unité : heures. Vide si la semaine n'a connu aucun échec.

Les quatre métriques DORA sont donc couvertes : fréquence
de déploiement (1), délai de mise en production (2, avec
la variante 3), taux d'échec (4) et temps de restauration (5).

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

Les ports n'écoutent que sur `127.0.0.1`, comme le reste de
l'outillage. Les dashboards sont lisibles sans connexion
(accès anonyme en lecture). Le compte `admin` (mot de passe
initial `admin`, à changer à la première connexion, puis
rangé dans le gestionnaire de mots de passe) sert à
l'administration.

Les applications n'écoutent que sur `127.0.0.1` : Prometheus
ne peut pas les joindre par l'hôte. Les scripts de
déploiement le connectent donc au réseau Docker de chaque
stack (`scripts/connect-prometheus.sh`), et il collecte les
conteneurs par leur nom (`stepbystep-staging-app-1`,
`stepbystep-production-app-blue-1`, `…-green-1`). Après un
redémarrage de Prometheus, relancer :

```bash
./scripts/connect-prometheus.sh stepbystep-staging
./scripts/connect-prometheus.sh stepbystep-production
```

La couleur inactive de la production peut apparaître `DOWN`
(code 404) tant qu'elle porte une version antérieure à
l'instrumentation : c'est attendu, elle sera à jour au
déploiement suivant.

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
4. Les cinq panneaux du dashboard Grafana.
5. Les endpoints `/actuator/prometheus` de staging
   et production après déploiement de la version
   instrumentée.
