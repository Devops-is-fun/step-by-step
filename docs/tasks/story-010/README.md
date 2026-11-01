# Story 010 — Créer le job Multibranch et brancher le webhook GitHub

Découpage TDA-friendly en **10 tutoriels**, à faire **dans l'ordre**.

- **Exigences TP couvertes :** `EXG-COM-06` (job Multibranch déclenché par webhook), `EXG-COM-07`
- **Qui :** 🧑‍💻 **toi**, du début à la fin : cette story est 100 % CI/CD
- **Fichiers produits :** `Jenkinsfile` (minimal), `scripts/check-webhook.sh`,
  `docs/tooling/webhook-relay.md` ; un conteneur `cicd-webhook-relay` ; 2 credentials Jenkins ; un
  job `step-by-step` ; un webhook GitHub ; une PR de démonstration
- **Durée totale :** ⏱️ 2 h 50
- **Prérequis de la story :** story 009 terminée ; stack `cicd` démarré ; agent `docker-agent`
  en ligne ; accès à ton compte GitHub de l'école et à Bitwarden

> **Répertoire de travail de tous les tutoriels :** la racine du projet, le dossier qui contient
> `docs/`.

## L'image de la story

Ton Jenkins habite une maison **sans adresse publique** (`localhost`) : le facteur GitHub ne peut
pas sonner. Plutôt que de percer un trou dans le mur (un tunnel, qui exposerait toute la maison), tu
prends une **boîte postale** chez smee.io. GitHub y dépose ses lettres (les webhooks) ; un **coursier**
(le relais) va les chercher et les glisse sous la porte de Jenkins. Chaque lettre est **cachetée**
(le secret partagé) : Jenkins jette celles dont le cachet est faux.

## Ordre des tutoriels

| # | Titre | Temps | Difficulté | Dépend de |
|---|-------|-------|------------|-----------|
| [001](task-001.md) | Ouvrir la boîte postale smee et démarrer le relais | ⏱️ 20 | ⭐⭐⭐ | story 009 |
| [002](task-002.md) | Créer le secret du webhook et le confier à Jenkins | ⏱️ 15 | ⭐⭐ | 001 |
| [003](task-003.md) | Créer le jeton GitHub et la credential `github-token` | ⏱️ 20 | ⭐⭐⭐ | 002 |
| [004](task-004.md) | Écrire le `Jenkinsfile` minimal et le pousser | ⏱️ 15 | ⭐⭐ | 003 |
| [005](task-005.md) | Créer le job Multibranch `step-by-step` | ⏱️ 20 | ⭐⭐⭐ | 004 |
| [006](task-006.md) | Brancher le webhook dans GitHub | ⏱️ 15 | ⭐⭐ | 005 |
| [007](task-007.md) | Écrire et lancer `scripts/check-webhook.sh` | ⏱️ 15 | ⭐⭐ | 006 |
| [008](task-008.md) | Écrire `docs/tooling/webhook-relay.md` | ⏱️ 15 | ⭐⭐ | 007 |
| [009](task-009.md) | La PR de démonstration : push → build → statut → merge | ⏱️ 25 | ⭐⭐⭐ | 008 |
| [010](task-010.md) | Faire le bilan | ⏱️ 10 | ⭐ | 009 |

Coupures naturelles : après le **003** (≈ 55 min, les secrets sont rangés) et après le **006**
(≈ 1 h 45, le premier webhook arrive).

## Jalons

| Jalon | Tutoriels | Preuve que tu y es |
|-------|-----------|--------------------|
| **J1 — Le coursier** | 001 | `docker logs cicd-webhook-relay` : `Connected` + `Forwarding` |
| **J2 — Les clés** | 002, 003 | credentials `github-webhook-secret` et `github-token` dans Jenkins |
| **J3 — Le job** | 004, 005 | job `step-by-step` ; `main` détectée ; build vert sur `docker-agent` |
| **J4 — Le webhook** | 006, 007 | ping GitHub reçu ; `check-webhook.sh` → `Webhook path is working` |
| **J5 — La boucle complète** | 008 à 010 | push → build en < 10 s ; statut vert sur la PR ; PR mergée |

## Décisions déjà prises

| Sujet | Décision |
|-------|----------|
| Joindre Jenkins | relais **smee.io** (conteneur `cicd-webhook-relay`), Jenkins reste privé ; plus de tunnel |
| URL smee | rangée dans **Bitwarden**, lue par `read -rs` : quiconque la connaît peut lire tes notifications |
| Secret du webhook | généré par Bitwarden ; credential Jenkins `github-webhook-secret` (*Secret text*) |
| Jeton GitHub | *fine-grained*, limité au dépôt `step-by-step` ; credential `github-token` (*Username with password*) |
| Jenkinsfile | minimal : `./mvnw -B validate` sur `docker-agent` |
| Preuve PR | branche `demo-webhook` + PR, mergée à la fin |

Le détail est dans le `## Journal des décisions` du tutoriel 001.
