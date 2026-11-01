#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
readme=README.md
[[ -f "$readme" ]] || { echo 'ERREUR : README.md absent' >&2; exit 1; }
expected=(
  '## 1. Présentation'
  '## 2. Prérequis'
  '## 3. Démarrage'
  '## 4. Ordre des étapes et credentials'
  '## 5. Ports'
  '## 6. Démonstration de la chaîne'
  '## 7. Tests et couverture'
  '## 8. Règles métier'
  '## 9. Dépannage'
  '## 10. Liens et livrables'
)
last=0
for title in "${expected[@]}"; do
  line=$(grep -nFx "$title" "$readme" | head -1 | cut -d: -f1 || true)
  if [[ -z "$line" || "$line" -le "$last" ]]; then
    echo "ERREUR : section manquante ou mal ordonnée : $title" >&2
    exit 1
  fi
  last=$line
done
# Ports requis : leur publication est répartie entre le compose racine et ceux des environnements.
compose_files=(docker-compose.yml environments/staging/docker-compose.yml environments/production/docker-compose.yml)
for file in "${compose_files[@]}"; do
  [[ -f "$file" ]] || { echo "ERREUR : Compose absent : $file" >&2; exit 1; }
done
for port in 8080 9000 8081 8082 8088 8083 8084 9090 3000; do
  grep -Eq "\b${port}\b" "$readme" || { echo "ERREUR : port $port absent du README" >&2; exit 1; }
  if ! grep -Eq "(^|[^0-9])${port}:[0-9]+([^0-9]|$)" "${compose_files[@]}"; then
    echo "ERREUR : port hôte $port introuvable dans les fichiers Compose" >&2
    exit 1
  fi
done
printf 'OK : 10 sections dans le bon ordre et 9 ports documentés dans Compose.\n'
