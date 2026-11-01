# Story 025 — Écrire un README qui permet de tout relancer

- **Jalon :** 4 — Rendus attendus
- **Exigences couvertes :** `EXG-REND-02`
- **Dépendances :** stories 001 à 024

## Description

En tant que jury, je veux que le `README.md` permette de reproduire toute la chaîne sur une machine
vierge, afin que je puisse évaluer le projet sans te poser de question.

## Critères d'acceptation

Le README contient, dans cet ordre :

1. **Présentation** en trois phrases, et l'équipe : un binôme, avec le lien vers la répartition des
   stories (spécification, « Organisation du binôme »).
2. **Prérequis** : versions exactes (Docker Desktop, JDK 17, `docker compose` v2, relais webhook smee.io).
3. **Démarrage** : une commande pour l'outillage, une commande pour le relais webhook, une commande pour l'app.
4. **Ordre des étapes** : la liste des 6 credentials à créer, avec l'ordre exact.
5. **Ports** : le tableau complet (8080, 9000, 8081, 8082, 8088, 8083, 8084, 9090, 3000).
6. **Démonstration de la chaîne** : la séquence de 8 commandes, de `git push` à `curl` production.
7. **Tests et couverture** : `./mvnw -B verify`, seuil 60 %.
8. **Règles métier** : les sept règles de gestion.
9. **Dépannage** : au minimum 5 pannes réelles rencontrées, avec le symptôme et la parade.
10. **Palette des liens** vers le rapport technique et la **vidéo de démonstration**.

## Preuves attendues

| Preuve | Where |
|--------|------- |
| README relu par une personne extérieure | relecture datée |
| Suite de commandes exécutée à la suite | sortie console archivée |
| 5 pannes documentées | section correspondante |

## Automatisation des tests

`scripts/check-readme.sh` : vérifie la présence des 10 sections par titre, et l'existence de chaque
port documenté dans `docker-compose.yml`. Le README ne peut donc pas mentir sur l'outillage.

## Documentation

Le fichier est lui-même la documentation ; la structure ci-dessus fait office de plan.

## Fichiers à versionner

- `README.md`
- `scripts/check-readme.sh`

## Fichiers à ne jamais versionner

Captures d'écran contenant des mots de passe ou tokens

## Risques

| Risque | Parade |
|--------|--------|
| README écrit de mémoire, désynchronisé | `check-readme.sh` exécuté après chaque changement de port |
| Trop long, illisible | Sommaire cliquable en tête |
| Oubli d'un secret dans une commande d'exemple | Relire avec `check-no-secrets.sh` |

## Critères de Done

- [ ] 10 sections présentes
- [ ] Suite de démonstration exécutée avec succès
- [ ] 5 pannes documentées
- [ ] `check-readme.sh` vert

## Message de commit

`docs: add readme with full local reproduction instructions`
