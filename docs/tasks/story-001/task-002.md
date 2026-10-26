# Tutoriel 002 — Story #001 : Créer le squelette du fichier Compose

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 001
**Prérequis :** Docker et Compose fonctionnels (tutoriel 001 validé)
**Exigence TP couverte :** `EXG-COM-01`
**Fichier principal :** `docker-compose.yml`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Créer le fichier `docker-compose.yml` avec sa structure, son réseau et ses volumes nommés — **sans
encore aucun service**, pour valider que Docker lit le fichier avant d'y ajouter quoi que ce soit.

## 📋 Ce que tu vas apprendre

- La structure minimale d'un fichier Docker Compose : `services`, `networks`, `volumes`
- À quoi sert un **volume nommé**, et pourquoi il vaut mieux qu'un chemin hôte
- Ce que fait la clé `name:` en tête de fichier

---

## 🪜 Les étapes

### Étape 1 : Crée le fichier vide

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Dans le dossier du projet, exécute :
>
> ```bash
> touch docker-compose.yml
> ```

**Résultat attendu :** aucun message, le fichier existe.

**✅ Vérifie que :**
- [ ] `ls -la docker-compose.yml` affiche le fichier

> **Pourquoi ?** Un fichier YAML vide est **invalide**. On ne le valide qu'une fois les trois
> sections écrites, sinon Docker affiche une erreur qui ne t'apprend rien.

---

### Étape 2 : Écris l'en-tête et le nom du projet

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Ouvre `docker-compose.yml` et écris **exactement** ce contenu :
>
> ```yaml
> # Outillage CI/CD du TP — Jenkins, SonarQube, Nexus.
> # Story 001 — exigences EXG-COM-01 (une commande) et EXG-COM-02 (les trois outils).
> #
> # Règle absolue : aucun secret en clair dans ce fichier.
> # Tout secret vit dans les credentials Jenkins (story 019).
> # Les environments staging / production ont leurs propres fichiers, sous environments/
> # (stories 015 et 017).
>
> name: cicd
> ```

**✅ Vérifie que :**
- [ ] La clé `name: cicd` est présente en dernière ligne
- [ ] Il n'y a aucune tabulation (YAML n'accepte que des espaces)

> **Pourquoi ?** `name: cicd` fixe le nom du projet Docker Compose. Sans lui, Compose invente un nom
> à partir du nom du dossier. Avec lui, les conteneurs s'appellent `cicd-jenkins`, `cicd-sonarqube`,
> `cicd-nexus` et les volumes `cicd_jenkins_home`… Ce nom est **stable** d'une machine à l'autre,
> ce qui est exactement ce qu'on veut pour un rapport technique.

---

### Étape 3 : Écris la section `services` vide

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> À la suite du fichier, ajoute :
>
> ```yaml
>
> services: {}
> ```

**✅ Vérifie que :**
- [ ] La ligne contient bien `services: {}` — les deux accolades sont **obligatoires**
- [ ] La clé `services:` est alignée **au début de la ligne**, sans espace devant

> **Pourquoi les accolades ?** Une clé YAML sans valeur vaut `null`. Or Compose exige que `services`
> soit une **table de correspondance** : il refuse `null` avec le message
> `services must be a mapping`. `{}` est la table vide explicite, et c'est exactement ce que Docker
> réécrit de toute façon quand tu lances `docker compose config`. Les trois services seront ajoutés
> sous cette clé aux tutoriels 003, 004 et 005.
>
> **À noter :** les clés `volumes:` et `networks:` de l'étape 4 acceptent, elles, les sous-clés vides
> (`jenkins_home:` tout seul). Ce n'est donc pas une règle générale de YAML : c'est une exigence
> propre à Compose sur la clé `services`.

---

### Étape 4 : Écris la section `networks`

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> À la suite du fichier, ajoute :
>
> ```yaml
>
> networks:
>   cicd:
>     driver: bridge
> ```

**✅ Vérifie que :**
- [ ] `networks:` est aligné au début de la ligne
- [ ] Le sous-clé `driver` est indenté de deux espaces, `bridge` de quatre

> **Pourquoi ?** Un réseau dédié évite que Jenkins, SonarQube et Nexus se voient mutuellement sur le
> réseau global de Docker. Dans notre cas c'est de la propreté : Nexus doit pouvoir **joindre** le
> registre Maven et l'agent, mais l'application ne doit pas être capable d'atteindre l'IHM de SonarQube
> sans passer par son propre réseau.

---

