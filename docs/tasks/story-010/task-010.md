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

Retirer l'attente inutile de Jenkins (*quiet period*), mesurer de nouveau le délai push → build sur
`main`, et le consigner dans la story.

---

## 🪜 Les étapes

### Étape 1 : Passe la *quiet period* à 0

**Où :** `http://localhost:8080` → **Manage Jenkins → System**

**Action :**

> Champ **Quiet period** : remplace `5` par `0`. Clique **Save**.

**Pourquoi c'est sans risque ?** La *quiet period* attend 5 s avant chaque build pour **regrouper**
plusieurs commits poussés coup sur coup, une habitude de l'époque où Jenkins interrogeait le dépôt
toutes les minutes. Avec un webhook, chaque push arrive au bon moment : l'attente ne fait que
ralentir. L'attente de 5 s de *GitHub Branch Source*, elle, reste : elle laisse à l'API GitHub le
temps d'être à jour.

**Documente-le** : ajoute **à la fin** de `docs/tooling/jenkins-configuration.md` :

> ```markdown
>
> ## 7. System settings
>
> | Setting | Value | Why |
> |---------|-------|-----|
> | Manage Jenkins -> System -> Quiet period | `0` | builds start as soon as the webhook event is processed (story 010) |
> ```

---

### Étape 2 : Consigne la mesure… en la refaisant sur `main`

**Où :** `docs/stories/story-010-multibranch-webhook.md`, dans IntelliJ

**Action :**

> À la fin du fichier, ajoute (remplace `<date>` par la date du jour ; `<N>` sera rempli à
> l'étape 3) :
>
> ```markdown
>
> ## Mesure
>
> Push sur `main` → début du build : **<N> s** (<date>, relais smee.io, *quiet period* 0).
> Le webhook atteint Jenkins en ≈ 1 s ; GitHub Branch Source attend ensuite 5 s par conception.
> Première mesure (branche neuve `demo-webhook`, *quiet period* 5) : 16 s.
> ```

---

### Étape 3 : Commite, pousse et chronomètre

**Où :** la racine du projet (`JENKINS_USER` et `JENKINS_TOKEN` toujours chargés)

**Action :**

> ```bash
> git add docs/stories/story-010-multibranch-webhook.md docs/tooling/jenkins-configuration.md
> git commit -m "docs(jenkins): record webhook push-to-build delay"
> next=$(( $(curl -fsSg -u "$JENKINS_USER:$JENKINS_TOKEN" 'http://localhost:8080/job/step-by-step/job/main/api/json?tree=lastBuild[number]' | grep -oE '[0-9]+' | tail -1) + 1 ))
> t0=$(date +%s); git push
> for _ in $(seq 60); do
>   ts=$(curl -fsSg -u "$JENKINS_USER:$JENKINS_TOKEN" "http://localhost:8080/job/step-by-step/job/main/$next/api/json?tree=timestamp" 2>/dev/null | grep -oE '[0-9]{13}') && break
>   sleep 1
> done
> echo "push -> build #$next start: $(( ${ts:0:10} - t0 )) s"
> ```

**Résultat attendu :** `OK  no secret found…`, `main -> main`, puis
`push -> build #<n> start: <N> s` avec **N autour de 6 à 8 s**.

**`next`, c'est quoi ?** Le numéro du **prochain** build de `main` (dernier numéro + 1) : la boucle
attend précisément celui-là.

Remplace `<N>` dans la story par ta mesure, puis :

> ```bash
> git add docs/stories/story-010-multibranch-webhook.md
> git commit -m "docs(jenkins): fill in measured webhook delay"
> git push
> ```

---

### Étape 4 : Oublie le jeton

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
| Un webhook déclenche un build en < 10 s, sans scan manuel | mesure `N` sur `main` ; cause « événement » | 009, 010 |
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
