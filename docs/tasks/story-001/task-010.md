# Tutoriel 010 — Story #001 : Fichier `.gitignore`, section README et contrôle des secrets

**Temps estimé :** ⏱️ 25 minutes
**Difficulté :** ⭐⭐
**Dépend de :** Tutoriel 009
**Prérequis :** les trois conteneurs `healthy`, preuve de persistance notée
**Exigence TP couverte :** critère « aucun secret en clair » de la story 001
**Fichier principal :** `.gitignore` et `README.md`

> **Répertoire de travail :** la racine du projet (le dossier qui contient `docs/`).

---

## 🎯 Objectif

Créer le `.gitignore` qui protège le dépôt des fichiers locaux, rédiger la première section du
`README.md`, puis vérifier qu'aucun secret n'est présent dans les fichiers à versionner. C'est le
dernier critère de la story : **« Aucun secret en clair dans le dépôt »**.

## 📋 Ce que tu vas apprendre

- Ce que fait un `.gitignore`, et pourquoi il doit exister **avant** le premier `git add`
- Contrôler automatiquement la présence d'un secret, au lieu de compter sur sa mémoire
- Pourquoi un secret ne doit pas être « dans un fichier caché » mais **pas du tout dans le dépôt**

---

## 🪜 Les étapes

### Étape 1 : Liste ce qui ne doit jamais être versionné

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Avant d'écrire quoi que ce soit, liste dans `notes-story-001.txt` les fichiers de ta machine qui
> n'ont rien à faire dans un dépôt Git :
>
> ```
> 1. .idea/          -> configuration d'IDE personnelle
> 2. *.iml           -> même origine
> 3. notes-story-001.txt -> mes notes de travail
> 4. .env            -> variables d'environnement (staging/prod, story 015+)
> 5. volumes/        -> si un jour on utilise des bind mounts
> 6. target/         -> artefacts de compilation Maven
> ```

**✅ Vérifie que :**
- [ ] Ta liste contient au moins ces six entrées
- [ ] Tu as compris que `.idea/` est **déjà** dans ton dépôt (tu l'avais ajouté avant de comprendre
      pourquoi il ne fallait pas) : le `.gitignore` ne suffira pas, il faudra aussi retirer les
      fichiers déjà suivis, ce qui est fait en story 006

---

### Étape 2 : Crée le fichier `.gitignore`

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Crée le fichier `.gitignore` **à la racine du projet**, et écris ce contenu :
>
> ```gitignore
> # ---- Outillage Docker (story 001) ----------------------------------------
> # Aucun secret ne doit être versionné : les identifiants vivent dans les
> # credentials Jenkins (story 019), jamais dans un fichier du dépôt.
> .env
> .env.*
> !.env.example
> volumes/
> jenkins_home/
> sonarqube_data/
> sonarqube_logs/
> sonarqube_extensions/
> nexus_data/
>
> # ---- IDE et fichiers personnels ------------------------------------------
> .idea/
> *.iml
> *.iws
> .vscode/
> .classpath
> .project
> .settings/
>
> # ---- Build Java -----------------------------------------------------------
> target/
> *.class
> *.jar
> !*.jar.original
>
> # ---- Système et éditeurs -------------------------------------------------
> .DS_Store
> Thumbs.db
> *~
> *.swp
>
> # ---- Fichiers de travail locaux ------------------------------------------
> notes-story-*.txt
> *.log
> ```

**Clés attendues :** `.env`, `.env.*`, `!.env.example`, `volumes/`, `jenkins_home/`,
`sonarqube_data/`, `sonarqube_logs/`, `sonarqube_extensions/`, `nexus_data/`, `.idea/`, `*.iml`,
`target/`, `notes-story-*.txt`.

**✅ Vérifie que :**
- [ ] Le fichier est à la **racine** du projet, pas dans `docs/`
> [ ] Chaque ligne est soit un chemin, soit un motif, soit un commentaire commençant par `#`
- [ ] Le fichier se termine par un saut de ligne

