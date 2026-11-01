# Deployment environments

Staging and production run on the same Docker host as the CI/CD tooling, as two Docker Compose stacks.
Both deploy **the artefact published in Nexus** by the CI (`step-by-step-<version>.jar`): the image
`step-by-step:<version>` is built once from that jar (`scripts/build-runtime-image.sh`) and reused,
so production runs exactly what staging validated.

The first version of this page and of the staging stack was written by Morgan (story 015, PR #6):
idempotency and failure tests below come from that work. The scripts were then adapted to run inside
the Jenkins agent and to start from the Nexus artefact.

## Staging (stories 015-016)

| Item | Value |
|------|-------|
| Compose file | `environments/staging/docker-compose.yml`, project `stepbystep-staging` |
| Services | `db` (PostgreSQL 16, volume `db_data`), `app` (`127.0.0.1:8082`) |
| Deployment | `scripts/deploy-staging.sh <version>`, automatic on every build of `main` |
| Secrets | `DB_USER` / `DB_PASSWORD` from the Jenkins credential `staging-db-creds` |
| Health | Compose healthcheck on `/actuator/health`; `up --wait` returns only when it is `UP` |

Idempotency: deploying the same version twice changes nothing (containers keep their IDs); a new
version only recreates `app`. A failed deployment (unknown version, unhealthy app) returns a non-zero
exit code and the build fails.

## Production (stories 017-018, module N2a)

| Item | Value |
|------|-------|
| Compose file | `environments/production/docker-compose.yml`, project `stepbystep-production` |
| Services | `db`, `app-blue` (`127.0.0.1:8083`), `app-green` (`127.0.0.1:8084`), `proxy` (nginx, `127.0.0.1:8088`) |
| Deployment | `scripts/deploy-production.sh <version>`, after the manual approval (`input`, submitter `admin`) |
| Secrets | `DB_USER` / `DB_PASSWORD` from the Jenkins credential `production-db-creds` |

Blue/green sequence:

1. read the active colour in the proxy (`/etc/nginx/upstream/active.conf`, on a named volume);
2. deploy the version on the idle colour and wait until it is healthy;
3. switch the proxy (`nginx -s reload`) and check `/actuator/health` **through the proxy**;
4. any failure: the proxy stays on, or goes back to, the previous colour (**automatic rollback**).

The previous colour keeps running: a manual rollback is a single proxy switch.

Tested on 01/11/2026: first deployment on blue, second on green, then a deliberately broken version
was refused (blue unhealthy, stopped) while production kept serving green with `"status":"UP"`.

## Why no bind mounts

The deployment commands run inside the Jenkins agent container, but the containers are created by the
host's Docker daemon: a path of the agent workspace does not exist on the host. Configuration files are
therefore baked into images (`environments/runtime/Dockerfile`, `environments/production/proxy/`)
instead of being mounted. For the same reason, health checks run inside the containers (Compose
healthchecks, `docker exec`), never against `localhost` from the agent.

## Useful commands

```bash
docker compose -p stepbystep-staging ps
docker compose -p stepbystep-production ps
curl localhost:8082/actuator/health          # staging
curl localhost:8088/actuator/health          # production, through the proxy
docker exec stepbystep-production-proxy-1 cat /etc/nginx/upstream/active.conf
```
