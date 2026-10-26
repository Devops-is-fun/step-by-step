# Tutoriel 007 — Story #002 : Documenter la configuration et faire le bilan

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 006
**Prérequis :** `scripts/check-jenkins.sh` passe ; `JENKINS_TOKEN` chargé dans le terminal
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `docs/tooling/jenkins-configuration.md` (à créer)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Écrire la documentation qui permet à quelqu'un d'autre de refaire la configuration de Jenkins, puis
vérifier qu'aucun secret n'a fui dans les fichiers du projet.

## 📋 Ce que tu vas apprendre

- Documenter une configuration faite dans une interface (elle n'existe dans aucun fichier)
- Chercher la valeur d'un secret dans des fichiers **sans l'afficher**
- Faire le bilan d'une story avec ses critères de Done

---

## 🪜 Les étapes

### Étape 1 : Crée le dossier de documentation

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> mkdir -p docs/tooling
> ```

**Résultat attendu :** aucune sortie.

---

### Étape 2 : Écris la documentation

**Où :** la racine du projet

**Action :**

> Crée `docs/tooling/jenkins-configuration.md` (réponds **Cancel** à IntelliJ) et écris-y **ta
> traduction en anglais** du contenu ci-dessous : les docs de `docs/tooling/` sont en anglais
> uniquement (décision du 25/10/2026). Les commandes, chemins et identifiants ne se traduisent pas.
>
> ```markdown
> # Configuration de Jenkins
>
> La configuration de Jenkins se fait dans son interface : elle n'existe dans aucun fichier du
> dépôt. Cette page permet de la refaire à l'identique sur un Jenkins vierge.
>
> ## 1. Déblocage
>
> 1. Ouvrir `http://localhost:8080`.
> 2. Lire le mot de passe initial, sans le stocker :
>    `docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword`
> 3. Le coller, puis choisir « Installer les plugins suggérés ».
>
> ## 2. Compte administrateur
>
> | Champ | Valeur |
> |-------|--------|
> | Nom d'utilisateur | `admin` |
> | Mot de passe | généré et rangé dans un gestionnaire de mots de passe, jamais dans un fichier |
> | Nom complet | `Administrateur TP` |
> | Adresse courriel | `admin@example.com` |
> | URL de Jenkins | `http://localhost:8080/` |
>
> ## 3. Plugins
>
> | Identifiant | Origine | Rôle |
> |-------------|---------|------|
> | `git` | suggéré | récupérer le code |
> | `github-branch-source` | suggéré | job Multibranch et webhook GitHub |
> | `workflow-aggregator` | suggéré | Pipeline as Code (`Jenkinsfile`) |
> | `pipeline-graph-view` | suggéré | visualisation des étapes du pipeline |
> | `credentials-binding` | suggéré | `withCredentials` dans le pipeline |
> | `junit` | suggéré | rapports de tests |
> | `sonar` | ajouté | analyse SonarQube et Quality Gate |
> | `coverage` | ajouté | rapport de couverture JaCoCo |
> | `warnings-ng` | ajouté | résultats de Checkstyle et SpotBugs |
> | `pipeline-utility-steps` | ajouté | lecture de fichiers dans le pipeline |
>
> Plugins ajoutés : Administrer Jenkins → Plugins → Plugins disponibles, puis
> `docker compose restart jenkins`.
>
> ## 4. Jeton d'API pour les scripts
>
> 1. `http://localhost:8080/me/security/` → « Ajouter un nouveau jeton », nom `scripts-tp`.
> 2. Ranger le jeton dans le gestionnaire de mots de passe.
> 3. Le charger dans le terminal, sans l'afficher ni le laisser dans l'historique :
>
> ```bash
> export JENKINS_USER=admin
> read -rs JENKINS_TOKEN && export JENKINS_TOKEN
> ```
>
> ## 5. Vérification
>
> ```bash
> ./scripts/check-jenkins.sh
> ```
>
> Le script vérifie l'authentification par jeton, la protection CSRF (crumb) et les 10 plugins. Il
> affiche « Jenkins is compliant » et sort avec le code `0`, ou sort avec le code `1` au premier
> problème d'authentification.
>
> ## 6. Sécurité
>
> - Ports `8080` et `50000` en écoute sur `127.0.0.1` uniquement : aucune URL d'agent exposée.
> - Protection CSRF active (crumb vérifié par le script).
> - Aucun mot de passe ni jeton dans le dépôt : gestionnaire de mots de passe uniquement.
> - Controller à 0 exécuteur : configuré en story 005, avec l'agent.
> ```

**Résultat attendu :** le fichier existe, avec 6 sections numérotées.

**Pourquoi documenter ?** Tout ce que tu as fait dans l'interface vit dans le volume
`cicd_jenkins_home`, pas dans Git. Sans cette page, un `docker compose down -v` effacerait la
configuration **et** la façon de la refaire. C'est aussi ce que demande le rendu
`EXG-REND-02` : une personne extérieure doit pouvoir relancer la chaîne.

**✅ Vérifie que :**
- [ ] Le fichier est dans `docs/tooling/`
- [ ] Il ne contient que des **commandes** et des **noms**, aucune valeur secrète

---

### Étape 3 : Cherche ton jeton dans les fichiers du projet

**Où :** la racine du projet

**Action :**

> Exécute :
>
> ```bash
> grep -rlF "$JENKINS_TOKEN" . --exclude-dir=.git || echo "OK : jeton absent des fichiers"
> ```

**Résultat attendu :** `OK : jeton absent des fichiers`

**Pourquoi `-l` et `-F` ?** `-F` cherche le texte **tel quel** (pas comme une regex) ; `-l` n'affiche
que le **nom** des fichiers trouvés, jamais la ligne. Même si le jeton avait fui, il ne s'afficherait
pas à l'écran.

**✅ Vérifie que :**
- [ ] La sortie est `OK : jeton absent des fichiers`

---

### Étape 4 : Relance le contrôle des secrets de la story 001

**Où :** la racine du projet

**Action :**

> Exécute :
>
> ```bash
> grep -rniE '(password|passwd|secret|token|api[_-]?key)[[:space:]]*[:=][[:space:]]*[^<{$[:space:]]' \
>   docker-compose.yml README.md README.en.md scripts/ docs/tooling/ 2>/dev/null || echo "OK : aucun secret detecte"
> ```

**Résultat attendu :** **deux** fausses alertes (les numéros de ligne peuvent varier) :

```
README.en.md:24:| Jenkins    | http://localhost:8080   | initial password: `docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword` |
scripts/check-jenkins.sh:9:: "${JENKINS_TOKEN:?JENKINS_TOKEN is not set (see docs/tooling/jenkins-configuration.md)}"
```

**Pourquoi des alertes ?** Le motif cherche « `password` ou `token` suivi de `:` ou `=` puis d'une
valeur ».
- Dans `README.en.md`, après `initial password:` vient la **commande** qui lit le mot de passe, pas le
  mot de passe. (Le README français dit « mot de passe initial », que le motif anglais ne voit pas.)
- Dans le script, `:?` est la syntaxe bash qui **exige** la variable : c'est son **nom**, pas sa valeur.

`grep` trouve des mots ; c'est toi qui juges.

**✅ Vérifie que :**
- [ ] Les seules lignes signalées sont celles de `README.en.md` et `scripts/check-jenkins.sh` ci-dessus
- [ ] Toute autre ligne signalée est lue une à une : une **valeur** de secret doit être supprimée

---

### Étape 5 : Regarde l'état du dépôt

**Où :** la racine du projet

**Action :**

> Exécute :
>
> ```bash
> git status --porcelain
> ```

**Résultat attendu :** une nouvelle ligne `?? scripts/` ; `docs/` était déjà non suivi. Aucun `.env`,
aucun fichier de notes.

**✅ Vérifie que :**
- [ ] `?? scripts/` apparaît
- [ ] `notes-story-002.txt` n'apparaît **pas** (s'il existe, il est ignoré)

---

## 🧹 Comment tout annuler

```bash
rm docs/tooling/jenkins-configuration.md
rmdir docs/tooling   # seulement si le dossier est vide
```

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| L'étape 3 affiche un nom de fichier | Le jeton a fui dans ce fichier : supprime-le du fichier, **révoque** le jeton dans Jenkins et génères-en un nouveau (tutoriel 004) |
| L'étape 3 affiche `OK` mais `echo "${#JENKINS_TOKEN}"` vaut `0` | Le jeton n'est pas chargé, donc la recherche n'a rien cherché : recharge-le (tutoriel 004) et relance |
| Le Markdown affiche des `>` au début des lignes | Ils viennent du copier-coller depuis la citation du tutoriel : supprime-les |

---

## 📚 Aide-mémoire des commandes de vérification

| Besoin | Commande |
|--------|----------|
| Chercher un secret sans l'afficher | `grep -rlF "$JENKINS_TOKEN" . --exclude-dir=.git` |
| Vérifier la configuration de Jenkins | `./scripts/check-jenkins.sh` |
| État du dépôt | `git status --porcelain` |

---

## ✅ Avant de passer à la story suivante — bilan de la story 002

### Ce que tu dois avoir fait

- [ ] Étapes 1 et 2 → documentation écrite
- [ ] Étapes 3 et 4 → aucun secret dans les fichiers
- [ ] Étape 5 → état du dépôt vérifié

### Critères de Done de la story 002

| Critère | Preuve |
|---------|--------|
| Jenkins déverrouillé, compte `admin` créé | connexion avec le mot de passe du gestionnaire (tutoriel 002) |
| Crumb CSRF disponible | `OK  CSRF protection` (tutoriel 006) |
| Les 10 plugins installés | 10 lignes `OK  plugin` (tutoriel 006) |
| Le script échoue quand il le doit | code `1` sur faux jeton et sans jeton (tutoriel 006) |
| Configuration documentée | `docs/tooling/jenkins-configuration.md` |

### 🛡️ Garde-fous du TP

- [ ] Aucun mot de passe ni jeton dans un fichier du projet
- [ ] Aucun build ne s'est exécuté sur le controller Jenkins
- [ ] `git status` ne montre pas de fichier inattendu (`.env`, `*.key`)

### Ce que tu dois avoir versionné

Rien dans cette story. En **story 006**, le premier commit inclura notamment :

- `scripts/check-jenkins.sh`
- `docs/tooling/jenkins-configuration.md`

avec un message du type `docs(jenkins): document jenkins unlock and required plugins`.

---

## 🎉 Bravo !

Tu as terminé la story 002 ! Tu as appris à :

- ✨ Documenter une configuration qui n'existe que dans une interface
- ✨ Chercher un secret sans jamais l'afficher
- ✨ Boucler une story avec des preuves, pas des impressions

**Prochaine story :** Story 003 — Configurer SonarQube. Ses tutoriels seront générés avec
`/ tasks-fr 003`.

Pause méritée ☕
