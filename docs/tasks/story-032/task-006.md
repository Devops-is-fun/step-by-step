# Tutoriel 006 — Story #032 : Lancer l'application sur sa machine

**Pour :** 👋 Morgan
**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 005
**Prérequis :** Docker 29.x avec Docker Compose v2 ; un JDK 17 ou plus récent (`java -version`) ;
Maven **non** nécessaire
**Exigence TP couverte :** `EXG-REND-02` (part)
**Fichier principal :** `README.md`, section « Lancer l'application en local »

> **Répertoire de travail :** la racine du dépôt.

---

## 🎯 Objectif

Faire tourner Step by Step chez toi, **en suivant le README seulement**, et noter tout ce qui
coince : c'est un premier test du README (story 025, la tienne).

## 📋 Ce que tu vas apprendre

- La base PostgreSQL de test, jetable
- Les deux familles de tests : unitaires (sans base) et d'intégration (avec base)
- Construire et lancer l'image Docker de l'application

---

## 🪜 Les étapes

### Étape 1 : Ouvre un carnet d'écarts

**Où :** un fichier **hors** du dépôt (par exemple `~/notes-onboarding.txt`)

**Action :**

> Note-y, au fil des étapes, **tout** ce qui t'a fait hésiter : commande qui échoue, phrase pas
> claire, prérequis manquant.

**Pourquoi hors du dépôt ?** Ce sont des brouillons. Tu reporteras les vrais écarts dans ta première
PR (tutoriel 007) ou dans la story 025.

---

### Étape 2 : Les tests unitaires, sans Docker

**Où :** la racine du dépôt

**Action :**

> ```bash
> ./mvnw -B test | grep -E 'Tests run:|BUILD'
> ```

**Résultat attendu :** au premier lancement, 1 à 2 minutes de téléchargements, puis
`Tests run: 21, Failures: 0, Errors: 0, Skipped: 0` et `BUILD SUCCESS`.

---

### Étape 3 : Suis le README

**Où :** `README.md`, section **« Lancer l'application en local »**

**Action :**

> Exécute les commandes du README **dans l'ordre**, telles qu'elles sont écrites :
>
> 1. démarrer la base de test (`docker compose -f src/test/resources/compose-test.yml up -d --wait`) ;
> 2. `./mvnw -B verify` ;
> 3. lancer le JAR avec les variables `SPRING_DATASOURCE_…`, puis le `curl` de santé ;
> 4. la variante **conteneur** (`docker build`, `docker run`).

**Résultat attendu :**

| Commande | Attendu |
|----------|---------|
| `up -d --wait` | `Container stepbystep-test-postgres-1  Healthy` |
| `./mvnw -B verify` | 21 tests unitaires + 7 tests d'intégration, `BUILD SUCCESS` |
| `curl localhost:8090/actuator/health` | `"status":"UP"` (JAR, puis conteneur) |

**Pourquoi le port 8090 ?** Sur la machine de Géraldine, 8080 et 8081 sont pris par Jenkins et
Nexus. Garder les mêmes ports chez vous deux évite les surprises.

**✅ Vérifie que :**
- [ ] `BUILD SUCCESS` avec les tests d'intégration
- [ ] `"status":"UP"` pour le JAR **et** pour le conteneur

---

### Étape 4 : Range tout

**Où :** la racine du dépôt

**Action :**

> Arrête le JAR (Ctrl+C), puis :
>
> ```bash
> docker rm -f step-by-step-local
> docker compose -f src/test/resources/compose-test.yml down
> ```

**Résultat attendu :** `step-by-step-local`, puis `Container stepbystep-test-postgres-1  Removed`.

**Pourquoi `down` sans `-v` ?** La base de test garde ses données en mémoire (`tmpfs`) : il n'y a
aucun volume. Et c'est une règle du projet : jamais de `-v` par habitude, il effacerait les données
de l'outillage.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `Bind for 127.0.0.1:5433 failed` | Un PostgreSQL occupe 5433 chez toi : note-le dans ton carnet, et préviens Géraldine |
| `Port 8090 was already in use` | Un autre programme utilise 8090 : `ss -ltnp \| grep 8090` |
| Tests d'intégration en erreur `Connection refused` | La base n'est pas démarrée : étape 3.1 |
| `permission denied … docker.sock` | Ton utilisateur n'est pas dans le groupe `docker` : `sudo usermod -aG docker $USER`, puis reconnecte-toi |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Comment | Attendu |
|--------------|---------|---------|
| Tests unitaires | `./mvnw -B test` | 21 verts |
| Chaîne complète | `./mvnw -B verify` (base allumée) | 21 + 7 verts |
| Application | `curl` | `UP` (JAR et conteneur) |
| Rangé | `docker ps -a --filter name=step-by-step` | vide |
| Carnet d'écarts | `~/notes-onboarding.txt` | rempli (même « aucun écart ») |

---

## 🎉 Bravo !

L'application tourne chez toi : tu as tout ce qu'il faut pour les stories 015 et 017. 🎉

Prochain tutoriel : **#007** — Ta première PR, et le bilan de la story ⏱️ 20 min
