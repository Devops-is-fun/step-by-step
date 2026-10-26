# Tutoriel 002 — Story #004 : Vérifier la politique de `maven-releases`

**Temps estimé :** ⏱️ 10 minutes
**Difficulté :** ⭐
**Dépend de :** Tutoriel 001
**Prérequis :** connectée à Nexus avec `admin`
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `http://localhost:8081` → Administration → Repositories → `maven-releases`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Vérifier que le dépôt `maven-releases`, créé par défaut, n'accepte **qu'une seule publication par
version**.

## 📋 Ce que tu vas apprendre

- Ce que sont la *version policy* et la *deployment policy* d'un dépôt Maven
- Pourquoi une release doit être immuable

---

## 🪜 Les étapes

### Étape 1 : Ouvre le dépôt

**Où :** `http://localhost:8081` (navigateur)

**Action :**

> Clique sur la **roue dentée** (Administration) → **Repository** → **Repositories** →
> **maven-releases**.

**Résultat attendu :** la fiche du dépôt, de type **hosted**, format **maven2**.

---

### Étape 2 : Lis les deux politiques

**Où :** la fiche de `maven-releases`

**Action :**

> Repère, sans rien modifier :
>
> | Section | Réglage | Valeur attendue |
> |---------|---------|-----------------|
> | Maven 2 | **Version policy** | `Release` |
> | Hosted | **Deployment policy** | `Disable redeploy` |

**Pourquoi ces deux réglages ?**

| Réglage | Il garantit que… |
|---------|------------------|
| Version policy `Release` | le dépôt refuse les versions `-SNAPSHOT` (versions de travail, qui changent) |
| Deployment policy `Disable redeploy` | une version publiée ne peut **jamais** être remplacée |

C'est la base de l'exigence « même artefact en staging et en production » (`EXG-COM-17`) : si une
version pouvait être republiée, rien ne garantirait que la production reçoit ce qui a été testé.

**✅ Vérifie que :**
- [ ] Version policy = `Release`
- [ ] Deployment policy = `Disable redeploy`
- [ ] Tu n'as **rien** enregistré (pas de clic sur **Save**)

---

## 🧹 Comment tout annuler

Rien à annuler : ce tutoriel ne fait que lire.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Pas de roue dentée | Tu n'es pas connectée en `admin` : **Sign in** |
| `maven-releases` absent | Rare sur une installation neuve : signale-le dans `retours.md` avant d'aller plus loin |
| Deployment policy ≠ `Disable redeploy` | Choisis `Disable redeploy`, puis **Save** |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

- [ ] `maven-releases` : `Release` + `Disable redeploy`
- [ ] `git status` ne montre pas de fichier inattendu

La preuve **par l'action** de ces réglages arrive au tutoriel 005 : une republication sera refusée.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Lire les politiques d'un dépôt Maven
- ✨ Relier l'immuabilité des releases à une exigence du TP

Prochain tutoriel : **#003** — Créer le rôle `step-by-step-deployer` ⏱️ 15 min

Pause méritée ☕
