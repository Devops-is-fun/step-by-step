# Tutoriel 003 — Story #006 : Écrire `scripts/check-no-secrets.sh`

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** aucun (ce tutoriel écrit un fichier ; il sera branché au tutoriel 004)
**Exigence TP couverte :** `EXG-COM-19` (part)
**Fichier principal :** `scripts/check-no-secrets.sh` (à créer)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Écrire le **vigile** : un script qui fouille ce que Git s'apprête à enregistrer, et refuse s'il y
trouve un secret.

## 📋 Ce que tu vas apprendre

- `git grep --cached` : chercher dans l'**index** (le carton), pas dans le dossier
- Deux façons de reconnaître un secret : par sa **forme**, ou par son **nom**
- Distinguer « rien trouvé » d'une **erreur** dans un script

---

## 🪜 Les étapes

### Étape 1 : Écris le script

**Où :** la racine du projet

**Action :**

> Crée `scripts/check-no-secrets.sh` avec ce contenu **exact** (réponds **Cancel** à IntelliJ) :
>
> ```bash
> #!/usr/bin/env bash
> # Looks for secrets in what Git is about to version (the index), before each commit.
> # Used by the pre-commit hook (.githooks/pre-commit); can also be run by hand.
> set -euo pipefail
>
> # Searches the index; prints the matching lines. Returns 0 if found, 1 if not; stops on a git error.
> scan() {
>   local status=0
>   git grep --cached -nI "$@" || status=$?
>   if [ "$status" -gt 1 ]; then
>     echo "KO  scan failed (git grep exit code $status)"
>     exit 2
>   fi
>   return "$status"
> }
>
> found=0
>
> # 1. Well-known secret formats, in every file:
> #    private key, SonarQube token, Jenkins API token, GitHub token
> FORMATS='-----BEGIN [A-Z ]*PRIVATE KEY-----$|sq[apu]_[0-9a-f]{40}|\b11[0-9a-f]{32}\b|gh[pousr]_[A-Za-z0-9]{36}|github_pat_[A-Za-z0-9_]{20,}'
> if scan -E -e "$FORMATS"; then
>   found=1
> fi
>
> # 2. A long value assigned to a secret-like name, in configuration and code
> #    (Markdown excluded: its prose explains secrets without containing any)
> ASSIGN='(password|passwd|secret|token|api[_-]?key)[[:space:]]*[:=][[:space:]]*[^<{$?[:space:]]{8,}'
> if scan -iE -e "$ASSIGN" -- . ':(exclude)*.md'; then
>   found=1
> fi
>
> if [ "$found" -eq 0 ]; then
>   echo "OK  no secret found in the files to commit"
> else
>   echo "KO  possible secret(s) above: remove the value, or unstage the file (git restore --staged <file>)"
>   exit 1
> fi
> ```

---

### Étape 2 : Comprends les deux filets

**Où :** ton éditeur

**Filet 1 — la forme (`FORMATS`), dans tous les fichiers.** Certains secrets se reconnaissent à leur
allure, comme une plaque d'immatriculation :

| Motif | Reconnaît |
|-------|-----------|
| `-----BEGIN … PRIVATE KEY-----` en fin de ligne | une clé privée (story 005) |
| `sq[apu]_` + 40 caractères hexadécimaux | un jeton SonarQube (`squ_`, `sqp_`… story 003) |
| `11` + 32 hexadécimaux | un jeton d'API Jenkins (ton `scripts-tp`, 34 caractères) |
| `ghp_…`, `github_pat_…` | un jeton GitHub |

**Filet 2 — le nom (`ASSIGN`), hors Markdown.** `password: <valeur d'au moins 8 caractères>` dans un
`.yml`, un `.sh`, un `.properties`… La valeur ne doit pas commencer par `$`, `{`, `<` ou `?` : ce
seraient des **références** (`$SONAR_TOKEN`, `${JENKINS_TOKEN:?…}`), pas des valeurs.

**Pourquoi exclure le Markdown du filet 2 ?** Tes docs **parlent** de mots de passe
(« new admin password: generated… ») : à chaque commit, le vigile crierait au loup. Le filet 1, lui,
fouille aussi le Markdown : un vrai jeton collé dans une doc serait attrapé.

**Pourquoi « au moins 8 caractères » ?** Un vrai secret généré (Bitwarden) en fait 20 ou plus.
Limite assumée : `password=admin` passerait. Ce n'est pas un secret à protéger, c'est un mot de passe
à changer.

**Et `scan()` ?** `git grep` répond `0` (trouvé), `1` (rien trouvé)… ou plus de `1` en cas d'**erreur**.
Sans cette fonction, une erreur ressemblerait à « rien trouvé » : le vigile dormirait en disant que
tout va bien. Même famille de piège que le `< /dev/null` de `check-nexus.sh`.

**✅ Vérifie que :**
- [ ] Tu sais dire pourquoi `${JENKINS_TOKEN:?…}` ne déclenche pas d'alerte

---

### Étape 3 : Rends-le exécutable et lance-le

**Où :** la racine du projet

**Action :**

> ```bash
> chmod +x scripts/check-no-secrets.sh
> bash -n scripts/check-no-secrets.sh && echo "syntaxe OK"
> ./scripts/check-no-secrets.sh; echo "exit code: $?"
> ```

**Résultat attendu :**

```
syntaxe OK
OK  no secret found in the files to commit
exit code: 0
```

**Pourquoi déjà « OK » ?** L'index ne contient que `.gitignore` pour l'instant : le vigile a peu à
fouiller. Au tutoriel 004, on lui tend un piège.

---

## 🧹 Comment tout annuler

```bash
rm scripts/check-no-secrets.sh
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `KO  scan failed (git grep exit code 128)` | Un caractère du motif a été perdu au copier-coller : recopie la ligne `FORMATS=` ou `ASSIGN=` |
| `Permission denied` | Étape 3 : `chmod +x` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

- [ ] Script écrit, exécutable, syntaxe OK
- [ ] `OK  no secret found…`, code `0`

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Chercher dans l'index avec `git grep --cached`
- ✨ Reconnaître un secret par sa forme ou par son nom
- ✨ Ne pas confondre « rien trouvé » et « erreur »

Prochain tutoriel : **#004** — Brancher le vigile (hook) et le tester ⏱️ 15 min

Pause méritée ☕
