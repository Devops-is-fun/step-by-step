# Story 005 — Créer l'agent Jenkins et interdire le build sur le controller

Découpage TDA-friendly en **10 tutoriels**, à faire **dans l'ordre**.

- **Exigence TP couverte :** `EXG-COM-04`
- **Fichiers produits :** `jenkins-agent/Dockerfile`, service `jenkins-agent` dans `docker-compose.yml`,
  `scripts/check-agents.sh`, `docs/tooling/jenkins-agent.md`
- **Durée totale :** ⏱️ 2 h 55
- **Prérequis de la story :** story 004 terminée, stack `cicd` démarré et `healthy`, Bitwarden ouvert,
  jeton `scripts-tp` de Jenkins dans Bitwarden

> **Répertoire de travail de tous les tutoriels :** la racine du projet, le dossier qui contient
> `docs/`.
>
> **Interface :** Jenkins est en anglais ; les libellés sont donnés tels quels. Si un libellé diffère
> légèrement, fie-toi au chemin des menus et note-le dans `retours.md`.

## L'image de la story

Aujourd'hui, Jenkins est à la fois **chef de chantier** (le controller) et **ouvrier** (il a 2
exécuteurs). On embauche un ouvrier dédié, `docker-agent`, et on retire les outils au chef : 0
exécuteur. Le chef ouvre la porte de l'atelier avec **sa clé** (SSH) : la serrure (clé publique) est
sur la porte, la clé (privée) reste dans la poche du chef (credential Jenkins).

## Ordre des tutoriels

| # | Titre | Temps | Difficulté | Dépend de |
|---|-------|-------|------------|-----------|
| [001](task-001.md) | Écrire le `Dockerfile` de l'agent | ⏱️ 15 | ⭐⭐ | story 004 |
| [002](task-002.md) | Ajouter le service `jenkins-agent` au `docker-compose.yml` | ⏱️ 20 | ⭐⭐ | 001 |
| [003](task-003.md) | Créer la paire de clés : la privée dans Jenkins, la publique dans le compose | ⏱️ 20 | ⭐⭐⭐ | 002 |
| [004](task-004.md) | Démarrer l'agent et vérifier Docker | ⏱️ 15 | ⭐⭐ | 003 |
| [005](task-005.md) | Déclarer le nœud `docker-agent` dans Jenkins | ⏱️ 20 | ⭐⭐⭐ | 004 |
| [006](task-006.md) | Passer le controller à 0 exécuteur | ⏱️ 15 | ⭐⭐ | 005 |
| [007](task-007.md) | Créer le job `agent-smoke-test` | ⏱️ 15 | ⭐⭐ | 006 |
| [008](task-008.md) | Écrire `scripts/check-agents.sh` | ⏱️ 20 | ⭐⭐⭐ | 005 |
| [009](task-009.md) | Lancer le script, puis le faire échouer exprès | ⏱️ 15 | ⭐⭐ | 008 |
| [010](task-010.md) | Documenter (en anglais) et faire le bilan | ⏱️ 20 | ⭐⭐ | 009 |

## Jalons

| Jalon | Tutoriels | Preuve que tu y es |
|-------|-----------|--------------------|
| **J1 — Agent construit** | 001 à 004 | `docker version` répond depuis l'utilisateur `jenkins` de l'agent |
| **J2 — Agent branché** | 005 | `Agent successfully connected and online` |
| **J3 — Controller désarmé** | 006, 007 | `Built-In Node` à 0 ; `Running on docker-agent` |
| **J4 — Vérification automatique** | 008, 009 | `check-agents.sh` affiche « Agents are compliant » |
| **J5 — Story terminée** | 010 | doc écrite, aucune clé privée dans le projet |

## Décisions déjà prises

| Sujet | Décision |
|-------|----------|
| Connexion | agent **SSH** : le controller se connecte à l'agent (plugin *SSH Build Agents*, déjà installé) |
| Clé privée | **uniquement** dans la credential Jenkins `docker-agent-ssh-key` |
| Clé publique | dans `docker-compose.yml` (ce n'est pas un secret) |
| Image | `jenkins-agent/Dockerfile` : `jenkins/ssh-agent:8.6.0-jdk17` + CLI Docker `29.9.0` + Compose `v5.5.1` |
| Docker | socket de l'hôte monté ; groupe `docker-host` (GID `963`) créé **dans l'image** (une session SSH ignore `group_add`) |
| Job de démonstration | `agent-smoke-test`, conservé |
| `check-agents.sh` | lancé depuis le terminal ; l'appel par le pipeline est reporté à la story 011 |
| Langue | messages du script et `docs/tooling/jenkins-agent.md` en anglais |

Le détail est dans le `## Journal des décisions` du tutoriel 001.

## Après la story

- Laisse le stack **en marche**.
- **Aucun commit** : le premier commit du projet est fait en story 006.
