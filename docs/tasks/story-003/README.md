# Story 003 — Configurer le projet SonarQube

Découpage TDA-friendly en **8 tutoriels**, à faire **dans l'ordre**.

- **Exigences TP couvertes :** `EXG-COM-02`, `EXG-COM-19` (part)
- **Fichiers produits :** `scripts/check-sonarqube.sh`, `docs/tooling/sonarqube.md`
- **Durée totale :** ⏱️ 2 h 15
- **Prérequis de la story :** story 002 terminée, stack `cicd` démarré et `healthy`, Bitwarden ouvert

> **Répertoire de travail de tous les tutoriels :** la racine du projet, le dossier qui contient
> `docs/`.
>
> **Interface :** SonarQube 9.9 n'existe qu'en anglais ; les libellés sont donnés tels quels.

## Ordre des tutoriels

| # | Titre | Temps | Difficulté | Dépend de |
|---|-------|-------|------------|-----------|
| [001](task-001.md) | Changer le mot de passe admin | ⏱️ 15 | ⭐ | story 002 |
| [002](task-002.md) | Créer le projet `step-by-step` | ⏱️ 10 | ⭐ | 001 |
| [003](task-003.md) | Créer la Quality Gate à 60 % et l'associer | ⏱️ 20 | ⭐⭐ | 002 |
| [004](task-004.md) | Ranger le jeton d'analyse dans Jenkins | ⏱️ 20 | ⭐⭐ | 002 |
| [005](task-005.md) | Créer le jeton utilisateur et le charger dans le terminal | ⏱️ 15 | ⭐⭐ | 001 |
| [006](task-006.md) | Écrire `scripts/check-sonarqube.sh` | ⏱️ 20 | ⭐⭐⭐ | 005 |
| [007](task-007.md) | Lancer le script, puis le faire échouer exprès | ⏱️ 15 | ⭐⭐ | 003, 006 |
| [008](task-008.md) | Documenter (en anglais) et faire le bilan | ⏱️ 20 | ⭐⭐ | 007 |

## Jalons

| Jalon | Tutoriels | Preuve que tu y es |
|-------|-----------|--------------------|
| **J1 — SonarQube sécurisé** | 001 | connexion avec le mot de passe de Bitwarden ; `admin/admin` refusé |
| **J2 — Projet prêt** | 002, 003 | `step-by-step` listé, associé à `step-by-step-gate` |
| **J3 — Jetons en place** | 004, 005 | credential `sonar-token` dans Jenkins ; `SONAR_TOKEN` chargé |
| **J4 — Vérification automatique** | 006, 007 | `check-sonarqube.sh` affiche « SonarQube is compliant » |
| **J5 — Story terminée** | 008 | doc écrite, aucun secret dans les fichiers |

## Critères de Done de la story

- [ ] Mot de passe admin changé, `admin/admin` refusé
- [ ] Projet créé, Quality Gate à 60 % associée
- [ ] Jeton d'analyse en credential Jenkins `sonar-token`
- [ ] `check-sonarqube.sh` passe, et échoue quand il le doit
- [ ] Configuration documentée (en anglais)

## Décisions déjà prises

| Sujet | Décision |
|-------|----------|
| Configuration de l'analyse | dans le `pom.xml` (stories 007 et 013), pas de `sonar-project.properties` |
| Première analyse | en story 007, sur le vrai code |
| Jetons | analyse de projet → credential Jenkins `sonar-token` ; utilisateur → Bitwarden + `SONAR_TOKEN` |
| Quality Gate | `step-by-step-gate`, copie de *Sonar way*, couverture du nouveau code à 60 % |
| Langue | messages des scripts et docs `docs/tooling/` en anglais |

Le détail est dans le `## Journal des décisions` du tutoriel 001.

## Après la story

- Laisse le stack **en marche** : la story 004 configure Nexus.
- **Aucun commit** : le premier commit du projet est fait en story 006.
