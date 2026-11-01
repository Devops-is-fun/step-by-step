# REST API

The application exposes a small JSON API over the business rules of `TaskService`. Business refusals
(`REG-01`, `REG-02`) come back as `400` with a message written for the user, in French.

| Method | Path | Body | Answer |
|--------|------|------|--------|
| `GET` | `/api/tasks` | — | `200`, all tasks with their steps |
| `GET` | `/api/tasks/{id}` | — | `200`, or `404` |
| `POST` | `/api/tasks` | `{"title", "estimatedMinutes", "plannedFor"?}` | `201`; `400` if the day already has an unfinished task (`REG-02`) |
| `POST` | `/api/tasks/{id}/steps` | `{"label", "estimatedMinutes"}` | `201` with the task; `400` if the step is outside 1-30 minutes (`REG-01`) |
| `PUT` | `/api/tasks/{id}/planned-for` | `{"plannedFor": "2026-11-02"}` or `null` | `200`; `400` if the day is taken (`REG-02`) |
| `GET` | `/actuator/health` | — | `{"status":"UP", …}`, used by the pipeline and the blue/green switch |

Errors are always `{"error": "<message>"}`.

## Try it (production, through the proxy)

```bash
curl -s -X POST localhost:8088/api/tasks -H 'Content-Type: application/json' \
  -d '{"title":"Appeler le syndic","estimatedMinutes":25,"plannedFor":"2026-11-02"}'
curl -s -X POST localhost:8088/api/tasks/1/steps -H 'Content-Type: application/json' \
  -d '{"label":"Trouver le numéro","estimatedMinutes":5}'
curl -s -X POST localhost:8088/api/tasks/1/steps -H 'Content-Type: application/json' \
  -d '{"label":"Tout faire d un coup","estimatedMinutes":45}'
# {"error":"Cette sous-étape dure 45 minutes : c'est plus de 30 minutes. Découpe-la en plusieurs sous-étapes plus courtes."}
curl -s localhost:8088/api/tasks
```

Staging answers on `localhost:8082` with the same paths. Use the `id` returned by the first call.

## Tests

| Test | Kind |
|------|------|
| `TaskControllerTest` | unit (`@WebMvcTest`, service mocked): routes, JSON, error mapping |
| `TaskServiceTest` | unit: business rules and reading |
| `TaskRepositoryIT` | integration (PostgreSQL): persistence and the queries of `REG-02` |

The API has no authentication: it is reachable only from the machine (`127.0.0.1`), like the tooling.
