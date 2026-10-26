#!/usr/bin/env bash
# Checks the Jenkins agents (story 005): builds run on docker-agent, never on the controller.
# Prerequisites: JENKINS_USER and JENKINS_TOKEN exported (API token).
# Run it from the project root (it uses docker compose for the last check).
set -euo pipefail

JENKINS_URL="${JENKINS_URL:-http://localhost:8080}"
AGENT="${JENKINS_AGENT:-docker-agent}"
: "${JENKINS_USER:?JENKINS_USER is not set (see docs/tooling/jenkins-configuration.md)}"
: "${JENKINS_TOKEN:?JENKINS_TOKEN is not set (see docs/tooling/jenkins-configuration.md)}"

# Authenticated call to the Jenkins API. -g: brackets and parentheses are not curl patterns.
api() {
  curl -fsSg -u "$JENKINS_USER:$JENKINS_TOKEN" "$JENKINS_URL$1"
}

errors=0

# 1. The controller has no executor: no build can run on it
controller_json="$(api "/computer/(built-in)/api/json?tree=numExecutors" || true)"
if [ -z "$controller_json" ]; then
  echo "KO  Jenkins API does not answer (check JENKINS_URL and the token)"
  exit 1
elif grep -q '"numExecutors":0[,}]' <<< "$controller_json"; then
  echo "OK  controller has 0 executors"
else
  echo "KO  controller has executors: builds could run on it"
  errors=$((errors + 1))
fi

# 2. The agent exists and is online
agent_json="$(api "/computer/$AGENT/api/json?tree=offline,numExecutors" || true)"
if grep -q '"offline":false' <<< "$agent_json"; then
  echo "OK  agent $AGENT is online"
else
  echo "KO  agent $AGENT is missing or offline"
  errors=$((errors + 1))
fi

# 3. The agent has 2 executors
if grep -q '"numExecutors":2[,}]' <<< "$agent_json"; then
  echo "OK  agent $AGENT has 2 executors"
else
  echo "KO  agent $AGENT does not have 2 executors"
  errors=$((errors + 1))
fi

# 4. Docker answers from the agent (integration tests and deployments need it).
#    < /dev/null: docker compose exec would otherwise read (and swallow) this script's stdin.
if docker compose exec -T -u jenkins jenkins-agent docker version > /dev/null 2>&1 < /dev/null; then
  echo "OK  docker reachable from the agent"
else
  echo "KO  docker not reachable from the agent"
  errors=$((errors + 1))
fi

if [ "$errors" -eq 0 ]; then
  echo "Agents are compliant"
else
  echo "$errors check(s) failed"
  exit 1
fi