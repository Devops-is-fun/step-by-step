# Tutoriel 002 — Story #006 : Compléter le `.gitignore`

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 001
**Prérequis :** index nettoyé
**Exigence TP couverte :** `EXG-COM-19` (part)
**Fichier principal :** `.gitignore`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Ajouter au `.gitignore` les entrées de **clés et secrets** demandées par la story, puis **prouver**
que chaque règle fonctionne.

## 📋 Ce que tu vas apprendre

- La différence entre `data/` et `/data/` dans un `.gitignore`
- Prouver qu'une règle marche avec `git check-ignore -v`

---

## 🪜 Les étapes

### Étape 1 : Ajoute le bloc

**Où :** `.gitignore`

**Action :**

> Ajoute ce bloc **à la fin** du fichier :
>
> ```gitignore
>
> # Keys and secrets (story 006)
> *.key
> *.pem
> id_rsa*
> id_ed25519*
> secrets/
> /data/
> ```

**Pourquoi ces lignes ?**

| Règle | Ce qu'elle bloque |
|-------|-------------------|
| `*.key`, `*.pem` | fichiers de clés et de certificats |
| `id_rsa*`, `id_ed25519*` | paires de clés SSH (la story 005 a utilisé **ed25519** : la story n'avait prévu que `id_rsa*`) |
| `secrets/` | tout dossier nommé `secrets` |
| `/data/` | **seulement** le dossier `data/` à la racine |

**Pourquoi `/data/` et pas `data/` ?** Sans `/`, la règle ignorerait **tous** les dossiers `data`,
même un futur package Java `src/main/java/…/data/`. Le `/` au début veut dire « à la racine ».

---

### Étape 2 : Prouve que les règles marchent

**Où :** la racine du projet

**Action :**

> ```bash
> git check-ignore -v .env .env.local target/ .idea/ ci-cd-project.iml secrets/ id_ed25519 x.pem x.key data/ notes-story-005.txt
> ```

**Résultat attendu :** **11 lignes**, une par chemin, de la forme
`.gitignore:<ligne>:<règle>    <chemin>`. Par exemple : `.gitignore:41:id_ed25519*    id_ed25519`.

**Pourquoi c'est une preuve ?** `check-ignore -v` dit **quelle règle** ignore chaque chemin. Les
chemins n'ont pas besoin d'exister : on teste les règles, pas les fichiers.

**✅ Vérifie que :**
- [ ] 11 lignes, aucun chemin oublié

---

### Étape 3 : Vérifie ce qui ne doit **pas** être ignoré

**Où :** la racine du projet

**Action :**

> ```bash
> git check-ignore -q .env.example || echo ".env.example is NOT ignored"
> git check-ignore -q src/main/java/app/data/X.java || echo "src/.../data/ is NOT ignored"
> ```

**Résultat attendu :** les deux messages `… is NOT ignored`.

**Pourquoi :** `.env.example` (un modèle **sans** valeurs) doit pouvoir être versionné, grâce à la
règle `!.env.example` de la story 001. Et le `/` de `/data/` protège bien les packages Java.

---

## 🧹 Comment tout annuler

Supprime le bloc « Keys and secrets » de `.gitignore`.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Moins de 11 lignes à l'étape 2 | Le chemin manquant n'a pas de règle : compare le bloc à l'étape 1 |
| `src/.../data/` est ignoré | Tu as écrit `data/` au lieu de `/data/` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Attendu |
|--------------|---------|
| `git check-ignore -v …` (étape 2) | 11 lignes |
| `.env.example`, `src/…/data/` | **non** ignorés |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Ancrer une règle à la racine avec `/`
- ✨ Prouver un `.gitignore` au lieu d'y croire

Prochain tutoriel : **#003** — Écrire `scripts/check-no-secrets.sh` ⏱️ 20 min

Pause méritée ☕
