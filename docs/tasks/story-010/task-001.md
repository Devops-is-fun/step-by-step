# Tutoriel 001 — Story #010 : Ouvrir la boîte postale smee et démarrer le relais

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** story 009 terminée
**Prérequis :** stack `cicd` démarré (`docker compose ps` : Jenkins `healthy`) ; Bitwarden ouvert
**Exigence TP couverte :** `EXG-COM-06`
**Fichier principal :** le conteneur `cicd-webhook-relay`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Obtenir une adresse publique **fixe** pour les webhooks de GitHub (un canal smee.io), et démarrer le
**relais** qui les apporte à Jenkins.

## 📋 Ce que tu vas apprendre

- La différence entre un **tunnel** et un **relais**
- Lancer un conteneur qui **redémarre tout seul** (`--restart unless-stopped`)
- Brancher un conteneur sur le réseau d'un stack Compose existant

---

## 🪜 Les étapes

### Étape 1 : Ouvre un canal smee

**Où :** `https://smee.io`

**Action :**

> Clique **Start a new channel**.

**Résultat attendu :** une page dont l'adresse ressemble à `https://smee.io/AbCd1234EfGh5678` (16
caractères au hasard), avec le titre *Webhook Proxy URL*.

**Pourquoi c'est une « boîte postale » ?** GitHub enverra ses webhooks à **cette** adresse, qui est
publique. smee.io les garde et les **pousse** à tous les clients connectés au canal. Ton relais en
sera un.

---

### Étape 2 : Range l'URL dans Bitwarden

**Où :** Bitwarden

**Action :**

> Crée une entrée **Note** (ou **Login**) :
>
> | Champ | Valeur |
> |-------|--------|
> | Nom | `smee.io — webhook GitHub step-by-step` |
> | Contenu / URL | l'adresse **complète** du canal (copie-la depuis la barre d'adresse) |
>
> **Enregistre.** Ne la colle **nulle part ailleurs** (ni fichier, ni note du projet).

**Pourquoi la cacher ?** Quiconque connaît l'URL peut **lire** les notifications de ton dépôt
privé (messages de commit, noms de fichiers) en ouvrant la page smee. Elle ne permet pas de pousser
du code, mais elle se traite comme une donnée sensible.

---

### Étape 3 : Charge l'URL dans le terminal

**Où :** la racine du projet

**Action :**

> ```bash
> read -rs SMEE_URL && export SMEE_URL
> ```
>
> Colle l'URL depuis Bitwarden, puis **Entrée**. Rien ne s'affiche : c'est voulu.

---

### Étape 4 : Trouve le réseau du stack `cicd`

**Où :** la racine du projet

**Action :**

> ```bash
> docker network ls --format '{{.Name}}' | grep cicd
> ```

**Résultat attendu :** `cicd_cicd`

**Pourquoi ce nom ?** Compose préfixe le nom du réseau (`cicd`, déclaré dans `docker-compose.yml`)
par le nom du projet (`cicd`). Sur ce réseau, Jenkins s'appelle simplement `jenkins`.

---

### Étape 5 : Démarre le relais

**Où :** la racine du projet, le même terminal

**Action :**

> ```bash
> docker run -d --name cicd-webhook-relay --restart unless-stopped --network cicd_cicd \
>   node:22.23.4-alpine3.24 \
>   npx --yes smee-client@5.0.0 --url "$SMEE_URL" --target 'http://jenkins:8080/github-webhook/?source=smee'
> ```

