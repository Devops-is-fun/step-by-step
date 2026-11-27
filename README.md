# Step by Step

[English version](README.en.md)

Step by Step est une application Java 17 / Spring Boot de gestion de tâches, exposée par une API REST JSON. Ce dépôt démontre une chaîne CI/CD Jenkins complète, depuis le webhook GitHub jusqu'au déploiement staging puis production blue/green, avec SonarQube, Nexus et une validation manuelle. Prometheus, Grafana et les métriques DORA permettent de suivre les déploiements et leur qualité.

**Équipe :** Géraldine et Morgan (binôme). [Répartition des stories et organisation du binôme](docs/specification/specification.md#organisation-du-binôme-depuis-le-01112026).

## Sommaire

1. [Présentation](#1-présentation)
2. [Prérequis](#2-prérequis)
3. [Démarrage](#3-démarrage)
4. [Ordre des étapes et credentials](#4-ordre-des-étapes-et-credentials)
5. [Ports](#5-ports)
6. [Démonstration de la chaîne](#6-démonstration-de-la-chaîne)
7. [Tests et couverture](#7-tests-et-couverture)
8. [Règles métier](#8-règles-métier)
9. [Dépannage](#9-dépannage)
10. [Liens et livrables](#10-liens-et-livrables)
11. [Déployer sur Kubernetes](#11-déployer-sur-kubernetes)

## 1. Présentation

L'application sert de support concret aux tests, à l'analyse statique et aux déploiements automatisés. Le `Jenkinsfile` déclaratif orchestre le pipeline sur l'agent `docker-agent` (jamais sur le contrôleur). La production utilise deux instances blue/green derrière nginx, avec vérification de santé et retour arrière en cas d'échec.

Le dépôt contient les stacks Docker Compose, les scripts de déploiement, les configurations d'observabilité et la documentation technique. Les versions sont publiées via **release-please** (GitHub Actions) à partir des Conventional Commits ; la Shared Library Jenkins est versionnée dans `pipeline-library/`.

## 2. Prérequis

- Docker Desktop avec Docker Engine **29.x** et `docker compose` **v2** (la version exacte de Compose dépend de l'installation ; vérifier avec `docker compose version`). Sous Linux, Docker Engine et Compose v2 conviennent également.
- **JDK 17** ou plus récent, configuré pour compiler en Java 17 (`java -version`).
- Git, accès au dépôt GitHub privé et connexion Internet pour télécharger images et dépendances.
- Au moins **8 Go de RAM disponibles pour Docker**.
- **Bash** pour les scripts `.sh` ; sous Windows, utiliser Git Bash (et non la commande `bash` de WSL si WSL n'est pas configuré).
- Un canal **smee.io**, un secret de webhook et un jeton GitHub fine-grained limité au dépôt. Ne jamais les enregistrer dans Git.
- Maven local non requis : utiliser `./mvnw` (ou `./mvnw.cmd` sous Windows).

## 3. Démarrage

Depuis la racine du dépôt :

```bash
docker compose up -d --build
```

Initialiser Jenkins, SonarQube, Nexus, l'agent et les credentials comme indiqué à la section 4 **avant** de déclencher un déploiement. La stack de base démarre Jenkins, SonarQube, Nexus, l'agent, Prometheus, Grafana et l'exporteur DORA.

**Relais webhook** (sur l'hôte Docker, après création du canal smee.io) :

```bash
read -rs SMEE_URL && export SMEE_URL
docker run -d --name cicd-webhook-relay --restart unless-stopped --network cicd_cicd \
  node:22.23.4-alpine3.24 \
  npx --yes smee-client@5.0.0 --url "$SMEE_URL" --target 'http://jenkins:8080/github-webhook/?source=smee'
```

Le `?source=smee` évite une réponse HTTP 405. Après `docker compose down`, recréer le relais si le réseau Docker a été recréé. Voir [la procédure webhook](docs/tooling/webhook-relay.md).

**Application locale** : démarrer d'abord la base de test, puis compiler et lancer l'application (remplacer la version du JAR par celle réellement produite dans `target/`) :

```bash
docker compose -f src/test/resources/compose-test.yml up -d --wait
./mvnw -B verify
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/stepbystep_test \
SPRING_DATASOURCE_USERNAME=stepbystep SPRING_DATASOURCE_PASSWORD=test \
java -jar target/step-by-step-*.jar --server.port=8090
```

La commande `java -jar` ci-dessus est destinée à un shell qui développe le joker ; si nécessaire, remplacer `*` par le nom exact du JAR. Tester `curl http://localhost:8090/actuator/health`. La base de test utilise uniquement des identifiants jetables. Sous Windows, lancer le wrapper `./mvnw.cmd` et définir les variables avec la syntaxe PowerShell.

**Utiliser l'API** : après déploiement, `curl http://localhost:8088/api/tasks` liste les tâches en production ; staging répond sur `http://localhost:8082/api/tasks`. Pour créer une tâche ou une sous-étape, voir [la documentation des endpoints et des exemples JSON](docs/architecture/api.md). L'API ne demande pas d'authentification et n'est exposée que sur `127.0.0.1` : ne pas ouvrir ces ports publiquement.

## 4. Ordre des étapes et credentials

Les configurations initiales des outils sont réalisées dans leurs interfaces web, **pas dans les fichiers Git**. Ordre conseillé pour une installation vierge :

1. **Jenkins** : ouvrir `http://localhost:8080`, lire le mot de passe initial avec `docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword`, installer les plugins recommandés, créer l'administrateur et définir le contrôleur à **0 exécuteur**. Configurer l'agent SSH `docker-agent` à **2 exécuteurs**.
2. **SonarQube** : ouvrir `http://localhost:9000`, changer `admin/admin`, créer le projet `step-by-step`, associer la Quality Gate `step-by-step-gate` (couverture du nouveau code ≥ **60 %**) et générer le jeton d'analyse. Puis **Administration → Configuration → Webhooks → Create** : nom `jenkins`, URL `http://jenkins:8080/sonarqube-webhook/` (sans ce webhook, le stage *Quality gate* attend jusqu'à son délai maximal).
3. **Nexus** : ouvrir `http://localhost:8081`, lire le mot de passe initial avec `docker compose exec nexus cat /nexus-data/admin.password`, terminer l'assistant, autoriser la lecture anonyme, conserver `maven-releases` en mode *Disable redeploy* et créer le compte `nexus-deployer`.
4. **Jenkins → Manage Jenkins → Credentials** : créer les credentials suivants **dans cet ordre de dépendance** :

| Ordre | ID Jenkins | Type | Usage |
|---|---|---|---|
| 1 | `docker-agent-ssh-key` | SSH Username with private key | Connexion à l'agent (`jenkins`) |
| 2 | `github-token` | Username with password | Scan Multibranch et statut des PR |
| 3 | `github-webhook-secret` | Secret text | Vérification des webhooks GitHub |
| 4 | `sonar-token` | Secret text | Analyse SonarQube |
| 5 | `nexus-deploy-creds` | Username with password | Publication Maven dans Nexus |
| 6 | `discord-webhook` | Secret text | Notifications du pipeline |
| 7 | `staging-db-creds` | Username with password | Base PostgreSQL staging |
| 8 | `production-db-creds` | Username with password | Base PostgreSQL production |

Les **six premiers** couvrent les credentials transverses ; les **deux derniers** sont indispensables aux déploiements. L'ordre du tableau est un ordre de configuration recommandé, non une séquence imposée par Jenkins. Les mots de passe, clés privées, jetons, URL smee et secret GitHub restent dans un gestionnaire de mots de passe ou dans Jenkins Credentials, jamais dans le dépôt.

5. Dans **Manage Jenkins → System** : section **SonarQube servers** → Name `sonarqube` (nom exact attendu par le `Jenkinsfile`), Server URL `http://sonarqube:9000`, token `sonar-token` ; champ **Quiet period** à `0`. Installer les plugins listés dans [la configuration Jenkins](docs/tooling/jenkins-configuration.md), puis créer le job **Multibranch Pipeline** `step-by-step` avec le dépôt GitHub et le credential `github-token`. Configurer la Shared Library selon [sa documentation](pipeline-library/README.md).
6. Dans GitHub → Settings → Webhooks, définir l'URL du canal smee.io, le secret partagé, `application/json`, et les événements **Pushes** et **Pull requests**. Dans Jenkins, référencer `github-webhook-secret` comme secret partagé. Pour **release-please** : GitHub → Settings → Actions → General → Workflow permissions → **Read and write permissions** et **Allow GitHub Actions to create and approve pull requests**.
7. Vérifier l'installation avec `scripts/check-jenkins.sh`, `scripts/check-agents.sh`, `scripts/check-sonarqube.sh`, `scripts/check-nexus.sh` et `scripts/check-webhook.sh` en chargeant les variables d'environnement requises **sans les afficher**.

Documentation détaillée : [Jenkins](docs/tooling/jenkins-configuration.md), [agent](docs/tooling/jenkins-agent.md), [SonarQube](docs/tooling/sonarqube.md), [Nexus](docs/tooling/nexus.md), [webhook](docs/tooling/webhook-relay.md), [environnements](docs/architecture/environments.md).

## 5. Ports

| Port hôte | Service | Stack / fichier | Accès |
|---|---|---|---|
| `8080` | Jenkins | `docker-compose.yml` | `http://localhost:8080` |
| `9000` | SonarQube | `docker-compose.yml` | `http://localhost:9000` |
| `8081` | Nexus | `docker-compose.yml` | `http://localhost:8081` |
| `8082` | Application staging | `environments/staging/docker-compose.yml` | `http://localhost:8082` |
| `8088` | nginx production (blue/green) | `environments/production/docker-compose.yml` | `http://localhost:8088` |
| `8083` | Instance production blue | `environments/production/docker-compose.yml` | `http://localhost:8083` |
| `8084` | Instance production green | `environments/production/docker-compose.yml` | `http://localhost:8084` |
| `9090` | Prometheus | `docker-compose.yml` | `http://localhost:9090/targets` |
| `3000` | Grafana | `docker-compose.yml` | `http://localhost:3000` |

Ports complémentaires : `50000` (Jenkins, local), `5433` (PostgreSQL de test), `8090` (application locale). Les ports de la stack sont liés à **`127.0.0.1`** ; ils ne doivent pas être exposés directement à Internet. Grafana autorise la consultation anonyme en lecture ; l'administration utilise initialement `admin/admin`, à modifier immédiatement.

Prometheus collecte les applications via leurs **réseaux Docker**, et non `host.docker.internal` :

```bash
./scripts/connect-prometheus.sh stepbystep-staging
./scripts/connect-prometheus.sh stepbystep-production
```

Ces commandes sont à relancer après un redémarrage/recréation de Prometheus si les connexions réseau ont disparu. Voir [observabilité et DORA](docs/observability/dora.md).

## 6. Démonstration de la chaîne

Exemple de parcours **en huit commandes/étapes**, après configuration des credentials et du job Multibranch :

```bash
# 1. Créer une branche de démonstration
git switch -c demo-readme-validation
# 2. Apporter une modification sans risque (ex. documentation), puis préparer le commit
git add README.md
# 3. Créer un commit Conventional Commit
git commit -m "docs: verify readme reproduction"
# 4. Pousser la branche : GitHub → smee.io → Jenkins
git push -u origin demo-readme-validation
# 5. Observer les services CI/CD
docker compose ps
# 6. Après revue, fusionner la PR dans main depuis GitHub, puis récupérer main
git switch main && git pull origin main
# 7. Vérifier staging après le build main
curl -fsS http://localhost:8082/actuator/health
# 8. Après approbation manuelle du déploiement dans Jenkins, vérifier production
curl -fsS http://localhost:8088/actuator/health
```

**Important :** les commandes 4 à 8 ne sont pas entièrement automatisées par le shell. Créer et fusionner la PR dans GitHub, attendre les étapes Jenkins (lint, tests, SonarQube, publication Nexus, staging) puis approuver manuellement le stage de production avec le compte autorisé (`admin`). Le déploiement réutilise **le même JAR de Nexus** et l'image construite depuis celui-ci. En cas d'échec de la nouvelle couleur, le proxy conserve/reprend l'ancienne. Ne pas pousser une modification factice uniquement pour la démonstration sans validation du binôme.

Les métriques DORA sont calculées depuis Jenkins et Git avec `bash scripts/dora-metrics.sh`, vérifiées par `bash scripts/check-dora.sh`, exportées vers Prometheus et visualisées dans Grafana. Le CSV est dans `observability/dora/dora-metrics.csv` : fréquence, lead time commit, lead time intégration, taux d'échec, temps de restauration (les deux lead times sont deux variantes d'une métrique DORA).

## 7. Tests et couverture

```bash
./mvnw -B test
./mvnw -B verify
```

`verify` exécute les vérifications Maven, tests unitaires et tests d'intégration (avec PostgreSQL de test selon la configuration). Démarrer au préalable la base de test avec `docker compose -f src/test/resources/compose-test.yml up -d --wait` si les tests d'intégration l'exigent. Les rapports JUnit et JaCoCo sont publiés dans Jenkins. Le seuil JaCoCo et la Quality Gate SonarQube visent **60 %** de couverture. Le lint Checkstyle et la Quality Gate SonarQube bloquent les changements non conformes (SpotBugs n'est pas branché, voir le rapport technique).

Vérifications supplémentaires : `bash scripts/check-no-secrets.sh`, `bash scripts/check-dora.sh`, `bash scripts/check-readme.sh`.

## 8. Règles métier

| Règle | Comportement | État |
|---|---|---|
| `REG-01` | Une sous-étape dure entre 1 et 30 minutes ; sinon l'application invite à la découper. | Implémentée |
| `REG-02` | Au plus une tâche non terminée peut être planifiée à une date donnée. | Implémentée |
| `REG-03` | Une tâche terminée ne peut plus être modifiée. | Prévue (story 028) |
| `REG-04` | Un rappel n'est envoyé que s'il reste au moins une sous-étape non faite. | Prévue (story 028) |
| `REG-05` | La vue « aujourd'hui » renvoie une seule sous-étape : la première non faite. | Prévue (story 028) |
| `REG-06` | Le libellé d'une sous-étape est une phrase courte de 1 à 80 caractères. | Prévue (story 028) |
| `REG-07` | La durée totale d'une tâche est la somme des durées estimées de ses sous-étapes. | Prévue (story 028) |

Voir [règles métier](docs/architecture/business-rules.md) et [spécification](docs/specification/specification.md).

## 9. Dépannage

| Symptôme rencontré | Cause probable et parade |
|---|---|
| Jenkins refuse le webhook avec **405** | Le relais smee supprime le slash final : ajouter `?source=smee` à l'URL cible. |
| Le relais renvoie **400** | Secret partagé GitHub/Jenkins différent : vérifier `github-webhook-secret` et le secret GitHub sans les afficher. |
| Jenkins indique **Bad credentials** pour GitHub | Jeton fine-grained expiré ou permissions insuffisantes : renouveler `github-token`, limité au dépôt. |
| L'agent Jenkins est **offline** | Vérifier `docker compose ps`, la clé `docker-agent-ssh-key`, le nom `jenkins-agent` et la configuration SSH de l'agent. |
| `bash` lance WSL sous Windows et échoue | Utiliser Git Bash : `& "C:\Program Files\Git\bin\bash.exe" scripts/check-readme.sh`. |
| Prometheus affiche staging/production **DOWN** | Vérifier `/actuator/prometheus`, la version déployée et les réseaux Docker ; relancer `scripts/connect-prometheus.sh` pour chaque stack. |
| Nexus refuse une nouvelle publication avec **409** | Le dépôt `maven-releases` interdit de remplacer une version : publier une **nouvelle version**. |
| Un déploiement blue/green échoue au healthcheck | Inspecter `docker compose -p stepbystep-production ps` et les logs ; le proxy doit rester sur la couleur précédente. |

Ne jamais utiliser `docker compose down -v` pour un dépannage ordinaire : cela supprime aussi les volumes persistants de Jenkins, SonarQube, Nexus et Grafana.

## 10. Liens et livrables

- [Rapport technique](docs/rapport-technique/rapport.md) et [scénario de démonstration](docs/rapport-technique/demo.md).
- **[Vidéo de démonstration](https://reseauges75-my.sharepoint.com/:f:/r/personal/g_rouy_myskolae_fr/Documents/Step%20by%20Step%20-%20DEMO?d=w3f8749ca9c3a4e7887fbc8d281c30769&csf=1&web=1&e=PclZux)** (4 parties, sans son) : [script](docs/rapport-technique/demo.md) et [commentaires scène par scène](docs/rapport-technique/video-commentary.md).
- [Spécification et organisation du binôme](docs/specification/specification.md).
- [Architecture des environnements](docs/architecture/environments.md) ; [API REST](docs/architecture/api.md).
- [Observabilité et métriques DORA](docs/observability/dora.md) ; [dashboard Grafana](observability/grafana/dashboards/dora.json).
- [Configuration Jenkins](docs/tooling/jenkins-configuration.md), [SonarQube](docs/tooling/sonarqube.md), [Nexus](docs/tooling/nexus.md), [relais webhook](docs/tooling/webhook-relay.md).

**Preuves à joindre avant de marquer la story Done :** relecture extérieure datée, sortie console d'une reproduction complète sur une installation propre, `scripts/check-readme.sh` réussi, lien vidéo final vérifié.

## 11. Déployer sur Kubernetes

Prérequis : `kubectl` connecté à un cluster (en local : `kind create cluster --name step-by-step`). Depuis la racine du dépôt :

```bash
# 1. Namespace, puis Secret de la base créé à la main (jamais versionné : le dépôt est public)
kubectl apply -f k8s/00-namespace.yaml
kubectl -n step-by-step create secret generic db-credentials \
  --from-literal=username=stepbystep --from-literal=password='<mot-de-passe>'

# 2. PostgreSQL + application (image ghcr.io/devops-is-fun/step-by-step:<sha du commit>)
kubectl apply -f k8s/
kubectl -n step-by-step get pods -w        # attendre db-0 et les 2 pods step-by-step en READY 1/1

# 3. Accès temporaire, en attendant l'Ingress du cluster cible
kubectl -n step-by-step port-forward svc/app 8085:80
curl localhost:8085/actuator/health        # {"status":"UP",...}
```

Nouvelle version : `kubectl -n step-by-step set image deployment/step-by-step app=ghcr.io/devops-is-fun/step-by-step:<nouveau-sha>` (rolling update sans interruption).

### Auto-scaling (HPA) et test de charge

`k8s/70-hpa.yaml` : de **2 à 5 pods** selon le CPU (cible 70 % de la request de 250m). Prérequis : metrics-server (`scripts/setup-cluster.sh`). Le Deployment ne fixe volontairement pas `replicas` : c'est le HPA qui décide (sinon Flux le remettrait à sa valeur toutes les minutes).

```bash
kubectl -n step-by-step get hpa,pods -w     # terminal 1 : suivre le scaling
./scripts/load-test.sh 3m                   # terminal 2 : charge k6 depuis le cluster
```

Le test échoue si plus de 1 % des requêtes échouent : relancé pendant un rolling update, il sert aussi de preuve du zéro-downtime.
