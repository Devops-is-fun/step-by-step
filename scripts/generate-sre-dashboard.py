
import json
from pathlib import Path

output = Path("observability/grafana/dashboards/sre-golden-signals.json")
output.parent.mkdir(parents=True, exist_ok=True)

datasource = {"type": "prometheus", "uid": "prometheus"}

http_labels = 'namespace="step-by-step",service="app"'
container_labels = 'namespace="step-by-step",container="app",pod=~"step-by-step-.*"'


def panel(identifier, title, query, x, y, unit):
    return {
        "id": identifier,
        "title": title,
        "type": "timeseries",
        "datasource": datasource,
        "gridPos": {"h": 9, "w": 12, "x": x, "y": y},
        "fieldConfig": {
            "defaults": {"unit": unit},
            "overrides": []
        },
        "options": {
            "legend": {
                "displayMode": "list",
                "placement": "bottom"
            },
            "tooltip": {"mode": "single"}
        },
        "targets": [{
            "refId": "A",
            "expr": query,
            "datasource": datasource
        }]
    }


http_count = f"http_server_requests_seconds_count{{{http_labels}}}"
http_bucket = f"http_server_requests_seconds_bucket{{{http_labels}}}"

panels = [
    panel(
        1, "Latence p95",
        f"histogram_quantile(0.95, sum by (le) (rate({http_bucket}[$__rate_interval])))",
        0, 0, "s"
    ),
    panel(
        2, "Trafic HTTP",
        f"sum(rate({http_count}[$__rate_interval]))",
        12, 0, "reqps"
    ),
    panel(
        3, "Erreurs HTTP 5xx (%)",
        f"100 * ( sum(rate(http_server_requests_seconds_count{{ namespace=\"step-by-step\", service=\"app\", status=~\"5..\" }}[$__rate_interval])) or vector(0) ) / clamp_min( sum(rate(http_server_requests_seconds_count{{ namespace=\"step-by-step\", service=\"app\" }}[$__rate_interval])), 1e-9 )",
        0, 9, "percent"
    ),
    panel(
        4, "CPU backend",
        f"sum(rate(container_cpu_usage_seconds_total{{{container_labels}}}[$__rate_interval]))",
        12, 9, "cores"
    ),
    panel(
        5, "Mémoire backend",
        f"sum(container_memory_working_set_bytes{{{container_labels}}})",
        0, 18, "bytes"
    ),
    panel(
        6, "Disponibilité du scrape",
        f"max(up{{{http_labels}}})",
        12, 18, "short"
    )
]

dashboard = {
    "uid": "step-by-step-sre",
    "title": "Step by Step - SRE Golden Signals",
    "tags": ["step-by-step", "sre", "golden-signals"],
    "timezone": "browser",
    "schemaVersion": 39,
    "version": 1,
    "refresh": "15s",
    "time": {"from": "now-1h", "to": "now"},
    "editable": False,
    "panels": panels
}

output.write_text(
    json.dumps(dashboard, indent=2, ensure_ascii=False) + "\n",
    encoding="utf-8"
)

print(f"Dashboard généré : {output}")
