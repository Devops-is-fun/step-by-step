
#!/usr/bin/env bash
set -euo pipefail

ENVIRONMENT="${1:-}"
IMAGE_TAG="${2:-}"

if [[ "$ENVIRONMENT" != "staging" ]]; then
  echo "Usage: $0 staging IMAGE_TAG"
  exit 1
fi

if [[ -z "$IMAGE_TAG" ]]; then
  echo "ERROR: Image tag is required"
  exit 1
fi

: "${STAGING_DB_USER:?STAGING_DB_USER is required}"
: "${STAGING_DB_PASSWORD:?STAGING_DB_PASSWORD is required}"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/environments/staging/docker-compose.yml"

export IMAGE_TAG

echo "Deploying image step-by-step:$IMAGE_TAG to staging"

# Verify the image exists locally or can be retrieved.
if ! docker image inspect "step-by-step:$IMAGE_TAG" >/dev/null 2>&1; then
  docker pull "step-by-step:$IMAGE_TAG"
fi

# Reuse unchanged containers.
docker compose -f "$COMPOSE_FILE" up -d --wait --no-build

# Verify the application is responding.
bash "$ROOT_DIR/scripts/healthcheck.sh" \
  "http://localhost:8082/actuator/health"

echo "Staging deployment successful"
