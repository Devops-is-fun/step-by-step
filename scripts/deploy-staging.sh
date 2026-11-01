#!/usr/bin/env bash
# Deploys a version to staging (stories 015-016). Idempotent: running it twice with the same version
# changes nothing; with a new version, only the app container is recreated.
# Needs DB_USER and DB_PASSWORD (Jenkins credential staging-db-creds).
# Usage: scripts/deploy-staging.sh <version>
set -euo pipefail

VERSION="${1:?usage: deploy-staging.sh <version>}"
: "${DB_USER:?DB_USER is not set}" "${DB_PASSWORD:?DB_PASSWORD is not set}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PROJECT="${PROJECT:-stepbystep-staging}"

"$ROOT/scripts/build-runtime-image.sh" "$VERSION"

# --wait returns only when the database and the application are healthy (/actuator/health = UP)
APP_VERSION="$VERSION" docker compose -p "$PROJECT" -f "$ROOT/environments/staging/docker-compose.yml" up -d --wait
"$ROOT/scripts/connect-prometheus.sh" "$PROJECT"
echo "OK  staging runs step-by-step:$VERSION"
