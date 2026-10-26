---
name: specification-en
description: Iterative specification creation for the CI/CD course project, with requirement traceability from the PDF statement (English)
---

# Skill: Specification (EN) — CI/CD Project

> **Note :** this is the English variant of `specification-fr`. The primary skill for this project is
> `specification-fr` (the course and the statement are in French). Use this variant only if English
> documentation is explicitly required.

## Project context (read first)

| Item | Value |
|------|-------|
| Nature | CI/CD course assignment (ESGI Master 1, "Information Architecture of Software") |
| Statement | `docs/Projet_CICD_Jenkins.pdf` — **source of truth** |
| Working mode | **Solo.** The statement's "in a group / trinôme" requirement is **ignored** |
| Web application | **Java / Spring Boot**, **Maven** build, **JUnit 5** tests |
| Tooling | Docker Compose: **Jenkins** (controller + agents), **SonarQube**, **Nexus** |
| Environments | **staging** and **production**, as two local **Docker Compose** stacks |
| Cross-cutting rules | Builds run on **agents**, never on the controller · Secrets **only** in Jenkins credentials |

## What I do

I turn the PDF statement into a project specification: the web application to build, the CI/CD
chain architecture, and the expected deliverables.

Two guarantees:

1. **No statement requirement is forgotten** — each one gets a traceable `EXG-*` identifier.
2. **No decision is taken on the user's behalf** — choices come from questions asked **in the chat**,
   and each answer is recorded in the decision log.

## When to use me

At the very beginning of the project, before any code, to turn the PDF statement into an actionable
specification.

## Workflow

### Step 0 — Read the statement (mandatory)

The PDF cannot be read directly by models, so extract it first.

```bash
pdftotext -layout docs/Projet_CICD_Jenkins.pdf - | head -100
```

If `pdftotext` is missing: `sudo apt install poppler-utils`.

Then split the statement into numbered requirements:

| Prefix | Meaning |
|--------|---------|
| `EXG-COM-*` | common part (mandatory) |
| `EXG-N1a-*` | N1 module "Quality and fast feedback" |
| `EXG-N1b-*` | N1 module "Automated releases" |
| `EXG-N2a-*` | N2 module "Progressive blue/green deployment" |
| `EXG-N2b-*` | N2 module "Ephemeral environments" |
| `EXG-N3a-*` | N3 module "Environments as Code" |
| `EXG-N3b-*` | N3 module "Observability and DORA" |
| `EXG-REND-*` | expected deliverables (README, technical report, demo) |

These identifiers are **stable**: they are reused verbatim in the stories and the plan.

### Step 1 — Initiation

- Create `docs/specification/` if missing
- Create `specification.md` with empty sections and the `## Exigences du TP` traceability matrix
  **already filled in** by Step 0
- Nothing is invented: every uncertainty becomes `[TO_CLARIFY: ...]`

### Step 2 — Scoping questions (in the chat)

At most **5 questions per batch**, asked in the chat:

```text
Question 1 — <topic>
  a) <option A>  → <consequence>       (⭐ recommended: <reason>)
  b) <option B>  → <consequence>
Reply with: 1a, 2b, 3c...
```

Topics for this project: module scope, Spring Boot app feature, Git remote, automated versioning,
artifact format, build notification channel.

Answers are copied into the `## Decision log` section.

### Step 3 — Write the specification

- Replace `[TO_CLARIFY: ...]` with the answers
- Document every architecture choice **with a reason** (this is what the technical report grades)
- Keep `[TO_VALIDATE: ...]` for what can only be settled later

### Step 4 — Iterate

- Re-read: are any `[TO_CLARIFY]` / `[TO_VALIDATE]` left?
- If yes → next batch of chat questions
- If no → **specification complete**

### Step 5 — Move to stories

- Announce completion
- Next command: `/ story-en docs/specification/specification.md`

## Sections of `specification.md`

1. Context and objectives
2. Chosen scope (chosen modules **and** dropped modules, with reasons)
3. Traceability matrix — `EXG-*` requirements
4. Web application specification
5. CI/CD chain architecture (components, flow, environments)
6. Detailed technical requirements (declarative Jenkinsfile, tagged Shared Library, idempotent
   deploy script, secrets via credentials)
7. Secrets inventory
8. Expected deliverables (README, technical report, DORA metrics, demo)
9. Self-assessment against the grading scale
10. Decision log
11. Open points

## ✅ Before moving on — verification checklist

- [ ] `pdftotext` returned the statement text and **every** requirement was read
- [ ] The traceability matrix has one row per requirement, with an `EXG-*` id
- [ ] `specification.md` contains **no** `[TO_CLARIFY: ...]` marker
- [ ] Module scope is explicit, and dropped modules are justified
- [ ] The secrets table is filled and **no secret is in clear text**
- [ ] The decision log records every answer given in the chat
- [ ] Staging and production addresses are distinct and recorded