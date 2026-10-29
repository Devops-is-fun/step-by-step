# Tutoriel 003 — Story #008 : Démarrer la base de test et l'interroger

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** `compose OK`
**Exigence TP couverte :** prépare `EXG-COM-10`
**Fichier principal :** le conteneur `stepbystep-test-postgres-1`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Allumer le banc d'essai, attendre qu'il soit **prêt**, et lui poser une question en SQL.

---

## 🪜 Les étapes

### Étape 1 : Démarre la base et attends qu'elle soit prête

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose -f src/test/resources/compose-test.yml up -d --wait
> ```

**Résultat attendu :** au premier lancement, le téléchargement de l'image (≈ 30 s), puis :

```
 ✔ Network stepbystep-test_default       Created
 ✔ Container stepbystep-test-postgres-1  Healthy
```

**Pourquoi `--wait` ?** Compose te rend la main seulement quand le healthcheck dit `healthy`. Pas de
`sleep` à deviner : c'est la commande que Jenkins utilisera en story 012.

---

### Étape 2 : Vérifie l'état

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose -f src/test/resources/compose-test.yml ps
> ```

**Résultat attendu :** une ligne `stepbystep-test-postgres-1`, `STATUS` = `Up … (healthy)`, `PORTS` =
`127.0.0.1:5433->5432/tcp`.

**✅ Vérifie que :**
- [ ] `(healthy)`
- [ ] `127.0.0.1:5433->5432/tcp`

---

### Étape 3 : Interroge PostgreSQL

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose -f src/test/resources/compose-test.yml exec -T postgres psql -U stepbystep -d stepbystep_test -c 'select version();'
> ```

**Résultat attendu :** une ligne qui commence par `PostgreSQL 16.15`.

**Pourquoi `exec` ?** Tu entres **dans** le conteneur pour y lancer `psql`, le client de PostgreSQL :
inutile de l'installer sur ton poste.

---

### Étape 4 : Constate que la base est vide

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose -f src/test/resources/compose-test.yml exec -T postgres psql -U stepbystep -d stepbystep_test -c '\dt'
> ```

**Résultat attendu :** `Did not find any relations.` (ou en français, `Aucune relation trouvée.`)

**Pourquoi vide ?** Personne n'a encore monté l'escalier Flyway. Ce sont les tests d'intégration qui
démarreront l'application, et donc Flyway, au tutoriel 005. Garde cette image en tête : tu reverras
cette commande remplie de tables.

**Laisse la base allumée** pour le tutoriel suivant.

---

## 🧹 Comment tout annuler

```bash
docker compose -f src/test/resources/compose-test.yml down
```

(Les données sont en mémoire : rien ne reste.)

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `Bind for 127.0.0.1:5433 failed: port is already allocated` | Un autre programme occupe 5433 : `ss -ltnp \| grep 5433` et montre-moi le résultat |
| `container stepbystep-test-postgres-1 is unhealthy` | `docker compose -f src/test/resources/compose-test.yml logs postgres` : lis les dernières lignes |
| `service "postgres" is not running` à l'étape 3 | Refais l'étape 1 |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Base prête | `docker compose -f src/test/resources/compose-test.yml ps` | `(healthy)` |
| PostgreSQL 16 | `psql … -c 'select version();'` | `PostgreSQL 16.15` |
| Schéma vide | `psql … -c '\dt'` | aucune table |

---

## 🎉 Bravo !

Ton banc d'essai est allumé et répond. 🎉 **Jalon J2 atteint.**

C'est une bonne coupure ☕ (≈ 50 min). Si tu t'arrêtes ici :
`docker compose -f src/test/resources/compose-test.yml down`, et tu relanceras l'étape 1 en reprenant.

Prochain tutoriel : **#004** — Brancher Failsafe dans le `pom.xml` ⏱️ 15 min
