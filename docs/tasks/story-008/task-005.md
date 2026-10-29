# Tutoriel 005 — Story #008 : Lancer les tests d'intégration et lire leurs rapports

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriels 003 et 004
**Prérequis :** la base de test est allumée (`(healthy)`)
**Exigence TP couverte :** prépare `EXG-COM-09` et `EXG-COM-10`
**Fichier principal :** `target/failsafe-reports/`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Faire tourner **toute** la chaîne de tests contre la vraie base, lire les rapports, et constater
qu'une base absente fait **échouer** le build.

---

## 🪜 Les étapes

### Étape 1 : Vérifie que la base est allumée

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose -f src/test/resources/compose-test.yml ps --format '{{.Name}} {{.Status}}'
> ```

**Résultat attendu :** `stepbystep-test-postgres-1 Up … (healthy)`. Si la ligne est **vide** :
`docker compose -f src/test/resources/compose-test.yml up -d --wait`.

---

### Étape 2 : Lance toute la chaîne

**Où :** la racine du projet

**Action :**

> ```bash
> ./mvnw -B verify | grep -E 'Tests run:|BUILD'
> ```

**Résultat attendu :** en 20 à 40 secondes :

```
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, … in com.esgi.stepbystep.domain.TaskValidationTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, … in com.esgi.stepbystep.StepByStepApplicationIT
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, … in com.esgi.stepbystep.domain.TaskRepositoryIT
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**L'ordre de `TaskRepositoryIT` et `StepByStepApplicationIT` peut être inversé** : Failsafe ne
garantit pas l'ordre des classes. Les lignes `WARNING` sur Mockito et « Java agent » (JDK 25) peuvent
s'intercaler : elles ne sont pas filtrées par `grep` et sont sans conséquence.

**Lis-le comme ça :** deux blocs. D'abord **Surefire** (9 tests unitaires), puis **Failsafe**
(6 tests d'intégration), dans cet ordre, car `test` vient avant `integration-test`.

**Ce que prouvent les 5 tests de `TaskRepositoryIT` :**

| Test | Preuve |
|------|--------|
| `savesATaskAndReadsItsStepsBackInOrder` | une tâche et ses sous-étapes sont écrites puis relues, **dans l'ordre** de `position` |
| `updatesAndDeletesATaskWithItsSteps` | modifier, puis supprimer (les sous-étapes partent avec la tâche) |
| `refusesTwoStepsAtTheSamePosition` | la base refuse deux sous-étapes à la même place |
| `refusesTwoUnfinishedTasksOnTheSameDay` | la base refuse deux tâches non terminées le même jour (`REG-02`) |
| `keepsTheNotificationsOfATask` | les traces de rappel sont enregistrées et relues |

**✅ Vérifie que :**
- [ ] `Tests run: 9` (Surefire) **et** `Tests run: 6` (Failsafe), `Failures: 0, Errors: 0`
- [ ] `BUILD SUCCESS`

---

### Étape 3 : Regarde les tables créées par Flyway

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose -f src/test/resources/compose-test.yml exec -T postgres psql -U stepbystep -d stepbystep_test -c '\dt'
> ```

**Résultat attendu :** 4 tables, alors qu'il n'y en avait **aucune** au tutoriel 003 :
`flyway_schema_history`, `notification_log`, `task`, `task_step`.

**Pourquoi les tables sont-elles vides ?** Chaque test de `TaskRepositoryIT` s'exécute dans une
transaction **annulée** à la fin (`@DataJpaTest`) : les tests ne se marchent pas dessus. Le
**schéma**, lui, reste.

---

### Étape 4 : Ouvre les rapports

**Où :** la racine du projet

**Action :**

> ```bash
> ls target/surefire-reports/ target/failsafe-reports/
> ```

**Résultat attendu :** dans chacun, des fichiers `TEST-….xml` (un par classe de test).

**Pourquoi ces XML ?** C'est le **format JUnit** : Jenkins les lira pour afficher les courbes de
tests (exigence `EXG-COM-09`, story 011). Tu viens de voir la matière première.

---

### Étape 5 : Prouve qu'une base absente fait échouer le build

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose -f src/test/resources/compose-test.yml stop
> ./mvnw -B verify | grep -E 'Tests run:|BUILD'
> ```

**Résultat attendu :** les 9 tests unitaires passent toujours, puis
`Tests run: 6, Failures: 0, Errors: 6` et `BUILD FAILURE`.

**Pourquoi le faire exprès ?** Un test qui ne peut **pas** échouer ne prouve rien. Ici, sans base,
les tests d'intégration échouent **et** le build est rouge : c'est l'objectif `verify` de Failsafe
qui fait son travail.

---

### Étape 6 : Rallume la base

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose -f src/test/resources/compose-test.yml start
> ```

**Résultat attendu :** `Container stepbystep-test-postgres-1  Started`. Garde-la allumée pour le
tutoriel 006.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `Connection to localhost:5433 refused` à l'étape 2 | La base est éteinte : étape 1 |
| `Schema-validation: missing table [task]` | Flyway n'a pas tourné : montre-moi les 40 lignes avant cette erreur |
| Un test de `TaskRepositoryIT` en échec | Copie-moi le fichier `target/failsafe-reports/com.esgi.stepbystep.domain.TaskRepositoryIT.txt` : c'est mon code |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Chaîne complète | `./mvnw -B verify` | 9 + 6 tests, `BUILD SUCCESS` |
| Schéma créé | `psql … -c '\dt'` | 4 tables |
| Rapports JUnit | `ls target/failsafe-reports/` | `TEST-*.xml` |
| Sans base = rouge | étape 5 | `BUILD FAILURE` |

---

## 🎉 Bravo !

Tes premiers **tests d'intégration** contre une vraie base ! 🎉 **Jalon J3 atteint.**

Prochain tutoriel : **#006** — Mettre à jour le README (FR + EN) ⏱️ 15 min
