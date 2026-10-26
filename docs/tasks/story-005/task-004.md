# Tutoriel 004 — Story #005 : Démarrer l'agent et vérifier Docker

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 003
**Prérequis :** clé publique dans `docker-compose.yml`
**Exigence TP couverte :** `EXG-COM-04`
**Fichier principal :** `docker-compose.yml`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Construire l'image, démarrer **seulement** l'agent, et prouver que l'utilisateur `jenkins` de l'agent
parle à Docker et que le controller peut le joindre.

## 📋 Ce que tu vas apprendre

- Démarrer un seul service d'un stack
- Exécuter une commande dans un conteneur **en tant qu'un autre utilisateur** (`-u`)
- Tester qu'un port est joignable sans outil réseau

---

## 🪜 Les étapes

### Étape 1 : Construis et démarre l'agent

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose up -d --build jenkins-agent
> ```

**Résultat attendu :** le téléchargement des trois images (1 à 2 minutes la première fois), puis
`Container cicd-jenkins-agent  Started`. Les trois autres services ne sont **pas** redémarrés.

**Comment annuler :** `docker compose stop jenkins-agent`

---

### Étape 2 : Vérifie qu'il tourne

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose ps jenkins-agent
> ```

**Résultat attendu :** `cicd-jenkins-agent … Up …`, **sans** colonne de ports publiés (et sans
`(healthy)` : ce service n'a pas de healthcheck).

---

### Étape 3 : Docker depuis l'utilisateur `jenkins`

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose exec -T -u jenkins jenkins-agent id
> docker compose exec -T -u jenkins jenkins-agent docker version --format 'client {{.Client.Version}}, server {{.Server.Version}}'
> docker compose exec -T -u jenkins jenkins-agent docker compose version
> ```

**Résultat attendu :**

```
uid=1000(jenkins) gid=1000(jenkins) groups=1000(jenkins),963(docker-host)
client 29.9.0, server 29.9.0
Docker Compose version v5.5.1
```

**Pourquoi `-u jenkins` ?** Par défaut, `exec` entre en **root** : ça marcherait toujours, et ne
prouverait rien. Les builds tourneront en `jenkins` : c'est lui qu'on teste. Le `963(docker-host)`
dans `groups` est le groupe créé par le `Dockerfile` (tutoriel 001).

**✅ Vérifie que :**
- [ ] `963` apparaît dans `groups`
- [ ] Le **server** répond (sinon, le socket n'est pas accessible)

---

### Étape 4 : Le controller peut-il joindre l'agent ?

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose exec -T jenkins bash -c 'echo > /dev/tcp/jenkins-agent/22 && echo "port 22 reachable"'
> ```

**Résultat attendu :** `port 22 reachable`

**Pourquoi :** on demande au **controller** d'ouvrir une connexion vers `jenkins-agent:22`. Le nom
`jenkins-agent` fonctionne parce que les deux conteneurs sont sur le réseau `cicd`. `/dev/tcp/…` est
une astuce de `bash` : pas besoin d'installer `nc` ou `telnet`.

---

### Étape 5 : Depuis ta machine, le port est fermé

**Où :** la racine du projet

**Action :**

> ```bash
> bash -c 'echo > /dev/tcp/127.0.0.1/22' 2>/dev/null && echo "something listens on 22" || echo "port 22 of the agent not published"
> ```

**Résultat attendu :** `port 22 of the agent not published`

**Pourquoi :** l'agent n'est joignable **que** par le controller. (Si tu as un serveur SSH sur ta
machine, tu verras `something listens on 22` : c'est le tien, pas l'agent. `docker compose ps` à
l'étape 2 fait foi.)

---

## 🧹 Comment tout annuler

```bash
docker compose stop jenkins-agent
docker compose rm -f jenkins-agent
```

(⚠️ Jamais `docker compose down -v` : il supprimerait **tous** les volumes, dont la configuration de
Jenkins, SonarQube et Nexus.)

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `failed to solve: … not found` | Faute de frappe dans un `FROM` du `Dockerfile` : compare au tutoriel 001 |
| `permission denied while trying to connect to the Docker daemon socket` à l'étape 3 | `963` absent de `groups` : vérifie les lignes `ARG DOCKER_GID` / `RUN groupadd` du `Dockerfile` (tutoriel 001) et refais l'étape 1 |
| `Restarting` en boucle | `docker compose logs --tail=30 jenkins-agent` : souvent une clé publique mal collée (tutoriel 003, étape 5) |
| `Connection refused` à l'étape 4 | L'agent n'est pas démarré : étape 2 |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Agent démarré | `docker compose ps jenkins-agent` | `Up` |
| Docker accessible en `jenkins` | `docker compose exec -T -u jenkins jenkins-agent docker version` | client **et** server |
| Compose disponible | `… docker compose version` | `v5.5.1` |
| Joignable par le controller | étape 4 | `port 22 reachable` |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Démarrer un seul service d'un stack
- ✨ Tester en tant que le **bon** utilisateur
- ✨ Tester un port avec `/dev/tcp`

Prochain tutoriel : **#005** — Déclarer le nœud `docker-agent` dans Jenkins ⏱️ 20 min

Pause méritée ☕
