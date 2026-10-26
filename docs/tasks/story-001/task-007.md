# Tutoriel 007 — Story #001 : Démarrer le stack et attendre les healthchecks

**Temps estimé :** ⏱️ 25 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 006
**Prérequis :** les trois images téléchargées, aucun conteneur démarré
**Exigence TP couverte :** `EXG-COM-01` (le cœur de l'exigence)
**Fichier principal :** `docker-compose.yml`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Démarrer les trois services avec **une seule commande**, puis attendre qu'ils soient tous déclarés
`sain` par Docker avant de poursuivre.

## 📋 Ce que tu vas apprendre

- Ce que fait `-d` dans `docker compose up -d`
- Lire la colonne `STATUS` de `docker compose ps`
- La différence entre les états `starting`, `healthy` et `unhealthy`

---

## 🪜 Les étapes

### Étape 1 : Démarre tout le stack en arrière-plan

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose up -d
> ```

**Résultat attendu :** une ligne par service, du type `Container cicd-nexus Created` puis
`Container cicd-nexus Started`.

**✅ Vérifie que :**
- [ ] Trois conteneurs sont créés et démarrés
- [ ] Aucune erreur `port is already allocated` n'apparaît
- [ ] La commande **rend la main** rapidement (c'est le rôle du `-d`)

> **Pourquoi `-d` ?** Sans lui, `docker compose up` affiche les journaux en continu et le terminal
> est bloqué : impossible de taper une autre commande. `-d` signifie « detached », c'est-à-dire
> « démarre et libère le terminal ». C'est le mode qu'on utilisera **toujours**.

---

### Étape 2 : Regarde l'état initial

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Immédiatement après le démarrage, exécute :
>
> ```bash
> docker compose ps
> ```

**Résultat attendu :** trois lignes dont la colonne `STATUS` indique `starting` (ou `health: starting`).

**✅ Vérifie que :**
- [ ] Les trois conteneurs apparaissent avec la colonne `STATUS`
- [ ] Les ports affichés sont `127.0.0.1:8080->8080`, `127.0.0.1:8081->8081`, `127.0.0.1:9000->9000`

> **Pourquoi les ports affichés comptent ?** C'est la preuve que ton `127.0.0.1` est bien pris en
> compte. Si tu vois `0.0.0.0:8080->8080`, ta restriction n'a pas été appliquée.

---

### Étape 3 : Regarde les journaux de SonarQube

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose logs --tail=20 sonarqube
> ```

**Résultat attendu :** des lignes de log nivelées `INFO`, et si tout va bien, une ligne
`SonarQube is operational`.

**✅ Vérifie que :**
- [ ] Aucune ligne `ERROR` ni `System error`
> [ ] Tu vois bien `INFO  ... SonarQube is operational` à un moment donné

> **Pourquoi lire les logs ?** Docker dit qu'un conteneur est `starting`, mais il ne te dira pas
> **pourquoi** il reste bloqué. Les journaux sont la seule source d'information sur ce qui se passe
> à l'intérieur. C'est la compétence la plus utile en Docker : savoir lire `docker compose logs`.

---

### Étape 4 : Regarde les journaux de Nexus

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose logs --tail=20 nexus
> ```

**Résultat attendu :** des lignes contenant `Started Nexus` ou `Started Sonatype Nexus Repository`.

**✅ Vérifie que :**
- [ ] Le message de démarrage est présent
> [ ] Tu vois une ligne indiquant le mot de passe administrateur temporaire, du type
>     `Admin password: xxxxx` — **ne le note pas dans un fichier versionné**, il est régénéré à chaque
>     recréation du conteneur

---

### Étape 5 : Attends que les trois soient `healthy`

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Boucle d'attente, à exécuter et **laisser tourner** jusqu'à ce que la dernière ligne soit `healthy` :
>
> ```bash
> watch -n5 "docker compose ps --format 'table {{.Service}}\t{{.Status}}'"
> ```
>
> Si `watch` n'existe pas sur ta machine, remplace par :
>
> ```bash
> while true; do
>   date +%H:%M:%S
>   docker compose ps --format '{{.Service}} : {{.Status}}'
>   sleep 5
> done
> ```
>
> **Résultat attendu :** au bout de **2 à 5 minutes**, les trois lignes indiquent `Up ... (healthy)`.
> Quand c'est le cas, fais `Ctrl+C` pour quitter.

**✅ Vérifie que :**
- [ ] `cicd-jenkins` est `healthy`
- [ ] `cicd-sonarqube` est `healthy`
- [ ] `cicd-nexus` est `healthy`

> **Pourquoi l'`healthcheck` prend du temps ?** Il vérifie une URL interne **toutes les 15 secondes**.
> Au premier lancement, la `start_period` couvre l'initialisation (90 s pour Jenkins, 120 s pour
> SonarQube, 60 s pour Nexus). Ensuite, il suffit d'un ou deux tests réussis pour passer `healthy`.

---

### Étape 6 : Si un conteneur reste bloqué, identifie lequel

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose ps --format 'table {{.Service}}\t{{.Status}}'
> ```
>
> Si un service n'est pas `healthy` après 6 minutes, exécute :
>
> ```bash
> docker compose logs --tail=40 <nom-du-service>
> ```
>
> avec `jenkins`, `sonarqube` ou `nexus`.

**✅ Vérifie que :**
- [ ] Tu sais quel service pose problème
- [ ] Tu as lu au moins 40 lignes de ses journaux

---

### Comment annuler

Pour arrêter les conteneurs **en conservant les données** :

```bash
docker compose stop
```

Pour arrêter et supprimer les conteneurs et le réseau, tout en **conservant** les volumes :

```bash
docker compose down
```

> ⚠️ **Ne lance jamais `docker compose down -v`** avant le tutoriel 009 : l'option `-v` supprime les
> volumes, donc Jenkins, SonarQube et Nexus repartiraient complètement à zéro.

---

## ✅ Avant de passer au tutoriel suivant

**Action :**

> Vérifie l'état du dépôt :
>
> ```bash
> git status
> ```

**Fichiers modifiés :**
- [ ] Aucun : ce tutoriel ne touche qu'à l'état de Docker

> ⚠️ **Aucun commit n'est demandé ici.** Le premier commit du projet est fait dans la **story 006**.

**✅ Vérifie que :**
- [ ] `docker compose ps` affiche trois services `healthy`
- [ ] Tu n'as toujours pas ouvert les interfaces dans un navigateur : c'est l'objet du tutoriel suivant

---

## 🧹 Comment tout annuler

```bash
# 1. arrêter les conteneurs en gardant les données
docker compose stop
# 2. arrêter et supprimer conteneurs + réseau, en gardant les volumes
docker compose down
# 3. (déconseillé) tout supprimer, DONNÉES COMPRISES
# docker compose down -v
```

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `port is already allocated` | Un port hôte est occupé. Change le port dans `docker-compose.yml`, par exemple `127.0.0.1:8089:8080`, puis `docker compose up -d` |
| SonarQube reste `starting` plus de 6 minutes | Reprends l'étape 3 du tutoriel 001 pour `vm.max_map_count`, puis `docker compose restart sonarqube` |
| Un service reste `starting` alors que ses logs sont verts | Le `healthcheck` échoue, pas l'application. `docker inspect --format '{{json .State.Health.Log}}' <conteneur>` : s'il affiche `curl: not found`, l'image n'a pas `curl` (cas de SonarQube) — remplace `curl` par `wget -q -O -` dans le `test`, puis `docker compose up -d` |
| SonarQube affiche `System error` | Une des deux variables mémoire manque. Reprends l'étape 4 du tutoriel 004, puis `docker compose up -d` |
| Nexus affiche `unhealthy` | Le fichier `/nexus-data` n'est pas monté. Vérifie le chemin dans le tutoriel 005, étape 5 |
| Le conteneur est `Exited (1)` | Il a planté. Lis `docker compose logs <service>` : la cause est presque toujours dans les 10 dernières lignes |
| La machine saccade ou manque de RAM | `free -h` : si `available` est passée sous 2 Go, arrête tout avec `docker compose down` et relance Docker Desktop |
| `watch: command not found` | Utilise la boucle `while true` proposée en variante |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel ! Tu as appris à :
- ✨ Démarrer un stack entier en **une seule commande**, et savoir que c'est le critère `EXG-COM-01`
- ✨ Lire `docker compose ps` et sa colonne `STATUS` pour comprendre l'état réel d'un service
- ✨ Diagnostiquer un conteneur bloqué grâce à `docker compose logs`

**Prochain tutoriel :** Tutoriel 008 - Vérifier que les trois interfaces répondent
