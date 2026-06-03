# Explicación de `SecurityConfig`

Esta clase configura la seguridad de una aplicación Spring Boot utilizando **Spring Security** y autenticación basada en **JWT (JSON Web Token)**.

## Anotaciones principales

### `@Configuration`
Indica que esta clase contiene configuraciones de Spring y que sus métodos pueden registrar beans en el contenedor de Spring.

### `@EnableWebSecurity`
Activa la configuración de seguridad web de Spring Security.

### `@RequiredArgsConstructor`
Anotación de Lombok que genera automáticamente un constructor con los atributos `final`, permitiendo la inyección de dependencias.

---

# Dependencias inyectadas

```java
private final JwtAuthenticationFilter jwtAuthenticationFilter;
private final AuthenticationProvider authenticationProvider;
```

## `JwtAuthenticationFilter`
Filtro personalizado encargado de:

- Interceptar las solicitudes HTTP.
- Leer el token JWT enviado por el cliente.
- Validar el token.
- Autenticar al usuario en el contexto de seguridad de Spring.

## `AuthenticationProvider`
Componente responsable de realizar el proceso de autenticación.

Normalmente:

- Verifica credenciales.
- Consulta usuarios desde la base de datos.
- Construye el objeto `Authentication` válido.

---

# Bean `SecurityFilterChain`

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
```

Define toda la cadena de filtros y reglas de seguridad de la aplicación.

---

# 1. Deshabilitar CSRF

```java
.csrf(AbstractHttpConfigurer::disable)
```

## ¿Qué es CSRF?

CSRF (*Cross-Site Request Forgery*) es un ataque que aprovecha sesiones basadas en cookies.

## ¿Por qué se deshabilita?

Cuando se trabaja con JWT:

- No se usan sesiones del servidor.
- La autenticación viaja en cada petición mediante un token.

Por eso, normalmente no es necesario mantener la protección CSRF.

---

# 2. Configuración de autorización

```java
.authorizeHttpRequests(authRequest ->
        authRequest
                .requestMatchers("/auth/**", "/error").permitAll()
                .anyRequest().authenticated()
)
```

## Rutas públicas

```java
.requestMatchers("/auth/**", "/error").permitAll()
```

Permite acceso sin autenticación a:

| Ruta | Descripción |
|--------|-------------|
| `/auth/**` | Login, registro, refresh token, etc. |
| `/error` | Página/controlador de errores |

Ejemplos:

```http
POST /auth/login
POST /auth/register
```

No requieren JWT.

---

## Rutas protegidas

```java
.anyRequest().authenticated()
```

Toda petición distinta a las anteriores:

- Debe estar autenticada.
- Debe contener un JWT válido.

Ejemplo:

```http
GET /users
GET /products
POST /orders
```

Requieren autenticación.

---

# 3. Política de sesiones

```java
.sessionManagement(sessionManagement ->
        sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

## `STATELESS`

Indica que Spring Security:

- No crea sesiones HTTP.
- No almacena usuarios autenticados en memoria.
- Cada petición debe autenticarse por sí misma mediante JWT.

### Flujo

```text
Cliente
   │
   ├── Login
   │
   └── JWT
         │
         ▼
Petición 1 → JWT
Petición 2 → JWT
Petición 3 → JWT
```

El servidor no recuerda al usuario entre solicitudes.

---

# 4. Registro del AuthenticationProvider

```java
.authenticationProvider(authenticationProvider)
```

Le indica a Spring Security qué componente debe usar para autenticar usuarios.

Generalmente este provider:

1. Recibe usuario y contraseña.
2. Busca el usuario en la base de datos.
3. Verifica la contraseña.
4. Retorna una autenticación válida.

---

# 5. Registro del filtro JWT

```java
.addFilterBefore(
        jwtAuthenticationFilter,
        UsernamePasswordAuthenticationFilter.class
)
```

## ¿Qué hace?

Inserta el filtro JWT antes del filtro estándar de autenticación de Spring.

### Orden simplificado

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
Controlador
```

## ¿Por qué antes?

Porque el JWT debe validarse antes de que Spring intente procesar la autenticación tradicional basada en usuario y contraseña.

### Flujo típico

```text
1. Llega una petición
2. JwtAuthenticationFilter lee Authorization Header
3. Extrae el token
4. Valida el JWT
5. Obtiene el usuario
6. Lo registra en SecurityContext
7. Continúa la petición
```

---

# Resultado final de la configuración

Esta configuración implementa una arquitectura de seguridad basada en JWT con las siguientes características:

✅ CSRF deshabilitado.

✅ Endpoints `/auth/**` y `/error` públicos.

✅ Todas las demás rutas requieren autenticación.

✅ No se crean sesiones HTTP (`STATELESS`).

✅ Se utiliza un `AuthenticationProvider` personalizado.

✅ Se ejecuta un filtro JWT antes del filtro de autenticación estándar.

---

# Flujo completo de autenticación

```text
POST /auth/login
        │
        ▼
AuthenticationProvider
        │
        ▼
Generar JWT
        │
        ▼
Cliente recibe JWT
        │
        ▼
Authorization: Bearer <token>
        │
        ▼
JwtAuthenticationFilter
        │
        ▼
Valida token
        │
        ▼
Usuario autenticado
        │
        ▼
Acceso al endpoint protegido
```