
#!/usr/bin/env python3
"""Expose le CSV DORA au format Prometheus."""
import csv
import os
from datetime import datetime, timezone
from http.server import BaseHTTPRequestHandler, HTTPServer
from pathlib import Path

CSV_PATH = Path(os.getenv("DORA_CSV", "/data/dora-metrics.csv"))
PORT = int(os.getenv("DORA_PORT", "9105"))

METRICS = {
    "deployment_frequency": "dora_deployment_frequency",
    "lead_time_hours": "dora_lead_time_hours",
    "change_lead_time_hours": "dora_change_lead_time_hours",
    "change_failure_rate": "dora_change_failure_rate",
    "time_to_restore_hours": "dora_time_to_restore_hours",
}


def render_metrics():
    lines = []
    for name in METRICS.values():
        lines.append(f"# TYPE {name} gauge")

    if CSV_PATH.exists():
        with CSV_PATH.open(encoding="utf-8-sig", newline="") as file:
            for row in csv.DictReader(file):
                week = row["week"]
                try:
                    year, number = week.split("-W")
                    datetime.fromisocalendar(int(year), int(number), 1)
                except (ValueError, KeyError):
                    continue

                for column, metric in METRICS.items():
                    value = row.get(column, "")
                    if value:
                        try:
                            numeric = float(value)
                        except ValueError:
                            continue
                        lines.append(
                            f'{metric}{{week="{week}"}} {numeric}'
                        )

    return "\n".join(lines) + "\n"


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path != "/metrics":
            self.send_error(404)
            return

        body = render_metrics().encode("utf-8")
        self.send_response(200)
        self.send_header(
            "Content-Type", "text/plain; version=0.0.4; charset=utf-8"
        )
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)


if __name__ == "__main__":
    print(f"DORA exporter : port {PORT}", flush=True)
    HTTPServer(("0.0.0.0", PORT), Handler).serve_forever()
