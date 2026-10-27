# Tutoriel 004 — Story #007 : Lancer la première analyse SonarQube

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 003
**Prérequis :** stack `cicd` démarré ; ton jeton **utilisateur** `scripts-tp` (`squ_…`) dans Bitwarden
**Exigence TP couverte :** prépare `EXG-COM-08`
**Fichier principal :** `http://localhost:9000`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Envoyer la **première analyse** de Step by Step à SonarQube, à la main, et voir le projet se
remplir.

## 📋 Ce que tu vas apprendre

- Lancer le scanner Maven avec un jeton **qui ne laisse aucune trace**
- Lire le résultat dans SonarQube (interface **et** API)

---

## 🪜 Les étapes

### Étape 1 : Charge ton jeton

**Où :** un terminal, à la racine du projet

**Action :**

> ```bash
> read -rs SONAR_TOKEN && export SONAR_TOKEN
> ```
>
> Colle le jeton **`scripts-tp`** (entrée Bitwarden de la story 003), puis **Entrée**.

**Résultat attendu :** rien ne s'affiche. C'est voulu.

---

### Étape 2 : Vérifie que SonarQube est prêt

**Où :** le même terminal, à la racine du projet

**Action :**

> ```bash
> ./scripts/check-sonarqube.sh
> ```

**Résultat attendu :** le même résultat **OK** qu'à la fin de la story 003.

**Pourquoi avant l'analyse ?** Si SonarQube dort, l'analyse échouera avec un message moins clair.
Le script a besoin du jeton chargé à l'étape 1 : sans lui, il s'arrête sur
`SONAR_TOKEN is not set`.

---

### Étape 3 : Lance l'analyse

**Où :** le même terminal

**Action :**

> ```bash
> ./mvnw -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.login="$SONAR_TOKEN"
> ```

**Résultat attendu :** en moins d'une minute :

```
[INFO] Communicating with SonarQube Server 9.9.8.100196
…
[INFO] ANALYSIS SUCCESSFUL, you can find the results at: http://localhost:9000/dashboard?id=step-by-step
…
[INFO] BUILD SUCCESS
```

**Un `WARNING` « Missing blame information » ?** Normal : SonarQube lit dans Git **qui** a écrit
chaque ligne (*blame*). Ces fichiers ne sont pas encore commités, donc Git n'a rien à lui dire.
L'avertissement disparaîtra aux analyses suivantes, après le commit.

**Pourquoi `verify` **avant** `sonar` ?** Le scanner analyse les fichiers **compilés** et les résultats
de tests : il faut d'abord les produire.

**Pourquoi `-Dsonar.login="$SONAR_TOKEN"` ?** Le jeton vit **seulement** dans la mémoire de ce
terminal. Tu ne l'as tapé nulle part : ni dans un fichier, ni dans l'historique (`history` montre
`$SONAR_TOKEN`, pas sa valeur).

**✅ Vérifie que :**
- [ ] `ANALYSIS SUCCESSFUL`

---

### Étape 4 : Regarde le projet dans SonarQube

**Où :** `http://localhost:9000/dashboard?id=step-by-step`

**Action :**

> Ouvre l'URL (connecte-toi si besoin).

**Résultat attendu :** le projet **Step by Step** n'est plus vide : une date d'analyse, un statut de
Quality Gate, et un nombre de **Lines of Code** sous la barre de résumé.

**Le statut de la Quality Gate peut être `Passed` ou `Failed`** : on ne s'en occupe pas maintenant
(story 013). Le critère ici, c'est que des lignes ont été analysées.

---

### Étape 5 : Le même constat, par l'API

**Où :** le même terminal

**Action :**

> ```bash
> curl -fsS -u "$SONAR_TOKEN:" 'http://localhost:9000/api/measures/component?component=step-by-step&metricKeys=ncloc'; echo
> ```

**Résultat attendu :** un JSON qui se termine par `"metric":"ncloc","value":"<un nombre>"}]}}`, avec
un nombre **plus grand que 0**.

**Pourquoi l'API ?** C'est la preuve demandée par la story (`ncloc > 0`), et c'est **comme ça** qu'un
script ou Jenkins la vérifiera : sans clic.

---

### Étape 6 : Oublie le jeton

**Où :** le même terminal

**Action :**

> ```bash
> unset SONAR_TOKEN
> ```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `Not authorized. Please check the properties sonar.login` | Jeton vide ou mal collé : refais l'étape 2 (le jeton `scripts-tp`, pas `jenkins-step-by-step`) |
| `SonarQube server [http://localhost:9000] can not be reached` | SonarQube arrêté : `docker compose up -d sonarqube`, attends `UP`, recommence |
| `curl: (22) … 401` à l'étape 5 | Même cause que la 1re ligne |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Comment | Attendu |
|--------------|---------|---------|
| Analyse | sortie Maven | `ANALYSIS SUCCESSFUL` |
| Lignes analysées | API `measures` | `ncloc` > 0 |
| Jeton oublié | `echo "${SONAR_TOKEN:-vide}"` | `vide` |

### 🛡️ Garde-fous du TP

- [ ] Le jeton n'est écrit dans **aucun** fichier : `git grep -c squ_` ne trouve rien

---

## 🎉 Bravo !

Step by Step est passée au **contrôle qualité** pour la première fois ! 🎉 **Jalon J2 atteint.**

C'est une bonne coupure ☕ (≈ 55 min depuis le début).

Prochain tutoriel : **#005** — Écrire le `Dockerfile` et le `.dockerignore` ⏱️ 20 min
