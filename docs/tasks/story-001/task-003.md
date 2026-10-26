# Tutoriel 003 — Story #001 : Ajouter le service Jenkins

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** `docker-compose.yml` valide avec ses sections `networks` et `volumes`
**Exigence TP couverte :** `EXG-COM-01`, `EXG-COM-02`
**Fichier principal :** `docker-compose.yml`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Ajouter le service Jenkins dans `docker-compose.yml`, avec son port restreint à la machine locale, son
plafond de mémoire et une vérification de santé automatique.

## 📋 Ce que tu vas apprendre

- Épingler la version d'une image Docker (`image: nom:version`) au lieu d'un tag flottant
- Ce qu'est un `healthcheck` et à quoi sert le champ `start_period`
- Pourquoi un port hôte s'écrit `ADRESSE:PORT:PORT`

---

## 🪜 Les étapes

### Étape 1 : Place le curseur sous `services:`

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Dans `docker-compose.yml`, replaces ton curseur **entre** la ligne `services:` et le commentaire
> `# Les trois services seront ajoutés...`, puis ajoute deux espaces d'indentation. Garde cette
> indentation pour toutes les étapes suivantes de ce tutoriel.

**Résultat attendu :** ton curseur est aligné deux caractères à droite de la marge.

---

### Étape 2 : Écris le bloc Jenkins

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Écris ce bloc complet, en respectant les deux espaces d'indentation :
>
> ```yaml
>   jenkins:
>     image: jenkins/jenkins:2.541.3-lts-jdk17
>     container_name: cicd-jenkins
>     ports:
>       - "127.0.0.1:8080:8080"
>       - "127.0.0.1:50000:50000"
>     environment:
>       JAVA_OPTS: "-Xms512m -Xmx512m"
>     volumes:
>       - jenkins_home:/var/jenkins_home
>     healthcheck:
>       test: ["CMD-SHELL", "curl -fsS http://localhost:8080/login > /dev/null || exit 1"]
>       interval: 15s
>       timeout: 5s
>       retries: 20
>       start_period: 90s
>     restart: unless-stopped
>     networks:
>       - cicd
> ```

**Clés attendues, pour repérage visuel :** `jenkins`, `image`, `container_name`, `ports`,
`environment`, `JAVA_OPTS`, `volumes`, `healthcheck`, `test`, `interval`, `timeout`, `retries`,
`start_period`, `restart`, `networks`.

