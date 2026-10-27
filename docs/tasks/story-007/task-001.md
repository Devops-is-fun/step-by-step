# Tutoriel 001 — Story #007 : Découvrir le squelette et le construire

**Qui :** 🤖 Claude a implémenté — tu découvres et tu vérifies
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐
**Dépend de :** story 006 terminée
**Prérequis :** aucun (Maven n'a **pas** besoin d'être installé)
**Exigence TP couverte :** support applicatif
**Fichier principal :** `pom.xml`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Repérer les fichiers que Claude a écrits, puis construire l'application avec **une seule commande**.

## 📋 Ce que tu vas apprendre

- À quoi sert le **wrapper Maven** (`./mvnw`)
- Lire la version Java **cible** dans un `pom.xml`
- Vérifier qu'un `.class` est bien du Java 17, même compilé par ton JDK 25

---

## 🪜 Les étapes

### Étape 1 : Fais le tour des nouveaux fichiers

**Où :** la racine du projet

**Action :**

> ```bash
> git status --short
> ```

**Résultat attendu :** 6 lignes `??` :

```
?? .gitattributes
?? .mvn/
?? mvnw
?? mvnw.cmd
?? pom.xml
?? src/
```

| Fichier | Rôle |
|---------|------|
| `pom.xml` | la **recette** : nom, version, dépendances (web + actuator + tests) |
| `mvnw`, `mvnw.cmd`, `.mvn/wrapper/` | le **wrapper** : télécharge Maven 3.9.16 tout seul (Linux / Windows) |
| `.gitattributes` | garde `mvnw` en fins de ligne Linux et `mvnw.cmd` en fins de ligne Windows |
| `src/main/java/…/StepByStepApplication.java` | le point de départ de l'application |
| `src/main/resources/application.yml` | la configuration : nom de l'app, seul endpoint `health` exposé |
| `src/test/java/…/StepByStepApplicationTests.java` | le test `contextLoads` : « l'application sait démarrer » |

**Pourquoi un wrapper ?** C'est un **mode d'emploi qui apporte son propre outil**. Ton poste, l'agent
Jenkins et l'image Docker utiliseront **exactement** le même Maven, sans rien installer.

---

### Étape 2 : Repère la version Java cible

**Où :** `pom.xml`, dans IntelliJ

**Action :**

> Ouvre `pom.xml` et repère le bloc `<properties>`.

**Résultat attendu :**

```xml
<java.version>17</java.version>
<!-- Compiles Java 17 bytecode even with a newer local JDK -->
<maven.compiler.release>17</maven.compiler.release>
```

**Pourquoi ?** Ton JDK local est un **25** ; l'agent Jenkins et l'image tournent en **17**.
`release 17` dit au compilateur : « produis du Java 17, et refuse les API plus récentes ».

**Si IntelliJ propose « Load Maven project » :** clique dessus. S'il propose **« Add file to Git? »** :
**Cancel**.

---

### Étape 3 : Construis

**Où :** la racine du projet

**Action :**

> ```bash
> ./mvnw -B verify
> ```

**Résultat attendu :** au **premier** lancement, 1 à 2 minutes de `Downloading from central…`, puis :

```
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
…
BUILD SUCCESS
Total time:  …
```

**Les `WARNING` sur Mockito et « A Java agent has been loaded dynamically »** sont normaux avec un
JDK 25 : ce sont des avertissements pour de futures versions de Java, pas des erreurs.

**Pourquoi `verify` ?** C'est l'étape Maven qui **compile, teste et emballe** (le JAR). C'est la
commande que Jenkins lancera.

**✅ Vérifie que :**
- [ ] `BUILD SUCCESS`
- [ ] `Tests run: 1, Failures: 0`

---

### Étape 4 : Vérifie le binaire

**Où :** la racine du projet

**Action :**

> ```bash
> ls target/*.jar
> javap -v -cp target/classes com.esgi.stepbystep.StepByStepApplication | grep major
> ```

**Résultat attendu :**

```
target/step-by-step-0.1.0-SNAPSHOT.jar
  major version: 61
```

**Pourquoi 61 ?** C'est le **numéro de Java 17** dans un fichier `.class` (Java 25 = 69). La preuve
que `release 17` fonctionne.

---

## 🧹 Comment tout annuler

```bash
./mvnw -B clean
```

(Supprime `target/` ; le code n'est pas touché.)

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `./mvnw: Permission non accordée` | `chmod +x mvnw` |
| `Could not transfer artifact` / pas de réseau | Vérifie ta connexion ; relance la même commande (les téléchargements reprennent) |
| `BUILD FAILURE` dans les tests | Copie-moi les 30 dernières lignes : c'est le code de Claude, c'est à moi de corriger |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Build | `./mvnw -B verify` | `BUILD SUCCESS` |
| Nom du JAR | `ls target/*.jar` | `target/step-by-step-0.1.0-SNAPSHOT.jar` |
| Java 17 | `javap … \| grep major` | `major version: 61` |

### 🛡️ Garde-fous du TP

- [ ] Aucun secret écrit dans un fichier
- [ ] `target/` n'apparaît **pas** dans `git status` (il est ignoré)

---

## Journal des décisions

Réponses de cadrage `tasks-fr 007` (26/10/2026) : **1a, 2a, 3a, 4a, 5a**.

| # | Question | Réponse | Conséquence |
|---|----------|---------|-------------|
| 1 | Version de Spring Boot | **a** — 4.1.2 | version maintenue ; la spec passe de « 3.x » à 4.1 |
| 2 | Auteur du `Dockerfile` | **a** — toi, guidée | tutoriels 005-006 |
| 3 | Bloc Sonar du `pom.xml` | **a** — Claude écrit le `pom.xml` applicatif, toi le bloc Sonar | tutoriel 003 |
| 4 | Étape de build de l'image | **a** — `eclipse-temurin:17-jdk` + `./mvnw` | un seul chemin de build |
| 5 | Périmètre du code | **a** — squelette strict | le modèle arrive en story 008 |

Décisions prises à la génération (vérifiées sur ta machine) :

| Sujet | Décision | Raison |
|-------|----------|--------|
| Port local | **8090** | 8080 (Jenkins) et 8081 (Nexus) sont pris ; staging = 8082, prod = 8083/8084 |
| JSON de santé | `{"groups":["liveness","readiness"],"status":"UP"}` | Spring Boot 4 ajoute les groupes de sondes ; le critère est `"status":"UP"` |
| Scanner Sonar | `sonar-maven-plugin` **5.8.0.7211** épinglé | dialogue vérifié avec SonarQube 9.9.8 et ton JDK 25 (analyse réussie sur un SonarQube jetable) |
| Vigile | exclure `mvnw` du filet « affectation » | fausse alerte sur `${MVNW_PASSWORD:+has-password}` (script fourni par Maven) |

---

## 🎉 Bravo !

L'application existe et se construit en une commande. 🎉

Prochain tutoriel : **#002** — Lancer l'application et interroger sa santé ⏱️ 10 min
