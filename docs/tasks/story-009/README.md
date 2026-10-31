# Story 009 — Implémenter les règles REG-01 et REG-02 avec leurs tests unitaires

Découpage TDA-friendly en **5 tutoriels**, à faire **dans l'ordre**.

- **Exigences TP couvertes :** support applicatif ; alimente `EXG-COM-09` (rapports JUnit),
  `EXG-N1a-01` (couverture) et `EXG-COM-11` (Quality Gate)
- **Fichiers produits :**
  - code (écrit par Claude) : `service/TaskService.java`, `service/exception/` (5 classes),
    `TaskServiceTest`, requêtes de `TaskRepository`, `docs/architecture/business-rules.md` ;
  - CI/CD (toi) : pas de nouveau fichier. Tu **lis** ce que produisent tes outils : rapport JUnit,
    analyse SonarQube ;
  - **2 commits** poussés sur GitHub.
- **Durée totale :** ⏱️ 1 h 10
- **Prérequis de la story :** story 008 terminée ; stack `cicd` démarré (SonarQube `UP`)

> **Répertoire de travail de tous les tutoriels :** la racine du projet, le dossier qui contient
> `docs/`.

## L'image de la story

Claude a écrit le **règlement** de Step by Step : « pas de sous-étape de plus de 30 minutes », « une
seule tâche par jour ». Toi, tu joues l'**inspectrice** : tu lis le procès-verbal des tests (le
rapport JUnit), puis tu demandes l'avis du **contrôle qualité** (SonarQube), qui va te dire une chose
importante sur ce qui manque encore à ta chaîne.

## Ordre des tutoriels

| # | Titre | Qui | Temps | Difficulté | Dépend de |
|---|-------|-----|-------|------------|-----------|
| [001](task-001.md) | Découvrir les règles et lancer leurs tests | 🤖 | ⏱️ 15 | ⭐ | story 008 |
| [002](task-002.md) | Lire un rapport JUnit | 🧑‍💻 | ⏱️ 10 | ⭐ | 001 |
| [003](task-003.md) | Relancer l'analyse SonarQube | 🧑‍💻 | ⏱️ 15 | ⭐⭐ | 002 |
| [004](task-004.md) | Lire le verdict de SonarQube | 🧑‍💻 | ⏱️ 15 | ⭐⭐ | 003 |
| [005](task-005.md) | Commiter, pousser, bilan | 🧑‍💻 | ⏱️ 15 | ⭐⭐ | 004 |

## Jalons

| Jalon | Tutoriels | Preuve que tu y es |
|-------|-----------|--------------------|
| **J1 — Règles testées** | 001, 002 | `TaskServiceTest` : 12 tests verts, sans Docker ; rapport JUnit lu |
| **J2 — Contrôle qualité** | 003, 004 | analyse envoyée ; tu sais **pourquoi** la Quality Gate est rouge |
| **J3 — Story terminée** | 005 | 2 commits poussés ; `git status` vide |

## Décisions déjà prises

| Sujet | Décision |
|-------|----------|
| Périmètre | `REG-01` et `REG-02` seulement ; `REG-03` à `REG-07` en story 028 |
| `REG-02` | le service interroge la base **avant** d'enregistrer ; l'index unique de la story 008 reste le filet |
| Messages | en **français** pour l'utilisateur ; code et logs en anglais |
| Doc | `docs/architecture/business-rules.md`, en anglais, par Claude |
| Ta partie | lire le rapport JUnit et l'analyse SonarQube |

Le détail est dans le `## Journal des décisions` du tutoriel 001.
