# Step by Step

[Version française](README.md) (reference documentation)

Task management app designed for ADHD profiles (each task is split into small steps),
**continuously delivered on Kubernetes**: GitHub Actions CI, images on GHCR, GitOps deployment with Flux,
zero-downtime updates, autoscaling and Prometheus / Grafana monitoring.

Team: Géraldine, Morgan, Zhou — *Advanced DevOps* course (ESGI M1, November 2026).

## Quick start

```bash
./scripts/setup-cluster.sh                       # kind cluster (Kubernetes 1.37) + metrics-server
export GRAFANA_ADMIN_PASSWORD='<password>'
./scripts/install-monitoring.sh                  # Prometheus + Grafana
./scripts/install-ingress.sh                     # ingress-nginx
kubectl apply -f k8s/00-namespace.yaml
kubectl -n step-by-step create secret generic db-credentials \
  --from-literal=username=stepbystep --from-literal=password='<password>'
./scripts/install-flux.sh                        # Flux then deploys k8s/ from main
```

The application is served on <http://localhost/>.

## Delivery chain

```
PR → CI checks → merge → CI on main → image → Trivy → GHCR → image tag committed in k8s/ → Flux → rolling update
```

See the French [README](README.md) and the [project report](docs/dossier-projet.md) for details.
