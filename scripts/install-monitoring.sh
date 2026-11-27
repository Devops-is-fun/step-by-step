
#!/usr/bin/env bash
set -euo pipefail

CHART_VERSION="94.1.0"
RELEASE_NAME="monitoring"
NAMESPACE="monitoring"

echo "=== Installation Prometheus + Grafana ==="

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

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VALUES_FILE="${SCRIPT_DIR}/values-monitoring.yaml"

echo "Configuration du dépôt Helm..."
helm repo add prometheus-community \
    https://prometheus-community.github.io/helm-charts \
    --force-update

helm repo update prometheus-community

if ! kubectl get namespace "$NAMESPACE" >/dev/null 2>&1; then
    kubectl create namespace "$NAMESPACE"
fi

# Créer le Secret Grafana s'il n'existe pas
if ! kubectl -n "$NAMESPACE" get secret monitoring-grafana-admin \
    >/dev/null 2>&1; then

    echo "Création du Secret administrateur Grafana..."

    if [[ -z "${GRAFANA_ADMIN_PASSWORD:-}" ]]; then
        echo "ERREUR : définis GRAFANA_ADMIN_PASSWORD avant l'installation."
        exit 1
    fi

    kubectl -n "$NAMESPACE" create secret generic monitoring-grafana-admin \
        --from-literal=admin-user=admin \
        --from-literal="admin-password=${GRAFANA_ADMIN_PASSWORD}"
fi

echo "Installation de kube-prometheus-stack..."
helm upgrade --install "$RELEASE_NAME" \
    prometheus-community/kube-prometheus-stack \
    --version "$CHART_VERSION" \
    --namespace "$NAMESPACE" \
    --create-namespace \
    --values "$VALUES_FILE" \
    --wait \
    --timeout 10m

echo "=== Monitoring installé ==="
kubectl get pods -n "$NAMESPACE"

echo "Accès Grafana :"
echo "kubectl -n monitoring port-forward svc/monitoring-grafana 3000:80"
