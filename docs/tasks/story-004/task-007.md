# Tutoriel 007 — Story #004 : Lancer le script, puis le faire échouer exprès

**Temps estimé :** ⏱️ 15 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 006
**Prérequis :** tutoriels 001 à 005 terminés
**Exigence TP couverte :** `EXG-COM-02`
**Fichier principal :** `scripts/check-nexus.sh`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Voir le script au vert, puis le faire échouer deux fois sans rien casser.

---

## 🪜 Les étapes

### Étape 1 : Lance le script

**Où :** la racine du projet

**Action :**

> ```bash
> ./scripts/check-nexus.sh; echo "exit code: $?"
> ```

**Résultat attendu :**

```
OK  Nexus is up
OK  Nexus is writable
OK  anonymous read on maven-releases
OK  anonymous write on maven-releases refused
OK  setup wizard completed
Nexus is compliant
exit code: 0
```

**✅ Vérifie que :**
- [ ] 5 lignes `OK`, code `0`

---

### Étape 2 : Échec 1, Nexus injoignable

**Où :** la racine du projet

**Action :**

> ```bash
> NEXUS_URL=http://localhost:8099 ./scripts/check-nexus.sh; echo "exit code: $?"
> ```

**Résultat attendu :** `KO  Nexus does not answer (HTTP 000)`, puis `exit code: 1`.

**Pourquoi `000` ?** Ce n'est pas un vrai code HTTP : `curl` l'affiche quand **personne** ne répond
sur ce port. C'est le « aucune réponse » du tutoriel 008 de la story 001.

---

### Étape 3 : Échec 2, un dépôt qui n'existe pas

**Où :** la racine du projet

**Action :**

> ```bash
> NEXUS_REPO=maven-releasez ./scripts/check-nexus.sh; echo "exit code: $?"
> ```

**Résultat attendu :**

```
OK  Nexus is up
OK  Nexus is writable
KO  anonymous read on maven-releasez refused (HTTP 404)
KO  anonymous write on maven-releasez not refused (HTTP 404)
OK  setup wizard completed
2 check(s) failed
exit code: 1
```

**Pourquoi le contrôle 4 échoue aussi ?** Il attend **exactement** `401`. Un `404` n'est pas un refus
d'écriture, c'est un dépôt absent. Le script ne confond pas « interdit » et « introuvable ». Ces
sorties ont été vérifiées sur ton poste à la génération du tutoriel.

**✅ Vérifie que :**
- [ ] `2 check(s) failed`, code `1`

---

## 🧹 Comment tout annuler

Rien à annuler : les variables `NEXUS_URL` et `NEXUS_REPO` n'existent que le temps de la commande.

---

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `KO  anonymous read … (HTTP 401)` à l'étape 1 | Accès anonyme non activé : tutoriel 001, étape 5 |
| `KO  setup wizard not completed` | Assistant non terminé : tutoriel 001 |
| `no configuration file provided` | Lance le script depuis la racine du projet |

---

## ✅ Avant de passer au tutoriel suivant — résumé des vérifications

| Vérification | Commande | Attendu |
|--------------|----------|---------|
| Nexus conforme | `./scripts/check-nexus.sh` | `Nexus is compliant`, code `0` |
| Nexus injoignable détecté | `NEXUS_URL=http://localhost:8099 ./scripts/check-nexus.sh` | code `1` |
| Dépôt absent détecté | `NEXUS_REPO=maven-releasez ./scripts/check-nexus.sh` | `2 check(s) failed` |

---

## 🎉 Bravo !

Tu as terminé ce tutoriel. Tu as appris à :

- ✨ Distinguer « aucune réponse » (`000`), « interdit » (`401`) et « introuvable » (`404`)
- ✨ Provoquer des échecs par variables, sans modifier le script

Prochain tutoriel : **#008** — Documenter (en anglais) et faire le bilan ⏱️ 20 min

Pause méritée ☕
