# Seguridad y JWT 🔐

La seguridad puede ser la parte más confusa cuando estás aprendiendo. Aquí intentamos hacerlo de forma sencilla usando JWT (JSON Web Token).

### ¿Qué es JWT?
Imagínalo como una tarjeta de acceso. Cuando haces login y tus datos son correctos, el servidor te da esta tarjeta. En cada petición que hagas después, envías esa tarjeta para demostrar quién eres sin tener que mandar tu contraseña cada vez.

### Los archivos clave:

1.  **[`SecurityConfig.java`](../security-docs/es/security-config.es.md)**: Es donde configuramos las reglas. Aquí decimos qué rutas son públicas (como el registro) y cuáles necesitan que el usuario esté identificado.
****
2.  **[`JwtAuthenticationFilter.java`](../security-docs/es/jwt-authenticationFilter.es.md)**: Es un "filtro" que revisa cada petición que llega. Si la petición trae el carnet (el token), el filtro lo lee y le dice a Spring: "Oye, este usuario es válido, déjalo pasar".
****
3.  **[`JwtService.java`](../security-docs/es/jwt-service.es.md)**: Es el archivo que sabe fabricar los tokens y leerlos. Aquí configuramos cuánto tiempo duran (Ejemplo: 24 horas para el acceso normal y 7 días para el de refresco).
****
### Access Token vs Refresh Token
*   **Access Token**: Es el que usas para todo. Dura poco tiempo por seguridad.
*   **Refresh Token**: Si el primero caduca, usas este para pedir uno nuevo sin tener que loguearte otra vez.
