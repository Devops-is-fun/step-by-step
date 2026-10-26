# Tutoriel 001 — Story #002 : Débloquer Jenkins et installer les plugins suggérés

**Temps estimé :** ⏱️ 20 minutes (dont 5 à 10 d'attente)
**Difficulté :** ⭐
**Dépend de :** story 001 terminée
**Prérequis :** les trois conteneurs `healthy` (`docker compose ps`)
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `http://localhost:8080`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Passer l'écran « Débloquer Jenkins » avec le mot de passe initial **lu à la volée**, puis laisser
Jenkins installer ses plugins suggérés.

## 📋 Ce que tu vas apprendre

- Lire un secret dans un conteneur sans jamais l'écrire dans un fichier
- Ce que sont les « plugins suggérés » de Jenkins
- Pourquoi Jenkins est presque vide tant qu'on ne lui ajoute pas de plugins

---

## 🪜 Les étapes

### Étape 1 : Ouvre Jenkins

**Où :** `http://localhost:8080` (navigateur)

**Action :**

> Ouvre `http://localhost:8080` dans ton navigateur.

**Résultat attendu :** la page **« Débloquer Jenkins »** (« Unlock Jenkins » en anglais), avec un
champ **« Mot de passe administrateur »**.

**✅ Vérifie que :**
- [ ] La page « Débloquer Jenkins » s'affiche

---

### Étape 2 : Lis le mot de passe initial

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
> ```

**Résultat attendu :** une seule ligne de 32 caractères, chiffres et lettres de `a` à `f`.

**Pourquoi :** le mot de passe est **lu à la volée** dans le volume de Jenkins : il s'affiche dans
ton terminal, mais n'est écrit dans aucun fichier du projet (décision de la spécification § 8). Il
ne servira qu'une fois : dès le tutoriel 002, ton propre mot de passe le remplace.

**✅ Vérifie que :**
- [ ] Tu n'as **pas** copié ce mot de passe dans un fichier, ni dans tes notes

---

### Étape 3 : Débloque Jenkins

**Où :** `http://localhost:8080` (navigateur)

**Action :**

> Copie la ligne affichée à l'étape 2, colle-la dans le champ « Mot de passe administrateur », puis
> clique sur **« Continuer »** (« Continue »).

**Résultat attendu :** la page **« Personnaliser Jenkins »** (« Customize Jenkins »), avec deux
grands boutons.

**✅ Vérifie que :**
- [ ] La page « Personnaliser Jenkins » s'affiche

---

### Étape 4 : Installe les plugins suggérés

**Où :** `http://localhost:8080` (navigateur)

**Action :**

> Clique sur **« Installer les plugins suggérés »** (« Install suggested plugins »).

**Résultat attendu :** une grille de plugins qui passent un à un au **vert**. Compte **5 à 10
minutes**, selon ta connexion.

**Pourquoi :** Jenkins seul ne sait presque rien faire : Git, les pipelines, les rapports JUnit…
tout vient de plugins. Les « suggérés » sont le socle recommandé par l'équipe Jenkins ; ils
contiennent déjà 6 des 10 plugins de ta liste (`git`, `github-branch-source`, `workflow-aggregator`,
`pipeline-graph-view`, `credentials-binding`, `junit`). Les 4 autres sont installés au tutoriel 003.

**✅ Vérifie que :**
- [ ] Tous les plugins de la grille sont verts
- [ ] Si un plugin reste rouge, tu as cliqué sur **« Réessayer »** (« Retry »)

---

### Étape 5 : Arrête-toi sur la création du compte

**Où :** `http://localhost:8080` (navigateur)

**Action :**

> Quand l'installation est finie, observe la page qui s'affiche. **Ne remplis rien et ne clique sur
> rien.**

**Résultat attendu :** la page **« Créer le 1er utilisateur administrateur »** (« Create First Admin
User »).

**✅ Vérifie que :**
- [ ] La page « Créer le 1er utilisateur administrateur » s'affiche
- [ ] Tu n'as **pas** cliqué sur « Continuer en tant qu'administrateur » : sinon le compte `admin`
      garderait le mot de passe initial

> **Pourquoi s'arrêter ?** Créer le compte demande de préparer un mot de passe dans ton gestionnaire
> d'abord. C'est le tutoriel 002. Laisse l'onglet ouvert.

---

## 🧹 Comment tout annuler

Ce tutoriel modifie l'état de Jenkins (plugins installés). Revenir en arrière demande de supprimer le
volume de Jenkins, ce qui est **irréversible** et efface toute sa configuration :

```bash
# ATTENTION — à ne lancer que si tu veux repartir d'un Jenkins vierge :
# docker compose down
# docker volume rm cicd_jenkins_home
# docker compose up -d
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `No such file or directory` à l'étape 2 | L'assistant a déjà été passé. Connecte-toi avec ton compte, ou repars d'un Jenkins vierge (« Comment tout annuler ») |
| Mot de passe refusé à l'étape 3 | Une espace ou un retour à la ligne s'est glissé dans le copier-coller. Recopie seulement les 32 caractères |
| Plusieurs plugins rouges | Souvent un souci réseau passager : clique sur « Réessayer ». Si ça persiste : `docker compose logs --tail=50 jenkins` |
| L'installation semble figée depuis plus de 15 minutes | Rafraîchis la page : l'assistant reprend où il en était |
| Tu as fermé l'onglet avant l'étape 5 | Rouvre `http://localhost:8080` ; si Jenkins demande de se connecter, utilise `admin` et le mot de passe de l'étape 2 |

---

## 📚 Aide-mémoire des commandes de vérification

| Besoin | Commande |
|--------|----------|
| Voir les conteneurs et leur état | `docker compose ps` |
| Lire les logs de Jenkins | `docker compose logs --tail=50 jenkins` |
| Lire le mot de passe initial (sans le stocker) | `docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

### Ce que tu dois avoir fait

- [ ] Étape 1 → ouvert la page « Débloquer Jenkins »
- [ ] Étape 2 → lu le mot de passe initial dans le terminal
- [ ] Étape 3 → débloqué Jenkins
- [ ] Étape 4 → installé les plugins suggérés
- [ ] Étape 5 → arrivé sur « Créer le 1er utilisateur administrateur », sans rien remplir

### Ce que tu dois avoir vérifié

| Vérification | Comment vérifier | Attendu |
|--------------|------------------|---------|
| Jenkins tourne | `docker compose ps` | `jenkins ... (healthy)` |
| Plugins suggérés installés | grille de l'assistant | tous verts |

### 🛡️ Garde-fous du TP

- [ ] Le mot de passe initial n'a été écrit dans **aucun** fichier
- [ ] Aucun build ne s'est exécuté sur le controller Jenkins
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien : ce tutoriel ne crée aucun fichier. Le premier commit du projet est fait en **story 006**.

---

## 📝 Journal des décisions

Décisions prises le 25/10/2026, au cadrage de `tasks-fr 002` :

| # | Question | Réponse | Raison |
|---|----------|---------|--------|
| 1 | Liste des plugins | `1a` — modernisée : `pipeline-graph-view` au lieu de `pipeline-stage-view`, `coverage` au lieu de `jacoco`, sans `maven-integration` ni `publish-over-ssh` | aucun plugin déprécié dans la chaîne |
| 2 | Installation | `2a` — par l'interface, liste documentée et vérifiée par `check-jenkins.sh` | conforme à la story, plus léger |
| 3 | Controller à 0 exécuteur | `3a` — en story 005 | pas de doublon ; a du sens une fois l'agent créé |
| 4 | « Aucune URL d'agent exposée » | `4a` — rempli par l'écoute sur `127.0.0.1` ; type d'agent choisi en story 005 | pas de décision prématurée |
| 5 | Identifiants des scripts | `5a` — jeton d'API via `JENKINS_USER` / `JENKINS_TOKEN` | même convention que la story 005 ; pas de mot de passe dans l'historique |

Précision apportée à la génération : l'écran « Sélectionner les plugins » de l'assistant ne propose
qu'une liste réduite, sans `sonar`, `coverage` ni `warnings-ng`. D'où le découpage : plugins suggérés
dans l'assistant (001), puis les 4 complémentaires dans le gestionnaire de plugins (003).

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Lire un secret dans un conteneur sans le stocker
- ✨ Débloquer un Jenkins tout neuf
- ✨ Installer le socle de plugins recommandé

Prochain tutoriel : **#002** — Créer le compte administrateur ⏱️ 15 min

Pause méritée ☕
