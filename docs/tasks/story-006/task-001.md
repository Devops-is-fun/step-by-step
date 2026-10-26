# Tutoriel 001 — Story #006 : Régler ton identité Git et nettoyer l'index

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** story 005 terminée
**Prérequis :** l'e-mail de ton compte GitHub de l'école sous la main
**Exigence TP couverte :** `EXG-REND-01`
**Fichier principal :** `.git/config` (via `git config`)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Signer les commits de ce projet avec ton identité **école**, sans toucher à tes réglages Git
globaux (Vates), puis retirer de l'index les fichiers d'IntelliJ ajoutés par erreur.

## 📋 Ce que tu vas apprendre

- La différence entre réglage Git **global** et **local** (un seul dépôt)
- Ce qu'est l'**index** (la « zone de préparation »)
- Retirer un fichier de l'index **sans** le supprimer du disque

---

## 🪜 Les étapes

### Étape 1 : Constate l'état « avant »

**Où :** la racine du projet

**Action :**

> ```bash
> git config --show-origin user.name; git config --show-origin user.email
> ```

**Résultat attendu :** **aucune** ligne pour `user.name`, puis
`file:/home/geraldine_rouy/.gitconfig  geraldine.rouy@…vates.tech`.

**Pourquoi c'est un problème ?** Sans `user.name`, Git **refuse** de commiter. Et l'e-mail vient de
ton réglage **global** : tous tes commits d'école porteraient ton adresse professionnelle.

---

### Étape 2 : Règle ton identité pour ce dépôt seulement

**Où :** la racine du projet

**Action :**

> Remplace les deux valeurs par **les tiennes** (ton nom, et l'e-mail de ton compte GitHub de
> l'école) :
>
> ```bash
> git config user.name "Prénom Nom"
> git config user.email "ton-email-ecole@example.com"
> ```

**Pourquoi sans `--global` ?** Sans option, `git config` écrit dans `.git/config` : le réglage ne
vaut **que** pour ce dépôt. Ton travail chez Vates garde son identité.

**Astuce :** GitHub propose aussi une adresse qui masque ton vrai e-mail (`…@users.noreply.github.com`),
visible dans **Settings → Emails** de ton compte. Les deux conviennent.

---

### Étape 3 : Vérifie

**Où :** la racine du projet

**Action :**

> ```bash
> git config --show-origin user.name; git config --show-origin user.email
> ```

**Résultat attendu :** deux lignes qui commencent par `file:.git/config`, avec tes valeurs d'école.

**✅ Vérifie que :**
- [ ] Les deux lignes viennent de `.git/config` (pas de `~/.gitconfig`)

---

### Étape 4 : Regarde ce qui est dans l'index

**Où :** la racine du projet

**Action :**

> ```bash
> git status --short | grep '^A'
> ```

**Résultat attendu :**

```
AM .gitignore
A  .idea/.gitignore
A  .idea/material_theme_project_new.xml
A  .idea/misc.xml
A  .idea/modules.xml
A  .idea/vcs.xml
A  ci-cd-project.iml
```

**Pourquoi :** `A` = « ajouté à l'index », prêt à partir au prochain commit. IntelliJ les y a mis
quand tu as répondu oui à « Add file to Git? » (story 001). `.gitignore` doit y être ; les fichiers
d'IntelliJ, **non** : ce sont tes réglages d'éditeur, pas le projet.

**Image :** l'index, c'est le **carton** qu'on prépare avant de l'envoyer. Des objets y sont tombés
par erreur ; on les ressort avant de fermer le carton.

---

### Étape 5 : Ressors les fichiers d'IntelliJ du carton

**Où :** la racine du projet

**Action :**

> ```bash
> git rm -r --cached .idea ci-cd-project.iml
> ```

**Résultat attendu :** 6 lignes `rm '…'`.

**Pourquoi `--cached` ?** Il retire **seulement de l'index**. Sans lui, Git supprimerait aussi les
fichiers du disque, et IntelliJ perdrait ta configuration.

**Comment annuler :** `git add -f .idea ci-cd-project.iml` (à ne pas faire).

---

### Étape 6 : Vérifie

**Où :** la racine du projet

**Action :**

> ```bash
> git status --short | grep '^A'
> ls .idea | head -3
> ```

**Résultat attendu :** une seule ligne `AM .gitignore`, puis les fichiers de `.idea/` toujours
présents sur le disque.

**✅ Vérifie que :**
- [ ] Plus aucun `.idea` ni `.iml` en `A`
- [ ] `.idea/` existe toujours (IntelliJ fonctionne)

---

## 🧹 Comment tout annuler

```bash
git config --unset user.name
git config --unset user.email
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `fatal: pathspec '.idea' did not match any files` | Déjà retiré : passe à l'étape 6 |
| Les lignes de l'étape 3 commencent par `file:/home/…/.gitconfig` | Tu as tapé `--global` : refais l'étape 2 sans, puis `git config --global --unset user.name` |
| IntelliJ redemande « Add file to Git? » | Réponds **Cancel** (et coche « Don't ask again » si proposé) |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Identité locale | `git config --show-origin user.email` | `file:.git/config` + e-mail d'école |
| Index propre | `git status --short \| grep '^A'` | seulement `.gitignore` |

---

## 📝 Journal des décisions

Décisions prises le 26/10/2026, au cadrage de `tasks-fr 006` :

| # | Question | Réponse | Raison |
|---|----------|---------|--------|
| 1 | Identité Git | `1a` — identité d'école, réglée pour ce dépôt seulement | pas d'adresse pro dans un projet d'école ; réglages globaux inchangés |
| 2 | Dépôt distant | `2a` — GitHub privé, SSH via l'alias `github` (clé `id_esgi`) | clé déjà prête ; aucun jeton à gérer |
| 3 | Contenu | `3a` (supposé : réponse saisie « 1a ») — `.opencode/skills/` et le PDF versionnés, pas `node_modules` | dépôt privé ; montre la méthode de travail |
| 4 | Historique | `4a` — un commit par story | Conventional Commits notés ; l'historique raconte le jalon 0 |
| 5 | Scan des secrets | `5a` — hook versionné `.githooks/pre-commit` | automatique : rien à retenir |

Vérifié à la génération, sur une **copie jetable** de ton projet :
- les 7 commits passent le vigile, `git status` est vide à la fin, aucun secret dans l'historique ;
- le scan ne donne **aucune** fausse alerte sur tes ~100 fichiers, et détecte : clé privée, jetons
  SonarQube et Jenkins, valeur longue dans un `.yml` ou un `.env` ; un commit piégé est **bloqué** ;
- `.opencode/.gitignore` s'ignore lui-même (il contient la ligne `.gitignore`) : on versionne donc
  `.opencode/skills/` seulement.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Régler Git pour un seul dépôt
- ✨ Sortir un fichier de l'index sans le perdre

Prochain tutoriel : **#002** — Compléter le `.gitignore` ⏱️ 15 min

Pause méritée ☕
