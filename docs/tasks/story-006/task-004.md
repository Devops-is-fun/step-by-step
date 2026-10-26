# Tutoriel 004 — Story #006 : Brancher le vigile (hook) et le tester

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 003
**Prérequis :** `scripts/check-no-secrets.sh` fonctionne
**Exigence TP couverte :** `EXG-COM-19` (part)
**Fichier principal :** `.githooks/pre-commit` (à créer)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Faire lancer le vigile **automatiquement** par Git avant chaque commit, puis prouver qu'il bloque un
faux secret.

## 📋 Ce que tu vas apprendre

- Ce qu'est un hook Git `pre-commit`
- Pourquoi `.githooks/` + `core.hooksPath` plutôt que `.git/hooks/`
- Tester un garde-fou en le **piégeant**

---

## 🪜 Les étapes

### Étape 1 : Écris le hook

**Où :** la racine du projet

**Action :**

> ```bash
> mkdir .githooks
> ```
>
> Crée `.githooks/pre-commit` avec ce contenu **exact** (réponds **Cancel** à IntelliJ) :
>
> ```bash
> #!/usr/bin/env bash
> # Git pre-commit hook: blocks the commit when a secret is found (see scripts/check-no-secrets.sh).
> exec "$(git rev-parse --show-toplevel)/scripts/check-no-secrets.sh"
> ```

**Pourquoi si court ?** Le hook **délègue** au script : une seule version de la règle, que tu peux
aussi lancer à la main. `git rev-parse --show-toplevel` donne la racine du dépôt, d'où que le commit
soit lancé.

---

### Étape 2 : Rends-le exécutable et branche-le

**Où :** la racine du projet

**Action :**

> ```bash
> chmod +x .githooks/pre-commit
> git config core.hooksPath .githooks
> git config core.hooksPath
> ```

**Résultat attendu :** `.githooks`

**Pourquoi pas `.git/hooks/` ?** Le dossier `.git/` n'est **jamais** versionné : un hook posé là
disparaîtrait au prochain clone. `.githooks/` est versionné ; `core.hooksPath` dit à Git d'y
chercher ses hooks. Ce réglage, lui, est local : après un clone, on le refait une fois (ce sera écrit
dans `git-conventions.md`).

---

### Étape 3 : Tends un piège au vigile

**Où :** la racine du projet

**Action :**

> **Prédis d'abord** : le commit va-t-il passer ? Puis :
>
> ```bash
> printf 'token=Xk9fLq2mZpWw\n' > trap.properties
> git add trap.properties
> git commit -m "test: this commit must be refused"; echo "commit exit code: $?"
> ```

**Résultat attendu :**

```
trap.properties:1:token=Xk9fLq2mZpWw
KO  possible secret(s) above: remove the value, or unstage the file (git restore --staged <file>)
commit exit code: 1
```

**Pourquoi c'est la preuve clé ?** Le commit n'a **pas** eu lieu : le vigile a fouillé le carton,
trouvé le faux secret, et fermé la porte. (`Xk9fLq2mZpWw` est inventé.)

**✅ Vérifie que :**
- [ ] `commit exit code: 1`
- [ ] `git log` répond toujours `your current branch 'main' does not have any commits yet`

---

### Étape 4 : Retire le piège

**Où :** la racine du projet

**Action :**

> ```bash
> git rm --cached trap.properties
> rm trap.properties
> git status --short | grep -c trap
> ```

**Résultat attendu :** `rm 'trap.properties'`, puis `0`

**Pourquoi `git rm --cached` et pas `git restore --staged` ?** `restore --staged` remet le carton
« comme au dernier commit » (`HEAD`). Avant le premier commit, il n'y a pas de `HEAD` : la commande
échoue (`fatal: could not resolve HEAD`). `git rm --cached` retire le fichier du carton sans
regarder l'historique, et sans toucher au fichier sur le disque.

---

## 🧹 Comment tout annuler

```bash
git config --unset core.hooksPath
rm -r .githooks
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Le commit de l'étape 3 **passe** | `git config core.hooksPath` doit répondre `.githooks` ; `ls -l .githooks/pre-commit` doit montrer `x`. Puis annule le commit : `git update-ref -d HEAD` (premier commit seulement) et reprends l'étape 4 |
| `hint: The '.githooks/pre-commit' hook was ignored because it's not set as executable` | `chmod +x .githooks/pre-commit` |
| `Author identity unknown` | Tutoriel 001, étape 2 |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Hook branché | `git config core.hooksPath` | `.githooks` |
| Faux secret bloqué | étape 3 | `commit exit code: 1` |
| Piège retiré | `git status --short \| grep -c trap` | `0` |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Brancher un hook Git versionné
- ✨ Tester un garde-fou en le piégeant

Prochain tutoriel : **#005** — Écrire `docs/tooling/git-conventions.md` ⏱️ 15 min

Pause méritée ☕
