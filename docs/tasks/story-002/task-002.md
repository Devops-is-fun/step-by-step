# Tutoriel 002 — Story #002 : Créer le compte administrateur

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐
**Dépend de :** Tutoriel 001
**Prérequis :** la page « Créer le 1er utilisateur administrateur » est ouverte ; ton gestionnaire de
mots de passe est ouvert
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `http://localhost:8080`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Créer le compte `admin` avec un mot de passe **généré et rangé dans ton gestionnaire**, puis terminer
l'assistant de Jenkins.

## 📋 Ce que tu vas apprendre

- Créer un mot de passe solide sans jamais le taper ni l'écrire dans un fichier
- Ce que règle l'écran « Configuration de l'instance »
- Prouver qu'un service est protégé : `403` sans identifiants, `200` avec

---

## 🪜 Les étapes

### Étape 1 : Prépare le mot de passe dans ton gestionnaire

**Où :** ton gestionnaire de mots de passe

**Action :**

> Crée une nouvelle entrée avec :
>
> | Champ | Valeur |
> |-------|--------|
> | Nom | `Jenkins local — TP CI/CD` |
> | Identifiant | `admin` |
> | URL | `http://localhost:8080` |
> | Mot de passe | **généré** par le gestionnaire, 20 caractères minimum |
>
> Enregistre l'entrée.

**Résultat attendu :** l'entrée apparaît dans ton gestionnaire.

**Pourquoi :** le gestionnaire **génère** le mot de passe et le **garde** pour toi. Il n'existe donc
dans aucun fichier du projet, pas même un `.env` ignoré (décision du 25/10/2026).

**✅ Vérifie que :**
- [ ] L'entrée est enregistrée **avant** de remplir le formulaire de Jenkins

---

### Étape 2 : Remplis le formulaire

**Où :** `http://localhost:8080`, page « Créer le 1er utilisateur administrateur »

**Action :**

> Remplis les champs :
>
> | Champ | Valeur |
> |-------|--------|
> | Nom d'utilisateur (Username) | `admin` |
> | Mot de passe (Password) | colle le mot de passe de ton gestionnaire |
> | Confirmation du mot de passe (Confirm password) | colle-le une deuxième fois |
> | Nom complet (Full name) | `Administrateur TP` |
> | Adresse courriel (E-mail address) | `admin@example.com` |
>
> Clique sur **« Sauver et continuer »** (« Save and Continue »).

**Résultat attendu :** la page **« Configuration de l'instance »** (« Instance Configuration »).

**Pourquoi `admin@example.com` ?** Jenkins exige une adresse au bon format. Le domaine `example.com`
est réservé aux exemples : aucune donnée personnelle n'est mise dans Jenkins.

**✅ Vérifie que :**
- [ ] La page « Configuration de l'instance » s'affiche

---

### Étape 3 : Valide l'URL de Jenkins

**Où :** `http://localhost:8080`, page « Configuration de l'instance »

**Action :**

> Vérifie que le champ **« URL de Jenkins »** (« Jenkins URL ») contient exactement
> `http://localhost:8080/`, puis clique sur **« Sauver et terminer »** (« Save and Finish »).

**Résultat attendu :** la page **« Jenkins est prêt ! »** (« Jenkins is ready! »).

**Pourquoi :** Jenkins utilise cette URL pour fabriquer les liens qu'il envoie ailleurs (statuts de
build sur GitHub, notifications). Une URL fausse donnerait des liens cassés.

**✅ Vérifie que :**
- [ ] La page « Jenkins est prêt ! » s'affiche

---

### Étape 4 : Entre dans Jenkins

**Où :** `http://localhost:8080`

**Action :**

> Clique sur **« Commencer à utiliser Jenkins »** (« Start using Jenkins »).

**Résultat attendu :** le tableau de bord de Jenkins, avec « Bienvenue sur Jenkins ! » (« Welcome to
Jenkins! ») et ton nom `Administrateur TP` en haut à droite.

