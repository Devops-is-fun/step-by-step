# step-by-step-lib — Jenkins Shared Library

Common pipeline steps of Step by Step, **versioned by Git tag** (`lib-v<major>.<minor>.<patch>`).
The `Jenkinsfile` pins the version it uses:

```groovy
@Library('step-by-step-lib@lib-v1.1.0') _
```

## Jenkins configuration

Manage Jenkins -> System -> **Global Trusted Pipeline Libraries** -> Add:

| Field | Value |
|-------|-------|
| Name | `step-by-step-lib` |
| Default version | `lib-v1.0.0` |
| Allow default version to be overridden | checked |
| Retrieval method | Modern SCM -> GitHub, credential `github-token`, repository `GeraldineRouy/step-by-step` |
| Library Path | `pipeline-library/` |

The library lives in the application repository (folder `pipeline-library/`) so that a single
fine-grained GitHub token is enough. Its versions are independent from the application versions.

## Steps

| Step | What it does | Credentials |
|------|--------------|-------------|
| `integrationTests()` | throwaway PostgreSQL per build (Compose), agent joins its network, `./mvnw verify`, cleanup | — |
| `publishToNexus(version)` | uploads the jar and a pom to `maven-releases`, skips an already published version | `nexus-deploy-creds` |
| `deployStaging(version)` | `scripts/deploy-staging.sh` | `staging-db-creds` |
| `deployProduction(version)` | `scripts/deploy-production.sh` (blue/green, automatic rollback) | `production-db-creds` |
| `notifyDiscord(version)` | posts the build result on Discord | `discord-webhook` |

## Versions

| Tag | Change |
|-----|--------|
| `lib-v1.0.0` | first version: integration tests, Nexus publication, deployments, Discord |
| `lib-v1.1.0` | `publishToNexus` finds the jar whatever the `pom.xml` version (release-please bumps it) |

## Releasing a new version

1. Change the steps in a branch, open a PR.
2. After the merge: `git tag -a lib-v1.1.0 -m "step-by-step-lib 1.1.0" && git push origin lib-v1.1.0`.
3. Bump the version in `@Library(...)` of the `Jenkinsfile`, in a PR: the pipeline never changes by surprise.
