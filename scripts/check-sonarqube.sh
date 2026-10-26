#!/usr/bin/env bash
# Checks the SonarQube configuration (story 003).
# Requires: SONAR_TOKEN exported (user token, never the admin password).
set -euo pipefail

SONAR_URL="${SONAR_URL:-http://localhost:9000}"
PROJECT_KEY="step-by-step"
GATE_NAME="step-by-step-gate"
: "${SONAR_TOKEN:?SONAR_TOKEN is not set (see docs/tooling/sonarqube.md)}"

# Authenticated call to the SonarQube API: the token is the login, the password stays empty.
api() {
  curl -fsS -u "$SONAR_TOKEN:" "$SONAR_URL$1"
}

errors=0

# 1. Token authentication. This endpoint always answers 200: read "valid", not the HTTP code.
if grep -q '"valid":true' <<< "$(api "/api/authentication/validate" || true)"; then
  echo "OK  token authentication"
else
  echo "KO  token authentication refused"
  exit 1
fi

# 2. The default credentials (public, built into the product) must be refused
if grep -q '"valid":false' <<< "$(curl -fsS -u admin:admin "$SONAR_URL/api/authentication/validate" || true)"; then
  echo "OK  default admin/admin refused"
else
  echo "KO  default admin/admin still accepted"
  errors=$((errors + 1))
fi

# 3. The project exists
if api "/api/components/show?component=$PROJECT_KEY" > /dev/null; then
  echo "OK  project $PROJECT_KEY exists"
else
  echo "KO  project $PROJECT_KEY missing"
  errors=$((errors + 1))
fi

# 4. The project uses the dedicated quality gate
if grep -q "\"name\":\"$GATE_NAME\"" <<< "$(api "/api/qualitygates/get_by_project?project=$PROJECT_KEY" || true)"; then
  echo "OK  quality gate $GATE_NAME assigned"
else
  echo "KO  quality gate $GATE_NAME not assigned"
  errors=$((errors + 1))
fi

# 5. The gate requires 60% coverage on new code
if grep -q '"metric":"new_coverage","op":"LT","error":"60"' <<< "$(api "/api/qualitygates/show?name=$GATE_NAME" || true)"; then
  echo "OK  new code coverage threshold: 60%"
else
  echo "KO  new code coverage threshold is not 60%"
  errors=$((errors + 1))
fi

if [ "$errors" -eq 0 ]; then
  echo "SonarQube is compliant"
else
  echo "$errors check(s) failed"
  exit 1
fi
