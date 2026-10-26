# Jenkins Build Agent

Builds never run on the Jenkins controller: the controller has **0 executors**, and every build runs
on the `docker-agent` agent, a container of the `cicd` stack.

## 1. Agent container

| Item | Value |
|------|-------|
| Service | `jenkins-agent` in `docker-compose.yml` |
| Image | built from `jenkins-agent/Dockerfile`: `jenkins/ssh-agent:8.6.0-jdk17` + Docker CLI `29.9.0` + Docker Compose `v5.5.1` |
| Docker access | host socket `/var/run/docker.sock`; group `docker-host` (GID `963`, the host `docker` group) created in the image |
| Workspace | named volume `jenkins_agent_work`, mounted on `/home/jenkins/agent` |

Mounting the Docker socket gives the agent full control of the host's Docker. This is accepted for a
local lab; a shared server would need a dedicated Docker host.

The group is created in the image, not with `group_add` in `docker-compose.yml`: builds reach the agent
through SSH, and an SSH session only gets the groups listed in the image's `/etc/group`. If the host
`docker` group has another GID (`getent group docker`), update `ARG DOCKER_GID` in the `Dockerfile`.

## 2. SSH key pair

The controller connects to the agent over SSH (plugin *SSH Build Agents*).

| Half | Where it lives |
|------|----------------|
| Public key | `JENKINS_AGENT_SSH_PUBKEY` in `docker-compose.yml` (not a secret) |
| Private key | **only** in the Jenkins credential `docker-agent-ssh-key` (SSH Username with private key, user `jenkins`) |

To replace the key pair: generate a new pair in a temporary directory, update the credential and
`docker-compose.yml`, run `docker compose up -d jenkins-agent`, then delete the temporary directory.

## 3. Jenkins nodes

| Node | Setting | Value |
|------|---------|-------|
| `Built-In Node` | Number of executors | `0` |
| `docker-agent` | Number of executors | `2` |
| `docker-agent` | Remote root directory | `/home/jenkins/agent` |
| `docker-agent` | Labels | `docker-agent` |
| `docker-agent` | Launch method | Launch agents via SSH, host `jenkins-agent`, credential `docker-agent-ssh-key` |
| `docker-agent` | Host Key Verification Strategy | Non verifying (see below) |
| `docker-agent` | Availability | Keep this agent online as much as possible |

The agent image generates new SSH host keys each time the container is recreated: a trusted host key
would break the connection after every `docker compose up`. Port 22 of the agent is not published; it
is reachable only on the private Docker network `cicd`.

## 4. Smoke test

The Pipeline job `agent-smoke-test` prints the node name and the Docker versions. Its console shows
`Running on docker-agent`.

## 5. Verification

```bash
./scripts/check-agents.sh
```

The script needs `JENKINS_USER` and `JENKINS_TOKEN` (see `jenkins-configuration.md`). It checks that the controller has 0 executors, that `docker-agent` is online with 2 executors, and that Docker answers from the agent. It displays "Agents are compliant" and exits with code 0; otherwise it exits with code 1.
