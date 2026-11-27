# Runtime image of Step by Step, built by the CI (GitHub Actions) and pushed to GHCR.
# The jar tested by the CI is copied here as app.jar: the same file is deployed everywhere.
FROM eclipse-temurin:17.0.21.1_2-jre-noble
# Fixed numeric UID/GID: Kubernetes can only check "runAsNonRoot" on a numeric user (Hadolint DL3066)
RUN groupadd --system --gid 10001 app && useradd --system --uid 10001 --gid app app
WORKDIR /app
COPY app.jar app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
