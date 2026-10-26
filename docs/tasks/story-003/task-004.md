# Tutoriel 004 — Story #003 : Ranger le jeton d'analyse dans Jenkins

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** connectée à SonarQube (`admin`) et à Jenkins (`admin`)
**Exigence TP couverte :** `EXG-COM-19` (part)
**Fichier principal :** `http://localhost:9000/account/security`, puis Jenkins → Credentials

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Générer un jeton d'**analyse de projet**, limité à `step-by-step`, et le ranger dans une credential
Jenkins *Secret text* nommée `sonar-token`. Le pipeline l'utilisera en story 013.

## 📋 Ce que tu vas apprendre

- Les trois types de jetons SonarQube, et pourquoi choisir le plus limité
- Créer une credential Jenkins
- Pourquoi un secret du pipeline vit **dans Jenkins**, et nulle part ailleurs

---

## 🪜 Les étapes

### Étape 1 : Génère le jeton d'analyse

**Où :** `http://localhost:9000/account/security` (navigateur)

**Action :**

> Dans **Generate Tokens**, remplis :
>
> | Champ | Valeur |
> |-------|--------|
> | Name | `jenkins-step-by-step` |
> | Type | **Project Analysis Token** |
> | Project | **Step by Step** |
> | Expires in | **1 year** |
>
> Clique sur **Generate**.

**Résultat attendu :** un jeton qui commence par `sqp_`, affiché **une seule fois**, avec un bouton
**Copy**.

**Pourquoi ce type ?** SonarQube 9.9 propose trois types de jetons :

| Type | Peut faire | Usage ici |
|------|------------|-----------|
| User Token | tout ce que peut l'utilisateur (`admin` : tout) | script local (tutoriel 005) |
| Global Analysis Token | analyser **tous** les projets | aucun |
| **Project Analysis Token** | analyser **un seul** projet | ✅ le pipeline |

Si le jeton du pipeline fuitait, il ne permettrait que d'envoyer une analyse de `step-by-step`.
C'est le **moindre privilège**.

**✅ Vérifie que :**
- [ ] Le jeton commence par `sqp_`
- [ ] Tu gardes la page ouverte jusqu'à l'étape 3

---

### Étape 2 : Ouvre le formulaire de credential Jenkins

**Où :** `http://localhost:8080/manage/credentials/store/system/domain/_/newCredentials` (navigateur)

**Action :**

> Ouvre cette URL. Par les menus : **Administrer Jenkins** (Manage Jenkins) → **Credentials** →
> **System** → **Identifiants globaux (illimités)** (Global credentials (unrestricted)) → **Add
> Credentials**.

**Résultat attendu :** un formulaire avec un champ **Type** (Kind).

---

### Étape 3 : Crée la credential `sonar-token`

**Où :** le formulaire de credential

**Action :**

> | Champ | Valeur |
> |-------|--------|
> | Type (Kind) | **Secret text** (« Texte secret ») |
> | Portée (Scope) | **Global** |
> | Secret | colle le jeton `sqp_…` |
> | ID | `sonar-token` |
> | Description | `SonarQube project analysis token (step-by-step)` |
>
> Clique sur **Create** (« Créer »).

**Résultat attendu :** la liste des credentials, avec une ligne `sonar-token`.

**Pourquoi l'ID compte ?** Le `Jenkinsfile` de la story 013 référencera `sonar-token` **par son ID** :
`withCredentials([string(credentialsId: 'sonar-token', ...)])`. Le jeton n'apparaîtra jamais dans le
code, seulement ce nom.

**Pourquoi pas aussi dans Bitwarden ?** Ce jeton ne sert qu'au pipeline. Le garder ailleurs
multiplierait les endroits d'où il peut fuiter. S'il est perdu, on en génère un autre.

**✅ Vérifie que :**
- [ ] La ligne `sonar-token` apparaît
- [ ] En l'ouvrant, le secret est **masqué** (« Concealed for Confidentiality » ou équivalent)

---

### Étape 4 : Ferme la page SonarQube

**Où :** `http://localhost:9000/account/security`

**Action :**

> Rafraîchis la page.

**Résultat attendu :** le jeton `jenkins-step-by-step` est listé (type Project Analysis, projet Step
by Step), **sans sa valeur**.

**✅ Vérifie que :**
- [ ] Le jeton est listé, sa valeur n'est plus visible
- [ ] Le jeton n'a été collé **nulle part ailleurs** que dans Jenkins

---

## 🧹 Comment tout annuler

1. Jenkins : credential `sonar-token` → **Delete**.
2. SonarQube : `http://localhost:9000/account/security` → **Revoke** sur `jenkins-step-by-step`.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Le type **Project Analysis Token** n'est pas proposé | Le projet n'existe pas encore : tutoriel 002 |
| Pas de type « Secret text » dans Jenkins | Le plugin `credentials-binding` n'est pas installé : vérifie avec `./scripts/check-jenkins.sh` |
| Page fermée avant de copier le jeton | Révoque-le dans SonarQube et génère-en un nouveau |
| « This ID is already in use » | Une credential `sonar-token` existe déjà : ouvre-la → **Update** pour remplacer le secret |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

### Ce que tu dois avoir fait

- [ ] Étape 1 → jeton d'analyse généré
- [ ] Étapes 2 et 3 → credential `sonar-token` créée
- [ ] Étape 4 → jeton listé sans sa valeur

### 🛡️ Garde-fous du TP

- [ ] Le jeton n'existe que dans Jenkins
- [ ] Aucun build ne s'est exécuté sur le controller Jenkins
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien : le premier commit du projet est fait en **story 006**.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Choisir le jeton le plus limité possible
- ✨ Créer une credential Jenkins
- ✨ Référencer un secret par son ID, jamais par sa valeur

Prochain tutoriel : **#005** — Créer le jeton utilisateur et le charger dans le terminal ⏱️ 15 min

Pause méritée ☕
