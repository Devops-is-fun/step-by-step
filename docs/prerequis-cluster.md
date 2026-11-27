# Installation du cluster Kubernetes local

Ce guide explique comment installer et utiliser l'environnement Kubernetes local du projet **Step by Step**.

L'environnement repose sur kind, ingress-nginx, metrics-server, Prometheus, Grafana et Flux pour le déploiement continu GitOps.

Il est destiné au développement et aux démonstrations. Il ne remplace pas l'environnement de production Kubernetes à deux machines virtuelles prévu par le projet DevOps.

## 1. Prérequis

Outils nécessaires :

| Outil | Version utilisée lors des tests |
|---|---|
| Docker Desktop | 27.5.1 |
| kind | 0.33.0 |
| kubectl | 1.37.x recommandé (à valider lors des tests) |
| Helm | 4.3.2 |
| Git Bash | Environnement Windows |

La configuration locale utilise Kubernetes `kindest/node:v1.37.0`.

L'image du nœud kind est épinglée par son digest SHA256 dans `scripts/setup-cluster.sh` afin de garantir la reproductibilité de l'installation.

Prévoir environ 8 Go de mémoire disponibles pour Docker, selon les composants exécutés simultanément.

Sous Windows, exécuter les scripts Bash depuis **Git Bash** et vérifier que Docker Desktop utilise les conteneurs Linux.

## 2. Création du cluster

Depuis la racine du dépôt :

```bash
bash scripts/setup-cluster.sh
```

Ce script :

- Vérifie la disponibilité de Docker, kind et kubectl.
- Crée le cluster `step-by-step` s'il n'existe pas.
- Utilise `scripts/kind-config.yaml` pour exposer les ports 80 et 443.
- Sélectionne le contexte `kind-step-by-step`.
- Installe metrics-server avec les adaptations TLS nécessaires à kind.

Vérification :

```bash
kubectl get nodes
kubectl top nodes
```

Le nœud doit être `Ready`. Les métriques CPU et mémoire peuvent prendre quelques secondes avant d'apparaître.

L'option `--kubelet-insecure-tls` utilisée par metrics-server est réservée à cet environnement local.

**Attention :** si un cluster `step-by-step` existe déjà avec une ancienne version de Kubernetes, le script ne met pas automatiquement à jour sa version.

Une recréation du cluster peut être nécessaire. Sauvegarder les données PostgreSQL et les configurations nécessaires avant toute suppression.

## 3. Installation d'Ingress NGINX

```bash
bash scripts/install-ingress.sh
```

Vérifier :

```bash
kubectl get pods -n ingress-nginx
curl -i http://localhost/
```

Avant de configurer les routes applicatives, une réponse HTTP `404` du contrôleur NGINX est normale : elle confirme que le contrôleur répond, mais qu'aucune route correspondante n'est encore définie.

## 4. Installation de Prometheus et Grafana

Le monitoring utilise le chart Helm `kube-prometheus-stack` en version `94.1.0`.

Définir un mot de passe Grafana dans Git Bash :

```bash
read -rsp "Mot de passe administrateur Grafana : " GRAFANA_ADMIN_PASSWORD
echo
export GRAFANA_ADMIN_PASSWORD
```

Installer :

```bash
bash scripts/install-monitoring.sh
```

Effacer ensuite la variable :

```bash
unset GRAFANA_ADMIN_PASSWORD
```

Le script crée le Secret Kubernetes `monitoring-grafana-admin` s'il n'existe pas et installe Prometheus, Grafana ainsi que les composants nécessaires à la supervision Kubernetes.

Le mot de passe ne doit jamais être enregistré dans Git.

Vérifier :

```bash
kubectl get pods -n monitoring
```

### Prérequis ServiceMonitor et ordre d'installation

**Important :** le monitoring doit être installé avant d'appliquer les ressources `ServiceMonitor`.

Le fichier `k8s/50-servicemonitor.yaml` définit une ressource personnalisée (`ServiceMonitor`) qui nécessite la CRD fournie par le Prometheus Operator, installé avec `kube-prometheus-stack`.

Après l'installation du monitoring, vérifier que la CRD est disponible :

```bash
kubectl get crd servicemonitors.monitoring.coreos.com
```

La commande doit retourner la CRD `servicemonitors.monitoring.coreos.com`.


**Ordre de déploiement recommandé :**

