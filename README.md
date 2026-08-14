# JAVA_JOB

Project migrated from NestJS to Spring Boot, with the React frontend kept in a separate folder.

## Structure

- `backend/`: Spring Boot API, MongoDB, PostgreSQL/Flyway, JWT, MailHog and Docker Compose.
- `frontend/`: React/Vite frontend.

## Run the full stack

```bash
cd backend
docker compose up -d --build
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8081/api/v1
- MailHog: http://localhost:8025
