# Jenkins Configuration

Jenkins configuration is set in its web UI: it doesn't exist in a file of the repository.
This document provides instructions on how to do it on a new Jenkins installation.

## 1. Unlocking

1. Open your browser and navigate to `http://localhost:8080`.
2. Read initial password, without saving it: `docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword`
3. Paste it in the password field, then click on "Install suggested plugins".

## 2. Admin account

| Field | Value |
|-------|--------|
| User Name | `admin` |
| Password | generated and saved in a password manager/vault, never in a file |
| Full Name | `Administrateur TP` |
| e-mail address | `admin@example.com` |
| Jenkins URL | `http://localhost:8080/` |

## 3. Plugins

| Id | Source | Task |
|-------------|---------|------|
| `git` | suggested | get source code |
| `github-branch-source` | suggested | Multibranch job and GitHub webhook |
| `workflow-aggregator` | suggested | Pipeline as Code (`Jenkinsfile`) |
| `pipeline-graph-view` | suggested | display pipeline stages |
| `credentials-binding` | suggested | `withCredentials` in the pipeline |
| `junit` | suggested | test reports |
| `sonar` | added | SonarQube analysis and Quality Gate |
| `coverage` | added | JaCoCo coverage report |
| `warnings-ng` | added | Checkstyle and SpotBugs reports |
| `pipeline-utility-steps` | added | read files in the pipeline |
| `locale` | added | force the English UI: Manage Jenkins -> Appearance, Default Language `English`, browser preference ignored |

Plugins added: Manage Jenkins -> Plugins -> Available plugins, then `docker compose restart jenkins`.

## 4. API token for scripts

1. `http://localhost:8080/me/security/` -> "Add a new token", name `scripts-tp`.
2. Save the token in the password manager.
3. Load it in the terminal, without showing it or keeping it in the history:

```bash
export JENKINS_USER=admin
read -rs JENKINS_TOKEN && export JENKINS_TOKEN
```

## 5. Verify

```bash
./scripts/check-jenkins.sh
```

The script checks authentication by token, CSRF protection (crumb) and 10 plugins. It displays "Jenkins is compliant" and
exits with code 0, or exits with code 1 immediately if authentication fails, or after all checks if any other check fails.

## 6. Security

- Ports `8080` and `50000` are exposed on `127.0.0.1` only: no agent URL is exposed.
- CSRF protection (crumb) is enabled.
- No password nor API token in the repository: only in a password manager.
- The controller will be set to 0 executors in story 005, along with the agent.
