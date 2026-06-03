# Explicación de `JwtService`

Esta clase es el servicio encargado de gestionar los **JSON Web Tokens (JWT)** dentro de la aplicación.

Sus responsabilidades principales son:

- Generar Access Tokens.
- Generar Refresh Tokens.
- Extraer información de un JWT.
- Validar tokens.
- Verificar expiración.
- Firmar y verificar tokens usando una clave secreta.

---

# Anotación principal

### `@Service`

```java
@Service
```

Registra esta clase como un servicio de Spring para que pueda ser inyectada en otros componentes.

Ejemplo:

```java
private final JwtService jwtService;
```

---

# Clave secreta

```java
@Value("${app.secretkey}")
private String SECRET_KEY;
```

Obtiene la clave desde el archivo de configuración.

Ejemplo:

```properties
app.secretkey=VGhpcy1pcy1teS1zZWNyZXQta2V5...
```

Esta clave se utiliza para:

- Firmar los tokens.
- Verificar que no hayan sido modificados.

---

# Generación de Access Token

```java
public String getToken(UserDetails userDetails)
```

Método público utilizado para generar un JWT de acceso.

Internamente delega a:

```java
getToken(new HashMap<>(), userDetails);
```

---

## Construcción del JWT

```java
Jwts.builder()
```

Permite crear un token paso a paso.

---

### Claims adicionales

```java
.claims(extraClaims)
```

Información personalizada que puede agregarse al token.

Ejemplo:

```json
{
  "role": "ADMIN",
  "country": "EC"
}
```

En este caso se envía un mapa vacío:

```java
new HashMap<>()
```

---

### Subject

```java
.subject(userDetails.getUsername())
```

Define el propietario del token.

Ejemplo:

```json
{
  "sub": "edgar"
}
```

---

### Fecha de emisión

```java
.issuedAt(new Date(System.currentTimeMillis()))
```

Representa cuándo fue generado el token.

Claim JWT:

```json
{
  "iat": 1710000000
}
```

---

### Fecha de expiración

```java
.expiration(
    new Date(System.currentTimeMillis()
    + 1000 * 60 * 60 * 24)
)
```

Duración:

```text
24 horas
```

Cálculo:

```text
1000 ms × 60 × 60 × 24
```

---

### Firma

```java
.signWith(getKey())
```

Firma digitalmente el JWT usando la clave secreta.

Esto evita que el contenido sea modificado.

---

### Generación final

```java
.compact()
```

Convierte toda la información en una cadena JWT.

Ejemplo:

```text
eyJhbGciOiJIUzI1NiJ9...
```

---

# Generación de Refresh Token

```java
public String getRefreshToken(UserDetails userDetails)
```

Genera un token de renovación.

---

## Diferencia principal

La expiración es mayor:

```java
1000 * 60 * 60 * 24 * 7
```

Equivale a:

```text
7 días
```

---

## Objetivo

Permitir generar nuevos Access Tokens sin volver a iniciar sesión.

Flujo típico:

```text
Login
   │
   ├── Access Token (24h)
   │
   └── Refresh Token (7d)
```

---

# Obtención de la clave

```java
private SecretKey getKey()
```

Convierte la clave almacenada en texto Base64 a una clave criptográfica válida.

---

## Decodificación

```java
Decoders.BASE64.decode(SECRET_KEY)
```

Transforma:

```text
VGhpcy1pcy1hLXNlY3JldA==
```

en bytes.

---

## Creación de la clave

```java
Keys.hmacShaKeyFor(encodedKey)
```

Genera un objeto:

```java
SecretKey
```

que será utilizado para:

- Firmar tokens.
- Validar firmas.

---

# Obtener username desde el token

```java
public String getUsernameFromToken(String token)
```

Extrae el campo:

```json
{
  "sub": "edgar"
}
```

mediante:

```java
Claims::getSubject
```

Resultado:

```java
"edgar"
```

---

# Obtener cualquier Claim

```java
public <T> T getClaim(
        String token,
        Function<Claims, T> claimsResolver)
```

Método genérico para extraer cualquier información del JWT.

---

## Ejemplo

Obtener expiración:

```java
getClaim(token, Claims::getExpiration)
```

Obtener subject:

```java
getClaim(token, Claims::getSubject)
```

---

# Obtener todos los Claims

```java
private Claims getAllClaims(String token)
```

Lee completamente el contenido del JWT.

---

## Validación de firma

```java
.verifyWith(getKey())
```

Verifica que:

- El token fue firmado con la clave correcta.
- No fue alterado.

---

## Parseo

```java
.parseSignedClaims(token)
```

Extrae todos los datos contenidos en el JWT.

---

## Payload

```java
.getPayload()
```

Retorna algo similar a:

```json
{
  "sub": "edgar",
  "iat": 1710000000,
  "exp": 1710086400
}
```

---

# Validación del token

```java
public boolean isTokenValid(
        String token,
        UserDetails userDetails)
```

Comprueba dos condiciones:

---

## 1. Usuario correcto

```java
username.equals(
        userDetails.getUsername()
)
```

Verifica que el usuario del JWT coincide con el usuario cargado desde la base de datos.

---

## 2. No expirado

```java
!isTokenExpired(token)
```

Verifica que la fecha actual sea menor que la fecha de expiración.

---

## Resultado final

```java
return username.equals(...)
       && !isTokenExpired(token);
```

El token es válido únicamente si ambas condiciones son verdaderas.

---

# Obtener fecha de expiración

```java
private Date getExpirationDate(String token)
```

Extrae el claim:

```json
{
  "exp": 1710086400
}
```

---

# Verificar expiración

```java
private boolean isTokenExpired(String token)
```

Compara:

```java
getExpirationDate(token)
```

contra:

```java
new Date()
```

---

## Lógica

```java
return expirationDate.before(new Date());
```

Si la fecha de expiración es anterior al momento actual:

```text
Token expirado
```

Resultado:

```java
true
```

---

# Flujo completo de generación

```text
Usuario inicia sesión
         │
         ▼
JwtService.getToken()
         │
         ▼
Crear JWT
         │
         ├── subject
         ├── issuedAt
         ├── expiration
         └── signature
         │
         ▼
JWT generado
         │
         ▼
Cliente recibe token
```

---

# Flujo completo de validación

```text
Petición HTTP
       │
       ▼
JWT recibido
       │
       ▼
Extraer username
       │
       ▼
Obtener usuario
       │
       ▼
Verificar firma
       │
       ▼
Verificar expiración
       │
       ▼
Comparar username
       │
       ▼
Token válido
```

---

# Resumen

`JwtService` centraliza toda la lógica relacionada con JWT.

### Funciones principales

✅ Generar Access Tokens.

✅ Generar Refresh Tokens.

✅ Firmar tokens con una clave secreta.

✅ Extraer Claims.

✅ Obtener el usuario desde el token.

✅ Verificar integridad del JWT.

✅ Validar expiración.

✅ Confirmar que el token pertenece al usuario autenticado.

Es el núcleo de la autenticación basada en JWT dentro de la aplicación.