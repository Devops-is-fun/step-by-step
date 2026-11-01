# Tutoriel 005 — Story #032 : Cloner le dépôt et activer le vigile

**Pour :** 👋 Morgan, qui arrive sur le projet
**Temps estimé :** ⏱️ 10 minutes
**Difficulté :** ⭐
**Dépend de :** Tutoriel 004 (fait par Géraldine)
**Prérequis :** collaborateur du dépôt `GeraldineRouy/step-by-step` ; une clé SSH ajoutée à ton compte
GitHub ; Git installé
**Exigence TP couverte :** `EXG-REND-01`
**Fichier principal :** ta copie locale du dépôt

> **Répertoire de travail :** le dossier où tu ranges tes projets, puis la racine du dépôt cloné.

---

## 🎯 Objectif

Récupérer le projet et activer le **vigile** anti-secrets avant ton premier commit.

## 📋 Ce que tu vas apprendre

- Pourquoi un hook Git n'est **pas** actif automatiquement après un clone
- Régler son identité Git pour **un seul** dépôt

---

## 🪜 Les étapes

### Étape 1 : Clone le dépôt

**Où :** le dossier de tes projets

**Action :**

> ```bash
> git clone git@github.com:GeraldineRouy/step-by-step.git
> cd step-by-step
> ```

**Résultat attendu :** `Cloning into 'step-by-step'…`, puis tu es dans le dossier qui contient
`README.md`, `CONTRIBUTING.md` et `docs/`.

**Si `Permission denied (publickey)`** : ta clé SSH n'est pas sur ton compte GitHub. Ajoute-la
(**Settings → SSH and GPG keys**), ou clone en HTTPS :
`git clone https://github.com/GeraldineRouy/step-by-step.git` (GitHub demandera un jeton à la place
du mot de passe).

---

### Étape 2 : Active le vigile

**Où :** la racine du dépôt

**Action :**

> ```bash
> git config core.hooksPath .githooks
> git config core.hooksPath
> ```

**Résultat attendu :** `.githooks`

**Pourquoi c'est à faire à la main ?** Pour des raisons de sécurité, Git n'exécute **jamais** un
script reçu par un clone sans ton accord. Cette commande dit : « avant chaque commit, lance le hook
du dossier `.githooks` », c'est-à-dire `scripts/check-no-secrets.sh`, qui cherche des jetons, des
clés privées et des mots de passe dans ce que tu t'apprêtes à commiter.

---

### Étape 3 : Règle ton identité pour ce dépôt

**Où :** la racine du dépôt

**Action :**

> Remplace par **tes** valeurs (celles de ton compte GitHub d'école) :
>
> ```bash
> git config user.name "Prénom Nom"
> git config user.email "ton-email@ecole.example"
> ```

**Pourquoi sans `--global` ?** Le réglage ne vaut que pour ce dépôt : tes autres projets gardent
leur identité.

---

### Étape 4 : Vérifie que le vigile répond

**Où :** la racine du dépôt

**Action :**

> ```bash
> ./scripts/check-no-secrets.sh; echo "exit code: $?"
> ```

**Résultat attendu :** `OK  no secret found in the files to commit`, puis `exit code: 0`.

---

### Étape 5 : Lis les deux pages d'accueil

**Où :** ton éditeur ou GitHub

**Action :**

> Lis `CONTRIBUTING.md` (les règles), puis `docs/team/onboarding.md` (la visite guidée).

**✅ Vérifie que tu sais répondre à :**
- [ ] Puis-je pousser directement sur `main` ? (non : branche + PR)
- [ ] Que faire si ma PR n'a pas de statut Jenkins ? (demander à Géraldine d'allumer sa stack)
- [ ] Puis-je coller un jeton dans ChatGPT pour qu'il m'aide ? (jamais)
- [ ] Quelles sont mes stories ? (015, 017, 023, 024, 025)

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `Repository not found` | L'invitation de collaborateur n'est pas acceptée : regarde tes e-mails ou `https://github.com/notifications` |
| `./scripts/check-no-secrets.sh: Permission denied` | `git config core.fileMode` doit être `true` ; sinon `chmod +x scripts/*.sh` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Dépôt cloné | `git remote -v` | `origin … GeraldineRouy/step-by-step.git` |
| Vigile actif | `git config core.hooksPath` | `.githooks` |
| Identité locale | `git config user.email` | ton e-mail d'école |

---

## 🎉 Bravo !

Tu as le projet et le vigile veille. 🎉

Prochain tutoriel : **#006** — Lancer l'application sur ta machine ⏱️ 20 min
