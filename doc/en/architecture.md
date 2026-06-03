# 📂 Project Structure

This project follows a standard layered architecture based on **Spring Boot**, designed to favor separation of concerns, maintainability, and system scalability.

## General Architecture

```text
HTTP Client
      │
      ▼
 Controller
      │
      ▼
  Service
      │
      ▼
 Repository
      │
      ▼
  Database
```

---

## 1. Controller Layer (`auth/controller`)

The controller layer is the application's entry point for HTTP requests.

### Responsibilities

* Expose REST API endpoints.
* Receive and process HTTP requests.
* Validate input data.
* Delegate business logic to the service layer.
* Build and return the corresponding HTTP responses.

### Considerations

* Uses the `@Valid` annotation to automatically validate objects received in requests.
* Contains no business logic; it only coordinates the flow between the API and the services.

---

## 2. Service Layer (`auth/service`)

The service layer centralizes the application's business logic.

### Responsibilities

* Implement business rules.
* Orchestrate operations between different components.
* Perform additional functional validations.
* Manage transactional processes when necessary.
* Coordinate access to the persistence layer.

### Benefits

* Reduces coupling between the API and the database.
* Facilitates unit testing.
* Allows reusing business logic in different parts of the application.

---

## 3. Repository Layer (`user/repository`)

The repository layer provides data access using **Spring Data JPA**.

### Responsibilities

* Execute CRUD (*Create, Read, Update, Delete*) operations.
* Manage database queries.
* Abstract persistence details from higher layers.

### Benefits

* Reduces the need to write manual SQL queries.
* Leverages Spring Data JPA's automatic query generation capabilities.
* Facilitates migration or modification of the persistence strategy.

---

## 4. Data Transfer Objects (DTO) (`auth/dto`)

DTOs (*Data Transfer Objects*) are structures used to exchange information between the client and the server.

### Responsibilities

* Define the API's input and output format.
* Limit the information exposed to the client.
* Decouple the external representation from the persistence models.

### Benefits

* Increases API security.
* Reduces coupling between entities and public contracts.
* Facilitates the evolution of the data model without affecting API consumers.

---

## 5. Entities (`user/entity`)

Entities represent the persistent domain model and maintain correspondence with database tables.

### Responsibilities

* Model persisted data.
* Define relationships between entities.
* Configure object-relational mapping using JPA.

### Common Annotations

```java
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
```

### Benefits

* Allow representing the database structure using Java objects.
* Facilitate the use of ORM (*Object-Relational Mapping*).
* Maintain consistency between the domain and persistence.

---

# Data Flow

The processing of a request follows this path:

```text
HTTP Request
      │
      ▼
 Controller
      │
      ▼
    DTO
      │
      ▼
  Service
      │
      ▼
 Repository
      │
      ▼
  Entity
      │
      ▼
 Database
```

---

# Summary of Components

| Component      | Description                                                             |
| -------------- | ----------------------------------------------------------------------- |
| **Controller** | Manages HTTP requests and responses.                                    |
| **Service**    | Implements business logic and coordinates system operations.            |
| **Repository** | Provides access to the persistence layer via Spring Data JPA.           |
| **DTO**        | Defines the API's data exchange contracts.                             |
| **Entity**     | Represents the persistent model and mapping with the database.          |

---

## Principles Applied

* **Separation of Concerns (SoC)**
* **Dependency Inversion**
* **Layered Architecture**
* **Low Coupling**
* **High Cohesion**
* **Maintainability and Scalability**
