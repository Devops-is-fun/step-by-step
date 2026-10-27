# Story 007 — Démarrer le squelette Spring Boot exécutable

- **Jalon :** 1 — Socle applicatif
- **Exigences couvertes :** support applicatif (alimente `EXG-COM-09`, `EXG-N1a-01`, `EXG-COM-12`)
- **Dépendances :** story 006

## Description

En tant que développeur, je veux un projet Spring Boot 4.1 / Java 17 qui se construit en local, produit
un JAR et une image Docker exécutable, afin d'avoir un artefact réel à publier et à déployer dès les
stories suivantes.

## Critères d'acceptation

- `./mvnw -B verify` réussit en moins de 2 minutes en local.
- `<maven.compiler.release>17</maven.compiler.release>` est fixé : le JDK local plus récent compile
  quand même en Java 17.
- Le binaire produit s'appelle `target/step-by-step-0.1.0-SNAPSHOT.jar` (artifactId sans espace).
- `/actuator/health` renvoie `"status":"UP"` en local (Spring Boot 4 ajoute
  `"groups":["liveness","readiness"]`), sur le port **8090** (8080 = Jenkins, 8081 = Nexus).
- Le `Dockerfile` multi-étapes (build Maven + runtime JRE) produit une image qui démarre et répond
  sur `/actuator/health`.
- Le `README.md` affiche le nom **Step by Step** ; l'artefact Maven reste `step-by-step`.
- Le `pom.xml` déclare `sonar.projectKey=step-by-step` et `sonar.host.url=http://localhost:9000`
  (décision `tasks-fr 003`, `1a`).
- Première analyse SonarQube manuelle :
  `./mvnw -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.login="$SONAR_TOKEN"`
  (jeton chargé par `read -rs`) ; le projet `step-by-step` affiche `ncloc > 0` (décision `2a`).

## Preuves attendues

| Preuve | Where |
|--------|-------|
| `BUILD SUCCESS` + durée | sortie Maven |
| JSON `UP` | `curl localhost:8090/actuator/health` |
| Image construite | `docker images | grep step-by-step` |
| Conteneur joignable | `docker run -p 127.0.0.1:8090:8080 <image> && curl` |

## Automatisation des tests

- `StepByStepApplicationTests#contextLoads` : smoke test du contexte Spring.
- Test d'intégration futur déjà branché sur `/actuator/health` (story 012).

## Documentation

`README.md` : sections « Prérequis » et « Lancer en local ».

## Fichiers à versionner

- `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`, `.gitattributes`
- `Dockerfile`, `.dockerignore`
- `src/main/java/com/esgi/stepbystep/StepByStepApplication.java`
- `src/main/resources/application.yml`
- `src/test/java/com/esgi/stepbystep/StepByStepApplicationTests.java`

## Fichiers à ne jamais versionner

`target/`, `*.log` ; pas de `.mvn/wrapper/maven-wrapper.jar` (wrapper `only-script`)

## Risques

| Risque | Parade |
|--------|--------|
| JDK 21/25 local casse la compatibilité 17 | `maven.compiler.release=17` + image `eclipse-temurin:17-jre` |
| Maven non installé en local | Wrapper `./mvnw` versionné |
| Image trop lourde | Étape runtime JRE sans JDK ni shell de build |

## Critères de Done

- [ ] `./mvnw -B verify` vert
- [ ] JAR au nom conforme
- [ ] Image Docker démarre et répond `UP`
- [ ] Actuateur exposé
- [ ] Première analyse visible dans SonarQube

## Décisions (`tasks-fr 007`, 26/10/2026)

| Sujet | Décision |
|-------|----------|
| Version | Spring Boot **4.1.2** (`1a`) |
| `Dockerfile`, `.dockerignore` | écrits par l'étudiante, guidée (`2a`) |
| `pom.xml` | applicatif écrit par Claude ; bloc Sonar (`sonar.projectKey`, `sonar.host.url`, `sonar-maven-plugin` 5.8.0.7211 en `pluginManagement`) écrit par l'étudiante (`3a`) |
| Build de l'image | `eclipse-temurin:17.0.20.1_1-jdk-noble` + `./mvnw`, runtime `…-jre-noble`, utilisateur `app` (`4a`) |
| Périmètre du code | squelette strict (`5a`) |
| Vigile | `mvnw` exclu du filet « affectation » (fausse alerte `${MVNW_PASSWORD:+has-password}`) |

## Message de commit

1. `fix(repo): skip the maven wrapper script in the secret assignment scan`
2. `build(app): bootstrap spring boot project with maven wrapper and dockerfile`
3. `docs(app): add story 007 tutorials and record spring boot decisions`
