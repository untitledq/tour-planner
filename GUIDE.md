# Start Guide

This file explains how to run the project locally:

- frontend + backend
- frontend only with mock user

## Requirements

For the full project you need:

- Java 21+
- Node.js + npm
- Docker / Docker Compose

## 1. Run Frontend Only with Mock User

This is the easiest option if you only want to test the UI.

### Enable Mock Auth

File:

- `frontend/src/app/core/config/api.config.ts`

Value:

```ts
export const USE_MOCK_AUTH = true;
```

### Start Frontend

```bash
cd frontend
npm install
npm start
```
or

```bash
cd frontend
ng serve
```

After that, the frontend is available at:

- `http://localhost:4200`

### Mock Login Credentials

- Username: `testuser`
- Password: `test1234`

### Important

If `USE_MOCK_AUTH = true` is set:

- login, registration, and logout run only inside the frontend
- the backend is not used for auth

## 2. Run Frontend and Backend Together

### Step 1: Disable Mock Auth

File:

- `frontend/src/app/core/config/api.config.ts`

Value:

```ts
export const USE_MOCK_AUTH = false;
```

### Step 2: Start Database

Inside the `backend` folder:

```bash
cd backend
docker compose up -d
```

Note:

- `compose.yaml` uses the required environment variables
- `init.sql` creates the tables

### Step 3: Start Backend

Inside the `backend` folder:

```bash
cd backend
./mvnw spring-boot:run
```

After that, the backend is available at:

- `http://localhost:8080`

### Step 4: Start Frontend

In a second terminal:

```bash
cd frontend
npm install
npm start
```

After that, the frontend is available at:

- `http://localhost:4200`

## Known Issues

### Backend does not start because of `JWT_SECRET`

If `JWT_SECRET` is not set, Spring Boot will not start.

### Backend does not start because of PostgreSQL password

If Docker Compose was started earlier with different or missing environment variables, Spring Boot may fail during automatic Docker Compose handling.

In that case, this often helps:

```bash
cd backend
docker compose down -v
```

and then:

```bash
docker compose up -d
```

## Summary

### Frontend Only

- `USE_MOCK_AUTH = true`
- `cd frontend && npm start`

### Full Project

- `USE_MOCK_AUTH = false`
- start DB with Docker Compose
- start backend with environment variables
- start frontend
