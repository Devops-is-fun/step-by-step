# Installation du cluster Kubernetes local

Ce guide explique comment installer et utiliser l'environnement Kubernetes local du projet **Step by Step**.

L'environnement repose sur kind, ingress-nginx, metrics-server, Prometheus et Grafana.

Il est destiné au développement et aux démonstrations. Il ne remplace pas l'environnement de production Kubernetes à deux machines virtuelles prévu par le projet DevOps.

## 1. Prérequis

Outils nécessaires :

| Outil | Version utilisée lors des tests |
|---|---|
| Docker Desktop | 27.5.1 |
| kind | 0.33.0 |
| kubectl | 1.31.4 |
| Helm | 4.3.2 |
| Git Bash | Environnement Windows |

La configuration locale utilise Kubernetes `kindest/node:v1.31.4`.

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

## 5. Déployer PostgreSQL

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

Déployer PostgreSQL :

```bash
kubectl apply -f k8s/10-postgres.yaml
kubectl -n step-by-step rollout status statefulset/db --timeout=180s
```

Vérifier :

```bash
kubectl get pods -n step-by-step
```

Le pod `db-0` doit être `1/1 Running`.

## 6. Déployer le backend Spring Boot

```bash
kubectl apply -f k8s/20-app.yaml
kubectl -n step-by-step rollout status deployment/step-by-step --timeout=180s
```

Vérifier :

```bash
kubectl get pods -n step-by-step
```

Le déploiement utilise deux réplicas Spring Boot, avec des sondes de santé et une stratégie RollingUpdate.

### Tester l'application

Dans un terminal :

```bash
kubectl -n step-by-step port-forward svc/app 8082:80
```

Dans un autre terminal :

```bash
curl http://localhost:8082/actuator/health
```

L'endpoint doit retourner un état `UP` lorsque l'application est saine.

## 7. Observabilité applicative

Le backend expose les métriques Prometheus sur :

`/actuator/prometheus`

Un ServiceMonitor permet à Prometheus de collecter les métriques des réplicas Spring Boot lorsque la configuration correspondante est déployée.

Les métriques comprennent notamment :

- Le trafic HTTP.
- Les erreurs HTTP 5xx.
- La consommation CPU.
- La consommation mémoire.
- La disponibilité de la collecte.
- La latence p95, lorsque les histogrammes Micrometer sont activés dans l'image déployée.

Le dashboard **Step by Step - SRE Golden Signals** est provisionné par une ConfigMap Kubernetes lorsque les manifests correspondants sont disponibles.

Les fichiers du dashboard et du ServiceMonitor font l'objet de PR séparées et doivent être fusionnés avant une installation entièrement reproductible de cette partie.

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

## 9. Précautions

- Ne jamais versionner les mots de passe ou les Secrets Kubernetes.
- Vérifier le contexte Kubernetes avant toute opération.
- Ne pas supprimer le cluster kind sans sauvegarder les données nécessaires.
- Les données PostgreSQL utilisent un volume Kubernetes local ; la suppression du cluster peut entraîner leur perte.
- Le stockage Prometheus et Grafana de cette installation est destiné au développement et n'est pas configuré pour une conservation durable.
- Les options de sécurité spécifiques à kind ne doivent pas être reprises telles quelles en production.