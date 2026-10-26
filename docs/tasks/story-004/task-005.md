# Tutoriel 005 — Story #004 : Publier un témoin, prouver qu'on ne peut pas le republier

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** mot de passe admin Nexus dans Bitwarden (entrée `Nexus local — TP CI/CD`)
**Exigence TP couverte :** `EXG-COM-02` (prépare `EXG-COM-13` et `EXG-COM-17`)
**Fichier principal :** `http://localhost:8081/repository/maven-releases/`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Publier un petit fichier témoin dans `maven-releases`, constater qu'une **seconde** publication de la
même version est **refusée**, puis supprimer le témoin.

## 📋 Ce que tu vas apprendre

- Publier dans Nexus avec un simple `curl -T`
- Prouver l'immuabilité d'une release par un refus
- Créer un fichier temporaire hors du projet avec `mktemp`

---

## 🪜 Les étapes

### Étape 1 : Crée le témoin hors du projet

**Où :** la racine du projet

**Action :**

> ```bash
> WITNESS="$(mktemp --suffix=.pom)"
> printf '<project><modelVersion>4.0.0</modelVersion><groupId>com.example</groupId><artifactId>witness</artifactId><version>1.0.0</version></project>\n' > "$WITNESS"
> echo "$WITNESS"
> ```

**Résultat attendu :** un chemin du type `/tmp/tmp.AbCd1234.pom`.

**Pourquoi `mktemp` ?** Le fichier est créé dans un dossier temporaire, **hors du projet** : il
n'apparaîtra jamais dans `git status`. Pourquoi un `.pom` ? C'est le plus petit fichier qu'un dépôt
Maven accepte sans discuter : un faux `.jar` serait refusé par la validation de type de contenu.

---

### Étape 2 : Publie le témoin

**Où :** la racine du projet

**Action :**

> ```bash
> curl -s -o /dev/null -w "1re publication : %{http_code}\n" -u admin -T "$WITNESS" \
>   http://localhost:8081/repository/maven-releases/com/example/witness/1.0.0/witness-1.0.0.pom
> ```
>
> Quand `curl` affiche `Enter host password for user 'admin':`, colle le mot de passe **admin de
> Nexus** (entrée Bitwarden `Nexus local — TP CI/CD`).

**Résultat attendu :** `1re publication : 201` (`201` = « Créé »).

**✅ Vérifie que :**
- [ ] Le code est `201`

---

### Étape 3 : Lis-le anonymement

**Où :** la racine du projet

**Action :**

> ```bash
> curl -s http://localhost:8081/repository/maven-releases/com/example/witness/1.0.0/witness-1.0.0.pom; echo
> ```

**Résultat attendu :** le contenu XML du témoin, **sans** avoir donné d'identifiants.

**Pourquoi c'est important ?** C'est exactement ce que fera la production : télécharger un artefact
anonymement (décision `5a`).

---

### Étape 4 : Essaie de le republier

**Où :** la racine du projet

**Action :**

> Relance **la même** commande qu'à l'étape 2, en changeant seulement le texte affiché :
>
> ```bash
> curl -s -o /dev/null -w "2e publication : %{http_code}\n" -u admin -T "$WITNESS" \
>   http://localhost:8081/repository/maven-releases/com/example/witness/1.0.0/witness-1.0.0.pom
> ```

**Résultat attendu :** `2e publication : 409` (« Conflit » : la version existe déjà, Nexus refuse de
la remplacer). Code observé sur Nexus 3.96.4 ; une ancienne version de ce tutoriel annonçait `400`.

**Pourquoi c'est la preuve clé de la story ?** La politique *Disable redeploy* lue au tutoriel 002
vient d'agir. Même l'**admin** ne peut pas remplacer la version `1.0.0`. Si ce test répondait `201`,
deux fichiers différents pourraient porter le même numéro de version, et « ce qui part en
production est ce qui a été testé » ne serait plus garanti.

**✅ Vérifie que :**
- [ ] Le code est `409` (surtout **pas** `201`)

---

### Étape 5 : Supprime le témoin

**Où :** `http://localhost:8081` (navigateur, connectée en `admin`)

**Action :**

> **Browse** (cube dans la barre de gauche) → **maven-releases** → déplie `com` → `example` →
> sélectionne le dossier **witness** → **Delete folder** (ou sélectionne la version `1.0.0` →
> **Delete component**). Confirme.

**Résultat attendu :** `com/example/witness` n'apparaît plus.

---

### Étape 6 : Vérifie la suppression et nettoie

**Où :** la racine du projet

**Action :**

> ```bash
> curl -s -o /dev/null -w "après suppression : %{http_code}\n" \
>   http://localhost:8081/repository/maven-releases/com/example/witness/1.0.0/witness-1.0.0.pom
> rm -f "$WITNESS"
> ```

**Résultat attendu :** `après suppression : 404`

**✅ Vérifie que :**
- [ ] Le code est `404`

---

## 🧹 Comment tout annuler

L'étape 5 **est** l'annulation : le témoin est supprimé. Le fichier temporaire est supprimé à
l'étape 6.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `401` à l'étape 2 | Mot de passe admin mal collé : relance |
| `409` dès l'étape 2 | Le témoin existe déjà (essai précédent) : fais l'étape 5, puis recommence à l'étape 2 |
| `400` avec « Invalid content type » dans `docker compose logs nexus` | Le contenu ne ressemble pas à un `.pom` : recrée le fichier avec l'étape 1 |
| `201` à l'étape 4 | La politique n'est pas *Disable redeploy* : tutoriel 002, puis supprime le témoin et recommence |
| `$WITNESS` est vide | Nouveau terminal : refais l'étape 1 |
| Pas de **Delete folder** | Sélectionne la version `1.0.0` ou le fichier `.pom` → **Delete component** / **Delete asset** |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Attendu |
|--------------|---------|
| 1re publication | `201` |
| Lecture anonyme du témoin | contenu XML |
| 2e publication | `409` |
| Après suppression | `404` |

### 🛡️ Garde-fous du TP

- [ ] Aucun mot de passe tapé dans une commande (saisi à l'invite de `curl`)
- [ ] `git status` ne montre aucun fichier témoin

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Publier dans Nexus avec `curl`
- ✨ Prouver l'immuabilité d'une release
- ✨ Travailler avec un fichier temporaire hors du projet

Prochain tutoriel : **#006** — Écrire `scripts/check-nexus.sh` ⏱️ 20 min

Pause méritée ☕
