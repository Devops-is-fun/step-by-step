# Tutoriel 008 — Story #005 : Écrire `scripts/check-agents.sh`

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 005
**Prérequis :** aucun (ce tutoriel écrit un fichier ; il sera lancé au tutoriel 009)
**Exigence TP couverte :** `EXG-COM-04`
**Fichier principal :** `scripts/check-agents.sh` (à créer)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Écrire un script qui vérifie l'invariant de la story : le controller a 0 exécuteur, `docker-agent`
est en ligne avec 2 exécuteurs, et Docker répond depuis l'agent.

## 📋 Ce que tu vas apprendre

- Interroger l'état des nœuds par l'API Jenkins
- Pourquoi `(built-in)` a besoin de `curl -g`
- Réutiliser les briques de `check-jenkins.sh` et `check-nexus.sh`

---

## 🪜 Les étapes

### Étape 1 : Écris le script

**Où :** la racine du projet

**Action :**

> Crée `scripts/check-agents.sh` avec ce contenu **exact** (réponds **Cancel** à IntelliJ) :
>
> ```bash
> #!/usr/bin/env bash
> # Checks the Jenkins agents (story 005): builds run on docker-agent, never on the controller.
> # Prerequisites: JENKINS_USER and JENKINS_TOKEN exported (API token).
> # Run it from the project root (it uses docker compose for the last check).
> set -euo pipefail
>
> JENKINS_URL="${JENKINS_URL:-http://localhost:8080}"
> AGENT="${JENKINS_AGENT:-docker-agent}"
> : "${JENKINS_USER:?JENKINS_USER is not set (see docs/tooling/jenkins-configuration.md)}"
> : "${JENKINS_TOKEN:?JENKINS_TOKEN is not set (see docs/tooling/jenkins-configuration.md)}"
>
> # Authenticated call to the Jenkins API. -g: brackets and parentheses are not curl patterns.
> api() {
>   curl -fsSg -u "$JENKINS_USER:$JENKINS_TOKEN" "$JENKINS_URL$1"
> }
>
> errors=0
>
> # 1. The controller has no executor: no build can run on it
> controller_json="$(api "/computer/(built-in)/api/json?tree=numExecutors" || true)"
> if [ -z "$controller_json" ]; then
>   echo "KO  Jenkins API does not answer (check JENKINS_URL and the token)"
>   exit 1
> elif grep -q '"numExecutors":0[,}]' <<< "$controller_json"; then
>   echo "OK  controller has 0 executors"
> else
>   echo "KO  controller has executors: builds could run on it"
>   errors=$((errors + 1))
> fi
>
> # 2. The agent exists and is online
> agent_json="$(api "/computer/$AGENT/api/json?tree=offline,numExecutors" || true)"
> if grep -q '"offline":false' <<< "$agent_json"; then
>   echo "OK  agent $AGENT is online"
> else
>   echo "KO  agent $AGENT is missing or offline"
>   errors=$((errors + 1))
> fi
>
> # 3. The agent has 2 executors
> if grep -q '"numExecutors":2[,}]' <<< "$agent_json"; then
>   echo "OK  agent $AGENT has 2 executors"
> else
>   echo "KO  agent $AGENT does not have 2 executors"
>   errors=$((errors + 1))
> fi
>
> # 4. Docker answers from the agent (integration tests and deployments need it).
> #    < /dev/null: docker compose exec would otherwise read (and swallow) this script's stdin.
> if docker compose exec -T -u jenkins jenkins-agent docker version > /dev/null 2>&1 < /dev/null; then
>   echo "OK  docker reachable from the agent"
> else
>   echo "KO  docker not reachable from the agent"
>   errors=$((errors + 1))
> fi
>
> if [ "$errors" -eq 0 ]; then
>   echo "Agents are compliant"
> else
>   echo "$errors check(s) failed"
>   exit 1
> fi
> ```

**✅ Vérifie que :**
- [ ] Le script ne contient **aucun** identifiant (seulement les **noms** des variables)

---

### Étape 2 : Repère ce que tu connais déjà

**Où :** ton éditeur

| Passage | Déjà vu dans |
|---------|--------------|
| `: "${JENKINS_TOKEN:?…}"`, `api()` avec `curl -fsSg` | `check-jenkins.sh` |
| `errors=$((errors + 1))`, résumé final | `check-nexus.sh` |
| `docker compose exec -T … < /dev/null` | `check-nexus.sh` (le piège de l'entrée standard) |

**Et les nouveautés :**

| Passage | Ce qu'il fait |
|---------|---------------|
| `/computer/(built-in)/…` | l'API du controller. Sans `-g`, `curl` essaierait d'interpréter certains caractères de l'URL comme des motifs |
| `"numExecutors":0[,}]` | `0` suivi d'une virgule ou d'une accolade : `"numExecutors":0` seul accepterait aussi `"numExecutors":02`… et le contrôle 3 avec `2` accepterait `20` |
| `AGENT="${JENKINS_AGENT:-docker-agent}"` | nom du nœud modifiable pour un test, sans toucher au script (tutoriel 009) |
| `-u jenkins` au contrôle 4 | on teste Docker **en tant que l'utilisateur des builds**, pas en root |

**✅ Vérifie que :**
- [ ] Tu sais dire pourquoi le contrôle 1 arrête le script tout de suite quand l'API ne répond pas

---

### Étape 3 : Rends-le exécutable et vérifie la syntaxe

**Où :** la racine du projet

**Action :**

> ```bash
> chmod +x scripts/check-agents.sh
> bash -n scripts/check-agents.sh && echo "syntaxe OK"
> ```

**Résultat attendu :** `syntaxe OK`

---

## 🧹 Comment tout annuler

```bash
rm scripts/check-agents.sh
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `syntax error near unexpected token` | Un caractère perdu au copier-coller : recopie le bloc entier |
| IntelliJ souligne `<<<` | C'est du bash valide (*here-string*), déjà utilisé dans `check-jenkins.sh` |

Ce script a été vérifié à la génération : syntaxe, lecture du JSON (`0` vs `02`, `2` vs `20`) et arrêt
immédiat quand Jenkins refuse le jeton. Les contrôles 2 à 4 seront vus pour la première fois au
tutoriel 009, sur ton Jenkins.

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

- [ ] `scripts/check-agents.sh` écrit, exécutable, syntaxe OK
- [ ] Aucun identifiant dans le script

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Interroger l'état des nœuds par l'API Jenkins
- ✨ Écrire un motif `grep` qui ne confond pas `2` et `20`
- ✨ Réutiliser tes propres briques de script

Prochain tutoriel : **#009** — Lancer le script, puis le faire échouer exprès ⏱️ 15 min

Pause méritée ☕
