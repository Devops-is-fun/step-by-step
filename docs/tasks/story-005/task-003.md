# Tutoriel 003 — Story #005 : Créer la paire de clés

**Temps estimé :** ⏱️ 20 minutes
**Difficulté :** ⭐⭐⭐
**Dépend de :** Tutoriel 002
**Prérequis :** service `jenkins-agent` déclaré ; connectée à Jenkins (`http://localhost:8080`)
**Exigence TP couverte :** `EXG-COM-04`
**Fichier principal :** `http://localhost:8080` et `docker-compose.yml`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).
>
> **⚠️ Fais ce tutoriel d'une traite, dans un seul terminal** : la variable `KEYDIR` disparaît si tu
> fermes le terminal. Si ça arrive, va dans « Si tu es bloqué ».

---

## 🎯 Objectif

Créer une paire de clés SSH : la moitié **privée** va dans une credential Jenkins, la moitié
**publique** dans `docker-compose.yml`. Puis supprimer les fichiers : il ne reste aucune clé privée
sur le disque.

## 📋 Ce que tu vas apprendre

- La différence entre clé publique (serrure) et clé privée (clé)
- Copier un secret sans l'afficher, avec `wl-copy`
- Créer une credential *SSH Username with private key*

---

## 🪜 Les étapes

### Étape 1 : Crée la paire dans un dossier temporaire

**Où :** la racine du projet

**Action :**

> ```bash
> KEYDIR="$(mktemp -d)"
> ssh-keygen -q -t ed25519 -N '' -C docker-agent -f "$KEYDIR/docker-agent"
> ls -l "$KEYDIR"
> ```

**Résultat attendu :** deux fichiers, `docker-agent` (`-rw-------`) et `docker-agent.pub`.

**Pourquoi :**
- `mktemp -d` : un dossier **hors du projet**, lisible par toi seule ; il n'apparaîtra jamais dans
  `git status`.
- `-N ''` : pas de phrase de passe. Jenkins doit pouvoir utiliser la clé seul, sans personne pour la
  taper ; elle est protégée par Jenkins, qui la chiffre.
- `-C docker-agent` : un commentaire pour reconnaître la clé.

**Image :** `docker-agent.pub` est une **serrure** : tu peux la montrer à tout le monde, elle ne sert
qu'à vérifier. `docker-agent` est **la clé** : qui l'a, entre.

---

### Étape 2 : Copie la clé privée sans l'afficher

**Où :** la racine du projet

**Action :**

> ```bash
> wl-copy < "$KEYDIR/docker-agent"
> ```

**Résultat attendu :** rien ne s'affiche. La clé est dans ton presse-papiers, mais **pas** à l'écran
ni dans l'historique du terminal.

---

### Étape 3 : Crée la credential dans Jenkins

**Où :** `http://localhost:8080`

**Action :**

> **Manage Jenkins** → **Credentials** → **System** → **Global credentials (unrestricted)** →
> **Add Credentials**, puis remplis :
>
> | Champ | Valeur |
> |-------|--------|
> | Kind | `SSH Username with private key` |
> | Scope | `Global` |
> | ID | `docker-agent-ssh-key` |
> | Description | `SSH key used by the controller to reach docker-agent` |
> | Username | `jenkins` |
> | Private Key | coche **Enter directly** → **Add** → colle (Ctrl+V) |
> | Passphrase | vide |
>
> Clique **Create**.

**Résultat attendu :** la liste affiche `jenkins (SSH key used by the controller to reach docker-agent)`.

**✅ Vérifie que :**
- [ ] L'ID est exactement `docker-agent-ssh-key`
- [ ] Le Username est `jenkins` (l'utilisateur **dans** l'agent, pas ton admin Jenkins)

---

### Étape 4 : Vide le presse-papiers

**Où :** la racine du projet

**Action :**

> ```bash
> wl-copy --clear
> ```

**Pourquoi :** sinon, ton prochain Ctrl+V collerait la clé privée n'importe où (un chat, un
document…).

---

### Étape 5 : Mets la clé publique dans `docker-compose.yml`