### Étape 5 : Écris la section `volumes`

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> À la suite du fichier, ajoute :
>
> ```yaml
>
> volumes:
>   jenkins_home:
>   sonarqube_data:
>   sonarqube_logs:
>   sonarqube_extensions:
>   nexus_data:
> ```

**Clés attendues, pour repérage visuel :** `name`, `services: {}`, `networks`, `cicd`, `driver`,
`bridge`, `volumes`, `jenkins_home`, `sonarqube_data`,
`sonarqube_logs`, `sonarqube_extensions`, `nexus_data`.

**✅ Vérifie que :**
- [ ] Les cinq volumes sont déclarés, avec **rien** après les deux-points

> **Pourquoi ?** Un volume déclaré avec un nom mais sans chemin est un volume **géré par Docker** :
> Docker le crée et le range dans `/var/lib/docker/volumes/`. C'est ce qu'on veut, parce que :
> `docker compose down` supprime les conteneurs mais **conserve** les volumes, donc Jenkins ne perd
> pas sa configuration au redémarrage. Un chemin hôte écrit directement (`./jenkins_home:/var/jenkins_home`)
> ferait la même chose, mais polluerait ton dépôt Git de centaines de fichiers.

---

### Étape 6 : Vérifie que le fichier est valide

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose config --quiet && echo "YAML valide"
> ```

**Résultat attendu :** `YAML valide`, sans autre message.

**✅ Vérifie que :**
- [ ] La commande sort avec le code 0
- [ ] Tu ne vois pas le mot `error`

---

### Étape 7 : Vérifie le nom du projet

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose config | head -5
> ```

**Résultat attendu :** les premières lignes affichent `name: cicd`, puis `services: {}`.

**✅ Vérifie que :**
- [ ] `name: cicd` apparaît bien en tête de la sortie
- [ ] `services: {}` apparaît juste après : c'est Docker qui réécrit ta ligne, tu n'as rien fait de
      travers

> **Pourquoi ?** `docker compose config` affiche le fichier **interprété et normalisé** par Docker :
> c'est ta meilleure vérification. Si une indentation est fausse, tu la vois ici immédiatement, avec
> le numéro de ligne, au lieu de la découvrir au démarrage du stack.

---

### Comment annuler

Pour revenir à l'état initial :

```bash
rm docker-compose.yml
```

Le fichier est créé dans ce tutoriel, rien d'autre n'a été touché.

---

## ✅ Avant de passer au tutoriel suivant

**Action :**

> Vérifie l'état du dépôt :
>
> ```bash
> git status
> ```

**Fichiers créés à ce stade :**
- [ ] `docker-compose.yml` (squelette : `name`, `services: {}` vide, `networks`, `volumes`)

> ⚠️ **Aucun commit n'est demandé ici.** Le premier commit du projet est fait dans la **story 006**.

**✅ Vérifie que :**
- [ ] `docker-compose.yml` apparaît en non suivi dans `git status`
- [ ] Tu n'as exécuté aucun `git add`

---

## 🧹 Comment tout annuler

```bash
# 1. supprimer le squelette du fichier Compose
rm docker-compose.yml
# 2. supprimer les notes du tutoriel 001
rm -f notes-story-001.txt
```

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `error while loading yaml: mapping values are not allowed` | Un `:` suivi d'un espace s'est glissé dans une valeur. Relis la ligne signalée : un commentaire doit être `# texte`, jamais `#: texte` |
| `found character that cannot start any token` | Tu as utilisé une tabulation. Remplace **toutes** les tabulations par des espaces (`sed -i 's/\t/  /g' docker-compose.yml`) |
| `services must be a mapping` | Tu as écrit `services:` seul ou suivi uniquement d'un commentaire. Il faut `services: {}` : les deux accolades. Reprends l'étape 3 |
| `services` doit être la première clé | L'ordre des sections n'a pas d'importance en YAML, mais `services:` ne doit pas être **indenté** |
| `docker compose config` ne montre rien | Le fichier est vide : tu n'as pas enregistré. Reprends l'étape 2 |
| `network cicd declared as external, but could not be found` | Tu as recopié une section `external: true` qui n'existe pas ici. Relis l'étape 4 |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel ! Tu as appris à :
- ✨ Structurer un fichier Docker Compose en trois sections : `services`, `networks`, `volumes`
- ✨ Utiliser des volumes **nommés** pour que les données survivent à `docker compose down`
- ✨ Valider un YAML avec `docker compose config` avant de lancer quoi que ce soit

**Prochain tutoriel :** Tutoriel 003 - Ajouter le service Jenkins
