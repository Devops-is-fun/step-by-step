# Tutoriel 003 — Story #007 : Ajouter le bloc Sonar au `pom.xml`

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** `./mvnw -B verify` vert
**Exigence TP couverte :** prépare `EXG-COM-08` (analyse statique)
**Fichier principal :** `pom.xml`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Dire à Maven **quel projet** SonarQube analyser, **où** est le serveur, et **quelle version** du
scanner utiliser.

## 📋 Ce que tu vas apprendre

- La différence entre `<properties>` (des réglages) et `<pluginManagement>` (des versions d'outils)
- Pourquoi on **épingle** la version d'un plugin
- Pourquoi le jeton n'apparaît **jamais** dans le `pom.xml`

---

## 🪜 Les étapes

### Étape 1 : Ajoute les deux propriétés Sonar

**Où :** `pom.xml`, dans IntelliJ

**Action :**

> Dans le bloc `<properties>`, juste **après** la ligne `<maven.compiler.release>17</maven.compiler.release>`,
> ajoute :
>
> ```xml
>
>         <!-- SonarQube analysis (story 007): the token is passed on the command line, never stored here -->
>         <sonar.projectKey>step-by-step</sonar.projectKey>
>         <sonar.host.url>http://localhost:9000</sonar.host.url>
> ```

**Résultat attendu :** le bloc `<properties>` contient 4 propriétés : `java.version`,
`maven.compiler.release`, `sonar.projectKey`, `sonar.host.url`.

**Pourquoi ici et pas dans `sonar-project.properties` ?** Le scanner **Maven** lit le `pom.xml` et
ignore ce fichier (décision de la story 003).

**Pourquoi `localhost:9000` ?** C'est l'adresse vue **depuis ton poste**. Dans Jenkins (story 013),
l'agent passera une autre adresse en ligne de commande : une option `-D` **écrase** la propriété.

---

### Étape 2 : Épingle la version du scanner

**Où :** `pom.xml`

**Action :**

> Dans le bloc `<build>`, juste **avant** la ligne `<plugins>`, ajoute :
>
> ```xml
>         <pluginManagement>
>             <plugins>
>                 <plugin>
>                     <groupId>org.sonarsource.scanner.maven</groupId>
>                     <artifactId>sonar-maven-plugin</artifactId>
>                     <version>5.8.0.7211</version>
>                 </plugin>
>             </plugins>
>         </pluginManagement>
> ```

**Résultat attendu :** `<build>` contient **d'abord** `<pluginManagement>`, **puis** `<plugins>`
(avec `spring-boot-maven-plugin`).

**Pourquoi épingler ?** Sans version, Maven prend **la dernière publiée** : le même commit pourrait
analyser différemment demain, ou casser. `5.8.0.7211` a été testée avec ton SonarQube 9.9.8.

**Pourquoi `pluginManagement` et pas `plugins` ?** `plugins` **branche** un outil sur chaque build.
`pluginManagement` dit seulement « **si** on l'appelle, prends cette version » : le scanner ne tourne
que quand tu le demandes (tutoriel 004).

**✅ Vérifie que :**
- [ ] Aucun jeton (`squ_…`, `sqp_…`) dans le fichier

---

### Étape 3 : Vérifie que Maven lit bien la configuration

**Où :** la racine du projet

**Action :**

> ```bash
> ./mvnw -B -q help:evaluate -Dexpression=sonar.projectKey -DforceStdout; echo
> ./mvnw -B -q validate && echo "pom OK"
> ```

**Résultat attendu :**

```
step-by-step
pom OK
```

**Pourquoi `help:evaluate` ?** Il demande à Maven « quelle valeur **toi**, tu vois ? ». C'est la
preuve que ta propriété est lue, sans lancer d'analyse.

---

## 🧹 Comment tout annuler

Retire les lignes ajoutées aux étapes 1 et 2 (Ctrl+Z dans IntelliJ).

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `Non-parseable POM` / `Unrecognised tag` | Une balise mal fermée ou au mauvais endroit : compare l'indentation avec les blocs voisins |
| `help:evaluate` affiche `null object or invalid expression` | La propriété n'est pas dans `<properties>` : refais l'étape 1 |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Clé lue par Maven | `./mvnw -B -q help:evaluate -Dexpression=sonar.projectKey -DforceStdout` | `step-by-step` |
| POM valide | `./mvnw -B -q validate` | aucune erreur |
| Pas de secret | `grep -cE 'sq[apu]_' pom.xml` | `0` |

---

## 🎉 Bravo !

Ton premier réglage CI/CD **dans le code** de l'application. 🎉

Prochain tutoriel : **#004** — Lancer la première analyse SonarQube ⏱️ 15 min
