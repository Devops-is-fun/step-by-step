#!/usr/bin/env bash
# Looks for secrets in what Git is about to version (the index), before each commit.
# Used by the pre-commit hook (.githooks/pre-commit); can also be run by hand.
set -euo pipefail

# Searches the index; prints the matching lines. Returns 0 if found, 1 if not; stops on a git error.
scan() {
  local status=0
  git grep --cached -nI "$@" || status=$?
  if [ "$status" -gt 1 ]; then
    echo "KO  scan failed (git grep exit code $status)"
    exit 2
  fi
  return "$status"
}

found=0

# 1. Well-known secret formats, in every file:
#    private key, SonarQube token, Jenkins API token, GitHub token
FORMATS='-----BEGIN [A-Z ]*PRIVATE KEY-----$|sq[apu]_[0-9a-f]{40}|\b11[0-9a-f]{32}\b|gh[pousr]_[A-Za-z0-9]{36}|github_pat_[A-Za-z0-9_]{20,}'
if scan -E -e "$FORMATS"; then
  found=1
fi

# 2. A long value assigned to a secret-like name, in configuration and code
#    (Markdown excluded: its prose explains secrets without containing any)
ASSIGN='(password|passwd|secret|token|api[_-]?key)[[:space:]]*[:=][[:space:]]*[^<{$?[:space:]]{8,}'
if scan -iE -e "$ASSIGN" -- . ':(exclude)*.md'; then
  found=1
fi

if [ "$found" -eq 0 ]; then
  echo "OK  no secret found in the files to commit"
else
  echo "KO  possible secret(s) above: remove the value, or unstage the file (git restore --staged <file>)"
  exit 1
fi