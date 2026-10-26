# Tutoriel 004 — Story #004 : Créer `nexus-deployer` et sa credential Jenkins

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 003
**Prérequis :** connectée à Nexus (`admin`) **et** à Jenkins (`admin`), dans deux onglets ; Bitwarden ouvert
**Exigence TP couverte :** `EXG-COM-19` (part)
**Fichier principal :** Nexus → Security → Users, puis Jenkins → Credentials

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Créer le compte `nexus-deployer` avec un mot de passe généré, et ranger ce mot de passe
**uniquement** dans la credential Jenkins `nexus-deploy-creds`.

## 📋 Ce que tu vas apprendre

- Générer un secret sans le sauvegarder dans le gestionnaire
- Créer une credential *Username with password*
- Pourquoi un secret « perdu » n'est pas grave quand on peut le régénérer

> ⚠️ **Fais ce tutoriel d'une traite** : le mot de passe ne vit que dans ton presse-papiers entre
> l'étape 1 et l'étape 4.

---

## 🪜 Les étapes

### Étape 1 : Génère le mot de passe, sans l'enregistrer

**Où :** Bitwarden → onglet **Générateur**

**Action :**

> Génère un mot de passe de 20 caractères minimum et **copie-le**. Ne crée **pas** d'entrée.

**Pourquoi ne pas l'enregistrer ?** Ce mot de passe ne sert qu'au pipeline (décision `2a`). Le seul
endroit où il doit vivre, c'est Jenkins. Si on le perd, on le réinitialise dans Nexus et on met à
jour la credential.

---

### Étape 2 : Crée l'utilisateur

**Où :** Nexus → **roue dentée** → **Security** → **Users** → **Create local user**

**Action :**

> | Champ | Valeur |
> |-------|--------|
> | ID | `nexus-deployer` |
> | First name | `Nexus` |
> | Last name | `Deployer` |
> | Email | `nexus-deployer@example.com` |
> | Password / Confirm password | colle le mot de passe généré |
> | Status | **Active** |
> | Roles | **uniquement** `step-by-step-deployer` |
>
> Clique sur **Create local user** (ou **Save**).

**✅ Vérifie que :**
- [ ] `nexus-deployer` apparaît dans la liste, avec le seul rôle `step-by-step-deployer`

---

### Étape 3 : Ouvre le formulaire de credential Jenkins

**Où :** `http://localhost:8080/manage/credentials/store/system/domain/_/newCredentials`

**Action :**

> Ouvre cette URL (même chemin que pour `sonar-token` en story 003).

---

### Étape 4 : Crée `nexus-deploy-creds`

**Où :** le formulaire de credential

**Action :**

> | Champ | Valeur |
> |-------|--------|
> | Type (Kind) | **Username with password** (« Nom d'utilisateur et mot de passe ») |
> | Portée (Scope) | **Global** |
> | Username | `nexus-deployer` |
> | Password | colle le mot de passe généré |
> | ID | `nexus-deploy-creds` |
> | Description | `Nexus deployer account (maven-releases)` |
>
> Clique sur **Create** (« Créer »).

**✅ Vérifie que :**
- [ ] `nexus-deploy-creds` apparaît, mot de passe masqué

---

### Étape 5 : Nettoie les traces

**Où :** Bitwarden et ton presse-papiers

**Action :**

> 1. Copie n'importe quel autre texte, pour écraser le presse-papiers.
> 2. Dans Bitwarden → **Générateur** → **Historique du générateur** : efface l'historique (le
>    générateur garde les derniers mots de passe générés).

**✅ Vérifie que :**
- [ ] Le mot de passe de `nexus-deployer` n'existe plus que dans Jenkins (et, haché, dans Nexus)

---

## 🧹 Comment tout annuler

1. Jenkins : `nexus-deploy-creds` → **Delete**.
2. Nexus : **Security** → **Users** → `nexus-deployer` → **Delete**.

**Mot de passe perdu ?** Nexus → **Users** → `nexus-deployer` → **Change password** avec un nouveau mot
de passe généré, puis Jenkins → `nexus-deploy-creds` → **Update**.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Le rôle `step-by-step-deployer` n'est pas proposé | Tutoriel 003 non terminé |
| Presse-papiers perdu entre l'étape 2 et l'étape 4 | Utilise « Mot de passe perdu ? » ci-dessus |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

- [ ] `nexus-deployer` actif, seul rôle `step-by-step-deployer`
- [ ] Credential `nexus-deploy-creds` dans Jenkins
- [ ] Mot de passe absent de Bitwarden et de tout fichier
- [ ] `git status` ne montre pas de fichier inattendu

Les droits de `nexus-deployer` seront prouvés **par l'action** en story 014, quand le pipeline
publiera le premier vrai artefact.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Créer un compte de service à droits minimaux
- ✨ Ranger un secret au seul endroit où il sert

Prochain tutoriel : **#005** — Publier un témoin, prouver qu'on ne peut pas le republier ⏱️ 20 min

Pause méritée ☕
