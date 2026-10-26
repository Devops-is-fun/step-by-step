# Story 019 — Centraliser tous les secrets dans les credentials Jenkins

- **Jalon :** 3 — Modules et fiabilité
- **Exigences couvertes :** `EXG-COM-19`
- **Dépendances :** stories 002, 003, 004

## Description

En tant que responsable sécurité, je veux que chaque secret soit stocké uniquement dans Jenkins et
référencé par identifiant dans le pipeline, afin qu'aucun secret ne puisse fuiter par le dépôt Git.

## Critères d'acceptation

- Les six secrets de la spécification (§ 7) existent comme credentials Jenkins : `sonar-token`,
  `nexus-deploy-creds`, `discord-webhook`, `github-token`, identifiants PostgreSQL staging,
  identifiants PostgreSQL production.
- Le pipeline ne contient **aucune** valeur en clair : uniquement des `credentialsId` et `withCredentials`.
- Les mots de passe PostgreSQL sont injectés via `withCredentials` puis passés à `docker compose`
  comme variables d'environnement, jamais écrits dans un `docker-compose.yml` versionné.
- `scripts/check-no-secrets.sh` est vert sur l'ensemble du dépôt et est exécuté en début de pipeline.
- Une valeur manquante provoque un **échec bruyant et lisible**, pas un silence.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Inventaire des credentials | Jenkins → Credentials (valeurs masquées) |
| Aucune valeur en clair | sortie de `check-no-secrets.sh` |
| Secret absent de l'historique | `git log -p \| grep -i token` sans résultat |
| Rotation possible | procédure documentée |

## Automatisation des tests

```bash
scripts/check-no-secrets.sh
git ls-files | xargs grep -nEI '(password|token|secret|api[_-]?key)[[:space:]]*[:=]'
# attendu : aucune affectation en dur
```

## Documentation

`docs/tooling/secrets.md` : inventaire (secret → credential ID → consommateur), procédure de
création, procédure de rotation,ce qu'il ne faut **jamais** versionner.

## Fichiers à versionner

- `docs/tooling/secrets.md`
- `scripts/check-no-secrets.sh`
- `.gitignore`

## Fichiers à ne jamais versionner

`.env`, `secrets/`, `*.pem`, `*.key`, `id_rsa*`, `settings.xml` local

## Risques

| Risque | Parade |
|--------|--------|
| Secret dans l'historique Git | `git filter-repo` puis **rotation** immédiate du secret |
| Secret visible dans les logs Jenkins | `set +x` autour des commandes sensibles |
| `.env.example` confondu avec `.env` | `.env.example` ne contient que des noms de variables |

## Critères de Done

- [ ] 6 credentials Jenkins créées
- [ ] Aucun secret en clair dans le dépôt
- [ ] `check-no-secrets.sh` vert et branché sur le pipeline
- [ ] Procédure de rotation documentée

## Message de commit

`docs(security): centralise every secret in jenkins credentials`
