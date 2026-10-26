# Tutoriel 004 — Story #001 : Ajouter le service SonarQube

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 003
**Prérequis :** service `jenkins` déclaré et image Jenkins téléchargée
**Exigence TP couverte :** `EXG-COM-01`, `EXG-COM-02`
**Fichier principal :** `docker-compose.yml`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Ajouter SonarQube dans `docker-compose.yml`, en adaptant ses deux réglages mémoire au format
particulier de cette image, et en lui donnant trois volumes pour que ses données persistent.

## 📋 Ce que tu vas apprendre

- Qu'une image peut fournir **deux** variables mémoire différentes selon le sous-processus
- À quoi sert chacun des trois répertoires de SonarQube (`data`, `logs`, `extensions`)
- Pourquoi le port `9000` n'est pas un hasard

---

## 🪜 Les étapes

### Étape 1 : Place le curseur à la fin du bloc `jenkins`

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Dans `docker-compose.yml`, descends jusqu'à la dernière ligne du bloc Jenkins :
>
> ```yaml
>     networks:
>       - cicd
> ```
>
> Place ton curseur **après** `- cicd`, puis appuie sur Entrée pour créer une nouvelle ligne à la
> **même** indentation que `jenkins:` (2 espaces).

---

### Étape 2 : Écris l'en-tête du service et son image

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Écris :
>
> ```yaml
>
>   sonarqube:
>     image: sonarqube:9.9.8-community
>     container_name: cicd-sonarqube
> ```

**✅ Vérifie que :**
- [ ] `sonarqube:` est aligné sur `jenkins:` (2 espaces)
- [ ] La version est `9.9.8-community` — pas `community`, pas `latest`, pas `26.x`

> **Pourquoi cette version précise ?** Ce projet tourne sur **Java 17**. Or la Community Build de
> SonarQube **à partir de la version 26.8 exige Java 21** pour son scanner, et ne fonctionne donc
> plus sur notre JDK. `9.9.8-community` est la dernière version de la série 9.x : elle est stable,
> légère, et c'est le choix que nous avons validé ensemble. C'est exactement le type de décision
> qu'il faut documenter dans ton rapport.

---

### Étape 3 : Déclare le port, avec sa restriction à la machine locale

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Continue le bloc :
>
> ```yaml
>     ports:
>       - "127.0.0.1:9000:9000"
> ```

**✅ Vérifie que :**
- [ ] Le format est bien `IP:PORT_HOTE:PORT_CONTENEUR`
- [ ] `9000` est le port externe **et** interne

> **Pourquoi 9000 ?** Historiquement, SonarQube standalone écoutait sur 9000 ; l'image Docker
> officielle a conservé ce port. Il n'existe aucune raison technique de le changer — seule
> l'habitude. Le format `IP:PORT:PORT` impose ici une IP : on reste sur `127.0.0.1` pour la même
> raison que Jenkins.

---

### Étape 4 : Déclare les deux variables mémoire

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Continue le bloc :
>
> ```yaml
>     environment:
>       SONAR_WEB_JAVAOPTS: "-Xms512m -Xmx512m"
>       SONAR_SEARCH_JAVAOPTS: "-Xms512m -Xmx512m"
> ```

**Clés attendues :** `environment`, `SONAR_WEB_JAVAOPTS`, `SONAR_SEARCH_JAVAOPTS`.

**✅ Vérifie que :**
- [ ] Il y a **deux** variables, pas une
- [ ] Chacune contient `-Xms512m` **et** `-Xmx512m`
- [ ] Les valeurs sont entre guillemets doubles

