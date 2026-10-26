# Story 001 — Démarrer l'outillage complet en une commande

Découpage TDA-friendly en **10 tutoriels**, à faire **dans l'ordre**.

- **Exigences TP couvertes :** `EXG-COM-01`, `EXG-COM-02`
- **Fichiers produits :** `docker-compose.yml`, `.gitignore`, `README.md`
- **Durée totale :** ⏱️ 3 h 05
- **Prérequis de la story :** Docker + Compose installés, ports 8080/8081/9000 libres

> **Répertoire de travail de tous les tutoriels :** la racine du projet, le dossier qui contient
> `docs/`. Toutes les commandes `docker compose` s'y exécutent, car Compose cherche son fichier
> `docker-compose.yml` en remontant les dossiers parents.

## Ordre des tutoriels

| # | Titre | Temps | Difficulté | Dépend de | Exigence |
|---|-------|-------|------------|-----------|----------|
| [001](task-001.md) | Vérifier que la machine est prête | ⏱️ 10 | ⭐ | — | `EXG-COM-02` |
| [002](task-002.md) | Créer le squelette du fichier Compose | ⏱️ 15 | ⭐⭐ | 001 | `EXG-COM-01` |
| [003](task-003.md) | Ajouter le service Jenkins | ⏱️ 20 | ⭐⭐ | 002 | `EXG-COM-01`, `EXG-COM-02` |
| [004](task-004.md) | Ajouter le service SonarQube | ⏱️ 20 | ⭐⭐ | 003 | `EXG-COM-01`, `EXG-COM-02` |
| [005](task-005.md) | Ajouter le service Nexus | ⏱️ 15 | ⭐⭐ | 004 | `EXG-COM-01`, `EXG-COM-02` |
| [006](task-006.md) | Valider la configuration et télécharger les images | ⏱️ 20 | ⭐⭐ | 005 | `EXG-COM-01` |
| [007](task-007.md) | Démarrer le stack et attendre les healthchecks | ⏱️ 25 | ⭐⭐ | 006 | `EXG-COM-01` |
| [008](task-008.md) | Vérifier que les trois interfaces répondent | ⏱️ 15 | ⭐ | 007 | `EXG-COM-02` |
| [009](task-009.md) | Tester la persistance et l'idempotence | ⏱️ 20 | ⭐⭐ | 008 | persistance |
| [010](task-010.md) | `.gitignore`, README et contrôle des secrets | ⏱️ 25 | ⭐⭐ | 009 | aucun secret |

Les tutoriels sont **linéaires** : chacun dépend du précédent. Aucun ne peut être fait dans le désordre.

## Jalons

| Jalon | Tutoriels | Preuve que tu y es |
|-------|-----------|--------------------|
| **J1 — Prêt à configurer** | 001 | `free -h`, `sysctl vm.max_map_count`, `ss -ltn` ne signalent aucun blocage |
| **J2 — Fichier complet** | 002 à 005 | `docker compose config --quiet` sort avec le code 0 |
| **J3 — Images téléchargées** | 006 | `docker images` liste les trois images avec leurs versions |
| **J4 — Stack sain** | 007 | `docker compose ps` affiche 3 services `healthy` |
| **J5 — Exigences prouvées** | 008 à 009 | 3 codes HTTP 200 + dépôts Nexus intacts après un `down`/`up` |
| **J6 — Story terminée** | 010 | aucun secret détecté, `.gitignore` et `README.md` en place |

## Critères de Done de la story

- [ ] `docker compose up -d` démarre exactement trois services : Jenkins, SonarQube, Nexus
- [ ] Les trois interfaces répondent en HTTP 200 : 8080, 9000, 8081
- [ ] `docker compose down` puis `up -d` laisse les données intactes
- [ ] Aucun mot de passe ni token n'apparaît en clair dans le fichier versionné

Ces quatre cases se cochent au tutoriel **010**, qui fait le bilan.

## Décisions déjà prises (aucune question ne sera posée)

Ces décisions viennent des échanges préalables et sont **figées** pour cette story. Ne les rediscute
pas dans les tutoriels : elles sont documentées dans `docs/specification/specification.md`, section 8.

| Sujet | Décision |
|-------|----------|
| Versions d'images | Épinglées : `jenkins/jenkins:2.541.3-lts-jdk17`, `sonarqube:9.9.8-community`, `sonatype/nexus3:3.96.4` |
| Mémoire | Plafonds JVM explicites : Jenkins et Nexus 512 Mio, SonarQube web et search 512 Mio chacun |
| Exposition des ports | `127.0.0.1` uniquement |
| Organisation | Un fichier Compose par finalité : racine = outillage ; `environments/` pour staging et production |
| Mot de passe Jenkins initial | Lu à la volée dans le volume, jamais stocké dans un fichier |
| Nom du projet Compose | `cicd` (clé `name:`), donc conteneurs `cicd-*` et volumes `cicd_*` |

## Notes de progression

Le fichier `notes-story-001.txt`, créé au tutoriel 001, sert de **journal de bord local** : il n'est
jamais versionné (le `.gitignore` du tutoriel 010 s'en charge). S'il n'existe plus, recrée-le : les
tutoriels 001, 007, 009 et 010 y écrivent.

## Après la story

- Laisse le stack **en marche** : la story 002 configure Jenkins par son interface.
- Le **premier commit Git** est fait en story 006, qui complète ce `.gitignore` et retire `.idea/`
  de l'index. Ne fais surtout pas `git add -A` d'ici là.
- Le prochain `.env.example` apparaîtra en story 015, avec les variables de staging.
