# Tutoriel 005 — Story #002 : Écrire `scripts/check-jenkins.sh`

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 004
**Prérequis :** aucun (ce tutoriel écrit un fichier ; il sera lancé au tutoriel 006)
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `scripts/check-jenkins.sh` (à créer)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Écrire un script qui vérifie en une commande ce que tu as constaté à la main : authentification par
jeton, protection CSRF, et présence des 10 plugins.

## 📋 Ce que tu vas apprendre

- Rendre un script bash strict avec `set -euo pipefail`
- Exiger une variable d'environnement avec `${VAR:?message}`
- Éviter le piège des crochets dans une URL `curl`

---

## 🪜 Les étapes

### Étape 1 : Crée le dossier `scripts/`

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> mkdir -p scripts
> ```

**Résultat attendu :** aucune sortie ; `ls` montre un dossier `scripts`.

---

### Étape 2 : Écris le script

**Où :** la racine du projet

**Action :**

> Crée le fichier `scripts/check-jenkins.sh` avec ce contenu **exact**. Si IntelliJ demande « Add
> file to Git? », réponds **Cancel**.
>
> ```bash
> #!/usr/bin/env bash
> # Vérifie la configuration de Jenkins (story 002).
> # Prérequis : JENKINS_USER et JENKINS_TOKEN exportés (jeton d'API, jamais le mot de passe).
> set -euo pipefail
>
> JENKINS_URL="${JENKINS_URL:-http://localhost:8080}"
> : "${JENKINS_USER:?JENKINS_USER is not set (see docs/tooling/jenkins-configuration.md)}"
> : "${JENKINS_TOKEN:?JENKINS_TOKEN is not set (see docs/tooling/jenkins-configuration.md)}"
>
> REQUIRED_PLUGINS=(
>   git
>   github-branch-source
>   workflow-aggregator
>   pipeline-graph-view
>   sonar
>   credentials-binding
>   junit
>   coverage
>   warnings-ng
>   pipeline-utility-steps
> )
>
> # Appel authentifié à l'API Jenkins. -g : les crochets de l'URL ne sont pas des motifs curl.
> api() {
>   curl -fsSg -u "$JENKINS_USER:$JENKINS_TOKEN" "$JENKINS_URL$1"
> }
>
> errors=0
>
> # 1. Authentification par jeton : sans elle, inutile d'aller plus loin
> if api "/api/json" > /dev/null; then
>   echo "OK  token authentication"
> else
>   echo "KO  token authentication refused"
>   exit 1
> fi
>
> # 2. Protection CSRF : le crumb doit être disponible
> crumb_json="$(api "/crumbIssuer/api/json" || true)"
> if grep -q '"crumb"' <<< "$crumb_json"; then
>   echo "OK  CSRF protection (crumb available)"
> else
>   echo "KO  CSRF protection: no crumb"
>   errors=$((errors + 1))
> fi
>
> # 3. Plugins obligatoires installés
> plugins_json="$(api "/pluginManager/api/json?tree=plugins[shortName]")"
> for plugin in "${REQUIRED_PLUGINS[@]}"; do
>   if grep -q "\"shortName\":\"$plugin\"" <<< "$plugins_json"; then
>     echo "OK  plugin $plugin"
>   else
>     echo "KO  plugin $plugin missing"
>     errors=$((errors + 1))
>   fi
> done
>
> if [ "$errors" -eq 0 ]; then
>   echo "Jenkins is compliant"
> else
>   echo "$errors check(s) failed"
>   exit 1
> fi
> ```

**Résultat attendu :** le fichier existe et se termine par un saut de ligne.

**✅ Vérifie que :**
- [ ] Le fichier est dans `scripts/`, pas à la racine
- [ ] Il ne contient **aucune** valeur de jeton ni de mot de passe : seulement les **noms** de
      variables `JENKINS_USER` et `JENKINS_TOKEN`

---

### Étape 3 : Lis le script, bloc par bloc

**Où :** ton éditeur

**Action :**

> Relis le script en t'aidant de ce tableau. Pour chaque ligne du tableau, retrouve le passage dans
> le fichier.

| Passage | Ce qu'il fait |
|---------|---------------|
| `set -euo pipefail` | `-e` : arrêt à la première erreur ; `-u` : erreur si une variable n'existe pas ; `pipefail` : une erreur au milieu d'un `\|` compte |
| `"${JENKINS_URL:-http://localhost:8080}"` | valeur **par défaut** si `JENKINS_URL` n'est pas défini |
| `: "${JENKINS_TOKEN:?message}"` | arrête le script avec ce message si la variable est absente ; `:` est une commande qui ne fait rien d'autre |
| `curl -f` | considère une réponse `4xx`/`5xx` comme un échec (sinon `curl` réussit même sur un `401`) |
| `curl -g` | désactive les motifs de `curl` : sans lui, `plugins[shortName]` provoque l'erreur `bad range in URL` |
| `<<< "$plugins_json"` | donne le contenu de la variable à `grep`, comme si c'était un fichier |
| `exit 1` | code de sortie « échec » : un pipeline Jenkins s'arrêtera dessus |

**Pourquoi le piège des crochets ?** `curl` comprend `[1-3]` comme « fais 3 requêtes ». En voyant
`[shortName]`, il cherche une plage et plante. Cela a été vérifié sur ton poste au moment de la
génération de ce tutoriel : sans `-g`, `curl` sort avec l'erreur 3.

**✅ Vérifie que :**
- [ ] Tu sais dire, avec tes mots, à quoi servent `-f` et `-g`

---

### Étape 4 : Rends le script exécutable

**Où :** la racine du projet

**Action :**

> Exécute :
>
> ```bash
> chmod +x scripts/check-jenkins.sh
> ```

**Résultat attendu :** `ls -l scripts/` affiche `-rwxr-xr-x` devant `check-jenkins.sh`.

---

### Étape 5 : Vérifie la syntaxe sans exécuter

**Où :** la racine du projet

**Action :**

> Exécute :
>
> ```bash
> bash -n scripts/check-jenkins.sh && echo "syntaxe OK"
> ```

**Résultat attendu :** `syntaxe OK`

**Pourquoi `bash -n` ?** Il **lit** le script sans l'exécuter. Il attrape les fautes de frappe
(guillemet oublié, `fi` manquant) avant tout effet de bord.

**✅ Vérifie que :**
- [ ] La sortie est `syntaxe OK`

---

## 🧹 Comment tout annuler

```bash
rm scripts/check-jenkins.sh
rmdir scripts   # seulement si le dossier est vide
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `bash -n` signale `syntax error near unexpected token` | Compare la ligne indiquée avec le tutoriel : souvent un `fi`, un `done` ou un guillemet oublié |
| Le fichier contient des `>` au début des lignes | Ils viennent du copier-coller depuis la citation du tutoriel : supprime-les |
| `Permission denied` en lançant le script | L'étape 4 a été sautée : `chmod +x scripts/check-jenkins.sh` |