> **Pourquoi `.env` ET `.env.*` avec une exception `!.env.example` ?** C'est un couple à
> comprendre : `.env.*` exclut **tous** les fichiers de variables, y compris
> `.env.staging` et `.env.production`. La ligne `!.env.example` annule ensuite l'exclusion pour le
> seul fichier de **modèle**, qui ne contient aucun secret et doit être versionné. L'ordre compte :
> l'exception doit venir **après** l'exclusion, sinon elle est ignorée.

> **Pourquoi `*.jar` est-il exclu ?** Parce que les artefacts compilés ne doivent jamais être
> versionnés : ils se régénèrent avec `mvn package`. Le `!*.jar.original` est une précaution pour
> les archives de déploiement, qui sont générées par la commande `package` de Maven et ne sont pas
> reproductibles autrement. Nous n'en avons pas besoin ici, mais cela ne coûte rien.

---

### Étape 3 : Vérifie que `.gitignore` fonctionne

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> git status --porcelain
> ```

**Résultat attendu :** `notes-story-001.txt` **disparaît** de la liste (il était non suivi, `??`).
`.idea/` et le fichier `.iml` **restent** affichés avec `A`, car ils sont déjà indexés.

**✅ Vérifie que :**
- [ ] `notes-story-001.txt` n'apparaît plus
- [ ] `.idea/` apparaît toujours, avec `A` : c'est normal
- [ ] `docker-compose.yml` apparaît toujours

> **Si `.gitignore` apparaît avec `A` ou `AM` :** IntelliJ l'a ajouté à l'index en proposant
> « Add file to Git? » à la création. C'est sans gravité. D'ici la story 006, réponds **Cancel** à
> cette question (c'est probablement ainsi que `.idea/` a été indexé).

> **Pourquoi ça ne suffit pas ?** Un `.gitignore` n'agit que sur les fichiers **non encore suivis**.
> `.idea/` étant déjà dans l'index Git, il reste suivi malgré le `.gitignore`. Il faudra l'exclure
> explicitement avec `git rm --cached` — c'est précisément l'objet de la **story 006**. On le note,
> on ne le fait pas maintenant.

---

### Étape 4 : Vérifie que `notes-story-001.txt` est bien ignoré

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> git check-ignore -v notes-story-001.txt
> ```

**Résultat attendu :**
```
.gitignore:39:notes-story-*.txt    notes-story-001.txt
```

**✅ Vérifie que :**
- [ ] La commande affiche le motif qui a ignoré le fichier
- [ ] Le numéro de ligne correspond à la ligne `notes-story-*.txt` de ton fichier

> **Pourquoi `git check-ignore` ?** Parce que `.gitignore` est cumulatif et parfois contre-intuitif.
> Cette commande te dit **quel motif** a ignoré le fichier et **à quelle ligne**. C'est le moyen
> fiable de comprendre pourquoi un fichier n'apparaît pas dans `git status`.

---

### Étape 5 : Contrôle qu'aucun secret n'est dans le dépôt

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute les trois commandes :
>
> ```bash
> grep -rniE '(password|passwd|secret|token|api[_-]?key)[[:space:]]*[:=][[:space:]]*[^<{$[:space:]]' \
>   docker-compose.yml README.md 2>/dev/null || echo "OK : aucun secret detecte"
> ```
>
> ```bash
> grep -rn "initialAdminPassword" . --exclude-dir=.git --exclude-dir=.opencode || echo "OK : aucun mot de passe Jenkins"
> ```
>
> ```bash
> docker compose config | grep -iE "password|secret|token" || echo "OK : aucune variable suspecte dans la config"
> ```
>
> **Résultat attendu :** `OK : ...` sur les trois commandes.

**✅ Vérifie que :**
- [ ] Aucun mot de passe n'apparaît dans `docker-compose.yml`
- [ ] La seule occurrence possible du mot `initialAdminPassword` est **dans cette documentation**, pas
      dans un fichier de configuration
> [ ] `docker compose config` ne révèle aucune variable `PASSWORD` ou `SECRET`

