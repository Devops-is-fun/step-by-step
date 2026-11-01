
#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
CSV_FILE="${1:-$ROOT/observability/dora/dora-metrics.csv}"
BUILDS_FILE="${2:-}"

python - "$CSV_FILE" "$BUILDS_FILE" <<'PY'
import csv
import datetime
import json
import pathlib
import re
import sys

csv_path = pathlib.Path(sys.argv[1])
builds_path = pathlib.Path(sys.argv[2]) if sys.argv[2] else None

expected = [
    "week",
    "deployment_frequency",
    "lead_time_hours",
    "change_lead_time_hours",
    "change_failure_rate",
    "time_to_restore_hours",
]

if not csv_path.is_file():
    sys.exit(f"KO : CSV introuvable : {csv_path}")

with csv_path.open(encoding="utf-8-sig", newline="") as f:
    reader = csv.DictReader(f)
    if reader.fieldnames != expected:
        sys.exit(f"KO : colonnes incorrectes : {reader.fieldnames}")
    rows = list(reader)

if not rows:
    sys.exit("KO : CSV vide")

weeks = set()

for row in rows:
    week = row["week"]

    if not re.fullmatch(r"\d{4}-W\d{2}", week):
        sys.exit(f"KO : format de semaine incorrect : {week}")

    year, number = week.split("-W")

    try:
        datetime.date.fromisocalendar(int(year), int(number), 1)
    except ValueError:
        sys.exit(f"KO : semaine invalide : {week}")

    if week in weeks:
        sys.exit(f"KO : semaine dupliquée : {week}")
    weeks.add(week)

    try:
        frequency = int(row["deployment_frequency"])
        if frequency < 0:
            raise ValueError()

        for column in ("lead_time_hours", "change_lead_time_hours", "time_to_restore_hours"):
            if row[column]:
                value = float(row[column])
                if not 0 <= value < float("inf"):
                    raise ValueError()

        rate = row["change_failure_rate"]
        if rate:
            value = float(rate)
            if not 0 <= value <= 100:
                raise ValueError()

        if frequency == 0 and (rate or row["lead_time_hours"] or row["change_lead_time_hours"]):
            raise ValueError()

    except ValueError:
        sys.exit(f"KO : valeurs invalides pour {week}")

if builds_path:
    if not builds_path.is_file():
        sys.exit(f"KO : historique Jenkins introuvable : {builds_path}")

    with builds_path.open(encoding="utf-8") as f:
        data = json.load(f)

    builds = data.get("builds", [])
    build_weeks = set()

    for build in builds:
        timestamp = build.get("timestamp")
        if timestamp is None:
            continue

        date = datetime.datetime.fromtimestamp(
            int(timestamp) / 1000, datetime.timezone.utc
        )
        year, number, _ = date.isocalendar()
        build_weeks.add(f"{year}-W{number:02d}")

    missing = sorted(build_weeks - weeks)

    if missing:
        sys.exit(
            "KO : semaines avec builds Jenkins absentes du CSV : "
            + ", ".join(missing)
        )

print(f"OK : CSV valide ({len(rows)} semaine(s))")

if builds_path:
    print("OK : toutes les semaines avec builds Jenkins sont représentées")
else:
    print("INFO : historique Jenkins non fourni, semaines manquantes non vérifiées")
PY
