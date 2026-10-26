# Tutoriel 001 — Story #003 : Changer le mot de passe admin

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐
**Dépend de :** story 002 terminée
**Prérequis :** `sonarqube` est `healthy` (`docker compose ps`) ; Bitwarden ouvert
**Exigence TP couverte :** `EXG-COM-19` (part)
**Fichier principal :** `http://localhost:9000`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Remplacer le mot de passe par défaut `admin/admin` de SonarQube par un mot de passe généré et rangé
dans Bitwarden, puis prouver que `admin/admin` ne fonctionne plus.

## 📋 Ce que tu vas apprendre

- Pourquoi un identifiant par défaut est public, donc dangereux
- Prouver un refus d'authentification avec l'API
- Lire la **réponse** d'une API quand le code HTTP ne suffit pas

---

## 🪜 Les étapes

### Étape 1 : Constate que `admin/admin` fonctionne encore

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> curl -s -u admin:admin http://localhost:9000/api/authentication/validate; echo
> ```

**Résultat attendu :** `{"valid":true}`

**Pourquoi écrire `admin:admin` en clair ici ?** Ce n'est pas un secret : c'est le mot de passe
**livré avec le produit**, écrit dans sa documentation publique. N'importe qui le connaît, et c'est
justement pour ça qu'il faut le changer.

**✅ Vérifie que :**
- [ ] La réponse est `{"valid":true}`

---

### Étape 2 : Prépare le mot de passe dans Bitwarden

**Où :** Bitwarden

**Action :**

> Crée une entrée de type **Identifiant** :
>
> | Champ | Valeur |
> |-------|--------|
> | Nom | `SonarQube local — TP CI/CD` |
> | Nom d'utilisateur | `admin` |
> | URL | `http://localhost:9000` |
> | Mot de passe | **généré** (icône 🔄), 20 caractères minimum |
>
> Enregistre l'entrée.

**✅ Vérifie que :**
- [ ] L'entrée est enregistrée **avant** de toucher à SonarQube

---

### Étape 3 : Connecte-toi avec `admin/admin`

**Où :** `http://localhost:9000` (navigateur)

**Action :**

> Ouvre `http://localhost:9000`, saisis `admin` dans **Login** et `admin` dans **Password**, puis
> clique sur **Log in**.

**Résultat attendu :** la page **« Update your password »**. SonarQube 9.9 **oblige** à changer le
mot de passe par défaut à la première connexion.

**✅ Vérifie que :**
- [ ] La page « Update your password » s'affiche

---

### Étape 4 : Change le mot de passe

**Où :** la page « Update your password »

**Action :**

> Remplis :
>
> | Champ | Valeur |
> |-------|--------|
> | Old Password | `admin` |
> | New Password | colle le mot de passe de Bitwarden |
> | Confirm Password | colle-le une deuxième fois |
>
> Clique sur **Update**.

**Résultat attendu :** la page d'accueil de SonarQube, avec « How do you want to create your
project? ».

**✅ Vérifie que :**
- [ ] Tu es connectée et la page d'accueil s'affiche
- [ ] Tu ne crées **pas** encore de projet : c'est le tutoriel 002

---

### Étape 5 : Prouve que `admin/admin` est refusé

**Où :** la racine du projet

**Action :**

> Relance la commande de l'étape 1 :
>
> ```bash
> curl -s -u admin:admin http://localhost:9000/api/authentication/validate; echo
> ```

**Résultat attendu :** `{"valid":false}`

**Pourquoi regarder la réponse et pas le code HTTP ?** Cette URL répond **toujours** `200`, que les
identifiants soient bons ou non. Seul le champ `valid` dit la vérité. C'est un piège classique :
`curl -f` ne verrait aucune erreur ici. Le script du tutoriel 006 lit donc la réponse.

**✅ Vérifie que :**
- [ ] La réponse est `{"valid":false}`

---

## 🧹 Comment tout annuler

Pour changer de nouveau le mot de passe : avatar en haut à droite → **My Account** → **Security** →
**Change password**, puis mets à jour Bitwarden. Revenir à `admin/admin` n'a aucun intérêt.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Pas de page « Update your password » après la connexion | Le mot de passe a déjà été changé : connecte-toi avec celui de Bitwarden, ou cherche-le dans tes notes de la story 001 |
| « Password must be different from the old one » | Le gestionnaire a peut-être collé `admin` : recolle le mot de passe généré |
| `{"valid":true}` à l'étape 5 | Le changement n'a pas été enregistré : reprends l'étape 3 |
| `curl: (7) Failed to connect` | SonarQube ne tourne pas : `docker compose ps`, puis `docker compose logs --tail=50 sonarqube` |

---

## 📚 Aide-mémoire des commandes de vérification

| Besoin | Commande |
|--------|----------|
| Tester des identifiants SonarQube | `curl -s -u <login>:<mot de passe> http://localhost:9000/api/authentication/validate` |
| État de SonarQube | `curl -s http://localhost:9000/api/system/status` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

### Ce que tu dois avoir fait

- [ ] Étape 1 → constaté `{"valid":true}` avec `admin/admin`
- [ ] Étape 2 → entrée Bitwarden créée
- [ ] Étapes 3 et 4 → mot de passe changé
- [ ] Étape 5 → `admin/admin` refusé

### Ce que tu dois avoir vérifié

| Vérification | Comment vérifier | Attendu |
|--------------|------------------|---------|
| Ancien mot de passe refusé | `curl` de l'étape 5 | `{"valid":false}` |
| Nouveau mot de passe accepté | connexion dans le navigateur | page d'accueil |

### 🛡️ Garde-fous du TP

- [ ] Le nouveau mot de passe n'existe que dans Bitwarden
- [ ] Aucun build ne s'est exécuté sur le controller Jenkins
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien : le premier commit du projet est fait en **story 006**.

---

## 📝 Journal des décisions

Décisions prises le 25/10/2026, au cadrage de `tasks-fr 003` :

| # | Question | Réponse | Raison |
|---|----------|---------|--------|
| 1 | Configuration de l'analyse | `1a` — dans le `pom.xml`, pas de `sonar-project.properties` | le scanner Maven ignore ce fichier |
| 2 | Première analyse | `2a` — déplacée en story 007 | on analyse le vrai code, rien de jetable |
| 3 | Jetons | `3a` — analyse de projet pour Jenkins (`sonar-token`), utilisateur pour le script (`SONAR_TOKEN`) | moindre privilège |
| 4 | Quality Gate | `4a` — `step-by-step-gate`, couverture du nouveau code à 60 % | *Sonar way* exige 80 %, la spec fixe 60 % |
| 5 | Langue des scripts | `5a` — messages en anglais, `check-jenkins.sh` aligné | cohérent avec la doc en anglais |

Vérifié sur ton SonarQube à la génération : *Sonar way* contient bien la condition
`new_coverage < 80` ; `api/authentication/validate` répond `200` même avec un faux jeton.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Neutraliser un identifiant par défaut
- ✨ Prouver un refus d'authentification
- ✨ Lire la réponse d'une API quand le code HTTP ment

Prochain tutoriel : **#002** — Créer le projet `step-by-step` ⏱️ 10 min

Pause méritée ☕
