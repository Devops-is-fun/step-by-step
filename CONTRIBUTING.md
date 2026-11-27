# Contributing to Step by Step

Team agreement of the *Advanced DevOps* project (ESGI M1, November 2026). Read it before your first commit.

| Member | Main area |
|--------|-----------|
| Géraldine | CI/CD (GitHub Actions, GHCR), supply-chain security, GitOps (Flux), HPA, zero-downtime |
| Morgan | Platform (kind cluster scripts, ingress), observability (Prometheus, Grafana, Loki, Alloy), resilience tests |
| Zhou | Frontend (Vue, tests, Dockerfile, nginx) |

The detailed split is in the [project report](docs/dossier-projet.md#9-organisation-et-répartition-des-rôles).

## 1. First setup

```bash
git clone git@github.com:Devops-is-fun/step-by-step.git
cd step-by-step
git config core.hooksPath .githooks        # enables the secret scan before each commit
```

To run the whole platform locally, follow the [README](README.md#3-installer-le-cluster-et-déployer).

## 2. Workflow

1. `git switch main && git pull`
2. `git switch -c <type>/<topic>`, for example `feat/hpa`, `fix/kind-config`, `docs/readme`
3. Small commits following Conventional Commits (`feat:`, `fix:`, `ci:`, `docs:`, `chore:`…)
4. `git push -u origin <branch>`, then open a pull request (not a draft when it is ready for review)
5. Wait for the checks ✅: **CI** (backend), **Front** (when `front/` changes), **CodeQL**
6. Another member reviews the PR, with the **Approve** button
7. **Merge pull request**, then **Delete branch**

What happens next, without any manual step: the CI pushes the images to GHCR and commits the new image tags
in `k8s/` (`chore(deploy): …`); Flux applies them to the cluster within a minute.

Rules that keep GitOps working:

- `k8s/` contains **Kubernetes resources only** (Flux applies the whole folder). Tool configs (kind, Helm values)
  go to `scripts/`, next to the script that uses them.
- Never change the cluster by hand with `kubectl` for something that must last: Flux reverts it. Change Git.
- The Deployment of the backend has no `replicas`: the HPA decides.

## 3. Secrets

- The repository is **public**: no secret in a file, not even an ignored `.env`.
- Database and Grafana passwords are created by hand in the cluster (Kubernetes Secret, Helm value), never committed.
- Never paste a token or a password into an AI assistant or a chat.
- Load a secret in a terminal with `read -rs NAME && export NAME`, then `unset NAME`.
- Leaked a secret? Revoke it and create a new one first, then clean up.
- The pre-commit hook and **gitleaks** in the CI scan every change and the whole history.

## 4. Using AI assistants

Allowed **if you can explain** what you commit: each member must be able to explain the whole chain.

| Category | Files | AI generation |
|----------|-------|---------------|
| Application code | `src/`, `front/src/`, tests | ✅ generate, then check: tests green, quick review |
| CI/CD and platform | `.github/workflows/`, `k8s/`, `flux/`, `scripts/`, Dockerfiles | 🟠 guided: understand every line; another member reviews the PR |
| Docs and report | `docs/`, README | 🟡 a draft is fine, reread it and correct it |
| Secrets | tokens, passwords | ❌ never |

## 5. Definition of done for a PR

- [ ] Checks ✅ (CI, Front if relevant, CodeQL)
- [ ] Approved by another member
- [ ] Docs updated when behaviour changes (README, `docs/`)
- [ ] No secret in the diff
