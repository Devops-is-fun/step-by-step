# Tutoriel 003 — Story #004 : Créer le rôle `step-by-step-deployer`

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 001
**Prérequis :** connectée à Nexus avec `admin`
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `http://localhost:8081` → Administration → Security → Roles

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Créer un rôle qui donne **juste** les droits nécessaires pour publier dans `maven-releases`, et rien
d'autre.

## 📋 Ce que tu vas apprendre

- Lire un nom de privilège Nexus
- Construire un rôle selon le principe du **moindre privilège**

---

## 🪜 Comprendre un nom de privilège

```
nx-repository-view-maven2-maven-releases-add
   │          │      │        │           │
   │          │      │        │           └─ action : add, edit, read, browse, delete
   │          │      │        └─ dépôt : maven-releases
   │          │      └─ format : maven2
   │          └─ accès au contenu d'un dépôt (view)
   └─ privilège Nexus
```

| Action | Pourquoi le déploiement en a besoin |
|--------|-------------------------------------|
| `add` | déposer un nouveau fichier |
| `edit` | mettre à jour les métadonnées Maven (`maven-metadata.xml`) |
| `read` | relire ce qui vient d'être déposé |
| `browse` | parcourir le dépôt |
| ~~`delete`~~ | **non** : le pipeline n'a jamais à supprimer une release |

---

## 🪜 Les étapes

### Étape 1 : Ouvre la création de rôle

**Où :** `http://localhost:8081` (navigateur)

**Action :**

> **Roue dentée** (Administration) → **Security** → **Roles** → **Create Role** → type **Nexus role**.

**Résultat attendu :** un formulaire avec **Role ID**, **Role Name**, **Description** et une zone
**Privileges**.

---

### Étape 2 : Remplis l'identité du rôle

**Où :** le formulaire de rôle

**Action :**

> | Champ | Valeur |
> |-------|--------|
> | Role ID | `step-by-step-deployer` |
> | Role Name | `step-by-step-deployer` |
> | Role Description | `Deploy to maven-releases only (CI pipeline)` |

---

### Étape 3 : Ajoute les 4 privilèges

**Où :** la zone **Privileges** du formulaire

**Action :**

> Filtre avec `nx-repository-view-maven2-maven-releases`, puis ajoute **exactement** ces quatre
> privilèges à la liste des privilèges accordés :
>
> - `nx-repository-view-maven2-maven-releases-add`
> - `nx-repository-view-maven2-maven-releases-browse`
> - `nx-repository-view-maven2-maven-releases-edit`
> - `nx-repository-view-maven2-maven-releases-read`
>
> Clique sur **Save** (ou **Create Role**).

**✅ Vérifie que :**
- [ ] Ni `-delete`, ni `-*` (tous les droits) ne sont ajoutés
- [ ] Aucun privilège d'un autre dépôt (`maven-snapshots`, `maven-public`…)

---

### Étape 4 : Relis le rôle

**Où :** **Security** → **Roles** → `step-by-step-deployer`

**Action :**

> Ouvre le rôle et compte ses privilèges.

**Résultat attendu :** exactement 4 privilèges, tous sur `maven-releases`.

---

## 🧹 Comment tout annuler

**Security** → **Roles** → `step-by-step-deployer` → **Delete**.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Le filtre ne trouve rien | Essaie seulement `maven-releases` ; les privilèges existent dès que le dépôt existe |
| Tu as ajouté un privilège en trop | Rouvre le rôle, retire-le, **Save** |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

- [ ] Rôle `step-by-step-deployer` avec 4 privilèges, tous sur `maven-releases`, sans `delete`
- [ ] `git status` ne montre pas de fichier inattendu

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Décoder un nom de privilège
- ✨ Appliquer le moindre privilège

Prochain tutoriel : **#004** — Créer `nexus-deployer` et sa credential Jenkins ⏱️ 20 min

Pause méritée ☕