**✅ Vérifie que :**
- [ ] Le tableau de bord s'affiche

---

### Étape 5 : Prouve que ton mot de passe fonctionne

**Où :** `http://localhost:8080`

**Action :**

> Déconnecte-toi (lien **« Se déconnecter »** / « log out » en haut à droite, parfois dans le menu
> sous ton nom), puis reconnecte-toi avec `admin` et le mot de passe **rempli par ton gestionnaire**.

**Résultat attendu :** tu retrouves le tableau de bord.

**✅ Vérifie que :**
- [ ] La connexion fonctionne avec le mot de passe du gestionnaire

---

### Étape 6 : Prouve que Jenkins est protégé

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> curl -s -o /dev/null -w "API sans identifiants : %{http_code}\n" http://localhost:8080/api/json
> ```

**Résultat attendu :** `API sans identifiants : 403`

**Pourquoi :** `403` veut dire « Interdit » : sans identifiants, Jenkins refuse de donner la moindre
information. Attention : Jenkins protège son API **dès le premier démarrage**, avant même
l'assistant ; ce `403` prouve que l'API est fermée, pas que le compte a été créé. La preuve du compte,
c'est la reconnexion de l'étape 5. Le `200` avec identifiants viendra au tutoriel 004, avec un jeton
d'API.

**✅ Vérifie que :**
- [ ] Le code est `403`

---

## 🧹 Comment tout annuler

Le compte créé ne peut pas être « dé-créé » simplement. Pour repartir d'un Jenkins vierge, suis la
section « Comment tout annuler » du tutoriel 001 (**irréversible**).

Pour changer seulement le mot de passe : `http://localhost:8080/me/account/` (ou
`http://localhost:8080/me/configure` selon la version), puis mets à jour ton gestionnaire.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| « Invalid e-mail address » | Vérifie qu'il n'y a pas d'espace autour de `admin@example.com` |
| « Password didn't match » | Recolle le mot de passe du gestionnaire dans les deux champs |
| L'URL de Jenkins affiche autre chose que `http://localhost:8080/` | Corrige-la à la main avant « Sauver et terminer » |
| Connexion refusée à l'étape 5 | Le gestionnaire a peut-être rempli un ancien mot de passe : vérifie l'entrée `Jenkins local — TP CI/CD` |
| `200` au lieu de `403` à l'étape 6 | L'accès anonyme a été ouvert : `Administrer Jenkins → Sécurité`, vérifie que les utilisateurs anonymes n'ont aucun droit |

---

## 📚 Aide-mémoire des commandes de vérification

| Besoin | Commande |
|--------|----------|
| Code HTTP de l'API sans identifiants | `curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/json` |
| Voir les conteneurs et leur état | `docker compose ps` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

### Ce que tu dois avoir fait

- [ ] Étape 1 → entrée créée dans le gestionnaire, mot de passe généré
- [ ] Étape 2 → formulaire du compte `admin` rempli
- [ ] Étape 3 → URL `http://localhost:8080/` validée
- [ ] Étape 4 → tableau de bord affiché
- [ ] Étape 5 → reconnexion réussie
- [ ] Étape 6 → API protégée (`403`)

### Ce que tu dois avoir vérifié

| Vérification | Comment vérifier | Attendu |
|--------------|------------------|---------|
| Le compte fonctionne | déconnexion puis reconnexion | tableau de bord |
| L'API est protégée | `curl` de l'étape 6 | `403` |

### 🛡️ Garde-fous du TP

- [ ] Le mot de passe admin n'existe **que** dans ton gestionnaire
- [ ] Aucun build ne s'est exécuté sur le controller Jenkins
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien : le premier commit du projet est fait en **story 006**.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Créer un compte avec un mot de passe que tu n'as jamais tapé
- ✨ Terminer l'assistant de Jenkins
- ✨ Prouver avec un code HTTP qu'un service est protégé

Prochain tutoriel : **#003** — Installer les quatre plugins complémentaires ⏱️ 20 min

Pause méritée ☕
