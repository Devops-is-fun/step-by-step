# GitHub webhook relay

Jenkins runs on `localhost:8080` and cannot be reached from GitHub. Instead of a tunnel, which would
expose the whole Jenkins UI to the Internet, GitHub webhooks go through a **smee.io relay**:

```
GitHub --POST--> smee.io channel <--SSE-- cicd-webhook-relay --POST--> jenkins:8080/github-webhook/
```

Jenkins stays private: no port of the machine is open to the Internet.

## 1. Values and where they live

| Value | Stored in | Used by |
|-------|-----------|---------|
| smee.io channel URL | password manager | GitHub webhook *Payload URL*, `cicd-webhook-relay`, `check-webhook.sh` (`SMEE_URL`) |
| Webhook shared secret | password manager + Jenkins credential `github-webhook-secret` (Secret text) | GitHub webhook *Secret*, Jenkins *Shared secrets*, `check-webhook.sh` (`WEBHOOK_SECRET`) |
| GitHub fine-grained token (`step-by-step` only, 90 days) | password manager + Jenkins credential `github-token` (Username with password) | Multibranch scan, commit statuses |

The smee URL is not a password, but anyone who knows it can read the repository notifications:
keep it out of the project files.

## 2. Relay container

Created once, outside `docker-compose.yml` so that the smee URL is never written in a file:

```bash
read -rs SMEE_URL && export SMEE_URL
docker run -d --name cicd-webhook-relay --restart unless-stopped --network cicd_cicd \
  node:22.23.4-alpine3.24 \
  npx --yes smee-client@5.0.0 --url "$SMEE_URL" --target 'http://jenkins:8080/github-webhook/?source=smee'
```

The `?source=smee` parameter is required: without it, smee drops the trailing `/` and Jenkins
answers `405`. The container restarts with Docker; after a `docker compose down` (network
recreated), remove it and run the command again.

## 3. Jenkins

| Setting | Value |
|---------|-------|
| Manage Jenkins -> System -> GitHub -> Advanced -> Shared secrets | `github-webhook-secret` |
| Job | `step-by-step`, *Multibranch Pipeline*, source GitHub, credential `github-token` |
| Behaviours | defaults: branches (excluding those filed as PRs), PRs from origin merged with target |
| Scan trigger | *Periodically if not otherwise run*: 1 day (fallback when a webhook is missed) |

## 4. GitHub webhook

Repository -> Settings -> Webhooks: *Payload URL* = smee URL, `application/json`, *Secret* = shared
secret, SSL verification on, events **Pushes** and **Pull requests**.

## 5. Check

```bash
read -rs SMEE_URL && export SMEE_URL
read -rs WEBHOOK_SECRET && export WEBHOOK_SECRET
./scripts/check-webhook.sh
```

Expected: `Webhook path is working`. The script sends a ping signed like GitHub's and reads the
answer of Jenkins in the relay logs.

## 6. Troubleshooting

| Symptom | Fix |
|---------|-----|
| Relay log `- 400` | the shared secrets of GitHub and Jenkins differ |
| Relay log `- 405` | `?source=smee` missing in `--target` |
| No relay log line after a push | `docker logs cicd-webhook-relay`: not `Connected`? `docker restart cicd-webhook-relay` |
| Scan fails with `Bad credentials` | the GitHub token expired: create a new one with the same permissions, update `github-token` |
| Machine was off during a push | *Scan Repository Now* on the job, or wait for the daily scan |
| smee.io down | fallback: the daily scan, or *Scan Repository Now* |