# Tutoriel 005 — Story #010 : Créer le job Multibranch `step-by-step`

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 004
**Prérequis :** `Jenkinsfile` poussé sur `main` ; credential `github-token` ; agent `docker-agent` en ligne
**Exigence TP couverte :** `EXG-COM-06`
**Fichier principal :** `http://localhost:8080/job/step-by-step/`

> **Répertoire de travail :** aucun (navigateur), sauf l'étape 6.

---

## 🎯 Objectif

Créer **un** job qui fabrique automatiquement un sous-job par branche et par pull request.

## 📋 Ce que tu vas apprendre

- Ce qu'est un job **Multibranch** et son **scan**
- Lire un *scan log* et une console de build

---

## 🪜 Les étapes

### Étape 1 : Crée le job

**Où :** `http://localhost:8080` → **New Item**

**Action :**

> | Champ | Valeur |
> |-------|--------|
> | Enter an item name | `step-by-step` |
> | Type | **Multibranch Pipeline** |
>
> Clique **OK**.

**Pourquoi Multibranch ?** Un job *Pipeline* simple suit **une** branche. Un job *Multibranch*
**surveille le dépôt** : chaque branche avec un `Jenkinsfile`, et chaque PR, devient un sous-job,
créé et supprimé automatiquement.

---

### Étape 2 : Branche le dépôt GitHub

**Où :** la page de configuration qui s'ouvre, section **Branch Sources**

**Action :**

> 1. **Add source → GitHub**.
> 2. **Credentials** : choisis `<ton-compte>/****** (GitHub fine-grained token: step-by-step only (story 010))`.
> 3. **Repository HTTPS URL** : `https://github.com/<ton-compte>/step-by-step`
> 4. Clique **Validate**.

**Résultat attendu :** sous le bouton,
`Credentials ok. Connected to https://github.com/<ton-compte>/step-by-step.`

**Laisse les *Behaviours* par défaut** :

| Behaviour | Valeur par défaut | Sens |
|-----------|-------------------|------|
| Discover branches | *Exclude branches that are also filed as PRs* | une branche qui a une PR est construite **une** fois, en tant que PR |
| Discover pull requests from origin | *Merging the pull request with the current target branch revision* | Jenkins teste le résultat **après** merge |
| Discover pull requests from forks | (réglage par défaut) | sans effet : ton dépôt est privé |

---

### Étape 3 : La configuration du build

**Où :** même page, section **Build Configuration**

**Action :**

> Vérifie : **Mode** = `by Jenkinsfile`, **Script Path** = `Jenkinsfile`.

---

### Étape 4 : Le filet de sécurité

**Où :** même page, section **Scan Multibranch Pipeline Triggers**

**Action :**

> Coche **Periodically if not otherwise run**, **Interval** = `1 day`. Clique **Save**.

**Pourquoi, alors qu'on aura un webhook ?** Si ta machine est éteinte quand tu pousses, le webhook
est perdu. Le scan quotidien rattrape l'oubli. C'est le **plan B**, pas la solution.

---

### Étape 5 : Lis le scan

**Où :** la page qui s'ouvre après **Save** (*Scan Repository Log*)

**Action :**

> Attends la fin du log (10 à 30 secondes).

**Résultat attendu :** parmi les lignes :

```
Checking branch main
      ‘Jenkinsfile’ found
    Met criteria
Scheduled build for branch: main
…
Finished: SUCCESS
```

**✅ Vérifie que :**
- [ ] `‘Jenkinsfile’ found` pour `main`
- [ ] `Finished: SUCCESS`

---

### Étape 6 : Regarde le premier build

**Où :** `http://localhost:8080/job/step-by-step/job/main/` → build **#1** → **Console Output**

**Action :**

> Ouvre la console du build #1.

**Résultat attendu :** dans l'ordre :

```
Running on docker-agent in /home/jenkins/agent/workspace/step-by-step_main
…
[Pipeline] { (Declarative: Checkout SCM)
…
[Pipeline] { (Validate)
+ ./mvnw -B validate
…
[INFO] BUILD SUCCESS
…
Finished: SUCCESS
```

**Pourquoi `Running on docker-agent` est important ?** C'est la preuve que la règle d'or est
respectée : **jamais** de build sur le controller.

---

### Étape 7 : Le statut sur GitHub

**Où :** `https://github.com/<ton-compte>/step-by-step/commits/main`

**Action :**

> Regarde le dernier commit (`ci(pipeline): add minimal jenkinsfile…`).

**Résultat attendu :** une coche verte ✅ à côté. Au survol :
`continuous-integration/jenkins/branch — This commit looks good`.

**Pourquoi c'est là ?** C'est la permission *Commit statuses* du jeton : Jenkins écrit le résultat
**sur le commit**. Le lien *Details* pointe vers `localhost:8080` : il ne marche que sur ta machine,
c'est normal.

---

## 🧹 Comment tout annuler

Jenkins → `step-by-step` → **Delete Multibranch Pipeline**.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `Invalid credentials` à l'étape 2 | Jeton mal collé, ou *Username* faux : refais la credential (tutoriel 003, étape 3) |
| `Repository not found` / `404` | Le jeton n'a pas accès à ce dépôt (*Only select repositories*), ou faute dans l'URL |
| `Does not meet criteria` pour `main` | Le `Jenkinsfile` n'est pas sur `origin/main` : tutoriel 004, étape 3 |
| Le build attend `Waiting for next available executor on ‘docker-agent’` | L'agent est hors ligne : **Manage Jenkins → Nodes → docker-agent → Launch agent** |
| Pas de coche verte sur GitHub | Permission *Commit statuses* oubliée : édite le jeton sur GitHub, puis relance le build (**Build Now** sur `main`) |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Où | Attendu |
|--------------|----|---------|
| Type de job | Jenkins → `step-by-step` | icône et menu *Multibranch* (**Scan Repository Now**) |
| Branche détectée | Scan Repository Log | `main` : `‘Jenkinsfile’ found` |
| Build sur l'agent | `main` #1 | `Running on docker-agent`, `Finished: SUCCESS` |
| Statut GitHub | commits de `main` | ✅ |

---

## 🎉 Bravo !

Ton **premier build de pipeline** sur ton propre projet ! 🎉 **Jalon J3 atteint.**

Prochain tutoriel : **#006** — Brancher le webhook dans GitHub ⏱️ 15 min
