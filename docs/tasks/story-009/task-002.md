# Tutoriel 002 — Story #009 : Lire un rapport JUnit

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 10 minutes
**Difficulté :** ⭐
**Dépend de :** Tutoriel 001
**Prérequis :** aucun
**Exigence TP couverte :** `EXG-COM-09` (tests unitaires avec rapport JUnit)
**Fichier principal :** `target/surefire-reports/TEST-com.esgi.stepbystep.service.TaskServiceTest.xml`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Lire **toi-même** le fichier que Jenkins lira en story 011 pour dessiner ses courbes de tests.

## 📋 Ce que tu vas apprendre

- La structure d'un rapport JUnit : une **suite**, des **cas**
- Retrouver une preuve précise (la frontière 30/31) dans un rapport

---

## 🪜 Les étapes

### Étape 1 : Repars d'un dossier propre

**Où :** la racine du projet

**Action :**

> ```bash
> ./mvnw -B -q clean test
> ls target/surefire-reports/
> ```

**Résultat attendu :** 4 fichiers, 2 par classe de test :

```
com.esgi.stepbystep.domain.TaskValidationTest.txt
com.esgi.stepbystep.service.TaskServiceTest.txt
TEST-com.esgi.stepbystep.domain.TaskValidationTest.xml
TEST-com.esgi.stepbystep.service.TaskServiceTest.xml
```

**Pourquoi `clean` ?** Il supprime `target/`. Sans lui, d'anciens rapports (par exemple celui de
`StepByStepApplicationTests`, renommé en story 008) traîneraient à côté des nouveaux, et Jenkins les
compterait.

**Pourquoi deux formats ?** Le `.txt` est pour les **humains** (un résumé lisible). Le `.xml` est
pour les **outils** : Jenkins, SonarQube, ton IDE.

---

### Étape 2 : Lis l'en-tête de la suite

**Où :** la racine du projet

**Action :**

> ```bash
> grep -o '<testsuite [^>]*' target/surefire-reports/TEST-com.esgi.stepbystep.service.TaskServiceTest.xml | grep -oE '(name|tests|failures|errors|skipped)="[^"]*"'
> ```

**Résultat attendu :**

```
name="com.esgi.stepbystep.service.TaskServiceTest"
tests="12"
errors="0"
skipped="0"
failures="0"
```

**Lis-le comme ça :**

| Attribut | Sens |
|----------|------|
| `tests` | nombre de cas exécutés |
| `failures` | une **assertion** a échoué : le code ne fait pas ce qu'on attend |
| `errors` | le test a **planté** (exception inattendue) |
| `skipped` | test désactivé |

Jenkins additionne ces 4 nombres sur tous les fichiers `TEST-*.xml` pour colorer le build.

---

### Étape 3 : Trouve la preuve de la frontière 30/31

**Où :** la racine du projet

**Action :**

> ```bash
> grep -oE '<testcase name="[^"]+"' target/surefire-reports/TEST-com.esgi.stepbystep.service.TaskServiceTest.xml
> ```

**Résultat attendu :** 12 lignes, dont :

```
<testcase name="shouldAcceptStepOfExactly30Minutes"
<testcase name="shouldRejectStepLongerThan30Minutes"
```

**Pourquoi des noms aussi longs ?** Un bon nom de test est une **phrase** : quand un build rouge
affiche `shouldRejectStepLongerThan30Minutes`, tu sais **quelle règle** est cassée sans ouvrir le
code. C'est la preuve demandée par la story : « frontière 30 acceptée / 31 refusée, visible dans les
noms des tests ».

**✅ Vérifie que :**
- [ ] Tu retrouves les 4 tests cités par la story : `shouldRejectStepLongerThan30Minutes`,
  `shouldAcceptStepOfExactly30Minutes`, `shouldRejectSecondUnfinishedTaskOnSameDate`,
  `shouldAcceptAnotherTaskWhenFirstIsDone`

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `No such file or directory` | L'étape 1 n'a pas tourné jusqu'au bout : relance-la sans `-q` pour voir l'erreur |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Rapports présents | `ls target/surefire-reports/` | 2 `TEST-*.xml` |
| Suite du service | `grep … tests=` | `tests="12"`, `failures="0"`, `errors="0"` |
| Frontière | `grep … testcase` | `…Exactly30Minutes` et `…LongerThan30Minutes` |

---

## 🎉 Bravo !

Tu sais lire ce que Jenkins lira. 🎉 **Jalon J1 atteint.**

Prochain tutoriel : **#003** — Relancer l'analyse SonarQube ⏱️ 15 min
