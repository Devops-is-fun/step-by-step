# Tutoriel 002 — Story #032 : Écrire la visite guidée `docs/team/onboarding.md`

**Pour :** toi
**Temps estimé :** ⏱️ 10 minutes
**Difficulté :** ⭐
**Dépend de :** Tutoriel 001
**Prérequis :** sur la branche `docs-team-onboarding`
**Exigence TP couverte :** `EXG-REND-06`
**Fichier principal :** `docs/team/onboarding.md`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Donner à Morgan **une page** qui répond à « qu'est-ce qui existe déjà, et où je le trouve ? ».

---

## 🪜 Les étapes

### Étape 1 : Crée la visite guidée

**Où :** `docs/team/` (dossier à créer), dans IntelliJ

**Action :**

> Crée `docs/team/onboarding.md` avec ce contenu **exact** :
>
> ````markdown
> # Visite guidée du projet
>
> Bienvenue Morgan ! Cette page explique en 5 minutes ce qui existe déjà. Les règles de travail sont dans
> `CONTRIBUTING.md` : lis-le d'abord.
>
> ## La chaîne, aujourd'hui
>
> ```
> git push ──► GitHub ──webhook──► smee.io ──► relais (conteneur) ──► Jenkins (controller)
>                                                                        │
>                                                                        ▼
>                                                     agent docker-agent : ./mvnw -B validate
>                                                                        │
>                                                                        ▼
>                                                  statut ✅/❌ affiché sur le commit et la PR
> ```
>
> Tout tourne en Docker Compose, **sur la machine de Géraldine**. Jenkins n'est pas exposé à
> Internet : c'est le relais qui va chercher les webhooks chez smee.io.
>
> ## Ce qui est fait
>
> | Story | Ce qui existe | Doc |
> |-------|---------------|-----|
> | 001 | Jenkins, SonarQube, Nexus en une commande (`docker compose up -d`) | `README.md` |
> | 002 | Jenkins débloqué, plugins, jeton pour les scripts | `docs/tooling/jenkins-configuration.md` |
> | 003 | SonarQube : projet `step-by-step`, Quality Gate à 60 % de couverture | `docs/tooling/sonarqube.md` |
> | 004 | Nexus : dépôt `maven-releases`, compte de déploiement | `docs/tooling/nexus.md` |
> | 005 | Agent Jenkins `docker-agent` : **aucun build sur le controller** | `docs/tooling/jenkins-agent.md` |
> | 006 | Dépôt Git, vigile anti-secrets (hook `pre-commit`) | `docs/tooling/git-conventions.md` |
> | 007 | Application Spring Boot 4.1 / Java 17, image Docker, première analyse Sonar | `README.md` |
> | 008 | Modèle de données, Flyway, PostgreSQL de test (`compose-test.yml`), tests d'intégration | `docs/architecture/data-model.md` |
> | 009 | Règles métier `REG-01` et `REG-02`, 21 tests unitaires | `docs/architecture/business-rules.md` |
> | 010 | Job Multibranch `step-by-step`, webhook GitHub via relais smee.io | `docs/tooling/webhook-relay.md` |
>
> ## Tes stories
>
> | Story | Sujet | Ce dont tu as besoin chez toi |
> |-------|-------|-------------------------------|
> | 015 | Stack staging idempotente | Docker, l'image de l'application |
> | 017 | Blue/green en production (module N2a) | Docker, nginx |
> | 023 | Releases automatisées (module N1b) | Git, Maven |
> | 024 | Prometheus, Grafana, métriques DORA (module N3b) | Docker |
> | 025 | README de relance complète | toute la stack : c'est le test du README |
>
> Chaque story est décrite dans `docs/stories/story-0XX-….md` (critères d'acceptation, preuves,
> risques). Les décisions déjà prises sont dans `docs/specification/specification.md`, section
> « Journal des décisions ».
>
> ## Petit glossaire
>
> | Mot | Sens ici |
> |-----|----------|
> | Controller | le Jenkins principal : il orchestre, il ne construit **jamais** |
> | Agent | le conteneur `cicd-jenkins-agent`, où tournent les builds |
> | Multibranch | un job qui crée tout seul un sous-job par branche et par PR |
> | Vigile | `scripts/check-no-secrets.sh`, lancé avant chaque commit (hook) et par Jenkins |
> | Relais smee | le conteneur `cicd-webhook-relay` qui apporte les webhooks de GitHub à Jenkins |
> | Quality Gate | le verdict de SonarQube ; il bloquera le pipeline (story 013) |
> | Credential | un secret rangé dans Jenkins, appelé par son identifiant, jamais écrit en clair |
>
> ## Par où commencer
>
> 1. `CONTRIBUTING.md`, section « First setup ».
> 2. Lancer l'application chez toi : `README.md`, section « Lancer l'application en local ».
> 3. Ta première PR : note tes écarts du premier lancement en bas de cette page.
>
> ## Notes du premier lancement (Morgan)
>
> *(à compléter dans ta première PR : ce qui a coincé en suivant le README, ou « aucun écart »)*
> ````
>
> **Enregistre.** Si IntelliJ demande **« Add file to Git? »** : **Cancel**.

**Pourquoi « sur la machine de Géraldine » ?** Morgan doit savoir **tout de suite** pourquoi une PR
n'a pas de statut quand ta machine est éteinte.

**✅ Vérifie que :**
- [ ] L'aperçu montre le schéma dans un cadre et 3 tableaux

---

## 🧹 Comment tout annuler

```bash
rm -r docs/team
```

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Fichier | `ls docs/team/onboarding.md` | présent |

---

## 🎉 Bravo !

La visite guidée est prête. 🎉

Prochain tutoriel : **#003** — Commiter, ouvrir la PR, merger ⏱️ 10 min
