# 🔐 Demo de Autenticación con Spring Boot y JWT

Esta demostración muestra una forma de implementar autenticación basada en **JSON Web Tokens (JWT)** utilizando **Spring Boot 3** y **Spring Security**.

El objetivo es servir como referencia educativa para comprender los conceptos fundamentales de autenticación, autorización y protección de APIs REST mediante tokens.

---

[**Read in English 🇪🇸**](README.md)

---

## 🎯 Objetivo

Esta demo implementa un flujo básico de autenticación que permite:

* Registrar usuarios.
* Autenticar credenciales mediante login.
* Generar y validar Access Tokens JWT.
* Renovar sesiones utilizando Refresh Tokens.
* Proteger endpoints mediante Spring Security.
* Gestionar errores y validaciones de forma consistente.

No pretende ser una solución completa para producción, sino un ejemplo práctico de cómo integrar JWT en una aplicación Spring Boot.

---

## 🛠️ Tecnologías utilizadas

* **Java 21**
* **Spring Boot 3**
* **Spring Security**
* **JSON Web Tokens (JWT)**
* **Spring Data JPA**
* **PostgreSQL**
* **Lombok**

---

## 📂 Organización del código

La aplicación sigue una estructura basada en capas para separar responsabilidades:

### `controller`

Expone los endpoints REST y recibe las solicitudes HTTP.

### `service`

Contiene la lógica de negocio relacionada con autenticación y usuarios.

### `repository`

Gestiona el acceso a la base de datos mediante Spring Data JPA.

### `dto`

Define los objetos utilizados para intercambiar información entre cliente y servidor.

### `security`

Incluye la configuración de Spring Security, filtros JWT y componentes relacionados con la autenticación.

### `exception`

Centraliza el manejo de errores y la generación de respuestas consistentes.

---

## 🛡️ Funcionalidades implementadas

### Validación de datos

Se validan automáticamente los datos recibidos en las solicitudes, incluyendo:

* Campos obligatorios.
* Longitud mínima de usuario y contraseña.
* Confirmación de contraseña.
* Restricciones definidas mediante Bean Validation.

### Autenticación con JWT

Tras autenticarse correctamente, el usuario recibe:

* **Access Token**: utilizado para acceder a recursos protegidos.
* **Refresh Token**: utilizado para obtener un nuevo Access Token sin volver a iniciar sesión.

### Manejo centralizado de errores

Las excepciones y errores de validación se transforman en respuestas claras y estructuradas.

Ejemplo:

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

## 🚀 Ejecución

### Requisitos

* Java 21
* PostgreSQL
* Maven o Gradle
* IDE compatible (IntelliJ IDEA, VS Code, Eclipse, etc.)

### Configuración

1. Crear una base de datos PostgreSQL.
2. Configurar las credenciales de conexión en `application.properties` o `application-dev.properties`.
3. Ejecutar la aplicación.
4. Consumir los endpoints mediante Postman, Insomnia o cualquier cliente HTTP.

---

## 📌 Endpoints disponibles

| Método | Endpoint              | Descripción                          |
| ------ | --------------------- | ------------------------------------ |
| POST   | `/auth/register`      | Registro de usuarios                 |
| POST   | `/auth/login`         | Autenticación y generación de tokens |
| POST   | `/auth/refresh-token` | Renovación del Access Token          |

## 📚 Documentación

Puedes encontrar documentación detallada sobre el proyecto en los siguientes enlaces:

### General
*   [**Estructura del Proyecto**](doc/es/arquitectura.md)
*   [**Manejo de Errores**](doc/es/errores.md)
*   [**Seguridad y JWT**](doc/es/seguridad.md)

### Detalles Técnicos (Seguridad)
*   [**Configuración de Seguridad (SecurityConfig)**](doc/security-docs/es/security-config.es.md)
*   [**Filtro de Autenticación (JwtAuthenticationFilter)**](doc/security-docs/es/jwt-authenticationFilter.es.md)
*   [**Servicio JWT (JwtService)**](doc/security-docs/es/jwt-service.es.md)

---

## 📚 Propósito educativo

Esta demo fue creada con fines de aprendizaje para mostrar una posible implementación de autenticación JWT en Spring Boot.

Puede utilizarse como punto de partida para experimentar con conceptos como:

* Autenticación stateless.
* Spring Security.
* JWT Access y Refresh Tokens.
* Validación de solicitudes.
* Manejo global de excepciones.
* Arquitectura por capas.
