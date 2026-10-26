# Tutoriel 003 — Story #002 : Installer les quatre plugins complémentaires

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** connecté à Jenkins avec le compte `admin`
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `http://localhost:8080/manage/pluginManager/available`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Installer les 4 plugins de ta liste qui ne font pas partie des plugins suggérés : `sonar`,
`coverage`, `warnings-ng` et `pipeline-utility-steps`.

## 📋 Ce que tu vas apprendre

- Utiliser le gestionnaire de plugins de Jenkins
- Distinguer le **nom affiché** d'un plugin de son **identifiant**
- Redémarrer Jenkins proprement pour charger de nouveaux plugins

---

## 🪜 Les plugins à installer

| Identifiant | Nom affiché dans Jenkins | À quoi il servira |
|-------------|--------------------------|-------------------|
| `sonar` | SonarQube Scanner | analyse et Quality Gate (stories 003 et 013) |
| `coverage` | Coverage | rapport de couverture JaCoCo (story 021) |
| `warnings-ng` | Warnings | résultats de Checkstyle et SpotBugs (story 011) |
| `pipeline-utility-steps` | Pipeline Utility Steps | lire des fichiers (`readJSON`, `readMavenPom`) dans le pipeline |

> **Nom affiché ou identifiant ?** Le nom affiché peut changer d'une version à l'autre ; l'identifiant
> jamais. Pour vérifier l'identifiant d'un plugin dans la liste, survole son nom : le lien pointe vers
> `https://plugins.jenkins.io/<identifiant>`.

---

## 🪜 Les étapes

### Étape 1 : Ouvre la liste des plugins disponibles

**Où :** `http://localhost:8080/manage/pluginManager/available` (navigateur)

**Action :**

> Ouvre cette URL. Par les menus : **Administrer Jenkins** (Manage Jenkins) → **Plugins** → onglet
> **Plugins disponibles** (Available plugins).

**Résultat attendu :** une liste de plugins avec une case à cocher devant chacun, et un champ de
recherche en haut.

**✅ Vérifie que :**
- [ ] La liste des plugins disponibles s'affiche

---

### Étape 2 : Coche `sonar`

**Où :** la page « Plugins disponibles »

**Action :**

> Tape `SonarQube Scanner` dans le champ de recherche, puis coche la ligne **SonarQube Scanner**.

**Résultat attendu :** la case est cochée.

**✅ Vérifie que :**
- [ ] En survolant le nom, le lien se termine par `/sonar`

---

### Étape 3 : Coche `coverage`

**Où :** la page « Plugins disponibles »

**Action :**

> Efface la recherche, tape `Coverage`, puis coche la ligne **Coverage**.

**Résultat attendu :** la case est cochée.

**✅ Vérifie que :**
- [ ] Le lien se termine par `/coverage` (plusieurs plugins parlent de couverture : c'est le bon
      identifiant qui compte)

---

### Étape 4 : Coche `warnings-ng`

**Où :** la page « Plugins disponibles »

**Action :**

> Efface la recherche, tape `Warnings`, puis coche la ligne dont le lien se termine par
> `/warnings-ng`.

**✅ Vérifie que :**
- [ ] Le lien se termine par `/warnings-ng`

---

### Étape 5 : Coche `pipeline-utility-steps`

**Où :** la page « Plugins disponibles »

**Action :**

> Efface la recherche, tape `Pipeline Utility Steps`, puis coche la ligne **Pipeline Utility Steps**.

**✅ Vérifie que :**
- [ ] Le lien se termine par `/pipeline-utility-steps`
- [ ] Le bouton **« Installer »** (« Install ») en haut indique **4** plugins sélectionnés

---

### Étape 6 : Installe

**Où :** la page « Plugins disponibles »

**Action :**

> Clique sur **« Installer »** (« Install »).

**Résultat attendu :** une page de progression ; chaque plugin, et ses dépendances, passe à
**« Succès »** (« Success »). Compte 2 à 5 minutes.

**Pourquoi d'autres plugins apparaissent ?** Ce sont les **dépendances** : un plugin peut avoir
besoin d'autres plugins pour fonctionner. Jenkins les installe automatiquement.

**✅ Vérifie que :**
- [ ] Toutes les lignes affichent « Succès »

---

