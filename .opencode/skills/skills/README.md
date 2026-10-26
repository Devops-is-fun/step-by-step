# Skills du TP CI/CD — ESGI Master 1

Ce dossier contient les guides de workflow (skills) utilisés pour piloter le projet **CI/CD avec
Jenkins** de bout en bout, de l'énoncé jusqu'aux tutoriels d'implémentation.

## Contexte du projet

| Élément | Valeur |
|---------|--------|
| Nature du travail | TP du cours CI/CD (ESGI Master 1 « Information Architecture des Logiciels ») |
| Énoncé | `docs/Projet_CICD_Jenkins.pdf` — source de vérité |
| Mode de travail | **En solo.** L'exigence « en groupe / trinôme / quadrinôme » de l'énoncé est ignorée |
| Application web | Java / Spring Boot, build Maven, tests JUnit 5 |
| Outillage | Docker Compose : Jenkins (controller + agents), SonarQube, Nexus |
| Environnements | `staging` et `production`, deux stacks Docker Compose en local |
| Règles transverses | Builds sur agents, jamais sur le controller · Secrets uniquement dans les credentials Jenkins |
| Rendu final | Dépôts Git, `README.md` de relance, rapport technique (architecture, choix, DORA), soutenance |

## Chaîne de skills

```
  docs/Projet_CICD_Jenkins.pdf
              │
              ▼
    [specification-fr]  ──►  docs/specification/specification.md
        exigences EXG-*         (matrice de traçabilité)
              │
              ▼
       [story-fr]        ──►  docs/stories/story-XXX.md
        stories                 docs/stories/plan-implementation.md
              │                 (feuille de route solo + jalons)
              ▼
       [tasks-fr]        ──►  docs/tasks/story-XXX/task-YYY.md
        tutoriels TDA           (pas-à-pas, 10-25 min, vérifications)

  [feedback-fr]  ──►  retours / incidents du TP  ──►  nouvelles stories
```

## Skills disponibles

| Skill | Rôle | Produit |
|-------|------|---------|
| **[specification-fr](./specification-fr/SKILL.md)** | Extraction des exigences de l'énoncé et rédaction itérative de la spécification | `docs/specification/specification.md` |
| **[story-fr](./story-fr/SKILL.md)** | Découpage en user stories traçables aux exigences + feuille de route solo | `docs/stories/story-XXX.md`, `docs/stories/plan-implementation.md` |
| **[tasks-fr](./tasks-fr/SKILL.md)** | Découpage d'une story en tutoriels pas-à-pas TDA-friendly | `docs/tasks/story-XXX/task-YYY.md` |
| **[feedback-fr](./feedback-fr/SKILL.md)** | Transformation des retours et incidents du TP en nouvelles stories | stories neuves + mise à jour de la traçabilité |
| **[specification-en](./specification-en/SKILL.md)** | Variante anglaise de la spécification | idem |
| **[story-en](./story-en/SKILL.md)** | Variante anglaise des stories | idem |

> `tasks-en` (variante anglaise orientée « prompt pour agent de code ») a été **supprimé** : les
> tutoriels doivent rester exécutables à la main, en français, pour l'apprentissage.

## Utiliser une skill

Demander explicitement à l'agent, dans cet ordre :

```
/ specification-fr
/ story-fr docs/specification/specification.md
/ tasks-fr 001
/ feedback-fr
```

## Conventions partagées

### 1. Questions dans le chat, pas de fichier `questions.md`

Les choix techniques sont tranchés par **questions posées dans le chat**, par lots de 5 maximum,
au format :

```text
Question 1 — <sujet>
  a) <option A>  → <conséquence>       (⭐ recommandé : <raison>)
  b) <option B>  → <conséquence>
Réponds avec : 1a, 2b, 3c...
```

Chaque réponse est **recopiée** dans le `## Journal des décisions` du fichier produit, ce qui
conserve une trace écrite sans avoir à éditer un fichier de questions.

### 2. Traçabilité des exigences

Chaque exigence de l'énoncé reçoit un identifiant **stable**, repris à l'identique dans la
spécification, les stories et le plan :

| Préfixe | Signification |
|---------|---------------|
| `EXG-COM-*` | partie commune (obligatoire) |
| `EXG-N1a-*` / `EXG-N1b-*` | modules de niveau 1 |
| `EXG-N2a-*` / `EXG-N2b-*` | modules de niveau 2 |
| `EXG-N3a-*` / `EXG-N3b-*` | modules de niveau 3 |
| `EXG-REND-*` | rendus attendus (README, rapport, soutenance) |

**Règle :** aucune exigence sans story, aucune story sans exigence.

### 3. TDA-friendly

Le découpage `tasks-fr` est conçu pour une **personne en apprentissage avec un TDA** :

- une action par étape : une commande, un clic d'interface, ou un fichier
- 10 à 25 minutes maximum par tutoriel, une seule tâche mentale
- contenu exact et textuel, jamais « ajoute la configuration »
- répertoire de travail ou URL toujours explicité
- « Résultat attendu » et « Vérifie que » à chaque étape
- section « Comment annuler » pour les étapes à effet de bord
- bloc « Pourquoi » pour expliquer la notion après l'avoir appliquée
- section finale obligatoire `## ✅ Avant de passer au tutoriel suivant`

### 4. Commits Conventional Commits en anglais

Exigence du module N1 de l'énoncé. Format : `type(scope): description`, avec `scope` parmi `app`,
`jenkins`, `pipeline`, `envs`, `sonar`, `nexus`, `docs`.

### 5. Secrets

**Uniquement** dans les credentials Jenkins. Jamais dans un fichier versionné (`.env`, `secrets/`,
`*.pem`, `*.key`, `id_rsa*`). Chaque tutoriel qui touche à un secret rappelle ce garde-fou, et la
section `git status` avant commit sert de filet.

### 6. Arborescence documentaire attendue

```
docs/
├── Projet_CICD_Jenkins.pdf        énoncé
├── specification/
│   ├── specification.md           spécification + matrice EXG-*
│   └── retours.md                 retours professeur, incidents, écarts
├── stories/
│   ├── story-001.md               user stories
│   └── plan-implementation.md     feuille de route solo + jalons
├── tasks/
│   └── story-001/
│       ├── task-001.md            tutoriels TDA-friendly
│       └── task-002.md
└── rapport-technique/             architecture, choix, métriques DORA, soutenance
```