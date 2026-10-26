# Tutoriel 005 — Story #001 : Ajouter le service Nexus

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 004
**Prérequis :** services `jenkins` et `sonarqube` déclarés
**Exigence TP couverte :** `EXG-COM-01`, `EXG-COM-02`
**Fichier principal :** `docker-compose.yml`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Ajouter Nexus Repository au fichier Compose : c'est lui qui accueillera le registre Maven, le
dépôt d'images Docker et les artefacts JAR produits par le pipeline.

## 📋 Ce que tu vas apprendre

- Que l'image Nexus est une image **commerciale avec édition Community**, obtenue gratuitement
- La différence entre une variable `JAVA_OPTS` et `INSTALL4J_ADD_VM_PARAMS`
- À quoi correspond le chemin `/nexus-data` et pourquoi il est inhabituelse

---

## 🪜 Les étapes

### Étape 1 : Place le curseur à la fin du bloc `sonarqube`

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Descends à la dernière ligne du bloc SonarQube :
>
> ```yaml
>     networks:
>       - cicd
> ```
>
> Place ton curseur après cette ligne, puis appuie sur Entrée. La nouvelle ligne doit avoir
> **2 espaces** d'indentation (comme `jenkins:` et `sonarqube:`).

---

### Étape 2 : Écris l'en-tête du service et son image

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Écris :
>
> ```yaml
>
>   nexus:
>     image: sonatype/nexus3:3.96.4
>     container_name: cicd-nexus
> ```

**✅ Vérifie que :**
- [ ] `nexus:` est aligné sur `jenkins:` et `sonarqube:`
- [ ] Le tag est `3.96.4`, un tag **complet** (ni `latest`, ni `3`, ni `3.96`)

> **Pourquoi ce tag précis ?** L'image officielle Sonatype Nexus Repository 3 est publiée sous
> `sonatype/nexus3`. Elle nécessite une **adresse e-mail administrateur** pour démarrer, mais
> l'édition **Community** est gratuite : c'est ce que nous avons choisi. On épingle la version
> `3.96.4` pour que le format des dépôts reste stable dans le temps.

---

### Étape 3 : Déclare le port

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Continue le bloc :
>
> ```yaml
>     ports:
>       - "127.0.0.1:8081:8081"
> ```

**✅ Vérifie que :**
- [ ] Le port hôte est bien `8081`, et non `8080` (déjà pris par Jenkins)

> **Pourquoi 8081 ?** C'est le port historique de Nexus 3, et c'est celui qu'on retrouve dans
> toutes les documentations et tous les tutoriels : moins tu changes les conventions, moins tu
> perds de temps sur des détails. Jenkins occupe 8080, Nexus prend donc 8081.

---

### Étape 4 : Déclare le plafond mémoire

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Continue le bloc :
>
> ```yaml
>     environment:
>       INSTALL4J_ADD_VM_PARAMS: "-Xms512m -Xmx512m -XX:MaxDirectMemorySize=256m"
> ```

**Clés attendues :** `environment`, `INSTALL4J_ADD_VM_PARAMS`.

**✅ Vérifie que :**
- [ ] `-Xms512m` et `-Xmx512m` sont tous les deux présents
- [ ] `-XX:MaxDirectMemorySize=256m` est présent

> **Pourquoi `INSTALL4J_ADD_VM_PARAMS` et pas `JAVA_OPTS` ?** L'image Nexus n'est pas construite
> comme celle de Jenkins. Elle est produite par **install4j**, un outil de packaging qui ignore la
> variable `JAVA_OPTS`. Seul `INSTALL4J_ADD_VM_PARAMS` est lu par son script de démarrage. C'est une
> erreur classique : mettre `JAVA_OPTS` fonctionne silencieusement… c'est-à-dire **sans effet**,
> et Nexus consomme alors plusieurs gigaoctets au lieu de 512 Mio.
>
> **À quoi sert `MaxDirectMemorySize` ?** Nexus utilise la mémoire hors tas (mémoire allouée
> directement, à côté du tas Java) pour indexer ses dépôts. Sans plafond explicite, ce mécanisme
> peut faire exploser la consommation de la machine d'un coup.

---

### Étape 5 : Déclare le volume

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Continue le bloc :
>
> ```yaml
>     volumes:
>       - nexus_data:/nexus-data
> ```

**✅ Vérifie que :**
- [ ] Le chemin est bien `/nexus-data` (avec un **tiret**, pas un tiret bas)
- [ ] Aucun espace autour du `:` du couple `source:cible`

