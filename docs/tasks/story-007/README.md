# Story 007 — Démarrer le squelette Spring Boot exécutable

Découpage TDA-friendly en **9 tutoriels**, à faire **dans l'ordre**.

- **Exigences TP couvertes :** support applicatif (alimente `EXG-COM-09`, `EXG-N1a-01`, `EXG-COM-12`)
- **Fichiers produits :**
  - code (écrit par Claude) : `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`,
    `.gitattributes`, `src/…` ;
  - CI/CD (écrits par toi) : bloc Sonar du `pom.xml`, `Dockerfile`, `.dockerignore`, retouche de
    `scripts/check-no-secrets.sh`, sections du README ;
  - **2 commits** poussés sur GitHub.
- **Durée totale :** ⏱️ 2 h 05
- **Prérequis de la story :** story 006 terminée ; stack `cicd` démarré (SonarQube `UP`)

> **Répertoire de travail de tous les tutoriels :** la racine du projet, le dossier qui contient
> `docs/`.

## Nouveau partage des rôles (à partir de cette story)

| Qui | Quoi |
|-----|------|
| **Toi** | les décisions (questions de cadrage) |
| **Claude** | le code de l'application : classes, tests, dépendances applicatives du `pom.xml` |
| **Toi, guidée** | tout ce qui touche à la CI/CD : Sonar, Docker, scripts, Jenkins, Nexus |
| **Toi** | les commits et les push |

Chaque tutoriel porte une étiquette : **🤖 Claude a implémenté** (tu découvres et tu vérifies) ou
**🧑‍💻 Tu fais (guidée)**.

## L'image de la story

Jusqu'ici, tu as construit **l'usine** : Jenkins, SonarQube, Nexus. Il lui manquait un **produit** à
fabriquer. Claude a posé la première pièce, une application qui démarre et dit « je vais bien »
(`/actuator/health`). Toi, tu l'emballes (le `Dockerfile`) et tu la fais passer au contrôle qualité
(SonarQube).

## Ordre des tutoriels

| # | Titre | Qui | Temps | Difficulté | Dépend de |
|---|-------|-----|-------|------------|-----------|
| [001](task-001.md) | Découvrir le squelette et le construire | 🤖 | ⏱️ 15 | ⭐ | story 006 |
| [002](task-002.md) | Lancer l'application et interroger sa santé | 🤖 | ⏱️ 10 | ⭐ | 001 |
| [003](task-003.md) | Ajouter le bloc Sonar au `pom.xml` | 🧑‍💻 | ⏱️ 15 | ⭐⭐ | 002 |
| [004](task-004.md) | Lancer la première analyse SonarQube | 🧑‍💻 | ⏱️ 15 | ⭐⭐ | 003 |
| [005](task-005.md) | Écrire le `Dockerfile` et le `.dockerignore` | 🧑‍💻 | ⏱️ 20 | ⭐⭐⭐ | 002 |
| [006](task-006.md) | Construire l'image et la tester | 🧑‍💻 | ⏱️ 15 | ⭐⭐ | 005 |
| [007](task-007.md) | Apprendre au vigile à ignorer `mvnw` | 🧑‍💻 | ⏱️ 10 | ⭐⭐ | 001 |
| [008](task-008.md) | Compléter le README (FR + EN) | 🧑‍💻 | ⏱️ 15 | ⭐ | 006 |
| [009](task-009.md) | Commiter, pousser et faire le bilan | 🧑‍💻 | ⏱️ 15 | ⭐⭐ | 004, 007, 008 |

Bonne coupure naturelle : après le **004** (≈ 55 min, analyse Sonar visible).

## Jalons

| Jalon | Tutoriels | Preuve que tu y es |
|-------|-----------|--------------------|
| **J1 — L'app existe** | 001, 002 | `BUILD SUCCESS` ; `"status":"UP"` |
| **J2 — Contrôle qualité** | 003, 004 | projet `step-by-step` dans SonarQube, lignes de code > 0 |
| **J3 — L'app est emballée** | 005, 006 | image `step-by-step:0.1.0-SNAPSHOT` qui répond `UP` |
| **J4 — Story terminée** | 007 à 009 | 2 commits poussés ; `git status` vide |

## Décisions déjà prises

| Sujet | Décision |
|-------|----------|
| Version | Spring Boot **4.1.2**, Java **17** (`maven.compiler.release`), Maven **3.9.16** par le wrapper |
| Port local | **8090** (8080 = Jenkins, 8081 = Nexus) ; l'application écoute sur 8080 **dans** le conteneur |
| Dockerfile | écrit par toi ; build `eclipse-temurin:17.0.20.1_1-jdk-noble` + `./mvnw`, runtime `…-jre-noble`, utilisateur non-root |
| Bloc Sonar | écrit par toi : `sonar.projectKey`, `sonar.host.url`, version du scanner épinglée |
| Code | squelette strict : classe principale, `contextLoads`, `/actuator/health` |

Le détail est dans le `## Journal des décisions` du tutoriel 001.
