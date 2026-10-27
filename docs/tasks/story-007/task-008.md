# Tutoriel 008 — Story #007 : Compléter le README (FR + EN)

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐
**Dépend de :** Tutoriel 006
**Prérequis :** l'application démarre en local et en conteneur
**Exigence TP couverte :** `EXG-REND-01` (documentation)
**Fichier principal :** `README.md` et `README.en.md`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Qu'une personne qui clone le dépôt sache **construire et lancer** Step by Step sans te demander.

---

## 🪜 Les étapes

### Étape 1 : Corrige les prérequis (FR)

**Où :** `README.md`, section `## Prérequis`

**Action :**

> Remplace la ligne :
>
> ```markdown
> - Java 17 et Maven 3.9 (stories 008 et 011)
> ```
>
> par :
>
> ```markdown
> - Un JDK 17 ou plus récent (le code est compilé en Java 17)
> - Maven n'est **pas** nécessaire : le wrapper `./mvnw` télécharge la bonne version
> ```

---

### Étape 2 : Ajoute la section « Lancer l'application en local » (FR)

**Où :** `README.md`, juste **avant** `## Suite du projet`

**Action :**

> Ajoute :
>
> ````markdown
> ## Lancer l'application en local
>
> ```bash
> ./mvnw -B verify                                                    # compiler, tester, emballer
> java -jar target/step-by-step-0.1.0-SNAPSHOT.jar --server.port=8090
> curl localhost:8090/actuator/health                                 # {"status":"UP", …}
> ```
>
> Ou en conteneur :
>
> ```bash
> docker build -t step-by-step:0.1.0-SNAPSHOT .
> docker run -d --name step-by-step-local -p 127.0.0.1:8090:8080 step-by-step:0.1.0-SNAPSHOT
> ```
>
> Le port **8090** évite le conflit avec Jenkins (8080) et Nexus (8081).
>
> ````

---

### Étape 3 : Les mêmes changements en anglais

**Où :** `README.en.md`

**Action :**

> 1. Dans `## Prerequisites`, remplace `- Java 17 and Maven 3.9` par :
>
>    ```markdown
>    - A JDK 17 or newer (the code is compiled for Java 17)
>    - Maven is **not** required: the `./mvnw` wrapper downloads the right version
>    ```
>
> 2. Juste **avant** `## What's next`, ajoute :
>
>    ````markdown
>    ## Run the application locally
>
>    ```bash
>    ./mvnw -B verify                                                    # compile, test, package
>    java -jar target/step-by-step-0.1.0-SNAPSHOT.jar --server.port=8090
>    curl localhost:8090/actuator/health                                 # {"status":"UP", …}
>    ```
>
>    Or as a container:
>
>    ```bash
>    docker build -t step-by-step:0.1.0-SNAPSHOT .
>    docker run -d --name step-by-step-local -p 127.0.0.1:8090:8080 step-by-step:0.1.0-SNAPSHOT
>    ```
>
>    Port **8090** avoids clashing with Jenkins (8080) and Nexus (8081).
>
>    ````

**✅ Vérifie que :**
- [ ] `grep -c 8090 README.md README.en.md` affiche `4` pour chacun

---

## 🧹 Comment tout annuler

```bash
git restore README.md README.en.md
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| L'aperçu Markdown d'IntelliJ « casse » après ton ajout | Un bloc ```` ``` ```` n'est pas refermé : compte-les, il en faut un nombre pair |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Port documenté | `grep -c 8090 README.md README.en.md` | `4` et `4` |
| Plus de « Maven 3.9 » en prérequis | `grep -c 'Maven 3.9' README.md README.en.md` | `0` et `0` |

---

## 🎉 Bravo !

Ton README sait maintenant expliquer l'application, en deux langues. 🎉

Prochain tutoriel : **#009** — Commiter, pousser et faire le bilan ⏱️ 15 min
