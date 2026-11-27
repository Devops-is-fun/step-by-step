
# Step by Step — Frontend

## 1. Présentation

Step by Step est une application de gestion de tâches destinée
notamment aux personnes atteintes de TDAH.

Elle permet de décomposer les tâches en petites étapes afin
de faciliter leur organisation et leur réalisation.

Le frontend communique avec le backend Spring Boot à travers
une API REST et affiche les données stockées dans PostgreSQL.

## 2. Technologies utilisées

- Vue.js 3
- JavaScript
- Vite
- Axios
- ESLint et Oxlint
- Vitest 4.1.12
- Testing Library
- Jest DOM 6.6.3
- Docker
- Nginx Unprivileged

## 3. Fonctionnalités

- Affichage des tâches et de leurs étapes.
- Création de tâches avec une durée estimée.
- Planification d'une tâche lors de sa création.
- Ajout d'étapes à une tâche.
- Affichage des statistiques.
- Affichage de la progression globale et individuelle.
- Affichage des messages d'erreur retournés par le backend.

### Règles métier

- REG-01 : la durée estimée d'une étape doit être comprise
  entre 1 et 30 minutes.
- REG-02 : on ne peut planifier qu'une seule tâche non
  terminée par jour.

La validation des règles métier est assurée par le backend.
Le frontend affiche les messages d'erreur de l'API.

### Limites actuelles

La modification du statut d'une tâche et la validation
des étapes ne sont pas encore disponibles dans le frontend.

Les statistiques et les barres de progression sont calculées
à partir des données retournées par l'API.

## 4. Architecture

```text
Navigateur
    |
    v
Frontend (Vue.js / Nginx)
    |
    | API REST / JSON
    v
Backend (Spring Boot)
    |
    v
Base de données (PostgreSQL)
```

Les requêtes HTTP sont centralisées dans le fichier `src/api.js`.

Le frontend utilise des URL relatives (`/api/...`) afin de
faciliter la portabilité entre les environnements.

## 5. Structure du projet

```text
front/
├── src/
│   ├── assets/
│   ├── __tests__/
│   │   └── App.spec.js
│   ├── App.vue
│   ├── api.js
│   └── main.js
├── Dockerfile
├── .dockerignore
├── default.conf.template
├── package.json
├── package-lock.json
├── vite.config.js
└── README.md
```

## 6. Développement local

### Prérequis

- Node.js compatible avec Vite
- npm
- Docker
- Backend Spring Boot accessible sur le port 8085

Depuis le dossier `front/` :

```bash
npm ci
npm run dev
```

Le frontend est accessible à l'adresse :

http://localhost:5173

Le proxy Vite redirige les requêtes `/api` vers :

http://localhost:8085

Cette configuration permet de communiquer avec le backend
sans utiliser d'adresse absolue dans le code Vue.js.

## 7. Qualité du code et tests automatisés

### Vérification du code

```bash
npm run lint
```

ESLint et Oxlint permettent de vérifier la qualité du code.

### Exécution des tests

```bash
npm test
```

Les tests frontend sont réalisés avec Vitest et Testing Library.

Les scénarios de test prévus sont :

1. Affichage des tâches et de leurs étapes.
2. Vérification des données lors de la création d'une tâche.
3. Affichage des erreurs retournées par le backend.
4. Ajout d'une étape à une tâche existante.

Les appels à l'API sont simulés avec des mocks afin de
permettre l'exécution des tests indépendamment du backend
et de PostgreSQL.

### Construction pour la production

```bash
npm run build
```

Les fichiers statiques sont générés dans le dossier `dist/`.

## 8. Conteneurisation

### Construction de l'image Docker

Depuis la racine du dépôt :

```bash
docker build -t front-test front/
```

Le Dockerfile utilise une construction multi-stage.

#### Stage 1 : Build

- Installation des dépendances avec `npm ci`.
- Construction du frontend avec `npm run build`.
- Génération des fichiers statiques dans `dist/`.

#### Stage 2 : Runtime

- Utilisation de Nginx Unprivileged.
- Copie des fichiers statiques.
- Exécution sans privilèges root.
- Écoute sur le port 8080.
- Reverse proxy vers le backend.

Cette approche permet de réduire la taille de l'image finale
et de séparer l'environnement de construction de
l'environnement d'exécution.

## 9. Exécution avec Docker

Le backend (`app`) et PostgreSQL (`db`) doivent être
disponibles sur le réseau Docker `sbs`.

```bash
docker run --rm --name front --network sbs -p 8086:8080 -e API_UPSTREAM=http://app:8080 front-test
```

Le frontend conteneurisé est accessible à l'adresse :

http://localhost:8086

### Configuration de l'environnement

La variable `API_UPSTREAM` permet de configurer l'adresse
du backend sans reconstruire l'image Docker.

| Environnement | API_UPSTREAM |
|---|---|
| Docker local | http://app:8080 |
| Kubernetes | http://app:80 |

La configuration Nginx est générée à partir du fichier
`default.conf.template` au démarrage du conteneur.

## 10. Tests d'intégration

### Vérification du backend

```bash
curl -i http://localhost:8085/api/tasks
```

### Vérification du reverse proxy Nginx

```bash
curl -i http://localhost:8086/api/tasks
```

### Vérification de l'utilisateur non-root

```bash
docker exec front id
```

L'identifiant de l'utilisateur doit être différent de 0.

### Vérification de la règle REG-02

1. Créer une tâche avec une date de planification.
2. Créer une deuxième tâche avec la même date.
3. Vérifier que le backend refuse la deuxième création.
4. Vérifier que le frontend affiche le message d'erreur.

Résultat attendu : une réponse HTTP 400 lors de la deuxième
création.

## 11. Intégration continue et déploiement

Les commandes suivantes peuvent être intégrées dans
le pipeline GitHub Actions :

```bash
npm ci
npm run lint
npm test
npm run build
```

L'image Docker du frontend peut ensuite être publiée dans
GitHub Container Registry (GHCR) et déployée sur Kubernetes.

Le routage prévu avec Ingress est :

- `/` : frontend.
- `/api` : backend.

## 12. Difficultés rencontrées

| Problème | Solution |
|---|---|
| Incompatibilité ARM64 / AMD64 du backend | Utilisation de `--platform linux/amd64` |
| Conflit de processus Vite | Arrêt du processus précédent |
| Communication frontend/backend | Configuration du proxy Vite |
| Portabilité entre environnements | Variable `API_UPSTREAM` |
| Taille de l'image Docker | Construction multi-stage |
| Sécurité du conteneur | Nginx Unprivileged |
| Conflit de dépendances entre Vitest 4 et jest-dom 8 (ERESOLVE) | Installation de @testing-library/jest-dom@6.6.3 pour conserver la compatibilité avec Vitest 4 |
