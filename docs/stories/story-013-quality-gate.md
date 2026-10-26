# Story 013 — Rendre la Quality Gate SonarQube bloquante

- **Jalon :** 1 — Intégration continue
- **Exigences couvertes :** `EXG-COM-11`
- **Dépendances :** stories 003, 011

## Description

En tant que responsable CI/CD, je veux que le build échoue tant que la Quality Gate SonarQube n'est
pas au vert, afin de ne jamais publier ni déployer du code non conforme.

## Critères d'acceptation

- Le stage `Quality Gate` s'exécute **après** lint et tests, en réutilisant les résultats JaCoCo déjà
  produits (pas de seconde analyse complète).
- `sonar.host.url`, `sonar.projectKey` et le jeton (credential `sonar-token`, via `withCredentials`)
  sont fournis au scanner ; **aucun** token dans le dépôt. Avec SonarQube 9.9, la propriété est
  `sonar.login` (`sonar.token` n'existe qu'à partir de la 10.0).
- Depuis le conteneur Jenkins, SonarQube se joint par `http://sonarqube:9000` (nom du service
  Compose), pas par `localhost`.
- `waitForQualityGate` est appelé avec un délai borné et **bloquant** : `FAILURE` ⇒ build rouge.
- Un commit qui dégrade volontairement la qualité (duplication, code smell) est **bloqué** par le
  pipeline — c'est la matière de la story 027.
- L'URL du dashboard du projet est affichée dans le log du stage.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| `Quality Gate status: SUCCESS` | console du stage |
| Build rouge sur gate KO | vue build, stage rouge |
| Lien vers le dashboard | console |
| Aucune occurrence du token dans le dépôt | `check-no-secrets.sh` |

## Automatisation des tests

Deux exécutions manuelles normées, consignées dans la story :

1. build propre ⇒ gate SUCCESS, build vert ;
2. commit avec duplication volontaire ⇒ gate FAILED, build rouge, **aucun** déploiement.

## Documentation

`docs/architecture/quality-gate.md` : conditions de la gate, ordre des stages, comportement en cas
d'indisponibilité de SonarQube (`fail closed` : on bloque plutôt que de laisser passer).

## Fichiers à versionner

- `Jenkinsfile` (stage `Quality Gate`)
- `sonar-project.properties`
- `docs/architecture/quality-gate.md`

## Fichiers à ne jamais versionner

`.sonar/`, `*.scannerwork/`, tout fichier contenant `sonar.login`

## Risques

| Risque | Parade |
|--------|--------|
| SonarQube indisponible ⇒ build bloqué à tort | Comportement assumé (`fail closed`) et documenté |
| Analyse vide car classes absentes | `sonar.java.binaries=target/classes` |
| Faux positif de la gate | Documenter l'exclusion **justifiée**, jamais un `ignore` global |

## Critères de Done

- [ ] Stage `Quality Gate` présent et bloquant
- [ ] Token lu depuis une credential
- [ ] Gate rouge ⇒ build rouge
- [ ] Dashboard accessible depuis le log

## Message de commit

`ci(pipeline): enforce blocking sonarqube quality gate`
