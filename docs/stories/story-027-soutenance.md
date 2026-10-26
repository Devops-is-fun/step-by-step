# Story 027 — Préparer et répéter la soutenance

- **Jalon :** 4 — Rendus attendus
- **Exigences couvertes :** `EXG-REND-04`, `EXG-REND-05`, `EXG-REND-06`
- **Dépendances :** stories 013, 016, 018, 024

## Description

En tant que candidate à la soutenance, je veux deux scénarios répétables — une réussite et un échec
volontaire — et un argumentaire complet, afin de démontrer la chaîne en direct sans improvisation.

## Critères d'acceptation

- **Scénario 1 — succès** (`EXG-REND-04`) : un commit sur `feature/*` → PR → merge `main` → webhook →
  lint, tests, intégration, gate, artefact Nexus → staging automatique → validation manuelle →
  bascule blue/green en production → `curl` production. Durée chronométrée et inférieure à 10 minutes
  de build.
- **Scénario 2 — échec volontaire** (`EXG-REND-05`) : un commit violant volontairement la Quality Gate,
  ou la couverture, **bloque** le pipeline, et le non-déploiement est démontré (`docker images` inchangé,
  staging toujours sur l'ancienne version).
- **Argumentaire** (`EXG-REND-06`) : une réponse préparée pour chacune de ces questions probables :
  pourquoi un agent et pas le controller, pourquoi jreleaser, comment fonctionne le rollback, d'où
  viennent les métriques DORA, que se passe-t-il si SonarQube tombe, pourquoi un tunnel.
- **Plan B** : captures d'écran et journaux de tout build réussi, au cas où le direct échoue.
- La répétition est faite **au moins deux fois**, avec un chronomètre.

## Preuves attendues

| Preuve | Where |
|--------|------- |
| Scénario 1 exécuté et minuté | `docs/rapport-technique/soutenance.md` |
| Scénario 2 exécuté, build rouge | idem |
| Questionnaire d'entraînement | section questions |
| Plan B complet | dossier de captures |

## Automatisation des tests

Le scénario 2 est un test de bout en bout de la chaîne : le commit fautif doit être identifiable dans
l'historique, et le staging doit être resté sur l'ancienne version. Consigner la sortie.

## Documentation

`docs/rapport-technique/soutenance.md` : déroulé minuté des deux scénarios, questions préparées,
plan B.

## Fichiers à versionner

- `docs/rapport-technique/soutenance.md`
- `docs/rapport-technique/figures/soutenance/*.png`

## Fichiers à ne jamais versionner

Captures d'écran de sessions contenant des tokens

## Risques

| Risque | Parade |
|--------|--------|
| Tunnel mort pendant la soutenance | Plan B : captures et reLANCEMENT du tunnel en 10 s |
| Démo trop longue | Scénario 1 minuté, décisions de coupe listées |
| Panne inconnue | Scénario 2 réglé à l'avance, donc maîtrisé |

## Critères de Done

- [ ] Scénario 1 minuté et exécuté
- [ ] Scénario 2 exécuté avec build bloqué
- [ ] 6 questions préparées avec réponse
- [ ] Deux répétitions complètes

## Message de commit

`docs(soutenance): add demo script for success and failure scenarios`
