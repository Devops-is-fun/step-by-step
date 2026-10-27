# Tutoriel 006 — Story #007 : Construire l'image et la tester

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 005
**Prérequis :** `docker build --check .` sans avertissement
**Exigence TP couverte :** `EXG-COM-12`
**Fichier principal :** l'image `step-by-step:0.1.0-SNAPSHOT`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Construire l'image, la démarrer en conteneur, et constater qu'elle répond `UP`.

---

## 🪜 Les étapes

### Étape 1 : Construis l'image

**Où :** la racine du projet

**Action :**

> ```bash
> docker build -t step-by-step:0.1.0-SNAPSHOT .
> ```

**Résultat attendu :** environ **1 minute** la première fois (téléchargement des images Temurin et
des dépendances), puis `naming to docker.io/library/step-by-step:0.1.0-SNAPSHOT`.

**Pourquoi ce tag ?** Il reprend la version du `pom.xml`. La story 014 remplacera `SNAPSHOT` par une
vraie version (`0.1.0-<sha>`).

---

### Étape 2 : Regarde la taille

**Où :** la racine du projet

**Action :**

> ```bash
> docker images step-by-step
> ```

**Résultat attendu :** une ligne `step-by-step   0.1.0-SNAPSHOT`, environ **410 Mo**.

**Pourquoi c'est bien ?** L'atelier (JDK + Maven + dépendances) dépasse le Go ; le colis livré n'en
garde que le JRE et le JAR.

---

### Étape 3 : Démarre un conteneur

**Où :** la racine du projet

**Action :**

> ```bash
> docker run -d --name step-by-step-local -p 127.0.0.1:8090:8080 step-by-step:0.1.0-SNAPSHOT
> ```

**Résultat attendu :** un long identifiant de conteneur.

**Pourquoi `8090:8080` ?** **Dans** le conteneur, l'application écoute sur 8080 (son port par
défaut). **Sur ton poste**, on la publie sur 8090, car 8080 est à Jenkins. `127.0.0.1` : joignable
seulement depuis ta machine, comme l'outillage.

---

### Étape 4 : Interroge la santé

**Où :** la racine du projet

**Action :**

> ```bash
> docker ps --filter name=step-by-step-local --format '{{.Names}} {{.Status}}'
> timeout 60 sh -c 'until curl -sf localhost:8090/actuator/health; do sleep 2; done'; echo
> ```

**Résultat attendu :** d'abord `step-by-step-local Up …` (si cette ligne est **vide**, le conteneur
n'existe pas : refais l'étape 3). Puis, en quelques secondes :

```json
{"groups":["liveness","readiness"],"status":"UP"}
```

**Pourquoi `timeout 60` ?** `until … done` réessaie **tant que** l'application ne répond pas. Si
elle ne répond jamais (conteneur absent ou planté), la boucle tournerait sans fin. `timeout 60`
l'arrête au bout d'une minute.

**✅ Vérifie que :**
- [ ] `"status":"UP"`

---

### Étape 5 : Vérifie l'utilisateur

**Où :** la racine du projet

**Action :**

> ```bash
> docker exec step-by-step-local id
> ```

**Résultat attendu :** `uid=999(app) gid=999(app) groups=999(app)`. Pas `root`.

---

### Étape 6 : Supprime le conteneur de test

**Où :** la racine du projet

**Action :**

> ```bash
> docker rm -f step-by-step-local
> ```

**Résultat attendu :** `step-by-step-local`

**Pourquoi le supprimer ?** C'était un essai. Les vrais environnements arrivent en story 015 (staging)
et 017 (production). L'**image**, elle, reste.

---

## 🧹 Comment tout annuler

```bash
docker rm -f step-by-step-local
docker rmi step-by-step:0.1.0-SNAPSHOT
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `./mvnw: Permission denied` pendant le build | `mvnw` a perdu son droit d'exécution : `chmod +x mvnw`, puis reconstruis |
| `Bind for 127.0.0.1:8090 failed: port is already allocated` | L'application du tutoriel 002 tourne encore : Ctrl+C dans son terminal |
| `Conflict. The container name "/step-by-step-local" is already in use` | Étape 6, puis recommence l'étape 3 |
| L'étape 4 ne rend la main qu'au bout de 60 s, sans JSON | `docker ps -a --filter name=step-by-step-local` : rien → refais l'étape 3 ; `Exited` → `docker logs step-by-step-local` et lis la dernière erreur |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Image construite | `docker images step-by-step` | `0.1.0-SNAPSHOT`, ≈ 410 Mo |
| Conteneur joignable | `curl localhost:8090/actuator/health` | `"status":"UP"` |
| Pas de root | `docker exec … id` | `app` |
| Conteneur supprimé | `docker ps -a --filter name=step-by-step-local -q` | vide |

---

## 🎉 Bravo !

Step by Step est **emballée** et prête à voyager. 🎉 **Jalon J3 atteint.**

Prochain tutoriel : **#007** — Apprendre au vigile à ignorer `mvnw` ⏱️ 10 min
