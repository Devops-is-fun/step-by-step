# Commentaires de la vidéo de démonstration

La vidéo est **sans son** : ce document la commente, scène par scène. L'énoncé demande que chaque
membre sache expliquer toute la chaîne : **Morgan commente les parties écrites par Géraldine**
(CI, déploiement blue/green), **Géraldine commente les parties écrites par Morgan** (release-please,
métriques DORA).

- Vidéo (OneDrive, 4 parties) : **[vidéo de démonstration](https://reseauges75-my.sharepoint.com/:f:/r/personal/g_rouy_myskolae_fr/Documents/Step%20by%20Step%20-%20DEMO?d=w3f8749ca9c3a4e7887fbc8d281c30769&csf=1&web=1&e=PclZux)**
- Script de tournage : [demo.md](demo.md)
- Les horodatages (`mm:ss`) sont relevés après le tournage.

## Scénario 1 — un commit jusqu'à la production

### `00:00` — État initial (Géraldine)

La production répond (`/actuator/health` = `UP`) et nginx envoie le trafic vers une couleur, lue dans
`/etc/nginx/upstream/active.conf`. L'autre couleur garde la version précédente : c'est elle qui
servirait en cas de retour arrière.

### `mm:ss` — Merge de la PR README (commentaire de Morgan sur la CI)

*À rédiger par Morgan, avec ses mots. Points à couvrir :*

- *pourquoi la PR affiche le statut `continuous-integration/jenkins/pr-merge` ;*
- *ce que font les stages en parallèle (Secrets, Lint, Unit tests), puis Integration tests (base
  PostgreSQL jetable par build) et Quality gate (bloquante) ;*
- *pourquoi tout tourne sur `docker-agent` et jamais sur le controller ;*
- *comment l'artefact est versionné (`<version du pom>-<sha court>`) et publié dans Nexus.*

### `mm:ss` — Staging, validation, production (commentaire de Morgan sur le blue/green)

*À rédiger par Morgan. Points à couvrir :*

- *pourquoi l'image est construite à partir du JAR de Nexus (même artefact partout) ;*
- *le rôle de l'`input` avec `submitter: 'admin'` ;*
- *les étapes du blue/green : couleur libre, contrôle de santé, bascule nginx, retour arrière
  automatique.*

### `mm:ss` — L'application en production (Géraldine)

Création d'une tâche par l'API (`POST /api/tasks`), puis d'une sous-étape de 45 minutes : refusée
(`400`) avec le message de la règle `REG-01`, « Découpe-la en plusieurs sous-étapes plus courtes ».
Les règles métier vivent dans `TaskService` : elles s'appliquent quelle que soit l'entrée, et elles
sont prouvées par des tests unitaires.

## Scénario 2 — un échec volontaire bloqué

### `mm:ss` — La règle des 30 minutes est relâchée (Géraldine)

Sur une branche, `TaskStep.MAX_MINUTES` passe de 30 à 45. Le stage *Unit tests* échoue : le test de
frontière `shouldRejectStepLongerThan30Minutes` (31 minutes doit être refusé) attrape la régression.
La PR reçoit un statut ❌, Discord affiche l'échec, et rien n'est publié ni déployé : staging et
production restent sur la version précédente (`docker ps`). La PR est fermée sans merge.

## Release et métriques (commentaires de Géraldine sur le travail de Morgan)

### `mm:ss` — release-please (module N1b)

Les messages de commit suivent les Conventional Commits (`feat:`, `fix:`…). À chaque push sur `main`,
une GitHub Action (release-please) relit les commits depuis la dernière version et calcule la
suivante : un `feat` fait monter le chiffre du milieu (`0.1.x` → `0.2.0`), un `fix` le dernier. Elle
ouvre une **PR de release** avec le `CHANGELOG.md` généré et la nouvelle version du `pom.xml` ; rien
n'est publié tant qu'un humain ne l'a pas mergée. Au merge, elle crée le tag `step-by-step-v0.2.0` et
la release GitHub avec ses notes. Le pipeline lit ensuite la version dans le `pom.xml` : l'artefact
Nexus devient `0.2.0-<sha>`. La release et l'artefact sont donc liés. Leçon retenue : toujours
« Squash and merge », sinon le CHANGELOG liste certaines fonctionnalités plusieurs fois.

### `mm:ss` — Métriques DORA (module N3b)

`scripts/dora-metrics.sh` lit l'API Jenkins (builds de `main` et leurs consoles) et `git log`, et
écrit une ligne par semaine dans `observability/dora/dora-metrics.csv`. Un exporteur publie ce CSV
pour Prometheus, et Grafana affiche le dashboard « Step by Step - DORA ». Mesure du 01/11/2026 :
**11 déploiements** en production, **≈ 2-3 minutes** du commit à la production (médiane), **0 %**
d'échec en production, et un temps de restauration non mesurable faute d'échec en production. Limite :
une seule journée de mesure ; ces chiffres prouvent que la mesure est automatisée, pas une
performance durable. Et 0 % d'échec ne veut pas dire zéro erreur : le scénario 2 montre qu'elles
sont arrêtées **avant** la production.
