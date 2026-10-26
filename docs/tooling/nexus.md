# Nexus Configuration

Nexus is configured in its web UI: this configuration is not stored in any file of the
repository. This page describes how to redo it on a fresh Nexus instance (Community Edition 3.96).

## 1. Setup wizard

1. Read the initial password, without saving it:
   `docker compose exec nexus cat /nexus-data/admin.password`
2. Sign in at `http://localhost:8081` as `admin` and follow the wizard:
    - new admin password: generated and stored in a password manager;
    - anonymous access: **enabled** (read only), so that staging and production download
      artifacts without credentials.
3. Nexus deletes `/nexus-data/admin.password` when the wizard is completed.

## 2. Repository

The default `maven-releases` hosted repository is used, unchanged:

| Setting | Value | Why |
|---------|-------|-----|
| Version policy | `Release` | no `-SNAPSHOT` versions |
| Deployment policy | `Disable redeploy` | a published version can never be replaced |

## 3. Deployer account

| Item | Value |
|------|-------|
| Role | `step-by-step-deployer`: `nx-repository-view-maven2-maven-releases-` `add`, `edit`, `read`, `browse` (no `delete`) |
| User | `nexus-deployer`, role `step-by-step-deployer` only |
| Password | generated, stored **only** in the Jenkins credential `nexus-deploy-creds` (Username with password) |

If the password is lost: reset it in Nexus, then update the Jenkins credential.

## 4. Immutability check

A witness `.pom` published twice to the same version: the first upload returns `201`, the second
returns `409`. The witness is then deleted.

## 5. Verification

```bash
./scripts/check-nexus.sh