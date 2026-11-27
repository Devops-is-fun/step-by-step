#!/usr/bin/env bash
# Installs Flux on the kind cluster and connects it to the GitHub repository (GitOps).
# Then every change of k8s/ on main is applied by Flux within about a minute.
set -euo pipefail

FLUX_VERSION="v2.10.0"
FLUX_MANIFEST="https://github.com/fluxcd/flux2/releases/download/${FLUX_VERSION}/install.yaml"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FLUX_CONFIG_DIR="${SCRIPT_DIR}/../flux"

echo "=== Installation de Flux ${FLUX_VERSION} ==="

if ! command -v kubectl >/dev/null 2>&1; then
    echo "ERREUR : kubectl est introuvable."
    exit 1
fi

CONTEXT="$(kubectl config current-context)"
if [[ "$CONTEXT" != "kind-step-by-step" ]]; then
    echo "ERREUR : contexte Kubernetes inattendu : $CONTEXT"
    echo "Contexte attendu : kind-step-by-step"
    exit 1
fi

# The application reads its database credentials from this Secret, never versioned (public repo)
if ! kubectl -n step-by-step get secret db-credentials >/dev/null 2>&1; then
    echo "ERREUR : le Secret step-by-step/db-credentials n'existe pas encore."
    echo "Le créer d'abord (voir README, section « Déployer sur Kubernetes »)."
    exit 1
fi

# Flux applies the whole k8s/ folder, which contains a ServiceMonitor: its CRD comes with the monitoring
# stack. Without it, the synchronisation fails ("no matches for kind ServiceMonitor").
if ! kubectl get crd servicemonitors.monitoring.coreos.com >/dev/null 2>&1; then
    echo "ERREUR : la CRD ServiceMonitor est absente."
    echo "Installer le monitoring d'abord : ./scripts/install-monitoring.sh"
    exit 1
fi

echo "Installation des contrôleurs Flux..."
kubectl apply -f "$FLUX_MANIFEST"

echo "Attente des contrôleurs..."
kubectl -n flux-system rollout status deployment/source-controller --timeout=180s
kubectl -n flux-system rollout status deployment/kustomize-controller --timeout=180s

echo "Connexion au dépôt GitHub..."
kubectl apply -f "$FLUX_CONFIG_DIR"

echo "Attente de la première synchronisation..."
kubectl -n flux-system wait kustomization/step-by-step --for=condition=Ready --timeout=5m

echo "=== Flux installé ==="
kubectl -n flux-system get gitrepositories,kustomizations
echo "Suivi : kubectl -n flux-system get kustomizations -w"
