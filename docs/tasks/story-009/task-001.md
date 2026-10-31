# Tutoriel 001 — Story #009 : Découvrir les règles et lancer leurs tests

**Qui :** 🤖 Claude a implémenté — tu découvres et tu vérifies
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐
**Dépend de :** story 008 terminée
**Prérequis :** aucun (pas de Docker dans ce tutoriel)
**Exigence TP couverte :** alimente `EXG-COM-09`
**Fichier principal :** `src/main/java/com/esgi/stepbystep/service/TaskService.java`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Repérer où vivent les deux règles, et constater que leurs tests tournent **sans base**, en une
seconde.

## 📋 Ce que tu vas apprendre

- Ce qu'est un **service métier**, et pourquoi les règles y vivent
- Ce qu'est un **mock** (Mockito)
- Ce qu'est un **test de frontière** (30 accepté, 31 refusé)

---

## 🪜 Les étapes

### Étape 1 : Fais le tour des changements

**Où :** la racine du projet

**Action :**

> ```bash
> git status --short
> ```

**Résultat attendu :** ces lignes (l'ordre peut varier) :

```
 M docs/specification/specification.md
 M docs/stories/story-009-regles-metier.md
 M src/main/java/com/esgi/stepbystep/domain/TaskRepository.java
 M src/test/java/com/esgi/stepbystep/StepByStepApplicationIT.java
 M src/test/java/com/esgi/stepbystep/domain/TaskRepositoryIT.java
?? docs/architecture/business-rules.md
?? docs/tasks/story-009/
?? src/main/java/com/esgi/stepbystep/service/
?? src/test/java/com/esgi/stepbystep/service/
```

**Si tu ne vois que ` M pom.xml` et `?? docs/tasks/story-009/`** : c'est normal pour ton dépôt. Le
code de cette story est **déjà commité** (commit `0498e35`, voir la note du tutoriel 005). Pour voir
les fichiers de la story, lance plutôt :

```bash
git show --stat --format='%h %s' 0498e35
```

| Fichier | Rôle |
|---------|------|
| `service/TaskService.java` | le **règlement** : `createTask`, `planFor`, `addStep` |
| `service/exception/StepTooLongException.java` | refus de `REG-01` : « Découpe-la en plusieurs sous-étapes plus courtes » |
| `service/exception/AlreadyPlannedException.java` | refus de `REG-02` : « une seule tâche par jour » |
| `service/exception/StepTooShortException.java`, `BusinessRuleException.java`, `TaskNotFoundException.java` | moins d'1 minute ; la famille commune des refus ; tâche inconnue |
| `domain/TaskRepository.java` | 2 requêtes : « y a-t-il une tâche **non terminée** ce jour-là ? » |
| `TaskServiceTest.java` | 12 tests **unitaires** des deux règles |
| `TaskRepositoryIT.java` | 1 test d'intégration de plus : les 2 requêtes, contre la vraie base |
| `StepByStepApplicationIT.java` | corrigé : le test vérifie maintenant quelque chose (tu verras pourquoi au tutoriel 004) |
| `docs/architecture/business-rules.md` | règle → où elle s'applique → quel test la prouve |

**Pourquoi les règles vivent dans un « service » ?** Plus tard, on créera une tâche depuis un
formulaire web, peut-être depuis une API. Si la règle était dans le formulaire, l'API pourrait la
contourner. Dans le service, **tout le monde** passe par le même guichet.

---

### Étape 2 : Lis la règle REG-01

**Où :** `TaskService.java`, dans IntelliJ

**Action :**

> Repère la méthode `validateStepDuration`.

**Résultat attendu :**

```java
if (minutes < 1) {
    throw new StepTooShortException();
}
if (minutes > TaskStep.MAX_MINUTES) {
    throw new StepTooLongException(minutes);
}
```

**Pourquoi la vérifier ici, alors que l'entité a déjà `@Max(30)` ?** Trois gardes, du plus aimable
au plus strict : le **service** donne un message clair (« découpe-la »), l'**entité** refuse une
valeur invalide, la **base** refuse en dernier recours. Le tableau « Defence in depth » de
`business-rules.md` les montre côte à côte.

---

### Étape 3 : Lance seulement les tests du service

**Où :** la racine du projet

**Action :**

> ```bash
> ./mvnw -B test -Dtest='TaskServiceTest' | grep -E 'Tests run:|BUILD'
> ```

**Résultat attendu :** en quelques secondes :

```
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0, … -- in com.esgi.stepbystep.service.TaskServiceTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Comment ces tests tournent-ils sans base ?** Le `TaskRepository` y est un **mock** : une doublure
Mockito, à qui le test dicte ses réponses (« oui, ce lundi est déjà pris »). Le test vérifie
seulement que le service **réagit** bien. Que la vraie requête dise la vérité, c'est le travail du
test d'intégration `findsOnlyUnfinishedTasksWhenCheckingADay`.

**✅ Vérifie que :**
- [ ] `Tests run: 12, Failures: 0`

---

### Étape 4 : Tous les tests unitaires

**Où :** la racine du projet

**Action :**

> ```bash
> ./mvnw -B test | grep -E 'Tests run:|BUILD'
> ```

**Résultat attendu :** `TaskValidationTest` (9) et `TaskServiceTest` (12), puis
`Tests run: 21, Failures: 0, Errors: 0, Skipped: 0` et `BUILD SUCCESS`.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `No tests matching pattern "TaskServiceTest" were executed` | Il manque les guillemets simples autour de `TaskServiceTest`, ou une faute de frappe |
| Un test en échec | Copie-moi `target/surefire-reports/com.esgi.stepbystep.service.TaskServiceTest.txt` : c'est mon code |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Tests du service | `./mvnw -B test -Dtest='TaskServiceTest'` | `Tests run: 12, Failures: 0` |
| Tous les unitaires | `./mvnw -B test` | `Tests run: 21` |

---

## Journal des décisions

Réponses de cadrage `tasks-fr 009` (29/10/2026) : **1a, 2a, 3a, 4a, 5a**.

| # | Question | Réponse | Conséquence |
|---|----------|---------|-------------|
| 1 | Périmètre | **a** — `REG-01` et `REG-02` | `REG-03` à `REG-07` restent en story 028 |
| 2 | Vérification de `REG-02` | **a** — le service interroge la base avant d'enregistrer | testable avec Mockito ; requêtes vérifiées par un test d'intégration |
| 3 | Langue des messages | **a** — français pour l'utilisateur, code en anglais | messages des `BusinessRuleException` |
| 4 | Doc | **a** — Claude, en anglais | `docs/architecture/business-rules.md` |
| 5 | Ta partie | **a** — rapport JUnit + analyse SonarQube | tutoriels 002 à 004 |

Décisions prises à la génération :

| Sujet | Décision | Raison |
|-------|----------|--------|
| Durée < 1 minute | `StepTooShortException`, en plus de `StepTooLongException` | « redécoupe » n'a pas de sens pour 0 minute ; message différent |
| Famille des refus | `BusinessRuleException` (abstraite) | un futur contrôleur web traitera **tous** les refus de la même façon |
| Tests | 12 au lieu des 4 minimum | frontières 1/0 et 30/31, date vide, date libre, date occupée, tâche terminée, re-planification, retrait de date |
| `contextLoads` | une vraie assertion | SonarQube signalait un test sans assertion (gravité **Blocker**), vérifié sur un SonarQube jetable |

---

## 🎉 Bravo !

Le règlement de Step by Step est écrit et prouvé. 🎉

Prochain tutoriel : **#002** — Lire un rapport JUnit ⏱️ 10 min