**✅ Vérifie que :**
- [ ] `image:` contient bien `:2.541.3-lts-jdk17` — pas `:lts-jdk17`, pas `:latest`
- [ ] Chaque ligne est indentée de deux espaces de plus que la précédente
- [ ] `jenkins_home:/var/jenkins_home` n'a **pas** d'espace autour du `:` (c'est un chemin, pas un couple clé/valeur YAML)
- [ ] Le bloc est **entièrement** indenté sous `services:`

> **Pourquoi la version épinglée ?** `2.541.3` est la **dernière** version LTS de Jenkins qui propose
> une image `jdk17`. Les LTS plus récentes ont supprimé cette variante. Épingler la version, c'est
> garantir que le comportement de ton outillage ne changera pas tout seul — c'est exactement ce
> qu'attend le critère « reproductibilité » du barème.

> **Pourquoi `127.0.0.1:8080:8080` ?** La syntaxe est `ADRESSE_HOTE:PORT_HOTE:PORT_CONTENEUR`.
> En écrivant `127.0.0.1` devant le port, tu fais écouter le service **uniquement** sur ta machine.
> Jenkins est un contrôleur d'exécution : personne, pas même ton colocataire, ne doit pouvoir y
> accéder par le réseau local. Le seul accès public prévu est le tunnel de la story 010.

> **Pourquoi `start_period: 90s` ?** Au premier démarrage, Jenkins doit décompresser ses plugins et
>(initialiser son annuaire : une à deux minutes. Pendant cette période, les échecs de `healthcheck`
> ne comptent pas. Sans ce champ, Docker déclare le conteneur `unhealthy` avant même qu'il ait eu le
> temps de démarrer.

> **Pourquoi le port 50000 ?** C'est le port d'écoute des agents Jenkins entrants. Il n'est pas encore
> utilisé : le story 005 ajoutera un agent. On l'ouvre dès maintenant pour que la story 005 n'ait pas
> à modifier le fichier.

---

### Étape 3 : Vérifie que Compose comprend le service

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose config
> ```

**Résultat attendu :** une sortie qui contient `image: jenkins/jenkins:2.541.3-lts-jdk17` et
`published: "8080"`.

**✅ Vérifie que :**
- [ ] La section `services:` ne contient qu'un bloc, `jenkins`
- [ ] Aucune ligne `error` n'apparaît
- [ ] `networks: [cicd]` est présent sous le bloc

---

### Étape 4 : Vérifie les clés en YAML

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose config --services
> ```

**Résultat attendu :** la ligne `jenkins`, et rien d'autre.

**✅ Vérifie que :**
- [ ] La sortie est exactement `jenkins`
- [ ] Si tu vois `sonarqube` ou `nexus`, tu as mal rattaché un bloc : il est indenté au mauvais endroit

---

### Étape 5 : Vérifie l'image sans la démarrer

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose pull jenkins
> ```

**Résultat attendu :** des lignes de progression, puis `Pulled` ou `Image ... already present`.
Compte **1 à 4 minutes** la première fois : l'image fait environ 500 Mo.

**✅ Vérifie que :**
- [ ] La commande se termine sans `error`
- [ ] `docker images | grep jenkins` affiche une ligne avec le tag `2.541.3-lts-jdk17`

> **Pourquoi cette étape avant de démarrer ?** Télécharger 500 Mo maintenant, pendant que tu es
> concentré, vaut mieux que de le découvrir coincé dans un `up -d` pendant que tu attends le résultat
> du tutoriel suivant.

---

### Comment annuler

Pour retirer uniquement le service Jenkins du fichier : supprime le bloc `jenkins:` écrit à l'étape 2,
en gardant `services:` et son commentaire.

Pour supprimer l'image téléchargée (gagne ~500 Mo) :

```bash
docker rmi jenkins/jenkins:2.541.3-lts-jdk17
```

Le volume `cicd_jenkins_home` n'existe pas encore : il sera créé au tutoriel 007.

---

## ✅ Avant de passer au tutoriel suivant

**Action :**

> Vérifie l'état du dépôt :
>
> ```bash
> git status
> ```

**Fichiers modifiés :**
- [ ] `docker-compose.yml` — bloc `jenkins` ajouté sous `services:`

> ⚠️ **Aucun commit n'est demandé ici.** Le premier commit du projet est fait dans la **story 006**.

**✅ Vérifie que :**
- [ ] `docker-compose.yml` est la seule modification de la story
- [ ] Aucun fichier `.env` n'apparaît

---

## 🧹 Comment tout annuler

```bash
# 1. retirer l'image Jenkins (~500 Mo récupérés)
docker rmi jenkins/jenkins:2.541.3-lts-jdk17
# 2. supprimer le bloc jenkins: dans docker-compose.yml
#    (de la ligne "  jenkins:" jusqu'à la dernière ligne "      - cicd")
# 3. supprimer le fichier Compose s'il ne reste plus que le squelette
rm docker-compose.yml
```

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `service "jenkins" has neither an image nor a build context` | La ligne `image:` est mal indentée : elle doit être à 4 espaces, pas 2 |
| `invalid interpolation format` | Une accolade `${` s'est glissée dans le fichier. Utilise `$(...)` ou supprime-la |
| `port is already allocated` | Le port est déjà pris. Change `127.0.0.1:8080:8080` en `127.0.0.1:8089:8080`, et note la nouvelle valeur pour le README |
| `manifest for jenkins/jenkins:2.541.3-lts-jdk17 not found` | Vérifie la saisie du tag. La liste des tags valides est sur `hub.docker.com/r/jenkins/jenkins/tags?image_filter=jdk17` |
| `Error response from daemon: pull access denied` | Docker ne peut pas télécharger l'image. Teste `docker pull jenkins/jenkins:2.541.3-lts-jdk17` seul pour avoir le message complet |
| Le fichier devient rouge dans l'éditeur | L'extension YAML signale un problème d'indentation. Compare avec les tutoriels : 2 espaces par niveau, jamais de tabulation |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel ! Tu as appris à :
- ✨ Déclarer un service Compose avec une version d'image **épinglée**
- ✨ Restreindre l'exposition d'un port à `127.0.0.1` avec la syntaxe `ADRESSE:PORT:PORT`
- ✨ Écrire un `healthcheck` avec `start_period`, indispensable pour un service lent à démarrer

**Prochain tutoriel :** Tutoriel 004 - Ajouter le service SonarQube
