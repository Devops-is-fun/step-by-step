# Tutoriel 001 — Story #005 : Écrire le `Dockerfile` de l'agent

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** story 004 terminée
**Prérequis :** stack `cicd` démarré
**Exigence TP couverte :** `EXG-COM-04`
**Fichier principal :** `jenkins-agent/Dockerfile` (à créer)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Écrire la recette de l'image de l'agent : Java 17 et un serveur SSH (image officielle), plus les
commandes `docker` et `docker compose`.

## 📋 Ce que tu vas apprendre

- Lire un `Dockerfile` multi-étapes (*multi-stage*)
- Récupérer un binaire dans une autre image avec `COPY --from`
- Pourquoi l'agent a besoin de Docker

---

## 🪜 Les étapes

### Étape 1 : Constate l'état « avant »

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose exec -T jenkins sh -c 'grep -o "<numExecutors>[0-9]*</numExecutors>" /var/jenkins_home/config.xml'
> ```

**Résultat attendu :** `<numExecutors>2</numExecutors>`

**Pourquoi :** c'est le nombre d'exécuteurs du **controller** aujourd'hui. Avec 2, un build pourrait
s'exécuter sur lui : c'est exactement ce que l'énoncé interdit. Ce nombre passera à `0` au
tutoriel 006.

---

### Étape 2 : Crée le dossier

**Où :** la racine du projet

**Action :**

> ```bash
> mkdir jenkins-agent
> ```

---

### Étape 3 : Écris le `Dockerfile`

**Où :** la racine du projet

**Action :**

> Crée `jenkins-agent/Dockerfile` avec ce contenu **exact** (réponds **Cancel** à IntelliJ) :
>
> ```dockerfile
> # Jenkins build agent (story 005): Java 17 + SSH server, plus the Docker CLI and Docker Compose
> # (integration tests and deployments run "docker compose" from the agent).
> FROM docker:29.9.0-cli AS docker-cli
> FROM docker/compose-bin:v5.5.1 AS compose-bin
>
> FROM jenkins/ssh-agent:8.6.0-jdk17
> COPY --from=docker-cli /usr/local/bin/docker /usr/local/bin/docker
> COPY --from=compose-bin /docker-compose /usr/local/lib/docker/cli-plugins/docker-compose
>
> # Builds reach the agent through SSH: the SSH session only gets the groups listed in /etc/group.
> # DOCKER_GID is the GID of the host "docker" group (getent group docker), owner of the socket.
> ARG DOCKER_GID=963
> RUN groupadd --gid "${DOCKER_GID}" docker-host && usermod -aG docker-host jenkins
> ```

**Comment annuler :** `rm -r jenkins-agent`

---

### Étape 4 : Repère les trois `FROM`

**Où :** ton éditeur

| Ligne | Ce qu'elle fait |
|-------|-----------------|
| `FROM docker:29.9.0-cli AS docker-cli` | une image qui contient **seulement** la commande `docker` ; on lui donne un surnom |
| `FROM docker/compose-bin:v5.5.1 AS compose-bin` | une image qui contient **seulement** le plugin `docker compose` |
| `FROM jenkins/ssh-agent:8.6.0-jdk17` | la **vraie** base : Java 17 + serveur SSH, préparée par Jenkins |
| `COPY --from=…` | on prend un fichier dans une image surnommée, et on le pose dans la base |
| `groupadd` + `usermod` | crée **dans l'image** un groupe `docker-host` avec le GID `963` du groupe `docker` de ta machine, et y ajoute `jenkins` : il pourra parler au socket Docker |

**Pourquoi le groupe dans l'image, et pas seulement dans le compose ?** Les builds arrivent par une
**session SSH**, qui ne reçoit que les groupes écrits dans `/etc/group` de l'image. Un `group_add`
dans le compose ne s'applique qu'au processus principal et à `docker compose exec` : la première
version de ce tutoriel l'utilisait, et le build échouait avec `permission denied … docker.sock`.

**Pourquoi cette forme ?** Image : on passe dans deux magasins prendre **un seul outil** chacun, sans
ramener le magasin. L'image finale ne contient que la base + deux fichiers.

**Pourquoi ces versions ?**
- `8.6.0-jdk17` : la dernière version de l'image avec Java **17**, comme ton controller et ton
  application (la suite est en Java 21).
- `29.9.0` et `v5.5.1` : exactement les versions de Docker et de Compose installées sur ton poste.

**Pourquoi l'agent a besoin de Docker ?** Les tests d'intégration (story 012) et les déploiements
(story 015) lancent `docker compose` **depuis l'agent**.

**✅ Vérifie que :**
- [ ] Tu sais dire pourquoi il y a trois `FROM`
- [ ] Tu sais dire pourquoi le groupe `docker-host` est créé dans l'image

---

## 🧹 Comment tout annuler

```bash
rm -r jenkins-agent
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `mkdir: impossible de créer le répertoire « jenkins-agent »: Le fichier existe` | Le dossier existe déjà : passe à l'étape 3 |
| IntelliJ colore le fichier en rouge | Il n'est pas suivi par Git : c'est normal (premier commit en story 006) |

