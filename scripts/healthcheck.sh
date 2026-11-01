
#!/usr/bin/env bash
set -euo pipefail

URL="${1:-http://localhost:8082/actuator/health}"
MAX_ATTEMPTS="${2:-30}"

echo "Checking application health: $URL"

for ((i=1; i<=MAX_ATTEMPTS; i++)); do
  if curl --fail --silent --show-error --max-time 3 "$URL" \
      | grep -q '"status":"UP"'; then
    echo "Application is healthy"
    exit 0
  fi

  echo "Waiting for application... ($i/$MAX_ATTEMPTS)"
  sleep 2
done

echo "ERROR: Application healthcheck failed"
exit 1
