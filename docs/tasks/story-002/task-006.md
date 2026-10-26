# Tutoriel 006 — Story #002 : Lancer le script, puis le faire échouer exprès

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriels 003 et 005
**Prérequis :** `JENKINS_USER` et `JENKINS_TOKEN` chargés dans le terminal (sinon, refais les
étapes 3 et 4 du tutoriel 004)
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `scripts/check-jenkins.sh`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Voir le script passer au vert, puis **le faire échouer exprès**, pour prouver qu'il détecte vraiment
les problèmes.

## 📋 Ce que tu vas apprendre

- Lire un code de sortie avec `echo $?`
- Pourquoi un test qui n'a jamais échoué ne prouve rien
- Vérifier qu'aucun secret n'est resté dans l'historique du terminal

---

## 🪜 Les étapes

### Étape 1 : Vérifie que le jeton est chargé

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> echo "${#JENKINS_TOKEN}"
> ```

**Résultat attendu :** `34`

**✅ Vérifie que :**
- [ ] La sortie est `34` ; si c'est `0`, refais les étapes 3 et 4 du tutoriel 004

---

### Étape 2 : Lance le script

**Où :** la racine du projet

**Action :**

> Exécute :
>
> ```bash
> ./scripts/check-jenkins.sh; echo "code de sortie : $?"
> ```

**Résultat attendu :**

```
OK  token authentication
OK  CSRF protection (crumb available)
OK  plugin git
OK  plugin github-branch-source
OK  plugin workflow-aggregator
OK  plugin pipeline-graph-view
OK  plugin sonar
OK  plugin credentials-binding
OK  plugin junit
OK  plugin coverage
OK  plugin warnings-ng
OK  plugin pipeline-utility-steps
Jenkins is compliant
code de sortie : 0
```

**Pourquoi le code de sortie ?** `0` veut dire « succès », tout autre nombre « échec ». C'est ce
code, pas le texte affiché, que Jenkins regardera quand le script tournera dans un pipeline.

**✅ Vérifie que :**
- [ ] 12 lignes `OK`, puis `Jenkins is compliant`
- [ ] `code de sortie : 0`

---

### Étape 3 : Fais-le échouer avec un faux jeton

**Où :** la racine du projet

**Action :**

> Exécute :
>
> ```bash
> JENKINS_TOKEN=faux ./scripts/check-jenkins.sh; echo "code de sortie : $?"
> ```

**Résultat attendu :**

```
curl: (22) The requested URL returned error: 401
KO  token authentication refused
code de sortie : 1
```

**Pourquoi `JENKINS_TOKEN=faux` devant la commande ?** La variable n'est remplacée **que pour cette
commande**. Ton vrai jeton reste chargé pour la suite.

**✅ Vérifie que :**
- [ ] `KO  token authentication refused`
- [ ] `code de sortie : 1`

---

### Étape 4 : Fais-le échouer sans jeton

**Où :** la racine du projet

**Action :**

> Exécute :
>
> ```bash
> env -u JENKINS_TOKEN ./scripts/check-jenkins.sh; echo "code de sortie : $?"
> ```

**Résultat attendu :** une ligne qui se termine par
`JENKINS_TOKEN is not set (see docs/tooling/jenkins-configuration.md)`, puis `code de sortie : 1`.

**Pourquoi tester ces échecs ?** Un script qui affiche toujours « OK » ne prouve rien : peut-être
qu'il ne vérifie rien du tout. En le voyant **échouer quand il le doit**, tu prouves qu'il vérifie
vraiment. C'est aussi ce que l'énoncé demande à la soutenance avec l'« échec volontaire bloqué ».

**✅ Vérifie que :**
- [ ] Le message nomme la variable manquante
- [ ] `code de sortie : 1`

---

### Étape 5 : Vérifie l'historique du terminal

**Où :** la racine du projet

**Action :**

> Exécute :
>
> ```bash
> history | grep -c -F "$JENKINS_TOKEN"
> ```

**Résultat attendu :** `0`

**Pourquoi :** cette commande compte les lignes de l'historique qui contiennent la **valeur** de ton
jeton, sans l'afficher. `0` prouve que `read -rs` a fait son travail.

**✅ Vérifie que :**
- [ ] La sortie est `0`

---

## 🧹 Comment tout annuler

Ce tutoriel ne modifie rien : il lance un script en lecture seule sur l'API de Jenkins.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `KO  plugin ... missing` à l'étape 2 | Le plugin n'est pas installé : reprends le tutoriel 003, étapes 1 à 7, pour ce plugin |
| `KO  token authentication` à l'étape 2 | Le jeton n'est plus chargé ou a été révoqué : `echo "${#JENKINS_TOKEN}"`, puis tutoriel 004 |
| `curl: (7) Failed to connect` | Jenkins ne tourne pas : `docker compose ps` |
| `curl: (3) bad range in URL` | L'option `-g` manque dans la fonction `api()` : reprends le tutoriel 005, étape 2 |
| `history` affiche `1` ou plus à l'étape 5 | Le jeton a été tapé dans une commande. Révoque-le dans Jenkins, génère-en un nouveau (tutoriel 004), puis nettoie l'historique avec `history -c` |

---

## 📚 Aide-mémoire des commandes de vérification

| Besoin | Commande |
|--------|----------|
| Lancer la vérification | `./scripts/check-jenkins.sh` |
| Voir le code de sortie de la dernière commande | `echo $?` |
| Tester avec une valeur temporaire | `JENKINS_TOKEN=faux ./scripts/check-jenkins.sh` |
| Tester sans une variable | `env -u JENKINS_TOKEN ./scripts/check-jenkins.sh` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

### Ce que tu dois avoir fait

- [ ] Étape 1 → jeton chargé vérifié
- [ ] Étape 2 → script lancé, tout au vert
- [ ] Étape 3 → échec sur faux jeton
- [ ] Étape 4 → échec sans jeton
- [ ] Étape 5 → historique propre

### Ce que tu dois avoir vérifié

| Vérification | Comment vérifier | Attendu |
|--------------|------------------|---------|
| Jenkins is compliant | `./scripts/check-jenkins.sh` | `Jenkins is compliant`, code `0` |
| Faux jeton détecté | `JENKINS_TOKEN=faux ./scripts/check-jenkins.sh` | code `1` |
| Jeton absent détecté | `env -u JENKINS_TOKEN ./scripts/check-jenkins.sh` | code `1` |
| Historique propre | `history \| grep -c -F "$JENKINS_TOKEN"` | `0` |

### 🛡️ Garde-fous du TP

- [ ] Le jeton n'apparaît ni dans un fichier ni dans l'historique
- [ ] Aucun build ne s'est exécuté sur le controller Jenkins
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien : le premier commit du projet est fait en **story 006**.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Lire un code de sortie
- ✨ Prouver qu'un test fonctionne en le faisant échouer
- ✨ Vérifier qu'un secret n'a laissé aucune trace

Prochain tutoriel : **#007** — Documenter la configuration et faire le bilan ⏱️ 20 min

Pause méritée ☕