**Où :** la racine du projet, puis `docker-compose.yml`

**Action :**

> Affiche la clé publique (pas de risque, c'est la serrure) :
>
> ```bash
> cat "$KEYDIR/docker-agent.pub"
> ```
>
> Dans `docker-compose.yml`, remplace **seulement** `REMPLACE_MOI_AU_TUTORIEL_003` par la ligne
> affichée, **en gardant les guillemets**.

**Résultat attendu :** une ligne de la forme
`JENKINS_AGENT_SSH_PUBKEY: "ssh-ed25519 AAAAC3Nza… docker-agent"`.

**✅ Vérifie que :**
- [ ] La ligne commence par `"ssh-ed25519 ` et finit par ` docker-agent"`
- [ ] Il n'y a **pas** `PRIVATE KEY` dans `docker-compose.yml` (ce serait la mauvaise moitié !)

---

### Étape 6 : Supprime les fichiers de clés

**Où :** la racine du projet

**Action :**

> ```bash
> rm -r "$KEYDIR"
> ls "$KEYDIR" 2>&1 | head -1
> grep -c 'PRIVATE KEY' docker-compose.yml
> ```

**Résultat attendu :** `ls` répond `Aucun fichier ou dossier de ce nom`, puis `0`.

**Pourquoi :** la clé privée existe maintenant **à un seul endroit** : la credential Jenkins. Si un
jour elle est perdue, on n'essaie pas de la retrouver : on en crée une nouvelle (procédure dans la doc,
tutoriel 010).

---

### Étape 7 : Vérifie le compose

**Où :** la racine du projet

**Action :**

> ```bash
> docker compose config --quiet && echo "config OK"
> ```

**Résultat attendu :** `config OK`

---

## 🧹 Comment tout annuler

1. Supprime la credential : **Manage Jenkins** → **Credentials** → `docker-agent-ssh-key` → **Delete**.
2. Remets `REMPLACE_MOI_AU_TUTORIEL_003` dans `docker-compose.yml`.
3. `rm -r "$KEYDIR"` si le dossier existe encore.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| Terminal fermé en cours de route (`$KEYDIR` vide) | La clé privée n'est peut-être plus récupérable : recommence **tout** à l'étape 1 (nouvelle paire). Supprime d'abord la credential si tu l'avais créée, et l'ancien dossier : `ls -d /tmp/tmp.*` pour le retrouver |
| `wl-copy: command not found` | Ouvre `"$KEYDIR/docker-agent"` dans un éditeur **hors IntelliJ** (`gnome-text-editor "$KEYDIR/docker-agent"`), copie, ferme sans enregistrer |
| Pas de **Kind** `SSH Username with private key` | Le plugin `ssh-credentials` manque : **Manage Jenkins** → **Plugins** → **Installed plugins**, cherche « SSH Credentials » |
| Ctrl+V colle autre chose | Refais l'étape 2 juste avant de coller |
| `config OK` absent après l'étape 5 | Guillemets perdus ou ligne coupée : la clé publique doit tenir sur **une seule** ligne |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Comment vérifier | Attendu |
|--------------|------------------|---------|
| Credential créée | Manage Jenkins → Credentials | `docker-agent-ssh-key`, username `jenkins` |
| Clé publique dans le compose | `grep -c 'ssh-ed25519' docker-compose.yml` | `1` |
| Aucune clé privée dans le projet | `grep -rl 'PRIVATE KEY' . --exclude-dir=.git --exclude-dir=docs` | rien |
| Fichiers temporaires supprimés | `ls "$KEYDIR"` | `Aucun fichier ou dossier` |

### 🛡️ Garde-fous du TP

- [ ] La clé privée n'a jamais été affichée ni écrite dans le projet
- [ ] Presse-papiers vidé

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Distinguer la serrure (publique) de la clé (privée)
- ✨ Copier un secret sans l'afficher
- ✨ Ranger une clé privée **uniquement** dans Jenkins

Prochain tutoriel : **#004** — Démarrer l'agent et vérifier Docker ⏱️ 15 min

Pause méritée ☕
