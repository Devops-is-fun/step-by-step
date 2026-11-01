# Story 010 — Créer le job Multibranch et brancher le webhook GitHub

- **Jalon :** 1 — Intégration continue
- **Exigences couvertes :** `EXG-COM-06`, `EXG-COM-07`
- **Dépendances :** stories 002, 006

## Description

En tant que développeur, je veux que chaque push sur le dépôt GitHub déclenche automatiquement un
build Jenkins multibranche, et que l'état de ce build soit visible sur la pull request, afin
d'avoir une boucle de feedback court exigée par le cours.

## Décision structurante

`EXG-COM-06` exige un webhook. Jenkins tournant sur `localhost:8080` est **injoignable** depuis GitHub.
Choix initial : un tunnel (Cloudflare Tunnel ou ngrok). **Remplacé le 31/10/2026 (`tasks-fr 010`, `1a`)
par un relais smee.io** : un tunnel exposerait toute l'interface Jenkins à Internet, alors que le
relais (conteneur `cicd-webhook-relay`) ne transmet que les webhooks, signés par le secret partagé.
Le scan périodique quotidien est conservé comme **repli documenté**.

## Critères d'acceptation

- Le job `step-by-step` est de type **Multibranch Pipeline**, branché sur le dépôt GitHub via
  `credentialsId: github-token` (*Username with password*, jeton *fine-grained* limité au dépôt).
- Le scan initial détecte `main`.
- Un webhook `POST` GitHub déclenche un build en moins de 10 secondes, sans scan manuel.
- Le webhook est configuré dans GitHub avec le secret partagé.
- Le build apparaît sur la pull request (statut de commit `continuous-integration/jenkins/pr-merge`).
- Le relais est documenté avec sa commande de relance exacte.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Type de job | Jenkins → `step-by-step` → Multibranch |
| Webhook reçu | console : `Started by SCM change` ou `... by webhook` |
| Horodatage push → build | écart mesuré, consigné dans la story |
| Statut PR | onglet **Checks** de la pull request |

## Automatisation des tests

`scripts/check-webhook.sh` : vérifie que le relais tourne, poste sur l'URL smee un *ping* signé comme
ceux de GitHub, puis lit dans les logs du relais la réponse de **Jenkins** (`200` attendu, `400` =
mauvais secret) en moins de 5 secondes. Le `200` de smee.io seul ne prouverait rien.

## Documentation

`docs/tooling/webhook-relay.md` : relais smee.io, valeurs et leur rangement, configuration Jenkins et
GitHub, commande de relance, vérification, dépannage, repli (scan quotidien).

## Fichiers à versionner

- `Jenkinsfile` (minimal)
- `docs/tooling/webhook-relay.md`
- `scripts/check-webhook.sh`

## Fichiers à ne jamais versionner

l'URL du canal smee.io, le secret du webhook, le jeton GitHub (Bitwarden + credentials Jenkins seulement)

## Risques

| Risque | Parade |
|--------|--------|
| smee.io indisponible ou lent | Scan quotidien + *Scan Repository Now* documentés |
| Machine éteinte → webhook perdu | Relais `--restart unless-stopped` ; scan quotidien au retour |
| URL smee divulguée | Lecture des notifications seulement ; tout webhook non signé est refusé (`400`) ; nouveau canal + mise à jour du webhook |
| Jeton GitHub expiré (90 jours) | Date d'expiration notée dans Bitwarden ; procédure dans `webhook-relay.md` |
| Plugin manquant → pas de Multibranch | `github-branch-source` vérifié en story 002 |

## Critères de Done

- [ ] Job Multibranch actif sur le dépôt
- [ ] Push → build automatique en moins de 10 s
- [ ] Statut de build visible sur la PR
- [ ] Relais documenté avec commande de relance

## Message de commit

1. `ci(pipeline): add minimal jenkinsfile running on the docker agent`
2. `ci(webhook): add webhook check script and relay documentation` (branche `demo-webhook`, PR #1)
3. `docs(jenkins): record webhook push-to-build delay`

## Mesure

Push sur `main` → début du build : **10 s** (<date>, relais smee.io, *quiet period* 0).
Le webhook atteint Jenkins en ≈ 1 s ; GitHub Branch Source attend ensuite 5 s par conception.
Première mesure (branche neuve `demo-webhook`, *quiet period* 5) : 16 s.
