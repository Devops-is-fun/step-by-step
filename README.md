# Step by Step

[🇬🇧 English version](README.en.md)

Projet de démonstration d'une chaîne CI/CD complète : Jenkins, SonarQube et Nexus,
déclenchés par un webhook GitHub.

## Prérequis

- Docker 29.x avec Docker Compose v2 (`docker compose`)
- 8 Go de RAM disponibles pour Docker
- Un JDK 17 ou plus récent (le code est compilé en Java 17)
- Maven n'est **pas** nécessaire : le wrapper `./mvnw` télécharge la bonne version

## Démarrage de l'outillage

Les trois outils de la chaîne CI/CD se démarrent avec **une seule commande** :

```bash
docker compose up -d
```

| Outil      | URL                     | Accès        |
|------------|-------------------------|--------------|
| Jenkins    | http://localhost:8080   | mot de passe initial : `docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword` |
| SonarQube  | http://localhost:9000   | admin / admin (à changer) |
| Nexus      | http://localhost:8081   | admin / mot de passe temporaire dans `/nexus-data/admin.password`; commande `docker compose exec nexus cat /nexus-data/admin.password` |

Les ports n'écoutent que sur `127.0.0.1` : ils ne sont pas accessibles depuis une autre machine.

### Commandes utiles

```bash
docker compose ps                            # état et santé des services
docker compose logs -f sonarqube             # suivre les journaux
docker compose stop                          # arrêter, conserver les données
docker compose down                          # arrêter et supprimer les conteneurs
docker compose down -v                       # tout supprimer, DONNÉES INCLUES
```

Ne lancez jamais `down -v` sans vérifier deux fois : les volumes contiennent la configuration de
Jenkins, les analyses SonarQube et les dépôts Nexus.

## Lancer l'application en local

L'application a besoin d'une base PostgreSQL. En local, on utilise la base **de test**, jetable
(identifiants de test `stepbystep` / `test`, sans données réelles) :

```bash
docker compose -f src/test/resources/compose-test.yml up -d --wait   # base de test, port 5433
./mvnw -B verify                                                      # compiler, tests unitaires et d'intégration, emballer
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/stepbystep_test \
SPRING_DATASOURCE_USERNAME=stepbystep SPRING_DATASOURCE_PASSWORD=test \
java -jar target/step-by-step-0.1.0-SNAPSHOT.jar --server.port=8090
curl localhost:8090/actuator/health                                   # {"status":"UP", …}
```

Ou en conteneur, branché sur le réseau de la base de test :

```bash
docker build -t step-by-step:0.1.0-SNAPSHOT .
docker run -d --name step-by-step-local --network stepbystep-test_default -p 127.0.0.1:8090:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/stepbystep_test \
  -e SPRING_DATASOURCE_USERNAME=stepbystep -e SPRING_DATASOURCE_PASSWORD=test \
  step-by-step:0.1.0-SNAPSHOT
```

Tests unitaires seuls, **sans Docker** : `./mvnw -B test`.
Éteindre la base de test : `docker compose -f src/test/resources/compose-test.yml down`.

Le port **8090** évite le conflit avec Jenkins (8080) et Nexus (8081).

## Suite du projet

Ce README sera complété au fil des stories.
