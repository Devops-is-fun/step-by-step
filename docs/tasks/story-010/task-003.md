# Tutoriel 003 — Story #010 : Créer le jeton GitHub et la credential `github-token`

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** connectée à ton compte GitHub **de l'école** sur github.com
**Exigence TP couverte :** `EXG-COM-06`, `EXG-COM-07`
**Fichier principal :** `https://github.com/settings/personal-access-tokens`

> **Répertoire de travail :** aucun (navigateur seulement).

---

## 🎯 Objectif

Donner à Jenkins une clé GitHub qui n'ouvre **qu'une porte** : lire le dépôt `step-by-step` et y
écrire le statut des builds.

## 📋 Ce que tu vas apprendre

- La différence entre un jeton **classic** et **fine-grained**
- Le principe du **moindre privilège**

---

## 🪜 Les étapes

### Étape 1 : Crée le jeton

**Où :** `https://github.com/settings/personal-access-tokens/new`

**Action :**

> | Champ | Valeur |
> |-------|--------|
> | Token name | `jenkins-step-by-step` |
> | Description | `Local Jenkins: scan the repository and publish build statuses` |
> | Resource owner | ton compte d'école |
> | Expiration | **90 days** |
> | Repository access | **Only select repositories** → `step-by-step` |
>
> Puis, dans **Permissions → Repository permissions** :
>
> | Permission | Niveau |
> |------------|--------|
> | Commit statuses | **Read and write** |
> | Contents | **Read-only** |
> | Metadata | **Read-only** (imposé) |
> | Pull requests | **Read-only** |
>
> Laisse **toutes** les autres permissions sur *No access*. Clique **Generate token**.

**Résultat attendu :** un jeton qui commence par `github_pat_`, affiché **une seule fois**.

**Pourquoi *fine-grained* ?** Un jeton *classic* avec le droit `repo` ouvre **tous** tes dépôts,
en lecture **et** écriture. Celui-ci n'ouvre que `step-by-step`, et ne peut **ni pousser du code, ni
modifier les réglages**. Si quelqu'un le volait, il ne pourrait presque rien faire.

| Permission | Pourquoi Jenkins en a besoin |
|------------|------------------------------|
| Contents (lecture) | récupérer le code et lister les branches |
| Pull requests (lecture) | lister les PR à construire |
| Commit statuses (écriture) | afficher ✅/❌ sur les commits et les PR |

---

### Étape 2 : Range le jeton dans Bitwarden

**Où :** Bitwarden

**Action :**

> Crée une entrée **Login** :
>
> | Champ | Valeur |
> |-------|--------|
> | Nom | `GitHub — jenkins-step-by-step (fine-grained)` |
> | Identifiant | ton nom de compte GitHub d'école |
> | Mot de passe | le jeton `github_pat_…` |
> | Notes | `Expire le <date affichée par GitHub>` |
>
> **Enregistre.**

**Pourquoi noter l'expiration ?** Dans 90 jours, les builds échoueront avec `Bad credentials`. La
date te dira tout de suite pourquoi. Le renouvellement est décrit au tutoriel 008.

---

### Étape 3 : Crée la credential `github-token`

**Où :** `http://localhost:8080` → **Manage Jenkins → Credentials → System → Global credentials
(unrestricted)** → **Add Credentials**

**Action :**

> | Champ | Valeur |
> |-------|--------|
> | Kind | `Username with password` |
> | Scope | `Global` |
> | Username | ton nom de compte GitHub d'école |
> | Treat username as secret | ☐ décoché |
> | Password | le jeton `github_pat_…` (depuis Bitwarden) |
> | ID | `github-token` |
> | Description | `GitHub fine-grained token: step-by-step only (story 010)` |
>
> Clique **Create**.

**Pourquoi *Username with password* et pas *Secret text* ?** Le plugin *GitHub Branch Source* (le
job Multibranch) n'accepte **que** ce type pour se connecter à un dépôt. Le jeton prend la place du
mot de passe. (La spec disait *Secret text* : elle est corrigée.)

**✅ Vérifie que :**
- [ ] Jenkins → Credentials montre `github-token` **et** `github-webhook-secret`
- [ ] Le jeton n'est écrit **nulle part** ailleurs que GitHub (qui ne le montre plus), Bitwarden
  et Jenkins

---

## 🧹 Comment tout annuler

1. Jenkins → Credentials : supprime `github-token`.
2. GitHub → **Settings → Developer settings → Personal access tokens → Fine-grained tokens** →
   `jenkins-step-by-step` → **Delete**.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `step-by-step` absent de la liste des dépôts | Le **Resource owner** n'est pas ton compte d'école |
| Tu as fermé la page avant de copier le jeton | Supprime-le et recommence l'étape 1 : GitHub ne le remontre jamais |
| « Commit statuses » introuvable | Déplie **Repository permissions** ; la liste est alphabétique |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Où | Attendu |
|--------------|----|---------|
| Jeton limité | GitHub → Fine-grained tokens | `jenkins-step-by-step`, 1 dépôt |
| Rangé | Bitwarden | entrée avec la date d'expiration |
| Credential | Jenkins → Credentials | `github-token` (*Username with password*) |

---

## 🎉 Bravo !

Les deux clés sont rangées, chacune avec le **strict minimum** de droits. 🎉 **Jalon J2 atteint.**

Bonne coupure ☕ (≈ 55 min).

Prochain tutoriel : **#004** — Écrire le `Jenkinsfile` minimal et le pousser ⏱️ 15 min
