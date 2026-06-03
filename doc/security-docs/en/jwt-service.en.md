# `JwtService` Explanation

This class is the service responsible for managing **JSON Web Tokens (JWT)** within the application.

Its main responsibilities are:

- Generate Access Tokens.
- Generate Refresh Tokens.
- Extract information from a JWT.
- Validate tokens.
- Verify expiration.
- Sign and verify tokens using a secret key.

---

# Main Annotation

### `@Service`

```java
@Service
```

Registers this class as a Spring service so that it can be injected into other components.

Example:

```java
private final JwtService jwtService;
```

---

# Secret Key

```java
@Value("${app.secretkey}")
private String SECRET_KEY;
```

Obtains the key from the configuration file.

Example:

```properties
app.secretkey=VGhpcy1pcy1teS1zZWNyZXQta2V5...
```

This key is used to:

- Sign tokens.
- Verify that they have not been modified.

---

# Access Token Generation

```java
public String getToken(UserDetails userDetails)
```

Public method used to generate an access JWT.

Internally it delegates to:

```java
getToken(new HashMap<>(), userDetails);
```

---

## JWT Construction

```java
Jwts.builder()
```

Allows creating a token step by step.

---

### Additional Claims

```java
.claims(extraClaims)
```

Custom information that can be added to the token.

Example:

```json
{
  "role": "ADMIN",
  "country": "EC"
}
```

In this case, an empty map is sent:

```java
new HashMap<>()
```

---

### Subject

```java
.subject(userDetails.getUsername())
```

Defines the owner of the token.

Example:

```json
{
  "sub": "edgar"
}
```

---

### Issue Date

```java
.issuedAt(new Date(System.currentTimeMillis()))
```

Represents when the token was generated.

JWT Claim:

```json
{
  "iat": 1710000000
}
```

---

### Expiration Date

```java
.expiration(
    new Date(System.currentTimeMillis()
    + 1000 * 60 * 60 * 24)
)
```

Duration:

```text
24 hours
```

Calculation:

```text
1000 ms × 60 × 60 × 24
```

---

### Signature

```java
.signWith(getKey())
```

Digitally signs the JWT using the secret key.

This prevents the content from being modified.

---

### Final Generation

```java
.compact()
```

Converts all information into a JWT string.

Example:

```text
eyJhbGciOiJIUzI1NiJ9...
```

---

# Refresh Token Generation

```java
public String getRefreshToken(UserDetails userDetails)
```

Generates a renewal token.

---

## Main Difference

The expiration is longer:

```java
1000 * 60 * 60 * 24 * 7
```

Equivalent to:

```text
7 days
```

---

## Objective

To allow generating new Access Tokens without logging in again.

Typical flow:

```text
Login
   │
   ├── Access Token (24h)
   │
   └── Refresh Token (7d)
```

---

# Obtaining the Key

```java
private SecretKey getKey()
```

Converts the key stored in Base64 text to a valid cryptographic key.

---

## Decoding

```java
Decoders.BASE64.decode(SECRET_KEY)
```

Transforms:

```text
VGhpcy1pcy1hLXNlY3JldA==
```

into bytes.

---

## Key Creation

```java
Keys.hmacShaKeyFor(encodedKey)
```

Generates an object:

```java
SecretKey
```

which will be used to:

- Sign tokens.
- Validate signatures.

---

# Get username from token

```java
public String getUsernameFromToken(String token)
```

Extracts the field:

```json
{
  "sub": "edgar"
}
```

via:

```java
Claims::getSubject
```

Result:

```java
"edgar"
```

---

# Get any Claim

```java
public <T> T getClaim(
        String token,
        Function<Claims, T> claimsResolver)
```

Generic method to extract any information from the JWT.

---

## Example

Get expiration:

```java
getClaim(token, Claims::getExpiration)
```

Get subject:

```java
getClaim(token, Claims::getSubject)
```

---

# Get all Claims

```java
private Claims getAllClaims(String token)
```

Completely reads the content of the JWT.

---

## Signature Validation

```java
.verifyWith(getKey())
```

Verifies that:

- The token was signed with the correct key.
- It was not altered.

---

## Parsing

```java
.parseSignedClaims(token)
```

Extracts all data contained in the JWT.

---

## Payload

```java
.getPayload()
```

Returns something similar to:

```json
{
  "sub": "edgar",
  "iat": 1710000000,
  "exp": 1710086400
}
```

---

# Token Validation

```java
public boolean isTokenValid(
        String token,
        UserDetails userDetails)
```

Checks two conditions:

---

## 1. Correct User

```java
username.equals(
        userDetails.getUsername()
)
```

Verifies that the user from the JWT matches the user loaded from the database.

---

## 2. Not Expired

```java
!isTokenExpired(token)
```

Verifies that the current date is less than the expiration date.

---

## Final Result

```java
return username.equals(...)
       && !isTokenExpired(token);
```

The token is valid only if both conditions are true.

---

# Get Expiration Date

```java
private Date getExpirationDate(String token)
```

Extracts the claim:

```json
{
  "exp": 1710086400
}
```

---

# Verify Expiration

```java
private boolean isTokenExpired(String token)
```

Compares:

```java
getExpirationDate(token)
```

against:

```java
new Date()
```

---

## Logic

```java
return expirationDate.before(new Date());
```

If the expiration date is before the current moment:

```text
Token expired
```

Result:

```java
true
```

---

# Complete Generation Flow

```text
User logs in
         │
         ▼
JwtService.getToken()
         │
         ▼
Create JWT
         │
         ├── subject
         ├── issuedAt
         ├── expiration
         └── signature
         │
         ▼
JWT generated
         │
         ▼
Client receives token
```

---

# Complete Validation Flow

```text
HTTP Request
       │
       ▼
JWT received
       │
       ▼
Extract username
       │
       ▼
Obtain user
       │
       ▼
Verify signature
       │
       ▼
Verify expiration
       │
       ▼
Compare username
       │
       ▼
Valid token
```

---

# Summary

`JwtService` centralizes all logic related to JWT.

### Main Functions

✅ Generate Access Tokens.

✅ Generate Refresh Tokens.

✅ Sign tokens with a secret key.

✅ Extract Claims.

✅ Get the user from the token.

✅ Verify JWT integrity.

✅ Validate expiration.

✅ Confirm that the token belongs to the authenticated user.

It is the core of JWT-based authentication within the application.
