# Plan d'implémentation — Projet CI/CD Jenkins

> 32 stories : **28 au fil du périmètre principal**, **4 en bonus**. Travail en **binôme** depuis le
> 01/11/2026 : répartition dans la spécification, section « Organisation du binôme ». Toutes découlent de
> `docs/specification/specification.md` ; chaque exigence `EXG-*` est couverte au moins une fois.

## Jalons

| Jalon | Stories | Objectif | Critère de sortie |
|-------|---------|----------|-------------------|
| **0 — Outillage** | 001 à 006 | Jenkins, SonarQube, Nexus, agent, dépôt Git | Les 3 services démarrent en une commande, un agent existe, le dépôt est propre |
| **1 — Socle applicatif** | 007 à 009 | Application Spring Boot minimale **testable** | `./mvnw -B verify` vert, 4 tests unitaires sur `REG-01`/`REG-02` |
| **1 — Intégration continue** | 010 à 014, 032 | Webhook, accueil du binôme, Jenkinsfile, intégration, gate, artefact | Un push déclenche un build complet qui publie un artefact versionné |
| **2 — Livraison continue** | 015 à 018 | Staging, blue/green, validation manuelle | Un push sur `main` atteint la **production** après validation, par bascule |
| **3 — Modules et fiabilité** | 019 à 024 | Secrets, Shared Library, N1a, N1b, N3b | Couverture 60 %, build < 10 min, release taguée, DORA mesurées |
| **4 — Rendus attendus** | 025 à 027 | README, rapport, vidéo de démonstration | Une personne extérieure peut relancer la chaîne |
| **5 — Bonus** | 028 à 031 | App complète, Pomodoro, Flyway, tests de la lib | Uniquement si le jalon 4 est bouclé |

## Ordre d'exécution et dépendances

```
001 ──► 002 ──► 003 ──┐
  │      │      └────► 004
  │      └───► 005      │
  └───► 006             │
                        ├──► 019 ──► 020 ──► 021
007 ──► 008 ──► 009     │         └──► 022
                        └──► 023, 024

006 ──► 010 ──► 011 ──► 012 ──► 013 ──► 014 ──► 015 ──► 016 ──► 017 ──► 018
                                    └──► 022
                                                  └──► 021

015..024 ──► 025 ──► 026 ──► 027 ──► 028 ──► 029 / 030 / 031
```

Parallélisme du binôme : après 010 → 032, l'étudiante enchaîne 011 → 014 pendant que le binôme
fait 015 (staging), puis 017, 023, 024.

