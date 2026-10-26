# Tutoriel 003 — Story #003 : Créer la Quality Gate à 60 % et l'associer

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** connectée à SonarQube avec `admin` ; projet `step-by-step` créé
**Exigence TP couverte :** `EXG-COM-02` (prépare `EXG-COM-11`, story 013)
**Fichier principal :** `http://localhost:9000/quality_gates`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Créer la Quality Gate `step-by-step-gate`, copie de *Sonar way* avec une couverture du nouveau code
à **60 %** au lieu de 80 %, et l'associer au projet.

## 📋 Ce que tu vas apprendre

- Ce qu'est une Quality Gate et ce qu'est le « nouveau code »
- Pourquoi on copie une gate intégrée au lieu de la modifier
- Aligner l'outil sur une décision écrite dans la spécification

---

## 🪜 Les étapes

### Étape 1 : Copie *Sonar way*

**Où :** `http://localhost:9000/quality_gates` (navigateur)

**Action :**

> Clique sur **Sonar way** dans la liste de gauche, puis sur le bouton **Copy** en haut à droite.
> Nomme la copie `step-by-step-gate` et clique sur **Copy**.

**Résultat attendu :** `step-by-step-gate` apparaît dans la liste de gauche, avec les mêmes
conditions que *Sonar way*.

**Pourquoi copier ?** *Sonar way* est **intégrée** (« Built-in ») : SonarQube interdit de la modifier.
La copie hérite de toutes ses règles, et on n'en change qu'une.

**✅ Vérifie que :**
- [ ] `step-by-step-gate` est sélectionnée
- [ ] Sa section **Conditions on New Code** contient une ligne **Coverage** à `80.0%`

---

### Étape 2 : Passe la couverture à 60 %

**Où :** la page de `step-by-step-gate`

**Action :**

> Sur la ligne **Coverage** de « Conditions on New Code », clique sur l'icône **crayon** (Edit).
> Remplace `80` par `60` dans **Value**, puis clique sur **Update Condition**.

**Résultat attendu :** la ligne affiche `is less than 60.0%`.

> ⚠️ SonarQube peut afficher un avertissement indiquant que la gate n'est plus conforme à
> « Clean as You Code ». C'est attendu : cette approche recommande 80 %, ta spécification a choisi
> 60 %. Ignore l'avertissement.

**Pourquoi « nouveau code » ?** SonarQube juge surtout le code **ajouté ou modifié** récemment, pas
tout l'historique. Ainsi, une équipe qui reprend un vieux projet peut avoir une gate verte tant que
ce qu'elle ajoute est propre. Ici, tout ton code sera « nouveau » au début.

**✅ Vérifie que :**
- [ ] La condition Coverage affiche `60.0%`
- [ ] Les autres conditions n'ont pas bougé (Duplicated Lines 3 %, ratings A, Security Hotspots
      Reviewed 100 %)

---

### Étape 3 : Associe la gate au projet

**Où :** la page de `step-by-step-gate`

**Action :**

> Descends jusqu'à la section **Projects**. Clique sur l'onglet **All** (ou **Without**), puis coche
> **Step by Step**.

**Résultat attendu :** **Step by Step** apparaît dans l'onglet **With**.

**Si la section Projects n'existe pas :** ouvre le projet → **Project Settings** → **Quality Gate** →
**Always use a specific Quality Gate** → choisis `step-by-step-gate` → **Save**.

**✅ Vérifie que :**
- [ ] Le projet est dans l'onglet **With**

---

### Étape 4 : Vérifie avec l'API

**Où :** la racine du projet

**Action :**

> Exécute. Quand `curl` affiche `Enter host password for user 'admin':`, colle le mot de passe
> **admin de SonarQube** (entrée Bitwarden `SonarQube local — TP CI/CD`, celle du tutoriel 001) :
>
> ```bash
> curl -s -u admin "http://localhost:9000/api/qualitygates/get_by_project?project=step-by-step"; echo
> ```

**Résultat attendu :** une réponse qui contient `"name":"step-by-step-gate"`.

**Pourquoi `-u admin` sans mot de passe ?** `curl` te le demande alors **sans l'afficher**, et rien
n'entre dans l'historique. C'est la version « à la main » du `read -rs` de la story 002.

**✅ Vérifie que :**
- [ ] La réponse contient `"name":"step-by-step-gate"`

---

## 🧹 Comment tout annuler

1. Projet → **Project Settings** → **Quality Gate** → **Always use the instance default**.
2. `http://localhost:9000/quality_gates` → `step-by-step-gate` → menu **⋯** ou bouton **Delete**.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Pas de bouton **Copy** | Vérifie que tu es connectée avec `admin` |
| Pas de crayon sur les conditions | Tu es sur *Sonar way* (non modifiable) : sélectionne `step-by-step-gate` |
| L'API répond `"name":"Sonar way"` | L'association n'est pas faite : reprends l'étape 3 |
| L'API répond `401` | Mot de passe mal saisi : relance la commande |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

### Ce que tu dois avoir fait

- [ ] Étape 1 → gate copiée
- [ ] Étape 2 → couverture à 60 %
- [ ] Étape 3 → gate associée au projet
- [ ] Étape 4 → association vérifiée par l'API

### 🛡️ Garde-fous du TP

- [ ] Aucun mot de passe tapé dans une commande ni écrit dans un fichier
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien : le premier commit du projet est fait en **story 006**.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Créer une Quality Gate sur mesure
- ✨ Comprendre la notion de « nouveau code »
- ✨ Aligner un outil sur une décision de ta spécification

Prochain tutoriel : **#004** — Ranger le jeton d'analyse dans Jenkins ⏱️ 20 min

Pause méritée ☕
