# Story 010 — Créer le job Multibranch et brancher le webhook GitHub

- **Jalon :** 1 — Intégration continue
- **Exigences couvertes :** `EXG-COM-06`, `EXG-COM-07`
- **Dépendances :** stories 002, 006

## Description

En tant que développeur, je veux que chaque push sur le dépôt GitHub déclenche automatiquement un
build Jenkins multibranche, et que l'état de ce build soit visible sur la pull request, afin
d'avoir une boucle de feedback court exigée par le cours.

## Décision structurante

`EXG-COM-06` exige un webhook. Jenkins tournant sur `localhost:8080` est **injoignable** depuis GitHub :
le choix retenu est un **tunnel** (Cloudflare Tunnel ou ngrok) qui expose Jenkins publiquement.
Le SCM polling n'est conservé que comme **repli documenté**, pas comme solution principale.

## Critères d'acceptation

- Le job `step-by-step` est de type **Multibranch Pipeline**, branché sur le dépôt GitHub via
  `credentialsId: github-token`.
- Le scan initial détecte `main`.
- Un webhook `POST` GitHub déclenche un build en moins de 10 secondes, sans scan manuel.
- Le webhook est configuré dans GitHub avec le secret partagé.
- Le build apparaît sur la pull request (statut dans l'onglet Checks), ou le build notifié par le SCM.
- Le tunnel est documenté avec sa commande de relance exacte.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Type de job | Jenkins → `step-by-step` → Multibranch |
| Webhook reçu | console : `Started by SCM change` ou `... by webhook` |
| Horodatage push → build | écart mesuré, consigné dans la story |
| Statut PR | onglet **Checks** de la pull request |

## Automatisation des tests

`scripts/check-webhook.sh` : envoie un `POST` vide sur l'URL du webhook et vérifie un
`HTTP 200`/`201` en moins de 5 secondes. Le test échoue si Jenkins ne répond pas — donc si le tunnel
est mort.

## Documentation

`docs/tooling/webhook-tunnel.md` : installation du tunnel, URL publique, configuration du webhook
GitHub, procédure de relance, plan B (polling).

## Fichiers à versionner

- `docs/tooling/webhook-tunnel.md`
- `scripts/check-webhook.sh`

## Fichiers à ne jamais versionner

`cloudflared.yml` local si il contient un token, `tunnel.log`

## Risques

| Risque | Parade |
|--------|--------|
| URL du tunnel change à chaque relance | Domaine tunnel nommé (stable) et procédure de reconfiguration documentée |
| Machine éteinte → webhook perdu | Tunnel relancé au démarrage ; le SCM rescanne au retour |
| Plugin manquant → pas de Multibranch | `github-branch-source` vérifié en story 002 |

## Critères de Done

- [ ] Job Multibranch actif sur le dépôt
- [ ] Push → build automatique en moins de 10 s
- [ ] Statut de build visible sur la PR
- [ ] Tunnel documenté avec commande de relance

## Message de commit

`docs(jenkins): document multibranch pipeline and github webhook tunnel`
