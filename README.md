# Step by Step

[🇬🇧 English version](README.en.md)

Projet de démonstration d'une chaîne CI/CD complète : Jenkins, SonarQube et Nexus,
déclenchés par un webhook GitHub.

## Prérequis

- Docker 29.x avec Docker Compose v2 (`docker compose`)
- 8 Go de RAM disponibles pour Docker
- Java 17 et Maven 3.9 (stories 008 et 011)

## Démarrage de l'outillage

Les trois outils de la chaîne CI/CD se démarrent avec **une seule commande** :

```bash
docker compose up -d
```

| Outil      | URL                     | Accès        |
|------------|-------------------------|--------------|
| Jenkins    | http://localhost:8080   | mot de passe initial : `docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword` |
| SonarQube  | http://localhost:9000   | admin / admin (à changer) |
| Nexus      | http://localhost:8081   | admin / mot de passe temporaire dans `/nexus-data/admin.password`; commande `docker compose exec nexus cat /nexus-data/admin.password` |

Les ports n'écoutent que sur `127.0.0.1` : ils ne sont pas accessibles depuis une autre machine.

### Commandes utiles

```bash
docker compose ps                            # état et santé des services
docker compose logs -f sonarqube             # suivre les journaux
docker compose stop                          # arrêter, conserver les données
docker compose down                          # arrêter et supprimer les conteneurs
docker compose down -v                       # tout supprimer, DONNÉES INCLUES
```

Ne lancez jamais `down -v` sans vérifier deux fois : les volumes contiennent la configuration de
Jenkins, les analyses SonarQube et les dépôts Nexus.

## Suite du projet

Ce README sera complété au fil des stories.