1. Créer le cluster avec `scripts/setup-cluster.sh`.
2. Installer ingress-nginx avec `scripts/install-ingress.sh`.
3. Installer Prometheus et Grafana avec `scripts/install-monitoring.sh`, après avoir configuré le mot de passe administrateur.
4. Vérifier la disponibilité de la CRD `ServiceMonitor`.
5. Créer le namespace `step-by-step` et le Secret PostgreSQL `db-credentials`.
6. Installer Flux avec `scripts/install-flux.sh`.
7. Attendre la synchronisation des ressources Kubernetes.
8. Vérifier le fonctionnement de PostgreSQL, du backend et du monitoring.

Flux applique automatiquement les manifestes du dossier `k8s/` présents sur la branche `main`.

Le fichier de configuration Helm `values-monitoring.yaml` est situé dans `scripts/`, afin que le répertoire `k8s/` contienne uniquement des manifestes Kubernetes.

**Important :** installer le monitoring et vérifier la disponibilité de la CRD `ServiceMonitor` avant d'installer Flux.

### Accéder à Grafana

Dans un terminal :

```bash
kubectl -n monitoring port-forward svc/monitoring-grafana 3000:80
```

Ouvrir http://localhost:3000.

Identifiant : `admin`.

Mot de passe : celui défini lors de l'installation.

### Accéder à Prometheus

Dans un autre terminal :

```bash
kubectl -n monitoring port-forward svc/monitoring-kube-prometheus-prometheus 9090:9090
```

Ouvrir http://localhost:9090/targets.

Certaines cibles internes du control plane peuvent être DOWN sur kind en raison de leurs interfaces d'écoute. Cela ne signifie pas nécessairement que le monitoring applicatif est défaillant.

## 5. Préparer le namespace et le Secret PostgreSQL

Créer le namespace :

```bash
kubectl apply -f k8s/00-namespace.yaml
```

Créer les identifiants PostgreSQL :

```bash
read -rsp "Mot de passe PostgreSQL : " DB_PASSWORD
echo

kubectl -n step-by-step create secret generic db-credentials \
  --from-literal=username=stepbystep \
  --from-literal="password=$DB_PASSWORD"

unset DB_PASSWORD
```

Vérifier la présence du Secret PostgreSQL :

```bash
kubectl -n step-by-step get secret db-credentials
```

Le Secret doit être présent avant l'installation de Flux.

PostgreSQL sera automatiquement déployé lors de la synchronisation des manifestes Kubernetes, décrite dans la section suivante.


## 6. Déploiement continu avec Flux

Flux assure la synchronisation automatique des manifestes Kubernetes présents dans le dossier `k8s/` de la branche `main`.

### Prérequis

Avant d'installer Flux, vérifier que :

- Le cluster Kubernetes est opérationnel.
- Prometheus et Grafana sont installés.
- La CRD `ServiceMonitor` est disponible.
- Le namespace `step-by-step` existe.
- Le Secret PostgreSQL `db-credentials` est créé.

### Installation

Depuis la racine du dépôt :

```bash
bash scripts/install-flux.sh
```

### Vérifier la synchronisation

```bash
kubectl -n flux-system get gitrepositories
kubectl -n flux-system get kustomizations
```

Les ressources doivent afficher un statut `READY=True`.

Surveiller le déploiement de l'application :

```bash
kubectl -n step-by-step get pods -w
```

Attendre la disponibilité de PostgreSQL et du backend :

```bash
kubectl -n step-by-step rollout status statefulset/db --timeout=180s
kubectl -n step-by-step rollout status deployment/step-by-step --timeout=180s
```

### Tester l'application

Dans un premier terminal :

```bash
kubectl -n step-by-step port-forward svc/app 8085:80
```

Dans un second terminal :

```bash
curl http://localhost:8085/actuator/health
```

L'application doit retourner un statut `UP`.

### Tester via l'Ingress (front + API)

L'Ingress expose l'application sur les ports 80/443 de la machine (mapping de `scripts/kind-config.yaml`) :

```bash
curl -I http://localhost/            # frontend Vue (200, text/html)
curl http://localhost/api/tasks      # API du backend (200, JSON)
```

L'application s'ouvre dans le navigateur sur <http://localhost/>. L'actuator n'est pas exposé par l'Ingress
(seul `/api` est routé vers le backend).

### Tester l'auto-scaling et le zéro-downtime

```bash
kubectl -n step-by-step get hpa,pods -w      # terminal 1
./scripts/load-test.sh 3m                    # terminal 2 : charge k6, le HPA passe de 2 à 5 pods
./scripts/test-resilience.sh                 # self-healing + rolling restart sous trafic (voir docs/tests-resilience.md)
```

### Fonctionnement du GitOps

