# Tutoriel 009 — Story #001 : Tester la persistance et l'idempotence

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 008
**Prérequis :** les trois conteneurs `healthy`
**Exigence TP couverte :** critère de persistance de la story 001 (volumes nommés)
**Fichier principal :** `docker-compose.yml` (sections `volumes:` et `networks:`)

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Prouver que les données des trois services **survivent** à un `down` suivi d'un `up`, et que la
commande de démarrage peut être répétée sans effet de bord. C'est le critère de persistance et le
principe d'**idempotence** (`EXG-COM-13`).

## 📋 Ce que tu vas apprendre

- Ce qui est détruit par `docker compose down`, et ce qui survit
- Relier une preuve « avant » et une preuve « après » pour convaincre
- Ce que recouvre le mot **idempotence**, et pourquoi elle compte en déploiement

---

## 🪜 Les étapes

### Étape 1 : Note l'état « avant »

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose ps --format 'table {{.Service}}\t{{.Status}}\t{{.ID}}'
> ```

**Résultat attendu :** trois conteneurs avec un identifiant court du type `a1b2c3d4e5f6`.

**✅ Vérifie que :**
- [ ] Tu as noté les trois identifiants dans `notes-story-001.txt`, sous une ligne `avant :`
- [ ] Tu as bien noté l'identifiant **du conteneur**, pas celui de l'image

> **Pourquoi noter l'identifiant ?** Parce que c'est **lui** qui va changer. Si l'identifiant est
> identique après le redémarrage, c'est que le conteneur n'a jamais recréé — et notre test de
> persistance n'aurait rien prouvé.

---

### Étape 2 : Note la preuve Nexus « avant »

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose exec -T nexus ls -la /nexus-data/admin.password
> ```

**Résultat attendu :** une ligne du type
`-rw-r--r--    1 nexus    nexus    36 Oct  4 18:02 /nexus-data/admin.password`.

**✅ Vérifie que :**
- [ ] Tu as noté la **date** du fichier dans `notes-story-001.txt`, sous ta ligne `avant :`
- [ ] Tu as utilisé `ls -la` et **pas** `cat` : on lit la date du fichier, jamais son contenu

> **Pourquoi ce fichier ?** Nexus le crée **une seule fois**, au tout premier démarrage, dans son
> volume `/nexus-data`. Si le volume survit au `down`, le nouveau conteneur retrouvera le même
> fichier, avec la même date. Sinon, un Nexus tout neuf en recréera un, daté d'aujourd'hui.
>
> **Pourquoi ne pas se connecter à Nexus ?** La première connexion lance un assistant obligatoire
> (nouveau mot de passe admin, accès anonyme) qui **modifie** la configuration. Cet assistant est
> l'objet de la story 004.

---

### Étape 3 : Arrête et supprime les conteneurs

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose down
> ```

**Résultat attendu :** trois lignes `Container cicd-jenkins  Removed`, puis la confirmation du réseau
supprimé. **Aucun message ne parle des volumes : c'est normal.**

**✅ Vérifie que :**
- [ ] `docker compose ps` n'affiche plus aucun conteneur
- [ ] La commande a pris **quelques secondes**, pas plusieurs minutes

---

### Étape 4 : Confirme que les volumes ont survécu

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker volume ls --filter name=cicd
> ```

**Résultat attendu :** les cinq volumes :
```
cicd_jenkins_home
cicd_nexus_data
cicd_sonarqube_data
cicd_sonarqube_extensions
cicd_sonarqube_logs
```

**✅ Vérifie que :**
- [ ] Les **cinq** volumes sont listés
- [ ] Ils portent tous le préfixe `cicd_` (il vient de la clé `name:` du fichier, étape 2 du
      tutoriel 002)

> **Pourquoi le préfixe `cicd_` est une bonne nouvelle ?** Parce que ce préfixi rend tes volumes
> impossibles à confondre avec ceux d'un autre projet sur la même machine. Un volume `data` nu ne
> serait rattachable à rien.

---

### Étape 5 : Redémarre le stack

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose up -d
> ```

**Résultat attendu :** les trois conteneurs repartent. Compte **2 à 5 minutes** avant que les trois
soient `healthy` (Jenkins et SonarQube ont surtout à relire leurs données).

---

### Étape 6 : Vérifie l'état « après »

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose ps --format 'table {{.Service}}\t{{.Status}}\t{{.ID}}'
> ```

**Résultat attendu :** trois services `healthy`, avec des **identifiants différents** de ceux notés
à l'étape 1.

**✅ Vérifie que :**
- [ ] Les trois services sont `healthy`
- [ ] Les identifiants ont changé (c'est attendu et c'est la preuve que le test a du sens)

