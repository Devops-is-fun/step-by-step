# Tutoriel 004 — Story #032 : Protéger `main` et prévenir Morgan

**Pour :** toi
**Temps estimé :** ⏱️ 10 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 003
**Prérequis :** PR #2 mergée ; sur `main`, à jour
**Exigence TP couverte :** `EXG-COM-07`
**Fichier principal :** `https://github.com/GeraldineRouy/step-by-step/settings/rules`

> **Répertoire de travail :** la racine du projet (étape 3).

---

## 🎯 Objectif

Poser la **serrure** : plus personne ne pousse directement sur `main`, et rien n'y entre sans le ✅ de
Jenkins.

## 📋 Ce que tu vas apprendre

- Ce qu'est un **ruleset** GitHub
- Exiger un **status check** avant un merge

---

## 🪜 Les étapes

### Étape 1 : Crée la règle

**Où :** `https://github.com/GeraldineRouy/step-by-step` → **Settings → Rules → Rulesets → New ruleset → New branch ruleset**

**Action :**

> | Champ | Valeur |
> |-------|--------|
> | Ruleset Name | `protect-main` |
> | Enforcement status | **Active** |
> | Bypass list | vide |
> | Target branches | **Add target → Include default branch** |
>
> Dans **Rules**, coche :
>
> | Règle | Réglage |
> |-------|---------|
> | Restrict deletions | ☑ |
> | Require a pull request before merging | ☑, **Required approvals : 0** |
> | Require status checks to pass | ☑ → **Add checks** → tape `continuous-integration/jenkins/pr-merge` → choisis-le |
> | Block force pushes | ☑ |
>
> Laisse le reste décoché. Clique **Create**.

**Résultat attendu :** le ruleset `protect-main` apparaît, **Active**, sur `main`.

**Pourquoi 0 approbation ?** C'est votre décision (`1a`) : la relecture est une règle d'équipe écrite
dans `CONTRIBUTING.md`, mais elle ne bloque pas si l'un de vous est indisponible. Le ✅ de Jenkins,
lui, est **obligatoire**.

**Pourquoi `pr-merge` est proposé dans la liste ?** GitHub ne propose que les statuts **déjà vus**
sur le dépôt : ceux des PR #1 et #2.

**Si GitHub affiche « Upgrade to GitHub Pro or make this repository public »** : ton compte n'a pas
GitHub Pro, et la protection des dépôts privés est réservée aux comptes payants. Deux options :
active le **GitHub Student Developer Pack** (`https://education.github.com/pack`), qui inclut Pro,
puis recommence ; ou garde la règle comme **convention** (elle est déjà écrite dans
`CONTRIBUTING.md`) et passe directement à l'étape 4. Note ton cas : on le mentionnera dans le
rapport.

---

### Étape 2 : Vérifie la serrure

**Avant tout :** la liste **Settings → Rules → Rulesets** montre `protect-main` avec la mention
**Active**. Sinon, le push de test **partirait vraiment** sur `main`.

**Où :** la racine du projet

**Action :**

> ```bash
> git commit --allow-empty -m "test: direct push to main must be refused"
> git push
> ```

**Résultat attendu :** le push est **refusé** :

```
remote: error: GH013: Repository rule violations found for refs/heads/main.
remote: - Changes must be made through a pull request.
…
 ! [remote rejected] main -> main (push declined due to repository rule violations)
```

**Pourquoi un commit vide ?** Il ne change aucun fichier : c'est un test sans risque.

---

### Étape 3 : Retire le commit de test

**Où :** la racine du projet

**Action :**

> ```bash
> git reset --soft HEAD~1
> git status
> ```

**Résultat attendu :** `Your branch is up to date with 'origin/main'`, `nothing to commit`.

**Pourquoi `--soft` suffit ?** Il défait le commit sans toucher aux fichiers ; comme le commit était
vide, il ne reste rien.

**✅ Vérifie que :**
- [ ] Étape 2 : `rejected`
- [ ] Étape 3 : `up to date`

---

### Étape 4 : Préviens Morgan

**Où :** ta messagerie habituelle

**Action :**

> Envoie ce message à Morgan :
>
> > Le dépôt est prêt pour toi ! Commence par les tutoriels de la story 032, partie 2 :
> > `docs/tasks/story-032/task-005.md`, puis 006 et 007. Ils te font cloner le dépôt, activer le
> > contrôle anti-secrets, lancer l'application chez toi, puis faire ta première PR.
> > Avant ta première PR (tuto 007), préviens-moi : mon Jenkins doit être allumé pour qu'elle reçoive
> > son ✅. Les règles : `CONTRIBUTING.md`. La visite : `docs/team/onboarding.md`.
> > Rappel important : **aucun secret** (jeton, mot de passe, URL) dans un fichier ni dans ChatGPT ou
> > Claude.

---

## 🧹 Comment tout annuler

GitHub → **Settings → Rules → Rulesets** → `protect-main` → **Delete ruleset**.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `continuous-integration/jenkins/pr-merge` introuvable dans **Add checks** | Tape le début (`continuous`) ; sinon vérifie que la PR #2 a bien reçu son ✅ |
| L'étape 2 est **acceptée** (`main -> main`) | Le ruleset n'était pas enregistré ou pas **Active**. Le commit vide est sur `main` : **laisse-le** (le retirer demanderait un `push --force`). Termine l'étape 1, puis refais l'étape 2 avec un nouveau commit vide |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Où | Attendu |
|--------------|----|---------|
| Ruleset | GitHub → Settings → Rules | `protect-main`, **Active** |
| Push direct | étape 2 | `rejected` |
| Local propre | `git status` | `up to date`, rien à commiter |
| Morgan prévenu | ta messagerie | message envoyé |

---

## 🎉 Bravo !

`main` est protégée, et Morgan peut commencer. 🎉 **Jalon J2 atteint.** Ta partie de la story est
terminée : la suite, c'est Morgan (tutoriels 005 à 007), puis toi pour relire sa PR.

Prochain tutoriel **pour toi** : la story 011 (`/ tasks-fr 011`), pendant que Morgan fait les
tutoriels 005 et 006.
