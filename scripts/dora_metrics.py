#!/usr/bin/env python3
"""Weekly DORA metrics from Jenkins Pipeline and Git (stdlib only)."""
import argparse
import base64
import csv
import json
import os
import re
import statistics
import subprocess
import sys
import urllib.error
import urllib.parse
import urllib.request
from collections import defaultdict
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
COLUMNS = ['week', 'deployment_frequency', 'lead_time_hours',
           'change_lead_time_hours', 'change_failure_rate']
SUCCESS = re.compile(r'OK\s+production serves\s+step-by-step:([^\s]+)')
ROLLBACK = re.compile(r'KO\s+[^\n]*rollback', re.IGNORECASE)
VERSION_SHA = re.compile(r'-([0-9a-f]{7,40})$')


def api(base, path, user, token, raw=False):
    req = urllib.request.Request(base.rstrip('/') + '/' + path.lstrip('/'))
    if user and token:
        encoded = base64.b64encode(f'{user}:{token}'.encode()).decode()
        req.add_header('Authorization', f'Basic {encoded}')
    with urllib.request.urlopen(req, timeout=30) as response:
        data = response.read().decode('utf-8', errors='replace')
    return data if raw else json.loads(data)


def git(*args):
    return subprocess.run(['git', *args], cwd=ROOT, check=True,
                          capture_output=True, text=True).stdout.strip()


def commit_time(sha):
    try:
        return int(git('show', '-s', '--format=%ct', sha))
    except (subprocess.CalledProcessError, ValueError):
        return None


def main_history():
    """First-parent commits: merge/landing timestamps approximated by commit time."""
    try:
        rows = git('log', 'origin/main', '--first-parent', '--format=%H|%ct').splitlines()
    except (subprocess.CalledProcessError, FileNotFoundError):
        try:
            rows = git('log', 'main', '--first-parent', '--format=%H|%ct').splitlines()
        except (subprocess.CalledProcessError, FileNotFoundError):
            rows = []
    return [(sha, int(ts)) for sha, ts in (r.split('|') for r in rows if '|' in r)]


def landing_time(sha, history):
    """First-parent commit timestamp; NOT a guaranteed GitHub PR merge event time."""
    for full_sha, ts in history:
        if full_sha.startswith(sha):
            return ts
    return None


def stage_time(stages):
    for stage in stages:
        if stage.get('name', '').lower() == 'deploy production':
            start = stage.get('startTimeMillis')
            duration = stage.get('durationMillis')
            if start is not None and duration is not None:
                return (int(start) + int(duration)) / 1000
    return None


def get_builds(args):
    if args.builds_file:
        builds = json.loads(Path(args.builds_file).read_text(encoding='utf-8'))['builds']
        return builds, True
    parts = [urllib.parse.quote(p, safe='') for p in args.job.strip('/').split('/')]
    path = '/'.join('job/' + p for p in parts)
    builds = api(args.url, path + '/api/json?tree=builds[number,url,result,timestamp,duration,displayName]',
                 args.user, args.token)['builds']
    return builds, False


def collect(args):
    builds, offline = get_builds(args)
    history = main_history()
    weekly = defaultdict(lambda: {'count': 0, 'failed': 0, 'commit': [], 'merge': [], 'builds': 0})
    for build in builds:
        build_start = int(build['timestamp']) / 1000
        build_week = datetime.fromtimestamp(build_start, timezone.utc).strftime('%G-W%V')
        weekly[build_week]['builds'] += 1
        number = build['number']
        if offline:
            if number != args.console_build:
                continue
            console = Path(args.console_file).read_text(encoding='utf-8', errors='replace')
            deployed = None
        else:
            url = build.get('url')
            if not url:
                continue
            console = api(args.url, url.rstrip('/') + '/consoleText' if url.startswith('/') else
                          urllib.parse.urlsplit(url).path + '/consoleText', args.user, args.token, raw=True)
            try:
                stages = api(args.url, urllib.parse.urlsplit(url).path + '/wfapi/describe',
                             args.user, args.token).get('stages', [])
                deployed = stage_time(stages)
            except (urllib.error.HTTPError, KeyError, ValueError):
                deployed = None
        match = SUCCESS.search(console)
        rollback = bool(ROLLBACK.search(console))
        if not match and not rollback:
            continue
        # Without stage timing, build completion is only a documented approximation.
        event_time = deployed if deployed is not None else build_start + int(build.get('duration', 0)) / 1000
        week = datetime.fromtimestamp(event_time, timezone.utc).strftime('%G-W%V')
        row = weekly[week]
        row['count'] += 1
        if rollback:
            row['failed'] += 1
        if not match:
            continue
        sha_match = VERSION_SHA.search(match.group(1))
        if not sha_match:
            continue
        sha = sha_match.group(1)
        authored = commit_time(sha)
        landed = landing_time(sha, history)
        if authored is not None and event_time >= authored:
            row['commit'].append((event_time - authored) / 3600)
        if landed is not None and event_time >= landed:
            row['merge'].append((event_time - landed) / 3600)
    return weekly


def write_csv(weekly, output):
    output.parent.mkdir(parents=True, exist_ok=True)
    with output.open('w', newline='', encoding='utf-8') as stream:
        writer = csv.writer(stream)
        writer.writerow(COLUMNS)
        for week, row in sorted(weekly.items()):
            def median(key):
                return round(statistics.median(row[key]), 2) if row[key] else ''
            writer.writerow([week, row['count'], median('commit'), median('merge'),
                             round(100 * row['failed'] / row['count'], 2) if row['count'] else ''])
    print(f'CSV écrit : {output} ({len(weekly)} semaines)')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--url', default=os.getenv('JENKINS_URL', 'http://localhost:8080/'))
    parser.add_argument('--user', default=os.getenv('JENKINS_USER', ''))
    parser.add_argument('--token', default=os.getenv('JENKINS_TOKEN', ''))
    parser.add_argument('--job', default=os.getenv('JENKINS_JOB', 'step-by-step/main'))
    parser.add_argument('--builds-file')
    parser.add_argument('--console-file')
    parser.add_argument('--console-build', type=int, default=20)
    parser.add_argument('--output', default=str(ROOT / 'observability/dora/dora-metrics.csv'))
    args = parser.parse_args()
    if bool(args.builds_file) != bool(args.console_file):
        parser.error('--builds-file et --console-file doivent être utilisés ensemble')
    if not args.builds_file and not (args.user and args.token):
        parser.error('JENKINS_USER et JENKINS_TOKEN requis en mode réel')
    write_csv(collect(args), Path(args.output))
    if args.builds_file:
        print('Mode test : seules les consoles fournies sont analysées ; autres builds non vérifiés.')


if __name__ == '__main__':
    try:
        main()
    except (OSError, ValueError, subprocess.CalledProcessError, urllib.error.URLError) as exc:
        print(f'Erreur DORA : {exc}', file=sys.stderr)
        sys.exit(1)