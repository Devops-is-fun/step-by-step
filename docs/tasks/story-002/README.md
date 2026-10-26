# Story 002 — Débloquer Jenkins et installer les plugins

Découpage TDA-friendly en **7 tutoriels**, à faire **dans l'ordre**.

- **Exigence TP couverte :** `EXG-COM-02`
- **Fichiers produits :** `scripts/check-jenkins.sh`, `docs/tooling/jenkins-configuration.md`
- **Durée totale :** ⏱️ 2 h 05
- **Prérequis de la story :** story 001 terminée, stack `cicd` démarré et `healthy`, un gestionnaire
  de mots de passe installé (Bitwarden, KeePassXC…)

> **Répertoire de travail de tous les tutoriels :** la racine du projet, le dossier qui contient
> `docs/`.

## Ordre des tutoriels

| # | Titre | Temps | Difficulté | Dépend de |
|---|-------|-------|------------|-----------|
| [001](task-001.md) | Débloquer Jenkins et installer les plugins suggérés | ⏱️ 20 | ⭐ | story 001 |
| [002](task-002.md) | Créer le compte administrateur | ⏱️ 15 | ⭐ | 001 |
| [003](task-003.md) | Installer les quatre plugins complémentaires | ⏱️ 20 | ⭐⭐ | 002 |
| [004](task-004.md) | Créer un jeton d'API et le charger dans le terminal | ⏱️ 15 | ⭐⭐ | 002 |
| [005](task-005.md) | Écrire `scripts/check-jenkins.sh` | ⏱️ 20 | ⭐⭐⭐ | 004 |
| [006](task-006.md) | Lancer le script, puis le faire échouer exprès | ⏱️ 15 | ⭐⭐ | 003, 005 |
| [007](task-007.md) | Documenter la configuration et faire le bilan | ⏱️ 20 | ⭐⭐ | 006 |

## Jalons

| Jalon | Tutoriels | Preuve que tu y es |
|-------|-----------|--------------------|
| **J1 — Jenkins débloqué** | 001, 002 | tu te connectes avec `admin` et le mot de passe de ton gestionnaire |
| **J2 — Plugins complets** | 003 | les 10 plugins de la liste apparaissent dans « Plugins installés » |
| **J3 — Accès scripté** | 004 | `curl` authentifié par jeton renvoie `200` |
| **J4 — Vérification automatique** | 005, 006 | `check-jenkins.sh` affiche « Jenkins is compliant » et échoue sur un faux jeton |
| **J5 — Story terminée** | 007 | doc écrite, aucun secret dans les fichiers du projet |

## Critères de Done de la story

- [ ] Jenkins déverrouillé, compte `admin` créé, mot de passe dans le gestionnaire uniquement
- [ ] Crumb CSRF disponible
- [ ] Les 10 plugins de la liste sont installés
- [ ] `scripts/check-jenkins.sh` passe, et échoue quand il le doit
- [ ] Configuration documentée dans `docs/tooling/jenkins-configuration.md`

## Décisions déjà prises

| Sujet | Décision |
|-------|----------|
| Plugins | `git`, `github-branch-source`, `workflow-aggregator`, `pipeline-graph-view`, `sonar`, `credentials-binding`, `junit`, `coverage`, `warnings-ng`, `pipeline-utility-steps` |
| Installation | plugins suggérés dans l'assistant, puis les 4 complémentaires dans le gestionnaire de plugins |
| Mots de passe et jeton | gestionnaire de mots de passe, jamais dans un fichier du projet |
| Scripts | authentifiés par jeton d'API via `JENKINS_USER` / `JENKINS_TOKEN` |
| Controller à 0 exécuteur, type d'agent | story 005 |

Le détail est dans le `## Journal des décisions` du tutoriel 001.

## Notes de progression

Tu peux utiliser `notes-story-002.txt` comme journal de bord local : il est ignoré par Git grâce au
motif `notes-story-*.txt`. **Jamais de mot de passe ni de jeton dedans.**

## Après la story

- Laisse le stack **en marche** : la story 003 configure SonarQube.
- **Aucun commit** dans cette story : le premier commit du projet est fait en story 006.
