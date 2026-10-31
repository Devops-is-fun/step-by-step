# Tutoriel 003 — Story #009 : Relancer l'analyse SonarQube

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** stack `cicd` démarré ; jeton `scripts-tp` dans Bitwarden
**Exigence TP couverte :** prépare `EXG-COM-08` et `EXG-COM-11`
**Fichier principal :** `http://localhost:9000/dashboard?id=step-by-step`

> **Répertoire de travail :** la racine du projet. Garde **le même terminal** du début à la fin.

---

## 🎯 Objectif

Envoyer à SonarQube le code des stories 008 et 009, avec **tous** les tests (unitaires et
d'intégration).

---

## 🪜 Les étapes

### Étape 1 : Charge ton jeton

**Où :** la racine du projet

**Action :**

> ```bash
> read -rs SONAR_TOKEN && export SONAR_TOKEN
> ```
>
> Colle le jeton **`scripts-tp`** (Bitwarden), puis **Entrée**. Rien ne s'affiche : c'est voulu.

---

### Étape 2 : Vérifie que SonarQube est prêt

**Où :** le même terminal

**Action :**

> ```bash
> ./scripts/check-sonarqube.sh
> ```

**Résultat attendu :** les 5 lignes `OK` et `SonarQube is compliant`.

---

### Étape 3 : Allume la base de test

**Où :** le même terminal

**Action :**

> ```bash
> docker compose -f src/test/resources/compose-test.yml up -d --wait
> ```

**Résultat attendu :** `Container stepbystep-test-postgres-1  Healthy`

**Pourquoi la base ?** L'analyse part de `verify`, qui lance aussi les tests d'intégration.

---

### Étape 4 : Lance l'analyse

**Où :** le même terminal

**Action :**

> ```bash
> ./mvnw -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.login="$SONAR_TOKEN" | grep -E 'Tests run: [0-9]+, Failures: [0-9]+, Errors: [0-9]+, Skipped: [0-9]+$|ANALYSIS|BUILD'
> ```

**Résultat attendu :** en moins d'une minute :

```
[INFO] Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] ANALYSIS SUCCESSFUL, you can find the results at: http://localhost:9000/dashboard?id=step-by-step
[INFO] BUILD SUCCESS
```

(21 tests unitaires, puis 7 tests d'intégration.)

**Des `WARNING` « Missing blame information » ?** Normal pour les fichiers **pas encore commités**
(ceux de cette story) : SonarQube ne peut pas demander à Git qui les a écrits.

**`BUILD SUCCESS` alors que la Quality Gate sera rouge ?** Oui : ici, Maven **envoie** l'analyse,
il n'attend pas le verdict. Rendre la gate **bloquante**, c'est le travail de Jenkins en story 013
(`waitForQualityGate`).

**✅ Vérifie que :**
- [ ] `ANALYSIS SUCCESSFUL`

---

### Étape 5 : Éteins la base, oublie le jeton… presque

**Où :** le même terminal

**Action :**

> ```bash
> docker compose -f src/test/resources/compose-test.yml down
> ```

**Garde `SONAR_TOKEN` chargé** : le tutoriel 004 s'en sert pour interroger l'API. Tu l'oublieras à
la fin du 004.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `SONAR_TOKEN is not set` | Étape 1 dans **ce** terminal |
| `Not authorized` | Jeton mal collé : étape 1 de nouveau (le jeton `scripts-tp`, pas `jenkins-step-by-step`) |
| `Connection to localhost:5433 refused` | Étape 3 |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Comment | Attendu |
|--------------|---------|---------|
| Tests | sortie Maven | 21 + 7, `Failures: 0` |
| Analyse | sortie Maven | `ANALYSIS SUCCESSFUL` |
| Base éteinte | `docker ps --filter name=stepbystep-test -q` | vide |

---

## 🎉 Bravo !

Analyse envoyée. 🎉 Voyons maintenant ce qu'en pense le contrôle qualité.

Prochain tutoriel : **#004** — Lire le verdict de SonarQube ⏱️ 15 min
