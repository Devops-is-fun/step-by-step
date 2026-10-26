# Tutoriel 004 — Story #002 : Créer un jeton d'API et le charger dans le terminal

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** connecté à Jenkins avec le compte `admin` ; gestionnaire de mots de passe ouvert
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `http://localhost:8080/me/security/`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Créer un **jeton d'API** pour les scripts, le ranger dans ton gestionnaire, puis le charger dans ton
terminal **sans qu'il s'affiche ni reste dans l'historique**.

## 📋 Ce que tu vas apprendre

- La différence entre un mot de passe et un jeton d'API
- Saisir un secret dans le terminal avec `read -s`
- Prouver qu'un accès authentifié fonctionne : `200` avec jeton, `403` sans

---

## 🪜 Les étapes

### Étape 1 : Génère le jeton

**Où :** `http://localhost:8080/me/security/` (navigateur)

**Action :**

> Ouvre cette URL. Dans la section **« Jeton d'API »** (« API Token »), clique sur **« Ajouter un
> nouveau jeton »** (« Add new Token »), nomme-le `scripts-tp`, puis clique sur **« Générer »**
> (« Generate »).

**Résultat attendu :** une ligne `scripts-tp` avec une valeur de **34 caractères**, et un
avertissement : le jeton ne sera **plus jamais affiché**.

**Pourquoi un jeton plutôt que le mot de passe ?** Un jeton sert à **un seul usage** (ici, les
scripts), se **révoque** sans toucher à ton mot de passe, et ne permet pas de se connecter à
l'interface. Si un script le laisse fuiter, tu le supprimes et tu en génères un autre.

**✅ Vérifie que :**
- [ ] Le jeton `scripts-tp` est affiché
- [ ] Tu ne fermes **pas** la page avant l'étape 2

---

### Étape 2 : Range le jeton dans ton gestionnaire

**Où :** ton gestionnaire de mots de passe

**Action :**

> Copie le jeton, puis crée une nouvelle entrée :
>
> | Champ | Valeur |
> |-------|--------|
> | Nom | `Jenkins local — jeton scripts-tp` |
> | Identifiant | `admin` |
> | Mot de passe | le jeton copié |
>
> Enregistre l'entrée. Si la page Jenkins affiche un bouton **« Enregistrer »** (« Save ») en bas,
> clique dessus.

**✅ Vérifie que :**
- [ ] L'entrée est enregistrée dans le gestionnaire
- [ ] Le jeton n'est écrit dans **aucun** fichier (ni notes, ni `.env`)

---

### Étape 3 : Charge l'identifiant dans le terminal

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> export JENKINS_USER=admin
> ```

**Résultat attendu :** aucune sortie.

---

### Étape 4 : Charge le jeton sans l'afficher

**Où :** le même terminal

**Action :**

> Exécute la commande ci-dessous, puis colle le jeton depuis ton gestionnaire et appuie sur
> **Entrée**. **Rien ne s'affiche pendant que tu colles : c'est voulu.**
>
> ```bash
> read -rs JENKINS_TOKEN && export JENKINS_TOKEN
> ```

**Résultat attendu :** l'invite de commande revient, sans rien afficher.

**Pourquoi `read -rs` ?** `-s` (*silent*) n'affiche pas ce que tu colles ; `-r` garde le texte tel
quel. Le jeton est tapé **en réponse** à `read`, pas dans la commande : il n'entre donc pas dans
l'historique du terminal. Un `export JENKINS_TOKEN=11ab…` l'y aurait laissé en clair.

**✅ Vérifie que :**
- [ ] Exécute `echo "${#JENKINS_TOKEN}"` : la sortie est `34` (la **longueur** du jeton, pas sa
      valeur)

---

### Étape 5 : Teste l'accès authentifié

**Où :** le même terminal

**Action :**

> Exécute :
>
> ```bash
> curl -s -o /dev/null -w "API avec jeton : %{http_code}\n" -u "$JENKINS_USER:$JENKINS_TOKEN" http://localhost:8080/api/json
> ```

**Résultat attendu :** `API avec jeton : 200`

**Pourquoi :** au tutoriel 002, la même URL renvoyait `403` sans identifiants. Avec le jeton, Jenkins
répond `200`. Ces deux codes ensemble forment la preuve « curl authentifié OK » de la story.

**✅ Vérifie que :**
- [ ] Le code est `200`

---

## ⚠️ À retenir pour la suite

Les variables `JENKINS_USER` et `JENKINS_TOKEN` n'existent **que dans ce terminal**. Si tu le fermes,
ou si tu en ouvres un autre, refais les étapes 3 et 4.

---

## 🧹 Comment tout annuler

1. Dans le terminal : `unset JENKINS_USER JENKINS_TOKEN`
2. Dans Jenkins, `http://localhost:8080/me/security/` : bouton de révocation (icône 🗑 ou
   « Révoquer ») sur la ligne `scripts-tp`
3. Dans ton gestionnaire : supprime l'entrée du jeton

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `http://localhost:8080/me/security/` renvoie une erreur 404 | Sur certaines versions, le jeton est dans `http://localhost:8080/me/configure` |
| `echo "${#JENKINS_TOKEN}"` affiche `0` | Le collage n'a pas marché : refais l'étape 4 |
| `echo "${#JENKINS_TOKEN}"` affiche `35` ou plus | Une espace s'est glissée dans le collage : refais l'étape 4 |
| `401` à l'étape 5 | Jeton ou identifiant faux : vérifie `echo "$JENKINS_USER"` (doit afficher `admin`), puis refais l'étape 4 |
| Jeton perdu (page fermée avant de le copier) | Révoque-le et génère-en un nouveau : un jeton ne se réaffiche jamais |

---

## 📚 Aide-mémoire des commandes de vérification

| Besoin | Commande |
|--------|----------|
| Recharger l'identifiant | `export JENKINS_USER=admin` |
| Recharger le jeton sans l'afficher | `read -rs JENKINS_TOKEN && export JENKINS_TOKEN` |
| Vérifier que le jeton est chargé | `echo "${#JENKINS_TOKEN}"` → `34` |
| Tester l'accès authentifié | `curl -s -o /dev/null -w "%{http_code}\n" -u "$JENKINS_USER:$JENKINS_TOKEN" http://localhost:8080/api/json` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

### Ce que tu dois avoir fait

- [ ] Étape 1 → jeton `scripts-tp` généré
- [ ] Étape 2 → jeton rangé dans le gestionnaire
- [ ] Étapes 3 et 4 → variables chargées dans le terminal
- [ ] Étape 5 → accès authentifié testé

### Ce que tu dois avoir vérifié

| Vérification | Comment vérifier | Attendu |
|--------------|------------------|---------|
| Jeton chargé | `echo "${#JENKINS_TOKEN}"` | `34` |
| Accès authentifié | `curl` de l'étape 5 | `200` |

### 🛡️ Garde-fous du TP

- [ ] Le jeton n'existe que dans le gestionnaire et dans la mémoire du terminal
- [ ] `history | tail -5` ne montre **pas** la valeur du jeton
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien : le premier commit du projet est fait en **story 006**.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Créer un jeton d'API révocable
- ✨ Saisir un secret dans le terminal sans laisser de trace
- ✨ Prouver un accès authentifié avec deux codes HTTP

Prochain tutoriel : **#005** — Écrire `scripts/check-jenkins.sh` ⏱️ 20 min

Pause méritée ☕
