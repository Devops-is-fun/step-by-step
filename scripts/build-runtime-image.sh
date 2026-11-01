#!/usr/bin/env bash
# Builds the image step-by-step:<version> from the jar published in Nexus (stories 015-017).
# Idempotent: an existing image is reused, so staging and production run the very same artefact.
# Usage: scripts/build-runtime-image.sh <version>     (NEXUS_URL defaults to the Jenkins network address)
set -euo pipefail

VERSION="${1:?usage: build-runtime-image.sh <version>}"
NEXUS_URL="${NEXUS_URL:-http://nexus:8081}"
IMAGE="step-by-step:$VERSION"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

if docker image inspect "$IMAGE" > /dev/null 2>&1; then
  echo "OK  image $IMAGE already built"
  exit 0
fi

context="$(mktemp -d)"
trap 'rm -rf "$context"' EXIT
jar_url="$NEXUS_URL/repository/maven-releases/com/esgi/step-by-step/$VERSION/step-by-step-$VERSION.jar"
curl -fsS -o "$context/app.jar" "$jar_url"
cp "$ROOT/environments/runtime/Dockerfile" "$context/"
docker build -q -t "$IMAGE" "$context" > /dev/null
echo "OK  image $IMAGE built from $jar_url"
