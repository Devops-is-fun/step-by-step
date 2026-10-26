# Tutoriel 008 — Story #001 : Vérifier que les trois interfaces répondent

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐
**Dépend de :** Tutoriel 007
**Prérequis :** les trois conteneurs `healthy`
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `http://localhost:8080`, `http://localhost:9000`, `http://localhost:8081`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Prouver que les trois interfaces web répondent bien en HTTP 200, **depuis ta machine**, et non
seulement depuis l'intérieur des conteneurs. C'est le critère `EXG-COM-02`.

## 📋 Ce que tu vas apprendre

- Vérifier un service HTTP en ligne de commande avec `curl -o /dev/null -w`
- Ouvrir une interface dans un navigateur et repérer ce qui doit s'afficher
- Distinguer « le conteneur tourne » de « l'interface répond »

---

## 🪜 Les étapes

### Étape 1 : Vérifie Jenkins en ligne de commande

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> curl -s -o /dev/null -w "Jenkins    : %{http_code}\n" http://localhost:8080/login
> ```

**Résultat attendu :** `Jenkins    : 200`

**✅ Vérifie que :**
- [ ] Le code affiché est `200`
- [ ] Si tu vois `000`, le service ne répond pas encore : reprends le tutoriel 007

> **Pourquoi `curl` plutôt que le navigateur ?** Un navigateur affiche une page, mais ne te dit pas
> le code HTTP. Or c'est **le code HTTP** qui prouve que le service répond. `-o /dev/null` masque le
> corps de la réponse, et `-w` affiche le code à la place. C'est le réflexe à prendre pour tout
> service qu'on veut surveiller — et c'est exactement ce que fera ton `healthcheck`.

---

### Étape 2 : Vérifie SonarQube

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> curl -s -o /dev/null -w "SonarQube  : %{http_code}\n" http://localhost:9000/api/system/status
> ```

**Résultat attendu :** `SonarQube  : 200`

**✅ Vérifie que :**
- [ ] Le code est `200`
- [ ] Si tu veux voir le contenu, exécute `curl -s http://localhost:9000/api/system/status` : tu dois lire
      `{"status":"UP",...}`

---

### Étape 3 : Vérifie Nexus

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> curl -s -o /dev/null -w "Nexus      : %{http_code}\n" http://localhost:8081/service/rest/v1/status
> ```

**Résultat attendu :** `Nexus      : 200`

**✅ Vérifie que :**
- [ ] Le code est `200`
- [ ] Si tu obtiens `404`, utilise plutôt `http://localhost:8081/service/rest/v1/status` — c'est bien
      cette URL ; un `404` signifie que Nexus est en version antérieure à cette API

---

### Étape 4 : Vérifie les trois d'un coup

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute les trois commandes à la suite, puis compare :
>
> ```bash
> for port in 8080 8081 9000; do
>   printf "%s : %s\n" "$port" "$(curl -s -o /dev/null -w '%{http_code}' http://localhost:$port/ || echo KO)"
> done
> ```
>
> **Résultat attendu :** trois codes qui ne sont ni `000` ni `5xx`. Typiquement :
> `8080 : 403`, `8081 : 200`, `9000 : 200`.

**✅ Vérifie que :**
- [ ] Aucune ligne n'affiche `000` ni `KO`

> **Pourquoi `403` compte aussi ?** `403` veut dire « Interdit » : Jenkins a bien reçu la demande
> et refuse `/` tant que tu n'es pas connecté (alors que `/login` répond `200`). Un refus, une
> redirection (`302`) ou une demande d'authentification (`401`), c'est un service qui fonctionne.
> Les codes à surveiller sont `000` (aucune réponse) et `5xx` (erreur du service).

---

### Étape 5 : Vérifie que les ports ne sont pas exposés sur le réseau

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> ss -ltn | grep -E ":(8080|8081|9000) "
> ```

**Résultat attendu :** trois lignes dont l'adresse commence par `127.0.0.1:8080`, `127.0.0.1:8081`,
`127.0.0.1:9000`.

**✅ Vérifie que :**
- [ ] Aucune ligne ne commence par `0.0.0.0:8080` ni par `*:8080`

> **Pourquoi cette vérification compte pour le jury ?** Parce que c'est la preuve mesurable de la
> règle « Jenkins est un contrôleur d'exécution, seul le tunnel donne l'accès public ». Si le port
> n'écoute que sur `127.0.0.1`, ton Chef de projet sur un autre ordinateur ne peut **pas** y accéder.
> Note cette ligne : tu la réutiliseras telle quelle dans ton rapport.

---

### Étape 6 : Ouvre Jenkins dans le navigateur

**Où :** `http://localhost:8080` (navigateur)

