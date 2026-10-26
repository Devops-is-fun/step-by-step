# Tutoriel 002 — Story #003 : Créer le projet `step-by-step`

**Temps estimé :** ⏱️ 10 minutes
**Difficulté :** ⭐
**Dépend de :** Tutoriel 001
**Prérequis :** connectée à SonarQube avec `admin`
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `http://localhost:9000`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Créer dans SonarQube le projet qui recevra les analyses de l'application, avec la clé
`step-by-step`.

## 📋 Ce que tu vas apprendre

- La différence entre le **nom affiché** et la **clé** d'un projet SonarQube
- Pourquoi la clé doit être identique partout (SonarQube, `pom.xml`, Jenkins)

---

## 🪜 Les étapes

### Étape 1 : Ouvre la création manuelle

**Où :** `http://localhost:9000` (navigateur)

**Action :**

> Sur la page d'accueil (« How do you want to create your project? »), clique sur **Manually**.
> Si tu ne vois pas cette page : menu **Projects** → bouton **Create Project** → **Manually**.

**Résultat attendu :** un formulaire « Create a project ».

---

### Étape 2 : Remplis le formulaire

**Où :** le formulaire « Create a project »

**Action :**

> | Champ | Valeur |
> |-------|--------|
> | Project display name | `Step by Step` |
> | Project key | `step-by-step` |
> | Main branch name | `main` |
>
> Clique sur **Set Up**.

**Résultat attendu :** une page « How do you want to analyze your repository? » avec plusieurs
choix (Jenkins, Locally…). **Ne choisis rien** : l'analyse est configurée en stories 007 et 013.

**Pourquoi la clé compte plus que le nom ?** Le nom affiché peut changer ; la **clé** est l'identifiant
que le scanner Maven enverra (`sonar.projectKey`, story 007). Si elle diffère d'un seul caractère,
SonarQube créera un autre projet ou refusera l'analyse. C'est la même logique que les identifiants de
plugins Jenkins.

**✅ Vérifie que :**
- [ ] La clé est exactement `step-by-step` (minuscules, tirets)

---

### Étape 3 : Vérifie le projet par l'API

**Où :** `http://localhost:9000/projects` (navigateur)

**Action :**

> Ouvre la liste des projets.

**Résultat attendu :** une carte **Step by Step**, avec la mention « Project's Main Branch is not
analyzed yet ».

**✅ Vérifie que :**
- [ ] Le projet apparaît dans la liste

---

## 🧹 Comment tout annuler

Menu **Administration** → **Projects** → **Management** : coche `Step by Step` → **Delete**.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| « Could not create Project with key: "step-by-step". A similar key already exists » | Le projet existe déjà : vérifie la liste des projets |
| Tu as choisi une méthode d'analyse par erreur | Sans importance : rien n'est lancé tant qu'aucun scanner n'envoie d'analyse |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

### Ce que tu dois avoir fait

- [ ] Étapes 1 et 2 → projet créé avec la clé `step-by-step`
- [ ] Étape 3 → projet visible dans la liste

### 🛡️ Garde-fous du TP

- [ ] Aucun secret écrit dans un fichier
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien : le premier commit du projet est fait en **story 006**.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Créer un projet SonarQube
- ✨ Distinguer le nom affiché de la clé

Prochain tutoriel : **#003** — Créer la Quality Gate à 60 % et l'associer ⏱️ 20 min

Pause méritée ☕
