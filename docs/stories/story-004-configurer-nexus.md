# Story 004 — Configurer le dépôt d'artefacts Nexus

- **Jalon :** 0 — Outillage
- **Exigences couvertes :** `EXG-COM-02`, `EXG-COM-19` (part)
- **Dépendances :** story 001

## Description

En tant que responsable CI/CD, je veux un dépôt Maven `maven-releases` dans Nexus avec un compte de
déploiement dédié, afin que le pipeline puisse publier un artefact versionné dès la story 014.

## Critères d'acceptation

- L'assistant de première connexion est terminé : mot de passe initial lu à la volée
  (`docker compose exec nexus cat /nexus-data/admin.password`), nouveau mot de passe admin rangé dans
  le gestionnaire de mots de passe, **accès anonyme activé** (indispensable à `scripts/check-nexus.sh`).
- Le dépôt `maven-releases` créé par défaut (hosted) a la *deployment policy* **Disable redeploy**
  (release une seule fois).
- Le rôle `step-by-step-deployer` regroupe les privilèges `nx-repository-view-maven2-maven-releases-*`
  nécessaires au déploiement (`add`, `edit`, `read`, `browse`) ; l'utilisateur `nexus-deployer` n'a
  que ce rôle.
- Le mot de passe de `nexus-deployer`, généré par Bitwarden, est rangé **uniquement** dans une
  credential Jenkins *Username with password* `nexus-deploy-creds`.
- Une publication témoin (un `.pom`, avec le compte admin) fonctionne, une **seconde** publication de
  la même version est **refusée**, puis le témoin est supprimé.
- `scripts/check-nexus.sh`, **sans identifiants**, confirme : Nexus répond et accepte les écritures,
  lecture anonyme autorisée, écriture anonyme refusée, assistant terminé. Messages en anglais.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Assistant terminé | `/nexus-data/admin.password` n'existe plus dans le conteneur |
| Dépôt listé | Nexus → Repositories |
| Compte et rôles visibles | Nexus → Security → Users / Privileges |
| Publication témoin | Nexus → `maven-releases` → composant |
| Credential masquée | Jenkins → Credentials |

## Automatisation des tests

`scripts/check-nexus.sh` :

Script sans identifiants (Nexus Community n'a pas de jetons d'API) ; voir les critères ci-dessus.

## Documentation

`docs/tooling/nexus.md`

## Fichiers à versionner

- `docs/tooling/nexus.md`
- `scripts/check-nexus.sh`

## Fichiers à ne jamais versionner

`.env`, `settings.xml` local contenant `<password>`, `*.credentials`

## Décisions (25/10/2026, `tasks-fr 004`)

- `maven-releases` par défaut, politique vérifiée (`1a`).
- Mot de passe de `nexus-deployer` uniquement dans Jenkins (`2a`).
- `check-nexus.sh` sans identifiants (`3a`).
- Publication témoin avec le compte admin + republication refusée (`4a`) ; les droits de
  `nexus-deployer` sont prouvés en story 014 par le pipeline.
- Accès anonyme en lecture activé (`5a`).

## Risques

| Risque | Parade |
|--------|--------|
| Réutiliser le compte admin Nexus | Compte `nexus-deployer` dédié, droits restreints |
| Dépôt en `allow redeploy` | Vérifier l'absence de redéploiement de la même version |

## Critères de Done

- [ ] Assistant terminé, mot de passe admin dans Bitwarden, accès anonyme en lecture
- [ ] `maven-releases` en *Disable redeploy*
- [ ] Rôle et compte dédiés, credential Jenkins `nexus-deploy-creds`
- [ ] Publication témoin réussie, republication refusée, témoin nettoyé
- [ ] `check-nexus.sh` passe, et échoue quand il le doit ; doc en anglais

## Message de commit

`ci(nexus): add nexus check script and documentation`
