# Tutoriel 007 — Story #006 : Commits des stories 001 à 003

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 006
**Prérequis :** premier commit fait
**Exigence TP couverte :** `EXG-REND-01`
**Fichier principal :** l'historique Git

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Enregistrer le travail des stories 001 (stack), 002 (Jenkins) et 003 (SonarQube) : un commit par
story, chacun avec ses seuls fichiers.

---

## 🪜 Les étapes

### Étape 1 : Deux retouches avant de commiter `check-jenkins.sh`

**Où :** `scripts/check-jenkins.sh` et `docs/tooling/jenkins-configuration.md`

**Action :**

> 1. Dans `scripts/check-jenkins.sh`, corrige `complient` en `compliant` (ligne `echo "Jenkins is …"`),
>    et `JENKINS8TOKEN` en `JENKINS_TOKEN` (commentaire du début). **Enregistre** (Ctrl+S).
> 2. Dans `docs/tooling/jenkins-configuration.md`, ajoute cette ligne **à la fin** du tableau de la
>    section `## 3. Plugins` :
>
>    ```markdown
>    | `locale` | added | force the English UI: Manage Jenkins -> Appearance, Default Language `English`, browser preference ignored |
>    ```

**Pourquoi maintenant ?** Un commit est **définitif** une fois poussé : on corrige avant d'enregistrer.

**✅ Vérifie que :**
- [ ] `grep -c complient scripts/check-jenkins.sh` affiche `0`

---

### Étape 2 : Commit de la story 001

**Où :** la racine du projet

**Action :**

> ```bash
> git add docker-compose.yml README.md README.en.md
> git status --short | grep -v '^??'
> git commit -m "build(tooling): start jenkins, sonarqube and nexus from one compose file"
> ```

**Résultat attendu :** 3 lignes `A ` ; puis `OK  no secret found…` et `3 files changed`.

**À savoir :** `docker-compose.yml` contient **déjà** le service `jenkins-agent` de la story 005.
On l'assume : on enregistre le fichier tel qu'il est aujourd'hui. Découper un fichier entre deux
commits est possible (`git add -p`), mais n'apporterait rien ici.

---

### Étape 3 : Commit de la story 002

**Où :** la racine du projet

**Action :**

> ```bash
> git add scripts/check-jenkins.sh docs/tooling/jenkins-configuration.md
> git status --short | grep -v '^??'
> git commit -m "ci(jenkins): add jenkins check script and configuration docs"
> ```

**Résultat attendu :** 2 lignes `A ` ; puis `OK  no secret found…` et `2 files changed`.

**Pourquoi le vigile ne voit-il rien dans `check-jenkins.sh` ?** `${JENKINS_TOKEN:?…}` est une
**référence** (la valeur commence par `?`), pas une valeur. Le filet de `grep` des stories
précédentes, lui, la signalait comme fausse alerte.

---

### Étape 4 : Commit de la story 003

**Où :** la racine du projet

**Action :**

> ```bash
> git add scripts/check-sonarqube.sh docs/tooling/sonarqube.md
> git status --short | grep -v '^??'
> git commit -m "ci(sonar): add sonarqube check script and documentation"
> ```

**Résultat attendu :** 2 lignes `A ` ; puis `OK  no secret found…` et `2 files changed`.

---

### Étape 5 : Vérifie l'historique

**Où :** la racine du projet

**Action :**

> ```bash
> git log --oneline
> ```

**Résultat attendu :** 4 lignes, la plus récente en haut :

```
xxxxxxx ci(sonar): add sonarqube check script and documentation
xxxxxxx ci(jenkins): add jenkins check script and configuration docs
xxxxxxx build(tooling): start jenkins, sonarqube and nexus from one compose file
xxxxxxx chore(repo): initialise version control hygiene and secret scan
```

---

## 🧹 Comment tout annuler

Le dernier commit seulement (les fichiers restent dans l'index) : `git reset --soft HEAD~1`. **Tant
que rien n'est poussé.**

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Une ligne `A ` en trop avant un commit | `git restore --staged <fichier>` |
| Faute dans un message, juste après le commit | `git commit --amend -m "bon message"` (avant le push seulement) |
| Le vigile répond `KO` | Lis la ligne signalée ; si c'est une vraie valeur, retire-la et refais `git add` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Historique | `git log --oneline` | 4 commits |
| Rien d'oublié dans le carton | `git status --short \| grep -v '^??'` | vide |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Trois stories enregistrées, chacune avec sa ligne dans le registre.

Prochain tutoriel : **#008** — Commits de la story 004, de la story 005 et des docs ⏱️ 20 min

Pause méritée ☕
