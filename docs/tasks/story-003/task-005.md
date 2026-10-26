# Tutoriel 005 — Story #003 : Créer le jeton utilisateur et le charger dans le terminal

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 001
**Prérequis :** connectée à SonarQube avec `admin` ; Bitwarden ouvert
**Exigence TP couverte :** `EXG-COM-19` (part)
**Fichier principal :** `http://localhost:9000/account/security`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Créer un jeton **utilisateur** pour les scripts locaux, le ranger dans Bitwarden et le charger dans
le terminal sous le nom `SONAR_TOKEN`, sans l'afficher.

## 📋 Ce que tu vas apprendre

- Réutiliser le geste `read -rs` de la story 002 sur un autre outil
- S'authentifier à SonarQube avec un jeton (le jeton sert d'identifiant, mot de passe vide)

---

## 🪜 Les étapes

### Étape 1 : Génère le jeton utilisateur

**Où :** `http://localhost:9000/account/security` (navigateur)

**Action :**

> Dans **Generate Tokens** :
>
> | Champ | Valeur |
> |-------|--------|
> | Name | `scripts-tp` |
> | Type | **User Token** |
> | Expires in | **1 year** |
>
> Clique sur **Generate**.

**Résultat attendu :** un jeton qui commence par `squ_`, affiché une seule fois.

**Pourquoi un jeton utilisateur ici ?** Le script doit **lire** la configuration (projet, gate) par
l'API. Un jeton d'analyse ne sait qu'envoyer des analyses : il ne peut pas lire ces informations.

**✅ Vérifie que :**
- [ ] Le jeton commence par `squ_`

---

### Étape 2 : Range-le dans Bitwarden

**Où :** Bitwarden

**Action :**

> Crée une entrée **Identifiant** : nom `SonarQube local — jeton scripts-tp`, nom d'utilisateur
> `admin`, mot de passe = le jeton copié. Enregistre.

**✅ Vérifie que :**
- [ ] L'entrée est enregistrée ; le jeton n'est dans aucun fichier

---

### Étape 3 : Charge le jeton sans l'afficher

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute, colle le jeton depuis Bitwarden, puis appuie sur **Entrée** (rien ne s'affiche) :
>
> ```bash
> read -rs SONAR_TOKEN && export SONAR_TOKEN
> ```

**Résultat attendu :** l'invite revient sans rien afficher.

**✅ Vérifie que :**
- [ ] `echo "${#SONAR_TOKEN}"` affiche `44` (`squ_` + 40 caractères)

---

### Étape 4 : Teste le jeton

**Où :** la racine du projet

**Action :**

> Exécute :
>
> ```bash
> curl -s -u "$SONAR_TOKEN:" http://localhost:9000/api/authentication/validate; echo
> ```

**Résultat attendu :** `{"valid":true}`

**Pourquoi `"$SONAR_TOKEN:"` avec un `:` à la fin ?** Avec SonarQube 9.9, le jeton se passe comme
**identifiant**, avec un mot de passe **vide** : c'est ce que dit le `:` final. Sans lui, `curl`
demanderait un mot de passe.

**✅ Vérifie que :**
- [ ] La réponse est `{"valid":true}`

---

## ⚠️ À retenir pour la suite

`SONAR_TOKEN` n'existe que dans ce terminal. Dans un nouveau terminal, refais l'étape 3.

---

## 🧹 Comment tout annuler

1. `unset SONAR_TOKEN`
2. SonarQube : `http://localhost:9000/account/security` → **Revoke** sur `scripts-tp`
3. Bitwarden : supprime l'entrée

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `echo "${#SONAR_TOKEN}"` affiche `0` | Le collage n'a pas marché : refais l'étape 3 |
| `{"valid":false}` à l'étape 4 | Jeton mal collé ou révoqué : refais l'étape 3, ou génère un nouveau jeton |
| `curl` demande un mot de passe | Il manque le `:` après `$SONAR_TOKEN` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

### Ce que tu dois avoir fait

- [ ] Étape 1 → jeton `scripts-tp` généré
- [ ] Étape 2 → rangé dans Bitwarden
- [ ] Étapes 3 et 4 → chargé et testé

### 🛡️ Garde-fous du TP

- [ ] `history | grep -c -F "$SONAR_TOKEN"` affiche `0`
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien : le premier commit du projet est fait en **story 006**.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Réutiliser `read -rs` sur un nouvel outil
- ✨ T'authentifier à SonarQube par jeton

Prochain tutoriel : **#006** — Écrire `scripts/check-sonarqube.sh` ⏱️ 20 min

Pause méritée ☕
