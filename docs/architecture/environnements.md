
# Environnements de déploiement

## Staging

L'environnement staging permet de valider une version de l'application avant son déploiement en production.

### Architecture

- Application : Spring Boot, port local 8082
- Base de données : PostgreSQL 16
- Réseau : Docker bridge dédié `staging`
- Stockage : volume Docker persistant `staging_db_data`
- Orchestration : Docker Compose

L'application est accessible à l'adresse :
http://localhost:8082

Le contrôle de santé est disponible sur :
http://localhost:8082/actuator/health

### Déploiement

Le script `scripts/deploy.sh` accepte deux arguments :

```bash
bash scripts/deploy.sh staging 0.1.0-SNAPSHOT
```

Les variables `STAGING_DB_USER` et `STAGING_DB_PASSWORD` doivent être fournies par l'environnement d'exécution. Aucun secret ne doit être enregistré dans Git.

L'image `step-by-step:<tag>` doit être disponible localement ou dans un registre Docker accessible.

### Idempotence

Deux déploiements successifs de la même version ont été réalisés avec succès.

Les conteneurs Spring Boot et PostgreSQL ont conservé leurs identifiants Docker, confirmant l'absence de recréation inutile.

### Gestion des erreurs

Le script refuse un tag d'image inexistant et retourne un code d'erreur non nul.

Après cet échec, l'application précédemment déployée est restée disponible et son endpoint de santé a répondu `UP`.

### Limites

Le staging est actuellement exécuté localement. L'intégration avec Jenkins et la fourniture des secrets via les credentials Jenkins restent à réaliser.
