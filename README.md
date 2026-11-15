# TourPlanner

A full-stack web application for planning tours, visualizing routes on interactive maps, and tracking completed activities.

**Tech Stack:** Angular · TypeScript · Java · Spring Boot · PostgreSQL · Docker  
**Context:** Fourth-semester team project for Software Engineering 2 at FH Technikum Wien (University of Applied Sciences)

**Highlights:**
- Route calculation and geocoding through OpenRouteService
- REST API with JWT authentication and user-specific data management
- Layered architecture with automated unit and integration tests

---

## Features

### Tour Planning and Management

Users can create, edit, delete, and search tours. Each tour includes a name, description, starting point, destination, and activity type. Supported types include cycling, hiking, running, and vacation.

OpenRouteService calculates the route, distance, and estimated duration. Routes are displayed on an interactive map using Leaflet and OpenStreetMap.

### Activity Tracking

Users can record completed activities through tour logs containing:

- Date and time
- Distance covered and total duration
- Difficulty and rating
- Personal comments

The application derives statistics from these logs, including average difficulty, popularity, and child-friendliness.

### Additional Features

- User registration and login with JWT authentication
- User-specific tours and activity logs
- Search across tours and tour logs
- Favorites with filtering
- XML import and export of tours and their associated logs

## Technologies

| Area | Technologies |
| --- | --- |
| Frontend | Angular 21, TypeScript, RxJS |
| UI Components | PrimeNG, PrimeIcons |
| Maps | Leaflet, OpenStreetMap |
| Backend | Java 25, Spring Boot 4 |
| Database | PostgreSQL 17 |
| Persistence | Spring Data JPA, Hibernate |
| Security | Spring Security, JWT, BCrypt |
| External API | OpenRouteService |
| XML Processing | Jackson XML |
| Testing | JUnit, Mockito |
| Build and Infrastructure | Maven, npm, Docker Compose |

## Architecture

The Angular frontend communicates with the Spring Boot backend through a REST API. The backend handles business logic, authentication, database access, and external service integration.

A typical request follows this flow:

    Angular Component
        → Angular Service
        → REST Controller
        → Backend Service
        → Repository
        → PostgreSQL

### Backend

The backend follows a layered architecture:

- **Controllers:** Handle HTTP requests and responses.
- **Services:** Implement business logic and interact with external services.
- **Repositories and Entities:** Manage data access and database mappings.
- **DTOs and Mappers:** Separate API representations from persistence models.
- **Security:** Handle authentication, token processing, and password hashing.

Dependency injection and interfaces decouple components and allow dependencies to be replaced with mocks during unit testing.

### Frontend

The frontend is organized by feature, including authentication, tours, tour logs, import, and export.

Reusable UI components are located in `shared`. Global configuration, guards, HTTP interceptors, and session management are grouped under `core`. Reactive Forms and Angular Signals support form handling and state management.

## Testing

Backend unit tests cover key application behavior:

- User registration and validation
- Tour and tour log management
- Search and derived statistics
- Favorites
- XML import and export
- Image management

Dependencies such as repositories and external clients are mocked with Mockito. Additional integration tests verify communication with OpenRouteService. A Postman collection is included for testing the REST API.

## Project Structure

    tour_planner/
    ├── backend/       Spring Boot application, database configuration, and tests
    ├── frontend/      Angular application and UI components
    ├── docs/          Project reports, UML diagrams, and test documentation
    └── GUIDE.md       Local setup instructions

## Local Development

Running the complete application requires:

- Java 25
- Node.js and npm
- Docker and Docker Compose
- An OpenRouteService API key

Backend configuration uses the variables `DB_USER`, `DB_PASSWORD`, `DB_NAME`, `DB_PORT`, `JWT_SECRET`, and `ORS_API_KEY`.

By default, the frontend runs on port **4200** and the backend on port **8080**. Setup instructions are provided in `GUIDE.md`.

Optional mock authentication supports development of the login interface without backend authentication. It is controlled through `useMockAuth` in the frontend environment configuration. Other application features still require the backend.

## Academic Context

This project was developed during the summer semester of 2026 as part of **Software Engineering 2 (SWEN2)** in the **fourth semester at FH Technikum Wien**.

The coursework focused on applying software architecture principles, integrating external services, writing automated tests, and documenting architectural and development decisions.

**Project Team:** Michael Stangl and Wenhao Wen
