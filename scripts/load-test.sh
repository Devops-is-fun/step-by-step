#!/usr/bin/env bash
# Sends load to the backend from inside the cluster (k6), to show the HPA adding pods.
# Inside the cluster, the Service spreads the requests over all the pods
# (a port-forward would send everything to a single pod).
#
# Usage : ./scripts/load-test.sh [duration]     e.g. ./scripts/load-test.sh 5m
# In another terminal : kubectl -n step-by-step get hpa,pods -w
set -euo pipefail

K6_IMAGE="grafana/k6:1.8.2"
DURATION="${1:-3m}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=== Test de charge : 20 utilisateurs virtuels pendant ${DURATION} ==="
echo "Suivi dans un autre terminal : kubectl -n step-by-step get hpa,pods -w"

# The k6 script is sent on stdin ("run -"); the pod is deleted at the end (--rm)
kubectl -n step-by-step run k6-load-test \
    --rm -i --restart=Never \
    --image="$K6_IMAGE" \
    --env="DURATION=${DURATION}" \
    -- run - < "${SCRIPT_DIR}/load-test.js"
