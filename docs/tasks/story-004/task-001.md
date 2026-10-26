# Tutoriel 001 — Story #004 : Passer l'assistant de Nexus

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** story 003 terminée
**Prérequis :** `nexus` est `healthy` (`docker compose ps`) ; Bitwarden ouvert
**Exigence TP couverte :** `EXG-COM-19` (part)
**Fichier principal :** `http://localhost:8081`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Terminer l'assistant de première connexion de Nexus : nouveau mot de passe admin rangé dans
Bitwarden, et **accès anonyme en lecture activé**.

## 📋 Ce que tu vas apprendre

- Lire le mot de passe initial de Nexus sans le stocker (même geste que Jenkins)
- Ce que change l'accès anonyme, et pourquoi on l'active ici
- Prouver la fin de l'assistant par la disparition d'un fichier

---

## 🪜 Les étapes

### Étape 1 : Constate l'état « avant »

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose exec -T nexus ls -la /nexus-data/admin.password
> curl -s -o /dev/null -w "lecture anonyme : %{http_code}\n" http://localhost:8081/repository/maven-releases/
> ```

**Résultat attendu :** la ligne du fichier (daté du 4 octobre, ta preuve de persistance de la
story 001), puis `lecture anonyme : 401`.

**✅ Vérifie que :**
- [ ] Le fichier existe et la lecture anonyme est refusée (`401`)

---

### Étape 2 : Prépare le mot de passe dans Bitwarden

**Où :** Bitwarden

**Action :**

> Crée une entrée **Identifiant** : nom `Nexus local — TP CI/CD`, nom d'utilisateur `admin`, URL
> `http://localhost:8081`, mot de passe **généré** (🔄, 20 caractères minimum). Enregistre.

---

### Étape 3 : Lis le mot de passe initial

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose exec nexus cat /nexus-data/admin.password; echo
> ```

**Résultat attendu :** une ligne de 36 caractères (lettres, chiffres et tirets).

**Pourquoi le `; echo` ?** Le fichier ne se termine pas par un retour à la ligne : sans `echo`, ton
invite de commande se collerait au mot de passe (souviens-toi de la réponse JSON de SonarQube au
tutoriel 008 de la story 001).

---

### Étape 4 : Connecte-toi

**Où :** `http://localhost:8081` (navigateur)

**Action :**

> Clique sur **Sign in** en haut à droite. Username : `admin`, Password : la ligne de l'étape 3.
> Clique sur **Sign in**.

**Résultat attendu :** l'assistant **Setup** s'ouvre.

---

### Étape 5 : Suis l'assistant

**Où :** la fenêtre **Setup**

**Action :**

> Avance avec **Next** à chaque écran :
>
> | Écran | Ce que tu fais |
> |-------|----------------|
> | Welcome / présentation | **Next** |
> | Licence (EULA), si l'écran apparaît | lis-la, coche l'acceptation, **Next** |
> | Nouveau mot de passe admin | colle le mot de passe de Bitwarden dans les **deux** champs |
> | **Configure Anonymous Access** | choisis **Enable anonymous access** |
> | Autre écran (statistiques d'usage…) | garde le choix par défaut |
> | Complete | **Finish** |

**Pourquoi activer l'accès anonyme ?** Les environnements staging et production téléchargeront
l'artefact depuis Nexus **sans identifiants** (décision `5a`). L'anonyme peut **lire**, pas
**publier** : la publication restera réservée à `nexus-deployer`. Le script du tutoriel 006 vérifiera
les deux.

> ⚠️ L'ordre exact des écrans peut varier selon la version. Ce qui compte : nouveau mot de passe
> = celui de Bitwarden, accès anonyme = **activé**.

**✅ Vérifie que :**
- [ ] L'assistant est fermé et tu es connectée en `admin`

---

### Étape 6 : Constate l'état « après »

**Où :** la racine du projet

**Action :**

> Relance les deux commandes de l'étape 1.

**Résultat attendu :** `No such file or directory` pour `admin.password`, puis
`lecture anonyme : 200`.

**Pourquoi le fichier a disparu ?** Nexus supprime son mot de passe initial **à la fin de
l'assistant** : il ne sert plus, donc il ne doit plus traîner. C'est la preuve de la story 004
« assistant terminé ».

**✅ Vérifie que :**
- [ ] `admin.password` n'existe plus
- [ ] La lecture anonyme répond `200`

---

## 🧹 Comment tout annuler

- Accès anonyme : **Administration** (roue dentée) → **Security** → **Anonymous Access** → décoche
  → **Save**.
- Mot de passe : menu de ton compte → **Change password**, puis mets à jour Bitwarden.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `No such file` dès l'étape 1 | L'assistant a déjà été passé : connecte-toi avec le mot de passe de Bitwarden, puis va à l'étape 6 |
| L'assistant ne s'ouvre pas après la connexion | Rafraîchis la page ; sinon, menu de ton compte → l'assistant se relance tant qu'il n'est pas fini |
| `lecture anonyme : 401` à l'étape 6 | L'accès anonyme n'est pas activé : **Administration** → **Security** → **Anonymous Access** → coche → **Save** |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Comment vérifier | Attendu |
|--------------|------------------|---------|
| Assistant terminé | `docker compose exec -T nexus ls /nexus-data/admin.password` | `No such file or directory` |
| Lecture anonyme | `curl` de l'étape 1 | `200` |
| Mot de passe admin | connexion dans le navigateur | OK avec Bitwarden |

### 🛡️ Garde-fous du TP

- [ ] Le mot de passe initial n'a été écrit nulle part ; le nouveau n'est que dans Bitwarden
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien : le premier commit du projet est fait en **story 006**.

---

## 📝 Journal des décisions

Décisions prises le 25/10/2026, au cadrage de `tasks-fr 004` :

| # | Question | Réponse | Raison |
|---|----------|---------|--------|
| 1 | Dépôt | `1a` — `maven-releases` par défaut, politique vérifiée | il existe déjà, réglé pour des versions immuables |
| 2 | Mot de passe `nexus-deployer` | `2a` — uniquement dans Jenkins | il ne sert qu'au pipeline (même logique que `sonar-token`) |
| 3 | `check-nexus.sh` | `3a` — sans identifiants | Nexus Community n'a pas de jetons d'API |
| 4 | Publication témoin | `4a` — avec admin, republication refusée, puis suppression | prouve « release une seule fois » |
| 5 | Accès anonyme | `5a` — lecture activée | staging et production téléchargent sans secret |

Vérifié sur ton Nexus à la génération : édition **COMMUNITY 3.96.4** ; avant l'assistant, lecture
et écriture anonymes répondent `401`. Le script a révélé un piège : `docker compose exec` lit
l'entrée standard et peut « avaler » la suite d'un script ; d'où le `< /dev/null` du tutoriel 006.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Sécuriser Nexus au premier démarrage
- ✨ Ouvrir la lecture anonyme en gardant la publication fermée
- ✨ Prouver la fin d'un assistant par un fichier disparu

Prochain tutoriel : **#002** — Vérifier la politique de `maven-releases` ⏱️ 10 min

Pause méritée ☕
