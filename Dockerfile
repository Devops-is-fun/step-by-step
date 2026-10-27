# Step by Step application image (story 007): build with the Maven wrapper, run on a JRE only.

# 1. Build stage: full JDK + the same Maven wrapper as on the laptop and the Jenkins agent
FROM eclipse-temurin:17.0.20.1_1-jdk-noble AS build
WORKDIR /workspace
COPY mvnw pom.xml ./
COPY .mvn .mvn
# Dependencies first: this layer stays cached as long as pom.xml does not change
RUN ./mvnw -B -q dependency:go-offline
COPY src src
# Tests already ran in "./mvnw verify" (laptop, then Jenkins): the image only packages
RUN ./mvnw -B -q package -DskipTests

# 2. Runtime stage: JRE only, no build tools, non-root user
FROM eclipse-temurin:17.0.20.1_1-jre-noble
RUN groupadd --system app && useradd --system --gid app app
WORKDIR /app
COPY --from=build /workspace/target/step-by-step-0.1.0-SNAPSHOT.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]