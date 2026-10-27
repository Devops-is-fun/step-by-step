# Retours, incidents et écarts

## 2026-10-25 — Erreurs dans les tutoriels de la story 001

* `task-008` ét. 4 : seuls `200` et `302` sont annoncés, mais Jenkins répond `403` sur `/` (page protégée tant qu'on n'est pas connecté). `403` prouve aussi que le service répond [TRAITE: story-001 + tasks-fr]
* `task-008` ét. 6 : l'assistant « Débloquer Jenkins » n'affiche pas le mot de passe, seulement le chemin du fichier `initialAdminPassword`. Demander de « noter le mot de passe » contredit la spécification § 8 (lu à la volée, jamais stocké) [TRAITE: story-001 + story-002 + tasks-fr]
* `task-009` ét. 2 : le mot de passe Nexus n'est pas dans `docker compose logs nexus`, mais dans le fichier `/nexus-data/admin.password`. De plus, se connecter lance un assistant obligatoire (nouveau mot de passe admin, accès anonyme) qui modifie la configuration. Preuve de persistance remplacée par la date de `admin.password` (`docker compose exec nexus ls -la /nexus-data/admin.password`) [TRAITE: story-001 + story-004 + tasks-fr]
* `task-010` ét. 3 et 7 : il est annoncé que `.idea/` disparaît de `git status` après le `.gitignore`, alors qu'il est déjà indexé (le bloc « Pourquoi » du même tutoriel dit l'inverse). Cause de l'indexation : IntelliJ propose « Add file to Git? » à la création des fichiers [TRAITE: story-001 + tasks-fr]
* `task-010` ét. 5 : le motif du premier `grep` est entre guillemets doubles ; bash interprète `$[:space:]` comme un calcul arithmétique et échoue. Corrigé avec des quotes simples [TRAITE: story-001 + tasks-fr]
* `task-010` ét. 6 : le modèle de README indique le mot de passe Nexus « dans `docker compose logs nexus` » (même erreur que `task-009`). Corrigé par `docker compose exec nexus cat /nexus-data/admin.password` [TRAITE: story-001]
* `task-010` ét. 6 : le modèle de README cite SonarQube en story 020 et Nexus en story 021, alors que leur configuration est en story 003 et 004 (analyse bloquante en 013, publication en 014) [TRAITE: story-001]

## 2026-10-25 — Erreurs dans les tutoriels de la story 002

* `task-002` ét. 6 : le `403` sur `/api/json` sans identifiants était présenté comme la preuve que l'assistant est terminé. En réalité, Jenkins 2.541.3 répond déjà `403` avant l'assistant. Preuve réelle : la reconnexion de l'étape 5 (et la suppression de `initialAdminPassword` en fin d'assistant) [TRAITE: story-002]
* `task-003` ét. 8 : demandait de chercher les plugins installés par identifiant, alors que la recherche de l'interface porte sur les noms affichés (`workflow-aggregator` s'affiche « Pipeline »). Tableau identifiant → nom ajouté [TRAITE: story-002]
* `task-007` ét. 4 : une seule fausse alerte annoncée (script), mais `README.en.md` en déclenche une deuxième (`initial password: ` suivi de la commande `docker compose exec …`). Le motif avait été testé sur le script et la doc, pas sur le README anglais [TRAITE: story-002 task-007]

## 2026-10-25 — Erreurs dans les tutoriels de la story 003

* `task-003` ét. 4 : « saisis ton mot de passe Bitwarden » est ambigu (plusieurs entrées : Jenkins, SonarQube, jetons). Précisé : mot de passe admin SonarQube, entrée `SonarQube local — TP CI/CD` [TRAITE: story-003]

## 2026-10-26 — Erreurs dans les tutoriels de la story 004

* `task-005` ét. 4 : la 2e publication du témoin répond `409` (Conflict), pas `400` comme annoncé. Nexus 3.96.4 signale ainsi le refus de remplacer une release (*Disable redeploy*). Le code `400` est aussi cité dans `task-005` (tableau « Si tu es bloqué », résumé), `task-008` (doc `nexus.md`, bilan) et la story 004 [TRAITE: story-004 task-005 + task-008 + README]

## 2026-10-26 — Erreurs dans les tutoriels de la story 005

* `task-006` ét. 2 : il était écrit qu'un job sans `agent { label … }` « attend dans la file » quand le controller a 0 exécuteur. Avec `docker-agent` en *Use this node as much as possible*, il part sur l'agent ; il n'attend que si aucun agent n'est disponible. Question posée pendant la story [TRAITE: story-005 task-006]
* `task-007` ét. 3 : `permission denied while trying to connect to the docker API at unix:///var/run/docker.sock`. Le `group_add` du compose ne s'applique pas aux sessions SSH par lesquelles Jenkins lance les builds (seulement au processus principal et à `docker compose exec`). La recette avait été testée avec `docker exec -u jenkins`, pas par SSH. Corrigé : groupe `docker-host` (GID `963`) créé dans le `Dockerfile`, `group_add` retiré [TRAITE: story-005 task-001/002/004/007/010 + tasks-fr]

## 2026-10-26 — Erreurs dans les tutoriels de la story 006

* `task-004` ét. 4 : `git restore --staged trap.properties` échoue avec `fatal : impossible de résoudre 'HEAD'`, car il n'y a encore aucun commit. Le piège reste dans l'index (`AD trap.properties`, compteur à `1`). La simulation avait retiré le piège autrement. Corrigé : `git rm --cached` [TRAITE: story-006 task-004 + task-006 + tasks-fr]

## 2026-10-26 — Erreurs dans les tutoriels de la story 007

* `task-004` ét. 1 : `./scripts/check-sonarqube.sh` lancé **avant** le chargement du jeton → `SONAR_TOKEN is not set`. Le script exige `SONAR_TOKEN` (story 003). Corrigé : les étapes 1 et 2 sont inversées (jeton d'abord) [TRAITE: story-007 task-004]
* `task-006` ét. 4 : la boucle `until curl …; do sleep 2; done` tourne sans fin quand rien n'écoute sur 8090 (ici, aucun conteneur `step-by-step-local`) ; il a fallu Ctrl+C. Corrigé : `docker ps` d'abord, boucle bornée par `timeout 60`, ligne « Si tu es bloqué » précisée [TRAITE: story-007 task-006]
