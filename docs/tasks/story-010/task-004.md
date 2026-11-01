# Tutoriel 004 — Story #010 : Écrire le `Jenkinsfile` minimal et le pousser

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 003
**Prérequis :** `git status` propre (seulement `?? docs/tasks/story-010/` et les docs modifiées par Claude)
**Exigence TP couverte :** `EXG-COM-06`
**Fichier principal :** `Jenkinsfile` (racine du projet)

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Écrire la **recette** que Jenkins suivra pour chaque branche, et la pousser sur `main` **avant** de
créer le job.

## 📋 Ce que tu vas apprendre

- La structure d'un pipeline **déclaratif** : `agent`, `options`, `stages`, `steps`
- Pourquoi un job Multibranch a **besoin** d'un `Jenkinsfile` dans le dépôt

---

## 🪜 Les étapes

### Étape 1 : Crée le `Jenkinsfile`

**Où :** la racine du projet (à côté de `pom.xml`), dans IntelliJ

**Action :**

> Crée le fichier `Jenkinsfile` (sans extension) avec ce contenu **exact** :
>
> ```groovy
> // Step by Step CI pipeline. Story 010: minimal pipeline proving webhook -> agent -> Maven.
> // Story 011 adds the build, test and analysis stages.
> pipeline {
>     agent { label 'docker-agent' }
>
>     options {
>         timeout(time: 15, unit: 'MINUTES')
>         buildDiscarder(logRotator(numToKeepStr: '20'))
>     }
>
>     stages {
>         stage('Validate') {
>             steps {
>                 sh './mvnw -B validate'
>             }
>         }
>     }
> }
> ```
>
> **Enregistre.** Si IntelliJ demande **« Add file to Git? »** : **Cancel**.

| Bloc | Sens |
|------|------|
| `agent { label 'docker-agent' }` | construire **sur l'agent** (story 005), jamais sur le controller |
| `timeout(15 min)` | un build bloqué est tué au bout de 15 minutes, au lieu d'occuper l'agent pour toujours |
| `buildDiscarder(… '20')` | ne garder que les 20 derniers builds par branche (disque) |
| `stage('Validate')` | une étape nommée, visible dans l'interface |
| `sh './mvnw -B validate'` | Maven vérifie que le `pom.xml` est valide : rapide, et prouve que l'agent sait lancer Maven |

**Où est le `git clone` ?** Un job Multibranch récupère le code **tout seul** avant le premier stage
(étape *Declarative: Checkout SCM*).

**Pourquoi le pousser avant de créer le job ?** Le job ne retient que les branches qui
**contiennent** un `Jenkinsfile`. Sans lui, le scan dirait « 0 branche ».

---

### Étape 2 : Commite

**Où :** la racine du projet

**Action :**

> ```bash
> git add Jenkinsfile
> git status --short | grep '^[AMD]'
> git commit -m "ci(pipeline): add minimal jenkinsfile running on the docker agent"
> ```

**Résultat attendu :** `A  Jenkinsfile` ; puis `OK  no secret found…` et `1 file changed`.

---

### Étape 3 : Pousse

**Où :** la racine du projet

**Action :**

> ```bash
> git push
> ```

**Résultat attendu :** `main -> main`. **Rien** ne se passe dans Jenkins : il n'y a pas encore de
job, et pas encore de webhook. C'est normal.

---

## 🧹 Comment tout annuler

Avant le push : `git reset --soft HEAD~1`. Après : un commit qui supprime le fichier.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| IntelliJ nomme le fichier `Jenkinsfile.groovy` | Renomme-le en `Jenkinsfile`, sans extension : c'est le nom que Jenkins cherche |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Fichier sur GitHub | `git log --oneline -1 origin/main` | `ci(pipeline): add minimal jenkinsfile…` |

---

## 🎉 Bravo !

Ton premier `Jenkinsfile` ! 🎉 Le « pipeline as code » commence.

Prochain tutoriel : **#005** — Créer le job Multibranch `step-by-step` ⏱️ 20 min
