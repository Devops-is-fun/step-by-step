
#!/usr/bin/env bash
set -euo pipefail

INGRESS_VERSION="controller-v1.13.3"
INGRESS_MANIFEST="https://raw.githubusercontent.com/kubernetes/ingress-nginx/${INGRESS_VERSION}/deploy/static/provider/kind/deploy.yaml"

echo "=== Installation ingress-nginx sur kind ==="

# Vérifier que kubectl est disponible
if ! command -v kubectl >/dev/null 2>&1; then
    echo "ERREUR : kubectl est introuvable."
    exit 1
fi

# Vérifier le contexte Kubernetes
CONTEXT="$(kubectl config current-context)"

if [[ "$CONTEXT" != "kind-step-by-step" ]]; then
    echo "ERREUR : contexte Kubernetes inattendu : $CONTEXT"
    echo "Contexte attendu : kind-step-by-step"
    exit 1
fi

# Installer ingress-nginx
echo "Installation du contrôleur..."
kubectl apply -f "$INGRESS_MANIFEST"

# Attendre que le contrôleur soit prêt
echo "Attente du contrôleur..."
kubectl -n ingress-nginx rollout status \
    deployment/ingress-nginx-controller \
    --timeout=180s

echo "=== Installation terminée ==="
kubectl get pods -n ingress-nginx

echo "Test HTTP : curl -i http://localhost/"
