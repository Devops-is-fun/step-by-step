# Tutoriel 007 — Story #010 : Écrire et lancer `scripts/check-webhook.sh`

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 006
**Prérequis :** webhook GitHub actif ; URL smee et secret dans Bitwarden
**Exigence TP couverte :** `EXG-COM-06`
**Fichier principal :** `scripts/check-webhook.sh`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Vérifier **en une commande** que tout le trajet GitHub → smee → relais → Jenkins fonctionne, comme
tes scripts `check-jenkins.sh`, `check-sonarqube.sh`, `check-nexus.sh` et `check-agents.sh`.

## 📋 Ce que tu vas apprendre

- Signer un message HMAC avec `openssl`
- Lire la réponse dans les logs d'un conteneur, **depuis** un instant précis (`--since`)

---

## 🪜 Les étapes

### Étape 1 : Crée le script

**Où :** `scripts/`, dans IntelliJ

**Action :**

> Crée `scripts/check-webhook.sh` avec ce contenu **exact** :
>
> ```bash
> #!/usr/bin/env bash
> # Checks the GitHub -> smee.io -> Jenkins webhook path (story 010), end to end.
> # Needs (loaded with read -rs, never written in a file):
> #   SMEE_URL        the smee.io channel of the GitHub webhook
> #   WEBHOOK_SECRET  the shared secret of the GitHub webhook (Jenkins credential github-webhook-secret)
> set -euo pipefail
> 
> : "${SMEE_URL:?SMEE_URL is not set (see docs/tooling/webhook-relay.md)}"
> : "${WEBHOOK_SECRET:?WEBHOOK_SECRET is not set (see docs/tooling/webhook-relay.md)}"
> RELAY=cicd-webhook-relay
> 
> # 1. The relay container runs
> if [ "$(docker inspect -f '{{.State.Running}}' "$RELAY" 2>/dev/null)" = "true" ]; then
>   echo "OK  relay container $RELAY is running"
> else
>   echo "KO  relay container $RELAY is not running: docker start $RELAY"
>   exit 1
> fi
> 
> # 2. A signed ping sent to smee.io reaches Jenkins within 5 seconds
> body="{\"zen\":\"check-webhook $(date +%s)\",\"hook_id\":0}"
> signature="sha256=$(printf '%s' "$body" | openssl dgst -sha256 -hmac "$WEBHOOK_SECRET" | awk '{print $NF}')"
> since=$(date +%s)
> curl -fsS -o /dev/null -X POST "$SMEE_URL" \
>   -H 'Content-Type: application/json' -H 'X-GitHub-Event: ping' \
>   -H "X-Hub-Signature-256: $signature" --data-binary "$body"
> 
> for _ in 1 2 3 4 5; do
>   sleep 1
>   result=$(docker logs --since "$since" "$RELAY" 2>&1 | grep -oE 'github-webhook/[^ ]* - [0-9]{3}' | tail -1 || true)
>   if [ -n "$result" ]; then
>     break
>   fi
> done
> 
> case "$result" in
>   *" - 200") echo "OK  signed ping forwarded to Jenkins (HTTP 200)" ;;
>   *" - 400") echo "KO  Jenkins refused the ping (HTTP 400): WEBHOOK_SECRET differs from the Jenkins shared secret"; exit 1 ;;
>   "")        echo "KO  no answer within 5 seconds: is smee.io reachable? docker logs $RELAY"; exit 1 ;;
>   *)         echo "KO  unexpected answer: $result"; exit 1 ;;
> esac
> echo "Webhook path is working"
> ```
>
> **Enregistre.** Si IntelliJ demande **« Add file to Git? »** : **Cancel**.

**Ce qu'il fait :**

| Partie | Rôle |
|--------|------|
| `: "${SMEE_URL:?…}"` | s'arrête tout de suite si une variable manque, avec un message clair |
| 1. | le conteneur relais tourne-t-il ? |
| 2. `openssl dgst -sha256 -hmac` | fabrique un *ping* **cacheté exactement comme GitHub** |
| `curl … "$SMEE_URL"` | le dépose dans la boîte smee |
| `docker logs --since` | lit la réponse de Jenkins **à ce ping-là** (pas une ancienne ligne) |
| `case` | `200` = OK ; `400` = mauvais secret ; rien en 5 s = smee ou relais en panne |

