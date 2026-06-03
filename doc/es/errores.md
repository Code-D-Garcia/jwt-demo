# Manejo de Errores

Cuando algo sale mal (por ejemplo, pones una contraseña corta), el servidor suele lanzar un error muy largo y difícil de leer. Para evitar eso, creamos un sistema que "atrapa" esos errores y los explica mejor.

### 1. ErrorResponse.java
Es una clase que define cómo queremos que se vea el error. En lugar de un texto gigante, mandamos un JSON organizado con:
*   La hora del error.
*   El código (ej. 400 o 401).
*   Un mensaje sencillo de entender.

### 2. GlobalExceptionHandler.java
Este archivo es como un "escuchador" de problemas. 
*   Si falta un dato en el formulario, él lo nota y te dice exactamente qué campo está mal.
*   Si pones mal la contraseña, él atrapa el error y te dice "Usuario o contraseña incorrectos".

Esto es muy útil porque así quien use tu API (como una aplicación de celular o una página web) sabrá exactamente qué pasó.
