# Tutoriel 006 — Story #008 : Mettre à jour le README (FR + EN)

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐
**Dépend de :** Tutoriel 005
**Prérequis :** la base de test est allumée ; `./mvnw -B verify` vert
**Exigence TP couverte :** `EXG-REND-01` (documentation)
**Fichier principal :** `README.md` et `README.en.md`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Mettre à jour « Lancer l'application en local » : depuis cette story, l'application a **besoin d'une
base** pour démarrer.

## 📋 Ce que tu vas apprendre

- Donner sa configuration à une application Spring **par l'environnement**
- Brancher un conteneur sur le réseau d'un **autre** projet Compose

---

## 🪜 Les étapes

### Étape 1 : Constate le problème

**Où :** la racine du projet

**Action :**

> ```bash
> java -jar target/step-by-step-0.1.0-SNAPSHOT.jar --server.port=8090 2>&1 | grep -m1 -E 'Failed to configure a DataSource|url'
> ```

**Résultat attendu :** une ligne qui contient `Failed to configure a DataSource: 'url' attribute is
not specified`, puis l'application s'arrête d'elle-même.

**Pourquoi ?** La commande du README de la story 007 ne dit pas **où** est la base. C'est voulu : le
`application.yml` ne contient aucune adresse de base. Chaque environnement (test, staging, prod)
donnera **la sienne**.

---

### Étape 2 : Lance l'application avec la base de test

**Où :** terminal 1, la racine du projet

**Action :**

> ```bash
> SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/stepbystep_test \
> SPRING_DATASOURCE_USERNAME=stepbystep SPRING_DATASOURCE_PASSWORD=test \
> java -jar target/step-by-step-0.1.0-SNAPSHOT.jar --server.port=8090
> ```

Puis, dans le **terminal 2** :

> ```bash
> curl -s localhost:8090/actuator/health; echo
> ```

**Résultat attendu :** `{"groups":["liveness","readiness"],"status":"UP"}`. Arrête ensuite
l'application (Ctrl+C dans le terminal 1).

