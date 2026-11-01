# Contributing to Step by Step

Step by Step is built by a team of two. This guide is the team agreement: read it before your
first commit.

| Member | Stories | Main area |
|--------|---------|-----------|
| Géraldine | 001-014, 016, 018-022 | Jenkins: pipeline, quality, artefacts, secrets, Shared Library |
| Morgan | 015, 017, 023, 024, 025 | delivery: staging, blue/green, releases, DORA, README |
| both | 026, 027 | technical report, demo video, reviews |

The detailed split is in `docs/specification/specification.md`, section « Organisation du binôme ».
New here? Start with the guided tour: `docs/team/onboarding.md`.

## 1. First setup

```bash
git clone git@github.com:GeraldineRouy/step-by-step.git
cd step-by-step
git config core.hooksPath .githooks        # enables the secret scan before each commit
git config user.name "First Last"          # this repository only
git config user.email "you@school.example"
```

Check: `git config core.hooksPath` prints `.githooks`.

## 2. Workflow

`main` is protected: nobody pushes to it directly.

1. `git switch main && git pull`
2. `git switch -c <type>-<topic>`, for example `feat-staging-stack` (no `/`: Jenkins encodes it in URLs)
3. Small commits following Conventional Commits (`docs/tooling/git-conventions.md`)
4. `git push -u origin <branch>`, then open a pull request on GitHub
5. Wait for the Jenkins status `continuous-integration/jenkins/pr-merge` ✅ (required to merge)
6. The other member reviews the PR (expected for every PR, even when not enforced)
7. **Merge pull request**, then **Delete branch**

Jenkins runs on one member's machine. No status after a few minutes? Ask for the stack to be
started; never work around the check.

## 3. Secrets

- Secrets live **only** in Jenkins credentials and in the password manager: never in a file, not
  even an ignored `.env`.
- Never paste a token, a password or the smee.io URL into ChatGPT, Claude or a chat.
- Load a secret in a terminal with `read -rs NAME && export NAME`, then `unset NAME`.
- Leaked a secret? Revoke it and create a new one first, then clean up.
- The pre-commit hook and the **Secrets** stage of the pipeline scan every change
  (`scripts/check-no-secrets.sh`).

## 4. Using AI assistants

The course allows tools outside its scope **if you can explain them**, and each member must be able
to explain the whole chain.

| Category | Files | AI generation |
|----------|-------|---------------|
| Application code | `src/`, tests, application dependencies in `pom.xml` | ✅ generate directly, then check: tests green, quick review |
| CI/CD | `Jenkinsfile`, Shared Library, `scripts/`, Compose files, `Dockerfile`, nginx | 🟠 guided: understand every line you commit; the other member reviews the PR |
| Docs and report | `docs/tooling/`, README, report | 🟡 a draft is fine, rewrite it in your own words |
| Secrets | tokens, passwords, smee URL, credentials | ❌ never |

## 5. Project skills

The method skills (specification, stories, tutorials, feedback) are versioned in `.opencode/skills/skills/`:
- **opencode** loads them directly;
- **Claude Code**: ask it to read `.opencode/skills/skills/<skill>/SKILL.md` and follow it (for example `tasks-fr` with the story number);
- **ChatGPT**: create a Project and attach the `SKILL.md`, the specification and the story as files.

## 6. Definition of done for a PR

- [ ] Jenkins status ✅
- [ ] Reviewed by the other member
- [ ] Docs updated when behaviour changes (`docs/tooling/`, README)
- [ ] No secret in the diff
