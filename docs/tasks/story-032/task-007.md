# Tutoriel 007 — Story #032 : Ta première PR, et le bilan de la story

**Pour :** 👋 Morgan (étapes 1 à 5), puis Géraldine (étape 4)
**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 006
**Prérequis :** **préviens Géraldine** : sa stack (Jenkins + relais) doit être allumée pour que ta PR
reçoive son statut
**Exigence TP couverte :** `EXG-COM-07`, `EXG-REND-06`
**Fichier principal :** ta PR sur `https://github.com/GeraldineRouy/step-by-step/pulls`

> **Répertoire de travail :** la racine du dépôt.

---

## 🎯 Objectif

Faire le parcours complet d'une contribution : branche → commit vérifié par le vigile → PR → statut
Jenkins → relecture → merge.

---

## 🪜 Les étapes

### Étape 1 : Crée ta branche

**Où :** la racine du dépôt

**Action :**

> ```bash
> git switch main && git pull
> git switch -c docs-onboarding-notes
> ```

---

### Étape 2 : Note tes écarts du premier lancement

**Où :** `docs/team/onboarding.md`, section **« Notes du premier lancement (Morgan) »**

**Action :**

> 1. Remplace la ligne en italique par les écarts de ton carnet (tutoriel 006), en liste, ou par
>    « Aucun écart. ».
> 2. Si un écart est une correction **simple** du README (une phrase, une commande), fais-la aussi
>    dans `README.md` **et** `README.en.md`. Les écarts plus gros attendront la story 025.

**Pourquoi c'est utile ?** Tu es la première personne à relancer le projet sans l'avoir construit :
tes écarts sont exactement ce qu'un correcteur rencontrerait. Ils nourriront la story 025.

---

### Étape 3 : Commite et pousse

**Où :** la racine du dépôt

**Action :**

> ```bash
> git add docs/team/onboarding.md    # + README.md README.en.md si tu les as modifiés
> git status --short | grep '^[AMD]'
> git commit -m "docs(team): add first-run notes from onboarding"
> git push -u origin docs-onboarding-notes
> ```

**Résultat attendu :** **avant** le commit, la ligne `OK  no secret found in the files to commit` :
c'est le vigile que tu as activé au tutoriel 005. Puis `[new branch]`.

**Pourquoi jamais `git add .` ?** On ajoute les fichiers **par leur nom** : un fichier oublié dans le
dossier (notes, `.env`, export d'IA) ne part jamais par accident.

---

### Étape 4 : PR, statut, relecture

**Où :** GitHub

**Action :**

> **Toi (Morgan)** : **Compare & pull request** → **Create pull request**. Attends ≈ 1 minute :
> `continuous-integration/jenkins/pr-merge — This commit looks good` ✅.
>
> **Géraldine** : ouvre la PR → onglet **Files changed** → relis → **Review changes → Approve →
> Submit review**.

**Pourquoi la relecture, alors qu'elle n'est pas obligatoire ?** C'est votre règle d'équipe : chacun
doit pouvoir expliquer **toute** la chaîne, y compris ce que l'autre a écrit. Relire, c'est le moyen
le plus simple d'y arriver.

**Pas de statut après 2 minutes ?** La stack de Géraldine est éteinte, ou le relais est arrêté. Quand
elle la rallume, Jenkins rattrape la PR (scan).

---

### Étape 5 : Merge

**Où :** GitHub, puis la racine du dépôt

**Action :**

> **Merge pull request → Confirm merge → Delete branch**, puis :
>
> ```bash
> git switch main && git pull
> git branch -d docs-onboarding-notes
> ```

**Résultat attendu :** la PR est **Merged** ; tes notes sont dans `docs/team/onboarding.md` sur `main`.

---

## ✅ Bilan de la story 032

| Critère d'acceptation | Preuve | Tutoriel |
|-----------------------|--------|----------|
| `CONTRIBUTING.md` : flux, hook, secrets, IA, conventions | fichier sur `main` | 001, 003 |
| `main` protégée (PR + statut ✅) | ruleset `protect-main` ; push direct refusé | 004 |
| Morgan : clone, hook, première PR ✅ relue et mergée | PR mergée, approbation | 005, 007 |
| Morgan : application relancée sur sa machine | `UP` en JAR et en conteneur | 006 |
| Visite guidée | `docs/team/onboarding.md` | 002 |

### 🛡️ Garde-fous du TP

- [ ] Aucun secret partagé par fichier ni par messagerie
- [ ] Le vigile est actif chez **les deux** membres (`git config core.hooksPath`)

---

## 🎉 Bravo à vous deux !

La story 032 est terminée : vous êtes une **équipe** avec des règles, une serrure sur `main` et une
boucle de relecture. 🎉

**La suite, en parallèle :**

| Géraldine | Morgan |
|-----------|--------|
| Story 011 — Jenkinsfile de CI (`/ tasks-fr 011`) | Story 015 — Staging idempotent (`/ tasks-fr 015`) |
