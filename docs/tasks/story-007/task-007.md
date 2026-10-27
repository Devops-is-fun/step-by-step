# Tutoriel 007 — Story #007 : Apprendre au vigile à ignorer `mvnw`

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 10 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 001
**Prérequis :** `mvnw` présent à la racine
**Exigence TP couverte :** `EXG-COM-19` (part)
**Fichier principal :** `scripts/check-no-secrets.sh`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Éviter que le vigile **bloque** le commit de l'application à cause d'une fausse alerte dans `mvnw`.

## 📋 Ce que tu vas apprendre

- Reconnaître une **fausse alerte** et la traiter **au plus étroit**
- Exclure un fichier d'une recherche `git grep` (*pathspec*)

---

## 🪜 Les étapes

### Étape 1 : Constate la fausse alerte

**Où :** la racine du projet

**Action :**

> ```bash
> grep -n 'MVNW_PASSWORD:+' mvnw
> ```

**Résultat attendu :** `189:case "${MVNW_PASSWORD:+has-password}" in`

**Pourquoi le vigile s'y trompe ?** Son filet « affectation » cherche `password`, puis `:` ou `=`,
puis 8 caractères. `PASSWORD:+has-password}` coche les trois cases. Or ce n'est **pas** une valeur :
c'est une syntaxe bash qui veut dire « si la variable `MVNW_PASSWORD` existe, écris `has-password` ».

---

### Étape 2 : Exclus `mvnw` du filet « affectation »

**Où :** `scripts/check-no-secrets.sh`, dans IntelliJ

**Action :**

> Repère cette ligne (vers la ligne 29) :
>
> ```bash
> if scan -iE -e "$ASSIGN" -- . ':(exclude)*.md'; then
> ```
>
> Remplace-la par :
>
> ```bash
> if scan -iE -e "$ASSIGN" -- . ':(exclude)*.md' ':(exclude)mvnw'; then
> ```
>
> Puis, deux lignes **au-dessus**, remplace le commentaire
> `#    (Markdown excluded: its prose explains secrets without containing any)` par :
>
> ```bash
> #    (Markdown excluded: its prose explains secrets without containing any;
> #     mvnw excluded: Maven's own script, "${MVNW_PASSWORD:+...}" is bash syntax, not a value)
> ```
>
> **Enregistre** (Ctrl+S).

**Pourquoi exclure **le fichier** plutôt que changer le motif ?** Changer le motif (accepter `+`
après `:`) ouvrirait un trou **partout** : `password=+Xk9fLq2…` passerait. Exclure `mvnw` ne retire
qu'un fichier qu'on n'écrit pas soi-même, et **seulement** du filet « affectation » : le filet
« forme » (clés privées, jetons) le fouille toujours.

---

### Étape 3 : Prouve que l'exclusion marche… et que le piège marche encore

**Où :** la racine du projet

**Action :**

> ```bash
> git add mvnw
> ./scripts/check-no-secrets.sh; echo "exit code: $?"
> printf 'password: Xk9fLq2mZpWw\n' > trap.yml
> git add trap.yml
> ./scripts/check-no-secrets.sh; echo "exit code: $?"
> git rm --cached mvnw trap.yml
> rm trap.yml
> ```

**Résultat attendu :**

1. `OK  no secret found in the files to commit` puis `exit code: 0` (`mvnw` ne déclenche plus rien) ;
2. `trap.yml:1:password: Xk9fLq2mZpWw`, `KO  possible secret(s) above…` puis `exit code: 1` (le
   piège est toujours attrapé) ;
3. `rm 'mvnw'` et `rm 'trap.yml'` : le carton est vidé, `mvnw` reste sur le disque.

**Pourquoi `git rm --cached` ?** Le vigile ne fouille que **le carton** (l'index) : on y met les
fichiers le temps du test, puis on les retire. `mvnw` reviendra au tutoriel 009, avec son commit.

**✅ Vérifie que :**
- [ ] Premier passage : `exit code: 0`
- [ ] Second passage : `exit code: 1`
- [ ] `git status --short | grep -c trap` affiche `0`

---

## 🧹 Comment tout annuler

```bash
git restore scripts/check-no-secrets.sh
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Le 1er passage dit encore `mvnw:189:…` | La ligne `if scan -iE …` n'a pas été enregistrée, ou il manque un `'` autour de `:(exclude)mvnw` |
| `KO  scan failed (git grep exit code 128)` | Une faute dans la *pathspec* : recopie exactement `':(exclude)mvnw'` |
| `git status` montre encore `A  mvnw` | `git rm --cached mvnw` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Comment | Attendu |
|--------------|---------|---------|
| `mvnw` ignoré du filet « affectation » | 1er passage | `exit code: 0` |
| Piège toujours attrapé | 2e passage | `exit code: 1` |
| Carton vide | `git status --short \| grep -c '^A'` | `0` |

---

## 🎉 Bravo !

Ton vigile est plus fin **sans** être plus laxiste. 🎉

Prochain tutoriel : **#008** — Compléter le README (FR + EN) ⏱️ 15 min
