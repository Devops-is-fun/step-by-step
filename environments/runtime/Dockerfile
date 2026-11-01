# Runtime image of Step by Step for staging and production (stories 015-017).
# The jar is the artefact published in Nexus by the CI: the same file is deployed everywhere.
FROM eclipse-temurin:17.0.20.1_1-jre-noble
RUN groupadd --system app && useradd --system --gid app app
WORKDIR /app
COPY app.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
