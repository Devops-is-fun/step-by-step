# Tutoriel 006 — Story #010 : Brancher le webhook dans GitHub

**Qui :** 🧑‍💻 Tu fais (guidée)
**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 005
**Prérequis :** relais `Connected` ; URL smee et secret dans Bitwarden
**Exigence TP couverte :** `EXG-COM-06`
**Fichier principal :** `https://github.com/<ton-compte>/step-by-step/settings/hooks`

> **Répertoire de travail :** la racine du projet (étape 3).

---

## 🎯 Objectif

Dire à GitHub de **poster** une lettre cachetée dans ta boîte smee à chaque push et chaque PR.

---

## 🪜 Les étapes

### Étape 1 : Ajoute le webhook

**Où :** `https://github.com/<ton-compte>/step-by-step` → **Settings → Webhooks → Add webhook**

**Action :**

> | Champ | Valeur |
> |-------|--------|
> | Payload URL | l'URL smee (Bitwarden) |
> | Content type | `application/json` |
> | Secret | le secret `github-webhook-secret` (Bitwarden) |
> | SSL verification | **Enable SSL verification** |
> | Which events…? | **Let me select individual events** → coche **Pushes** et **Pull requests**, rien d'autre |
> | Active | ☑ |
>
> Clique **Add webhook**.

**Pourquoi `application/json` ?** L'autre format (`x-www-form-urlencoded`) emballe le JSON dans un
formulaire : smee et Jenkins attendent du JSON brut.

**Pourquoi seulement 2 événements ?** Ce sont les seuls dont Jenkins a besoin. Moins d'événements,
c'est moins d'informations qui transitent par smee.io.

---

### Étape 2 : Regarde le *ping* de GitHub

**Où :** la page du webhook → onglet **Recent Deliveries**

**Action :**

> Clique sur la première livraison (un **ping**, envoyé à la création).

**Résultat attendu :** une coche verte ✅ et **Response 200**.

**Attention, ce 200 vient de smee.io**, pas de Jenkins : il dit seulement « lettre déposée dans la
boîte ». La vraie preuve est à l'étape suivante.

---

### Étape 3 : Vérifie que Jenkins a accepté le cachet

**Où :** la racine du projet

**Action :**

> ```bash
> docker logs cicd-webhook-relay 2>&1 | tail -1
> ```

**Résultat attendu :** `POST http://jenkins:8080/github-webhook/?source=smee - 200`

**Pourquoi 200, alors qu'au tutoriel 002 c'était 400 ?** Cette fois, la lettre vient de **GitHub**,
cachetée avec le **même** secret que celui de Jenkins : la signature est valide.

**✅ Vérifie que :**
- [ ] `- 200` (et pas `- 400`)

---

## 🧹 Comment tout annuler

GitHub → **Settings → Webhooks** → le webhook → **Delete webhook**.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `- 400` à l'étape 3 | Les deux secrets diffèrent : GitHub → webhook → **Edit** → recolle le secret depuis Bitwarden → **Update webhook**, puis **Recent Deliveries → Redeliver** |
| Aucune ligne nouvelle à l'étape 3 | Le relais n'est pas connecté au **même** canal : compare l'URL du webhook et celle du relais (`docker inspect cicd-webhook-relay \| grep smee.io`) |
| ❌ dans *Recent Deliveries* | Faute dans la *Payload URL* |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Où | Attendu |
|--------------|----|---------|
| Webhook actif | GitHub → Settings → Webhooks | ✅ sur la dernière livraison |
| Cachet accepté | `docker logs cicd-webhook-relay` | `- 200` |

---

## 🎉 Bravo !

GitHub sait maintenant **prévenir** ton Jenkins. 🎉

Bonne coupure ☕ (≈ 1 h 45).

Prochain tutoriel : **#007** — Écrire et lancer `scripts/check-webhook.sh` ⏱️ 15 min
