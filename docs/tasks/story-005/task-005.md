# Tutoriel 005 — Story #005 : Déclarer le nœud `docker-agent` dans Jenkins

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 004
**Prérequis :** agent démarré ; credential `docker-agent-ssh-key` créée
**Exigence TP couverte :** `EXG-COM-04`
**Fichier principal :** `http://localhost:8080`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Dire à Jenkins : « il existe un ouvrier `docker-agent`, voici comment le joindre ». Puis voir Jenkins
s'y connecter.

## 📋 Ce que tu vas apprendre

- Créer un agent permanent (*Permanent Agent*) lancé par SSH
- Ce qu'est un label, et pourquoi il porte ici le même nom que le nœud
- Lire le journal de connexion d'un agent

---

## 🪜 Les étapes

### Étape 1 : Crée le nœud

**Où :** `http://localhost:8080`

**Action :**

> **Manage Jenkins** → **Nodes** → **New Node**.
> Node name : `docker-agent` ; Type : **Permanent Agent** ; clique **Create**.

**Résultat attendu :** un formulaire de configuration du nœud.

---

### Étape 2 : Remplis le formulaire

**Où :** le formulaire du nœud

**Action :**

> | Champ | Valeur |
> |-------|--------|
> | Description | `Build agent (Docker container of the cicd stack)` |
> | Number of executors | `2` |
> | Remote root directory | `/home/jenkins/agent` |
> | Labels | `docker-agent` |
> | Usage | `Use this node as much as possible` |
> | Launch method | `Launch agents via SSH` |
> | Host | `jenkins-agent` |
> | Credentials | `jenkins (SSH key used by the controller to reach docker-agent)` |
> | Host Key Verification Strategy | `Non verifying Verification Strategy` |
> | Availability | `Keep this agent online as much as possible` |
>
> Laisse le reste par défaut (dont **Advanced** : port `22`). Clique **Save**.

**Pourquoi ces valeurs ?**

| Valeur | Raison |
|--------|--------|
| Host `jenkins-agent` | le **nom du service** Compose (le controller le résout sur le réseau `cicd`) — pas `docker-agent`, qui est le nom **Jenkins** du nœud |
| `/home/jenkins/agent` | le volume `jenkins_agent_work` : les espaces de travail survivent aux recréations |
| Label `docker-agent` | le Jenkinsfile dira `agent { label 'docker-agent' }` (spécification, `EXG-COM-04`) |
| *Non verifying* | l'image **régénère ses clés d'hôte** à chaque recréation du conteneur : une clé d'hôte mémorisée casserait la connexion après chaque `docker compose up --build`. Le risque (quelqu'un qui se ferait passer pour l'agent) est limité : le port 22 n'est joignable que sur le réseau privé `cicd` |

**À dire à la soutenance :** *Non verifying* est un **compromis assumé et documenté**, pas un oubli.

**✅ Vérifie que :**
- [ ] Host = `jenkins-agent`, Label = `docker-agent`
- [ ] Credentials = celle de l'utilisateur `jenkins`

---

### Étape 3 : Lis le journal de connexion

**Où :** `http://localhost:8080`

**Action :**

> **Manage Jenkins** → **Nodes** → **docker-agent** → **Log** (menu de gauche).
> Attends 10 à 30 secondes, rafraîchis si besoin.

**Résultat attendu :** les dernières lignes contiennent `Agent successfully connected and online`.

**Pourquoi :** le controller vient d'ouvrir une session SSH vers l'agent avec la clé privée, d'y
copier son programme (`remoting.jar`) et de le lancer avec le Java 17 de l'image.

---

### Étape 4 : Vérifie la liste des nœuds

**Où :** `http://localhost:8080`

**Action :**

> **Manage Jenkins** → **Nodes**.

**Résultat attendu :** deux lignes : `Built-In Node` et `docker-agent`, **sans** croix rouge sur
`docker-agent`. Les colonnes d'espace disque et d'architecture sont remplies.

**✅ Vérifie que :**
- [ ] `docker-agent` n'est pas marqué *offline*

---

## 🧹 Comment tout annuler

**Manage Jenkins** → **Nodes** → **docker-agent** → **Delete Agent** → confirme.

---

## 🤔 Si tu es bloqué

| Problème dans le Log | Solution |
|----------------------|----------|
| `java.net.UnknownHostException: jenkins-agent` | Host mal tapé, ou agent arrêté : `docker compose ps jenkins-agent` |
| `Authentication failed` / `Server rejected the 1 private key(s)` | La clé publique du compose ne correspond pas à la clé privée de la credential : refais le tutoriel 003 (nouvelle paire), puis `docker compose up -d jenkins-agent` |
| `Java not found` / `No Known Java` | Advanced → **JavaPath** : `/opt/java/openjdk/bin/java`, puis **Save** et **Launch agent** |
| `Connection refused` | `docker compose logs --tail=30 jenkins-agent` |
| L'agent reste *offline* sans erreur | Clique **Launch agent** sur la page du nœud |

Le chemin Java a été vérifié à la génération : en SSH non interactif, `java` est trouvé dans
`/opt/java/openjdk/bin`. La connexion complète par le plugin n'a pas pu être testée à l'avance (c'est
ta configuration) : si un message diffère, note-le dans `retours.md`.

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Où | Attendu |
|--------------|----|---------|
| Nœud créé | Manage Jenkins → Nodes | `docker-agent`, 2 exécuteurs |
| Agent connecté | docker-agent → Log | `Agent successfully connected and online` |

### 🛡️ Garde-fous du TP

- [ ] Aucun mot de passe ni clé saisi ailleurs que dans la credential
- [ ] Aucun build lancé

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Brancher un agent permanent par SSH
- ✨ Distinguer nom de service, nom de nœud et label
- ✨ Lire le journal de connexion d'un agent

Prochain tutoriel : **#006** — Passer le controller à 0 exécuteur ⏱️ 15 min

Pause méritée ☕