**Résultat attendu :** un long identifiant de conteneur (au premier lancement, d'abord le
téléchargement de l'image `node`, ≈ 20 s).

| Option | Sens |
|--------|------|
| `--restart unless-stopped` | le relais redémarre tout seul (après un redémarrage du PC, par exemple), sauf si **tu** l'as arrêté |
| `--network cicd_cicd` | le relais rejoint le réseau de Jenkins, et peut l'appeler `jenkins` |
| `node:22.23.4-alpine3.24` | une image officielle, épinglée, qui contient `npx` |
| `smee-client@5.0.0` | le client officiel smee, version épinglée |
| `--target '…/github-webhook/?source=smee'` | l'adresse où Jenkins reçoit les webhooks GitHub |

**Pourquoi `?source=smee` à la fin ?** smee retire le `/` final de l'adresse cible, et Jenkins
répond alors `405`. Avec un paramètre derrière, smee garde le `/`. Piège trouvé en préparant cette
story.

**Pourquoi pas dans `docker-compose.yml` ?** L'URL devrait alors y être écrite, ou exportée avant
**chaque** `docker compose up`. Ici, tu la donnes **une fois**, à la création du conteneur.

---

### Étape 6 : Vérifie que le coursier a pris son poste

**Où :** la racine du projet

**Action :**

> ```bash
> sleep 20; docker logs cicd-webhook-relay 2>&1 | tail -2
> ```

**Résultat attendu :**

```
Connected to https://smee.io/…
Forwarding https://smee.io/… to http://jenkins:8080/github-webhook/?source=smee
```

(Le `sleep 20` laisse le temps à `npx` de télécharger `smee-client` au premier démarrage.)

**✅ Vérifie que :**
- [ ] `Connected`
- [ ] `Forwarding … to http://jenkins:8080/github-webhook/?source=smee`

---

### Étape 7 : Un premier courrier, à la main

**Où :** la racine du projet

**Action :**

> ```bash
> curl -fsS -o /dev/null -w '%{http_code}\n' -X POST "$SMEE_URL" -H 'Content-Type: application/json' -H 'X-GitHub-Event: ping' --data-binary '{"zen":"hello","hook_id":0}'
> sleep 3; docker logs cicd-webhook-relay 2>&1 | tail -1
> ```

**Résultat attendu :** `200`, puis `POST http://jenkins:8080/github-webhook/?source=smee - 200`.

**Pourquoi c'est une victoire ?** Ton message a fait le trajet complet : Internet → smee.io → relais
→ Jenkins, **sans** qu'aucun port de ta machine soit ouvert à Internet. 🎉

---

## 🧹 Comment tout annuler

```bash
docker rm -f cicd-webhook-relay
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `network cicd_cicd not found` | Le stack n'est pas démarré : `docker compose up -d`, puis étape 5 |
| `Conflict. The container name "/cicd-webhook-relay" is already in use` | `docker rm -f cicd-webhook-relay`, puis étape 5 |
| L'étape 6 n'affiche rien | Attends encore 20 s ; puis `docker logs cicd-webhook-relay` en entier |
| `- 405` à l'étape 7 | Il manque `?source=smee` à la fin de `--target` : supprime le conteneur et refais l'étape 5 |
| `- 404` à l'étape 7 | Faute de frappe dans `github-webhook` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Relais démarré | `docker ps --filter name=cicd-webhook-relay --format '{{.Status}}'` | `Up …` |
| Connecté à smee | `docker logs cicd-webhook-relay` | `Connected`, `Forwarding` |
| Trajet complet | étape 7 | `- 200` |

### 🛡️ Garde-fous du TP

- [ ] L'URL smee est **seulement** dans Bitwarden (et dans la config du conteneur, sur ta machine)
- [ ] `git status` : aucun nouveau fichier

---

## Journal des décisions

Réponses de cadrage `tasks-fr 010` (31/10/2026) : **1a, 2a, 3a, 4a**.

| # | Question | Réponse | Conséquence |
|---|----------|---------|-------------|
| 1 | Joindre le Jenkins local | **a** — relais smee.io | Jenkins n'est pas exposé à Internet ; la spec abandonne le tunnel |
| 2 | Authentification GitHub | **a** — jeton *fine-grained* limité au dépôt | statut de commit sur la PR (liste des vérifications) |
| 3 | Jenkinsfile | **a** — minimal, `./mvnw -B validate` sur `docker-agent` | la story 011 ajoute les stages |
| 4 | Preuve PR | **a** — branche + PR de démonstration, puis merge | tutoriel 009 |

Décisions prises à la génération (vérifiées sur ta machine) :

| Sujet | Décision | Raison |
|-------|----------|--------|
| Relais | conteneur `cicd-webhook-relay` créé par `docker run`, pas dans `docker-compose.yml` | l'URL smee ne doit pas être écrite dans un fichier du projet |
| Cible | `http://jenkins:8080/github-webhook/?source=smee` | sans paramètre, smee retire le `/` final → Jenkins `405` |
| Signature | conservée par smee (testé : accents, guillemets, emoji) | seuls les nombres décimaux seraient réécrits ; GitHub n'en envoie pas |
| Credential `github-token` | *Username with password* (pas *Secret text*) | le plugin GitHub Branch Source n'accepte que ce type pour scanner un dépôt |
| Agent | `./mvnw` fonctionne sur `docker-agent` sans `curl` | le wrapper télécharge Maven avec Java (testé) |

---

## 🎉 Bravo !

Ta boîte postale est ouverte et le coursier est à son poste. 🎉 **Jalon J1 atteint.**

Prochain tutoriel : **#002** — Créer le secret du webhook et le confier à Jenkins ⏱️ 15 min
