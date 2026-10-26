# Story 004 — Configurer Nexus

Découpage TDA-friendly en **8 tutoriels**, à faire **dans l'ordre**.

- **Exigences TP couvertes :** `EXG-COM-02`, `EXG-COM-19` (part)
- **Fichiers produits :** `scripts/check-nexus.sh`, `docs/tooling/nexus.md`
- **Durée totale :** ⏱️ 2 h 20
- **Prérequis de la story :** story 003 terminée, stack `cicd` démarré et `healthy`, Bitwarden ouvert

> **Répertoire de travail de tous les tutoriels :** la racine du projet, le dossier qui contient
> `docs/`.
>
> **Interface :** Nexus 3.96 Community n'existe qu'en anglais ; les libellés sont donnés tels quels.
> Si un libellé diffère légèrement, fie-toi au chemin des menus et note-le dans `retours.md`.

## Ordre des tutoriels

| # | Titre | Temps | Difficulté | Dépend de |
|---|-------|-------|------------|-----------|
| [001](task-001.md) | Passer l'assistant de Nexus | ⏱️ 20 | ⭐⭐ | story 003 |
| [002](task-002.md) | Vérifier la politique de `maven-releases` | ⏱️ 10 | ⭐ | 001 |
| [003](task-003.md) | Créer le rôle `step-by-step-deployer` | ⏱️ 15 | ⭐⭐ | 001 |
| [004](task-004.md) | Créer `nexus-deployer` et sa credential Jenkins | ⏱️ 20 | ⭐⭐ | 003 |
| [005](task-005.md) | Publier un témoin, prouver qu'on ne peut pas le republier | ⏱️ 20 | ⭐⭐⭐ | 002 |
| [006](task-006.md) | Écrire `scripts/check-nexus.sh` | ⏱️ 20 | ⭐⭐⭐ | 001 |
| [007](task-007.md) | Lancer le script, puis le faire échouer exprès | ⏱️ 15 | ⭐⭐ | 006 |
| [008](task-008.md) | Documenter (en anglais) et faire le bilan | ⏱️ 20 | ⭐⭐ | 007 |

## Jalons

| Jalon | Tutoriels | Preuve que tu y es |
|-------|-----------|--------------------|
| **J1 — Nexus sécurisé** | 001 | connexion avec le mot de passe de Bitwarden ; `admin.password` supprimé |
| **J2 — Dépôt et droits** | 002 à 004 | *Disable redeploy* ; rôle et compte créés ; credential `nexus-deploy-creds` |
| **J3 — Immuabilité prouvée** | 005 | 1re publication `201`, 2e publication `409` |
| **J4 — Vérification automatique** | 006, 007 | `check-nexus.sh` affiche « Nexus is compliant » |
| **J5 — Story terminée** | 008 | doc écrite, aucun secret dans les fichiers |

## Décisions déjà prises

| Sujet | Décision |
|-------|----------|
| Dépôt | `maven-releases` créé par défaut, politique *Disable redeploy* vérifiée |
| Mot de passe admin | Bitwarden |
| Mot de passe `nexus-deployer` | généré par Bitwarden, rangé **uniquement** dans Jenkins (`nexus-deploy-creds`) |
| `check-nexus.sh` | sans identifiants (Nexus Community n'a pas de jetons d'API) |
| Publication témoin | avec le compte admin ; republication refusée ; témoin supprimé |
| Accès anonyme | lecture activée (staging et production téléchargent sans identifiants) |
| Langue | messages du script et `docs/tooling/nexus.md` en anglais |

Le détail est dans le `## Journal des décisions` du tutoriel 001.

## Après la story

- Laisse le stack **en marche** : la story 005 crée l'agent Jenkins.
- **Aucun commit** : le premier commit du projet est fait en story 006.
