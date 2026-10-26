# Story 017 — Déployer en blue/green avec healthcheck et retour arrière

- **Jalon :** 2 — Livraison continue
- **Exigences couvertes :** `EXG-N2a-01`, `EXG-N2a-02`, `EXG-N2a-03` (module N2a)
- **Dépendances :** stories 015, 016

## Description

En tant que responsable de production, je veux que la production bascule d'une instance bleue vers une
instance verte derrière un reverse proxy, avec vérification de santé et retour arrière automatique,
afin de ne jamais laisser un utilisateur devant une version cassée.

## Critères d'acceptation

- La production est composée de `nginx` + `blue` + `green`, plus le PostgreSQL de production.
- `scripts/switch-color.sh <cible>` bascule l'upstream nginx vers la couleur cible **sans recréer** le
  proxy.
- Après bascule, le script interroge `/actuator/health` **de l'instance cible** pendant une fenêtre
  donnée (`EXG-N2a-02`).
- Si la santé ne devient pas `UP`, le script **restaure** l'upstream précédent et sort en code ≠ 0
  (`EXG-N2a-03`).
- Le log indique explicitement la couleur active avant, après, et le motif d'un éventuel retour
  arrière.
- L'instance inactive est arrêtée après une bascule réussie (économie de ressources, cohérente avec
  l'idempotence).

## Preuves attendues

| Preuve | Where |
|--------|------- |
| `nginx -t` valide + reload | log du script |
| Version servie avant / après | `/actuator/info` via `localhost:8088` |
| Retour arrière | log `ROLLBACK` + version servie revenue à la précédente |
| Codes de sortie | `echo $?` (0 succès, non 0 échec) |

## Automatisation des tests

Test du chemin d'échec, heartbeart de la story :

```bash
scripts/switch-color.sh blue     # vert attendu, code 0
scripts/deploy.sh production <tag-invalide> || true
scripts/switch-color.sh green    # doit échouer et revenir à blue, code != 0
```

Le second scénario utilise une image dont le healthcheck échoue : le rollback est donc **observé**,
pas supposé.

## Documentation

`docs/architecture/blue-green.md` : schéma, procédure de bascule, fenêtre de santé, politique de
retour arrière, limites (durée de bascule, absence de bascule à chaud du trafic).

## Fichiers à versionner

- `environments/production/docker-compose.yml`
- `environments/production/nginx.conf`
- `scripts/switch-color.sh`, `scripts/rollback.sh`
- `docs/architecture/blue-green.md`

## Fichiers à ne jamais versionner

`environments/production/.env`

## Risques

| Risque | Parade |
|--------|--------|
| Contournement du cache nginx après bascule | Purge du cache ou configuration `proxy_cache off` |
| Bascule pendant une requête en cours | `nginx -s reload` sans coupure (rechargement gracieux) |
| Faux positif de santé sur une seule réponse | Fenêtre de plusieurs tentatives avant décision |

## Critères de Done

- [ ] Bascule bleu/vert fonctionnelle et vérifiée
- [ ] Santé vérifiée automatiquement après bascule
- [ ] Retour arrière automatique observé sur un échec réel
- [ ] Codes de sortie corrects

## Message de commit

`feat(envs): add blue green deployment with health check and rollback`
