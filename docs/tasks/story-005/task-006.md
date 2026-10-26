# Tutoriel 006 — Story #005 : Passer le controller à 0 exécuteur

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 005
**Prérequis :** `docker-agent` connecté
**Exigence TP couverte :** `EXG-COM-04`
**Fichier principal :** `http://localhost:8080`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Retirer ses outils au chef de chantier : le controller passe de 2 à **0** exécuteur. Plus aucun build
ne peut s'exécuter sur lui.

## 📋 Ce que tu vas apprendre

- Où se règlent les exécuteurs du controller
- Pourquoi 0 (et pas 1) rend la contrainte **impossible à contourner**

---

## 🪜 Les étapes

### Étape 1 : Ouvre la configuration du controller

**Où :** `http://localhost:8080`

**Action :**

> **Manage Jenkins** → **Nodes** → **Built-In Node** → **Configure** (menu de gauche).

**Résultat attendu :** un formulaire avec **Number of executors** à `2`.

---

### Étape 2 : Mets 0

**Où :** le formulaire du Built-In Node

**Action :**

> **Number of executors** : `0`. Clique **Save**.

**Pourquoi 0 et pas 1 ?** Avec 1 exécuteur, un job sans `agent { label … }` (par exemple
`agent any`) pourrait encore s'exécuter sur le controller : la règle dépendrait de ta vigilance.
Avec 0, c'est **impossible** : un tel job part sur `docker-agent` (réglé sur *Use this node as much
as possible*) ; et si aucun agent n'est disponible, il **attend dans la file**, mais ne s'exécute
jamais sur le controller. La contrainte est garantie par la configuration, pas par la discipline.

---

### Étape 3 : Vérifie dans l'interface

**Où :** `http://localhost:8080` (tableau de bord)

**Action :**

> Regarde le panneau **Build Executor Status**, en bas à gauche du tableau de bord.

**Résultat attendu :** seulement `docker-agent`, avec 2 lignes `Idle`. Le Built-In Node n'apparaît
plus (ou n'a plus aucune ligne).

---

### Étape 4 : Vérifie dans le fichier de configuration

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose exec -T jenkins sh -c 'grep -o "<numExecutors>[0-9]*</numExecutors>" /var/jenkins_home/config.xml'
> ```

**Résultat attendu :** `<numExecutors>0</numExecutors>` (c'était `2` au tutoriel 001).

**Pourquoi :** la configuration est écrite dans le volume `jenkins_home` : elle survivra aux
redémarrages, comme tout ce que tu as configuré depuis la story 002.

**✅ Vérifie que :**
- [ ] `0` dans le fichier
- [ ] Seul `docker-agent` a des exécuteurs dans **Build Executor Status**

---

## 🧹 Comment tout annuler

Même chemin, **Number of executors** : `2`, **Save**. (À ne faire que pour un essai : l'énoncé exige 0.)

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Pas de **Configure** sur le Built-In Node | Tu es sur la page d'un autre nœud : reviens à **Nodes** et clique bien **Built-In Node** |
| Toujours `2` dans `config.xml` | La page n'a pas été enregistrée : refais l'étape 2 et clique **Save** |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Comment vérifier | Attendu |
|--------------|------------------|---------|
| Controller désarmé | `grep numExecutors` dans `config.xml` | `0` |
| Agent disponible | Build Executor Status | `docker-agent`, 2 × `Idle` |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Désarmer le controller
- ✨ Rendre une règle impossible à contourner plutôt que « à respecter »

Prochain tutoriel : **#007** — Créer le job `agent-smoke-test` ⏱️ 15 min

Pause méritée ☕
