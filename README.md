# Step by Step

[English version](README.en.md)

Application de gestion de tâches pensée pour les profils TDAH (chaque tâche est découpée en petites étapes),
**livrée en continu sur Kubernetes** : CI GitHub Actions, images sur GHCR, déploiement GitOps avec Flux,
zéro-downtime, auto-scaling et supervision Prometheus / Grafana.

**Équipe :** Géraldine, Morgan, Zhou — projet du cours *DevOps avancé* (ESGI M1, novembre 2026).
📄 **[Dossier de projet](docs/dossier-projet.md)** (architecture, choix techniques, difficultés, rôles).

## Sommaire

1. [Architecture](#1-architecture)
2. [Prérequis](#2-prérequis)
3. [Installer le cluster et déployer](#3-installer-le-cluster-et-déployer)
4. [Chaîne de livraison](#4-chaîne-de-livraison)
5. [Démonstration](#5-démonstration)
6. [Développer en local](#6-développer-en-local)
7. [Documentation](#7-documentation)

## 1. Architecture

| Composant | Technologie | Image |
|---|---|---|
| Frontend | Vue 3 + nginx non-root | `ghcr.io/devops-is-fun/step-by-step-front:<sha>` |
| Backend | Java 17 / Spring Boot, API REST, Flyway | `ghcr.io/devops-is-fun/step-by-step:<sha>` |
| Base de données | PostgreSQL 16 (StatefulSet + volume persistant) | `postgres:16.15-alpine3.24` |

```
GitHub (main) ──► GitHub Actions ──► GHCR (images publiques)
      ▲                 │
      │                 └─► commit du nouveau tag d'image dans k8s/
      │
  Flux (dans le cluster) lit main toutes les minutes et applique k8s/
      │
Cluster kind ── Ingress : /  → front (2 pods)
                          /api → backend (2 à 5 pods, HPA) → PostgreSQL
             ── Prometheus + Grafana (dashboard SRE)
```

| Dossier | Contenu |
|---|---|
| `src/`, `pom.xml` | Backend |
| `front/` | Frontend (code, tests, Dockerfile, config nginx) |
| `environments/runtime/Dockerfile` | Image du backend |
| `.github/workflows/` | CI/CD : `ci.yml` (backend), `front.yml` (frontend), `codeql.yml`, `release-please.yml` |
| `k8s/` | **Uniquement des ressources Kubernetes**, appliquées par Flux |
| `flux/` | Configuration GitOps |
| `scripts/` | Installation du cluster, tests de charge et de résilience (+ configs kind / Helm) |
| `docs/` | Documentation |

## 2. Prérequis

- Docker, [kind](https://kind.sigs.k8s.io/), `kubectl`, [Helm](https://helm.sh/)
- Ports **80** et **443** libres sur la machine (Ingress)
- Environ 8 Go de RAM disponibles pour Docker

Détail des versions et de chaque étape : **[docs/prerequis-cluster.md](docs/prerequis-cluster.md)**.

## 3. Installer le cluster et déployer

Depuis la racine du dépôt, un cluster complet se reconstruit en ~10 minutes :

```bash
./scripts/setup-cluster.sh                       # cluster kind (Kubernetes 1.37) + metrics-server

export GRAFANA_ADMIN_PASSWORD='<mot-de-passe>'   # jamais versionné
./scripts/install-monitoring.sh                  # Prometheus + Grafana

./scripts/install-ingress.sh                     # ingress-nginx
./scripts/install-logging.sh                     # Loki + Alloy (logs centralisés)

kubectl apply -f k8s/00-namespace.yaml           # Secret de la base, jamais versionné (dépôt public)
kubectl -n step-by-step create secret generic db-credentials \
  --from-literal=username=stepbystep --from-literal=password='<mot-de-passe>'

./scripts/install-flux.sh                        # Flux déploie ensuite tout k8s/ depuis main
```

Vérification :

```bash
kubectl -n flux-system get kustomizations        # READY True
kubectl -n step-by-step get pods                 # db-0, 2 pods backend, 2 pods front
curl http://localhost/api/tasks                  # l'API, via l'Ingress
```

L'application est sur **<http://localhost/>**.

## 4. Chaîne de livraison

```
PR → CI (contrôles) → merge → CI sur main → image → Trivy → GHCR → commit du tag → Flux → rolling update
```

| Étape | Backend (`ci.yml`) | Frontend (`front.yml`, si `front/` change) |
|---|---|---|
| Secrets (tout l'historique) | gitleaks | — |
| Lint | checkstyle | oxlint + eslint (`lint:ci`, sans `--fix`) |
| Tests | unitaires + intégration (PostgreSQL), couverture ≥ 60 % | Vitest |
| Dockerfile | Hadolint | Hadolint |
| Image | build, puis **Trivy** (bloquant sur HIGH/CRITICAL) | idem |
| Publication + déploiement | `main` uniquement : push GHCR, puis tag écrit dans `k8s/20-app.yaml` | idem, `k8s/30-front.yaml` |

`codeql.yml` ajoute une analyse statique de sécurité (Java + JavaScript), visible dans l'onglet *Security*.

**GitOps :** la CI ne touche jamais le cluster. Toute modification passe par Git : une modification faite à la main
avec `kubectl` est annulée par Flux. Retour arrière : `git revert` du commit `chore(deploy)` concerné.

## 5. Démonstration

| Quoi | Commande |
|---|---|
| Dashboard Grafana | `kubectl -n monitoring port-forward svc/monitoring-grafana 3000:80` → <http://localhost:3000> (voir [docs/observabilite.md](docs/observabilite.md)) |
| Auto-scaling | terminal 1 : `kubectl -n step-by-step get hpa,pods -w` — terminal 2 : `./scripts/load-test.sh 3m` |
| Logs centralisés | Grafana → *Explore* → source **Loki** : `{namespace="step-by-step", level="WARN"}` (voir [docs/logging.md](docs/logging.md)) |
| Zéro-downtime + self-healing | `./scripts/test-resilience.sh` (120 requêtes pendant un rolling restart, 0 échec attendu) |
| Déploiement continu | merger une PR, puis `kubectl -n flux-system get kustomizations -w` et `kubectl -n step-by-step get pods -w` |

## 6. Développer en local

```bash
./mvnw verify            # backend : lint, tests unitaires et d'intégration (PostgreSQL de test sur le port 5433)
cd front && npm ci && npm run lint:ci && npm test && npm run dev
```

Les conventions de travail (branches `type/sujet`, Conventional Commits, PR relues, règles GitOps) sont décrites dans [CONTRIBUTING.md](CONTRIBUTING.md).

## 7. Documentation

| Document | Contenu |
|---|---|
| [docs/dossier-projet.md](docs/dossier-projet.md) | Dossier de projet |
| [docs/prerequis-cluster.md](docs/prerequis-cluster.md) | Installation détaillée du cluster local |
| [docs/observabilite.md](docs/observabilite.md) | Utiliser et lire le dashboard Grafana |
| [docs/logging.md](docs/logging.md) | Logs centralisés (Loki + Alloy) : installation et recherche par service, date, gravité |
| [docs/demo-soutenance.md](docs/demo-soutenance.md) | Déroulé de la démonstration |
| [docs/tests-resilience.md](docs/tests-resilience.md) | Tests de résilience et de rolling update |
| [docs/architecture/api.md](docs/architecture/api.md) | API REST |
| [docs/architecture/business-rules.md](docs/architecture/business-rules.md) | Règles métier |

Le projet est la suite de notre projet CI/CD (Jenkins, Nexus, blue/green avec Docker Compose), dont la
documentation reste dans `docs/` (rapport technique, stories) et dans l'historique Git.
