#!/usr/bin/env bash
# Blue/green deployment to production (stories 017-018, module N2a).
#  1. deploy the version on the idle colour and wait until it is healthy;
#  2. switch the proxy to it and check the health through the proxy;
#  3. on any failure, the proxy stays on (or goes back to) the previous colour: automatic rollback.
# The previous colour keeps running, so a manual rollback is one proxy switch away.
# Needs DB_USER and DB_PASSWORD (Jenkins credential production-db-creds).
# Usage: scripts/deploy-production.sh <version>
set -euo pipefail

VERSION="${1:?usage: deploy-production.sh <version>}"
: "${DB_USER:?DB_USER is not set}" "${DB_PASSWORD:?DB_PASSWORD is not set}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PROJECT="${PROJECT:-stepbystep-production}"
COMPOSE=(docker compose -p "$PROJECT" -f "$ROOT/environments/production/docker-compose.yml")
PROXY="$PROJECT-proxy-1"

running() { [ "$(docker inspect -f '{{.State.Running}}' "$1" 2>/dev/null)" = "true" ]; }
version_of() { docker inspect -f '{{.Config.Image}}' "$PROJECT-app-$1-1" 2>/dev/null | sed 's/^step-by-step://'; }
switch_to() {
  docker exec "$PROXY" sh -c "echo 'upstream app { server app-$1:8080; }' > /etc/nginx/upstream/active.conf && nginx -t -q && nginx -s reload"
}
proxy_healthy() {
  for _ in $(seq 10); do
    docker exec "$PROXY" wget -qO- http://127.0.0.1/actuator/health 2>/dev/null | grep -q '"status":"UP"' && return 0
    sleep 2
  done
  return 1
}

"$ROOT/scripts/build-runtime-image.sh" "$VERSION"
docker build -q -t step-by-step-proxy:1 "$ROOT/environments/production/proxy" > /dev/null

active=none
if running "$PROXY"; then
  active="$(docker exec "$PROXY" sed -n 's/.*server app-\([a-z]*\):.*/\1/p' /etc/nginx/upstream/active.conf)"
fi
if [ "$active" = "blue" ]; then target=green; else target=blue; fi
echo "Active colour: $active -> deploying $VERSION on $target"

# Keep the version of the other colour unchanged in the Compose interpolation
export APP_BLUE_VERSION="$(version_of blue || true)" APP_GREEN_VERSION="$(version_of green || true)"
if [ "$target" = "blue" ]; then APP_BLUE_VERSION="$VERSION"; else APP_GREEN_VERSION="$VERSION"; fi

if ! "${COMPOSE[@]}" up -d --wait db "app-$target"; then
  echo "KO  $target is not healthy: production stays on $active (rollback)"
  "${COMPOSE[@]}" stop "app-$target"
  exit 1
fi

if ! running "$PROXY"; then
  "${COMPOSE[@]}" up -d --wait proxy
fi
switch_to "$target"

if ! proxy_healthy; then
  if [ "$active" != "none" ]; then
    switch_to "$active"
    echo "KO  health check failed through the proxy: switched back to $active (rollback)"
  else
    echo "KO  health check failed through the proxy on the first deployment"
  fi
  exit 1
fi
"$ROOT/scripts/connect-prometheus.sh" "$PROJECT"
echo "OK  production serves step-by-step:$VERSION on $target (previous: $active)"
