# Step by Step

[🇫🇷 Version française](README.md)

This project is a full CI/CD pipeline using Jenkins, SonarQube and Nexus,
triggered by a GitHub webhook.

## Prerequisites

- Docker 29.x and Docker Compose v2 (`docker compose`)
- 8 GB of RAM available for Docker
- A JDK 17 or newer (the code is compiled for Java 17)
- Maven is **not** required: the `./mvnw` wrapper downloads the right version

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

## Run the application locally

The application needs a PostgreSQL database. Locally, use the throwaway **test** database
(test-only credentials `stepbystep` / `test`, no real data):

```bash
docker compose -f src/test/resources/compose-test.yml up -d --wait   # test database, port 5433
./mvnw -B verify                                                      # compile, unit and integration tests, package
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/stepbystep_test \
SPRING_DATASOURCE_USERNAME=stepbystep SPRING_DATASOURCE_PASSWORD=test \
java -jar target/step-by-step-0.1.0-SNAPSHOT.jar --server.port=8090
curl localhost:8090/actuator/health                                   # {"status":"UP", …}
```

Or as a container, attached to the test database network:

```bash
docker build -t step-by-step:0.1.0-SNAPSHOT .
docker run -d --name step-by-step-local --network stepbystep-test_default -p 127.0.0.1:8090:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/stepbystep_test \
  -e SPRING_DATASOURCE_USERNAME=stepbystep -e SPRING_DATASOURCE_PASSWORD=test \
  step-by-step:0.1.0-SNAPSHOT
```

Unit tests only, **without Docker**: `./mvnw -B test`.
Stop the test database: `docker compose -f src/test/resources/compose-test.yml down`.

Port **8090** avoids clashing with Jenkins (8080) and Nexus (8081).

## What's next

This README will be updated with future stories.
