# Tutoriel 009 — Story #005 : Lancer le script, puis le faire échouer exprès

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 008
**Prérequis :** tutoriels 001 à 007 terminés ; jeton `scripts-tp` dans Bitwarden
**Exigence TP couverte :** `EXG-COM-04`
**Fichier principal :** `scripts/check-agents.sh`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Voir le script au vert, puis le faire échouer deux fois : un nœud qui n'existe pas, puis un agent
**vraiment** arrêté (et le voir revenir seul).

---

## 🪜 Les étapes

### Étape 1 : Charge le jeton

**Où :** la racine du projet

**Action :**

> Si `JENKINS_USER` et `JENKINS_TOKEN` ne sont pas déjà chargés dans ce terminal :
>
> ```bash
> export JENKINS_USER=admin
> read -rs JENKINS_TOKEN && export JENKINS_TOKEN
> ```
>
> Colle le jeton `scripts-tp` (Bitwarden), puis **Entrée**. Rien ne s'affiche : c'est normal.

**✅ Vérifie que :**
- [ ] `echo "${#JENKINS_TOKEN}"` affiche `34`

---

### Étape 2 : Lance le script

**Où :** la racine du projet

**Action :**

> ```bash
> ./scripts/check-agents.sh; echo "exit code: $?"
> ```

**Résultat attendu :**

```
OK  controller has 0 executors
OK  agent docker-agent is online
OK  agent docker-agent has 2 executors
OK  docker reachable from the agent
Agents are compliant
exit code: 0
```

**✅ Vérifie que :**
- [ ] 4 lignes `OK`, code `0`

---

### Étape 3 : Échec 1, un nœud qui n'existe pas

**Où :** la racine du projet

**Action :**

> ```bash
> JENKINS_AGENT=docker-agentz ./scripts/check-agents.sh; echo "exit code: $?"
> ```

**Résultat attendu :** une ligne `curl: (22) … 404` (Jenkins ne connaît pas ce nœud), puis :

```
OK  controller has 0 executors
KO  agent docker-agentz is missing or offline
KO  agent docker-agentz does not have 2 executors
OK  docker reachable from the agent
2 check(s) failed
exit code: 1
```

**Pourquoi le contrôle 4 reste OK ?** Il interroge le **conteneur** `jenkins-agent`, pas le nœud
Jenkins : le conteneur, lui, tourne toujours.

---

### Étape 4 : Échec 2, l'agent arrêté pour de vrai

**Où :** la racine du projet

**Action :**

> **Prédis d'abord** : quels contrôles vont échouer si on arrête le conteneur de l'agent ? Puis :
>
> ```bash
> docker compose stop jenkins-agent
> ./scripts/check-agents.sh; echo "exit code: $?"
> ```

**Résultat attendu :** les contrôles 2 (`offline`) et 4 (`docker not reachable`) en `KO`, le code `1`.
Le contrôle 3 devrait rester `OK` : le nœud est **configuré** avec 2 exécuteurs, même hors ligne.
Ce cas n'a pas pu être testé à l'avance sur ton Jenkins : si le résultat diffère, c'est une
découverte, note-la dans `retours.md`.

---

### Étape 5 : Redémarre l'agent et regarde-le revenir

**Où :** la racine du projet, puis `http://localhost:8080`

**Action :**

> ```bash
> docker compose start jenkins-agent
> ```
>
> Attends 1 à 2 minutes, puis relance le script. Si l'agent est toujours *offline* après 3 minutes :
> **Manage Jenkins** → **Nodes** → **docker-agent** → **Launch agent**.

**Résultat attendu :** de nouveau `Agents are compliant`, `exit code: 0`.

**Pourquoi il revient seul ?** *Keep this agent online as much as possible* (tutoriel 005) : Jenkins
réessaie régulièrement de se connecter. Et `restart: unless-stopped` relancera le conteneur après un
redémarrage de ta machine.

**✅ Vérifie que :**
- [ ] Le script est de nouveau au vert

---

## 🧹 Comment tout annuler

Rien à annuler, à condition que l'étape 5 soit faite : l'agent doit être **redémarré**.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `KO  Jenkins API does not answer` à l'étape 2 | Jeton absent ou faux : étape 1 |
| `KO  controller has executors` | Tutoriel 006 |
| `KO  docker not reachable from the agent` à l'étape 2 | `docker compose ps jenkins-agent` ; tutoriel 004 |
| `no configuration file provided` | Lance le script depuis la racine du projet |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Agents conformes | `./scripts/check-agents.sh` | `Agents are compliant`, code `0` |
| Nœud absent détecté | `JENKINS_AGENT=docker-agentz ./scripts/check-agents.sh` | `2 check(s) failed` |
| Agent arrêté détecté | `docker compose stop jenkins-agent` puis le script | code `1` |
| Agent revenu | `docker compose start jenkins-agent` puis le script | code `0` |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Distinguer le **nœud** Jenkins du **conteneur** qui le fait tourner
- ✨ Provoquer une vraie panne et la voir se réparer

Prochain tutoriel : **#010** — Documenter (en anglais) et faire le bilan ⏱️ 20 min

Pause méritée ☕