> **Pourquoi deux variables ?** C'est le détail qui surprend. Contrairement à d'autres serveurs,
> SonarQube ne
> tourne pas dans un seul processus Java : le serveur web d'un côté, et **Elasticsearch** (le moteur
> d'indexation) de l'autre. Ils ont chacun leur propre JVM, donc chacun son propre plafond. Si tu
> ne déclares qu'un seul des deux, Elasticsearch se verrouille en second et SonarQube affiche
> `System error` au démarrage.
>
> `-Xmx` fixe le **maximum** de tas, `-Xms` le **minimum** : les fixer tous les deux à 512 Mio évite
> que la JVM démarre petite puis tente de réallouer en cours de route, ce qui provoque des pauses
> inutiles et, à la longue, un ralentissement général de la machine.

---

### Étape 5 : Déclare les trois volumes

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Continue le bloc :
>
> ```yaml
>     volumes:
>       - sonarqube_data:/opt/sonarqube/data
>       - sonarqube_logs:/opt/sonarqube/logs
>       - sonarqube_extensions:/opt/sonarqube/extensions
> ```

**✅ Vérifie que :**
- [ ] Les trois volumes sont **dans le même ordre** que dans la section `volumes:` du fichier
- [ ] Aucun espace autour du `:` de chaque ligne

> **Pourquoi trois ?**
> - `data` : la base de l'analyse, les projets, les utilisateurs. **C'est ce qui doit survivre.**
> - `logs` : les journaux, pratique à monter séparément pour pouvoir les lire depuis l'hôte.
> - `extensions` : les plugins installés depuis l'IHM. On le volume aussi pour ne pas les
>   réinstaller à chaque recréation du conteneur.

---

### Étape 6 : Déclare le `healthcheck`

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Continue le bloc :
>
> ```yaml
>     healthcheck:
>       test: ["CMD-SHELL", "wget -q -O - http://localhost:9000/api/system/status | grep -q '\"status\":\"UP\"' || exit 1"]
>       interval: 15s
>       timeout: 5s
>       retries: 40
>       start_period: 120s
>     restart: unless-stopped
>     networks:
>       - cicd
> ```

**Clés attendues :** `healthcheck`, `test`, `interval`, `timeout`, `retries`, `start_period`, `restart`,
`networks`.

