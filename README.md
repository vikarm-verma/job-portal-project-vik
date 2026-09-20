# Job Portal

This repository contains the project structure for a full-stack job portal site built with:
- React for frontend
- Spring Boot for backend
- MySQL for database
- REST APIs
- JWT authentication

## Structure

- frontend/ - React application
- backend/ - Spring Boot application
- database/ - SQL schema and migrations
- docs/ - API and architecture documentation

## Local Database Configuration

The backend connects to MySQL using the following environment variables:

| Variable | Development default | Purpose |
| --- | --- | --- |
| `DB_HOST` | `localhost` | MySQL hostname |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `jobportal_db` | Database name |
| `DB_USERNAME` | `jobportal` | Application database user |
| `DB_PASSWORD` | `jobportal123` | Application database password |
| `JWT_SECRET` | Development-only fallback | JWT signing secret; use a strong secret outside development |
| `MYSQL_ROOT_PASSWORD` | Development-only fallback | Root password used only by Docker MySQL |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://127.0.0.1:5173` | Allowed frontend origins |

The local defaults are intended for development only. Set `DB_PASSWORD`, `JWT_SECRET`, and
`MYSQL_ROOT_PASSWORD` through the environment or a local, untracked `.env` file for any shared
or production-like environment. Never commit production credentials.

### Start MySQL

From the `job-portal` directory:

```bash
docker compose up -d mysql
```

The container is named `jobportal-mysql`, publishes MySQL on `localhost:3306`, uses the
`jobportal_db` database by default, and persists data in the `mysql_data` Docker volume.
The `database/schema/init.sql` script initializes a fresh volume with the tables used by the
JPA entities. It runs against the database selected by `MYSQL_DATABASE`; for manual execution,
select the target database first.

### Start the backend

With the default local Docker credentials, the backend can use `application.properties` as-is.
To override configuration, set the environment variables before starting Spring Boot. For
example, the application connects using:

```text
jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
```

For development, Hibernate uses `spring.jpa.hibernate.ddl-auto=update`, which preserves existing
tables and data. For production, override it with `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` and
apply reviewed database migrations instead of allowing Hibernate to alter the schema.
