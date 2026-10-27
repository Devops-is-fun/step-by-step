# Tutoriel 005 — Story #007 : Écrire le `Dockerfile` et le `.dockerignore`

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** aucun conteneur à arrêter
**Exigence TP couverte :** `EXG-COM-12` (artefact déployable)
**Fichier principal :** `Dockerfile` (racine du projet)

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Écrire la recette de **l'emballage** de Step by Step : une image Docker qui construit le JAR, puis
ne garde que le strict nécessaire pour le faire tourner.

## 📋 Ce que tu vas apprendre

- Le principe du build **multi-étapes** (un atelier, puis un colis)
- Pourquoi l'ordre des `COPY` accélère les reconstructions
- Pourquoi l'application ne tourne **pas** en `root`

---

## 🪜 Les étapes

### Étape 1 : Crée le `Dockerfile`

**Où :** la racine du projet (à côté de `pom.xml`), dans IntelliJ

**Action :**

> Crée le fichier `Dockerfile` avec ce contenu **exact** :
>
> ```dockerfile
> # Step by Step application image (story 007): build with the Maven wrapper, run on a JRE only.
>
> # 1. Build stage: full JDK + the same Maven wrapper as on the laptop and the Jenkins agent
> FROM eclipse-temurin:17.0.20.1_1-jdk-noble AS build
> WORKDIR /workspace
> COPY mvnw pom.xml ./
> COPY .mvn .mvn
> # Dependencies first: this layer stays cached as long as pom.xml does not change
> RUN ./mvnw -B -q dependency:go-offline
> COPY src src
> # Tests already ran in "./mvnw verify" (laptop, then Jenkins): the image only packages
> RUN ./mvnw -B -q package -DskipTests
>
> # 2. Runtime stage: JRE only, no build tools, non-root user
> FROM eclipse-temurin:17.0.20.1_1-jre-noble
> RUN groupadd --system app && useradd --system --gid app app
> WORKDIR /app
> COPY --from=build /workspace/target/step-by-step-0.1.0-SNAPSHOT.jar app.jar
> USER app
> EXPOSE 8080
> ENTRYPOINT ["java", "-jar", "app.jar"]
> ```
>
> **Enregistre** (Ctrl+S). Si IntelliJ demande **« Add file to Git? »** : **Cancel**.

**Pourquoi deux `FROM` ?** Le premier est l'**atelier** : un JDK complet, Maven, les sources. Le
second est le **colis** : un JRE et le JAR, rien d'autre. Seul le colis est livré ; l'atelier est
jeté. Moins de choses dans l'image = moins de failles et moins de poids.

**Pourquoi `pom.xml` **avant** `src` ?** Docker garde chaque étape en cache tant que ce qu'elle
copie ne change pas. Tu modifies le code 100 fois plus souvent que le `pom.xml` : les dépendances
(la partie longue) ne sont retéléchargées que si le `pom.xml` change.

**Pourquoi `-DskipTests` ?** Les tests tournent **avant**, dans `./mvnw verify`. Les relancer dans
l'image doublerait la durée sans rien prouver de plus.

**Pourquoi un utilisateur `app` ?** Si l'application était piratée, l'attaquant serait `app`, pas
`root` : il ne pourrait pas modifier le système de l'image.

**Pourquoi des versions exactes (`17.0.20.1_1`) ?** Comme pour Jenkins, SonarQube et Nexus : la même
recette donne **la même image** demain.

---

### Étape 2 : Crée le `.dockerignore`

**Où :** la racine du projet

**Action :**

> Crée le fichier `.dockerignore` avec ce contenu **exact** :
>
> ```
> # Keep the Docker build context small and free of local files (story 007)
> target/
> .git/
> .idea/
> *.iml
> docs/
> .env
> .env.*
> ```

**Pourquoi ?** `docker build .` envoie **tout le dossier** au moteur Docker (le *contexte*). Sans ce
fichier, il enverrait l'historique Git, le PDF de l'énoncé, ton `target/`… et un éventuel `.env`.
C'est le `.gitignore` de Docker.

---

### Étape 3 : Vérifie la syntaxe sans construire

**Où :** la racine du projet

**Action :**

> ```bash
> docker build --check .
> ```

**Résultat attendu :** `Check complete, no warnings found.`

**Pourquoi ?** `--check` relit la recette et signale les erreurs courantes, en une seconde, sans rien
télécharger.

**✅ Vérifie que :**
- [ ] Les deux fichiers sont à la racine, à côté de `pom.xml`
- [ ] `no warnings found`

---

## 🧹 Comment tout annuler

```bash
rm Dockerfile .dockerignore
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `Dockerfile parse error` | Copie de nouveau le bloc : un caractère `>` du tutoriel a pu être collé en début de ligne |
| Un *warning* `JSONArgsRecommended` | La ligne `ENTRYPOINT` doit avoir les crochets et guillemets exacts |
| `unknown flag: --check` | Docker trop ancien : `docker version` doit afficher 29.x |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Recette valide | `docker build --check .` | `no warnings found` |
| Fichiers présents | `git status --short Dockerfile .dockerignore` | 2 lignes `??` |

---

## 🎉 Bravo !

Tu as écrit ta première recette d'image **multi-étapes**. 🎉

Prochain tutoriel : **#006** — Construire l'image et la tester ⏱️ 15 min
