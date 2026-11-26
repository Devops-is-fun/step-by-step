
#!/usr/bin/env bash
set -euo pipefail

CLUSTER_NAME="step-by-step"
K8S_IMAGE="kindest/node:v1.31.4"
METRICS_VERSION="v0.7.2"

echo "=== Step by Step : installation du cluster ==="

# Vérifier les outils nécessaires
for tool in docker kind kubectl; do
    if ! command -v "$tool" >/dev/null 2>&1; then
        echo "ERREUR : $tool n'est pas installé."
        exit 1
    fi
done

# Vérifier que Docker fonctionne
if ! docker info >/dev/null 2>&1; then
    echo "ERREUR : Docker n'est pas démarré."
    exit 1
fi

# Créer le cluster uniquement s'il n'existe pas
if kind get clusters | grep -Fxq "$CLUSTER_NAME"; then
    echo "Le cluster $CLUSTER_NAME existe déjà."
else
    echo "Création du cluster kind..."
    kind create cluster \
        --name "$CLUSTER_NAME" \
        --image "$K8S_IMAGE" \
        --wait 180s
fi

# Utiliser explicitement notre cluster
kubectl config use-context "kind-$CLUSTER_NAME"

echo "Vérification du cluster..."
kubectl wait --for=condition=Ready node --all --timeout=180s

# Installer metrics-server
echo "Installation de metrics-server..."
kubectl apply -f \
    "https://github.com/kubernetes-sigs/metrics-server/releases/download/${METRICS_VERSION}/components.yaml"

# Ajouter l'option TLS nécessaire sur kind
# Attention : cette option est réservée au développement local.
echo "Configuration de metrics-server pour kind..."

# Ajouter l'option TLS si elle n'est pas déjà présente
if ! kubectl -n kube-system get deployment metrics-server \
    -o jsonpath='{.spec.template.spec.containers[0].args}' |
    grep -Fq -- '--kubelet-insecure-tls'; then

    kubectl -n kube-system patch deployment metrics-server \
        --type=json \
        -p='[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]'
fi

# Attendre que le déploiement soit prêt
echo "Attente du démarrage de metrics-server..."
kubectl -n kube-system rollout status \
    deployment/metrics-server --timeout=180s

echo "Installation terminée !"
echo "Les métriques peuvent prendre quelques secondes à apparaître."
echo "Test : kubectl top nodes"
