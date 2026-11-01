# Step by Step

[🇫🇷 Version française](README.md) — the French README is the reference; this page summarises it.

Step by Step is a Java 17 / Spring Boot task manager exposed through a JSON REST API. The repository
demonstrates a complete Jenkins CI/CD chain: GitHub webhook, CI (lint, unit and integration tests,
SonarQube quality gate), versioned artefact in Nexus, automatic staging, manual approval, then
blue/green production. Prometheus, Grafana and the DORA metrics follow the deliveries.

**Team:** Géraldine and Morgan.

## 1. Prerequisites

- Docker Engine 29.x with Docker Compose v2, at least 8 GB of RAM for Docker
- A JDK 17 or newer; Maven is not required (`./mvnw`)
- Git and access to the private GitHub repository
- A smee.io channel, a webhook secret and a fine-grained GitHub token limited to the repository,
  kept in a password manager, never in Git

## 2. Start

```bash
docker compose up -d --build        # Jenkins, agent, SonarQube, Nexus, Prometheus, Grafana, DORA exporter
read -rs SMEE_URL && export SMEE_URL
docker run -d --name cicd-webhook-relay --restart unless-stopped --network cicd_cicd \
  node:22.23.4-alpine3.24 \
  npx --yes smee-client@5.0.0 --url "$SMEE_URL" --target 'http://jenkins:8080/github-webhook/?source=smee'
```

## 3. One-time configuration

1. **Jenkins** (`http://localhost:8080`): unlock, plugins of `docs/tooling/jenkins-configuration.md`,
   controller with **0 executor**, SSH agent `docker-agent` with 2 executors; System: SonarQube server
   `sonarqube` (`http://sonarqube:9000`, token `sonar-token`), quiet period `0`, GitHub shared secret
   `github-webhook-secret`, trusted library `step-by-step-lib` (`pipeline-library/README.md`);
   Multibranch job `step-by-step` with `github-token`.
2. **SonarQube** (`http://localhost:9000`): project `step-by-step`, gate `step-by-step-gate` (new code
   coverage ≥ 60 %), webhook `http://jenkins:8080/sonarqube-webhook/`.
3. **Nexus** (`http://localhost:8081`): anonymous read, `maven-releases` with redeploy disabled,
   account `nexus-deployer`.
4. **Jenkins credentials:** `docker-agent-ssh-key`, `github-token`, `github-webhook-secret`,
   `sonar-token`, `nexus-deploy-creds`, `discord-webhook`, `staging-db-creds`, `production-db-creds`.
5. **GitHub:** webhook to the smee URL (`application/json`, secret, *Pushes* and *Pull requests*);
   Actions → Workflow permissions → *Read and write* + *Allow GitHub Actions to create and approve pull
   requests* (release-please).

Checks: `scripts/check-jenkins.sh`, `check-agents.sh`, `check-sonarqube.sh`, `check-nexus.sh`,
`check-webhook.sh`, `check-no-secrets.sh`, `check-dora.sh`, `check-readme.sh`.

## 4. Ports (all on 127.0.0.1)

| Port | Service |
|------|---------|
| 8080 | Jenkins |
| 9000 | SonarQube |
| 8081 | Nexus |
| 8082 | Staging |
| 8088 | Production (nginx, blue 8083 / green 8084) |
| 9090 | Prometheus |
| 3000 | Grafana (anonymous read) |

## 5. Use the chain

Branch → push → pull request (Jenkins status `pr-merge` ✅) → **Squash and merge** → build of `main`:
Nexus publication, staging, approval by `admin`, blue/green production with automatic rollback.

```bash
curl http://localhost:8082/actuator/health     # staging
curl http://localhost:8088/api/tasks           # production API, see docs/architecture/api.md
```

DORA metrics: `bash scripts/dora-metrics.sh` (with `JENKINS_USER` / `JENKINS_TOKEN` loaded by `read -rs`),
then the Grafana dashboard "Step by Step - DORA".

## 6. Documentation

**[Demo video](https://reseauges75-my.sharepoint.com/:f:/r/personal/g_rouy_myskolae_fr/Documents/Step%20by%20Step%20-%20DEMO?d=w3f8749ca9c3a4e7887fbc8d281c30769&csf=1&web=1&e=PclZux)** (4 parts, no sound) · [Technical report](docs/rapport-technique/rapport.md) · [Demo script](docs/rapport-technique/demo.md) ·
[Environments](docs/architecture/environments.md) · [REST API](docs/architecture/api.md) ·
[DORA](docs/observability/dora.md) · [Webhook relay](docs/tooling/webhook-relay.md) ·
[Contributing](CONTRIBUTING.md)

Never run `docker compose down -v`: it deletes the Jenkins, SonarQube, Nexus and Grafana data.
