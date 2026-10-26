# Tutoriel 007 — Story #005 : Créer le job `agent-smoke-test`

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 006
**Prérequis :** controller à 0 exécuteur ; `docker-agent` connecté
**Exigence TP couverte :** `EXG-COM-04`
**Fichier principal :** `http://localhost:8080`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Lancer ton **premier build** et prouver qu'il s'exécute sur `docker-agent`, avec Docker disponible.

## 📋 Ce que tu vas apprendre

- Créer un job Pipeline avec un script écrit dans l'interface
- Lire la console d'un build : `Running on …`
- La variable `NODE_NAME`

---

## 🪜 Les étapes

### Étape 1 : Crée le job

**Où :** `http://localhost:8080`

**Action :**

> Tableau de bord → **New Item** → nom : `agent-smoke-test` → type **Pipeline** → **OK**.

**Résultat attendu :** la page de configuration du job.

---

### Étape 2 : Écris le script

**Où :** la configuration du job, section **Pipeline**

**Action :**

> **Definition** : `Pipeline script`. Dans **Script**, colle ce contenu **exact** :
>
> ```groovy
> pipeline {
>   agent { label 'docker-agent' }
>   stages {
>     stage('Where am I?') {
>       steps {
>         sh 'echo "node: $NODE_NAME"'
>         sh 'docker version --format "docker server: {{.Server.Version}}"'
>         sh 'docker compose version'
>       }
>     }
>   }
> }
> ```
>
> Laisse **Use Groovy Sandbox** coché. Clique **Save**.

**Pourquoi un script dans l'interface, et pas un `Jenkinsfile` ?** Ce job ne teste que l'agent ; il
n'appartient pas à l'application. Le vrai pipeline, versionné dans un `Jenkinsfile`, arrive en
story 011.

---

### Étape 3 : Lance-le

**Où :** la page du job

**Action :**

> Clique **Build Now**, puis le numéro **#1** → **Console Output**.

**Résultat attendu :** dans la console, notamment :

```
Running on docker-agent in /home/jenkins/agent/workspace/agent-smoke-test
…
node: docker-agent
…
docker server: 29.9.0
…
Docker Compose version v5.5.1
…
Finished: SUCCESS
```

**✅ Vérifie que :**
- [ ] `Running on docker-agent` (et **pas** `Built-In Node`)
- [ ] `Finished: SUCCESS`

**Pourquoi c'est la preuve clé de la story ?** C'est exactement ce que le correcteur cherchera :
le build s'est exécuté sur un agent, et cet agent sait parler à Docker.

---

### Étape 4 : Une preuve à garder

**Où :** la page du job

**Action :**

> Rien à faire : le job est **conservé** (décision `4a`). À la soutenance, **Build Now** refait la
> preuve en 10 secondes.

---

## 🧹 Comment tout annuler

Page du job → **Delete Pipeline** → confirme.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Le build reste en file : `Waiting for next available executor on 'docker-agent'` | L'agent est *offline* : **Manage Jenkins** → **Nodes** → **docker-agent** → **Log** (tutoriel 005) |
| `There are no nodes with the label 'docker-agent'` | Label mal saisi sur le nœud : tutoriel 005, étape 2 |
| `docker: not found` | L'image n'a pas été reconstruite : `docker compose up -d --build jenkins-agent` |
| `permission denied … docker.sock` | Groupe `docker-host` absent de l'image : tutoriel 001 (lignes `ARG DOCKER_GID` / `RUN groupadd`), puis `docker compose up -d --build jenkins-agent` et **Launch agent** |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Où | Attendu |
|--------------|----|---------|
| Build sur l'agent | Console #1 | `Running on docker-agent` |
| Docker depuis un build | Console #1 | `docker server: 29.9.0` |
| Build vert | Console #1 | `Finished: SUCCESS` |

### 🛡️ Garde-fous du TP

- [ ] Aucun build ne s'est exécuté sur le controller

---

## 🎉 Bravo !

Tu as lancé ton **premier build** Jenkins ! 🎉 Tu as appris à :

- ✨ Créer un job Pipeline
- ✨ Lire `Running on …` dans une console
- ✨ Prouver qu'un build tourne sur l'agent

Prochain tutoriel : **#008** — Écrire `scripts/check-agents.sh` ⏱️ 20 min

Pause méritée ☕
