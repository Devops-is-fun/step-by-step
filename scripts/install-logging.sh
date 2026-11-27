#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="monitoring"
LOKI_VERSION="21.6.3"
ALLOY_VERSION="1.16.0"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=== Installation de Loki et Grafana Alloy ==="

for tool in helm kubectl; do
    if ! command -v "$tool" >/dev/null 2>&1; then
        echo "ERREUR : $tool est introuvable."
        exit 1
    fi
done

CONTEXT="$(kubectl config current-context)"

if [[ "$CONTEXT" != "kind-step-by-step" ]]; then
    echo "ERREUR : contexte Kubernetes inattendu : $CONTEXT"
    exit 1
fi

if ! kubectl -n "$NAMESPACE" get deployment monitoring-grafana \
    >/dev/null 2>&1; then
    echo "ERREUR : Grafana doit être installé avant Loki."
    echo "Exécuter scripts/install-monitoring.sh."
    exit 1
fi

echo "Configuration des dépôts Helm..."

helm repo add grafana-community \
    https://grafana-community.github.io/helm-charts \
    --force-update

helm repo add grafana \
    https://grafana.github.io/helm-charts \
    --force-update

helm repo update grafana-community grafana

echo "Installation de Loki..."

helm upgrade --install loki grafana-community/loki \
    --version "$LOKI_VERSION" \
    --namespace "$NAMESPACE" \
    --values "$SCRIPT_DIR/values-loki.yaml" \
    --wait \
    --timeout 5m

echo "Installation de Grafana Alloy..."

helm upgrade --install alloy grafana/alloy \
    --version "$ALLOY_VERSION" \
    --namespace "$NAMESPACE" \
    --values "$SCRIPT_DIR/values-alloy.yaml" \
    --wait \
    --timeout 5m

echo "=== Centralisation des logs installée ==="

kubectl get pods -n "$NAMESPACE"

echo "La source de données Loki est provisionnée par Flux."
echo "Accès Grafana :"
echo "kubectl -n monitoring port-forward svc/monitoring-grafana 3000:80"
echo "Consulter les logs dans Grafana Explore."
