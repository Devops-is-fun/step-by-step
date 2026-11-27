# Déroulé de la démonstration (soutenance)

Durée visée : **12 à 15 minutes**. Une personne parle, une autre manipule.
Idée directrice : **un seul changement de code traverse toute la chaîne**, et pendant les temps d'attente
(CI ≈ 2 min, Flux ≈ 1 min) on montre la supervision, l'auto-scaling et la résilience.

## 0. Préparation (avant la soutenance, ~15 min)

- [ ] Cluster en place (`kubectl -n flux-system get kustomizations` → `READY True`, **Flux non suspendu**)
- [ ] Toutes les PR du jour mergées ; `kubectl -n step-by-step get pods` : `db-0`, 2 backend, 2 front en `Running`
- [ ] Aucune autre application sur les ports 80, 3000, 9090 (ancienne stack Docker arrêtée)
- [ ] Préparer la **branche de démo** (sans la pousser) :
  ```bash
  git switch main && git pull && git switch -c demo/soutenance
  # front/src/App.vue : changer le sous-titre « Un petit pas vers votre objectif. »
  # par exemple en « Un petit pas vers votre objectif — démo du jour ! »
  git commit -am "feat(front): update the subtitle for the demo"
  ```
- [ ] Onglets du navigateur : <http://localhost/> · GitHub (onglets *Pull requests* et *Actions*) · Grafana
  (`kubectl -n monitoring port-forward svc/monitoring-grafana 3000:80`, dashboard *SRE Golden Signals*, période *Last 15 minutes*)
- [ ] 3 terminaux ouverts à la racine du dépôt
- [ ] Plan B : captures d'écran et fichiers de preuves (voir § 8) si le réseau ou le cluster fait défaut

## 1. Contexte et architecture (2 min)

- L'application : Step by Step (front Vue, back Spring Boot, PostgreSQL), reprise de notre projet CI/CD.
- Le schéma d'architecture du [dossier](dossier-projet.md#2-architecture-déployée).
- Le principe : **GitOps** — la CI construit et publie, Flux (dans le cluster) déploie ce qui est dans Git.

## 2. Le changement de code part dans la chaîne (2 min)

```bash
git push -u origin demo/soutenance
```

- Ouvrir la PR sur GitHub → le workflow **Front** démarre : lint (`lint:ci`), tests, Hadolint, build, **Trivy**.
- Expliquer : sur une PR, l'image est construite et scannée mais **jamais publiée**.
- Quand c'est vert : **merge**. Le workflow repart sur `main` : push sur **GHCR**, puis commit automatique
  `chore(deploy): step-by-step-front <sha>`.

## 3. Pendant la CI : la supervision (2 min)

- Grafana : les 6 panneaux (latence p95, trafic, erreurs 5xx, CPU, mémoire, scrape) — voir [observabilite.md](observabilite.md).
- Logs centralisés (Loki) dans Grafana : recherche par service, date, niveau. *(selon l'état de la PR de Morgan)*
- Trace d'une requête front → back → base (OpenTelemetry). *(selon l'état de la PR de Morgan)*

## 4. Le déploiement sans interruption (2 min)

Terminal 1 — surveiller la disponibilité pendant la mise à jour :

```bash
while true; do printf '%s ' "$(curl -s -o /dev/null -w '%{http_code}' http://localhost/)"; sleep 0.2; done
```

Terminal 2 — voir Flux et le rolling update :

```bash
kubectl -n flux-system get kustomizations -w
kubectl -n step-by-step get pods -w
```

- Flux voit le nouveau commit (≤ 1 min) et applique le nouveau tag → les pods front sont remplacés **un par un**.
- Terminal 1 : uniquement des `200`. Recharger <http://localhost/> : **le nouveau sous-titre apparaît**.
- Phrase clé : *« Personne n'a lancé de commande de déploiement : le cluster a tiré la nouvelle version depuis Git. »*

## 5. L'auto-scaling (3 min)

```bash
kubectl -n step-by-step get hpa,pods -w     # terminal 2
./scripts/load-test.sh 2m                   # terminal 3
```

- Grafana : trafic et CPU montent ; le HPA passe de **2 à 5 pods** ; latence basse, **0 % d'erreur**.
- Pendant la redescente (2 min), passer à la suite.

## 6. Résilience et zéro-downtime prouvés (2 min)

```bash
./scripts/test-resilience.sh
```

- Self-healing : un pod supprimé est recréé.
- 120 requêtes pendant un rolling restart : **120/120**.
- Raconter la difficulté : la première fois **119/120** → correctif `preStop` (le pod est retiré du Service et reçoit
  SIGTERM en même temps).

## 7. Retour arrière et reproductibilité (1 min)

- Rollback : `git revert` du commit `chore(deploy)` → Flux redéploie la version précédente (même mécanisme).
- Reproductibilité : le cluster complet se reconstruit en ~10 min avec les 5 commandes du [README](../README.md#3-installer-le-cluster-et-déployer).
- Sécurité de la chaîne : gitleaks, Hadolint, Trivy, CodeQL, images non-root.

## 8. Plan B (si quelque chose ne marche pas en direct)

| Problème | Ce qu'on montre à la place |
|---|---|
| CI trop lente / GitHub indisponible | Un run vert existant dans l'onglet *Actions* + les commits `chore(deploy)` de l'historique |
| Flux ne synchronise pas | `kubectl -n flux-system get kustomizations` + forcer : `kubectl -n flux-system annotate --overwrite kustomization/step-by-step reconcile.fluxcd.io/requestedAt="$(date +%s)"` |
| Cluster indisponible | Captures d'écran (Grafana, HPA) et fichiers de preuves horodatés (sorties des tests du 27/11) |

## Répartition proposée

| Partie | Qui parle | Qui manipule |
|---|---|---|
| 1. Contexte, architecture | Géraldine | — |
| 2. Chaîne CI | Géraldine | Géraldine |
| 3. Supervision, logs, traces | Morgan | Morgan |
| 4. Déploiement sans interruption | Géraldine | Morgan |
| 5. Auto-scaling | Morgan | Géraldine |
| 6. Résilience | Morgan | Morgan |
| 7. Rollback, reproductibilité, sécurité | Géraldine | — |
| Le frontend et ses tests (dans les parties 1-2) | Zhou | Zhou |
