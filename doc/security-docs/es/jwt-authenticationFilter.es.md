# Explicación de `JwtAuthenticationFilter`

Esta clase es un filtro personalizado de Spring Security encargado de:

- Interceptar cada petición HTTP.
- Extraer el JWT enviado por el cliente.
- Validar el token.
- Obtener la información del usuario.
- Registrar al usuario autenticado en el contexto de seguridad de Spring.

Es una pieza fundamental en una arquitectura de autenticación basada en JWT.

---

# Anotaciones principales

### `@Component`

```java
@Component
```

Registra automáticamente esta clase como un Bean de Spring.

---

### `@RequiredArgsConstructor`

```java
@RequiredArgsConstructor
```

Genera automáticamente un constructor con las dependencias `final`.

---

# Herencia

```java
extends OncePerRequestFilter
```

`OncePerRequestFilter` garantiza que el filtro se ejecute una sola vez por cada petición HTTP.

Esto evita ejecuciones duplicadas dentro de la misma solicitud.

---

# Dependencias inyectadas

```java
private final JwtService jwtService;
private final UserDetailsService userDetailsService;
```

## `JwtService`

Servicio encargado de:

- Leer el JWT.
- Extraer información del token.
- Validar firma y expiración.

Ejemplos:

```java
jwtService.getUsernameFromToken(token);
jwtService.isTokenValid(token, userDetails);
```

---

## `UserDetailsService`

Servicio estándar de Spring Security.

Su responsabilidad es cargar usuarios desde la base de datos.

```java
userDetailsService.loadUserByUsername(username);
```

---

# Método principal

```java
@Override
protected void doFilterInternal(...)
```

Este método se ejecuta cada vez que llega una petición HTTP.

---

# Paso 1: Obtener el token

```java
final String token = getTokenFromRequest(request);
```

Busca el token dentro del header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1Ni...
```

---

# Método auxiliar

```java
private String getTokenFromRequest(HttpServletRequest request)
```

Obtiene el header:

```java
request.getHeader("Authorization");
```

Verifica que empiece por:

```text
Bearer
```

y elimina el prefijo:

```java
return authorizationHeader.substring(7);
```

Ejemplo:

```http
Authorization: Bearer abc123xyz
```

Resultado:

```text
abc123xyz
```

---

# Paso 2: Verificar si existe token

```java
if (token == null) {
    filterChain.doFilter(request, response);
    return;
}
```

Si la petición no contiene JWT:

- No intenta autenticar.
- Continúa la cadena de filtros.

Esto permite acceder a rutas públicas como:

```http
/auth/login
/auth/register
```

---

# Paso 3: Extraer usuario del JWT

```java
final String username =
        jwtService.getUsernameFromToken(token);
```

Normalmente el JWT contiene información como:

```json
{
  "sub": "edgar",
  "exp": 1750000000
}
```

Donde:

```text
sub = username
```

---

# Paso 4: Verificar si el usuario ya está autenticado

```java
SecurityContextHolder
    .getContext()
    .getAuthentication() == null
```

Spring Security mantiene el usuario autenticado en:

```java
SecurityContextHolder
```

Si ya existe una autenticación:

- No se vuelve a autenticar.

---

# Paso 5: Cargar usuario desde la base de datos

```java
UserDetails userDetails =
        userDetailsService.loadUserByUsername(username);
```

Obtiene:

- Usuario
- Contraseña
- Roles
- Permisos

Ejemplo:

```java
UserDetails
```

con:

```text
username = edgar
roles = ADMIN
```

---

# Paso 6: Validar el token

```java
jwtService.isTokenValid(token, userDetails)
```

Normalmente verifica:

### Firma

```text
¿Fue firmado con la clave correcta?
```

### Expiración

```text
¿No ha expirado?
```

### Usuario

```text
¿Pertenece realmente al usuario?
```

---

# Paso 7: Crear la autenticación

```java
UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
```

Aquí se crea el objeto de autenticación que Spring Security utilizará durante la petición.

Contiene:

- Usuario
- Roles
- Permisos

---

# Paso 8: Asociar detalles de la petición

```java
authToken.setDetails(
        new WebAuthenticationDetails(request));
```

Guarda información adicional de la petición:

Por ejemplo:

- Dirección IP
- Información de sesión
- Datos del request

---

# Paso 9: Registrar usuario autenticado

```java
SecurityContextHolder
        .getContext()
        .setAuthentication(authToken);
```

Este es el paso más importante.

A partir de aquí Spring considera que el usuario está autenticado.

Los controladores podrán acceder al usuario mediante:

```java
Authentication authentication
```

o

```java
@AuthenticationPrincipal
```

---

# Paso 10: Continuar la petición

```java
filterChain.doFilter(request, response);
```

Permite que la solicitud continúe hacia:

```text
Controller
Service
Repository
```

---

# Manejo de errores

```java
catch (Exception e)
```

Si ocurre cualquier error:

- Token inválido.
- Token expirado.
- Usuario inexistente.
- Error de validación.

Se devuelve:

```java
response.setStatus(
        HttpServletResponse.SC_UNAUTHORIZED
);
```

Código HTTP:

```http
401 Unauthorized
```

---

# Respuesta enviada

```json
{
  "error": "Unauthorized",
  "message": "detalle del error"
}
```

Ejemplo:

```json
{
  "error": "Unauthorized",
  "message": "JWT expired"
}
```

---

# Flujo completo

```text
Petición HTTP
      │
      ▼
Authorization: Bearer <token>
      │
      ▼
JwtAuthenticationFilter
      │
      ▼
Extraer JWT
      │
      ▼
Obtener username
      │
      ▼
Buscar usuario
      │
      ▼
Validar token
      │
      ▼
Crear Authentication
      │
      ▼
Guardar en SecurityContext
      │
      ▼
Continuar petición
      │
      ▼
Controller
```

---

# Resumen

Este filtro es el responsable de convertir un JWT válido en una autenticación reconocida por Spring Security.

### Funciones principales

✅ Leer el token del header `Authorization`.

✅ Extraer el usuario contenido en el JWT.

✅ Cargar el usuario desde la base de datos.

✅ Validar firma y expiración del token.

✅ Crear el objeto `Authentication`.

✅ Registrar al usuario en el `SecurityContextHolder`.

✅ Permitir el acceso a endpoints protegidos.

✅ Devolver `401 Unauthorized` cuando el token sea inválido.