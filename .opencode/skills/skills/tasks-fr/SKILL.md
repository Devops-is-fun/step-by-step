---
name: tasks-fr
description: Découpe une story CI/CD en tutoriels pas-à-pas TDA-friendly (infrastructure, Jenkins, Docker, Maven) avec points de contrôle explicites
---

# Skill: Tasks-FR (TDA-friendly — TP CI/CD)

## Contexte du projet (à connaître avant toute chose)

| Élément | Valeur |
|---------|--------|
| Nature du travail | TP du cours CI/CD (ESGI Master 1 « Information Architecture des Logiciels ») |
| Énoncé de référence | `docs/Projet_CICD_Jenkins.pdf` |
| Travail | **En solo.** L'exigence « en groupe / trinôme / quadrinôme » de l'énoncé est **ignorée** |
| Application web | **Java / Spring Boot**, build **Maven**, tests **JUnit 5**, lint via plugin Maven |
| Outillage CI/CD | Docker Compose : **Jenkins** (controller), **SonarQube**, **Nexus** (dépôt d'artefacts) |
| Environnements | **staging** et **production**, deux stacks **Docker Compose en local** |
| Exigence transverse | Les builds s'exécutent sur des **agents**, **jamais sur le controller** |
| Secrets | **Uniquement** dans les credentials Jenkins. Jamais dans un fichier versionné |

## Partage des rôles (à partir de la story 007)

| Qui | Quoi |
|-----|------|
| L'utilisatrice | toutes les décisions (questions de cadrage) |
| Claude | **implémente directement** le code applicatif (classes, tests, dépendances applicatives du `pom.xml`), le vérifie (`./mvnw -B verify`), puis le présente dans un tutoriel « 🤖 Claude a implémenté » (découvrir + vérifier) |
| L'utilisatrice, guidée | tout ce qui touche à la CI/CD : réglages Sonar/JaCoCo du `pom.xml`, `Dockerfile`, scripts, Jenkins, Nexus — tutoriels « 🧑‍💻 Tu fais (guidée) » |
| L'utilisatrice | les commits et les push (jamais faits par Claude) |

Zone grise (plugin Maven de qualité, profil de build) : classée CI/CD par défaut, et signalée.

## Qu'est-ce que je fais

Je prends une story de la chaîne CI/CD et je la découpe en **tutoriels** élémentaires.
Chaque tutoriel est une suite d'instructions **explicites, exécutables une par une**, adaptées à une
**personne en apprentissage avec un TDA**.

Ce n'est pas un texte à comprendre, c'est un **mode d'emploi** : à chaque étape, tu sais exactement
quoi taper, où, quoi attendre, et comment vérifier.

**Principe fondamental :** Aucune décision technique n'est prise sans l'accord explicite de
l'utilisateur. Si un choix est nécessaire, je pose la question **dans le chat**.

## Quand m'utiliser

Après avoir créé les user stories avec `story-fr`, quand tu veux passer à l'implémentation.

## Workflow

### Étape 1 — Lecture de la story

- Prendre le numéro de story fourni en argument (ex : `/ tasks-fr 001`)
- Lire `docs/stories/story-XXX.md`
- Lire `docs/specification/specification.md` (notamment la matrice de traçabilité) pour le contexte
- Lire les tutoriels déjà produits dans `docs/tasks/story-XXX/` pour ne pas répéter ce qui est fait
- Lister les **questions techniques** à trancher avant de découper

### Étape 2 — Questions de cadrage (dans le chat)

Je pose au maximum **5 questions par lot**, dans le chat, au format :

```text
Question 1 — <sujet>
  a) <option A>  → <conséquence>       (⭐ recommandé : <raison>)
  b) <option B>  → <conséquence>
Réponds avec : 1a, 2b, 3c...
```

Je n'écris **aucun tutoriel** tant que l'utilisateur n'a pas répondu. Les réponses sont ensuite
reportées dans le `## Journal des décisions` du premier tutoriel généré.

### Étape 3 — Découpage et génération des tutoriels

**Règles de découpage spécifiques TDA + infrastructure :**

- Chaque tutoriel dure **10 à 25 minutes maximum**. Au-delà → **redécouper immédiatement**
- Un tutoriel = **une seule tâche mentale**. Exemples :
  - « Créer le fichier `docker-compose.yml` avec Jenkins » ≠ « Démarrer et vérifier le stack »
  - « Créer l'agent dans l'UI Jenkins » ≠ « Vérifier que l'agent est `Online` »
  - « Écrire l'étape `stages { test { ... } }` » ≠ « Relancer le build et lire le résultat »
- **Une action = une commande, ou un clic d'interface, ou un fichier.**
  Jamais « configure Jenkins, SonarQube et Nexus » (3 actions = 3 étapes).
- **Zéro décision** dans le tutoriel : tout est déjà décidé (nom de fichier, contenu exact, valeur exacte)
- Privilégier le **faire avant de comprendre** : on agit, puis on explique dans *Pourquoi*

**Adaptation à l'infrastructure (obligatoire) :**

| Situation | Le tutoriel doit préciser |
|-----------|--------------------------|
| Commande shell | Le **répertoire de travail** dans lequel la taper |
| Commande qui démarre un truc | Le **temps d'attente** réaliste et le signe de succès |
| Opération dans l'UI Jenkins | Le **chemin exact des menus** : `Manage Jenkins → Nodes → New node` |
| Opération dans l'UI Jenkins | Le **nom du bouton exact** : `Save`, `Apply`, `Test Connection` |
| Opération dans SonarQube/Nexus | L'**URL** (`http://localhost:9000`) et le chemin des menus |
| Modification d'un fichier | Le **contenu exact** à écrire (jamais « ajoute la config » ) |
| Modification d'un YAML | La **liste des clés attendues**, pour repérage visuel |

**Critères de qualité (obligatoires, à vérifier pour chaque tutoriel) :**

- ✅ Chaque instruction contient **un seul verbe d'action** (« Crée », « Ajoute », « Exécute », « Clique »)
- ✅ Le répertoire de travail ou l'URL est **nommé explicitement**
- ✅ Le contenu à écrire / la commande à taper est **donné textuellement**
- ✅ Chaque étape a un **« Résultat attendu »** (« tu dois voir `jenkins Started »`)
- ✅ Un **point de contrôle** (« Vérifie que ») au moins toutes les 3-4 instructions
- ✅ Une section **« Comment annuler »** pour les étapes à effet de bord
- ✅ Une section **« Si tu es bloqué »** avec des solutions concrètes, pas des généralités
- ✅ Un bloc **« Pourquoi »** après chaque notion nouvelle (pour l'apprentissage)
- ✅ Une **estimation du temps** en minutes
- ✅ Une **section de célébration** à la fin
- ✅ Une section finale **`## ✅ Avant de passer au tutoriel suivant`** (voir plus bas)

### Étape 4 — Vérification de cohérence

Analyser les tutoriels générés entre eux :

- Aucune dépendance circulaire, ordre d'exécution non contradictoire
- Chaque tutoriel a des **entrées et sorties claires** (ce qui doit exister avant, ce qui existe après)
- La somme des tutoriels couvre **100 % des critères d'acceptation** de la story
- Aucun doublon ni chevauchement entre tutoriels
- Cohérence des noms de variables, clés YAML, ports, chemins, versions
- **Cohérence avec l'énoncé** : chaque exigence `EXG-*` visée par la story est couverte
- **Cohérence avec les exigences transverses** : builds sur agents (pas sur le controller), secrets
  uniquement dans les credentials Jenkins, staging/prod séparés
- **Cohérence avec l'existant** : fichiers référencés qui existent déjà, ou explicitement marqués « à créer »
- Chaque tutoriel respecte **tous les critères TDA**

Si un problème est trouvé → corriger le tutoriel avant de le livrer.

### Étape 5 — Présentation

- Lister les tutoriels dans l'ordre, avec temps estimé et dépendances
- Laisser l'utilisateur choisir par où commencer s'il y a des tutoriels indépendants
- Passer à l'implémentation

## Structure d'un tutoriel

Chaque tutoriel est un fichier `docs/tasks/story-XXX/task-YYY.md` :

```
docs/tasks/story-XXX/
├── README.md          ← plan de la story (ordre, temps, jalons)
├── task-001.md
├── task-002.md
└── ...
```

## Format du tutoriel (task-YYY.md)

```markdown
# Tutoriel #YYY - Story #XXX : [titre très court]

**Temps estimé :** ⏱️ XX minutes
**Difficulté :** ⭐ (1 à 5)
**Dépend de :** Tutoriel #ZZZ (à faire avant) / Aucune
**Prérequis :** [état du système attendu, ex: « stack `docker compose up -d` déjà démarré »]
**Exigence TP couverte :** `EXG-COM-03`
**Fichier principal :** `jenkins/Dockerfile` (ou `http://localhost:8080` pour une étape d'interface)

---

## 🎯 Objectif

[Une phrase simple]

## 📋 Ce que tu vas apprendre

- [Compétence 1]
- [Compétence 2]
- [Compétence 3 maximum]

---

## 🪜 Les étapes

### Étape 1 : [verbe au présent + titre court]

**Où :** [répertoire exact ou URL]

**Action :**

> Exécute cette commande :
>
> ```bash
> docker compose ps
> ```

**Résultat attendu :**

> Un tableau avec les colonnes `NAME`, `IMAGE`, `STATUS`, `PORTS`, et `STATUS` valant
> `Up ... (healthy)` pour le conteneur qui expose un healthcheck.

**Pourquoi :** [explication courte, 1 à 3 phrases, pour l'apprentissage]

**✅ Vérifie que :**

- [ ] La commande ne renvoie pas d'erreur
- [ ] Le statut du conteneur est `Up`
- [ ] Tu n'as pas lancé de build (cette étape est une simple vérification)

---

### Étape 2 : [titre court]

**Où :** ...

**Action :**

> Crée le fichier `docker-compose.yml` à la racine du dépôt avec ce contenu **exact** :
>
> ```yaml
> services:
>   jenkins:
>     image: jenkins/jenkins:lts-jdk17
>     ...
> ```

**Résultat attendu :** [ce que le fichier doit contenir, où le vérifier]

**Comment annuler :** [ce qu'il faut faire pour revenir en arrière]

**✅ Vérifie que :**

- [ ] Le fichier est à la racine du dépôt
- [ ] `docker compose config` ne signale aucune erreur

---

### Étape N : Vérifier le résultat

**Où :** ...

**Action :**

> [Commande ou manipulation de vérification finale]

**Résultat attendu :** [preuve visible]

---

## 🧹 Comment tout annuler

[Liste numérotée des commandes de retour arrière, dans l'ordre inverse des étapes]

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `permission denied while trying to connect to the Docker daemon` | Docker n'est pas lancé ou ton utilisateur n'est pas dans le groupe `docker` : `sudo systemctl start docker`, puis `sudo usermod -aG docker $USER` et **redémarre ta session**. |
| `port is already allocated` | Un autre processus occupe le port. Trouve-le : `sudo lsof -i :8080` puis arrête le processus, ou change le port dans `docker-compose.yml` **et** dans tes URL. |
| Le conteneur `restarting` en boucle | Le service a crashé au démarrage. Lis les logs : `docker compose logs --tail=50 jenkins`. |
| La commande prend plus de temps que prévu | Jenkins et SonarQube mettent 1 à 3 minutes au premier démarrage (installation de plugins, création de la base interne). **Attends**, ce n'est pas un blocage. |
| [Problème spécifique au tutoriel] | [Solution concrète] |

---

## 📚 Aide-mémoire des commandes de vérification

| Besoin | Commande |
|--------|----------|
| Voir les conteneurs et leur état | `docker compose ps` |
| Lire les logs d'un service | `docker compose logs --tail=50 jenkins` |
| Voir les images construites | `docker images` |
| Tester une URL | `curl -i http://localhost:8080` |
| Tester une URL et ne garder que le code HTTP | `curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080` |
| Attendre qu'un endpoint réponde | `until curl -sf http://localhost:8080 > /dev/null; do sleep 5; echo "en attente..."; done` |
| Compiler et tester le projet | `./mvnw -B verify` |
| Tests unitaires seuls | `./mvnw -B test` |
| Tests d'intégration seuls | `./mvnw -B verify -DskipUnitTests` (adapter selon la config du projet) |
| Rapports JUnit générés | rapports dans `target/surefire-reports/` et `target/failsafe-reports/` |
| Couverture JaCoCo générée | `target/site/jacoco/index.html` |
| Lancer un stack d'environnement | `cd environments/staging && docker compose up -d` |
| Voir les logs d'un environnement | `docker compose -f environments/staging/docker-compose.yml logs -f` |
| Historique Git avec les messages de commit | `git log --oneline -10` |
| Fichiers modifiés non versionnés | `git status` |
| Voir ce qui est prêt à être commité | `git add -n .` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

**Cette section est obligatoire dans chaque tutoriel.** C'est mon récapitulatif de tout ce que tu
dois avoir constaté avant d'enchaîner.

### Ce que tu dois avoir fait

- [ ] Étape 1 → [action]
- [ ] Étape 2 → [action]
- [ ] Étape N → [action]

### Ce que tu dois avoir vérifié

| Vérification | Comment vérifier | Attendu |
|--------------|------------------|---------|
| [ex: le conteneur jenkins tourne] | `docker compose ps` | `Up ... (healthy)` |
| [ex: l'endpoint répond] | `curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080` | `200` |
| [ex: le build est vert] | build Jenkins n° <N> | `Finished: SUCCESS` |

### 🛡️ Garde-fous du TP

- [ ] Aucun secret n'a été écrit dans un fichier versionné (juste des références à des credentials Jenkins)
- [ ] Aucun build ne s'est exécuté sur le controller Jenkins
- [ ] `git status` ne montre pas de fichier inattendu (`.env`, `*.key`, token Nexus/Sonar)

### Ce que tu dois avoir versionné

```bash
git status
```

- [ ] Fichier 1 : `chemin/vers/fichier1`
- [ ] Fichier 2 : `chemin/vers/fichier2`

Puis :

```bash
git add chemin/vers/fichier1 chemin/vers/fichier2
git commit -m "type(scope): description concise en anglais"
```

**Types de commit autorisés** (Conventional Commits, exigé par le module N1 de l'énoncé) :

| Type | Quand l'utiliser |
|------|------------------|
| `feat` | nouvelle fonctionnalité de l'app ou du pipeline |
| `fix` | correction d'un bug |
| `docs` | documentation uniquement |
| `test` | ajout de tests |
| `build` | `Dockerfile`, `docker-compose.yml`, `pom.xml` |
| `ci` | `Jenkinsfile`, Shared Library, config Jenkins/Sonar |
| `chore` | nettoyage, `.gitignore`, outillage annexe |

Le message est **en anglais**, de la forme : `type(scope): description`, `scope` étant le domaine
concerné (`app`, `jenkins`, `pipeline`, `envs`, `sonar`, `nexus`).

### ⚠️ Si une vérification échoue

**Ne passe pas au tutoriel suivant.** Note l'étape exacte qui a échoué et le message d'erreur
complet, puis reprends le tableau « Si tu es bloqué » de ce tutoriel.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ [Compétence 1]
- ✨ [Compétence 2]
- ✨ [Compétence 3]

Prochain tutoriel : **#ZZZ** — [Titre] ⏱️ [temps]

Pause méritée ☕
```

## Règle d'or des secrets

À **chaque** tutoriel qui touche Jenkins, SonarQube, Nexus ou un déploiement :

1. **Jamais** de secret littéral dans un fichier versionné (`.env`, `pom.xml`, `Jenkinsfile`, `settings.xml` versionné)
2. Toujours passer par les **credentials Jenkins** et y faire référence par nom dans le `Jenkinsfile` :

```groovy
withCredentials([usernamePassword(
    credentialsId: 'nexus-deploy-creds',
    usernameVariable: 'NEXUS_USER',
    passwordVariable: 'NEXUS_PASSWORD'
)]) {
    sh './mvnw -B deploy'
}
```

3. Si une étape est ambiguë sur ce point, **c'est une question à poser à l'utilisateur**, pas une décision à prendre.
4. **Jamais** demander de « noter » un mot de passe initial : donner la commande qui le lit à la volée
   (`docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword`,
   `docker compose exec nexus cat /nexus-data/admin.password`). Les mots de passe définis ensuite vont
   dans le gestionnaire de mots de passe.

## Distinction controller / agent (exigence `EXG-COM-01`)

RappelPermanent : l'énoncé impose que les builds s'exécutent sur des **agents**, jamais sur le
controller. Chaque tutoriel qui crée ou utilise un agent doit :

- ✅ Déclarer un `agent { label '...' }` **et** un numéro d'exécuteur dans le `Jenkinsfile`
- ✅ Configurer le nombre d'exécuteurs du controller à **0** (ou 1 max pour les jobs d'admin)
- ✅ Préciser dans « Vérifie que » que le build s'est exécuté sur l'agent et non sur `Built-In Node`

## Exemple d'utilisation

```
/ tasks-fr 001
```

**Story #001 — Outillage : stack Docker Compose (Jenkins, SonarQube, Nexus)**

Questions posées dans le chat avant découpage :

- Question 1 — Versions d'images ?
  a) `jenkins/jenkins:lts-jdk17` + `sonarqube:9.9-community` + `sonatype/nexus3:latest` (⭐ recommandé : LTS stables, cours-compatible)
  b) `latest` partout → risque de casse après une mise à jour non maîtrisée
- Question 2 — Emplacement du fichier compose ?
  a) `docker-compose.yml` à la racine du dépôt (⭐ recommandé : l'énoncé dit « démarré en une commande »)
  b) `infrastructure/docker-compose.yml` → moins visible pour le correcteur

**Découpage obtenu :**

| # | Titre | Temps | Prérequis |
|---|-------|-------|-----------|
| 001 | Écrire le `docker-compose.yml` des 3 services | 25 min | Docker installé |
| 002 | Démarrer le stack et vérifier les 3 conteneurs | 15 min | 001 |
| 003 | Récupérer le mot de passe initial Jenkins et débloquer l'assistant | 20 min | 002 |
| 004 | Installer les plugins Jenkins requis | 20 min | 003 |
| 005 | Configurer SonarQube (auth + projet) | 25 min | 002 |
| 006 | Configurer Nexus (repo `maven-releases`) | 20 min | 002 |
| 007 | Créer un agent et vérifier qu'il est `Online` | 25 min | 003 |
| 008 | Interdire les builds sur le controller + commit | 15 min | 007 |

Temps total : ≈ 2 h 45. Chaque tutoriel se vérifie indépendamment et produit un commit.

## Erreurs fréquentes à éviter

| Erreur | Pourquoi c'est un problème | Comment l'éviter |
|--------|---------------------------|------------------|
| `git restore --staged` avant le premier commit | Pas de `HEAD` : `fatal: could not resolve HEAD`, le fichier reste dans l'index | Avant le premier commit, retirer de l'index avec `git rm --cached <fichier>` |
| Écrire un tutoriel de 45 min | Trop lourd pour une charge TDA : on perd le fil et on ne sait plus où on en est | Scinder à 25 min maximum |
| « Configure SonarQube » | 3 notions dans une phrase, impossible à vérifier | Une action par étape |
| Oublier « Résultat attendu » | Impossible de savoir si l'étape a réussi | Toujours l'écrire, même en une ligne |
| Ne pas préciser le répertoire | `./mvnw` échoue si tu es dans le mauvais dossier | Toujours `Où :` |
| Sauter la section de vérification finale | L'utilisateur avance sur une étape en échec | Section obligatoire, générée systématiquement |
| Faire commiter des secrets | Viole l'exigence de l'énoncé et pollue l'historique Git | Vérifier `git status` avant chaque commit |
| Motif `grep` / regex entre guillemets doubles | bash interprète `$`, `$[` ou `` ` `` avant `grep` : la commande plante ou cherche autre chose | Toujours des quotes simples `'...'` autour d'un motif |
| `docker compose exec` dans un script, sans `< /dev/null` | La commande lit l'entrée standard et peut « avaler » la suite du script (pipeline, `bash -s`) : le script s'arrête en silence avec le code `0` | Toujours `docker compose exec -T … < /dev/null` dans un script |
| Tester un agent avec `docker exec` alors que Jenkins y entre par SSH | `docker exec` (et `group_add`) donne des groupes qu'une session SSH n'a pas : le test passe, le build échoue | Tester **par le même chemin que Jenkins** (une vraie session SSH) |
| Tester un agent avec `docker exec` alors que Jenkins y entre par SSH | `docker exec` (et `group_add`) donne des groupes qu'une session SSH n'a pas : le test passe, le build échoue | Tester **par le même chemin que Jenkins** (une vraie session SSH) |
| N'accepter que `200` pour « le service répond » | `302`, `401`, `403` sont des réponses valides (redirection, page protégée) : faux échec | « Répond » = tout code sauf `000` et `5xx` |
| Annoncer un comportement non vérifié (emplacement d'un mot de passe, contenu d'une page, effet d'un `.gitignore` sur un fichier déjà indexé) | Le tutoriel se contredit ou fait modifier la configuration par erreur | Vérifier sur la version **épinglée** de l'outil ; en cas de doute, le dire dans le tutoriel |
| Installer des plugins non vus en cours | Contre-productif pour la note : tu dois savoir expliquer | Se limiter aux plugins du cours, justifier tout le reste |