**Action :**

> Ouvre ton navigateur sur `http://localhost:8080` et observe la page affichée.

**Résultat attendu :** la page **« Débloquer Jenkins »** (« Unlock Jenkins » en anglais), avec un
encadré qui indique le **chemin** du fichier contenant le mot de passe administrateur initial.

**✅ Vérifie que :**
- [ ] La page « Débloquer Jenkins » s'affiche
- [ ] Tu ne notes **pas** le mot de passe : il est lu à la volée en story 002, jamais stocké
      (spécification § 8)

> **Pourquoi ne pas terminer l'installation ici ?** L'installation de Jenkins (choix des plugins,
> création du compte administrateur) est l'objet de la **story 002**. On s'arrête ici au critère
> « l'interface répond », et on laisse le stack en place.

---

### Étape 7 : Ouvre SonarQube et Nexus dans le navigateur

**Où :** `http://localhost:9000` puis `http://localhost:8081` (navigateur)

**Action :**

> Ouvre successivement :
>
> - `http://localhost:9000` — tu dois voir un logo SonarQube et une demande de connexion
> - `http://localhost:8081` — tu dois voir `Sign in` et `Welcome to Nexus Repository 3`

**✅ Vérifie que :**
- [ ] SonarQube affiche sa page de connexion
- [ ] Nexus affiche sa page de connexion avec le compte `admin`

> **Pourquoi les identifiants par défaut sont-ils un risque ?** Nexus et SonarQube démarrent avec
> `admin/admin`. C'est un mot de passe **en dur dans le produit**, pas un secret que tu as inventé.
> On ne le considère donc pas comme une fuite — mais on le **change** dès que l'outil est réellement
> utilisé. C'est fait en story 020 pour SonarQube et en story 021 pour Nexus.

---

### Comment annuler

Ce tutoriel n'a rien modifié : il se contente de lire. Pour fermer ton navigateur, ferme simplement les
onglets.

Si tu veux arrêter les services maintenant plutôt qu'à la fin de la story :

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
- [ ] Aucun

> ⚠️ **Aucun commit n'est demandé ici.** Le premier commit du projet est fait dans la **story 006**.

**✅ Vérifie que :**
- [ ] Les trois codes HTTP sont `200`
- [ ] Les trois ports n'écoutent que sur `127.0.0.1`
- [ ] Les trois onglets se sont ouverts sans erreur

---

## 🧹 Comment tout annuler

```bash
# ce tutoriel n'a rien modifié : ferme simplement les onglets du navigateur
# pour arrêter les services :
docker compose stop
```

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `curl: (7) Failed to connect` ou code `000` | Le conteneur correspondant n'est pas `healthy`. `docker compose ps`, puis `docker compose logs <service>` |
| `curl: (52) Empty reply from server` | Le service est en cours d'initialisation. Attends 1 minute et réessaie |
| `500 Internal Server Error` | Service démarré mais base de données non prête. Regarde les logs : le message y est |
| `404` sur l'API Nexus | Utilise `http://localhost:8081/service/rest/v1/status` ; si toujours 404, ton Nexus est plus ancien que prévu |
| La page s'ouvre sur une autre adresse | `localhost` peut résoudre en IPv6 (`::1`). Force `http://127.0.0.1:8080` |
| Jenkins affiche « Jenkins is almost ready » | C'est normal : il finalise son installation. Le service répond déjà, c'est suffisant pour cette story |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel ! Tu as appris à :
- ✨ Prouver qu'un service web répond avec un **code HTTP** plutôt qu'à l'œil
- ✨ Vérifier qu'un port n'écoute que sur `127.0.0.1` — une preuve mesurable de sécurité
- ✨ Faire la différence entre un conteneur qui tourne et une interface réellement disponible

**Prochain tutoriel :** Tutoriel 009 - Tester la persistance et l'idempotence
