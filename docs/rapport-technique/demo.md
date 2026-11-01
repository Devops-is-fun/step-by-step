# Script de la vidéo de démonstration

Consigne du 01/11/2026 : la démonstration en direct est remplacée par **une vidéo** (moins de
10 minutes), hébergée hors du dépôt, sur OneDrive : **[vidéo de démonstration](https://reseauges75-my.sharepoint.com/:f:/r/personal/g_rouy_myskolae_fr/Documents/Step%20by%20Step%20-%20DEMO?d=w3f8749ca9c3a4e7887fbc8d281c30769&csf=1&web=1&e=PclZux)**. Elle est
sans son ; les commentaires sont dans [video-commentary.md](video-commentary.md).

Outil : enregistreur d'écran de GNOME (touche `Impr. écran` → mode vidéo). Les attentes longues sont
coupées au montage, avec la durée réelle affichée.

## Avant de tourner

- [ ] Stack `cicd` démarré, agent `docker-agent` en ligne, relais `cicd-webhook-relay` connecté
- [ ] Discord ouvert sur `#builds`
- [ ] Aucun secret à l'écran : ni page *Webhooks* de GitHub (URL smee), ni credential ouverte en
      édition, ni terminal où un `read -rs` serait relu, ni Bitwarden
- [ ] Onglets prêts : GitHub (PR), Jenkins (`step-by-step`), Nexus (`maven-releases`), terminal

## Scénario 1 — un commit jusqu'à la production (≈ 6 min)

| # | À l'écran | Ce qu'on dit |
|---|-----------|--------------|
| 1 | `curl localhost:8088/actuator/health`, puis `docker exec stepbystep-production-proxy-1 cat /etc/nginx/upstream/active.conf` | « La production tourne, nginx envoie le trafic sur blue. » |
| 2 | une petite modification sur une branche, `git push` | « Je pousse une branche. » |
| 3 | PR sur GitHub, puis Jenkins : stages en parallèle (Secrets, Lint, Unit tests), Integration tests, Quality gate | « Le webhook arrive en une seconde ; tout tourne sur l'agent, jamais sur le controller. » |
| 4 | statut ✅ `pr-merge` sur la PR, puis **Merge** | « Rien n'entre dans main sans le feu vert de Jenkins. » |
| 5 | build de `main` : Publish → Nexus (`0.x.y-<sha>`), Deploy staging, `curl localhost:8082/actuator/health` | « L'artefact versionné au hash du commit est publié, puis déployé automatiquement en staging. » |
| 6 | stage *Approve production* → **Deploy to production** | « Seule la personne autorisée valide, et c'est le même artefact qui part en production. » |
| 7 | `cat active.conf` → green ; `curl localhost:8088/actuator/health` ; message ✅ sur Discord | « Bascule blue/green après contrôle de santé ; blue reste prête pour un retour arrière. » |
| 8 | `docs/architecture/api.md` : créer une tâche en production, ajouter une sous-étape de 5 min, puis une de 45 min | « L'application elle-même : la règle des 30 minutes répond en langage courant, en production. » |

## Scénario 2 — un échec volontaire bloqué (≈ 3 min)

| # | À l'écran | Ce qu'on dit |
|---|-----------|--------------|
| 1 | sur une branche, `TaskStep.MAX_MINUTES` passe de `30` à `45` (« on relâche la règle REG-01 ») ; `git push` | « Quelqu'un casse une règle métier. » |
| 2 | Jenkins : stage *Unit tests* rouge, `shouldRejectStepLongerThan30Minutes` en échec ; ❌ sur Discord | « Le test de frontière 30/31 attrape la régression. » |
| 3 | PR GitHub : statut ❌ `pr-merge` | « La PR ne peut pas être mergée : rien n'est publié ni déployé. » |
| 4 | `docker ps --filter name=stepbystep-` : staging et production sur l'ancienne version | « Les environnements n'ont pas bougé. » |
| 5 | la PR est **fermée** sans merge, la branche supprimée | |

Variante possible : un `import` inutile → stage *Lint* rouge.

## Commentaires croisés (exigence « chaque membre explique ce qu'il n'a pas écrit »)

- Morgan commente le pipeline CI et le blue/green.
- Géraldine commente release-please et les métriques DORA.

## Durées réelles relevées au tournage

| Étape | Durée |
|-------|-------|
| push → début du build | |
| CI d'une PR | |
| `main` → production (hors attente de validation) | |
