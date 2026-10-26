# Jenkins build agent (story 005): Java 17 + SSH server, plus the Docker CLI and Docker Compose
# (integration tests and deployments run "docker compose" from the agent).
FROM docker:29.9.0-cli AS docker-cli
FROM docker/compose-bin:v5.5.1 AS compose-bin

FROM jenkins/ssh-agent:8.6.0-jdk17
COPY --from=docker-cli /usr/local/bin/docker /usr/local/bin/docker
COPY --from=compose-bin /docker-compose /usr/local/lib/docker/cli-plugins/docker-compose

# Builds reach the agent through SSH: the SSH session only gets the groups listed in /etc/group.
# DOCKER_GID is the GID of the host "docker" group (getent group docker), owner of the socket.
ARG DOCKER_GID=963
RUN groupadd --gid "${DOCKER_GID}" docker-host && usermod -aG docker-host jenkins