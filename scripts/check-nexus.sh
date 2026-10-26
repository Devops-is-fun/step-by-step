#!/usr/bin/env bash
# Checks the Nexus configuration (story 004).
# Needs no credentials: everything checked here is public on purpose.
# Run it from the project root (it uses docker compose for the last check).
set -euo pipefail

NEXUS_URL="${NEXUS_URL:-http://localhost:8081}"
REPO="${NEXUS_REPO:-maven-releases}"

# HTTP code of an anonymous request ("000" when Nexus does not answer)
http_code() {
  curl -s -o /dev/null -w '%{http_code}' "$@" || true
}

errors=0

# 1. Nexus answers: no point going further without it
code="$(http_code "$NEXUS_URL/service/rest/v1/status")"
if [ "$code" = "200" ]; then
  echo "OK  Nexus is up"
else
  echo "KO  Nexus does not answer (HTTP $code)"
  exit 1
fi

# 2. Nexus accepts writes (disk not full, not in read-only mode)
code="$(http_code "$NEXUS_URL/service/rest/v1/status/writable")"
if [ "$code" = "200" ]; then
  echo "OK  Nexus is writable"
else
  echo "KO  Nexus is not writable (HTTP $code)"
  errors=$((errors + 1))
fi

# 3. Anonymous users can read the repository (staging and production download without credentials)
code="$(http_code "$NEXUS_URL/repository/$REPO/")"
if [ "$code" = "200" ]; then
  echo "OK  anonymous read on $REPO"
else
  echo "KO  anonymous read on $REPO refused (HTTP $code)"
  errors=$((errors + 1))
fi

# 4. Anonymous users cannot publish
code="$(http_code -X PUT --data check "$NEXUS_URL/repository/$REPO/check/anonymous/1/anonymous-1.pom")"
if [ "$code" = "401" ]; then
  echo "OK  anonymous write on $REPO refused"
else
  echo "KO  anonymous write on $REPO not refused (HTTP $code)"
  errors=$((errors + 1))
fi

# 5. The setup wizard is completed: Nexus deletes its initial password file at the end.
#    < /dev/null: docker compose exec would otherwise read (and swallow) this script's stdin.
if docker compose exec -T nexus test ! -f /nexus-data/admin.password < /dev/null; then
  echo "OK  setup wizard completed"
else
  echo "KO  setup wizard not completed (/nexus-data/admin.password still exists)"
  errors=$((errors + 1))
fi

if [ "$errors" -eq 0 ]; then
  echo "Nexus is compliant"
else
  echo "$errors check(s) failed"
  exit 1
fi