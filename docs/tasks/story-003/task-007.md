# Tutoriel 007 — Story #003 : Lancer le script, puis le faire échouer exprès

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriels 003 et 006
**Prérequis :** `SONAR_TOKEN` chargé (sinon : tutoriel 005, étape 3)
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `scripts/check-sonarqube.sh`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Voir le script passer au vert, puis le faire échouer de trois façons, pour prouver qu'il vérifie
vraiment.

## 📋 Ce que tu vas apprendre

- Provoquer un échec sans rien casser (valeur temporaire d'une variable)
- Lire un résultat partiellement en échec

---

## 🪜 Les étapes

### Étape 1 : Lance le script

**Où :** la racine du projet

**Action :**

> ```bash
> ./scripts/check-sonarqube.sh; echo "exit code: $?"
> ```

**Résultat attendu :**

```
OK  token authentication
OK  default admin/admin refused
OK  project step-by-step exists
OK  quality gate step-by-step-gate assigned
OK  new code coverage threshold: 60%
SonarQube is compliant
exit code: 0
```

**✅ Vérifie que :**
- [ ] 5 lignes `OK`, puis `SonarQube is compliant`, code `0`

---

### Étape 2 : Échec 1, un faux jeton

**Où :** la racine du projet

**Action :**

> ```bash
> SONAR_TOKEN=faux ./scripts/check-sonarqube.sh; echo "exit code: $?"
> ```

**Résultat attendu :** `KO  token authentication refused`, puis `exit code: 1`.

**Remarque :** contrairement à Jenkins, aucune ligne `curl: (22)` ne s'affiche : SonarQube répond
`200` avec `{"valid":false}`. C'est le script qui détecte l'échec en lisant la réponse.

**✅ Vérifie que :**
- [ ] `exit code: 1`

---

### Étape 3 : Échec 2, pas de jeton

**Où :** la racine du projet

**Action :**

> ```bash
> env -u SONAR_TOKEN ./scripts/check-sonarqube.sh; echo "exit code: $?"
> ```

**Résultat attendu :** une ligne qui se termine par
`SONAR_TOKEN is not set (see docs/tooling/sonarqube.md)`, puis `exit code: 1`.

**✅ Vérifie que :**
- [ ] `exit code: 1`

---

### Étape 4 : Échec 3, un projet qui n'existe pas

**Où :** la racine du projet

**Action :**

> Ouvre `scripts/check-sonarqube.sh`, remplace **temporairement** `PROJECT_KEY="step-by-step"` par
> `PROJECT_KEY="step-by-stepp"`, puis exécute :
>
> ```bash
> ./scripts/check-sonarqube.sh; echo "exit code: $?"
> ```

**Résultat attendu :**

```
OK  token authentication
OK  default admin/admin refused
curl: (22) The requested URL returned error: 404
KO  project step-by-stepp missing
curl: (22) The requested URL returned error: 404
KO  quality gate step-by-step-gate not assigned
OK  new code coverage threshold: 60%
2 check(s) failed
exit code: 1
```

> **Remets ensuite `PROJECT_KEY="step-by-step"`**, puis relance l'étape 1 pour revenir au vert.

**Pourquoi cet échec est le plus utile ?** Il montre que le script **continue** après un `KO` et liste
**tous** les problèmes, grâce aux `|| true`. Une faute de frappe dans une clé est aussi l'erreur la
plus probable de la story 007 (`sonar.projectKey` dans le `pom.xml`).

**✅ Vérifie que :**
- [ ] `2 check(s) failed`, code `1`
- [ ] Après correction, l'étape 1 repasse au vert

---

### Étape 5 : Vérifie l'historique

**Où :** la racine du projet

**Action :**

> ```bash
> history | grep -c -F "$SONAR_TOKEN"
> ```

**Résultat attendu :** `0`

---

## 🧹 Comment tout annuler

Rien à annuler, à condition d'avoir remis `PROJECT_KEY="step-by-step"` (étape 4).

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `KO  default admin/admin still accepted` | Le mot de passe admin n'a pas été changé : tutoriel 001 |
| `KO  quality gate ... not assigned` à l'étape 1 | Tutoriel 003, étape 3 |
| `KO  new code coverage threshold is not 60%` | Tutoriel 003, étape 2 |
| `KO  token authentication refused` à l'étape 1 | Jeton non chargé : `echo "${#SONAR_TOKEN}"`, puis tutoriel 005 |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Comment vérifier | Attendu |
|--------------|------------------|---------|
| SonarQube conforme | `./scripts/check-sonarqube.sh` | `SonarQube is compliant`, code `0` |
| Faux jeton détecté | `SONAR_TOKEN=faux ./scripts/check-sonarqube.sh` | code `1` |
| Jeton absent détecté | `env -u SONAR_TOKEN ./scripts/check-sonarqube.sh` | code `1` |
| Mauvaise clé détectée | clé modifiée temporairement | `2 check(s) failed` |
| `PROJECT_KEY` restauré | `grep PROJECT_KEY= scripts/check-sonarqube.sh` | `step-by-step` |

### 🛡️ Garde-fous du TP

- [ ] Le jeton n'apparaît ni dans un fichier ni dans l'historique
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien : le premier commit du projet est fait en **story 006**.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Provoquer des échecs sans rien casser
- ✨ Lire un rapport partiellement en échec

Prochain tutoriel : **#008** — Documenter (en anglais) et faire le bilan ⏱️ 20 min

Pause méritée ☕
