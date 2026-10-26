#!/usr/bin/env bash
# Check Jenkins configuration
# Prerequisites : JENKINS_USER and JENKINS_TOKEN exported (API token)

set -euo pipefail

JENKINS_URL="${JENKINS_URL:-http://localhost:8080}"
: "${JENKINS_USER:?JENKINS_USER is not set (see docs/tooling/jenkins-configuration.md)}"
: "${JENKINS_TOKEN:?JENKINS_TOKEN is not set (see docs/tooling/jenkins-configuration.md)}"

REQUIRED_PLUGINS=(
  git
  github-branch-source
  workflow-aggregator
  pipeline-graph-view
  sonar
  credentials-binding
  junit
  coverage
  warnings-ng
  pipeline-utility-steps
)

# Authentified call to Jenkins API. -g: URL crochets are not curl patterns.
api() {
  curl -fsSg -u "$JENKINS_USER:$JENKINS_TOKEN" "$JENKINS_URL$1"
}

errors=0

# 1. Token authentication: without it, no need to go on
if api "/api/json" > /dev/null; then
  echo "OK  token authentication"
else
  echo "KO  token authentication refused"
  exit 1
fi

# 2. CSRF protection: crumb must be available
crumb_json="$(api "/crumbIssuer/api/json" || true)"
if grep -q '"crumb"' <<< "$crumb_json"; then
  echo "OK  CSRF protection (crumb available)"
else
  echo "KO  CSRF protection: no crumb"
  errors=$((errors + 1))
fi

# 3. Installed plugins
plugins_json="$(api "/pluginManager/api/json?tree=plugins[shortName]")"
for plugin in "${REQUIRED_PLUGINS[@]}"; do
  if grep -q "\"shortName\":\"$plugin\"" <<< "$plugins_json"; then
    echo "OK  plugin $plugin"
  else
    echo "KO  plugin $plugin missing"
    errors=$((errors + 1))
  fi
done

if [ "$errors" -eq 0 ]; then
  echo "Jenkins is compliant"
else
  echo "$errors check(s) failed"
  exit 1
fi