Contrainte de séquencement forte : **018 avant 027** (la vidéo de démonstration montre l'`input`), **013
avant 018** (on ne déploie que du code conforme), **019 avant 020** (la bibliothèque consomme des
credentials).

## Traçabilité des exigences

### Partie commune

| Exigence | Stories |
|----------|---------|
| `EXG-COM-01` Outillage en une commande | 001 |
| `EXG-COM-02` Jenkins + SonarQube + Nexus | 001, 002, 003, 004 |
| `EXG-COM-03` Deux environnements | 015 (staging), 017 (production) |
| `EXG-COM-04` Builds sur agent, pas sur le controller | 005, 011 |
| `EXG-COM-05` Pipeline as Code déclaratif | 011, 020 |
| `EXG-COM-06` Job Multibranch déclenché par webhook | 010 |
| `EXG-COM-07` Statut de build sur les PR | 010 |
| `EXG-COM-08` Lint | 011 |
| `EXG-COM-09` Tests unitaires + rapport JUnit | 009, 011, 021 |
| `EXG-COM-10` Tests d'intégration avec Docker Compose | 008, 012 |
| `EXG-COM-11` Quality Gate bloquante | 013 |
| `EXG-COM-12` Artefact versionné au hash du commit | 014 |
| `EXG-COM-13` Publication dans le dépôt d'artefacts | 014 |
| `EXG-COM-14` Déploiement par script idempotent | 015, 017 |
| `EXG-COM-15` Déploiement staging automatique depuis `main` | 016 |
| `EXG-COM-16` Production après validation manuelle | 018 |
| `EXG-COM-17` Même artefact staging et production | 018 |
| `EXG-COM-18` Shared Library versionnée par tag | 020 |
| `EXG-COM-19` Secrets uniquement dans Jenkins | 003, 004, 019, 025 |

### Modules

| Exigence | Story | Module |
|----------|-------|--------|
| `EXG-N1a-01` Couverture minimale | 021 | N1a |
| `EXG-N1a-02` Rapports publiés dans Jenkins | 021 | N1a |
| `EXG-N1a-03` Notification de chaque build | 021 | N1a |
| `EXG-N1a-04` Pipeline sous 10 minutes | 022 | N1a |
| `EXG-N1b-01` Conventional Commits | 006, 023 | N1b |
| `EXG-N1b-02` Version calculée automatiquement | 023 | N1b |
| `EXG-N1b-03` Changelog généré | 023 | N1b |
| `EXG-N1b-04` Tag Git créé | 023 | N1b |
| `EXG-N1b-05` Artefact versionné | 014, 023 | N1b |
| `EXG-N1b-06` Notes de version publiées | 023 | N1b |
| `EXG-N2a-01` Blue/green derrière un proxy | 017 | N2a |
| `EXG-N2a-02` Vérification de santé après bascule | 017 | N2a |
| `EXG-N2a-03` Retour arrière automatique | 017 | N2a |
| `EXG-N3b-01` Prometheus et Grafana | 024 | N3b |
| `EXG-N3b-02` Quatre métriques DORA | 024 | N3b |
| `EXG-N3b-03` Suivi dans la durée | 024 | N3b |

### Rendus

| Exigence | Stories |
|----------|---------|
| `EXG-REND-01` Dépôts Git | 006, 020 |
| `EXG-REND-02` README de relance | 025 |
| `EXG-REND-03` Rapport technique | 026 |
| `EXG-REND-04` Démo live commit → production | 027 |
| `EXG-REND-05` Échec volontaire bloqué | 013, 021, 027 |
| `EXG-REND-06` Capacité à expliquer la chaîne | 027 |

### Hors périmètre (assumé)

`EXG-BON-01` : écarté. Les modules ci-dessous sont écartés par la spécification § 2 ; aucune story
ne les couvre, et c'est un choix explicite à assumer à la soutenance.

| Exigence | Raison de l'exclusion |
|----------|------------------------|
| `EXG-N2b-01` | `EXG-N2b-02` | `EXG-N2b-03` | `EXG-N2b-04` | Un stack Docker par pull request : le module le plus lourd, hors budget solo |
| `EXG-N3a-01` | `EXG-N3a-02` | `EXG-N3a-03` | `EXG-N3a-04` | Terraform/OpenTofu et Ansible non installés ; le module le plus long à maintenir |

### Bonus

| Exigence | Story |
|----------|-------|
| `EXG-BON-01` Feature flags | *écarté* |
| `EXG-BON-02` Migrations expand/contract | 030 |
| `EXG-BON-03` Tests JenkinsPipelineUnit | 031 |

## Sortie du périmètre obligatoire

Toutes les exigences obligatoires ont au moins une story. Il ne reste **aucune** story d'obligation
ouverte : le plan est complet.

## Règles de travail

1. Une story à la fois ; on ne démarre pas la story N+1 tant que la N n'est pas cochée.
2. `scripts/check-no-secrets.sh` et `git status` sont exécutés **avant chaque commit**.
3. Les commits suivent les Conventional Commits en anglais, sinon jreleaser (story 023) refusera de
   calculer une version.
4. Le découpage en tâches elementaires est produit par le skill `tasks-fr` au moment de l'implémentation
   de chaque story : pas de tâches figées ici, pour ne pas devenir faux dès qu'une décision change.
