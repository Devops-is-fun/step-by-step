# Tutoriel 010 — Story #010 : Faire le bilan

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 10 minutes
**Difficulté :** ⭐
**Dépend de :** Tutoriel 009
**Prérequis :** PR #1 mergée ; ton délai `N` noté
**Exigence TP couverte :** `EXG-COM-06`, `EXG-COM-07`, `EXG-REND-01`
**Fichier principal :** `docs/stories/story-010-multibranch-webhook.md`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Consigner la mesure du délai dans la story, et la pousser **directement** sur `main` : un dernier
build déclenché par webhook, pour la route.

---

## 🪜 Les étapes

### Étape 1 : Consigne la mesure

**Où :** `docs/stories/story-010-multibranch-webhook.md`, dans IntelliJ

**Action :**

> À la fin du fichier, ajoute (remplace `<N>` par ta mesure et `<date>` par la date du jour) :
>
> ```markdown
>
> ## Mesure
>
> Push de la branche `demo-webhook` → début du build : **<N> s** (<date>, relais smee.io).
> ```

---

### Étape 2 : Commite et pousse

**Où :** la racine du projet

**Action :**

> ```bash
> git add docs/stories/story-010-multibranch-webhook.md
> git commit -m "docs(jenkins): record webhook push-to-build delay"
> git push
> ```

**Résultat attendu :** `OK  no secret found…`, puis `main -> main`. Quelques secondes plus tard,
`main` lance un nouveau build dans Jenkins, **sans que tu aies rien cliqué**.

---

### Étape 3 : Oublie le jeton

**Où :** la racine du projet

**Action :**

> ```bash
> unset JENKINS_USER JENKINS_TOKEN
> git status
> ```

**Résultat attendu :** `nothing to commit, working tree clean`.

---

## ✅ Bilan de la story 010

| Critère d'acceptation | Preuve | Tutoriel |
|-----------------------|--------|----------|
| Job **Multibranch** `step-by-step`, credential `github-token` | Jenkins → `step-by-step` | 003, 005 |
| Le scan initial détecte `main` | *Scan Repository Log* | 005 |
| Un webhook déclenche un build en < 10 s, sans scan manuel | mesure `N` ; cause « événement » | 009 |
| Webhook GitHub avec secret partagé | ping `- 200`, lettre sans cachet `- 400` | 002, 006 |
| Statut du build sur la PR | ✅ `continuous-integration/jenkins/pr-merge` | 009 |
| Relais documenté, avec sa commande de relance | `docs/tooling/webhook-relay.md` | 008 |

### 🛡️ Garde-fous du TP

- [ ] Aucune URL smee, aucun secret, aucun jeton dans un fichier versionné
- [ ] Tous les builds sur `docker-agent`, aucun sur le controller
- [ ] `git status` vide

---

## 🎉 Bravo !

Tu as terminé la story 010 ! 🎉 Ta chaîne **réagit toute seule** : tu pousses, Jenkins construit, et
GitHub affiche le verdict. C'est le cœur de l'intégration continue.

**Prochaine story :** 011 — Le `Jenkinsfile` de CI complet : compilation, tests, rapports JUnit
(`/ tasks-fr 011`).

Pause méritée ☕