**Pourquoi des variables `SPRING_DATASOURCE_…` ?** Spring lit **aussi** l'environnement :
`SPRING_DATASOURCE_URL` devient `spring.datasource.url`. C'est par là que Jenkins donnera les vrais
mots de passe de staging et de prod (credentials Jenkins → variables d'environnement), **sans** les
écrire dans un fichier.

---

### Étape 3 : Remplace la section FR

**Où :** `README.md`

**Action :**

> Remplace **toute** la section `## Lancer l'application en local` (du titre jusqu'à la ligne
> `Le port **8090** évite…` incluse) par :
>
> ````markdown
> ## Lancer l'application en local
>
> L'application a besoin d'une base PostgreSQL. En local, on utilise la base **de test**, jetable
> (identifiants de test `stepbystep` / `test`, sans données réelles) :
>
> ```bash
> docker compose -f src/test/resources/compose-test.yml up -d --wait   # base de test, port 5433
> ./mvnw -B verify                                                      # compiler, tests unitaires et d'intégration, emballer
> SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/stepbystep_test \
> SPRING_DATASOURCE_USERNAME=stepbystep SPRING_DATASOURCE_PASSWORD=test \
> java -jar target/step-by-step-0.1.0-SNAPSHOT.jar --server.port=8090
> curl localhost:8090/actuator/health                                   # {"status":"UP", …}
> ```
>
> Ou en conteneur, branché sur le réseau de la base de test :
>
> ```bash
> docker build -t step-by-step:0.1.0-SNAPSHOT .
> docker run -d --name step-by-step-local --network stepbystep-test_default -p 127.0.0.1:8090:8080 \
>   -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/stepbystep_test \
>   -e SPRING_DATASOURCE_USERNAME=stepbystep -e SPRING_DATASOURCE_PASSWORD=test \
>   step-by-step:0.1.0-SNAPSHOT
> ```
>
> Tests unitaires seuls, **sans Docker** : `./mvnw -B test`.
> Éteindre la base de test : `docker compose -f src/test/resources/compose-test.yml down`.
>
> Le port **8090** évite le conflit avec Jenkins (8080) et Nexus (8081).
> ````

**Pourquoi `postgres:5432` dans le conteneur, et `localhost:5433` hors du conteneur ?** Dans un
conteneur, `localhost` désigne **le conteneur lui-même**, pas ton poste. Branché sur le réseau
`stepbystep-test_default`, il joint la base par le **nom de son service** (`postgres`) et son port
**interne** (5432). C'est exactement le problème que la story 012 résoudra pour l'agent Jenkins.

---

### Étape 4 : Remplace la section EN

**Où :** `README.en.md`

**Action :**

> Remplace **toute** la section `## Run the application locally` (jusqu'à `Port **8090** avoids…`
> inclus) par :
>
> ````markdown
> ## Run the application locally
>
> The application needs a PostgreSQL database. Locally, use the throwaway **test** database
> (test-only credentials `stepbystep` / `test`, no real data):
>
> ```bash
> docker compose -f src/test/resources/compose-test.yml up -d --wait   # test database, port 5433
> ./mvnw -B verify                                                      # compile, unit and integration tests, package
> SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/stepbystep_test \
> SPRING_DATASOURCE_USERNAME=stepbystep SPRING_DATASOURCE_PASSWORD=test \
> java -jar target/step-by-step-0.1.0-SNAPSHOT.jar --server.port=8090
> curl localhost:8090/actuator/health                                   # {"status":"UP", …}
> ```
>
> Or as a container, attached to the test database network:
>
> ```bash
> docker build -t step-by-step:0.1.0-SNAPSHOT .
> docker run -d --name step-by-step-local --network stepbystep-test_default -p 127.0.0.1:8090:8080 \
>   -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/stepbystep_test \
>   -e SPRING_DATASOURCE_USERNAME=stepbystep -e SPRING_DATASOURCE_PASSWORD=test \
>   step-by-step:0.1.0-SNAPSHOT
> ```
>
> Unit tests only, **without Docker**: `./mvnw -B test`.
> Stop the test database: `docker compose -f src/test/resources/compose-test.yml down`.
>
> Port **8090** avoids clashing with Jenkins (8080) and Nexus (8081).
> ````

---

### Étape 5 : Teste la version conteneur du README

**Où :** la racine du projet

**Action :**

> 1. Copie-colle depuis ton `README.md` les deux commandes `docker build` et `docker run`.
> 2. Puis :
>
> ```bash
> timeout 60 sh -c 'until curl -sf localhost:8090/actuator/health; do sleep 2; done'; echo
> docker rm -f step-by-step-local
> ```

**Résultat attendu :** `{"groups":["liveness","readiness"],"status":"UP"}`, puis `step-by-step-local`.

**Pourquoi reconstruire l'image ?** Celle de la story 007 n'avait pas le modèle. Ton `Dockerfile` n'a
**pas** changé : il emballe simplement le nouveau code. Le copier-coller depuis le README prouve que
la doc **marche**.

**✅ Vérifie que :**
- [ ] `"status":"UP"`
- [ ] Conteneur supprimé

---

## 🧹 Comment tout annuler

```bash
git restore README.md README.en.md
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `network stepbystep-test_default not found` | La base de test est éteinte : `docker compose -f src/test/resources/compose-test.yml up -d --wait` |
| `Connection to localhost:5432 refused` dans les logs du conteneur | L'URL du conteneur doit viser `postgres:5432`, pas `localhost` |
| `Port 8090 was already in use` | Une instance de l'étape 2 tourne encore : Ctrl+C dans le terminal 1 |
| `Conflict. The container name "/step-by-step-local" is already in use` | Un `docker run` précédent a échoué (réseau absent, port pris…) mais a laissé un conteneur `Created`, jamais démarré : `docker rm -f step-by-step-local`, puis relance **seulement** le `docker run` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| JAR + base | étape 2 | `"status":"UP"` |
| Conteneur + base (copié du README) | étape 5 | `"status":"UP"` |
| Les deux README | `grep -c compose-test README.md README.en.md` | `2` et `2` |

---

## 🎉 Bravo !

Ton README dit la vérité sur la nouvelle version de l'application. 🎉

Prochain tutoriel : **#007** — Éteindre la base, commiter, pousser, bilan ⏱️ 15 min
