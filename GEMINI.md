# GEMINI.md

## Project Overview

This is a multi-tenant, enterprise-grade identity and access management platform. It consists of a Kotlin/Spring Boot backend and a Vue.js frontend.

**Backend:**
- Language: Kotlin
- Framework: Spring Boot
- Build Tool: Maven
- Key Dependencies: Spring Web, Spring Data JPA, Spring Security, JJWT, SpringDoc (Swagger), TOTP for MFA.

**Frontend:**
- Framework: Vue.js 3
- Build Tool: Vite
- State Management: Pinia
- Key Dependencies: Axios, Lucide Vue Next.

The project follows Clean Architecture and Domain-Driven Design (DDD) principles.

## Key Files

- `pom.xml`: The Maven project configuration file, including all dependencies for the backend.
- `auth-dashboard/package.json`: The npm configuration file for the frontend, including all dependencies and scripts.
- `src/main/kotlin/com/authservice/core/AuthCoreApplication.kt`: The main entry point for the Spring Boot application.
- `src/main/kotlin/com/authservice/core/domain/model/`: This directory contains the domain models, which represent the core business objects of the application. For example, `Tenant.kt` defines the `Tenant` object.
- `src/main/kotlin/com/authservice/core/domain/repository/`: This directory contains the repository interfaces, which define the contract for a data source. For example, `TenantRepository.kt` defines how to interact with tenant data.
- `src/main/kotlin/com/authservice/core/web/controller/`: This directory contains the REST controllers for the backend API. For example, `TenantController.kt` defines the endpoints for managing tenants.
- `src/main/kotlin/com/authservice/core/application/usecase/`: This directory contains the use cases that implement the business logic. For example, `CreateTenantUseCase.kt` handles the creation of a new tenant.
- `src/main/resources/application.properties`: The configuration file for the backend, including database and JWT settings.
- `docker-compose.yml`: The Docker Compose file for setting up the local development environment.
- `auth-dashboard/src/App.vue`: The main Vue component for the frontend application. This component renders the main layout and handles view switching.
- `auth-dashboard/src/components/dashboard/Sidebar.vue`: The sidebar component, which handles navigation within the dashboard.
- `auth-dashboard/src/stores/app.ts`: The main Pinia store for managing the frontend's state and making API calls to the backend.

## Building and Running

### Prerequisites
- Java 21+
- Node.js 18+
- Docker & Docker Compose
- Maven 3.9+

### 1. Launch Infrastructure
Start the database (MariaDB) and cache (Redis) using Docker Compose.

```bash
docker-compose up -d
```

### 2. Run the Backend
Navigate to the root directory and start the Spring Boot application:

```bash
./mvnw spring-boot:run
```

- API Base: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### 3. Run the Frontend Dashboard
Navigate to the `auth-dashboard` directory and start the development server:

```bash
cd auth-dashboard
npm install
npm run dev
```

- Dashboard URL: [http://localhost:5173](http://localhost:5173)

## Configuration

### Backend

The backend configuration is located in `src/main/resources/application.properties`.

- **Database:** The application uses MariaDB. The connection details are:
  - URL: `jdbc:mariadb://localhost:3307/auth_db`
  - User: `auth_user`
  - Password: `auth_password`
- **Redis:** The application uses Redis for caching.
  - Host: `localhost`
  - Port: `6379`
- **JWT:** The JWT secret and expiration times are configured in this file.

### Infrastructure

The `docker-compose.yml` file defines the following services:
- **mariadb**: A MariaDB container for the.
  - Port: 3307
- **redis**: A Redis container for caching.
  - Port: 6379

## Development Conventions

- The backend follows Clean Architecture principles, with code organized into `domain`, `application`, `infrastructure`, and `web` layers. The `domain` layer contains the core business objects and repository interfaces. The `web` layer contains the REST controllers that define the API endpoints, and the `application` layer contains the business logic in the form of use cases. The `infrastructure` layer provides the concrete implementation of the repositories.
- The frontend uses a component-based architecture with Vue.js and manages state with Pinia. Communication between components is handled through props and events.
- API documentation is available via Swagger UI.
- The frontend communicates with the backend via a REST API, with the base URL `http://localhost:8080/api/v1`.