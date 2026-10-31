# Tutoriel 004 — Story #009 : Lire le verdict de SonarQube

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 003
**Prérequis :** `SONAR_TOKEN` encore chargé dans ton terminal
**Exigence TP couverte :** prépare `EXG-COM-11` et `EXG-N1a-01`
**Fichier principal :** `http://localhost:9000/dashboard?id=step-by-step`

> **Répertoire de travail :** la racine du projet, dans le terminal du tutoriel 003.

---

## 🎯 Objectif

Lire le verdict de la Quality Gate, et comprendre **pourquoi** il est rouge alors que le code est
propre.

## 📋 Ce que tu vas apprendre

- Le **nouveau code** (*New Code*) selon SonarQube
- La différence entre « le code est sain » et « le code est **prouvé** par des tests »
- Lire le verdict par l'**API**, comme le fera Jenkins

---

## 🪜 Les étapes

### Étape 1 : Ouvre le tableau de bord

**Où :** `http://localhost:9000/dashboard?id=step-by-step`

**Action :**

> Ouvre la page et regarde le bloc **Quality Gate Status** en haut.

**Résultat attendu :** **Failed**, avec **1 condition en échec** : la couverture du nouveau code
(**Coverage on New Code**) vaut **0.0 %**, alors que ta gate `step-by-step-gate` en exige **60 %**.

**Ne panique pas : c'est le résultat attendu.** 🙂 Ce rouge est une **information**, pas une erreur
de ta part.

---

### Étape 2 : Comprends ce rouge

**Où :** le même tableau de bord, onglet **New Code**

**Action :**

> Regarde les 4 blocs de l'onglet **New Code** : *Reliability* (bugs), *Security* (vulnérabilités),
> *Maintainability* (code smells), *Coverage*.

**Résultat attendu :**

| Bloc | Valeur | Verdict |
|------|--------|---------|
| Bugs | 0 | ✅ |
| Vulnerabilities | 0 | ✅ |
| Code Smells | 0 | ✅ |
| Coverage | 0.0 % | ❌ |

**Pourquoi 0 % alors qu'il y a 28 tests ?** SonarQube ne **lance pas** les tests : il lit un
**rapport de couverture** qui dit quelles lignes ont été exécutées. Ce rapport est produit par un
outil, **JaCoCo**, que ta chaîne n'a pas encore (story 021). Sans rapport, SonarQube compte 0 ligne
couverte.

**Image :** le contrôle qualité a inspecté la voiture (pas de défaut visible), mais on ne lui a pas
donné le **carnet d'essais**. Sans carnet, il refuse de signer.

**Qu'est-ce que le « nouveau code » ?** Tout ce qui a changé **depuis ta première analyse** (story
007). SonarQube est exigeant sur le **nouveau** code, et tolérant sur l'ancien : on n'a pas à tout
réparer d'un coup, mais on ne laisse rien entrer de mal fait.

---

### Étape 3 : Le même verdict, par l'API

**Où :** la racine du projet, le même terminal

**Action :**

> ```bash
> curl -fsS -u "$SONAR_TOKEN:" 'http://localhost:9000/api/qualitygates/project_status?projectKey=step-by-step' | grep -oE '"(status|metricKey|actualValue|errorThreshold)":"[^"]*"' | head -5
> ```

**Résultat attendu :** les premières lignes :

```
"status":"ERROR"
"status":"OK"
"metricKey":"new_reliability_rating"
…
```

Le premier `"status":"ERROR"` est le **verdict global** de la gate.

**Pourquoi l'API ?** C'est **exactement** cette question que Jenkins posera en story 013
(`waitForQualityGate`) : `ERROR` → build rouge. Aujourd'hui, tu la poses à la main.

---

### Étape 4 : Regarde les problèmes signalés

**Où :** `http://localhost:9000/project/issues?id=step-by-step&resolved=false`

**Action :**

> Ouvre la page **Issues** du projet.

**Résultat attendu :** **0 issue** ouverte.

**Le savais-tu ?** Pendant la préparation de cette story, Claude a fait la même analyse sur un
SonarQube jetable. SonarQube y a trouvé un **vrai défaut** dans le code de Claude : le test
`contextLoads` ne vérifiait **rien** (« Add at least one assertion to this test case », gravité
**Blocker**). Corrigé avant de te le livrer. Le contrôle qualité sert à ça : il attrape aussi les
erreurs de ceux qui écrivent le code.

---

### Étape 5 : Oublie le jeton

**Où :** le même terminal

**Action :**

> ```bash
> unset SONAR_TOKEN
> ```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| La gate est **Passed** | Ta période de « nouveau code » est réglée autrement : montre-moi **Project Settings → New Code** |
| D'autres conditions en échec que la couverture | Montre-moi la liste : je regarde si c'est mon code |
| Des issues ouvertes à l'étape 4 | Copie-moi leur message et leur fichier |
| `curl: (22) … 401` | Le jeton n'est plus chargé : `read -rs SONAR_TOKEN && export SONAR_TOKEN` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Où | Attendu |
|--------------|----|---------|
| Verdict | tableau de bord / API | `Failed` / `"status":"ERROR"` |
| Seule cause | onglet New Code | Coverage 0.0 % < 60 % |
| Code propre | Issues | 0 |
| Jeton oublié | `echo "${SONAR_TOKEN:-vide}"` | `vide` |

---

## 🎉 Bravo !

Tu sais lire un verdict de Quality Gate, **et** l'expliquer. 🎉 **Jalon J2 atteint.**

Tu as même trouvé seule le chaînon manquant de ta chaîne : la couverture (JaCoCo, story 021). En
story 013, ce rouge **bloquera** le pipeline : d'ici là, il faudra que la story 021 l'ait fait passer
au vert.

Prochain tutoriel : **#005** — Commiter, pousser, bilan ⏱️ 15 min
