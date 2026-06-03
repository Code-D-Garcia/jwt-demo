# `SecurityConfig` Explanation

This class configures the security of a Spring Boot application using **Spring Security** and **JWT (JSON Web Token)** based authentication.

## Main Annotations

### `@Configuration`
Indicates that this class contains Spring configurations and that its methods can register beans in the Spring container.

### `@EnableWebSecurity`
Activates Spring Security's web security configuration.

### `@RequiredArgsConstructor`
Lombok annotation that automatically generates a constructor with `final` attributes, allowing dependency injection.

---

# Injected Dependencies

```java
private final JwtAuthenticationFilter jwtAuthenticationFilter;
private final AuthenticationProvider authenticationProvider;
```

## `JwtAuthenticationFilter`
Custom filter responsible for:

- Intercepting HTTP requests.
- Reading the JWT token sent by the client.
- Validating the token.
- Authenticating the user in the Spring security context.

## `AuthenticationProvider`
Component responsible for performing the authentication process.

Normally:

- Verifies credentials.
- Queries users from the database.
- Builds a valid `Authentication` object.

---

# `SecurityFilterChain` Bean

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
```

Defines the entire filter chain and security rules of the application.

---

# 1. Disable CSRF

```java
.csrf(AbstractHttpConfigurer::disable)
```

## What is CSRF?

CSRF (*Cross-Site Request Forgery*) is an attack that exploits cookie-based sessions.

## Why is it disabled?

When working with JWT:

- Server sessions are not used.
- Authentication travels in each request via a token.

Therefore, CSRF protection is usually not necessary.

---

# 2. Authorization Configuration

```java
.authorizeHttpRequests(authRequest ->
        authRequest
                .requestMatchers("/auth/**", "/error").permitAll()
                .anyRequest().authenticated()
)
```

## Public Routes

```java
.requestMatchers("/auth/**", "/error").permitAll()
```

Allows access without authentication to:

| Route | Description |
|--------|-------------|
| `/auth/**` | Login, registration, refresh token, etc. |
| `/error` | Error page/controller |

Examples:

```http
POST /auth/login
POST /auth/register
```

They do not require a JWT.

---

## Protected Routes

```java
.anyRequest().authenticated()
```

Every request other than the ones above:

- Must be authenticated.
- Must contain a valid JWT.

Example:

```http
GET /users
GET /products
POST /orders
```

They require authentication.

---

# 3. Session Policy

```java
.sessionManagement(sessionManagement ->
        sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

## `STATELESS`

Indicates that Spring Security:

- Does not create HTTP sessions.
- Does not store authenticated users in memory.
- Each request must authenticate itself via JWT.

### Flow

```text
Client
   │
   ├── Login
   │
   └── JWT
         │
         ▼
Request 1 → JWT
Request 2 → JWT
Request 3 → JWT
```

The server does not remember the user between requests.

---

# 4. AuthenticationProvider Registration

```java
.authenticationProvider(authenticationProvider)
```

Tells Spring Security which component it should use to authenticate users.

Generally, this provider:

1. Receives username and password.
2. Looks for the user in the database.
3. Verifies the password.
4. Returns a valid authentication.

---

# 5. JWT Filter Registration

```java
.addFilterBefore(
        jwtAuthenticationFilter,
        UsernamePasswordAuthenticationFilter.class
)
```

## What does it do?

Inserts the JWT filter before Spring's standard authentication filter.

### Simplified Order

```text
Request
   │
   ▼
JwtAuthenticationFilter
   │
   ▼
UsernamePasswordAuthenticationFilter
   │
   ▼
Controller
```

## Why before?

Because the JWT must be validated before Spring attempts to process traditional username and password-based authentication.

### Typical Flow

```text
1. A request arrives
2. JwtAuthenticationFilter reads Authorization Header
3. Extracts the token
4. Validates the JWT
5. Obtains the user
6. Registers it in SecurityContext
7. Continues the request
```

---

# Final Configuration Result

This configuration implements a JWT-based security architecture with the following features:

✅ CSRF disabled.

✅ Public `/auth/**` and `/error` endpoints.

✅ All other routes require authentication.

✅ No HTTP sessions are created (`STATELESS`).

✅ A custom `AuthenticationProvider` is used.

✅ A JWT filter is executed before the standard authentication filter.

---

# Complete Authentication Flow

```text
POST /auth/login
        │
        ▼
AuthenticationProvider
        │
        ▼
Generate JWT
        │
        ▼
Client receives JWT
        │
        ▼
Authorization: Bearer <token>
        │
        ▼
JwtAuthenticationFilter
        │
        ▼
Validates token
        │
        ▼
Authenticated user
        │
        ▼
Access to protected endpoint
```
