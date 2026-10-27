# Tutoriel 002 — Story #007 : Lancer l'application et interroger sa santé

**Qui :** 🤖 Claude a implémenté — tu vérifies
**Temps estimé :** ⏱️ 10 minutes
**Difficulté :** ⭐
**Dépend de :** Tutoriel 001
**Prérequis :** `target/step-by-step-0.1.0-SNAPSHOT.jar` existe
**Exigence TP couverte :** support applicatif
**Fichier principal :** `http://localhost:8090/actuator/health`

> **Répertoire de travail :** la racine du projet. Il te faut **deux terminaux**.

---

## 🎯 Objectif

Démarrer le JAR et constater que l'application répond « je vais bien ».

## 📋 Ce que tu vas apprendre

- Démarrer un JAR Spring Boot sur un autre port
- Ce qu'est **Actuator** et son endpoint `health`

---

## 🪜 Les étapes

### Étape 1 : Démarre l'application

**Où :** terminal 1, la racine du projet

**Action :**

> ```bash
> java -jar target/step-by-step-0.1.0-SNAPSHOT.jar --server.port=8090
> ```

**Résultat attendu :** en 2-3 secondes, la bannière Spring, puis une ligne
`Started StepByStepApplication in … seconds`. Le terminal reste **occupé** : c'est normal.

**Pourquoi `--server.port=8090` ?** Spring Boot écoute sur **8080** par défaut, déjà pris par
Jenkins. Sans ce réglage : `Port 8080 was already in use`.

---

### Étape 2 : Interroge la santé

**Où :** terminal 2, n'importe où

**Action :**

> ```bash
> curl -s localhost:8090/actuator/health; echo
> ```

**Résultat attendu :**

```json
{"groups":["liveness","readiness"],"status":"UP"}
```

**Pourquoi c'est important ?** Actuator est le **tableau de bord** de l'application. Jenkins, le
déploiement idempotent (story 015) et le blue/green (story 017) interrogeront **cette** URL pour savoir
si une version est prête. Les `groups` sont les deux questions qu'un orchestrateur pose : « es-tu
vivante ? » et « es-tu prête à recevoir du trafic ? ».

**✅ Vérifie que :**
- [ ] `"status":"UP"`

---

### Étape 3 : Vérifie que rien d'autre n'est exposé

**Où :** terminal 2

**Action :**

> ```bash
> curl -s -o /dev/null -w "%{http_code}\n" localhost:8090/actuator/env
> ```

**Résultat attendu :** `404`

**Pourquoi ?** `/actuator/env` afficherait les variables d'environnement (donc, un jour, des
secrets). `application.yml` n'expose **que** `health`.

---

### Étape 4 : Arrête l'application

**Où :** terminal 1

**Action :**

> Appuie sur **Ctrl+C**.

**Résultat attendu :** le terminal te rend la main.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `Port 8090 was already in use` | Une ancienne instance tourne : `pgrep -af step-by-step` puis `kill <PID>` |
| `curl: (7) Failed to connect` | L'application n'a pas fini de démarrer : attends la ligne `Started…` |
| `Unable to access jarfile` | Tutoriel 001, étape 3 (`./mvnw -B verify`) |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Santé | `curl -s localhost:8090/actuator/health` | `"status":"UP"` |
| Pas d'`env` | `curl … /actuator/env` | `404` |
| Application arrêtée | terminal 1 | invite de commande revenue |

---

## 🎉 Bravo !

Premier `UP` de Step by Step ! 🎉 **Jalon J1 atteint.**

Prochain tutoriel : **#003** — Ajouter le bloc Sonar au `pom.xml` ⏱️ 15 min
