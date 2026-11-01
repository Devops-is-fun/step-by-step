# Tutoriel 009 — Story #010 : La PR de démonstration : push → build → statut → merge

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 25 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 008
**Prérequis :** `check-webhook.sh` vert ; ton jeton Jenkins `scripts-tp` (Bitwarden)
**Exigence TP couverte :** `EXG-COM-06`, `EXG-COM-07`
**Fichier principal :** une pull request sur `https://github.com/<ton-compte>/step-by-step`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Vivre la boucle complète, comme dans une vraie équipe : une branche, un push **mesuré**, une PR avec
son statut, un merge.

## 📋 Ce que tu vas apprendre

- Travailler sur une **branche** et ouvrir une **pull request**
- Mesurer le délai push → build
- Lire le statut d'un build **sur la PR**

---

## 🪜 Les étapes

### Étape 1 : Crée la branche et commite le travail de la story

**Où :** la racine du projet

**Action :**

> ```bash
> git switch -c demo-webhook
> git add scripts/check-webhook.sh docs/tooling/webhook-relay.md docs/tasks/story-010 docs/stories docs/specification
> git status --short | grep '^[AMD]'
> git commit -m "ci(webhook): add webhook check script and relay documentation"
> ```

**Résultat attendu :** `Switched to a new branch 'demo-webhook'` ; dans la liste,
`A  scripts/check-webhook.sh`, `A  docs/tooling/webhook-relay.md`, les fichiers de
`docs/tasks/story-010/` et quelques `M ` ; puis `OK  no secret found…`.

**Pourquoi une branche ?** Sur `main`, chaque commit est « officiel ». Une branche est un brouillon :
Jenkins le vérifie **avant** qu'il rejoigne `main`. Et pas de `/` dans le nom : Jenkins l'encoderait
dans ses URL (`%2F`).

---

### Étape 2 : Charge ton jeton Jenkins

**Où :** la racine du projet

**Action :**

> ```bash
> read -r JENKINS_USER && export JENKINS_USER
> read -rs JENKINS_TOKEN && export JENKINS_TOKEN
> ```
>
> Tape ton identifiant Jenkins (`admin`), **Entrée** ; colle le jeton `scripts-tp`, **Entrée**.

---

### Étape 3 : Pousse en chronométrant

**Où :** la racine du projet

**Action :**

> ```bash
> t0=$(date +%s); git push -u origin demo-webhook
> for _ in $(seq 60); do
>   ts=$(curl -fsSg -u "$JENKINS_USER:$JENKINS_TOKEN" 'http://localhost:8080/job/step-by-step/job/demo-webhook/1/api/json?tree=timestamp' 2>/dev/null | grep -oE '[0-9]{13}') && break
>   sleep 1
> done
> echo "push -> build start: $(( ${ts:0:10} - t0 )) s"
> ```

**Résultat attendu :** les lignes du `git push`, puis, en quelques secondes,
`push -> build start: <N> s` avec **N < 10**.

**Ce que mesure la boucle :** elle demande chaque seconde à Jenkins « le build n°1 de
`demo-webhook` existe-t-il ? », et compare son heure de départ (`timestamp`, en millisecondes) à
l'heure du push.

**Si `N` dépasse 10 s (≈ 16 s sur une branche neuve) :** le webhook, lui, arrive en ≈ 1 s
(`docker logs -t cicd-webhook-relay`). Le reste, ce sont **deux attentes volontaires de Jenkins** :

| Attente | Durée | Où la voir |
|---------|-------|------------|
| GitHub Branch Source attend avant de traiter l'événement (le temps que l'API GitHub soit à jour) | 5 s | *Multibranch Pipeline Events* du job : `Received …` puis `processed in …` |
| *Quiet period* : Jenkins attend avant de lancer un build planifié (pour regrouper plusieurs commits) | 5 s | **Manage Jenkins → System → Quiet period** |

La *quiet period* ne sert à rien avec un webhook : tu la passes à **0** au tutoriel 010, puis tu
refais la mesure sur `main`. C'est **cette** mesure que tu reporteras dans la story.

**✅ Vérifie que :**
- [ ] `N` < 10

---

### Étape 4 : Regarde la cause du build

**Où :** `http://localhost:8080/job/step-by-step/job/demo-webhook/1/` → **Console Output**

**Action :**