L'image sera **construite** au tutoriel 004. Cette recette a été testée sur ton poste dans un conteneur
jetable, **par une vraie session SSH** (comme Jenkins) : `docker` 29.9.0 et `docker compose` v5.5.1
répondent depuis l'utilisateur `jenkins`, et Java 17 est trouvé.

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

- [ ] Controller à `2` exécuteurs constaté (état « avant »)
- [ ] `jenkins-agent/Dockerfile` écrit, avec trois `FROM`
- [ ] `git status` : `jenkins-agent/` apparaît en `??` (non suivi), c'est normal

---

## 📝 Journal des décisions

Décisions prises le 26/10/2026, au cadrage de `tasks-fr 005` :

| # | Question | Réponse | Raison |
|---|----------|---------|--------|
| 1 | Connexion de l'agent | `1b` — agent SSH, clé privée **uniquement** dans Jenkins | même règle que `nexus-deploy-creds` ; l'agent survit aux redémarrages sans rien exporter |
| 2 | Image | `2a` — `jenkins-agent/Dockerfile` avec CLI Docker et Compose | le critère « accès au socket » est prouvé dès cette story |
| 3 | Accès à Docker | `3a` — socket de l'hôte + groupe `963` | simple ; limite documentée (accès complet au Docker de l'hôte) |
| 4 | Job de démonstration | `4a` — `agent-smoke-test`, conservé | preuve relançable à la soutenance |
| 5 | `check-agents.sh` | `5a` — depuis le terminal ; pipeline en story 011 | le pipeline n'existe pas encore |

Vérifié sur ton poste à la génération :
- le plugin *SSH Build Agents* (`ssh-slaves`) est **déjà installé**, comme dépendance d'un autre
  plugin : pas de 11e plugin à ajouter ;
- le groupe `docker` de l'hôte a le GID `963` ; il est recréé **dans l'image** (`docker-host`), car
  `group_add` ne s'applique pas aux sessions SSH (découvert au tutoriel 007, le 26/10/2026) ;
- SELinux est actif mais ne bloque pas le socket : Docker n'a pas l'option SELinux activée ;
- l'image `jenkins/ssh-agent` **régénère ses clés d'hôte SSH** à chaque recréation du conteneur :
  d'où la stratégie *Non verifying* au tutoriel 005 (expliquée là-bas).

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Lire un `Dockerfile` multi-étapes
- ✨ Prendre un seul outil dans une autre image avec `COPY --from`

Prochain tutoriel : **#002** — Ajouter le service `jenkins-agent` au `docker-compose.yml` ⏱️ 20 min

Pause méritée ☕
