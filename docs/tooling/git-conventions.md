# Git Conventions

## Branches

| Branch | Use |
|--------|-----|
| `main` | reference branch, built by the pipeline |
| `feature/<story>-<short-name>` | one branch per story, e.g. `feature/007-spring-boot-skeleton` |

## Commit messages

[Conventional Commits](https://www.conventionalcommits.org/), in English: `type(scope): description`.

| Type | Use |
|------|-----|
| `feat` | new feature of the application or the pipeline |
| `fix` | bug fix |
| `docs` | documentation only |
| `test` | tests only |
| `build` | `Dockerfile`, `docker-compose.yml`, `pom.xml` |
| `ci` | `Jenkinsfile`, shared library, Jenkins / SonarQube / Nexus configuration and checks |
| `chore` | repository housekeeping (`.gitignore`, hooks) |

Scopes: `app`, `pipeline`, `jenkins`, `sonar`, `nexus`, `tooling`, `envs`, `repo`.

## Staging files

Files are added by name (`git add <file>`), never with `git add -A` or `git add .`: each commit
contains exactly what its message describes, and nothing else.

## Secret scan

`scripts/check-no-secrets.sh` runs before every commit through the hook `.githooks/pre-commit`.
After a fresh clone, enable the hook once:

```bash
git config core.hooksPath .githooks
```

The commit is blocked when the files to commit contain a known secret format (private key, SonarQube, Jenkins or GitHub token), or a long value assigned to a secret-like name (`password`, `token`…) outside Markdown files. Never bypass it with `git commit --no-verify`: remove the value, and if a secret was ever committed, revoke it and generate a new one.