> **Pourquoi les identifiants changent-ils ?** `down` a **supprimé** les conteneurs. `up` a donc dû
> en créer de nouveaux. Si rien ne les avait supprimés, les identifiants seraient identiques et on
> n'aurait pas testé grand-chose.

---

### Étape 7 : Compare la preuve Nexus

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Relance la commande de l'étape 2 :
>
> ```bash
> docker compose exec -T nexus ls -la /nexus-data/admin.password
> ```

**✅ Vérifie que :**
- [ ] La date est **identique** à celle notée à l'étape 2
- [ ] Tu as noté cette date sous ta ligne `après :`

> **Pourquoi c'est la preuve décisive ?** Le conteneur est neuf (identifiant différent à l'étape 6),
> mais il a retrouvé exactement le même fichier. La donnée n'était donc pas dans le conteneur : elle
> était dans le volume nommé. C'est exactement ce que le jury demande de vérifier.

---

### Étape 8 : Vérifie l'idempotence

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute **deux fois de suite** :
>
> ```bash
> docker compose up -d
> ```

**Résultat attendu :** chaque fois, la réponse contient `Container cicd-jenkins  Running` — et **pas**
> `Recreated` ni `Recreated`.

**✅ Vérifie que :**
- [ ] Aucune ligne ne contient `Recreated`
- [ ] Les conteneurs n'ont pas redémarré

> **Pourquoi c'est de l'idempotence ?** Lancer deux fois la même commande produit le même état final,
> sans effet de bord. Un déploiement doit être idempotent : si le script échoue en plein milieu et
> qu'on le relance, il doit retrouver le même état, pas empiler des doublons ni tout casser. C'est le
> principe que respectera le script de déploiement idempotent de la story 018.

---

### Étape 9 : Note la conclusion

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Dans `notes-story-001.txt`, ajoute :
>
> ```
> PERSISTANCE : OK  (admin.password : ____ avant / ____ après)
> IDEMPOTENCE  : OK  (up x3 = aucun Recreated)
> ```

**✅ Vérifie que :**
- [ ] Les deux lignes `OK` sont remplies avec des valeurs réelles

---

### Comment annuler

Pour revenir à l'état d'avant ce tutoriel, il faudrait supprimer les volumes, ce qui est
**irréversible** :

```bash
docker compose down -v
```

> ⚠️ **Ne lance pas cette commande** sur ton outillage si tu as déjà configuré Jenkins ou SonarQube.
> Elle supprime les cinq volumes, donc toute la configuration de ces outils.

Pour arrêter simplement les services sans rien détruire :

```bash
docker compose stop
```

---

## ✅ Avant de passer au tutoriel suivant

**Action :**

> Vérifie l'état du dépôt :
>
> ```bash
> git status
> ```

**Fichiers modifiés :**
- [ ] Aucun fichier du dépôt : ce tutoriel ne touche qu'à l'état de Docker
- [ ] `notes-story-001.txt` (fichier **local**, jamais versionné) mis à jour

> ⚠️ **Aucun commit n'est demandé ici.** Le premier commit du projet est fait dans la **story 006**.

**✅ Vérifie que :**
- [ ] Les trois services sont `healthy` et laissés **en marche**
- [ ] Les notes de persistance et d'idempotence sont remplies

---

## 🧹 Comment tout annuler

```bash
# arrêter les services sans rien détruire
docker compose stop

# ATTENTION — irréversible, ne lance pas cette commande :
# docker compose down -v   # supprime les 5 volumes et toute la configuration
```

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `docker compose down` affiche une erreur sur un conteneur arret | Ajoute `-t 10` pour accorder 10 s à l'arrêt : `docker compose down -t 10` |
| La date de `admin.password` a changé après le redémarrage | Le chemin du volume est mal écrit. Reprends l'étape 5 du tutoriel 005 et vérifie que c'est bien `/nexus-data` |
| Jenkins affiche « Welcome back » comme neuf | Ses données sont bien là, mais l'assistant d'installation n'a pas été terminé (c'est normal, story 002). Seuls ses paramètres*dossiers* comptent ici |
| `docker volume ls` ne liste rien avec le filtre `cicd` | Utilise `docker volume ls` sans filtre, ou vérifie que la clé `name: cicd` est bien présente dans ton fichier |
| `up -d` affiche `Recreated` au lieu de `Running` | Le fichier a changé entre les deux appels (par exemple un commentaire modifié). Fais-le une troisième fois : il doit dire `Running` |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel ! Tu as appris à :
- ✨ Prouver une persistance par une **comparaison avant / après** plutôt que par une intuition
- ✨ Comprendre ce que `down` détruit (conteneurs, réseau) et ce qu'il préserve (volumes)
- ✨ Vérifier l'**idempotence** en relançant une commande et en cherchant l'absence d'effet de bord

**Prochain tutoriel :** Tutoriel 010 - Fichier `.gitignore`, section README et contrôle des secrets
