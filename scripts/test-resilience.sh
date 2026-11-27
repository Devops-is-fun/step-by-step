
#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="step-by-step"
DEPLOYMENT="step-by-step"
SERVICE="app"
CLIENT="resilience-client-test"
IMAGE="curlimages/curl:8.12.1"
REQUESTS=120

echo "=== Tests de résilience Kubernetes ==="

if [[ "$(kubectl config current-context)" != "kind-step-by-step" ]]; then
  echo "ERREUR : le contexte Kubernetes attendu est kind-step-by-step."
  exit 1
fi

CLIENT_CREATED=false
cleanup() {
  if [[ "$CLIENT_CREATED" == "true" ]]; then
    kubectl -n "$NAMESPACE" delete pod "$CLIENT" \
      --ignore-not-found --wait=false >/dev/null 2>&1 || true
  fi

  if [[ -n "${HTTP_LOG:-}" ]]; then
    rm -f "$HTTP_LOG"
  fi
}
trap cleanup EXIT

echo "1. Vérification du Deployment..."
kubectl -n "$NAMESPACE" rollout status \
  deployment/"$DEPLOYMENT" --timeout=180s

REPLICAS=$(kubectl -n "$NAMESPACE" get deployment "$DEPLOYMENT" \
  -o jsonpath='{.spec.replicas}')

if [[ "$REPLICAS" -lt 2 ]]; then
  echo "ERREUR : au moins 2 réplicas sont nécessaires."
  exit 1
fi


echo "2. Test de self-healing..."

OLD_POD=$(kubectl -n "$NAMESPACE" get pods \
  -l app="$DEPLOYMENT" \
  -o jsonpath='{.items[0].metadata.name}')

if [[ -z "$OLD_POD" ]]; then
  echo "ERREUR : aucun pod backend trouvé."
  exit 1
fi

echo "Suppression du pod $OLD_POD..."
kubectl -n "$NAMESPACE" delete pod "$OLD_POD"

kubectl -n "$NAMESPACE" rollout status \
  deployment/"$DEPLOYMENT" --timeout=180s

# rollout status peut réussir alors que le remplacement
# d'un pod est encore en cours.
echo "Attente du remplacement du pod..."
kubectl -n "$NAMESPACE" wait \
  --for=delete pod/"$OLD_POD" --timeout=180s

kubectl -n "$NAMESPACE" wait \
  --for=condition=Available deployment/"$DEPLOYMENT" \
  --timeout=180s

echo "Vérification des réplicas après self-healing..."

for i in $(seq 1 60); do
  READY=$(kubectl -n "$NAMESPACE" get deployment "$DEPLOYMENT" \
    -o jsonpath='{.status.readyReplicas}')

  if [[ "${READY:-0}" -ge "$REPLICAS" ]]; then
    echo "Self-healing confirmé : $READY/$REPLICAS réplicas prêts."
    break
  fi

  if [[ "$i" -eq 60 ]]; then
    echo "ERREUR : les réplicas ne sont pas tous prêts."
    exit 1
  fi

  sleep 2
done

echo "Self-healing terminé."

echo "3. Création du client HTTP..."

if kubectl -n "$NAMESPACE" get pod "$CLIENT" >/dev/null 2>&1; then
  echo "ERREUR : le pod $CLIENT existe déjà."
  exit 1
fi

kubectl -n "$NAMESPACE" run "$CLIENT" \
  --image="$IMAGE" \
  --restart=Never \
  --command -- sleep 600

CLIENT_CREATED=true

kubectl -n "$NAMESPACE" wait \
  --for=condition=Ready pod/"$CLIENT" \
  --timeout=120s

echo "4. Test HTTP pendant le rolling restart..."

HTTP_LOG=$(mktemp)

kubectl -n "$NAMESPACE" exec "$CLIENT" -- sh -c '
  i=1
  while [ "$i" -le 120 ]; do
    code=$(curl -s -o /dev/null -w "%{http_code}" \
        --max-time 2 http://app/actuator/health) || true
    code="${code:-000}"
    echo "HTTP $code"
    i=$((i + 1))
    sleep 0.5
  done
' > "$HTTP_LOG" &

HTTP_PID=$!

sleep 3

kubectl -n "$NAMESPACE" rollout restart \
  deployment/"$DEPLOYMENT"

kubectl -n "$NAMESPACE" rollout status \
  deployment/"$DEPLOYMENT" --timeout=180s

wait "$HTTP_PID"

echo "5. Résultats HTTP..."

awk -v expected="$REQUESTS" '/^HTTP / {
  total++
  if ($2 == 200) ok++
  else errors++
}
END {
  printf "Total: %d | HTTP 200: %d | Échecs: %d | Disponibilité: %.2f%%\n",
    total, ok, errors, (total > 0 ? ok * 100 / total : 0)
  if (total != expected || errors > 0) exit 1
}' "$HTTP_LOG"

rm -f "$HTTP_LOG"

echo "=== Tests de résilience terminés ==="
