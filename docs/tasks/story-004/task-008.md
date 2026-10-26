# Tutoriel 008 — Story #004 : Documenter (en anglais) et faire le bilan

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 007
**Prérequis :** `check-nexus.sh` passe
**Exigence TP couverte :** `EXG-COM-02`, `EXG-COM-19` (part)
**Fichier principal :** `docs/tooling/nexus.md` (à créer)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Écrire la doc **en anglais** qui permet de refaire la configuration de Nexus, puis faire le bilan
de la story.

---

## 🪜 Les étapes

### Étape 1 : Écris la documentation

**Où :** la racine du projet

**Action :**

> Crée `docs/tooling/nexus.md` (réponds **Cancel** à IntelliJ). Relis et adapte les tournures si tu
> le souhaites, sans changer les valeurs :
>
> ```markdown
> # Nexus Configuration
>
> Nexus is configured in its web UI: this configuration is not stored in any file of the
> repository. This page describes how to redo it on a fresh Nexus instance (Community Edition 3.96).
>
> ## 1. Setup wizard
>
> 1. Read the initial password, without saving it:
>    `docker compose exec nexus cat /nexus-data/admin.password`
> 2. Sign in at `http://localhost:8081` as `admin` and follow the wizard:
>    - new admin password: generated and stored in a password manager;
>    - anonymous access: **enabled** (read only), so that staging and production download
>      artifacts without credentials.
> 3. Nexus deletes `/nexus-data/admin.password` when the wizard is completed.
>
> ## 2. Repository
>
> The default `maven-releases` hosted repository is used, unchanged:
>
> | Setting | Value | Why |
> |---------|-------|-----|
> | Version policy | `Release` | no `-SNAPSHOT` versions |
> | Deployment policy | `Disable redeploy` | a published version can never be replaced |
>
> ## 3. Deployer account
>
> | Item | Value |
> |------|-------|
> | Role | `step-by-step-deployer`: `nx-repository-view-maven2-maven-releases-` `add`, `edit`, `read`, `browse` (no `delete`) |
> | User | `nexus-deployer`, role `step-by-step-deployer` only |
> | Password | generated, stored **only** in the Jenkins credential `nexus-deploy-creds` (Username with password) |
>
> If the password is lost: reset it in Nexus, then update the Jenkins credential.
>
> ## 4. Immutability check
>
> A witness `.pom` published twice to the same version: the first upload returns `201`, the second
> returns `409` (Conflict). The witness is then deleted.
>
> ## 5. Verification
>
> ```bash
> ./scripts/check-nexus.sh
> ```
>
> The script needs no credentials. It checks that Nexus is up and writable, that anonymous users
> can read `maven-releases` but cannot write to it, and that the setup wizard is completed. It
> displays "Nexus is compliant" and exits with code 0; it exits with code 1 immediately if Nexus
> does not answer, or after all checks if any other check fails.
> ```

---

### Étape 2 : Vérifie l'absence de secrets

**Où :** la racine du projet

**Action :**

> ```bash
> grep -rniE '(password|passwd|secret|token|api[_-]?key)[[:space:]]*[:=][[:space:]]*[^<{$[:space:]]' \
>   scripts/check-nexus.sh docs/tooling/nexus.md 2>/dev/null || echo "OK : aucun secret detecte"
> ```

**Résultat attendu :** **une seule** fausse alerte (vérifiée à la génération du tutoriel) :

```
docs/tooling/nexus.md:11:   - new admin password: generated and stored in a password manager;
```

Après `password:`, il y a une **explication**, pas une valeur. Si tu as reformulé la doc, le numéro de
ligne ou le texte peuvent changer : lis chaque ligne signalée.

---

### Étape 3 : Regarde l'état du dépôt

**Où :** la racine du projet

**Action :**

> ```bash
> git status --porcelain
> ```

**Résultat attendu :** aucune nouvelle ligne par rapport à la story 003 (`scripts/` et `docs/` déjà
non suivis) ; aucun fichier témoin.

---

## ✅ Avant de passer à la story suivante — bilan de la story 004

| Critère | Preuve |
|---------|--------|
| Assistant terminé, admin dans Bitwarden, lecture anonyme | `OK  setup wizard completed`, `OK  anonymous read…` |
| `maven-releases` en *Disable redeploy* | 2e publication du témoin : `409` |
| Rôle et compte dédiés, credential Jenkins | `step-by-step-deployer`, `nexus-deployer`, `nexus-deploy-creds` |
| Témoin nettoyé | `404` après suppression |
| Script conforme, échoue quand il le doit | tutoriel 007 |
| Doc en anglais | `docs/tooling/nexus.md` |

### 🛡️ Garde-fous du TP

- [ ] Aucun mot de passe dans un fichier du projet
- [ ] Aucun build ne s'est exécuté sur le controller Jenkins
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien dans cette story. En **story 006**, le premier commit inclura notamment
`scripts/check-nexus.sh` et `docs/tooling/nexus.md`, avec un message du type
`ci(nexus): add nexus check script and documentation`.

---

## 🎉 Bravo !

Tu as terminé la story 004 ! Les trois outils de la chaîne sont configurés et vérifiables en une
commande chacun.

**Prochaine story :** Story 005 — Créer l'agent Jenkins. Ses tutoriels seront générés avec
`/ tasks-fr 005`.

Pause méritée ☕
