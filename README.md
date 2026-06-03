# 🔐 Authentication Demo with Spring Boot and JWT

This demonstration shows a way to implement authentication based on **JSON Web Tokens (JWT)** using **Spring Boot 3** and **Spring Security**.

The goal is to serve as an educational reference to understand the fundamental concepts of authentication, authorization, and protection of REST APIs using tokens.

---

[**Leer en Español 🇪🇸**](README.es.md)

---

## 🎯 Objective

This demo implements a basic authentication flow that allows:

* Registering users.
* Authenticating credentials through login.
* Generating and validating JWT Access Tokens.
* Renewing sessions using Refresh Tokens.
* Protecting endpoints using Spring Security.
* Managing errors and validations consistently.

It is not intended to be a complete solution for production, but rather a practical example of how to integrate JWT into a Spring Boot application.

---

## 🛠️ Technologies Used

* **Java 21**
* **Spring Boot 3**
* **Spring Security**
* **JSON Web Tokens (JWT)**
* **Spring Data JPA**
* **PostgreSQL**
* **Lombok**

---

## 📂 Code Organization

The application follows a layered structure to separate responsibilities:

### `controller`

Exposes REST endpoints and receives HTTP requests.

### `service`

Contains the business logic related to authentication and users.

### `repository`

Manages database access via Spring Data JPA.

### `dto`

Defines the objects used to exchange information between client and server.

### `security`

Includes Spring Security configuration, JWT filters, and authentication-related components.

### `exception`

Centralizes error handling and generation of consistent responses.

---

## 🛡️ Implemented Features

### Data Validation

Data received in requests are automatically validated, including:

* Required fields.
* Minimum username and password length.
* Password confirmation.
* Restrictions defined via Bean Validation.

### Authentication with JWT

After successful authentication, the user receives:

* **Access Token**: used to access protected resources.
* **Refresh Token**: used to obtain a new Access Token without logging in again.

### Centralized Error Handling

Exceptions and validation errors are transformed into clear and structured responses.

Example:

```json
{
  "status": 400,
  "error": "Validation Error",
  "message": "Input validation failed",
  "validations": {
    "password": "Password must be at least 8 characters"
  }
}
```

---

## 🚀 Execution

### Requirements

* Java 21
* PostgreSQL
* Maven or Gradle
* Compatible IDE (IntelliJ IDEA, VS Code, Eclipse, etc.)

### Configuration

1. Create a PostgreSQL database.
2. Configure connection credentials in `application.properties` or `application-dev.properties`.
3. Run the application.
4. Consume endpoints using Postman, Insomnia, or any HTTP client.

---

## 📌 Available Endpoints

| Method | Endpoint              | Description                          |
| ------ | --------------------- | ------------------------------------ |
| POST   | `/auth/register`      | User registration                    |
| POST   | `/auth/login`         | Authentication and token generation |
| POST   | `/auth/refresh-token` | Access Token renewal                 |

---

## 📚 Documentation

You can find detailed documentation about the project in the following links:

### General
*   [**Project Structure**](doc/en/architecture.md)
*   [**Error Handling**](doc/en/errors.md)
*   [**Security and JWT**](doc/en/security.md)

### Technical Details (Security)
*   [**Security Configuration (SecurityConfig)**](doc/security-docs/en/security-config.en.md)
*   [**Authentication Filter (JwtAuthenticationFilter)**](doc/security-docs/en/jwt-authenticationFilter.en.md)
*   [**JWT Service (JwtService)**](doc/security-docs/en/jwt-service.en.md)

---

## 📚 Educational Purpose

This demo was created for learning purposes to show a possible implementation of JWT authentication in Spring Boot.

It can be used as a starting point to experiment with concepts such as:

* Stateless authentication.
* Spring Security.
* JWT Access and Refresh Tokens.
* Request validation.
* Global exception handling.
* Layered architecture.