---

## 📚 Aide-mémoire des commandes de vérification

| Besoin | Commande |
|--------|----------|
| Vérifier la syntaxe d'un script | `bash -n scripts/check-jenkins.sh` |
| Voir les droits d'un fichier | `ls -l scripts/` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

### Ce que tu dois avoir fait

- [ ] Étape 1 → dossier `scripts/` créé
- [ ] Étape 2 → `scripts/check-jenkins.sh` écrit
- [ ] Étape 3 → script lu bloc par bloc
- [ ] Étape 4 → script exécutable
- [ ] Étape 5 → syntaxe vérifiée

### Ce que tu dois avoir vérifié

| Vérification | Comment vérifier | Attendu |
|--------------|------------------|---------|
| Syntaxe | `bash -n scripts/check-jenkins.sh` | aucune erreur |
| Droits | `ls -l scripts/` | `-rwxr-xr-x` |

### 🛡️ Garde-fous du TP

- [ ] Le script ne contient **aucun** secret, seulement des noms de variables
- [ ] Aucun build ne s'est exécuté sur le controller Jenkins
- [ ] `git status` montre `scripts/` en non suivi (`??`), rien d'autre de nouveau

### Ce que tu dois avoir versionné

Rien pour l'instant : `scripts/check-jenkins.sh` sera commité en **story 006**.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Écrire un script bash strict
- ✨ Exiger une variable d'environnement avec un message clair
- ✨ Éviter le piège des crochets dans `curl`

Prochain tutoriel : **#006** — Lancer le script, puis le faire échouer exprès ⏱️ 15 min

Pause méritée ☕
