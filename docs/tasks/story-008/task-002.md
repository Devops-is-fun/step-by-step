# Tutoriel 002 — Story #008 : Écrire `compose-test.yml`

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 001
**Prérequis :** le port 5433 est libre (`ss -ltn | grep -c ':5433 '` affiche `0`)
**Exigence TP couverte :** prépare `EXG-COM-10`
**Fichier principal :** `src/test/resources/compose-test.yml`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Écrire la recette du **banc d'essai** : une base PostgreSQL 16 jetable, que les tests d'intégration
(et plus tard Jenkins) allument et éteignent à volonté.

## 📋 Ce que tu vas apprendre

- Donner un **nom de projet** à un fichier Compose
- Rendre une base **jetable** avec `tmpfs`
- Écrire un **healthcheck** : comment Docker sait qu'un service est **prêt**, pas seulement démarré

---

## 🪜 Les étapes

### Étape 1 : Crée le fichier

**Où :** `src/test/resources/`, dans IntelliJ (le dossier existe déjà, il contient `application-test.yml`)

**Action :**

> Crée `src/test/resources/compose-test.yml` avec ce contenu **exact** :
>
> ```yaml
> # Throwaway PostgreSQL for the integration tests (story 008).
> # Test-only credentials: this database holds no real data and lives only during the tests.
> name: stepbystep-test
>
> services:
>   postgres:
>     image: postgres:16.15-alpine3.24
>     environment:
>       POSTGRES_DB: stepbystep_test
>       POSTGRES_USER: stepbystep
>       POSTGRES_PASSWORD: test
>     ports:
>       - "127.0.0.1:5433:5432"
>     tmpfs:
>       - /var/lib/postgresql/data
>     healthcheck:
>       test: ["CMD-SHELL", "pg_isready -U stepbystep -d stepbystep_test"]
>       interval: 5s
>       timeout: 3s
>       retries: 10
> ```
>
> **Enregistre** (Ctrl+S). Si IntelliJ demande **« Add file to Git? »** : **Cancel**.

**Clés à repérer :** `name`, `services.postgres` → `image`, `environment`, `ports`, `tmpfs`,
`healthcheck`.

**Pourquoi `name: stepbystep-test` ?** Sans nom, Compose prend celui du **dossier** du fichier :
`resources`. Peu parlant, et il pourrait entrer en collision avec un autre projet. Le nom donne aussi
celui du réseau, `stepbystep-test_default`, que tu réutiliseras (tutoriel 006, story 012).

**Pourquoi `127.0.0.1:5433:5432` ?** **Dans** le conteneur, PostgreSQL écoute sur son port habituel,
5432. **Sur ton poste**, on le publie sur 5433, pour ne jamais gêner un PostgreSQL installé
localement. `127.0.0.1` : accessible depuis ta machine seulement, comme l'outillage.

**Pourquoi `tmpfs` ?** Les données vivent **en mémoire**. Quand le conteneur s'arrête, elles
disparaissent : chaque série de tests repart d'une base vierge, et aucun volume ne traîne. C'est le
sens de « jetable ».

**Pourquoi le mot de passe `test` est-il écrit en clair ?** C'est une base **de test**, sans données
réelles, joignable seulement depuis ta machine, et détruite après usage. Ce risque est **assumé et
documenté** dans la story. Les vraies bases (staging, prod) auront leurs mots de passe dans les
credentials Jenkins (story 019).

**Pourquoi un healthcheck ?** Un conteneur **démarré** n'est pas un PostgreSQL **prêt** : il lui faut
quelques secondes pour s'initialiser. `pg_isready` pose la question toutes les 5 secondes. Ainsi,
Jenkins attendra « prêt » au lieu d'un `sleep` au hasard (exigence de la story 012).

---

### Étape 2 : Fais relire la recette par Compose

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose -f src/test/resources/compose-test.yml config --quiet && echo "compose OK"
> ```

**Résultat attendu :** `compose OK`

**Pourquoi `-f` ?** Sans `-f`, `docker compose` utiliserait `docker-compose.yml`, celui de
l'**outillage** (Jenkins, SonarQube, Nexus). `-f` désigne explicitement le banc d'essai : les deux
stacks restent séparés.

**✅ Vérifie que :**
- [ ] `compose OK`

---

### Étape 3 : Vérifie que le vigile accepte le fichier

**Où :** la racine du projet

**Action :**

> ```bash
> git add src/test/resources/compose-test.yml
> ./scripts/check-no-secrets.sh; echo "exit code: $?"
> git rm --cached -q src/test/resources/compose-test.yml
> ```

**Résultat attendu :** `OK  no secret found in the files to commit`, `exit code: 0`.

**Pourquoi le vigile laisse passer `POSTGRES_PASSWORD: test` ?** Son filet « affectation » ne réagit
qu'aux valeurs de **8 caractères ou plus** : `test` en a 4. C'est voulu : une valeur de test courte
et évidente, pas un vrai mot de passe.

---

## 🧹 Comment tout annuler

```bash
rm src/test/resources/compose-test.yml
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `yaml: line N: did not find expected key` | Un décalage d'indentation : 2 espaces par niveau, **jamais** de tabulation |
| `services.postgres Additional property … is not allowed` | Une clé mal orthographiée ou au mauvais niveau : compare avec la liste des clés de l'étape 1 |
| `KO` du vigile sur ce fichier | Le mot de passe n'est pas exactement `test` : corrige-le |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Recette valide | `docker compose -f src/test/resources/compose-test.yml config --quiet` | aucune erreur |
| Vigile | `./scripts/check-no-secrets.sh` (fichier dans le carton) | `OK` |
| Carton vidé | `git status --short \| grep -c '^A'` | `0` |

---

## 🎉 Bravo !

Ton banc d'essai a sa recette. 🎉

Prochain tutoriel : **#003** — Démarrer la base de test et l'interroger ⏱️ 15 min
