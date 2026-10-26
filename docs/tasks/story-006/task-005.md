# Tutoriel 005 — Story #006 : Écrire `docs/tooling/git-conventions.md`

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 004
**Prérequis :** hook branché
**Exigence TP couverte :** `EXG-REND-01`
**Fichier principal :** `docs/tooling/git-conventions.md` (à créer)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Écrire **en anglais** les règles Git du projet : branches, messages de commit, ajout des fichiers,
vigile.

---

## 🪜 Les étapes

### Étape 1 : Écris la documentation

**Où :** la racine du projet

**Action :**

> Crée `docs/tooling/git-conventions.md` (réponds **Cancel** à IntelliJ). Relis et adapte les
> tournures si tu le souhaites, sans changer les règles :
>
> ```markdown
> # Git Conventions
>
> ## Branches
>
> | Branch | Use |
> |--------|-----|
> | `main` | reference branch, built by the pipeline |
> | `feature/<story>-<short-name>` | one branch per story, e.g. `feature/007-spring-boot-skeleton` |
>
> ## Commit messages
>
> [Conventional Commits](https://www.conventionalcommits.org/), in English: `type(scope): description`.
>
> | Type | Use |
> |------|-----|
> | `feat` | new feature of the application or the pipeline |
> | `fix` | bug fix |
> | `docs` | documentation only |
> | `test` | tests only |
> | `build` | `Dockerfile`, `docker-compose.yml`, `pom.xml` |
> | `ci` | `Jenkinsfile`, shared library, Jenkins / SonarQube / Nexus configuration and checks |
> | `chore` | repository housekeeping (`.gitignore`, hooks) |
>
> Scopes: `app`, `pipeline`, `jenkins`, `sonar`, `nexus`, `tooling`, `envs`, `repo`.
>
> ## Staging files
>
> Files are added by name (`git add <file>`), never with `git add -A` or `git add .`: each commit
> contains exactly what its message describes, and nothing else.
>
> ## Secret scan
>
> `scripts/check-no-secrets.sh` runs before every commit through the hook `.githooks/pre-commit`.
> After a fresh clone, enable the hook once:
>
> ```bash
> git config core.hooksPath .githooks
> ```
>
> The commit is blocked when the files to commit contain a known secret format (private key,
> SonarQube, Jenkins or GitHub token), or a long value assigned to a secret-like name (`password`,
> `token`…) outside Markdown files. Never bypass it with `git commit --no-verify`: remove the value,
> and if a secret was ever committed, revoke it and generate a new one.
> ```

---

### Étape 2 : Repère la dernière phrase

**Où :** ton éditeur

**Action :**

> Relis : « if a secret was ever committed, revoke it and generate a new one ».

**Pourquoi c'est important (et utile à la soutenance) ?** Un secret commité puis supprimé reste dans
l'**historique** : n'importe qui ayant cloné le dépôt peut le retrouver. Le seul vrai remède est de
le rendre **inutile** (le révoquer), pas de le cacher.

---

## 🧹 Comment tout annuler

```bash
rm docs/tooling/git-conventions.md
```

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

- [ ] `docs/tooling/git-conventions.md` écrit, en anglais
- [ ] Tu sais pourquoi on n'utilise jamais `--no-verify`

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tes règles sont écrites : tout le monde (toi dans 3 mois comprise) peut
les suivre.

Prochain tutoriel : **#006** — Le premier commit ⏱️ 15 min

Pause méritée ☕
