# Tutoriel 010 — Story #005 : Documenter (en anglais) et faire le bilan

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 009
**Prérequis :** `check-agents.sh` passe
**Exigence TP couverte :** `EXG-COM-04`
**Fichier principal :** `docs/tooling/jenkins-agent.md` (à créer)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Écrire la doc **en anglais** qui permet de refaire l'agent, vérifier qu'aucune clé privée n'est dans
le projet, puis faire le bilan de la story.

---

## 🪜 Les étapes

### Étape 1 : Écris la documentation

**Où :** la racine du projet

**Action :**

> Crée `docs/tooling/jenkins-agent.md` (réponds **Cancel** à IntelliJ). Relis et adapte les tournures
> si tu le souhaites, sans changer les valeurs :
>
> ```markdown
> # Jenkins Build Agent
>
> Builds never run on the Jenkins controller: the controller has **0 executors**, and every build runs
> on the `docker-agent` agent, a container of the `cicd` stack.
>
> ## 1. Agent container
>
> | Item | Value |
> |------|-------|
> | Service | `jenkins-agent` in `docker-compose.yml` |
> | Image | built from `jenkins-agent/Dockerfile`: `jenkins/ssh-agent:8.6.0-jdk17` + Docker CLI `29.9.0` + Docker Compose `v5.5.1` |
> | Docker access | host socket `/var/run/docker.sock`; group `docker-host` (GID `963`, the host `docker` group) created in the image |
> | Workspace | named volume `jenkins_agent_work`, mounted on `/home/jenkins/agent` |
>
> Mounting the Docker socket gives the agent full control of the host's Docker. This is accepted for a
> local lab; a shared server would need a dedicated Docker host.
>
> The group is created in the image, not with `group_add` in `docker-compose.yml`: builds reach the agent
> through SSH, and an SSH session only gets the groups listed in the image's `/etc/group`. If the host
> `docker` group has another GID (`getent group docker`), update `ARG DOCKER_GID` in the `Dockerfile`.
>
> ## 2. SSH key pair
>
> The controller connects to the agent over SSH (plugin *SSH Build Agents*).
>
> | Half | Where it lives |
> |------|----------------|
> | Public key | `JENKINS_AGENT_SSH_PUBKEY` in `docker-compose.yml` (not a secret) |
> | Private key | **only** in the Jenkins credential `docker-agent-ssh-key` (SSH Username with private key, user `jenkins`) |
>
> To replace the key pair: generate a new pair in a temporary directory, update the credential and
> `docker-compose.yml`, run `docker compose up -d jenkins-agent`, then delete the temporary directory.
>
> ## 3. Jenkins nodes
>
> | Node | Setting | Value |
> |------|---------|-------|
> | `Built-In Node` | Number of executors | `0` |
> | `docker-agent` | Number of executors | `2` |
> | `docker-agent` | Remote root directory | `/home/jenkins/agent` |
> | `docker-agent` | Labels | `docker-agent` |
> | `docker-agent` | Launch method | Launch agents via SSH, host `jenkins-agent`, credential `docker-agent-ssh-key` |
> | `docker-agent` | Host Key Verification Strategy | Non verifying (see below) |
> | `docker-agent` | Availability | Keep this agent online as much as possible |
>
> The agent image generates new SSH host keys each time the container is recreated: a trusted host key
> would break the connection after every `docker compose up`. Port 22 of the agent is not published; it
> is reachable only on the private Docker network `cicd`.
>
> ## 4. Smoke test
>
> The Pipeline job `agent-smoke-test` prints the node name and the Docker versions. Its console shows
> `Running on docker-agent`.
>
> ## 5. Verification
>
> ```bash
> ./scripts/check-agents.sh
> ```
>
> The script needs `JENKINS_USER` and `JENKINS_TOKEN` (see `jenkins-configuration.md`). It checks that
> the controller has 0 executors, that `docker-agent` is online with 2 executors, and that Docker answers
> from the agent. It displays "Agents are compliant" and exits with code 0; otherwise it exits with
> code 1.
> ```

---

### Étape 2 : Vérifie l'absence de secrets

**Où :** la racine du projet

**Action :**

> ```bash
> grep -rniE '(password|passwd|secret|token|api[_-]?key)[[:space:]]*[:=][[:space:]]*[^<{$[:space:]]' \
>   docker-compose.yml jenkins-agent/ scripts/check-agents.sh docs/tooling/jenkins-agent.md 2>/dev/null || echo "OK : aucun secret detecte"
> grep -rl 'PRIVATE KEY' . --exclude-dir=.git --exclude-dir=docs || echo "OK : aucune cle privee"
> ```

**Résultat attendu :** **une seule** fausse alerte (vérifiée à la génération du tutoriel), puis
`OK : aucune cle privee` :

```
scripts/check-agents.sh:10:: "${JENKINS_TOKEN:?JENKINS_TOKEN is not set (see docs/tooling/jenkins-configuration.md)}"
OK : aucune cle privee
```

Même fausse alerte que pour `check-jenkins.sh` : c'est le **nom** de la variable, pas sa valeur. Si tu
as reformulé la doc, lis chaque ligne signalée.

**Pourquoi le 2e `grep` ?** Une clé privée commence toujours par `-----BEGIN OPENSSH PRIVATE KEY-----`.
Si ce texte apparaît quelque part dans le projet, c'est une fuite. On exclut `docs/` : les tutoriels
de cette story **parlent** de clé privée (ce tutoriel compris), sans en contenir.

---

### Étape 3 : Regarde l'état du dépôt

**Où :** la racine du projet

**Action :**

> ```bash
> git status --porcelain
> ```

**Résultat attendu :** une seule nouvelle ligne par rapport à la story 004 : `?? jenkins-agent/`.
(`docker-compose.yml`, `scripts/` et `docs/` étaient déjà non suivis.)

---

## ✅ Avant de passer à la story suivante — bilan de la story 005

| Critère d'acceptation | Preuve |
|-----------------------|--------|
| `docker-agent` `Online`, 2 exécuteurs | `OK  agent docker-agent is online`, `… has 2 executors` |
| Controller à **0** exécuteur | `OK  controller has 0 executors` |
| Accès au socket Docker | `OK  docker reachable from the agent` ; console `docker server: 29.9.0` |
| Job de démonstration sur l'agent | `agent-smoke-test` : `Running on docker-agent` |
| Script qui échoue quand il le doit | tutoriel 009 |
| Doc en anglais | `docs/tooling/jenkins-agent.md` |

### 🛡️ Garde-fous du TP

- [ ] Clé privée **uniquement** dans la credential `docker-agent-ssh-key`
- [ ] Aucun build ne s'est exécuté sur le controller
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien dans cette story. En **story 006**, le premier commit inclura notamment `docker-compose.yml`,
`jenkins-agent/Dockerfile`, `scripts/check-agents.sh` et `docs/tooling/jenkins-agent.md`, avec un
message du type `build(jenkins): add dedicated build agent and forbid controller executors`.

---

## 🎉 Bravo !

Tu as terminé la story 005 ! Ton Jenkins a maintenant un vrai ouvrier, et le chef de chantier ne
peut plus construire lui-même. Le **jalon 0 — Outillage** est complet.

**Prochaine story :** Story 006 — Dépôt Git et hygiène (le premier commit !). Ses tutoriels seront
générés avec `/ tasks-fr 006`.

Pause méritée ☕