> Lis les premières lignes.

**Résultat attendu :** une cause qui parle d'un **événement** (par exemple `Branch event` ou
`Push event to branch demo-webhook`), **pas** `Branch indexing` (scan) ni `Started by user`. Puis
`Running on docker-agent` et `Finished: SUCCESS`.

**Pourquoi c'est la preuve du webhook ?** `Branch indexing` voudrait dire « trouvé par un scan ».
Un **événement**, c'est GitHub qui a sonné.

---

### Étape 5 : Ouvre la pull request

**Où :** `https://github.com/<ton-compte>/step-by-step`

**Action :**

> 1. Clique **Compare & pull request** dans le bandeau jaune (ou **Pull requests → New pull
>    request**, base `main` ← compare `demo-webhook`).
> 2. Titre : `ci(webhook): add webhook check script and relay documentation`
> 3. Clique **Create pull request**.

**Résultat attendu :** dans Jenkins, un nouveau sous-job **`PR-1`** apparaît (onglet *Pull
Requests* de `step-by-step`) et se construit. Le sous-job `demo-webhook` devient grisé : la branche
est désormais construite **en tant que PR**.

---

### Étape 6 : Lis le statut sur la PR

**Où :** la page de la PR, en bas

**Action :**

> Attends la fin du build `PR-1` (≈ 30 s), puis rafraîchis la page.

**Résultat attendu :** **All checks have passed**, avec la ligne
`continuous-integration/jenkins/pr-merge — This commit looks good` ✅.

**Pourquoi `pr-merge` ?** Jenkins n'a pas testé ta branche **seule** : il a testé le résultat **du
merge** avec `main`. Si `main` avait bougé entre-temps, c'est ce mélange qui compte.

**✅ Vérifie que :**
- [ ] ✅ `continuous-integration/jenkins/pr-merge`

---

### Étape 7 : Merge

**Où :** la page de la PR

**Action :**

> Clique **Merge pull request → Confirm merge**, puis **Delete branch**.

**Résultat attendu :** la PR passe en **Merged** (violet). Dans Jenkins, `main` lance un **nouveau
build**, déclenché par le webhook du merge (c'est un push sur `main`). `PR-1` disparaît au scan
suivant.

---

### Étape 8 : Ramène `main` en local

**Où :** la racine du projet

**Action :**

> ```bash
> git switch main
> git pull
> git branch -d demo-webhook
> git log --oneline -3
> ```

**Résultat attendu :** `Fast-forward` ; `Deleted branch demo-webhook` ; en haut du log,
`Merge pull request #1 from <ton-compte>/demo-webhook`, puis ton commit `ci(webhook): …`.

---

## 🧹 Comment tout annuler

Une PR mergée ne s'annule pas : on ferait un commit qui défait le travail (`git revert`). Ici, rien à
annuler : c'est le travail réel de la story.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| La boucle de l'étape 3 finit sans afficher de délai | Aucun build en 60 s : `docker logs cicd-webhook-relay \| tail -3`. `- 200` mais pas de build → montre-moi le *Scan Repository Log* |
| `push -> build start` > 10 s | Note la valeur réelle : on en parle au bilan (smee.io peut être lent par moments) |
| Pas de `PR-1` dans Jenkins | Le webhook n'envoie pas **Pull requests** : GitHub → Settings → Webhooks → coche l'événement |
| Statut ❌ sur la PR | Ouvre la console de `PR-1` et montre-moi la dernière erreur |
| `error: The branch 'demo-webhook' is not fully merged` | Le `git pull` n'a pas eu lieu : refais-le, puis `git branch -d` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Où | Attendu |
|--------------|----|---------|
| Délai | étape 3 | `N` < 10 s |
| Cause | console `demo-webhook` #1 | événement, pas *Branch indexing* |
| Statut PR | PR #1 | ✅ `continuous-integration/jenkins/pr-merge` |
| Merge | GitHub | PR **Merged** ; nouveau build de `main` |
| Local | `git status` | `up to date with 'origin/main'`, propre |

---

## 🎉 Bravo !

Ta **première pull request vérifiée par la CI** ! 🎉 C'est exactement la boucle qu'on vit chaque
jour en équipe. **Jalon J5 atteint.**

Prochain tutoriel : **#010** — Faire le bilan ⏱️ 10 min
