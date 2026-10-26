# Step by Step

[🇫🇷 Version française](README.md)

This project is a full CI/CD pipeline using Jenkins, SonarQube and Nexus,
triggered by a GitHub webhook.

## Prerequisites

- Docker 29.x and Docker Compose v2 (`docker compose`)
- 8 GB of RAM available for Docker
- Java 17 and Maven 3.9

## Tooling

The CI/CD tools start with a **single command**:

```bash
docker compose up -d
```

| Tool      | URL                     | Access       |
|------------|-------------------------|--------------|
| Jenkins    | http://localhost:8080   | initial password: `docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword` |
| SonarQube  | http://localhost:9000   | admin / admin (change it) |
| Nexus      | http://localhost:8081   | admin / temporary password in `/nexus-data/admin.password`; command `docker compose exec nexus cat /nexus-data/admin.password` |

Ports only listen on `127.0.0.1`: they are not accessible from the outside.

### Useful commands

```bash
docker compose ps                            # services state and health
docker compose logs -f sonarqube             # to follow logs
docker compose stop                          # to stop, keep data
docker compose down                          # to stop and delete containers
docker compose down -v                       # to delete everything, INCLUDING DATA
```

Never run `down -v` without checking twice: volumes contain Jenkins configuration, SonarQube analysis results, and Nexus artifacts.

## What's next

This README will be updated with future stories.
