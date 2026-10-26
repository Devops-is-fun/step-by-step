# Tutoriel 006 — Story #001 : Valider la configuration et télécharger les images

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 005
**Prérequis :** les trois services déclarés dans `docker-compose.yml`
**Exigence TP couverte :** `EXG-COM-01`
**Fichier principal :** `docker-compose.yml`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Vérifier une dernière fois que Docker interprète correctement le fichier, puis télécharger les trois
images **avant** de lancer quoi que ce soit. Ce tutoriel ne démarre aucun conteneur.

## 📋 Ce que tu vas apprendre

- La différence entre `docker compose config`, `config --services` et `config --images`
- La différence entre `pull` et `up` (et pourquoi puller d'abord est mieux)
- Vérifier l'espace disque consommé par les images

---

## 🪜 Les étapes

### Étape 1 : Vérifie la syntaxe du fichier complet

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose config --quiet && echo "YAML valide"
> ```

**Résultat attendu :** `YAML valide`.

**✅ Vérifie que :**
- [ ] La commande se termine avec le code de sortie 0
> ```bash
> echo $?
> ```
> doit afficher `0`.

---

### Étape 2 : Relis le fichier interprété par Docker

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose config
> ```

**Résultat attendu :** le fichier entier, réécrit proprement par Docker : il a retiré nos
commentaires, normalisé les valeurs, et les présente dans un ordre canonique.

**✅ Vérifie que :**
- [ ] `name: cicd` figure en tête
- [ ] Trois services apparaissent : `jenkins`, `sonarqube`, `nexus`
- [ ] Les trois versions d'images sont bien `2.541.3-lts-jdk17`, `9.9.8-community`, `3.96.4`
- [ ] Les trois ports hôte sont précédés de `127.0.0.1`

> **Pourquoi relire cette sortie ?** `config` te montre ce que Docker a **compris**, pas ce que tu
> as écrit. C'est l'outil de débogage numéro un quand un conteneur se comporte de façon inattendue :
> dans 9 cas sur 10, la réponse est dans `docker compose config`.

---

### Étape 3 : Vérifie la liste des services

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
nexus
jenkins
```

**✅ Vérifie que :**
- [ ] La sortie contient `jenkins`, `sonarqube` et `nexus`
- [ ] Aucun autre service n'apparaît
- [ ] Tu remarques que l'ordre est **alphabétique** : Compose trie ses services par nom, donc cette
      commande ne dit rien sur l'ordre de ton fichier

---

### Étape 4 : Vérifie la liste des images

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose config --images
> ```

**Résultat attendu :**
```
jenkins/jenkins:2.541.3-lts-jdk17
sonarqube:9.9.8-community
sonatype/nexus3:3.96.4
```

**✅ Vérifie que :**
- [ ] Aucune ligne ne contient `latest`
- [ ] Chaque version est identique à celle attendue ci-dessus, au caractère près

> **Pourquoi cette vérification mérite un tutoriel ?** Parce qu'un tag `latest` échappé ici est un
> bug **invisible** : le projet fonctionnera chez toi, et cassera chez le correcteur dans six mois.
> C'est le genre de détail que le jury regarde.

---

### Étape 5 : Vérifie qu'aucun conteneur n'est démarré

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose ps
> ```

**Résultat attendu :** un tableau vide, ou un message du type `no containers`.

**✅ Vérifie que :**
- [ ] Aucun conteneur `cicd-*` n'est actif
- [ ] Tu n'as lancé aucun `up` pour l'instant

> **Pourquoi on vérifie ?** Pour être sûr de commencer le prochain tutoriel d'un état propre. Si un
> conteneur tourne déjà, les vérifications de santé du tutoriel 007 ne voudront rien dire.

---

### Étape 6 : Télécharge les trois images

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose pull
> ```

**Résultat attendu :** des lignes de progression pendant **3 à 8 minutes** (2 Go au total), puis
`Pulled` pour chaque service.

**✅ Vérifie que :**
- [ ] La commande se termine sans `error`
- [ ] Aucune ligne ne contient `manifest unknown`

> **Pourquoi `pull` avant `up` ?** `docker compose up -d` télécharge et démarre en même temps : si le
> téléchargement prend 5 minutes, tu ne sais pas si le silence est normal ou si c'est bloqué. En
> séparant les deux étapes, tu peux lancer le téléchargement, le laisser tourner, et ne revenir
> vérifier qu'une fois. C'est plus confortable quand on apprend.

---

### Étape 7 : Confirme que les images sont bien là

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}" | grep -E "jenkins|sonarqube|nexus"
> ```

**Résultat attendu :** trois lignes, une par image, avec leur taille.

**✅ Vérifie que :**
- [ ] Les trois dépôts apparaissent : `jenkins/jenkins`, `sonarqube`, `sonatype/nexus3`
- [ ] Les tailles sont plausibles : environ 500 Mo pour Jenkins, 700 Mo pour SonarQube, 700 Mo pour
      Nexus

---

### Étape 8 : Vérifie l'espace disque

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> df -h / | tail -1
> docker system df
> ```

