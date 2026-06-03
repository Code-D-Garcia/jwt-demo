# `JwtAuthenticationFilter` Explanation

This class is a custom Spring Security filter responsible for:

- Intercepting every HTTP request.
- Extracting the JWT sent by the client.
- Validating the token.
- Obtaining user information.
- Registering the authenticated user in the Spring security context.

It is a fundamental piece in a JWT-based authentication architecture.

---

# Main Annotations

### `@Component`

```java
@Component
```

Automatically registers this class as a Spring Bean.

---

### `@RequiredArgsConstructor`

```java
@RequiredArgsConstructor
```

Automatically generates a constructor with `final` dependencies.

---

# Inheritance

```java
extends OncePerRequestFilter
```

`OncePerRequestFilter` guarantees that the filter is executed only once for each HTTP request.

This prevents duplicate executions within the same request.

---

# Injected Dependencies

```java
private final JwtService jwtService;
private final UserDetailsService userDetailsService;
```

## `JwtService`

Service responsible for:

- Reading the JWT.
- Extracting information from the token.
- Validating signature and expiration.

Examples:

```java
jwtService.getUsernameFromToken(token);
jwtService.isTokenValid(token, userDetails);
```

---

## `UserDetailsService`

Standard Spring Security service.

Its responsibility is to load users from the database.

```java
userDetailsService.loadUserByUsername(username);
```

---

# Main Method

```java
@Override
protected void doFilterInternal(...)
```

This method is executed every time an HTTP request arrives.

---

# Step 1: Obtain the token

```java
final String token = getTokenFromRequest(request);
```

Looks for the token inside the header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1Ni...
```

---

# Auxiliary Method

```java
private String getTokenFromRequest(HttpServletRequest request)
```

Obtains the header:

```java
request.getHeader("Authorization");
```

Verifies that it starts with:

```text
Bearer
```

and removes the prefix:

```java
return authorizationHeader.substring(7);
```

Example:

```http
Authorization: Bearer abc123xyz
```

Result:

```text
abc123xyz
```

---

# Step 2: Verify if the token exists

```java
if (token == null) {
    filterChain.doFilter(request, response);
    return;
}
```

If the request does not contain a JWT:

- It does not attempt to authenticate.
- It continues the filter chain.

This allows access to public routes such as:

```http
/auth/login
/auth/register
```

---

# Step 3: Extract user from JWT

```java
final String username =
        jwtService.getUsernameFromToken(token);
```

Normally the JWT contains information such as:

```json
{
  "sub": "edgar",
  "exp": 1750000000
}
```

Where:

```text
sub = username
```

---

# Step 4: Verify if the user is already authenticated

```java
SecurityContextHolder
    .getContext()
    .getAuthentication() == null
```

Spring Security maintains the authenticated user in:

```java
SecurityContextHolder
```

If an authentication already exists:

- It is not authenticated again.

---

# Step 5: Load user from the database

```java
UserDetails userDetails =
        userDetailsService.loadUserByUsername(username);
```

Obtains:

- Username
- Password
- Roles
- Permissions

Example:

```java
UserDetails
```

with:

```text
username = edgar
roles = ADMIN
```

---

# Step 6: Validate the token

```java
jwtService.isTokenValid(token, userDetails)
```

Normally verifies:

### Signature

```text
Was it signed with the correct key?
```

### Expiration

```text
Has it not expired?
```

### User

```text
Does it actually belong to the user?
```

---

# Step 7: Create the authentication

```java
UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
```

Here the authentication object that Spring Security will use during the request is created.

It contains:

- User
- Roles
- Permissions

---

# Step 8: Associate request details

```java
authToken.setDetails(
        new WebAuthenticationDetails(request));
```

Saves additional information about the request:

For example:

- IP address
- Session information
- Request data

---

# Step 9: Register authenticated user

```java
SecurityContextHolder
        .getContext()
        .setAuthentication(authToken);
```

This is the most important step.

From here on, Spring considers the user to be authenticated.

Controllers can access the user via:

```java
Authentication authentication
```

or

```java
@AuthenticationPrincipal
```

---

# Step 10: Continue the request

```java
filterChain.doFilter(request, response);
```

Allows the request to continue towards:

```text
Controller
Service
Repository
```

---

# Error Handling

```java
catch (Exception e)
```

If any error occurs:

- Invalid token.
- Expired token.
- Non-existent user.
- Validation error.

It returns:

```java
response.setStatus(
        HttpServletResponse.SC_UNAUTHORIZED
);
```

HTTP Code:

```http
401 Unauthorized
```

---

# Response sent

```json
{
  "error": "Unauthorized",
  "message": "error details"
}
```

Example:

```json
{
  "error": "Unauthorized",
  "message": "JWT expired"
}
```

---

# Complete Flow

```text
HTTP Request
      │
      ▼
Authorization: Bearer <token>
      │
      ▼
JwtAuthenticationFilter
      │
      ▼
Extract JWT
      │
      ▼
Obtain username
      │
      ▼
Find user
      │
      ▼
Validate token
      │
      ▼
Create Authentication
      │
      ▼
Save in SecurityContext
      │
      ▼
Continue request
      │
      ▼
Controller
```

---

# Summary

This filter is responsible for converting a valid JWT into an authentication recognized by Spring Security.

### Main Functions

✅ Read the token from the `Authorization` header.

✅ Extract the user contained in the JWT.

✅ Load the user from the database.

✅ Validate token signature and expiration.

✅ Create the `Authentication` object.

✅ Register the user in the `SecurityContextHolder`.

✅ Allow access to protected endpoints.

✅ Return `401 Unauthorized` when the token is invalid.
