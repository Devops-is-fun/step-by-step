# Observabilité : utiliser le dashboard Grafana

Le dashboard **« Step by Step - SRE Golden Signals »** suit les 4 signaux SRE de l'application
(latence, trafic, erreurs, saturation). Il est déployé par Flux comme une ConfigMap
(`k8s/60-grafana-dashboard.yaml`) et chargé automatiquement par Grafana.

## 1. Ouvrir Grafana

```bash
kubectl -n monitoring port-forward svc/monitoring-grafana 3000:80
```

- URL directe : <http://localhost:3000/d/step-by-step-sre/step-by-step-sre-golden-signals>
- Utilisateur `admin`, mot de passe : celui de `GRAFANA_ADMIN_PASSWORD` choisi à l'installation
  (`scripts/install-monitoring.sh`, voir `docs/prerequis-cluster.md`).
- En haut à droite : période **« Last 15 minutes »** pour une démonstration ; le dashboard se rafraîchit toutes les 15 s.

> Si une autre application occupe déjà le port 3000 sur la machine, utiliser un autre port local
> (`3001:80`) : sinon on ouvre un autre Grafana sans s'en rendre compte.

## 2. Les 6 panneaux

Les métriques viennent de Prometheus : `/actuator/prometheus` du backend (Micrometer, découvert par le
ServiceMonitor `k8s/50-servicemonitor.yaml`) et cAdvisor (intégré au kubelet) pour les conteneurs.

| Panneau | Signal | Ce qu'il mesure | Repère mesuré sur kind (27/11/2026) |
|---|---|---|---|
| **Latence p95** | Latence | Temps sous lequel répondent 95 % des requêtes HTTP du backend | ~5 ms au calme, ~4 ms sous charge k6 |
| **Trafic HTTP** | Trafic | Requêtes par seconde reçues par le backend | quelques req/s au calme, plusieurs milliers sous k6 |
| **Erreurs HTTP 5xx (%)** | Erreurs | Part des réponses en erreur serveur (5xx) | **0 %**, y compris pendant les rolling updates |
| **CPU backend** | Saturation | CPU consommé par les pods backend (en cœurs) | quasi nul au calme ; c'est lui qui déclenche le HPA |
| **Mémoire backend** | Saturation | Mémoire utilisée par les pods backend | ~300 Mo par pod (JVM) ; augmente quand le HPA ajoute des pods |
| **Disponibilité du scrape** | (santé du monitoring) | 1 = Prometheus arrive à lire les métriques du backend | **1** en permanence |

Les erreurs **4xx** (ex. tâche inconnue → 404, règle métier REG-01/REG-02 → 400) ne sont **pas** comptées
dans le panneau 5xx : ce sont des refus normaux du métier, pas des pannes.

## 3. Scénario de démonstration

Deux terminaux + le dashboard ouvert :

```bash
# Terminal 1 : suivre le nombre de pods
kubectl -n step-by-step get hpa,pods -w

# Terminal 2 : envoyer de la charge depuis le cluster (k6, 20 utilisateurs virtuels)
./scripts/load-test.sh 3m
```

Ce qu'on observe :

1. **Trafic** et **CPU** montent en quelques secondes.
2. Le HPA passe de **2 à 5 pods** (terminal 1) ; la **mémoire** augmente avec les nouveaux pods.
3. La **latence p95** reste basse et les **erreurs 5xx** restent à **0 %**.
4. Fin de la charge : CPU et trafic retombent ; après **2 minutes** de calme, le HPA revient à **2 pods**.

## 4. Diagnostiquer avec le dashboard

| Ce qu'on voit | Piste |
|---|---|
| Latence p95 ↑, CPU ↑ | Saturation : le HPA doit ajouter des pods ; s'il est au maximum (5), augmenter `maxReplicas` ou les ressources |
| Latence p95 ↑, CPU stable | Le problème est ailleurs : base de données (vérifier le pod `db-0`), réseau |
| Erreurs 5xx > 0 % | Regarder les logs du backend (`kubectl -n step-by-step logs deploy/step-by-step`) et l'état de PostgreSQL |
| Disponibilité du scrape = 0 / panneaux vides | Prometheus ne lit plus l'app : pods backend arrêtés, ou ServiceMonitor/label `monitoring: enabled` modifiés |
| Trafic à 0 alors que l'app est utilisée | Les requêtes n'arrivent pas au backend : vérifier l'Ingress et le Service `app` |
