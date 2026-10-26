---
name: feedback-fr
description: Transformation des retours et incidents du TP CI/CD en stories, en filtrant ceux déjà traités
---

# Skill: Feedbacks → Stories (FR) — TP CI/CD

## Contexte du projet (à connaître avant toute chose)

| Élément | Valeur |
|---------|--------|
| Nature du travail | TP du cours CI/CD (ESGI Master 1) |
| Énoncé de référence | `docs/Projet_CICD_Jenkins.pdf` |
| Travail | **En solo** |
| Stack | Java / Spring Boot + Maven · Jenkins + SonarQube + Nexus · staging et production en Docker Compose local |
| Traçabilité | Toute story générée doit être rattachée à un identifiant `EXG-*` de la spécification |

## Qu'est-ce que je fais

Je prends le fichier `docs/specification/retours.md`, j'identifie les points **non encore traités**, et
j'en génère des stories au même format que `story-fr`.

Dans ce projet, un « retour » ne vient pas d'un utilisateur de l'application. Il vient de quatre
sources, toutes utiles :

| Source | Exemple concret |
|--------|-----------------|
| **Retour du professeur** | « La Quality Gate ne bloque pas le build, refaites-la » |
| **Incident de pipeline** | « Le build #42 a échoué : l'agent était `Offline` » |
| **Écart vs énoncé** | « Tu n'as pas d'artefact versionné avec le hash du commit » |
| **Point faible pour la soutenance** | « Je n'arrive pas à expliquer le blue/green à l'oral » |

## Quand m'utiliser

- Après une séance avec le professeur qui a relevé des points à corriger
- Après un incident notable sur un build ou un déploiement
- En fin de projet, en re-lisant la matrice de traçabilité et en notant ce qui reste faible

## Prérequis

Le fichier `docs/specification/retours.md` suit ce format :

```markdown
## 2026-10-05 — Retour professeur

* La Quality Gate ne bloque pas le build
* Le déploiement en prod ne demande pas de validation manuelle

## 2026-10-12 — Incident pipeline

* Build #42 : agent `Offline`, échec du stage `Test` [TRAITE: story-014]
```

Chaque retour traité reçoit le marqueur `[TRAITE: story-XXX]` (ou `[TRAITE: EXG-COM-04]` si le retour
ne justifie pas une nouvelle story mais corrige une exigence existante).

## Workflow

### Étape 1 — Lecture des retours

- Lire `docs/specification/retours.md`
- Repérer les retours **non marqués** `[TRAITE: ...]`
- Si tous sont marqués → annoncer qu'il n'y a rien de nouveau → fin du workflow

### Étape 2 — Analyse des stories existantes

- Lire les stories de `docs/stories/` (et `docs/stories/done/` si le dossier existe)
- Vérifier si un retour non marqué est **déjà couvert** par une story existante
  - Exemple : « le déploiement en prod ne demande pas de validation » est déjà traité par
    `story-007` → ajouter `[TRAITE: story-007]` et **ne pas** recréer de story
- Les retours restants (ni marqués, ni couverts) sont à transformer

### Étape 3 — Questions de cadrage (dans le chat)

Je pose au maximum **5 questions par lot**, dans le chat :

```text
Question 1 — <sujet>
  a) <option A>  → <conséquence>       (⭐ recommandé : <raison>)
  b) <option B>  → <conséquence>
Réponds avec : 1a, 2b, 3c...
```

Questions types : faut-il une nouvelle story ou corriger une story existante ? Quel niveau de
priorité par rapport aux stories en cours ?

### Étape 4 — Regroupement en stories

- Regrouper les retours par thème (ex : « SonarQube », « déploiement », « documentation »)
- Un retour seul peut devenir une story s'il est suffisamment riche
- Plusieurs retours liés → une seule story
- Un retour complexe → plusieurs stories

### Étape 5 — Génération des stories

- Déterminer le prochain numéro de story à partir de `docs/stories/`
- Créer les nouvelles stories dans `docs/stories/` au format de `story-fr`, **avec** les sections
  spécifiques à ce projet :
  - `**Exigences TP couvertes :** \`EXG-...\``
  - `## Preuves attendues` (build vert, rapport, artefact, commande de vérification)
  - `## 💬 Message de commit` en Conventional Commits anglais
  - `## 📦 Fichiers à versionner` / **fichiers à NE PAS versionner** (`.env`, `secrets/`, `*.key`,
    credentials Jenkins)
- Ajouter `## Origine` pour tracer le retour source (date + texte du retour)
- **Mettre à jour la matrice de traçabilité** de `docs/specification/specification.md` si une
  exigence `EXG-*` nouvellement traitée apparaît
- Marquer chaque retour traité dans `retours.md` avec `[TRAITE: story-XXX]`

### Étape 6 — Mise à jour

- L'utilisateur répond dans le chat (`1a, 2b, …`)
- Je mets à jour les stories selon les réponses
- Retour à l'Étape 2 (re-vérifier la cohérence, y compris avec les stories existantes)

### Étape 7 — Fin du workflow

- Quand il n'y a plus rien à clarifier
- Insérer les nouvelles stories dans la feuille de route `docs/stories/plan-implementation.md`, à
  l'emplacement cohérent avec les dépendances (travail **solo** → ordre strictement séquentiel)
- Ajouter une ligne dans le tableau « Coverage check » si une exigence devient couverte
- Annoncer la fin et indiquer la suite : `/ tasks-fr <nouveau numéro>`

## Format d'un retour traité

```markdown
## 2026-10-05 — Retour professeur

* La Quality Gate ne bloque pas le build [TRAITE: story-011]
* Le déploiement en prod ne demande pas de validation manuelle [TRAITE: story-007]
* Le README ne mentionne pas les ports [TRAITE: story-015]
```

## ✅ Avant de passer à l'étape suivante — vérifications

- [ ] `retours.md` ne contient **plus aucun** retour sans marqueur `[TRAITE: ...]`
- [ ] Aucun retour n'a produit de doublon avec une story existante
- [ ] Chaque nouvelle story porte un identifiant `EXG-*` cohérent avec la spécification
- [ ] La matrice de traçabilité de `specification.md` a été mise à jour si besoin
- [ ] `plan-implementation.md` place les nouvelles stories **après** leurs dépendances
- [ ] Aucune story ne compromet les exigences transverses (build sur agent, secrets dans Jenkins)
- [ ] Le prochain numéro de story est correct et ne percute pas une story existante

## Exemple

**`retours.md` initial :**

```markdown
## 2026-10-12 — Retour professeur

* La Quality Gate ne bloque pas le build
* Le build dure 22 minutes, l'objectif est < 10 minutes
* Explique le rollback automatique pendant la soutenance
```

**Stories générées :**

| Story | Titre | Exigence | Origine |
|-------|-------|----------|---------|
| `story-011` | Quality Gate bloquante avec `waitForQualityGate` | `EXG-COM-06` | Retour 2026-10-12 #1 |
| `story-013` | Pipeline sous 10 min (parallélisme + cache) | `EXG-N1a-04` | Retour 2026-10-12 #2 |

Le retour n°3 (« expliquer le rollback ») n'est pas une story de code : il alimente le
`docs/rapport-technique/soutenance.md` et est marqué `[TRAITE: rapport-soutenance]`.

**`retours.md` après traitement :**

```markdown
## 2026-10-12 — Retour professeur

* La Quality Gate ne bloque pas le build [TRAITE: story-011]
* Le build dure 22 minutes, l'objectif est < 10 minutes [TRAITE: story-013]
* Explique le rollback automatique pendant la soutenance [TRAITE: rapport-soutenance]
```