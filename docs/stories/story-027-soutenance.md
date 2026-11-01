# Story 027 — Tourner la vidéo de démonstration

- **Jalon :** 4 — Rendus attendus
- **Exigences couvertes :** `EXG-REND-04`, `EXG-REND-05`, `EXG-REND-06`
- **Dépendances :** stories 013, 016, 018, 024, 026
- **Qui :** le binôme, ensemble

## Contexte

Consigne du 01/11/2026 : il n'y a **plus de démonstration en direct**. Le rendu comprend une doc qui
justifie les choix (story 026) et une **preuve visuelle** que la chaîne fonctionne : une **vidéo**
(choix du binôme, sans captures d'écran dans le dépôt).

## Description

En tant que jury, je veux une vidéo courte qui montre un commit aller jusqu'à la production, puis
un échec volontaire bloqué par le pipeline, afin de constater que la chaîne fonctionne sans avoir à
la relancer.

## Critères d'acceptation

- **Scénario 1 — succès** (`EXG-REND-04`) : une branche → PR (statut ✅ visible) → merge sur `main` →
  webhook → lint, tests, intégration, Quality Gate, artefact Nexus → staging automatique →
  validation manuelle (`input` avec `submitter`) → bascule blue/green en production → `curl`
  production qui montre la nouvelle version.
- **Scénario 2 — échec volontaire** (`EXG-REND-05`) : un commit qui viole la Quality Gate (ou la
  couverture) **bloque** le pipeline ; la vidéo montre que staging est resté sur l'ancienne version.
- La vidéo dure **moins de 10 minutes** ; les attentes longues sont coupées au montage et signalées
  à l'écran (« build : 4 min 12 s »).
- Chaque membre **commente** une partie de la chaîne qu'il n'a pas écrite (`EXG-REND-06`).
- **Aucun secret à l'écran** : ni URL smee (page *Webhooks* de GitHub), ni jeton, ni page de
  credential en édition, ni terminal où un secret serait affiché.
- La vidéo est hébergée **hors du dépôt** (fichier trop lourd), avec un accès limité au jury ; son
  lien figure dans le `README.md` et le rapport technique.

## Preuves attendues

| Preuve | Where |
|--------|-------|
| Vidéo de démonstration | lien dans `README.md` et dans le rapport |
| Déroulé minuté des deux scénarios | `docs/rapport-technique/demo.md` |
| Relecture « aucun secret à l'écran » faite par les deux membres | case cochée dans `demo.md` |

## Automatisation des tests

Le scénario 2 est un test de bout en bout de la chaîne : le commit fautif est identifiable dans
l'historique, et staging est resté sur l'ancienne version (sortie consignée dans `demo.md`).

## Documentation

`docs/rapport-technique/demo.md` : déroulé des deux scénarios, durées réelles, commandes, lien vers
la vidéo, et réponses courtes aux questions probables (pourquoi un agent et pas le controller,
pourquoi jreleaser, comment fonctionne le retour arrière, d'où viennent les métriques DORA, que se
passe-t-il si SonarQube tombe, pourquoi un relais smee.io plutôt qu'un tunnel).

## Fichiers à versionner

- `docs/rapport-technique/demo.md`

## Fichiers à ne jamais versionner

La vidéo elle-même et ses rushes (trop lourds, et une image pourrait contenir un secret).

## Risques

| Risque | Parade |
|--------|--------|
| Un secret apparaît dans une image | Relecture image par image par les deux membres avant publication ; recoupe ou floute |
| Relais ou smee.io en panne pendant le tournage | `docker restart cicd-webhook-relay`, ou *Scan Repository Now* ; on retourne la prise |
| Vidéo trop longue | Attentes coupées au montage, durées réelles affichées |
| Outil d'enregistrement | Enregistreur d'écran intégré à GNOME (touche Impr. écran → mode vidéo), sans installation |

## Critères de Done

- [ ] Scénario 1 filmé, de la PR à la production
- [ ] Scénario 2 filmé, build bloqué et staging inchangé
- [ ] Chaque membre commente une partie qu'il n'a pas écrite
- [ ] Vidéo relue : aucun secret visible
- [ ] Lien dans le README et le rapport

## Message de commit

`docs(demo): add demo script and video link`
