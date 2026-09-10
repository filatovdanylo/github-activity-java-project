# GitHub Activity CLI

A Spring Boot service that fetches a GitHub user's recent public activity (pushes, issues, stars, pull requests, etc.) from the GitHub Events API, normalizes it into a simple domain model, and exposes it over a REST endpoint. Results are cached in Redis to avoid hammering the GitHub API for repeat lookups.

Built with a hexagonal (ports & adapters) architecture: the domain and use case are isolated from GitHub, the database, and the web layer behind clearly defined ports.

## Features

- Fetch a user's recent GitHub events via `GET /api/users/{username}/activity`
- Maps raw GitHub event types (`PushEvent`, `IssuesEvent`, `WatchEvent`, `PullRequestEvent`, `CreateEvent`, `ForkEvent`, ...) to a clean `ActivityType` enum, falling back to `OTHER` for anything unrecognized
- Redis-backed caching per username (5-minute TTL) to reduce GitHub API calls
- Consistent JSON error responses for missing users, rate limiting, and upstream GitHub failures
- MySQL/JPA persistence layer for activity history (present in the codebase but **currently disabled** — see [Known limitations](#known-limitations))

## Architecture

```
src/main/java/pj/
├── ApplicationMain.java          # Spring Boot entry point
├── domain/model/                 # Activity, ActivityType — framework-free domain model
├── app/
│   ├── port/in/                  # LoadUserActivityUseCase (inbound port)
│   ├── port/out/                 # FetchGithubEventsPort, ActivityRepositoryPort (outbound ports)
│   └── service/                  # GitHubActivityService — use case implementation
├── adapter/
│   ├── in/rest/                  # REST controller, DTOs, exception handler
│   ├── in/cli/                   # CLI command (not currently wired into a runner)
│   ├── out/github/               # GitHub API client, DTOs, console formatter
│   └── out/persistence/          # JPA entity, repository, mapper (currently unused)
├── config/                       # GitHub RestClient, Redis cache, and properties config
└── exceptions/                   # Domain-specific exceptions (404, rate limit, generic API error)
```

## Requirements

- Java 17+
- A GitHub [personal access token](https://github.com/settings/tokens) (used for authenticated GitHub API calls)
- Redis (for response caching)
- MySQL (the app connects to it on startup even though persistence is currently disabled — see below)

## Configuration

Configuration lives in `src/main/resources/application.yml` and pulls the following from the environment:

| Variable        | Purpose                                   |
|-----------------|--------------------------------------------|
| `GITHUB_TOKEN`  | Bearer token used to call the GitHub API   |
| `PASSWORD`      | Password for the local MySQL user (`root`) |

Other defaults set in `application.yml`:

- GitHub API base URL: `https://api.github.com`
- MySQL: `jdbc:mysql://localhost:3306/gamification_app`
- Redis: `localhost:6379`
- Server port: `8080`

Adjust these as needed for your environment.

## Running locally

1. Start MySQL and Redis (e.g. via Docker):
   ```bash
   docker run -d --name redis -p 6379:6379 redis
   docker run -d --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=<your-password> -e MYSQL_DATABASE=gamification_app mysql:8
   ```
2. Export the required environment variables:
   ```bash
   export GITHUB_TOKEN=ghp_yourtokenhere
   export PASSWORD=<your-mysql-password>
   ```
3. Run the app:
   ```bash
   ./gradlew bootRun
   ```
4. Query a user's activity:
   ```bash
   curl http://localhost:8080/api/users/octocat/activity
   ```

## Example response

```json
{
  "username": "octocat",
  "activityCount": 2,
  "activities": [
    {
      "type": "PUSH",
      "repositoryName": "octocat/Hello-World",
      "occurredAt": "2026-09-01T12:34:56Z"
    },
    {
      "type": "STARRED",
      "repositoryName": "octocat/Spoon-Knife",
      "occurredAt": "2026-08-30T09:12:00Z"
    }
  ]
}
```

On failure, the API returns a structured error instead:

```json
{
  "status": 404,
  "error": "User not found",
  "message": "User not found: some-nonexistent-user",
  "timestamp": "2026-09-10T10:00:00"
}
```

| Scenario                    | HTTP status |
|-----------------------------|-------------|
| User does not exist         | 404         |
| GitHub API rate limit hit   | 429         |
| Other GitHub/API errors     | 500         |

## Testing

Run the test suite with:

```bash
./gradlew test
```

Tests cover the REST controller, the GitHub API client, the activity service, and the domain model.

## Known limitations

- **Database persistence is disabled.** `GitHubActivityService` fetches events straight from GitHub without writing to the `activity_history` table — the `saveAll` call is commented out. The JPA entity, repository, and mapper are wired up but not currently used, so a running MySQL instance is required for startup but no data is persisted yet.
- **The CLI command is not active.** `GitHubActivityCommand` exists but isn't invoked by a `CommandLineRunner` or similar, so activity is currently only reachable through the REST endpoint.
- No pagination or filtering is applied to GitHub's event feed — it returns whatever GitHub's `/users/{username}/events` endpoint gives back (GitHub limits this to the last 90 days / 300 events).

## Tech stack

- Java 17, Spring Boot 3.3
- Spring Web, Spring Data JPA, Spring Data Redis, Spring Cache
- MySQL (via `mysql-connector-j`)
- Lombok
- JUnit 5 + Mockito
