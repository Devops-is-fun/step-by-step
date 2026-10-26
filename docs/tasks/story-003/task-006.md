# Tutoriel 006 — Story #003 : Écrire `scripts/check-sonarqube.sh`

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 005
**Prérequis :** aucun (ce tutoriel écrit un fichier ; il sera lancé au tutoriel 007)
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `scripts/check-sonarqube.sh` (à créer)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Écrire le script qui vérifie en une commande la configuration de SonarQube : jeton valide,
`admin/admin` refusé, projet existant, gate associée, seuil à 60 %.

## 📋 Ce que tu vas apprendre

- Réutiliser la structure de `check-jenkins.sh`
- Tester le **contenu** d'une réponse d'API quand le code HTTP ne suffit pas
- Empêcher une erreur attendue d'arrêter un script strict (`|| true`)

---

## 🪜 Les étapes

### Étape 1 : Écris le script

**Où :** la racine du projet

**Action :**

> Crée `scripts/check-sonarqube.sh` avec ce contenu **exact** (réponds **Cancel** à IntelliJ) :
>
> ```bash
> #!/usr/bin/env bash
> # Checks the SonarQube configuration (story 003).
> # Requires: SONAR_TOKEN exported (user token, never the admin password).
> set -euo pipefail
>
> SONAR_URL="${SONAR_URL:-http://localhost:9000}"
> PROJECT_KEY="step-by-step"
> GATE_NAME="step-by-step-gate"
> : "${SONAR_TOKEN:?SONAR_TOKEN is not set (see docs/tooling/sonarqube.md)}"
>
> # Authenticated call to the SonarQube API: the token is the login, the password stays empty.
> api() {
>   curl -fsS -u "$SONAR_TOKEN:" "$SONAR_URL$1"
> }
>
> errors=0
>
> # 1. Token authentication. This endpoint always answers 200: read "valid", not the HTTP code.
> if grep -q '"valid":true' <<< "$(api "/api/authentication/validate" || true)"; then
>   echo "OK  token authentication"
> else
>   echo "KO  token authentication refused"
>   exit 1
> fi
>
> # 2. The default credentials (public, built into the product) must be refused
> if grep -q '"valid":false' <<< "$(curl -fsS -u admin:admin "$SONAR_URL/api/authentication/validate" || true)"; then
>   echo "OK  default admin/admin refused"
> else
>   echo "KO  default admin/admin still accepted"
>   errors=$((errors + 1))
> fi
>
> # 3. The project exists
> if api "/api/components/show?component=$PROJECT_KEY" > /dev/null; then
>   echo "OK  project $PROJECT_KEY exists"
> else
>   echo "KO  project $PROJECT_KEY missing"
>   errors=$((errors + 1))
> fi
>
> # 4. The project uses the dedicated quality gate
> if grep -q "\"name\":\"$GATE_NAME\"" <<< "$(api "/api/qualitygates/get_by_project?project=$PROJECT_KEY" || true)"; then
>   echo "OK  quality gate $GATE_NAME assigned"
> else
>   echo "KO  quality gate $GATE_NAME not assigned"
>   errors=$((errors + 1))
> fi
>
> # 5. The gate requires 60% coverage on new code
> if grep -q '"metric":"new_coverage","op":"LT","error":"60"' <<< "$(api "/api/qualitygates/show?name=$GATE_NAME" || true)"; then
>   echo "OK  new code coverage threshold: 60%"
> else
>   echo "KO  new code coverage threshold is not 60%"
>   errors=$((errors + 1))
> fi
>
> if [ "$errors" -eq 0 ]; then
>   echo "SonarQube is compliant"
> else
>   echo "$errors check(s) failed"
>   exit 1
> fi
> ```

**✅ Vérifie que :**
- [ ] Le fichier est dans `scripts/`
- [ ] Il ne contient **aucun** jeton ; `admin:admin` y figure, mais c'est l'identifiant **public** par
      défaut, que le script vérifie justement **refusé**

---

### Étape 2 : Compare avec `check-jenkins.sh`

**Où :** ton éditeur

**Action :**

> Ouvre les deux scripts côte à côte et repère ce qui est **pareil** et ce qui **change**.

| Pareil | Nouveau ici |
|--------|-------------|
| `set -euo pipefail`, `${VAR:?message}`, fonction `api()`, compteur `errors`, `exit 1` | `-u "$SONAR_TOKEN:"` : le jeton est l'identifiant, mot de passe vide |
| `curl -fsS` | pas besoin de `-g` : aucune URL ne contient de crochets |
| test de l'authentification en premier, avec arrêt immédiat | `grep -q '"valid":true'` : on lit la **réponse**, car cette URL répond toujours `200` |
| | `\|\| true` dans les `$(...)` : un `404` attendu ne doit pas arrêter le script strict |

**Pourquoi `|| true` ?** Avec `set -e`, la moindre commande en échec arrête le script. Or un projet
manquant fait échouer `curl -f` (code `404`). Sans `|| true`, le script s'arrêterait **sans
afficher** le `KO` qui t'explique le problème.

**✅ Vérifie que :**
- [ ] Tu sais dire pourquoi l'étape 1 du script ne se contente pas du code HTTP

---

### Étape 3 : Rends-le exécutable et vérifie la syntaxe

**Où :** la racine du projet

**Action :**

> ```bash
> chmod +x scripts/check-sonarqube.sh
> bash -n scripts/check-sonarqube.sh && echo "syntaxe OK"
> ```

**Résultat attendu :** `syntaxe OK`

**✅ Vérifie que :**
- [ ] `ls -l scripts/` affiche `-rwxr-xr-x` devant `check-sonarqube.sh`

---

## 🧹 Comment tout annuler

```bash
rm scripts/check-sonarqube.sh
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `syntax error` | Compare la ligne indiquée avec le tutoriel : souvent un guillemet ou un `fi` |
| Des `>` au début des lignes | Ils viennent du copier-coller depuis la citation : supprime-les |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

### Ce que tu dois avoir fait

- [ ] Étape 1 → script écrit
- [ ] Étape 2 → comparé avec `check-jenkins.sh`
- [ ] Étape 3 → exécutable, syntaxe OK

### 🛡️ Garde-fous du TP

- [ ] Le script ne contient aucun jeton
- [ ] `git status` : `scripts/` toujours non suivi (`??`)

### Ce que tu dois avoir versionné

Rien : `scripts/check-sonarqube.sh` sera commité en **story 006**.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Réutiliser une structure de script éprouvée
- ✨ Tester le contenu d'une réponse d'API
- ✨ Garder un script strict bavard grâce à `|| true`

Prochain tutoriel : **#007** — Lancer le script, puis le faire échouer exprès ⏱️ 15 min

Pause méritée ☕
