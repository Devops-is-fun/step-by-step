#!/usr/bin/env bash
# Connects Prometheus (tooling stack) to the network of a deployed stack so that it can scrape
# /actuator/prometheus by container name (module N3b). The applications only listen on 127.0.0.1 on the host,
# so Prometheus cannot reach them through the host. Safe to run again; does nothing if Prometheus is not running.
# Usage: scripts/connect-prometheus.sh <compose project>, for example stepbystep-production
set -euo pipefail

PROJECT="${1:?usage: connect-prometheus.sh <compose project>}"
PROMETHEUS="${PROMETHEUS_CONTAINER:-step-by-step-prometheus}"

if [ "$(docker inspect -f '{{.State.Running}}' "$PROMETHEUS" 2>/dev/null)" != "true" ]; then
  echo "INFO prometheus is not running: $PROJECT not connected"
  exit 0
fi
if docker inspect -f '{{json .NetworkSettings.Networks}}' "$PROMETHEUS" | grep -q "\"${PROJECT}_default\""; then
  echo "OK  prometheus already on ${PROJECT}_default"
else
  docker network connect "${PROJECT}_default" "$PROMETHEUS"
  echo "OK  prometheus connected to ${PROJECT}_default"
fi
