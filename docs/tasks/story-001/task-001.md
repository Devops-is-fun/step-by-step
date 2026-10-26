# Tutoriel 001 — Story #001 : Vérifier que la machine est prête

**Temps estimé :** ⏱️ 10 minutes
**Difficulté :** ⭐
**Dépend de :** aucun — c'est le premier tutoriel
**Prérequis :** aucun : c'est le premier tutoriel de la story
**Exigence TP couverte :** `EXG-COM-02` (prérequis machine)
**Fichier principal :** aucun fichier — ce tutoriel lit l'état du poste

> **Répertoire de travail de toute la story :** la racine du projet, c'est-à-dire le dossier qui
> contient déjà `docs/`. Sur ta machine :
> `/home/geraldine_rouy/dev/esgi/m1/ci-cd/ci-cd-project/ci-cd-project`
>
> Toutes les commandes `docker compose` de cette story s'exécutent **dans ce dossier**, car Docker
> Compose cherche un fichier `docker-compose.yml` en remontant les dossiers parents.

---

## 🎯 Objectif

Vérifier que Docker fonctionne, que la mémoire libre suffit pour les trois services, et que les trois
ports dont on a besoin sont libres — **avant** d'écrire la moindre ligne de configuration.

## 📋 Ce que tu vas apprendre

-Lire l'état d'un poste Docker avant de dimensionner un stack (`free`, `ss`, `docker compose version`)
- Comprendre pourquoi `vm.max_map_count` est un prérequis d'Elasticsearch, donc de SonarQube
- Distinguer la RAM **disponible** de la RAM **totale** — c'est la seule qui compte

---

## 🪜 Les étapes

### Étape 1 : Confirme que Docker et Compose répondent

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Ouvre un terminal dans le dossier du projet et exécute :
>
> ```bash
> docker --version
> docker compose version
> ```

**Résultat attendu :** deux lignes qui commencent par `Docker version` et `Docker Compose version`.

> **Pourquoi ?** Depuis Docker Engine 23, la commande est `docker compose` (avec un espace), et
> `docker-compose` (avec un tiret) a disparu. Si la seconde commande échoue, ton Docker est trop
> ancien : il faut installer le plugin Compose v2.

---

### Étape 2 : Mesure la mémoire **disponible**

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> free -h
> ```

**Résultat attendu :** la ligne `Mem:` contient une colonne `available`.

**✅ Vérifie que :**
- [ ] `available` est d'au moins **4 Gio**. Si c'est moins, ferme les autres applications avant de
      continuer : les tutoriels 007 et suivants démarrent trois services Java en même temps.
- [ ] Tu as lu la colonne `available`, **pas** la colonne `total`

> **Pourquoi ?** Une machine peut afficher `total: 14Gi` et `available: 2Gi` : le reste est déjà
> pris par ton navigateur, ton IDE, Docker Desktop. C'est `available` qui décide si SonarQube aura
> sa place. Sur cette machine, la valeur attendue est autour de **5,3 Gio**.

---

### Étape 3 : Vérifie le prérequis Elasticsearch

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> sysctl vm.max_map_count
> ```

**Résultat attendu :** un nombre **supérieur ou égal à 262144**. Sur cette machine, tu dois lire
`vm.max_map_count = 1048576`.

**✅ Vérifie que :**
- [ ] La valeur est ≥ 262144 → on peut continuer
- [ ] Si elle est plus petite, exécute `sudo sysctl -w vm.max_map_count=524288` puis re-teste

> **Pourquoi ?** SonarQube embarque Elasticsearch en interne. Elasticsearch réserve de la mémoire
> sous forme de « mapping regions » ; sous Linux, le noyau bride ce mécanisme tant que
> `vm.max_map_count` est trop bas. Résultat symptomatique : SonarQube affiche `System error` au
> démarrage, sans message clair. C'est le piège n°1 de SonarQube sous Linux, d'où cette vérification
> avant même d'écrire le fichier.

---

### Étape 4 : Vérifie que les trois ports sont libres

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> ss -ltn | grep -E ":(8080|8081|9000) " || echo "les trois ports sont libres"
> ```

**Résultat attendu :** la ligne `les trois ports sont libres`.

**✅ Vérifie que :**
- [ ] Aucune ligne ne commence par `LISTEN` sur 8080, 8081 ou 9000
- [ ] Si un port est occupé, note lequel : tu devras changer le port **hôte** dans
      `docker-compose.yml` (tutoriels 003 à 005), pas le port interne du service

> **Pourquoi ?** `ports: - "8080:8080"` veut dire : « le port 8080 de ma machine renvoie vers le port
> 8080 du conteneur ». Si 8080 est déjà pris sur ta machine, Docker refuse de démarrer avec
> `bind: address already in use`. On choisit donc un port hôte libre, et l'application continue de
> croire qu'elle écoute sur 8080 à l'intérieur.

---

### Étape 5 : Vérifie l'espace disque

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> df -h / | tail -1
> ```

