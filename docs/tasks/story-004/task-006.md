# Tutoriel 006 — Story #004 : Écrire `scripts/check-nexus.sh`

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 001
**Prérequis :** aucun (ce tutoriel écrit un fichier ; il sera lancé au tutoriel 007)
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `scripts/check-nexus.sh` (à créer)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Écrire un script **sans identifiants** qui vérifie : Nexus répond et accepte les écritures, la
lecture anonyme est ouverte, l'écriture anonyme est fermée, l'assistant est terminé.

## 📋 Ce que tu vas apprendre

- Comparer des codes HTTP dans un script
- Pourquoi `docker compose exec` a besoin de `< /dev/null` dans un script
- Écrire un script de vérification qui n'a besoin d'aucun secret

---

## 🪜 Les étapes

### Étape 1 : Écris le script

**Où :** la racine du projet

**Action :**

> Crée `scripts/check-nexus.sh` avec ce contenu **exact** (réponds **Cancel** à IntelliJ) :
>
> ```bash
> #!/usr/bin/env bash
> # Checks the Nexus configuration (story 004).
> # Needs no credentials: everything checked here is public on purpose.
> # Run it from the project root (it uses docker compose for the last check).
> set -euo pipefail
>
> NEXUS_URL="${NEXUS_URL:-http://localhost:8081}"
> REPO="${NEXUS_REPO:-maven-releases}"
>
> # HTTP code of an anonymous request ("000" when Nexus does not answer)
> http_code() {
>   curl -s -o /dev/null -w '%{http_code}' "$@" || true
> }
>
> errors=0
>
> # 1. Nexus answers: no point going further without it
> code="$(http_code "$NEXUS_URL/service/rest/v1/status")"
> if [ "$code" = "200" ]; then
>   echo "OK  Nexus is up"
> else
>   echo "KO  Nexus does not answer (HTTP $code)"
>   exit 1
> fi
>
> # 2. Nexus accepts writes (disk not full, not in read-only mode)
> code="$(http_code "$NEXUS_URL/service/rest/v1/status/writable")"
> if [ "$code" = "200" ]; then
>   echo "OK  Nexus is writable"
> else
>   echo "KO  Nexus is not writable (HTTP $code)"
>   errors=$((errors + 1))
> fi
>
> # 3. Anonymous users can read the repository (staging and production download without credentials)
> code="$(http_code "$NEXUS_URL/repository/$REPO/")"
> if [ "$code" = "200" ]; then
>   echo "OK  anonymous read on $REPO"
> else
>   echo "KO  anonymous read on $REPO refused (HTTP $code)"
>   errors=$((errors + 1))
> fi
>
> # 4. Anonymous users cannot publish
> code="$(http_code -X PUT --data check "$NEXUS_URL/repository/$REPO/check/anonymous/1/anonymous-1.pom")"
> if [ "$code" = "401" ]; then
>   echo "OK  anonymous write on $REPO refused"
> else
>   echo "KO  anonymous write on $REPO not refused (HTTP $code)"
>   errors=$((errors + 1))
> fi
>
> # 5. The setup wizard is completed: Nexus deletes its initial password file at the end.
> #    < /dev/null: docker compose exec would otherwise read (and swallow) this script's stdin.
> if docker compose exec -T nexus test ! -f /nexus-data/admin.password < /dev/null; then
>   echo "OK  setup wizard completed"
> else
>   echo "KO  setup wizard not completed (/nexus-data/admin.password still exists)"
>   errors=$((errors + 1))
> fi
>
> if [ "$errors" -eq 0 ]; then
>   echo "Nexus is compliant"
> else
>   echo "$errors check(s) failed"
>   exit 1
> fi
> ```

**✅ Vérifie que :**
- [ ] Le script ne contient **aucun** identifiant

---

### Étape 2 : Repère les nouveautés

**Où :** ton éditeur

| Passage | Ce qu'il fait |
|---------|---------------|
| `http_code()` sans `-f` | on veut **le code**, pas un échec : `-w '%{http_code}'` l'affiche, `000` si Nexus ne répond pas |
| `[ "$code" = "401" ]` au contrôle 4 | ici, le **bon** résultat est un refus |
| `REPO="${NEXUS_REPO:-maven-releases}"` | dépôt modifiable pour un test, sans toucher au script (tutoriel 007) |
| `< /dev/null` | `docker compose exec` lit l'entrée standard : sans cette redirection, il peut « avaler » la suite du script, qui s'arrête alors **en silence avec le code 0** |

**Pourquoi ce dernier point est important ?** Ce piège a été **découvert en testant ce script** sur
ton poste, à la génération du tutoriel : deux contrôles en échec, et pourtant un code de sortie `0`.
Un pipeline aurait cru que tout allait bien.

**✅ Vérifie que :**
- [ ] Tu sais dire pourquoi le contrôle 4 attend `401`

---

### Étape 3 : Rends-le exécutable et vérifie la syntaxe

**Où :** la racine du projet

**Action :**

> ```bash
> chmod +x scripts/check-nexus.sh
> bash -n scripts/check-nexus.sh && echo "syntaxe OK"
> ```

**Résultat attendu :** `syntaxe OK`

---

## 🧹 Comment tout annuler

```bash
rm scripts/check-nexus.sh
```

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

- [ ] `scripts/check-nexus.sh` écrit, exécutable, syntaxe OK
- [ ] Aucun identifiant dans le script
- [ ] `git status` : `scripts/` toujours non suivi

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Vérifier une configuration sans aucun secret
- ✨ Attendre un refus comme bon résultat
- ✨ Éviter le piège de l'entrée standard avec `docker compose exec`

Prochain tutoriel : **#007** — Lancer le script, puis le faire échouer exprès ⏱️ 15 min

Pause méritée ☕