**Pourquoi ne pas simplement poster sur l'URL smee et lire le code HTTP ?** smee.io répond `200`
dès qu'il a **reçu** la lettre, même si le relais est mort. Seule la réponse de **Jenkins** prouve
le trajet complet.

---

### Étape 2 : Rends-le exécutable

**Où :** la racine du projet

**Action :**

> ```bash
> chmod +x scripts/check-webhook.sh
> ```

---

### Étape 3 : Charge les deux valeurs

**Où :** la racine du projet

**Action :**

> ```bash
> read -rs SMEE_URL && export SMEE_URL
> read -rs WEBHOOK_SECRET && export WEBHOOK_SECRET
> ```
>
> Colle l'URL smee, **Entrée** ; puis le secret `github-webhook-secret`, **Entrée**
> (tous deux depuis Bitwarden).

---

### Étape 4 : Lance le script

**Où :** la racine du projet

**Action :**

> ```bash
> ./scripts/check-webhook.sh; echo "exit code: $?"
> ```

**Résultat attendu :**

```
OK  relay container cicd-webhook-relay is running
OK  signed ping forwarded to Jenkins (HTTP 200)
Webhook path is working
exit code: 0
```

**✅ Vérifie que :**
- [ ] `exit code: 0`

---

### Étape 5 : Prouve qu'il sait dire non

**Où :** la racine du projet

**Action :**

> ```bash
> WEBHOOK_SECRET=wrong ./scripts/check-webhook.sh; echo "exit code: $?"
> docker stop cicd-webhook-relay
> ./scripts/check-webhook.sh; echo "exit code: $?"
> docker start cicd-webhook-relay
> ```

**Résultat attendu :**

1. `KO  Jenkins refused the ping (HTTP 400): WEBHOOK_SECRET differs…`, `exit code: 1` ;
2. `cicd-webhook-relay` (arrêt) ;
3. `KO  relay container cicd-webhook-relay is not running…`, `exit code: 1` ;
4. `cicd-webhook-relay` (redémarrage).

**Pourquoi `WEBHOOK_SECRET=wrong` devant la commande ?** La variable ne vaut `wrong` **que** pour
cette commande : ton vrai secret reste chargé pour la suite.

---

### Étape 6 : Oublie les valeurs

**Où :** la racine du projet

**Action :**

> ```bash
> sleep 20; ./scripts/check-webhook.sh && unset SMEE_URL WEBHOOK_SECRET
> ```

**Résultat attendu :** `Webhook path is working` (le relais redémarré s'est reconnecté), puis les
deux variables sont effacées.

---

## 🧹 Comment tout annuler

```bash
rm scripts/check-webhook.sh
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `KO … (HTTP 400)` avec le **vrai** secret | Le secret de Jenkins diffère : refais la credential (tutoriel 002) et recolle le même dans GitHub (tutoriel 006) |
| `KO  no answer within 5 seconds` | `docker logs cicd-webhook-relay` : le relais est-il `Connected` ? L'URL chargée est-elle celle du relais ? |
| `curl: (22) … 404` | L'URL smee chargée est fausse ou incomplète |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Trajet complet | `./scripts/check-webhook.sh` | `exit code: 0` |
| Mauvais secret détecté | `WEBHOOK_SECRET=wrong …` | `exit code: 1` |
| Relais arrêté détecté | après `docker stop` | `exit code: 1` |
| Relais redémarré | `docker ps --filter name=cicd-webhook-relay` | `Up` |

---

## 🎉 Bravo !

Ton 5e script de vérification, et sans doute le plus malin. 🎉 **Jalon J4 atteint.**

Prochain tutoriel : **#008** — Écrire `docs/tooling/webhook-relay.md` ⏱️ 15 min
