# Tutoriel 002 — Story #010 : Créer le secret du webhook et le confier à Jenkins

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 001
**Prérequis :** relais `Connected` ; `SMEE_URL` chargé dans ton terminal (sinon : `read -rs SMEE_URL && export SMEE_URL`)
**Exigence TP couverte :** `EXG-COM-06`
**Fichier principal :** `http://localhost:8080/manage/credentials/`

> **Répertoire de travail :** la racine du projet.

---

## 🎯 Objectif

Créer le **cachet** que GitHub apposera sur chaque webhook, et apprendre à Jenkins à **jeter** les
lettres sans cachet.

## 📋 Ce que tu vas apprendre

- Ce qu'est une **signature HMAC** de webhook
- Générer un secret **sans jamais le voir écrit en clair** ailleurs que dans Bitwarden et Jenkins

---

## 🪜 Les étapes

### Étape 1 : Génère le secret dans Bitwarden

**Où :** Bitwarden → **Générateur** (*Generator*)

**Action :**

> Règle le générateur : **Mot de passe**, longueur **40**, majuscules + minuscules + chiffres,
> **sans** caractères spéciaux. Puis crée une entrée **Login** :
>
> | Champ | Valeur |
> |-------|--------|
> | Nom | `Jenkins — github-webhook-secret` |
> | Mot de passe | le secret généré |
>
> **Enregistre.**

**Pourquoi sans caractères spéciaux ?** Le secret sera collé dans un terminal (tutoriel 007) : un `!`
ou un `$` y serait interprété. 40 lettres et chiffres restent très solides.

**Comment marche le cachet ?** GitHub calcule une **empreinte** du message avec ce secret (HMAC
SHA-256) et l'envoie dans l'en-tête `X-Hub-Signature-256`. Jenkins refait le calcul avec **son**
exemplaire du secret : si les empreintes diffèrent, le message a été modifié ou forgé, et il est
refusé. Le secret, lui, ne voyage **jamais**.

---

### Étape 2 : Crée la credential dans Jenkins

**Où :** `http://localhost:8080` → **Manage Jenkins → Credentials → System → Global credentials
(unrestricted)**

**Action :**

> Clique **Add Credentials**, puis remplis :
>
> | Champ | Valeur |
> |-------|--------|
> | Kind | `Secret text` |
> | Scope | `Global` |
> | Secret | colle le secret depuis Bitwarden |
> | ID | `github-webhook-secret` |
> | Description | `Shared secret of the GitHub webhook (story 010)` |
>
> Clique **Create**.

**Résultat attendu :** la ligne `github-webhook-secret` apparaît dans la liste, le secret masqué.

---

### Étape 3 : Dis à Jenkins de vérifier les cachets

**Où :** `http://localhost:8080` → **Manage Jenkins → System**, section **GitHub**

**Action :**

> 1. Dans la section **GitHub**, clique le bouton **Advanced…** (sous *GitHub Servers*).
> 2. Dans **Shared secrets**, clique **Add**.
> 3. Choisis la credential **`Shared secret of the GitHub webhook (story 010)`**.
> 4. Clique **Save** en bas de la page.

**Si tu ne trouves pas « Shared secrets »** : `Ctrl+F` → `Shared secret` dans la page.

---

### Étape 4 : Prouve que les lettres sans cachet sont jetées

**Où :** la racine du projet, le terminal où `SMEE_URL` est chargé

**Action :**

> ```bash
> curl -fsS -o /dev/null -X POST "$SMEE_URL" -H 'Content-Type: application/json' -H 'X-GitHub-Event: ping' --data-binary '{"zen":"no seal","hook_id":0}'
> sleep 3; docker logs cicd-webhook-relay 2>&1 | tail -1
> ```

**Résultat attendu :** `POST http://jenkins:8080/github-webhook/?source=smee - 400`

**Pourquoi c'est la bonne réponse ?** Au tutoriel 001, le même message était accepté (`200`).
Maintenant, il n'a pas de signature : Jenkins le **refuse**. Quelqu'un qui découvrirait ton URL smee
ne pourrait donc **pas** déclencher de build.

**✅ Vérifie que :**
- [ ] `- 400`

---

## 🧹 Comment tout annuler

1. **Manage Jenkins → System → GitHub → Advanced… → Shared secrets** : supprime la ligne, **Save**.
2. **Manage Jenkins → Credentials** : supprime `github-webhook-secret`.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Toujours `- 200` à l'étape 4 | Le **Save** de l'étape 3 n'a pas été cliqué, ou la credential n'a pas été choisie |
| La liste des credentials est vide à l'étape 3 | La credential est en *Scope* `System` : recrée-la en `Global` |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Où | Attendu |
|--------------|----|---------|
| Secret rangé | Bitwarden | entrée `Jenkins — github-webhook-secret` |
| Credential | Jenkins → Credentials | `github-webhook-secret` |
| Lettre sans cachet | étape 4 | `- 400` |

### 🛡️ Garde-fous du TP

- [ ] Le secret n'a été collé **que** dans Jenkins (et le sera dans GitHub au tutoriel 006)

---

## 🎉 Bravo !

Jenkins sait reconnaître un **vrai** courrier de GitHub. 🎉

Prochain tutoriel : **#003** — Créer le jeton GitHub et la credential `github-token` ⏱️ 20 min
