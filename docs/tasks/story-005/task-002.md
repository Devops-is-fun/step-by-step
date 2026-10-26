# Tutoriel 002 — Story #005 : Ajouter le service `jenkins-agent` au `docker-compose.yml`

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 001
**Prérequis :** `jenkins-agent/Dockerfile` écrit
**Exigence TP couverte :** `EXG-COM-04`
**Fichier principal :** `docker-compose.yml`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Déclarer l'agent comme 4e service du stack `cicd`, **sans le démarrer** : il manque encore sa clé
publique (tutoriel 003).

## 📋 Ce que tu vas apprendre

- `build:` et `image:` ensemble dans un service Compose
- Monter le socket Docker dans un conteneur
- Vérifier un fichier Compose sans rien démarrer

---

## 🪜 Les étapes

### Étape 1 : Vérifie le GID du groupe `docker`

**Où :** la racine du projet

**Action :**

> ```bash
> getent group docker
> ```

**Résultat attendu :** `docker:x:963:geraldine_rouy`

**Pourquoi :** `963` est le numéro du groupe qui a le droit d'utiliser le socket Docker
(`srw-rw---- root docker`). C'est la valeur de `DOCKER_GID` dans le `Dockerfile` (tutoriel 001).

**✅ Vérifie que :**
- [ ] Le 3e champ vaut `963` (sinon, mets **ton** nombre dans `ARG DOCKER_GID=` du `Dockerfile`)

---

### Étape 2 : Ajoute le service

**Où :** `docker-compose.yml`

**Action :**

> Colle ce bloc **après** le service `nexus` (après sa ligne `- cicd`) et **avant** la ligne
> `networks:` qui commence en colonne 1. Indentation : 2 espaces, comme les autres services.
>
> ```yaml
>   jenkins-agent:
>     build: ./jenkins-agent
>     image: cicd-jenkins-agent:1.0.0
>     container_name: cicd-jenkins-agent
>     environment:
>       # Clé PUBLIQUE uniquement (pas un secret). La clé privée vit seulement dans la
>       # credential Jenkins « docker-agent-ssh-key ».
>       JENKINS_AGENT_SSH_PUBKEY: "REMPLACE_MOI_AU_TUTORIEL_003"
>     volumes:
>       - jenkins_agent_work:/home/jenkins/agent
>       - /var/run/docker.sock:/var/run/docker.sock
>     restart: unless-stopped
>     networks:
>       - cicd
> ```

**Clés attendues, pour repérage visuel :** `build`, `image`, `container_name`, `environment`,
`volumes`, `restart`, `networks`.

---

### Étape 3 : Déclare le volume

**Où :** `docker-compose.yml`, tout en bas

**Action :**

> Ajoute cette ligne **à la fin** de la liste `volumes:` du bas du fichier (sous `nexus_data:`) :
>
> ```yaml
>   jenkins_agent_work:
> ```

**Pourquoi :** l'image déclare `/home/jenkins/agent` comme volume. Sans volume **nommé**, Docker en
crée un anonyme, perdu à chaque recréation du conteneur : les espaces de travail des builds
disparaîtraient.

---

### Étape 4 : Vérifie le fichier sans rien démarrer

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose config --quiet && echo "config OK"
> docker compose config --services
> ```

**Résultat attendu :** `config OK`, puis les 4 services (`jenkins`, `jenkins-agent`, `nexus`,
`sonarqube`, dans un ordre quelconque).

**✅ Vérifie que :**
- [ ] `config OK` s'affiche
- [ ] `jenkins-agent` est dans la liste

---

### Étape 5 : Comprends chaque ligne

| Ligne | Ce qu'elle fait |
|-------|-----------------|
| `build:` + `image:` | Compose **construit** l'image depuis `jenkins-agent/` et lui donne ce nom (au lieu de la télécharger) |
| `JENKINS_AGENT_SSH_PUBKEY` | la **serrure** : l'image l'écrit dans `authorized_keys` au démarrage. Remplie au tutoriel 003 |
| `/var/run/docker.sock` | le « téléphone » vers le Docker de ta machine |
| pas de `ports:` | le port SSH `22` n'est **pas** publié : seul le controller, sur le réseau `cicd`, peut le joindre |

**⚠️ La limite à connaître (pour la soutenance) :** qui a le socket Docker contrôle **tout** Docker sur
ta machine. C'est acceptable pour un TP en local ; sur un serveur partagé, il faudrait un hôte Docker
dédié aux builds.

**✅ Vérifie que :**
- [ ] Tu sais dire pourquoi il n'y a pas de `ports:`

---

## 🧹 Comment tout annuler

Supprime le bloc `jenkins-agent:` et la ligne `jenkins_agent_work:` de `docker-compose.yml`. Rien n'a
été démarré.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `yaml: line N: did not find expected key` | Indentation : `jenkins-agent:` doit être aligné sur `nexus:` (2 espaces) |
| `service "jenkins-agent" refers to undefined volume jenkins_agent_work` | Étape 3 oubliée |
| `jenkins-agent` absent de la liste | Le bloc est collé sous `networks:` ou `volumes:` du bas : remonte-le sous `services:` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Fichier valide | `docker compose config --quiet && echo "config OK"` | `config OK` |
| Service déclaré | `docker compose config --services` | contient `jenkins-agent` |
| Rien démarré | `docker compose ps --services` | **pas** de `jenkins-agent` |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Construire une image depuis Compose
- ✨ Monter le socket Docker dans un conteneur
- ✨ Vérifier un fichier Compose sans rien démarrer

Prochain tutoriel : **#003** — Créer la paire de clés ⏱️ 20 min

Pause méritée ☕
