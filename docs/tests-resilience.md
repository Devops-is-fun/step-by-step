# Tests de résilience Kubernetes

## 1. Objectif

Ce document présente les tests de résilience réalisés sur l'application Step by Step déployée sur un cluster Kubernetes local avec kind.

Les tests permettent de vérifier :

- L'auto-réparation des pods (*self-healing*).
- Le remplacement progressif des pods (*rolling restart*).
- La continuité des réponses HTTP pendant un redémarrage progressif.

## 2. Prérequis

- Cluster kind `step-by-step` opérationnel.
- Namespace Kubernetes `step-by-step`.
- Backend Spring Boot déployé avec au moins deux réplicas.
- Service Kubernetes `app` accessible depuis le cluster.
- Outil `kubectl` installé et configuré.
- Git Bash pour exécuter le script sous Windows.

Le Deployment utilise la stratégie suivante :

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1
    maxUnavailable: 0
```

Cette configuration permet de créer un pod supplémentaire pendant le déploiement et demande à Kubernetes de conserver les réplicas disponibles pendant leur remplacement.

## 3. Exécution des tests

Depuis la racine du dépôt :

```bash
bash scripts/test-resilience.sh
```

**Attention :** ce script provoque volontairement la suppression et le redémarrage des pods du backend. Il doit être exécuté uniquement sur un environnement de test.

Le script utilise le contexte Kubernetes `kind-step-by-step` afin de limiter les risques d'exécution sur un autre cluster.

## 4. Test de self-healing

Le script sélectionne un pod backend et le supprime volontairement.

Kubernetes doit automatiquement créer un nouveau pod afin de rétablir le nombre de réplicas défini dans le Deployment.

**Résultat observé :**

- Pod backend supprimé avec succès.
- Remplacement automatique effectué par Kubernetes.
- Deployment rétabli à 2/2 réplicas prêts.

**Statut : réussi.**

## 5. Test de rolling restart

Le script déclenche un redémarrage progressif :

```bash
kubectl -n step-by-step rollout restart deployment/step-by-step
```

Il attend ensuite la fin du déploiement avec `kubectl rollout status`.

**Résultat observé :**

- Redémarrage progressif exécuté.
- Nouveaux pods créés.
- Deployment revenu à 2/2 réplicas disponibles.

**Statut : réussi.**

## 6. Test de disponibilité HTTP

Un pod temporaire utilisant l'image `curlimages/curl:8.12.1` envoie des requêtes vers :

```text
http://app/actuator/health
```

Les requêtes sont envoyées toutes les 0,5 seconde environ pendant le rolling restart.

Ce test utilise directement le Service Kubernetes et évite les interruptions artificielles liées à `kubectl port-forward`.

### Résultats obtenus

| Indicateur | Résultat |
|---|---|
| Requêtes envoyées | 120 |
| Réponses HTTP 200 | 120 |
| Échecs | 0 |
| Disponibilité HTTP observée | 100 % |

**Statut : réussi.**

Aucune erreur HTTP n'a été observée pendant cette exécution du test.

## 7. Limites

Ces résultats démontrent la continuité des réponses HTTP pendant la fenêtre de test, mais ne garantissent pas une disponibilité absolue.

Les tests ne couvrent pas :

- Une panne complète d'un nœud Kubernetes.
- Une panne de PostgreSQL.
- Une charge importante ou prolongée.
- Une indisponibilité réseau externe.
- Un véritable changement de version de l'image applicative.

Le rolling restart valide le mécanisme de remplacement progressif, mais ne remplace pas un test de mise à jour applicative complète.

## 8. Conclusion

Les tests confirment que le cluster Kubernetes local permet de rétablir automatiquement les réplicas du backend et d'effectuer un redémarrage progressif.

Lors du test HTTP réalisé, les 120 requêtes ont reçu une réponse HTTP 200, sans interruption observée.

Ces résultats constituent une première validation de la résilience applicative et du fonctionnement de la stratégie RollingUpdate.