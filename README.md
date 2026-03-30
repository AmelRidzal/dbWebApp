# dbWebApp

A Spring Boot web app for managing customer records, built for a small repair shop.

## What it does

- Add, view, edit, and delete customer entries
- Automatically backs up changes to a secondary (remote) database every 15 minutes
- If the remote database is offline, changes are saved locally and synced when it comes back

## Stack

- Java / Spring Boot
- Spring Data JPA + HikariCP
- Thymeleaf (frontend)
- PostgreSQL (secondary/backup via Neon)

## Setup

Edit `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.primary.jdbc-url=...
spring.datasource.primary.username=...
spring.datasource.primary.password=...

spring.datasource.secondary.jdbc-url=...
spring.datasource.secondary.username=...
spring.datasource.secondary.password=...
```

Then run:

```bash
./mvnw spring-boot:run
```

## Pages

| Route | Description |
|---|---|
| `/` | Home |
| `/prijemAparata` | New customer / device intake |
| `/dbManager` | Run raw SQL queries |
| `/dbSettings` | Update database connection settings |