**Résultat attendu :** au moins **10 Go** disponibles.

**✅ Vérifie que :**
- [ ] La colonne `Dispo` indique plus de 10 Go

> **Pourquoi ?** Les trois images pèsent environ 2 Go au total, et les volumes Docker guardent la base
> de SonarQube, l'index d'analyse et les dépôts Nexus. Sur cette machine, tu as environ **429 Go**.

---

### Étape 6 : Note le résultat de toutes les vérifications

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Recopie et complète ce tableau dans un fichier texte `notes-story-001.txt` **à la racine du projet**,
> pour toi seule (il ne sera pas versionné) :
>
> ```
> docker      : OK / KO   -> ______________________
> RAM dispo   : ____ Gio  (minimum 4)
> max_map_cnt : __________ (minimum 262144)
> ports       : 8080 ____ | 8081 ____ | 9000 ____
> disque      : ____ Go
> ```

**✅ Vérifie que :**
- [ ] Aucune ligne ne contient `KO`
- [ ] `notes-story-001.txt` est créé à la racine du projet

---

### Comment annuler

Ce tutoriel ne modifie **rien** : il ne fait que lire l'état du poste. Rien à annuler.

Si tu as dû exécuter `sudo sysctl -w vm.max_map_count=524288`, ce réglage est **temporaire** : il
disparaît au redémarrage de la machine. Ce n'est pas grave ici, car sur cette machine la valeur est
déjà correcte.

---

## ✅ Avant de passer au tutoriel suivant

**Action :**

> Vérifie l'état du dépôt avant d'ajouter des fichiers. Dans le dossier du projet, exécute :
>
> ```bash
> git status
> ```

**Fichiers créés à ce stade :**
- [ ] `notes-story-001.txt` (fichier **local**, il ne doit jamais être versionné)

> ⚠️ **Aucun commit n'est demandé ici.** Le premier commit du projet est fait dans la **story 006**,
> qui installe le `.gitignore` complet. Ne fais surtout pas `git add -A` maintenant : sans
> `.gitignore`, tu ajouterais des fichiers parasites.

**✅ Vérifie que :**
- [ ] `git status` affiche `notes-story-001.txt` en non suivi
- [ ] Tu n'as exécuté aucun `git add`

---

## 🧹 Comment tout annuler

Ce tutoriel ne modifie rien : il ne fait que lire l'état du poste. Si tu as dû exécuter
`sudo sysctl -w vm.max_map_count=524288`, ce réglage est temporaire et disparaît au redémarrage.

```bash
# aucun retour à effectuer ; le seul fichier créé est local :
rm -f notes-story-001.txt
```

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `docker: command not found` | Docker n'est pas installé ou pas dans le `PATH`. Lance Docker Desktop, puis rouvre le terminal |
| `permission denied` sur le socket Docker | Ton utilisateur n'est pas dans le groupe `docker` : `sudo usermod -aG docker $USER`, puis **déconnecte-toi et reconnecte-toi** |
| `sysctl: cannot read` | Tu es sur macOS ou Windows : cette vérification ne s'applique pas, passe à l'étape 4. Sur macOS, Docker Desktop règle déjà la valeur |
| `free` n'existe pas | Sur macOS, utilise `vm_stat`. Sur Linux, c'est installé par défaut |
| Un port est occupé | Note-le et continue : le tutoriel 003 ou 004 te fera changer le port hôte correspondant |
| `available` est sous 4 Gio | Ferme le navigateur et l'IDE, ré-exécute `free -h`. Si ça ne suffit pas, relance Docker Desktop avec moins de conteneurs ouverts |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel ! Tu as appris à :
- ✨ Vérifier un poste Docker **avant** de configurer quoi que ce soit
- ✨Lire `free -h` correctement (`available`, pas `total`)
- ✨ Anticiper le prérequis `vm.max_map_count` d'Elasticsearch, cause n°1 des échecs de SonarQube

Tu mérites une petite pause ☕ avant d'écrire le fichier de configuration.

**Prochain tutoriel :** Tutoriel 002 - Créer le fichier `docker-compose.yml` et son squelette