Flux surveille la branche `main` et synchronise les manifestes Kubernetes toutes les minutes.

Lorsqu'une nouvelle version du backend est publiée par la CI, le tag de l'image est mis à jour automatiquement dans `k8s/20-app.yaml`.

Flux détecte cette modification et déclenche un rolling update du Deployment Kubernetes.

Les modifications des ressources gérées par Flux doivent être réalisées dans Git afin de garantir la cohérence entre l'état déclaré et l'état réel du cluster.

Avec `prune: true`, les ressources précédemment gérées par Flux et supprimées de Git sont également supprimées du cluster.


## 7. Observabilité applicative

Le backend expose les métriques Prometheus sur :

`/actuator/prometheus`

Le fichier `k8s/50-servicemonitor.yaml` configure la collecte des métriques des réplicas Spring Boot par Prometheus.

Les métriques comprennent notamment :

- Le trafic HTTP.
- Les erreurs HTTP 5xx.
- La consommation CPU.
- La consommation mémoire.
- La disponibilité de la collecte.
- La latence p95, calculée à partir des histogrammes Micrometer.

Le dashboard **Step by Step - SRE Golden Signals** est provisionné automatiquement dans Grafana grâce à la ConfigMap `k8s/60-grafana-dashboard.yaml` et au sidecar Grafana.

Flux applique automatiquement le ServiceMonitor et la ConfigMap du dashboard lors de la synchronisation du dossier `k8s/`.

Mode d'emploi du dashboard (lecture des panneaux, scénario de démonstration, diagnostic) : [docs/observabilite.md](observabilite.md).

### Vérifier la collecte

```bash
kubectl -n monitoring get servicemonitor
kubectl -n monitoring get configmap -l grafana_dashboard=1
```

Dans Prometheus, ouvrir la page `/targets` et vérifier que les cibles du backend Spring Boot sont UP.

Dans Grafana, ouvrir le dashboard **Step by Step - SRE Golden Signals** et générer quelques requêtes HTTP vers l'application afin d'alimenter les métriques.

La présence effective des données doit être vérifiée après le déploiement de l'image applicative.

## 8. Commandes de diagnostic

Afficher les pods :

```bash
kubectl get pods -A
```

Consulter les logs du backend :

```bash
kubectl -n step-by-step logs deployment/step-by-step --tail=100
```

Consulter les événements :

```bash
kubectl -n step-by-step get events --sort-by=.lastTimestamp
```

Inspecter un pod :

```bash
kubectl -n step-by-step describe pod NOM_DU_POD
```

Vérifier les ressources :

```bash
kubectl top nodes
kubectl top pods -n step-by-step
```

### Problèmes fréquents

| Problème | Vérification |
|---|---|
| Docker inaccessible | Démarrer Docker Desktop et vérifier `docker info` |
| Cluster introuvable | Vérifier `kind get clusters` et `kubectl config current-context` |
| metrics-server indisponible | Vérifier son Deployment dans `kube-system` |
| PostgreSQL ne démarre pas | Vérifier le Secret `db-credentials` et les logs de `db-0` |
| Backend non prêt | Vérifier les logs Spring Boot, la connexion PostgreSQL et les sondes |
| Grafana inaccessible | Vérifier le pod Grafana et le port-forward |
| Métriques absentes | Vérifier `/actuator/prometheus` et les targets Prometheus |
| Port 80 ou 443 occupé | Vérifier les applications et conteneurs utilisant ces ports |


### Diagnostic Flux

Vérifier les composants Flux :

```bash
kubectl get pods -n flux-system
```

Vérifier les sources Git :

```bash
kubectl -n flux-system get gitrepositories
```

Vérifier la synchronisation des manifestes :

```bash
kubectl -n flux-system get kustomizations
```

Consulter les logs des contrôleurs :

```bash
kubectl -n flux-system logs deployment/source-controller --tail=100
kubectl -n flux-system logs deployment/kustomize-controller --tail=100
```

## 9. Précautions

- Ne jamais versionner les mots de passe ou les Secrets Kubernetes.
- Vérifier le contexte Kubernetes avant toute opération.
- Ne pas supprimer le cluster kind sans sauvegarder les données nécessaires.
- Les données PostgreSQL utilisent un volume Kubernetes local ; la suppression du cluster peut entraîner leur perte.
- Le stockage Prometheus et Grafana de cette installation est destiné au développement et n'est pas configuré pour une conservation durable.
- Les options de sécurité spécifiques à kind ne doivent pas être reprises telles quelles en production.