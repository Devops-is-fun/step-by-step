#!/usr/bin/env bash
# Checks the GitHub -> smee.io -> Jenkins webhook path (story 010), end to end.
# Needs (loaded with read -rs, never written in a file):
#   SMEE_URL        the smee.io channel of the GitHub webhook
#   WEBHOOK_SECRET  the shared secret of the GitHub webhook (Jenkins credential github-webhook-secret)
set -euo pipefail

: "${SMEE_URL:?SMEE_URL is not set (see docs/tooling/webhook-relay.md)}"
: "${WEBHOOK_SECRET:?WEBHOOK_SECRET is not set (see docs/tooling/webhook-relay.md)}"
RELAY=cicd-webhook-relay

# 1. The relay container runs
if [ "$(docker inspect -f '{{.State.Running}}' "$RELAY" 2>/dev/null)" = "true" ]; then
  echo "OK  relay container $RELAY is running"
else
  echo "KO  relay container $RELAY is not running: docker start $RELAY"
  exit 1
fi

# 2. A signed ping sent to smee.io reaches Jenkins within 5 seconds
body="{\"zen\":\"check-webhook $(date +%s)\",\"hook_id\":0}"
signature="sha256=$(printf '%s' "$body" | openssl dgst -sha256 -hmac "$WEBHOOK_SECRET" | awk '{print $NF}')"
since=$(date +%s)
curl -fsS -o /dev/null -X POST "$SMEE_URL" \
  -H 'Content-Type: application/json' -H 'X-GitHub-Event: ping' \
  -H "X-Hub-Signature-256: $signature" --data-binary "$body"

for _ in 1 2 3 4 5; do
  sleep 1
  result=$(docker logs --since "$since" "$RELAY" 2>&1 | grep -oE 'github-webhook/[^ ]* - [0-9]{3}' | tail -1 || true)
  if [ -n "$result" ]; then
    break
  fi
done

case "$result" in
  *" - 200") echo "OK  signed ping forwarded to Jenkins (HTTP 200)" ;;
  *" - 400") echo "KO  Jenkins refused the ping (HTTP 400): WEBHOOK_SECRET differs from the Jenkins shared secret"; exit 1 ;;
  "")        echo "KO  no answer within 5 seconds: is smee.io reachable? docker logs $RELAY"; exit 1 ;;
  *)         echo "KO  unexpected answer: $result"; exit 1 ;;
esac
echo "Webhook path is working"