**✅ Vérifie que :**
- [ ] Le `test` interroge `/api/system/status` et non la page d'accueil
- [ ] Le `test` utilise `wget` et **pas** `curl` (voir l'encadré ci-dessous)
- [ ] `start_period: 120s` est supérieur à celui de Jenkins
- [ ] La ligne `- cicd` du réseau est bien présente et indentée à 6 espaces

> **⚠️ Pourquoi `wget` et pas `curl` ?** Le healthcheck est exécuté **dans** le conteneur, par le
> microprogramme `/bin/sh` de l'image. L'image `sonarqube:9.9.8-community` est une Ubuntu minimale :
> elle n'embarque **ni `curl` ni `python`**, seulement `wget`. Un `test` qui appelle `curl` échoue
> alors en boucle avec `sh: 1: curl: not found`, et le conteneur reste indéfiniment
> `health: starting` — **alors que SonarQube fonctionne parfaitement**. C'est le piège classique :
> un healthcheck rouge ne prouve pas que l'application est cassée.
> Pour voir la sortie réelle de chaque tentative, exécute :
>
> ```bash
> docker inspect --format '{{json .State.Health.Log}}' cicd-sonarqube
> ```

> **Pourquoi ce `healthcheck` et pas celui de Jenkins ?** On interroge une **API** qui renvoie du
> JSON et non une page HTML. `grep -q '"status":"UP"'` vérifie que SonarQube répond `UP` et pas
> seulement « une page s'affiche » — car une page d'erreur renvoyée en 200 ne dirait rien de la
> santé du service.
>
> **Pourquoi `start_period: 120s` et `retries: 40` ?** SonarQube est nettement plus lent que Jenkins au
> premier démarrage : il initialise sa base et crée son index interne. Compte **2 à 4 minutes** avant
> qu'il passe `healthy`. Le `start_period` couvre cette phase, et les 40 tentatives couvrent le cas
> où l'image est encore en cours de pull.

---

### Étape 7 : Vérifie le service déclaré

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose config --services
> ```

**Résultat attendu :**
```
sonarqube
jenkins
```

**✅ Vérifie que :**
- [ ] La sortie contient `jenkins` et `sonarqube`
- [ ] `nexus` n'apparaît pas encore
- [ ] Tu remarques que l'ordre est **alphabétique**, pas celui du fichier

> **Pourquoi l'ordre n'est pas celui du fichier ?** Docker Compose classe ses services par nom pour
> des raisons d'ordre de démarrage internes. Ne t'en sert donc jamais comme source de vérité sur
> l'ordre de lecture d'un fichier : c'est `docker compose config` qui montre le fichier interprété.

---

### Étape 8 : Vérifie l'interprétation du bloc

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose config
> ```

**Résultat attendu :** dans la section `sonarqube`, tu vois `SONAR_WEB_JAVAOPTS: -Xms512m -Xmx512m`
et `SONAR_SEARCH_JAVAOPTS: -Xms512m -Xmx512m`.

**✅ Vérifie que :**
- [ ] Les deux variables apparaissent avec les bonnes valeurs
- [ ] Les trois volumes apparaissent sous le service
- [ ] Aucune erreur YAML

---

### Comment annuler

Pour retirer SonarQube : supprime tout le bloc `sonarqube:` écrit aux étapes 2 à 6 (de la ligne
`sonarqube:` jusqu'à la dernière ligne `- cicd`).

Les volumes `sonarqube_data`, `sonarqube_logs`, `sonarqube_extensions` restent déclarés dans la
section `volumes:` du fichier : c'est sans conséquence, Docker les ignore tant qu'aucun service ne
les utilise.

---

## ✅ Avant de passer au tutoriel suivant

**Action :**

> Vérifie l'état du dépôt :
>
> ```bash
> git status
> ```

**Fichiers modifiés :**
- [ ] `docker-compose.yml` — bloc `sonarqube` ajouté

> ⚠️ **Aucun commit n'est demandé ici.** Le premier commit du projet est fait dans la **story 006**.

**✅ Vérifie que :**
- [ ] `docker-compose.yml` est la seule modification
- [ ] Tu n'as téléchargé aucune image à ce stade (`docker compose pull` vient au tutoriel 006)

---

## 🧹 Comment tout annuler

```bash
# 1. supprimer le bloc sonarqube: dans docker-compose.yml
#    (de "  sonarqube:" jusqu'à la dernière ligne "      - cicd")
# 2. les volumes sonarqube_* peuvent rester déclarés : sans conséquence
# 3. supprimer le fichier Compose s'il ne reste plus que le squelette
rm docker-compose.yml
```

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `error while loading yaml: line N` | Souvent le `\"` dans le `healthcheck`. Vérifie que tu as bien écrit `grep -q '\"status\":\"UP\"'` avec des **apostrophes** autour |
| `service "sonarqube" has neither an image nor a build context` | Le bloc est indenté de 2 espaces au lieu de 4 pour ses sous-clés. `image:` doit être à 4 espaces |
| J'ai oublié une des deux variables mémoire | Le conteneur démarre mais affiche `System error` dans les logs. Reprends l'étape 4 |
| `max virtual memory areas vm.max_map_count [65530] is too low` | Reprends l'étape 3 du tutoriel 001 : `sudo sysctl -w vm.max_map_count=524288` |
| `docker compose config --services` n'affiche qu'un service | Le bloc `sonarqube` a été écrit **après** la ligne `networks:` racine au lieu d'être sous `services:`. Vérifie l'indentation |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel ! Tu as appris à :
- ✨ Comprendre qu'une image peut avoir **plusieurs** réglages mémoire (une JVM par processus)
- ✨ Déclarer plusieurs volumes nommés pour un même service
- ✨ Écrire un `healthcheck` basé sur une **API JSON**, plus fiable qu'un test HTTP sur la page d'accueil

**Prochain tutoriel :** Tutoriel 005 - Ajouter le service Nexus
