# Story 021 — Imposer la couverture minimale, publier les rapports et notifier sur Discord

- **Jalon :** 3 — N1a — Qualité et retour rapide
- **Exigences couvertes :** `EXG-N1a-01`, `EXG-N1a-02`, `EXG-N1a-03`
- **Dépendances :** stories 011, 019

## Description

En tant que responsable CI/CD, je veux que le build échoue sous un seuil de couverture de 60 %, que les
rapports JUnit et JaCoCo soient consultables dans Jenkins, et que chaque build notifie Discord, afin
d'avoir un retour rapide et une exigence de qualité mesurable.

## Décision structurante

Seuil JaCoCo retenu : **60 % de lignes couvertes** (`EXG-N1a-01`), valeur paramétrable dans `pom.xml`
pour rester ajustable sans toucher au pipeline.

## Critères d'acceptation

- `jacoco-maven-plugin` génère le rapport pendant `test` et applique un `check` avec
  `minimum>0.60</minimum>` sur les lignes ; sous le seuil, le build échoue.
- Le seuil est un **test** : une classe volontairement non couverte (ou un seuil brièvement surélevé)
  produit un build rouge — preuve consignée.
- Les rapports sont publiés en HTML (`jacoco`, `junit`) et archivés en artefact, y compris en cas
  d'échec (`post { always }`).
- Chaque fin de build (succès **et** échec) envoie une notification Discord via la credential
  `discord-webhook`, contenant dépôt, branche, durée et statut.
- Le lien vers le build est inclus dans la notification.

## Preuves attendues

| Preuve | Where |
|--------|------- |
| `Rule violated? Baeldung coverage ...` | console sous le seuil |
| Page HTML JaCoCo | onglet du build |
| Artefacts `jacoco.xml`, `junit.xml` | liste des artefacts |
| Message Discord avec statut | capture datée |
| Build rouge sous le seuil | vue build |

## Automatisation des tests

```bash
./mvnw -B verify -Djacoco.line.minimum=0.9   # attendu : échec
./mvnw -B verify                              # attendu : succès
```

La première commande **est** le test du seuil : elle ne peut pas réussir par construction.

## Documentation

`docs/architecture/quality-gate.md`, section « Couverture » ; `docs/tooling/discord.md` (création
du webhook, format du message).

## Fichiers à versionner

- `pom.xml` (plugin JaCoCo + seuil)
- `Jenkinsfile` (publication HTML + notification)
- `cicd-shared-lib/vars/notifyDiscord.groovy`
- `docs/tooling/discord.md`

## Fichiers à ne jamais versionner

L'URL du webhook Discord (credential `discord-webhook` uniquement)

## Risques

| Risque | Parade |
|--------|--------|
| Seuil de 60 % inatteignable au début | Story 009 fournit déjà des tests sur le service métier ; ajuster en priorité par des tests, pas en baissant le seuil |
| Notification bruyante | Un seul message de fin de build, pas un message par stage |
| Webhook Discord divulgué dans les logs | Valeur masquée par Jenkins ; jamais `echo` |

## Critères de Done

- [ ] Seuil 60 % bloquant et testé dans les deux sens
- [ ] Rapports JUnit et JaCoCo publiés et archivés
- [ ] Notification Discord à chaque build, succès et échec
- [ ] Aucun secret exposé

## Message de commit

`feat(pipeline): enforce coverage threshold and publish quality reports`
