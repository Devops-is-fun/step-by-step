# Story 003 — Configurer le projet SonarQube

- **Jalon :** 0 — Outillage
- **Exigences couvertes :** `EXG-COM-02`, `EXG-COM-19` (part)
- **Dépendances :** story 001

## Description

En tant que responsable CI/CD, je veux un projet SonarQube prêt à recevoir l'analyse de l'application,
afin que la Quality Gate puisse être branchée dès la story 013.

## Critères d'acceptation

- Le mot de passe admin SonarQube est modifié et rangé dans le gestionnaire de mots de passe ;
  `admin/admin` ne fonctionne plus.
- Le projet `step-by-step` est créé avec la clé `step-by-step`, branche principale `main`.
- La Quality Gate `step-by-step-gate` (copie de *Sonar way*, couverture du nouveau code à **60 %**)
  est associée au projet.
- Un jeton d'**analyse de projet** (limité à `step-by-step`) est rangé dans une credential Jenkins
  *Secret text* nommée `sonar-token`.
- Un jeton **utilisateur**, rangé dans le gestionnaire de mots de passe, sert à
  `scripts/check-sonarqube.sh` via la variable `SONAR_TOKEN`.
- La première analyse est faite en story 007, une fois le code de l'application créé.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Projet listé | SonarQube → Projects |
| Quality Gate associée | SonarQube → Quality Gates → `step-by-step-gate` → Projects |
| Token masqué dans Jenkins | `Manage Jenkins` → Credentials |

## Automatisation des tests

`scripts/check-sonarqube.sh` : vérifie via l'API que `admin/admin` est refusé, que le projet existe,
qu'il utilise `step-by-step-gate` et que la condition de couverture est à 60 %. Le jeton reste hors du
dépôt : le script lit `SONAR_TOKEN` depuis l'environnement. Messages en anglais.

## Documentation

`docs/tooling/sonarqube.md`

## Fichiers à versionner

- `docs/tooling/sonarqube.md`
- `scripts/check-sonarqube.sh`

## Fichiers à ne jamais versionner

`.env`, tout fichier contenant un jeton SonarQube, `.scannerwork/`

## Décisions (25/10/2026, `tasks-fr 003`)

- Pas de `sonar-project.properties` : la configuration de l'analyse vit dans le `pom.xml` (plugin
  Maven, stories 007 et 013) (`1a`).
- Première analyse déplacée en story 007 (`2a`).
- Deux jetons : analyse de projet pour Jenkins, utilisateur pour le script local (`3a`).
- Quality Gate `step-by-step-gate` à 60 % de couverture du nouveau code (`4a`).
- Messages des scripts en anglais ; `check-jenkins.sh` aligné (`5a`).

## Risques

| Risque | Parade |
|--------|--------|
| Sonar way exige 80 % de couverture du nouveau code | Gate dédiée `step-by-step-gate` à 60 % |
| Community Build sans analyse de branches | Épingler une version compatible |

## Critères de Done

- [ ] Mot de passe admin changé, `admin/admin` refusé
- [ ] Projet créé, Quality Gate à 60 % associée
- [ ] Jeton d'analyse en credential Jenkins `sonar-token`
- [ ] `check-sonarqube.sh` passe, et échoue quand il le doit
- [ ] Configuration documentée (en anglais)

## Message de commit

`ci(sonar): add sonarqube check script and documentation`
