# 📂 Estructura del Proyecto

Este proyecto sigue una arquitectura de capas estándar basada en **Spring Boot**, diseñada para favorecer la separación de responsabilidades, la mantenibilidad y la escalabilidad del sistema.
## Arquitectura General

```text
Cliente HTTP
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
 Base de Datos
```

---

## 1. Capa de Controlador (`auth/controller`)

La capa de controladores constituye el punto de entrada de la aplicación para las solicitudes HTTP.

### Responsabilidades

* Exponer los endpoints de la API REST.
* Recibir y procesar solicitudes HTTP.
* Validar los datos de entrada.
* Delegar la lógica de negocio a la capa de servicios.
* Construir y devolver las respuestas HTTP correspondientes.

### Consideraciones

* Utiliza la anotación `@Valid` para validar automáticamente los objetos recibidos en las peticiones.
* No contiene lógica de negocio; únicamente coordina el flujo entre la API y los servicios.

---

## 2. Capa de Servicio (`auth/service`)

La capa de servicios centraliza la lógica de negocio de la aplicación.

### Responsabilidades

* Implementar las reglas de negocio.
* Orquestar operaciones entre distintos componentes.
* Realizar validaciones funcionales adicionales.
* Gestionar procesos transaccionales cuando sea necesario.
* Coordinar el acceso a la capa de persistencia.

### Beneficios

* Reduce el acoplamiento entre la API y la base de datos.
* Facilita las pruebas unitarias.
* Permite reutilizar lógica de negocio en distintos puntos de la aplicación.

---

## 3. Capa de Repositorio (`user/repository`)

La capa de repositorios proporciona acceso a los datos mediante **Spring Data JPA**.

### Responsabilidades

* Ejecutar operaciones CRUD (*Create, Read, Update, Delete*).
* Gestionar consultas a la base de datos.
* Abstraer los detalles de persistencia de las capas superiores.

### Beneficios

* Reduce la necesidad de escribir consultas SQL manuales.
* Aprovecha las capacidades de generación automática de consultas de Spring Data JPA.
* Facilita la migración o modificación de la estrategia de persistencia.

---

## 4. Objetos de Transferencia de Datos (DTO) (`auth/dto`)

Los DTO (*Data Transfer Objects*) son estructuras utilizadas para intercambiar información entre el cliente y el servidor.

### Responsabilidades

* Definir el formato de entrada y salida de la API.
* Limitar la información expuesta al cliente.
* Desacoplar la representación externa de los modelos de persistencia.

### Beneficios

* Incrementa la seguridad de la API.
* Reduce el acoplamiento entre las entidades y los contratos públicos.
* Facilita la evolución del modelo de datos sin afectar a los consumidores de la API.

---

## 5. Entidades (`user/entity`)

Las entidades representan el modelo de dominio persistente y mantienen la correspondencia con las tablas de la base de datos.

### Responsabilidades

* Modelar los datos persistidos.
* Definir relaciones entre entidades.
* Configurar el mapeo objeto-relacional mediante JPA.

### Anotaciones Comunes

```java
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
```

### Beneficios

* Permiten representar la estructura de la base de datos mediante objetos Java.
* Facilitan el uso de ORM (*Object-Relational Mapping*).
* Mantienen la coherencia entre el dominio y la persistencia.

---

# Flujo de Datos

El procesamiento de una solicitud sigue el siguiente recorrido:

```text
Request HTTP
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

# Resumen de Componentes

| Componente     | Descripción                                                             |
| -------------- | ----------------------------------------------------------------------- |
| **Controller** | Gestiona las solicitudes y respuestas HTTP.                             |
| **Service**    | Implementa la lógica de negocio y coordina las operaciones del sistema. |
| **Repository** | Proporciona acceso a la capa de persistencia mediante Spring Data JPA.  |
| **DTO**        | Define los contratos de intercambio de datos de la API.                 |
| **Entity**     | Representa el modelo persistente y el mapeo con la base de datos.       |

---

## Principios Aplicados

* **Separación de responsabilidades (SoC)**
* **Inversión de dependencias**
* **Arquitectura en capas**
* **Bajo acoplamiento**
* **Alta cohesión**
* **Mantenibilidad y escalabilidad**
