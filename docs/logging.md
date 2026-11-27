
# Centralisation des logs Kubernetes

## 1. Objectif

La centralisation des logs permet de consulter les journaux des composants
applicatifs et techniques depuis une interface unique, afin de faciliter
la supervision et le diagnostic des incidents.

## 2. Architecture et choix techniques

La solution repose sur trois composants :

- **Grafana Alloy** : découverte des pods et collecte des logs via l'API Kubernetes.
- **Grafana Loki** : centralisation, indexation des labels et stockage des logs.
- **Grafana** : consultation et recherche des logs avec Grafana Explore et LogQL.

Ces composants sont déployés dans le namespace `monitoring`.

| Composant | Version du chart Helm | Version applicative |
|---|---|---|
| Loki | 21.6.3 | 3.7.10 |
| Alloy | 1.16.0 | 1.22.0 |

Loki est déployé en mode monolithique avec un seul réplica,
un volume persistant de 2 Gio et une rétention configurée à 24 heures.

Cette architecture légère est adaptée à notre cluster kind local.

## 3. Prérequis

- Cluster Kubernetes `kind-step-by-step` opérationnel.
- Prometheus et Grafana installés.
- Helm et kubectl disponibles.
- Contexte Kubernetes `kind-step-by-step`.

## 4. Installation

Depuis la racine du dépôt :

```bash
bash scripts/install-logging.sh
```

Le script configure les dépôts Helm et installe Loki et Alloy
avec leurs versions épinglées.

Il peut être réexécuté pour mettre à jour les installations existantes.

Les fichiers de configuration sont :

- `scripts/values-loki.yaml`
- `scripts/values-alloy.yaml`

La source de données Loki est provisionnée automatiquement dans Grafana
par la ConfigMap `k8s/80-loki-datasource.yaml`, gérée par Flux.

## 5. Vérification

```bash
helm list -n monitoring
kubectl get pods -n monitoring
kubectl get pvc -n monitoring
```

Vérifier que Loki, sa gateway et Alloy sont opérationnels.

Consulter les logs du collecteur :

```bash
kubectl -n monitoring logs deployment/alloy -c alloy --tail=40
```

## 6. Consultation des logs

Exposer Grafana localement :

```bash
kubectl -n monitoring port-forward svc/monitoring-grafana 3000:80
```

Ouvrir http://localhost:3000, accéder à Explore et sélectionner Loki
comme source de données.

Utiliser le mode Code pour saisir les requêtes LogQL.

### Tous les logs applicatifs

```logql
{namespace="step-by-step"}
```

### Backend Spring Boot

```logql
{namespace="step-by-step", container="app"}
```

### Frontend Vue / NGINX

```logql
{namespace="step-by-step", container="front"}
```

### PostgreSQL

```logql
{namespace="step-by-step", container="postgres"}
```

### Ingress NGINX

```logql
{namespace="ingress-nginx"}
```

### Flux GitOps

```logql
{namespace="flux-system"}
```

### Recherche par niveau de gravité

Alloy extrait le niveau de chaque ligne (étape `loki.process "levels"` de `scripts/values-alloy.yaml`)
et l'ajoute comme label **`level`** :

| Source | Valeurs de `level` |
|---|---|
| Backend Spring Boot | `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR` |
| PostgreSQL | `LOG`, `INFO`, `NOTICE`, `WARNING`, `ERROR`, `FATAL`, `PANIC` (noms propres à PostgreSQL) |

```logql
{namespace="step-by-step", level="ERROR"}                      # erreurs de l'application
{namespace="step-by-step", level=~"WARN|WARNING"}               # avertissements (Spring + PostgreSQL)
{namespace="step-by-step", container="postgres", level=~"ERROR|FATAL"}
```

Dans Grafana Explore, le label `level` apparaît dans le *Label browser* : on combine service (`app`, `container`),
date (sélecteur de période) et gravité sans écrire de requête.

Vérifié sur kind (27/11) : un redémarrage de PostgreSQL produit des lignes `level="WARN"` côté backend
(HikariPool) et `level="LOG"` côté PostgreSQL.

### Recherche textuelle (lignes sans niveau)

Les composants sans niveau reconnu (logs d'accès nginx, par exemple) restent cherchables par texte :

```logql
{namespace="step-by-step"} |~ "(?i)error"
```

### Exclusion des sondes Kubernetes

```logql
{namespace="step-by-step", container="front"} != "kube-probe"
```

Le sélecteur temporel de Grafana permet de rechercher les événements
sur une période donnée.

Les recherches d'erreurs et d'avertissements utilisent des filtres
textuels sur le contenu des messages.

## 7. Résultats des tests

Tests réalisés le 27 novembre 2026 sur Kubernetes 1.37.0.

| Test | Résultat |
|---|---|
| Validation Helm des configurations | Réussi |
| Installation de Loki | Réussi |
| Installation d'Alloy | Réussi |
| Création du volume persistant Loki | Réussi |
| Découverte des pods applicatifs et techniques | Réussi |
| Consultation des logs dans Grafana Explore | Réussi |
| Réexécution du script d'installation | Réussi |

Alloy a ouvert des flux de collecte pour le backend, le frontend,
PostgreSQL, Ingress, Flux et les composants techniques Kubernetes.

La consultation des logs dans Grafana Explore a confirmé
le fonctionnement de la chaîne de centralisation.

La consommation mémoire totale du cluster après l'installation
de Loki et Alloy était d'environ 5 016 Mio, soit 65 %.

## 8. Limites et précautions

- Loki utilise un seul réplica : il n'est pas hautement disponible.
- Le stockage local est destiné au développement et à la démonstration.
- La suppression du cluster peut entraîner la perte des logs.
- Loki n'est pas exposé publiquement et ne possède pas
  de mécanisme d'authentification propre dans cette configuration.
- Le niveau n'est extrait que pour les formats connus (Spring Boot, PostgreSQL) ;
  les autres composants se cherchent par texte.
- Ne jamais écrire de mots de passe ou de jetons dans les logs.
- Le tracing distribué est hors périmètre, après échange avec le professeur.

## 9. Conclusion

L'intégration de Loki et Grafana Alloy complète notre infrastructure
d'observabilité Kubernetes.

Les métriques sont centralisées dans Prometheus et les logs dans Loki.
Grafana constitue le point d'accès commun pour la supervision
et le diagnostic des services applicatifs et techniques.