**Résultat attendu :** `docker system df` affiche les images occupant environ **2 Go**.

**✅ Vérifie que :**
- [ ] Ton disque conserve au moins 30 Go libres
- [ ] Tu sais où se trouve la section `Images`

> **Pourquoi cette étape est utile ?** Parce que le tutoriel 007 va créer 5 volumes qui, eux,
> grossissent au fil des mois. Savoir surveiller `docker system df` évite de découvrir un disque
> plein un samedi soir avant une soutenance.

---

### Comment annuler

Pour supprimer les images téléchargées et récupérer environ 2 Go :

```bash
docker compose down --rmi all
```

Attention : `--rmi all` supprime **toutes** les images de ce projet Compose, y compris celles que tu
aurais pu télécharger pour un autre usage. Comme on est au début du projet, c'est sans conséquence.
Ne lance pas `--volumes` : la commande est plus longue et nous n'avons pas encore de données à
protéger.

---

## ✅ Avant de passer au tutoriel suivant

**Action :**

> Vérifie l'état du dépôt :
>
> ```bash
> git status
> ```

**Fichiers modifiés :**
- [ ] `docker-compose.yml` — inchangé depuis le tutoriel 005 (seules les images Docker ont été
      téléchargées, hors du dépôt)

> ⚠️ **Aucun commit n'est demandé ici.** Le premier commit du projet est fait dans la **story 006**.

**✅ Vérifie que :**
- [ ] `git status` ne montre que `docker-compose.yml` (non suivi) et `notes-story-001.txt`
- [ ] `docker compose ps` est toujours vide

---

## 🧹 Comment tout annuler

```bash
# supprimer les trois images téléchargées (~2 Go récupérés)
docker compose down --rmi all
#   ne PAS ajouter -v : cette option supprimerait les volumes
```

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `manifest unknown` | Une version est fausse. Reprends l'étape 4 et compare au caractère près avec la liste attendue |
| `no matching manifest for linux/amd64` | L'image n'existe pas pour ta'architecture CPU. Sur Apple Silicon, `sonarqube` et `jenkins` ont bien des versions `arm64` : vérifie avec `uname -m` puis `docker manifest inspect <image>` |
| `toomanyrequests: You have reached your pull rate limit` | Docker Hub limite le nombre de téléchargements anonymes par IP. Attends quelques heures, ou connecte-toi avec `docker login` |
| Le téléchargement semble figé | Le réseau est lent ou instable. Controle avec `docker compose pull --quiet` ; relance une seule fois, Docker réutilise les couches déjà téléchargées |
| `docker compose config --services` renvoie un service en trop | Un bloc a été collé deux fois. Relis `docker compose config` en entier |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel ! Tu as appris à :
- ✨ Utiliser `docker compose config` comme outil de débogage principal
- ✨ Séparer le téléchargement des images (`pull`) du démarrage (`up`)
- ✨ Surveiller la consommation d'espace disque avec `docker system df`

**Prochain tutoriel :** Tutoriel 007 - Démarrer le stack et attendre les healthchecks