### Étape 7 : Redémarre Jenkins

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose restart jenkins
> ```
>
> Puis relance `docker compose ps` toutes les 30 secondes, jusqu'à voir `jenkins` en `healthy`
> (1 à 2 minutes).

**Résultat attendu :** `jenkins ... Up ... (healthy)`.

**Pourquoi :** certains plugins ne sont complètement chargés qu'au démarrage de Jenkins.
`restart` relance le même conteneur : le volume, et donc ta configuration, ne bouge pas.

**✅ Vérifie que :**
- [ ] `jenkins` est `healthy`
- [ ] Tu peux te reconnecter avec `admin`

---

### Étape 8 : Vérifie la liste des plugins installés

**Où :** `http://localhost:8080/manage/pluginManager/installed` (navigateur)

**Action :**

> Ouvre cette URL et cherche, un par un, les 10 plugins de ta liste **par leur nom affiché** : la
> recherche de l'interface porte sur les noms, pas sur les identifiants.
>
> | Identifiant | Nom affiché à chercher |
> |-------------|------------------------|
> | `git` | Git plugin |
> | `github-branch-source` | GitHub Branch Source Plugin |
> | `workflow-aggregator` | Pipeline |
> | `pipeline-graph-view` | Pipeline Graph View Plugin |
> | `sonar` | SonarQube Scanner for Jenkins |
> | `credentials-binding` | Credentials Binding Plugin |
> | `junit` | JUnit Plugin |
> | `coverage` | Coverage Plugin |
> | `warnings-ng` | Warnings Plugin |
> | `pipeline-utility-steps` | Pipeline Utility Steps |

**Résultat attendu :** chacun apparaît, activé.

**Pourquoi `workflow-aggregator` s'appelle « Pipeline » ?** Avant 2016, les pipelines Jenkins
s'appelaient « Workflow ». Le nom affiché a changé, l'identifiant non : trop d'installations en
dépendaient. C'est pour ça que le script du tutoriel 005 vérifie les **identifiants**.

**Pourquoi le faire à la main alors qu'un script arrive ?** Pour savoir à quoi ressemble « le bon
résultat » avant de l'automatiser. Au tutoriel 006, le script devra dire la même chose que tes yeux.

**✅ Vérifie que :**
- [ ] Les 10 plugins sont présents

---

## 🧹 Comment tout annuler

Pour désinstaller un plugin : `http://localhost:8080/manage/pluginManager/installed`, bouton de
désinstallation (icône ✕ ou « Désinstaller ») sur la ligne du plugin, puis
`docker compose restart jenkins`. Jenkins refuse si un autre plugin en dépend.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Le bouton « Installer » indique moins de 4 | La sélection n'a pas été gardée entre deux recherches : installe les plugins **un par un** (une recherche, une case, « Installer »), puis redémarre une seule fois à la fin |
| Un plugin introuvable dans « Plugins disponibles » | Il est peut-être déjà installé (comme dépendance) : cherche-le dans « Plugins installés » |
| Une ligne en « Échec » (« Failure ») | Relance l'installation de ce plugin seul ; si ça persiste, `docker compose logs --tail=50 jenkins` |
| `jenkins` reste `starting` plus de 3 minutes | `docker compose logs --tail=50 jenkins` : un plugin incompatible est signalé dans les logs |

---

## 📚 Aide-mémoire des commandes de vérification

| Besoin | Commande |
|--------|----------|
| Redémarrer Jenkins sans perdre de données | `docker compose restart jenkins` |
| Voir l'état des conteneurs | `docker compose ps` |
| Lire les logs de Jenkins | `docker compose logs --tail=50 jenkins` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

### Ce que tu dois avoir fait

- [ ] Étapes 1 à 5 → coché `sonar`, `coverage`, `warnings-ng`, `pipeline-utility-steps`
- [ ] Étape 6 → installé
- [ ] Étape 7 → redémarré Jenkins
- [ ] Étape 8 → vérifié les 10 plugins

### Ce que tu dois avoir vérifié

| Vérification | Comment vérifier | Attendu |
|--------------|------------------|---------|
| Jenkins redémarré | `docker compose ps` | `jenkins ... (healthy)` |
| Plugins présents | « Plugins installés » | les 10 identifiants |

### 🛡️ Garde-fous du TP

- [ ] Aucun secret n'a été écrit dans un fichier
- [ ] Aucun build ne s'est exécuté sur le controller Jenkins
- [ ] `git status` ne montre pas de fichier inattendu

### Ce que tu dois avoir versionné

Rien : le premier commit du projet est fait en **story 006**.

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Installer des plugins avec le gestionnaire de Jenkins
- ✨ Reconnaître un plugin par son identifiant, pas par son nom
- ✨ Redémarrer Jenkins sans rien perdre

Prochain tutoriel : **#004** — Créer un jeton d'API et le charger dans le terminal ⏱️ 15 min

Pause méritée ☕