> **Pourquoi faire ce contrôle à la main ?** Parce qu'un secret versionné est **irrécupérable** : même
> en supprimant le fichier, il reste dans l'historique Git. Un secret versionné est un secret
> compromis.
> Le moment où on peut encore éviter le problème, c'est maintenant, avant le premier commit.
>
> **À propos de `admin/admin` sur SonarQube et Nexus :** ce n'est pas une fuite. Ces identifiants
> sont écrits **en dur dans le produit** et sont publics. La règle concerne les secrets que **nous**
> créons. On les changera en story 020 et 021.

---

### Étape 6 : Rédige la première section du README

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Crée `README.md` **à la racine du projet** (il n'existe pas encore), avec ce contenu :
>
> ```markdown
> # Step by Step
>
> Projet de démonstration d'une chaîne CI/CD complète : Jenkins, SonarQube et Nexus,
> déclenchés par un webhook GitHub.
>
> ## Prérequis
>
> - Docker 29.x avec Docker Compose v2 (`docker compose`)
> - 8 Go de RAM disponibles pour Docker
> - Java 17 et Maven 3.9 (stories 008 et 011)
>
> ## Démarrage de l'outillage
>
> Les trois outils de la chaîne CI/CD se démarrent avec **une seule commande** :
>
> \`\`\`bash
> docker compose up -d
> \`\`\`
>
> | Outil      | URL                     | Accès        |
> |------------|-------------------------|--------------|
> | Jenkins    | http://localhost:8080   | mot de passe initial : `docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword` |
> | SonarQube  | http://localhost:9000   | admin / admin (à changer) |
> | Nexus      | http://localhost:8081   | admin / mot de passe initial : `docker compose exec nexus cat /nexus-data/admin.password` |
>
> Les ports n'écoutent que sur `127.0.0.1` : ils ne sont pas accessibles depuis une autre machine.
>
> ### Commandes utiles
>
> \`\`\`bash
> docker compose ps                             # état et santé des services
> docker compose logs -f sonarqube             # suivre les journaux
> docker compose stop                          # arrêter, conserver les données
> docker compose down                          # arrêter et supprimer les conteneurs
> docker compose down -v                       # tout supprimer, DONNÉES INCLUES
> \`\`\`
>
> Ne lancez jamais \`down -v\` sans vérifier deux fois : les volumes contiennent la configuration de
> Jenkins, les analyses SonarQube et les dépôts Nexus.
>
> ## Suite du projet
>
> Ce README sera complété au fil des stories : configuration Jenkins (002), SonarQube (003) et Nexus (004),
> Quality Gate bloquante (013), publication de l'artefact (014), déploiement staging (015).
> ```
>
> **Clés attendues :** `# Step by Step`, `## Prérequis`, `## Démarrage de l'outillage`,
> `### Commandes utiles`, `## Suite du projet`.

**✅ Vérifie que :**
- [ ] Le fichier s'appelle `README.md` et se trouve à la racine
- [ ] Les URLs sont correctes : 8080, 9000, 8081
- [ ] Aucun mot de passe réel n'apparaît : uniquement des **commandes** et `admin/admin` par défaut

> **Pourquoi le README commence-t-il par l'outillage ?** Parce que c'est la seule partie du projet
> qu'un correcteur peut reproduire **sans Java, sans PostgreSQL, sans compte GitHub**. C'est ta
> démonstration de bout en bout la plus rapide à faire fonctionner.

---

### Étape 7 : Vérifie le dépôt une dernière fois

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> git status --porcelain
> ```
>
> Puis :
>
> ```bash
> ls -la
> ```

**Résultat attendu :** `docker-compose.yml`, `README.md`, `.gitignore`, `docs/`. `.idea/` apparaît
toujours avec `A` (déjà indexé, retiré en story 006).

**✅ Vérifie que :**
- [ ] `notes-story-001.txt` n'apparaît **pas** dans `git status`
- [ ] `.idea/` apparaît encore avec `A` : normal, il est déjà indexé
- [ ] `docker-compose.yml`, `README.md`, `.gitignore` apparaissent

---

### Étape 8 : Vérifie que le stack est toujours en marche

**Où :** la racine du projet (le dossier qui contient `docs/`)

**Action :**

> Exécute :
>
> ```bash
> docker compose ps --format 'table {{.Service}}\t{{.Status}}'
> ```

**Résultat attendu :** les trois services `healthy`.

**✅ Vérifie que :**
- [ ] Jenkins, SonarQube et Nexus tournent toujours
- [ ] Tu peux laisser le stack tourner pour les stories suivantes

---

### Comment annuler

Pour supprimer les fichiers créés dans ce tutoriel :

```bash
rm .gitignore README.md
```

> ⚠️ Attention : `.gitignore` ne sera pas nécessaire tout de suite. Si tu le supprimes, tu dois
> surtout **ne pas** faire `git add -A` d'ici la story 006, sinon tu rejoueras le problème qu'on
> vient de résoudre.

Pour arrêter le stack sans rien détruire : `docker compose stop`.

---

## ✅ Avant de passer au tutoriel suivant

**Action :**

> Fais le point final sur la story :
>
> ```bash
> git status
> docker compose ps
> ```
>
> **Critères de Done de la story 001 :**
> - [ ] ✅ Les 3 services démarrent avec une seule commande (`docker compose up -d`)
> - [ ] ✅ Les 3 interfaces répondent 200 (tutoriel 008)
> - [ ] ✅ Les données survivent à un `down` / `up` (tutoriel 009)
> - [ ] ✅ Aucun secret en clair dans le dépôt (étape 5)

**✅ Vérifie que :**
- [ ] Les quatre cases sont cochées
- [ ] `notes-story-001.txt` est toujours ignoré par Git
- [ ] Le stack est laissé en marche pour la story 002

---

## 🧹 Comment tout annuler

```bash
# 1. arrêter le stack
docker compose stop
# 2. supprimer les fichiers créés par ce tutoriel
rm .gitignore README.md
#    ne pas faire "git add -A" entre-temps
# 3. supprimer les notes locales
rm -f notes-story-001.txt
```

## 🤔 Si tu es bloqué

| Problème | Solution |
|----------|----------|
| `.idea/` apparaît toujours dans `git status` | Normal : il est **déjà suivi**. Le retirer se fait en story 006 avec `git rm -r --cached .idea` |
| `notes-story-001.txt` apparaît encore | Vérifie que `.gitignore` est **à la racine** du projet et contient `notes-story-*.txt`. Teste avec `git check-ignore -v notes-story-001.txt` |
| La commande `grep` de l'étape 5 affiche des correspondances | Lis-les une à une. Si c'est un mot cité dans un commentaire d'explication (comme ici), ce n'est pas un secret. Si c'est une vraie valeur, supprime-la |
| `grep` affiche `Permission denied` sur un dossier | Ajoute `--exclude-dir` pour `.git` et `.opencode` |
| Le README s'affiche avec des `` `\`\`\` `` littéraux | Les accents graves insérés pour l'échappement n'ont pas été retirés. Remplace-les par de vrais accents graves |
| `docker compose ps` affiche des services `unhealthy` | Reprends le tableau des blocages du tutoriel 007, étape 6 |

---

## 🎉 Bravo !

Tu as terminé la story 001 et ses 10 tutoriels ! Tu as appris à :
- ✨ Écrire un `.gitignore` **avant** le premier `git add`, et savoir qu'il ne suffit pas pour les
      fichiers déjà suivis
- ✨ Contrôler la présence de secrets par des commandes, au lieu de compter sur ta mémoire
- ✨ Écrire un `README` qui rend le projet reproductible par quelqu'un qui te connaît pas

## 🏁 Récapitulatif de la story

Tu as maintenant :
- un `docker-compose.yml` avec **trois** services, versions épinglées et mémoire plafonnée
- un stack qui démarre en **une** commande et reste sain
- des volumes nommés qui prouvent la persistance
- des ports limités à `127.0.0.1`
- un `.gitignore` et un `README.md`

**Prochaine story :** Story 002 - Configurer Jenkins (assistants d'installation, plugins,
credentials). Les tutoriels seront générés quand tu commenceras cette story.