> **Pourquoi un chemin aussi inhabituel ?** Presque toutes les images Docker stockent leurs données
> dans `/data`, `/var/lib/xxx` ou `/opt/xxx`. Nexus est une exception : son répertoire de données
> s'appelle `/nexus-data`, à la racine. C'est le genre de détail qu'il faut copier tel quel, sinon le
> volume se monte au mauvais endroit et Nexus repart de zéro à chaque `up`.

---

### Étape 6 : Déclare le `healthcheck`

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Continue le bloc :
>
> ```yaml
>     healthcheck:
>       test: ["CMD-SHELL", "curl -fsS http://localhost:8081/service/rest/v1/status > /dev/null || exit 1"]
>       interval: 15s
>       timeout: 5s
>       retries: 40
>       start_period: 60s
>     restart: unless-stopped
>     networks:
>       - cicd
> ```

**Clés attendues :** `healthcheck`, `test`, `interval`, `timeout`, `retries`, `start_period`, `restart`,
`networks`.

**✅ Vérifie que :**
- [ ] Le `test` cible `/service/rest/v1/status` (et pas `/`, qui répond aussi quand le service est en
      cours d'initialisation)
- [ ] Le réseau `- cicd` est bien déclaré avec 6 espaces d'indentation

> **Pourquoi cette API-là ?** La racine `/` de Nexus renvoie une page HTML **dès le début** du
> démarrage, même quand la base n'est pas prête. Le point d'entrée `/service/rest/v1/status` ne
> répond `200` que lorsque le service est réellement opérationnel. Un `healthcheck` utile doit
> tester la **fonctionnalité**, pas la simple présence d'un processus.

---

### Étape 7 : Vérifie les trois services déclarés

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
- [ ] La sortie contient les trois noms : `jenkins`, `sonarqube`, `nexus`
- [ ] L'ordre est alphabétique, pas celui du fichier

> **Pourquoi l'ordre change-t-il ?** Parce que Compose trie ses services par nom. Si le compte ne te
> revient pas, compare avec `docker compose config`, qui lui respecte l'ordre du fichier.

---

### Étape 8 : Vérifie que le fichier reste valide

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose config --quiet && echo "YAML valide"
> ```

**Résultat attendu :** `YAML valide`.

**✅ Vérifie que :**
- [ ] La commande sort avec le code 0
- [ ] Aucun message d'erreur

---

### Comment annuler

Pour retirer Nexus : supprime le bloc `nexus:` écrit aux étapes 2 à 6, jusqu'à la dernière ligne
`- cicd`.

Le volume `nexus_data` reste déclaré dans la section `volumes:` : sans conséquence.

---

## ✅ Avant de passer au tutoriel suivant

**Action :**

> Vérifie l'état du dépôt :
>
> ```bash
> git status
> ```

**Fichiers modifiés :**
- [ ] `docker-compose.yml` — bloc `nexus` ajouté

> ⚠️ **Aucun commit n'est demandé ici.** Le premier commit du projet est fait dans la **story 006**.

**✅ Vérifie que :**
- [ ] `docker compose config --services` affiche exactement 3 services
- [ ] Aucun conteneur n'est démarré : tu en es encore à la phase de configuration

---

## 🧹 Comment tout annuler

```bash
# 1. supprimer le bloc nexus: dans docker-compose.yml
#    (de "  nexus:" jusqu'à la dernière ligne "      - cicd")
# 2. supprimer le fichier Compose s'il ne reste plus que le squelette
rm docker-compose.yml
```

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `service "nexus" has neither an image nor a build context` | Le bloc n'est pas indenté sous `services:`. Vérifie les 2 espaces de `nexus:` et 4 pour `image:` |
| Nexus consomme 4 Go au lieu de 512 Mio | Tu as écrit `JAVA_OPTS` au lieu de `INSTALL4J_ADD_VM_PARAMS` |
| `nexus_data` semble vide alors que Nexus tourne | Le chemin est mal écrit : c'est `/nexus-data`, pas `/nexus_data` ni `/data` |
| `manifest unknown` | Vérifie le tag : `sonatype/nexus3:3.96.4`. Les tags `latest` et `3` existent aussi, mais ne sont pas reproductibles |
| `docker compose config --services` affiche 2 services | Le bloc `nexus` a été écrit après la section `networks:` racine. Reprends l'étape 1 |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel ! Tu as appris à :
- ✨ Utiliser la bonne variable mémoire pour une image install4j (`INSTALL4J_ADD_VM_PARAMS`)
- ✨ Reconnaître un chemin de données non standard (`/nexus-data`) et ne pas le « corriger »
- ✨ Écrire un `healthcheck` qui teste la fonctionnalité et pas seulement le processus

**Prochain tutoriel :** Tutoriel 006 - Valider la configuration et télécharger les trois images
